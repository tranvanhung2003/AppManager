package com.example.appmanager.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appmanager.adapter.SanPhamMoiAdapter;
import com.example.appmanager.databinding.ActivityQuanLyBinding;
import com.example.appmanager.model.EventBus.SuaXoaEvent;
import com.example.appmanager.model.SanPhamMoi;
import com.example.appmanager.retrofit.ApiBanThuoc;
import com.example.appmanager.retrofit.RetrofitClient;
import com.example.appmanager.utils.Utils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class QuanLyActivity extends AppCompatActivity {
    ActivityQuanLyBinding binding;
    ApiBanThuoc apiBanThuoc;
    CompositeDisposable compositeDisposable = new CompositeDisposable();
    List<SanPhamMoi> list;
    SanPhamMoiAdapter adapter;
    SanPhamMoi sanPhamSuaXoa;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityQuanLyBinding.inflate(getLayoutInflater());
        apiBanThuoc = RetrofitClient.getInstance(Utils.BASE_URL).create(ApiBanThuoc.class);
        setContentView(binding.getRoot());

        initView();
        initControl();
    }

    private void initControl() {
        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        binding.toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        binding.imgThem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ThemSanPhamActivity.class);
                startActivity(intent);
            }
        });
    }

    private void getSpMoi() {
        binding.progressbar.setVisibility(View.VISIBLE);
        compositeDisposable.add(apiBanThuoc.getSpMoi()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        sanPhamMoiModel -> {
                            if (sanPhamMoiModel.isSuccess()) {
                                list = sanPhamMoiModel.getResult();
                                adapter = new SanPhamMoiAdapter(getApplicationContext(), list);
                                binding.recycleviewQuanly.setAdapter(adapter);
                                binding.progressbar.setVisibility(View.INVISIBLE);
                            }
                        },
                        throwable -> {
                            Toast.makeText(getApplicationContext(),
                                    "Không kết nối được với server " + throwable.getMessage(), Toast.LENGTH_SHORT).show();
                            binding.progressbar.setVisibility(View.INVISIBLE);
                        }
                ));
    }

    private void initView() {
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this, 2);
        binding.recycleviewQuanly.setLayoutManager(layoutManager);
        binding.recycleviewQuanly.setHasFixedSize(true);
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        if (item.getTitle().equals("Sửa")) {
            suaSanPham();
        } else {
            xoaSanPham();
        }
        return super.onContextItemSelected(item);
    }

    private void xoaSanPham() {
        compositeDisposable.add(apiBanThuoc.xoaSanPham(sanPhamSuaXoa.getId())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        messageModel -> {
                            if (messageModel.isSuccess()) {
                                Toast.makeText(getApplicationContext(), messageModel.getMessage(), Toast.LENGTH_SHORT).show();
                                getSpMoi();
                            } else {
                                Toast.makeText(getApplicationContext(), messageModel.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        },
                        throwable -> {
                            Toast.makeText(getApplicationContext(), throwable.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                ));
    }

    private void suaSanPham() {
        Intent intent = new Intent(getApplicationContext(), ThemSanPhamActivity.class);
        intent.putExtra("sua", sanPhamSuaXoa);
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        compositeDisposable.clear();
        super.onDestroy();
    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void eventSuaXoa(SuaXoaEvent event) {
        if (event != null) {
            sanPhamSuaXoa = event.getSanPhamMoi();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        getSpMoi();
    }
}