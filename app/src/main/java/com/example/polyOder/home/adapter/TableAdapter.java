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

import com.example.polyOder.R;
import com.example.polyOder.databinding.LayoutItemTableBinding;
import com.example.polyOder.interfaces.OnTouchTheTable;
import com.example.polyOder.model.Table;

import java.util.ArrayList;
import java.util.List;

public class TableAdapter extends RecyclerView.Adapter<TableAdapter.ViewHolderTable> {
    private List<Table> listTable;
    private OnTouchTheTable onclickOptionTable ;
    private Context context;


    public TableAdapter(List<Table> listTable, OnTouchTheTable onclickOptionTable, Context context) {
        this.listTable = listTable;
        this.onclickOptionTable = onclickOptionTable;
        this.context = context;

    }

    public void setFilterList(ArrayList<Table> listTable) {
        this.listTable = listTable;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolderTable onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolderTable(LayoutItemTableBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderTable holder, int position) {
        Table table = listTable.get(position);
        if (table == null){
            return;
        } else {
            holder.initData(table,context);
        }

    }

    @Override
    public int getItemCount() {
        return listTable.size();
    }

    class ViewHolderTable extends RecyclerView.ViewHolder {
        private TextView tvName, tvLogo;
        private ConstraintLayout layoutHeaderTable,layoutBodyTable ;
        private ImageView logo;


        public ViewHolderTable(LayoutItemTableBinding binding) {
            super(binding.getRoot());
            tvName = binding.tvNameTable;
            logo = binding.icLogoTable;
            tvLogo = binding.tvLogoTable;
            layoutHeaderTable = binding.layoutHeaderTable;
            layoutBodyTable = binding.layoutBodyTable;
        }

        void initData(Table table, Context context){
            tvName.setText(table.getName_table());
            itemView.setOnClickListener(v -> {
                onclickOptionTable.onClickTable(table);
            });
            itemView.setOnLongClickListener(v ->{
                onclickOptionTable.onLongClickTable(table);
                return true;
            });

            if (table.getStatus().equals("true")){
                tvName.setTextColor(context.getColor(R.color.brown_250));
                logo.setImageDrawable(context.getDrawable(R.drawable.ic_logo));
                tvLogo.setTextColor(context.getColor(R.color.brown_250));
                layoutHeaderTable.setBackgroundColor(context.getColor(R.color.orange_200) );
                layoutBodyTable.setBackgroundColor(context.getColor(R.color.grey_10) );

            } else {
                tvName.setTextColor(context.getColor(R.color.grey_200));
                logo.setImageDrawable(context.getDrawable(R.drawable.ic_logo_black));
                tvLogo.setTextColor(context.getColor(R.color.grey_200));
                layoutHeaderTable.setBackgroundColor(context.getColor(R.color.grey_65) );
                layoutBodyTable.setBackgroundColor(context.getColor(R.color.grey_55) );
            }
        }

    }
}
