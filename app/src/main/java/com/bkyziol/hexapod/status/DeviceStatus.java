package com.bkyziol.hexapod.status;

import org.json.JSONException;
import org.json.JSONObject;

public class DeviceStatus {

    private static boolean bodyIsMoving = false;
    private static boolean headIsMoving = false;

    private static boolean statusReportNeeded = true;

    private static String bodyMovement = "STAND_BY";
    private static String headMovement = "STAND_BY";
    private static boolean sleepMode = true;

    private static boolean fastMode = false;
    private static int speedFast = 100;
    private static int speedSlow = 20;

    private static int strideLength = 70;
    private static int headSpeed = 50;

    private static boolean cameraEnabled = false;
    private static boolean faceDetectionEnabled = false;
    private static int videoQuality = 3;
    private static int videoFPS = 4;


    public static boolean isHexapodMoving() {
        return bodyIsMoving;
    }

    public static boolean isHeadMoving() {
        return headIsMoving;
    }

    public static boolean isStatusReportNeeded() {
        return statusReportNeeded;
    }

    public static void setStatusReportNeeded(boolean statusReportNeeded) {
        DeviceStatus.statusReportNeeded = statusReportNeeded;
    }

    public static String getBodyMovement() {
        return bodyMovement;
    }

    public static void setBodyMovement(String bodyMovement) {
        DeviceStatus.bodyMovement = bodyMovement;
        bodyIsMoving = true;
    }

    public static String getHeadMovement() {
        return headMovement;
    }

    public static void setHeadMovement(String headMovement) {
        DeviceStatus.headMovement = headMovement;
        headIsMoving = true;
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

    private static int getBodySpeed() {
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

    public static int getHeadSpeed() {
        return headSpeed;
    }

    public static void setHeadSpeed(int headSpeed) {
        DeviceStatus.headSpeed = headSpeed;
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
            jsonObject.put("bodyMovement", bodyMovement);
            if (bodyMovement.equals("STAND_BY")) {
                bodyIsMoving = false;
            }
            jsonObject.put("headMovement", headMovement);
            if (headMovement.equals("STAND_BY")) {
                headIsMoving = false;
            }
            jsonObject.put("statusReportNeeded", statusReportNeeded);
            statusReportNeeded = false;
            jsonObject.put("cameraEnabled", cameraEnabled);
            jsonObject.put("faceDetectionEnabled", faceDetectionEnabled);
            jsonObject.put("bodySpeed", getBodySpeed());
            jsonObject.put("strideLength", strideLength);
            jsonObject.put("headSpeed", headSpeed);
            jsonObject.put("videoQuality", videoQuality);
            jsonObject.put("videoFPS", videoFPS);
            return jsonObject.toString();
        } catch (JSONException e) {
            e.printStackTrace();
            return "";
        }
    }
}
