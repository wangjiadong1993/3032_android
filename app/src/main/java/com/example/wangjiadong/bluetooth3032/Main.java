package com.example.wangjiadong.bluetooth3032;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;


public class Main extends Activity {
    private BluetoothAdapter BA;
    private int bt_status= 0;
    private Set<BluetoothDevice> pairedDevices;
    private ArrayList lstDevices;
    private ArrayAdapter adtDevices;
    private BluetoothDevice bt_device;
    private BluetoothSocket bt_socket;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        //adding the bluetooth adaptor;
        BA = BluetoothAdapter.getDefaultAdapter();
        final Button show_devices = (Button) findViewById(R.id.show_devices);
        Button show = (Button) findViewById(R.id.show_pair);




        //check availability and status
        if(BA == null)
            bt_status = -1;
        else{
            if(BA.isEnabled() == true)
                bt_status = 1;
            else
                bt_status = 0;
        }



        Button button =(Button) findViewById(R.id.bluetooth);
        //visibility
        if(bt_status == -1)
            button.setVisibility(View.INVISIBLE);
        else
            button.setVisibility(View.VISIBLE);
        button.setOnClickListener(new View.OnClickListener() {
            Intent turnOn;
            @Override
            public void onClick(View v) {
                if(bt_status == 0) {
                    turnOn  = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    bt_status = 1;
                    startActivityForResult(turnOn, 0);
                }else{
                    bt_status = 0;
                    BA.disable();//directly
                }
                set_visible(show_devices);
            }
        });


        //
        final ListView lv1 = (ListView) findViewById(R.id.listView2);
        lstDevices = new ArrayList();
        adtDevices = new ArrayAdapter(this, android.R.layout.simple_list_item_1, lstDevices);
        lv1.setAdapter(adtDevices);

       // lstDevices.add("initial one");
       // adtDevices.notifyDataSetChanged();

        lv1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d("connect", lstDevices.get(position).toString());
                connect_device(lstDevices.get(position).toString());
            }
        });




        final ListView lv =  (ListView) findViewById(R.id.listView1);
        ArrayList list = new ArrayList();
        pairedDevices = BA.getBondedDevices();
        for(BluetoothDevice bt : pairedDevices)
            list.add(bt.getName());
        final ArrayAdapter adapter = new ArrayAdapter(this,android.R.layout.simple_list_item_1, list);
        lv.setAdapter(adapter);


        show.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if(lv.getVisibility() == View.VISIBLE)
                {
                    Log.d("status", "hidden");
                    lv.setVisibility(View.INVISIBLE);
                }else{
                    Log.d("status", "shown");
                    lv.setVisibility(View.VISIBLE);
                }
            }
        });

        show_devices.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BA.startDiscovery();
            }
        });


    }


    private void set_visible(Button show_devices){
        if(bt_status == 1)
        {
            show_devices.setVisibility(View.VISIBLE);
        }else{
            show_devices.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    public void onResume(){
        super.onResume();
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(searchDevices, filter);
    }

    private void connect_device(String device)
    {
        Intent intent_new= new Intent(this, Communication.class);
        intent_new.putExtra("Device", device);
        startActivity(intent_new);

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
    private BroadcastReceiver searchDevices = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Bundle b = intent.getExtras();
            Object[] lstName = b.keySet().toArray();
            // 显示所有收到的消息及其细节
            for (int i = 0; i < lstName.length; i++) {
                String keyName = lstName[i].toString();
                Log.e(keyName, String.valueOf(b.get(keyName)));
            }
            //搜索设备时，取得设备的MAC地址
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent
                        .getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                String str= device.getName() + "|" + device.getAddress();

                if (lstDevices.indexOf(str) == -1)// 防止重复添加
                    lstDevices.add(str); // 获取设备名称和mac地址
                adtDevices.notifyDataSetChanged();
            }
        }
    };
}

