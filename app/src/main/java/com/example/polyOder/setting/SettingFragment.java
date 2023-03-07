package com.example.polyOder.setting;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.polyOder.MainActivity;
import com.example.polyOder.R;
import com.example.polyOder.base.BaseFragment;
import com.example.polyOder.chatPoly.messageChat.ChatsFragment;
import com.example.polyOder.databinding.FragmentSettingBinding;
import com.example.polyOder.model.User;
import com.example.polyOder.setting.sales.DailySalesReportFragment;
import com.example.polyOder.setting.sales.FinanceOverviewFragment;
import com.example.polyOder.setting.sales.StatisticalFragment;
import com.example.polyOder.setting.sales.TopProductFragment;
import com.example.polyOder.setting.users.UserManagementFragment;
import com.example.polyOder.setting.users.UpdateUserFragment;
import com.example.polyOder.ui.SignInActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SettingFragment extends BaseFragment {
    private FragmentSettingBinding binding = null;
    private User user;



    public SettingFragment() {
        // Required empty public constructor
    }

    public static SettingFragment newInstance() {
        SettingFragment fragment = new SettingFragment();
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
        binding = FragmentSettingBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setColorStatusBar(getActivity().getColor(R.color.brown_120));
        user = new User();

        getImageObjFromStorageReference("avatars", FirebaseAuth.getInstance().getCurrentUser().getUid(),binding.imgAvatar);
        showBottomBar();
        listening();
        initObSever();
    }


    @Override
    public void loadData() {

        getDataFirebaseUser("users",new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                user = snapshot.getValue(User.class);
                binding.tvName.setText(user.getName_user());
                if (FirebaseAuth.getInstance().getCurrentUser().getUid() != null && !user.isUserAuthorization()) {
                    binding.tvContent.setText(R.string.content_setting_user);
                    binding.layoutEmployeeManagement.setVisibility(View.GONE);
                    binding.layoutFinancialOverview.setVisibility(View.GONE);
                    binding.icLine3.setVisibility(View.GONE);
                }else {
                    binding.tvContent.setText(R.string.content_setting_admin);
                    binding.layoutEmployeeManagement.setVisibility(View.VISIBLE);
                    binding.layoutFinancialOverview.setVisibility(View.VISIBLE);
                    binding.icLine3.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

    }


    @Override
    public void listening() {
        binding.icEditUser.setOnClickListener(ic -> {
            replaceFragment(new UpdateUserFragment().newInstance(user));
        });
        binding.btnLogout.setOnClickListener(btn -> {
            signOut(getContext());
        });

        binding.layoutSalesReport.setOnClickListener(v -> {
            replaceFragment(DailySalesReportFragment.newInstance());
        });


        binding.layoutOrderStatistics.setOnClickListener(v -> {
            replaceFragment(StatisticalFragment.newInstance());
        });

        binding.layoutFinancialOverview.setOnClickListener(v -> {
            replaceFragment(FinanceOverviewFragment.newInstance());
        });

        binding.layoutEmployeeManagement.setOnClickListener(v ->{
            replaceFragment(UserManagementFragment.newInstance());
        });
        binding.layoutTopProduct.setOnClickListener(v ->{
            replaceFragment(TopProductFragment.newInstance());
        });
        binding.layoutChat.setOnClickListener(v ->{
            replaceFragment(ChatsFragment.newInstance());
        });


        visibleBottomBarOnScroll(null, binding.scrollViewSetting);


    }

    @Override
    public void initObSever() {
    }

    @Override
    public void initView() {

    }


    private void signOut(Context context) {
        getConfirmResponse(context,"Đăng xuất tài khoản" ,R.drawable.ic_logout,"Bạn chắc chắn muốn đăng xuất!" ,"Đồng ý", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(getContext(), SignInActivity.class));
                Toast.makeText(context, "Đã đăng xuất! ", Toast.LENGTH_SHORT).show();
                dialog.cancel();
            }
        },"hủy", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(context, "Đã hủy !", Toast.LENGTH_SHORT).show();
                dialog.cancel();
            }
        });
    }
}