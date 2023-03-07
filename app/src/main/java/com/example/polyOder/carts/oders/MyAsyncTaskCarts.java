package com.example.polyOder.carts.oders;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.SystemClock;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.andremion.counterfab.CounterFab;
import com.example.polyOder.R;
import com.example.polyOder.model.Receipt;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MyAsyncTaskCarts extends AsyncTask<Void, Integer, Void> {
    Activity contextParent;


    public MyAsyncTaskCarts(Activity contextParent) {
        this.contextParent = contextParent;

    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();


    }

    @Override
    protected Void doInBackground(Void... params) {
        for (int i = 0; i <= 100; i++) {
            SystemClock.sleep(100);
            publishProgress(i);
        }
        return null;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        CounterFab fabCarts =(CounterFab) contextParent.findViewById(R.id.fab_oder);
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("OderSave");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<Receipt> listData = new ArrayList<>();
                listData.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Receipt receipt = dataSnapshot.getValue(Receipt.class);
                    listData.add(receipt);
                }
                if(!listData.isEmpty()){
                    fabCarts.setCount(listData.size());
                }else {
                    fabCarts.setCount(0);
                }


            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });



    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);

    }
}
