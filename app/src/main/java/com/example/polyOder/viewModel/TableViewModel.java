package com.example.polyOder.viewModel;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.polyOder.model.Product;
import com.example.polyOder.model.Receipt;
import com.example.polyOder.model.Token;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class TableViewModel extends ViewModel {

    public MutableLiveData<List<Product>> listProductOder = new MutableLiveData<>();
    public MutableLiveData<List<Product>> listProductOderSave = new MutableLiveData<>();
    public MutableLiveData<List<Product>> listProductTop = new MutableLiveData<>();
    public MutableLiveData<List<Product>> listProductTopMonth = new MutableLiveData<>();
    public MutableLiveData<String> oderTableStatus = new MutableLiveData<>();

    public MutableLiveData<Receipt> liveDataGetReceipt = new MutableLiveData<>();
    public MutableLiveData<String> liveDataPayReceipt = new MutableLiveData<>();
    public MutableLiveData<List<Token>> liveDataListToken = new MutableLiveData<>();
    public MutableLiveData<Receipt> liveDataGetCancelReceipt = new MutableLiveData<>();
    public MutableLiveData<String> liveDataCancelReceipt = new MutableLiveData<>();
    private DatabaseReference reference;

    public void getDataFromFirebase(String path, ValueEventListener valueEventListener ) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(path);
        databaseReference.addValueEventListener(valueEventListener);
    }


    public LiveData<List<Product>> listLiveData(List<String> listIdProduct) {
        ArrayList<Product> list = new ArrayList<>();
        for (String idProduct : listIdProduct) {
            getDataFromFirebase("list_product",new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()
                    ) {
                        Product product1 = dataSnapshot.getValue(Product.class);
                        if (product1.getId().equals(idProduct)) {
                            Log.d("TAG", "onDataChange: "+idProduct);
                            list.add(product1);
                        }
                    }
                    listProductOder.postValue(list);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        };

        return listProductOder;
    }
    public LiveData<List<Product>> listProductOderSave(List<String> listIdProduct) {
        ArrayList<Product> list = new ArrayList<>();
        for (String idProduct : listIdProduct) {
            getDataFromFirebase("list_product",new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()
                    ) {
                        Product product1 = dataSnapshot.getValue(Product.class);
                        if (product1.getId().equals(idProduct)) {

                            list.add(product1);
                        }
                    }
                    listProductOderSave.postValue(list);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        };

        return listProductOderSave;
    }
    public LiveData<List<Product>> listLiveTop(List<String> listIdProduct) {
        ArrayList<Product> list = new ArrayList<>();
        for (String idProduct : listIdProduct) {
            getDataFromFirebase("list_product",new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()
                    ) {
                        Product product1 = dataSnapshot.getValue(Product.class);
                        if (product1.getId().equals(idProduct)) {
                            Log.d("TAG", "onDataChange: "+idProduct);
                            list.add(product1);
                        }
                    }
                    listProductTop.postValue(list);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        };

        return listProductTop;
    }
    public LiveData<List<Product>> listLiveTopMonth(List<String> listIdProduct) {
        ArrayList<Product> list = new ArrayList<>();
        for (String idProduct : listIdProduct) {
            getDataFromFirebase("list_product",new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()
                    ) {
                        Product product1 = dataSnapshot.getValue(Product.class);
                        if (product1.getId().equals(idProduct)) {
                            Log.d("TAG", "onDataChange: "+idProduct);
                            list.add(product1);
                        }
                    }
                    listProductTopMonth.postValue(list);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        };

        return listProductTopMonth;
    }


    public LiveData<String> setStatusTable(String idTable, String status){
         reference = FirebaseDatabase.getInstance().getReference("tables");
         reference.child(idTable).child("status").setValue(status);
         oderTableStatus.postValue(status);
        return oderTableStatus;
    }

    public LiveData<Receipt> liveDataGetReceipt(String idTable){
        getDataFromFirebase("OderSave",new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot:snapshot.getChildren()
                     ) {
                     Receipt receipt1 =dataSnapshot.getValue(Receipt.class);
                     if (receipt1.getIdTable().equals(idTable)){
                         liveDataGetReceipt.postValue(receipt1);
                     }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        return liveDataGetReceipt;
    }
    public LiveData<Receipt> liveDataGetCancelReceipt(String idTable){
        getDataFromFirebase("CancelReceipt",new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot:snapshot.getChildren()
                ) {
                    Receipt receipt1 =dataSnapshot.getValue(Receipt.class);
                    if (receipt1.getIdTable().equals(idTable)){
                        liveDataGetCancelReceipt.postValue(receipt1);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        return liveDataGetCancelReceipt;
    }

    public LiveData<String> liveDataCancelReceipt(Receipt receipt){
        reference = FirebaseDatabase.getInstance().getReference("CancelReceipt");
        reference.child(receipt.getIdReceipt()).setValue(receipt);
        liveDataDeleteReceipt(receipt);
        liveDataCancelReceipt.postValue("cancel");
        return liveDataCancelReceipt;
    }





    public LiveData<String> liveDataPayReceipt(Receipt receipt){
        reference = FirebaseDatabase.getInstance().getReference("PayReceipt");
        reference.child(receipt.getIdReceipt()).setValue(receipt);
        liveDataDeleteReceipt(receipt);
        liveDataPayReceipt.postValue("success");
        return liveDataPayReceipt;
    }


    public LiveData<Receipt> liveDataDeleteReceipt(Receipt receipt){
        reference = FirebaseDatabase.getInstance().getReference("OderSave");
        Query query = reference.orderByChild("idReceipt").equalTo(receipt.getIdReceipt());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot: snapshot.getChildren()
                     ) {
                        dataSnapshot.getRef().removeValue();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        return  null;
    }
    public LiveData<List<Token>> liveDataGetAllToken(){
        ArrayList<Token> tokenList = new ArrayList<>();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        reference.child("Tokens").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot:snapshot.getChildren()
                     ) {
                    Token token = dataSnapshot.getValue(Token.class);
                    Log.d("TAG", "onDataChange: "+token.getToken());
                    tokenList.add(token);
                }
                liveDataListToken.postValue(tokenList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        return liveDataListToken;
    }


}
