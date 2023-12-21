package com.example.appmanager.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appmanager.R;
import com.example.appmanager.adapter.DonHangAdapter;
import com.example.appmanager.model.DonHang;
import com.example.appmanager.model.EventBus.DonHangEvent;
import com.example.appmanager.model.NotiSendData;
import com.example.appmanager.retrofit.ApiBanThuoc;
import com.example.appmanager.retrofit.ApiPushNotification;
import com.example.appmanager.retrofit.RetrofitClient;
import com.example.appmanager.retrofit.RetrofitClientNoti;
import com.example.appmanager.utils.Utils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class XemDonActivity extends AppCompatActivity {
    CompositeDisposable compositeDisposable = new CompositeDisposable();
    ApiBanThuoc apiBanThuoc;
    RecyclerView redonhang;
    Toolbar toolbar;
    ProgressBar progressBar;
    DonHang donHang;
    int tinhtrang;
    AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_xem_don);

        initView();
        initToolbar();
        getOrder();
    }

    private void getOrder() {
        if (Utils.user_current.getStatus() == 0) {
            getOrderUser();
        } else {
            getOrderAdmin();
        }
    }

    private void getOrderAdmin() {
        progressBar.setVisibility(View.VISIBLE);
        compositeDisposable.add(apiBanThuoc.xemDonHang(0)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        donHangModel -> {
                            DonHangAdapter adapter = new DonHangAdapter(getApplicationContext(), donHangModel.getResult());
                            redonhang.setAdapter(adapter);
                            progressBar.setVisibility(View.INVISIBLE);
                        },
                        throwable -> {
                            progressBar.setVisibility(View.INVISIBLE);
                        }
                ));
    }

    private void getOrderUser() {
        progressBar.setVisibility(View.VISIBLE);
        compositeDisposable.add(apiBanThuoc.xemDonHang(Utils.user_current.getId())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        donHangModel -> {
                            DonHangAdapter adapter = new DonHangAdapter(getApplicationContext(), donHangModel.getResult());
                            redonhang.setAdapter(adapter);
                            progressBar.setVisibility(View.INVISIBLE);
                        },
                        throwable -> {
                            progressBar.setVisibility(View.INVISIBLE);
                        }
                ));
    }

    private void initToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void initView() {
        apiBanThuoc = RetrofitClient.getInstance(Utils.BASE_URL).create(ApiBanThuoc.class);
        redonhang = findViewById(R.id.recycleview_donhang);
        toolbar = findViewById(R.id.toolbar);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        redonhang.setLayoutManager(layoutManager);
        progressBar = findViewById(R.id.progressbar);
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
        EventBus.getDefault().removeAllStickyEvents();
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void eventDonHang(DonHangEvent event) {
        Log.d("vanhung", "concho");
        if (event != null) {
            donHang = event.getDonHang();
            showCustomDialog();
        }
    }

    private void showCustomDialog() {
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_donhang, null);
        Spinner spinner = view.findViewById(R.id.spinner_dialog);
        AppCompatButton btndongy = view.findViewById(R.id.dongy_dialog);
        List<String> list = new ArrayList<>();
        list.add("Đơn hàng đang được xử lý");
        list.add("Đơn hàng đã được nhận");
        list.add("Đơn hàng đã được giao cho DVVC");
        list.add("Đơn hàng được giao thành công");
        list.add("Đơn hàng đã bị hủy");
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, list);
        spinner.setAdapter(adapter);

        spinner.setSelection(donHang.getTrangthai());
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                tinhtrang = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        btndongy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                capNhatDonHang();
            }
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(view);
        dialog = builder.create();
        dialog.show();
    }

    private void capNhatDonHang() {
        compositeDisposable.add(apiBanThuoc.updateOrder(donHang.getId(), tinhtrang)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        messageModel -> {
                            getOrder();
                            dialog.dismiss();
                            pushNotiToUser();
                        },
                        throwable -> {
                            Toast.makeText(getApplicationContext(), throwable.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                ));
    }

    private void pushNotiToUser() {
        // get token
        compositeDisposable.add(apiBanThuoc.getToken(0, donHang.getIduser())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        userModel -> {
                            if (userModel.isSuccess()) {
                                for (int i = 0; i < userModel.getResult().size(); ++i) {
                                    // String token = "d7vwNtziTIizvflDwGPxl4:APA91bE-rMNwaN3wjsntRE_UgOC9xiqsks5z2h_fi_Up7A7UYShMUuQNhuzGhlzgtD1M0RZSz0aR6r-Z93YHOCwVwN7ggiVgRq3aFCJPUQSZi6KxAPEm0pJzOxpdtJPrN5sdmBfsz6Gc";
                                    Map<String, String> data = new HashMap<>();
                                    data.put("title", "Thông báo");
                                    data.put("body", trangThaiDon(tinhtrang));
                                    NotiSendData notiSendData = new NotiSendData(userModel.getResult().get(i).getToken(), data);
                                    ApiPushNotification apiPushNotification = RetrofitClientNoti.getInstance().create(ApiPushNotification.class);
                                    compositeDisposable.add(apiPushNotification.sendNotification(notiSendData)
                                            .subscribeOn(Schedulers.io())
                                            .observeOn(AndroidSchedulers.mainThread())
                                            .subscribe(
                                                    notiResponse -> {

                                                    },
                                                    throwable -> {
                                                        Log.d("Log", throwable.getMessage());
                                                    }
                                            ));
                                }
                            }
                        },
                        throwable -> {
                            Log.d("Log", throwable.getMessage());
                        }
                ));
    }

    private String trangThaiDon(int status) {
        String result;
        switch (status) {
            case 0:
                result = "Đơn hàng đang được xử lý";
                break;
            case 1:
                result = "Đơn hàng đã được nhận";
                break;
            case 2:
                result = "Đơn hàng đã được giao cho DVVC";
                break;
            case 3:
                result = "Đơn hàng được giao thành công";
                break;
            case 4:
                result = "Đơn hàng đã bị hủy";
                break;
            default:
                result = "";
        }
        return result;
    }
}