package com.app.numplex.resistor;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.numplex.R;

import java.util.List;

public class ColorAdapter extends BaseAdapter {
    private Context context;
    private List<Colors> colorList;

    public ColorAdapter(Context context, List<Colors> fruitList) {
        this.context = context;
        this.colorList = fruitList;
    }

    @Override
    public int getCount() {
        return colorList != null ? colorList.size() : 0;
    }

    @Override
    public Object getItem(int i) {
        return i;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        @SuppressLint("ViewHolder") View rootView = LayoutInflater.from(context)
                .inflate(R.layout.item_resistor_spinner, viewGroup, false);

        TextView txtName = rootView.findViewById(R.id.name);
        ImageView image = rootView.findViewById(R.id.image);

        txtName.setText(colorList.get(i).getName());
        image.setImageResource(colorList.get(i).getImage());

        return rootView;
    }
}
