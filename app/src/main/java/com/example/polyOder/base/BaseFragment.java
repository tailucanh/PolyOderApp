package com.example.polyOder.base;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.polyOder.MainActivity;
import com.example.polyOder.R;
import com.example.polyOder.databinding.LayoutNotificationInputBinding;
import com.example.polyOder.interfaces.IOnBackPressed;
import com.example.polyOder.model.Token;
import com.example.polyOder.pushNotification.FMC;
import com.example.polyOder.viewModel.TableViewModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

public abstract class BaseFragment extends Fragment  {
    MainActivity mainActivity;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initView();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mainActivity = (MainActivity)context;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadData();

    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    abstract public void loadData();

    abstract public void listening();

    abstract public void initObSever();

    abstract public void initView();

    public void replaceFragment(Fragment fragment) {
        getParentFragmentManager().beginTransaction().setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left,
                R.anim.slide_out_right).replace(R.id.fade_control, fragment).addToBackStack(null).commit();
    }

    public void backStack() {
        getParentFragmentManager().popBackStack();
    }

    public void hideBottomBar(){
        mainActivity.hideBottomBar();
    }
    public void showBottomBar(){
        mainActivity.showBottomBar();
    }


    public void visibleBottomBarOnScroll(RecyclerView recyclerView, NestedScrollView nestedScrollView){
        if(recyclerView != null){
            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                    if (dy > 0) {
                        hideBottomBar();
                    } else {
                        showBottomBar();
                    }
                }
            });
        }
        if(nestedScrollView != null){
            nestedScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
                @Override
                public void onScrollChange(@NonNull NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                    if (scrollY > oldScrollY) {
                       hideBottomBar();
                    }
                    if (scrollY < oldScrollY) {
                        showBottomBar();
                    }

                }
            });
        }


    }

    public void getDataFromFirebase(String path, ValueEventListener valueEventListener ) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(path);
        databaseReference.addValueEventListener(valueEventListener);
    }

    public void getDataFromFirebase2(String path, ValueEventListener valueEventListener ,ChildEventListener childEventListener) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(path);
        databaseReference.addValueEventListener(valueEventListener);
        databaseReference.addChildEventListener(childEventListener);
    }

    public void getDataFirebaseUser(String path, ValueEventListener valueEventListener) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.child(path).child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addListenerForSingleValueEvent(valueEventListener);
    }


    public void getConfirmResponse(Context context, String title,int icon, String message,String positiveText, DialogInterface.OnClickListener positiveClickListener, String negativeText, DialogInterface.OnClickListener negativeClickListener){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setIcon(context.getDrawable(icon));
        builder.setMessage(message);
        builder.setCancelable(false);
        builder.setPositiveButton(positiveText, positiveClickListener);
        builder.setNegativeButton(negativeText, negativeClickListener);
        AlertDialog sh = builder.create();
        sh.show();
    }



    public void setColorStatusBar(int color){
        Window window = getActivity().getWindow();
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        window.setStatusBarColor(color);
    }



    public void getImageObjFromStorageReference(String strGetReference, String idObj, ImageView imgLoad){
        StorageReference reference = FirebaseStorage.getInstance().getReference().child(strGetReference);
        reference.listAll().addOnSuccessListener(listResult -> {
            for (StorageReference files: listResult.getItems()){
                if(files.getName().equals(idObj)){
                    files.getDownloadUrl().addOnSuccessListener(uri -> {
                        if(getActivity() != null){
                            Glide.with(getActivity()).load(uri).into(imgLoad);
                        }

                    });
                }
            }
        });
    }

    public void pushMessage(TableViewModel model, String title, String message){
        model.liveDataListToken.observe(getViewLifecycleOwner(), new Observer<List<Token>>() {
            @Override
            public void onChanged(List<Token> tokens) {
                for (Token token: tokens
                ) {
                    FMC.pushNotification(requireContext(),token.getToken(),title, message);
                }
            }
        });
    }


}
