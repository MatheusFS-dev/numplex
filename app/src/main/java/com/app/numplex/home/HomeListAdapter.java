package com.app.numplex.home;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.app.numplex.R;
import com.varunest.sparkbutton.SparkButton;
import com.varunest.sparkbutton.SparkEventListener;

import java.util.ArrayList;

public class HomeListAdapter extends ArrayAdapter<HomeElements> {
    private final ArrayList<HomeElements> originalData;
    private final ArrayList<HomeElements> displayedData;
    private final Context context;
    private final RecyclerViewAdapter recyclerViewAdapter;
    private static boolean mayDoubleClick = true;

    public HomeListAdapter(Context context, ArrayList<HomeElements> dataList, RecyclerViewAdapter recyclerViewAdapter) {
        super(context, R.layout.item_list_element, dataList);
        this.originalData = dataList;
        this.context = context;
        displayedData = new ArrayList<>(originalData);
        this.recyclerViewAdapter = recyclerViewAdapter;
    }

    public static void setMayDoubleClick(boolean mayDoubleClick) {
        HomeListAdapter.mayDoubleClick = mayDoubleClick;
    }

    public ArrayList<HomeElements> getOriginalData() {
        return originalData;
    }

    @Override
    public int getCount() {
        return displayedData.size();
    }

    @SuppressLint("InflateParams")
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final HomeElements element = displayedData.get(position);
        ViewHolder viewHolder;

        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.item_home_elements, null);
            viewHolder = new ViewHolder();
            viewHolder.container = convertView.findViewById(R.id.container);
            viewHolder.name = convertView.findViewById(R.id.name);
            viewHolder.category = convertView.findViewById(R.id.category);
            viewHolder.icon = convertView.findViewById(R.id.icon);
            viewHolder.favorite = convertView.findViewById(R.id.favoriteButton);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        if (element.isFavorite())
            viewHolder.favorite.setChecked(true);
        else
            viewHolder.favorite.setChecked(false);

        Drawable drawable = null;
        String category = "";
        String description = "";
        String name = "";
        switch (element.getId()) {
            case "calculator" -> {
                name = context.getString(R.string.calculator);
                description = "";
                drawable = ContextCompat.getDrawable(context, R.drawable.ic_calculator);
                category = context.getString(R.string.category_math);
            }
            case "complex" -> {
                name = context.getString(R.string.complex_calculator);
                description = context.getString(R.string.desc_complex);
                drawable = ContextCompat.getDrawable(context, R.drawable.ic_home_complex);
                category = context.getString(R.string.category_math);
            }
            case "algebra" -> {
                name = context.getString(R.string.vector_calculator);
                description = context.getString(R.string.desc_algebra);
                drawable = ContextCompat.getDrawable(context, R.drawable.ic_home_algebra);
                category = context.getString(R.string.category_algebra);
            }
            case "matrix" -> {
                name = context.getString(R.string.matrix_calculator);
                description = context.getString(R.string.desc_matrix);
                drawable = ContextCompat.getDrawable(context, R.drawable.ic_matrix);
                category = context.getString(R.string.category_algebra);
            }
            case "matrixComplex" -> {
                name = context.getString(R.string.home_matrix_complex);
                description = context.getString(R.string.desc_matrix);
                drawable = ContextCompat.getDrawable(context, R.drawable.ic_complex_matrix);
                category = context.getString(R.string.category_algebra);
            }
            case "digital" -> {
                name = context.getString(R.string.numeric_base_converter);
                description = context.getString(R.string.desc_digital);
                drawable = ContextCompat.getDrawable(context, R.drawable.ic_home_digital);
                category = context.getString(R.string.category_converters);
            }
            case "resistor" -> {
                name = context.getString(R.string.resistor_color_code);
                description = context.getString(R.string.desc_resistor);
                drawable = ContextCompat.getDrawable(context, R.drawable.ic_home_resistor);
                category = context.getString(R.string.category_electronics);
            }
            case "mmc" -> {
                name = context.getString(R.string.mmc_mdc);
                description = context.getString(R.string.desc_mmc);
                drawable = ContextCompat.getDrawable(context, R.drawable.ic_fraction);
                category = context.getString(R.string.category_math);
            }
            case "randomizer" -> {
                name = context.getString(R.string.home_randomizer);
                description = context.getString(R.string.desc_rand);
                drawable = ContextCompat.getDrawable(context, R.drawable.ic_groups);
                category = context.getString(R.string.category_miscellaneous);
            }
            case "distribution" -> {
                name = context.getString(R.string.distribution);
                description = context.getString(R.string.desc_rand);
                drawable = ContextCompat.getDrawable(context, R.drawable.ic_dices);
                category = context.getString(R.string.category_miscellaneous);
            }
            case "series" -> {
                name = context.getString(R.string.home_series);
                description = context.getString(R.string.desc_series);
                drawable = ContextCompat.getDrawable(context, R.drawable.ic_series);
                category = context.getString(R.string.category_math);
            }
            case "boolean" -> {
                name = context.getString(R.string.home_boolean);
                description = context.getString(R.string.desc_boolean);
                drawable = ContextCompat.getDrawable(context, R.drawable.ic_boolean);
                category = context.getString(R.string.category_algebra);
            }
            case "kmap" -> {
                name = context.getString(R.string.home_karnaugh);
                description = context.getString(R.string.desc_karnaugh);
                drawable = ContextCompat.getDrawable(context, R.drawable.ic_kmap);
                category = context.getString(R.string.category_electronics);
            }
            case "network" -> {
                name = context.getString(R.string.home_network);
                description = context.getString(R.string.desc_network);
                drawable = ContextCompat.getDrawable(context, R.drawable.ic_network);
                category = context.getString(R.string.category_miscellaneous);
            }
            case "partial_fractions" -> {
                name = context.getString(R.string.partial_fractions);
                description = context.getString(R.string.desc_partial_fractions);
                drawable = ContextCompat.getDrawable(context, R.drawable.ic_partial_fractions);
                category = context.getString(R.string.category_math);
            }
            case "queue" -> {
                name = context.getString(R.string.queue);
                description = context.getString(R.string.description_queue);
                drawable = ContextCompat.getDrawable(context, R.drawable.ic_queue);
                category = context.getString(R.string.category_math);
            }
            case "dig_com" -> {
                name = context.getString(R.string.digital_communication);
                description = context.getString(R.string.digital_communication);
                drawable = ContextCompat.getDrawable(context, R.drawable.ic_dig_com);
                category = context.getString(R.string.category_miscellaneous);
            }
        }

        element.setName(name);
        viewHolder.name.setText(name);
        element.setDescription(description);
        viewHolder.category.setText(category);
        element.setCategory(category);
        viewHolder.icon.setImageDrawable(drawable);

        // Calculate the width of the TextView
        int width = parent.getWidth() - viewHolder.name.getPaddingLeft() - viewHolder.name.getPaddingRight();
        // Set the text size based on the width of the TextView
        viewHolder.name.setTextSize(TypedValue.COMPLEX_UNIT_PX, calculateTextSize(width));

        viewHolder.favorite.setAnimationSpeed(0.7f);
        viewHolder.favorite.setEventListener(new SparkEventListener() {
            @Override
            public void onEvent(ImageView button, boolean buttonState) {
                if (mayDoubleClick) {
                    toggleElementFavorite(position);
                    viewHolder.favorite.setChecked(element.isFavorite());
                }
            }

            @Override
            public void onEventAnimationEnd(ImageView button, boolean buttonState) {
                viewHolder.favorite.setChecked(element.isFavorite());
            }

            @Override
            public void onEventAnimationStart(ImageView button, boolean buttonState) {

            }
        });

        return convertView;
    }

    public void toggleElementFavorite(int pos) {
        displayedData.get(pos).setFavorite(!displayedData.get(pos).isFavorite());

        if (displayedData.get(pos).isFavorite())
            recyclerViewAdapter.addItem(displayedData.get(pos).getId());
        else
            recyclerViewAdapter.removeItem(displayedData.get(pos).getId());
        HomeActivity.elements = getOriginalData();
        HomeActivity homeActivity = new HomeActivity();
        homeActivity.saveData(context);
    }

    public void filter(CharSequence constraint) {
        displayedData.clear();
        if (constraint == null || constraint.length() == 0) {
            displayedData.addAll(originalData);
        } else {
            String filterPattern = constraint.toString().toLowerCase().trim();
            for (int i = 0; i < originalData.size(); i++) {
                String itemName = originalData.get(i).getName();
                String itemDesc = originalData.get(i).getDescription();
                String itemCat = originalData.get(i).getCategory();
                if (itemName != null && itemName.toLowerCase().contains(filterPattern)
                        || itemDesc != null && itemDesc.toLowerCase().contains(filterPattern)
                        || itemCat != null && itemCat.toLowerCase().contains(filterPattern)) {
                    displayedData.add(originalData.get(i));
                }
            }
        }
        notifyDataSetChanged();
    }

    private float calculateTextSize(int width) {
        // Calculate the text size based on the width of the TextView
        return Math.min(width * 0.05f, -26f);
    }

    public static class ViewHolder {
        LinearLayout container;
        TextView name;
        TextView category;
        ImageView icon;
        SparkButton favorite;
    }
}