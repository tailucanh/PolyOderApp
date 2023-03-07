package com.example.polyOder.carts.oders.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.polyOder.databinding.LayoutItemReceiptHorizontalBinding;
import com.example.polyOder.databinding.LayoutItemReceiptVerticalBinding;
import com.example.polyOder.interfaces.OnTouchTheOder;
import com.example.polyOder.model.Receipt;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

public class ListOderAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private ArrayList<Receipt> listReceipt;
    private OnTouchTheOder onTouchTheOder;

    private int typeLayout;
    public static final int TYPE_HORIZONTAL = 0;
    public static final int TYPE_VERTICAL = 1;



    public ListOderAdapter(ArrayList<Receipt> listReceipt,OnTouchTheOder onTouchTheOder, int typeLayout) {
        this.listReceipt = listReceipt;
        this.onTouchTheOder = onTouchTheOder;
        this.typeLayout= typeLayout;

    }

    public void setFilterList(ArrayList<Receipt> filterList) {
        this.listReceipt = filterList;
        notifyDataSetChanged();
    }
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(TYPE_HORIZONTAL == viewType){
            return new ViewHolderListOderHorizontal(LayoutItemReceiptHorizontalBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false));
        }else{
            return new ViewHolderListOderVertical(LayoutItemReceiptVerticalBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false));
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Receipt receipt = listReceipt.get(position);
        if(receipt==null) {
            return;
        }else {
            if(TYPE_HORIZONTAL == holder.getItemViewType()){
                ((ViewHolderListOderHorizontal) holder).initData(receipt);
            }else {
                ((ViewHolderListOderVertical) holder).initData(receipt);
            }
        }

    }

    @Override
    public int getItemCount() {
        return listReceipt.size();
    }

    @Override
    public int getItemViewType(int position) {
        if(typeLayout == 0){
            return TYPE_HORIZONTAL;
        }else{
            return TYPE_VERTICAL;
        }

    }

    public String isFormatMoney(Double money){
        Locale locale = new Locale("en", "EN");
        NumberFormat numberFormat = NumberFormat.getInstance(locale);
        return  numberFormat.format(money);
    }

    public  class ViewHolderListOderHorizontal extends RecyclerView.ViewHolder {
        TextView tvNameBill,tvTotalMoney,tvTimeOder, tvStatus;
        ConstraintLayout layoutItem;

        public ViewHolderListOderHorizontal(LayoutItemReceiptHorizontalBinding binding) {
            super(binding.getRoot());
            tvNameBill=binding.tvNameBill;
            tvTotalMoney=binding.tvTotalMoney;
            tvTimeOder=binding.tvTimeOder;
            tvStatus= binding.tvStatus;
            layoutItem= binding.layoutItem;
        }
        void initData(Receipt receipt){

            tvNameBill.setText("POLY000"+receipt.getIdReceipt().substring(16,20));
            tvTimeOder.setText(receipt.getTimeOder());
            Double Money =receipt.getMoney();
            tvTotalMoney.setText(isFormatMoney(Money));
            tvStatus.setText(receipt.isStatusOder() ? "Đã thanh toán" : "Đơn hủy");


            layoutItem.setOnClickListener(v->{
                onTouchTheOder.onClickOder(receipt);
            });
        }
    }
    public  class ViewHolderListOderVertical extends RecyclerView.ViewHolder {
        TextView tvNameBill,tvTotalMoney,tvTimeOder,tvNoteBill, tvStatus;
        CardView layoutItem;
        LinearLayout layoutOderPrint;


        public ViewHolderListOderVertical(LayoutItemReceiptVerticalBinding binding) {
            super(binding.getRoot());
            tvNameBill=binding.tvNameBill;
            tvTotalMoney=binding.tvTotalMoney;
            tvTimeOder=binding.tvTimeOder;
            tvNoteBill= binding.tvNoteBill;
            tvStatus= binding.tvStatus;
            layoutItem= binding.layoutItem;
            layoutOderPrint = binding.layoutOderPrint;
        }
        void initData(Receipt receipt){
            tvNameBill.setText("POLY000"+receipt.getIdReceipt().substring(16,20));
            tvStatus.setText("Đã thanh toán");
            tvTimeOder.setText(receipt.getTimeOder());
            Double money =receipt.getMoney();
            tvTotalMoney.setText(isFormatMoney(money));
            if(!receipt.getNoteOder().equals("")){
                tvNoteBill.setText(receipt.getNoteOder());
            }
            tvStatus.setText(receipt.isStatusOder() ? "Đã thanh toán" : "Đơn hủy");
            layoutOderPrint.setOnClickListener(layout ->{
                if(receipt.isStatusOder()){
                    Toast.makeText(itemView.getContext(), "Chưa thiết lập máy in!", Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(itemView.getContext(), "Đơn đã bị hủy. Không thể in!", Toast.LENGTH_SHORT).show();
                }

            });

            layoutItem.setOnClickListener(v->{
                onTouchTheOder.onClickOder(receipt);
            });
        }
    }

}
