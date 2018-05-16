package com.bkyziol.hexapod.status;

import org.json.JSONException;
import org.json.JSONObject;

public class DeviceStatus {

    private static boolean hexapodIsMoving = false;
    private static boolean cameraIsMoving = false;

    private static boolean statusReportNeeded = true;

    private static String hexapodMovement = "STAND_BY";
    private static String cameraMovement = "STAND_BY";
    private static boolean sleepMode = true;

    private static boolean fastMode = false;
    private static int speedFast = 100;
    private static int speedSlow = 20;

    private static int strideLength = 70;
    private static int cameraSpeed = 100;

    private static boolean cameraEnabled = false;
    private static boolean faceDetectionEnabled = false;
    private static int videoQuality = 3;
    private static int videoFPS = 4;


    public static boolean isHexapodMoving() {
        return hexapodIsMoving;
    }

    public static boolean isCameraMoving() {
        return cameraIsMoving;
    }

    public static boolean isStatusReportNeeded() {
        return statusReportNeeded;
    }

    public static void setStatusReportNeeded(boolean statusReportNeeded) {
        DeviceStatus.statusReportNeeded = statusReportNeeded;
    }

    public static String getHexapodMovement() {
        return hexapodMovement;
    }

    public static void setHexapodMovement(String hexapodMovement) {
        DeviceStatus.hexapodMovement = hexapodMovement;
        hexapodIsMoving = true;
    }

    public static String getCameraMovement() {
        return cameraMovement;
    }

    public static void setCameraMovement(String cameraMovement) {
        DeviceStatus.cameraMovement = cameraMovement;
        cameraIsMoving = true;
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

    private static int getHexapodSpeed() {
        if (isFastMode()) {
            return speedFast;
        } else {
            return speedSlow;
        }
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

    public static boolean isFaceDetectionEnabled() {
        return faceDetectionEnabled;
    }

    public static void setFaceDetectionEnabled(boolean faceDetectionEnabled) {
        DeviceStatus.faceDetectionEnabled = faceDetectionEnabled;
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

    public static String commandJSON() {

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("timestamp", System.currentTimeMillis());
            jsonObject.put("hexapodMovement", hexapodMovement);
            if (hexapodMovement.equals("STAND_BY")) {
                hexapodIsMoving = false;
            }
            jsonObject.put("cameraMovement", cameraMovement);
            if (cameraMovement.equals("STAND_BY")) {
                cameraIsMoving = false;
            }
            jsonObject.put("statusReportNeeded", statusReportNeeded);
            statusReportNeeded = false;
            jsonObject.put("cameraEnabled", cameraEnabled);
            jsonObject.put("faceDetectionEnabled", faceDetectionEnabled);
            jsonObject.put("hexapodSpeed", getHexapodSpeed());
            jsonObject.put("strideLength", strideLength);
            jsonObject.put("cameraSpeed", cameraSpeed);
            jsonObject.put("videoQuality", videoQuality);
            jsonObject.put("videoFPS", videoFPS);
            return jsonObject.toString();
        } catch (JSONException e) {
            e.printStackTrace();
            return "";
        }
    }
}
