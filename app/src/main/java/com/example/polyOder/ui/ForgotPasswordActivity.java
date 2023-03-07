package com.example.polyOder.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.polyOder.R;
import com.example.polyOder.base.Helpers;
import com.example.polyOder.databinding.ActivityForgotPasswordBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPasswordActivity extends AppCompatActivity {
    private ActivityForgotPasswordBinding binding = null;
    private SignInActivity signInActivity;
    private Helpers helpers = new Helpers();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityForgotPasswordBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setBindingAnimation();
        signInActivity = new SignInActivity();
        binding.cavBack.setOnClickListener(cav ->{
            onBackPressed();
        });

        binding.btnForgotPass.setOnClickListener(btn ->{
            if (helpers.isInternetConnect(this)) {
                helpers.getDialogNoInternet(this, false);
                if(validateEmail()){
                    sendEmail();
                }
            }else{
                helpers.getDialogNoInternet(this, true);
            }

        });
    }

    private void sendEmail(){
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        binding.progressBar.setVisibility(View.VISIBLE);
        binding.layoutForgot.setAlpha(0.2f);
        String email = binding.edEmailForgot.getText().toString();
        firebaseAuth.sendPasswordResetEmail(email).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(ForgotPasswordActivity.this, getString(R.string.notifi_forgot_pass_success), Toast.LENGTH_SHORT).show();
                        binding.progressBar.setVisibility(View.VISIBLE);
                        onBackPressed();
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(ForgotPasswordActivity.this, getString(R.string.notifi_forgot_pass_fail), Toast.LENGTH_SHORT).show();
                        binding.progressBar.setVisibility(View.GONE);
                        binding.layoutForgot.setAlpha(1f);
                    }
                });


    }


    private boolean validateEmail(){
        String strEmail = binding.edEmailForgot.getText().toString().trim();
        if(helpers.isEmptyString(strEmail)){
            helpers.notificationErrInput(this,getString(R.string.error_email_1) );
            binding.edEmailForgot.requestFocus();
            return false;
        }else if(!(helpers.isEmailMatcher(strEmail))) {
            helpers.notificationErrInput(this,getString(R.string.error_email_2) );
            binding.edEmailForgot.requestFocus();
            return false;
        }else {
            return true;
        }
    }

    public  void setBindingAnimation(){
        helpers.isAnimationView(binding.cavBack,"translationX",1500, 300f, 0f);
        helpers.isAnimationView(binding.tvTitle,"translationX",1500, 400f, 0f);
        helpers.isAnimationView(binding.cavImg,"translationY",1500, -400f, 0f);
        helpers.isAnimationView(binding.tvContent,"translationX",1500, 400f, 0f);
        helpers.isAnimationView(binding.tilEmail,"translationX",1500, -400f, 0f);
        helpers.isAnimationView(binding.cavButton,"translationY",1500, 300f, 0f);

    }




}