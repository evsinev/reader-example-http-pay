package com.payneteasy.httppay.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class Gsons {

    public static final Gson PRETTY_GSON = new GsonBuilder()
            .setPrettyPrinting()
            .setDateFormat("yyyy.MM.dd HH:mm:ss")
            .create();

}
