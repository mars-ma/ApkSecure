package dev.mars.secure;

import android.app.Application;
import android.content.ContentProvider;
import android.content.Context;
import android.os.Build;
import android.util.ArrayMap;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Iterator;


public class ProxyApplication extends Application {

    static {
        System.loadLibrary("secret");
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        try {
            onAttachBaseContext(Build.VERSION.SDK_INT);
        } catch (Exception e) {
            LogUtils.d(e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void onCreate() {//得到ActivityThread对象
        onShellCreate(Build.VERSION.SDK_INT);
    }

    private native void onAttachBaseContext(int sdkInt) throws Exception;

    private native void onShellCreate(int sdkInt);

}
