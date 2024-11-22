package com.app.numplex.algebra;

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

public class AlgebraListAdapter extends ArrayAdapter<Elements> {
    private final ArrayList<Elements> data;
    private final Context context;

    public AlgebraListAdapter(Context context, ArrayList<Elements> dataList) {
        super(context, R.layout.item_list_element,dataList);
        this.data = dataList;
        this.context = context;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        Drawable selected = ContextCompat.getDrawable(context, R.drawable.ripple_effect_blue);
        Drawable notSelected = ContextCompat.getDrawable(context, R.drawable.ripple_effect_black);
        final Elements element = getItem(position);
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

        if(element.isChecked()) {
            if(!AlgebraActivity.onlyOne) {
                viewHolder.selection.setVisibility(View.VISIBLE);
                viewHolder.selection.setText(element.getSelectionNumber());
            } else
                viewHolder.selection.setVisibility(View.GONE);
            viewHolder.container.setBackground(selected);
        } else {
            viewHolder.container.setBackground(notSelected);
            viewHolder.selection.setVisibility(View.GONE);
        }

        viewHolder.name.setText(element.getName());

        Drawable drawable = null;
        switch (element.getType()){
            case "vector":
                drawable = ContextCompat.getDrawable(context, R.drawable.ic_arrow);
                break;
            case "plane":
                drawable = ContextCompat.getDrawable(context, R.drawable.ic_plane);
                break;
            case "point":
                drawable = ContextCompat.getDrawable(context, R.drawable.ic_point);
                break;
            case "line":
                drawable = ContextCompat.getDrawable(context, R.drawable.ic_line);
                break;
        }
        viewHolder.value.setText(AlgebraActivity.arrayToString(element.getValues(), element.getType()));
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