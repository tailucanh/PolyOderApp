package com.example.polyOder.product;

import static android.app.Activity.RESULT_OK;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.polyOder.MainActivity;
import com.example.polyOder.R;
import com.example.polyOder.base.BaseFragment;
import com.example.polyOder.base.Helpers;
import com.example.polyOder.databinding.DialogFunctionProductBinding;
import com.example.polyOder.databinding.FragmentEditProductBinding;
import com.example.polyOder.databinding.LayoutFullImageProductBinding;
import com.example.polyOder.model.Product;
import com.example.polyOder.model.TypeProduct;
import com.example.polyOder.product.adapter.SpinnerTypeProductAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class UpdateProductFragment extends BaseFragment {
    private static final int PICL_IMAGES_CODE = 1000;
    private Uri avatar;
    private FragmentEditProductBinding binding = null;
    private Product dataProduct = null;
    private ArrayList<TypeProduct> listTypeProduct;
    private Helpers helpers = new Helpers();

    public UpdateProductFragment(Product product) {
        this.dataProduct = product;
    }

    public UpdateProductFragment newInstance() {
        return new UpdateProductFragment(dataProduct);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentEditProductBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        listTypeProduct = new ArrayList<>();

        hideBottomBar();
        listening();
        initObSever();

    }

    @Override
    public void loadData() {


       getDataFromFirebase("list_type_product",new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listTypeProduct.clear();
                for(DataSnapshot datasnapshot : snapshot.getChildren()){
                    TypeProduct type = datasnapshot.getValue(TypeProduct.class);
                    listTypeProduct.add(type);
                }
                SpinnerTypeProductAdapter spinnerAdapter = new SpinnerTypeProductAdapter(listTypeProduct);
                binding.spinnerType.setAdapter(spinnerAdapter);
                spinnerAdapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });


    }



    @Override
    public void listening() {
        binding.btnSaveUpdate.setOnClickListener(v -> {
            if(checkInputData()){
                updateProduct(getContext());
            }

        });
        binding.icSaveEditProduct.setOnClickListener(v -> {
            if(checkInputData()){
                updateProduct(getContext());
            }
        });

        binding.imgProduct.setOnClickListener(v->{
            dialogFunctionImage(getContext());
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



    private void updateProduct(Context context){
        if (avatar != null) {
            StorageReference reference = FirebaseStorage.getInstance().getReference("imgProducts/"+dataProduct.getId());
            reference.putFile(avatar).addOnSuccessListener(taskSnapshot -> {
            }).addOnFailureListener(command -> {
                Toast.makeText(requireContext(), "Cập nhật ảnh sản phẩm thất bại", Toast.LENGTH_SHORT).show();
            });
        }

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("list_product");
        TypeProduct typeProduct = (TypeProduct) binding.spinnerType.getSelectedItem();

        Product product = new Product(dataProduct.getId(),binding.edNameProduct.getText().toString().trim(),binding.edDescribe.getText().toString().trim(),typeProduct,
                Double.parseDouble(binding.edPrice.getText().toString().trim()), binding.edNote.getText().toString().trim(),dataProduct.isHidden());

        getConfirmResponse(context, "Cập nhật thông tin", R.drawable.ic_update, "Bạn chắc chắn muốn thay đổi thông tin sản phẩm", "Đồng ý",
            new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                reference.child(dataProduct.getId()).setValue(product).addOnCompleteListener(task->{
                    if (task.isSuccessful()){
                        helpers.notificationSuccessInput(getContext(),"Cập nhật thành công!");
                        backStack();
                        dialogInterface.cancel();
                    }else {
                        helpers.notificationSuccessInput(getContext(),"Cập nhật không thành công!");
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


    private void dialogFunctionImage(Context context) {
        final Dialog dialog = new Dialog(context);
        DialogFunctionProductBinding bindingDialog = DialogFunctionProductBinding.inflate(LayoutInflater.from(context));
        dialog.setContentView(bindingDialog.getRoot());
        helpers.setLayoutDialog(dialog, Gravity.BOTTOM,WindowManager.LayoutParams.WRAP_CONTENT);
        bindingDialog.dialogChooserFunction.setTranslationY(150);
        bindingDialog.dialogChooserFunction.animate().translationYBy(-150).setDuration(400);

        bindingDialog.tvFun1.setOnClickListener(v->{
            requestPermission();
            dialog.cancel();
        });
        bindingDialog.tvFun2.setOnClickListener(tv ->{
            dialogFullImg(getContext());
            dialog.cancel();
        });

        dialog.show();

    }

    private boolean checkInputData(){
        if(helpers.isEmptyString(binding.edNameProduct.getText().toString())){
            helpers.notificationErrInput(getContext(),"Hãy nhập tên sản phẩm!");
            return false;
        }else if(binding.spinnerType.getSelectedItem() == null) {
            helpers.notificationErrInput(getContext(),"Hãy thêm loại sản phẩm!");
            replaceFragment(new TypeProductFragment().newInstance());
            return false;
        } else if(helpers.isEmptyString(binding.edPrice.getText().toString())){
            helpers.notificationErrInput(getContext(),"Hãy nhập giá sản phẩm!");
            return false;
        }else{
            return true;
        }
    }

    private void showDetailProduct(){
        DatabaseReference reference2 = FirebaseDatabase.getInstance().getReference();
        reference2.child("list_product").child(dataProduct.getId()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                dataProduct = snapshot.getValue(Product.class);
                if (dataProduct == null) {
                    return;
                }
                binding.edNameProduct.setText(dataProduct.getNameProduct());
                binding.edDescribe.setText(dataProduct.getDescribe());
                binding.edPrice.setText(String.format("%.0f", dataProduct.getPrice()));
                binding.edNote.setText(dataProduct.getNote());

                listTypeProduct = new ArrayList<>();
               getDataFromFirebase("list_type_product", new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        listTypeProduct.clear();
                        for(DataSnapshot datasnapshot : snapshot.getChildren()){
                            TypeProduct type = datasnapshot.getValue(TypeProduct.class);
                            listTypeProduct.add(type);
                        }
                        for(int i =0;i<listTypeProduct.size();i++){
                            TypeProduct temp = listTypeProduct.get(i);
                            if(temp.getNameType().equals(dataProduct.getTypeProduct().getNameType())){
                                binding.spinnerType.setSelection(i);
                                binding.spinnerType.setSelected(true);
                            }
                        }

                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        getImageObjFromStorageReference("imgProducts",dataProduct.getId(),binding.imgProduct);
    }

    private void dialogFullImg(Context context){
        final Dialog dialog = new Dialog(context);
        LayoutFullImageProductBinding binding = LayoutFullImageProductBinding.inflate(LayoutInflater.from(context));
        dialog.setContentView(binding.getRoot());
        dialog.setCancelable(false);
        helpers.setLayoutDialog(dialog, Gravity.BOTTOM,WindowManager.LayoutParams.MATCH_PARENT);
        binding.layoutFullImg.setTranslationY(150);
        binding.layoutFullImg.animate().translationYBy(-150).setDuration(200);

        binding.icCloseDialog.setOnClickListener(ic ->{
            dialog.cancel();
        });
        getImageObjFromStorageReference("imgProducts",dataProduct.getId(),binding.imgFullProduct);

        dialog.show();
    }
    private void requestPermission(){
        if (ContextCompat.checkSelfPermission(requireContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(),
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {

            } else {
                ActivityCompat.requestPermissions(requireActivity(),
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        1);
            }

        } else {
            addImage();
        }
    }
    private void addImage() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICL_IMAGES_CODE);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == PICL_IMAGES_CODE) {
                avatar = data.getData();
                binding.imgProduct.setImageURI(avatar);
                Log.d("TAG", "onActivityResult: "+data.getData());
            }
        }
    }


}


