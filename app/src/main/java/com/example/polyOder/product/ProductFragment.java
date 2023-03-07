package com.example.polyOder.product;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.polyOder.MainActivity;
import com.example.polyOder.R;
import com.example.polyOder.base.BaseFragment;
import com.example.polyOder.base.Helpers;

import com.example.polyOder.databinding.FragmentProductBinding;
import com.example.polyOder.interfaces.OnTouchTheProduct;
import com.example.polyOder.model.Product;
import com.example.polyOder.model.TypeProduct;
import com.example.polyOder.model.User;
import com.example.polyOder.product.adapter.ProductAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ProductFragment extends BaseFragment implements OnTouchTheProduct {
    private FragmentProductBinding binding = null;
    private ArrayList<Product> listProduct;
    public ProductAdapter productAdapter = null;
    private TypeProduct typeProduct;
    private Helpers helpers = new Helpers();


    public ProductFragment() {
    }

    public ProductFragment(TypeProduct typeProduct) {
        this.typeProduct = typeProduct;
    }

    public static ProductFragment newInstance() {
        ProductFragment fragment = new ProductFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentProductBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        ((AppCompatActivity)getActivity()).setSupportActionBar(binding.toolbar);
        binding.collapsingToolbar.setExpandedTitleColor(getContext().getColor(android.R.color.transparent));

        if (typeProduct == null) {
            binding.tvNameTypeProduct.setText(R.string.text_type_product_1);
            binding.collapsingToolbar.setTitle("Danh sách sản phẩm");
            listProduct = new ArrayList<>();
            getProduct();
        } else {
            binding.tvNameTypeProduct.setText(typeProduct.getNameType());
            binding.collapsingToolbar.setTitle("Danh sách sản phẩm loại "+typeProduct.getNameType().toLowerCase());
            listProduct = new ArrayList<>();
            getFilterProduct();
        }
        productAdapter = new ProductAdapter(listProduct, ProductFragment.this,getActivity());
        helpers.setReverseItemRecycleView(getContext(), binding.listProduct);
        binding.listProduct.setAdapter(productAdapter);

        showBottomBar();
        listening();
        initObSever();

    }

    @Override
    public void loadData() {
       getDataFirebaseUser("users", new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                if (FirebaseAuth.getInstance().getCurrentUser().getUid() != null && !user.isUserAuthorization()) {
                    binding.fabAddProduct.setVisibility(View.GONE);

                }else {
                    binding.fabAddProduct.setVisibility(View.VISIBLE);
                    showHideWhenScroll();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    @Override
    public void listening() {
        binding.fabAddProduct.setOnClickListener(v -> {
            replaceFragment(new AddProductFragment().newInstance());
        });
        binding.layoutType.setOnClickListener(layout -> {
            replaceFragment(new TypeProductFragment().newInstance());
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
                if (typeProduct == null) {
                    getProduct();
                } else {
                    getFilterProduct();
                }
                binding.listProduct.setAdapter(productAdapter);
                binding.swiperRefreshLayout.setRefreshing(false);
            }
        });

        visibleBottomBarOnScroll( binding.listProduct,binding.nestedScrollView);


    }

    @Override
    public void initObSever() {

    }

    @Override
    public void initView() {


    }


    private void getProduct() {
        getDataFromFirebase2("list_product" ,new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listProduct.clear();
                for (DataSnapshot datasnapshot : snapshot.getChildren()) {
                    Product product = datasnapshot.getValue(Product.class);
                    if(product.isHidden()){
                        listProduct.add(product);
                    }
                }
                visibleViewByList(listProduct,"Có " + listProduct.size() + " sản phẩm ", "Chưa có dữ liệu" );

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        },new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                Product product = snapshot.getValue(Product.class);
                if (product == null || listProduct == null || listProduct.isEmpty()) {
                    return;
                }
                for (int i = 0; i < listProduct.size(); i++) {
                    if (product.getId() == listProduct.get(i).getId()) {
                        listProduct.remove(listProduct.get(i));
                        break;
                    }
                }
                productAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    private void getFilterProduct() {
        getDataFromFirebase2("list_product",new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listProduct.clear();
                for (DataSnapshot datasnapshot : snapshot.getChildren()) {
                    Product product = datasnapshot.getValue(Product.class);
                    listProduct.add(product);
                }
                ArrayList<Product> listFilter = new ArrayList<>();
                for (Product product : listProduct) {
                    if (product.getTypeProduct().getId().equalsIgnoreCase(typeProduct.getId())) {
                        listFilter.add(product);
                    }
                }

                listProduct.retainAll(listFilter);
                visibleViewByList(listProduct,"Có " + listProduct.size() + " sản phẩm ", "Không có sản phẩm thuộc loại "+"\""+typeProduct.getNameType()+"\"" );

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        }, new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                Product product = snapshot.getValue(Product.class);
                if (product == null || listProduct == null || listProduct.isEmpty()) {
                    return;
                }
                for (int i = 0; i < listProduct.size(); i++) {
                    if (product.getId() == listProduct.get(i).getId()) {
                        listProduct.remove(listProduct.get(i));
                        break;
                    }
                }
                productAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void visibleViewByList(ArrayList arrayList, String strContent, String strNull){
        if(!(helpers.isEmptyList(arrayList))){
            visibleViewData(View.VISIBLE, View.GONE , strContent,"");
            productAdapter.notifyDataSetChanged();
        }else {
            visibleViewData(View.GONE, View.VISIBLE , "",strNull);
        }
    }

    private void visibleViewData(int visible1,int visible2, String content, String notificator){
        binding.tvCountProduct.setText(content);
        binding.listProduct.setVisibility(visible1);
        binding.layoutNotifiNullData.setVisibility(visible2);
        binding.tvContentNull.setText(notificator);

    }

    private void filterList(String text, ArrayList<Product> listProduct) {
        ArrayList<Product> filterLists = new ArrayList<>();
        for (Product product : listProduct) {
            if (product.getNameProduct().toLowerCase().contains(text.toLowerCase())) {
                filterLists.add(product);
            }
        }
        if (!filterLists.isEmpty()) {
            productAdapter.setFilterList(filterLists);
            visibleViewData(View.VISIBLE, View.GONE , "Có " + filterLists.size() + " sản phẩm.","");
        }else {
            visibleViewData(View.GONE, View.VISIBLE , "","Không có sản phẩm  "+"\"" +text+"\"");

        }
    }


    private void showHideWhenScroll() {
        binding.listProduct.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                //dy > 0: scroll up; dy < 0: scroll down
                if (dy > 0) binding.fabAddProduct.hide();
                else  binding.fabAddProduct.show();
                super.onScrolled(recyclerView, dx, dy);
            }
        });
    }

    @Override
    public void onClickProduct(Product product) {
        replaceFragment(new DetailProductFragment(product));
    }
}