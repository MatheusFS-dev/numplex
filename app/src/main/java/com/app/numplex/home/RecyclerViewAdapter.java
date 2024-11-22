package com.app.numplex.home;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.app.numplex.R;
import com.app.numplex.algebra.AlgebraActivity;
import com.app.numplex.booleanSimplifier.BooleanActivity;
import com.app.numplex.calculator.CalculatorActivity;
import com.app.numplex.complex_calculator.ComplexCalculatorActivity;
import com.app.numplex.digital.DigitalActivity;
import com.app.numplex.digital_communication.DigitalCommunicationActivity;
import com.app.numplex.distributions.DistributionsActivity;
import com.app.numplex.karnaugh.KarnaughActivity;
import com.app.numplex.matrix.MatrixActivity;
import com.app.numplex.matrix_complex.MatrixComplexActivity;
import com.app.numplex.mmc_mdc.MMC_MDC_Activity;
import com.app.numplex.network.NetworkActivity;
import com.app.numplex.partial_fractions.PartialFractionActivity;
import com.app.numplex.queue.QueueActivity;
import com.app.numplex.randomizer.RandomizerActivity;
import com.app.numplex.resistor.ResistorActivity;
import com.app.numplex.series.SeriesActivity;
import com.app.numplex.utils.ItemTouchHelperAdapter;

import java.util.ArrayList;
import java.util.Collections;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> implements ItemTouchHelperAdapter {
    private final ArrayList<String> data;
    private final Context context;
    private final TextView favoritesText;
    private final RecyclerView recyclerView;
    private boolean isDragging = false;

    public RecyclerViewAdapter(Context context, ArrayList<String> values, TextView favoritesText, RecyclerView recyclerView) {
        data = values;
        this.context = context;
        this.favoritesText = favoritesText;
        this.recyclerView = recyclerView;
    }

    public void setDragging(boolean dragging) {
        isDragging = dragging;
    }

    public ArrayList<String> getData() {
        return data;
    }

    public void addItem(String item) {
        final Animation bounceAnim = AnimationUtils.loadAnimation(context, R.anim.bounce_in);
        if(data.isEmpty()) {
            data.add(item);
            notifyItemInserted(0);

            new Handler().postDelayed(() -> {
                favoritesText.setVisibility(View.VISIBLE);
                favoritesText.startAnimation(bounceAnim);
            }, 200);

            new Handler().postDelayed(() -> {
                recyclerView.setVisibility(View.VISIBLE);
                recyclerView.startAnimation(bounceAnim);
            }, 500);
        }else{
            data.add(item);
            notifyItemInserted(data.size() - 1);
        }
        HomeActivity.favorites = getData();
    }

    public void removeItem(String item) {
        int pos = data.indexOf(item);

        if(data.size() == 1){
            HomeListAdapter.setMayDoubleClick(false);

            bounceOutAnim(favoritesText);
            new Handler().postDelayed(() -> bounceOutAnim(recyclerView), 200);

            new Handler().postDelayed(() -> {
                data.remove(item);
                notifyItemRemoved(pos);
                HomeActivity.favorites = getData();
            }, 400);

            new Handler().postDelayed(() -> HomeListAdapter.setMayDoubleClick(true), 1000);
        }else{
            data.remove(item);
            notifyItemRemoved(pos);
            HomeActivity.favorites = getData();
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_favorites, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.itemView.setVisibility(View.VISIBLE);
        holder.itemView.setScaleX(1.0f);
        holder.itemView.setScaleY(1.0f);
        final Animation bounceAnim = AnimationUtils.loadAnimation(context, R.anim.bounce);
        String name = null;
        Drawable drawable = null;

        switch (data.get(position)) {
            case "calculator" -> {
                name = context.getString(R.string.calculator);
                drawable = ContextCompat.getDrawable(context, R.drawable.ic_calculator);
            }
            case "complex" -> {
                name = context.getString(R.string.home_complex);
                drawable = ContextCompat.getDrawable(context, R.drawable.ic_home_complex);
            }
            case "algebra" -> {
                name = context.getString(R.string.home_algebra);
                drawable = ContextCompat.getDrawable(context, R.drawable.ic_home_algebra);
            }
            case "matrix" -> {
                name = context.getString(R.string.home_matrix);
                drawable = ContextCompat.getDrawable(context, R.drawable.ic_matrix);
            }
            case "matrixComplex" -> {
                name = context.getString(R.string.home_matrix);
                drawable = ContextCompat.getDrawable(context, R.drawable.ic_complex_matrix);
            }
            case "digital" -> {
                name = context.getString(R.string.home_digital);
                drawable = ContextCompat.getDrawable(context, R.drawable.ic_home_digital);
            }
            case "resistor" -> {
                name = context.getString(R.string.home_resistor);
                drawable = ContextCompat.getDrawable(context, R.drawable.ic_home_resistor);
            }
            case "mmc" -> {
                name = context.getString(R.string.mmc_mdc);
                drawable = ContextCompat.getDrawable(context, R.drawable.ic_fraction);
            }
            case "randomizer" -> {
                name = context.getString(R.string.home_randomizer);
                drawable = ContextCompat.getDrawable(context, R.drawable.ic_groups);
            }
            case "distribution" -> {
                name = context.getString(R.string.distribution);
                drawable = ContextCompat.getDrawable(context, R.drawable.ic_dices);
            }
            case "series" -> {
                name = context.getString(R.string.home_series2);
                drawable = ContextCompat.getDrawable(context, R.drawable.ic_series);
            }
            case "boolean" -> {
                name = context.getString(R.string.home_boolean2);
                drawable = ContextCompat.getDrawable(context, R.drawable.ic_boolean);
            }
            case "kmap" -> {
                name = context.getString(R.string.home_karnaugh);
                drawable = ContextCompat.getDrawable(context, R.drawable.ic_kmap);
            }
            case "network" -> {
                name = context.getString(R.string.home_network);
                drawable = ContextCompat.getDrawable(context, R.drawable.ic_network);
            }
            case "partial_fractions" -> {
                name = context.getString(R.string.partial_fractions);
                drawable = ContextCompat.getDrawable(context, R.drawable.ic_partial_fractions);
            }
            case "queue" -> {
                name = context.getString(R.string.queue_2);
                drawable = ContextCompat.getDrawable(context, R.drawable.ic_queue);
            }
            case "dig_com" -> {
                name = context.getString(R.string.digital_communication);
                drawable = ContextCompat.getDrawable(context, R.drawable.ic_dig_com);
            }
        }

        holder.name.setText(name);
        holder.icon.setImageDrawable(drawable);
        holder.container.setOnClickListener(view -> {
            holder.container.startAnimation(bounceAnim);
            if(!isDragging) {
                Intent intent = switch (data.get(position)) {
                    case "calculator" -> new Intent(context, CalculatorActivity.class);
                    case "complex" -> new Intent(context, ComplexCalculatorActivity.class);
                    case "algebra" -> new Intent(context, AlgebraActivity.class);
                    case "matrix" -> new Intent(context, MatrixActivity.class);
                    case "matrixComplex" -> new Intent(context, MatrixComplexActivity.class);
                    case "digital" -> new Intent(context, DigitalActivity.class);
                    case "resistor" -> new Intent(context, ResistorActivity.class);
                    case "mmc" -> new Intent(context, MMC_MDC_Activity.class);
                    case "randomizer" -> new Intent(context, RandomizerActivity.class);
                    case "distribution" -> new Intent(context, DistributionsActivity.class);
                    case "series" -> new Intent(context, SeriesActivity.class);
                    case "boolean" -> new Intent(context, BooleanActivity.class);
                    case "kmap" -> new Intent(context, KarnaughActivity.class);
                    case "network" -> new Intent(context, NetworkActivity.class);
                    case "partial_fractions" -> new Intent(context, PartialFractionActivity.class);
                    case "queue" -> new Intent(context, QueueActivity.class);
                    case "dig_com" -> new Intent(context, DigitalCommunicationActivity.class);
                    default -> null;
                };

                new Handler().postDelayed(() -> context.startActivity(intent), 100);
            }
        });

        holder.container.setLongClickable(false);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    @Override
    public void onItemMove(int fromPosition, int toPosition) {
        if (fromPosition < toPosition) {
            for (int i = fromPosition; i < toPosition; i++) {
                Collections.swap(data, i, i + 1);
            }
        } else {
            for (int i = fromPosition; i > toPosition; i--) {
                Collections.swap(data, i, i - 1);
            }
        }
        notifyItemMoved(fromPosition, toPosition);
        HomeActivity homeActivity = new HomeActivity();
        homeActivity.saveData(context);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final View view;
        public final TextView name;
        public final ImageView icon;
        public final LinearLayout container;

        public ViewHolder(View view) {
            super(view);
            this.view = view;
            icon = view.findViewById(R.id.icon);
            name = view.findViewById(R.id.name);
            container = view.findViewById(R.id.favoriteContainer);
        }
    }

    private void bounceOutAnim(View view) {
        if (view.getVisibility() == View.VISIBLE) {
            final Animation bounceOut = AnimationUtils.loadAnimation(context, R.anim.bounce_out);
            view.startAnimation(bounceOut);
            new Handler().postDelayed(() -> view.setVisibility(View.GONE), 200);
        }
    }
}