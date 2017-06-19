/*
 * Copyright (c) 2017. Foose Industries
 */

package com.fooseindustries.apihelper;

import org.json.JSONArray;

/**
 * Created by ianfoose on 6/6/17.
 */
public interface JSONArrayListener {
    void jsonArray(JSONArray array);

    void error(Exception e);
}