package com.academy_edu.test1;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;

public class PathPickerActivity extends Activity {
    ListView list_dir;
    TextView textPath;
    Context _context;
    int select_id_list = -1;
    String path = "/storage/emulated/0";

    ArrayList<String> ArrayDir = new ArrayList<String>();
    ArrayAdapter<String> adapter;

    protected void onCreate(Bundle savedInstanceState) {
        _context = this;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ppa);

        list_dir = (ListView) findViewById(R.id.list_dir);
        textPath = (TextView) findViewById(R.id.textPath);

        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, ArrayDir);
        list_dir.setAdapter(adapter);

        update_list_dir();

        list_dir.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                select_id_list = (int) id;
                update_list_dir();
            }
        });

    }

    public void onClickBack(View view) {
        try {
            if(!path.equals("/storage/emulated/0")){
            path = (new File(path)).getParent();
            update_list_dir();}
            else throw new NullPointerException();
        } catch (NullPointerException e){
            Toast.makeText(getApplicationContext(),"Выше уже некуда", Toast.LENGTH_SHORT).show();
        }
    }

    public void onClickGo(View view) {
        Intent intent = new Intent();
        intent.putExtra("path", path);
        setResult(Activity.RESULT_OK, intent);
        finish();
    }

    private void update_list_dir() {
        if (select_id_list != -1) path = path + "/" + ArrayDir.get(select_id_list);
        select_id_list = -1;
        ArrayDir.clear();
        File[] files = new File(path).listFiles();
        for (File aFile : files) {
            if (aFile.isDirectory()) {
                if (dir_opened(aFile.getPath())) {
                    ArrayDir.add(aFile.getName());
                }
            }
        }

        adapter.notifyDataSetChanged();
        textPath.setText(path);
    }

    private boolean dir_opened(String url) {
        try {
            File[] files = new File(url).listFiles();
            for (@SuppressWarnings("unused") File aFile : files) {
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}