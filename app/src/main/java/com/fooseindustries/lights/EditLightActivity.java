/*
 * Copyright (c) 2017. Foose Industries
 */

package com.fooseindustries.lights;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.fooseindustries.apihelper.APIHelper;
import com.fooseindustries.apihelper.APIListener;
import com.fooseindustries.apihelper.Method;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class EditLightActivity extends AppCompatActivity implements TextWatcher {

    private Light light;
    private Button saveButton;
    private EditText lightName;
    private EditText lightChannel;
    private int index;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_light);

        lightName = (EditText) findViewById(R.id.nameText);
        lightChannel = (EditText) findViewById(R.id.channelText);
        saveButton = (Button) findViewById(R.id.saveButton);
        Button deleteButton = (Button) findViewById(R.id.deleteButton);

        Intent i = getIntent();
        light = (Light) i.getSerializableExtra("light");
        index = i.getIntExtra("index",-1);

        if(light == null) { // new
            deleteButton.setVisibility(View.GONE);
        } else { // editing
            lightName.setText(light.name);
            lightChannel.setText(light.channel);
        }

        saveButton.setEnabled(false);

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Map<String,String> params = new HashMap<String,String>();
                params.put("name",lightName.getText().toString());
                params.put("channel",lightChannel.getText().toString());

                if(light == null) { // create
                    new APIHelper().request(false, Method.POST, "http://fooseindustries.com:8080/api/lights/", null, params, new APIListener() {
                        @Override
                        public void success(String obj) {
                            try {
                                JSONObject nl = new JSONObject(obj);

                                light = new Light(nl.getString("_id"),nl.getString("name"),nl.getString("channel"),Boolean.valueOf(nl.getString("state")));

                                Intent i = new Intent();
                                i.putExtra("action","create");
                                i.putExtra("light",light);
                                setResult(RESULT_OK,i);
                                finish();
                            } catch(JSONException e) {
                                e.printStackTrace();
                                showToast("Error Creating");
                            }
                        }

                        @Override
                        public void error(Exception e) {
                            e.printStackTrace();
                            showToast("Error creating");
                        }
                    });
                } else { // save
                    new APIHelper().request(false, Method.PUT, "http://fooseindustries.com:8080/api/lights/"+light.id, null, params, new APIListener() {
                        @Override
                        public void success(String obj) {
                            Intent i = new Intent();
                            i.putExtra("action","edit");
                            i.putExtra("index",index);
                            i.putExtra("light",new Light(light.id,lightName.getText().toString(),lightChannel.getText().toString(),light.state));
                            setResult(RESULT_OK,i);
                            finish();
                        }

                        @Override
                        public void error(Exception e) {
                            e.printStackTrace();
                            showToast("Error Editing");
                        }
                    });
                }
            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(light != null) {
                    new APIHelper().request(false, Method.DELETE, "http://fooseindustries.com:8080/api/lights/" + light.id, null, null, new APIListener() {
                        @Override
                        public void success(String obj) {
                            Intent i = new Intent();
                            i.putExtra("action","delete");
                            i.putExtra("index",index);
                            setResult(RESULT_OK,i);
                            finish();
                        }

                        @Override
                        public void error(Exception e) {
                            e.printStackTrace();
                            showToast("Error Deleting");
                        }
                    });
                }
            }
        });

        lightName.addTextChangedListener(this);
        lightChannel.addTextChangedListener(this);
    }

    private void showToast(final String msg) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(EditLightActivity.this,msg,Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) { }

    @Override
    public void afterTextChanged(Editable s) {
        checkText();
    }

    private void checkText() {
        if(!lightName.getText().toString().isEmpty() && !lightChannel.getText().toString().isEmpty()) {
            if(lightName.getText().toString().equals(light.name) && lightChannel.getText().toString().equals(light.channel)) {
                saveButton.setEnabled(false);
            } else {
                saveButton.setEnabled(true);
            }
        } else {
            saveButton.setEnabled(false);
        }
    }
}