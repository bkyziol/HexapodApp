package com.bkyziol.hexapod.mqtt;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.iot.AWSIotMqttClientStatusCallback;
import com.amazonaws.mobileconnectors.iot.AWSIotMqttManager;
import com.amazonaws.mobileconnectors.iot.AWSIotMqttNewMessageCallback;
import com.amazonaws.mobileconnectors.iot.AWSIotMqttQos;
import com.amazonaws.regions.Regions;
import com.bkyziol.hexapod.R;
import com.bkyziol.hexapod.image.ImageDecoder;
import com.bkyziol.hexapod.status.DeviceStatus;

public final class HexapodConnection {

    private final Activity activity;
    private final Resources resources;
    private final TextView serverStatusTextView;
    private final ImageView cameraImageView;
    private final Handler imageHandler;
    private final ImageDecoder imageDecoder;
    private int id = 0;
    private AWSIotMqttManager mqttManager;

    public HexapodConnection(final Activity activity) {
        this.activity = activity;
        this.serverStatusTextView = activity.findViewById(R.id.serverStatusTextView);
        this.cameraImageView = activity.findViewById(R.id.cameraView);

        Context context = activity.getApplicationContext();
        this.resources = context.getResources();
        CognitoCachingCredentialsProvider credentialsProvider = new CognitoCachingCredentialsProvider(
                context,
                resources.getString(R.string.cognito_pool_id),
                Regions.EU_WEST_1
        );
        connectToAWS(credentialsProvider);
        this.imageHandler = new Handler() {
            @Override
            public void handleMessage(Message image) {
                Bitmap bmp = (Bitmap) image.obj;
                cameraImageView.setImageBitmap(bmp);
            }
        };
        this.imageDecoder = new ImageDecoder(imageHandler);
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
                    if (throwable != null) {
                        setStatusTextViewText(R.color.red, "Server: connection error");
                    } else {
                        if (status == AWSIotMqttClientStatus.Connecting) {
                            setStatusTextViewText(R.color.yellow, "Server: connecting...");
                        } else if (status == AWSIotMqttClientStatus.Connected) {
                            setStatusTextViewText(R.color.green, "Server: connected");
                            subscribeToStatusTopic();
                            subscribeToCameraTopic();
                        } else if (status == AWSIotMqttClientStatus.Reconnecting) {
                            setStatusTextViewText(R.color.yellow, "Server: reconnecting...");
                        } else {
                            setStatusTextViewText(R.color.red, "Server: disconnected");
                        }
                    }
                }
            });
        } catch (final Exception e) {
            setStatusTextViewText(R.color.red, "Server: connection error");
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
                                    System.out.println("Command arrived");
                                }
                            });
                        }
                    });
        } catch (Exception e) {
            setStatusTextViewText(R.color.red, "Server: subscription error");
        }
    }

    private void subscribeToCameraTopic() {
        try {
            mqttManager.subscribeToTopic(resources.getString(R.string.camera_topic), AWSIotMqttQos.QOS0,
                    new AWSIotMqttNewMessageCallback() {
                        @Override
                        public void onMessageArrived(final String topic, final byte[] data) {
                            id++;
                            System.out.println("id: " + id);
                            if (imageDecoder != null && !imageDecoder.isAlive()) {
                                System.out.println("imageDecoder: is not alive");
                                imageDecoder.decode(data, cameraImageView.getWidth(), cameraImageView.getHeight(), id);
                            } else {
                                System.out.println("imageDecoder: is alive");
                            }
//                            new ImageDecoder(imageHandler, data, cameraImageView.getWidth(), cameraImageView.getHeight(), id));
                        }
//                            activity.runOnUiThread(new Runnable() {
//                                @Override
//                                public void run() {
//                                    Bitmap bmp = BitmapFactory.decodeByteArray(data, 0, data.length);
//                                    cameraImageView.setImageBitmap(Bitmap.createScaledBitmap(bmp, cameraImageView.getWidth(),
//                                            cameraImageView.getHeight(), false));
//                                });
//                            });
//                        }
//                    })
                    });
        } catch (Exception e) {
            setStatusTextViewText(R.color.red, "Server: subscription error");
        }
    }

    private void startSendCommandsInterval() {
        startSendCommandsInterval(500);
    }

    private void startSendCommandsInterval(long interval) {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                try {
                    String jsonMessage = DeviceStatus.toJSON();
                    mqttManager.publishString(jsonMessage, resources.getString(R.string.command_topic), AWSIotMqttQos.QOS0);
                } catch (Exception e) {
                    setStatusTextViewText(R.color.red, "Server: disconnected");
                }
            }
        }, 0, interval);
    }

    private void setStatusTextViewText(final int color, final String text) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                serverStatusTextView.setText(text);
                serverStatusTextView.setTextColor(resources.getColor(color));
            }
        });
    }
}
