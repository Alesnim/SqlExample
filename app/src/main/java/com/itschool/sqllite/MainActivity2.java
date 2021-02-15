package com.itschool.sqllite;

import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

public class MainActivity2 extends AppCompatActivity {

    private SQLiteDatabase db;
    private Cursor cursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        ListView listView = findViewById(R.id.lv_list);

        SQLiteOpenHelper dbHelper = new DBHelper(this);
        db = dbHelper.getReadableDatabase();
        cursor = db.query("testBase", null, null, null, null,
                null, null);
        SimpleCursorAdapter adapter = new SimpleCursorAdapter(
                this,
                R.layout.item_list,
                cursor,
                new String[]{"_id", "name", "hp"},
                new int[]{R.id.tv_id, R.id.tv_name, R.id.tv_hp},
                0);


        listView.setAdapter(adapter);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        cursor.close();
        db.close();

    }
}