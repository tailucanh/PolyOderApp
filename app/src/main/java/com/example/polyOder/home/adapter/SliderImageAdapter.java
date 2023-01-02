package com.example.polyOder.home.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.polyOder.R;
import com.example.polyOder.model.PhotosSlider;

import java.util.ArrayList;

public class SliderImageAdapter extends RecyclerView.Adapter<SliderImageAdapter.PhotoViewHolder>{
    ArrayList<PhotosSlider> listPhotos;

    public SliderImageAdapter(ArrayList<PhotosSlider> listPhotos) {
        this.listPhotos = listPhotos;
    }

    @NonNull
    @Override
    public PhotoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_items_slide_img_home,parent,false);
        return new PhotoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PhotoViewHolder holder, int position) {
        PhotosSlider photo = listPhotos.get(position);
        holder.img_slide.setImageResource(photo.getIdPhoto());
    }

    @Override
    public int getItemCount() {
        return listPhotos.size();
    }

    public class PhotoViewHolder extends RecyclerView.ViewHolder{
        ImageView img_slide;
        public PhotoViewHolder(@NonNull View view) {
            super(view);
            img_slide = view.findViewById(R.id.imgSlider);

        }
    }
}
