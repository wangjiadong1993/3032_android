package com.example.wangjiadong.bluetooth3032;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.app.Application;
import com.activeandroid.query.Select;

/**
 * Created by wangjiadong on 7/3/15.
 */
public class Loadata extends Application{
    @Override
    public void onCreate() {
        super.onCreate();
        ActiveAndroid.initialize(this);
    }
    public void save_load(double a, double b , double c, double d, String e)
    {
        //Shoeload load_ = new Shoeload(a,b,c,d,e);
//        load_.front =a;
//        load_.middle = b;
//        load_.rare = c;
//        load_.temp = d;
//        load_.time = e;
//        load_.save();
    }
//    public static Load retrieve__first_load()
//    {
//        //return new Select().from(Load.class).
//    }
}
