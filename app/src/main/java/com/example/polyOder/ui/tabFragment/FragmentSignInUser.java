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
import com.example.polyOder.databinding.LayoutSignInUserBinding;
import com.example.polyOder.model.Token;
import com.example.polyOder.model.User;
import com.example.polyOder.ui.ForgotPasswordActivity;
import com.example.polyOder.ui.SignInActivity;
import com.example.polyOder.ui.SignUpActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

public class FragmentSignInUser extends Fragment {
    private LayoutSignInUserBinding binding;
    private SignInActivity signInActivity;
    private Helpers helpers = new Helpers();

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

        binding.tvForgotPass.setOnClickListener(v -> {
            startActivity(new Intent(getContext(), ForgotPasswordActivity.class));
        });

        binding.tvSignUp.setOnClickListener(v -> {
            startActivity(new Intent(getContext(), SignUpActivity.class));
        });

        binding.btnLogin.setOnClickListener(v -> {
            if (helpers.isInternetConnect(getContext())) {
                helpers.getDialogNoInternet(getContext(), false);
                if (validate()) {
                    signIn();
                }
            }else{
                helpers.getDialogNoInternet(getContext(), true);
            }
        });

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

    private void signIn() {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        String strEmail = binding.email.getText().toString().trim();
        String strPass = binding.password.getText().toString().trim();
        DatabaseReference mRef = FirebaseDatabase.getInstance().getReference("users");
        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                    User user = dataSnapshot.getValue(User.class);
                    if(user.getEmail().equals(strEmail)){
                       mAuth.signInWithEmailAndPassword(strEmail, strPass).addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                               @Override
                               public void onComplete(@NonNull Task<AuthResult> task) {
                                   if (task.isSuccessful()) {
                                       startActivity(new Intent(getContext(), MainActivity.class));
                                       getToken();
                                       Toast.makeText(getContext(), getString(R.string.notifi_login_success), Toast.LENGTH_SHORT).show();
                                       signInActivity.showLoading();
                                       getActivity().finish();
                                   } else {
                                       helpers.notificationSuccessInput(getContext(), getString(R.string.notifi_login_fail));
                                       signInActivity.hideLoading();
                                   }
                               }
                           });
                       }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }



    private void getToken() {
        String firebaseAuth = FirebaseAuth.getInstance().getUid();
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
            @Override
            public void onComplete(@NonNull Task<String> task) {

                String token = task.getResult();
                Token token1 = new Token(token);
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
                reference.child("Tokens").child(firebaseAuth).setValue(token1).addOnCompleteListener(new OnCompleteListener<Void>() {
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
        if(helpers.isMatcherRegex(strEmail,"lucanhtai1504@gmail.com" ) && helpers.isMatcherRegex(strPass,"Lucanhtai15")){
            helpers.notificationErrInput(getContext(), getString(R.string.notifi_login_fail));
            return false;
        } else if (helpers.isEmptyString(strEmail) ) {
            helpers.notificationErrInput(getContext(), getString(R.string.error_email_1));
            binding.email.requestFocus();
            return false;
        } else if (!helpers.isEmailMatcher(strEmail)) {
            helpers.notificationErrInput(getContext(), getString(R.string.error_email_2));
            binding.email.requestFocus();
            return false;
        } else if (helpers.isEmptyString(strPass)) {
            helpers.notificationErrInput(getContext(), getString(R.string.error_pass_1));
            binding.password.requestFocus();
            return false;
        } else {
            return true;
        }
    }




}
