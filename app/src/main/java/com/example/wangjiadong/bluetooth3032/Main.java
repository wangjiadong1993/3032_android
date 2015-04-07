package com.example.wangjiadong.bluetooth3032;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Scanner;
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
    private GetGeo get_thread = null;
    private Handler mainhandler = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final ListView lv1 = (ListView) findViewById(R.id.listView2);
        Button location = (Button) findViewById(R.id.get_location);


        //for test only
        //Intent intent = new Intent(this, Communication.class);
        //startActivity(intent);

        //adding the bluetooth adaptor;
        BA = BluetoothAdapter.getDefaultAdapter();
        final Button show_devices = (Button) findViewById(R.id.show_devices);
        //Button show = (Button) findViewById(R.id.show_pair);

        mainhandler = new Handler(){
            @Override
            public void handleMessage(Message msg){
              Log.d("what", Integer.toString(msg.what));
              Log.d("info", msg.obj.toString());
              Toast.makeText(Main.this, msg.obj.toString(), Toast.LENGTH_LONG).show();

                Scanner sc = new Scanner(msg.obj.toString());
                float la = (float)sc.nextDouble();
                float lo = (float)sc.nextDouble();
                Log.d("Info:",la + "  "+ lo);
              Main.this.get_location(msg.obj.toString(),la, lo);
          }
        };


        //check availability and status
        if(BA == null)
            bt_status = -1;
        else{
            if(BA.isEnabled() == true)
                bt_status = 1;
            else
                bt_status = 0;
        }


        //this part is the turn on turn off button of the BT, and its reaction
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
                    adtDevices.clear();
                    lstDevices.clear();
                    BA.disable();//directly
                }
                set_visible(show_devices);
            }
        });


        //

        lstDevices = new ArrayList();
        adtDevices = new ArrayAdapter(this, android.R.layout.simple_list_item_1, lstDevices);
        lv1.setAdapter(adtDevices);


        lv1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d("connect", lstDevices.get(position).toString());
                connect_device(lstDevices.get(position).toString());
            }
        });




        //final ListView lv =  (ListView) findViewById(R.id.listView1);
        ArrayList list = new ArrayList();
        pairedDevices = BA.getBondedDevices();
        for(BluetoothDevice bt : pairedDevices)
            list.add(bt.getName());
        //final ArrayAdapter adapter = new ArrayAdapter(this,android.R.layout.simple_list_item_1, list);
        //lv.setAdapter(adapter);


        show_devices.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BA.startDiscovery();
            }
        });

        location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                double latitude = 0;
                double longitude  = 0;
                //String uri = String.format(Locale.ENGLISH, "geo:0,0?q=%f,%f", latitude, longitude);
                String uri = String.format(Locale.ENGLISH, "http://128.199.213.135?saddr=%f,%f", latitude, longitude);

                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                get_thread = new GetGeo("http://128.199.213.135/locations/new");
                get_thread.start();
                //Main.this.startActivity(intent);
            }
        });

    }

    private void set_visible(Button show_devices){
        if(bt_status == 1)
        {
            show_devices.setEnabled(true);
        }else{
            show_devices.setEnabled(false);
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

    private void get_location(String str, float a, float b)
    {
        String uri = String.format(Locale.ENGLISH, "geo:%f,%f", a, b);
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
        Main.this.startActivity(intent);
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
    private class GetGeo extends Thread {
        HttpClient httpclient = new DefaultHttpClient();
        HttpResponse response;
        String responseString = null;
        private String url = null;
        private String lati = null;
        private String longi = null;
        private String time_date = null;
        public GetGeo(String uni) {
            url = uni;
        }

        public void run() {
            JSONObject temp=null;
            Log.d("Info", "it is the sub thread");
            try {
                response = httpclient.execute(new HttpGet(url));
                StatusLine statusLine = response.getStatusLine();
                if (statusLine.getStatusCode() == HttpStatus.SC_OK) {
                    Log.d("Correct","Correct");
                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                    response.getEntity().writeTo(out);
                    out.close();
                    responseString = out.toString();
                    Log.d("Correct",responseString);
                } else {
                    //Closes the connection.
                    response.getEntity().getContent().close();
                  //  Log.d("info", "in else");
                    throw new IOException(statusLine.getReasonPhrase());
                }
            } catch (ClientProtocolException e) {
                Log.d("Wrong","Cought by ClientProtocolException");
            } catch (IOException e) {
                Log.d("Wrong", "IOException");
            }
            try {
                JSONObject jo = new JSONObject(responseString);
                if(jo.getString("status").equals("1"))
                {
                    temp = jo.getJSONObject("location");
                    lati = temp.getString("latitude");
                    longi = temp.getString("longitude");
                    time_date = temp.getString("created_at");
                }
                mainhandler.obtainMessage(1, lati+" "+longi).sendToTarget();
            }catch(JSONException e)
            {

            }
        }

        /* Call this from the main activity to send data to the remote device */
        public void write(byte[] bytes) {

        }

        /* Call this from the main activity to shutdown the connection */
        public void cancel() {
        }
    }
}

