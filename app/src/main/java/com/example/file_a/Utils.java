package com.example.file_a;

import android.graphics.Bitmap;
import android.graphics.Color;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;


public class Utils {
    static public byte[] readFile(String filePath){
        File file = new File(filePath);
        int size = (int) file.length();
        byte[] bytes = new byte[size];
        try {
            BufferedInputStream buf = new BufferedInputStream(new FileInputStream(file));
            buf.read(bytes, 0, bytes.length);
            buf.close();
            return bytes;
        } catch (FileNotFoundException e) {
            return bytes;
        } catch (IOException e) {
            return bytes;
        }
    }

    static public Bitmap heatMap (String fileName){
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

        for(int bx = 0; bx < resolution; bx++){
            for(int by = 0; by < resolution; by++) {
                bytesA[bx][by] = new ByteA(bx, by);
            }
        }

        byte[] bytes = readFile(fileName);

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

        for (ByteA[] bs:bytesA){
            for (ByteA b:bs){
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
        if (minPixelValue < avgPixelValues/ colors.length) {
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
}