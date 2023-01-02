package com.example.polyOder.Oder.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.polyOder.databinding.LayoutItemProductBinding;
import com.example.polyOder.databinding.LayoutParentItemOderSaveBinding;
import com.example.polyOder.model.Receipt;

import java.util.ArrayList;

public class ParentListOderSaveAdapter extends RecyclerView.Adapter<ParentListOderSaveAdapter.ParentViewHolder> {

    private RecyclerView.RecycledViewPool viewPool = new RecyclerView.RecycledViewPool();
    private ArrayList<Receipt> listItemParent;
    Context context;

    ParentListOderSaveAdapter() {

    }

    public ParentListOderSaveAdapter(ArrayList<Receipt> listItemParent, Context context) {
        this.listItemParent = listItemParent;
        this.context = context;
    }

    @NonNull
    @Override
    public ParentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ParentViewHolder(LayoutParentItemOderSaveBinding.inflate(LayoutInflater.from(parent.getContext()),parent, false));

    }

    @Override
    public void onBindViewHolder(@NonNull ParentViewHolder parentViewHolder, int position) {
        Receipt receipt = listItemParent.get(position);
        if (receipt == null) {
            return;
        } else {
            parentViewHolder.initData(receipt,context);
        }

    }


    @Override
    public int getItemCount()
    {
       return listItemParent.size();
    }


    class ParentViewHolder extends RecyclerView.ViewHolder {
        private TextView tvNameTable, tvTime, tvTotalProduct, tvTotalMoney;
        private RecyclerView childRecyclerView;

        public ParentViewHolder(LayoutParentItemOderSaveBinding binding) {
            super(binding.getRoot());
            tvNameTable = binding.tvNameTable;
            tvTime = binding.tvTimeOder;
            tvTotalProduct = binding.tvTotal;
            tvTotalMoney = binding.tvTotalMoney;
            childRecyclerView = binding.recVListProduct;
        }

        public void initData(Receipt receipt, Context context ) {
            LinearLayoutManager layoutManager = new LinearLayoutManager(childRecyclerView.getContext(), LinearLayoutManager.HORIZONTAL, false);
            layoutManager.setInitialPrefetchItemCount(receipt.getListIdProduct().size());
            ChildListOderSaveAdapter childItemAdapter = new ChildListOderSaveAdapter(receipt.getListIdProduct());
            childRecyclerView.setLayoutManager(layoutManager);
            childRecyclerView.setAdapter(childItemAdapter);
            childRecyclerView.setRecycledViewPool(viewPool);





        }



    }
}
