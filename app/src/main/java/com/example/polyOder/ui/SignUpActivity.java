package com.example.polyOder.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import com.example.polyOder.R;
import com.example.polyOder.base.Helpers;

import com.example.polyOder.databinding.ActivitySignUpBinding;
import com.example.polyOder.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignUpActivity extends AppCompatActivity   {
    private ActivitySignUpBinding binding = null;
    private Helpers helpers = new Helpers();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignUpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Window window = getWindow();
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        window.setStatusBarColor(getColor(R.color.brown_120));
        setBindingAnimation(binding);

        binding.icBack.setOnClickListener(ic -> {
            onBackPressed();
        });

        binding.btnSignup.setOnClickListener(v -> {
            if (helpers.isInternetConnect(this)) {
                helpers.getDialogNoInternet(this, false);
                if (validateInput()) {
                    createUser();
                }
            }else{
                helpers.getDialogNoInternet(this, true);
            }

        });


    }
    private void createUser(){
        binding.progressBar.setVisibility(View.VISIBLE);
        binding.layoutSignUp.setAlpha(0.2f);
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(binding.email.getText().toString(), binding.pass.getText().toString())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            User user = new User(FirebaseAuth.getInstance().getCurrentUser().getUid(), binding.name.getText().toString(),
                                    null, "", "", "", binding.pass.getText().toString(),
                                    binding.email.getText().toString(),false);
                            DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
                            reference.child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .setValue(user)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Toast.makeText(SignUpActivity.this, getString(R.string.notifi_sign_up_success), Toast.LENGTH_SHORT).show();
                                                startActivity(new Intent(SignUpActivity.this, SignInActivity.class));
                                                binding.progressBar.setVisibility(View.VISIBLE);
                                                finishAffinity();
                                            }
                                        }
                                    });
                        } else {
                            helpers.notificationSuccessInput(SignUpActivity.this, getString(R.string.notifi_sign_up_fail));
                            binding.progressBar.setVisibility(View.GONE);
                            binding.layoutSignUp.setAlpha(1f);
                        }
                    }
                });
    }

    private boolean validateInput() {
        String strName = binding.name.getText().toString().trim();
        String strEmail = binding.email.getText().toString().trim();
        String strPass = binding.pass.getText().toString().trim();
        String strRePass = binding.rePass.getText().toString().trim();
        if (helpers.isEmptyString(strName)) {
            helpers.notificationErrInput(this,getString(R.string.error_name));
            binding.name.requestFocus();
            return false;
        } else if (helpers.isEmptyString(strEmail)) {
            helpers.notificationErrInput(this,getString(R.string.error_email_1));
            binding.email.requestFocus();
            return false;
        } else if (!(helpers.isEmailMatcher(strEmail))) {
            helpers.notificationErrInput(this,getString(R.string.error_email_2));
            binding.email.requestFocus();
            return false;
        } else if (helpers.isEmptyString(strPass)) {
            helpers.notificationErrInput(this,getString(R.string.error_pass_1));
            binding.pass.requestFocus();
            return false;
        } else if (!(helpers.isMatcherRegex(strPass,strRePass)) || helpers.isEmptyString(strRePass)) {
            helpers.notificationErrInput(this,getString(R.string.error_pass_2));
            binding.rePass.requestFocus();
            return false;
        } else {
            return true;
        }

    }

    private void setBindingAnimation(ActivitySignUpBinding binding) {
        helpers.isAnimationView(binding.icBack, "translationX", 1800,-400f, 0f);
        helpers.isAnimationView(binding.icLogo, "translationX", 1800, 400f, 0f);
        helpers.isAnimationView(binding.tvIcon, "translationX", 1800, -400f, 0f);
        helpers.isAnimationView(binding.tvContent, "translationY", 1800, -400f, 0f);
        helpers.isAnimationView(binding.name, "translationX", 1800, 400f, 0f);
        helpers.isAnimationView(binding.email, "translationX", 1800, -400f, 0f);
        helpers.isAnimationView(binding.tilPass, "translationX", 1800, 400f, 0f);
        helpers.isAnimationView(binding.tilRePass, "translationX", 1800, -400f, 0f);
        helpers.isAnimationView(binding.cavButton, "translationY", 1800, 400f, 0f);

    }

}













