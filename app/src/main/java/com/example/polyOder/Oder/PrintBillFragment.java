package com.example.polyOder.Oder;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.polyOder.MainActivity;
import com.example.polyOder.base.BaseFragment;
import com.example.polyOder.databinding.FragmentOderDetailsBinding;
import com.example.polyOder.databinding.FragmentPrintBillBinding;
import com.example.polyOder.model.Product;
import com.example.polyOder.model.Receipt;
import com.example.polyOder.model.User;
import com.example.polyOder.table.TableViewModel;
import com.example.polyOder.table.adapter.OderAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class PrintBillFragment extends BaseFragment {
    private FragmentPrintBillBinding binding = null;
    private TableViewModel tableModel = null;
    private  Receipt receiptModel;
    private  ArrayList<String> listIdProduct;
    private User user;
    MainActivity mainActivity;


    public PrintBillFragment(Receipt receipt) {
        this.receiptModel = receipt;
    }

    public PrintBillFragment() {
    }

    public PrintBillFragment newInstance() {
        return new PrintBillFragment(receiptModel);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentPrintBillBinding.inflate(inflater, container, false);
        tableModel = new ViewModelProvider(this).get(TableViewModel.class);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mainActivity.hideBottomBar();
        listening();
        initObSever();
    }

    @Override
    public void loadData() {
        user = new User();
        String userID = FirebaseAuth.getInstance().getUid();
        binding.tvIdUser.setText("Id: "+"P000"+userID.substring(24,28));
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.child("users").child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                user = snapshot.getValue(User.class);
                if (FirebaseAuth.getInstance().getUid() != null) {
                    binding.tvNameUser.setText(user.getName_user());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        binding.tvIdBill.setText("ACCOUNT NO: "+receiptModel.getIdReceipt());
        binding.tvTime.setText(receiptModel.getTimeOder());
        int amountProduct = 0;
        for(int i = 0; i < receiptModel.getListCountProduct().size(); i++){
            amountProduct += receiptModel.getListCountProduct().get(i);
        }
        binding.tvAmountProduct.setText(amountProduct+"");

        listIdProduct = (ArrayList<String>) receiptModel.getListIdProduct();
        tableModel.listLiveData(listIdProduct);
        tableModel.listProductOder.observe(getViewLifecycleOwner(), new Observer<List<Product>>() {
            @Override
            public void onChanged(List<Product> products) {
                if(products.size() != 0){
                    for(int i = 0; i < receiptModel.getListCountProduct().size(); i++){
                        for(int k = 0 ; k < products.size(); k++) {
                            products.get(k).setIsClick(receiptModel.getListCountProduct().get(k));
                        }
                    }
                }

                OderAdapter adapter = new OderAdapter(products,1,getActivity());
                Locale locale = new Locale("en", "EN");
                NumberFormat numberFormat = NumberFormat.getInstance(locale);
                String strMoney = numberFormat.format(receiptModel.getMoney());;
                binding.tvTotalBill.setText(strMoney);


                binding.recListItemsBill.setAdapter(adapter);
            }
        });




    }

    @Override
    public void listening() {
        binding.icBack.setOnClickListener(v->{
            backStack();
        });
        binding.icPrint.setOnClickListener(ic ->{
            notificationErrInput(getContext(),"Chưa thiết lập máy in đơn.");
        });


    }

    @Override
    public void initObSever() {

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }


    @Override
    public void initView() {

    }
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mainActivity = (MainActivity)context;
    }



}