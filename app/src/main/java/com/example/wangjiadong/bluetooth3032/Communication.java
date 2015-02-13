package com.example.wangjiadong.bluetooth3032;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;


public class Communication extends Activity {
    private int connection_status = 0;
    private BluetoothDevice bt_device;
    private BluetoothSocket bt_socket;
    private InputStream is;
    private OutputStream os;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent  = getIntent();
        String device = intent.getStringExtra("Device");
        setContentView(R.layout.activity_communication);
        Button send = (Button) findViewById(R.id.send);
        Log.d("device", device);
        connect_device(device);
        try
        {
            is = bt_socket.getInputStream();
            os = bt_socket.getOutputStream();
        }catch(IOException e)
        {

        }
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText et = (EditText) findViewById(R.id.message);
                String message = et.getText().toString();
                Log.d("info", "interrupted");
            }
        });
    }
    private void connect_device(String device)
    {
        int start = device.indexOf('|');
        String mac = device.substring(start+1);
        Log.d("MAC", mac);
        bt_device = BluetoothAdapter.getDefaultAdapter().getRemoteDevice(mac);
        Log.d("info", "connection start");
        try {

            bt_socket = bt_device.createRfcommSocketToServiceRecord(UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"));
        }
        catch(IOException e)
        {
            Log.e("connection failed", "connection failed");
        }
        Log.d("info", "connection succeeded"
        );
        try {
            bt_socket.connect();
            Log.e("Tag", " BT connection established, data transfer link open.");
            manage_socket(bt_socket);//自定义函数进行蓝牙通信处理

        } catch (IOException e) {
            Log.e("Tag", " Connection failed.", e);
            setTitle("连接失败..");
        }
    }

    private void manage_socket(BluetoothSocket bt_socket)
    {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.communication, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
