package com.example.wangjiadong.bluetooth3032;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.activeandroid.ActiveAndroid;


public class Test extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        SQLiteDatabase db = openOrCreateDatabase("shoeload.db", Context.MODE_PRIVATE, null);
        //refresh_db(db);
        Shoeload shoeload = new Shoeload(1.0,2.0,3.0,4.0, 5);
        insert_data(db, shoeload);
        insert_data(db, shoeload);
        insert_data(db, shoeload);
        insert_data(db, shoeload);

        show_all_data(db);
        db.close();
        //Loadata ld = new Loadata();
        //ld.save_load(1.0, 2.0, 3.0, 4.0,"2010");
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.test, menu);
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
    public void refresh_db(SQLiteDatabase db)
    {
        db.execSQL("DROP TABLE IF EXISTS shoeload");
        db.execSQL("CREATE TABLE shoeload (_id INTEGER PRIMARY KEY AUTOINCREMENT, front DOUBLE, middle DOUBLE, rare DOUBLE, temp DOUBLE, time DOUBLE)");
    }
    public void insert_data(SQLiteDatabase db,Shoeload sl)
    {
        db.execSQL("INSERT INTO shoeload VALUES (NULL, ?, ?, ? , ?, ?)", new Object[]{sl.front, sl.middle, sl.rare, sl.temp, sl.time});
    }
    public void show_all_data(SQLiteDatabase db)
    {
        Cursor c = db.rawQuery("SELECT * FROM shoeload WHERE front >= ?", new String[]{"0.0"});
        while(c.moveToNext()){
            int _id = c.getInt(c.getColumnIndex("_id"));
            double middle = c.getDouble(c.getColumnIndex("middle"));
            Log.d("result from db", Integer.toString(_id)+"  "+Double.toString(middle));
        }
        c.close();
    }

}
