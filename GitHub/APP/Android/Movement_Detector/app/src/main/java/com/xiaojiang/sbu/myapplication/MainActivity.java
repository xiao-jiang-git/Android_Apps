package com.xiaojiang.sbu.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.text.format.DateFormat;
import android.util.Log;
import android.widget.TextView;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private Sensor stepDetector;//单次步伐传感器

    SensorManager mSensorManager;//管理器实例
    Sensor stepCounter;//传感器
    float x,y,z,a,b,c;
    float mSteps = 0;//步数
    float tempSteps = 0;//步数
    TextView steps;//显示步数
    TextView time;//显示时间
    TextView speed,states,HumanStates;
    Double Speed = 0.0;
    int tempnum;



    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        new TimeThread().start();
        // 获取SensorManager管理器实例
        mSensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);

        steps = (TextView)findViewById(R.id.StepCounter);
        time = (TextView)findViewById(R.id.time);
        speed = (TextView)findViewById(R.id.speed);
        states = (TextView)findViewById(R.id.States);

        // 为重力传感器注册监听器
        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY), SensorManager.SENSOR_DELAY_GAME);

        // 获取计步器sensor
        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER), SensorManager.SENSOR_DELAY_NORMAL);




    }


    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        float[] value = sensorEvent.values;
        int type = sensorEvent.sensor.getType();
        switch (type){
            case Sensor.TYPE_GRAVITY:
                x = value[0];
                y = value[1];
                z = value[2];
                break;
            case Sensor.TYPE_STEP_COUNTER:
                mSteps = value[0];
                steps.setText(""+String.valueOf((int)mSteps)+"steps");
                break;

        }


    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }


    class TimeThread extends Thread {
        @Override
        public void run() {
            do {
                try {
                    tempSteps = mSteps;
                    Thread.sleep(5000);
                    Message msg = new Message();
                    msg.what = 1;  //消息(一个整型值)
                    msg.arg1 = (int)tempSteps;
                    mHandler.sendMessage(msg);// 每隔1秒发送一个msg给mHandler
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } while (true);
        }
    }

    
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    Log.v("**********", ""+((int)mSteps-msg.arg1));
                    Speed = (mSteps-msg.arg1)*0.7*3600/1000/5;
                    HumanStatementJudge(Speed,x,y,z);
                    speed.setText(""+Speed);
                    long sysTime = System.currentTimeMillis();//获取系统时间
                    CharSequence sysTimeStr = DateFormat.format("hh:mm:ss", sysTime);//时间显示格式
                    time.setText(sysTimeStr); //更新时间
                    break;
                default:
                    break;

            }
        }
    };


    public void HumanStatementJudge(double Speed, float x, float y, float z){
        if (0<=Speed && Speed<1.5){
            if(x>7||x<-7||z>0&&z<4||z<0&&z>-4){
                states.setText("You are Standing");
            }else {
                states.setText("You are Sitting");
            }
        }else if(Speed>=1.5&&Speed<5.7){
            states.setText("You are Walking");
        }else if(Speed>=5.2){
            states.setText("you are Running");
        }

    }
    public void save(){
        String data = "Data to save";
        FileOutputStream out = null;
        BufferedWriter writer = null;
        try {
            out = openFileOutput("data", Context.MODE_PRIVATE);
            writer = new BufferedWriter(new OutputStreamWriter(out));
            writer.write(data);
        }catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if(writer!= null){
                    writer.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
