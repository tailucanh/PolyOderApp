package com.example.polyOder.setting.sales;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.polyOder.MainActivity;
import com.example.polyOder.R;
import com.example.polyOder.base.BaseFragment;
import com.example.polyOder.base.Helpers;
import com.example.polyOder.databinding.DialogShowBarcharClickBinding;
import com.example.polyOder.databinding.FragmentFinancialOverviewBinding;
import com.example.polyOder.model.DataBarChart;
import com.example.polyOder.model.Receipt;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Year;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class FinanceOverviewFragment extends BaseFragment {

    private FragmentFinancialOverviewBinding binding = null;
    private double totalMoney;
    private BarData barData;
    private BarDataSet barDataSet;
    private BarChart barChartTurnover;
    private ArrayList<BarEntry> barEntriesArrayList;
    private ArrayList<DataBarChart> listsData;
    private ArrayList<String> listsLabelName;
    private PopupMenu popupMenu;
    private Helpers helpers = new Helpers();

    public FinanceOverviewFragment() {
        // Required empty public constructor
    }

    public static FinanceOverviewFragment newInstance() {
        FinanceOverviewFragment fragment = new FinanceOverviewFragment();
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
        binding = FragmentFinancialOverviewBinding.inflate(inflater, container, false);
        barChartTurnover = binding.barCharTurnover;
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setColorStatusBar(getActivity().getColor(R.color.white));
        hideBottomBar();
        getMoneyOfCurrentYear(Year.now().getValue());
        listening();
        initObSever();
    }

    @Override
    public void loadData() {
        int year = Year.now().getValue();
        binding.tvFilterTime.setText("Năm "+year);

    }

    @Override
    public void listening() {
        binding.icBack.setOnClickListener(ic ->{
            backStack();
        });
        binding.layoutTime.setOnClickListener(ic ->{
            binding.icArrow.animate().rotation(0).start();
            popupMenu   = new PopupMenu(getContext(),binding.layoutTime);
            popupMenu.inflate(R.menu.menu_popup_finance_overview);
            Menu menu = popupMenu.getMenu();
            int year = Year.now().getValue();
            menu.getItem(0).setTitle("Năm "+year);
            menu.getItem(1).setTitle("Năm "+getPreviousYear());
            menu.getItem(2).setTitle("Năm "+getTwoYearAgo());
            menu.getItem(3).setTitle("Năm "+getThreeYearAgo());
            popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    return menuItemClicked(item);
                }
            });
            popupMenu.setOnDismissListener(new PopupMenu.OnDismissListener() {
                @Override
                public void onDismiss(PopupMenu popupMenu) {
                    binding.icArrow.animate().rotation(180).start();
                }
            });

            popupMenu.show();
        });



    }

    @Override
    public void initObSever() {
    }

    @Override
    public void initView() {
    }


    private void getMoneyOfCurrentYear(int year){
        YearMonth january = YearMonth.of(year, 1 );
        LocalDate firstOfJanuary = january.atDay( 1);
        LocalDate lastOfJanuary = january.atEndOfMonth();

        YearMonth february = YearMonth.of(year, 2 );
        LocalDate firstOfFebruary = february.atDay( 1);
        LocalDate lastOfFebruary = february.atEndOfMonth();

        YearMonth march = YearMonth.of(year, 3 );
        LocalDate firstOfMarch = march.atDay( 1);
        LocalDate lastOfMarch = march.atEndOfMonth();


        YearMonth april = YearMonth.of(year, 4 );
        LocalDate firstOfApril = april.atDay( 1);
        LocalDate lastOfApril = april.atEndOfMonth();

        YearMonth may = YearMonth.of(year, 5 );
        LocalDate firstOfMay = may.atDay( 1);
        LocalDate lastOfMay = may.atEndOfMonth();

        YearMonth june = YearMonth.of(year, 6 );
        LocalDate firstOfJune = june.atDay( 1);
        LocalDate lastOfJune = june.atEndOfMonth();

        YearMonth july = YearMonth.of(year, 7 );
        LocalDate firstOfJuly = july.atDay( 1);
        LocalDate lastOfJuly = july.atEndOfMonth();

        YearMonth august = YearMonth.of(year, 8 );
        LocalDate firstOfAugust = august.atDay( 1);
        LocalDate lastOfAugust = august.atEndOfMonth();

        YearMonth september = YearMonth.of(year, 9 );
        LocalDate firstOfSeptember = september.atDay( 1);
        LocalDate lastOfSeptember = september.atEndOfMonth();

        YearMonth october = YearMonth.of(year, 10 );
        LocalDate firstOfOctober = october.atDay( 1);
        LocalDate lastOfOctober = october.atEndOfMonth();

        YearMonth november = YearMonth.of(year, 11 );
        LocalDate firstOfNovember = november.atDay( 1);
        LocalDate lastOfNovember = november.atEndOfMonth();

        YearMonth december = YearMonth.of(year, 12 );
        LocalDate firstOfDecember = december.atDay( 1);
        LocalDate lastOfDecember = december.atEndOfMonth();


        getDataFromFirebase("PayReceipt",new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                barEntriesArrayList = new ArrayList<>();
                listsData = new ArrayList<>();
                listsLabelName = new ArrayList<>();
                float moneyJanuary = 0;
                float moneyFebruary = 0;
                float moneyMarch= 0;
                float moneyApril = 0;
                float moneyMay = 0;
                float moneyJune = 0;
                float moneyJuly = 0;
                float moneyAugust= 0;
                float moneySeptember = 0;
                float moneyOctober = 0;
                float moneyNovember = 0;
                float moneyDecember = 0;
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Receipt receipt = dataSnapshot.getValue(Receipt.class);
                    String day = receipt.getTimeOder().substring(0, receipt.getTimeOder().lastIndexOf(" "));
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    try {
                        Date dayOder = sdf.parse(day);
                        Date startDay1 = sdf.parse(String.valueOf(firstOfJanuary));
                        Date endDay1 = sdf.parse(String.valueOf(lastOfJanuary));

                        if (startDay1.getTime() <= dayOder.getTime() && endDay1.getTime() >= dayOder.getTime()) {
                            moneyJanuary += receipt.getMoney();
                        }
                        Date startDay2 = sdf.parse(String.valueOf(firstOfFebruary));
                        Date endDay2 = sdf.parse(String.valueOf(lastOfFebruary));

                        if (startDay2.getTime() <= dayOder.getTime() && endDay2.getTime() >= dayOder.getTime()) {
                            moneyFebruary += receipt.getMoney();
                        }
                        Date startDay3 = sdf.parse(String.valueOf(firstOfMarch));
                        Date endDay3 = sdf.parse(String.valueOf(lastOfMarch));

                        if (startDay3.getTime() <= dayOder.getTime() && endDay3.getTime() >= dayOder.getTime()) {
                            moneyMarch += receipt.getMoney();
                        }
                        Date startDay4 = sdf.parse(String.valueOf(firstOfApril));
                        Date endDay4 = sdf.parse(String.valueOf(lastOfApril));

                        if (startDay4.getTime() <= dayOder.getTime() && endDay4.getTime() >= dayOder.getTime()) {
                            moneyApril += receipt.getMoney();
                        }
                        Date startDay5 = sdf.parse(String.valueOf(firstOfMay));
                        Date endDay5 = sdf.parse(String.valueOf(lastOfMay));

                        if (startDay5.getTime() <= dayOder.getTime() && endDay5.getTime() >= dayOder.getTime()) {
                            moneyMay += receipt.getMoney();
                        }
                        Date startDay6 = sdf.parse(String.valueOf(firstOfJune));
                        Date endDay6 = sdf.parse(String.valueOf(lastOfJune));

                        if (startDay6.getTime() <= dayOder.getTime() && endDay6.getTime() >= dayOder.getTime()) {
                            moneyJune += receipt.getMoney();
                        }
                        Date startDay7 = sdf.parse(String.valueOf(firstOfJuly));
                        Date endDay7 = sdf.parse(String.valueOf(lastOfJuly));

                        if (startDay7.getTime() <= dayOder.getTime() && endDay7.getTime() >= dayOder.getTime()) {
                            moneyJuly += receipt.getMoney();
                        }
                        Date startDay8 = sdf.parse(String.valueOf(firstOfAugust));
                        Date endDay8 = sdf.parse(String.valueOf(lastOfAugust));

                        if (startDay8.getTime() <= dayOder.getTime() && endDay8.getTime() >= dayOder.getTime()) {
                            moneyAugust += receipt.getMoney();
                        }
                        Date startDay9 = sdf.parse(String.valueOf(firstOfSeptember));
                        Date endDay9 = sdf.parse(String.valueOf(lastOfSeptember));

                        if (startDay9.getTime() <= dayOder.getTime() && endDay9.getTime() >= dayOder.getTime()) {
                            moneySeptember += receipt.getMoney();
                        }
                        Date startDay10 = sdf.parse(String.valueOf(firstOfOctober));
                        Date endDay10 = sdf.parse(String.valueOf(lastOfOctober));

                        if (startDay10.getTime() <= dayOder.getTime() && endDay10.getTime() >= dayOder.getTime()) {
                            moneyOctober += receipt.getMoney();
                        }
                        Date startDay11 = sdf.parse(String.valueOf(firstOfNovember));
                        Date endDay11 = sdf.parse(String.valueOf(lastOfNovember));

                        if (startDay11.getTime() <= dayOder.getTime() && endDay11.getTime() >= dayOder.getTime()) {
                            moneyNovember += receipt.getMoney();
                        }
                        Date startDay12 = sdf.parse(String.valueOf(firstOfDecember));
                        Date endDay12 = sdf.parse(String.valueOf(lastOfDecember));

                        if (startDay12.getTime() <= dayOder.getTime() && endDay12.getTime() >= dayOder.getTime()) {
                            moneyDecember += receipt.getMoney();
                        }


                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }

                totalMoney = moneyJanuary+moneyFebruary+moneyMarch+moneyApril+moneyMay+moneyJune+moneyJuly+moneyAugust+moneySeptember+moneyOctober+moneyNovember+moneyDecember;
                String strMoney = helpers.isFormatMoney(totalMoney);

                binding.tvProfit.setText(strMoney);

                listsData.add(new DataBarChart("Jan",  moneyJanuary));
                listsData.add(new DataBarChart("Feb",  moneyFebruary));
                listsData.add(new DataBarChart("Mar",moneyMarch));
                listsData.add(new DataBarChart("Apr",moneyApril ));
                listsData.add(new DataBarChart("May", moneyMay));
                listsData.add(new DataBarChart("Jun",moneyJune));
                listsData.add(new DataBarChart("Jul",moneyJuly));
                listsData.add(new DataBarChart("Aug",moneyAugust));
                listsData.add(new DataBarChart("Sep", moneySeptember));
                listsData.add(new DataBarChart("Oct", moneyOctober));
                listsData.add(new DataBarChart("Nov",moneyNovember));
                listsData.add(new DataBarChart("Dec",moneyDecember));
                for(int i = 0 ; i < listsData.size(); i ++){
                    String months = listsData.get(i).getMonths();
                    float totalMoney = listsData.get(i).getTotalMoney();
                    barEntriesArrayList.add(new BarEntry(i,totalMoney));
                    listsLabelName.add(months);

                }

                barDataSet = new BarDataSet(barEntriesArrayList, "Sơ đồ tổng quan");
                barData = new BarData(barDataSet);
                barChartTurnover.setData(barData);

                XAxis xAxis = barChartTurnover.getXAxis();
                xAxis.setValueFormatter(new IndexAxisValueFormatter(listsLabelName));
                xAxis.setPosition(XAxis.XAxisPosition.TOP);
                xAxis.setLabelCount(listsLabelName.size());

                barDataSet.setColors(ColorTemplate.MATERIAL_COLORS);
                barDataSet.setValueTextColor(Color.BLACK);
                barDataSet.setValueTextSize(10f);
                barChartTurnover.getAxisRight().setDrawLabels(false);
                barChartTurnover.getDescription().setEnabled(true);
                barChartTurnover.setTouchEnabled(true);
                barChartTurnover.setDragEnabled(true);
                barChartTurnover.setDoubleTapToZoomEnabled(false);
                barChartTurnover.setScaleEnabled(true);
                barChartTurnover.getDescription().setText("Poly Revenue");
                barChartTurnover.invalidate();
                barChartTurnover.animateY(2000);


                barChartTurnover.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
                    @Override
                    public void onValueSelected(Entry e, Highlight h) {
                        final Dialog dialog = new Dialog(getContext());
                        DialogShowBarcharClickBinding bindingDialog = DialogShowBarcharClickBinding.inflate(LayoutInflater.from(getContext()));
                        dialog.setContentView(bindingDialog.getRoot());
                        helpers.setLayoutDialog(dialog, 0,WindowManager.LayoutParams.WRAP_CONTENT);

                        int index = barChartTurnover.getBarData().getDataSetForEntry(e).getEntryIndex((BarEntry) e);
                        String month = listsData.get(index).getMonths();
                        float moneys = listsData.get(index).getTotalMoney();
                        String strMonthFormat = "";
                        if(month.equals("Jan")){
                            strMonthFormat = "tháng 1";
                        }else if(month.equals("Feb")){
                            strMonthFormat = "tháng 2";
                        }else if(month.equals("Mar")){
                            strMonthFormat = "tháng 3";
                        }else if(month.equals("Apr")){
                            strMonthFormat = "tháng 4";
                        }else if(month.equals("May")){
                            strMonthFormat = "tháng 5";
                        }else if(month.equals("Jun")){
                            strMonthFormat = "tháng 6";
                        }else if(month.equals("Jul")){
                            strMonthFormat = "tháng 7";
                        }else if(month.equals("Aug")){
                            strMonthFormat = "tháng 8";
                        }else if(month.equals("Sep")){
                            strMonthFormat = "tháng 9";
                        }else if(month.equals("Oct")){
                            strMonthFormat = "tháng 10";
                        }else if(month.equals("Nov")){
                            strMonthFormat = "tháng 11";
                        }else if(month.equals("Dec")){
                            strMonthFormat = "tháng 12";
                        }


                        String strMoney = helpers.isFormatMoney((double) moneys);

                        bindingDialog.tvTitle.setText("Doanh thu "+strMonthFormat);
                        bindingDialog.tvMoney.setText(strMoney+"đ");


                        dialog.show();
                    }

                    @Override
                    public void onNothingSelected() {

                    }
                });

            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

    }
    private  int getPreviousYear() {
        Calendar prevYear = Calendar.getInstance();
        prevYear.add(Calendar.YEAR, -1);
        return prevYear.get(Calendar.YEAR);
    }
    private  int getTwoYearAgo() {
        Calendar prevYear = Calendar.getInstance();
        prevYear.add(Calendar.YEAR, -2);
        return prevYear.get(Calendar.YEAR);
    }
    private  int getThreeYearAgo() {
        Calendar prevYear = Calendar.getInstance();
        prevYear.add(Calendar.YEAR, -3);
        return prevYear.get(Calendar.YEAR);
    }
    private boolean menuItemClicked(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.tvYear:
                int year = Year.now().getValue();
                binding.tvFilterTime.setText("Năm "+year);
                getMoneyOfCurrentYear(year);
                break;
            case R.id.tvPreviousYear:
                binding.tvFilterTime.setText("Năm "+getPreviousYear());
                getMoneyOfCurrentYear(getPreviousYear());
                break;
            case R.id.tvTwoYearAgo:
                binding.tvFilterTime.setText("Năm "+getTwoYearAgo());
                getMoneyOfCurrentYear(getTwoYearAgo());
                break;
            case R.id.tvThreeYearAgo:
                binding.tvFilterTime.setText("Năm "+getThreeYearAgo());
                getMoneyOfCurrentYear(getThreeYearAgo());
                break;
            default:
                Toast.makeText(getContext(), item.getTitle(), Toast.LENGTH_SHORT).show();
                break;
        }

        return  true;
    }

}