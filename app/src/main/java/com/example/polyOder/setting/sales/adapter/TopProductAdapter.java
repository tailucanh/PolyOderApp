package com.example.polyOder.setting.sales.adapter;

import android.content.Context;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.polyOder.R;
import com.example.polyOder.carts.oders.adapter.ListOderAdapter;
import com.example.polyOder.databinding.LayoutItemProductBinding;
import com.example.polyOder.databinding.LayoutItemTopMonthProductBinding;
import com.example.polyOder.databinding.LayoutItemTopProductBinding;
import com.example.polyOder.interfaces.OnTouchTheProduct;
import com.example.polyOder.model.Product;
import com.example.polyOder.model.Receipt;
import com.example.polyOder.product.adapter.ProductAdapter;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

public class TopProductAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private Context context;
    private ArrayList<Product> listProduct;
    private AnimatedVectorDrawable emptyHeart;
    private AnimatedVectorDrawable fillHeart;
    private boolean full = false;
    private OnTouchTheProduct mOnClickItemListener;
    private int typeLayout;
    public static final int TYPE_HORIZONTAL = 0;
    public static final int TYPE_VERTICAL = 1;


    public TopProductAdapter(ArrayList<Product> listProduct, OnTouchTheProduct mOnClickItemListener, int typeLayout, Context context) {
        this.listProduct = listProduct;
        this.context = context;
        this.mOnClickItemListener = mOnClickItemListener;
        this.typeLayout = typeLayout;
    }
    public void setFilterList(ArrayList<Product> filterList) {
        this.listProduct = filterList;
        notifyDataSetChanged();
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(TYPE_HORIZONTAL == viewType){
            return new ViewHolderTopMonthProduct(LayoutItemTopMonthProductBinding.inflate(LayoutInflater.from(parent.getContext()),parent, false));
        }else {
            return new ViewHolderTopProduct(LayoutItemTopProductBinding.inflate(LayoutInflater.from(parent.getContext()),parent, false));
        }

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Product product = listProduct.get(position);
        if(product==null) {
            return;
        }else {
            if(TYPE_HORIZONTAL == holder.getItemViewType()){
                ((ViewHolderTopMonthProduct) holder).initData(product,context);
            }else {
                ((ViewHolderTopProduct) holder).initData(product,context,position);
            }
        }

    }

    @Override
    public int getItemCount() {
        return listProduct.size();

    }
    @Override
    public int getItemViewType(int position) {
        if(typeLayout == 0){
            return TYPE_HORIZONTAL;
        }else{
            return TYPE_VERTICAL;
        }

    }

    public class ViewHolderTopProduct extends RecyclerView.ViewHolder {
        ImageView imgProduct;
        TextView tvName, tvDescribe, tvPrice,tvDetails,tvTop;

        public ViewHolderTopProduct(LayoutItemTopProductBinding binding) {
            super(binding.getRoot());
            imgProduct = binding.imgProduct;
            tvName = binding.tvNameTop;
            tvDescribe = binding.tvDescTop;
            tvPrice = binding.tvPriceTop;
            tvDetails = binding.tvDetails;
            tvTop = binding.tvTop;
        }

        void initData(Product product, Context context, int position) {
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

            Locale locale = new Locale("en","EN");
            NumberFormat numberFormat = NumberFormat.getInstance(locale);
            Double price = product.getPrice();
            String strPrice = numberFormat.format(price);
            tvPrice.setText(strPrice +"đ");

            tvDescribe.setText(product.getDescribe());
            tvDetails.setOnClickListener(tv ->{
                mOnClickItemListener.onClickProduct(product);
            });

            tvTop.setText((position+1)+"");


        }


    }

    public class ViewHolderTopMonthProduct extends RecyclerView.ViewHolder {
        ImageView imgProduct, icHeart;
        TextView tvName, tvDescribe, tvPrice;
        ConstraintLayout layoutItem;

        public ViewHolderTopMonthProduct(LayoutItemTopMonthProductBinding binding) {
            super(binding.getRoot());
            imgProduct = binding.imgProduct;
            tvName = binding.tvNameTop;
            tvDescribe = binding.tvDescTop;
            tvPrice = binding.tvPriceTop;
            layoutItem = binding.layoutItem;
            icHeart = binding.icHeart;
        }

        void initData(Product product, Context context) {
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

            Locale locale = new Locale("en","EN");
            NumberFormat numberFormat = NumberFormat.getInstance(locale);
            Double price = product.getPrice();
            String strPrice = numberFormat.format(price);
            tvPrice.setText(strPrice +"đ");
            tvDescribe.setText(product.getDescribe());
            layoutItem.setOnClickListener(tv ->{
                mOnClickItemListener.onClickProduct(product);
            });
            icHeart.setOnClickListener(ic ->{
                animateHeart(icHeart);
            });

        }
        public void animateHeart(ImageView imageView)
        {
            emptyHeart = (AnimatedVectorDrawable)context.getDrawable(R.drawable.avd_heart_empty);
            fillHeart = (AnimatedVectorDrawable) context.getDrawable( R.drawable.avd_heart_fill);
            AnimatedVectorDrawable drawable = full ? emptyHeart : fillHeart;
            imageView.setImageDrawable(drawable);
            drawable.start();
            full = !full;
        }
    }
}
