package com.example.polyOder.product;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
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
import android.view.WindowManager;
import android.widget.Toast;

import com.example.polyOder.MainActivity;
import com.example.polyOder.R;
import com.example.polyOder.base.BaseFragment;
import com.example.polyOder.base.Helpers;

import com.example.polyOder.databinding.DialogAddTypeProductBinding;
import com.example.polyOder.databinding.DialogFunctionProductBinding;
import com.example.polyOder.databinding.FragmentTypeProductBinding;
import com.example.polyOder.interfaces.OnTouchTheTypeProduct;
import com.example.polyOder.model.TypeProduct;
import com.example.polyOder.model.User;
import com.example.polyOder.product.adapter.TypeProductAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class TypeProductFragment extends BaseFragment implements OnTouchTheTypeProduct {
    private FragmentTypeProductBinding binding = null;
    public ArrayList<TypeProduct> listType;
    private TypeProductAdapter typeAdapter;
    private User user;
    private Helpers helpers = new Helpers();




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
        listType = new ArrayList<>();

        hideBottomBar();
        listening();
        loadData();


    }

    @Override
    public void loadData() {
        getTypeProduct();

       getDataFirebaseUser("users",new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                 user = snapshot.getValue(User.class);
                if (FirebaseAuth.getInstance().getCurrentUser().getUid() != null && !user.isUserAuthorization()) {
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
    private void getTypeProduct(){
        getDataFromFirebase( "list_type_product",new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listType.clear();
                for(DataSnapshot datasnapshot : snapshot.getChildren()){
                    TypeProduct type = datasnapshot.getValue(TypeProduct.class);
                    if(type.isHidden()){
                        listType.add(type);
                    }
                }
                if(!(helpers.isEmptyList(listType))){
                    binding.listsTypeProduct.setVisibility(View.VISIBLE);
                    binding.layoutNotifiNullData.setVisibility(View.GONE);
                    typeAdapter = new TypeProductAdapter(listType,TypeProductFragment.this);
                    helpers.setReverseItemRecycleView(getContext(),binding.listsTypeProduct);
                    binding.listsTypeProduct.setAdapter(typeAdapter);

                }else {
                    binding.listsTypeProduct.setVisibility(View.GONE);
                    binding.layoutNotifiNullData.setVisibility(View.VISIBLE);
                    binding.tvContentNull.setText("Chưa có dữ liệu");
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });


    }


    public void dialogAddTypeProduct(Context context) {
        final Dialog dialog = new Dialog(context);
        DialogAddTypeProductBinding binding = DialogAddTypeProductBinding.inflate(LayoutInflater.from(context));
        dialog.setContentView(binding.getRoot());
        dialog.setCancelable(false);
        helpers.setLayoutDialog(dialog, 0,WindowManager.LayoutParams.WRAP_CONTENT);

        binding.tvCancel.setOnClickListener(tv ->{
            dialog.dismiss();
        });
        binding.edNameType.setHint(context.getString(R.string.text_hint_search_type_product));

        binding.tvAdd.setOnClickListener(tv->{
            FirebaseDatabase data = FirebaseDatabase.getInstance();
            DatabaseReference mRef = data.getReference("list_type_product");
            String key = mRef.push().getKey();

            if(helpers.isEmptyString(binding.edNameType.getText().toString())){
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



    private void filterListType(String text) {
        ArrayList<TypeProduct> filterListType =new ArrayList<>();
        for (TypeProduct type : listType) {
            if(type.getNameType().toLowerCase().contains(text.toLowerCase())){
                filterListType.add(type);
            }
        }
        if(!filterListType.isEmpty()){
            typeAdapter.setFilterListType(filterListType);
            binding.listsTypeProduct.setVisibility(View.VISIBLE);
            binding.layoutNotifiNullData.setVisibility(View.GONE);
        }else{
            binding.listsTypeProduct.setVisibility(View.GONE);
            binding.layoutNotifiNullData.setVisibility(View.VISIBLE);
            binding.tvContentNull.setText("Không có loại sản phẩm "+"\"" +text+"\"");
        }
    }

    private void dialogFunctionProduct(Context context, TypeProduct typeProduct) {
        final Dialog dialogFunction = new Dialog(context);
        DialogFunctionProductBinding bindingDialog = DialogFunctionProductBinding.inflate(LayoutInflater.from(context));
        dialogFunction.setContentView(bindingDialog.getRoot());
        helpers.setLayoutDialog(dialogFunction,Gravity.BOTTOM,WindowManager.LayoutParams.WRAP_CONTENT);
        bindingDialog.dialogChooserFunction.setTranslationY(150);
        bindingDialog.dialogChooserFunction.animate().translationYBy(-150).setDuration(400);

        bindingDialog.tvFun1.setText("Sửa thông tin loại sản phẩm");
        bindingDialog.tvFun2.setText("Xóa loại sản phẩm");


        bindingDialog.tvFun1.setOnClickListener(tv ->{
            final Dialog dialogChange = new Dialog(context);
            DialogAddTypeProductBinding bindingChange = DialogAddTypeProductBinding.inflate(LayoutInflater.from(context));
            dialogChange.setContentView(bindingChange.getRoot());
            dialogChange.setCancelable(false);
            helpers.setLayoutDialog(dialogChange,0,WindowManager.LayoutParams.WRAP_CONTENT);
            bindingChange.tvTitle.setText("Sửa thông tin");
            bindingChange.tvAdd.setText("Lưu");
            bindingChange.edNameType.setText(typeProduct.getNameType());
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("list_type_product");

            bindingChange.tvAdd.setOnClickListener(tv2 ->{
                if(helpers.isEmptyString(bindingChange.edNameType.getText().toString())){
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
            getConfirmResponse(context, "Xác nhận", R.drawable.ic_delete, "Xác nhận xóa "+typeProduct.getNameType(), "Đồng ý",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
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

                    }
                }, "Hủy", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(getContext(),"Đã hủy !",Toast.LENGTH_SHORT).show();
                        dialogInterface.cancel();
                        dialogFunction.cancel();
                    }
                });

        });
        dialogFunction.show();

    }


    @Override
    public void onClickTypeProduct(TypeProduct typeProduct) {
        replaceFragment( new ProductFragment(typeProduct));
    }

    @Override
    public void onLongClickTypeProduct(TypeProduct typeProduct) {
        if(user.isUserAuthorization()){
            dialogFunctionProduct(getContext(),typeProduct);
        }else {
            helpers.notificationErrInput(getContext(),"Bạn không thể chỉnh sửa loại sản phẩm!");
        }

    }
}