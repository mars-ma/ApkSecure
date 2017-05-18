package dev.mars.testapplication;

import android.app.Application;
import android.content.Context;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;


/**
 * Created by ma.xuanwei on 2017/5/11.
 */

public class MyApplication extends Application {
    //public static Methods methods = new Methods();
   // public static Methods2 methods2=new Methods2();
    static {
        System.loadLibrary("reflect");
        test();
    }

    private static native void test();

    /*static {
        try {
            Class methods2Class = Class.forName("dev.mars.testapplication.Methods2");
            Method l = methods2Class.getDeclaredMethod("l");
            l.invoke(methods2Class.newInstance());
        } catch (Exception e){
            e.printStackTrace();
        }
    }*/


    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        LogUtils.d(getClass().getCanonicalName()+" attachBaseContext");
       // methods2.l();
       // MultiDex.install(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        LogUtils.d(this.getClass().getName()+" onCreate");
        instance = this;
    }

    private static MyApplication instance;

    public static MyApplication getInstance(){
        return instance;
    }

    public static void exit(){
        instance=null;
    }

    private static final String TEST_WORD = "A field from "+MyApplication.class.getName();

    public void test(String str){
        LogUtils.d(str+" : "+TEST_WORD);
    }
}
