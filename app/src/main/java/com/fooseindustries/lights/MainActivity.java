package com.fooseindustries.lights;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.fooseindustries.apihelper.*;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    private SwipeRefreshLayout srl;
    private LightListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(MainActivity.this, EditLightActivity.class),1);
            }
        });

        Timer t = new Timer();
        TimerTask tt = new TimerTask() {
            @Override
            public void run() {
                getData();
            }
        };
        t.scheduleAtFixedRate(tt, 0, 2000);

        srl = (SwipeRefreshLayout) findViewById(R.id.swiperefresh);

        srl.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
               getData();
            }
        });

        getData();


    }

    private void getData() {
        new APIHelper().jsonArray(false, Method.GET, "http://fooseindustries.com:8080/api/lights", null, null, new JSONArrayListener() {
            @Override
            public void jsonArray(JSONArray array) {
                try {
                    final ArrayList<Light> lights = new ArrayList<Light>();

                    for (int i = 0; i < array.length(); i++) {
                        JSONObject cLight = array.getJSONObject(i);

                        lights.add(new Light(cLight.getString("_id"), cLight.getString("name"),
                                cLight.getString("channel"), Boolean.parseBoolean(cLight.getString("state"))));
                    }

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(srl != null)
                                srl.setRefreshing(false);

                            adapter = new LightListAdapter(MainActivity.this, lights);
                            ListView list = (ListView) findViewById(R.id.listView);
                            list.setAdapter(adapter);

                            list.setClickable(true);
                            list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                    Intent i = new Intent(MainActivity.this,EditLightActivity.class);
                                    i.putExtra("light",lights.get(position));
                                    i.putExtra("index",position);
                                    startActivityForResult(i, 1);
                                }
                            });
                        }
                    });
                } catch (JSONException e) {
                    Log.d("ERROR:", "JSON Object ERROR");
                }
            }

            @Override
            public void error(Exception e) {
                Toast.makeText(MainActivity.this,"An Error Occurred",Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == 1) {
            if(resultCode == RESULT_OK) {
                String action = data.getStringExtra("action");

                if(action.equals("delete")) {
                    int pos = data.getIntExtra("index",-1);

                    if(pos > -1) {
                        adapter.lights.remove(pos);
                        adapter.notifyDataSetChanged();
                    }
                } else if(action.equals("create")) {
                    Light light = (Light) data.getSerializableExtra("light");

                    adapter.lights.add(light);
                    adapter.notifyDataSetChanged();
                } else if(action.equals("edit")) {
                    Light light = (Light) data.getSerializableExtra("light");
                    int index = data.getIntExtra("index", -1);

                    if (index > -1) {
                        adapter.lights.set(index,light);
                        adapter.notifyDataSetChanged();
                    }
                }
            }
        }
    }
}