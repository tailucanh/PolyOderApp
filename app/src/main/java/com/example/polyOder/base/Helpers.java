package com.example.polyOder.base;

import static android.content.Context.CONNECTIVITY_SERVICE;

import android.animation.ObjectAnimator;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.NetworkRequest;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.polyOder.R;
import com.example.polyOder.databinding.DialogNoInternetBinding;
import com.example.polyOder.databinding.LayoutNotificationInputBinding;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class Helpers {

    public boolean isEmptyString(String str){
        return TextUtils.isEmpty(str) ? true : false;
    }

    public boolean isEmailMatcher(String email){
        return Patterns.EMAIL_ADDRESS.matcher(email).matches() ? true : false;
    }

    public boolean isMatcherRegex(String str,String regex){
        return str.matches(regex) ? true : false;
    }


    public String isConvertEditText(EditText editText){
        return editText.getText().toString().trim();
    }

    public  boolean isEmptyList(ArrayList arrayList){
        return arrayList.size() == 0 ? true : false;
    }


    public  String isFormatMoney(Double money){
        Locale locale = new Locale("en", "EN");
        NumberFormat numberFormat = NumberFormat.getInstance(locale);
        return  numberFormat.format(money);
    }

    public String isFormatTime(Date time, String regexTime){
        DateFormat dateFormat = new SimpleDateFormat(regexTime);
        return dateFormat.format(time);
    }





    public void isAnimationView(View view, String ani, int time, float... values) {
        ObjectAnimator animator = ObjectAnimator.ofFloat(view, ani, values);
        animator.setDuration(time);
        animator.start();
    }


    public void notificationErrInput(Context context, String textErr){
        final Dialog dialog = new Dialog(context);
        LayoutNotificationInputBinding binding = LayoutNotificationInputBinding.inflate(LayoutInflater.from(context));
        dialog.setContentView(binding.getRoot());

        setLayoutDialog(dialog, Gravity.TOP, WindowManager.LayoutParams.WRAP_CONTENT);

        binding.layoutErr.setTranslationY(-150);
        binding.layoutErr.animate().translationYBy(150).setDuration(300);
        binding.tvErr.setTranslationY(-150);
        binding.tvErr.animate().translationYBy(150).setDuration(700);

        binding.tvErr.setText(textErr);
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                dialog.dismiss();
            }
        },2200);
        dialog.show();
    }

    public void notificationSuccessInput(Context context,String textSuccess){
        final Dialog dialog = new Dialog(context);
        LayoutNotificationInputBinding binding = LayoutNotificationInputBinding.inflate(LayoutInflater.from(context));
        dialog.setContentView(binding.getRoot());

        setLayoutDialog(dialog, Gravity.TOP ,WindowManager.LayoutParams.WRAP_CONTENT);

        binding.layoutErr.setBackgroundColor(context.getColor(R.color.green_200));
        binding.imgErr.setImageDrawable(context.getDrawable(R.drawable.ic_round_check_circle));
        binding.layoutErr.setTranslationY(-150);
        binding.layoutErr.animate().translationYBy(150).setDuration(300);
        binding.tvErr.setTranslationY(-150);
        binding.tvErr.animate().translationYBy(150).setDuration(700);

        binding.tvErr.setText(textSuccess);
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                dialog.dismiss();

            }
        },2200);
        dialog.show();
    }


    public void setLayoutDialog(Dialog dialog, int gravity, int height){
        Window window = dialog.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT,height);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        WindowManager.LayoutParams layoutParams = window.getAttributes();
        if(gravity != 0){
            layoutParams.gravity = gravity;
            window.setAttributes(layoutParams);
        }
    }

    public void setReverseItemRecycleView(Context context, RecyclerView recyclerView){
        LinearLayoutManager layoutManager  = new LinearLayoutManager(context);
        layoutManager.setStackFromEnd(true);
        layoutManager.setReverseLayout(true);

        recyclerView.setLayoutManager(layoutManager);
    }

    public boolean isInternetConnect(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnected()) {
            return true;
        } else {
            return false;
        }
    }

    public void getDialogNoInternet(Context context, boolean isConnected ){
        final Dialog dialog = new Dialog(context);
        DialogNoInternetBinding binding = DialogNoInternetBinding.inflate(LayoutInflater.from(context));
        dialog.setContentView(binding.getRoot());
        dialog.setCancelable(false);
        setLayoutDialog(dialog, 0 ,WindowManager.LayoutParams.WRAP_CONTENT );



        binding.btnCancel.setOnClickListener(btn ->{
            dialog.cancel();
        });
        binding.btnOk.setOnClickListener(btn ->{

            WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            if(!(wifiManager.isWifiEnabled())){
                wifiManager.setWifiEnabled(true);
                Toast.makeText(context, "Đã bật wifi.", Toast.LENGTH_SHORT).show();
            }
            dialog.cancel();
        });

        if(isConnected){
            dialog.show();
        }else {
            dialog.cancel();
        }
    }

}
