/*
 * Copyright (c) 2017. Foose Industries
 */

package com.fooseindustries.lights;

import java.io.Serializable;

/**
 * Created by ianfoose on 6/4/17.
 */
public class Light implements Serializable {
    String id;
    String name;
    String channel;
    boolean state;

    public Light(String id, String name, String channel, boolean state) {
        this.id = id;
        this.name = name;
        this.channel = channel;
        this.state = state;
    }
}