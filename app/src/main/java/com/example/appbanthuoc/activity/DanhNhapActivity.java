package com.example.appbanthuoc.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.example.appbanthuoc.R;
import com.example.appbanthuoc.retrofit.ApiBanThuoc;
import com.example.appbanthuoc.retrofit.RetrofitClient;
import com.example.appbanthuoc.utils.Utils;

import io.paperdb.Paper;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class DanhNhapActivity extends AppCompatActivity {
    TextView txtdangky;
    EditText email, pass;
    AppCompatButton btndangnhap;
    ApiBanThuoc apiBanThuoc;
    CompositeDisposable compositeDisposable = new CompositeDisposable();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_danh_nhap);
        initView();
        initControl();
    }

    private void initControl() {
        txtdangky.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), DangKyActivity.class);
                startActivity(intent);
            }
        });

        btndangnhap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String str_email = email.getText().toString().trim();
                String str_pass = pass.getText().toString().trim();
                if (TextUtils.isEmpty(str_email) || TextUtils.isEmpty(str_pass)) {
                    Toast.makeText(getApplicationContext(), "Bạn chưa nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                } else {
                    dangNhap(str_email, str_pass, true);
                }
            }
        });
    }

    private void initView() {
        Paper.init(this);
        apiBanThuoc = RetrofitClient.getInstance(Utils.BASE_URL).create(ApiBanThuoc.class);

        txtdangky = findViewById(R.id.txtdangky);
        email = findViewById(R.id.email);
        pass = findViewById(R.id.pass);
        btndangnhap = findViewById(R.id.btndangnhap);

        // read data
        if (Paper.book().read("islogin") != null) {
            boolean flag = Paper.book().read("islogin");
            if (flag) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        dangNhap(Paper.book().read("email"), Paper.book().read("pass"), false);
                    }
                }, 1000);
            }
        } else {
            if (Paper.book().read("email") != null && Paper.book().read("pass") != null) {
                email.setText(Paper.book().read("email"));
                pass.setText(Paper.book().read("pass"));
            }
        }
    }

    private void dangNhap(String str_email, String str_pass, boolean needIsLogin) {
        compositeDisposable.add(apiBanThuoc.dangNhap(str_email, str_pass)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        userModel -> {
                            if (userModel.isSuccess()) {
                                Toast.makeText(getApplicationContext(), userModel.getMessage(), Toast.LENGTH_SHORT).show();

                                // save
                                if (needIsLogin) {
                                    Paper.book().write("islogin", true);
                                    Paper.book().write("email", str_email);
                                    Paper.book().write("pass", str_pass);
                                }

                                Utils.user_current = userModel.getResult().get(0);
                                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
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
    }

    @Override
    protected void onDestroy() {
        compositeDisposable.clear();
        super.onDestroy();
    }
}