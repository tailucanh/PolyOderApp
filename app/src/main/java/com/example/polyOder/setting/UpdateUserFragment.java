package com.example.polyOder.setting;

import static android.app.Activity.RESULT_OK;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.DatePicker;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.polyOder.MainActivity;
import com.example.polyOder.R;
import com.example.polyOder.base.BaseFragment;
import com.example.polyOder.databinding.LayoutChangeProfileBinding;
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

import java.util.Calendar;
import java.util.Date;

public class UpdateUserFragment extends BaseFragment {
    private LayoutChangeProfileBinding binding = null;
    private User userData = null;
    private FirebaseAuth firebaseAuth;
    private Uri avatar;
    private static final int PICL_IMAGES_CODE = 1000;
    FirebaseUser firebaseUser;
    Calendar calendar;
    final String rexPhone = "(84|0[3|5|7|8|9])+([0-9]{8})\\b";
    MainActivity mainActivity;


    public UpdateUserFragment newInstance(User user) {
        this.userData = user;
        return new UpdateUserFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = LayoutChangeProfileBinding.inflate(inflater,container,false);
//        Log.d("TAG", "onCreateView: "+ userData.getId());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Window window = getActivity().getWindow();
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        window.setStatusBarColor(getActivity().getColor(R.color.white));
        mainActivity.hideBottomBar();
        userData = new User();
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        StorageReference reference = FirebaseStorage.getInstance().getReference().child("avatars");
        reference.listAll().addOnSuccessListener(listResult -> {
            for (StorageReference files: listResult.getItems()) {
                if (files.getName().equals(firebaseUser.getUid())){
                    files.getDownloadUrl().addOnSuccessListener(uri -> {
                        if(getActivity() != null){
                            Glide.with(getActivity()).load(uri).into(binding.imgAvatar);
                        }
                    });
                }
            }
        });
        listening();
        initObSever();


    }

    @Override
    public void loadData() {
        showUserProfile();
    }

    @Override
    public void listening() {
        binding.icBack.setOnClickListener(v -> {
            backStack();
        });

        binding.icChooserBirth.setOnClickListener(v -> {
            dialogChooseBirth();
        });

        binding.icAddImg.setOnClickListener(v -> {
            requestPermission();
        });

        binding.icSave.setOnClickListener(v -> {
            if(TextUtils.isEmpty(binding.edName.getText().toString())){
                notificationErrInput(getContext(),"Hãy nhập tên của bạn");
            } else if(!binding.edPhone.getText().toString().matches(rexPhone) && binding.edPhone.getText().toString().length() >0){
                notificationErrInput(getContext(),"Hãy nhập số điện thoại đúng định dạng");
            }else {
                dialogConfirmUpdate(getContext());
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

    private void showUserProfile(){
        String userID = firebaseUser.getUid();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.child("users").child(userID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userData = snapshot.getValue(User.class);
                if(firebaseUser != null) {
                    binding.edName.setText(userData.getName_user());
                    binding.edPhone.setText(userData.getPhone_number());
                    binding.edEmail.setText(firebaseUser.getEmail());
                    binding.edAddress.setText(userData.getAddress());
                    binding.edBirth.setText(userData.getBirthday());
                    if(userData.getSex()){
                        binding.rdoMale.setChecked(true);
                    }else {
                        binding.rdoFemale.setChecked(true);
                    }

                }else {
                    Toast.makeText(getContext(), "Lỗi", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Something Wrong!!!", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void dialogChooseBirth(){
        calendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(),
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        String strDay = "";
                        String strMonths = "";
                        if(dayOfMonth < 10){
                            strDay = "0" + dayOfMonth;
                        }else{
                            strDay = String.valueOf(dayOfMonth);
                        }
                        if((month + 1) < 10){
                            strMonths = "0" + (month + 1);
                        }else{
                            strMonths = String.valueOf(month + 1);
                        }
                        binding.edBirth.setText(strDay + "/" + strMonths + "/" + year);
                    }
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.getDatePicker().setMaxDate(new Date().getTime());
        datePickerDialog.show();
    }

    private void updateUserProfile(){
        firebaseUser = firebaseAuth.getCurrentUser();
        String userID = firebaseUser.getUid();
        if (avatar != null) {
            StorageReference reference = FirebaseStorage.getInstance().getReference("avatars/"+userID);
            reference.putFile(avatar).addOnSuccessListener(taskSnapshot -> {
            }).addOnFailureListener(command -> {
                Toast.makeText(requireContext(), "Cập nhật ảnh đại diện thất bại", Toast.LENGTH_SHORT).show();
            });
        }

        int checkedRadioId = binding.rdoGroupSex.getCheckedRadioButtonId();
        if(checkedRadioId == R.id.rdoMale){
            userData.setSex(true);
        }else{
            userData.setSex(false);
        }


        userData = new User(binding.edName.getText().toString().trim(), userData.getSex(), binding.edPhone.getText().toString().trim(), binding.edBirth.getText().toString().trim(),
                binding.edAddress.getText().toString().trim(), binding.edEmail.getText().toString().trim());
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
            databaseReference.child("users").child(userID).updateChildren(userData.toMap(),
                    new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                    if (firebaseUser != null) {
                        notificationSuccessInput(getContext(),"Sửa thông tin thành công");
                    }
                }
            });
    }


    private void dialogConfirmUpdate(Context context){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Cập nhật thông tin cá nhân");
        builder.setIcon(context.getDrawable(R.drawable.ic_save));
        builder.setMessage("Bạn chắc chắn muốn thay đổi thông tin cá nhân");
        builder.setCancelable(false);
        builder.setPositiveButton("Đồng ý", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(firebaseUser.getEmail() != binding.edEmail.getText().toString()){
                    updateUserProfile();
                }
                dialog.cancel();
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
                binding.imgAvatar.setImageURI(avatar);
                Log.d("TAG", "onActivityResult: "+data.getData());

            }
        }
    }


}