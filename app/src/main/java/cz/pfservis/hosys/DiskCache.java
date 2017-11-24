package cz.pfservis.hosys;


import android.content.Context;
import android.os.Environment;
import android.util.Log;

import org.apache.commons.io.IOUtils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import cz.pfservis.hosys.enums.HosysPage;

import static android.os.Environment.isExternalStorageRemovable;
import static cz.pfservis.hosys.HosysConfig.UTF8;

public class DiskCache {
    public enum FileType {html, css};


    public static boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();

        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }

        return false;
    }

    public static boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();

        if (Environment.MEDIA_MOUNTED.equals(state) || Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }

        return false;
    }

    public static File getDiskCacheFile(HosysPage hosysPage, FileType fileType, Context context) {
        final File cachePath = isExternalStorageWritable() || !isExternalStorageRemovable()
                ? context.getExternalCacheDir() : context.getCacheDir();

        return new File(cachePath, hosysPage.toString() + "." + fileType.toString());
    }

    public static String readCacheData(File cacheFile, Context context) {
        if (!cacheFile.exists()) {
            return null;
        }

        InputStream is = null;

        try {
            is = new BufferedInputStream(new FileInputStream(cacheFile));

            return IOUtils.toString(is, HosysConfig.UTF8);
        }
        catch (IOException e) {
            Log.e("Exception", "File read failed: " + e.toString());
            return null;
        } finally {
            IOUtils.closeQuietly(is);
        }
    }

    public static String readCacheHtmlData(HosysPage hosysPage, Context context) {
        File cacheFile = getDiskCacheFile(hosysPage, FileType.html, context);

        return readCacheData(cacheFile, context);
    }

    public static String readCacheCssData(HosysPage hosysPage, Context context) {
        File cacheFile = getDiskCacheFile(hosysPage, FileType.css, context);

        return readCacheData(cacheFile, context);
    }

    public static void writeCacheData(String htmlData, File cacheFile, Context context) {
        if (cacheFile.exists()) {
            cacheFile.delete();
        }

        try {
            OutputStream os = new BufferedOutputStream(new FileOutputStream(cacheFile));

            IOUtils.write(htmlData, os, UTF8);

            IOUtils.closeQuietly(os);
        }
        catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }

    public static void writeCacheHtmlData(String fileData, HosysPage hosysPage, Context context) {
        File cacheFile = getDiskCacheFile(hosysPage, FileType.html, context);

        writeCacheData(fileData, cacheFile, context);
    }

    public static void writeCacheCssData(String fileData, HosysPage hosysPage, Context context) {
        File cacheFile = getDiskCacheFile(hosysPage, FileType.css, context);

        writeCacheData(fileData, cacheFile, context);
    }
}