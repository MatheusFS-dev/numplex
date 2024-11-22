package com.app.numplex.matrix_complex;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.numplex.R;
import com.app.numplex.utils.ItemTouchHelperAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;

public class RecyclerViewGridComplexAdapter extends RecyclerView.Adapter<RecyclerViewGridComplexAdapter.ViewHolder> implements ItemTouchHelperAdapter {
    private final ArrayList<String> data;
    private final Context context;
    private final RecyclerView recyclerView;
    private final OnEditTextClickListener editTextClickListener;


    public RecyclerViewGridComplexAdapter(Context context, ArrayList<String> values, RecyclerView recyclerView, OnEditTextClickListener editTextClickListener) {
        data = values;
        this.context = context;
        this.recyclerView = recyclerView;
        this.editTextClickListener = editTextClickListener;
    }

    public ArrayList<String> getData() {
        return data;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_complex_matrix, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if(!data.get(position).equals("0") && !data.get(position).equals("-0"))
            holder.values.setText(data.get(position));
        if(holder.values.getText().toString().contains("Infinity"))
            holder.values.setText("∞");
        if(holder.values.getText().toString().contains("NaN"))
            holder.values.setText(R.string.error);

        holder.values.setOnClickListener(v -> {
            if (editTextClickListener != null)
                editTextClickListener.onEditTextClicked(holder.getAdapterPosition(), recyclerView, holder.values);
        });

        holder.values.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(!editable.toString().equals(""))
                    data.set(holder.getAdapterPosition(), editable.toString());
            }
        });

        holder.values.setOnEditorActionListener((v, actionId, event) -> {
            try{
                if (actionId == EditorInfo.IME_ACTION_NEXT) {
                    // Get the adapter position of the current ViewHolder
                    RecyclerView.ViewHolder viewHolder = recyclerView.findContainingViewHolder(v);
                    int currentPosition = Objects.requireNonNull(viewHolder).getAdapterPosition();
                    // Get the next EditText view in the right
                    int nextPosition = currentPosition + 1;

                    View nextView = Objects.requireNonNull(recyclerView.getLayoutManager()).findViewByPosition(nextPosition);
                    if (nextView != null)
                        nextView.requestFocus();
                    return true;
                }
            }catch (Exception ignored){}
            return false;
        });

        if(MatrixComplexActivity.isAnimEnabled){
            Animation anim = AnimationUtils.loadAnimation(context,R.anim.grid_item_anim);
            if(data.size() >= 48)
                anim.setStartOffset(position * 10L);
            if(data.size() >= 32)
                anim.setStartOffset(position * 30L);
            else
                anim.setStartOffset(position * 100L);
            holder.view.setAnimation(anim);
            anim.start();
        }
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
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final View view;
        public final EditText values;

        public ViewHolder(View view) {
            super(view);
            this.view = view;
            values = view.findViewById(R.id.text);
        }
    }

    public interface OnEditTextClickListener {
        void onEditTextClicked(int position, RecyclerView recyclerView, EditText editText);
    }
}

