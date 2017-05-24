package dev.mars.testapplication;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.ArrayMap;
import android.util.Log;
import android.view.View;

import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class MainActivity extends AppCompatActivity {
    Methods2 methods2 =new Methods2();
    WeakReference testWr;
    Object abc =null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        methods2.l();
        setContentView(R.layout.activity_main);
        LogUtils.d("MainActivity onCreate");
        ((MyApplication)getApplication()).test(this.getClass().getName());









    }


    /**
     * @deprecated 无效的，请使用DDMS的Gause gc
     * @param view
     */
    public void onGc(View view) {
        LogUtils.d("开始GC");
        System.gc();
        LogUtils.d("GC完毕");
    }

    public void showLoadedApk(View view) {
        try {
            Class ActivityThreadClass = Class.forName("android.app.ActivityThread");
            Method currentActivityThread=ActivityThreadClass.getDeclaredMethod("currentActivityThread");
            Object activityThread = currentActivityThread.invoke(null);
            Field mPackagesField = ActivityThreadClass.getDeclaredField("mPackages");
            mPackagesField.setAccessible(true);
            Map<String,WeakReference> mPackages;
            if(Build.VERSION.SDK_INT> Build.VERSION_CODES.KITKAT){
                mPackages = (ArrayMap<String, WeakReference>) mPackagesField.get(activityThread);
            }else{
                mPackages = (HashMap<String, WeakReference>) mPackagesField.get(activityThread);
            }
            Set<String> keys = mPackages.keySet();
            for(String k : keys){
                LogUtils.d("key : "+k);
            }

            LogUtils.d("current package name : "+getPackageName());
            WeakReference wr = mPackages.get(getPackageName());
            if(wr==null){
                LogUtils.d("WeakReference is null");
            }else{
                Object loadedApk = wr.get();
                if(loadedApk!=null){
                    LogUtils.d("loadedApk is not null");
                }else{
                    LogUtils.d("loadedApk is null");
                }
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
    }

    public void generateNewWeakReference(View view) {
        if(abc==null){
            abc=new Object();
        }
        testWr = new WeakReference(abc);
    }

    public void showWeakReference(View view) {
        if(testWr==null){
            LogUtils.d("wr is null");
        }else{
            Object obj = testWr.get();
            if(obj==null){
                LogUtils.d("obj = null");
            }else{
                LogUtils.d("obj is "+obj.toString());
            }
        }
    }

    public void removeStrongReference(View view) {
        abc=null;
    }

    public void useContentProvider(View view) {
        ContentValues cv = new ContentValues();
        cv.put("aa","bb");
        ContentResolver cr = getContentResolver();
        String authority = "content://dev.mars";
        Uri uri = Uri.parse(authority);
        cr.insert(uri,cv);
    }

    public void useService(View view) {
         Intent intent = new Intent(MainActivity.this,MyService.class);
        startService(intent);
    }

    public void sendBroadCast(View view) {
        Intent broadcastIntent = new Intent("dev_mars_broadcast_test");
        sendBroadcast(broadcastIntent);
    }
}
