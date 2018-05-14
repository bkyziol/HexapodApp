package com.bkyziol.hexapod.mqtt;

import android.content.Context;
import android.content.res.Resources;
import android.os.Message;
import android.widget.TextView;

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
    private final Resources resources;
    private final TextView serverStatusTextView;
    private final TextView hexapodStatusTextView;

    private AWSIotMqttManager mqttManager;

    private long lastStatusMessageTimestamp;
    private boolean connectedToServer = false;

    public HexapodConnection(final MainActivity activity) {
        this.activity = activity;
        this.serverStatusTextView = activity.findViewById(R.id.serverStatusTextView);
        this.hexapodStatusTextView = activity.findViewById(R.id.hexapodStatusTextView);

        Context context = activity.getApplicationContext();
        this.resources = context.getResources();
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
//        startSendCommandsInterval();
        startSendStatusPingInterval();
        try {
            mqttManager.connect(credentialsProvider, new AWSIotMqttClientStatusCallback() {
                @Override
                public void onStatusChanged(final AWSIotMqttClientStatus status,
                                            final Throwable throwable) {
                    connectedToServer = false;
                    if (throwable != null) {
                        setServerStatusTextView(R.color.red, "Server: connection error");
                    } else {
                        if (status == AWSIotMqttClientStatus.Connecting) {
                            setServerStatusTextView(R.color.yellow, "Server: connecting...");
                        } else if (status == AWSIotMqttClientStatus.Connected) {
                            setServerStatusTextView(R.color.green, "Server: connected");
                            connectedToServer = true;
                            subscribeToStatusTopic();
                            subscribeToCameraTopic();
                        } else if (status == AWSIotMqttClientStatus.Reconnecting) {
                            setServerStatusTextView(R.color.yellow, "Server: reconnecting...");
                        } else {
                            setServerStatusTextView(R.color.red, "Server: disconnected");
                        }
                    }
                    if (!connectedToServer) {
                        setHexapodStatusTextView(R.color.yellow, "Hexapod: unknown");
                    }
                }
            });
        } catch (final Exception e) {
            setServerStatusTextView(R.color.red, "Server: connection error");
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
            setServerStatusTextView(R.color.red, "Server: subscription error");
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
                                    lastStatusMessageTimestamp = System.currentTimeMillis();
                                    try {
                                        String string = new String(data, "UTF-8");
                                        switch (string) {
                                            case "OK":
                                                setHexapodStatusTextView(R.color.green, "Hexapod: connected");
                                                break;
                                            case "ERROR":
                                                setHexapodStatusTextView(R.color.red, "Hexapod: ERROR");
                                                break;
                                        }
                                    } catch (UnsupportedEncodingException e) {
                                        setHexapodStatusTextView(R.color.red, "Hexapod: ERROR");
                                    }
                                }
                            });
                        }
                    });
        } catch (Exception e) {
            setServerStatusTextView(R.color.red, "Server: subscription error");
        }
    }

    private void startSendCommandsInterval() {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                try {
                    String jsonMessage = DeviceStatus.toJSON();
                    mqttManager.publishString(jsonMessage, resources.getString(R.string.command_topic), AWSIotMqttQos.QOS0);
                } catch (Exception e) {
                    setServerStatusTextView(R.color.red, "Server: disconnected");
                    setHexapodStatusTextView(R.color.yellow, "Hexapod: unknown");
                }
            }
        }, 0, 500);
    }


    private void startSendStatusPingInterval() {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                try {
                    if (connectedToServer && lastStatusMessageTimestamp + 4000 < System.currentTimeMillis()) {
                        setHexapodStatusTextView(R.color.red, "Hexapod: offline");
                    }
                    mqttManager.publishString("PING", resources.getString(R.string.ping_topic), AWSIotMqttQos.QOS0);
                } catch (Exception e) {
                    setServerStatusTextView(R.color.red, "Server: disconnected");
                    setHexapodStatusTextView(R.color.yellow, "Hexapod: unknown");
                }
            }
        }, 0, 3000);
    }

    private void setServerStatusTextView(final int color, final String text) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                serverStatusTextView.setText(text);
                serverStatusTextView.setTextColor(resources.getColor(color));
            }
        });
    }

    private void setHexapodStatusTextView(final int color, final String text) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                hexapodStatusTextView.setText(text);
                hexapodStatusTextView.setTextColor(resources.getColor(color));
            }
        });
    }
}
