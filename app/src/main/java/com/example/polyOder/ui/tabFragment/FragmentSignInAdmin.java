package com.example.polyOder.ui.tabFragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.polyOder.MainActivity;
import com.example.polyOder.R;
import com.example.polyOder.base.Helpers;
import com.example.polyOder.databinding.LayoutSignInAdminBinding;
import com.example.polyOder.model.Token;
import com.example.polyOder.ui.SignInActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;

public class FragmentSignInAdmin extends Fragment {
    private LayoutSignInAdminBinding binding;
    private SignInActivity signInActivity;
    private Helpers helpers = new Helpers();
    public  final  String  EMAIL_ADMIN = "lucanhtai1504@gmail.com";
    public  final String PASS_ADMIN=  "Lucanhtai15";

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

        binding.btnLogin.setOnClickListener(button ->{
            if (helpers.isInternetConnect(getContext())) {
                helpers.getDialogNoInternet(getContext(), false);
                isSignInAdmin();
            } else {
                helpers.getDialogNoInternet(getContext(), true);
            }

        });
    }


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        signInActivity = (SignInActivity) context;
    }

    private void isSignInAdmin(){
        if(binding.edLoginCode.getText().toString().equals("1111")){
            FirebaseAuth mAuth = FirebaseAuth.getInstance();
            mAuth.signInWithEmailAndPassword(EMAIL_ADMIN, PASS_ADMIN)
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
            helpers.notificationErrInput(getContext(), "Sai mã đăng nhập, hãy kiểm tra lại!");

        }
    }



    private void getToken() {
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
            @Override
            public void onComplete(@NonNull Task<String> task) {
                //Token
                String token = task.getResult();
                Token token1 = new Token(token);
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
                reference.child("Tokens").child(FirebaseAuth.getInstance().getUid()).setValue(token1).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                    }
                });
            }
        });
    }

}
