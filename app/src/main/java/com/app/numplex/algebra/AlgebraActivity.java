package com.app.numplex.algebra;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
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
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.airbnb.lottie.LottieAnimationView;
import com.app.numplex.R;
import com.app.numplex.application.AppRater;
import com.app.numplex.customExceptions.IllegalSelectionException;
import com.app.numplex.utils.LottieDialog;
import com.app.numplex.utils.DoneOnEditorActionListener;
import com.app.numplex.utils.DuoDrawerSetter;
import com.app.numplex.utils.Functions;
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

public class AlgebraActivity extends AppCompatActivity {
    private DuoDrawerLayout drawerLayout;
    public static boolean onlyOne = false;

    private ArrayList<Integer> queue = new ArrayList<>();
    private ArrayList<Elements> elements = new ArrayList<>();

    private SwipeMenuListView listView;
    private AlgebraListAdapter listAdapter;
    private BottomSheetDialog addDialog;

    private boolean lastResultIsVector = false;
    private double[] lastVectorResult;
    private final double[] point1 = new double[3];
    private final double[] point2 = new double[3];

    public static boolean deg = true;
    private static int lastSpinnerPos = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /*----------------------------------------------------------------------------------------*/
        // Loading configs:
        Functions.updateConfigs(this, this);
        /*----------------------------------------------------------------------------------------*/

        setContentView(R.layout.activity_algebra);

        listView = findViewById(R.id.list);
        TextView hints = findViewById(R.id.hints);
        TextView result = findViewById(R.id.result);

        setListView();

        //Navigation drawer
        setDuoNavigationDrawer();

        Spinner spinner = findViewById(R.id.algebraSpinner);
        FloatingActionButton fabAdd = findViewById(R.id.fab);

        //Toolbar
        Functions.setToolbar(this, getResources().getString(R.string.home_algebra), drawerLayout, () -> Functions.showSpotlightMessage(AlgebraActivity.this,
                fabAdd,
                getString(R.string.home_algebra),
                getString(R.string.help_add_element),
                120f,
                () -> Functions.showSpotlightMessage(AlgebraActivity.this,
                        spinner,
                        getString(R.string.home_algebra),
                        getString(R.string.help_choose_element),
                        300f, null)));
        /*----------------------------------------------------------------------------------------*/
        //Spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.algebra_array, R.layout.item_spinner_text);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        if (lastSpinnerPos != -1)
            spinner.setSelection(lastSpinnerPos);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            final Animation bounceAnim = AnimationUtils.loadAnimation(AlgebraActivity.this, R.anim.bounce_in);

            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                LinearLayout point1 = findViewById(R.id.point1);
                LinearLayout point2 = findViewById(R.id.point2);
                switch (position) {
                    case 0:
                    case 1:
                    case 2:
                    case 4:
                    case 5:
                    case 15:
                    case 16:
                    case 17:
                        hints.setHint(getResources().getString(R.string.select_2_vector));
                        break;
                    case 3:
                        hints.setHint(getResources().getString(R.string.select_1_vector));
                        break;
                    case 6:
                        hints.setHint(getResources().getString(R.string.select_point_line));
                        break;
                    case 7:
                    case 13:
                        hints.setHint(getResources().getString(R.string.select_2_lines));
                        break;
                    case 8:
                        hints.setHint(getResources().getString(R.string.select_point_plane));
                        break;
                    case 9:
                    case 12:
                        hints.setHint(getResources().getString(R.string.select_2_planes));
                        break;
                    case 10:
                    case 11:
                        hints.setHint(getResources().getString(R.string.select_line_plane));
                        break;
                    case 14:
                        hints.setHint(getResources().getString(R.string.select_2_points));
                        break;
                }

                if (position == 3) {
                    onlyOne = true;
                    updateSelectedItems(null);
                } else {
                    onlyOne = false;
                    updateSelectedItems(null);
                }

                FloatingActionButton fabRadDeg = findViewById(R.id.fabRadDeg);
                if (position == 2 || position == 11 || position == 12 || position == 13) {
                    fabRadDeg.setVisibility(View.VISIBLE);
                    fabRadDeg.startAnimation(bounceAnim);
                    if (deg)
                        fabRadDeg.setImageBitmap(textAsBitmap("DEG", getResources().getColor(R.color.text_color)));
                    else
                        fabRadDeg.setImageBitmap(textAsBitmap("RAD", getResources().getColor(R.color.text_color)));
                } else
                    bounceOutAnim(fabRadDeg);

                if (position == 7) {
                    point1.setVisibility(View.VISIBLE);
                    point1.startAnimation(bounceAnim);

                    point2.setVisibility(View.VISIBLE);
                    point2.startAnimation(bounceAnim);
                } else {
                    bounceOutAnim(point1);
                    bounceOutAnim(point2);
                }

                calculate(position);
                lastSpinnerPos = position;
            }

            public void onNothingSelected(AdapterView<?> parent) {
                //Do nothing
            }
        });
        /*----------------------------------------------------------------------------------------*/
        //Floating Action Buttons
        final Animation bounceAnim = AnimationUtils.loadAnimation(this, R.anim.bounce);
        fabAdd.setOnClickListener(new MultipleClickListener() {
            @Override
            public void onDoubleClick() {
                // Do nothing
            }

            @Override
            public void onSingleClick() {
                fabAdd.startAnimation(bounceAnim);
                openAddDialog(false, 0);
            }
        });

        FloatingActionButton fabClear = findViewById(R.id.fabClear);
        fabClear.setOnClickListener(view -> {
            fabClear.startAnimation(bounceAnim);
            hints.setText("");
            result.setText("");
            if (!elements.isEmpty()) {
                elements.clear();
                queue = new ArrayList<>();
                saveData();
                LottieDialog customDialog = new LottieDialog(this);
                customDialog.startDeleteDialog();

                new Handler().postDelayed(this::setListView, 3000);
            }
        });

        FloatingActionButton fabRadDeg = findViewById(R.id.fabRadDeg);
        fabRadDeg.setOnClickListener(view -> {
            fabRadDeg.startAnimation(bounceAnim);
            deg = !deg;
            if (deg)
                fabRadDeg.setImageBitmap(textAsBitmap("DEG", getResources().getColor(R.color.text_color)));
            else
                fabRadDeg.setImageBitmap(textAsBitmap("RAD", getResources().getColor(R.color.text_color)));
            calculate(spinner.getSelectedItemPosition());
        });
        /*----------------------------------------------------------------------------------------*/
        //Result
        HorizontalScrollView container = findViewById(R.id.resultContainer);
        result.setOnLongClickListener(view -> {
            container.startAnimation(bounceAnim);
            PopupMenu popup = new PopupMenu(AlgebraActivity.this, view);
            MenuInflater inflater = popup.getMenuInflater();
            inflater.inflate(R.menu.copy_generate_vector_menu_algebra, popup.getMenu());

            popup.setOnMenuItemClickListener(item -> {
                ClipboardManager clipboardManager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);

                int itemId = item.getItemId();
                if (itemId == R.id.copy) {
                    if (!result.getText().toString().equals("")) {
                        ClipData clipData = ClipData.newPlainText("text", result.getText().toString());
                        clipboardManager.setPrimaryClip(clipData);

                        Toast.makeText(getApplicationContext(), R.string.copied, Toast.LENGTH_SHORT).show();
                    }
                    return true;
                } else if (itemId == R.id.generateVector) {
                    try {
                        if (lastResultIsVector) {
                            Elements newArray = new Elements();
                            newArray.setValues(lastVectorResult);
                            newArray.setType("vector");
                            newArray.setName(getResources().getString(R.string.vector) + " " + (elements.size() + 1));
                            elements.add(newArray);
                            listAdapter.notifyDataSetChanged();
                        } else
                            Toast.makeText(getApplicationContext(), R.string.cant_make_vector, Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        Toast.makeText(getApplicationContext(), R.string.something_went_wrong, Toast.LENGTH_SHORT).show();
                    }
                    return true;
                }
                return false;
            });
            popup.show();
            return false;
        });
        /*----------------------------------------------------------------------------------------*/
        //Additional points:
        EditText x1 = findViewById(R.id.point1X);
        EditText y1 = findViewById(R.id.point1Y);
        EditText z1 = findViewById(R.id.point1Z);
        EditText x2 = findViewById(R.id.point2X);
        EditText y2 = findViewById(R.id.point2Y);
        EditText z2 = findViewById(R.id.point2Z);

        x1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // Do nothing
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // Do nothing
            }

            @Override
            public void afterTextChanged(Editable editable) {
                calculate(spinner.getSelectedItemPosition());
            }
        });

        y1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // Do nothing
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // Do nothing
            }

            @Override
            public void afterTextChanged(Editable editable) {
                calculate(spinner.getSelectedItemPosition());
            }
        });

        z1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // Do nothing
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // Do nothing
            }

            @Override
            public void afterTextChanged(Editable editable) {
                calculate(spinner.getSelectedItemPosition());
            }
        });

        x2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // Do nothing
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // Do nothing
            }

            @Override
            public void afterTextChanged(Editable editable) {
                calculate(spinner.getSelectedItemPosition());
            }
        });

        y2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // Do nothing
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // Do nothing
            }

            @Override
            public void afterTextChanged(Editable editable) {
                calculate(spinner.getSelectedItemPosition());
            }
        });

        z2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // Do nothing
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // Do nothing
            }

            @Override
            public void afterTextChanged(Editable editable) {
                calculate(spinner.getSelectedItemPosition());
            }
        });

        z2.setOnEditorActionListener(new DoneOnEditorActionListener());

        //App rater:
        AppRater.app_launched(this, this);
    }

    /*--------------------------------------------------------------------------------------------*/
    //List methods:
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

                if (elements.get(position).isChecked())
                    updateSelectedItems(position);
                helper.animateRemoval(listView, getViewByPosition(position, listView));

                for(int i = 0; i < queue.size(); i++)
                    if(queue.get(i) != 0)
                        queue.set(i, queue.get(i) - 1);

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
            PopupMenu popup = new PopupMenu(AlgebraActivity.this, v);
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

                    if (elements.get(index).isChecked())
                        updateSelectedItems(index);
                    helper.animateRemoval(listView, v);

                    for(int i = 0; i < queue.size(); i++)
                        if(queue.get(i) != 0)
                            queue.set(i, queue.get(i) - 1);

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

        listView.setOnItemClickListener((adapterView, view, i, l) -> {
            Spinner spinner = findViewById(R.id.algebraSpinner);
            final Animation bounce = AnimationUtils.loadAnimation(this, R.anim.bounce);
            view.startAnimation(bounce);

            updateSelectedItems(i);
            calculate(spinner.getSelectedItemPosition());
        });
    }

    private void updateSelectedItems(Integer newPos) {
        Elements element;

        // If null you changed from !onlyOne to onlyOne(true)
        if (newPos != null) {
            element = elements.get(newPos);

            if (!element.isChecked()) {
                queue.add(newPos);
                if (!onlyOne && queue.size() == 3) {
                    // queue.get(0) --> to be replaced
                    // queue.get(1) --> previous selection
                    // queue.get(2) --> current selection

                    listAdapter.toggleElementChecked(queue.get(0));
                    queue.remove(0);
                } else if (onlyOne && queue.size() == 2) {
                    // queue.get(0) --> to be replaced
                    // queue.get(1) --> current selection

                    listAdapter.toggleElementChecked(queue.get(0));
                    queue.remove(0);
                }
                listAdapter.toggleElementChecked(newPos);
            } else {
                listAdapter.toggleElementChecked(newPos);
                queue.remove(newPos);
            }
        } else if (onlyOne && queue.size() == 2) {
            // queue.get(0) --> to be replaced
            // queue.get(1) --> current selection

            listAdapter.toggleElementChecked(queue.get(0));
            queue.remove(0);
        }
        updateSelectionNumbers();
        listAdapter.notifyDataSetChanged();
        saveData();
    }

    private void updateSelectionNumbers() {
        Elements element;
        if (!queue.isEmpty()) {
            if (!onlyOne) {
                if (queue.size() == 2) {
                    element = elements.get(queue.get(0));
                    element.setSelectionNumber("1");
                    element = elements.get(queue.get(1));
                    element.setSelectionNumber("2");
                } else if (queue.size() == 1) {
                    element = elements.get(queue.get(0));
                    element.setSelectionNumber("1");
                }
            } else {
                element = elements.get(queue.get(0));
                element.setSelectionNumber("1");
            }
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

    /*--------------------------------------------------------------------------------------------*/
    // Calculating:
    private void calculate(int spinnerPos) {
        final Animation slideIn = AnimationUtils.loadAnimation(this, R.anim.slide_in_left);
        TextView result = findViewById(R.id.result);
        String resultText = null;
        double[] outputArray;
        double outputValue = 0;

        double[] firstSelection;
        double[] secondSelection = null;

        lastResultIsVector = false;
        try {
            checkSelections(spinnerPos);

            if (!onlyOne) {
                firstSelection = elements.get(queue.get(0)).getValues();
                secondSelection = elements.get(queue.get(1)).getValues();
            } else
                firstSelection = elements.get(queue.get(0)).getValues();

            switch (spinnerPos) {
                case 0:
                    // Addition
                    outputArray = AlgebraLogic.calculateAddition(firstSelection, secondSelection);
                    lastResultIsVector = true;
                    lastVectorResult = outputArray;

                    resultText = arrayToString(outputArray, "vector");
                    break;
                case 1:
                    // Subtraction
                    outputArray = AlgebraLogic.calculateSubtraction(firstSelection, secondSelection);
                    lastResultIsVector = true;
                    lastVectorResult = outputArray;

                    resultText = arrayToString(outputArray, "vector");
                    break;
                case 2:
                case 11:
                case 12:
                case 13:
                    // Angle between line and plane
                    // Angle between planes
                    // Angle between vectors
                    // Angle between lines
                    switch (spinnerPos){
                        case 2:
                            outputValue = AlgebraLogic.calculateAngleVectors(firstSelection, secondSelection);
                            break;
                        case 11:
                            outputValue = AlgebraLogic.calculateAngleLineAndPlane(firstSelection, Objects.requireNonNull(secondSelection));
                            break;
                        case 12:
                            outputValue = AlgebraLogic.calculateAnglePlane(firstSelection, Objects.requireNonNull(secondSelection));
                            break;
                        case 13:
                            outputValue = AlgebraLogic.calculateAngleLine(firstSelection, secondSelection);
                            break;
                    }

                    if (deg)
                        resultText = String.format("%sº", outputValue);
                    else
                        resultText = String.format("%s rad", outputValue);
                    break;
                case 3:
                    // Length
                    outputValue = AlgebraLogic.sumVetA(firstSelection);
                    resultText = String.valueOf(outputValue);
                    break;
                case 4:
                    // Dot product
                    outputValue = AlgebraLogic.calculateDotProduct(firstSelection, secondSelection);
                    resultText = String.valueOf(outputValue);
                    break;
                case 5:
                    // Cross product
                    outputArray = AlgebraLogic.crossProduct(firstSelection, Objects.requireNonNull(secondSelection));
                    lastResultIsVector = true;
                    lastVectorResult = outputArray;

                    resultText = arrayToString(outputArray, "vector");
                    break;
                case 6:
                    // Distance between point and line
                    if (elements.get(queue.get(1)).getType().equals("line"))
                        outputValue = AlgebraLogic.distanceLineAndPoint(Objects.requireNonNull(secondSelection), firstSelection);
                    else
                        outputValue = AlgebraLogic.distanceLineAndPoint(firstSelection, Objects.requireNonNull(secondSelection));
                    resultText = String.valueOf(outputValue);
                    break;
                case 7:
                    // Distance between lines: this case requires 2 additional points
                    getAdditionalPoints();
                    outputValue = AlgebraLogic.distanceLines(point1, firstSelection, point2, secondSelection);
                    resultText = String.valueOf(outputValue);
                    break;
                case 8:
                    // Distance between point and plane
                    if (elements.get(queue.get(1)).getType().equals("plane"))
                        outputValue = AlgebraLogic.distancePlaneAndPoint(Objects.requireNonNull(secondSelection), firstSelection);
                    else
                        outputValue = AlgebraLogic.distancePlaneAndPoint(firstSelection, Objects.requireNonNull(secondSelection));
                    resultText = String.valueOf(outputValue);
                    break;
                case 9:
                    // Distance between planes
                    outputValue = AlgebraLogic.distancePlane(firstSelection, Objects.requireNonNull(secondSelection));
                    if (outputValue != -1)
                        resultText = String.valueOf(outputValue);
                    else
                        resultText = getResources().getString(R.string.planes_intersect);
                    break;
                case 10:
                    // Distance between line and plane
                    if (elements.get(queue.get(1)).getType().equals("plane"))
                        outputValue = AlgebraLogic.distancePlaneAndLine(Objects.requireNonNull(secondSelection), firstSelection);
                    else
                        outputValue = AlgebraLogic.distancePlaneAndLine(firstSelection, Objects.requireNonNull(secondSelection));
                    resultText = String.valueOf(outputValue);
                    break;
                case 14:
                    // Vector coordinates
                    outputArray = AlgebraLogic.calculateVectorCoordinates(firstSelection, secondSelection);
                    lastResultIsVector = true;
                    lastVectorResult = outputArray;

                    resultText = arrayToString(outputArray, "vector");
                    break;
                case 15:
                    // Parallel
                    if(AlgebraLogic.saoParalelos(firstSelection, Objects.requireNonNull(secondSelection)))
                        resultText = getResources().getString(R.string.yes);
                    else
                        resultText = getResources().getString(R.string.no);
                    break;
                case 16:
                    // Perpendicular
                    if(AlgebraLogic.saoPerpendiculares(firstSelection, Objects.requireNonNull(secondSelection)))
                        resultText = getResources().getString(R.string.yes);
                    else
                        resultText = getResources().getString(R.string.no);
                    break;
                case 17:
                    // Projection
                    outputArray = AlgebraLogic.projecao(firstSelection, secondSelection);
                    lastResultIsVector = true;
                    lastVectorResult = outputArray;

                    resultText = arrayToString(outputArray, "vector");
                    break;
            }

            //Bug here:
            /*if (resultText != null) {
                resultText = resultText.replace("0i + ", "");
                resultText = resultText.replace(" + 0j + ", " + ");
                resultText = resultText.replace(" + 0k", "");
                resultText = resultText.replace("0j + ", "");

                if (resultText.endsWith(".0"))
                    resultText = resultText.replace(".0", "");
            }*/

            result.setText(resultText);
        } catch (Exception ignored) {
            result.setText("");
        }
        if (!result.getText().toString().equals(""))
            result.startAnimation(slideIn);
    }

    public static String arrayToString(double[] array, String type) {
        String value = null;
        String part1 = String.valueOf(array[0]);
        String part2 = String.valueOf(array[1]);
        String part3 = String.valueOf(array[2]);

        if (part1.endsWith(".0"))
            part1 = part1.replace(".0", "");
        if (part2.endsWith(".0"))
            part2 = part2.replace(".0", "");
        if (part3.endsWith(".0"))
            part3 = part3.replace(".0", "");

        switch (type) {
            case "vector":
                value = String.format("%si + %sj + %sk", part1, part2, part3);
                break;
            case "plane":
                String part4 = String.valueOf(array[3]);
                if (part4.endsWith(".0"))
                    part4 = part4.replace(".0", "");

                value = String.format("%sx + %sy + %sz + %s = 0", part1, part2, part3, part4);
                break;
            case "point":
                if (!part3.equals("0"))
                    value = String.format("(%s;%s;%s)", part1, part2, part3);
                else
                    value = String.format("(%s;%s)", part1, part2);
                break;
            case "line":
                value = String.format("%sx + %sy + %s = 0", part1, part2, part3);
                break;
        }

        if (value != null)
            value = value.replace(" + -", " -");

        return value;
    }

    private void checkSelections(int position) throws IllegalSelectionException {
        Animation shake = AnimationUtils.loadAnimation(AlgebraActivity.this, R.anim.shake);
        TextView hints = findViewById(R.id.hints);
        Elements secondElement;
        Elements firstElement;

        String type1;
        String type2 = "";

        boolean error = false;
        if (onlyOne)
            firstElement = elements.get(queue.get(0));
        else {
            firstElement = elements.get(queue.get(1));
            secondElement = elements.get(queue.get(0));
            type2 = secondElement.getType();
        }
        type1 = firstElement.getType();

        switch (position) {
            case 0:
            case 1:
            case 2:
            case 4:
            case 5:
                if (!(type1.equals("vector") && type2.equals("vector"))) {
                    error = true;
                    hints.setText(getResources().getString(R.string.select_2_vector));
                }
                break;
            case 3:
                if (!(type1.equals("vector"))) {
                    error = true;
                    hints.setText(getResources().getString(R.string.select_1_vector));
                }
                break;
            case 6:
                if (!((type1.equals("point") && type2.equals("line")) ||
                        (type1.equals("line") && type2.equals("point")))) {
                    error = true;
                    hints.setText(getResources().getString(R.string.select_point_line));
                }
                break;
            case 7:
            case 13:
                if (!(type1.equals("line") && type2.equals("line"))) {
                    error = true;
                    hints.setText(getResources().getString(R.string.select_2_lines));
                }
                break;
            case 8:
                if (!((type1.equals("point") && type2.equals("plane")) ||
                        (type1.equals("plane") && type2.equals("point")))) {
                    error = true;
                    hints.setText(getResources().getString(R.string.select_point_plane));
                }
                break;
            case 9:
            case 12:
                if (!(type1.equals("plane") && type2.equals("plane"))) {
                    error = true;
                    hints.setText(getResources().getString(R.string.select_2_planes));
                }
                break;
            case 10:
            case 11:
                if (!((type1.equals("line") && type2.equals("plane")) ||
                        (type1.equals("plane") && type2.equals("line")))) {
                    error = true;
                    hints.setText(getResources().getString(R.string.select_line_plane));
                }
                break;
            case 14:
                if (!(type1.equals("point") && type2.equals("point"))) {
                    error = true;
                    hints.setText(getResources().getString(R.string.select_2_points));
                }
                break;
        }

        if (error) {
            hints.startAnimation(shake);
            throw new IllegalSelectionException();
        } else
            hints.setText("");
    }

    private void getAdditionalPoints() {
        Animation shake = AnimationUtils.loadAnimation(AlgebraActivity.this, R.anim.shake);
        TextView hints = findViewById(R.id.hints);
        EditText numbers;
        int zeroCount = 0;

        // Point 1
        numbers = findViewById(R.id.point1X);
        if (!numbers.getText().toString().equals(""))
            point1[0] = Double.parseDouble(Objects.requireNonNull(numbers).getText().toString());
        else {
            zeroCount++;
            point1[0] = 0.0;
        }

        numbers = findViewById(R.id.point1Y);
        if (!numbers.getText().toString().equals(""))
            point1[1] = Double.parseDouble(Objects.requireNonNull(numbers).getText().toString());
        else {
            zeroCount++;
            point1[1] = 0.0;
        }

        numbers = findViewById(R.id.point1Z);
        if (!numbers.getText().toString().equals(""))
            point1[2] = Double.parseDouble(Objects.requireNonNull(numbers).getText().toString());
        else {
            zeroCount++;
            point1[2] = 0.0;
        }

        if (zeroCount == 3) {
            hints.setText(getResources().getString(R.string.additional_points));
            hints.startAnimation(shake);
            throw new RuntimeException();
        } else
            zeroCount = 0;

        // Point 2
        numbers = findViewById(R.id.point2X);
        if (!numbers.getText().toString().equals(""))
            point1[0] = Double.parseDouble(Objects.requireNonNull(numbers).getText().toString());
        else {
            zeroCount++;
            point1[0] = 0.0;
        }

        numbers = findViewById(R.id.point2Y);
        if (!numbers.getText().toString().equals(""))
            point1[1] = Double.parseDouble(Objects.requireNonNull(numbers).getText().toString());
        else {
            zeroCount++;
            point1[1] = 0.0;
        }

        numbers = findViewById(R.id.point2Z);
        if (!numbers.getText().toString().equals(""))
            point1[2] = Double.parseDouble(Objects.requireNonNull(numbers).getText().toString());
        else {
            zeroCount++;
            point1[2] = 0.0;
        }

        if (zeroCount == 3) {
            hints.setText(getResources().getString(R.string.additional_points));
            hints.startAnimation(shake);
            throw new RuntimeException();
        } else
            hints.setText("");
    }

    /*--------------------------------------------------------------------------------------------*/
    // Bottom dialog:
    private void openAddDialog(boolean edit, int editPos) {
        final Animation bounceAnim = AnimationUtils.loadAnimation(this, R.anim.bounce_in);
        addDialog = new BottomSheetDialog(this);
        addDialog.setContentView(R.layout.bottom_dialog_algebra);

        Button save = addDialog.findViewById(R.id.save);
        LinearLayout vector = addDialog.findViewById(R.id.vector);
        LinearLayout point = addDialog.findViewById(R.id.point);
        LinearLayout line = addDialog.findViewById(R.id.line);
        LinearLayout plane = addDialog.findViewById(R.id.plane);

        /*----------------------------------------------------------------------------------------*/
        //Spinner
        Spinner spinner = addDialog.findViewById(R.id.addSpinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(AlgebraActivity.this,
                R.array.algebra_element_array, R.layout.item_spinner_text);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Objects.requireNonNull(spinner).setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                EditText auxToClearFocus;
                switch (position) {
                    case 0:
                        // Vector
                        Objects.requireNonNull(point).setVisibility(View.GONE);
                        Objects.requireNonNull(line).setVisibility(View.GONE);
                        Objects.requireNonNull(plane).setVisibility(View.GONE);

                        Objects.requireNonNull(vector).setVisibility(View.VISIBLE);
                        vector.startAnimation(bounceAnim);

                        auxToClearFocus = addDialog.findViewById(R.id.vectorX);
                        Objects.requireNonNull(auxToClearFocus).requestFocus();
                        break;
                    case 1:
                        // Point
                        Objects.requireNonNull(vector).setVisibility(View.GONE);
                        Objects.requireNonNull(line).setVisibility(View.GONE);
                        Objects.requireNonNull(plane).setVisibility(View.GONE);

                        Objects.requireNonNull(point).setVisibility(View.VISIBLE);
                        point.startAnimation(bounceAnim);

                        auxToClearFocus = addDialog.findViewById(R.id.pointX);
                        Objects.requireNonNull(auxToClearFocus).requestFocus();
                        break;
                    case 2:
                        // Line
                        Objects.requireNonNull(point).setVisibility(View.GONE);
                        Objects.requireNonNull(vector).setVisibility(View.GONE);
                        Objects.requireNonNull(plane).setVisibility(View.GONE);

                        Objects.requireNonNull(line).setVisibility(View.VISIBLE);
                        line.startAnimation(bounceAnim);

                        auxToClearFocus = addDialog.findViewById(R.id.lineA);
                        Objects.requireNonNull(auxToClearFocus).requestFocus();
                        break;
                    case 3:
                        // Plane
                        Objects.requireNonNull(point).setVisibility(View.GONE);
                        Objects.requireNonNull(line).setVisibility(View.GONE);
                        Objects.requireNonNull(vector).setVisibility(View.GONE);

                        Objects.requireNonNull(plane).setVisibility(View.VISIBLE);
                        plane.startAnimation(bounceAnim);

                        auxToClearFocus = addDialog.findViewById(R.id.planeA);
                        Objects.requireNonNull(auxToClearFocus).requestFocus();
                        break;
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
                case "vector":
                    spinner.setSelection(0);
                    Objects.requireNonNull(vector).setVisibility(View.VISIBLE);
                    vector.startAnimation(bounceAnim);

                    numbers = addDialog.findViewById(R.id.vectorX);
                    Objects.requireNonNull(numbers).setText(parseString(String.valueOf(target.getValues()[0])));

                    numbers = addDialog.findViewById(R.id.vectorY);
                    Objects.requireNonNull(numbers).setText(parseString(String.valueOf(target.getValues()[1])));

                    numbers = addDialog.findViewById(R.id.vectorZ);
                    Objects.requireNonNull(numbers).setText(parseString(String.valueOf(target.getValues()[2])));

                    name = addDialog.findViewById(R.id.vectorName);
                    Objects.requireNonNull(name).setText(target.getName());
                    break;
                case "line":
                    spinner.setSelection(2);
                    Objects.requireNonNull(line).setVisibility(View.VISIBLE);
                    line.startAnimation(bounceAnim);

                    numbers = addDialog.findViewById(R.id.lineA);
                    Objects.requireNonNull(numbers).setText(parseString(String.valueOf(target.getValues()[0])));

                    numbers = addDialog.findViewById(R.id.lineB);
                    Objects.requireNonNull(numbers).setText(parseString(String.valueOf(target.getValues()[1])));

                    numbers = addDialog.findViewById(R.id.lineC);
                    Objects.requireNonNull(numbers).setText(parseString(String.valueOf(target.getValues()[2])));

                    name = addDialog.findViewById(R.id.lineName);
                    Objects.requireNonNull(name).setText(target.getName());
                    break;
                case "point":
                    spinner.setSelection(1);
                    Objects.requireNonNull(point).setVisibility(View.VISIBLE);
                    point.startAnimation(bounceAnim);

                    numbers = addDialog.findViewById(R.id.pointX);
                    Objects.requireNonNull(numbers).setText(parseString(String.valueOf(target.getValues()[0])));

                    numbers = addDialog.findViewById(R.id.pointY);
                    Objects.requireNonNull(numbers).setText(parseString(String.valueOf(target.getValues()[1])));

                    numbers = addDialog.findViewById(R.id.pointZ);
                    Objects.requireNonNull(numbers).setText(parseString(String.valueOf(target.getValues()[2])));

                    name = addDialog.findViewById(R.id.pointName);
                    Objects.requireNonNull(name).setText(target.getName());
                    break;
                case "plane":
                    spinner.setSelection(3);
                    Objects.requireNonNull(plane).setVisibility(View.VISIBLE);
                    plane.startAnimation(bounceAnim);

                    numbers = addDialog.findViewById(R.id.planeA);
                    Objects.requireNonNull(numbers).setText(parseString(String.valueOf(target.getValues()[0])));

                    numbers = addDialog.findViewById(R.id.planeB);
                    Objects.requireNonNull(numbers).setText(parseString(String.valueOf(target.getValues()[1])));

                    numbers = addDialog.findViewById(R.id.planeC);
                    Objects.requireNonNull(numbers).setText(parseString(String.valueOf(target.getValues()[2])));

                    numbers = addDialog.findViewById(R.id.planeD);
                    Objects.requireNonNull(numbers).setText(parseString(String.valueOf(target.getValues()[3])));

                    name = addDialog.findViewById(R.id.planeName);
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
                        // Vector
                        values = new double[3];
                        name = addDialog.findViewById(R.id.vectorName);
                        newElement.setType("vector");
                        if (!Objects.requireNonNull(name).getText().toString().equals(""))
                            newElement.setName(Objects.requireNonNull(name).getText().toString());
                        else
                            newElement.setName(getResources().getString(R.string.vector) + " " + (elements.size() + 1));

                        numbers = addDialog.findViewById(R.id.vectorX);
                        if (!Objects.requireNonNull(numbers).getText().toString().equals(""))
                            values[0] = Double.parseDouble(Objects.requireNonNull(numbers).getText().toString());
                        else {
                            zeroCount++;
                            values[0] = 0.0;
                        }

                        numbers = addDialog.findViewById(R.id.vectorY);
                        if (!Objects.requireNonNull(numbers).getText().toString().equals(""))
                            values[1] = Double.parseDouble(Objects.requireNonNull(numbers).getText().toString());
                        else {
                            zeroCount++;
                            values[1] = 0.0;
                        }

                        numbers = addDialog.findViewById(R.id.vectorZ);
                        if (!Objects.requireNonNull(numbers).getText().toString().equals(""))
                            values[2] = Double.parseDouble(Objects.requireNonNull(numbers).getText().toString());
                        else {
                            zeroCount++;
                            values[2] = 0.0;
                        }

                        if (zeroCount != 3)
                            shouldSave = true;
                        newElement.setValues(values);
                        break;
                    case 1:
                        // Point
                        values = new double[3];
                        name = addDialog.findViewById(R.id.pointName);
                        newElement.setType("point");
                        if (!Objects.requireNonNull(name).getText().toString().equals(""))
                            newElement.setName(Objects.requireNonNull(name).getText().toString());
                        else
                            newElement.setName(getResources().getString(R.string.point) + " " + (elements.size() + 1));

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

                        numbers = addDialog.findViewById(R.id.pointZ);
                        if (!Objects.requireNonNull(numbers).getText().toString().equals(""))
                            values[2] = Double.parseDouble(Objects.requireNonNull(numbers).getText().toString());
                        else {
                            zeroCount++;
                            values[2] = 0.0;
                        }

                        if (zeroCount != 3)
                            shouldSave = true;
                        newElement.setValues(values);
                        break;
                    case 2:
                        // Line
                        values = new double[3];
                        name = addDialog.findViewById(R.id.lineName);
                        newElement.setType("line");
                        if (!Objects.requireNonNull(name).getText().toString().equals(""))
                            newElement.setName(Objects.requireNonNull(name).getText().toString());
                        else
                            newElement.setName(getResources().getString(R.string.line) + " " + (elements.size() + 1));

                        numbers = addDialog.findViewById(R.id.lineA);
                        if (!Objects.requireNonNull(numbers).getText().toString().equals(""))
                            values[0] = Double.parseDouble(Objects.requireNonNull(numbers).getText().toString());
                        else {
                            zeroCount++;
                            values[0] = 0.0;
                        }

                        numbers = addDialog.findViewById(R.id.lineB);
                        if (!Objects.requireNonNull(numbers).getText().toString().equals(""))
                            values[1] = Double.parseDouble(Objects.requireNonNull(numbers).getText().toString());
                        else {
                            zeroCount++;
                            values[1] = 0.0;
                        }

                        numbers = addDialog.findViewById(R.id.lineC);
                        if (!Objects.requireNonNull(numbers).getText().toString().equals(""))
                            values[2] = Double.parseDouble(Objects.requireNonNull(numbers).getText().toString());
                        else {
                            zeroCount++;
                            values[2] = 0.0;
                        }

                        if (zeroCount != 3)
                            shouldSave = true;
                        newElement.setValues(values);
                        break;
                    case 3:
                        values = new double[4];
                        name = addDialog.findViewById(R.id.planeName);
                        newElement.setType("plane");
                        if (!Objects.requireNonNull(name).getText().toString().equals(""))
                            newElement.setName(Objects.requireNonNull(name).getText().toString());
                        else
                            newElement.setName(getResources().getString(R.string.plane) + " " + (elements.size() + 1));

                        numbers = addDialog.findViewById(R.id.planeA);
                        if (!Objects.requireNonNull(numbers).getText().toString().equals(""))
                            values[0] = Double.parseDouble(Objects.requireNonNull(numbers).getText().toString());
                        else {
                            zeroCount++;
                            values[0] = 0.0;
                        }

                        numbers = addDialog.findViewById(R.id.planeB);
                        if (!Objects.requireNonNull(numbers).getText().toString().equals(""))
                            values[1] = Double.parseDouble(Objects.requireNonNull(numbers).getText().toString());
                        else {
                            zeroCount++;
                            values[1] = 0.0;
                        }

                        numbers = addDialog.findViewById(R.id.planeC);
                        if (!Objects.requireNonNull(numbers).getText().toString().equals(""))
                            values[2] = Double.parseDouble(Objects.requireNonNull(numbers).getText().toString());
                        else {
                            zeroCount++;
                            values[2] = 0.0;
                        }

                        numbers = addDialog.findViewById(R.id.planeD);
                        if (!Objects.requireNonNull(numbers).getText().toString().equals(""))
                            values[3] = Double.parseDouble(Objects.requireNonNull(numbers).getText().toString());
                        else {
                            zeroCount++;
                            values[3] = 0.0;
                        }

                        if (zeroCount != 4)
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
                        bounceOutAnim(empty);
                        bounceOutAnim(textEmpty);
                    }
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.saved), Toast.LENGTH_SHORT).show();
                    saveData();
                }
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

    /*--------------------------------------------------------------------------------------------*/
    //Navigation menu:
    private void setDuoNavigationDrawer() {
        LinearLayout toolbar = findViewById(R.id.toolbar);
        ImageButton toolbarButton = findViewById(R.id.toolbarButton);
        drawerLayout = findViewById(R.id.drawer);
        LinearLayout content = findViewById(R.id.content);

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
    // Some other methods:
    private void bounceOutAnim(View view) {
        if (view.getVisibility() == View.VISIBLE) {
            final Animation bounceOut = AnimationUtils.loadAnimation(this, R.anim.bounce_out);
            view.startAnimation(bounceOut);
            new Handler().postDelayed(() -> view.setVisibility(View.GONE), 200);
        }
    }

    // Method to convert a text to image
    private static Bitmap textAsBitmap(String text, int textColor) {
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setTextSize((float) 30);
        paint.setColor(textColor);
        paint.setTextAlign(Paint.Align.LEFT);
        float baseline = -paint.ascent(); // ascent() is negative
        int width = (int) (paint.measureText(text) + 0.0f); // round
        int height = (int) (baseline + paint.descent() + 0.0f);
        Bitmap image = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(image);
        canvas.drawText(text, 0, baseline, paint);
        return image;
    }

    /*--------------------------------------------------------------------------------------------*/
    // Save and load data:
    private void loadData() {
        try {
            SharedPreferences sharedPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE);
            Type type = new TypeToken<ArrayList<String>>() {
            }.getType();
            Gson gson = new Gson();

            String json = sharedPreferences.getString("elements", null);
            elements = stringToData(gson.fromJson(json, type));

            json = sharedPreferences.getString("queue", null);
            queue = queueStringToData(gson.fromJson(json, type));

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
            editor.putString("elements", json);
            editor.apply();

            json = gson.toJson(queueToString(queue));
            editor.putString("queue", json);
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

    private ArrayList<Integer> queueStringToData(ArrayList<String> array) {
        ArrayList<Integer> output = new ArrayList<>();

        for (int i = 0; i < array.size(); i++)
            output.add(Integer.valueOf(array.get(i)));
        return output;
    }

    private ArrayList<String> queueToString(ArrayList<Integer> array) {
        ArrayList<String> output = new ArrayList<>();

        for (int i = 0; i < array.size(); i++)
            output.add(String.valueOf(array.get(i)));
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