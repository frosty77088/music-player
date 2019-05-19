package com.academy_edu.test1;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    String path = "/storage/emulated/0";
    String[] names;
    ListView mGV;
    boolean STATE_FIRST_LAUNCH;
    SharedPreferences pathAlaunch;
   private ArrayAdapter<String> myAdapter;




    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mGV = findViewById(R.id.mGV);
        runtimePermission();
        pathAlaunch = getPreferences(MODE_PRIVATE);
        STATE_FIRST_LAUNCH = pathAlaunch.getBoolean("sfl", true);
        if(STATE_FIRST_LAUNCH){
            startActivityForResult(new Intent(MainActivity.this, PathPickerActivity.class), 1);
        } else {
            path = pathAlaunch.getString("path","/storage/emulated/0");
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public void runtimePermission() {
        Dexter.withActivity(this)
                .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        display();

                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {

                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).check();


    }

    public ArrayList<File> findSong(String file) {
        ArrayList<File> arrayList = new ArrayList<>();
        File gw = new File(file);
        File[] files = gw.listFiles();

        for(File singleFile: files) {
            if(singleFile.isDirectory() && !singleFile.isHidden()) {
                arrayList.addAll(findSong(singleFile.getAbsolutePath()));
            }
            else{
                if(singleFile.getName().endsWith(".mp3")) {
                    arrayList.add(singleFile);
                }
            }
        }
        return arrayList;
    }



    void display(){
        final ArrayList<File> mySongs = findSong(path);
        names = new String[mySongs.size()];

        for(int i=0;i<mySongs.size();i++) {

            names[i] = mySongs.get(i).getName().replace(".mp3","");

        }

        myAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_2, android.R.id.text1, names);
        mGV.setAdapter(myAdapter);



        mGV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {

                String songName = mySongs.get(pos).getName().replace(".mp3", "");

                startActivity(new Intent(getApplicationContext(),PlayerActivity.class)
                        .putExtra("songs",mySongs).putExtra("songname",songName)
                        .putExtra("pos",pos));
                Toast.makeText(getApplicationContext(), names[pos] /*"pos: "+String.valueOf(pos) + " \nid: "+ String.valueOf(id)*/,Toast.LENGTH_LONG).show();
            }
        });
    }

    public boolean onCreateOptionsMenu(Menu menu) {

        menu.add("Выбрать путь");

        return super.onCreateOptionsMenu(menu);
    }
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case 0:
                startActivityForResult(new Intent(MainActivity.this, PathPickerActivity.class), 1);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (data != null) {
            switch (requestCode) {
                case 1:
                    pathAlaunch = getPreferences(MODE_PRIVATE);
                    SharedPreferences.Editor myEditor = pathAlaunch.edit();
                    if (STATE_FIRST_LAUNCH) {
                        myEditor.putBoolean("sfl", false);
                        myEditor.apply();
                        STATE_FIRST_LAUNCH = false;
                    }
                    this.path = data.getStringExtra("path");
                    myEditor.putString("path", data.getStringExtra("path"));
                    myEditor.apply();
                    break;
            }
            display();
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}



