package com.example.polyOder.carts.oders;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.lifecycle.ViewModelProvider;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.polyOder.R;
import com.example.polyOder.base.BaseFragment;
import com.example.polyOder.base.Helpers;

import com.example.polyOder.carts.oders.adapter.ParentListOderSaveAdapter;
import com.example.polyOder.databinding.FragmentSaveToOderBinding;
import com.example.polyOder.interfaces.OnTouchTheOderSave;
import com.example.polyOder.model.Receipt;
import com.example.polyOder.model.Table;
import com.example.polyOder.model.User;
import com.example.polyOder.viewModel.TableViewModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class ListSaveOderFragment extends BaseFragment implements OnTouchTheOderSave {
    private FragmentSaveToOderBinding binding;
    private ArrayList<Receipt> listOderSave;
    private TableViewModel tableViewModel;
    private  ParentListOderSaveAdapter adapter;
    private Helpers helpers = new Helpers();


    public ListSaveOderFragment() {

    }

    public static ListSaveOderFragment newInstance() {
        ListSaveOderFragment fragment = new ListSaveOderFragment();
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
        binding = FragmentSaveToOderBinding.inflate(inflater,container,false);
        tableViewModel = new ViewModelProvider(this).get(TableViewModel.class);
        return binding.getRoot();

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        showBottomBar();
        listOderSave = new ArrayList<>();
        listening();

    }

    @Override
    public void loadData() {
        getDataFromFirebase( "OderSave",new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listOderSave.clear();
                for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                    Receipt receipt = dataSnapshot.getValue(Receipt.class);
                    listOderSave.add(receipt);
                }

                adapter = new ParentListOderSaveAdapter(listOderSave,ListSaveOderFragment.this,getContext());
                helpers.setReverseItemRecycleView(getContext(), binding.recVListOderSave);
                binding.recVListOderSave.setAdapter(adapter);

                binding.swiperRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        binding.recVListOderSave.setAdapter(adapter);
                        adapter.notifyDataSetChanged();
                        binding.swiperRefreshLayout.setRefreshing(false);
                    }
                });
                visibleViewByList(listOderSave,"Số lượng: "+listOderSave.size()+" đơn" );

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }




    @Override
    public void listening() {
        binding.icShowSearch.setOnClickListener(ic ->{
            visibleSearchView(View.VISIBLE, View.GONE);
        });
        binding.tvCloseSearchView.setOnClickListener(ic ->{
            visibleSearchView(View.GONE, View.VISIBLE);
        });

        binding.searchViewOderSave.clearFocus();
        binding.searchViewOderSave.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterReceipt(newText);
                return true;
            }
        });

        visibleBottomBarOnScroll(binding.recVListOderSave, null);


    }

    @Override
    public void initObSever() {

    }

    @Override
    public void initView() {

    }


    @Override
    public void onClickMenu(Receipt receipt, int menuId) {

        switch (menuId) {
            case R.id.tvPaySuccess:
                getPaymentOderSave(receipt);
                break;
            case R.id.tvOderCancel:
                getDataFirebaseUser("users",new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        User user = snapshot.getValue(User.class);
                        if(!user.isUserAuthorization()){
                            helpers.notificationErrInput(getContext(),"Bạn không thể hủy đơn , hãy liên hệ quản lí của bạn nếu gặp vấn đề.");
                        }else {
                            getCancelOderSave(receipt);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });

                break;
        }
    }

    private void filterReceipt(String text){
        ArrayList<Receipt> filterReceipt = new ArrayList<>();
        for (Receipt receipt: listOderSave) {
            getDataFromFirebase("tables", new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                        Table table = dataSnapshot.getValue(Table.class);

                        if (table.getId_table().equals(receipt.getIdTable())) {
                            if(table.getName_table().toLowerCase().contains(text.toLowerCase()) || receipt.getTimeOder().toLowerCase().contains(text.toLowerCase()) ||
                                    receipt.getMoney().toString().toLowerCase().contains(text.toLowerCase()) ||
                                    receipt.getMoney().toString().toLowerCase().contains(text.toLowerCase())){
                                filterReceipt.add(receipt);
                            }
                        }

                    }
                    if(!(helpers.isEmptyList(filterReceipt))) {
                        adapter.setFilterList(filterReceipt);
                        visibleViewData(View.VISIBLE, View.GONE , "","");
                        binding.tvCountSaveOder.setVisibility(View.GONE);
                    }else {
                        visibleViewData(View.GONE, View.VISIBLE , "","Không có đơn hàng  "+"\"" +text+"\"");
                        binding.tvCountSaveOder.setVisibility(View.GONE);
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        }


    }
    private void visibleViewByList(ArrayList arrayList, String strContent){
        if(!(helpers.isEmptyList(arrayList))){
            visibleViewData(View.VISIBLE, View.GONE , strContent,"");
            adapter.notifyDataSetChanged();
        }else {
            visibleViewData(View.GONE, View.VISIBLE , "","Chưa có đơn hàng");
        }
    }


    private void visibleViewData(int visible1,int visible2, String content, String notificator){
        binding.tvCountSaveOder.setText(content);
        binding.layoutNotificationNullData.setVisibility(visible2);
        binding.tvNotifiNull.setText(notificator);
        binding.recVListOderSave.setVisibility(visible1);
    }



    private void visibleSearchView(int vib1, int vib2){
        binding.icShowSearch.setVisibility(vib2);
        binding.tvCloseSearchView.setVisibility(vib1);
        binding.searchViewOderSave.setVisibility(vib1);
        binding.tvTitle.setVisibility(vib2);
        binding.tvCountSaveOder.setVisibility(vib2);

    }




    private void getPaymentOderSave(Receipt receipt){
        getConfirmResponse(getContext(), "Xác nhận", R.drawable.ic_dollar_sign, "Hãy xác nhận thanh toán đơn hàng.", "Đồng ý", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                tableViewModel.setStatusTable(receipt.getIdTable(), "false");
                tableViewModel.liveDataPayReceipt(receipt);
                helpers.notificationSuccessInput(getContext(),"Thanh toán thành công");
            }
        }, "Hủy", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Toast.makeText(getContext(),"Đã hủy !",Toast.LENGTH_SHORT).show();
                dialogInterface.cancel();
            }
        });


    }

    private void getCancelOderSave(Receipt receipt){
        getConfirmResponse(getContext(), "Xác nhận hủy đơn hàng", R.drawable.ic_cancelled, getString( R.string.notification_cancel_oder), "Đồng ý", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                tableViewModel.setStatusTable(receipt.getIdTable(), "false");
                tableViewModel.liveDataCancelReceipt(receipt);
                helpers.notificationSuccessInput(getContext(),"Đã hủy đơn hàng");
            }
        }, "Hủy", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Toast.makeText(getContext(),"Đã hủy !",Toast.LENGTH_SHORT).show();
                dialogInterface.cancel();
            }
        });

    }



}