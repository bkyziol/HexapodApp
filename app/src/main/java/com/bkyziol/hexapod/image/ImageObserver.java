package com.bkyziol.hexapod.image;

public interface ImageObserver {
    void updateCameraImageView(byte[] data);
}