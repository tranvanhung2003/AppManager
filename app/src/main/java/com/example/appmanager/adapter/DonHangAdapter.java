package com.example.appmanager.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appmanager.Interface.ItemClickListener;
import com.example.appmanager.R;
import com.example.appmanager.model.DonHang;
import com.example.appmanager.model.EventBus.DonHangEvent;
import com.example.appmanager.utils.Utils;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

public class DonHangAdapter extends RecyclerView.Adapter<DonHangAdapter.MyViewHolder> {
    List<DonHang> listdonhang;
    Context context;
    private RecyclerView.RecycledViewPool viewPool = new RecyclerView.RecycledViewPool();

    public DonHangAdapter(Context context, List<DonHang> listdonhang) {
        this.context = context;
        this.listdonhang = listdonhang;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_donhang, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        DonHang donHang = listdonhang.get(position);
        holder.txtdonhang.setText("Đơn hàng: " + donHang.getId());

        if (Utils.user_current.getStatus() == 1) {
            holder.trangthai.setText(trangThaiDon(donHang.getTrangthai()));
            holder.trangthai.setVisibility(View.VISIBLE);
        }

        LinearLayoutManager layoutManager = new LinearLayoutManager(holder.reChitiet.getContext(),
                LinearLayoutManager.VERTICAL, false);
        layoutManager.setInitialPrefetchItemCount(donHang.getItem().size());

        // adapter chi tiet
        ChiTietAdapter chiTietAdapter = new ChiTietAdapter(context, donHang.getItem());
        holder.reChitiet.setLayoutManager(layoutManager);
        holder.reChitiet.setAdapter(chiTietAdapter);
        holder.reChitiet.setRecycledViewPool(viewPool);

        if (Utils.user_current.getStatus() == 1) {
            holder.setListener(new ItemClickListener() {
                @Override
                public void onClick(View view, int pos, boolean isLongClick) {
                    if (isLongClick) {
                        EventBus.getDefault().postSticky(new DonHangEvent(donHang));
                    }
                }
            });
        }
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

    @Override
    public int getItemCount() {
        return listdonhang.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener {
        TextView txtdonhang, trangthai;
        RecyclerView reChitiet;
        ItemClickListener listener;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            txtdonhang = itemView.findViewById(R.id.iddonhang);
            trangthai = itemView.findViewById(R.id.tinhtrang);
            reChitiet = itemView.findViewById(R.id.recycleview_chitiet);
            if (Utils.user_current.getStatus() == 1) {
                itemView.setOnLongClickListener(this);
            }
        }

        public void setListener(ItemClickListener listener) {
            this.listener = listener;
        }

        @Override
        public boolean onLongClick(View v) {
            listener.onClick(v, getAdapterPosition(), true);
            return false;
        }
    }
}
