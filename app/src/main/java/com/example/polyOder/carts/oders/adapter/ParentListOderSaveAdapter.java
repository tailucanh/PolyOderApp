package com.example.polyOder.carts.oders.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.polyOder.R;
import com.example.polyOder.databinding.LayoutParentItemOderSaveBinding;
import com.example.polyOder.interfaces.OnTouchTheOder;
import com.example.polyOder.interfaces.OnTouchTheOderSave;
import com.example.polyOder.model.Product;
import com.example.polyOder.model.Receipt;
import com.example.polyOder.model.Table;
import com.example.polyOder.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

import okhttp3.Call;

public class ParentListOderSaveAdapter extends RecyclerView.Adapter<ParentListOderSaveAdapter.ParentViewHolder> {

    private RecyclerView.RecycledViewPool viewPool = new RecyclerView.RecycledViewPool();
    private ArrayList<Receipt> listItemParent;
    private OnTouchTheOderSave onClickMenu;
    private Context context;


    public ParentListOderSaveAdapter(ArrayList<Receipt> listItemParent,OnTouchTheOderSave onClickMenu, Context context) {
        this.listItemParent = listItemParent;
        this.onClickMenu = onClickMenu;
        this.context = context;
    }

    public void setFilterList(ArrayList<Receipt> filterList) {
        this.listItemParent = filterList;
        notifyDataSetChanged();
    }



    @NonNull
    @Override
    public ParentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ParentViewHolder(LayoutParentItemOderSaveBinding.inflate(LayoutInflater.from(parent.getContext()),parent, false));

    }

    @Override
    public void onBindViewHolder(@NonNull ParentViewHolder parentViewHolder, int position) {
        Receipt orderSave = listItemParent.get(position);

        if (orderSave == null) {
            return;
        } else {
            parentViewHolder.initData(orderSave,position,context);
        }

    }


    @Override
    public int getItemCount()
    {
       return listItemParent.size();
    }

    public String isFormatMoney(Double money){
        Locale locale = new Locale("en", "EN");
        NumberFormat numberFormat = NumberFormat.getInstance(locale);
        return  numberFormat.format(money);
    }

    class ParentViewHolder extends RecyclerView.ViewHolder {
        private TextView tvNameTable, tvTime, tvTotalProduct, tvTotalMoney;
        private RecyclerView childRecyclerView;
        private CardView icMenu;

        public ParentViewHolder(LayoutParentItemOderSaveBinding binding) {
            super(binding.getRoot());
            tvNameTable = binding.tvNameTable;
            tvTime = binding.tvTimeOder;
            tvTotalProduct = binding.tvTotal;
            tvTotalMoney = binding.tvTotalMoney;
            childRecyclerView = binding.recVListProduct;
            icMenu = binding.icMenu;
        }

        @SuppressLint("RestrictedApi")
        public void initData(Receipt receipt , int position, Context context) {
            ArrayList<Product> listProduct = new ArrayList<>();

            DatabaseReference mRef = FirebaseDatabase.getInstance().getReference("list_product");
            mRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    listProduct.clear();
                    for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                        Product product = dataSnapshot.getValue(Product.class);
                        for(int i = 0; i < receipt.getListIdProduct().size(); i++){
                            if(product.getId().equals(receipt.getListIdProduct().get(i))){
                                listProduct.add(product);
                            }
                        }

                    }
                    for(int i = 0; i < receipt.getListCountProduct().size(); i++){
                        listProduct.get(i).setIsClick(receipt.getListCountProduct().get(i));
                    }
                    LinearLayoutManager layoutManager = new LinearLayoutManager(childRecyclerView.getContext(), LinearLayoutManager.HORIZONTAL, false);
                    layoutManager.setInitialPrefetchItemCount(receipt.getListIdProduct().size());
                    ChildListOderSaveAdapter childItemAdapter = new ChildListOderSaveAdapter(listProduct, context);
                    childRecyclerView.setLayoutManager(layoutManager);
                    childRecyclerView.setAdapter(childItemAdapter);
                    childRecyclerView.setRecycledViewPool(viewPool);


                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

            DatabaseReference mRef2 = FirebaseDatabase.getInstance().getReference("tables");
            mRef2.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                        Table table = dataSnapshot.getValue(Table.class);
                        if(table.getId_table().equals(receipt.getIdTable())){
                            tvNameTable.setText(table.getName_table());
                        }

                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
            tvTime.setText(receipt.getTimeOder().substring(0,16));
            Double money = receipt.getMoney();
            tvTotalMoney.setText(isFormatMoney(money)+"đ");
            int quantityProduct = 0;
            for(int i = 0 ; i < receipt.getListCountProduct().size();i++){
                quantityProduct += receipt.getListCountProduct().get(i);
            }
            tvTotalProduct.setText(quantityProduct+"");

            icMenu.setOnClickListener(ic ->{
                icMenu.animate().rotation(180).start();

                PopupMenu popupMenu = new PopupMenu(context, tvTime);
                popupMenu.getMenuInflater().inflate(R.menu.menu_popup_oder_save, popupMenu.getMenu());
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    popupMenu.setForceShowIcon(true);
                }


                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        int id = menuItem.getItemId();
                        onClickMenu.onClickMenu(receipt, id);
                        return true;
                    }
                });

                popupMenu.setOnDismissListener(new PopupMenu.OnDismissListener() {
                    @Override
                    public void onDismiss(PopupMenu popupMenu) {
                        icMenu.animate().rotation(0).start();
                    }
                });
                popupMenu.show();


            });


        }
    }
}
