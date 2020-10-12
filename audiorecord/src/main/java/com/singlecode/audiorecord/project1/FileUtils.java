package com.singlecode.audiorecord.project1;

import android.os.Environment;

import java.io.File;

/**
 * 创建时间：2020/10/12
 * 创建人：singleCode
 * 功能描述：
 **/
public class FileUtils {
    public static String rootPath = Environment.getExternalStorageDirectory().getAbsolutePath();

    public static String getPcmFileAbsolutePath(String fileName) {
        return rootPath+"/pcm/"+fileName+".pcm";
    }

    public static String getWavFileAbsolutePath(String fileName) {
        return rootPath+"/wav/"+fileName+".wav";
    }
}
