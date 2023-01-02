package com.example.polyOder.product;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.example.polyOder.MainActivity;
import com.example.polyOder.R;
import com.example.polyOder.base.BaseFragment;
import com.example.polyOder.databinding.DialogAddTypeProductBinding;
import com.example.polyOder.databinding.DialogFunctionProductBinding;
import com.example.polyOder.databinding.FragmentTypeProductBinding;
import com.example.polyOder.model.TypeProduct;
import com.example.polyOder.model.User;
import com.example.polyOder.product.Adapter.TypeProductAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class TypeProductFragment extends BaseFragment implements  TypeProductAdapter.OnClickItemListener, TypeProductAdapter.OnItemLongClickListener{
    private FragmentTypeProductBinding binding = null;
    public ArrayList<TypeProduct> listType;
    private TypeProductAdapter typeAdapter;
    private User user;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    MainActivity mainActivity;


    public TypeProductFragment() {
        // Required empty public constructor
    }
    public static TypeProductFragment newInstance() {
        TypeProductFragment fragment = new TypeProductFragment();
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
       binding = FragmentTypeProductBinding.inflate(inflater,container,false);
       return binding.getRoot();

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        user = new User();
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        listType = new ArrayList<>();
        typeAdapter = new TypeProductAdapter(listType, TypeProductFragment.this,TypeProductFragment.this);
        LinearLayoutManager layoutManager  = new LinearLayoutManager(getContext());
        layoutManager.setStackFromEnd(true);
        layoutManager.setReverseLayout(true);
        binding.listsTypeProduct.setLayoutManager(layoutManager);
        binding.listsTypeProduct.setAdapter(typeAdapter);
        mainActivity.hideBottomBar();
        listening();
        loadData();


    }

    @Override
    public void loadData() {
        getTypeProduct();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.child("users").child(firebaseUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                user = snapshot.getValue(User.class);
                if (firebaseUser != null && !user.isUserAuthorization()) {
                    binding.icAddType.setVisibility(View.GONE);

                }else {
                    binding.icAddType.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    @Override
    public void listening() {
        binding.icShowSearch.setOnClickListener(ic ->{
            binding.tvTitle.setVisibility(View.GONE);
            binding.icShowSearch.setVisibility(View.GONE);
            binding.icAddType.setVisibility(View.GONE);
            binding.tvCloseSearchView.setVisibility(View.VISIBLE);
            binding.searchViewTypeProduct.setVisibility(View.VISIBLE);

        });
        binding.tvCloseSearchView.setOnClickListener(ic ->{
            binding.tvTitle.setVisibility(View.VISIBLE);
            binding.icShowSearch.setVisibility(View.VISIBLE);
            binding.icAddType.setVisibility(View.VISIBLE);
            binding.tvCloseSearchView.setVisibility(View.GONE);
            binding.searchViewTypeProduct.setVisibility(View.GONE);
            binding.tvAllProduct.setVisibility(View.VISIBLE);
        });
        binding.searchViewTypeProduct.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                filterListType(newText);
                binding.tvAllProduct.setVisibility(View.GONE);
                return false;
            }
        });

        binding.tvAllProduct.setOnClickListener(tv ->{
            replaceFragment(new ProductFragment().newInstance());
        });

        binding.icAddType.setOnClickListener(ic ->{
            dialogAddTypeProduct(getContext());
        });
        binding.icBack.setOnClickListener(ic ->{
              backStack();
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

    public void dialogAddTypeProduct(Context context) {
        final Dialog dialog = new Dialog(context);
        DialogAddTypeProductBinding binding = DialogAddTypeProductBinding.inflate(LayoutInflater.from(context));
        dialog.setContentView(binding.getRoot());
        dialog.setCancelable(false);
        Window window = dialog.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        binding.tvCancel.setOnClickListener(tv ->{
            dialog.dismiss();
        });
        binding.edNameType.setHint( context.getString(R.string.text_hint_search_type_product));

        binding.tvAdd.setOnClickListener(add->{
            FirebaseDatabase data = FirebaseDatabase.getInstance();
            DatabaseReference mRef = data.getReference("list_type_product");
            String key = mRef.push().getKey();

            if(TextUtils.isEmpty(binding.edNameType.getText().toString())){
                Toast.makeText(context, "Hãy nhập tên loại !"  , Toast.LENGTH_SHORT).show();
            }else {
                TypeProduct typeProduct = new TypeProduct(key,binding.edNameType.getText().toString().trim(),true);
                mRef.child(key).setValue(typeProduct).addOnCompleteListener(task -> {
                    if (task.isSuccessful()){
                        Toast.makeText(getContext(), "Thêm loại thành công", Toast.LENGTH_SHORT).show();
                    }else {
                        Toast.makeText(getContext(), "Thêm thất bại", Toast.LENGTH_SHORT).show();
                    }
                });

                dialog.dismiss();
            }
        });
        dialog.show();

    }

    private void getTypeProduct(){
        DatabaseReference mRef =  FirebaseDatabase.getInstance().getReference("list_type_product");
        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listType.clear();
                for(DataSnapshot datasnapshot : snapshot.getChildren()){
                    TypeProduct type = datasnapshot.getValue(TypeProduct.class);
                    if(type.isHidden()){
                        listType.add(type);
                    }

                }
                typeAdapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        binding.listsTypeProduct.setAdapter(typeAdapter);


    }


    private void filterListType(String text) {
        ArrayList<TypeProduct> filterListType =new ArrayList<>();
        for (TypeProduct type : listType) {
            if(type.getNameType().toLowerCase().contains(text.toLowerCase())){
                filterListType.add(type);
            }
        }
        if(filterListType.isEmpty()){
        }else{
            typeAdapter.setFilterListType(filterListType);
        }
    }

    private void dialogFunctionProduct(Context context, TypeProduct typeProduct) {
        final Dialog dialogFunction = new Dialog(context);
        DialogFunctionProductBinding bindingDialog = DialogFunctionProductBinding.inflate(LayoutInflater.from(context));
        dialogFunction.setContentView(bindingDialog.getRoot());
        Window window = dialogFunction.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT,WindowManager.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        WindowManager.LayoutParams layoutParams = window.getAttributes();
        layoutParams.gravity = Gravity.BOTTOM;
        window.setAttributes(layoutParams);
        bindingDialog.dialogChooserFunction.setTranslationY(150);
        bindingDialog.dialogChooserFunction.animate().translationYBy(-150).setDuration(400);

        bindingDialog.tvFun1.setText("Sửa thông tin loại sản phẩm");
        bindingDialog.tvFun2.setText("Xóa loại sản phẩm");


        bindingDialog.tvFun1.setOnClickListener(tv ->{
            final Dialog dialogChange = new Dialog(context);
            DialogAddTypeProductBinding bindingChange = DialogAddTypeProductBinding.inflate(LayoutInflater.from(context));
            dialogChange.setContentView(bindingChange.getRoot());
            dialogChange.setCancelable(false);
            Window window2 = dialogChange.getWindow();
            window2.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
            window2.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            bindingChange.tvTitle.setText("Sửa thông tin");
            bindingChange.tvAdd.setText("Lưu");
            bindingChange.edNameType.setText(typeProduct.getNameType());
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("list_type_product");

            bindingChange.tvAdd.setOnClickListener(tv2 ->{
                if(TextUtils.isEmpty(bindingChange.edNameType.getText().toString())){
                    Toast.makeText(context, "Hãy nhập tên loại !"  , Toast.LENGTH_SHORT).show();
                }else {
                    TypeProduct typeProduct1 = new TypeProduct(typeProduct.getId(), bindingChange.edNameType.getText().toString(), true);
                    reference.child(typeProduct.getId()).setValue(typeProduct1).addOnCompleteListener(task->{
                        if (task.isSuccessful()){
                            Toast.makeText(context, "Cập nhật thành công!", Toast.LENGTH_SHORT).show();
                            dialogChange.cancel();
                        }else {
                            Toast.makeText(context, "Cập nhật không thành công!", Toast.LENGTH_SHORT).show();
                            dialogChange.cancel();
                        }
                    });
                }

            });

            bindingChange.tvCancel.setOnClickListener(tv2 ->{
                dialogChange.dismiss();
            });

            dialogChange.show();
            dialogFunction.cancel();
        });

        bindingDialog.tvFun2.setOnClickListener(v->{
            typeProduct.setHidden(false);
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("list_type_product");
            reference.child(typeProduct.getId()).setValue(typeProduct).addOnCompleteListener(task->{
                if (task.isSuccessful()){
                    Toast.makeText(context, "Đã xóa", Toast.LENGTH_SHORT).show();
                    dialogFunction.cancel();
                }else {
                    Toast.makeText(context, "Xóa không thành công!", Toast.LENGTH_SHORT).show();
                    dialogFunction.cancel();
                }
            });
            typeAdapter.notifyDataSetChanged();
            dialogFunction.cancel();

        });
        dialogFunction.show();

    }


    @Override
    public void onClickItemProduct(TypeProduct typeProduct) {
        replaceFragment( new ProductFragment(typeProduct));
    }

    @Override
    public void onLongClickItemProduct(TypeProduct typeProduct) {
        if(user.isUserAuthorization()){
            dialogFunctionProduct(getContext(),typeProduct);
        }else {
            notificationErrInput(getContext(),"Bạn không thể chỉnh sửa loại sản phẩm!");
        }

    }
}