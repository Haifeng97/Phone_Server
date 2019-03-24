package com.example.haifeng.test2;

import android.accessibilityservice.AccessibilityService;
import android.app.Activity;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.TimeUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.net.ServerSocket;
import java.net.Socket;


public class Test extends AppCompatActivity implements View.OnClickListener {

    private SensorManager sensorManager;
    TextView tv;
    TextView tv2;
    TextView tv3;
    TextView tv4;
    TextView tv5;
    TextView tv6;
    TextView tv7;
    TextView tv8;
    Button bt1;
    Button bt_send, bt_close;
    EditText edit_port;
    int port;
    NetThread netThread;
    Thread thread;
    Data data;
    //int frequency = 0;
    boolean sendFlag;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test);

        //init
        tv = (TextView) findViewById(R.id.tv);
        tv2 = (TextView) findViewById(R.id.tv2);
        tv3 = (TextView) findViewById(R.id.tv3);
        tv4 = (TextView) findViewById(R.id.tv4);
        tv5 = (TextView) findViewById(R.id.tv5);
        tv6 = (TextView) findViewById(R.id.tv6);
        tv7 = (TextView) findViewById(R.id.tv7);
        tv8 = (TextView) findViewById(R.id.tv8);
        bt1 = (Button) findViewById(R.id.bt1);
        bt1.setOnClickListener(this);
        bt_send = (Button) findViewById(R.id.bt_send);
        bt_send.setOnClickListener(this);
        bt_close = (Button) findViewById(R.id.bt_close);
        bt_close.setOnClickListener(this);
        edit_port = (EditText) findViewById(R.id.edit_port);
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        Sensor sensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        Sensor sensor2 = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        Sensor sensor3 = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        sensorManager.registerListener(listener2, sensor2, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(listener, sensor, SensorManager.SENSOR_DELAY_NORMAL);
        //sensorManager.registerListener(listener3, sensor3, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(listener4, sensor2, SensorManager.SENSOR_DELAY_UI);
        sensorManager.registerListener(listener4, sensor3, SensorManager.SENSOR_DELAY_UI);
        data = new Data();
        //sendFlag = false;
        data.sendFlag = false;
    }
//Destroy
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (sensorManager != null) {
            sensorManager.unregisterListener(listener);
            sensorManager.unregisterListener(listener2);
            //sensorManager.unregisterListener(listener3);
            sensorManager.unregisterListener(listener4);
        }
    }

    private SensorEventListener listener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {
            float lt = sensorEvent.values[0];
            //frequency += 1;
            tv.setText("light level is " + lt + "lx");
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {

        }
    };

    private SensorEventListener listener2 = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {
            tv2.setText("v0 " + sensorEvent.values[0]);
            tv3.setText("v1 " + sensorEvent.values[1]);
            tv4.setText("v2 " + sensorEvent.values[2]);
            data.v0 = "" + sensorEvent.values[0];
            data.v1 = "" + sensorEvent.values[1];
            data.v2 = "" + sensorEvent.values[2];
            data.sendable = true;
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {

        }
    };

//Accelerator and mag
    private SensorEventListener listener4 = new SensorEventListener() {
    float[] aValues = new float[3];
    float[] mValues = new float[3];
        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {
            if (sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                aValues = sensorEvent.values.clone();
            } else if (sensorEvent.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
                mValues = sensorEvent.values.clone();
            }
            float[] R = new float[9];
            float[] values = new float[3];
            SensorManager.getRotationMatrix(R, null, aValues, mValues);
            SensorManager.getOrientation(R, values);
            tv5.setText("v0 is " + Math.toDegrees(values[0]));
            tv6.setText("v1 is " + Math.toDegrees(values[1]));
            tv7.setText("v2 is " + Math.toDegrees(values[2]));
            data.v3 = "" + Math.toDegrees(values[0]);
            data.v4 = "" + Math.toDegrees(values[1]);
            data.v5 = "" + Math.toDegrees(values[2]);
            data.sendable = true;
//            try {
//                Thread.sleep(100);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {

        }
    };

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.bt1) {
            //listen
            port =  Integer.parseInt(edit_port.getText().toString());
            netThread = new NetThread(port, data, tv8);
            thread = new Thread(netThread);
            thread.start();
        } else if (view.getId() == R.id.bt_send) {
            //send
            data.sendFlag = !data.sendFlag;
            if (!sendFlag) {
                tv8.setText("paused");
            } else {
                tv8.setText("continue");
            }
        } else if (view.getId() == R.id.bt_close) {
            //close
            netThread.shutdownConnection();
        }
    }

}
