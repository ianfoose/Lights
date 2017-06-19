/*
 * Copyright (c) 2017. Foose Industries
 */

package com.fooseindustries.lights;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.fooseindustries.apihelper.*;

/**
 * Created by ianfoose on 6/4/17.
 */
public class LightListAdapter extends ArrayAdapter<Light> {

    public ArrayList<Light> lights;
    private Context context;

    LightListAdapter(Context context, ArrayList<Light> lights) {
        super(context, R.layout.light_row, lights);

        this.context = context;
        this.lights = lights;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View customView = inflater.inflate(R.layout.light_row, parent, false);

        final Light light = getItem(position);
        TextView rowText = (TextView) customView.findViewById(R.id.textView);
        final Switch stateSwitch = (Switch) customView.findViewById(R.id.stateSwitch);

        rowText.setText(light.name);
        stateSwitch.setChecked(light.state);

        stateSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, final boolean isChecked) {
                Map<String,String> params = new HashMap<String,String>();
                params.put("state",String.valueOf(isChecked));

                stateSwitch.setEnabled(false);

                new APIHelper().request(false, Method.PUT, "http://fooseindustries.com:8080/api/lights/"+light.id, null, params, new APIListener() {
                   @Override
                   public void success(String obj) {
                       Activity a = (Activity) context;
                       a.runOnUiThread(new Runnable() {
                           @Override
                           public void run() {
                               String msg = "On";

                               if(!isChecked)
                                   msg = "Off";

                               stateSwitch.setEnabled(true);
                               Toast.makeText(context.getApplicationContext(), msg,Toast.LENGTH_SHORT).show();
                           }
                       });
                   }

                   @Override
                   public void error(Exception e) {
                       e.printStackTrace();

                       Activity a = (Activity) context;
                       a.runOnUiThread(new Runnable() {
                           @Override
                           public void run() {
                               stateSwitch.setEnabled(true);
                               Toast.makeText(context.getApplicationContext(), "Error",Toast.LENGTH_SHORT).show();
                           }
                       });
                   }
               });
            }
        });

        return customView;
    }
}