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
import android.view.Window;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.polyOder.MainActivity;
import com.example.polyOder.R;
import com.example.polyOder.base.BaseFragment;
import com.example.polyOder.databinding.FragmentSettingBinding;
import com.example.polyOder.model.User;
import com.example.polyOder.ui.SignInActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class SettingFragment extends BaseFragment {
    private FragmentSettingBinding binding = null;
    private User user;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    MainActivity mainActivity;


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
        Window window = getActivity().getWindow();
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        window.setStatusBarColor(getActivity().getColor(R.color.brown_120));

        user = new User();
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        StorageReference reference = FirebaseStorage.getInstance().getReference().child("avatars");
        reference.listAll().addOnSuccessListener(listResult -> {
            for (StorageReference files : listResult.getItems()
            ) {
                if (files.getName().equals(firebaseUser.getUid())) {
                    files.getDownloadUrl().addOnSuccessListener(uri -> {
                        if(getActivity() != null){
                            Glide.with(getActivity()).load(uri).into(binding.imgAvatar);
                        }
                    });
                }
            }
        });
        mainActivity.showBottomBar();
        listening();
        initObSever();
    }


    @Override
    public void loadData() {
        String userID = firebaseUser.getUid();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.child("users").child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                user = snapshot.getValue(User.class);
                if (firebaseUser != null && !user.isUserAuthorization()) {
                    binding.tvName.setText(user.getName_user());
                    binding.icEditUser.setVisibility(View.VISIBLE);
                    binding.tvContent.setVisibility(View.VISIBLE);
                    binding.layoutEmployeeManagement.setVisibility(View.GONE);
                    binding.layoutFinancialOverview.setVisibility(View.GONE);
                    binding.icLine3.setVisibility(View.GONE);
                }else {
                    binding.tvName.setText(user.getName_user());
                    binding.icEditUser.setVisibility(View.GONE);
                    binding.tvContent.setVisibility(View.GONE);
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
            replaceFragment(EmployeeManagementFragment.newInstance());
        });

        binding.scrollViewSetting.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(@NonNull NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (scrollY > oldScrollY) {
                    mainActivity.visibilityOfBottom(false);
                }
                if (scrollY < oldScrollY) {
                    mainActivity.visibilityOfBottom(true);
                }

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

    private void signOut(Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Đăng xuất tài khoản ");
        builder.setIcon(context.getDrawable(R.drawable.ic_logout));
        builder.setMessage("Bạn chắc chắn muốn đăng xuất!");
        builder.setCancelable(false);
        builder.setPositiveButton("Đồng ý", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(getContext(), SignInActivity.class));
                Toast.makeText(context, "Đã đăng xuất! ", Toast.LENGTH_SHORT).show();
                dialog.cancel();
            }
        });
        builder.setNegativeButton("Thoát", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(context, "Đã hủy !", Toast.LENGTH_SHORT).show();
                dialog.cancel();
            }
        });

        AlertDialog sh = builder.create();
        sh.show();
    }
}