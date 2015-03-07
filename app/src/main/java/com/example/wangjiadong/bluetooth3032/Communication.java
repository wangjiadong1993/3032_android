package com.example.wangjiadong.bluetooth3032;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Looper;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.UUID;
import android.os.Parcelable;

import com.activeandroid.ActiveAndroid;

public class Communication extends Activity{
    private int connection_status = 0;
    private BluetoothDevice bt_device;
    private BluetoothSocket bt_socket;
    private String name;
    private ArrayList msg_list;
    private ArrayList msg_back;
    private ArrayAdapter msg_adt;
    private IORUN io_thread=null;
    private ListView lv =null;
    private String temp="";
    private Handler mHandler;
    private SQLiteDatabase db;
    private Shoeload sl;
    private void show_temp(String input){
        Log.d("str len", Integer.toString(input.length()));
        if(input.equals("\n"))
        {
            Log.d("total", temp+'\n');
            msg_list.add(temp);
            //msg_adt.notifyDataSetChanged();
            temp = "";
            Log.d("main thread", Looper.getMainLooper().getThread().toString());
            Log.d("current thread", Thread.currentThread().toString());


        }
        else
        {
            temp =temp + input.charAt(0);
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent  = getIntent();
        String device = intent.getStringExtra("Device");
        setContentView(R.layout.activity_communication);
        Button send = (Button) findViewById(R.id.send);
        Log.d("device", device);
        connect_device(device);

        db = openOrCreateDatabase("shoeload.db", Context.MODE_PRIVATE, null);
        refresh_db();


        lv = (ListView) findViewById(R.id.list_msg);
        msg_list = new ArrayList();
        msg_back = new ArrayList();
        msg_list.add(0, "hello world");
        msg_adt = new ArrayAdapter(this, android.R.layout.simple_list_item_1, msg_list);
        lv.setAdapter(msg_adt);


        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText et = (EditText) findViewById(R.id.message);
                String message = et.getText().toString();
                Log.d("info", "interrupted");
                if(message != null)
                {
                    io_thread.write((message).getBytes());
                    msg_list.add(0, "me: "+message);
                    msg_adt.notifyDataSetChanged();
                    et.setText(null);
                }
            }
        });
        mHandler = new Handler(){
            @Override
            public void handleMessage(Message msg){
                double a_db;
                double b_db;
                double c_db;
                double d_db;
                int e_db;
                Log.d("info", "get a message");
                Log.d("Msg", msg.toString());
                Log.d("current thread", Thread.currentThread().toString());
                char a = (char)msg.what;
                if(a == '\r') {
                    Scanner sc = new Scanner(temp);
                    a_db = sc.nextDouble();
                    b_db = sc.nextDouble();
                    c_db = sc.nextDouble();
                    d_db = sc.nextDouble();
                    e_db = sc.nextInt();
                    sl = new Shoeload(a_db, b_db, c_db, d_db, e_db);
                    insert_data(sl);
                    temp = name+": " + temp;
                    update_view(temp);
                    Log.d("print", temp);
                    temp = "";
                    Log.d("array list", msg_list.toString());
                }
                else{
                    temp = temp + (char) msg.what;
                }

            }
        };
    }
    private void connect_device(String device)
    {
        int start = device.indexOf('|');
        String mac = device.substring(start+1);
        name = device.substring(0, start-1);
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

    private void update_view(String temp)
    {
        msg_list.add(0, temp);
        msg_adt = new ArrayAdapter(this, android.R.layout.simple_list_item_1, msg_list);
        lv.invalidate();
        lv.setAdapter(msg_adt);


    }

    public void refresh_db()
    {
        db.execSQL("DROP TABLE IF EXISTS shoeload");
        db.execSQL("CREATE TABLE shoeload (_id INTEGER PRIMARY KEY AUTOINCREMENT, front DOUBLE, middle DOUBLE, rare DOUBLE, temp DOUBLE, time DOUBLE)");
    }
    public void insert_data(Shoeload sl)
    {
        db.execSQL("INSERT INTO shoeload VALUES (NULL, ?, ?, ? , ?, ?)", new Object[]{sl.front, sl.middle, sl.rare, sl.temp, sl.time});
    }
    public void show_all_data()
    {
        Cursor c = db.rawQuery("SELECT * FROM shoeload WHERE front >= ?", new String[]{"0.0"});
        while(c.moveToNext()){
            int _id = c.getInt(c.getColumnIndex("_id"));
            double middle = c.getDouble(c.getColumnIndex("middle"));
            Log.d("result from db", Integer.toString(_id)+"  "+Double.toString(middle));
        }
        c.close();
    }



    private void manage_socket(BluetoothSocket bt_socket)
    {
        io_thread = new IORUN(bt_socket);
        io_thread.start();
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



    private class IORUN extends Thread {
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;
        //private String output;
        public IORUN(BluetoothSocket socket) {
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;
            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) { }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run() {
            byte[] buffer = new byte[1024];  // buffer store for the stream
            int bytes; // bytes returned from read()
            String output ;
            Log.d("Info", "it is the sub thread");

            while (true) {
                try {
                    // Read from the InputStream
                    bytes = mmInStream.read();
                    if(bytes != 0 && bytes != -1)
                    {
                        Log.d("input found", String.valueOf((char)bytes));
                        mHandler.obtainMessage(bytes).sendToTarget();
                    }
                } catch (IOException e) {
                    break;
                }
            }
        }

        /* Call this from the main activity to send data to the remote device */
        public void write(byte[] bytes) {
            try {
                for(int i = 0; i<bytes.length; i++)
                    mmOutStream.write(bytes[i]);
            } catch (IOException e) { }
        }

        /* Call this from the main activity to shutdown the connection */
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) { }
        }
    }
}
