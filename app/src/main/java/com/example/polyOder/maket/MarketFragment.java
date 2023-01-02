package com.example.polyOder.maket;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RatingBar;
import android.widget.Toast;

import com.example.polyOder.MainActivity;
import com.example.polyOder.Oder.ListOderFragment;
import com.example.polyOder.base.BaseFragment;
import com.example.polyOder.databinding.DialogEvaluateBinding;
import com.example.polyOder.databinding.FragmentMaketBinding;
import com.example.polyOder.model.Table;
import com.example.polyOder.table.DetailTableFragment;

public class MarketFragment extends BaseFragment {
    private FragmentMaketBinding binding = null;
    private Table table;
    MainActivity mainActivity;


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
        mainActivity.showBottomBar();
        listening();
        initObSever();
    }

    @Override
    public void loadData() {

    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mainActivity = (MainActivity)context;
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
            replaceFragment(DetailTableFragment.newInstance(table));
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

        Window window = dialog.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT,WindowManager.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

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