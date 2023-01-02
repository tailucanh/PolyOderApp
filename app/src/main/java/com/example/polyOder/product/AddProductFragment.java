package com.example.polyOder.product;

import static android.app.Activity.RESULT_OK;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.polyOder.MainActivity;
import com.example.polyOder.R;
import com.example.polyOder.base.BaseFragment;
import com.example.polyOder.databinding.DialogEvaluateBinding;
import com.example.polyOder.databinding.FragmentAddProductBinding;
import com.example.polyOder.databinding.FragmentTypeProductBinding;
import com.example.polyOder.model.Product;
import com.example.polyOder.model.TypeProduct;
import com.example.polyOder.product.Adapter.SpinnerTypeProductAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;


public class AddProductFragment extends BaseFragment {
    private FragmentAddProductBinding binding;
    private ArrayList<TypeProduct> listTypeProduct;
    private static final int PICL_IMAGES_CODE = 1001;
    private Uri imgProduct;
    MainActivity mainActivity;


    public AddProductFragment() {
        // Required empty public constructor
    }

    public static AddProductFragment newInstance() {
        AddProductFragment fragment = new AddProductFragment();
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
        // Inflate the layout for this fragment
        binding = FragmentAddProductBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        loadData();
        listening();
        binding.icSaveProduct.setOnClickListener(v -> {
            if(checkInputData()){
                saveProduct(getContext());
            }

        });
        binding.btnSaveProduct.setOnClickListener(v -> {
            if(checkInputData()){
                saveProduct(getContext());
            }
        });
        mainActivity.hideBottomBar();
    }

    @Override
    public void loadData() {
        listTypeProduct = new ArrayList<>();
        DatabaseReference mRef = FirebaseDatabase.getInstance().getReference("list_type_product");
        mRef.addValueEventListener(new ValueEventListener() {
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
        binding.icClose.setOnClickListener(v -> {
          backStack();
        });



        binding.icAddImg.setOnClickListener(v -> {
            requestPermission();
        });

    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mainActivity = (MainActivity)context;
    }

    @Override
    public void initObSever() {

    }

    @Override
    public void initView() {

    }


    private void saveProduct(Context context){
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("list_product");
            String key = reference.push().getKey();
            TypeProduct typeProduct = (TypeProduct) binding.spinnerType.getSelectedItem();
            Product product = new Product(key,binding.edNameProduct.getText().toString().trim(),binding.edDescribe.getText().toString().trim(),typeProduct,
                    Double.parseDouble(binding.edPrice.getText().toString().trim()), binding.edNote.getText().toString().trim(),true);
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("Thêm sản phẩm");
            builder.setMessage("Bạn chắc chắn muốn thêm " + binding.edNameProduct.getText().toString().trim() + " vào menu");
            builder.setIcon(context.getDrawable(R.drawable.ic_save));
            builder.setCancelable(false);
            builder.setPositiveButton("Đồng ý", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    reference.child(key).setValue(product).addOnCompleteListener(task -> {
                        if (task.isSuccessful()){
                            notificationSuccessInput(getContext(),"Thêm thành công!");
                            backStack();
                        }else {
                            notificationErrInput(getContext(),"Thêm thất bại");
                        }
                    });
                if(imgProduct != null){
                    StorageReference storageReference = FirebaseStorage.getInstance().getReference("imgProducts/"+product.getId());
                    storageReference.putFile(imgProduct).addOnSuccessListener(taskSnapshot -> {
                    }).addOnFailureListener(command -> {
                        Toast.makeText(getContext(), "Cập nhật ảnh thất bại", Toast.LENGTH_SHORT).show();
                    });
                }
                cleanEditText();
            }
        });
        builder.setNegativeButton("Hủy", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(context,"Đã hủy !",Toast.LENGTH_SHORT).show();
                dialog.cancel();
            }
        });

            AlertDialog sh = builder.create();
            sh.show();

    }


    private boolean checkInputData(){
        if(TextUtils.isEmpty(binding.edNameProduct.getText().toString())){
            notificationErrInput(getContext(),"Hãy nhập tên sản phẩm!");
            return false;
        }else if(binding.spinnerType.getSelectedItem() == null) {
            notificationErrInput(getContext(),"Hãy thêm loại sản phẩm!");
            replaceFragment(new TypeProductFragment().newInstance());
            return false;
        } else if(TextUtils.isEmpty(binding.edPrice.getText().toString())){
            notificationErrInput(getContext(),"Hãy nhập giá sản phẩm!");
            return false;
        }else{
            return true;
        }
    }


    private void cleanEditText(){
        binding.edNameProduct.setText("");
        binding.edDescribe.setText("");
        binding.edPrice.setText("");
        binding.edNote.setText("");
        binding.tvAddImgProduct.setVisibility(View.VISIBLE);
        if(getActivity() != null){
            Glide.with(getActivity()).load(R.drawable.ic_product).into(binding.imgProduct);
        }

    }

    private void requestPermission() {
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
            Log.d("TAG", "requestPermission: 11111111111");
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
                imgProduct = data.getData();
                binding.imgProduct.setImageURI(imgProduct);
                if(imgProduct != null){
                    binding.tvAddImgProduct.setVisibility(View.INVISIBLE);
                }

            }
        }
    }

}