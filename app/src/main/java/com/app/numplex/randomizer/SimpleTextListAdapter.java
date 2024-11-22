package com.app.numplex.randomizer;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.app.numplex.R;

import java.util.ArrayList;

public class SimpleTextListAdapter extends ArrayAdapter<String> {
    private final ArrayList<String> data;
    private final Context context;

    public SimpleTextListAdapter(Context context, ArrayList<String> dataList) {
        super(context, R.layout.item_list_icon_text_2,dataList);
        this.data = dataList;
        this.context = context;
    }

    @SuppressLint("InflateParams")
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final String element = getItem(position);
        ViewHolder viewHolder;

        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.item_list_icon_text_2, null);
            viewHolder = new ViewHolder();
            viewHolder.container = convertView.findViewById(R.id.container);
            viewHolder.name = convertView.findViewById(R.id.name);
            convertView.setTag(viewHolder);
        } else
            viewHolder = (ViewHolder) convertView.getTag();

        viewHolder.name.setText(element);

        return convertView;
    }

    public static class ViewHolder {
        LinearLayout container;
        TextView name;
    }
}
