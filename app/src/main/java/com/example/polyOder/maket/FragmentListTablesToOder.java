package com.example.polyOder.maket;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.polyOder.MainActivity;
import com.example.polyOder.R;
import com.example.polyOder.base.BaseFragment;
import com.example.polyOder.base.OnclickOptionMenu;
import com.example.polyOder.databinding.DialogAddTypeProductBinding;
import com.example.polyOder.databinding.DialogFunctionProductBinding;
import com.example.polyOder.databinding.DialogFunctionTableBinding;
import com.example.polyOder.databinding.FragmentTableToOderBinding;
import com.example.polyOder.model.Table;
import com.example.polyOder.model.User;
import com.example.polyOder.table.DetailTableFragment;
import com.example.polyOder.table.adapter.TableAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class FragmentListTablesToOder extends BaseFragment implements OnclickOptionMenu,TableAdapter.OnItemLongClickListener {
    private FragmentTableToOderBinding binding;
    private TableAdapter adapter = null;
    private ArrayList<Table> listTable;
    private PopupMenu popupMenuTables;
    private User user;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    MainActivity mainActivity;


    public FragmentListTablesToOder() {

    }

    public static FragmentListTablesToOder newInstance() {
        FragmentListTablesToOder fragment = new FragmentListTablesToOder();
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
        binding = FragmentTableToOderBinding.inflate(inflater,container,false);
        return binding.getRoot();

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        user = new User();
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        mainActivity.hideBottomBar();

        ((AppCompatActivity)getActivity()).setSupportActionBar(binding.toolbar);
        binding.collapsingToolbar.setExpandedTitleColor(getContext().getColor(android.R.color.transparent));
        binding.collapsingToolbar.setTitle("Danh sách bàn");

        listening();

    }

    @Override
    public void loadData() {
        listTable = new ArrayList<>();
        getAllTables();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.child("users").child(firebaseUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                user = snapshot.getValue(User.class);
                if (firebaseUser != null && !user.isUserAuthorization()) {
                    binding.fabAddTable.setVisibility(View.GONE);

                }else {
                    binding.fabAddTable.setVisibility(View.VISIBLE);
                    showHideWhenScroll();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    @Override
    public void listening() {

        binding.searchViewTable.clearFocus();
        binding.searchViewTable.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterTables(newText);
                return true;
            }
        });

        binding.swiperRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getAllTables();
                binding.recViewTables.setAdapter(adapter);
                binding.swiperRefreshLayout.setRefreshing(false);
            }
        });

        binding.layoutFilterTable.setOnClickListener(layout ->{
            popupMenuTables = new PopupMenu(getContext(),binding.layoutFilterTable);
            popupMenuTables.inflate(R.menu.menu_popup_table);
            Menu menu = popupMenuTables.getMenu();
            popupMenuTables.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    return menuItemClicked(item);
                }
            });
            popupMenuTables.show();

        });



        binding.fabAddTable.setOnClickListener(btn ->{
            dialogAddTable(getContext());
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

    public void dialogAddTable(Context context) {
        final Dialog dialog = new Dialog(context);
        DialogAddTypeProductBinding binding = DialogAddTypeProductBinding.inflate(LayoutInflater.from(context));
        dialog.setContentView(binding.getRoot());
        dialog.setCancelable(false);
        Window window = dialog.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        binding.tvTitle.setText("Thêm bàn trống mới");
        binding.edNameType.setHint("Nhập tên bàn");

        binding.tvCancel.setOnClickListener(tv ->{
            dialog.dismiss();
        });

        binding.tvAdd.setOnClickListener(add->{
            FirebaseDatabase data = FirebaseDatabase.getInstance();
            DatabaseReference mRef = data.getReference("tables");
            String key = mRef.push().getKey();
            if(TextUtils.isEmpty(binding.edNameType.getText().toString())){
                Toast.makeText(context, "Hãy nhập tên bàn !"  , Toast.LENGTH_SHORT).show();
            }else {
                Table table = new Table(key,binding.edNameType.getText().toString().trim(),"false",true);
                mRef.child(key).setValue(table).addOnCompleteListener(task -> {
                    if (task.isSuccessful()){
                        Toast.makeText(getContext(), "Thêm bàn thành công", Toast.LENGTH_SHORT).show();
                    }else {
                        Toast.makeText(getContext(), "Thêm thất bại", Toast.LENGTH_SHORT).show();
                    }
                });

                dialog.dismiss();
            }
        });
        dialog.show();

    }

    private boolean menuItemClicked(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.tvAll:
                getAllTables();
                binding.tvFilterTable.setText("Tất cả bàn");
                binding.collapsingToolbar.setTitle("Danh sách bàn");
                break;
            case R.id.tvEmpty:
                getFilterTable("false");
                binding.tvFilterTable.setText("Bàn đang trống");
                binding.collapsingToolbar.setTitle("Danh sách bàn trống");
                break;
            case R.id.tvOpen:
                getFilterTable("true");
                binding.tvFilterTable.setText("Bàn đang mở");
                binding.collapsingToolbar.setTitle("Danh sách bàn đang mở");
                break;
            default:
                Toast.makeText(getContext(), item.getTitle(), Toast.LENGTH_SHORT).show();
                break;
        }

        return  true;
    }


    private void getFilterTable(String statusTable){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("tables");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listTable.clear();
                for (DataSnapshot snapshot1: snapshot.getChildren()) {
                    Table table = snapshot1.getValue(Table.class);
                    if(table.isHidden() && table.getStatus().equals(statusTable)){
                        listTable.add(table);
                    }
                }
                if(statusTable.equals("true")){
                    binding.tvNumberOfTable.setText("Có "+listTable.size()+" bàn đang mở");
                }else if(statusTable.equals("false")){
                    binding.tvNumberOfTable.setText("Có "+listTable.size()+" bàn đang trống");
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        adapter = new TableAdapter(listTable, FragmentListTablesToOder.this, FragmentListTablesToOder.this,getContext());
        binding.recViewTables.setAdapter(adapter);
    }


    public void getAllTables(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("tables");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listTable.clear();
                for (DataSnapshot snapshot1: snapshot.getChildren()) {
                    Table table = snapshot1.getValue(Table.class);
                    if(table.isHidden()){
                        listTable.add(table);
                    }
                }
                binding.tvNumberOfTable.setText("Có tất cả "+listTable.size()+" bàn ");
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        adapter = new TableAdapter(listTable, FragmentListTablesToOder.this, FragmentListTablesToOder.this,getContext());

        binding.recViewTables.setAdapter(adapter);
    }

    private void filterTables(String text){
        ArrayList<Table> filterTables = new ArrayList<>();
        for (Table table: listTable) {
            if(table.getName_table().toLowerCase().contains(text.toLowerCase())){
                filterTables.add(table);
            }
        }
        if(!filterTables.isEmpty()) {
            adapter.setFilterList(filterTables);
            binding.tvNumberOfTable.setText(filterTables.size() + " bàn");
        }
    }

    private void dialogFunctionTables(Context context, Table table) {
        final Dialog dialogFunctionTable = new Dialog(context);
        DialogFunctionTableBinding bindingDialog = DialogFunctionTableBinding.inflate(LayoutInflater.from(context));
        dialogFunctionTable.setContentView(bindingDialog.getRoot());
        Window window = dialogFunctionTable.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT,WindowManager.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        WindowManager.LayoutParams layoutParams = window.getAttributes();
        layoutParams.gravity = Gravity.BOTTOM;
        window.setAttributes(layoutParams);
        bindingDialog.dialogChooserFunction.setTranslationY(150);
        bindingDialog.dialogChooserFunction.animate().translationYBy(-150).setDuration(400);

        bindingDialog.tvChangeName.setOnClickListener(tv ->{
            final Dialog dialogChange = new Dialog(context);
            DialogAddTypeProductBinding bindingChange = DialogAddTypeProductBinding.inflate(LayoutInflater.from(context));
            dialogChange.setContentView(bindingChange.getRoot());
            dialogChange.setCancelable(false);
            Window window2 = dialogChange.getWindow();
            window2.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
            window2.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            bindingChange.tvTitle.setText("Sửa thông tin bàn");
            bindingChange.tvAdd.setText("Lưu");
            bindingChange.edNameType.setText(table.getName_table());
            bindingChange.edNameType.setHint("Nhập tên bàn");
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("tables");
            bindingChange.tvAdd.setOnClickListener(tv2 ->{
                if(TextUtils.isEmpty(bindingChange.edNameType.getText().toString())){
                    Toast.makeText(context, "Hãy nhập tên bàn !"  , Toast.LENGTH_SHORT).show();
                }else {
                    Table table1 = new Table(table.getId_table(), bindingChange.edNameType.getText().toString(), table.getStatus(),true);
                    reference.child(table.getId_table()).setValue(table1).addOnCompleteListener(task->{
                        if (task.isSuccessful()){
                            Toast.makeText(context, "Cập nhật thành công!", Toast.LENGTH_SHORT).show();
                            dialogChange.cancel();
                        }else {
                            Toast.makeText(context, "Cập nhật không thành công!", Toast.LENGTH_SHORT).show();
                            dialogChange.cancel();
                        }
                    });
                }

            });

            bindingChange.tvCancel.setOnClickListener(tv2 ->{
                dialogChange.dismiss();
            });

            dialogChange.show();
            dialogFunctionTable.cancel();
        });



        bindingDialog.tvDelete.setOnClickListener(tv ->{
            if(table.getStatus().equals("true")){
                Toast.makeText(context, "Bàn đang được mở.Không thể xóa", Toast.LENGTH_SHORT).show();
            }else {
                table.setHidden(false);
                DatabaseReference reference2 = FirebaseDatabase.getInstance().getReference("tables");
                reference2.child(table.getId_table()).setValue(table).addOnCompleteListener(task->{
                    if (task.isSuccessful()){
                        Toast.makeText(context, "Đã xóa", Toast.LENGTH_SHORT).show();
                        dialogFunctionTable.cancel();
                    }else {
                        Toast.makeText(context, "Xóa không thành công!", Toast.LENGTH_SHORT).show();
                        dialogFunctionTable.cancel();
                    }
                });
                adapter.notifyDataSetChanged();
            }

            dialogFunctionTable.cancel();
        });

        dialogFunctionTable.show();

    }


    private void showHideWhenScroll() {
        binding.recViewTables.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                //dy > 0: scroll up; dy < 0: scroll down
                if (dy > 0) binding.fabAddTable.hide();
                else  binding.fabAddTable.show();
                super.onScrolled(recyclerView, dx, dy);
            }
        });
    }



    @Override
    public void onClick(Table table) {
        replaceFragment(DetailTableFragment.newInstance(table));
    }

    @Override
    public void onLongClickTable(Table table) {
        if(user.isUserAuthorization()){
            dialogFunctionTables(getContext(),table);
        }else {
            notificationErrInput(getContext(),"Bạn không thể chỉnh sửa bàn!");

        }

    }
}