package com.example.appmanager.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.example.appmanager.R;
import com.example.appmanager.retrofit.ApiBanThuoc;
import com.example.appmanager.retrofit.RetrofitClient;
import com.example.appmanager.utils.Utils;

import io.paperdb.Paper;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class DangKyActivity extends AppCompatActivity {
    EditText email, pass, repass, mobile, username;
    AppCompatButton button;
    ApiBanThuoc apiBanThuoc;
    CompositeDisposable compositeDisposable = new CompositeDisposable();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dang_ky);
        initView();
        initControl();
    }

    private void initControl() {
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dangKy();
            }
        });
    }

    private void dangKy() {
        String str_email = email.getText().toString().trim();
        String str_pass = pass.getText().toString().trim();
        String str_repass = repass.getText().toString().trim();
        String str_mobile = mobile.getText().toString().trim();
        String str_username = username.getText().toString().trim();

        if (TextUtils.isEmpty(str_email) || TextUtils.isEmpty(str_pass) || TextUtils.isEmpty(str_repass)
                || TextUtils.isEmpty(str_mobile) || TextUtils.isEmpty(str_username)) {
            Toast.makeText(getApplicationContext(), "Bạn chưa nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
        } else {
            if (str_pass.equals(str_repass)) {
                // post data
                compositeDisposable.add(apiBanThuoc.dangKy(str_email, str_pass, str_username, str_mobile)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                userModel -> {
                                    if (userModel.isSuccess()) {
                                        Toast.makeText(getApplicationContext(), userModel.getMessage(), Toast.LENGTH_SHORT).show();
                                        Utils.user_current.setEmail(str_email);
                                        Utils.user_current.setPass(str_pass);
                                        Paper.book().write("email", str_email);
                                        Paper.book().write("pass", str_pass);
                                        Intent intent = new Intent(getApplicationContext(), DanhNhapActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);
                                        finish();
                                    } else {
                                        Toast.makeText(getApplicationContext(), userModel.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                },
                                throwable -> {
                                    Toast.makeText(getApplicationContext(), throwable.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                        ));
            } else {
                Toast.makeText(getApplicationContext(), "Password chưa khớp", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void initView() {
        apiBanThuoc = RetrofitClient.getInstance(Utils.BASE_URL).create(ApiBanThuoc.class);

        email = findViewById(R.id.email);
        pass = findViewById(R.id.pass);
        repass = findViewById(R.id.repass);
        mobile = findViewById(R.id.mobile);
        username = findViewById(R.id.username);
        button = findViewById(R.id.btndangky);
    }

    @Override
    protected void onDestroy() {
        compositeDisposable.clear();
        super.onDestroy();
    }
}