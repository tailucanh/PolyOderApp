package com.example.polyOder.product;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.example.polyOder.MainActivity;
import com.example.polyOder.R;
import com.example.polyOder.base.BaseFragment;
import com.example.polyOder.base.Helpers;

import com.example.polyOder.databinding.FragmentDetailsProductBinding;
import com.example.polyOder.databinding.LayoutFullImageProductBinding;
import com.example.polyOder.model.Product;
import com.example.polyOder.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.NumberFormat;
import java.util.Locale;

public class DetailProductFragment extends BaseFragment {

    private FragmentDetailsProductBinding binding = null;
    private Product dataProduct = null;
    private Helpers helpers = new Helpers();

    public DetailProductFragment(Product product) {
        this.dataProduct = product;

    }

    public DetailProductFragment() {
    }

    public DetailProductFragment newInstance() {
        return new DetailProductFragment(dataProduct);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentDetailsProductBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        hideBottomBar();
        setColorStatusBar(getContext().getColor(R.color.white));
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
                    binding.btnDeleteProduct.setVisibility(View.GONE);
                    binding.icEditProduct.setVisibility(View.GONE);
                }else {
                    binding.btnDeleteProduct.setVisibility(View.VISIBLE);
                    binding.icEditProduct.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }
    @Override
    public void listening() {
        binding.btnDeleteProduct.setOnClickListener(v -> {
            dialogConfirmDelete(getContext());
        });
        binding.imgProduct.setOnClickListener(ic ->{
            StorageReference reference = FirebaseStorage.getInstance().getReference().child("imgProducts");
            reference.listAll().addOnSuccessListener(listResult -> {
                for (StorageReference files: listResult.getItems()){
                    if(files.getName().equals(dataProduct.getId())){
                        dialogFullImg(getContext());
                    }
                }
            });
        });

        binding.icEditProduct.setOnClickListener(v->{
            replaceFragment(new UpdateProductFragment(dataProduct));
        });
        binding.icBack.setOnClickListener(v->{
            backStack();
        });
    }

    @Override
    public void initObSever() {
        showDetailProduct();
    }

    @Override
    public void initView() {

    }

    private void dialogConfirmDelete(Context context){
        getConfirmResponse(context, "Xóa sản phẩm", R.drawable.ic_delete, "Bạn chắc chắn muốn xóa "+dataProduct.getNameProduct(), "Đồng ý", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dataProduct.setHidden(false);
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("list_product");
                reference.child(dataProduct.getId()).setValue(dataProduct).addOnCompleteListener(task->{
                    if (task.isSuccessful()){
                        helpers.notificationSuccessInput(getContext(),"Đã xóa sản phẩm!");
                        dialogInterface.cancel();
                        backStack();
                    }else {
                        helpers.notificationSuccessInput(getContext(),"Xóa không thành công!");
                        dialogInterface.cancel();
                    }
                });
            }
        }, "Hủy", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Toast.makeText(getContext(),"Đã hủy !",Toast.LENGTH_SHORT).show();
                dialogInterface.cancel();

            }
        });
    }

    private void showDetailProduct(){

        getImageObjFromStorageReference("imgProducts",dataProduct.getId(),binding.imgProduct);

        DatabaseReference reference2 = FirebaseDatabase.getInstance().getReference();
        reference2.child("list_product").child(dataProduct.getId()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                dataProduct = snapshot.getValue(Product.class);
                if (dataProduct == null) {
                    return;
                }

                String strPrice = helpers.isFormatMoney(dataProduct.getPrice());
                binding.tvNameProduct.setText(dataProduct.getNameProduct());
                binding.tvDescribe.setText(dataProduct.getDescribe());
                binding.tvTypeProduct.setText(dataProduct.getTypeProduct().getNameType());
                binding.tvPriceProduct.setText(strPrice+" đ");
                binding.tvNoteProduct.setText(dataProduct.getNote());
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void dialogFullImg(Context context){
        final Dialog dialog = new Dialog(context);
        LayoutFullImageProductBinding binding = LayoutFullImageProductBinding.inflate(LayoutInflater.from(context));
        dialog.setContentView(binding.getRoot());
        dialog.setCancelable(false);
        helpers.setLayoutDialog(dialog, Gravity.BOTTOM,WindowManager.LayoutParams.MATCH_PARENT);
        binding.layoutFullImg.setTranslationY(150);
        binding.layoutFullImg.animate().translationYBy(-150).setDuration(200);
        setColorStatusBar(getActivity().getColor(R.color.white));
        binding.icCloseDialog.setOnClickListener(ic ->{
            dialog.cancel();
        });
        getImageObjFromStorageReference("imgProducts",dataProduct.getId(),binding.imgFullProduct);

        dialog.show();
    }
}