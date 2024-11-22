package com.app.numplex.series;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.app.numplex.R;
import com.app.numplex.application.AppRater;
import com.app.numplex.application.AppStatus;
import com.app.numplex.utils.DuoDrawerSetter;
import com.app.numplex.utils.Functions;
import com.app.numplex.utils.RepeatListener;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import nl.psdcompany.duonavigationdrawer.views.DuoDrawerLayout;

public class SeriesActivity extends AppCompatActivity{
    private static int textsize = 24; //Text size
    private static String savedExpression = ""; //Auxiliary variable
    private DuoDrawerLayout drawerLayout;

    public static SharedPreferences sharedPreferences = null;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /*----------------------------------------------------------------------------------------*/
        // Loading configs:
        Functions.updateConfigs(this, this);
        /*----------------------------------------------------------------------------------------*/

        setContentView(R.layout.activity_series);

        AppStatus.checkAccess(this);

        /*----------------------------------------------------------------------------------------*/
        //Navigation drawer
        setDuoNavigationDrawer();
        /*------------------------------------------------------------------------*/
        EditText expression = findViewById(R.id.expression);
        loadData();

        expression.setText(savedExpression);
        expression.setSelection(expression.length());
        expression.setTextSize(textsize);

        calculate();
        /*------------------------------------------------------------------------*/
        //Button delete:
        final Animation bounceAnim = AnimationUtils.loadAnimation(this, R.anim.bounce);
        RepeatListener.setBounceAnim(bounceAnim);
        Button del = findViewById(R.id.del);
        del.setOnTouchListener(new RepeatListener(400, 150, view -> del()));

        //Calling navigation window:
        ImageButton navigationButton = findViewById(R.id.nav);
        navigationButton.setOnClickListener(v -> {
            if (drawerLayout.isDrawerOpen())
                drawerLayout.closeDrawer();
            else
                drawerLayout.openDrawer();
        });

        //Button move left
        Button moveleft = findViewById(R.id.left);
        moveleft.setOnTouchListener(new RepeatListener(400, 150, view -> {
            int cursorPos = expression.getSelectionStart();
            try {
                if (!expression.getText().toString().equals(getResources().getString(R.string.Syntax_Error)))
                    expression.setSelection(cursorPos - 1);
            } catch (Exception ignored) {
            }
        }));

        //Button move right
        Button moveright = findViewById(R.id.right);
        moveright.setOnTouchListener(new RepeatListener(400, 150, view -> {
            int cursorPos = expression.getSelectionStart();
            try {
                if (!expression.getText().toString().equals(getResources().getString(R.string.Syntax_Error)))
                    expression.setSelection(cursorPos + 1);
            } catch (Exception ignored) {
            }
        }));

        //App rater:
        AppRater.app_launched(this, this);
    }

    /*------------------------------------------------------------------------*/
    private void calculate(){
        EditText expression = findViewById(R.id.expression);
        TextView result = findViewById(R.id.type);
        TextView expressionResult = findViewById(R.id.expressionResult);
        String output;

        SequencesSeriesLogic sequencesSeriesLogic =
                new SequencesSeriesLogic(
                        getResources().getString(R.string.arithmetic_sequence),
                        getResources().getString(R.string.geometric_sequence),
                        getResources().getString(R.string.constant_sequence),
                        getResources().getString(R.string.ascending_sequence),
                        getResources().getString(R.string.descending_sequence),
                        getResources().getString(R.string.oscillating_sequence)
                );
        try{
            if(!expression.getText().toString().equals("")) {
                String oldTextValue = result.getText().toString();
                String oldTextValue2 = expressionResult.getText().toString();
                output = sequencesSeriesLogic.identifySequence(stringToDoubleArray(expression.getText().toString()));

                if (output == null)
                    throw new RuntimeException();
                else {
                    if (output.contains("\n")) {
                        String[] parts = output.split("\n");
                        result.setText(parts[0]);
                        expressionResult.setText(parts[1]);
                    } else {
                        result.setText(output);
                        expressionResult.setText(null);
                    }

                    if(!result.getText().toString().equals(oldTextValue)
                            && !expressionResult.getText().toString().equals(oldTextValue2)){
                        final Animation slideIn = AnimationUtils.loadAnimation(this, R.anim.slide_in_left);
                        expressionResult.startAnimation(slideIn);
                        result.startAnimation(slideIn);
                    }
                }
                SeriesActivity.setSavedExpression(expression.getText().toString());
            }
        }catch(Exception e){
            Animation shake = AnimationUtils.loadAnimation(SeriesActivity.this, R.anim.shake);
            result.setText(R.string.error);
            expressionResult.setText("");
            result.startAnimation(shake);
        }
    }

    public static double[] stringToDoubleArray(String numbers) {
        String[] tokens = numbers.split(",");
        double[] result = new double[tokens.length];
        for (int i = 0; i < tokens.length; i++) {
            String token = tokens[i];
            if (token.contains("/")) {
                String[] parts = token.split("/");
                double numerator = Double.parseDouble(parts[0]);
                double denominator = Double.parseDouble(parts[1]);
                result[i] = numerator / denominator;
            } else {
                result[i] = Double.parseDouble(token);
            }
        }
        return result;
    }

    /*------------------------------------------------------------------------*/
    //Method to insert letter on edit texts:
    private void writeText(String string) {
        EditText text = findViewById(R.id.expression);
        if (text.getText().toString().equals(getResources().getString(R.string.Syntax_Error)))
            text.setText("");

        int cursorPos = text.getSelectionStart();

        text.getText().insert(cursorPos, string);
    }

    //Button delete:
    private void del() {
        EditText text = findViewById(R.id.expression);
        if (text.getText().toString().equals(getResources().getString(R.string.Syntax_Error)))
            text.setText("");
        int cursorPos = text.getSelectionStart();
        int textLen = text.length();
        SpannableStringBuilder selection = (SpannableStringBuilder) text.getText();

        try {
            /*------------------------------------------------------------------------*/
            //Deleting cases:
            if (cursorPos == 0 && textLen != 0) {
                selection.replace(0, 1, "");
                text.setText(selection);
                text.setSelection(0);
            } else if (textLen != 0) {
                selection.replace(cursorPos - 1, cursorPos, "");
                text.setText(selection);
                text.setSelection(cursorPos - 1);
            }
            /*------------------------------------------------------------------------*/
        } catch (Exception ignored) {
        }
    }

    //Buttons:
    @SuppressLint("SetTextI18n")
    public void buttonPressed(View view) {
        final Animation bounceAnim = AnimationUtils.loadAnimation(this, R.anim.bounce);
        EditText expression = findViewById(R.id.expression);
        TextView result = findViewById(R.id.type);
        TextView expressionResult = findViewById(R.id.expressionResult);
        int id = view.getId();

        view.startAnimation(bounceAnim);
        if (id == R.id.AC) {
            expression.setText(null);
            result.setText(null);
            expressionResult.setText(null);
            expression.setSelection(expression.getText().length());
        }
        else if (id == R.id.del)
            del();
        else if (id == R.id.seven)
            writeText("7");
        else if (id == R.id.eight)
            writeText("8");
        else if (id == R.id.nine)
            writeText("9");
        else if (id == R.id.four)
            writeText("4");
        else if (id == R.id.five)
            writeText("5");
        else if (id == R.id.six)
            writeText("6");
        else if (id == R.id.one)
            writeText("1");
        else if (id == R.id.two)
            writeText("2");
        else if (id == R.id.three)
            writeText("3");
        else if (id == R.id.zero)
            writeText("0");
        else if (id == R.id.point)
            writeText(".");
        else if (id == R.id.negative)
            writeText("-");
        else if (id == R.id.division)
            writeText("/");
        else if (id == R.id.comma)
            writeText(",");
        else if (id == R.id.calculate)
            calculate();
        else if (id == R.id.zoomin) {
            if (textsize != 48) {
                textsize += 4;
                expression.setTextSize(textsize);
                saveData();
            }
        }
        else if (id == R.id.zoomout) {
            if (textsize != 8) {
                textsize -= 4;
                expression.setTextSize(textsize);
                saveData();
            }
        }
        else if (id == R.id.help)
            openHelp();
    }

    private void openHelp() {
        BottomSheetDialog bottomDialog = new BottomSheetDialog(this);
        bottomDialog.setContentView(R.layout.bottom_dialog_help_series);

        bottomDialog.show();
    }

    /*--------------------------------------------------------------------------------------------*/
    //Navigation menu:
    private void setDuoNavigationDrawer() {
        drawerLayout = findViewById(R.id.drawer);

        ConstraintLayout content = findViewById(R.id.content);
        ImageButton navigationButton = findViewById(R.id.nav);

        DuoDrawerSetter drawerSetter = new DuoDrawerSetter(drawerLayout, this);
        drawerSetter.setDuoNavigationDrawer(this, navigationButton, content);
    }

    //Others
    public static void setSavedExpression(String savedExpression) {
        SeriesActivity.savedExpression = savedExpression;
    }

    private void loadData() {
        try {
            SharedPreferences sharedPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE);

            if(!sharedPreferences.getString("textsize_series", null).equals(""))
                textsize = Integer.parseInt(sharedPreferences.getString("textsize_series", null));

            //Toast.makeText(this, "Load.", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            //Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void saveData() {
        try {
            SharedPreferences sharedPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();

            editor.putString("textsize_series", String.valueOf(textsize));
            editor.apply();

            //Toast.makeText(this, "Saved", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            //Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}
