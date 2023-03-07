package com.example.polyOder.setting.users;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.polyOder.MainActivity;
import com.example.polyOder.R;
import com.example.polyOder.base.BaseFragment;
import com.example.polyOder.base.Helpers;
import com.example.polyOder.databinding.DialogFunctionEmployeeManagementBinding;
import com.example.polyOder.databinding.FragmentEmployeeManagementBinding;
import com.example.polyOder.model.User;
import com.example.polyOder.setting.users.adapter.UserAdapter;
import com.example.polyOder.interfaces.OnTouchTheUser;
import com.google.android.gms.tasks.OnSuccessListener;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class UserManagementFragment extends BaseFragment implements OnTouchTheUser {
    private FragmentEmployeeManagementBinding binding = null;
    private  ArrayList<User> listEmployee;
    private UserAdapter adapter;
    private Helpers helpers = new Helpers();


    public UserManagementFragment(){

    }
    public static UserManagementFragment newInstance() {
        UserManagementFragment fragment = new UserManagementFragment();
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
        setColorStatusBar(getActivity().getColor(R.color.white));
        hideBottomBar();
        listEmployee = new ArrayList<>();

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
            binding.recListStaff.setVisibility(View.VISIBLE);
            binding.notifiNullData.setVisibility(View.GONE);
        }else {
            binding.tvNumberOfUser.setText("");
            binding.recListStaff.setVisibility(View.GONE);
            binding.notifiNullData.setVisibility(View.VISIBLE);
            binding.tvContentNull.setText("Không có nhân viên "+"\""+text+"\"");
        }
    }


    private void getAllEmployee(){
       getDataFromFirebase("users",new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listEmployee.clear();
                for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                    User user = dataSnapshot.getValue(User.class);
                    if(!user.isUserAuthorization()){
                        listEmployee.add(user);
                    }
                }
                if(!(helpers.isEmptyList(listEmployee))){
                    adapter = new UserAdapter(listEmployee, UserManagementFragment.this, getContext());
                    binding.tvNumberOfUser.setText("Có "+listEmployee.size() +" nhân viên");
                    helpers.setReverseItemRecycleView(getContext(), binding.recListStaff);
                    binding.recListStaff.setAdapter(adapter);
                    binding.recListStaff.setVisibility(View.VISIBLE);
                    binding.notifiNullData.setVisibility(View.GONE);
                }else {
                    binding.tvNumberOfUser.setText("");
                    binding.recListStaff.setVisibility(View.GONE);
                    binding.notifiNullData.setVisibility(View.VISIBLE);
                    binding.tvContentNull.setText("Chưa có dữ liệu");
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        binding.swiperRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                binding.recListStaff.setAdapter(adapter);
                adapter.notifyDataSetChanged();
                binding.swiperRefreshLayout.setRefreshing(false);
            }
        });

    }


    private void dialogFunctionEmployee(Context context, User user) {
        final Dialog dialogFunction = new Dialog(context);
        DialogFunctionEmployeeManagementBinding bindingDialog = DialogFunctionEmployeeManagementBinding.inflate(LayoutInflater.from(context));
        dialogFunction.setContentView(bindingDialog.getRoot());
        helpers.setLayoutDialog(dialogFunction,Gravity.BOTTOM,WindowManager.LayoutParams.WRAP_CONTENT);

        bindingDialog.dialogChooserFunction.setTranslationY(150);
        bindingDialog.dialogChooserFunction.animate().translationYBy(-150).setDuration(400);

        bindingDialog.tvShowInfo.setOnClickListener(tv ->{
            replaceFragment(new DetailUserFragment(user));
            dialogFunction.cancel();
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
                    StorageReference storageReference = FirebaseStorage.getInstance().getReference();
                    storageReference.child("avatars/"+user.getId()).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {

                        }
                    });
                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("users");
                    reference.child(user.getId()).removeValue(new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {

                            helpers.notificationSuccessInput(context,"Đã xóa nhân viên khỏi hệ thống.");
                            dialogFunction.cancel();
                        }
                    });



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
