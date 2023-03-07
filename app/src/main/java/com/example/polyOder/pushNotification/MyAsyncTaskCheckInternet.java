package com.example.polyOder.pushNotification;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.andremion.counterfab.CounterFab;
import com.example.polyOder.MainActivity;
import com.example.polyOder.R;
import com.example.polyOder.databinding.DialogNoInternetBinding;
import com.example.polyOder.databinding.LayoutNoInternetBinding;

public class MyAsyncTaskCheckInternet extends AsyncTask<Void, Integer, Void> {
    Context context;
    

    public MyAsyncTaskCheckInternet(Context context) {
        this.context = context;
      
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();


    }

    @Override
    protected Void doInBackground(Void... params) {
        for (int i = 0; i <= 100; i++) {
            SystemClock.sleep(100);
            publishProgress(i);
        }
        return null;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        if (isInternet(context)) {
            dialogNoInternet(context, false);
        } else {
            dialogNoInternet(context, true);
        }
        

    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);

    }

    public boolean isInternet(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnected()) {
            return true;
        } else {
            return false;
        }
    }
    public void dialogNoInternet(Context context, boolean isConnected ){
        final Dialog dialog = new Dialog(context);
        DialogNoInternetBinding binding = DialogNoInternetBinding.inflate(LayoutInflater.from(context));
        dialog.setContentView(binding.getRoot());
        dialog.setCancelable(false);
        Window window = dialog.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT,WindowManager.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        binding.btnCancel.setOnClickListener(btn ->{
            dialog.cancel();
        });
        binding.btnOk.setOnClickListener(btn ->{
            dialog.cancel();
        });

        if(isConnected){
            dialog.show();
        }else {
            dialog.cancel();
        }
    }



}

