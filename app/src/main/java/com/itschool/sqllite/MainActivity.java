package com.itschool.sqllite;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.Toolbar;
import android.widget.ViewSwitcher;

public class MainActivity extends AppCompatActivity {

    EditText etHP, etName, etAttack;
    Button create, pick;
    ImageSwitcher imageSwitcher;
    String[] imageName = {"circle", "triangle", "square"};
    int index = 0;
    float initialX;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etHP          = findViewById(R.id.et_hp);
        etName        = findViewById(R.id.et_name);
        etAttack      = findViewById(R.id.et_attack);
        create        = findViewById(R.id.bt_next);
        pick          = findViewById(R.id.pickDB);
        imageSwitcher = findViewById(R.id.imageSwitch);

        imageSwitcher.setInAnimation(AnimationUtils.loadAnimation(this,android.R.anim.fade_out));
        imageSwitcher.setOutAnimation(AnimationUtils.loadAnimation(this,android.R.anim.fade_in));

        imageSwitcher.setFactory(new ViewSwitcher.ViewFactory() {
            @Override
            public View makeView() {
                ImageView img = new ImageView(getApplicationContext());
                img.setScaleType(ImageView.ScaleType.FIT_CENTER);
                ImageSwitcher.LayoutParams par = new ImageSwitcher.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT);
                img.setLayoutParams(par);
                return img;
            }
        });
        showImage(index);

        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createRecord();
            }
        });
        pick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.this.pickRecord();
            }
        });
        imageSwitcher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (index >= imageName.length-1) {
                    index = 0;
                }
                else {
                    index++;
                }
                showImage(index);
            }
        });





    }

    private void pickRecord() {
        SQLiteOpenHelper dbHelper = new DBHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor c = db.query("testBase",null,null,null,null,
                null, null);

        int id = 0, name = 0, hp = 0, attack = 0,image = 0;

        if (c.moveToFirst()){
            id = c.getColumnIndex("id");
            name = c.getColumnIndex("name");
            hp = c.getColumnIndex("hp");
            attack = c.getColumnIndex("attack");
            image = c.getColumnIndex("image");

        }

       do {
            Log.d("app", String.format("%s Персонаж %s, здоровье %s, атака %s, аватарка %s",
                    c.getString(id),
                    c.getString(name),
                    c.getString(hp),
                    c.getString(attack),
                    c.getString(image)));
        }  while (c.moveToNext());

        c.close();
        dbHelper.close();
    }

    private void createRecord() {
        ContentValues cv = new ContentValues();
        String name = etName.getText().toString();
        String hp = etHP.getText().toString();
        String attack = etAttack.getText().toString();

        SQLiteOpenHelper dbHelper = new DBHelper(getApplicationContext());
        try (SQLiteDatabase sqLiteDatabase = dbHelper.getWritableDatabase()) {
          cv.put("name", name);
          cv.put("hp", hp);
          cv.put("attack", attack);
          cv.put("image", index);
          sqLiteDatabase.insert("testBase", null, cv);
        }

    }

    private void nextImage() {
        showImage(index);
        if (index < imageName.length-1){
            index++;
        }
        else {
            Toast.makeText(getApplicationContext(), "Следующего нет", Toast.LENGTH_SHORT);
            return;
        }

    }

    private void prevuesImage(){
        if (index > 0){
            index--;
        }
        else {
            Toast.makeText(getApplicationContext(), "Предыдущего нет",Toast.LENGTH_SHORT).show();
            return;
        }
        showImage(index);
    }

    private void showImage(int index) {
        String packageName = getPackageName();
        String imageToShow = imageName[index];
        int resId = getResources().getIdentifier(imageToShow, "drawable", packageName);
        if (resId != 0){
            imageSwitcher.setImageResource(resId);
        }
    }

    private class DBHelper extends SQLiteOpenHelper {

        public DBHelper(Context context){
            super(context, "testBase", null, 1);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            String query = "CREATE TABLE testBase" + "(id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "name TEXT, " +
                    "hp TEXT, " +
                    "attack TEXT," +
                    "image INTEGER" + ");";
            db.execSQL(query);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + "testBase");
            onCreate(db);
        }
    }
}
