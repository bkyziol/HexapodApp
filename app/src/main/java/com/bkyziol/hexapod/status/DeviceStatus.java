package com.bkyziol.hexapod.status;

import org.json.JSONException;
import org.json.JSONObject;

public class DeviceStatus {

    private static String hexapodMovement = "STAND_BY";
    private static String cameraMovement = "STAND_BY";
    private static boolean sleepMode = true;
    private static boolean fastMode = false;
    private static int speedFast = 100;
    private static int speedSlow = 20;
    private static int strideLength = 70;
    private static int cameraSpeed = 100;

    private static boolean cameraEnabled = true;
    private static int videoQuality = 3;
    private static int videoFPS = 4;

    public static String getHexapodMovement() {
        return hexapodMovement;
    }

    public static void setHexapodMovement(String hexapodMovement) {
        DeviceStatus.hexapodMovement = hexapodMovement;
    }

    public static String getCameraMovement() {
        return cameraMovement;
    }

    public static void setCameraMovement(String cameraMovement) {
        DeviceStatus.cameraMovement = cameraMovement;
    }

    public static boolean isSleepMode() {
        return sleepMode;
    }

    public static void setSleepMode(boolean sleepMode) {
        DeviceStatus.sleepMode = sleepMode;
    }

    public static boolean isFastMode() {
        return fastMode;
    }

    public static void setFastMode(boolean fastMode) {
        DeviceStatus.fastMode = fastMode;
    }

    public static int getSpeedFast() {
        return speedFast;
    }

    public static void setSpeedFast(int speedFast) {
        DeviceStatus.speedFast = speedFast;
    }

    public static int getSpeedSlow() {
        return speedSlow;
    }

    public static void setSpeedSlow(int speedSlow) {
        DeviceStatus.speedSlow = speedSlow;
    }

    public static int getStrideLength() {
        return strideLength;
    }

    public static void setStrideLength(int strideLength) {
        DeviceStatus.strideLength = strideLength;
    }

    public static int getCameraSpeed() {
        return cameraSpeed;
    }

    public static void setCameraSpeed(int cameraSpeed) {
        DeviceStatus.cameraSpeed = cameraSpeed;
    }


    public static boolean isCameraEnabled() {
        return cameraEnabled;
    }

    public static void setCameraEnabled(boolean cameraEnabled) {
        DeviceStatus.cameraEnabled = cameraEnabled;
    }

    public static int getVideoQuality() {
        return videoQuality;
    }

    public static void setVideoQuality(int videoQuality) {
        DeviceStatus.videoQuality = videoQuality;
    }

    public static int getVideoFPS() {
        return videoFPS;
    }

    public static void setVideoFPS(int videoFPS) {
        DeviceStatus.videoFPS = videoFPS;
    }

    public static String toJSON() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("timestamp", System.currentTimeMillis());
            jsonObject.put("hexapodMovement", getHexapodMovement());
            jsonObject.put("cameraMovement", getCameraMovement());
            jsonObject.put("sleepMode", isSleepMode());
            jsonObject.put("fastMode", isFastMode());
            jsonObject.put("speedFast", getSpeedFast());
            jsonObject.put("speedSlow", getSpeedSlow());
            jsonObject.put("strideLength", getStrideLength());
            jsonObject.put("cameraSpeed", getCameraSpeed());
            jsonObject.put("cameraEnabled", isCameraEnabled());
            jsonObject.put("videoQuality", getVideoQuality());
            jsonObject.put("videoFPS", getVideoFPS());
            return jsonObject.toString();
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return "";
        }
    }
}
