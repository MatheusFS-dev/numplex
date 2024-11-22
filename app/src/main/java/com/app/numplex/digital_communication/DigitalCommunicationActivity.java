package com.app.numplex.digital_communication;

import static com.app.numplex.utils.Animations.bounceOutAnim;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.BounceInterpolator;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.airbnb.lottie.LottieAnimationView;
import com.app.numplex.R;
import com.app.numplex.algebra.AlgebraAnimationHelper;
import com.app.numplex.algebra.AlgebraListAdapter;
import com.app.numplex.algebra.Elements;
import com.app.numplex.application.AppRater;
import com.app.numplex.application.AppStatus;
import com.app.numplex.utils.DuoDrawerSetter;
import com.app.numplex.utils.Functions;
import com.app.numplex.utils.LottieDialog;
import com.app.numplex.utils.MultipleClickListener;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

import nl.psdcompany.duonavigationdrawer.views.DuoDrawerLayout;

public class DigitalCommunicationActivity extends AppCompatActivity{

    private DuoDrawerLayout drawerLayout;

    private EditText currentSelection = null;

    private ArrayList<Elements> elements = new ArrayList<>();

    private SwipeMenuListView listView;
    private AlgebraListAdapter listAdapter;

    private BottomSheetDialog addDialog;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /*----------------------------------------------------------------------------------------*/
        // Loading configs:
        Functions.updateConfigs(this, this);
        /*----------------------------------------------------------------------------------------*/

        setContentView(R.layout.activity_digital_communication);

        AppStatus.checkAccess(this);

        /*----------------------------------------------------------------------------------------*/
        // Navigation menu
        setDuoNavigationDrawer();

        //Toolbar
        Functions.setToolbar(this, getResources().getString(R.string.digital_communication), drawerLayout, null);

        final Animation bounceAnim = AnimationUtils.loadAnimation(this, R.anim.bounce);
        ImageButton help = findViewById(R.id.help);

        help.setOnClickListener(view -> {
            help.startAnimation(bounceAnim);
            //TODO
        });

        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Code to handle before text changed
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Code to handle text changed
            }

            @Override
            public void afterTextChanged(Editable s) {
                calculate();
            }
        };

        EditText symbols_probabilities = findViewById(R.id.symbols_probabilities);
        EditText noise_power = findViewById(R.id.noise_power);

        View.OnFocusChangeListener focusChangeListener = (v, hasFocus) -> {
            if (hasFocus)
                currentSelection = (EditText) v;
        };

        symbols_probabilities.setOnFocusChangeListener(focusChangeListener);
        symbols_probabilities.addTextChangedListener(textWatcher);

        noise_power.setOnFocusChangeListener(focusChangeListener);
        noise_power.addTextChangedListener(textWatcher);

        listView = findViewById(R.id.list);
        setListView();

        //Floating Action Buttons
        FloatingActionButton fabAdd = findViewById(R.id.fab);
        fabAdd.setOnClickListener(new MultipleClickListener() {
            @Override
            public void onDoubleClick() {
                // Do nothing
            }

            @Override
            public void onSingleClick() {
                if(currentSelection != null)
                    currentSelection.clearFocus();
                fabAdd.startAnimation(bounceAnim);
                openAddDialog(false, 0);
            }
        });

        FloatingActionButton fabClear = findViewById(R.id.fabClear);
        fabClear.setOnClickListener(view -> {
            if(currentSelection != null)
                currentSelection.clearFocus();
            fabClear.startAnimation(bounceAnim);
            if (!elements.isEmpty()) {
                elements.clear();
                saveData();
                LottieDialog customDialog = new LottieDialog(this);
                customDialog.startDeleteDialog();

                new Handler().postDelayed(this::setListView, 3000);
            }
        });

        /*----------------------------------------------------------------------------------------*/
        //App rater:
        AppRater.app_launched(this, this);

        calculate();
    }

    private void calculate() {
        TextView union_bound = findViewById(R.id.union_bound);
        TextView origin_distance = findViewById(R.id.origin_distance);
        TextView energies = findViewById(R.id.energies);
        TextView distance_between_symbols = findViewById(R.id.distance_between_symbols);

        try {
            double[] symbolProbabilities = getSymbolsProbabilitiesText();
            double noisePower = getNoisePowerText();

            // Clear points in Constellation before adding new ones
            Constellation.clearSymbols();

            Point[] points = new Point[elements.size()];
            for (int i = 0; i < elements.size(); i++) {
                Elements element = elements.get(i);
                double[] values = element.getValues();
                if(values.length >= 2) { // Ensure the element has both x and y coordinates
                    points[i] = new Point(values[0], values[1]);
                    Constellation.addSymbol(points[i]);
                }
            }

            StringBuilder originDistanceBuilder = new StringBuilder();
            StringBuilder energiesBuilder = new StringBuilder();

            for (int i = 0; i < points.length; i++) {
                Point point = points[i];
                double distanceToOrigin = Constellation.calculateDistanceToOrigin(point);
                double energy = Constellation.calculateEnergy(point);

                originDistanceBuilder.append("S").append(i + 1).append(": ").append(distanceToOrigin).append("\n");
                energiesBuilder.append("S").append(i + 1).append(": ").append(energy).append("\n");
            }

            if (originDistanceBuilder.length() > 0) {
                originDistanceBuilder.setLength(originDistanceBuilder.length() - 1);
            }

            origin_distance.setText(originDistanceBuilder.toString());
            energies.setText(energiesBuilder.toString());

            StringBuilder distanceBetweenSymbolsBuilder = new StringBuilder();
            String to = getResources().getString(R.string.to_word);

            for (int i = 0; i < points.length; i++) {
                for (int j = i + 1; j < points.length; j++) {
                    double distance = Constellation.calculateDistance(points[i], points[j]);
                    distanceBetweenSymbolsBuilder.append("S")
                            .append(i + 1)
                            .append(" ")
                            .append(to)
                            .append(" S")
                            .append(j + 1)
                            .append(": ")
                            .append(distance)
                            .append("\n");
                }
            }

            if (energiesBuilder.length() > 0) {
                energiesBuilder.setLength(energiesBuilder.length() - 1);
            }

            distance_between_symbols.setText(distanceBetweenSymbolsBuilder.toString());

            if(symbolProbabilities != null && noisePower != 0) {
                double unionBounding = Constellation.calculateUnionBounding(points, symbolProbabilities, noisePower);
                union_bound.setText(String.valueOf(unionBounding));
            }
            else
                union_bound.setText(null);
        }
        catch (Exception e) {
            union_bound.setText(null);
            energies.setText(null);
            distance_between_symbols.setText(null);
            origin_distance.setText(null);
        }
    }

    /*----------------------------------------------------------------------------------------*/
    public double[] getSymbolsProbabilitiesText() {
        EditText symbols_probabilities = findViewById(R.id.symbols_probabilities);
        String text = symbols_probabilities.getText().toString();
        String[] parts = text.split(",");
        double[] symbolProbabilities = new double[parts.length];

        if(!text.equals("")) {
            for (int i = 0; i < parts.length; i++) {
                symbolProbabilities[i] = Double.parseDouble(parts[i].trim());
            }

            return symbolProbabilities;
        }
        else
            return null;
    }
    public double getNoisePowerText() {
        EditText noise_power = findViewById(R.id.noise_power);
        if(!noise_power.getText().toString().equals(""))
            return Double.parseDouble(noise_power.getText().toString());
        else
            return 0;
    }

    /*--------------------------------------------------------------------------------------------*/
    // Bottom dialog:
    private void openAddDialog(boolean edit, int editPos) {
        final Animation bounceAnim = AnimationUtils.loadAnimation(this, R.anim.bounce_in);
        addDialog = new BottomSheetDialog(this);
        addDialog.setContentView(R.layout.bottom_dialog_dig_com);

        Button save = addDialog.findViewById(R.id.save);
        LinearLayout vector = addDialog.findViewById(R.id.vector);
        LinearLayout point = addDialog.findViewById(R.id.point);
        LinearLayout line = addDialog.findViewById(R.id.line);
        LinearLayout plane = addDialog.findViewById(R.id.plane);

        /*----------------------------------------------------------------------------------------*/
        //Spinner
        Spinner spinner = addDialog.findViewById(R.id.addSpinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(DigitalCommunicationActivity.this,
                R.array.digital_com_element_array, R.layout.item_spinner_text);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Objects.requireNonNull(spinner).setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                EditText auxToClearFocus;
                switch (position) {
                    case 0 -> {
                        // Point
                        Objects.requireNonNull(vector).setVisibility(View.GONE);
                        Objects.requireNonNull(line).setVisibility(View.GONE);
                        Objects.requireNonNull(plane).setVisibility(View.GONE);
                        Objects.requireNonNull(point).setVisibility(View.VISIBLE);
                        point.startAnimation(bounceAnim);
                        auxToClearFocus = addDialog.findViewById(R.id.pointX);
                        Objects.requireNonNull(auxToClearFocus).requestFocus();
                    }
                }
            }

            public void onNothingSelected(AdapterView<?> parent) {
                //Do nothing
            }
        });
        /*----------------------------------------------------------------------------------------*/
        // Editing?
        if (edit) {
            LinearLayout spinnerContainer = addDialog.findViewById(R.id.spinnerContainer);
            Objects.requireNonNull(spinnerContainer).setVisibility(View.GONE);
            Elements target = elements.get(editPos);
            EditText numbers;
            EditText name;
            switch (target.getType()) {
                case "point":
                    spinner.setSelection(1);
                    Objects.requireNonNull(point).setVisibility(View.VISIBLE);
                    point.startAnimation(bounceAnim);

                    numbers = addDialog.findViewById(R.id.pointX);
                    Objects.requireNonNull(numbers).setText(parseString(String.valueOf(target.getValues()[0])));

                    numbers = addDialog.findViewById(R.id.pointY);
                    Objects.requireNonNull(numbers).setText(parseString(String.valueOf(target.getValues()[1])));

                    name = addDialog.findViewById(R.id.pointName);
                    Objects.requireNonNull(name).setText(target.getName());
                    break;
            }
        }

        /*----------------------------------------------------------------------------------------*/
        // Save button
        Objects.requireNonNull(save).setOnClickListener(view -> {
            boolean shouldSave = false;
            int zeroCount = 0;
            double[] values;
            EditText name;
            EditText numbers;
            Elements newElement = new Elements();
            try {
                switch (spinner.getSelectedItemPosition()) {
                    case 0:
                        // Point
                        values = new double[3];
                        name = addDialog.findViewById(R.id.pointName);
                        newElement.setType("point");
                        if (!Objects.requireNonNull(name).getText().toString().equals(""))
                            newElement.setName(Objects.requireNonNull(name).getText().toString());
                        else
                            newElement.setName(getResources().getString(R.string.symbol) + " " + (elements.size() + 1));

                        numbers = addDialog.findViewById(R.id.pointX);
                        if (!Objects.requireNonNull(numbers).getText().toString().equals(""))
                            values[0] = Double.parseDouble(Objects.requireNonNull(numbers).getText().toString());
                        else {
                            zeroCount++;
                            values[0] = 0.0;
                        }

                        numbers = addDialog.findViewById(R.id.pointY);
                        if (!Objects.requireNonNull(numbers).getText().toString().equals(""))
                            values[1] = Double.parseDouble(Objects.requireNonNull(numbers).getText().toString());
                        else {
                            zeroCount++;
                            values[1] = 0.0;
                        }

                        if (zeroCount != 2)
                            shouldSave = true;
                        newElement.setValues(values);
                        break;
                }
                if (shouldSave) {
                    if (!edit)
                        elements.add(newElement);
                    else
                        elements.set(editPos, newElement);
                    listAdapter.notifyDataSetChanged();

                    if (!elements.isEmpty()) {
                        TextView textEmpty = findViewById(R.id.emptyText);
                        LottieAnimationView empty = findViewById(R.id.empty);
                        bounceOutAnim(empty, this);
                        bounceOutAnim(textEmpty, this);
                    }
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.saved), Toast.LENGTH_SHORT).show();
                    saveData();
                }
                calculate();
            } catch (Exception e) {
                Toast.makeText(getApplicationContext(),
                        getResources().getString(R.string.something_went_wrong),
                        Toast.LENGTH_SHORT).show();
            }
            addDialog.dismiss();
        });

        addDialog.show();
    }

    private String parseString(String string) {
        // Removes some unwanted characters
        if (string.endsWith(".0"))
            string = string.replace(".0", "");
        if (string.equals("0"))
            string = "";
        return string;
    }

    private void setListView() {
        loadData();
        final Animation bounceAnim = AnimationUtils.loadAnimation(this, R.anim.bounce_in);
        listAdapter = new AlgebraListAdapter(this, elements);

        /* --------------------------------- Swipe option -------------------------------- */
        SwipeMenuCreator creator = menu -> {
            // create "delete" item
            SwipeMenuItem deleteItem = new SwipeMenuItem(
                    getApplicationContext());
            // set item background
            deleteItem.setBackground(ContextCompat.getDrawable(this, R.drawable.bg_swipe_delete_circle));
            // set item width
            deleteItem.setWidth(140);
            // set a icon
            deleteItem.setIcon(R.drawable.ic_baseline_delete_24);
            // add to menu
            menu.addMenuItem(deleteItem);
        };

        // set creator
        listView.setMenuCreator(creator);
        listView.setCloseInterpolator(new BounceInterpolator());
        listView.setOpenInterpolator(new BounceInterpolator());
        listView.setSwipeDirection(SwipeMenuListView.DIRECTION_LEFT);

        listView.setOnMenuItemClickListener((position, menu, index) -> {
            if (index == 0) {// delete
                AlgebraAnimationHelper helper = new AlgebraAnimationHelper(listAdapter, listView, elements);

                helper.animateRemoval(listView, getViewByPosition(position, listView));

                if (elements.isEmpty()) {
                    LottieAnimationView empty = findViewById(R.id.empty);
                    TextView textEmpty = findViewById(R.id.emptyText);
                    empty.setVisibility(View.VISIBLE);
                    empty.startAnimation(bounceAnim);
                    textEmpty.setVisibility(View.VISIBLE);
                    textEmpty.startAnimation(bounceAnim);
                }
                listAdapter.notifyDataSetChanged();
            }
            return true;
        });

        /* ------------------------------------------------------------------------------- */

        listView.setAdapter(listAdapter);
        listView.setOnItemLongClickListener((arg0, v, index, arg3) -> {
            if(currentSelection != null)
                currentSelection.clearFocus();
            PopupMenu popup = new PopupMenu(DigitalCommunicationActivity.this, v);
            MenuInflater inflater = popup.getMenuInflater();
            inflater.inflate(R.menu.edit_copy_delete_menu_algebra, popup.getMenu());

            popup.setOnMenuItemClickListener(item -> {
                ClipboardManager clipboardManager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);

                int itemId = item.getItemId();
                if (itemId == R.id.copy) {
                    ClipData clipData = ClipData.newPlainText("text", Arrays.toString(elements.get(index).getValues()));
                    clipboardManager.setPrimaryClip(clipData);

                    Toast.makeText(getApplicationContext(), R.string.copied, Toast.LENGTH_SHORT).show();
                    return true;
                } else if (itemId == R.id.delete) {
                    AlgebraAnimationHelper helper = new AlgebraAnimationHelper(listAdapter, listView, elements);
                    helper.animateRemoval(listView, v);

                    if (elements.isEmpty()) {
                        LottieAnimationView empty = findViewById(R.id.empty);
                        TextView textEmpty = findViewById(R.id.emptyText);
                        empty.setVisibility(View.VISIBLE);
                        empty.startAnimation(bounceAnim);
                        textEmpty.setVisibility(View.VISIBLE);
                        textEmpty.startAnimation(bounceAnim);
                    }
                    return true;
                } else if (itemId == R.id.edit) {
                    openAddDialog(true, index);
                }
                return false;
            });
            popup.show();
            return true;
        });

        LottieAnimationView empty = findViewById(R.id.empty);
        TextView textEmpty = findViewById(R.id.emptyText);
        if (elements.isEmpty()) {
            empty.setVisibility(View.VISIBLE);
            empty.startAnimation(bounceAnim);

            textEmpty.setVisibility(View.VISIBLE);
            textEmpty.startAnimation(bounceAnim);
        } else {
            empty.setVisibility(View.GONE);
            textEmpty.setVisibility(View.GONE);
        }
    }

    private View getViewByPosition(int pos, ListView listView) {
        final int firstListItemPosition = listView.getFirstVisiblePosition();
        final int lastListItemPosition = firstListItemPosition + listView.getChildCount() - 1;

        if (pos < firstListItemPosition || pos > lastListItemPosition) {
            return listView.getAdapter().getView(pos, null, listView);
        } else {
            final int childIndex = pos - firstListItemPosition;
            return listView.getChildAt(childIndex);
        }
    }

    /*------------------------------------------------------------------------*/
    //Navigation menu:
    private void setDuoNavigationDrawer() {
        LinearLayout toolbar = findViewById(R.id.toolbar);
        ImageButton toolbarButton = findViewById(R.id.toolbarButton);
        drawerLayout = findViewById(R.id.drawer);
        ScrollView content = findViewById(R.id.content);

        DuoDrawerSetter drawerSetter = new DuoDrawerSetter(drawerLayout, this);
        drawerSetter.setDuoNavigationDrawer(this, toolbar, toolbarButton, content);
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen())
            drawerLayout.closeDrawer();
        else
            super.onBackPressed();
    }

    /*--------------------------------------------------------------------------------------------*/
    // Save and load data:
    private void loadData() {
        try {
            SharedPreferences sharedPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE);
            Type type = new TypeToken<ArrayList<String>>() {
            }.getType();
            Gson gson = new Gson();

            String json = sharedPreferences.getString("elements_com", null);
            elements = stringToData(Objects.requireNonNull(gson.fromJson(json, type)));

            //Toast.makeText(this, "Load.", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            //Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void saveData() {
        try {
            SharedPreferences sharedPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            Gson gson = new Gson();

            String json = gson.toJson(dataToString(elements));
            editor.putString("elements_com", json);
            editor.apply();

            //Toast.makeText(this, "Saved", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            //Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private ArrayList<Elements> stringToData(ArrayList<String> array) {
        ArrayList<Elements> output = new ArrayList<>();

        Elements auxObject = new Elements();
        for (int i = 0; i < array.size(); i += 5) {
            auxObject.setType(array.get(i));
            auxObject.setName(array.get(i + 1));
            auxObject.setValues(arrayFromString(array.get(i + 2)));
            auxObject.setSelectionNumber(array.get(i + 3));
            auxObject.setChecked(Boolean.parseBoolean(array.get(i + 4)));

            output.add(auxObject);
            auxObject = new Elements();
        }

        return output;
    }

    private ArrayList<String> dataToString(ArrayList<Elements> array) {
        ArrayList<String> output = new ArrayList<>();

        for (int i = 0; i < array.size(); i++) {
            output.add(array.get(i).getType());
            output.add(array.get(i).getName());
            output.add(Arrays.toString(array.get(i).getValues()));
            output.add(array.get(i).getSelectionNumber());
            output.add(String.valueOf(array.get(i).isChecked()));
        }

        return output;
    }

    private static double[] arrayFromString(String string) {
        String[] strings = string.replace("[", "").replace("]", "").split(", ");
        double[] result = new double[strings.length];
        for (int i = 0; i < result.length; i++) {
            result[i] = Double.parseDouble(strings[i]);
        }
        return result;
    }
}