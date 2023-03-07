package com.example.polyOder.home.table;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.example.polyOder.MainActivity;
import com.example.polyOder.base.Helpers;

import com.example.polyOder.interfaces.IOnBackPressed;
import com.example.polyOder.pushNotification.FMC;
import com.example.polyOder.R;
import com.example.polyOder.viewModel.TableViewModel;
import com.example.polyOder.base.BaseFragment;
import com.example.polyOder.databinding.DialogSuggestOderBinding;
import com.example.polyOder.databinding.FragmentAddOderBinding;
import com.example.polyOder.home.HomeFragment;
import com.example.polyOder.home.adapter.OderAdapter;
import com.example.polyOder.carts.MarketFragment;
import com.example.polyOder.model.Product;
import com.example.polyOder.model.Receipt;
import com.example.polyOder.model.Table;
import com.example.polyOder.model.Token;
import com.example.polyOder.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DetailTableFragment extends BaseFragment {
    private FragmentAddOderBinding binding = null;
    private Table table;
    private ArrayList<String> getIdProduct = null;
    private ArrayList<Integer> getCountProduct = null;
    private String idTable = null;
    private Receipt receiptModel;
    private TableViewModel model = null;
    private String statusTable = "false";
    private Double totalMoney = 0.0;
    private ArrayList<String> listIdProduct;
    private ArrayList<Integer>listCountProduct = null;
    private Helpers helpers = new Helpers();


    public DetailTableFragment(Table table) {
        this.table = table;
    }

    public DetailTableFragment() {
    }

    public static DetailTableFragment newInstance(Table table) {
        DetailTableFragment fragment = new DetailTableFragment(table);
        Bundle args = new Bundle();
        args.putSerializable("table", (Serializable) table);
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
        binding = FragmentAddOderBinding.inflate(inflater, container, false);
        model = new ViewModelProvider(this).get(TableViewModel.class);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        table = new Table();
        listIdProduct = new ArrayList<>();
        getCountProduct = new ArrayList<>();
        getIdProduct = new ArrayList<>();
        listCountProduct = new ArrayList<>();

        hideBottomBar();
        listening();
        initObSever();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

    }

    @Override
    public void loadData() {

        model.liveDataGetAllToken();
        if (getArguments() != null) {
            if (getArguments().getSerializable("table") != null) {
                table = (Table) getArguments().getSerializable("table");
                idTable = String.valueOf(table.getId_table());
                if (getArguments().getStringArrayList("list_product_select") != null) {
                    getIdProduct = getArguments().getStringArrayList("list_product_select");
                    getCountProduct = getArguments().getIntegerArrayList("list_count_product");
                    listIdProduct.addAll(getIdProduct);
                    listCountProduct.addAll(getCountProduct);

                }
                statusTable = table.getStatus();
                binding.tvNameBill.setText(table.getName_table());
                if (table.getName_table() == null) {
                    binding.tvNameBill.setText("Bán mang về");
                    binding.btnPayOder.setVisibility(View.VISIBLE);
                    binding.btnSaveOder.setVisibility(View.GONE);
                    binding.icSuggest.setVisibility(View.GONE);
                }
            } else {
                binding.tvNameBill.setText("Bán mang về");
                binding.btnPayOder.setVisibility(View.VISIBLE);
                binding.btnSaveOder.setVisibility(View.GONE);
                binding.icSuggest.setVisibility(View.GONE);
            }
            model.listLiveData(listIdProduct);
        }



        if (statusTable.equals("true")) {
            model.liveDataGetReceipt(idTable);
            model.liveDataGetCancelReceipt(idTable);
            showViewByUser();
        }
        if (table.getName_table() != null) {
            model.liveDataGetReceipt.observe(getViewLifecycleOwner(), new Observer<Receipt>() {
                @Override
                public void onChanged(Receipt receipt) {
                    binding.btnSaveOder.setVisibility(View.GONE);
                    binding.btnPayOder.setVisibility(View.VISIBLE);

                    receiptModel = receipt;
                    model.listLiveData(receipt.getListIdProduct());

                    model.listProductOder.observe(getViewLifecycleOwner(), new Observer<List<Product>>() {
                    @Override
                    public void onChanged(List<Product> products) {
                        if(products.size() != 0){
                            for(int i = 0; i < receiptModel.getListCountProduct().size(); i++){
                                for(int k = 0 ; k < products.size(); k++) {
                                    products.get(k).setIsClick(receiptModel.getListCountProduct().get(k));
                                }
                            }

                        }

                        OderAdapter adapter = new OderAdapter(products,0,getActivity());
                        int totalProduct = 0;
                        for(int i = 0; i < receipt.getListCountProduct().size(); i++){
                            totalProduct += receipt.getListCountProduct().get(i);
                        }
                        binding.tvAmountProduct.setText(String.valueOf(totalProduct));

                        String strMoney = helpers.isFormatMoney(receiptModel.getMoney()) +"đ";
                        binding.tvTotalOder.setText(strMoney);
                        binding.tvTotalAmount.setText(strMoney);
                        binding.listProductOder.setAdapter(adapter);

                    }
                });

                    binding.layoutAddProduct.setVisibility(View.GONE);
                    binding.edNoteOder.setEnabled(false);
                    binding.edNoteOder.setText(receipt.getNoteOder() + "");

                }
            });


        }


        model.liveDataGetReceipt.observe(getViewLifecycleOwner(), new Observer<Receipt>() {
            @Override
            public void onChanged(Receipt receipt) {
                binding.btnSaveOder.setVisibility(View.GONE);
                receiptModel = receipt;
                model.listLiveData(receipt.getListIdProduct());
                binding.layoutAddProduct.setVisibility(View.GONE);
            }
        });

        model.liveDataPayReceipt.observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                if (s.equals("success")) {
                    helpers.notificationSuccessInput(getContext(), "Thanh toán thành công!");
                } else {
                    helpers.notificationErrInput(getContext(), "Thanh toán thất bại");
                }
            }
        });

        model.liveDataCancelReceipt.observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                if (s.equals("cancel")) {
                    helpers.notificationSuccessInput(getContext(), "Đã hủy đơn!");
                } else {
                    helpers.notificationErrInput(getContext(), "Hủy đơn thất bại!");
                }
            }
        });


    }

    @Override
    public void listening() {
        binding.icBack.setOnClickListener(v -> backStack());
        binding.layoutAddProduct.setOnClickListener(v -> {
            FragmentListAllProductToOder productToOder = new FragmentListAllProductToOder();
            Bundle bundle = new Bundle();
            bundle.putSerializable("table", table);
            productToOder.setArguments(bundle);
            replaceFragment(productToOder);
        });

        binding.icSuggest.setOnClickListener(ic ->{
            getSuggest(getContext());
        });

        binding.btnSaveOder.setOnClickListener(v -> {
            if (binding.listProductOder.getVisibility() == View.GONE) {
                helpers.notificationErrInput(getContext(), "Hãy chọn món !");
            } else {
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("OderSave");
                String key = reference.push().getKey();
                String strDate =  helpers.isFormatTime(Calendar.getInstance().getTime(), "yyyy-MM-dd hh:mm:ss");
                receiptModel = new Receipt(key, idTable, strDate, totalMoney, listIdProduct,listCountProduct, binding.edNoteOder.getText().toString(), true);
                reference.child(key).setValue(receiptModel);
                binding.cavCancelOder.setVisibility(View.VISIBLE);
                pushMessage(model,"PolyOder", table.getName_table()+" đang được sử dụng.Hãy chú ý thông tin.");
                model.setStatusTable(idTable, "true");
                replaceFragment(HomeFragment.newInstance());

            }
        });

        binding.btnPayOder.setOnClickListener(btn -> {
            if (binding.listProductOder.getVisibility() == View.GONE) {
                helpers.notificationErrInput(getContext(), "Hãy chọn món !");
            } else {
                getConfirmResponse(getContext(), "Xác nhận", R.drawable.ic_dollar_sign, "Hãy xác nhận thanh toán đơn hàng.", "Đồng ý", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (receiptModel != null) {
                            model.liveDataPayReceipt(receiptModel);
                            model.setStatusTable(idTable, "false");
                            pushMessage(model,"Thông báo thanh toán","Hóa đơn tại "+table.getName_table()+" đã được thanh toán.");
                            backStack();
                        }else {
                            DatabaseReference reference= FirebaseDatabase.getInstance().getReference("PayReceipt");
                            String strDate = helpers.isFormatTime(Calendar.getInstance().getTime(),"yyyy-MM-dd hh:mm:ss");
                            String key = reference.push().getKey();
                            Receipt receipt = new Receipt(key, "", strDate, totalMoney, listIdProduct,listCountProduct, binding.edNoteOder.getText().toString(), true);
                            reference.child(key).setValue(receipt);
                            helpers.notificationSuccessInput(getContext(), "Thanh toán thành công!");
                            pushMessage(model,"Thông báo thanh toán","Có 1 đơn đem về được thanh toán.");
                            replaceFragment(MarketFragment.newInstance());
                        }

                    }
                }, "Hủy", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(getContext(),"Đã hủy !",Toast.LENGTH_SHORT).show();
                        dialogInterface.cancel();
                    }
                });
            }
        });

        binding.btnCancelOder.setOnClickListener(btn ->{
            if (receiptModel != null) {
                getConfirmResponse(getContext(),"Xác nhận hủy đơn hàng",R.drawable.ic_cancelled, getString( R.string.notification_cancel_oder) ,"Đồng ý", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        receiptModel.setStatusOder(false);
                        model.setStatusTable(idTable, "false");
                        pushMessage(model,"Poly thông báo",table.getName_table()+" đã bị hủy đơn.");
                        model.liveDataCancelReceipt(receiptModel);
                        backStack();
                    }
                },"Hủy",  new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(getContext(),"Đã hủy !",Toast.LENGTH_SHORT).show();
                        dialog.cancel();
                    }
                });
            }
        });



    }

    @Override
    public void initObSever() {
        model.listProductOder.observe(getViewLifecycleOwner(), new Observer<List<Product>>() {
            @Override
            public void onChanged(List<Product> products) {
                binding.layoutAddProduct.setVisibility(View.GONE);
                binding.listProductOder.setVisibility(View.VISIBLE);
                if(products.size() != 0){
                    for(int i = 0 ; i <listCountProduct.size(); i ++){
                        for(int k = 0; k < products.size() ; k++){
                            products.get(k).setIsClick(listCountProduct.get(k));

                        }
                    }
                    OderAdapter adapter = new OderAdapter(products,0,getActivity());
                    int totalProduct = 0;
                    for(int i = 0; i < listCountProduct.size(); i++){
                        totalProduct += listCountProduct.get(i);

                    }
                    binding.tvAmountProduct.setText(String.valueOf(totalProduct));

                    for(int i = 0; i < products.size(); i++){
                        totalMoney += (products.get(i).getPrice() * products.get(i).getIsClick());
                    }


                    String strMoney = helpers.isFormatMoney(totalMoney)+"đ";

                    binding.tvTotalOder.setText(strMoney);
                    binding.tvTotalAmount.setText(strMoney);
                    binding.listProductOder.setAdapter(adapter);
                }


            }
        });


        model.oderTableStatus.observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String aBoolean) {
                if (aBoolean == "true") {
                    helpers.notificationSuccessInput(getContext(), "Đặt bàn thành công!");
                }
            }
        });
    }

    @Override
    public void initView() {

    }



    void showViewByUser(){
        getDataFirebaseUser("users",new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                if (FirebaseAuth.getInstance().getCurrentUser() != null && !user.isUserAuthorization()) {
                    binding.cavCancelOder.setVisibility(View.GONE);
                    binding.icSuggest.setVisibility(View.VISIBLE);
                }else {
                    binding.cavCancelOder.setVisibility(View.VISIBLE);
                    binding.icSuggest.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }


    private void getSuggest(Context context){
        final Dialog dialog = new Dialog(context);
        DialogSuggestOderBinding bindingDialog = DialogSuggestOderBinding.inflate(LayoutInflater.from(context));
        dialog.setContentView(bindingDialog.getRoot());
        Window window2 = dialog.getWindow();
        window2.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        window2.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        dialog.show();
    }




}