package com.app.numplex.home;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieDrawable;
import com.app.numplex.R;
import com.app.numplex.algebra.AlgebraActivity;
import com.app.numplex.application.SettingsActivity;
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
import com.app.numplex.utils.DuoDrawerSetter;
import com.app.numplex.utils.Functions;
import com.etebarian.meowbottomnavigation.MeowBottomNavigation;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

import nl.psdcompany.duonavigationdrawer.views.DuoDrawerLayout;

public class HomeActivity extends AppCompatActivity {
    private DuoDrawerLayout drawerLayout;
    private MeowBottomNavigation menu;

    public static ArrayList<String> favorites;
    public static ArrayList<HomeElements> elements;

    private RecyclerViewAdapter recyclerViewAdapter;
    private HomeListAdapter listAdapter;

    private boolean authorizeDelete = false;
    private FrameLayout deleteArea;
    private View toBeDeleted;
    private int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /*----------------------------------------------------------------------------------------*/
        // Loading configs:
        Functions.updateConfigs(this, this);
        /*----------------------------------------------------------------------------------------*/

        setContentView(R.layout.activity_home);
        deleteArea = findViewById(R.id.deleteArea);

        /*----------------------------------------------------------------------------------------*/
        //Navigation menu:
        setTabBar();
        setDuoNavigationDrawer();
        /*----------------------------------------------------------------------------------------*/
        // Favorites menu:
        setFavorites();
        /*----------------------------------------------------------------------------------------*/
        // List:
        setListView();

        /*----------------------------------------------------------------------------------------*/
        menu.show(2, false);
        /*----------------------------------------------------------------------------------------*/
        // Search view:
        SearchView searchView = findViewById(R.id.search_view);
        searchView.setQueryHint(Html.fromHtml("<font color = #05C2E3>" + getResources().getString(R.string.search) + "</font>"));

        // Set up a TextWatcher that will update the ListView based on the search query
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                listAdapter.filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                listAdapter.filter(newText);
                return false;
            }
        });

        /*----------------------------------------------------------------------------------------*/
        //Spinner
        Spinner spinner = findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.categories_array, R.layout.item_spinner_text);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        // Flag variable to track the first change event
        final boolean[] isFirstChange = {true};
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (isFirstChange[0])
                    isFirstChange[0] = false;
                else {
                    switch (position) {
                        case 0 -> searchView.setQuery("Mathematics", true);
                        case 1 -> searchView.setQuery("Algebra", true);
                        case 2 -> searchView.setQuery("Converters", true);
                        case 3 -> searchView.setQuery("Electronics", true);
                        case 4 -> searchView.setQuery("Miscellaneous", true);
                    }
                }
            }

            public void onNothingSelected(AdapterView<?> parent) {
                //Do nothing
            }
        });

        // Home has no App rater
    }

    /*--------------------------------------------------------------------------------------------*/
    //Set favorites:
    private void setFavorites() {
        final Animation bounceAnim = AnimationUtils.loadAnimation(this, R.anim.bounce_in);
        loadData();

        if (elements == null || elements.size() != 16) {
            elements = new ArrayList<>();
            elements.add(new HomeElements("calculator"));
            elements.add(new HomeElements("algebra"));
            elements.add(new HomeElements("matrix"));
            elements.add(new HomeElements("matrixComplex"));
            elements.add(new HomeElements("boolean"));
            elements.add(new HomeElements("digital"));
            elements.add(new HomeElements("complex"));
            elements.add(new HomeElements("mmc"));
            elements.add(new HomeElements("series"));
            elements.add(new HomeElements("resistor"));
            elements.add(new HomeElements("randomizer"));
            elements.add(new HomeElements("distribution"));
            elements.add(new HomeElements("kmap"));
            elements.add(new HomeElements("network"));
            //elements.add(new HomeElements("partial_fractions"));
            elements.add(new HomeElements("queue"));
            elements.add(new HomeElements("dig_com"));

            if(favorites != null)
                favorites.clear();
        }

        TextView favoritesText = findViewById(R.id.favoritesText);
        RecyclerView recyclerView = findViewById(R.id.favorites);
        recyclerView.bringToFront();

        if (favorites == null)
            favorites = new ArrayList<>();

        LinearLayoutManager layoutManager = new
                LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerViewAdapter = new RecyclerViewAdapter(this, favorites, favoritesText, recyclerView);

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(recyclerViewAdapter);

        // Drag and drop:
        ItemTouchHelper.Callback callback = new ItemTouchHelper.Callback() {
            @Override
            public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
                int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN | ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;
                return makeMovementFlags(dragFlags, 0);
            }

            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                position = target.getAdapterPosition();
                recyclerViewAdapter.onItemMove(viewHolder.getAdapterPosition(), target.getAdapterPosition());
                return true;
            }

            @Override
            public void onChildDraw(
                    @NonNull Canvas c,
                    @NonNull RecyclerView recyclerView,
                    @NonNull RecyclerView.ViewHolder viewHolder,
                    float dX,
                    float dY,
                    int actionState,
                    boolean isCurrentlyActive
            ) {
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
                LottieAnimationView anim = findViewById(R.id.animDelete);

                if (isViewOverlapping(viewHolder.itemView, deleteArea)) {
                    // The dragged view is within the bounds of the target view.
                    viewHolder.itemView.setAlpha(0.5f);
                    deleteArea.setScaleX(1.1f);
                    deleteArea.setScaleY(1.1f);
                    anim.setRepeatCount(LottieDrawable.INFINITE);
                    if(!anim.isAnimating())
                        anim.playAnimation();
                } else {
                    viewHolder.itemView.setAlpha(1f);
                    deleteArea.setScaleX(1.0f);
                    deleteArea.setScaleY(1.0f);
                    if(anim.isAnimating())
                        anim.setRepeatCount(1);
                }
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                //not implemented
            }

            @Override
            public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
                recyclerViewAdapter.setDragging(true);

                if (!authorizeDelete)
                    super.onSelectedChanged(viewHolder, actionState);

                if (actionState != ItemTouchHelper.ACTION_STATE_IDLE) {
                    viewHolder.itemView.setScaleX(1.1f);
                    viewHolder.itemView.setScaleY(1.1f);
                    viewHolder.itemView.requestFocus();

                    deleteArea.bringToFront();
                    ViewGroup parentView = (ViewGroup) viewHolder.itemView.getParent();
                    parentView.bringToFront();

                    deleteArea.setVisibility(View.VISIBLE);
                    deleteArea.startAnimation(bounceAnim);
                    toBeDeleted = viewHolder.itemView;
                    position = viewHolder.getAdapterPosition();
                    authorizeDelete = false;
                } else if (isViewOverlapping(toBeDeleted, deleteArea))
                    authorizeDelete = true;
            }

            @Override
            public void clearView(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
                recyclerViewAdapter.setDragging(false);
                super.clearView(recyclerView, viewHolder);
                bounceOutAnim(deleteArea);
                if (authorizeDelete) {
                    elements = listAdapter.getOriginalData();
                    favorites = recyclerViewAdapter.getData();

                    for (int i = 0; i < elements.size(); i++)
                        if (elements.get(i).getId().equals(favorites.get(position))) {
                            listAdapter.toggleElementFavorite(i);
                            listAdapter.notifyDataSetChanged();
                            break;
                        }
                    authorizeDelete = false;
                    deleteItemAnim(viewHolder.itemView);
                } else {
                    viewHolder.itemView.setScaleX(1.0f);
                    viewHolder.itemView.setScaleY(1.0f);
                }
            }
        };
        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(recyclerView);

        if (!favorites.isEmpty()) {
            favoritesText.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }

    private boolean isViewOverlapping(View firstView, View secondView) {
        int[] firstPosition = new int[2];
        int[] secondPosition = new int[2];

        firstView.getLocationOnScreen(firstPosition);
        secondView.getLocationOnScreen(secondPosition);

        // Rect constructor parameters: left, top, right, bottom
        Rect rectFirstView = new Rect(firstPosition[0], firstPosition[1],
                firstPosition[0] + firstView.getMeasuredWidth(), firstPosition[1] + firstView.getMeasuredHeight());
        Rect rectSecondView = new Rect(secondPosition[0], secondPosition[1],
                secondPosition[0] + secondView.getMeasuredWidth(), secondPosition[1] + secondView.getMeasuredHeight());
        return rectFirstView.intersect(rectSecondView);
    }

    /*--------------------------------------------------------------------------------------------*/
    //Set list:
    private void setListView() {
        ListView listView = findViewById(R.id.listHome);
        listAdapter = new HomeListAdapter(this, elements, recyclerViewAdapter);
        listView.setAdapter(listAdapter);

        listView.setOnItemClickListener((adapterView, view, i, l) -> {
            if(!elements.get(i).getId().equals("blank")) {
                final Animation bounce = AnimationUtils.loadAnimation(this, R.anim.bounce);
                view.startAnimation(bounce);

                Intent intent = switch (elements.get(i).getId()) {
                    case "calculator" -> new Intent(this, CalculatorActivity.class);
                    case "complex" -> new Intent(this, ComplexCalculatorActivity.class);
                    case "algebra" -> new Intent(this, AlgebraActivity.class);
                    case "matrix" -> new Intent(this, MatrixActivity.class);
                    case "digital" -> new Intent(this, DigitalActivity.class);
                    case "resistor" -> new Intent(this, ResistorActivity.class);
                    case "mmc" -> new Intent(this, MMC_MDC_Activity.class);
                    case "randomizer" -> new Intent(this, RandomizerActivity.class);
                    case "distribution" -> new Intent(this, DistributionsActivity.class);
                    case "series" -> new Intent(this, SeriesActivity.class);
                    case "boolean" -> new Intent(this, BooleanActivity.class);
                    case "matrixComplex" -> new Intent(this, MatrixComplexActivity.class);
                    case "kmap" -> new Intent(this, KarnaughActivity.class);
                    case "network" -> new Intent(this, NetworkActivity.class);
                    case "partial_fractions" -> new Intent(this, PartialFractionActivity.class);
                    case "queue" -> new Intent(this, QueueActivity.class);
                    case "dig_com" -> new Intent(this, DigitalCommunicationActivity.class);
                    default -> null;
                };

                new Handler().postDelayed(() -> this.startActivity(intent), 100);
            }
        });
    }

    /*--------------------------------------------------------------------------------------------*/
    //Tab bar menu:
    private void setTabBar() {
        final int ID_MENU = 1;
        final int ID_HOME = 2;
        final int ID_CONFIG = 3;

        menu = findViewById(R.id.tabBar);
        menu.add(new MeowBottomNavigation.Model(ID_MENU, R.drawable.ic_round_menu_24));
        menu.add(new MeowBottomNavigation.Model(ID_HOME, R.drawable.ic_baseline_round_home_24));
        menu.add(new MeowBottomNavigation.Model(ID_CONFIG, R.drawable.ic_baseline_settings_24));
        menu.show(2, false);
        menu.bringToFront();

        menu.setOnClickMenuListener(model -> {
            switch (model.getId()) {
                case ID_MENU -> new Handler().postDelayed(() -> drawerLayout.openDrawer(), 300);
                case ID_CONFIG -> new Handler().postDelayed(() -> {
                    Intent intent = new Intent(this, SettingsActivity.class);
                    this.startActivity(intent);
                }, 300);
            }
            return null;
        });
    }

    /*--------------------------------------------------------------------------------------------*/
    //Navigation menu:
    private void setDuoNavigationDrawer() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        drawerLayout = findViewById(R.id.homeDrawer);
        ConstraintLayout content = findViewById(R.id.content);

        DuoDrawerSetter drawerSetter = new DuoDrawerSetter(drawerLayout, this);
        drawerSetter.setDuoNavigationDrawer(this, toolbar, menu, content);
    }

    /*--------------------------------------------------------------------------------------------*/
    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen())
            drawerLayout.closeDrawer();
        else
            super.onBackPressed();
    }

    @Override
    protected void onResume() {
        super.onResume();
        new Handler().postDelayed(() -> menu.show(2, true), 200);
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

    private void deleteItemAnim(View view) {
        view.animate()
                .alpha(0)
                .setDuration(200)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        view.setVisibility(View.GONE);
                    }
                });
    }

    /*--------------------------------------------------------------------------------------------*/
    // Save and load data:
    private void loadData() {
        try {
            SharedPreferences sharedPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE);
            Type type = new TypeToken<ArrayList<String>>() {
            }.getType();
            Gson gson = new Gson();

            String json = sharedPreferences.getString("favorites", null);
            favorites = gson.fromJson(json, type);
            json = sharedPreferences.getString("homeElements", null);
            elements = stringToData(gson.fromJson(json, type));

            //Toast.makeText(this, "Load.", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            //Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    public void saveData(Context context) {
        try {
            SharedPreferences sharedPreferences = context.getSharedPreferences("shared preferences", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            Gson gson = new Gson();

            String json = gson.toJson(favorites);
            editor.putString("favorites", json);
            editor.apply();

            json = gson.toJson(dataToString(elements));
            editor.putString("homeElements", json);
            editor.apply();

            //Toast.makeText(this, "Saved", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            //Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private ArrayList<HomeElements> stringToData(ArrayList<String> array) {
        ArrayList<HomeElements> output = new ArrayList<>();

        HomeElements auxObject;
        for (int i = 0; i < array.size(); i += 2) {
            auxObject = new HomeElements(array.get(i));
            auxObject.setFavorite(Boolean.parseBoolean(array.get(i + 1)));

            output.add(auxObject);
        }

        return output;
    }

    private ArrayList<String> dataToString(ArrayList<HomeElements> array) {
        ArrayList<String> output = new ArrayList<>();

        for (int i = 0; i < array.size(); i++) {
            output.add(array.get(i).getId());
            output.add(String.valueOf(array.get(i).isFavorite()));
        }

        return output;
    }
}