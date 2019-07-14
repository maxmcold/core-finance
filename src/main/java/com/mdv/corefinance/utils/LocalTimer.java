package com.mdv.corefinance.utils;

import java.time.Instant;
import java.util.Date;

public class LocalTimer {
    public static String now(){
        return Date.from(Instant.now()).toString();

    }
}
