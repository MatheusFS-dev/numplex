package com.app.numplex.matrix;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.app.numplex.R;

import java.util.ArrayList;

public class MatrixListAdapter extends ArrayAdapter<Matrix> {
    private final ArrayList<Matrix> data;
    private final Context context;

    public MatrixListAdapter(Context context, ArrayList<Matrix> dataList) {
        super(context, R.layout.item_list_element,dataList);
        this.data = dataList;
        this.context = context;
    }

    @SuppressLint({"SetTextI18n", "InflateParams"})
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        Drawable selected = ContextCompat.getDrawable(context, R.drawable.ripple_effect_blue);
        Drawable notSelected = ContextCompat.getDrawable(context, R.drawable.ripple_effect_black);
        final Matrix matrix = getItem(position);
        ViewHolder viewHolder;

        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.item_list_element, null);
            viewHolder = new ViewHolder();
            viewHolder.container = convertView.findViewById(R.id.container);
            viewHolder.selection = convertView.findViewById(R.id.selection);
            viewHolder.name = convertView.findViewById(R.id.name);
            viewHolder.value = convertView.findViewById(R.id.value);
            viewHolder.icon = convertView.findViewById(R.id.icon);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        if(matrix.isChecked()) {
            if(!MatrixActivity.onlyOne) {
                viewHolder.selection.setVisibility(View.VISIBLE);
                viewHolder.selection.setText(matrix.getSelectionNumber());
            } else
                viewHolder.selection.setVisibility(View.GONE);
            viewHolder.container.setBackground(selected);
        } else {
            viewHolder.container.setBackground(notSelected);
            viewHolder.selection.setVisibility(View.GONE);
        }

        viewHolder.name.setText(matrix.getName());

        Drawable drawable = ContextCompat.getDrawable(context, R.drawable.ic_matrix);
        viewHolder.value.setText(matrix.getRows() + "x" + matrix.getCol());
        viewHolder.icon.setImageDrawable(drawable);

        return convertView;
    }

    public void toggleElementChecked(int pos){
        data.get(pos).setChecked(!data.get(pos).isChecked());

        // Problem of reorder solved here:
        notifyDataSetChanged();
    }

    public static class ViewHolder {
        LinearLayout container;
        TextView name;
        TextView value;
        TextView selection;
        ImageView icon;
    }
}