package com.example.polyOder.table.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.polyOder.databinding.ItemOderMenuBinding;
import com.example.polyOder.databinding.ItemPrintOderBinding;
import com.example.polyOder.model.Product;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class OderAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    List<Product> productList;
    Context context;
    private int typeLayout;
    public static final int TYPE_LAYOUT_DETAIL = 0;
    public static final int TYPE_LAYOUT_PRINT = 1;


    public OderAdapter(List<Product> productList, int typeLayout, Context context ) {
        this.productList = productList;
        this.typeLayout = typeLayout;
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(TYPE_LAYOUT_DETAIL == viewType){
            return new ViewHolderDetails(ItemOderMenuBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false));
        }else {
            return new ViewHolderPrints(ItemPrintOderBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false));
        }
    }

    @Override
    public void onBindViewHolder(@NonNull  RecyclerView.ViewHolder holder, int position) {
        Product product = productList.get(position);
        if (product == null){
            return;
        } else
        if(TYPE_LAYOUT_DETAIL == holder.getItemViewType()){
            ((OderAdapter.ViewHolderDetails) holder).initData(product,context);
        }else {
            ((OderAdapter.ViewHolderPrints) holder).initData(product);
        }


    }

    @Override
    public int getItemCount() {
        if (productList != null){
            return productList.size();
        }
        return 0;
    }
    @Override
    public int getItemViewType(int position) {
        if(typeLayout == 0){
            return TYPE_LAYOUT_DETAIL;
        }else{
            return TYPE_LAYOUT_PRINT;
        }

    }


    class ViewHolderDetails extends RecyclerView.ViewHolder{
        private TextView tvName, tvPrice, tvCountProduct;
        private ImageView imgProduct;

        public ViewHolderDetails(@NonNull ItemOderMenuBinding binding) {
            super(binding.getRoot());
            tvName = binding.tvNameProductOder;
            tvPrice = binding.tvMoneyProductOder;
            imgProduct = binding.itemImg;
            tvCountProduct = binding.tvCountProduct;
        }
         public void initData(Product product,Context context){

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
             Double price = product.getPrice() * product.getIsClick();
             String strPrice = numberFormat.format(price);
             tvPrice.setText(strPrice);
             tvCountProduct.setText(product.getIsClick()+"");
        }

    }

    class ViewHolderPrints extends RecyclerView.ViewHolder{
        private TextView tvName, tvPrice, tvCountProduct,tvTotalMoney;

        public ViewHolderPrints(@NonNull ItemPrintOderBinding binding) {
            super(binding.getRoot());
            tvName = binding.tvNameProductPrint;
            tvPrice = binding.tvPriceProductPrint;
            tvTotalMoney = binding.tvTotalProduct;
            tvCountProduct = binding.tvCountProduct;
        }
        public void initData(Product product){
            tvName.setText(product.getNameProduct());
            Locale locale = new Locale("en","EN");
            NumberFormat numberFormat = NumberFormat.getInstance(locale);
            Double price = product.getPrice();
            String strPrice = numberFormat.format(price);
            tvPrice.setText(strPrice);
            Double totalPrice = product.getPrice() * product.getIsClick();
            String strTotalPrice = numberFormat.format(totalPrice);
            tvTotalMoney.setText(strTotalPrice);
            if(product.getIsClick() < 10){
                tvCountProduct.setText("0"+product.getIsClick());
            }else {
                tvCountProduct.setText(product.getIsClick()+"");
            }

        }

    }


}
