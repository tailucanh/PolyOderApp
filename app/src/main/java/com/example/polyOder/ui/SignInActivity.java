package com.example.polyOder.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Bundle;
import android.view.View;
import android.view.Window;

import com.example.polyOder.R;

import com.example.polyOder.databinding.ActivitySignInBinding;
import com.example.polyOder.ui.adapter.AdapterTabLayoutSignIn;
import com.example.polyOder.ui.adapter.DepthPageTransformer;
import com.google.android.material.tabs.TabLayout;


public class SignInActivity extends AppCompatActivity {
    public ActivitySignInBinding binding = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignInBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Window window = getWindow();
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        window.setStatusBarColor(getColor(R.color.brown_120));


        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Nhân viên"));
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Quản lí"));

        binding.fragmentTab.setPageTransformer(new DepthPageTransformer());
        AdapterTabLayoutSignIn adapter = new AdapterTabLayoutSignIn(this);
        binding.fragmentTab.setAdapter(adapter);


        binding.tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                binding.fragmentTab.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        binding.fragmentTab.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                binding.tabLayout.selectTab(binding.tabLayout.getTabAt(position));
            }
        });

    }

    public  void showLoading(){
        binding.progressBar.setVisibility(View.VISIBLE);
        binding.layoutLogin.setAlpha(0.1f);
    }
    public  void hideLoading(){
        binding.progressBar.setVisibility(View.GONE);
        binding.layoutLogin.setAlpha(1f);
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishAffinity();
    }
}