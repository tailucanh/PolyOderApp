package com.example.polyOder.home;

import android.content.Context;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.polyOder.MainActivity;
import com.example.polyOder.base.Helpers;

import com.example.polyOder.carts.oders.ListOderFragment;
import com.example.polyOder.R;
import com.example.polyOder.base.BaseFragment;
import com.example.polyOder.chatPoly.messageChat.ChatsFragment;
import com.example.polyOder.interfaces.IOnBackPressed;
import com.example.polyOder.interfaces.OnTouchTheTable;
import com.example.polyOder.databinding.FragmentHomeBinding;
import com.example.polyOder.model.Receipt;
import com.example.polyOder.model.Table;
import com.example.polyOder.model.User;
import com.example.polyOder.product.ProductFragment;
import com.example.polyOder.setting.sales.DailySalesReportFragment;
import com.example.polyOder.viewModel.ReceiptViewModel;
import com.example.polyOder.home.table.DetailTableFragment;
import com.example.polyOder.home.adapter.TableAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class HomeFragment extends BaseFragment implements OnTouchTheTable {
    private FragmentHomeBinding binding;
    private ReceiptViewModel viewModel;
    private TableAdapter adapter = null;
    private List<Table> listTable;
    private Helpers helpers = new Helpers();

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
        viewModel = new ViewModelProvider(this).get(ReceiptViewModel.class);
        return binding.getRoot();

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        listTable = new ArrayList<>();

        showBottomBar();
        listening();
        initObSever();

    }

    @Override
    public void loadData() {
        getImageObjFromStorageReference("avatars", FirebaseAuth.getInstance().getCurrentUser().getUid(),binding.icUserSetting);
        getDataFirebaseUser("users",new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    User user = snapshot.getValue(User.class);
                     binding.tvName.setText(user.getName_user());
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });
            getAllTable();
            String strToday = helpers.isFormatTime(Calendar.getInstance().getTime(), "yyyy-MM-dd") ;
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
                        binding.tvTotalMoneyToDay.setText(helpers.isFormatMoney(money));

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
        binding.btnChat.setOnClickListener(ic ->{
            replaceFragment(ChatsFragment.newInstance());
        });

       visibleBottomBarOnScroll(binding.revListTable, binding.nestedScrollView);


    }

    @Override
    public void initObSever() {

    }

    @Override
    public void initView() {

    }


    private void selectTabFragment(){
        binding.btnAllTable.setOnClickListener(btn ->{
            onSelectTabButton(binding.tvTitleAll, binding.icDot1, View.VISIBLE, R.dimen.dimen_18dp,R.color.brown_300 );
            onSelectTabButton(binding.tvTitleEmpty, binding.icDot2, View.GONE, R.dimen.dimen_16dp,R.color.grey_300 );
            onSelectTabButton(binding.tvTitleOpen, binding.icDot3, View.GONE, R.dimen.dimen_16dp,R.color.grey_300 );

            getAllTable();
        });
        binding.btnTableEmpty.setOnClickListener(btn ->{
            onSelectTabButton(binding.tvTitleAll, binding.icDot1, View.GONE, R.dimen.dimen_16dp,R.color.grey_300 );
            onSelectTabButton(binding.tvTitleEmpty, binding.icDot2, View.VISIBLE, R.dimen.dimen_18dp,R.color.brown_300 );
            onSelectTabButton(binding.tvTitleOpen, binding.icDot3, View.GONE, R.dimen.dimen_16dp,R.color.grey_300 );

            getTable("false");
        });

        binding.btnTableOpen.setOnClickListener(btn ->{
            onSelectTabButton(binding.tvTitleAll, binding.icDot1, View.GONE, R.dimen.dimen_16dp,R.color.grey_300 );
            onSelectTabButton(binding.tvTitleEmpty, binding.icDot2, View.GONE, R.dimen.dimen_16dp,R.color.grey_300 );
            onSelectTabButton(binding.tvTitleOpen, binding.icDot3, View.VISIBLE, R.dimen.dimen_18dp,R.color.brown_300 );

            getTable("true");

        });
    }

    private void onSelectTabButton(TextView textView, ImageView imageView, int visible,  int textSize, int color ){
        setColorTextView(textView,color, textSize);
        imageView.setVisibility(visible);
    }


    private void setColorTextView( TextView tv ,int idColor, int dimen){
        tv.setTextColor(getContext().getColor(idColor));
        tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(dimen));
    }

    private void getTable(String statusTable){
        getDataFromFirebase("tables",new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listTable.clear();
                for (DataSnapshot snapshot1: snapshot.getChildren()) {
                    Table table = snapshot1.getValue(Table.class);
                    if(table.isHidden() && table.getStatus().equals(statusTable)){
                        listTable.add(table);
                    }

                }
                if(listTable.isEmpty() && statusTable.equals("false")){
                    binding.revListTable.setVisibility(View.GONE);
                    binding.layoutNotificationNullData.setVisibility(View.VISIBLE);
                    binding.tvNotifiNull.setText("Bàn trống đã hết.Hãy tạo thêm bàn.");
                }else if(listTable.isEmpty() && statusTable.equals("true")){
                    binding.revListTable.setVisibility(View.GONE);
                    binding.layoutNotificationNullData.setVisibility(View.VISIBLE);
                    binding.tvNotifiNull.setText("Không có bàn nào được sử dụng.");
                }else {
                    binding.revListTable.setVisibility(View.VISIBLE);
                    binding.layoutNotificationNullData.setVisibility(View.GONE);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        adapter = new TableAdapter(listTable, HomeFragment.this,getContext());
        binding.revListTable.setAdapter(adapter);
    }


    private void getAllTable(){
        getDataFromFirebase("tables",new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listTable.clear();
                for (DataSnapshot snapshot1: snapshot.getChildren()) {
                    Table table = snapshot1.getValue(Table.class);
                    if(table.isHidden()){
                        listTable.add(table);
                    }

                }

                if(!listTable.isEmpty()){
                    binding.revListTable.setVisibility(View.VISIBLE);
                    binding.layoutNotificationNullData.setVisibility(View.GONE);
                }else{
                    binding.revListTable.setVisibility(View.GONE);
                    binding.layoutNotificationNullData.setVisibility(View.VISIBLE);
                    binding.tvNotifiNull.setText("Chưa có bàn được tạo.");
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        adapter = new TableAdapter(listTable, HomeFragment.this,getContext());
        binding.revListTable.setAdapter(adapter);

    }


    @Override
    public void onClickTable(Table table) {
        replaceFragment(DetailTableFragment.newInstance(table));
    }


    @Override
    public void onLongClickTable(Table table) {
        helpers.notificationErrInput(getContext(),"Không thể sửa bàn ở trang này.Hãy vào danh sách bàn.");

    }


}