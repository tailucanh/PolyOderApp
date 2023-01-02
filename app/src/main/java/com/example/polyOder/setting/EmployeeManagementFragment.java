package com.example.polyOder.setting;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.polyOder.MainActivity;
import com.example.polyOder.R;
import com.example.polyOder.base.BaseFragment;
import com.example.polyOder.databinding.DialogFunctionEmployeeManagementBinding;
import com.example.polyOder.databinding.DialogFunctionTableBinding;
import com.example.polyOder.databinding.FragmentEmployeeManagementBinding;
import com.example.polyOder.model.User;
import com.example.polyOder.setting.adapter.EmployeeManagementAdapter;
import com.example.polyOder.setting.adapter.OnLongClickItemUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class EmployeeManagementFragment extends BaseFragment implements OnLongClickItemUser {
    private FragmentEmployeeManagementBinding binding = null;
    private  ArrayList<User> listEmployee;
    private EmployeeManagementAdapter adapter;
    MainActivity mainActivity;


    public EmployeeManagementFragment(){

    }
    public static EmployeeManagementFragment newInstance() {
        EmployeeManagementFragment fragment = new EmployeeManagementFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
       binding = FragmentEmployeeManagementBinding.inflate(inflater,container,false);
       return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Window window = getActivity().getWindow();
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        window.setStatusBarColor(getActivity().getColor(R.color.white));
        mainActivity.hideBottomBar();
        listEmployee = new ArrayList<>();
        adapter = new EmployeeManagementAdapter(listEmployee,EmployeeManagementFragment.this, getContext());
        listening();
        initObSever();
    }

    @Override
    public void loadData() {
        getAllEmployee();

    }

    @Override
    public void listening() {
        binding.searchViewStaff.clearFocus();
        binding.searchViewStaff.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                getFilterUser(newText);
                return true;
            }
        });


    }

    @Override
    public void initObSever() {

    }

    @Override
    public void initView() {

    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mainActivity = (MainActivity)context;
    }


    private void getFilterUser(String text) {
        ArrayList<User> filterUser =new ArrayList<>();
        for (User user : listEmployee) {
            if(user.getName_user().toLowerCase().contains(text.toLowerCase())){
                filterUser.add(user);
            }
        }
        if(!filterUser.isEmpty()){
            adapter.setFilterList(filterUser);
            binding.tvNumberOfUser.setText("Có "+filterUser.size()+" nhân viên");
        }
    }


    private void getAllEmployee(){
        DatabaseReference mRef = FirebaseDatabase.getInstance().getReference("users");
        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listEmployee.clear();
                for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                    User user = dataSnapshot.getValue(User.class);
                    if(!user.isUserAuthorization()){
                        listEmployee.add(user);
                    }
                }
                binding.tvNumberOfUser.setText("Có "+listEmployee.size() +" nhân viên");
                adapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        LinearLayoutManager layoutManager  = new LinearLayoutManager(getContext());
        layoutManager.setStackFromEnd(true);
        layoutManager.setReverseLayout(true);
        binding.recListStaff.setLayoutManager(layoutManager);
        binding.recListStaff.setAdapter(adapter);
        binding.swiperRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                binding.recListStaff.setAdapter(adapter);
                binding.swiperRefreshLayout.setRefreshing(false);
            }
        });

    }

    private void dialogFunctionEmployee(Context context, User user) {
        final Dialog dialogFunction = new Dialog(context);
        DialogFunctionEmployeeManagementBinding bindingDialog = DialogFunctionEmployeeManagementBinding.inflate(LayoutInflater.from(context));
        dialogFunction.setContentView(bindingDialog.getRoot());
        Window window = dialogFunction.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT,WindowManager.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        WindowManager.LayoutParams layoutParams = window.getAttributes();
        layoutParams.gravity = Gravity.BOTTOM;
        window.setAttributes(layoutParams);
        bindingDialog.dialogChooserFunction.setTranslationY(150);
        bindingDialog.dialogChooserFunction.animate().translationYBy(-150).setDuration(400);

        bindingDialog.tvShowInfo.setOnClickListener(tv ->{
            replaceFragment(new UpdateUserFragment().newInstance(user));
        });


        bindingDialog.tvDeleteEmployee.setOnClickListener(tv ->{
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("Xóa nhân viên");
            builder.setIcon(context.getDrawable(R.drawable.ic_delete));
            builder.setMessage("Bạn chắc chắn muốn xóa nhân viên");
            builder.setCancelable(false);
            builder.setPositiveButton("Đồng ý", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });

            builder.setNegativeButton("Hủy", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Toast.makeText(context,"Đã hủy !",Toast.LENGTH_SHORT).show();
                    dialog.cancel();
                }
            });
            AlertDialog sh = builder.create();
            sh.show();
        });

        dialogFunction.show();

    }




    @Override
    public void onLongClick(User user) {
        dialogFunctionEmployee(getContext(), user);
    }
}
