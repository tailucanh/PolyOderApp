package com.example.polyOder.base;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.polyOder.R;
import com.example.polyOder.databinding.DialogAddTypeProductBinding;
import com.example.polyOder.databinding.LayoutNotificationInputBinding;

public abstract class BaseFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView();

    }

    @Override
    public void onResume() {
        super.onResume();
        loadData();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    public void replaceFragment(Fragment fragment) {
        getParentFragmentManager().beginTransaction().setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left,
                R.anim.slide_out_right).replace(R.id.fade_control, fragment).addToBackStack(null).commit();
    }

    public void notificationErrInput(Context context,String textErr){
        final Dialog dialog = new Dialog(context);
        LayoutNotificationInputBinding binding = LayoutNotificationInputBinding.inflate(LayoutInflater.from(context));
        dialog.setContentView(binding.getRoot());
        Window window = dialog.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT,WindowManager.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        WindowManager.LayoutParams layoutParams = window.getAttributes();
        layoutParams.gravity = Gravity.TOP;
        window.setAttributes(layoutParams);

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
        },2000);
        dialog.show();
    }
    public void notificationSuccessInput(Context context,String textSuccess){
        final Dialog dialog = new Dialog(context);
        LayoutNotificationInputBinding binding = LayoutNotificationInputBinding.inflate(LayoutInflater.from(context));
        dialog.setContentView(binding.getRoot());
        Window window = dialog.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT,WindowManager.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        WindowManager.LayoutParams layoutParams = window.getAttributes();
        layoutParams.gravity = Gravity.TOP;
        window.setAttributes(layoutParams);

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
        },2000);
        dialog.show();
    }




    abstract public void loadData();

    abstract public void listening();

    abstract public void initObSever();

    public void backStack() {
        getParentFragmentManager().popBackStack();
    }

    abstract public void initView();

}
