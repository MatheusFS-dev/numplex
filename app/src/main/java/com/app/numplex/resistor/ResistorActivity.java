package com.app.numplex.resistor;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.content.ContextCompat;

import com.app.numplex.R;
import com.app.numplex.application.AppRater;
import com.app.numplex.application.AppStatus;
import com.app.numplex.utils.DoneOnEditorActionListener;
import com.app.numplex.utils.DuoDrawerSetter;
import com.app.numplex.utils.Functions;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.List;
import java.util.Locale;
import java.util.Objects;

import nl.psdcompany.duonavigationdrawer.views.DuoDrawerLayout;

public class ResistorActivity extends AppCompatActivity implements CustomSpinner.OnSpinnerEventsListener {

    /*
     * Positions:
     * 0 -> 1st band
     * 1 -> 2nd band
     * 2 -> 3rd band
     * 3 -> Multiplier
     * 4 -> Tolerance
     * 5 -> Temperature coefficient
     * */

    private static final String[] colors = {"brown", "black", "black", "black", "brown", "black"};
    private static int bands = 4;
    private DuoDrawerLayout drawerLayout;
    private int multiplier = 0;
    private FrameLayout band1;
    private FrameLayout band2;
    private FrameLayout band3;
    private FrameLayout band4;
    private FrameLayout band6;
    private double oldValue = -1.0;
    private EditText currentEditText;
    private BottomSheetDialog smdDialog;
    private int smdMultiplier = 0;
    private int eia = 0;
    private double oldSMDValue = -1.0;

    /*------------------------------------------------------------------------*/
    //Converting to engineer format:
    private static String convert(double val) {
        int PREFIX_OFFSET = 5;
        String[] PREFIX_ARRAY = {"f", "p", "n", "µ", "m", "", "k", "M", "G", "T"};

        String result;
        // If the value is zero, then simply return 0 with the correct number of dp
        if (val == 0)
            return "0";

        // If the value is negative, make it positive so the log10 works
        double posVal = (val < 0) ? -val : val;
        double log10 = Math.log10(posVal);

        // Determine how many orders of 3 magnitudes the value is
        int count = (int) Math.floor(log10 / 3);

        // Calculate the index of the prefix symbol
        int index = count + PREFIX_OFFSET;

        // Scale the value into the range 1<=val<1000
        val /= Math.pow(10, count * 3);

        if (index >= 0 && index < PREFIX_ARRAY.length) {
            // If a prefix exists use it to create the correct string
            result = String.format(Locale.ENGLISH, "%." + 3 + "f%s", val, PREFIX_ARRAY[index]);
        } else {
            // If no prefix exists just make a string of the form 000e000
            result = String.format(Locale.ENGLISH, "%." + 3 + "fe%d", val, count * 3);
        }

        result = result.replace(".000", "");
        if (result.contains(".")) {
            if (index != 5) {
                result = result.replace("00" + PREFIX_ARRAY[index], PREFIX_ARRAY[index]);
                result = result.replace("0" + PREFIX_ARRAY[index], PREFIX_ARRAY[index]);
            } else
                result = String.valueOf(Double.parseDouble(result));
        }

        return result;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /*----------------------------------------------------------------------------------------*/
        // Loading configs:
        Functions.updateConfigs(this, this);
        /*----------------------------------------------------------------------------------------*/

        setContentView(R.layout.activity_resistor);

        /*----------------------------------------------------------------------------------------*/
        // Navigation menu
        setDuoNavigationDrawer();

        //Toolbar
        Functions.setToolbar(this, getResources().getString(R.string.home_resistor), drawerLayout, null);

        final Animation bounceAnim = AnimationUtils.loadAnimation(this, R.anim.bounce);
        AppCompatButton smdButton = findViewById(R.id.smdButton);
        ImageButton help = findViewById(R.id.help);

        smdButton.setOnClickListener(view -> {
            smdButton.startAnimation(bounceAnim);

            new Handler().postDelayed(this::openSMDDialog, 300);
        });

        help.setOnClickListener(view -> {
            help.startAnimation(bounceAnim);

            new Handler().postDelayed(() -> {
                EditText result = findViewById(R.id.result);
                Functions.showSpotlightMessage(this, result,
                        getString(R.string.home_resistor),
                        getString(R.string.resistor_help),
                        80f, null);
            }, 300);
        });


        /* -------------------- Just passing the string resources ------------------- */
        Data.setRes(getResources());
        Data_firstBand.setRes(getResources());
        Data_temperature.setRes(getResources());
        Data_Tolerance.setRes(getResources());
        Data_Multiplier.setRes(getResources());
        /* -------------------------------------------------------------------------- */

        // Bands
        band1 = findViewById(R.id.band1);
        band2 = findViewById(R.id.band2);
        band3 = findViewById(R.id.band3);
        band4 = findViewById(R.id.band4);

        // Buttons
        Button fourBands = findViewById(R.id.fourBands);
        Button fiveBands = findViewById(R.id.fiveBands);
        Button sixBands = findViewById(R.id.sixBands);

        /*----------------------------------------------------------------------------------------*/
        // Spinners:
        CustomSpinner spinner1 = findViewById(R.id.first_band);
        spinner1.setSpinnerEventsListener(this);
        ColorAdapter adapter1 = new ColorAdapter(ResistorActivity.this, Data_firstBand.getColorList());
        spinner1.setAdapter(adapter1);

        CustomSpinner spinner2 = findViewById(R.id.second_band);
        spinner2.setSpinnerEventsListener(this);
        ColorAdapter adapter2 = new ColorAdapter(ResistorActivity.this, Data.getColorList());
        spinner2.setAdapter(adapter2);

        CustomSpinner spinner3 = findViewById(R.id.third_band);
        spinner3.setSpinnerEventsListener(this);
        ColorAdapter adapter3 = new ColorAdapter(ResistorActivity.this, Data.getColorList());
        spinner3.setAdapter(adapter3);

        CustomSpinner spinner4 = findViewById(R.id.multiplier_band);
        spinner4.setSpinnerEventsListener(this);
        ColorAdapter adapter4 = new ColorAdapter(ResistorActivity.this, Data_Multiplier.getColorList());
        spinner4.setAdapter(adapter4);

        CustomSpinner spinner5 = findViewById(R.id.tolerance_band);
        spinner5.setSpinnerEventsListener(this);
        ColorAdapter adapter5 = new ColorAdapter(ResistorActivity.this, Data_Tolerance.getColorList());
        spinner5.setAdapter(adapter5);

        CustomSpinner spinner6 = findViewById(R.id.temp_band);
        spinner6.setSpinnerEventsListener(this);
        ColorAdapter adapter6 = new ColorAdapter(ResistorActivity.this, Data_temperature.getColorList());
        spinner6.setAdapter(adapter6);

        Spinner spinnerOhm = findViewById(R.id.ohms);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.ohm_array, R.layout.item_spinner_resistor);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerOhm.setAdapter(adapter);
        /*----------------------------------------------------------------------------------------*/
        // Spinners listeners:

        spinner1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                int color = Integer.parseInt(spinner1.getSelectedItem().toString());
                colors[0] = intToString(1, color);
                updateResistor();
                calculateFromColor();
            }

            public void onNothingSelected(AdapterView<?> adapterView) {
                // Do nothing
            }
        });

        spinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                int color = Integer.parseInt(spinner2.getSelectedItem().toString());
                colors[1] = intToString(2, color);
                updateResistor();
                calculateFromColor();
            }

            public void onNothingSelected(AdapterView<?> adapterView) {
                // Do nothing
            }
        });

        spinner3.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                int color = Integer.parseInt(spinner3.getSelectedItem().toString());
                colors[2] = intToString(2, color);
                updateResistor();
                calculateFromColor();
            }

            public void onNothingSelected(AdapterView<?> adapterView) {
                // Do nothing
            }
        });

        spinner4.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                int color = Integer.parseInt(spinner4.getSelectedItem().toString());
                colors[3] = intToString(3, color);
                updateResistor();
                calculateFromColor();
            }

            public void onNothingSelected(AdapterView<?> adapterView) {
                // Do nothing
            }
        });

        spinner5.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                int color = Integer.parseInt(spinner5.getSelectedItem().toString());
                colors[4] = intToString(4, color);
                updateResistor();
                calculateFromColor();
            }

            public void onNothingSelected(AdapterView<?> adapterView) {
                // Do nothing
            }
        });

        spinner6.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                int color = Integer.parseInt(spinner6.getSelectedItem().toString());
                colors[5] = intToString(5, color);
                updateResistor();
                calculateFromColor();
            }

            public void onNothingSelected(AdapterView<?> adapterView) {
                // Do nothing
            }
        });

        spinnerOhm.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        multiplier = 1;
                        break;
                    case 1:
                        multiplier = 1000;
                        break;
                    case 2:
                        multiplier = 1000000;
                        break;
                    case 3:
                        multiplier = 1000000000;
                        break;
                }
                calculateFromValue();
            }

            public void onNothingSelected(AdapterView<?> parent) {
                //Do nothing
            }
        });
        /*----------------------------------------------------------------------------------------*/
        // Buttons listeners:
        fourBands.setOnClickListener(v -> {
            if (bands != 4) {
                bands = 4;
                updateButtons();
            }
        });

        fiveBands.setOnClickListener(v -> {
            if (bands != 5) {
                bands = 5;
                updateButtons();
            }
        });

        sixBands.setOnClickListener(v -> {
            if (bands != 6) {
                bands = 6;
                updateButtons();
            }
        });


        /*----------------------------------------------------------------------------------------*/
        // EditText
        EditText result = findViewById(R.id.result);
        result.setOnFocusChangeListener((v, hasFocus) -> calculateFromValue());
        result.setOnEditorActionListener(new DoneOnEditorActionListener());
        /*----------------------------------------------------------------------------------------*/
        updateButtons();
        calculateFromColor();

        //App rater:
        AppRater.app_launched(this, this);
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public void onPopupWindowOpened(Spinner spinner) {
        spinner.setBackground(ContextCompat.getDrawable(this, R.drawable.spinner_icon_up));
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public void onPopupWindowClosed(Spinner spinner) {
        spinner.setBackground(ContextCompat.getDrawable(this, R.drawable.spinner_icon_down));
    }

    /*--------------------------------------------------------------------------------------------*/
    //Auxiliary methods
    @SuppressLint("SetTextI18n")
    private void calculateFromColor() {
        Spinner spinnerOhm = findViewById(R.id.ohms);
        EditText resValue = findViewById(R.id.result);
        resValue.clearFocus();
        TextView coefficients = findViewById(R.id.coefficients);
        Double[] result;
        switch (bands) {
            case 4:
                result = ResistorLogic.colorToValue(4, List.of(colors));
                break;
            case 5:
                result = ResistorLogic.colorToValue(5, List.of(colors));
                break;
            case 6:
                result = ResistorLogic.colorToValue(6, List.of(colors));
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + bands);
        }
        oldValue = result[0];

        final Animation slideIn = AnimationUtils.loadAnimation(this, R.anim.slide_in_left);
        final Animation slideIn2 = AnimationUtils.loadAnimation(this, R.anim.slide_in_right);

        String oldTextValue = resValue.getText().toString();
        String oldTextCoefficients = coefficients.getText().toString();
        String resString;
        if (result[0] >= 1)
            resString = convert(result[0]);
        else
            resString = String.valueOf(result[0]);
        String temp = "";

        if (resString.contains("k")) {
            spinnerOhm.setSelection(1);
            multiplier = 1000;
        } else if (resString.contains("M")) {
            spinnerOhm.setSelection(2);
            multiplier = 1000000;
        } else if (resString.contains("G")) {
            spinnerOhm.setSelection(3);
            multiplier = 1000000000;
        } else {
            spinnerOhm.setSelection(0);
            multiplier = 1;
        }

        String tolerance = String.valueOf(result[1]);
        if (bands == 6)
            temp = String.valueOf(result[2]);

        resString = resString.replace("k", "");
        resString = resString.replace("M", "");
        resString = resString.replace("G", "");
        if (resString.endsWith(".0"))
            resString = resString.replace(".0", "");
        if (tolerance.endsWith(".0"))
            tolerance = tolerance.replace(".0", "");
        if (temp.endsWith(".0"))
            temp = temp.replace(".0", "");


        resValue.setText(resString);
        if (!resValue.getText().toString().equals(oldTextValue))
            resValue.startAnimation(slideIn);

        if (bands == 6)
            coefficients.setText("±" + tolerance + "% @" + temp + "ppm/°C");
        else
            coefficients.setText("±" + tolerance + "%");
        if (!coefficients.getText().toString().equals(oldTextCoefficients))
            coefficients.startAnimation(slideIn2);
    }

    private void calculateFromValue() {
        TextView error = findViewById(R.id.error);
        EditText result = findViewById(R.id.result);
        double value;

        try {
            if (!result.getText().toString().equals("")) {
                value = Double.parseDouble(result.getText().toString()) * multiplier;
                if (value != oldValue) {
                    String[] aux = ResistorLogic.valueToColor(bands, value);
                    colors[0] = aux[0];
                    colors[1] = aux[1];
                    if (aux[2] != null)
                        colors[2] = aux[2];
                    colors[3] = aux[3];
                    updateResistor();

                    CustomSpinner spinner1 = findViewById(R.id.first_band);
                    CustomSpinner spinner2 = findViewById(R.id.second_band);
                    CustomSpinner spinner3 = findViewById(R.id.third_band);
                    CustomSpinner spinner4 = findViewById(R.id.multiplier_band);

                    spinner1.setSelection(stringToInt(1, colors[0]));
                    spinner2.setSelection(stringToInt(2, colors[1]));
                    spinner3.setSelection(stringToInt(2, colors[2]));
                    spinner4.setSelection(stringToInt(3, colors[3]));
                }
            }
            if (error.getVisibility() != View.GONE)
                bounceOutAnim(error);
        } catch (Exception e) {
            final Animation shake = AnimationUtils.loadAnimation(ResistorActivity.this, R.anim.shake);
            final Animation bounce = AnimationUtils.loadAnimation(this, R.anim.bounce_in);

            if (error.getVisibility() == View.GONE) {
                error.setVisibility(View.VISIBLE);
                error.startAnimation(bounce);
                new Handler().postDelayed(() -> error.startAnimation(shake), 250);
            } else
                error.startAnimation(shake);
        }

    }


    /*------------------------------------------------------------------------*/
    // SMD dialog:

    private String intToString(int type, int number) {
        /*
         * 1 -> 1st digit
         * 2 -> digits
         * 3 -> multiplier
         * 4 -> tolerance
         * 5 -> temperature coefficient
         */

        String color = "";

        switch (type) {
            case 1:
                switch (number) {
                    case 0:
                        color = "brown";
                        break;
                    case 1:
                        color = "red";
                        break;
                    case 2:
                        color = "orange";
                        break;
                    case 3:
                        color = "yellow";
                        break;
                    case 4:
                        color = "green";
                        break;
                    case 5:
                        color = "blue";
                        break;
                    case 6:
                        color = "violet";
                        break;
                    case 7:
                        color = "grey";
                        break;
                    case 8:
                        color = "white";
                        break;
                }
                break;
            case 2:
                switch (number) {
                    case 0:
                        color = "black";
                        break;
                    case 1:
                        color = "brown";
                        break;
                    case 2:
                        color = "red";
                        break;
                    case 3:
                        color = "orange";
                        break;
                    case 4:
                        color = "yellow";
                        break;
                    case 5:
                        color = "green";
                        break;
                    case 6:
                        color = "blue";
                        break;
                    case 7:
                        color = "violet";
                        break;
                    case 8:
                        color = "grey";
                        break;
                    case 9:
                        color = "white";
                        break;
                }
                break;
            case 3:
                switch (number) {
                    case 0:
                        color = "black";
                        break;
                    case 1:
                        color = "brown";
                        break;
                    case 2:
                        color = "red";
                        break;
                    case 3:
                        color = "orange";
                        break;
                    case 4:
                        color = "yellow";
                        break;
                    case 5:
                        color = "green";
                        break;
                    case 6:
                        color = "blue";
                        break;
                    case 7:
                        color = "violet";
                        break;
                    case 8:
                        color = "grey";
                        break;
                    case 9:
                        color = "white";
                        break;
                    case 10:
                        color = "gold";
                        break;
                    case 11:
                        color = "silver";
                        break;
                }
                break;
            case 4:
                switch (number) {
                    case 0:
                        color = "brown";
                        break;
                    case 1:
                        color = "red";
                        break;
                    case 2:
                        color = "orange";
                        break;
                    case 3:
                        color = "yellow";
                        break;
                    case 4:
                        color = "green";
                        break;
                    case 5:
                        color = "blue";
                        break;
                    case 6:
                        color = "violet";
                        break;
                    case 7:
                        color = "grey";
                        break;
                    case 8:
                        color = "gold";
                        break;
                    case 9:
                        color = "silver";
                        break;
                    case 10:
                        color = "none";
                        break;
                }
                break;
            case 5:
                switch (number) {
                    case 0:
                        color = "black";
                        break;
                    case 1:
                        color = "brown";
                        break;
                    case 2:
                        color = "red";
                        break;
                    case 3:
                        color = "orange";
                        break;
                    case 4:
                        color = "yellow";
                        break;
                    case 5:
                        color = "green";
                        break;
                    case 6:
                        color = "blue";
                        break;
                    case 7:
                        color = "violet";
                        break;
                    case 8:
                        color = "grey";
                        break;
                }
                break;
        }


        return color;
    }

    private int stringToInt(int type, String color) {
        /*
         * 1 -> 1st digit
         * 2 -> digits
         * 3 -> multiplier
         * 4 -> tolerance
         * 5 -> temperature coefficient
         */

        int number = 0;

        switch (type) {
            case 1:
                switch (color) {
                    case "brown":
                        break;
                    case "red":
                        number = 1;
                        break;
                    case "orange":
                        number = 2;
                        break;
                    case "yellow":
                        number = 3;
                        break;
                    case "green":
                        number = 4;
                        break;
                    case "blue":
                        number = 5;
                        break;
                    case "violet":
                        number = 6;
                        break;
                    case "grey":
                        number = 7;
                        break;
                    case "white":
                        number = 8;
                        break;
                }
                break;
            case 2:
                switch (color) {
                    case "black":
                        break;
                    case "brown":
                        number = 1;
                        break;
                    case "red":
                        number = 2;
                        break;
                    case "orange":
                        number = 3;
                        break;
                    case "yellow":
                        number = 4;
                        break;
                    case "green":
                        number = 5;
                        break;
                    case "blue":
                        number = 6;
                        break;
                    case "violet":
                        number = 7;
                        break;
                    case "grey":
                        number = 8;
                        break;
                    case "white":
                        number = 9;
                        break;
                }
                break;
            case 3:
                switch (color) {
                    case "black":
                        break;
                    case "brown":
                        number = 1;
                        break;
                    case "red":
                        number = 2;
                        break;
                    case "orange":
                        number = 3;
                        break;
                    case "yellow":
                        number = 4;
                        break;
                    case "green":
                        number = 5;
                        break;
                    case "blue":
                        number = 6;
                        break;
                    case "violet":
                        number = 7;
                        break;
                    case "grey":
                        number = 8;
                        break;
                    case "white":
                        number = 9;
                        break;
                    case "gold":
                        number = 10;
                        break;
                    case "silver":
                        number = 11;
                        break;
                }
                break;
            case 4:
                switch (color) {
                    case "brown":
                        break;
                    case "red":
                        number = 1;
                        break;
                    case "orange":
                        number = 2;
                        break;
                    case "yellow":
                        number = 3;
                        break;
                    case "green":
                        number = 4;
                        break;
                    case "blue":
                        number = 5;
                        break;
                    case "violet":
                        number = 6;
                        break;
                    case "grey":
                        number = 7;
                        break;
                    case "gold":
                        number = 8;
                        break;
                    case "silver":
                        number = 9;
                        break;
                    case "none":
                        number = 10;
                        break;
                }
                break;
            case 5:
                switch (color) {
                    case "black":
                        break;
                    case "brown":
                        number = 1;
                        break;
                    case "red":
                        number = 2;
                        break;
                    case "orange":
                        number = 3;
                        break;
                    case "yellow":
                        number = 4;
                        break;
                    case "green":
                        number = 5;
                        break;
                    case "blue":
                        number = 6;
                        break;
                    case "violet":
                        number = 7;
                        break;
                    case "grey":
                        number = 8;
                        break;
                }
                break;
        }


        return number;
    }

    private int stringToColor(String color) {
        int result = 0;
        switch (color) {
            case "black":
                result = ContextCompat.getColor(this, R.color.black);
                break;
            case "brown":
                result = ContextCompat.getColor(this, R.color.brown);
                break;
            case "red":
                result = ContextCompat.getColor(this, R.color.red);
                break;
            case "orange":
                result = ContextCompat.getColor(this, R.color.orange);
                break;
            case "yellow":
                result = ContextCompat.getColor(this, R.color.yellow);
                break;
            case "green":
                result = ContextCompat.getColor(this, R.color.green);
                break;
            case "blue":
                result = ContextCompat.getColor(this, R.color.blue);
                break;
            case "violet":
                result = ContextCompat.getColor(this, R.color.violet);
                break;
            case "grey":
                result = ContextCompat.getColor(this, R.color.grey);
                break;
            case "white":
                result = ContextCompat.getColor(this, R.color.white);
                break;
            case "gold":
                result = ContextCompat.getColor(this, R.color.gold);
                break;
            case "silver":
                result = ContextCompat.getColor(this, R.color.silver);
                break;
            case "none":
                result = Color.parseColor("#d9bc7a");
                break;
        }
        return result;
    }

    private void updateResistor() {
        band1 = findViewById(R.id.band1);
        band2 = findViewById(R.id.band2);
        band3 = findViewById(R.id.band3);
        band4 = findViewById(R.id.band4); // Multiplier
        FrameLayout band5 = findViewById(R.id.band5); // Tolerance
        band6 = findViewById(R.id.band6); // Temperature coefficient

        switch (bands) {
            case 4:
                //Case 4 Bands:
                band1.setBackgroundColor(stringToColor(colors[0]));
                band2.setBackgroundColor(stringToColor(colors[1]));
                band4.setBackgroundColor(stringToColor(colors[3]));
                band5.setBackgroundColor(stringToColor(colors[4]));
                break;
            case 5:
                //Case 5 bands:
                band1.setBackgroundColor(stringToColor(colors[0]));
                band2.setBackgroundColor(stringToColor(colors[1]));
                band3.setBackgroundColor(stringToColor(colors[2]));
                band4.setBackgroundColor(stringToColor(colors[3]));
                band5.setBackgroundColor(stringToColor(colors[4]));
                break;
            case 6:
                //Case 6 bands
                band1.setBackgroundColor(stringToColor(colors[0]));
                band2.setBackgroundColor(stringToColor(colors[1]));
                band3.setBackgroundColor(stringToColor(colors[2]));
                band4.setBackgroundColor(stringToColor(colors[3]));
                band5.setBackgroundColor(stringToColor(colors[4]));
                band6.setBackgroundColor(stringToColor(colors[5]));
                break;
        }

        TextView error = findViewById(R.id.error);
        if (error.getVisibility() != View.GONE)
            bounceOutAnim(error);
    }

    private void updateBands() {
        LinearLayout thirdBand = findViewById(R.id.third);
        LinearLayout tempCoef = findViewById(R.id.temperature);
        FrameLayout parentBand3 = findViewById(R.id.parentBand3);
        FrameLayout parentBand6 = findViewById(R.id.parentBand6);
        band3 = findViewById(R.id.band3);
        band6 = findViewById(R.id.band6);

        // loading Animations
        final Animation bounce = AnimationUtils.loadAnimation(this, R.anim.bounce_in);

        switch (bands) {
            case 4:
                //Case 4 bands:
                bounceOutAnim(parentBand3);
                bounceOutAnim(band3);
                bounceOutAnim(thirdBand);

                if (parentBand6.getVisibility() != View.GONE) {
                    bounceOutAnim(parentBand6);
                    bounceOutAnim(band6);
                }
                if (tempCoef.getVisibility() != View.GONE)
                    bounceOutAnim(tempCoef);
                break;
            case 5:
                //Case 5 bands:
                parentBand3.setVisibility(View.VISIBLE);
                parentBand3.startAnimation(bounce);
                band3.setVisibility(View.VISIBLE);
                band3.startAnimation(bounce);

                if (parentBand6.getVisibility() != View.GONE) {
                    bounceOutAnim(parentBand6);
                    bounceOutAnim(band6);
                }
                if (thirdBand.getVisibility() != View.VISIBLE) {
                    thirdBand.setVisibility(View.VISIBLE);
                    thirdBand.startAnimation(bounce);
                }
                if (tempCoef.getVisibility() != View.GONE)
                    bounceOutAnim(tempCoef);
                break;
            case 6:
                //Case 6 bands
                parentBand3.setVisibility(View.VISIBLE);
                parentBand3.startAnimation(bounce);
                band3.setVisibility(View.VISIBLE);
                band3.startAnimation(bounce);

                parentBand6.setVisibility(View.VISIBLE);
                parentBand6.startAnimation(bounce);
                band6.setVisibility(View.VISIBLE);
                band6.startAnimation(bounce);

                if (thirdBand.getVisibility() != View.VISIBLE) {
                    thirdBand.setVisibility(View.VISIBLE);
                    thirdBand.startAnimation(bounce);
                }
                tempCoef.setVisibility(View.VISIBLE);
                tempCoef.startAnimation(bounce);

                break;
        }
        updateResistor();
    }

    private void updateButtons() {
        // Buttons
        Button fourBands = findViewById(R.id.fourBands);
        Button fiveBands = findViewById(R.id.fiveBands);
        Button sixBands = findViewById(R.id.sixBands);

        EditText result = findViewById(R.id.result);
        final Animation bounceAnim = AnimationUtils.loadAnimation(this, R.anim.bounce);

        switch (bands) {
            case 4:
                fourBands.startAnimation(bounceAnim);
                fourBands.setBackground(ContextCompat.getDrawable(this,
                        R.drawable.ripple_effect_blue_buttons_res));
                fiveBands.setBackground(ContextCompat.getDrawable(this,
                        R.drawable.ripple_effect_main_background));
                sixBands.setBackground(ContextCompat.getDrawable(this,
                        R.drawable.ripple_effect_main_background));

                updateBands();
                if (!result.hasFocus())
                    calculateFromColor();
                break;
            case 5:
                fiveBands.startAnimation(bounceAnim);
                fiveBands.setBackground(ContextCompat.getDrawable(this,
                        R.drawable.ripple_effect_blue_buttons_res));
                fourBands.setBackground(ContextCompat.getDrawable(this,
                        R.drawable.ripple_effect_main_background));
                sixBands.setBackground(ContextCompat.getDrawable(this,
                        R.drawable.ripple_effect_main_background));

                bands = 5;
                updateBands();
                if (!result.hasFocus())
                    calculateFromColor();
                break;
            case 6:
                sixBands.startAnimation(bounceAnim);
                sixBands.setBackground(ContextCompat.getDrawable(this,
                        R.drawable.ripple_effect_blue_buttons_res));
                fourBands.setBackground(ContextCompat.getDrawable(this,
                        R.drawable.ripple_effect_main_background));
                fiveBands.setBackground(ContextCompat.getDrawable(this,
                        R.drawable.ripple_effect_main_background));

                bands = 6;
                updateBands();
                if (!result.hasFocus())
                    calculateFromColor();
                break;
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private void openSMDDialog() {
        AppStatus.checkFeatureAccess(this, () -> {
            smdDialog = new BottomSheetDialog(this);
            smdDialog.setContentView(R.layout.bottom_dialog_smd);

            EditText code = smdDialog.findViewById(R.id.code);
            EditText result = smdDialog.findViewById(R.id.result);
            currentEditText = code;

            /*----------------------------------------------------------------------------------------*/
            //Spinner
            Spinner smdSpinnerOhm = smdDialog.findViewById(R.id.ohms);
            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                    R.array.ohm_array, R.layout.item_spinner_resistor);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            Objects.requireNonNull(smdSpinnerOhm).setAdapter(adapter);

            smdSpinnerOhm.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    switch (position) {
                        case 0:
                            smdMultiplier = 1;
                            break;
                        case 1:
                            smdMultiplier = 1000;
                            break;
                        case 2:
                            smdMultiplier = 1000000;
                            break;
                        case 3:
                            smdMultiplier = 1000000000;
                            break;
                    }
                    calculateFromValueSMD(Objects.requireNonNull(result).getText().toString());
                }

                public void onNothingSelected(AdapterView<?> parent) {
                    //Do nothing
                }
            });

            Spinner smdEIA = smdDialog.findViewById(R.id.EIA);
            ArrayAdapter<CharSequence> adapterEIA = ArrayAdapter.createFromResource(this,
                    R.array.eia_array, R.layout.item_spinner_resistor);
            adapterEIA.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            Objects.requireNonNull(smdEIA).setAdapter(adapterEIA);

            smdEIA.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    eia = position;
                    if (Objects.requireNonNull(code).isFocused())
                        calculateFromCodeSMD(Objects.requireNonNull(code).getText().toString());
                    else
                        calculateFromValueSMD(Objects.requireNonNull(result).getText().toString());
                }

                public void onNothingSelected(AdapterView<?> parent) {
                    //Do nothing
                }
            });
            /*----------------------------------------------------------------------------------------*/
            Objects.requireNonNull(code).addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void afterTextChanged(Editable editable) {
                    if (currentEditText == code)
                        calculateFromCodeSMD(Objects.requireNonNull(code).getText().toString().toUpperCase());
                }
            });
            Objects.requireNonNull(result).addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void afterTextChanged(Editable editable) {
                    if (currentEditText == result)
                        calculateFromValueSMD(Objects.requireNonNull(result).getText().toString());
                }
            });

            code.setOnTouchListener((view, motionEvent) -> {
                currentEditText = code;
                return false;
            });

            result.setOnTouchListener((view, motionEvent) -> {
                currentEditText = result;
                return false;
            });

            smdDialog.show();
        });
    }

    private void calculateFromCodeSMD(String code) {
        final Animation slideIn = AnimationUtils.loadAnimation(this, R.anim.slide_in_left);
        TextView error = smdDialog.findViewById(R.id.smdError);
        EditText result = smdDialog.findViewById(R.id.result);
        Spinner smdSpinnerOhm = smdDialog.findViewById(R.id.ohms);
        double resultValue;

        String oldTextValue = Objects.requireNonNull(result).getText().toString();
        String resString;

        try {
            switch (eia) {
                case 0:
                    // EIA 3
                    resultValue = SMDLogic.getOhmValue3Digits(code);
                    break;
                case 1:
                    // EIA 4
                    resultValue = Double.parseDouble(SMDLogic.getOhmValue4Digits(code));
                    break;
                default:
                    // EIA 96
                    resultValue = SMDLogic.getOhmValueEIA90(code);
                    break;
            }
            oldSMDValue = resultValue;

            if (resultValue >= 1)
                resString = convert(resultValue);
            else
                resString = String.valueOf(resultValue);

            if (resString.contains("k")) {
                Objects.requireNonNull(smdSpinnerOhm).setSelection(1);
                multiplier = 1000;
            } else if (resString.contains("M")) {
                Objects.requireNonNull(smdSpinnerOhm).setSelection(2);
                multiplier = 1000000;
            } else if (resString.contains("G")) {
                Objects.requireNonNull(smdSpinnerOhm).setSelection(3);
                multiplier = 1000000000;
            } else {
                Objects.requireNonNull(smdSpinnerOhm).setSelection(0);
                multiplier = 1;
            }

            resString = resString.replace("k", "");
            resString = resString.replace("M", "");
            resString = resString.replace("G", "");
            if (resString.endsWith(".0"))
                resString = resString.replace(".0", "");

            result.setText(resString);
            if (!result.getText().toString().equals(oldTextValue))
                result.startAnimation(slideIn);
            if (Objects.requireNonNull(error).getVisibility() != View.GONE)
                bounceOutAnim(error);
        } catch (Exception e) {
            final Animation shake = AnimationUtils.loadAnimation(ResistorActivity.this, R.anim.shake);
            final Animation bounce = AnimationUtils.loadAnimation(this, R.anim.bounce_in);

            if (code.length() >= 3) {
                Objects.requireNonNull(error).setText(R.string.invalid_code);
                if (Objects.requireNonNull(error).getVisibility() == View.GONE) {
                    error.setVisibility(View.VISIBLE);
                    error.startAnimation(bounce);
                    new Handler().postDelayed(() -> error.startAnimation(shake), 250);
                } else
                    error.startAnimation(shake);
            }
        }
    }

    /*------------------------------------------------------------------------*/

    private void calculateFromValueSMD(String result) {
        final Animation slideIn = AnimationUtils.loadAnimation(this, R.anim.slide_in_left);
        TextView error = smdDialog.findViewById(R.id.smdError);
        EditText code = smdDialog.findViewById(R.id.code);
        double value;

        String resString;
        String oldTextValue = Objects.requireNonNull(code).getText().toString();

        try {
            if (!result.equals("")) {
                value = Double.parseDouble(result) * smdMultiplier;
                if (value != oldSMDValue) {
                    switch (eia) {
                        case 0:
                            // EIA 3
                            resString = SMDLogic.getCodeEIA3(String.valueOf(value));
                            break;
                        case 1:
                            // EIA 4
                            resString = SMDLogic.getCodeEIA4(String.valueOf(value));
                            break;
                        default:
                            // EIA 96
                            resString = SMDLogic.getCodeEIA90(String.valueOf(value));
                            break;
                    }
                    Objects.requireNonNull(code).setText(resString);
                    if (!code.getText().toString().equals(oldTextValue))
                        code.startAnimation(slideIn);

                    if (Objects.requireNonNull(error).getVisibility() != View.GONE)
                        bounceOutAnim(error);
                }
            }
        } catch (Exception e) {
            final Animation shake = AnimationUtils.loadAnimation(ResistorActivity.this, R.anim.shake);
            final Animation bounce = AnimationUtils.loadAnimation(this, R.anim.bounce_in);

            Objects.requireNonNull(error).setText(R.string.resistor_not_found);
            if (Objects.requireNonNull(error).getVisibility() == View.GONE) {
                error.setVisibility(View.VISIBLE);
                error.startAnimation(bounce);
                new Handler().postDelayed(() -> error.startAnimation(shake), 250);
            } else
                error.startAnimation(shake);
            code.setText("");
        }
    }

    /*--------------------------------------------------------------------------------------------*/
    // Some other methods:
    private void bounceOutAnim(View view) {
        final Animation bounceOut = AnimationUtils.loadAnimation(this, R.anim.bounce_out);
        view.startAnimation(bounceOut);
        new Handler().postDelayed(() -> view.setVisibility(View.GONE), 200);
    }

    /*------------------------------------------------------------------------*/
    //Navigation menu:
    private void setDuoNavigationDrawer() {
        LinearLayout toolbar = findViewById(R.id.toolbar);
        ImageButton toolbarButton = findViewById(R.id.toolbarButton);
        drawerLayout = findViewById(R.id.drawer);
        ScrollView content = findViewById(R.id.content);

        DuoDrawerSetter drawerSetter = new DuoDrawerSetter(drawerLayout, this);
        drawerSetter.setContentNotRounded(true);
        drawerSetter.setDuoNavigationDrawer(this, toolbar, toolbarButton, content);
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen())
            drawerLayout.closeDrawer();
        else
            super.onBackPressed();
    }
}