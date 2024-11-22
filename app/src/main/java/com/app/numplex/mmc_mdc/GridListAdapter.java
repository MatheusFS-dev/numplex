package com.app.numplex.mmc_mdc;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.numplex.R;
import com.app.numplex.utils.RecyclerViewInterface;

import java.util.ArrayList;

public class GridListAdapter extends RecyclerView.Adapter<GridListAdapter.ViewHolder> {
    private final ArrayList<Double> data;
    private final RecyclerViewInterface recyclerViewInterface;
    private final Context context;

    public GridListAdapter(ArrayList<Double> values, Context context, RecyclerViewInterface recyclerViewInterface) {
        data = values;
        this.context = context;
        this.recyclerViewInterface = recyclerViewInterface;
    }

    public ArrayList<Double> getData() {
        return data;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_list_icon_text, parent, false);
        return new ViewHolder(view, recyclerViewInterface, context);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Animation bounceAnim = AnimationUtils.loadAnimation(context, R.anim.bounce);
        final Double number = data.get(position);

        String text = String.valueOf(number);
        if(text.endsWith(".0"))
            text = text.replace(".0", "");

        holder.value.setText(text);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final View view;
        public final TextView value;

        public ViewHolder(View view, RecyclerViewInterface recyclerViewInterface, Context context) {
            super(view);
            this.view = view;
            value = view.findViewById(R.id.value);

            final Animation bounceAnim = AnimationUtils.loadAnimation(context, R.anim.bounce);

            view.setOnClickListener(view1 -> {
                if(recyclerViewInterface != null) {
                    int pos = getAdapterPosition();
                    if(pos != RecyclerView.NO_POSITION)
                        recyclerViewInterface.onItemClick(pos);
                }
                view.startAnimation(bounceAnim);
            });

            view.setOnLongClickListener(view12 -> {
                if (recyclerViewInterface != null) {
                    int pos = ViewHolder.this.getAdapterPosition();
                    if (pos != RecyclerView.NO_POSITION)
                        recyclerViewInterface.onItemLongClick(pos, view12);
                }
                view.startAnimation(bounceAnim);
                return true;
            });

        }
    }
}
