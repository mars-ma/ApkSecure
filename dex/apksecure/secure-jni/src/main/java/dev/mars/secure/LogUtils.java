package dev.mars.secure;

import android.util.Log;

import java.util.HashMap;

/**
 * Created by ma.xuanwei on 2017/4/27.
 */

public class LogUtils {
    private static final boolean DEBUG = BuildConfig.DEBUG;
    private static final String TAG = "dev_mars";
    public static void d(String str){
        if(DEBUG){
            Log.d(TAG,str);
        }
    }
}
