package com.example.polyOder.Oder;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.polyOder.MainActivity;
import com.example.polyOder.R;
import com.example.polyOder.base.BaseFragment;
import com.example.polyOder.databinding.FragmentAddOderBinding;
import com.example.polyOder.databinding.FragmentDetailsProductBinding;
import com.example.polyOder.databinding.FragmentOderDetailsBinding;
import com.example.polyOder.databinding.LayoutFullImageProductBinding;
import com.example.polyOder.model.Product;
import com.example.polyOder.model.Receipt;
import com.example.polyOder.table.TableViewModel;
import com.example.polyOder.table.adapter.OderAdapter;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class DetailReceiptFragment extends BaseFragment {
    private FragmentOderDetailsBinding binding = null;
    private TableViewModel tableModel = null;
    private  Receipt receiptModel;
    private  ArrayList<String> listIdProduct;
    MainActivity mainActivity;


    public DetailReceiptFragment(Receipt receipt) {
        this.receiptModel = receipt;
    }

    public DetailReceiptFragment() {
    }

    public DetailReceiptFragment newInstance() {
        return new DetailReceiptFragment(receiptModel);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentOderDetailsBinding.inflate(inflater, container, false);
        tableModel = new ViewModelProvider(this).get(TableViewModel.class);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mainActivity.hideBottomBar();
        listening();
        initObSever();
    }

    @Override
    public void loadData() {
        binding.tvNameBill.setText("POLY000"+receiptModel.getIdReceipt().substring(16,20));
        if(receiptModel.isStatusOder()){
            binding.cavPrintOder.setVisibility(View.VISIBLE);
            binding.tvStatusOder2.setText("Đã thanh toán");
            binding.tvPaySuccess.setText("Đã thanh toán toàn bộ");

        }else {
            binding.cavPrintOder.setVisibility(View.GONE);
            binding.tvStatusOder2.setText("Đơn hủy");
            binding.tvPaySuccess.setText("Đơn đã hủy");

        }

        listIdProduct = (ArrayList<String>) receiptModel.getListIdProduct();
        tableModel.listLiveData(listIdProduct);
        tableModel.listProductOder.observe(getViewLifecycleOwner(), new Observer<List<Product>>() {
            @Override
            public void onChanged(List<Product> products) {
                if(products.size() != 0){
                    for(int i = 0; i < receiptModel.getListCountProduct().size(); i++){
                        for(int k = 0 ; k < products.size(); k++) {
                            products.get(k).setIsClick(receiptModel.getListCountProduct().get(k));
                        }
                    }
                }

                OderAdapter adapter = new OderAdapter(products,0,getActivity());
                if(receiptModel.getIdTable().length() > 0){
                    binding.tvStatusOder.setText("Thanh toán tại bàn");
                }else {
                    binding.tvStatusOder.setText("Thanh toán đem về");
                }
                Locale locale = new Locale("en", "EN");
                NumberFormat numberFormat = NumberFormat.getInstance(locale);
                String strMoney = numberFormat.format(receiptModel.getMoney());
                binding.tvTotalAmount.setText(strMoney);
                binding.tvTotalAmount2.setText(strMoney);
                binding.tvTotalAmount3.setText(strMoney);
                binding.tvTime.setText(receiptModel.getTimeOder());

                if(!receiptModel.getNoteOder().equals("")){
                    binding.tvNoteBill.setText(receiptModel.getNoteOder());
                }
                binding.listProductOder.setAdapter(adapter);
            }
        });





    }

    @Override
    public void listening() {
        binding.icBack.setOnClickListener(v->{
            backStack();
        });

        binding.btnPrintOder.setOnClickListener(btn ->{
            replaceFragment(new PrintBillFragment(receiptModel));
        });
    }

    @Override
    public void initObSever() {

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }



    @Override
    public void initView() {

    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mainActivity = (MainActivity)context;
    }

}