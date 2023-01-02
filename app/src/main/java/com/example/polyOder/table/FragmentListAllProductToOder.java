package com.example.polyOder.table;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.polyOder.MainActivity;
import com.example.polyOder.R;
import com.example.polyOder.base.BaseFragment;
import com.example.polyOder.databinding.FragmentAddProductToOderBinding;
import com.example.polyOder.model.Product;
import com.example.polyOder.model.Table;
import com.example.polyOder.table.adapter.ProductAdapterToOder;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class FragmentListAllProductToOder extends BaseFragment{
    private FragmentAddProductToOderBinding binding;
    public ProductAdapterToOder productAdapterToOder = null;
    private Table table = null;
    private ArrayList<Product> listProduct;
    private ArrayList<Product> listProductSelect;
    private ArrayList<String> listId;
    private ArrayList<Integer> listCount;
    MainActivity mainActivity;


    public FragmentListAllProductToOder() {
        // Required empty public constructor
    }


    public static FragmentListAllProductToOder newInstance() {
        FragmentListAllProductToOder fragment = new FragmentListAllProductToOder();
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
        binding = FragmentAddProductToOderBinding.inflate(inflater,container,false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.tvNameTypeProduct.setText(R.string.text_type_product_1);
        listProduct = new ArrayList<>();
        listId = new ArrayList<>();
        listProductSelect = new ArrayList<>();
        listCount = new ArrayList<>();
        getProduct();
        productAdapterToOder = new ProductAdapterToOder(listProduct,getActivity());
        LinearLayoutManager layoutManager  = new LinearLayoutManager(getContext());
        layoutManager.setStackFromEnd(true);
        layoutManager.setReverseLayout(true);
        binding.listProductToOder.setLayoutManager(layoutManager);
        binding.listProductToOder.setAdapter(productAdapterToOder);
        mainActivity.hideBottomBar();
        listening();
        initObSever();
    }

    @Override
    public void loadData() {
        if (getArguments() != null) {
            table = (Table) getArguments().getSerializable("table");
            Log.d("TAG", "loadData: Bán mang về "+table.getName_table());
        }

    }

    @Override
    public void listening() {
        binding.icBack.setOnClickListener(ic ->{
            backStack();
        });

        binding.searchViewProduct.clearFocus();
        binding.searchViewProduct.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterList(newText, listProduct);
                return true;
            }
        });
        binding.swiperRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getProduct();
                binding.tvNameTypeProduct.setText(R.string.text_type_product_1);
                binding.listProductToOder.setAdapter(productAdapterToOder);
                binding.swiperRefreshLayout.setRefreshing(false);
            }
        });

        binding.btnAddOder.setOnClickListener(btn ->{
            for(Product product : listProduct){
                if(product.isSelected() ){
                    listProductSelect.add(product);
                    listCount.add(product.getIsClick());
                }
            }
            for(Product product : listProductSelect){
                listId.add(product.getId());
            }
            if(listId.size() == 0 && listProductSelect.size() == 0 && listCount.size() == 0){
                notificationErrInput(getContext(),"Hãy chọn món!");
            }else {
                if (table != null){
                    DetailTableFragment detailTableFragment = new DetailTableFragment(table);
                    Bundle bundle = new Bundle();
                    bundle.putStringArrayList("list_product_select",  listId);
                    bundle.putIntegerArrayList("list_count_product",  listCount);
                    bundle.putSerializable("table", table);
                    detailTableFragment.setArguments(bundle);
                    replaceFragment(detailTableFragment);
                }
            }

        });
        binding.btnReselect.setOnClickListener(btn ->{
            for(Product product : listProduct) {
                product.setSelected(false);
            }
                listProductSelect.clear();
                listId.clear();
                listCount.clear();
                productAdapterToOder.notifyDataSetChanged();
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

    private void getProduct() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("list_product");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listProduct.clear();
                for (DataSnapshot datasnapshot : snapshot.getChildren()) {
                    Product product = datasnapshot.getValue(Product.class);
                    if(product.isHidden()){
                        listProduct.add(product);
                    }

                }
                productAdapterToOder.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

    }



    private void filterList(String text, ArrayList<Product> listProduct) {
        ArrayList<Product> filterLists = new ArrayList<>();
        for (Product product : listProduct) {
            if (product.getNameProduct().toLowerCase().contains(text.toLowerCase())) {
                filterLists.add(product);
            }else if(product.getTypeProduct().getNameType().toLowerCase().contains(text.toLowerCase())){
                filterLists.add(product);
            }
        }
        if (!filterLists.isEmpty()) {
            productAdapterToOder.setFilterList(filterLists);
            binding.tvNameTypeProduct.setText("Có " + filterLists.size() + " sản phẩm");
        }

    }



}