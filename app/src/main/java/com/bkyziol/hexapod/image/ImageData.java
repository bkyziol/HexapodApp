package com.bkyziol.hexapod.image;

import java.util.ArrayList;

public class ImageData {

    private static ArrayList<ImageObserver> imageObservers = new ArrayList<>();
    private static byte[] imageData;

    public static void register(ImageObserver newImageObserver) {
        imageObservers.add(newImageObserver);
    }

    public static void unregister(ImageObserver deleteImageObserver) {
        int observerIndex = imageObservers.indexOf(deleteImageObserver);
        imageObservers.remove(observerIndex);
    }

    public static void setImageData(byte[] imageData) {
        ImageData.imageData = imageData;
        notifyObserver();
    }

    public static void notifyObserver() {
        for (ImageObserver imageObserver : imageObservers) {
            imageObserver.update(imageData);
        }
    }
}
