package com.example.polyOder.setting;

import static java.time.DayOfWeek.MONDAY;
import static java.time.DayOfWeek.SUNDAY;
import static java.time.temporal.TemporalAdjusters.nextOrSame;
import static java.time.temporal.TemporalAdjusters.previousOrSame;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import com.example.polyOder.MainActivity;
import com.example.polyOder.Oder.Adapter.ListOderAdapter;
import com.example.polyOder.Oder.DetailReceiptFragment;
import com.example.polyOder.R;
import com.example.polyOder.base.BaseFragment;
import com.example.polyOder.databinding.DialogChooseFunctionFilterStatisticBinding;
import com.example.polyOder.databinding.DialogChooseTimeStatisticBinding;
import com.example.polyOder.databinding.FragmentOderStatisticsBinding;
import com.example.polyOder.model.Receipt;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class StatisticalFragment extends BaseFragment implements ListOderAdapter.OnClickListener {
    private FragmentOderStatisticsBinding binding = null;
    private SettingViewModel viewModel;
    private ListOderAdapter adapter;
    private int lastSelectedYear;
    private int lastSelectedMonth;
    private int lastSelectedDayOfMonth;
    MainActivity mainActivity;


    public StatisticalFragment() {
        // Required empty public constructor
    }

    public static StatisticalFragment newInstance() {
        StatisticalFragment fragment = new StatisticalFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Calendar c = Calendar.getInstance();
        this.lastSelectedYear = c.get(Calendar.YEAR);
        this.lastSelectedMonth = c.get(Calendar.MONTH);
        this.lastSelectedDayOfMonth = c.get(Calendar.DAY_OF_MONTH);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentOderStatisticsBinding.inflate(inflater, container, false);
        viewModel = new ViewModelProvider(this).get(SettingViewModel.class);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Window window = getActivity().getWindow();
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        window.setStatusBarColor(getActivity().getColor(R.color.white));
        mainActivity.hideBottomBar();
        listening();
        initObSever();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void loadData() {
        getPaidOder();
    }

    @Override
    public void listening() {
        binding.icBack.setOnClickListener(v -> backStack());
        binding.layoutTime.setOnClickListener(v -> dialogFunctionPickDate(requireContext()));

        binding.tvFilterFunction.setOnClickListener(v ->{
            dialogFunctionFilterOder(getContext());
        });

    }


    @Override
    public void initObSever() {


    }

    @Override
    public void initView() {
    }


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mainActivity = (MainActivity)context;
    }


    private void dialogFunctionFilterOder(Context context) {
        final Dialog dialog = new Dialog(context);
        DialogChooseFunctionFilterStatisticBinding bindingDialog = DialogChooseFunctionFilterStatisticBinding.inflate(LayoutInflater.from(context));
        dialog.setContentView(bindingDialog.getRoot());
        Window window = dialog.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        WindowManager.LayoutParams layoutParams = window.getAttributes();
        layoutParams.gravity = Gravity.BOTTOM;
        window.setAttributes(layoutParams);
        bindingDialog.dialogChooserFunction.setTranslationY(150);
        bindingDialog.dialogChooserFunction.animate().translationYBy(-150).setDuration(400);
        if( binding.tvFilterFunction.getText().toString().equals("Theo ngày hoàn thành")){
            bindingDialog.icFilter1.setVisibility(View.VISIBLE);
            bindingDialog.icFilter2.setVisibility(View.GONE);
            bindingDialog.icFilter3.setVisibility(View.GONE);
            bindingDialog.icFilter4.setVisibility(View.GONE);
        }else if( binding.tvFilterFunction.getText().toString().equals("Theo ngày hủy")){
            bindingDialog.icFilter2.setVisibility(View.VISIBLE);
            bindingDialog.icFilter1.setVisibility(View.GONE);
            bindingDialog.icFilter3.setVisibility(View.GONE);
            bindingDialog.icFilter4.setVisibility(View.GONE);
        }else if(binding.tvFilterFunction.getText().toString().equals("Theo thanh toán tại bàn")){
            bindingDialog.icFilter3.setVisibility(View.VISIBLE);
            bindingDialog.icFilter1.setVisibility(View.GONE);
            bindingDialog.icFilter2.setVisibility(View.GONE);
            bindingDialog.icFilter4.setVisibility(View.GONE);
        }else if(binding.tvFilterFunction.getText().toString().equals("Theo thanh toán đem về")){
            bindingDialog.icFilter4.setVisibility(View.VISIBLE);
            bindingDialog.icFilter1.setVisibility(View.GONE);
            bindingDialog.icFilter2.setVisibility(View.GONE);
            bindingDialog.icFilter3.setVisibility(View.GONE);
        }



        bindingDialog.layoutFilterCompleteOder.setOnClickListener(ic ->{
            binding.tvFilterFunction.setText("Theo ngày hoàn thành");
            binding.tvFilterTime.setText("Tất cả");
            getPaidOder();
            dialog.cancel();
        });
        bindingDialog.layoutFilterCancelOder.setOnClickListener(ic ->{
            binding.tvFilterFunction.setText("Theo ngày hủy");
            binding.tvFilterTime.setText("Tất cả");
            getCancelOder();
            dialog.cancel();
        });
        bindingDialog.layoutFilterOderToTable.setOnClickListener(ic ->{
            binding.tvFilterFunction.setText("Theo thanh toán tại bàn");
            binding.tvFilterTime.setText("Tất cả");
            getPaidOderToTable();
            dialog.cancel();
        });
        bindingDialog.layoutFilterOderTakeOut.setOnClickListener(ic ->{
            binding.tvFilterFunction.setText("Theo thanh toán đem về");
            binding.tvFilterTime.setText("Tất cả");
            getPaidOderToTakeOut();
            dialog.cancel();
        });


        dialog.show();

    }

    private void getPaidOder(){
        viewModel.getAllReceipt();
        viewModel.liveDateGetAllReceipt.observe(getViewLifecycleOwner(), new Observer<List<Receipt>>() {
            @Override
            public void onChanged(List<Receipt> receipts) {
                binding.tvOrderNumber.setText(receipts.size() + "");
                Double money = 0.0;
                for (Receipt receipt : receipts) {
                    money += receipt.getMoney();
                }
                Locale locale = new Locale("en", "EN");
                NumberFormat numberFormat = NumberFormat.getInstance(locale);
                String strMoney = numberFormat.format(money);
                binding.tvTotalOderValue.setText(strMoney);
                adapter = new ListOderAdapter((ArrayList<Receipt>) receipts,StatisticalFragment.this,0);
                LinearLayoutManager layoutManager  = new LinearLayoutManager(getContext());
                layoutManager.setStackFromEnd(true);
                layoutManager.setReverseLayout(true);
                binding.recVListOder.setLayoutManager(layoutManager);
                binding.recVListOder.setAdapter(adapter);
            }
        });

        viewModel.liveDateGetReceipt.observe(getViewLifecycleOwner(), new Observer<List<Receipt>>() {
            @Override
            public void onChanged(List<Receipt> receipts) {
                if(receipts.size() == 0){
                    binding.layoutNotificationNullData.setVisibility(View.VISIBLE);
                    binding.tvOrderNumber.setText("0");
                    binding.tvTotalOderValue.setText("0");
                    binding.viewHeader.setVisibility(View.GONE);
                    binding.recVListOder.setVisibility(View.GONE);
                    adapter.notifyDataSetChanged();
                }else {
                    binding.layoutNotificationNullData.setVisibility(View.GONE);
                    binding.viewHeader.setVisibility(View.VISIBLE);
                    binding.recVListOder.setVisibility(View.VISIBLE);
                    binding.tvOrderNumber.setText(receipts.size() + "");
                    Double money = 0.0;
                    for (Receipt receipt : receipts) {
                        money += receipt.getMoney();
                    }
                    Locale locale = new Locale("en", "EN");
                    NumberFormat numberFormat = NumberFormat.getInstance(locale);
                    String strMoney = numberFormat.format(money);

                    binding.tvTotalOderValue.setText(strMoney);
                    adapter = new ListOderAdapter((ArrayList<Receipt>) receipts,StatisticalFragment.this,0);
                    LinearLayoutManager layoutManager  = new LinearLayoutManager(getContext());
                    layoutManager.setStackFromEnd(true);
                    layoutManager.setReverseLayout(true);
                    binding.recVListOder.setLayoutManager(layoutManager);
                    binding.recVListOder.setAdapter(adapter);
                }
            }
        });
    }

    private void getCancelOder(){
        adapter.notifyDataSetChanged();
        viewModel.getAllCancelReceipt();
        viewModel.liveDateGetAllCancelReceipt.observe(getViewLifecycleOwner(), new Observer<List<Receipt>>() {
            @Override
            public void onChanged(List<Receipt> receipts) {
                binding.tvOrderNumber.setText(receipts.size() + "");
                Double money = 0.0;
                for (Receipt receipt : receipts) {
                    money += receipt.getMoney();
                }
                Locale locale = new Locale("en", "EN");
                NumberFormat numberFormat = NumberFormat.getInstance(locale);
                String strMoney = numberFormat.format(money);
                binding.tvTotalOderValue.setText(strMoney);
                adapter = new ListOderAdapter((ArrayList<Receipt>) receipts,StatisticalFragment.this,0);
                LinearLayoutManager layoutManager  = new LinearLayoutManager(getContext());
                layoutManager.setStackFromEnd(true);
                layoutManager.setReverseLayout(true);
                binding.recVListOder.setLayoutManager(layoutManager);
                binding.recVListOder.setAdapter(adapter);
            }
        });

        viewModel.liveDateGetCancelReceipt.observe(getViewLifecycleOwner(), new Observer<List<Receipt>>() {
            @Override
            public void onChanged(List<Receipt> receipts) {
                if(receipts.size() == 0){
                    binding.layoutNotificationNullData.setVisibility(View.VISIBLE);
                    binding.tvOrderNumber.setText("0");
                    binding.tvTotalOderValue.setText("0");
                    binding.viewHeader.setVisibility(View.GONE);
                    binding.recVListOder.setVisibility(View.GONE);
                    adapter.notifyDataSetChanged();
                }else {
                    binding.layoutNotificationNullData.setVisibility(View.GONE);
                    binding.viewHeader.setVisibility(View.VISIBLE);
                    binding.recVListOder.setVisibility(View.VISIBLE);
                    binding.tvOrderNumber.setText(receipts.size() + "");
                    Double money = 0.0;
                    for (Receipt receipt : receipts) {
                        money += receipt.getMoney();
                    }
                    Locale locale = new Locale("en", "EN");
                    NumberFormat numberFormat = NumberFormat.getInstance(locale);
                    String strMoney = numberFormat.format(money);

                    binding.tvTotalOderValue.setText(strMoney);
                    adapter = new ListOderAdapter((ArrayList<Receipt>) receipts,StatisticalFragment.this,0);
                    LinearLayoutManager layoutManager  = new LinearLayoutManager(getContext());
                    layoutManager.setStackFromEnd(true);
                    layoutManager.setReverseLayout(true);
                    binding.recVListOder.setLayoutManager(layoutManager);
                    binding.recVListOder.setAdapter(adapter);
                }
            }
        });
    }

    private void getPaidOderToTable(){
        ArrayList<Receipt> listData = new ArrayList<>();
         DatabaseReference reference = FirebaseDatabase.getInstance().getReference("PayReceipt");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listData.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Receipt receipt = dataSnapshot.getValue(Receipt.class);
                    if(receipt.getIdTable().length() > 0){
                        listData.add(receipt);
                    }
                }
                binding.tvOrderNumber.setText(listData.size() + "");
                Double money = 0.0;
                for (Receipt receipt : listData) {
                    money += receipt.getMoney();
                }
                Locale locale = new Locale("en", "EN");
                NumberFormat numberFormat = NumberFormat.getInstance(locale);
                String strMoney = numberFormat.format(money);
                binding.tvTotalOderValue.setText(strMoney);
                adapter = new ListOderAdapter( listData,StatisticalFragment.this,0);
                LinearLayoutManager layoutManager  = new LinearLayoutManager(getContext());
                layoutManager.setStackFromEnd(true);
                layoutManager.setReverseLayout(true);
                binding.recVListOder.setLayoutManager(layoutManager);
                binding.recVListOder.setAdapter(adapter);

            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void getPaidOderToTakeOut(){
        ArrayList<Receipt> listData = new ArrayList<>();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("PayReceipt");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listData.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Receipt receipt = dataSnapshot.getValue(Receipt.class);
                    if(receipt.getIdTable().equals("")){
                        listData.add(receipt);
                    }
                }
                binding.tvOrderNumber.setText(listData.size() + "");
                Double money = 0.0;
                for (Receipt receipt : listData) {
                    money += receipt.getMoney();
                }
                Locale locale = new Locale("en", "EN");
                NumberFormat numberFormat = NumberFormat.getInstance(locale);
                String strMoney = numberFormat.format(money);
                binding.tvTotalOderValue.setText(strMoney);
                adapter = new ListOderAdapter( listData,StatisticalFragment.this,0);
                LinearLayoutManager layoutManager  = new LinearLayoutManager(getContext());
                layoutManager.setStackFromEnd(true);
                layoutManager.setReverseLayout(true);
                binding.recVListOder.setLayoutManager(layoutManager);
                binding.recVListOder.setAdapter(adapter);

            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }


    private void getPaidOderToDate(String dateOder){
        viewModel.getReceiptByToDay(dateOder);
        viewModel.liveDateGetReceiptToDay.observe(getViewLifecycleOwner(), new Observer<List<Receipt>>() {
            @Override
            public void onChanged(List<Receipt> receipts) {
                if(receipts.size() == 0){
                    binding.layoutNotificationNullData.setVisibility(View.VISIBLE);
                    binding.tvOrderNumber.setText("0");
                    binding.tvTotalOderValue.setText("0");
                    binding.viewHeader.setVisibility(View.GONE);
                    binding.recVListOder.setVisibility(View.GONE);
                    adapter.notifyDataSetChanged();
                }else {
                    binding.layoutNotificationNullData.setVisibility(View.GONE);
                    binding.viewHeader.setVisibility(View.VISIBLE);
                    binding.recVListOder.setVisibility(View.VISIBLE);
                    binding.tvOrderNumber.setText(receipts.size() + "");
                    Double money = 0.0;
                    for (Receipt receipt : receipts) {
                        money += receipt.getMoney();
                    }
                    Locale locale = new Locale("en", "EN");
                    NumberFormat numberFormat = NumberFormat.getInstance(locale);
                    String strMoney = numberFormat.format(money);

                    binding.tvTotalOderValue.setText(strMoney);
                    adapter = new ListOderAdapter((ArrayList<Receipt>) receipts,StatisticalFragment.this,0);
                    LinearLayoutManager layoutManager  = new LinearLayoutManager(getContext());
                    layoutManager.setStackFromEnd(true);
                    layoutManager.setReverseLayout(true);
                    binding.recVListOder.setLayoutManager(layoutManager);
                    binding.recVListOder.setAdapter(adapter);
                }
            }
        });
    }

    private void getCancelOderToDate(String dateOder){
        viewModel.getReceiptCancelByToDay(dateOder);
        viewModel.liveDateGetCancelReceiptToDay.observe(getViewLifecycleOwner(), new Observer<List<Receipt>>() {
            @Override
            public void onChanged(List<Receipt> receipts) {
                if(receipts.size() == 0){
                    binding.layoutNotificationNullData.setVisibility(View.VISIBLE);
                    binding.tvOrderNumber.setText("0");
                    binding.tvTotalOderValue.setText("0");
                    binding.viewHeader.setVisibility(View.GONE);
                    binding.recVListOder.setVisibility(View.GONE);
                    adapter.notifyDataSetChanged();
                }else {
                    binding.layoutNotificationNullData.setVisibility(View.GONE);
                    binding.viewHeader.setVisibility(View.VISIBLE);
                    binding.recVListOder.setVisibility(View.VISIBLE);
                    binding.tvOrderNumber.setText(receipts.size() + "");
                    Double money = 0.0;
                    for (Receipt receipt : receipts) {
                        money += receipt.getMoney();
                    }
                    Locale locale = new Locale("en", "EN");
                    NumberFormat numberFormat = NumberFormat.getInstance(locale);
                    String strMoney = numberFormat.format(money);

                    binding.tvTotalOderValue.setText(strMoney);
                    adapter = new ListOderAdapter((ArrayList<Receipt>) receipts,StatisticalFragment.this,0);
                    LinearLayoutManager layoutManager  = new LinearLayoutManager(getContext());
                    layoutManager.setStackFromEnd(true);
                    layoutManager.setReverseLayout(true);
                    binding.recVListOder.setLayoutManager(layoutManager);
                    binding.recVListOder.setAdapter(adapter);
                }
            }
        });
    }


    private void dialogFunctionPickDate(Context context) {
        final Dialog dialog = new Dialog(context, android.R.style.Theme_Material_Light_NoActionBar);
        DialogChooseTimeStatisticBinding bindingDialog = DialogChooseTimeStatisticBinding.inflate(LayoutInflater.from(context));
        dialog.setContentView(bindingDialog.getRoot());
        Window window = dialog.getWindow();
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        window.setStatusBarColor(getActivity().getColor(R.color.white));
        bindingDialog.icBack.setOnClickListener(ic ->{
            dialog.cancel();
        });

        bindingDialog.layoutDate.setOnClickListener(ic -> {
            bindingDialog.tvChooserTime1.setText("Hôm nay");
            bindingDialog.tvChooserTime2.setText("Hôm qua");
            bindingDialog.tvTime1.setVisibility(View.VISIBLE);
            bindingDialog.tvTime2.setVisibility(View.VISIBLE);
            bindingDialog.icCheckTimeMonth1.setVisibility(View.GONE);
            bindingDialog.icCheckTimeMonth2.setVisibility(View.GONE);
            bindingDialog.icCheckTimeWeek1.setVisibility(View.GONE);
            bindingDialog.icCheckTimeWeek1.setVisibility(View.GONE);
            changeColorIsClick(bindingDialog.tvTitleDate, bindingDialog.lineDate);
            changeColorNotClick(bindingDialog.tvCustomTime, bindingDialog.lineCustomTime);
            changeColorNotClick(bindingDialog.tvTitleWeek, bindingDialog.lineWeek);
            changeColorNotClick(bindingDialog.tvTitleMonth, bindingDialog.lineMonth);

            if (binding.tvFilterTime.getText().equals("Hôm nay")) {
                bindingDialog.icCheckTimeDate1.setVisibility(View.VISIBLE);
                bindingDialog.icCheckTimeDate2.setVisibility(View.GONE);
            } else if (binding.tvFilterTime.getText().equals("Hôm qua")) {
                bindingDialog.icCheckTimeDate1.setVisibility(View.GONE);
                bindingDialog.icCheckTimeDate2.setVisibility(View.VISIBLE);
            }
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            DateFormat dateFormat2 = new SimpleDateFormat("EEEE , yyyy-MM-dd");
            Date toDay = Calendar.getInstance().getTime();
            bindingDialog.tvTime1.setText(dateFormat2.format(toDay));
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DATE, -1);
            bindingDialog.tvTime2.setText(dateFormat2.format(calendar.getTime()));

            String strToday = dateFormat.format(toDay);
            String strYesterday = dateFormat.format(calendar.getTime());

            bindingDialog.layoutChooserTime1.setOnClickListener(v -> {
                binding.tvFilterTime.setText("Hôm nay");
                bindingDialog.icCheckTimeDate1.setVisibility(View.VISIBLE);
                bindingDialog.icCheckTimeDate2.setVisibility(View.GONE);
            });
            bindingDialog.layoutChooserTime2.setOnClickListener(v -> {
                binding.tvFilterTime.setText("Hôm qua");
                bindingDialog.icCheckTimeDate2.setVisibility(View.VISIBLE);
                bindingDialog.icCheckTimeDate1.setVisibility(View.GONE);
            });
            bindingDialog.btnFilter.setOnClickListener(v -> {
                if(bindingDialog.icCheckTimeDate1.getVisibility() == View.GONE && bindingDialog.icCheckTimeDate2.getVisibility() == View.GONE){
                    Toast.makeText(context, "Hãy chọn dữ liệu thống kê", Toast.LENGTH_SHORT).show();
                }else{
                    if (binding.tvFilterFunction.getText().toString().equals("Theo ngày hủy")) {
                        if(binding.tvFilterTime.getText().equals("Hôm nay")){
                            getCancelOderToDate(strToday);
                            adapter.notifyDataSetChanged();
                        }else if(binding.tvFilterTime.getText().equals("Hôm qua")){
                            getCancelOderToDate(strYesterday);
                            adapter.notifyDataSetChanged();
                        }
                    } else {
                        if(binding.tvFilterTime.getText().equals("Hôm nay")){
                            getPaidOderToDate(strToday);
                            adapter.notifyDataSetChanged();
                        }else if(binding.tvFilterTime.getText().equals("Hôm qua")){
                            getPaidOderToDate(strYesterday);
                            adapter.notifyDataSetChanged();
                        }


                    }
                }
                dialog.cancel();
            });

        });

        bindingDialog.layoutWeek.setOnClickListener(ic ->{
            bindingDialog.tvChooserTime1.setText("Tuần này");
            bindingDialog.tvChooserTime2.setText("Tuần trước");
            bindingDialog.tvTime1.setVisibility(View.VISIBLE);
            bindingDialog.tvTime2.setVisibility(View.VISIBLE);
            bindingDialog.icCheckTimeDate1.setVisibility(View.GONE);
            bindingDialog.icCheckTimeDate2.setVisibility(View.GONE);
            bindingDialog.icCheckTimeMonth1.setVisibility(View.GONE);
            bindingDialog.icCheckTimeMonth2.setVisibility(View.GONE);

            changeColorIsClick(bindingDialog.tvTitleWeek,bindingDialog.lineWeek);
            changeColorNotClick(bindingDialog.tvCustomTime,bindingDialog.lineCustomTime);
            changeColorNotClick(bindingDialog.tvTitleDate,bindingDialog.lineDate);
            changeColorNotClick(bindingDialog.tvTitleMonth,bindingDialog.lineMonth);
            if( binding.tvFilterTime.getText().equals("Tuần này")){
                bindingDialog.icCheckTimeWeek1.setVisibility(View.VISIBLE);
                bindingDialog.icCheckTimeWeek2.setVisibility(View.GONE);
            }else if( binding.tvFilterTime.getText().equals("Tuần trước")){
                bindingDialog.icCheckTimeWeek1.setVisibility(View.GONE);
                bindingDialog.icCheckTimeWeek2.setVisibility(View.VISIBLE);
            }
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            //Tuần này
            LocalDate today = LocalDate.now();
            LocalDate monday = today.with(previousOrSame(MONDAY));
            LocalDate sunday = today.with(nextOrSame(SUNDAY));
            bindingDialog.tvTime1.setText(monday+" đến "+sunday);
            //Tuần trước
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(new Date());
            String startOfLastWeek = dateFormat.format(firstDayOfLastWeek(calendar).getTime());
            String endOfLastWeek = dateFormat.format(lastDayOfLastWeek(calendar).getTime());

            bindingDialog.tvTime2.setText(startOfLastWeek+" đến "+endOfLastWeek);


            bindingDialog.layoutChooserTime1.setOnClickListener(v -> {
                binding.tvFilterTime.setText("Tuần này");
                bindingDialog.icCheckTimeWeek1.setVisibility(View.VISIBLE);
                bindingDialog.icCheckTimeWeek2.setVisibility(View.GONE);

            });
            bindingDialog.layoutChooserTime2.setOnClickListener(v -> {
                binding.tvFilterTime.setText("Tuần trước");
                bindingDialog.icCheckTimeWeek2.setVisibility(View.VISIBLE);
                bindingDialog.icCheckTimeWeek1.setVisibility(View.GONE);
            });


            bindingDialog.btnFilter.setOnClickListener(v -> {
                if(bindingDialog.icCheckTimeWeek1.getVisibility() == View.GONE && bindingDialog.icCheckTimeWeek2.getVisibility() == View.GONE){
                    Toast.makeText(context, "Hãy chọn dữ liệu thống kê", Toast.LENGTH_SHORT).show();
                }else{
                    if (binding.tvFilterFunction.getText().toString().equals("Theo ngày hủy")) {
                        if(binding.tvFilterTime.getText().equals("Tuần này")){
                            viewModel.getReceiptCancelByDate(String.valueOf(monday),String.valueOf(sunday));
                            adapter.notifyDataSetChanged();
                        }else if(binding.tvFilterTime.getText().equals("Tuần trước")){
                            viewModel.getReceiptCancelByDate(startOfLastWeek, endOfLastWeek);
                            adapter.notifyDataSetChanged();
                        }
                    } else {
                        if(binding.tvFilterTime.getText().equals("Tuần này")){
                            viewModel.getReceiptByDate(String.valueOf(monday),String.valueOf(sunday));
                            adapter.notifyDataSetChanged();
                        }else if(binding.tvFilterTime.getText().equals("Tuần trước")){
                            viewModel.getReceiptByDate(startOfLastWeek, endOfLastWeek);
                            adapter.notifyDataSetChanged();
                        }

                    }
                }
                dialog.cancel();
            });

        });

        bindingDialog.layoutMonth.setOnClickListener(ic ->{
            bindingDialog.tvChooserTime1.setText("Tháng này");
            bindingDialog.tvChooserTime2.setText("Tháng trước");
            bindingDialog.tvTime1.setVisibility(View.VISIBLE);
            bindingDialog.tvTime2.setVisibility(View.VISIBLE);
            bindingDialog.icCheckTimeDate1.setVisibility(View.GONE);
            bindingDialog.icCheckTimeDate2.setVisibility(View.GONE);
            bindingDialog.icCheckTimeWeek1.setVisibility(View.GONE);
            bindingDialog.icCheckTimeWeek2.setVisibility(View.GONE);

            changeColorIsClick(bindingDialog.tvTitleMonth,bindingDialog.lineMonth);
            changeColorNotClick(bindingDialog.tvCustomTime,bindingDialog.lineCustomTime);
            changeColorNotClick(bindingDialog.tvTitleDate,bindingDialog.lineDate);
            changeColorNotClick(bindingDialog.tvTitleWeek,bindingDialog.lineWeek);
            if( binding.tvFilterTime.getText().equals("Tháng này")){
                bindingDialog.icCheckTimeMonth1.setVisibility(View.VISIBLE);
                bindingDialog.icCheckTimeMonth2.setVisibility(View.GONE);
            }else if( binding.tvFilterTime.getText().equals("Tháng trước")){
                bindingDialog.icCheckTimeMonth1.setVisibility(View.GONE);
                bindingDialog.icCheckTimeMonth2.setVisibility(View.VISIBLE);
            }

            //Tháng này
            LocalDate firstDateThisMonth = YearMonth.now().atDay(1);
            LocalDate lastDateThisMonth   = YearMonth.now().atEndOfMonth();

            bindingDialog.tvTime1.setText(firstDateThisMonth+" đến "+lastDateThisMonth);
            //Tháng trước
            LocalDate firstDateLastMonth = LocalDate.now().minusMonths(1).with(TemporalAdjusters.firstDayOfMonth());
            LocalDate lastDateLastMonth = LocalDate.now().minusMonths(1).with(TemporalAdjusters.lastDayOfMonth());
            bindingDialog.tvTime2.setText(firstDateLastMonth+" đến "+lastDateLastMonth);


            bindingDialog.layoutChooserTime1.setOnClickListener(v -> {
                binding.tvFilterTime.setText("Tháng này");
                bindingDialog.icCheckTimeMonth1.setVisibility(View.VISIBLE);
                bindingDialog.icCheckTimeMonth2.setVisibility(View.GONE);
            });
            bindingDialog.layoutChooserTime2.setOnClickListener(v -> {
                binding.tvFilterTime.setText("Tháng trước");
                bindingDialog.icCheckTimeMonth2.setVisibility(View.VISIBLE);
                bindingDialog.icCheckTimeMonth1.setVisibility(View.GONE);
            });

            bindingDialog.btnFilter.setOnClickListener(v -> {
                if(bindingDialog.icCheckTimeMonth1.getVisibility() == View.GONE && bindingDialog.icCheckTimeMonth2.getVisibility() == View.GONE){
                    Toast.makeText(context, "Hãy chọn dữ liệu thống kê", Toast.LENGTH_SHORT).show();
                }else{
                    if (binding.tvFilterFunction.getText().toString().equals("Theo ngày hủy")) {
                        if(binding.tvFilterTime.getText().equals("Tháng này")){
                            viewModel.getReceiptCancelByDate(String.valueOf(firstDateThisMonth),String.valueOf(lastDateThisMonth));
                            adapter.notifyDataSetChanged();
                        }else if(binding.tvFilterTime.getText().equals("Tháng trước")){
                            viewModel.getReceiptCancelByDate(String.valueOf(firstDateLastMonth),String.valueOf(lastDateLastMonth));
                            adapter.notifyDataSetChanged();
                        }
                    } else {
                        if(binding.tvFilterTime.getText().equals("Tháng này")){
                            viewModel.getReceiptByDate(String.valueOf(firstDateThisMonth),String.valueOf(lastDateThisMonth));
                            adapter.notifyDataSetChanged();
                        }else if(binding.tvFilterTime.getText().equals("Tháng trước")){
                            viewModel.getReceiptByDate(String.valueOf(firstDateLastMonth),String.valueOf(lastDateLastMonth));
                            adapter.notifyDataSetChanged();
                        }


                    }
                }
                dialog.cancel();
            });

        });

        bindingDialog.layoutCustomTime.setOnClickListener(ic ->{
            bindingDialog.tvChooserTime1.setText("Ngày bắt đầu");
            bindingDialog.tvChooserTime2.setText("Ngày kết thúc");
            bindingDialog.tvTime1.setVisibility(View.GONE);
            bindingDialog.tvTime2.setVisibility(View.GONE);
            bindingDialog.layoutCheck1.setVisibility(View.GONE);
            bindingDialog.layoutCheck2.setVisibility(View.GONE);
            changeColorIsClick(bindingDialog.tvCustomTime,bindingDialog.lineCustomTime);
            changeColorNotClick(bindingDialog.tvTitleDate,bindingDialog.lineDate);
            changeColorNotClick(bindingDialog.tvTitleWeek,bindingDialog.lineWeek);
            changeColorNotClick(bindingDialog.tvTitleMonth,bindingDialog.lineMonth);
            chooseArbitraryDate(bindingDialog,dialog,context);
        });

        //Lần đầu

        chooseArbitraryDate(bindingDialog,dialog,context);


        dialog.show();
    }

    private void chooseArbitraryDate(DialogChooseTimeStatisticBinding bindingDialog,Dialog dialog, Context context){
        bindingDialog.layoutChooserTime1.setOnClickListener(v -> {
            DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                    bindingDialog.tvTime1.setText(year + "-" + (monthOfYear + 1) + "-" + dayOfMonth);
                    bindingDialog.tvTime1.setVisibility(View.VISIBLE);
                    lastSelectedYear = year;
                    lastSelectedMonth = monthOfYear;
                    lastSelectedDayOfMonth = dayOfMonth;
                }
            };
            DatePickerDialog datePickerDialog = new DatePickerDialog(requireContext(), dateSetListener, lastSelectedYear, lastSelectedMonth,
                    lastSelectedDayOfMonth);
            datePickerDialog.getDatePicker().setMaxDate(new Date().getTime());
            datePickerDialog.show();
        });

        bindingDialog.layoutChooserTime2.setOnClickListener(tv -> {
            DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {

                @Override
                public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                    bindingDialog.tvTime2.setText(year + "-" + (monthOfYear + 1) + "-" + dayOfMonth);
                    bindingDialog.tvTime2.setVisibility(View.VISIBLE);
                    lastSelectedYear = year;
                    lastSelectedMonth = monthOfYear;
                    lastSelectedDayOfMonth = dayOfMonth;
                }
            };
            DatePickerDialog datePickerDialog = new DatePickerDialog(requireContext(), dateSetListener, lastSelectedYear, lastSelectedMonth,
                    lastSelectedDayOfMonth);
            datePickerDialog.getDatePicker().setMaxDate(new Date().getTime());
            datePickerDialog.show();
        });

        bindingDialog.btnFilter.setOnClickListener(v -> {
            if (bindingDialog.tvTime1.getVisibility() == View.GONE){
                Toast.makeText(context, "Hẫy chọn ngày bắt đầu", Toast.LENGTH_SHORT).show();
            }else if(bindingDialog.tvTime2.getVisibility() == View.GONE){
                Toast.makeText(context, "Hẫy chọn ngày kết thúc", Toast.LENGTH_SHORT).show();
            }else {
                if( binding.tvFilterFunction.getText().toString().equals("Theo ngày hủy")){
                   viewModel.getReceiptCancelByDate(bindingDialog.tvTime1.getText().toString(), bindingDialog.tvTime2.getText().toString());
                    adapter.notifyDataSetChanged();
                }else {
                    viewModel.getReceiptByDate(bindingDialog.tvTime1.getText().toString(), bindingDialog.tvTime2.getText().toString());
                    adapter.notifyDataSetChanged();
                }
                binding.tvFilterTime.setText(bindingDialog.tvTime1.getText().toString()+ " đến " +bindingDialog.tvTime2.getText().toString());
                dialog.cancel();
            }

        });
    }



    public  Calendar firstDayOfLastWeek(Calendar c) {
        c = (Calendar) c.clone();
        c.add(Calendar.WEEK_OF_YEAR, -1);
        c.set(Calendar.DAY_OF_WEEK, c.getFirstDayOfWeek());
        return c;
    }

    public  Calendar lastDayOfLastWeek(Calendar c) {
        c = (Calendar) c.clone();
        c.set(Calendar.DAY_OF_WEEK, c.getFirstDayOfWeek());
        c.add(Calendar.DAY_OF_MONTH, -1);
        return c;
    }

    private void changeColorIsClick(TextView tv , View view){
        tv.setTextColor(getContext().getColor(R.color.brown_200));
        view.setVisibility(View.VISIBLE);
    }

    private void changeColorNotClick(TextView tv , View view){
        tv.setTextColor(getContext().getColor(R.color.black));
        view.setVisibility(View.GONE);
    }



    @Override
    public void onClickListener(Receipt receipt) {
        replaceFragment(new DetailReceiptFragment(receipt));
    }
}