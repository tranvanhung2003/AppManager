package com.example.appbanthuoc.activity;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.appbanthuoc.R;
import com.example.appbanthuoc.model.User;
import com.example.appbanthuoc.utils.Utils;

import io.paperdb.Paper;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        Paper.init(this);

        Thread thread = new Thread() {
            public void run() {
                try {
                    sleep(1500);
                } catch (Exception ex) {

                } finally {
                    if (Paper.book().read("user") == null) {
                        Intent intent = new Intent(getApplicationContext(), DanhNhapActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        User user = Paper.book().read("user");
                        Utils.user_current = user;

                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }
            }
        };
        thread.start();
    }
}