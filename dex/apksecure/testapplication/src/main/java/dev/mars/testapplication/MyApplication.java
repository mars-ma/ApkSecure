package dev.mars.testapplication;

import android.app.Application;
import android.util.Log;

/**
 * Created by ma.xuanwei on 2017/5/11.
 */

public class MyApplication extends Application {

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
