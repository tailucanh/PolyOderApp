package com.example.polyOder.home;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.polyOder.MainActivity;
import com.example.polyOder.Oder.ListOderFragment;
import com.example.polyOder.R;
import com.example.polyOder.base.BaseFragment;
import com.example.polyOder.base.OnclickOptionMenu;
import com.example.polyOder.databinding.FragmentHomeBinding;
import com.example.polyOder.model.Receipt;
import com.example.polyOder.model.Table;
import com.example.polyOder.model.User;
import com.example.polyOder.product.ProductFragment;
import com.example.polyOder.setting.DailySalesReportFragment;
import com.example.polyOder.setting.SettingViewModel;
import com.example.polyOder.table.DetailTableFragment;
import com.example.polyOder.table.adapter.TableAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class HomeFragment extends BaseFragment implements OnclickOptionMenu, TableAdapter.OnItemLongClickListener{
    private FragmentHomeBinding binding;
    private User user;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private SettingViewModel viewModel;
    private TableAdapter adapter = null;
    private FirebaseDatabase database;
    private List<Table> listTable;
    MainActivity mainActivity;


    public HomeFragment() {
        // Required empty public constructor
    }

    public static HomeFragment newInstance() {
        HomeFragment fragment = new HomeFragment();
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
        binding = FragmentHomeBinding.inflate(inflater,container,false);
        viewModel = new ViewModelProvider(this).get(SettingViewModel.class);
        listTable = new ArrayList<>();
        user = new User();
        return binding.getRoot();

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

            firebaseAuth = FirebaseAuth.getInstance();
            firebaseUser = firebaseAuth.getCurrentUser();
            StorageReference reference = FirebaseStorage.getInstance().getReference().child("avatars");
            reference.listAll().addOnSuccessListener(listResult -> {
                for (StorageReference files: listResult.getItems()
                ) {
                    if (files.getName().equals(firebaseUser.getUid())){
                        files.getDownloadUrl().addOnSuccessListener(uri -> {
                            if(getActivity() != null){
                                Glide.with(getActivity()).load(uri).into(binding.icUserSetting);
                            }
                        });
                    }
                }
            });
        mainActivity.showBottomBar();
        listening();
        initObSever();

    }

    @Override
    public void loadData() {
            firebaseUser = firebaseAuth.getCurrentUser();
            String userID = firebaseUser.getUid();
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
            databaseReference.child("users").child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    user = snapshot.getValue(User.class);
                    if (firebaseUser != null && !user.isUserAuthorization()) {
                        binding.tvName.setText(user.getName_user());
                        binding.layoutSlide.setVisibility(View.VISIBLE);
                    }else {
                        binding.tvName.setText(user.getName_user());
                        binding.layoutSlide.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });
            Date toDay = Calendar.getInstance().getTime();
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            String strToday = dateFormat.format(toDay);

            viewModel.getReceiptByToDay(strToday);
            viewModel.getReceiptSavedByToDay(strToday);
            viewModel.getReceiptCancelByToDay(strToday);
            viewModel.liveDateGetSaveReceiptToDay.observe(getViewLifecycleOwner(), new Observer<List<Receipt>>() {
                @Override
                public void onChanged(List<Receipt> receipts) {
                    if(receipts.size() == 0){
                        binding.tvOderNew.setText("0");
                    }else {
                        binding.tvOderNew.setText(receipts.size() + "");
                    }
                }
            });

            viewModel.liveDateGetReceiptToDay.observe(getViewLifecycleOwner(), new Observer<List<Receipt>>() {
                @Override
                public void onChanged(List<Receipt> receipts) {
                    if(receipts.size() == 0){
                        binding.tvBillPaid.setText("0");
                    }else {
                        binding.tvBillPaid.setText(receipts.size() + "");
                        Double money = 0.0;
                        for (Receipt receipt : receipts) {
                            money += receipt.getMoney();
                        }
                        Locale locale = new Locale("en", "EN");
                        NumberFormat numberFormat = NumberFormat.getInstance(locale);
                        String strMoney = numberFormat.format(money);
                        binding.tvTotalMoneyToDay.setText(strMoney);

                    }
                }
            });
            viewModel.liveDateGetCancelReceiptToDay.observe(getViewLifecycleOwner(), new Observer<List<Receipt>>() {
                @Override
                public void onChanged(List<Receipt> receipts) {
                    if(receipts.size() == 0){
                        binding.tvOderCancel.setText("0");
                    }else {
                        binding.tvOderCancel.setText(receipts.size() + "");
                    }
                }
            });

    }

    @Override
    public void listening() {
        binding.icCloseSlide.setOnClickListener(ic ->{
            binding.layoutSlide.setVisibility(View.GONE);

        });
        selectTabFragment();
        binding.tvShowDetailsTurnover.setOnClickListener(tv ->{
            replaceFragment(DailySalesReportFragment.newInstance());
        });

        binding.btnCart.setOnClickListener(btn ->{
            replaceFragment(ListOderFragment.newInstance());
        });

        binding.btnProduct.setOnClickListener(btn ->{
            replaceFragment(ProductFragment.newInstance());
        });
        binding.btnTurnover.setOnClickListener(btn ->{
            replaceFragment(DailySalesReportFragment.newInstance());
        });
        binding.nestedScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(@NonNull NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (scrollY > oldScrollY) {
                    mainActivity.visibilityOfBottom(false);
                }
                if (scrollY < oldScrollY) {
                    mainActivity.visibilityOfBottom(true);
                }

            }
        });


    }

    @Override
    public void initObSever() {


    }

    @Override
    public void initView() {
        binding.tvTitleAll.setBackgroundColor(getContext().getColor(R.color.red_100));
        getAllTable();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mainActivity = (MainActivity)context;
    }


    private void selectTabFragment(){
        binding.btnAllTable.setOnClickListener(btn ->{
            changeBgColorTextView(binding.tvTitleAll,getContext().getColor(R.color.red_100));
            changeBgColorTextView(binding.tvTitleEmpty,getContext().getColor(R.color.grey_55));
            changeBgColorTextView(binding.tvTitleOpen,getContext().getColor(R.color.grey_55));
            getAllTable();
        });
        binding.btnTableEmpty.setOnClickListener(btn ->{
            changeBgColorTextView(binding.tvTitleAll,getContext().getColor(R.color.grey_55));
            changeBgColorTextView(binding.tvTitleEmpty,getContext().getColor(R.color.red_100));
            changeBgColorTextView(binding.tvTitleOpen,getContext().getColor(R.color.grey_55));
            getTable("false");
        });

        binding.btnTableOpen.setOnClickListener(btn ->{
            changeBgColorTextView(binding.tvTitleAll,getContext().getColor(R.color.grey_55));
            changeBgColorTextView(binding.tvTitleEmpty,getContext().getColor(R.color.grey_55));
            changeBgColorTextView(binding.tvTitleOpen,getContext().getColor(R.color.red_100));
            getTable("true");

        });
    }


    private void changeBgColorTextView( TextView tv ,int idColor){
        tv.setBackgroundColor(idColor);
    }

    private void getTable(String statusTable){
        database = FirebaseDatabase.getInstance();
        DatabaseReference reference = database.getReference("tables");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listTable.clear();
                for (DataSnapshot snapshot1: snapshot.getChildren()) {
                    Table table = snapshot1.getValue(Table.class);
                    if(table.isHidden() && table.getStatus().equals(statusTable)){
                        listTable.add(table);
                    }

                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        adapter = new TableAdapter(listTable, HomeFragment.this,HomeFragment.this,getContext());
        binding.revListTable.setAdapter(adapter);

    }
    private void getAllTable(){
        database = FirebaseDatabase.getInstance();
        DatabaseReference reference = database.getReference("tables");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listTable.clear();
                for (DataSnapshot snapshot1: snapshot.getChildren()) {
                    Table table = snapshot1.getValue(Table.class);
                    if(table.isHidden()){
                        listTable.add(table);
                    }

                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        adapter = new TableAdapter(listTable, HomeFragment.this,HomeFragment.this,getContext());
        binding.revListTable.setAdapter(adapter);

    }




    @Override
    public void onClick(Table table) {
        replaceFragment(DetailTableFragment.newInstance(table));
    }


    @Override
    public void onLongClickTable(Table table) {
        notificationErrInput(getContext(),"Không thể sửa bàn ở trang này.Hãy vào danh sách bàn.");

    }
}