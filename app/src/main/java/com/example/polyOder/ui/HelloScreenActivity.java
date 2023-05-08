package com.example.polyOder.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;

import com.example.polyOder.MainActivity;
import com.example.polyOder.R;
import com.example.polyOder.databinding.ActivityHelloScreenBinding;
import com.example.polyOder.databinding.ActivitySignInBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.time.LocalDate;

public class HelloScreenActivity extends AppCompatActivity {
    private ActivityHelloScreenBinding binding = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHelloScreenBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        objAni(binding.icLogo,"translationY", -250f, 0f);
        objAni(binding.text1,"translationY", 200f, 0f);
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                Intent intent;
                if(user == null){
                    intent = new Intent(HelloScreenActivity.this, SignInActivity.class);
                } else {
                    intent = new Intent(HelloScreenActivity.this, MainActivity.class);
                }
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
                finish();
            }
        },2700);
        LocalDate now = LocalDate.now();
        int year = now.getYear();
        binding.textFooter.setText("Copy right ©2022 - " +year+". All rights reserved.");


    }



    private void objAni(View view, String ani, float... values){
        ObjectAnimator animator = ObjectAnimator.ofFloat(view,ani,values);
        animator.setDuration(1500);
        animator.start();
    }
}