package com.example.appmp3online.Adapter;
import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.appmp3online.Model.BaiHat;
import com.example.appmp3online.R;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class AdapterBaiHat extends RecyclerView.Adapter<AdapterBaiHat.BaiHatViewHolder>{
    private final IListen iListen;
    public AdapterBaiHat(IListen iListen) {
        this.iListen = iListen;
    }
    @NonNull
    @Override
    public BaiHatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_nhac,parent,false);
        return new BaiHatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BaiHatViewHolder holder, @SuppressLint("RecyclerView") int position) {
        BaiHat baiHats = iListen.getData(position);
        holder.txtNameSong.setText(baiHats.getNameSong());
        holder.txtNameSinger.setText(baiHats.getNameSinger());
        holder.itemView.setOnClickListener(view -> iListen.onClickBaiHat(position));
    }

    @Override
    public int getItemCount() {
        return iListen.getCount();
    }


    public static class BaiHatViewHolder extends RecyclerView.ViewHolder{
        ImageView imgSong;
        TextView txtNameSong , txtNameSinger;
        public BaiHatViewHolder(@NonNull View itemView) {
            super(itemView);
            txtNameSong = itemView.findViewById(R.id.txtTenBaiHat);
            txtNameSinger = itemView.findViewById(R.id.txtTemCaSiBaiHat);
            imgSong = itemView.findViewById(R.id.imgBaiHat);

        }
    }
    public interface IListen{
        int getCount();
        BaiHat getData(int position);
        void onClickBaiHat(int position);
        Context onContext();
    }
}
