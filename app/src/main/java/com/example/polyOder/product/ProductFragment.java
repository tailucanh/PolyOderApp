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
import com.example.polyOder.databinding.FragmentProductBinding;
import com.example.polyOder.model.Product;
import com.example.polyOder.model.TypeProduct;
import com.example.polyOder.model.User;
import com.example.polyOder.product.Adapter.ProductAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ProductFragment extends BaseFragment implements  ProductAdapter.OnClickItemListener{
    private FragmentProductBinding bindProduct = null;
    private ArrayList<Product> listProduct;
    public ProductAdapter productAdapter = null;
    private TypeProduct typeProduct;
    private User user;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    MainActivity mainActivity;



    public ProductFragment() {
    }

    public ProductFragment(TypeProduct typeProduct) {
        this.typeProduct = typeProduct;
    }

    public ProductFragment newInstance2() {
        return new ProductFragment(typeProduct);
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
        bindProduct = FragmentProductBinding.inflate(inflater, container, false);
        return bindProduct.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        user = new User();
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        ((AppCompatActivity)getActivity()).setSupportActionBar(bindProduct.toolbar);
        bindProduct.collapsingToolbar.setExpandedTitleColor(getContext().getColor(android.R.color.transparent));

        if (typeProduct == null) {
            bindProduct.tvNameTypeProduct.setText(R.string.text_type_product_1);
            bindProduct.collapsingToolbar.setTitle("Danh sách sản phẩm");
            listProduct = new ArrayList<>();
            getProduct();
        } else {
            bindProduct.tvNameTypeProduct.setText(typeProduct.getNameType());
            bindProduct.collapsingToolbar.setTitle("Danh sách sản phẩm loại "+typeProduct.getNameType().toLowerCase());
            listProduct = new ArrayList<>();
            getFilterProduct();
        }
        productAdapter = new ProductAdapter(listProduct, ProductFragment.this,getActivity());
        LinearLayoutManager layoutManager  = new LinearLayoutManager(getContext());
        layoutManager.setStackFromEnd(true);
        layoutManager.setReverseLayout(true);
        bindProduct.listProduct.setLayoutManager(layoutManager);
        bindProduct.listProduct.setAdapter(productAdapter);

        mainActivity.showBottomBar();
        listening();
        initObSever();

    }

    @Override
    public void loadData() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.child("users").child(firebaseUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                user = snapshot.getValue(User.class);
                if (firebaseUser != null && !user.isUserAuthorization()) {
                    bindProduct.fabAddProduct.setVisibility(View.GONE);

                }else {
                    bindProduct.fabAddProduct.setVisibility(View.VISIBLE);
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
        bindProduct.fabAddProduct.setOnClickListener(v -> {
            replaceFragment(new AddProductFragment().newInstance());
        });
        bindProduct.layoutType.setOnClickListener(layout -> {
            replaceFragment(new TypeProductFragment().newInstance());
        });

        bindProduct.searchViewProduct.clearFocus();
        bindProduct.searchViewProduct.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
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
        bindProduct.swiperRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (typeProduct == null) {
                    getProduct();
                } else {
                    getFilterProduct();
                }
                bindProduct.listProduct.setAdapter(productAdapter);
                bindProduct.swiperRefreshLayout.setRefreshing(false);
            }
        });
        bindProduct.listProduct.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

            }
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0) {
                    mainActivity.visibilityOfBottom(false);
                } else {
                    mainActivity.visibilityOfBottom(true);
                }
            }
        });
        bindProduct.nestedScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
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
                bindProduct.tvCountProduct.setText("Có " + listProduct.size() + " sản phẩm");
                productAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
        reference.addChildEventListener(new ChildEventListener() {
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
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("list_product");
        reference.addValueEventListener(new ValueEventListener() {
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
                bindProduct.tvCountProduct.setText("Có " + listProduct.size() + " sản phẩm ");
                productAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
        reference.addChildEventListener(new ChildEventListener() {
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


    private void filterList(String text, ArrayList<Product> listProduct) {
        ArrayList<Product> filterLists = new ArrayList<>();
        for (Product product : listProduct) {
            if (product.getNameProduct().toLowerCase().contains(text.toLowerCase())) {
                filterLists.add(product);
            }
        }
        if (!filterLists.isEmpty()) {
            productAdapter.setFilterList(filterLists);
            bindProduct.tvCountProduct.setText("Có " + filterLists.size() + " sản phẩm.");
        }
    }


    private void showHideWhenScroll() {
        bindProduct.listProduct.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                //dy > 0: scroll up; dy < 0: scroll down
                if (dy > 0) bindProduct.fabAddProduct.hide();
                else  bindProduct.fabAddProduct.show();
                super.onScrolled(recyclerView, dx, dy);
            }
        });
    }

    @Override
    public void onClickItemProduct(Product product) {
        replaceFragment(new DetailProductFragment(product));
    }
}