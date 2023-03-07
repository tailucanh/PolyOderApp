package com.example.polyOder.carts.oders;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.ScaleAnimation;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.polyOder.base.Helpers;
import com.example.polyOder.carts.oders.adapter.ListOderAdapter;
import com.example.polyOder.interfaces.OnTouchTheOder;
import com.example.polyOder.R;
import com.example.polyOder.base.BaseFragment;
import com.example.polyOder.databinding.FragmentListOderBinding;
import com.example.polyOder.model.Receipt;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;



public class ListOderFragment extends BaseFragment implements OnTouchTheOder {
    private FragmentListOderBinding binding = null;
    private ArrayList<Receipt> listCancelReceipt;
    private ArrayList<Receipt> listReceipt;
    private ListOderAdapter adapter ;
    private int isChangedLayout = 0;
    private PopupMenu popupMenuTables;
    private Helpers helpers = new Helpers();


    public ListOderFragment(){

    }
    public static ListOderFragment newInstance() {
        ListOderFragment fragment = new ListOderFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
       binding = FragmentListOderBinding.inflate(inflater,container,false);
       return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        hideBottomBar();
        listReceipt = new ArrayList<>();
        listCancelReceipt = new ArrayList<>();

        ((AppCompatActivity)getActivity()).setSupportActionBar(binding.toolbar);
        binding.collapsingToolbar.setExpandedTitleColor(getContext().getColor(android.R.color.transparent));
        binding.collapsingToolbar.setTitle("Danh sách hóa đơn");
        binding.recListBill.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

            }
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0) {
                    binding.toolbar.setVisibility(View.VISIBLE);
                } else {
                    binding.toolbar.setVisibility(View.GONE);
                }
            }
        });

        listening();
        initObSever();
    }

    @Override
    public void loadData() {

        getAllReceipt();

    }

    @Override
    public void listening() {
        binding.icBack.setOnClickListener(ic->{
            backStack();
        });

        binding.searchViewOder.clearFocus();
        binding.searchViewOder.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
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
        binding.btnChangeLayoutHorizontal.setOnClickListener(btn ->{
            binding.tvFilterOder.setText("Tất cả đơn");
            binding.btnChangeLayoutHorizontal.setVisibility(View.GONE);
            binding.btnChangeLayoutVertical.setVisibility(View.VISIBLE);
            isChangedLayout = 1;
            getAllReceipt();

            adapter= new ListOderAdapter(listReceipt,ListOderFragment.this,isChangedLayout);
            setAdapterRecycleView(adapter, binding.recListBill, binding.swiperRefreshLayout);
        });
        binding.btnChangeLayoutVertical.setOnClickListener(btn ->{
            binding.tvFilterOder.setText("Tất cả đơn");
            binding.btnChangeLayoutVertical.setVisibility(View.GONE);
            binding.btnChangeLayoutHorizontal.setVisibility(View.VISIBLE);
            isChangedLayout = 0;
            getAllReceipt();
            adapter= new ListOderAdapter(listReceipt,ListOderFragment.this,isChangedLayout);
            setAdapterRecycleView(adapter, binding.recListBill, binding.swiperRefreshLayout);


        });

        binding.layoutFilterOder.setOnClickListener(layout ->{
            binding.icArrow.animate().rotation(0).start();
            popupMenuTables = new PopupMenu(getContext(),binding.layoutFilterOder);
            popupMenuTables.inflate(R.menu.menu_popup_oder);

            popupMenuTables.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    return menuItemClicked(item);
                }
            });
            popupMenuTables.setOnDismissListener(new PopupMenu.OnDismissListener() {
                @Override
                public void onDismiss(PopupMenu popupMenu) {
                    binding.icArrow.animate().rotation(180).start();
                }
            });
            popupMenuTables.show();


        });
    }

    @Override
    public void initObSever() {

    }

    @Override
    public void initView() {

    }


    private boolean menuItemClicked(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.tvAll:
                getAllReceipt();
                binding.tvFilterOder.setText("Tất cả đơn");
                binding.collapsingToolbar.setTitle("Danh sách hóa đơn");

                break;
            case R.id.tvPaySuccess:
                getFilterOder(true);
                binding.tvFilterOder.setText("Đơn thanh toán");
                binding.collapsingToolbar.setTitle("Danh sách đơn đã thanh toán");

                break;
            case R.id.tvOderCancel:
                getFilterOder(false);
                binding.tvFilterOder.setText("Đơn hủy");
                binding.collapsingToolbar.setTitle("Danh sách đơn đã hủy");

                break;
            default:
                Toast.makeText(getContext(), item.getTitle(), Toast.LENGTH_SHORT).show();
                break;
        }

        return  true;
    }

    private void getFilterOder(boolean statusOder){
        if(statusOder){
            getDataFromFirebase("PayReceipt",new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    listReceipt.clear();
                    for (DataSnapshot snapshot1: snapshot.getChildren()) {
                        Receipt receipt = snapshot1.getValue(Receipt.class);
                        listReceipt.add(receipt);
                    }
                    visibleViewByList(listReceipt, "Có "+listReceipt.size()+" đơn đã thanh toán", "Chưa có đơn thanh toán.");
                    adapter= new ListOderAdapter(listReceipt,ListOderFragment.this,isChangedLayout);
                    setAdapterRecycleView(adapter, binding.recListBill, binding.swiperRefreshLayout);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        }else  {
            getDataFromFirebase("CancelReceipt",new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    listReceipt.clear();
                    for (DataSnapshot snapshot1: snapshot.getChildren()) {
                        Receipt receipt = snapshot1.getValue(Receipt.class);
                        listReceipt.add(receipt);
                    }
                    visibleViewByList(listReceipt, "Có "+listReceipt.size()+" đơn đã hủy", "Chưa có đơn hủy.");
                    adapter= new ListOderAdapter(listReceipt,ListOderFragment.this,isChangedLayout);
                    setAdapterRecycleView(adapter, binding.recListBill, binding.swiperRefreshLayout);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });


        }

    }


    private void getAllReceipt(){
        getDataFromFirebase("CancelReceipt",new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listCancelReceipt.clear();
                for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                    Receipt receipt = dataSnapshot.getValue(Receipt.class);
                    listCancelReceipt.add(receipt);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        getDataFromFirebase("PayReceipt",new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listReceipt.clear();
                for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                    Receipt receipt = dataSnapshot.getValue(Receipt.class);
                    listReceipt.add(receipt);
                }
                getDataFromFirebase("CancelReceipt",new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        listCancelReceipt.clear();
                        for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                            Receipt receipt = dataSnapshot.getValue(Receipt.class);
                            listCancelReceipt.add(receipt);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                listReceipt.addAll(listCancelReceipt);
                Comparator<Receipt> timeComparator = new Comparator<Receipt>() {
                    DateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

                    public int compare(Receipt obj1, Receipt obj2) {
                        try {
                            Date time1 = format.parse(obj1.getTimeOder());
                            Date time2 = format.parse(obj2.getTimeOder());
                            return time1.compareTo(time2);
                        } catch (ParseException e) {
                            throw new IllegalArgumentException(e);
                        }
                    }
                };

                Collections.sort(listReceipt, timeComparator);
                visibleViewByList(listReceipt, "Có tất cả "+listReceipt.size()+" đơn", "Chưa có dữ liệu.");

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        adapter= new ListOderAdapter(listReceipt,ListOderFragment.this,isChangedLayout);
        setAdapterRecycleView(adapter, binding.recListBill, binding.swiperRefreshLayout);
    }

    private void setAdapterRecycleView(ListOderAdapter adapter, RecyclerView recyclerView,SwipeRefreshLayout swipeRefreshLayout ){
        helpers.setReverseItemRecycleView(getContext(), recyclerView);
        recyclerView.setAdapter(adapter);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                recyclerView.setAdapter(adapter);
                adapter.notifyDataSetChanged();
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }




    private void visibleViewByList(ArrayList arrayList, String strContent ,String strNull){
        if(!(helpers.isEmptyList(arrayList))){
            visibleViewData(View.VISIBLE, View.GONE , strContent,strNull);
            adapter.notifyDataSetChanged();
        }else {
            visibleViewData(View.GONE, View.VISIBLE , "",strNull);
        }

    }


    private void filterReceipt(String text){
        ArrayList<Receipt> filterReceipt = new ArrayList<>();
        for (Receipt receipt: listReceipt) {
            if(("POLY000"+receipt.getIdReceipt().substring(16,20)).toLowerCase().contains(text.toLowerCase())||
                    receipt.getTimeOder().toLowerCase().contains(text.toLowerCase()) ||
                    receipt.getMoney().toString().toLowerCase().contains(text.toLowerCase())){
                filterReceipt.add(receipt);
            }

        }
        if(!(helpers.isEmptyList(filterReceipt))) {
            adapter.setFilterList(filterReceipt);
            visibleViewData(View.VISIBLE, View.GONE , "Có "+filterReceipt.size() + " đơn","");

        }else {
            visibleViewData(View.GONE, View.VISIBLE , "","Không có đơn hàng  "+"\"" +text+"\"");
        }
    }

    private void visibleViewData(int visible1,int visible2, String content, String notificator){
        binding.tvNumberOfOder.setText(content);
        binding.notifiNullData.setVisibility(visible2);
        binding.tvContentNull.setText(notificator);
        binding.recListBill.setVisibility(visible1);
    }


    @Override
    public void onClickOder(Receipt receipt) {
        replaceFragment(new DetailReceiptFragment(receipt));

    }
}
