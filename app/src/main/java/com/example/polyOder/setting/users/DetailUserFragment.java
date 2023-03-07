package com.example.polyOder.setting.users;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.polyOder.MainActivity;
import com.example.polyOder.R;
import com.example.polyOder.base.BaseFragment;
import com.example.polyOder.databinding.LayoutChangeProfileBinding;
import com.example.polyOder.model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class DetailUserFragment extends BaseFragment {
    private LayoutChangeProfileBinding binding = null;
    private User userData = null;


    public DetailUserFragment(User user){
        this.userData = user;
    }

    public DetailUserFragment newInstance() {
        return new DetailUserFragment(userData);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = LayoutChangeProfileBinding.inflate(inflater,container,false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setColorStatusBar(getActivity().getColor(R.color.white));
        hideBottomBar();
        getImageObjFromStorageReference("avatars",userData.getId(),binding.imgAvatar );
        listening();
        initObSever();

    }

    @Override
    public void loadData() {
        showUserProfile();
        binding.tvTitle.setText("Chi tiết nhân viên");
        binding.icSave.setVisibility(View.GONE);
        binding.icAddImg.setVisibility(View.GONE);
        binding.icChooserBirth.setVisibility(View.GONE);
        binding.edName.setFocusable(false);
        binding.edPhone.setFocusable(false);
        binding.edEmail.setFocusable(false);
        binding.edBirth.setFocusable(false);
        binding.edAddress.setFocusable(false);
        for (int i = 0; i <   binding.rdoGroupSex.getChildCount(); i++) {
            binding.rdoGroupSex.getChildAt(i).setEnabled(false);
        }

    }

    @Override
    public void listening() {
        binding.icBack.setOnClickListener(v -> {
            backStack();
        });

    }


    @Override
    public void initObSever() {

    }

    @Override
    public void initView() {

    }


    private void showUserProfile(){
        DatabaseReference mRef = FirebaseDatabase.getInstance().getReference();
        mRef.child("users").child(userData.getId()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userData = snapshot.getValue(User.class);
                binding.edName.setText(userData.getName_user());
                binding.edPhone.setText(userData.getPhone_number());
                binding.edEmail.setText(userData.getEmail());
                binding.edAddress.setText(userData.getAddress());
                binding.edBirth.setText(userData.getBirthday());
                if(userData.getSex()){
                    binding.rdoMale.setChecked(true);
                }else {
                    binding.rdoFemale.setChecked(true);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }







}