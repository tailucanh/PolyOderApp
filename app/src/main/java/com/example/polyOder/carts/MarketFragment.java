package com.example.polyOder.carts;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.RatingBar;
import android.widget.Toast;

import com.example.polyOder.MainActivity;
import com.example.polyOder.base.Helpers;
import com.example.polyOder.carts.oders.ListOderFragment;
import com.example.polyOder.base.BaseFragment;
import com.example.polyOder.databinding.DialogEvaluateBinding;
import com.example.polyOder.databinding.FragmentMaketBinding;
import com.example.polyOder.model.Table;
import com.example.polyOder.home.table.DetailTableFragment;

public class MarketFragment extends BaseFragment {
    private FragmentMaketBinding binding = null;
    private Helpers helpers = new Helpers();


    public MarketFragment() {
        // Required empty public constructor
    }

    public static MarketFragment newInstance() {
        MarketFragment fragment = new MarketFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentMaketBinding.inflate(inflater,container,false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        showBottomBar();

        listening();
        initObSever();
    }

    @Override
    public void loadData() {

    }



    @Override
    public void listening() {
        binding.icCloseLayoutStar.setOnClickListener(ic ->{
            binding.layoutStart.setVisibility(View.GONE);
        });

        binding.layoutEvaluate.setOnClickListener(layout ->{
            dialogEvaluate(getContext());
        });

        binding.layoutTables.setOnClickListener(tv ->{
            replaceFragment(FragmentListTablesToOder.newInstance());
        });
        binding.layoutReceipt.setOnClickListener(ic->{
            replaceFragment(ListOderFragment.newInstance());
        });

        binding.btnAddOder.setOnClickListener(v -> {
            replaceFragment(DetailTableFragment.newInstance(null));
        });

    }

    @Override
    public void initObSever() {

    }

    @Override
    public void initView() {


    }

    public void dialogEvaluate(Context context){
        final Dialog dialog = new Dialog(context);
        DialogEvaluateBinding bindingDialog = DialogEvaluateBinding.inflate(LayoutInflater.from(context));
        dialog.setContentView(bindingDialog.getRoot());

        helpers.setLayoutDialog(dialog, 0,WindowManager.LayoutParams.WRAP_CONTENT);

        bindingDialog.ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                if(ratingBar.getRating() == 1){
                    bindingDialog.tvEvaluate.setText("Kém");
                }else if(ratingBar.getRating() == 2){
                    bindingDialog.tvEvaluate.setText("Trung bình");
                } else if(ratingBar.getRating() == 3){
                    bindingDialog.tvEvaluate.setText("Khá");
                } else if(ratingBar.getRating() == 4){
                    bindingDialog.tvEvaluate.setText("Tốt");
                } else if(ratingBar.getRating() == 5){
                    bindingDialog.tvEvaluate.setText("Rất tốt");
                }
            }
        });

        bindingDialog.btnCancel.setOnClickListener(button ->{
            dialog.dismiss();
        });
        bindingDialog.btnSend.setOnClickListener(button ->{
            Toast.makeText(context, "Cảm ơn đánh giá trải nghiệm của bạn.", Toast.LENGTH_SHORT).show();
            binding.layoutEvaluate.setVisibility(View.GONE);
            dialog.dismiss();
        });

        dialog.show();
    }



}