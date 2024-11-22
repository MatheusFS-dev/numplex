package com.app.numplex.complex_calculator;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.app.numplex.R;

import java.util.ArrayList;

public class HistoryListAdapter extends ArrayAdapter<HistoryData> {
    private final Context context;
    private int prevPos = 0;

    public HistoryListAdapter(Context context, ArrayList<HistoryData> dataList) {
        super(context, R.layout.item_history,dataList);
        this.context = context;
    }

    @SuppressLint("InflateParams")
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.item_history, null);
            viewHolder = new ViewHolder();
            viewHolder.expression = convertView.findViewById(R.id.historyExpression);
            viewHolder.result = convertView.findViewById(R.id.historyResult);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        final HistoryData myDataItem = getItem(position);

        viewHolder.expression.setText(myDataItem.getExpression());
        viewHolder.result.setText(myDataItem.getResult());

        if(ComplexCalculatorActivity.isHistoryAnimationEnabled)
            doCards(convertView, position, prevPos);
        prevPos = position; //take prevPos as global variable of int

        return convertView;
    }

    public static class ViewHolder {
        TextView expression;
        TextView result;
    }

    public static void doCards(View view, int position, int prevPosition) {
        view.setScaleX(0.5f);
        if (position > prevPosition) {
            view.setRotationX(90);
        } else {
            view.setRotationX(-90);
        }
        view.animate().rotationX(0).scaleX(1.0f).start();
    }
}
