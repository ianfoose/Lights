/*
 * Copyright (c) 2017. Foose Industries
 */

package com.fooseindustries.apihelper;

/**
 * Created by ianfoose on 6/4/17.
 */

public abstract interface APIListener {
    void success(String obj);

    void error(Exception e);
}