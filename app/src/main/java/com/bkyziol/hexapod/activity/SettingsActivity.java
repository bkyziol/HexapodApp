package com.bkyziol.hexapod.activity;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Toast;

import com.bkyziol.hexapod.R;
import com.bkyziol.hexapod.image.ImageData;
import com.bkyziol.hexapod.image.ImageObserver;
import com.bkyziol.hexapod.status.DeviceStatus;

public class SettingsActivity extends Activity implements ImageObserver {

    private ImageView cameraImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        cameraImageView = findViewById(R.id.cameraViewSettings);

        final SeekBar speedSlowBar;
        speedSlowBar = findViewById(R.id.speedSlowBar);
        speedSlowBar.setMax(getResources().getInteger(R.integer.max_speed));
        speedSlowBar.setProgress(DeviceStatus.getSpeedSlow() - 5);

        final SeekBar speedFastBar;
        speedFastBar = findViewById(R.id.speedFastBar);
        speedFastBar.setMax(getResources().getInteger(R.integer.max_speed));
        speedFastBar.setProgress(DeviceStatus.getSpeedFast() - 5);

        SeekBar strideLengthBar;
        strideLengthBar = findViewById(R.id.strideLengthBar);
        strideLengthBar.setMax(getResources().getInteger(R.integer.max_stride_length));
        strideLengthBar.setProgress(DeviceStatus.getStrideLength() - 10);

        SeekBar cameraSpeedBar;
        cameraSpeedBar = findViewById(R.id.cameraSpeedBar);
        cameraSpeedBar.setMax(getResources().getInteger(R.integer.max_camera_speed));
        cameraSpeedBar.setProgress(DeviceStatus.getCameraSpeed() - 5);

        SeekBar videoFPSBar;
        videoFPSBar = findViewById(R.id.videoFPSBar);
        videoFPSBar.setMax(getResources().getInteger(R.integer.max_video_FPS));
        videoFPSBar.setProgress(DeviceStatus.getVideoFPS() - 1);

        SeekBar videoQualityBar;
        videoQualityBar = findViewById(R.id.videoQualityBar);
        videoQualityBar.setMax(getResources().getInteger(R.integer.max_video_quality));
        videoQualityBar.setProgress(DeviceStatus.getVideoQuality() - 1);

        speedSlowBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                DeviceStatus.setSpeedSlow(progress + 5);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (DeviceStatus.getSpeedFast() < DeviceStatus.getSpeedSlow()) {
                    speedSlowBar.setProgress(DeviceStatus.getSpeedFast());
                    Toast toast = Toast.makeText(getApplicationContext(), "Wartość musi być mniejsza niż w trybie szybkim.", Toast.LENGTH_SHORT);
                    toast.show();
                } else {
                    double percent = DeviceStatus.getSpeedSlow();
                    percent = percent / (getResources().getInteger(R.integer.max_speed) + 5) * 100;
                    Toast toast = Toast.makeText(getApplicationContext(), "Tryb wolny: " + (int) percent + "%", Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
        });

        speedFastBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                DeviceStatus.setSpeedFast(progress + 5);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (DeviceStatus.getSpeedFast() < DeviceStatus.getSpeedSlow()) {
                    speedFastBar.setProgress(DeviceStatus.getSpeedSlow());
                    Toast toast = Toast.makeText(getApplicationContext(), "Wartość musi być większa niż w trybie wolnym.", Toast.LENGTH_SHORT);
                    toast.show();
                } else {
                    double percent = DeviceStatus.getSpeedFast();
                    percent = percent / (getResources().getInteger(R.integer.max_speed) + 5) * 100;
                    Toast toast = Toast.makeText(getApplicationContext(), "Tryb szybki: " + (int) percent + "%", Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
        });

        strideLengthBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                DeviceStatus.setStrideLength(progress + 10);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

                Toast toast = Toast.makeText(getApplicationContext(), "Długość kroku: " + DeviceStatus.getStrideLength() + "mm", Toast.LENGTH_SHORT);
                toast.show();
            }
        });

        cameraSpeedBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                DeviceStatus.setCameraSpeed(progress + 5);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                double percent = DeviceStatus.getCameraSpeed();
                percent = percent / (getResources().getInteger(R.integer.max_camera_speed) + 5) * 100;
                Toast toast = Toast.makeText(getApplicationContext(), "Prędkość kamery: " + (int) percent + "%", Toast.LENGTH_SHORT);
                toast.show();
            }
        });

        videoFPSBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                DeviceStatus.setVideoFPS(progress + 1);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                Toast toast = Toast.makeText(getApplicationContext(), "Liczba klatek/sek: " + DeviceStatus.getVideoFPS(), Toast.LENGTH_SHORT);
                toast.show();
            }
        });

        videoQualityBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                DeviceStatus.setVideoQuality(progress + 1);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                double percent = DeviceStatus.getVideoQuality();
                percent = percent / (getResources().getInteger(R.integer.max_video_quality) + 1) * 100;
                Toast toast = Toast.makeText(getApplicationContext(), "Jakość obrazu: " + (int) percent + "%", Toast.LENGTH_SHORT);
                toast.show();
            }
        });

        final ImageView exitButtonView = (ImageView) findViewById(R.id.exitButtonView);
        exitButtonView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exitButtonView.setImageResource(R.drawable.exit_pressed);
                finish();
            }
        });
    }

    @Override
    public void update(byte[] data) {
        if (cameraImageView != null && cameraImageView.getHeight() != 0 && cameraImageView.getWidth() != 0) {
            Bitmap bmp = BitmapFactory.decodeByteArray(data, 0, data.length);
            Bitmap scaledBmp = Bitmap.createScaledBitmap(bmp, cameraImageView.getWidth(), cameraImageView.getHeight(), false);
            cameraImageView.setImageBitmap(scaledBmp);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        ImageData.register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        ImageData.unregister(this);
    }
}
