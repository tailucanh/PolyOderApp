package com.example.polyOder.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import com.example.polyOder.R;
import com.example.polyOder.databinding.ActivityForgotPasswordBinding;
import com.example.polyOder.databinding.ActivitySignInBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPasswordActivity extends AppCompatActivity {
    private ActivityForgotPasswordBinding binding = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityForgotPasswordBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setBindingAnimation();

        binding.cavBack.setOnClickListener(cav ->{
            onBackPressed();
        });

        binding.btnForgotPass.setOnClickListener(btn ->{
            if(validateEmail()){
                sendEmail();
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

        if(TextUtils.isEmpty(strEmail)){
            binding.edEmailForgot.setError(getString(R.string.error_email_1),null);
            binding.edEmailForgot.requestFocus();
            return false;
        }else if(!Patterns.EMAIL_ADDRESS.matcher(strEmail).matches()) {
            binding.edEmailForgot.setError(getString(R.string.error_email_2),null);
            binding.edEmailForgot.requestFocus();
            return false;
        }else {
            return true;
        }
    }

    public  void setBindingAnimation(){
        viewAnimation(binding.cavBack,"translationX", 300f, 0f);
        viewAnimation(binding.tvTitle,"translationX", 400f, 0f);
        viewAnimation(binding.cavImg,"translationY", -400f, 0f);
        viewAnimation(binding.tvContent,"translationX", 400f, 0f);
        viewAnimation(binding.tilEmail,"translationX", -400f, 0f);
        viewAnimation(binding.cavButton,"translationY", 300f, 0f);

    }



    public  void viewAnimation(View view, String ani, float... values){
        ObjectAnimator animator = ObjectAnimator.ofFloat(view,ani,values);
        animator.setDuration(1500);
        animator.start();
    }
}