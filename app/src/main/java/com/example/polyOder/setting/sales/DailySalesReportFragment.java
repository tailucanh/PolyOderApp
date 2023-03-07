package com.example.polyOder.setting.sales;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.polyOder.MainActivity;
import com.example.polyOder.base.Helpers;
import com.example.polyOder.carts.oders.adapter.ListOderAdapter;
import com.example.polyOder.carts.oders.DetailReceiptFragment;
import com.example.polyOder.R;
import com.example.polyOder.interfaces.OnTouchTheOder;
import com.example.polyOder.viewModel.ReceiptViewModel;
import com.example.polyOder.base.BaseFragment;
import com.example.polyOder.databinding.FragmentSalesReportBinding;
import com.example.polyOder.model.Receipt;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DailySalesReportFragment extends BaseFragment implements OnTouchTheOder {

    private FragmentSalesReportBinding binding = null;
    private ReceiptViewModel viewModel;
    private ListOderAdapter adapter;
    private Helpers helpers = new Helpers();


    public DailySalesReportFragment() {
        // Required empty public constructor
    }

    public static DailySalesReportFragment newInstance() {
        DailySalesReportFragment fragment = new DailySalesReportFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentSalesReportBinding.inflate(inflater, container, false);
        viewModel = new ViewModelProvider(this).get(ReceiptViewModel.class);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setColorStatusBar(getActivity().getColor(R.color.white));
        hideBottomBar();
        listening();
        initObSever();
        loadData();
    }

    @Override
    public void loadData() {

        String strToday = helpers.isFormatTime(Calendar.getInstance().getTime(),"yyyy-MM-dd");
        String strToday2 = helpers.isFormatTime(Calendar.getInstance().getTime(),"dd-MM-yyyy");

        binding.tvDate.setText(strToday2);

        viewModel.getReceiptByToDay(strToday);
        viewModel.liveDateGetReceiptToDay.observe(getViewLifecycleOwner(), new Observer<List<Receipt>>() {
            @Override
            public void onChanged(List<Receipt> receipts) {
                if(receipts.size() == 0){
                    binding.layoutNotificationNullData.setVisibility(View.VISIBLE);
                    binding.tvOrderNumber.setText("0");
                    binding.tvTotalOderValue.setText("0");
                    binding.viewHeader.setVisibility(View.GONE);
                    binding.recVListOder.setVisibility(View.GONE);
                }else {
                    binding.layoutNotificationNullData.setVisibility(View.GONE);
                    binding.viewHeader.setVisibility(View.VISIBLE);
                    binding.recVListOder.setVisibility(View.VISIBLE);
                    binding.tvOrderNumber.setText(receipts.size() + "");
                    Double money = 0.0;
                    for (Receipt receipt : receipts) {
                        money += receipt.getMoney();
                    }

                    String strMoney = helpers.isFormatMoney(money);

                    binding.tvTotalOderValue.setText(strMoney);
                    adapter = new ListOderAdapter((ArrayList<Receipt>) receipts, DailySalesReportFragment.this, 0);
                    helpers.setReverseItemRecycleView(getContext(),binding.recVListOder);
                    binding.recVListOder.setAdapter(adapter);
                }
            }
        });

    }

    @Override
    public void listening() {
        binding.icBack.setOnClickListener(ic ->{
            backStack();
        });
    }

    @Override
    public void initObSever() {
    }

    @Override
    public void initView() {


    }


    @Override
    public void onClickOder(Receipt receipt) {
        replaceFragment(new DetailReceiptFragment(receipt));
    }
}