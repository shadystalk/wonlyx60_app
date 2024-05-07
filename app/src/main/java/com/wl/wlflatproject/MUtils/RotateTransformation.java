package com.wl.wlflatproject.MUtils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;

import androidx.annotation.NonNull;

import com.bumptech.glide.load.Transformation;
import com.bumptech.glide.load.engine.Resource;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapResource;

import java.security.MessageDigest;


/**
 * @Author zhuobaolian
 * @Date 15:28
 */
public class RotateTransformation implements Transformation<Bitmap> {

    private float rotateRotationAngle;
    private BitmapPool bitmapPool;
    public RotateTransformation(float rotateRotationAngle,BitmapPool bitmapPool) {
        this.rotateRotationAngle = rotateRotationAngle;
        this.bitmapPool=bitmapPool;
    }

    @Override
    public void updateDiskCacheKey(MessageDigest messageDigest) {
        // No-op
    }

    @NonNull
    @Override
    public Resource<Bitmap> transform(@NonNull Context context, @NonNull Resource resource, int outWidth, int outHeight) {
        Bitmap sourceBitmap = (Bitmap) resource.get();

        Matrix matrix = new Matrix();
        matrix.postRotate(rotateRotationAngle);
        Bitmap rotatedBitmap = Bitmap.createBitmap(sourceBitmap, 0, 0, sourceBitmap.getWidth(), sourceBitmap.getHeight(), matrix, true);

        if (rotatedBitmap != sourceBitmap) {
            sourceBitmap.recycle();
        }
        return BitmapResource.obtain(rotatedBitmap, bitmapPool);
    }
}
