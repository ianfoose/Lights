/*
 * Copyright (c) 2017. Foose Industries
 */

package com.fooseindustries.apihelper;

import org.json.JSONObject;

/**
 * Created by ianfoose on 6/6/17.
 */

public interface JSONObjectListener {
    void jsonObject(JSONObject obj);

    void error(Exception e);
}