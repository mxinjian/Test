package com.example.csdl.testapp;

import android.content.Context;
import android.os.Environment;

import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.text.SimpleDateFormat;

public class MyCrashHandler implements Thread.UncaughtExceptionHandler
{

    private static MyCrashHandler myCrashHandler ;
    private Context context;

    private SimpleDateFormat dataFormat = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");

    private MyCrashHandler()
    {

    }
    public static synchronized MyCrashHandler getInstance(){
        if(myCrashHandler!=null){
            return myCrashHandler;
        }else {
            myCrashHandler  = new MyCrashHandler();
            return myCrashHandler;
        }
    }
    public void init(Context context){
        this.context = context;

    }

    public void uncaughtException(Thread arg0, Throwable arg1) {

        String errorinfo = getErrorInfo(arg1);

        SimpleDateFormat sDateFormat=new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        String date    =    sDateFormat.format(new    java.util.Date());
        SimpleDateFormat sDateFormat2=new SimpleDateFormat("yyyyMMdd");
        String date2    =    sDateFormat2.format(new    java.util.Date());
        MyCrashHandler.writeFileSdcard(date2,date+":"+errorinfo);

    }

    public static void writeFileSdcard(String fileName, String message)
    {
        try
        {
        String path= Environment.getExternalStorageDirectory().toString();
         FileOutputStream fout = new FileOutputStream(path+"/"+fileName,true);
         byte [] bytes = message.getBytes();
         fout.write(bytes);
          fout.close();
         }

        catch(Exception e)
        {
        }
    }
    private String getErrorInfo(Throwable arg1) {
        Writer writer = new StringWriter();
        PrintWriter pw = new PrintWriter(writer);
        arg1.printStackTrace(pw);
        pw.close();
        String error= writer.toString();
        return error;
    }

}
