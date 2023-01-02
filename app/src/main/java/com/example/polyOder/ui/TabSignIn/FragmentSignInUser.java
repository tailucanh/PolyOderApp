package com.example.polyOder.ui.TabSignIn;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.polyOder.MainActivity;
import com.example.polyOder.R;
import com.example.polyOder.base.BaseFragment;
import com.example.polyOder.databinding.FragmentFinancialOverviewBinding;
import com.example.polyOder.databinding.LayoutSignInUserBinding;
import com.example.polyOder.model.Token;
import com.example.polyOder.ui.ForgotPasswordActivity;
import com.example.polyOder.ui.SignInActivity;
import com.example.polyOder.ui.SignUpActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;

public class FragmentSignInUser extends BaseFragment {
    private LayoutSignInUserBinding binding;
    SignInActivity signInActivity;



    public FragmentSignInUser() {
        // Required empty public constructor
    }

    public static FragmentSignInUser newInstance() {
        FragmentSignInUser fragment = new FragmentSignInUser();
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
        binding = LayoutSignInUserBinding.inflate(inflater, container, false);
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
        binding.tvForgotPass.setOnClickListener(v -> {
            startActivity(new Intent(getContext(), ForgotPasswordActivity.class));
        });

        binding.tvSignUp.setOnClickListener(v -> {
            startActivity(new Intent(getContext(), SignUpActivity.class));
        });

        binding.btnLogin.setOnClickListener(v -> {
            if (validate()) {
                onClickSignIn();
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
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        signInActivity = (SignInActivity) context;
    }

    private void onClickSignIn() {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        String strEmail = binding.email.getText().toString().trim();
        String strPass = binding.password.getText().toString().trim();


        mAuth.signInWithEmailAndPassword(strEmail, strPass)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            startActivity(new Intent(getContext(), MainActivity.class));
                            getToken();
                            Toast.makeText(getContext(), getString(R.string.notifi_login_success), Toast.LENGTH_SHORT).show();
                            signInActivity.showLoading();
                            getActivity().finish();
                        } else {
                            Toast.makeText(getContext(), getString(R.string.notifi_login_fail), Toast.LENGTH_SHORT).show();
                            signInActivity.hideLoading();
                        }
                    }
                });
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

    private boolean validate() {
        String strEmail = binding.email.getText().toString().trim();
        String strPass = binding.password.getText().toString().trim();
        if(strEmail.matches("lucanhtai1504@gmail.com") && strPass.matches("Lucanhtai15")){
            Toast.makeText(getContext(), getString(R.string.notifi_login_fail), Toast.LENGTH_SHORT).show();
            return false;
        } else if (TextUtils.isEmpty(strEmail)) {
            binding.email.setError(getString(R.string.error_email_1), null);
            binding.email.requestFocus();
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(strEmail).matches()) {
            binding.email.setError(getString(R.string.error_email_2), null);
            binding.email.requestFocus();
            return false;
        } else if (TextUtils.isEmpty(strPass)) {
            binding.password.setError(getString(R.string.error_pass_1), null);
            binding.password.requestFocus();
            return false;
        } else {
            return true;
        }
    }




}
