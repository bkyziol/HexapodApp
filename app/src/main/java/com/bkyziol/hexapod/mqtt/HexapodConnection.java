package com.bkyziol.hexapod.mqtt;

import android.content.Context;
import android.content.res.Resources;
import android.os.Message;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.iot.AWSIotMqttClientStatusCallback;
import com.amazonaws.mobileconnectors.iot.AWSIotMqttManager;
import com.amazonaws.mobileconnectors.iot.AWSIotMqttNewMessageCallback;
import com.amazonaws.mobileconnectors.iot.AWSIotMqttQos;
import com.amazonaws.regions.Regions;
import com.bkyziol.hexapod.R;
import com.bkyziol.hexapod.activity.MainActivity;
import com.bkyziol.hexapod.status.DeviceStatus;

import java.io.UnsupportedEncodingException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

public final class HexapodConnection {

    private final MainActivity activity;
    private final Context context;
    private final Resources resources;

    private AWSIotMqttManager mqttManager;

    private long statusRequestTimestamp = 0;
    private long statusResponseExpirationTimestamp = System.currentTimeMillis();
    private boolean connectedToServer = false;

    public HexapodConnection(final MainActivity activity) {
        this.activity = activity;
        this.context = activity.getApplicationContext();
        this.resources = context.getResources();
    }

    public void connect() {
        CognitoCachingCredentialsProvider credentialsProvider = new CognitoCachingCredentialsProvider(
                context,
                resources.getString(R.string.cognito_pool_id),
                Regions.EU_WEST_1
        );
        connectToAWS(credentialsProvider);
    }

    private void connectToAWS(CognitoCachingCredentialsProvider credentialsProvider) {

        mqttManager = new AWSIotMqttManager(UUID.randomUUID().toString(), resources.getString(R.string.client_endpoint));
        mqttManager.setAutoReconnect(true);
        mqttManager.setAutoResubscribe(true);
        mqttManager.setOfflinePublishQueueEnabled(false);
        mqttManager.setReconnectRetryLimits(5, 5);
        mqttManager.setMaxAutoReconnectAttepts(-1);
        startSendCommandsInterval();
        try {
            mqttManager.connect(credentialsProvider, new AWSIotMqttClientStatusCallback() {
                @Override
                public void onStatusChanged(final AWSIotMqttClientStatus status,
                                            final Throwable throwable) {
                    connectedToServer = false;
                    if (throwable != null) {
                        activity.setServerStatusTextView(R.color.red, "Server: connection error");
                    } else {
                        if (status == AWSIotMqttClientStatus.Connecting) {
                            activity.setServerStatusTextView(R.color.yellow, "Server: connecting...");
                        } else if (status == AWSIotMqttClientStatus.Connected) {
                            activity.setServerStatusTextView(R.color.green, "Server: connected");
                            connectedToServer = true;
                            subscribeToStatusTopic();
                            subscribeToCameraTopic();
                        } else if (status == AWSIotMqttClientStatus.Reconnecting) {
                            activity.setServerStatusTextView(R.color.yellow, "Server: reconnecting...");
                        } else {
                            activity.setServerStatusTextView(R.color.red, "Server: disconnected");
                        }
                    }
                    if (!connectedToServer) {
                        activity.setHexapodStatusTextView(R.color.yellow, "Hexapod: unknown");
                    }
                }
            });
        } catch (final Exception e) {
            activity.setServerStatusTextView(R.color.red, "Server: connection error");
        }
    }

    private void subscribeToCameraTopic() {
        try {
            mqttManager.subscribeToTopic(resources.getString(R.string.camera_topic), AWSIotMqttQos.QOS0,
                    new AWSIotMqttNewMessageCallback() {
                        @Override
                        public void onMessageArrived(final String topic, final byte[] data) {
                            if (DeviceStatus.isCameraEnabled()) {
                                Message msg = new Message();
                                msg.obj = data;
                                activity.getImageHandler().sendMessage(msg);
                            }
                        }
                    });
        } catch (Exception e) {
            activity.setServerStatusTextView(R.color.red, "Server: subscription error");
        }
    }

    private void subscribeToStatusTopic() {
        try {
            mqttManager.subscribeToTopic(resources.getString(R.string.status_topic), AWSIotMqttQos.QOS0,
                    new AWSIotMqttNewMessageCallback() {
                        @Override
                        public void onMessageArrived(final String topic, final byte[] data) {
                            activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    statusResponseExpirationTimestamp = System.currentTimeMillis() + 5000;
                                    try {
                                        String string = new String(data, "UTF-8");
                                        switch (string) {
                                            case "OK":
                                                activity.setHexapodStatusTextView(R.color.green, "Hexapod: connected");
                                                break;
                                            case "ERROR":
                                                activity.setHexapodStatusTextView(R.color.red, "Hexapod: ERROR");
                                                break;
                                            case "STANDING":
                                                DeviceStatus.setSleepMode(false);
                                                activity.showFullKeyboard();
                                                break;
                                            case "CROUCHING":
                                                DeviceStatus.setSleepMode(true);
                                                activity.showSleepModeKeyboard();
                                                break;
                                        }
                                    } catch (UnsupportedEncodingException e) {
                                        activity.setHexapodStatusTextView(R.color.red, "Hexapod: ERROR");
                                    }
                                }
                            });
                        }
                    });
        } catch (Exception e) {
            activity.setServerStatusTextView(R.color.red, "Server: subscription error");
        }
    }

    private void startSendCommandsInterval() {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                try {
                    if (connectedToServer && statusResponseExpirationTimestamp < System.currentTimeMillis()) {
                        System.out.println("offline");
                        activity.setHexapodStatusTextView(R.color.red, "Hexapod: offline");
                    }
                    if (statusRequestTimestamp + 3000  < System.currentTimeMillis()) {
                        DeviceStatus.setStatusReportNeeded(true);
                        statusRequestTimestamp = System.currentTimeMillis();
                    }
                    if (DeviceStatus.isHexapodMoving() || DeviceStatus.isCameraMoving() || DeviceStatus.isStatusReportNeeded()) {
                        System.out.println("command send");
                        String jsonMessage = DeviceStatus.commandJSON();
                        mqttManager.publishString(jsonMessage, resources.getString(R.string.command_topic), AWSIotMqttQos.QOS0);
                    }
                } catch (Exception e) {
                    activity.setServerStatusTextView(R.color.red, "Server: disconnected");
                    activity.setHexapodStatusTextView(R.color.yellow, "Hexapod: unknown");
                }
            }
        }, 0, 500);
    }
}
