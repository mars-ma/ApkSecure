package dev.mars.testapplication;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.net.URI;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {
    Methods2 methods2 =new Methods2();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        methods2.l();
        setContentView(R.layout.activity_main);
        LogUtils.d("MainActivity onCreate");
        ((MyApplication)getApplication()).test(this.getClass().getName());

        Intent intent = new Intent(MainActivity.this,MyService.class);
        startService(intent);

        Intent broadcastIntent = new Intent("dev_mars_broadcast_test");
        sendBroadcast(broadcastIntent);

        ContentValues cv = new ContentValues();
        cv.put("aa","bb");
        ContentResolver cr = getContentResolver();
        String authority = "content://dev.mars";
        Uri uri = Uri.parse(authority);
        cr.insert(uri,cv);

    }
}
