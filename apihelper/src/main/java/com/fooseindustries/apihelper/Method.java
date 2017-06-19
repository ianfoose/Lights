/*
 * Copyright (c) 2017. Foose Industries
 */

package com.fooseindustries.apihelper;

/**
 * Created by ianfoose on 6/6/17.
 */

public enum Method {
    POST("POST"),
    GET("GET"),
    HEAD("HEAD"),
    DELETE("DELETE"),
    PUT("PUT"),
    PATCH("PATCH");

    private final String text;

    private Method(final String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return text;
    }
}