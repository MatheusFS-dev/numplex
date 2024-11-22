package com.app.numplex.randomizer;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.BounceInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.airbnb.lottie.LottieAnimationView;
import com.app.numplex.R;
import com.app.numplex.application.AppRater;
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
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;

import nl.psdcompany.duonavigationdrawer.views.DuoDrawerLayout;

public class RandomizerActivity extends AppCompatActivity {
    private DuoDrawerLayout drawerLayout;

    private ArrayList<String> elements;
    private SwipeMenuListView listView;
    private BottomSheetDialog addDialog;
    private SimpleTextListAdapter listAdapter;
    private static List<List<String>> groups = new ArrayList<>();
    private static final String STATE_EDIT_TEXT = "state_edit_text";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /*----------------------------------------------------------------------------------------*/
        // Loading configs:
        Functions.updateConfigs(this, this);
        /*----------------------------------------------------------------------------------------*/

        setContentView(R.layout.activity_randomizer);

        listView = findViewById(R.id.list);

        setListView();

        //Navigation drawer
        setDuoNavigationDrawer();
        FloatingActionButton fabAdd = findViewById(R.id.fab);
        EditText groupsText = findViewById(R.id.groups);

        //Toolbar
        Functions.setToolbar(this, getResources().getString(R.string.home_randomizer), drawerLayout, () -> Functions.showSpotlightMessage(RandomizerActivity.this,
                fabAdd,
                getString(R.string.home_randomizer),
                getString(R.string.help_add_element_randomizer),
                120f,
                () -> Functions.showSpotlightMessage(RandomizerActivity.this,
                        groupsText,
                        getString(R.string.home_randomizer),
                        getString(R.string.help_group_randomizer),
                        120f, null)));
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
                openAddDialog(null);
            }
        });

        FloatingActionButton fabClear = findViewById(R.id.fabClear);
        fabClear.setOnClickListener(view -> {
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
        //Generate
        Button generate = findViewById(R.id.generate);
        generate.setOnClickListener(view -> {
            generate.startAnimation(bounceAnim);
            generate();
        });
        /*----------------------------------------------------------------------------------------*/
        HorizontalScrollView container = findViewById(R.id.resultContainer);
        TextView result = findViewById(R.id.result);
        result.setOnClickListener(view -> {
            container.startAnimation(bounceAnim);
            showResult();
        });

        //App rater:
        AppRater.app_launched(this, this);
    }

    /*--------------------------------------------------------------------------------------------*/
    private void generate() {
        final Animation shake = AnimationUtils.loadAnimation(RandomizerActivity.this, R.anim.shake);
        final Animation slideIn = AnimationUtils.loadAnimation(this, R.anim.slide_in_left);
        TextView result = findViewById(R.id.result);
        EditText groupsText = findViewById(R.id.groups);

        String oldTextValue = result.getText().toString();
        try {
            // Group randomizer
            int groupsNumber = 2;

            if (!groupsText.getText().toString().equals(""))
                groupsNumber = Integer.parseInt(groupsText.getText().toString());

            groups = GroupRandomizerLogic.divideIntoGroups(elements, groupsNumber);
            showResult();
            if (!result.getText().toString().equals(oldTextValue))
                result.startAnimation(slideIn);
        } catch (Exception e) {
            HorizontalScrollView container = findViewById(R.id.resultContainer);
            result.setText(R.string.error);
            container.startAnimation(shake);
        }
    }

    private void showResult() {
        HashMap<String, List<String>> expandableListDetail = new LinkedHashMap<>();

        if (!groups.isEmpty()) {
            int numberOfGroups = 1;
            for (List<String> group : groups)
                expandableListDetail.put(getResources().getString(R.string.group) + " " + numberOfGroups++, group);

            addDialog = new BottomSheetDialog(this);
            addDialog.setContentView(R.layout.layout_expandable_list);

            ExpandableListView expandableListView = addDialog.findViewById(R.id.expandableListView);

            CustomExpandableListAdapter expandableListAdapter =
                    new CustomExpandableListAdapter(this, new ArrayList<>(expandableListDetail.keySet()),
                            expandableListDetail);
            Objects.requireNonNull(expandableListView).setAdapter(expandableListAdapter);

            addDialog.show();
        }
    }

    /*--------------------------------------------------------------------------------------------*/
    //List methods:
    private void setListView() {
        loadData();
        final Animation bounceAnim = AnimationUtils.loadAnimation(this, R.anim.bounce_in);

        if (elements == null)
            elements = new ArrayList<>();

        listAdapter = new SimpleTextListAdapter(this, elements);

        /* --------------------------------- Swipe option -------------------------------- */
        SwipeMenuCreator creator = menu -> {
            // create "delete" item
            SwipeMenuItem deleteItem = new SwipeMenuItem(
                    getApplicationContext());
            // set item background
            deleteItem.setBackground(ContextCompat.getDrawable(this, R.drawable.bg_swipe_delete_circle));
            // set item width
            deleteItem.setWidth(170);
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
                SimpleTextAnimationHelper helper = new SimpleTextAnimationHelper(listAdapter, listView, elements);
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
                saveData();
            }
            return true;
        });

        /* ------------------------------------------------------------------------------- */

        listView.setAdapter(listAdapter);
        listView.setOnItemLongClickListener((arg0, v, index, arg3) -> {
            PopupMenu popup = new PopupMenu(RandomizerActivity.this, v);
            MenuInflater inflater = popup.getMenuInflater();
            inflater.inflate(R.menu.see_edit_delete_menu_matrix, popup.getMenu());

            popup.setOnMenuItemClickListener(item -> {
                int itemId = item.getItemId();
                if (itemId == R.id.delete) {
                    SimpleTextAnimationHelper helper = new SimpleTextAnimationHelper(listAdapter, listView, elements);
                    helper.animateRemoval(listView, getViewByPosition(index, listView));

                    if (elements.isEmpty()) {
                        LottieAnimationView empty = findViewById(R.id.empty);
                        TextView textEmpty = findViewById(R.id.emptyText);
                        empty.setVisibility(View.VISIBLE);
                        empty.startAnimation(bounceAnim);
                        textEmpty.setVisibility(View.VISIBLE);
                        textEmpty.startAnimation(bounceAnim);
                    }
                    saveData();
                    return true;
                } else if (itemId == R.id.edit) {
                    openAddDialog(index);
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
            final Animation bounce = AnimationUtils.loadAnimation(this, R.anim.bounce);
            view.startAnimation(bounce);

            openAddDialog(i);
        });
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
    // Bottom dialog:
    private void openAddDialog(Integer editPos) {
        addDialog = new BottomSheetDialog(this);
        addDialog.setContentView(R.layout.bottom_dialog_randomizer);

        Button save = addDialog.findViewById(R.id.saveClose);
        ImageButton add = addDialog.findViewById(R.id.save);
        EditText name = addDialog.findViewById(R.id.name);

        // Editing?
        if (editPos != null) {
            Objects.requireNonNull(add).setVisibility(View.GONE);
            Objects.requireNonNull(name).setText(elements.get(editPos));
            name.setSelection(name.length());
        }

        /*----------------------------------------------------------------------------------------*/
        // Save and close button
        Objects.requireNonNull(save).setOnClickListener(view -> {
            String newElement = Objects.requireNonNull(name).getText().toString();
            try {
                if (!newElement.equals("")) {
                    if (editPos == null)
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

        // Add button
        final Animation bounceAnim = AnimationUtils.loadAnimation(this, R.anim.bounce);
        Objects.requireNonNull(add).setOnClickListener(view -> {
            add.startAnimation(bounceAnim);
            String newElement = Objects.requireNonNull(name).getText().toString();
            try {
                if (!newElement.equals("")) {
                    elements.add(newElement);
                    listAdapter.notifyDataSetChanged();

                    name.setText("");

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
        });
        addDialog.show();
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

    private void saveData() {
        try {
            SharedPreferences sharedPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            Gson gson = new Gson();

            String json = gson.toJson(elements);
            editor.putString("names", json);
            editor.apply();

            //Toast.makeText(this, "Saved", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            //Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void loadData() {
        try {
            SharedPreferences sharedPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE);
            Type type = new TypeToken<ArrayList<String>>() {
            }.getType();
            Gson gson = new Gson();

            String json = sharedPreferences.getString("names", null);
            elements = gson.fromJson(json, type);

            //Toast.makeText(this, "Load.", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            //Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        TextView result = findViewById(R.id.result);
        outState.putString(STATE_EDIT_TEXT, result.getText().toString());
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        TextView result = findViewById(R.id.result);
        result.setText(savedInstanceState.getString(STATE_EDIT_TEXT));
    }
}
