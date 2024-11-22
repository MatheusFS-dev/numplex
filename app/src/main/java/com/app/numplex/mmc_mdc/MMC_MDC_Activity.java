package com.app.numplex.mmc_mdc;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.app.numplex.R;
import com.app.numplex.application.AppRater;
import com.app.numplex.utils.Dialogs;
import com.app.numplex.utils.DuoDrawerSetter;
import com.app.numplex.utils.Functions;
import com.app.numplex.utils.LottieDialog;
import com.app.numplex.utils.MultipleClickListener;
import com.app.numplex.utils.RecyclerViewInterface;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

import nl.psdcompany.duonavigationdrawer.views.DuoDrawerLayout;

public class MMC_MDC_Activity extends AppCompatActivity implements RecyclerViewInterface {
    private DuoDrawerLayout drawerLayout;

    private static ArrayList<Double> elements = new ArrayList<>();
    private GridListAdapter listAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /*----------------------------------------------------------------------------------------*/
        // Loading configs:
        Functions.updateConfigs(this, this);
        /*----------------------------------------------------------------------------------------*/

        setContentView(R.layout.activity_mmc_mdc);
        setListView();

        //Navigation drawer
        setDuoNavigationDrawer();

        FloatingActionButton fabAdd = findViewById(R.id.fab);
        //Toolbar
        Functions.setToolbar(this, getResources().getString(R.string.mmc_mdc), drawerLayout, () -> Functions.showSpotlightMessage(MMC_MDC_Activity.this,
                fabAdd,
                getString(R.string.mmc_mdc),
                getString(R.string.help_mmc_mdc),
                120f, null));

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
                calculate();

                new Handler().postDelayed(this::setListView, 3000);
            }
        });

        /*----------------------------------------------------------------------------------------*/
        //Result
        TextView mdc = findViewById(R.id.mdc_result);
        TextView mmc = findViewById(R.id.mmc_result);

        mdc.setOnLongClickListener(view -> {
            ClipboardManager clipboardManager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
            ClipData clipData = ClipData.newPlainText("text", mdc.getText().toString());
            clipboardManager.setPrimaryClip(clipData);
            mdc.startAnimation(bounceAnim);

            Toast.makeText(getApplicationContext(), R.string.copied, Toast.LENGTH_SHORT).show();
            return false;
        });

        mmc.setOnLongClickListener(view -> {
            ClipboardManager clipboardManager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
            ClipData clipData = ClipData.newPlainText("text", mmc.getText().toString());
            clipboardManager.setPrimaryClip(clipData);
            mmc.startAnimation(bounceAnim);

            Toast.makeText(getApplicationContext(), R.string.copied, Toast.LENGTH_SHORT).show();
            return false;
        });

        //App rater:
        AppRater.app_launched(this, this);

        calculate();
    }

    /*--------------------------------------------------------------------------------------------*/
    //List methods:
    private void setListView() {
        loadData();
        final Animation bounceAnim = AnimationUtils.loadAnimation(this, R.anim.bounce_in);

        RecyclerView listView = findViewById(R.id.list);
        listAdapter = new GridListAdapter(elements, this, this);
        listView.setAdapter(listAdapter);
        /* ------------------------------------------------------------------------------- */
        //Changing columns number:
        GridLayoutManager layoutManager = new GridLayoutManager(this, 2);
        listView.setLayoutManager(layoutManager);

        /* ------------------------------------------------------------------------------- */
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
    /*--------------------------------------------------------------------------------------------*/
    // Calculating:
    private void calculate() {
        final Animation slideIn = AnimationUtils.loadAnimation(this, R.anim.slide_in_right);
        Animation shake = AnimationUtils.loadAnimation(MMC_MDC_Activity.this, R.anim.shake);
        TextView mdc = findViewById(R.id.mdc_result);
        TextView mmc = findViewById(R.id.mmc_result);

        String text;
        double[] numbers = new double[elements.size()];
        String oldTextValue = mmc.getText().toString();
        try {
            if(elements.size() > 1) {
                for(int i = 0; i < elements.size(); i++)
                    numbers[i] = elements.get(i);

                text = String.valueOf(MMC_MDC_Logic.findGCF(numbers));

                if (text.endsWith(".0"))
                    text = text.replace(".0", "");
                mdc.setText(text);

                text = String.valueOf(MMC_MDC_Logic.findLCM(numbers));
                if (text.endsWith(".0"))
                    text = text.replace(".0", "");
                mmc.setText(text);
            }
            else{
                mdc.setText(null);
                mmc.setText(null);
            }
        } catch (Exception ignored) {
            mdc.setText(R.string.error);
            mmc.setText(R.string.error);

            mdc.startAnimation(shake);
            mmc.startAnimation(shake);
        }

        if (!mmc.getText().toString().equals(oldTextValue)) {
            if (!mdc.getText().toString().equals(""))
                mdc.startAnimation(slideIn);

            if (!mmc.getText().toString().equals(""))
                mmc.startAnimation(slideIn);
        }
    }

    /*--------------------------------------------------------------------------------------------*/
    private void openAddDialog(Integer editPos) {
        Dialogs.showKeyboard(this, value -> addNewElement(Double.valueOf(value), editPos));

        if (!elements.isEmpty()) {
            TextView textEmpty = findViewById(R.id.emptyText);
            LottieAnimationView empty = findViewById(R.id.empty);
            bounceOutAnim(empty);
            bounceOutAnim(textEmpty);
        }
    }

    public void addNewElement(Double value, Integer editPos){
        if(editPos != null) {
            elements.set(editPos, value);
            listAdapter.notifyItemChanged(editPos);
        }
        else {
            elements.add(value);
            listAdapter.notifyItemInserted(elements.size() - 1);
        }
        calculate();
        saveData();
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

            //Toast.makeText(this, "Load.", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            //Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    public void saveData() {
        try {
            SharedPreferences sharedPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            Gson gson = new Gson();

            String json = gson.toJson(dataToString(elements));
            editor.putString("elements", json);
            editor.apply();

            //Toast.makeText(this, "Saved", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            //Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private ArrayList<String> dataToString(ArrayList<Double> array){
        ArrayList<String> output = new ArrayList<>();

        for (int i = 0; i < array.size(); i++)
            output.add(String.valueOf(array.get(i)));

        return output;
    }

    private ArrayList<Double> stringToData(ArrayList<String> array){
        ArrayList<Double> output = new ArrayList<>();

        for (int i = 0; i < array.size(); i++)
            output.add(Double.valueOf(array.get(i)));

        return output;
    }

    @Override
    public void onItemClick(int position) {
        openAddDialog(position);
    }

    @Override
    public void onItemLongClick(int position, View view) {
        final Animation bounceAnim = AnimationUtils.loadAnimation(this, R.anim.bounce_in);
        PopupMenu popup = new PopupMenu(MMC_MDC_Activity.this, view);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.edit_copy_delete_menu_algebra, popup.getMenu());

        popup.setOnMenuItemClickListener(item -> {
            ClipboardManager clipboardManager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);

            int itemId = item.getItemId();
            if (itemId == R.id.copy) {
                ClipData clipData = ClipData.newPlainText("text", String.valueOf(elements.get(position)));
                clipboardManager.setPrimaryClip(clipData);

                Toast.makeText(getApplicationContext(), R.string.copied, Toast.LENGTH_SHORT).show();
                return true;
            } else if (itemId == R.id.delete) {
                elements.remove(position);
                listAdapter.notifyItemRemoved(position);

                if (elements.isEmpty()) {
                    LottieAnimationView empty = findViewById(R.id.empty);
                    TextView textEmpty = findViewById(R.id.emptyText);
                    empty.setVisibility(View.VISIBLE);
                    empty.startAnimation(bounceAnim);
                    textEmpty.setVisibility(View.VISIBLE);
                    textEmpty.startAnimation(bounceAnim);
                }
                calculate();
                return true;
            } else if (itemId == R.id.edit) {
                openAddDialog(position);
            }
            return false;
        });
        popup.show();
    }
}