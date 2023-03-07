package com.example.polyOder.product.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.polyOder.databinding.LayoutItemTypeProductBinding;
import com.example.polyOder.interfaces.OnTouchTheTypeProduct;
import com.example.polyOder.model.TypeProduct;

import java.util.ArrayList;

public class TypeProductAdapter extends RecyclerView.Adapter<TypeProductAdapter.ViewHolderTypeProduct> {
    private ArrayList<TypeProduct> listType;
    private OnTouchTheTypeProduct onTouchTheTypeProduct;


    public TypeProductAdapter(ArrayList<TypeProduct> listType) {
        this.listType = listType;
    }

    public TypeProductAdapter(ArrayList<TypeProduct> listType,OnTouchTheTypeProduct onTouchTheTypeProduct ) {
        this.listType = listType;
        this.onTouchTheTypeProduct = onTouchTheTypeProduct;
    }


    public  void setFilterListType(ArrayList<TypeProduct> filterList ){
        this.listType = filterList;
        notifyDataSetChanged();

    }


    @NonNull
    @Override
    public ViewHolderTypeProduct onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new TypeProductAdapter.ViewHolderTypeProduct(LayoutItemTypeProductBinding.inflate(LayoutInflater.from(parent.getContext()),parent, false));

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderTypeProduct holder, int position) {
        TypeProduct typeProduct = listType.get(position);
        if (typeProduct == null) {
            return;
        } else {
            holder.initData(typeProduct);
        }

    }

    @Override
    public int getItemCount() {
        return listType.size();
    }

    class ViewHolderTypeProduct extends RecyclerView.ViewHolder {
        private TextView tvNameType;
        private ConstraintLayout layoutItem;

        public ViewHolderTypeProduct(LayoutItemTypeProductBinding binding) {
            super(binding.getRoot());
            tvNameType = binding.tvNameType;
            layoutItem = binding.layoutItemType;
        }

        void initData(TypeProduct typeProduct){
            tvNameType.setText(typeProduct.getNameType());
            layoutItem.setOnClickListener(ic ->{
                onTouchTheTypeProduct.onClickTypeProduct(typeProduct);
            });

            layoutItem.setOnLongClickListener(ic ->{
                onTouchTheTypeProduct.onLongClickTypeProduct(typeProduct);
                return true;
            });
        }
    }




}
