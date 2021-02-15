package com.itschool.sqllite;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageSwitcher;
import android.widget.ImageView;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    EditText etHP, etName, etAttack;
    Button create, pick, next;
    ImageSwitcher imageSwitcher;
    String[] imageName = {"circle", "triangle", "square"};
    int index = 0;
    private SQLiteDatabase db;
    private Cursor cursor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etHP = findViewById(R.id.et_hp);
        etName = findViewById(R.id.et_name);
        etAttack = findViewById(R.id.et_attack);
        create = findViewById(R.id.bt_create);
        pick = findViewById(R.id.bt_pickDB);
        imageSwitcher = findViewById(R.id.imageSwitch);

        //imageSwitcher.setInAnimation(AnimationUtils.loadAnimation(this, android.R.anim.fade_out));
        //imageSwitcher.setOutAnimation(AnimationUtils.loadAnimation(this, android.R.anim.fade_in));

        imageSwitcher.setFactory(() -> {
            ImageView img = new ImageView(getApplicationContext());
            img.setScaleType(ImageView.ScaleType.FIT_CENTER);
            ImageSwitcher.LayoutParams par = new ImageSwitcher.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT);
            img.setLayoutParams(par);
            return img;
        });
        showImage(index);

        create.setOnClickListener(v -> createRecord());
        pick.setOnClickListener(v -> MainActivity.this.pickRecord());
        imageSwitcher.setOnClickListener(v -> {
            if (index >= imageName.length - 1) {
                index = 0;
            } else {
                index++;
            }
            showImage(index);
        });


        next = findViewById(R.id.bt_next);
        next.setOnClickListener((v -> {
            Intent i = new Intent(MainActivity.this, MainActivity2.class);
            startActivity(i);
        }));

    }

    private void pickRecord() {
        SQLiteOpenHelper dbHelper = new DBHelper(this);
        db = dbHelper.getReadableDatabase();
        cursor = db.query("testBase", null, null, null, null,
                null, null);

        // Cursor cursor = db.rawQuery("SELECT * from test where columns = ?", new String[]{"*"});

        int id = 0, name = 0, hp = 0, attack = 0, image = 0;

        if (cursor.moveToFirst()) {
            id = cursor.getColumnIndex("id");
            name = cursor.getColumnIndex("name");
            hp = cursor.getColumnIndex("hp");
            attack = cursor.getColumnIndex("attack");
            image = cursor.getColumnIndex("image");

        }
        try {
            do {
                Log.d("TAG", String.format("%s Персонаж %s, здоровье %s, атака %s, аватарка %s",
                        cursor.getString(id),
                        cursor.getString(name),
                        cursor.getString(hp),
                        cursor.getString(attack),
                        cursor.getString(image)));
            } while (cursor.moveToNext());
        }catch (SQLException e) {
            Log.d("TAG", Objects.requireNonNull(e.getMessage()));
        }



    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cursor.close();
        db.close();

    }

    private void createRecord() {
        ContentValues cv = new ContentValues();
        String name = etName.getText().toString();
        String hp = etHP.getText().toString();
        String attack = etAttack.getText().toString();

        try (SQLiteOpenHelper dbHelper = new DBHelper(getApplicationContext()); SQLiteDatabase sqLiteDatabase = dbHelper.getWritableDatabase()) {
            cv.put("name", name);
            cv.put("hp", hp);
            cv.put("attack", attack);
            cv.put("image", index);
            sqLiteDatabase.insert("testBase", null, cv);
        } catch (Exception e) {
            Log.d("TAG", Objects.requireNonNull(e.getMessage()));
        }

    }


    private void showImage(int index) {
        String packageName = getPackageName();
        String imageToShow = imageName[index];
        int resId = getResources().getIdentifier(imageToShow, "drawable", packageName);
        if (resId != 0) {
            imageSwitcher.setImageResource(resId);
        }
    }

}
