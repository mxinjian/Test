package com.example.csdl.testapp;

import android.Manifest;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;
import java.util.Scanner;

/**
 * Created by csdl on 2018/6/8.
 */

public class MyJobService extends JobService {
    private final String TAG = getClass().getName();
    private Context context;
    private LocationManager locationManager;
    private String provider;
    String loc;
    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
           /* Toast.makeText(MyJobService.this, "MyJobService", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "MyJobService start--------");
            JobParameters param = (JobParameters) msg.obj;
            jobFinished(param, true);*/


            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            //获取当前可用的位置控制器
            List<String> list = locationManager.getProviders(true);

            if (list.contains(LocationManager.GPS_PROVIDER)) {
                //是否为GPS位置控制器
                provider = LocationManager.GPS_PROVIDER;
            } else if (list.contains(LocationManager.NETWORK_PROVIDER)) {
                //是否为网络位置控制器
                provider = LocationManager.NETWORK_PROVIDER;

            } else {
                /*Toast.makeText(this, "请检查网络或GPS是否打开",
                        Toast.LENGTH_LONG).show();*/
               /* return;*/
            }


            if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                Toast.makeText(getApplicationContext(),"定位权限未打开",Toast.LENGTH_SHORT).show();
            }else{
                Location location = locationManager.getLastKnownLocation(provider);
                if (location != null) {
                    //获取当前位置，这里只用到了经纬度
                     loc = "纬度为：" + location.getLatitude() + ",经度为："
                            + location.getLongitude();
                    Log.d(TAG,loc);
                }

//绑定定位事件，监听位置是否改变
//第一个参数为控制器类型第二个参数为监听位置变化的时间间隔（单位：毫秒）
//第三个参数为位置变化的间隔（单位：米）第四个参数为位置监听器
                locationManager.requestLocationUpdates(provider, 2000, 2,
                        locationListener);
                new ClientSocket().start();
            }

            JobParameters param = (JobParameters) msg.obj;
            jobFinished(param, true);
            return true;
        }
    });


    LocationListener locationListener = new LocationListener() {

        @Override
        public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onProviderEnabled(String arg0) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onProviderDisabled(String arg0) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onLocationChanged(Location arg0) {
            // TODO Auto-generated method stub
            // 更新当前经纬度

        }
    };
    public class ClientSocket extends Thread {

        @Override
        public void run() {
            try {
                Socket socket = new Socket("192.168.1.112", 60000);
                OutputStream out = socket.getOutputStream();
                //OutputStreamWriter osw = new OutputStreamWriter(out, true);
                OutputStreamWriter osw = new OutputStreamWriter(out, "UTF-8");
                PrintWriter pw = new PrintWriter(osw, true);
                pw.println(loc);
                /*//pw.println("你好！服务器！");
                //创建Scanner读取用户输入内容
                Scanner scanner = new Scanner(System.in);
                while(true){
                    //scan.nextLine();
                    pw.println(scanner.nextLine());
                }*/
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public boolean onStartJob(JobParameters params) {
        Message m = Message.obtain();
        m.obj = params;
        handler.sendMessage(m);
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        handler.removeCallbacksAndMessages(null);
        return false;
    }
}
