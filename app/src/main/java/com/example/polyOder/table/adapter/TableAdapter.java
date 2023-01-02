package com.example.polyOder.table.adapter;

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
import com.example.polyOder.base.OnclickOptionMenu;
import com.example.polyOder.databinding.LayoutItemTableBinding;
import com.example.polyOder.model.Table;

import java.util.ArrayList;
import java.util.List;

public class TableAdapter extends RecyclerView.Adapter<TableAdapter.ViewHolderTable> {
    private List<Table> list;
    private OnclickOptionMenu onclickOptionMenu;
    private Context context;
    private OnItemLongClickListener mOnItemLongClickListener;

    public interface OnItemLongClickListener{
        void onLongClickTable(Table table);
    }

    public TableAdapter(List<Table> list, OnclickOptionMenu onclickOptionMenu,OnItemLongClickListener mOnItemLongClickListener,Context context) {
        this.list = list;
        this.onclickOptionMenu = onclickOptionMenu;
        this.mOnItemLongClickListener = mOnItemLongClickListener;
        this.context = context;

    }
    public TableAdapter(List<Table> list, OnclickOptionMenu onclickOptionMenu,Context context) {
        this.list = list;
        this.onclickOptionMenu = onclickOptionMenu;
        this.context = context;

    }
    public void setFilterList(ArrayList<Table> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolderTable onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolderTable(LayoutItemTableBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderTable holder, int position) {
        Table table = list.get(position);
        if (table == null){
            return;
        } else {
            holder.initData(table,context);
        }

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class ViewHolderTable extends RecyclerView.ViewHolder {
        private TextView tv_name, tvStatusOff, tvStatusOn;
        private ConstraintLayout layoutHeaderTable,layoutBodyTable ;
        private ImageView logo;


        public ViewHolderTable(LayoutItemTableBinding binding) {
            super(binding.getRoot());
            tv_name = binding.tvNameTable;
            logo = binding.icLogoTable;
            tvStatusOff = binding.tvStatusEmpty;
            tvStatusOn = binding.tvLogoTable;

            layoutHeaderTable = binding.layoutHeaderTable;
            layoutBodyTable = binding.layoutBodyTable;
        }

        void initData(Table table, Context context){
            tv_name.setText(table.getName_table());
            itemView.setOnClickListener(v -> {
                onclickOptionMenu.onClick(table);
            });
            itemView.setOnLongClickListener(v ->{
                mOnItemLongClickListener.onLongClickTable(table);
                return true;
            });

            if (table.getStatus().equals("true")){
                logo.setVisibility(View.VISIBLE);
                tvStatusOff.setVisibility(View.GONE);
                tvStatusOn.setVisibility(View.VISIBLE);


                layoutHeaderTable.setBackgroundColor(context.getColor(R.color.orange_200) );
                layoutBodyTable.setBackgroundColor(context.getColor(R.color.grey_10) );
            } else {
                logo.setVisibility(View.GONE);
                tvStatusOff.setVisibility(View.VISIBLE);
                tvStatusOn.setVisibility(View.GONE);
                layoutHeaderTable.setBackgroundColor(context.getColor(R.color.grey_65) );
                layoutBodyTable.setBackgroundColor(context.getColor(R.color.grey_55) );
            }
        }

    }
}
