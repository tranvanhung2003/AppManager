package com.example.appbanthuoc.activity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.appbanthuoc.R;
import com.example.appbanthuoc.adapter.LoaiSpAdapter;
import com.example.appbanthuoc.adapter.SanPhamMoiAdapter;
import com.example.appbanthuoc.model.LoaiSp;
import com.example.appbanthuoc.model.SanPhamMoi;
import com.example.appbanthuoc.retrofit.ApiBanThuoc;
import com.example.appbanthuoc.retrofit.RetrofitClient;
import com.example.appbanthuoc.utils.Utils;
import com.google.android.material.navigation.NavigationView;
import com.nex3z.notificationbadge.NotificationBadge;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {
    Toolbar toolbar;
    ViewFlipper viewFlipper;
    RecyclerView recyclerViewManHinhChinh;
    NavigationView navigationView;
    ListView listViewManHinhChinh;
    DrawerLayout drawerLayout;
    LoaiSpAdapter loaiSpAdapter;
    List<LoaiSp> mangloaisp;
    CompositeDisposable compositeDisposable = new CompositeDisposable();
    ApiBanThuoc apiBanThuoc;
    List<SanPhamMoi> mangSpMoi;
    SanPhamMoiAdapter spAdapter;
    NotificationBadge badge;
    FrameLayout frameLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        apiBanThuoc = RetrofitClient.getInstance(Utils.BASE_URL).create(ApiBanThuoc.class);

        anhXa();
        ActionBar();

        if (isConnected(this)) {
            ActionViewFlipper();
            getLoaiSanPham();
            getSpMoi();
            getEventClick();
        } else {
            Toast.makeText(getApplicationContext(), "Không có Internet, vui lòng kết nối", Toast.LENGTH_SHORT).show();
        }
    }

    private void getEventClick() {
        listViewManHinhChinh.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    Intent trangchu = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(trangchu);
                } else {
                    Intent thuoc = new Intent(getApplicationContext(), ThuocActivity.class);
                    thuoc.putExtra("loai", position);

                    String loaiSanPham = "";
                    switch (position) {
                        case 1:
                            loaiSanPham = "Thực phẩm chức năng";
                            break;
                        case 2:
                            loaiSanPham = "Dược mỹ phẩm";
                            break;
                        case 3:
                            loaiSanPham = "Chăm sóc cá nhân";
                            break;
                        case 4:
                            loaiSanPham = "Thiết bị y tế";
                            break;
                    }
                    thuoc.putExtra("loaiSanPham", loaiSanPham);

                    startActivity(thuoc);
                }
            }
        });
    }

    private void getSpMoi() {
        compositeDisposable.add(apiBanThuoc.getSpMoi()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        sanPhamMoiModel -> {
                            if (sanPhamMoiModel.isSuccess()) {
                                mangSpMoi = sanPhamMoiModel.getResult();
                                spAdapter = new SanPhamMoiAdapter(getApplicationContext(), mangSpMoi);
                                recyclerViewManHinhChinh.setAdapter(spAdapter);
                            }
                        },
                        throwable -> {
                            Toast.makeText(getApplicationContext(),
                                    "Không kết nối được với server " + throwable.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                ));
    }

    private void getLoaiSanPham() {
        compositeDisposable.add(apiBanThuoc.getLoaiSp()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        loaiSpModel -> {
                            if (loaiSpModel.isSuccess()) {
                                mangloaisp = loaiSpModel.getResult();
                                loaiSpAdapter = new LoaiSpAdapter(getApplicationContext(), mangloaisp);
                                listViewManHinhChinh.setAdapter(loaiSpAdapter);
                            }
                        },
                        throwable -> {
                            Toast.makeText(getApplicationContext(),
                                    "Không kết nối được với server " + throwable.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                ));
    }

    private void ActionViewFlipper() {
        List<String> mangquangcao = new ArrayList<>();
        mangquangcao.add("https://as2.ftcdn.net/v2/jpg/02/38/70/67/1000_F_238706716_aKnllr4AbeSc0wXX7YnwCjYx3K2MvbFL.jpg");
        mangquangcao.add("https://as1.ftcdn.net/v2/jpg/02/65/59/54/1000_F_265595483_jNQZb7VNm24iyTPVHqk7rxzKhqyyVGL4.jpg");
        mangquangcao.add("https://as1.ftcdn.net/v2/jpg/01/83/17/68/1000_F_183176882_Oy5v3GeIxx0YHbOBla7dbj1UrsNo3COI.jpg");
        mangquangcao.add("https://as2.ftcdn.net/v2/jpg/03/20/95/69/1000_F_320956905_kzzVPPtH1rt0VWNFQOOVmG4sPAvyqtMQ.jpg");
        mangquangcao.add("https://as1.ftcdn.net/v2/jpg/03/32/98/32/1000_F_332983252_aMO7sK9zWmwO0kmV1QLvEqCSOyUF2qpS.jpg");
        mangquangcao.add("https://as1.ftcdn.net/v2/jpg/02/38/24/64/1000_F_238246479_mBox8Xekrcrk6z9Iqc6wuTQgwfJP1BDe.jpg");
        mangquangcao.add("https://as1.ftcdn.net/v2/jpg/02/38/24/64/1000_F_238246479_mBox8Xekrcrk6z9Iqc6wuTQgwfJP1BDe.jpg");
        for (int i = 0; i < mangquangcao.size(); ++i) {
            ImageView imageView = new ImageView(getApplicationContext());
            Glide.with(getApplicationContext()).load(mangquangcao.get(i)).into(imageView);
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            viewFlipper.addView(imageView);
        }
        viewFlipper.setFlipInterval(3000);
        viewFlipper.setAutoStart(true);
        Animation slide_in = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_in_night);
        Animation slide_out = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_out_night);
        viewFlipper.setInAnimation(slide_in);
        viewFlipper.setOutAnimation(slide_out);
    }

    private void ActionBar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationIcon(android.R.drawable.ic_menu_sort_by_size);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });
    }

    private void anhXa() {
        toolbar = (Toolbar) findViewById(R.id.toolbarmanhinhchinh);
        viewFlipper = (ViewFlipper) findViewById(R.id.viewlipper);
        recyclerViewManHinhChinh = (RecyclerView) findViewById(R.id.recycleview);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this, 2);
        recyclerViewManHinhChinh.setLayoutManager(layoutManager);
        recyclerViewManHinhChinh.setHasFixedSize(true);
        listViewManHinhChinh = (ListView) findViewById(R.id.listviewmanhinhchinh);
        navigationView = (NavigationView) findViewById(R.id.navigationview);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawerlayout);
        badge = findViewById(R.id.menu_sl);
        frameLayout = findViewById(R.id.framegiohang);

        // khoi tao list
        mangloaisp = new ArrayList<>();
        mangSpMoi = new ArrayList<>();
        if (Utils.manggiohang == null) {
            Utils.manggiohang = new ArrayList<>();
        } else {
            int totalItem = 0;
            for (int i = 0; i < Utils.manggiohang.size(); ++i) {
                totalItem = totalItem + Utils.manggiohang.get(i).getSoluong();
            }
            if (totalItem > 0) {
                badge.setText(String.valueOf(totalItem));
            }
        }

        frameLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent giohang = new Intent(getApplicationContext(), GioHangActivity.class);
                startActivity(giohang);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        int totalItem = 0;
        for (int i = 0; i < Utils.manggiohang.size(); ++i) {
            totalItem = totalItem + Utils.manggiohang.get(i).getSoluong();
        }
        if (totalItem > 0) {
            badge.setText(String.valueOf(totalItem));
        }
    }

    private boolean isConnected(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager == null) {
            return false;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Network network = connectivityManager.getActiveNetwork();
            if (network == null) {
                return false;
            }
            NetworkCapabilities networkCapabilities = connectivityManager.getNetworkCapabilities(network);
            return networkCapabilities != null
                    && (networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
                    || networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR));
        } else {
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            return networkInfo != null && networkInfo.isConnected();
        }
    }

    @Override
    protected void onDestroy() {
        compositeDisposable.clear();
        super.onDestroy();
    }
}