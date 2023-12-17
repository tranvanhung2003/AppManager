package com.example.appmanager.activity;

import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appmanager.R;
import com.example.appmanager.adapter.ThuocAdapter;
import com.example.appmanager.model.SanPhamMoi;
import com.example.appmanager.retrofit.ApiBanThuoc;
import com.example.appmanager.retrofit.RetrofitClient;
import com.example.appmanager.utils.Utils;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class SearchActivity extends AppCompatActivity {
    Toolbar toolbar;
    RecyclerView recyclerView;
    EditText edtsearch;
    ThuocAdapter adapterThuoc;
    List<SanPhamMoi> sanPhamMoiList;
    ApiBanThuoc apiBanThuoc;
    CompositeDisposable compositeDisposable = new CompositeDisposable();
    Handler handler = new Handler();
    Runnable runnable;
    int seachsp;
    String loaiSanPham;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        seachsp = getIntent().getIntExtra("searchsp", 0);
        loaiSanPham = getIntent().getStringExtra("loaiSanPham");

        initView();
        ActionToolBar();
    }

    private void initView() {
        sanPhamMoiList = new ArrayList<>();

        apiBanThuoc = RetrofitClient.getInstance(Utils.BASE_URL).create(ApiBanThuoc.class);

        edtsearch = findViewById(R.id.edtsearch);
        edtsearch.setHint("Nhập " + loaiSanPham + " cần tìm");
        toolbar = findViewById(R.id.toolbar);
        recyclerView = findViewById(R.id.recycleview_search);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);
        edtsearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                handler.removeCallbacksAndMessages(null);

                runnable = () -> {
                    getDataSearch(s.toString().trim());
                };
                handler.postDelayed(runnable, 300);
            }
        });
    }

    private void getDataSearch(String s) {
        sanPhamMoiList.clear();

        if (s.length() != 0) {
            compositeDisposable.add(apiBanThuoc.search(s, seachsp)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            sanPhamMoiModel -> {
                                if (sanPhamMoiModel.isSuccess()) {
                                    sanPhamMoiList = sanPhamMoiModel.getResult();
                                }

                                adapterThuoc = new ThuocAdapter(getApplicationContext(), sanPhamMoiList);
                                recyclerView.setAdapter(adapterThuoc);
                            },
                            throwable -> {
                                Toast.makeText(getApplicationContext(), throwable.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                    ));
        } else {
            adapterThuoc = new ThuocAdapter(getApplicationContext(), sanPhamMoiList);
            recyclerView.setAdapter(adapterThuoc);
        }
    }

    private void ActionToolBar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    protected void onDestroy() {
        compositeDisposable.clear();
        super.onDestroy();
    }
}