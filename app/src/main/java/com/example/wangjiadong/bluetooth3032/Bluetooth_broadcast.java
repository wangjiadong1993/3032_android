package com.example.wangjiadong.bluetooth3032;

import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public class Bluetooth_broadcast extends BroadcastReceiver {
    public Bluetooth_broadcast() {
    }

    @Override
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
            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            String str= device.getName() + "|" + device.getAddress();
//
//            if (lstDevices.indexOf(str) == -1)// 防止重复添加
//                lstDevices.add(str); // 获取设备名称和mac地址
//            adtDevices.notifyDataSetChanged();
        }
    }
}
