package com.hdu.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.hdu.R;
import com.hdu.bean.MusicBean;
import com.hdu.component.WebUrlImageView;

import java.util.List;

public class MusicListAdaper extends RecyclerView.Adapter<MusicListAdaper.MusicListViewHolder> {
    Context context;
    List<MusicBean> music_data_list;

    OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {
        public void OnItemClick(View view, int position);
    }

    public MusicListAdaper(Context context, List<MusicBean> music_data_list) {
        this.context = context;
        this.music_data_list = music_data_list;
    }

    @NonNull
    @Override
    public MusicListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_music_item, parent, false);
        return new MusicListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MusicListViewHolder holder, int position) {
        MusicBean musicBean = music_data_list.get(position);
        holder.item_num_tv.setText(musicBean.getId());
        holder.item_song_name_tv.setText(musicBean.getSong());
        holder.item_singer_name_tv.setText(musicBean.getSinger());
        holder.item_album_name_tv.setText(musicBean.getAlbum());
        holder.item_duration_tv.setText(musicBean.getDuration());
        if (musicBean.getImg() != null)
//            holder.item_img_iv.setImageURL(musicBean.getImg());
            Glide.with(context).load(musicBean.getImg()).into(holder.item_img_iv);
        else
            Glide.with(context).load(R.drawable.music_item_img_default).into(holder.item_img_iv);
        holder.itemView.setOnClickListener(v -> {
            onItemClickListener.OnItemClick(v, position);
        });
    }

    @Override
    public int getItemCount() {
        return music_data_list.size();
    }

    class MusicListViewHolder extends RecyclerView.ViewHolder {
        TextView item_num_tv, item_song_name_tv, item_singer_name_tv, item_album_name_tv, item_duration_tv;
        ImageView item_img_iv;

        public MusicListViewHolder(@NonNull View itemView) {
            super(itemView);
            item_num_tv = itemView.findViewById(R.id.music_item_num);
            item_song_name_tv = itemView.findViewById(R.id.music_item_song_name);
            item_singer_name_tv = itemView.findViewById(R.id.music_item_singer_name);
            item_album_name_tv = itemView.findViewById(R.id.music_item_album_name);
            item_duration_tv = itemView.findViewById(R.id.music_item_duration);
            item_img_iv = itemView.findViewById(R.id.music_item_img);
        }
    }
}

















