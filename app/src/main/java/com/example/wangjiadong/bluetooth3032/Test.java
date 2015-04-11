package com.example.wangjiadong.bluetooth3032;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;

import com.activeandroid.ActiveAndroid;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;


public class Test extends Activity {
    SQLiteDatabase db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        GraphView graph = (GraphView) findViewById(R.id.graph);
        GraphView graph_1 = (GraphView) findViewById(R.id.graph_1);
        db = openOrCreateDatabase("shoeload.db", Context.MODE_PRIVATE, null);
        if( db == null)
                Log.d("Info_Test", "database closed");
        else
                Log.d("Info_Test", "Database started");


        ArrayList<DataPoint> dp_a = new ArrayList<DataPoint>();
        dp_a.add(new DataPoint(0,2));

        int i = 1;
        double sum_up =0.0;
        Cursor c = db.rawQuery("SELECT * FROM shoeload", null);
        while(c.moveToNext()){
            sum_up = c.getDouble(c.getColumnIndex("front")) +c.getDouble(c.getColumnIndex("middle"))+c.getDouble(c.getColumnIndex("rare"));
            dp_a.add(new DataPoint(i, sum_up/1000));
            i++;
        }

        DataPoint[] dp = dp_a.toArray(new DataPoint[dp_a.size()]);

        LineGraphSeries<DataPoint> series = new LineGraphSeries<DataPoint>(dp);
        Log.d("Lala", Integer.toString(dp.length));
        graph.addSeries(series);
        Log.d("finished","finished redering");
        //show_all_data(db);
        //graph.addSeries(new LineGraphSeries<DataPoint>());


        dp = new DataPoint[]{
            new DataPoint(1, 54),
            new DataPoint(2, 54),
            new DataPoint(3, 53),
            new DataPoint(4, 53),
            new DataPoint(5, 53),
            new DataPoint(6, 52),
            new DataPoint(7, 52),
            new DataPoint(8, 52),
            new DataPoint(9, 52),
            new DataPoint(10, 52),
            new DataPoint(11, 52),
            new DataPoint(12, 52),
            new DataPoint(13, 52),
            new DataPoint(14, 52),
            new DataPoint(14, 52),
            new DataPoint(14, 51)
        };
        series = new LineGraphSeries<DataPoint>(dp);
        graph_1.addSeries(series);






    }

    static <T> T[] append(T[] arr, T element) {
        final int N = arr.length;
        arr = Arrays.copyOf(arr, N + 1);
        arr[N] = element;
        return arr;
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
            double front  = c.getDouble(c.getColumnIndex("front"));
            double rare = c.getDouble(c.getColumnIndex("rare"));
            //Log.d("result from db", Integer.toString(_id)+"  "+Double.toString(middle+ front + rare));
        }
        c.close();
    }
    public void get_data_points(DataPoint[] dp)
    {
        int i = 1;
        Cursor c = db.rawQuery("SELECT * FROM shoeload", null);
        while(c.moveToNext()){
            double sum_up = c.getDouble(c.getColumnIndex("front")) +c.getDouble(c.getColumnIndex("middle"))+c.getDouble(c.getColumnIndex("rare"));
            //Log.d("Info", c.getInt(c.getColumnIndex("_id"))+ " "+ sum_up + " ");
            append(dp, new DataPoint(i, sum_up));
            i++;
        }

    }

}
