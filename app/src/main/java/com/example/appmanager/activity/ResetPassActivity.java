package com.example.appmanager.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.example.appmanager.R;
import com.example.appmanager.retrofit.ApiBanThuoc;
import com.example.appmanager.retrofit.RetrofitClient;
import com.example.appmanager.utils.Utils;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class ResetPassActivity extends AppCompatActivity {
    EditText email;
    AppCompatButton btnreset;
    ApiBanThuoc apiBanThuoc;
    CompositeDisposable compositeDisposable = new CompositeDisposable();
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_pass);
        initView();
        initControl();
    }

    private void initControl() {
        btnreset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String str_email = email.getText().toString().trim();
                if (TextUtils.isEmpty(str_email)) {
                    Toast.makeText(getApplicationContext(), "Bạn chưa nhập mail", Toast.LENGTH_SHORT).show();
                } else {
                    progressBar.setVisibility(View.VISIBLE);
                    compositeDisposable.add(apiBanThuoc.resetPass(str_email)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(
                                    userModel -> {
                                        if (userModel.isSuccess()) {
                                            Toast.makeText(getApplicationContext(), userModel.getMessage(), Toast.LENGTH_SHORT).show();
                                            finish();
                                        } else {
                                            Toast.makeText(getApplicationContext(), userModel.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                        progressBar.setVisibility(View.INVISIBLE);
                                    },
                                    throwable -> {
                                        Toast.makeText(getApplicationContext(), throwable.getMessage(), Toast.LENGTH_SHORT).show();
                                        progressBar.setVisibility(View.INVISIBLE);
                                    }
                            ));
                }
            }
        });
    }

    private void initView() {
        apiBanThuoc = RetrofitClient.getInstance(Utils.BASE_URL).create(ApiBanThuoc.class);
        email = findViewById(R.id.edtresetpass);
        btnreset = findViewById(R.id.btnresetpass);
        progressBar = findViewById(R.id.progressbar);
    }

    @Override
    protected void onDestroy() {
        compositeDisposable.clear();
        super.onDestroy();
    }
}