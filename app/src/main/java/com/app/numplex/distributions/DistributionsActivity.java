package com.app.numplex.distributions;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import com.app.numplex.R;
import com.app.numplex.application.AppRater;
import com.app.numplex.application.AppStatus;
import com.app.numplex.utils.Animations;
import com.app.numplex.utils.DuoDrawerSetter;
import com.app.numplex.utils.Functions;

import java.util.Arrays;
import java.util.Random;

import nl.psdcompany.duonavigationdrawer.views.DuoDrawerLayout;

public class DistributionsActivity extends AppCompatActivity {
    private DuoDrawerLayout drawerLayout;
    private static final String STATE_EDIT_TEXT = "state_edit_text";

    private static int distribution = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /*----------------------------------------------------------------------------------------*/
        // Loading configs:
        Functions.updateConfigs(this, this);
        /*----------------------------------------------------------------------------------------*/
        setContentView(R.layout.activity_distribution);

        AppStatus.checkAccess(this);

        //Navigation drawer
        setDuoNavigationDrawer();

        //Toolbar
        HorizontalScrollView container = findViewById(R.id.resultContainer);
        final Animation bounceAnim = AnimationUtils.loadAnimation(this, R.anim.bounce);
        Functions.setToolbar(this, getResources().getString(R.string.distribution), drawerLayout, () -> Functions.showSpotlightMessage(DistributionsActivity.this,
                container,
                getString(R.string.distribution),
                getString(R.string.help_distribution),
                120f, null));
        /*----------------------------------------------------------------------------------------*/
        //Generate
        Button generate = findViewById(R.id.generate);
        generate.setOnClickListener(view -> {
            generate.startAnimation(bounceAnim);
            generate();
        });
        /*----------------------------------------------------------------------------------------*/
        TextView result = findViewById(R.id.result);
        result.setOnClickListener(view -> {
            container.startAnimation(bounceAnim);

            if (!result.getText().toString().equals("")) {
                ClipboardManager clipboardManager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                ClipData clipData = ClipData.newPlainText("text", result.getText().toString());
                clipboardManager.setPrimaryClip(clipData);

                Toast.makeText(getApplicationContext(), R.string.copied, Toast.LENGTH_SHORT).show();
            }
        });

        /*----------------------------------------------------------------------------------------*/
        Spinner distributionSpinner = findViewById(R.id.distributionSpinner);
        ArrayAdapter<CharSequence> dataAdapter = ArrayAdapter.createFromResource(this,
                R.array.distribution, R.layout.item_spinner_text_network);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        LinearLayout separator = findViewById(R.id.separatorMaster);
        LinearLayout scale = findViewById(R.id.scaleMaster);
        LinearLayout average = findViewById(R.id.avgMaster);
        LinearLayout deviation = findViewById(R.id.deviationMaster);
        LinearLayout from = findViewById(R.id.linearLayout4);
        LinearLayout to = findViewById(R.id.linearLayout5);
        LinearLayout unique = findViewById(R.id.linearLayout7);

        distributionSpinner.setAdapter(dataAdapter);
        distributionSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                distribution = position;
                switch (distribution) {
                    case 0:
                        // Uniform
                        Animations.bounceInAnim(separator, DistributionsActivity.this);
                        Animations.bounceInAnim(from, DistributionsActivity.this);
                        Animations.bounceInAnim(to, DistributionsActivity.this);
                        Animations.bounceInAnim(unique, DistributionsActivity.this);

                        Animations.bounceOutAnim(average, DistributionsActivity.this);
                        Animations.bounceOutAnim(deviation, DistributionsActivity.this);
                        Animations.bounceOutAnim(scale, DistributionsActivity.this);

                        break;
                    case 1:
                        Animations.bounceOutAnim(separator, DistributionsActivity.this);
                        Animations.bounceOutAnim(scale, DistributionsActivity.this);
                        Animations.bounceOutAnim(from, DistributionsActivity.this);
                        Animations.bounceOutAnim(to, DistributionsActivity.this);
                        Animations.bounceOutAnim(unique, DistributionsActivity.this);

                        Animations.bounceInAnim(average, DistributionsActivity.this);
                        Animations.bounceInAnim(deviation, DistributionsActivity.this);
                        break;
                    case 2:
                    case 3:
                        Animations.bounceOutAnim(from, DistributionsActivity.this);
                        Animations.bounceOutAnim(to, DistributionsActivity.this);
                        Animations.bounceOutAnim(separator, DistributionsActivity.this);
                        Animations.bounceOutAnim(unique, DistributionsActivity.this);
                        Animations.bounceOutAnim(average, DistributionsActivity.this);
                        Animations.bounceOutAnim(deviation, DistributionsActivity.this);

                        Animations.bounceInAnim(scale, DistributionsActivity.this);
                        break;
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //App rater:
        AppRater.app_launched(this, this);
    }

    /*--------------------------------------------------------------------------------------------*/
    private void generate() {
        final Animation shake = AnimationUtils.loadAnimation(DistributionsActivity.this, R.anim.shake);
        final Animation slideIn = AnimationUtils.loadAnimation(this, R.anim.slide_in_left);
        TextView result = findViewById(R.id.result);
        EditText from = findViewById(R.id.from);
        EditText to = findViewById(R.id.to);
        EditText count = findViewById(R.id.count);
        EditText separator = findViewById(R.id.separator);
        EditText average = findViewById(R.id.avg);
        EditText deviation = findViewById(R.id.deviation);
        EditText scale = findViewById(R.id.scale);
        SwitchCompat unique = findViewById(R.id.unique);

        String oldTextValue = result.getText().toString();
        try {
            StringBuilder output = new StringBuilder();
            // Number generator
            int countNumber = 1;
            int min = 0;
            int max = 100;
            double avgValue = 0.0;
            double deviationValue = 0.0;
            double scaleValue = 0.0;

            if (!count.getText().toString().equals(""))
                countNumber = Integer.parseInt(count.getText().toString());
            if (!from.getText().toString().equals(""))
                min = Integer.parseInt(from.getText().toString());
            if (!to.getText().toString().equals(""))
                max = Integer.parseInt(to.getText().toString());
            if (!average.getText().toString().equals(""))
                avgValue = Double.parseDouble(average.getText().toString());
            if (!deviation.getText().toString().equals(""))
                deviationValue = Double.parseDouble(deviation.getText().toString());
            if (!scale.getText().toString().equals(""))
                scaleValue = Double.parseDouble(scale.getText().toString());

            if (min >= max)
                throw new RuntimeException();
            if (countNumber > max - min + 1)
                throw new RuntimeException();

            boolean uniqueTrue = unique.isChecked();
            int[] numbers = new int[0];
            double[] numbersDouble = new double[0];

            switch (distribution){
                case 0:
                    if (uniqueTrue)
                        numbers = randomNumbersWithoutRepetition(min, max, countNumber);
                    else
                        numbers = randomNumbersWithRepetition(min, max, countNumber);
                    break;
                case 1:
                    numbersDouble = Distributions.gaussianDistribution(countNumber, avgValue, deviationValue);
                    break;
                case 2:
                    numbersDouble = Distributions.rayleighDistribution(countNumber, scaleValue);
                    break;
                case 3:
                    numbersDouble = Distributions.riceDistribution(countNumber, scaleValue);
                    break;
            }

            switch (distribution){
                case 0:
                    for (int i = 0; i < countNumber; i++) {
                        if (i + 1 != countNumber)
                            output.append(numbers[i]).append(separator.getText().toString());
                        else
                            output.append(numbers[i]);
                    }
                    break;
                case 1:
                case 2:
                case 3:
                    for (int i = 0; i < countNumber; i++) {
                        if (i + 1 != countNumber)
                            output.append(numbersDouble[i]).append(separator.getText().toString());
                        else
                            output.append(numbersDouble[i]);
                    }
                    break;
            }

            result.setText(output.toString());
            if (!result.getText().toString().equals(oldTextValue))
                result.startAnimation(slideIn);
        } catch (Exception e) {
            HorizontalScrollView container = findViewById(R.id.resultContainer);
            result.setText(R.string.error);
            container.startAnimation(shake);
        }
    }

    private int[] randomNumbersWithoutRepetition(int start, int end, int count) {
        if (count > end - start + 1) {
            throw new IllegalArgumentException("Cannot generate " + count + " unique random numbers in the range [" + start + ", " + end + "]");
        }

        int[] result = new int[end - start + 1];
        for (int i = start; i <= end; i++) {
            result[i - start] = i;
        }

        Random rng = new Random();
        for (int i = 0; i < count; i++) {
            int j = rng.nextInt(end - start + 1 - i) + i;
            int temp = result[i];
            result[i] = result[j];
            result[j] = temp;
        }

        return Arrays.copyOf(result, count);
    }

    private int[] randomNumbersWithRepetition(int start, int end, int count) {
        Random random = new Random();
        int[] result = new int[count];
        for (int i = 0; i < count; i++) {
            result[i] = random.nextInt(end - start + 1) + start;
        }
        return result;
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
