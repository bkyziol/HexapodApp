package com.bkyziol.hexapod.image;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;

public class ImageDecoder extends Thread {

    private final Handler imageHandler;
    private byte[] data;
    private int width;
    private int height;
    private int id;

    public ImageDecoder(Handler imageHandler) {
        this.imageHandler = imageHandler;
    }

    public void decode(byte[] data, int width, int height, int id) {
        this.data = data;
        this.width = width;
        this.height = height;
        this.id = id;
        this.start();
    }

    @Override
    public void run() {
        System.out.println("executed id: " + id);
        if (width == 0 || height == 0) {
            return;
        }
        Bitmap bmp = BitmapFactory.decodeByteArray(data, 0, data.length);
        Bitmap scaledBmp = Bitmap.createScaledBitmap(bmp, width, height, false);
        Message msg = new Message();
        msg.obj = scaledBmp;
        imageHandler.sendMessage(msg);
    }
}
