package com.example.polyOder.home.adapter;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.polyOder.databinding.LayoutItemProductBinding;
import com.example.polyOder.model.Product;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

public class ProductAdapterToOder extends RecyclerView.Adapter<ProductAdapterToOder.ViewHolderProduct> {
    private ArrayList<Product> listProduct;
    private Context context;


    public ProductAdapterToOder(ArrayList<Product> listProduct) {
        this.listProduct = listProduct;
    }

    public ProductAdapterToOder(ArrayList<Product> listProduct, Context context) {
        this.listProduct = listProduct;
        this.context = context;

    }

    public void setFilterList(ArrayList<Product> filterList) {
        this.listProduct = filterList;
        notifyDataSetChanged();
    }


    @NonNull
    @Override
    public ViewHolderProduct onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolderProduct(LayoutItemProductBinding.inflate(LayoutInflater.from(parent.getContext()),parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderProduct holder, int position) {
        Product product = listProduct.get(position);

        if (product == null) {
            return;
        } else {
            holder.initData(product,context);
        }

    }


    @Override
    public int getItemCount() {
        return listProduct.size();

    }
    public String isFormatMoney(Double money){
        Locale locale = new Locale("en", "EN");
        NumberFormat numberFormat = NumberFormat.getInstance(locale);
        return  numberFormat.format(money);
    }

    public class ViewHolderProduct extends RecyclerView.ViewHolder {
        ImageView imgProduct;
        TextView tvName, tvDescribe, tvPrice,tvCount;
        ConstraintLayout layoutItem;


        public ViewHolderProduct(LayoutItemProductBinding binding) {
            super(binding.getRoot());
            imgProduct = binding.imgProduct;
            tvName = binding.tvNameProduct;
            tvDescribe = binding.tvDescribeProduct;
            tvPrice = binding.tvPriceProduct;
            tvCount = binding.tvCount;
            layoutItem = binding.layoutItem;
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
            tvName.setText(product.getNameProduct());
            Double price = product.getPrice();
            tvPrice.setText(isFormatMoney(price) +"đ");
            tvDescribe.setText(product.getDescribe());
            tvCount.setVisibility(product.isSelected() ? View.VISIBLE : View.GONE);
            if(!product.isSelected()){
                tvCount.setText("0");
           }
            layoutItem.setOnClickListener(v ->{
                tvCount.setVisibility(View.VISIBLE);
                product.setSelected(true);
                int mValueNumber = Integer.valueOf(tvCount.getText().toString());
                int count = increasingTheNumber(mValueNumber);
                tvCount.setText(count+"");
                product.setIsClick(count);
            });

        }

        public  int increasingTheNumber(int count){
            int mCount = count +1;
            return  mCount;
        }


    }
}
