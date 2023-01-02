package com.example.polyOder.Oder;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.polyOder.MainActivity;
import com.example.polyOder.base.BaseFragment;
import com.example.polyOder.databinding.FragmentSaveToOderBinding;
import com.example.polyOder.model.Table;
import com.example.polyOder.model.User;
import com.example.polyOder.table.adapter.TableAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;


public class ListSaveOderFragment extends BaseFragment {
    private FragmentSaveToOderBinding binding;
    private TableAdapter adapter = null;
    private ArrayList<Table> listTable;
    private PopupMenu popupMenuTables;
    private User user;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    MainActivity mainActivity;


    public ListSaveOderFragment() {

    }

    public static ListSaveOderFragment newInstance() {
        ListSaveOderFragment fragment = new ListSaveOderFragment();
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
        binding = FragmentSaveToOderBinding.inflate(inflater,container,false);
        return binding.getRoot();

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        user = new User();
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();


        listening();

    }

    @Override
    public void loadData() {

    }

    @Override
    public void listening() {



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





}