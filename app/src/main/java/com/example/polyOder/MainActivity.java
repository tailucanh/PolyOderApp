package com.example.polyOder;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;


import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.example.polyOder.carts.oders.ListSaveOderFragment;
import com.example.polyOder.carts.oders.MyAsyncTaskCarts;
import com.example.polyOder.chatPoly.AIChat.ChatPolyAIActivity;
import com.example.polyOder.databinding.LayoutNoInternetBinding;
import com.example.polyOder.interfaces.IOnBackPressed;
import com.example.polyOder.databinding.ActivityMainBinding;
import com.example.polyOder.home.HomeFragment;
import com.example.polyOder.carts.MarketFragment;
import com.example.polyOder.product.ProductFragment;
import com.example.polyOder.pushNotification.NetworkStateReceiver;
import com.example.polyOder.setting.SettingFragment;

import com.example.polyOder.viewModel.TableViewModel;

import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationBarView;

public class MainActivity extends AppCompatActivity implements View.OnTouchListener {
    public ActivityMainBinding binding = null;
    private TableViewModel model = null;
    private BroadcastReceiver myReceiver = null;
    private MyAsyncTaskCarts myAsyncTaskCarts;
    private float dX;
    private float dY;
    private int lastAction;
    private boolean collisionEventHandled = false;
    private Rect pic1Rect = new Rect();
    private Rect pic2Rect = new Rect();
    private float startX, startY;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        myReceiver = new NetworkStateReceiver();
        broadcastIntent();
        myAsyncTaskCarts = new MyAsyncTaskCarts(MainActivity.this);
        myAsyncTaskCarts.execute();



        binding.icBee.setOnTouchListener(this);
        binding.icBee.setOnClickListener(ic ->{
            startActivity(new Intent(this, ChatPolyAIActivity.class));
        });


        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        getWindow().setStatusBarColor(getColor(R.color.white));
        getSupportFragmentManager().beginTransaction().replace(R.id.fade_control, HomeFragment.newInstance()).commit();

        binding.bottomNav.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.home_fragment:
                        getSupportFragmentManager().beginTransaction().replace(R.id.fade_control, HomeFragment.newInstance()).commit();
                        setStatusBarColor(getColor(R.color.white));

                        break;
                    case R.id.maket_fragment:
                        getSupportFragmentManager().beginTransaction().replace(R.id.fade_control, MarketFragment.newInstance()).commit();
                        setStatusBarColor(getColor(R.color.white));

                        break;
                    case R.id.menu_fragment:
                        getSupportFragmentManager().beginTransaction().replace(R.id.fade_control, ProductFragment.newInstance()).commit();
                        setStatusBarColor(getColor(R.color.white));

                        break;
                    case R.id.setting_fragment:
                        getSupportFragmentManager().beginTransaction().replace(R.id.fade_control, SettingFragment.newInstance()).commit();
                        setStatusBarColor(getColor(R.color.brown_120));

                        break;
                }
                return true;
            }
        });
        model = new ViewModelProvider(this).get(TableViewModel.class);
        model.liveDataGetAllToken();
//        pushMessage("PolyOder", "Chào mừng trở quay lại. Chúc bạn một ngày tốt lành.");
        binding.fabOder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.bottomNav.setSelectedItemId(R.id.placeholder);
                getSupportFragmentManager().beginTransaction().replace(R.id.fade_control, ListSaveOderFragment.newInstance()).commit();
                setStatusBarColor(getColor(R.color.white));

            }
        });


    }

    @Override
    public void onBackPressed() {
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fade_control);
        if (!(fragment instanceof IOnBackPressed) || !((IOnBackPressed) fragment).onBackPressed()) {
            super.onBackPressed();
        }


    }

    @Override
    protected void onResume() {
        super.onResume();

    }
    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(myReceiver);
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
    public void broadcastIntent() {
        registerReceiver(myReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
    }

    @Override
    public boolean onTouch(View view, MotionEvent event) {

        switch (event.getAction()) {

            case MotionEvent.ACTION_DOWN:
                showCloseIcBee();
                dX = view.getX() - event.getRawX();
                dY = view.getY() - event.getRawY();
                lastAction = MotionEvent.ACTION_DOWN;
                startX = event.getX();
                startY = event.getY();
                break;

            case MotionEvent.ACTION_MOVE:
                view.setY(event.getRawY() + dY);
                view.setX(event.getRawX() + dX);
                lastAction = MotionEvent.ACTION_MOVE;
                if (Math.abs(event.getX() - startX) > 10 || Math.abs(event.getY() - startY) > 10) {
                    return false;
                }
                break;

            case MotionEvent.ACTION_UP:
                pic1Rect.left = (int) binding.icBee.getX();
                pic1Rect.top = (int) binding.icBee.getY();
                pic1Rect.right = (int) binding.icBee.getX() + binding.icBee.getWidth();
                pic1Rect.bottom = (int) binding.icBee.getY() + binding.icBee.getHeight();

                pic2Rect.left = (int) binding.icCloseBee.getX();
                pic2Rect.top = (int) binding.icCloseBee.getY();
                pic2Rect.right = (int) binding.icCloseBee.getX() + binding.icCloseBee.getWidth();
                pic2Rect.bottom = (int) binding.icCloseBee.getY() + binding.icCloseBee.getHeight();
                if(handleCollision(pic1Rect, pic2Rect)){
                    binding.icBee.setVisibility(View.GONE);
                    hideCloseIcBee();
                }else {
                    if (Math.abs(event.getX() - startX) < 10 && Math.abs(event.getY() - startY) < 10) {
                        view.performClick();
                    }
                }

                break;

        }
        return true;
    }
    private boolean handleCollision(Rect one, Rect two) {
        boolean hasCollision = hasCollision(one, two);
        if (collisionEventHandled != hasCollision) {
            collisionEventHandled = hasCollision;
            return hasCollision;
        }
        return false;
    }

    private static boolean hasCollision(Rect one, Rect two) {
        return (one.left < two.right &&
                one.right > two.left &&
                one.top < two.bottom &&
                one.bottom > two.top);
    }

    public void showCloseIcBee(){
        binding.icCloseBee.setTranslationY(200);
        binding.icCloseBee.animate().translationYBy(-200).setDuration(500);
        binding.layoutBlur.setVisibility(View.VISIBLE);
    }

    public void hideCloseIcBee(){
        binding.icCloseBee.setTranslationY(0);
        binding.icCloseBee.animate().translationYBy(450).setDuration(1000);
        binding.layoutBlur.setVisibility(View.GONE);

    }

    public void notificationNoInternet(Context context, boolean isConnected ){
        final Dialog dialog = new Dialog(context);
        LayoutNoInternetBinding binding = LayoutNoInternetBinding.inflate(LayoutInflater.from(context));
        dialog.setContentView(binding.getRoot());
        dialog.setCancelable(false);
        Window window = dialog.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT,WindowManager.LayoutParams.MATCH_PARENT);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        binding.btnReload.setOnClickListener(btn ->{
            binding.progressBar.setVisibility(View.VISIBLE);
            binding.tvLoading.setVisibility(View.VISIBLE);
            animationTextLoading(binding.tvLoading);
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = cm.getActiveNetworkInfo();
            if (netInfo != null && netInfo.isConnected()) {
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        dialog.cancel();
                    }
                },4500);

            } else{
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        binding.progressBar.setVisibility(View.GONE);
                        binding.tvLoading.setVisibility(View.GONE);
                        Toast.makeText(context, "Chưa kết nối được mạng.Hãy kiểm tra lại!", Toast.LENGTH_SHORT).show();

                    }
                },3500);

            }
        });
        if(isConnected){
            dialog.show();
        }else {
            dialog.cancel();
        }
    }

    public void animationTextLoading(TextView tv ){
        final Handler handler = new Handler();
        Runnable runnable = new Runnable() {
            int count = 0;
            @Override
            public void run() {
                count++;
                if (count == 1) {
                    tv.setText("Loading.");
                } else if (count == 2) {
                    tv.setText("Loading..");
                } else if (count == 3) {
                    tv.setText("Loading...");
                }
                if (count == 3)
                    count = 0;

                handler.postDelayed(this, 2 * 1000);
            }
        };
        handler.postDelayed(runnable, 1 * 1000);

    }

    public void setStatusBarColor(int color){
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        getWindow().setStatusBarColor(color);
    }



    public void hideBottomBar(){
        binding.bottomAppbar.setVisibility(View.GONE);
        binding.fabOder.setVisibility(View.GONE);
    }
    public void showBottomBar(){
        binding.bottomAppbar.setVisibility(View.VISIBLE);
        binding.fabOder.setVisibility(View.VISIBLE);
    }




}