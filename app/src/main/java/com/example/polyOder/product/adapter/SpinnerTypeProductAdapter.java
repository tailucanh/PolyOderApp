package com.example.polyOder.product.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.polyOder.R;
import com.example.polyOder.model.TypeProduct;

import java.util.ArrayList;

public class SpinnerTypeProductAdapter extends BaseAdapter {
    ArrayList<TypeProduct> listType;

    public SpinnerTypeProductAdapter(ArrayList<TypeProduct> listType) {
        this.listType = listType;
    }

    @Override
    public int getCount() {
        return listType.size();
    }

    @Override
    public Object getItem(int position) {
        TypeProduct objType = listType.get(position);
        return objType;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View itemSpinner;

        if (convertView == null) {
            itemSpinner = View.inflate(parent.getContext(), R.layout.layout_spiner_type_product, null);
        } else {
            itemSpinner = convertView;
        }
        TextView tv_name = itemSpinner.findViewById(R.id.tvSpinnerType);
        TypeProduct objType = listType.get(position);
        tv_name.setText(objType.getNameType());
        return itemSpinner;
    }
}
