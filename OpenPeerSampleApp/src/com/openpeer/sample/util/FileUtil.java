package com.openpeer.sample.util;

import android.graphics.Bitmap;

import com.openpeer.sample.PhotoHelper;

import java.io.BufferedOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by brucexia on 3/13/15.
 */
public class FileUtil {
    public static void saveFile(String path, byte[] data) {
        try {
            FileOutputStream stream = new FileOutputStream(path);


            BufferedOutputStream bos = new
                    BufferedOutputStream(stream);
            bos.write(data);
            bos.flush();
            bos.close();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }
}
