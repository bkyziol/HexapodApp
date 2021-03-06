package com.bkyziol.hexapod.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bkyziol.hexapod.R;
import com.bkyziol.hexapod.image.ImageData;
import com.bkyziol.hexapod.image.ImageObserver;
import com.bkyziol.hexapod.mqtt.HexapodConnection;
import com.bkyziol.hexapod.status.DeviceStatus;

public class MainActivity extends Activity implements ImageObserver {

    private HexapodConnection hexapodConnection;

    private ImageView buttonsMapView;
    private ImageView menuView;
    private ImageView pressedButton1View;
    private ImageView pressedButton2View;
    private ImageView cameraImageView;

    private TextView serverStatusTextView;
    private TextView hexapodStatusTextView;

    private Handler imageHandler;

    private int hexapodPointerIndex = -1;
    private int cameraPointerIndex = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.hexapodConnection = new HexapodConnection(this);
        hexapodConnection.connect();

        this.buttonsMapView = findViewById(R.id.buttonsMapView);
        this.menuView = findViewById(R.id.menuView);
        this.pressedButton1View = findViewById(R.id.pressedButton1View);
        this.pressedButton2View = findViewById(R.id.pressedButton2View);
        this.cameraImageView = findViewById(R.id.cameraView);

        this.serverStatusTextView = findViewById(R.id.serverStatusTextView);
        this.hexapodStatusTextView = findViewById(R.id.hexapodStatusTextView);

        ImageData.register(this);

        this.imageHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                ImageData.setImageData((byte[]) msg.obj);
            }
        };

        menuView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                int action = event.getActionMasked();

                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                        buttonPressed(event);
                        break;
                    case MotionEvent.ACTION_UP:
                        allButtonsReleased();
                        break;
                    case MotionEvent.ACTION_POINTER_DOWN:
                        buttonPressed(event);
                        break;
                    case MotionEvent.ACTION_POINTER_UP:
                        buttonReleased(event);
                        break;
                }
                return true;
            }
        });
    }

    private void buttonPressed(MotionEvent event) {
        int pointerIndex = event.getActionIndex();
        int evX = (int) event.getX(pointerIndex);
        int evY = (int) event.getY(pointerIndex);

        buttonsMapView.setDrawingCacheEnabled(true);
        Bitmap hotspots = Bitmap.createBitmap(buttonsMapView.getDrawingCache());
        buttonsMapView.setDrawingCacheEnabled(false);

        int pixel = hotspots.getPixel(evX, evY);
        int redValue = Color.red(pixel);
        int greenValue = Color.green(pixel);
        int blueValue = Color.blue(pixel);
        int pixelRGBValue = redValue * 65536 + greenValue * 256 + blueValue;
        if (DeviceStatus.getBodyMovement().equals("STAND_BY") && !DeviceStatus.isSleepMode()) {
            switch (pixelRGBValue) {
                case 0X43009e:
                    pressedButton1View.setImageResource(R.drawable.forward);
                    hexapodPointerIndex = pointerIndex;
                    DeviceStatus.setBodyMovement("FORWARD");
                    break;
                case 0X044c18:
                    pressedButton1View.setImageResource(R.drawable.forward);
                    hexapodPointerIndex = pointerIndex;
                    DeviceStatus.setBodyMovement("HARD_LEFT");
                    break;
                case 0Xc0cfff:
                    pressedButton1View.setImageResource(R.drawable.forward);
                    hexapodPointerIndex = pointerIndex;
                    DeviceStatus.setBodyMovement("LEFT");
                    break;
                case 0Xa6911d:
                    pressedButton1View.setImageResource(R.drawable.forward);
                    hexapodPointerIndex = pointerIndex;
                    DeviceStatus.setBodyMovement("SLIGHTLY_LEFT");
                    break;
                case 0X8a7b73:
                    pressedButton1View.setImageResource(R.drawable.forward);
                    hexapodPointerIndex = pointerIndex;
                    DeviceStatus.setBodyMovement("SLIGHTLY_RIGHT");
                    break;
                case 0Xfc29dc:
                    pressedButton1View.setImageResource(R.drawable.forward);
                    hexapodPointerIndex = pointerIndex;
                    DeviceStatus.setBodyMovement("RIGHT");
                    break;
                case 0Xe4cfc5:
                    pressedButton1View.setImageResource(R.drawable.forward);
                    hexapodPointerIndex = pointerIndex;
                    DeviceStatus.setBodyMovement("HARD_RIGHT");
                    break;
                case 0Xca0000:
                    pressedButton1View.setImageResource(R.drawable.turn_left);
                    hexapodPointerIndex = pointerIndex;
                    DeviceStatus.setBodyMovement("TURN_LEFT");
                    break;
                case 0Xc3006e:
                    pressedButton1View.setImageResource(R.drawable.turn_right);
                    hexapodPointerIndex = pointerIndex;
                    DeviceStatus.setBodyMovement("TURN_RIGHT");
                    break;
                case 0X01a7a9:
                    pressedButton1View.setImageResource(R.drawable.backward);
                    hexapodPointerIndex = pointerIndex;
                    DeviceStatus.setBodyMovement("BACKWARD");
                    break;
                case 0X00ff2a:
                    pressedButton1View.setImageResource(R.drawable.strafe_left);
                    hexapodPointerIndex = pointerIndex;
                    DeviceStatus.setBodyMovement("STRAFE_LEFT");
                    break;
                case 0Xac5d00:
                    pressedButton1View.setImageResource(R.drawable.strafe_right);
                    hexapodPointerIndex = pointerIndex;
                    DeviceStatus.setBodyMovement("STRAFE_RIGHT");
                    break;
            }
        }
        if (DeviceStatus.getBodyMovement().equals("STAND_BY") && pixelRGBValue == 0X6c0000) {
            if (DeviceStatus.isSleepMode()) {
                DeviceStatus.setBodyMovement("RISE");
            } else {
                DeviceStatus.setBodyMovement("CROUCH");
            }
            hexapodPointerIndex = pointerIndex;
            pressedButton1View.setImageResource(R.drawable.crouch);
        }
        switch (pixelRGBValue) {
            case 0Xa2b000:
                ImageView speedView = findViewById(R.id.speedView);
                if (DeviceStatus.isFastMode()) {
                    DeviceStatus.setFastMode(false);
                    speedView.setImageResource(R.drawable.speed_slow);
                } else {
                    DeviceStatus.setFastMode(true);
                    speedView.setImageResource(R.drawable.speed_fast);
                }
                break;
            case 0Xeaff00:
                ImageView cameraEnabledView = findViewById(R.id.cameraEnabled);
                if (!DeviceStatus.isCameraEnabled()) {
                    System.out.println("Camera: enabled");
                    DeviceStatus.setCameraEnabled(true);
                    cameraEnabledView.setImageResource(R.drawable.camera_enabled);
                } else {
                    System.out.println("Camera: disabled");
                    DeviceStatus.setCameraEnabled(false);
                    cameraImageView.setImageResource(R.color.black);
                    cameraEnabledView.setImageResource(0);
                }
                break;
            case 0Xa200ff:
                ImageView faceDetectionEnabled = findViewById(R.id.faceDetectionEnabled);
                if (!DeviceStatus.isFaceDetectionEnabled()) {
                    DeviceStatus.setFaceDetectionEnabled(true);
                    faceDetectionEnabled.setImageResource(R.drawable.face_detection_enabled);
                } else {
                    DeviceStatus.setFaceDetectionEnabled(false);
                    faceDetectionEnabled.setImageResource(0);
                }
                break;
            case 0Xffba00:
                pressedButton1View.setImageResource(R.drawable.settings);
                openMenuSettings();
                break;
        }
        if (DeviceStatus.getHeadMovement().equals("STAND_BY")) {
            switch (pixelRGBValue) {
                case 0Xff0090:
                    cameraPointerIndex = pointerIndex;
                    DeviceStatus.setHeadMovement("UP");
                    pressedButton2View.setImageResource(R.drawable.camera_up);
                    break;
                case 0X6c00ff:
                    cameraPointerIndex = pointerIndex;
                    DeviceStatus.setHeadMovement("LEFT");
                    pressedButton2View.setImageResource(R.drawable.camera_left);
                    break;
                case 0X00fdff:
                    cameraPointerIndex = pointerIndex;
                    DeviceStatus.setHeadMovement("RIGHT");
                    pressedButton2View.setImageResource(R.drawable.camera_right);
                    break;
                case 0Xff0000:
                    cameraPointerIndex = pointerIndex;
                    DeviceStatus.setHeadMovement("CENTER");
                    pressedButton2View.setImageResource(R.drawable.camera_center);
                    break;
                case 0Xd1ab7d:
                    cameraPointerIndex = pointerIndex;
                    DeviceStatus.setHeadMovement("DOWN");
                    pressedButton2View.setImageResource(R.drawable.camera_down);
                    break;
            }
        }
    }

    private void buttonReleased(MotionEvent event) {
        int pointerIndex = event.getActionIndex();
        if (pointerIndex == hexapodPointerIndex) {
            pressedButton1View.setImageResource(0x00000000);
            DeviceStatus.setBodyMovement("STAND_BY");
            hexapodPointerIndex = -1;
        }
        if (pointerIndex == cameraPointerIndex) {
            pressedButton2View.setImageResource(0x00000000);
            DeviceStatus.setHeadMovement("STAND_BY");
            cameraPointerIndex = -1;
        }
    }

    private void allButtonsReleased() {
        pressedButton1View.setImageResource(0x00000000);
        pressedButton2View.setImageResource(0x00000000);
        DeviceStatus.setBodyMovement("STAND_BY");
        DeviceStatus.setHeadMovement("STAND_BY");
        hexapodPointerIndex = -1;
        cameraPointerIndex = -1;
    }

    private void openMenuSettings() {
        Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
        MainActivity.this.startActivity(intent);
    }

    public Handler getImageHandler() {
        return imageHandler;
    }

    @Override
    public void updateCameraImageView(byte[] data) {
        if (cameraImageView.getHeight() != 0 && cameraImageView.getWidth() != 0) {
            Bitmap bmp = BitmapFactory.decodeByteArray(data, 0, data.length);
            Bitmap scaledBmp = Bitmap.createScaledBitmap(bmp, cameraImageView.getWidth(), cameraImageView.getHeight(), false);
            cameraImageView.setImageBitmap(scaledBmp);
        }
    }

    public void showFullKeyboard() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                menuView.setImageResource(R.drawable.keys);
            }
        });
    }

    public void showSleepModeKeyboard() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                menuView.setImageResource(R.drawable.keys_sleep_mode);
            }
        });
    }

    public void setServerStatusTextView(final int color, final String text) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                serverStatusTextView.setText(text);
                serverStatusTextView.setTextColor(getApplicationContext().getResources().getColor(color));
            }
        });
    }

    public void setHexapodStatusTextView(final int color, final String text) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                hexapodStatusTextView.setText(text);
                hexapodStatusTextView.setTextColor(getApplicationContext().getResources().getColor(color));
            }
        });
    }

}
