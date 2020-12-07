package com.example.file_a;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;


public class Utils {
    static public byte[] readFile(InputStream inputStream) {
        try {
            return getBytes(inputStream);
        } catch (FileNotFoundException e) {
            return new byte[0];
        } catch (IOException e) {
            return new byte[0];
        }
    }

    public static byte[] getBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];

        int len;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }
        return byteBuffer.toByteArray();
    }

    static public Bitmap heatMap(InputStream inputStream) {
        int x;
        int y;

        int resolution = 256;

        int[] colors = new int[]{
                Color.rgb(0, 0, 0),
                Color.rgb(6, 7, 28),
                Color.rgb(39, 21, 52),
                Color.rgb(48, 23, 58),

                Color.rgb(58, 26, 65),
                Color.rgb(69, 28, 71),
                Color.rgb(84, 30, 78),
                Color.rgb(122, 31, 89),

                Color.rgb(152, 27, 91),
                Color.rgb(191, 22, 84),
                Color.rgb(242, 111, 76),
                Color.rgb(246, 171, 131),

                Color.rgb(246, 180, 143),
                Color.rgb(246, 188, 153),
                Color.rgb(249, 224, 205),
                Color.rgb(250, 235, 221)
        };


        ByteA[][] bytesA = new ByteA[resolution][resolution];

        for (int bx = 0; bx < resolution; bx++) {
            for (int by = 0; by < resolution; by++) {
                bytesA[bx][by] = new ByteA(bx, by);
            }
        }

        byte[] bytes = readFile(inputStream);

        for (int i = 0; i < bytes.length - 1; i++) {
            x = bytes[i] & 0xff;
            y = bytes[i + 1] & 0xff;
            bytesA[x][y].add();
        }


        float maxPixelValue = 0;
        float minPixelValue = 2147483647;
        float sumPixelValue = 0;
        int curPixelValue;
        float avgPixelValues;

        for (ByteA[] bs : bytesA) {
            for (ByteA b : bs) {
                sumPixelValue += b.amount;
                if (maxPixelValue < b.amount) {
                    maxPixelValue = b.amount;
                }
                if (minPixelValue > b.amount) {
                    minPixelValue = b.amount;
                }
            }
        }

        avgPixelValues = sumPixelValue / (resolution * resolution);
        if (maxPixelValue > avgPixelValues * colors.length) {
            maxPixelValue = avgPixelValues * colors.length;
        }
        if (minPixelValue < avgPixelValues / colors.length) {
            minPixelValue = avgPixelValues / colors.length;
        }


        float[] colorThresholds = new float[colors.length + 1];
        colorThresholds[0] = 0;
        float colorThresholdsInc = (maxPixelValue - minPixelValue) / (float) colors.length;

        for (int i = 1; i < colors.length; i++) {
            colorThresholds[i] = colorThresholdsInc * i;
        }

        int curPixelColor;
        Bitmap bitmapHeatMap = Bitmap.createBitmap(resolution, resolution, Bitmap.Config.ARGB_8888);

        for (int bx = 0; bx < resolution; bx++) {
            for (int by = 0; by < resolution; by++) {
                curPixelValue = bytesA[bx][by].amount;
                curPixelColor = colors[0];
                for (int i = 0; i < colors.length; i++) {
                    if (colorThresholds[i] <= curPixelValue &&
                            curPixelValue <= colorThresholds[i + 1]) {
                        curPixelColor = colors[i];
                    }
                }
                bitmapHeatMap.setPixel(bx, by, curPixelColor);
            }
        }

        return Bitmap.createScaledBitmap(
                bitmapHeatMap,
                bitmapHeatMap.getWidth() * 8,
                bitmapHeatMap.getHeight() * 8,
                false
        );
    }

    public static long[] countBits(InputStream inputStream) {
        byte[] bytes = readFile(inputStream);
        long[] bitsAmount = {0, 0};

        for (byte b : bytes) {
            bitsAmount[0] += 8 - Integer.bitCount(b) & 0xff;
            bitsAmount[1] += Integer.bitCount(b) & 0xff;
        }

        return bitsAmount;
    }

    public static long[] countBytes(InputStream inputStream) {
        byte[] bytes = readFile(inputStream);
        int byteSize = 256;

        long[] bytesAmount = new long[byteSize];
        for (int i = 0; i < byteSize; i++) {
            bytesAmount[i] = 0;
        }

        for (byte b : bytes) {
            bytesAmount[b & 0xff] += 1;
        }

        return bytesAmount;
    }

}