package com.example.polyOder.ui.TabSignIn;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.polyOder.MainActivity;
import com.example.polyOder.R;
import com.example.polyOder.base.BaseFragment;
import com.example.polyOder.databinding.LayoutSignInAdminBinding;
import com.example.polyOder.databinding.LayoutSignInUserBinding;
import com.example.polyOder.model.Token;
import com.example.polyOder.ui.SignInActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;

public class FragmentSignInAdmin extends BaseFragment {
    private LayoutSignInAdminBinding binding;
    SignInActivity signInActivity;


    public FragmentSignInAdmin() {
        // Required empty public constructor
    }

    public static FragmentSignInAdmin newInstance() {
        FragmentSignInAdmin fragment = new FragmentSignInAdmin();
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
        binding = LayoutSignInAdminBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);



        listening();
        initObSever();
    }


    @Override
    public void loadData() {

    }

    @Override
    public void listening() {
        binding.btnLogin.setOnClickListener(button ->{
            if(binding.edLoginCode.getText().toString().equals("1111")){
                FirebaseAuth mAuth = FirebaseAuth.getInstance();
                mAuth.signInWithEmailAndPassword("lucanhtai1504@gmail.com", "Lucanhtai15")
                        .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    startActivity(new Intent(getContext(), MainActivity.class));
                                    getToken();
                                    Toast.makeText(getContext(), "Chào mừng đến với trang quản trị viên!", Toast.LENGTH_SHORT).show();
                                    signInActivity.showLoading();
                                    getActivity().finish();
                                } else {
                                    Toast.makeText(getContext(), getString(R.string.notifi_login_fail), Toast.LENGTH_SHORT).show();
                                    signInActivity.hideLoading();

                                }
                            }
                        });


            }else {
                Toast.makeText(getContext(), "Sai mã đăng nhập, hãy kiểm tra lại!", Toast.LENGTH_SHORT).show();
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
        signInActivity = (SignInActivity) context;
    }

    private void getToken() {
        String firebaseAuth = FirebaseAuth.getInstance().getUid();
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
            @Override
            public void onComplete(@NonNull Task<String> task) {
                //If task is failed then
                if (!task.isSuccessful()) {
                    Log.d("TAG", "onComplete: Failed to get the Token");
                }
                //Token
                String token = task.getResult();
                Log.d("TAG", "onComplete: " + token);
                Token token1 = new Token(token);
                Log.d("TAG", "onComplete: "+firebaseAuth);
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
                reference.child("Tokens")
                        .child(firebaseAuth)
                        .setValue(token1).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                            }
                        });
            }
        });
    }

}
