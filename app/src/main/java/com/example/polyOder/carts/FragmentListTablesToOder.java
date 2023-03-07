package com.example.polyOder.carts;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
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

import com.example.polyOder.base.Helpers;
import com.example.polyOder.interfaces.OnTouchTheTable;
import com.example.polyOder.databinding.DialogAddTypeProductBinding;
import com.example.polyOder.databinding.DialogFunctionTableBinding;
import com.example.polyOder.databinding.FragmentTableToOderBinding;
import com.example.polyOder.model.Table;
import com.example.polyOder.model.User;
import com.example.polyOder.home.table.DetailTableFragment;
import com.example.polyOder.home.adapter.TableAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class FragmentListTablesToOder extends BaseFragment implements OnTouchTheTable {
    private FragmentTableToOderBinding binding;
    private TableAdapter adapter = null;
    private ArrayList<Table> listTable;
    private PopupMenu popupMenuTables;
    private User user;
    private Helpers helpers = new Helpers();



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
        listTable = new ArrayList<>();

        ((AppCompatActivity)getActivity()).setSupportActionBar(binding.toolbar);
        binding.collapsingToolbar.setExpandedTitleColor(getContext().getColor(android.R.color.transparent));
        binding.collapsingToolbar.setTitle("Danh sách bàn");

        hideBottomBar();
        listening();

    }

    @Override
    public void loadData() {

        getAllTables();
        getDataFirebaseUser("users", new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                user = snapshot.getValue(User.class);
                if (FirebaseAuth.getInstance().getCurrentUser().getUid() != null && !user.isUserAuthorization()) {
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
            binding.icArrow.animate().rotation(0).start();
            popupMenuTables = new PopupMenu(getContext(),binding.layoutFilterTable);
            popupMenuTables.inflate(R.menu.menu_popup_table);

            popupMenuTables.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    return menuItemClicked(item);
                }
            });
            popupMenuTables.setOnDismissListener(new PopupMenu.OnDismissListener() {
                @Override
                public void onDismiss(PopupMenu popupMenu) {
                    binding.icArrow.animate().rotation(180).start();
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


    public void dialogAddTable(Context context) {
        final Dialog dialog = new Dialog(context);
        DialogAddTypeProductBinding binding = DialogAddTypeProductBinding.inflate(LayoutInflater.from(context));
        dialog.setContentView(binding.getRoot());
        dialog.setCancelable(false);
        helpers.setLayoutDialog(dialog, 0, WindowManager.LayoutParams.WRAP_CONTENT);

        binding.tvTitle.setText("Thêm bàn trống mới");
        binding.edNameType.setHint("Nhập tên bàn");

        binding.tvCancel.setOnClickListener(tv ->{
            dialog.dismiss();
        });

        binding.tvAdd.setOnClickListener(add->{
            DatabaseReference mRef =  FirebaseDatabase.getInstance().getReference("tables");
            String key = mRef.push().getKey();
            if(helpers.isEmptyString(helpers.isConvertEditText(binding.edNameType))){
                Toast.makeText(context, "Hãy nhập tên bàn!"  , Toast.LENGTH_SHORT).show();
            }else {
                getConfirmResponse(getContext(), "Xác nhận", R.drawable.ic_exchange, "Bạn muốn thêm bàn mới ", "Đồng ý", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Table table = new Table(key,helpers.isConvertEditText(binding.edNameType),"false",true);
                        mRef.child(key).setValue(table).addOnCompleteListener(task -> {
                            if (task.isSuccessful()){
                                Toast.makeText(getContext(), "Thêm bàn thành công", Toast.LENGTH_SHORT).show();
                            }else {
                                Toast.makeText(getContext(), "Thêm thất bại", Toast.LENGTH_SHORT).show();
                            }
                        });
                        dialog.dismiss();
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
        getDataFromFirebase("tables",new ValueEventListener() {
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
                    visibleViewByList(listTable,"Có "+listTable.size()+" bàn đang mở","Chưa có bàn đang mở");
                }else if(statusTable.equals("false")){
                    visibleViewByList(listTable,"Có "+listTable.size()+" bàn đang trống","Đã hết bàn trống");
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        adapter = new TableAdapter(listTable,  FragmentListTablesToOder.this,getContext());
        binding.recViewTables.setAdapter(adapter);
    }


    public void getAllTables(){
        getDataFromFirebase("tables",new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listTable.clear();
                for (DataSnapshot snapshot1: snapshot.getChildren()) {
                    Table table = snapshot1.getValue(Table.class);
                    if(table.isHidden()){
                        listTable.add(table);
                    }
                }
                visibleViewByList(listTable,"Có tất cả "+listTable.size()+" bàn ","Chưa có dữ liệu bàn");

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        adapter = new TableAdapter(listTable, FragmentListTablesToOder.this,getContext());
        binding.recViewTables.setAdapter(adapter);
    }

    private void visibleViewByList(ArrayList arrayList, String strContent ,String strNull){
        if(!(helpers.isEmptyList(arrayList))){
            visibleViewData(View.VISIBLE, View.GONE , strContent,strNull);
            adapter.notifyDataSetChanged();
        }else {
            visibleViewData(View.GONE, View.VISIBLE , "",strNull);
        }

    }

    private void visibleViewData(int visible1,int visible2, String content, String notificator){
        binding.tvNumberOfTable.setText(content);
        binding.notifiNullData.setVisibility(visible2);
        binding.tvContentNull.setText(notificator);
        binding.recViewTables.setVisibility(visible1);
    }


    private void filterTables(String text){
        ArrayList<Table> filterTables = new ArrayList<>();
        for (Table table: listTable) {
            if(table.getName_table().toLowerCase().contains(text.toLowerCase())){
                filterTables.add(table);
            }
        }
        if(!(helpers.isEmptyList(filterTables))) {
            adapter.setFilterList(filterTables);
            visibleViewData(View.VISIBLE, View.GONE , "","");
        }else {
            visibleViewData(View.GONE, View.VISIBLE , "","Không có bàn  "+"\"" +text+"\"");
        }
    }



    private void dialogFunctionTables(Context context, Table table) {
        final Dialog dialogFunctionTable = new Dialog(context);
        DialogFunctionTableBinding bindingDialog = DialogFunctionTableBinding.inflate(LayoutInflater.from(context));
        dialogFunctionTable.setContentView(bindingDialog.getRoot());
        helpers.setLayoutDialog(dialogFunctionTable, Gravity.BOTTOM,WindowManager.LayoutParams.WRAP_CONTENT);

        bindingDialog.dialogChooserFunction.setTranslationY(150);
        bindingDialog.dialogChooserFunction.animate().translationYBy(-150).setDuration(400);

        bindingDialog.tvChangeName.setOnClickListener(tv ->{
            final Dialog dialogChange = new Dialog(context);
            DialogAddTypeProductBinding bindingChange = DialogAddTypeProductBinding.inflate(LayoutInflater.from(context));
            dialogChange.setContentView(bindingChange.getRoot());
            dialogChange.setCancelable(false);
            helpers.setLayoutDialog(dialogChange, 0,WindowManager.LayoutParams.WRAP_CONTENT);

            bindingChange.tvTitle.setText("Sửa thông tin bàn");
            bindingChange.tvAdd.setText("Lưu");
            bindingChange.edNameType.setText(table.getName_table());
            bindingChange.edNameType.setHint("Nhập tên bàn");
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("tables");
            bindingChange.tvAdd.setOnClickListener(tv2 ->{
                if(helpers.isEmptyString(bindingChange.edNameType.getText().toString())){
                    Toast.makeText(context, "Hãy nhập tên bàn !"  , Toast.LENGTH_SHORT).show();
                }else {
                    getConfirmResponse(context, "Xác nhận", R.drawable.ic_exchange, "Bạn muốn sửa "+table.getName_table()+" thành "+bindingChange.edNameType.getText().toString(), "Đồng ý", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Table table1 = new Table(table.getId_table(), bindingChange.edNameType.getText().toString(), table.getStatus(),true);
                            reference.child(table.getId_table()).setValue(table1).addOnCompleteListener(task->{
                                if (task.isSuccessful()){
                                    Toast.makeText(context, "Cập nhật thành công!", Toast.LENGTH_SHORT).show();
                                    dialogChange.cancel();
                                    dialogFunctionTable.cancel();
                                }else {
                                    Toast.makeText(context, "Cập nhật không thành công!", Toast.LENGTH_SHORT).show();
                                    dialogChange.cancel();
                                    dialogFunctionTable.cancel();
                                }
                            });
                        }
                    }, "Hủy", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Toast.makeText(getContext(),"Đã hủy !",Toast.LENGTH_SHORT).show();
                            dialogInterface.cancel();
                            dialogFunctionTable.cancel();
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
                getConfirmResponse(context, "Xác nhận", R.drawable.ic_delete, "Bạn muốn xóa "+table.getName_table(), "Đồng ý", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
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
                }, "Hủy", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(getContext(),"Đã hủy !",Toast.LENGTH_SHORT).show();
                        dialogInterface.cancel();
                        dialogFunctionTable.cancel();
                    }
                });
            }

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
    public void onClickTable(Table table) {
        replaceFragment(DetailTableFragment.newInstance(table));
    }

    @Override
    public void onLongClickTable(Table table) {

        if(user.isUserAuthorization()){
            dialogFunctionTables(getContext(),table);
        }else {
            helpers.notificationErrInput(getContext(),"Bạn không thể chỉnh sửa bàn!");
        }

    }
}