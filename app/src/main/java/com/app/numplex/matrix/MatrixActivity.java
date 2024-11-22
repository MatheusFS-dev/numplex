package com.app.numplex.matrix;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.app.numplex.R;
import com.app.numplex.application.AppRater;
import com.app.numplex.customExceptions.DeterminantNotPossible;
import com.app.numplex.customExceptions.InverseNotPossible;
import com.app.numplex.customExceptions.MatrixDifferentDimensions;
import com.app.numplex.utils.Dialogs;
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
import java.util.Objects;

import nl.psdcompany.duonavigationdrawer.views.DuoDrawerLayout;

public class MatrixActivity extends AppCompatActivity {
    public static boolean onlyOne = false;
    public static boolean isAnimEnabled = false;
    private static int lastSpinnerPos = -1;
    private DuoDrawerLayout drawerLayout;
    private ArrayList<Integer> queue = new ArrayList<>();
    private ArrayList<String> editTexts;
    private int col = 4;
    private int rows = 2;
    private ArrayList<Matrix> matrices = new ArrayList<>();
    private RecyclerViewGridAdapter gridViewAdapter;
    private SwipeMenuListView listView;
    private MatrixListAdapter matrixListAdapter;
    private BottomSheetDialog addDialog;
    private boolean lastResultIsMatrix = false;
    private double[][] resultMatrix;

    private static String matrixToString(double[][] arr) {
        StringBuilder sb = new StringBuilder();
        for (double[] doubles : arr) {
            for (double aDouble : doubles)
                sb.append(aDouble).append(" ");
            sb.append("\n");
        }
        return sb.toString();
    }

    private static double[][] stringToMatrix(String str) {
        String[] lines = str.split("\n");
        double[][] arr = new double[lines.length][];
        for (int i = 0; i < lines.length; i++) {
            String[] parts = lines[i].split(" ");
            arr[i] = new double[parts.length];
            for (int j = 0; j < parts.length; j++)
                arr[i][j] = Double.parseDouble(parts[j]);
        }
        return arr;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /*----------------------------------------------------------------------------------------*/
        // Loading configs:
        Functions.updateConfigs(this, this);
        /*----------------------------------------------------------------------------------------*/

        setContentView(R.layout.activity_matrix);

        listView = findViewById(R.id.list);
        TextView hints = findViewById(R.id.hints);
        TextView result = findViewById(R.id.result);

        setListView();

        //Navigation drawer
        setDuoNavigationDrawer();

        FloatingActionButton fabAdd = findViewById(R.id.fab);
        Spinner spinner = findViewById(R.id.matrixSpinner);

        //Toolbar
        Functions.setToolbar(this, getResources().getString(R.string.home_matrix), drawerLayout, () -> Functions.showSpotlightMessage(MatrixActivity.this,
                fabAdd,
                getString(R.string.home_matrix),
                getString(R.string.help_add_element),
                120f,
                () -> Functions.showSpotlightMessage(MatrixActivity.this,
                        spinner,
                        getString(R.string.home_matrix),
                        getString(R.string.help_choose_element),
                        250f, null)));
        /*----------------------------------------------------------------------------------------*/
        //Spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.matrix_array, R.layout.item_spinner_text);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        if (lastSpinnerPos != -1)
            spinner.setSelection(lastSpinnerPos);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                final Animation bounceAnim = AnimationUtils.loadAnimation(MatrixActivity.this, R.anim.bounce_in);
                LinearLayout scalar = findViewById(R.id.scalar);
                switch (position) {
                    case 0:
                    case 1:
                    case 2:
                    case 9:
                        bounceOutAnim(scalar);
                        hints.setHint(getResources().getString(R.string.select_2_matrix));
                        hints.setText("");
                        onlyOne = false;
                        updateSelectedItems(null);
                        break;
                    case 3:
                    case 4:
                    case 8:
                        scalar.setVisibility(View.VISIBLE);
                        scalar.startAnimation(bounceAnim);
                        hints.setHint(getResources().getString(R.string.select_1_matrix));
                        hints.setText("");
                        onlyOne = true;
                        updateSelectedItems(null);
                        break;
                    case 5:
                    case 6:
                    case 7:
                        bounceOutAnim(scalar);
                        hints.setHint(getResources().getString(R.string.select_1_matrix));
                        hints.setText("");
                        onlyOne = true;
                        updateSelectedItems(null);
                        break;
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
                openAddDialog(false, -1);
            }
        });

        FloatingActionButton fabClear = findViewById(R.id.fabClear);
        fabClear.setOnClickListener(view -> {
            hints.setText("");
            result.setText("");
            fabClear.startAnimation(bounceAnim);
            if (!matrices.isEmpty()) {
                matrices.clear();
                queue = new ArrayList<>();
                saveData();
                LottieDialog customDialog = new LottieDialog(this);
                customDialog.startDeleteDialog();

                new Handler().postDelayed(this::setListView, 3000);
            }
        });

        /*----------------------------------------------------------------------------------------*/
        //Result
        HorizontalScrollView container = findViewById(R.id.resultContainer);
        result.setOnLongClickListener(view -> {
            container.startAnimation(bounceAnim);
            PopupMenu popup = new PopupMenu(MatrixActivity.this, view);
            MenuInflater inflater = popup.getMenuInflater();
            inflater.inflate(R.menu.copy_generate_matrix_menu_matrix, popup.getMenu());

            popup.setOnMenuItemClickListener(item -> {
                ClipboardManager clipboardManager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);

                int itemId = item.getItemId();
                if (itemId == R.id.copy) {
                    if (!result.getText().toString().equals("") && !lastResultIsMatrix) {
                        ClipData clipData = ClipData.newPlainText("text", result.getText().toString());
                        clipboardManager.setPrimaryClip(clipData);

                        Toast.makeText(getApplicationContext(), R.string.copied, Toast.LENGTH_SHORT).show();
                    }
                    return true;
                } else if (itemId == R.id.generateMatrix) {
                    try {
                        if (lastResultIsMatrix) {
                            Matrix newMatrix = new Matrix();
                            newMatrix.setValues(resultMatrix);
                            newMatrix.setRows(resultMatrix.length);
                            newMatrix.setCol(resultMatrix[0].length);
                            newMatrix.setName(getResources().getString(R.string.matrix) + " " + (matrices.size() + 1));
                            matrices.add(newMatrix);
                            matrixListAdapter.notifyDataSetChanged();
                            saveData();
                        } else
                            Toast.makeText(getApplicationContext(), R.string.cant_make_matrix, Toast.LENGTH_SHORT).show();
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

        result.setOnClickListener(view -> {
            container.startAnimation(bounceAnim);
            if (lastResultIsMatrix)
                openAddDialog(true, -1);
        });

        EditText scalar = findViewById(R.id.scalarNumber);
        scalar.setOnClickListener(v -> Dialogs.showKeyboard(MatrixActivity.this, value -> {
            scalar.setText(value);
            calculate(spinner.getSelectedItemPosition());
        }));

        //App rater:
        AppRater.app_launched(this, this);
    }

    /*--------------------------------------------------------------------------------------------*/
    //List methods:
    private void setListView() {
        loadData();
        final Animation bounceAnim = AnimationUtils.loadAnimation(this, R.anim.bounce_in);
        matrixListAdapter = new MatrixListAdapter(this, matrices);

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
                MatrixAnimationHelper helper = new MatrixAnimationHelper(matrixListAdapter, listView, matrices);

                if (matrices.get(position).isChecked())
                    updateSelectedItems(position);
                helper.animateRemoval(listView, getViewByPosition(position, listView));

                for (int i = 0; i < queue.size(); i++)
                    if (queue.get(i) != 0)
                        queue.set(i, queue.get(i) - 1);

                if (matrices.isEmpty()) {
                    LottieAnimationView empty = findViewById(R.id.empty);
                    TextView textEmpty = findViewById(R.id.emptyText);
                    empty.setVisibility(View.VISIBLE);
                    empty.startAnimation(bounceAnim);
                    textEmpty.setVisibility(View.VISIBLE);
                    textEmpty.startAnimation(bounceAnim);
                }
                matrixListAdapter.notifyDataSetChanged();
                saveData();
            }
            return true;
        });

        /* ------------------------------------------------------------------------------- */

        listView.setAdapter(matrixListAdapter);
        listView.setOnItemLongClickListener((arg0, v, index, arg3) -> {
            PopupMenu popup = new PopupMenu(MatrixActivity.this, v);
            MenuInflater inflater = popup.getMenuInflater();
            inflater.inflate(R.menu.see_edit_delete_menu_matrix, popup.getMenu());

            popup.setOnMenuItemClickListener(item -> {
                int itemId = item.getItemId();
                if (itemId == R.id.delete) {
                    MatrixAnimationHelper helper = new MatrixAnimationHelper(matrixListAdapter, listView, matrices);

                    if (matrices.get(index).isChecked())
                        updateSelectedItems(index);
                    helper.animateRemoval(listView, v);

                    for (int i = 0; i < queue.size(); i++)
                        if (queue.get(i) != 0)
                            queue.set(i, queue.get(i) - 1);

                    if (matrices.isEmpty()) {
                        LottieAnimationView empty = findViewById(R.id.empty);
                        TextView textEmpty = findViewById(R.id.emptyText);
                        empty.setVisibility(View.VISIBLE);
                        empty.startAnimation(bounceAnim);
                        textEmpty.setVisibility(View.VISIBLE);
                        textEmpty.startAnimation(bounceAnim);
                    }
                    saveData();
                    return true;
                } else if (itemId == R.id.edit)
                    openAddDialog(true, index);
                else if (itemId == R.id.duplicate)
                    openAddDialog(false, index);
                return false;
            });
            popup.show();
            return true;
        });

        LottieAnimationView empty = findViewById(R.id.empty);
        TextView textEmpty = findViewById(R.id.emptyText);
        if (matrices.isEmpty()) {
            empty.setVisibility(View.VISIBLE);
            empty.startAnimation(bounceAnim);

            textEmpty.setVisibility(View.VISIBLE);
            textEmpty.startAnimation(bounceAnim);
        } else {
            empty.setVisibility(View.GONE);
            textEmpty.setVisibility(View.GONE);
        }

        listView.setOnItemClickListener((adapterView, view, i, l) -> {
            Spinner spinner = findViewById(R.id.matrixSpinner);
            final Animation bounce = AnimationUtils.loadAnimation(this, R.anim.bounce);
            view.startAnimation(bounce);

            updateSelectedItems(i);
            calculate(spinner.getSelectedItemPosition());
        });
    }

    private void updateSelectedItems(Integer newPos) {
        Matrix element;

        // If null you changed from !onlyOne to onlyOne(true)
        if (newPos != null) {
            element = matrices.get(newPos);

            if (!element.isChecked()) {
                queue.add(newPos);
                if (!onlyOne && queue.size() == 3) {
                    // queue.get(0) --> to be replaced
                    // queue.get(1) --> previous selection
                    // queue.get(2) --> current selection

                    //Problem here
                    matrixListAdapter.toggleElementChecked(queue.get(0));
                    queue.remove(0);
                } else if (onlyOne && queue.size() == 2) {
                    // queue.get(0) --> to be replaced
                    // queue.get(1) --> current selection

                    matrixListAdapter.toggleElementChecked(queue.get(0));
                    queue.remove(0);
                }
                matrixListAdapter.toggleElementChecked(newPos);
            } else {
                matrixListAdapter.toggleElementChecked(newPos);
                queue.remove(newPos);
            }
        } else if (onlyOne && queue.size() == 2) {
            // queue.get(0) --> to be replaced
            // queue.get(1) --> current selection

            matrixListAdapter.toggleElementChecked(queue.get(0));
            queue.remove(0);
        }
        updateSelectionNumbers();
        matrixListAdapter.notifyDataSetChanged();
        saveData();
    }

    private void updateSelectionNumbers() {
        Matrix element;
        if (!queue.isEmpty()) {
            if (!onlyOne) {
                if (queue.size() == 2) {
                    element = matrices.get(queue.get(0));
                    element.setSelectionNumber("1");
                    element = matrices.get(queue.get(1));
                    element.setSelectionNumber("2");
                } else if (queue.size() == 1) {
                    element = matrices.get(queue.get(0));
                    element.setSelectionNumber("1");
                }
            } else {
                element = matrices.get(queue.get(0));
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
        Animation shake = AnimationUtils.loadAnimation(MatrixActivity.this, R.anim.shake);
        TextView hints = findViewById(R.id.hints);
        TextView result = findViewById(R.id.result);
        EditText scalar = findViewById(R.id.scalarNumber);
        double outputValue = 0;

        double[][] firstSelection;
        double[][] secondSelection = null;

        lastResultIsMatrix = false;
        try {
            if (!onlyOne) {
                firstSelection = matrices.get(queue.get(0)).getValues();
                secondSelection = matrices.get(queue.get(1)).getValues();
            } else
                firstSelection = matrices.get(queue.get(0)).getValues();

            double scalarNumber;
            switch (spinnerPos) {
                case 0:
                    // Addition
                    resultMatrix = MatrixLogic.sumMatrix(firstSelection, Objects.requireNonNull(secondSelection));
                    lastResultIsMatrix = true;
                    break;
                case 1:
                    // Subtraction
                    resultMatrix = MatrixLogic.subMatrix(firstSelection, Objects.requireNonNull(secondSelection));
                    lastResultIsMatrix = true;
                    break;
                case 2:
                    // Matrix multiplication
                    resultMatrix = MatrixLogic.multMatrix(firstSelection, Objects.requireNonNull(secondSelection));
                    lastResultIsMatrix = true;
                    break;
                case 3:
                    // Matrix power
                    if (!scalar.getText().toString().equals("")) {
                        resultMatrix = MatrixLogic.powLinear(firstSelection, Integer.parseInt(scalar.getText().toString()));
                        hints.setText("");
                        lastResultIsMatrix = true;
                    } else {
                        hints.setText(getResources().getString(R.string.define_scalar));
                        hints.startAnimation(shake);
                        throw new RuntimeException();
                    }
                    break;
                case 4:
                    // Scalar multiplication
                    if (!scalar.getText().toString().equals("")) {
                        scalarNumber = Double.parseDouble(scalar.getText().toString());
                        resultMatrix = MatrixLogic.scalarMultiplicationMatrix(firstSelection, scalarNumber);
                        hints.setText("");
                        lastResultIsMatrix = true;
                    } else {
                        hints.setText(getResources().getString(R.string.define_scalar));
                        hints.startAnimation(shake);
                        throw new RuntimeException();
                    }
                    break;
                case 5:
                    // Determinant
                    outputValue = MatrixLogic.determinante(firstSelection);
                    lastResultIsMatrix = false;
                    break;
                case 6:
                    // Transpose
                    resultMatrix = MatrixLogic.TransposeMatrix(firstSelection);
                    lastResultIsMatrix = true;
                    break;
                case 7:
                    // Inverse
                    resultMatrix = MatrixLogic.inverse(firstSelection);
                    lastResultIsMatrix = true;
                    break;
                case 8:
                    // Scalar division
                    if (!scalar.getText().toString().equals("")) {
                        scalarNumber = Double.parseDouble(scalar.getText().toString());
                        resultMatrix = MatrixLogic.scalarDivisionMatrix(firstSelection, scalarNumber);
                        hints.setText("");
                        lastResultIsMatrix = true;
                    } else {
                        hints.setText(getResources().getString(R.string.define_scalar));
                        hints.startAnimation(shake);
                        throw new RuntimeException();
                    }
                    break;
                case 9:
                    // Matrix division
                    resultMatrix = MatrixLogic.divideMatrices(firstSelection, Objects.requireNonNull(secondSelection));
                    lastResultIsMatrix = true;
                    break;
            }
            if (lastResultIsMatrix)
                result.setText(getResources().getString(R.string.see_result));
            else {
                resultMatrix = null;
                String text = String.valueOf(outputValue);
                if (text.endsWith(".0"))
                    text = text.replace(".0", "");
                result.setText(text);
            }
        } catch (IllegalArgumentException e) {
            result.setText(getResources().getString(R.string.scalar_error));
        } catch (DeterminantNotPossible e) {
            result.setText(getResources().getString(R.string.determinant_error));
        } catch (InverseNotPossible e) {
            result.setText(getResources().getString(R.string.inverse_error));
        } catch (MatrixDifferentDimensions e) {
            result.setText(getResources().getString(R.string.matrix_error));
        } catch (Exception ignored) {
            result.setText("");
        }
        if (!result.getText().toString().equals(""))
            result.startAnimation(slideIn);
    }

    /*--------------------------------------------------------------------------------------------*/
    // Bottom dialog:
    @SuppressLint({"SetTextI18n"})
    private void openAddDialog(boolean edit, int editPos) {
        final Animation bounceAnim = AnimationUtils.loadAnimation(this, R.anim.bounce);
        addDialog = new BottomSheetDialog(this);
        addDialog.setContentView(R.layout.bottom_dialog_matrix);
        EditText name = addDialog.findViewById(R.id.matrixName);
        isAnimEnabled = true;
        col = 2;
        rows = 2;

        /*----------------------------------------------------------------------------------------*/
        //Gridview
        RecyclerView recyclerView = addDialog.findViewById(R.id.matrixGrid);

        if (edit || editPos != -1) {
            Matrix target;
            ArrayList<String> list = new ArrayList<>();
            if (editPos != -1) {
                target = matrices.get(editPos);
                col = target.getCol();
                rows = target.getRows();
                Objects.requireNonNull(name).setText(matrices.get(editPos).getName());
                for (int i = 0; i < target.getValues().length; i++)
                    for (int j = 0; j < target.getValues()[i].length; j++) {
                        String aux = String.valueOf(target.getValues()[i][j]);
                        if (aux.endsWith(".0"))
                            aux = aux.replace(".0", "");
                        list.add(aux);
                    }
            } else {
                // See result
                for (double[] matrix : resultMatrix)
                    for (double v : matrix) {
                        String aux = String.valueOf(v);
                        if (aux.endsWith(".0"))
                            aux = aux.replace(".0", "");
                        list.add(aux);
                    }
                rows = resultMatrix.length;
                col = resultMatrix[0].length;
            }
            editTexts = list;
        } else {
            editTexts = new ArrayList<>();
            editTexts.add("");
            editTexts.add("");
            editTexts.add("");
            editTexts.add("");
        }

        gridViewAdapter = new RecyclerViewGridAdapter(this, editTexts, recyclerView, (position, recyclerView1, editText) -> Dialogs.showKeyboard(MatrixActivity.this, editText::setText));
        Objects.requireNonNull(recyclerView).setAdapter(gridViewAdapter);

        //Changing columns number:
        GridLayoutManager layoutManager = new GridLayoutManager(this, col);
        recyclerView.setLayoutManager(layoutManager);

        // Drag and drop:
        ItemTouchHelper.Callback callback = new ItemTouchHelper.Callback() {
            @Override
            public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
                int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN | ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;
                return makeMovementFlags(dragFlags, 0);
            }

            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                gridViewAdapter.onItemMove(viewHolder.getAdapterPosition(), target.getAdapterPosition());
                viewHolder.itemView.setAlpha(0.5f);
                return true;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                // Not implemented
            }

            @Override
            public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
                if (actionState != ItemTouchHelper.ACTION_STATE_IDLE) {
                    viewHolder.itemView.setScaleX(1.3f);
                    viewHolder.itemView.setScaleY(1.3f);
                    viewHolder.itemView.requestFocus();
                }
                super.onSelectedChanged(viewHolder, actionState);
            }

            @Override
            public void clearView(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
                super.clearView(recyclerView, viewHolder);
                viewHolder.itemView.setScaleX(1.0f);
                viewHolder.itemView.setScaleY(1.0f);
                viewHolder.itemView.setAlpha(1.0f);
            }
        };
        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(recyclerView);

        /*----------------------------------------------------------------------------------------*/
        TextView rowsText = addDialog.findViewById(R.id.rows);
        TextView colText = addDialog.findViewById(R.id.columns);

        Objects.requireNonNull(colText).setText(String.valueOf(col));
        Objects.requireNonNull(rowsText).setText(String.valueOf(rows));

        ImageButton addCol = addDialog.findViewById(R.id.plusCol);
        Objects.requireNonNull(addCol).setOnClickListener(view -> {
            isAnimEnabled = false;
            addCol.startAnimation(bounceAnim);
            if (col != 8) {
                addColumn(recyclerView);
                recyclerView.setAdapter(gridViewAdapter);
                setColumns(recyclerView);
                Objects.requireNonNull(colText).setText(String.valueOf(col));
            }
        });

        ImageButton addRow = addDialog.findViewById(R.id.plusRow);
        Objects.requireNonNull(addRow).setOnClickListener(view -> {
            isAnimEnabled = false;
            addRow.startAnimation(bounceAnim);
            if (rows != 8) {
                addRow(recyclerView);
                recyclerView.setAdapter(gridViewAdapter);
                setColumns(recyclerView);
                Objects.requireNonNull(rowsText).setText(String.valueOf(rows));
            }
        });

        ImageButton removeRow = addDialog.findViewById(R.id.minusRow);
        Objects.requireNonNull(removeRow).setOnClickListener(view -> {
            isAnimEnabled = false;
            removeRow.startAnimation(bounceAnim);
            if (rows != 1) {
                removeRow(recyclerView);
                recyclerView.setAdapter(gridViewAdapter);
                setColumns(recyclerView);
                Objects.requireNonNull(rowsText).setText(String.valueOf(rows));
            }
        });

        ImageButton removeCol = addDialog.findViewById(R.id.minusCol);
        Objects.requireNonNull(removeCol).setOnClickListener(view -> {
            isAnimEnabled = false;
            removeCol.startAnimation(bounceAnim);
            if (col != 1) {
                removeColumn(recyclerView);
                recyclerView.setAdapter(gridViewAdapter);
                setColumns(recyclerView);
                Objects.requireNonNull(colText).setText(String.valueOf(col));
            }
        });

        // Save button:
        Button save = addDialog.findViewById(R.id.save);
        Objects.requireNonNull(save).setOnClickListener(view -> {
            save.startAnimation(bounceAnim);
            Matrix matrix = new Matrix();

            try {
                matrix.setValues(getValues());
                if (matrix.getValues() != null) {
                    if (!Objects.requireNonNull(name).getText().toString().equals(""))
                        matrix.setName(Objects.requireNonNull(name).getText().toString());
                    else
                        matrix.setName(getResources().getString(R.string.matrix) + " " + (matrices.size() + 1));

                    matrix.setCol(col);
                    matrix.setRows(rows);

                    if (!edit || editPos == -1)
                        matrices.add(matrix);
                    else
                        matrices.set(editPos, matrix);

                    if (!matrices.isEmpty()) {
                        LottieAnimationView empty = findViewById(R.id.empty);
                        TextView textEmpty = findViewById(R.id.emptyText);
                        bounceOutAnim(empty);
                        bounceOutAnim(textEmpty);
                    }

                    matrixListAdapter.notifyDataSetChanged();
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

    @SuppressLint("NotifyDataSetChanged")
    private void removeRow(RecyclerView recyclerView) {
        rows -= 1;

        editTexts = gridViewAdapter.getData();
        editTexts = new ArrayList<>(editTexts.subList(0, editTexts.size() - col));

        gridViewAdapter = new RecyclerViewGridAdapter(this, editTexts, recyclerView, (position, recyclerView1, editText) -> Dialogs.showKeyboard(MatrixActivity.this, editText::setText));
        gridViewAdapter.notifyDataSetChanged();
    }

    private void addRow(RecyclerView recyclerView) {
        rows += 1;

        editTexts = gridViewAdapter.getData();
        for (int i = 0; i < col; i++)
            editTexts.add("");

        gridViewAdapter = new RecyclerViewGridAdapter(this, editTexts, recyclerView, (position, recyclerView1, editText) -> Dialogs.showKeyboard(MatrixActivity.this, editText::setText));
    }

    private void addColumn(RecyclerView recyclerView) {
        col += 1;
        int pos = col - 1;

        editTexts = gridViewAdapter.getData();
        for (int i = 0; i < rows; i++) {
            editTexts.add(pos, "");
            pos += col;
        }

        gridViewAdapter = new RecyclerViewGridAdapter(this, editTexts, recyclerView, (position, recyclerView1, editText) -> Dialogs.showKeyboard(MatrixActivity.this, editText::setText));
    }

    private void removeColumn(RecyclerView recyclerView) {
        int pos = col - 1;

        editTexts = gridViewAdapter.getData();
        for (int i = 0; i < rows; i++) {
            editTexts.remove(pos);
            pos += col - 1;
        }

        col -= 1;
        gridViewAdapter = new RecyclerViewGridAdapter(this, editTexts, recyclerView, (position, recyclerView1, editText) -> Dialogs.showKeyboard(MatrixActivity.this, editText::setText));
    }

    private void setColumns(RecyclerView recyclerView) {
        GridLayoutManager layoutManager = new GridLayoutManager(this, col);
        recyclerView.setLayoutManager(layoutManager);
    }

    private double[][] getValues() {
        boolean allNull = true;
        double[][] values = new double[rows][col];
        editTexts = gridViewAdapter.getData();
        for (int i = 0; i < rows; i++)
            for (int j = 0; j < col; j++)
                if (!editTexts.get(i * col + j).equals("")) {
                    values[i][j] = Double.parseDouble(editTexts.get(i * col + j));
                    allNull = false;
                }
        if (!allNull)
            return values;
        else
            return null;
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

    /*--------------------------------------------------------------------------------------------*/
    // Some other methods:

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen())
            drawerLayout.closeDrawer();
        else
            super.onBackPressed();
    }

    private void bounceOutAnim(View view) {
        if (view.getVisibility() == View.VISIBLE) {
            final Animation bounceOut = AnimationUtils.loadAnimation(this, R.anim.bounce_out);
            view.startAnimation(bounceOut);
            new Handler().postDelayed(() -> view.setVisibility(View.GONE), 200);
        }
    }

    /*--------------------------------------------------------------------------------------------*/
    // Save and load data:
    private void loadData() {
        try {
            SharedPreferences sharedPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE);
            Type type = new TypeToken<ArrayList<String>>() {
            }.getType();
            Gson gson = new Gson();

            String json = sharedPreferences.getString("matrices", null);
            matrices = stringToData(gson.fromJson(json, type));

            json = sharedPreferences.getString("matrix_queue", null);
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

            String json = gson.toJson(dataToString(matrices));
            editor.putString("matrices", json);
            editor.apply();

            json = gson.toJson(queueToString(queue));
            editor.putString("matrix_queue", json);
            editor.apply();

            //Toast.makeText(this, "Saved", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            //Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private ArrayList<Matrix> stringToData(ArrayList<String> array) {
        ArrayList<Matrix> output = new ArrayList<>();

        Matrix auxObject = new Matrix();
        for (int i = 0; i < array.size(); i += 6) {
            auxObject.setName(array.get(i));
            auxObject.setRows(Integer.parseInt(array.get(i + 1)));
            auxObject.setCol(Integer.parseInt(array.get(i + 2)));
            auxObject.setValues(stringToMatrix(array.get(i + 3)));
            auxObject.setSelectionNumber(array.get(i + 4));
            auxObject.setChecked(Boolean.parseBoolean(array.get(i + 5)));

            output.add(auxObject);
            auxObject = new Matrix();
        }

        return output;
    }

    private ArrayList<String> dataToString(ArrayList<Matrix> matrices) {
        ArrayList<String> output = new ArrayList<>();

        for (int i = 0; i < matrices.size(); i++) {
            output.add(matrices.get(i).getName());
            output.add(String.valueOf(matrices.get(i).getRows()));
            output.add(String.valueOf(matrices.get(i).getCol()));
            output.add(matrixToString(matrices.get(i).getValues()));
            output.add(matrices.get(i).getSelectionNumber());
            output.add(String.valueOf(matrices.get(i).isChecked()));
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
}