package com.singlecode.audiorecord.project1;

import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import java.io.File;

/**
 * 创建时间：2020/10/12
 * 创建人：singleCode
 * 功能描述：
 **/
public class AudioFileUtils {
    private static String TAG = AudioFileUtils.class.getSimpleName();

    public static String getPcmFileAbsolutePath(Context context,String fileName) {
        String folder = "";
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            folder = context.getExternalCacheDir() + "/pcm/";
        } else {
            if(isSdcardExit()){
                folder = Environment.getExternalStorageDirectory() + File.separator + "pcm" + File.separator;
            }else {
                folder = context.getExternalCacheDir() + "/pcm/";
            }
        }
        File dir = new File(folder);
        createFolder(dir);
        if(!TextUtils.isEmpty(fileName)&& !fileName.endsWith(".pcm")){
            fileName = fileName+".pcm";
        }
        File file = new File(dir, fileName);
//        delFileOrFolder(file);
        Log.d(TAG, "getPcmFileAbsolutePath: "+file.getAbsolutePath());
        return file.getAbsolutePath();
    }

    public static String getWavFileAbsolutePath(Context context,String fileName) {
        String folder = "";
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            folder = context.getExternalCacheDir() + "/wav/";
        } else {
            if(isSdcardExit()){
                folder = Environment.getExternalStorageDirectory() + File.separator + "wav" + File.separator;
            }else {
                folder = context.getExternalCacheDir() + "/wav/";
            }
        }
        File dir = new File(folder);
        createFolder(dir);
        if(!TextUtils.isEmpty(fileName)&& !fileName.endsWith(".wav")){
            fileName = fileName+".wav";
        }
        File file = new File(dir, fileName);
//        delFileOrFolder(file);
        Log.d(TAG, "getWavFileAbsolutePath: "+file.getAbsolutePath());
        return file.getAbsolutePath();
    }
    private static boolean createFolder(File targetFolder) {
        if (targetFolder.exists()) {
            if (targetFolder.isDirectory()) return true;
            //noinspection ResultOfMethodCallIgnored
            targetFolder.delete();
        }
        return targetFolder.mkdirs();
    }
   private static boolean delFileOrFolder(File file) {
        if (file == null || !file.exists()) {
            // do nothing
        } else if (file.isFile()) {
            file.delete();
        } else if (file.isDirectory()) {
            File[] files = file.listFiles();
            if (files != null) {
                for (File sonFile : files) {
                    delFileOrFolder(sonFile);
                }
            }
            file.delete();
        }
        return true;
    }
    /**
     * 判断是否有外部存储设备sdcard
     * @return true | false
     */
    public static boolean isSdcardExit() {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
            return true;
        else
            return false;
    }
}
