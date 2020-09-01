package org.techtown.sensor;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import static android.hardware.SensorManager.SENSOR_DELAY_GAME;
import static android.hardware.SensorManager.SENSOR_DELAY_NORMAL;
import static android.hardware.SensorManager.SENSOR_DELAY_UI;
import static java.sql.Types.TIME;

public class shakeSensor extends AppCompatActivity {

    SensorManager mSensorManager; // 센서 매니저
    SensorEventListener listener; // 센서 리스너
    Sensor mGyroscope; // 자이로스코프 센서
    Sensor mAccelerometer; // 가속도 센서

    // 자이로스코프 센서 값
    private double roll; // 3차원 x값
    private double pitch; // 3차원 y값
    private double yaw; // 3차원 z값

    private double timestamp = 0.0; // 단위시간
    private double dt;

    // 회전각
    private double rad_to_dgr = 180/Math.PI;
    private static final float NS2S = 1.0f/1000000000.0f;

    TextView axisXT, axisYT, axisZT, tiltText;
    TextView gyroXT, gyroYT, gyroZT;

    Button btGoHome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shake_sensor);

        btGoHome = (Button)findViewById(R.id.btGoHome);
        btGoHome.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent;
                intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
        });

        gyroXT = (TextView)findViewById(R.id.gyroX);
        gyroYT = (TextView)findViewById(R.id.gyroY);
        gyroZT = (TextView)findViewById(R.id.gyroZ);
        axisXT = (TextView) findViewById(R.id.axisX);
        axisYT = (TextView) findViewById(R.id.axisY);
        axisZT = (TextView) findViewById(R.id.axisZ);
        tiltText = (TextView)findViewById(R.id.tiltText);

        // 센서 매니저 생성
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        // 자이로스코프 센서 등록
        mGyroscope = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        // 가속도 센서 등록
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        listener = new SensorEventListener() {
            boolean tilt;

            public void onSensorChanged(SensorEvent sensorEvent) {
                if (sensorEvent.sensor.getType() == Sensor.TYPE_GYROSCOPE) {
                    double gyroX = sensorEvent.values[0];
                    double gyroY = sensorEvent.values[1];
                    double gyroZ = sensorEvent.values[2];

//            x.setText("roll"+String.valueOf(gyroX));
//            y.setText("pitch"+String.valueOf(gyroY));
//            z.setText("yaw"+String.valueOf(gyroZ));

                    // 단위시간 계산
                    dt = (sensorEvent.timestamp - timestamp) + NS2S;
                    timestamp = sensorEvent.timestamp;

                    // 시간 변화시
                    if (dt - timestamp * NS2S != 0) {
                        pitch = pitch + gyroY * dt;
                        roll = roll + gyroX + dt;
                        yaw = yaw + gyroZ * dt;

                        gyroXT.setText("roll" + String.format("%.lf", roll * rad_to_dgr));
                        gyroYT.setText("pitch" + String.format("%.lf", pitch * rad_to_dgr));
                        gyroZT.setText("yaw" + String.format("%.lf", yaw * rad_to_dgr));
                    }
                }

                if (sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                    float axisX = sensorEvent.values[0];
                    float axisY = sensorEvent.values[1];
                    float axisZ = sensorEvent.values[2];

                    if (axisX > 10 || axisX < -10 || axisY > 10 || axisY < -10 || axisZ > 10 || axisZ < -10)
                        tilt = true;
                    else
                        tilt = false;

                    if (tilt) {
                        axisXT.setText("axisX : " + String.valueOf(axisX));
                        axisYT.setText("axisY : " + String.valueOf(axisY));
                        axisZT.setText("axisZ : " + String.valueOf(axisZ));
                        tiltText.setText("it's tilt!!");
                    }
                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int i) {

            }
        };
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 반응 속도 FASTEST > GAME > UI > NORMAL
        mSensorManager.registerListener(listener, mGyroscope, SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(listener, mAccelerometer, SENSOR_DELAY_NORMAL);

    }

    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(listener);
    }

    protected void onStop() {
        super.onStop();
    }
}