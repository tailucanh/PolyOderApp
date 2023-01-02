package com.example.polyOder.product.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.polyOder.R;
import com.example.polyOder.databinding.LayoutItemProductBinding;
import com.example.polyOder.databinding.LayoutItemTableBinding;
import com.example.polyOder.databinding.LayoutItemTypeProductBinding;
import com.example.polyOder.databinding.LayoutProductTypeBinding;
import com.example.polyOder.model.TypeProduct;

import java.util.ArrayList;

public class TypeProductAdapter extends RecyclerView.Adapter<TypeProductAdapter.ViewHolderTypeProduct> {
    private ArrayList<TypeProduct> listType;
    private Context context;
    private OnClickItemListener mOnClickItemListener;
    private OnItemLongClickListener mOnItemLongClickListener;

    public interface OnClickItemListener{
         void onClickItemProduct(TypeProduct typeProduct);
    }
    public interface OnItemLongClickListener{
         void onLongClickItemProduct(TypeProduct typeProduct);
    }


    public TypeProductAdapter(ArrayList<TypeProduct> listType) {
        this.listType = listType;
    }

    public TypeProductAdapter(ArrayList<TypeProduct> listType,OnClickItemListener mOnClickItemListener, OnItemLongClickListener mOnItemLongClickListener) {
        this.listType = listType;
        this.mOnClickItemListener = mOnClickItemListener;
        this.mOnItemLongClickListener = mOnItemLongClickListener;
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
                mOnClickItemListener.onClickItemProduct(typeProduct);
            });

            layoutItem.setOnLongClickListener(ic ->{
                mOnItemLongClickListener.onLongClickItemProduct(typeProduct);
                return true;
            });
        }
    }




}
