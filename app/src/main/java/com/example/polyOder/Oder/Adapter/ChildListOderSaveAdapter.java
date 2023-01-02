package com.example.polyOder.Oder.Adapter;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.polyOder.databinding.LayoutChildItemOderSaveBinding;
import com.example.polyOder.databinding.LayoutItemProductBinding;
import com.example.polyOder.model.Product;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class ChildListOderSaveAdapter extends RecyclerView.Adapter<ChildListOderSaveAdapter.ChildViewHolder> {
    private ArrayList<Product> listItemChild;
    private Context context;
    private List<String> listIdProduct;


    public ChildListOderSaveAdapter(List<String> listIdProduct) {
        this.listIdProduct = listIdProduct;
    }

    public ChildListOderSaveAdapter(ArrayList<Product> listItemChild, Context context) {
        this.listItemChild = listItemChild;
        this.context = context;

    }



    @NonNull
    @Override
    public ChildViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ChildViewHolder(LayoutChildItemOderSaveBinding.inflate(LayoutInflater.from(parent.getContext()),parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ChildViewHolder holder, int position) {
        Product product = listItemChild.get(position);
        listIdProduct.add(listItemChild.get(position).getId());
        if (product == null) {
            return;
        } else {
            holder.initData(product,context);
        }

    }


    @Override
    public int getItemCount() {
        return listItemChild.size();

    }

    public class ChildViewHolder extends RecyclerView.ViewHolder {
        ImageView imgProduct;
        TextView tvCount;

        public ChildViewHolder(LayoutChildItemOderSaveBinding binding) {
            super(binding.getRoot());
            imgProduct = binding.imgProduct;
            tvCount = binding.tvCount;

        }

       public void initData(Product product, Context context ) {
            StorageReference reference = FirebaseStorage.getInstance().getReference().child("imgProducts");
            reference.listAll().addOnSuccessListener(listResult -> {
                for (StorageReference files: listResult.getItems()){
                    if(files.getName().equals(product.getId())){
                        files.getDownloadUrl().addOnSuccessListener(uri -> {
                            Glide.with(context).load(uri).into(imgProduct);
                        });
                    }
                }
            });

           tvCount.setText(product.getIsClick()+"");


        }



    }
}
