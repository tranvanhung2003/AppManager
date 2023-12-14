package com.example.appbanthuoc.utils;

import com.example.appbanthuoc.model.GioHang;
import com.example.appbanthuoc.model.User;

import java.util.ArrayList;
import java.util.List;

public class Utils {
    // 192.168.43.123:81
    // 192.168.0.104:81
    // hungtran2003.000webhostapp.com
    public static final String BASE_URL = "http://" + "hungtran2003.000webhostapp.com" + "/appbanthuoc/";
    public static List<GioHang> manggiohang = new ArrayList<>();
    public static List<GioHang> mangmuadhang = new ArrayList<>();
    public static User user_current = new User();
}
