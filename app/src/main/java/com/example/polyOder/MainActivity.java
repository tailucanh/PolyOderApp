package com.example.polyOder;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;


import android.os.Bundle;

import android.view.MenuItem;
import android.view.View;
import android.view.Window;

import com.example.polyOder.PushNotification.FMC;
import com.example.polyOder.databinding.ActivityMainBinding;
import com.example.polyOder.home.HomeFragment;
import com.example.polyOder.maket.MarketFragment;
import com.example.polyOder.model.Token;
import com.example.polyOder.product.ProductFragment;
import com.example.polyOder.setting.SettingFragment;
import com.example.polyOder.table.TableViewModel;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationBarView;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    public ActivityMainBinding binding = null;
    private TableViewModel model = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Window window = getWindow();
        getSupportFragmentManager().beginTransaction().replace(R.id.fade_control, HomeFragment.newInstance()).commit();
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        window.setStatusBarColor(getColor(R.color.white));


        binding.bottomNav.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.home_fragment:
                        getSupportFragmentManager().beginTransaction().replace(R.id.fade_control, HomeFragment.newInstance()).commit();
                        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
                        window.setStatusBarColor(getColor(R.color.white));
                        break;
                    case R.id.maket_fragment:
                        getSupportFragmentManager().beginTransaction().replace(R.id.fade_control, MarketFragment.newInstance()).commit();
                        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
                        window.setStatusBarColor(getColor(R.color.white));

                        break;
                    case R.id.menu_fragment:
                        getSupportFragmentManager().beginTransaction().replace(R.id.fade_control, ProductFragment.newInstance()).commit();
                        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
                        window.setStatusBarColor(getColor(R.color.white));

                        break;
                    case R.id.setting_fragment:
                        getSupportFragmentManager().beginTransaction().replace(R.id.fade_control, SettingFragment.newInstance()).commit();
                        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
                        window.setStatusBarColor(getColor(R.color.brown_120));
                        break;
                }
                return true;
            }
        });
        model = new ViewModelProvider(this).get(TableViewModel.class);
        model.liveDataGetAllToken();
//        pushMessage("PolyOder", "Chào mừng trở quay lại. Chúc bạn một ngày tốt lành.");
        binding.fabOder.setCount(1);
        binding.fabOder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // on below line we are calling a
                // method to increase the fab count.
                binding.fabOder.increase();
            }
        });

    }


    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
    public void visibilityOfBottom(boolean isScroll){
        if(isScroll){
            aniSlideUp(binding.bottomAppbar, binding.fabOder);
        } else{
            aniSlideDown(binding.bottomAppbar, binding.fabOder);
        }
    }

    public void hideBottomBar(){
        binding.bottomAppbar.setVisibility(View.GONE);
        binding.fabOder.setVisibility(View.GONE);
    }
    public void showBottomBar(){
        binding.bottomAppbar.setVisibility(View.VISIBLE);
        binding.fabOder.setVisibility(View.VISIBLE);
    }

    private void aniSlideUp(BottomAppBar child, FloatingActionButton fab) {
        child.clearAnimation();
        child.animate().translationY(0).setDuration(300);
        fab.clearAnimation();
        fab.animate().translationY(-20f).setDuration(300);
    }

    private void aniSlideDown(BottomAppBar child, FloatingActionButton fab) {
        child.clearAnimation();
        child.animate().translationY(child.getHeight()+30f).setDuration(300);
        fab.clearAnimation();
        fab.animate().translationY(fab.getHeight()+child.getHeight()).setDuration(300);
    }


    public void pushMessage(String title, String message){
        model.liveDataListToken.observe(this, new Observer<List<Token>>() {
            @Override
            public void onChanged(List<Token> tokens) {
                for (Token token: tokens
                ) {
                    FMC.pushNotification(getApplicationContext(),token.getToken(),title, message);
                }
            }
        });

    }



}