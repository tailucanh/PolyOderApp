package com.example.polyOder.ui.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.polyOder.ui.tabFragment.FragmentSignInAdmin;
import com.example.polyOder.ui.tabFragment.FragmentSignInUser;


public class AdapterTabLayoutSignIn extends FragmentStateAdapter {


    public AdapterTabLayoutSignIn(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if(position == 0){
            return new FragmentSignInUser();
        }
        return new FragmentSignInAdmin();
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
