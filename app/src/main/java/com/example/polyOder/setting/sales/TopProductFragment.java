package com.example.polyOder.setting.sales;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.polyOder.MainActivity;
import com.example.polyOder.R;
import com.example.polyOder.base.BaseFragment;
import com.example.polyOder.base.Helpers;
import com.example.polyOder.databinding.FragmentSaveToOderBinding;
import com.example.polyOder.databinding.FragmentTopProductBinding;
import com.example.polyOder.home.adapter.OderAdapter;
import com.example.polyOder.home.adapter.TableAdapter;
import com.example.polyOder.interfaces.OnTouchTheProduct;
import com.example.polyOder.model.Product;
import com.example.polyOder.model.Receipt;
import com.example.polyOder.model.Table;
import com.example.polyOder.model.User;
import com.example.polyOder.product.DetailProductFragment;
import com.example.polyOder.setting.sales.adapter.TopProductAdapter;
import com.example.polyOder.viewModel.ReceiptViewModel;
import com.example.polyOder.viewModel.TableViewModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.stream.Collectors;


public class TopProductFragment extends BaseFragment   implements OnTouchTheProduct {
    private FragmentTopProductBinding binding;
    private User user;
    private  ArrayList<String> listIdProduct;
    private  ArrayList<String> listIdMonthProduct;
    private ReceiptViewModel receiptViewModel;
    private TableViewModel tableViewModel;
    private LifecycleOwner owner;
    private Helpers helpers = new Helpers();

    public TopProductFragment() {

    }

    public static TopProductFragment newInstance() {
        TopProductFragment fragment = new TopProductFragment();
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
        binding = FragmentTopProductBinding.inflate(inflater,container,false);
        receiptViewModel = new ViewModelProvider(this).get(ReceiptViewModel.class);
        tableViewModel = new ViewModelProvider(this).get(TableViewModel.class);
        owner = getViewLifecycleOwner();
        return binding.getRoot();

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        hideBottomBar();
        setColorStatusBar(getContext().getColor(R.color.brown_95));

        listIdProduct = new ArrayList<>();
        listIdMonthProduct = new ArrayList<>();
        getImageObjFromStorageReference("avatars", FirebaseAuth.getInstance().getCurrentUser().getUid(),binding.icUserSetting);

        listening();
        initObSever();
    }

    @Override
    public void loadData() {

       getDataFirebaseUser("users",new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                user = snapshot.getValue(User.class);
                binding.tvName.setText(user.getName_user());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });


        getDataFromFirebase("PayReceipt",new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listIdProduct.clear();
                for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                    Receipt receipt = dataSnapshot.getValue(Receipt.class);
                    for (int i = 0; i < receipt.getListIdProduct().size(); i++) {
                        String str = receipt.getListIdProduct().get(i);
                        int multiplier = receipt.getListCountProduct().get(i);
                        for (int j = 0; j < multiplier; j++) {
                            listIdProduct.add(str);
                        }
                    }

                }
                ArrayList<String>  listIdTopProduct = getTopProduct(listIdProduct);

                if(!(helpers.isEmptyList(listIdTopProduct))){
                    tableViewModel.listLiveTop(listIdTopProduct);
                    tableViewModel.listProductTop.observe(owner, new Observer<List<Product>>() {
                        @Override
                        public void onChanged(List<Product> products) {
                            if(!products.isEmpty()){
                                TopProductAdapter topProductAdapter = new TopProductAdapter((ArrayList<Product>) products, TopProductFragment.this,1, getContext());
                                binding.relListTop.setAdapter(topProductAdapter);

                                binding.searchViewTopProduct.clearFocus();
                                binding.searchViewTopProduct.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                                    @Override
                                    public boolean onQueryTextSubmit(String query) {
                                        return false;
                                    }

                                    @Override
                                    public boolean onQueryTextChange(String newText) {
                                        filterList(newText, (ArrayList<Product>) products,topProductAdapter,0);
                                        return true;
                                    }
                                });
                            }

                        }
                    });
                    binding.notifiNullDataTop.setVisibility(View.GONE);
                }else {
                    binding.notifiNullDataTop.setVisibility(View.VISIBLE);
                    binding.tvNullTop.setText("Chưa có dữ liệu.");
                }

            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        LocalDate firstDateThisMonth = YearMonth.now().atDay(1);
        LocalDate lastDateThisMonth   = YearMonth.now().atEndOfMonth();
        receiptViewModel.getReceiptByDate(String.valueOf(firstDateThisMonth),String.valueOf(lastDateThisMonth));
        receiptViewModel.liveDateGetReceipt.observe(owner, new Observer<List<Receipt>>() {
            @Override
            public void onChanged(List<Receipt> receipts) {
                if(!receipts.isEmpty()){
                    binding.notifiNullDataTopMonth.setVisibility(View.GONE);
                    listIdMonthProduct.clear();
                    for(Receipt receipt : receipts){
                        for (int i = 0; i < receipt.getListIdProduct().size(); i++) {
                            String str = receipt.getListIdProduct().get(i);
                            int multiplier = receipt.getListCountProduct().get(i);
                            for (int j = 0; j < multiplier; j++) {
                                listIdMonthProduct.add(str);
                            }
                        }

                    }
                    ArrayList<String>  listIdTopMonthProduct = getTopProduct(listIdMonthProduct);

                    if(!(helpers.isEmptyList(listIdTopMonthProduct))){
                        tableViewModel.listLiveTopMonth(listIdTopMonthProduct);
                        tableViewModel.listProductTopMonth.observe(owner, new Observer<List<Product>>() {
                            @Override
                            public void onChanged(List<Product> products) {
                                if(!products.isEmpty()){
                                    TopProductAdapter topProductAdapter = new TopProductAdapter((ArrayList<Product>) products, TopProductFragment.this,0, getContext());
                                    binding.relListTopMonth.setAdapter(topProductAdapter);
                                    binding.searchViewTopMonth.clearFocus();
                                    binding.searchViewTopMonth.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                                        @Override
                                        public boolean onQueryTextSubmit(String query) {
                                            return false;
                                        }

                                        @Override
                                        public boolean onQueryTextChange(String newText) {
                                            filterList(newText, (ArrayList<Product>) products,topProductAdapter,1);
                                            return true;
                                        }
                                    });

                                }

                            }
                        });

                    }
                    binding.notifiNullDataTopMonth.setVisibility(View.GONE);
                }else {
                    binding.notifiNullDataTopMonth.setVisibility(View.VISIBLE);
                    binding.tvNullTopMonth.setText("Chưa có dữ liệu.");
                }
            }
        });


    }

    @Override
    public void listening() {
        binding.icShowSearch.setOnClickListener(ic ->{
            showSearchView(binding.searchViewTopMonth);
            binding.icHideSearch.setVisibility(View.VISIBLE);
            binding.icShowSearch.setVisibility(View.GONE);
            binding.tvContentTopMonth.setVisibility(View.GONE);
        });
        binding.icHideSearch.setOnClickListener(ic ->{
            hideSearchView(binding.searchViewTopMonth);
            binding.icShowSearch.setVisibility(View.VISIBLE);
            binding.tvContentTopMonth.setVisibility(View.VISIBLE);
            binding.icHideSearch.setVisibility(View.GONE);
        });

    }



    @Override
    public void initObSever() {
        LocalDate today = LocalDate.now();
        binding.tvContentTopMonth.setText("Top tháng  "+today.getMonthValue());
    }

    @Override
    public void initView() {

    }

    private  ArrayList<String> getTopProduct(ArrayList<String> listIdProduct){
        ArrayList<String> listIdTop = new ArrayList<>();
        TreeMap<String, Integer> countMap = new TreeMap<>();
        for (String item: listIdProduct) {
            if (countMap.containsKey(item))
                countMap.put(item, countMap.get(item) + 1);
            else
                countMap.put(item, 1);
        }

        Comparator<Map.Entry<String, Integer>> descendingComparator = (e1, e2) -> e2.getValue().compareTo(e1.getValue());
        List<Map.Entry<String, Integer>> sortedList = countMap.entrySet().stream()
                .sorted(descendingComparator)
                .collect(Collectors.toList());

        ArrayList<String> temps = new ArrayList<>();
        for(Map.Entry<String,Integer> entry : sortedList) {
            temps.add(entry.getKey());
        }

        for (int i = 0 ; i < temps.size() ; i ++){
            if(i < 10){
                listIdTop.add(temps.get(i));
            }
        }
        return listIdTop;
    }



    private void filterList(String text, ArrayList<Product> listProduct, TopProductAdapter topProductAdapter, int isFilterData) {
        ArrayList<Product> filterLists = new ArrayList<>();
        for (Product product : listProduct) {
            if (product.getNameProduct().toLowerCase().contains(text.toLowerCase()) || String.valueOf(product.getPrice()).contains(text)
            || product.getDescribe().toLowerCase().contains(text.toLowerCase()) ) {
                filterLists.add(product);
            }
        }
        if (!helpers.isEmptyList(filterLists) && isFilterData ==0) {
            topProductAdapter.setFilterList(filterLists);
            topProductAdapter.notifyDataSetChanged();
            binding.notifiNullDataTop.setVisibility(View.GONE);
            binding.relListTop.setVisibility(View.VISIBLE);
        }else {
            binding.notifiNullDataTop.setVisibility(View.VISIBLE);
            binding.relListTop.setVisibility(View.GONE);
            binding.tvNullTop.setText("Không tìm thấy sản phẩm "+"\""+text+"\"");
        }
        if (!helpers.isEmptyList(filterLists) && isFilterData ==1) {
            topProductAdapter.setFilterList(filterLists);
            topProductAdapter.notifyDataSetChanged();
            binding.notifiNullDataTopMonth.setVisibility(View.GONE);
            binding.relListTopMonth.setVisibility(View.VISIBLE);
        }else {
            binding.notifiNullDataTopMonth.setVisibility(View.VISIBLE);
            binding.relListTopMonth.setVisibility(View.GONE);
            binding.tvNullTop.setText("Không tìm thấy sản phẩm "+"\""+text+"\"");
        }



    }
    private void showSearchView(SearchView searchView) {
        searchView.setVisibility(View.VISIBLE);
        searchView.setAlpha(0f);
        searchView.setTranslationX(100);
        searchView.animate().alpha(1f).translationXBy(-100).setDuration(1000);
    }
    private void hideSearchView(SearchView searchView) {
        searchView.setAlpha(1f);
        searchView.setTranslationX(0);
        searchView.animate().alpha(0f).translationXBy(100).setDuration(1000);
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                searchView.setVisibility(View.GONE);
            }
        },1200);

    }

    @Override
    public void onClickProduct(Product product) {
        replaceFragment(new DetailProductFragment(product));
    }
}