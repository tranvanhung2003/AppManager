package com.example.appmanager.utils;

import com.example.appmanager.model.GioHang;
import com.example.appmanager.model.User;

import java.util.ArrayList;
import java.util.List;

import io.paperdb.Paper;

public class Utils {
    // 192.168.43.123:81
    // 192.168.0.104:81
    // hungtran2003.000webhostapp.com
    public static final String BASE_URL = "http://" + "hungtran2003.000webhostapp.com" + "/appbanthuoc/";
    public static List<GioHang> manggiohang;
    public static List<GioHang> mangmuadhang = new ArrayList<>();
    public static User user_current = new User();

    static {
        if (Paper.book().read("manggiohang") != null) {
            manggiohang = Paper.book().read("manggiohang");
        } else {
            manggiohang = new ArrayList<>();
        }
    }
}
