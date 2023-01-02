package com.example.polyOder.Oder;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.polyOder.MainActivity;
import com.example.polyOder.Oder.Adapter.ListOderAdapter;
import com.example.polyOder.R;
import com.example.polyOder.base.BaseFragment;
import com.example.polyOder.databinding.FragmentListOderBinding;
import com.example.polyOder.model.Receipt;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ListOderFragment extends BaseFragment implements ListOderAdapter.OnClickListener {
    private FragmentListOderBinding binding = null;
    private ArrayList<Receipt> listCancelReceipt;
    private ArrayList<Receipt> listReceipt;
    private ListOderAdapter adapter ;
    private int isChangedLayout = 0;
    private PopupMenu popupMenuTables;
    MainActivity mainActivity;


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
        mainActivity.hideBottomBar();
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
        listReceipt = new ArrayList<>();
        listCancelReceipt = new ArrayList<>();
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
            LinearLayoutManager layoutManager  = new LinearLayoutManager(getContext());
            layoutManager.setStackFromEnd(true);
            layoutManager.setReverseLayout(true);
            binding.recListBill.setLayoutManager(layoutManager);
            binding.recListBill.setAdapter(adapter);

        });
        binding.btnChangeLayoutVertical.setOnClickListener(btn ->{
            binding.tvFilterOder.setText("Tất cả đơn");
            binding.btnChangeLayoutVertical.setVisibility(View.GONE);
            binding.btnChangeLayoutHorizontal.setVisibility(View.VISIBLE);
            isChangedLayout = 0;
            getAllReceipt();
            adapter= new ListOderAdapter(listReceipt,ListOderFragment.this,isChangedLayout);
            LinearLayoutManager layoutManager  = new LinearLayoutManager(getContext());
            layoutManager.setStackFromEnd(true);
            layoutManager.setReverseLayout(true);
            binding.recListBill.setLayoutManager(layoutManager);
            binding.recListBill.setAdapter(adapter);


        });

        binding.layoutFilterOder.setOnClickListener(layout ->{
            popupMenuTables = new PopupMenu(getContext(),binding.layoutFilterOder);
            popupMenuTables.inflate(R.menu.menu_popup_oder);
            Menu menu = popupMenuTables.getMenu();
            popupMenuTables.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    return menuItemClicked(item);
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

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mainActivity = (MainActivity)context;
    }

    private boolean menuItemClicked(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.tvAll:
                getAllReceipt();
                binding.tvFilterOder.setText("Tất cả đơn");
                binding.collapsingToolbar.setTitle("Danh sách hóa đơn");
                binding.swiperRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        getAllReceipt();
                        binding.recListBill.setAdapter(adapter);
                        binding.swiperRefreshLayout.setRefreshing(false);
                    }
                });
                break;
            case R.id.tvPaySuccess:
                getFilterOder(true);
                binding.tvFilterOder.setText("Đơn thanh toán");
                binding.collapsingToolbar.setTitle("Danh sách đơn đã thanh toán");
                binding.swiperRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        getFilterOder(true);
                        binding.recListBill.setAdapter(adapter);
                        binding.swiperRefreshLayout.setRefreshing(false);
                    }
                });
                break;
            case R.id.tvOderCancel:
                getFilterOder(false);
                binding.tvFilterOder.setText("Đơn hủy");
                binding.collapsingToolbar.setTitle("Danh sách đơn đã hủy");
                binding.swiperRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        getFilterOder(false);
                        binding.recListBill.setAdapter(adapter);
                        binding.swiperRefreshLayout.setRefreshing(false);
                    }
                });
                break;
            default:
                Toast.makeText(getContext(), item.getTitle(), Toast.LENGTH_SHORT).show();
                break;
        }

        return  true;
    }

    private void getFilterOder(boolean statusOder){
        DatabaseReference reference;
        if(statusOder){
            reference = FirebaseDatabase.getInstance().getReference("PayReceipt");
            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    listReceipt.clear();
                    for (DataSnapshot snapshot1: snapshot.getChildren()) {
                        Receipt receipt = snapshot1.getValue(Receipt.class);
                        listReceipt.add(receipt);
                    }
                    binding.tvNumberOfOder.setText("Có "+listReceipt.size()+" đơn đã thanh toán");
                    adapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
            adapter= new ListOderAdapter(listReceipt,ListOderFragment.this,isChangedLayout);
            LinearLayoutManager layoutManager  = new LinearLayoutManager(getContext());
            layoutManager.setStackFromEnd(true);
            layoutManager.setReverseLayout(true);
            binding.recListBill.setLayoutManager(layoutManager);
        }else  {
            reference = FirebaseDatabase.getInstance().getReference("CancelReceipt");
            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    listReceipt.clear();
                    for (DataSnapshot snapshot1: snapshot.getChildren()) {
                        Receipt receipt = snapshot1.getValue(Receipt.class);
                        listReceipt.add(receipt);
                    }
                    binding.tvNumberOfOder.setText("Có "+listReceipt.size()+" đơn đã hủy");
                    adapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
            adapter= new ListOderAdapter(listReceipt,ListOderFragment.this,isChangedLayout);
            LinearLayoutManager layoutManager  = new LinearLayoutManager(getContext());
            layoutManager.setStackFromEnd(true);
            layoutManager.setReverseLayout(true);
            binding.recListBill.setLayoutManager(layoutManager);

        }
        binding.recListBill.setAdapter(adapter);


    }


    private void getAllReceipt(){
        DatabaseReference mRef = FirebaseDatabase.getInstance().getReference("CancelReceipt");
        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listReceipt.clear();
                for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                    Receipt receipt = dataSnapshot.getValue(Receipt.class);
                    listReceipt.add(receipt);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        DatabaseReference mRef2 = FirebaseDatabase.getInstance().getReference("PayReceipt");
        mRef2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listCancelReceipt.clear();
                for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                    Receipt receipt = dataSnapshot.getValue(Receipt.class);
                    listCancelReceipt.add(receipt);
                }
                listReceipt.addAll(listCancelReceipt);
                binding.tvNumberOfOder.setText("Có tất cả "+listReceipt.size()+" đơn");
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        adapter= new ListOderAdapter(listReceipt,ListOderFragment.this,isChangedLayout);
        LinearLayoutManager layoutManager  = new LinearLayoutManager(getContext());
        layoutManager.setStackFromEnd(true);
        layoutManager.setReverseLayout(true);
        binding.recListBill.setLayoutManager(layoutManager);
        binding.recListBill.setAdapter(adapter);
        binding.swiperRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                binding.recListBill.setAdapter(adapter);
                adapter.notifyDataSetChanged();
                binding.swiperRefreshLayout.setRefreshing(false);
            }
        });

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
        if(!filterReceipt.isEmpty()) {
            adapter.setFilterList(filterReceipt);
            binding.tvNumberOfOder.setText(filterReceipt.size() + " đơn");
        }
    }



    @Override
    public void onClickListener(Receipt receipt) {
        Log.e("TAG", "onClickListener: "+receipt.getIdTable() );

        replaceFragment(new DetailReceiptFragment(receipt));


    }
}
