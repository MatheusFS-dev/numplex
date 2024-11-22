package com.app.numplex.karnaugh;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import com.app.numplex.R;
import com.app.numplex.application.AppRater;
import com.app.numplex.utils.Animations;
import com.app.numplex.utils.DuoDrawerSetter;
import com.app.numplex.utils.Functions;

import java.util.ArrayList;
import java.util.List;

import nl.psdcompany.duonavigationdrawer.views.DuoDrawerLayout;

public class KarnaughActivity extends AppCompatActivity {

    private static int vars = 3;

    private DuoDrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /*----------------------------------------------------------------------------------------*/
        // Loading configs:
        Functions.updateConfigs(this, this);
        /*----------------------------------------------------------------------------------------*/

        setContentView(R.layout.activity_karnaugh);

        /*----------------------------------------------------------------------------------------*/
        // Navigation menu
        setDuoNavigationDrawer();

        //Toolbar
        Functions.setToolbar(this, getResources().getString(R.string.home_karnaugh), drawerLayout, null);

        final Animation bounceAnim = AnimationUtils.loadAnimation(this, R.anim.bounce);
        ImageButton help = findViewById(R.id.help);

        help.setOnClickListener(view -> {
            help.startAnimation(bounceAnim);

            new Handler().postDelayed(() -> {
                TextView result = findViewById(R.id.result);
                Functions.showSpotlightMessage(this, result,
                        getString(R.string.home_karnaugh),
                        getString(R.string.karnaugh_help),
                        200f, null);
            }, 300);
        });

        /*----------------------------------------------------------------------------------------*/
        updateButtons();
        setConstraintsListeners();

        //Buttons
        Button fill0 = findViewById(R.id.fill0);
        Button fill1 = findViewById(R.id.fill1);
        fill0.setOnClickListener(v -> {
            fill0.startAnimation(bounceAnim);
            fillWith(0);
        });

        fill1.setOnClickListener(v -> {
            fill1.startAnimation(bounceAnim);
            fillWith(1);
        });

        Button five = findViewById(R.id.five);
        Button four = findViewById(R.id.four);
        Button three = findViewById(R.id.three);
        Button two = findViewById(R.id.two);

        five.setOnClickListener(v -> {
            if (vars != 5) {
                vars = 5;
                updateButtons();
            }
        });

        four.setOnClickListener(v -> {
            if (vars != 4) {
                vars = 4;
                updateButtons();
            }
        });

        three.setOnClickListener(v -> {
            if (vars != 3) {
                vars = 3;
                updateButtons();
            }
        });

        two.setOnClickListener(v -> {
            if (vars != 2) {
                vars = 2;
                updateButtons();
            }
        });
        /*----------------------------------------------------------------------------------------*/


        //App rater:
        AppRater.app_launched(this, this);
    }

    private void calculate() {
        TextView result = findViewById(R.id.result);

        if(!areAllPositionsOne()) {
            result.setText(Quine_Mccluskey.calculate(getOnePositions(), vars));
        }
        else
            result.setText("1");
    }

    private void fillWith(int x) {
        if (x == 0)
            for (ConstraintLayout constraintLayout : getListConstraints())
                updateTextView(constraintLayout, "");
        else
            for (ConstraintLayout constraintLayout : getListConstraints())
                updateTextView(constraintLayout, "1");
    }

    private void setConstraintsListeners() {
        View.OnClickListener clickListener = view -> {
            final Animation bounceAnim = AnimationUtils.loadAnimation(this, R.anim.bounce);
            view.startAnimation(bounceAnim);
            updateTextView((ConstraintLayout) view, null);
        };

        for (ConstraintLayout constraintLayout : getListConstraints()) {
            constraintLayout.setOnClickListener(clickListener);
        }
    }

    private void updateTextView(ConstraintLayout constraintLayout, String text) {
        TextView textView = null;

        for (int i = 0; i < constraintLayout.getChildCount(); i++) {
            View view = constraintLayout.getChildAt(i);
            if (view instanceof TextView)
                textView = (TextView) view;
        }

        if (textView != null) {
            // TextView found, perform actions here
            if (text != null)
                textView.setText(text);
            else {
//                if (textView.getText().toString().equals("1"))
//                    textView.setText("X");
//                else if (textView.getText().toString().equals("X"))
//                    textView.setText("");
//                else
//                    textView.setText("1");

                if (textView.getText().toString().equals("1"))
                    textView.setText("");
                else
                    textView.setText("1");
            }
        }

        calculate();
    }

    private int[] getValues() {
        List<String> values = new ArrayList<>();

        for (ConstraintLayout constraintLayout : getListConstraints()) {
            TextView textView = null;

            for (int i = 0; i < constraintLayout.getChildCount(); i++) {
                View view = constraintLayout.getChildAt(i);
                if (view instanceof TextView)
                    textView = (TextView) view;
            }

            if (textView != null) {
                if (textView.getText().toString().equals(""))
                    values.add("0");
                else
                    values.add(textView.getText().toString());
            }
        }

        int[] intArray = new int[values.size()];
        for (int i = 0; i < values.size(); i++) {
            String value = values.get(i);
            int intValue = 0; // Default value if the string is empty or cannot be parsed

            try {
                intValue = Integer.parseInt(value);
            } catch (NumberFormatException e) {
                e.printStackTrace(); // Handle parsing errors if necessary
            }

            intArray[i] = intValue;
        }

        return intArray;
    }

    private int[] getOnePositions() {
        int[] values = getValues();
        List<Integer> onePositions = new ArrayList<>();

        for (int i = 0; i < values.length; i++) {
            if (values[i] == 1)
                onePositions.add(i);
        }

        int[] positionsArray = new int[onePositions.size()];
        for (int i = 0; i < onePositions.size(); i++) {
            positionsArray[i] = onePositions.get(i);
        }

        return positionsArray;
    }

    private boolean areAllPositionsOne() {
        int[] values = getValues();

        for (int value : values)
            if (value != 1)
                return false;

        return true;
    }

    private List<ConstraintLayout> getListConstraints() {
        ConstraintLayout k30 = findViewById(R.id.k30);
        ConstraintLayout k31 = findViewById(R.id.k31);
        ConstraintLayout k32 = findViewById(R.id.k32);
        ConstraintLayout k33 = findViewById(R.id.k33);
        ConstraintLayout k34 = findViewById(R.id.k34);
        ConstraintLayout k35 = findViewById(R.id.k35);
        ConstraintLayout k36 = findViewById(R.id.k36);
        ConstraintLayout k37 = findViewById(R.id.k37);

        ConstraintLayout k20 = findViewById(R.id.k20);
        ConstraintLayout k21 = findViewById(R.id.k21);
        ConstraintLayout k22 = findViewById(R.id.k22);
        ConstraintLayout k23 = findViewById(R.id.k23);

        ConstraintLayout k40 = findViewById(R.id.k40);
        ConstraintLayout k41 = findViewById(R.id.k41);
        ConstraintLayout k42 = findViewById(R.id.k42);
        ConstraintLayout k43 = findViewById(R.id.k43);
        ConstraintLayout k44 = findViewById(R.id.k44);
        ConstraintLayout k45 = findViewById(R.id.k45);
        ConstraintLayout k46 = findViewById(R.id.k46);
        ConstraintLayout k47 = findViewById(R.id.k47);
        ConstraintLayout k412 = findViewById(R.id.k412);
        ConstraintLayout k413 = findViewById(R.id.k413);
        ConstraintLayout k415 = findViewById(R.id.k415);
        ConstraintLayout k414 = findViewById(R.id.k414);
        ConstraintLayout k48 = findViewById(R.id.k48);
        ConstraintLayout k49 = findViewById(R.id.k49);
        ConstraintLayout k411 = findViewById(R.id.k411);
        ConstraintLayout k410 = findViewById(R.id.k410);

        ConstraintLayout k5NE0 = findViewById(R.id.k5NE0);
        ConstraintLayout k5NE1 = findViewById(R.id.k5NE1);
        ConstraintLayout k5NE2 = findViewById(R.id.k5NE2);
        ConstraintLayout k5NE3 = findViewById(R.id.k5NE3);
        ConstraintLayout k5NE4 = findViewById(R.id.k5NE4);
        ConstraintLayout k5NE5 = findViewById(R.id.k5NE5);
        ConstraintLayout k5NE6 = findViewById(R.id.k5NE6);
        ConstraintLayout k5NE7 = findViewById(R.id.k5NE7);
        ConstraintLayout k5NE12 = findViewById(R.id.k5NE12);
        ConstraintLayout k5NE13 = findViewById(R.id.k5NE13);
        ConstraintLayout k5NE15 = findViewById(R.id.k5NE15);
        ConstraintLayout k5NE14 = findViewById(R.id.k5NE14);
        ConstraintLayout k5NE8 = findViewById(R.id.k5NE8);
        ConstraintLayout k5NE9 = findViewById(R.id.k5NE9);
        ConstraintLayout k5NE11 = findViewById(R.id.k5NE11);
        ConstraintLayout k5NE10 = findViewById(R.id.k5NE10);

        ConstraintLayout k5E0 = findViewById(R.id.k5E0);
        ConstraintLayout k5E1 = findViewById(R.id.k5E1);
        ConstraintLayout k5E2 = findViewById(R.id.k5E2);
        ConstraintLayout k5E3 = findViewById(R.id.k5E3);
        ConstraintLayout k5E4 = findViewById(R.id.k5E4);
        ConstraintLayout k5E5 = findViewById(R.id.k5E5);
        ConstraintLayout k5E6 = findViewById(R.id.k5E6);
        ConstraintLayout k5E7 = findViewById(R.id.k5E7);
        ConstraintLayout k5E12 = findViewById(R.id.k5E12);
        ConstraintLayout k5E13 = findViewById(R.id.k5E13);
        ConstraintLayout k5E15 = findViewById(R.id.k5E15);
        ConstraintLayout k5E14 = findViewById(R.id.k5E14);
        ConstraintLayout k5E8 = findViewById(R.id.k5E8);
        ConstraintLayout k5E9 = findViewById(R.id.k5E9);
        ConstraintLayout k5E11 = findViewById(R.id.k5E11);
        ConstraintLayout k5E10 = findViewById(R.id.k5E10);

        List<ConstraintLayout> constraintLayouts = new ArrayList<>();

        switch (vars) {
            case 5 -> {
                constraintLayouts.add(k5E0);
                constraintLayouts.add(k5E1);
                constraintLayouts.add(k5E2);
                constraintLayouts.add(k5E3);
                constraintLayouts.add(k5E4);
                constraintLayouts.add(k5E5);
                constraintLayouts.add(k5E6);
                constraintLayouts.add(k5E7);
                constraintLayouts.add(k5E8);
                constraintLayouts.add(k5E9);
                constraintLayouts.add(k5E10);
                constraintLayouts.add(k5E11);
                constraintLayouts.add(k5E12);
                constraintLayouts.add(k5E13);
                constraintLayouts.add(k5E14);
                constraintLayouts.add(k5E15);
                constraintLayouts.add(k5NE0);
                constraintLayouts.add(k5NE1);
                constraintLayouts.add(k5NE2);
                constraintLayouts.add(k5NE3);
                constraintLayouts.add(k5NE4);
                constraintLayouts.add(k5NE5);
                constraintLayouts.add(k5NE6);
                constraintLayouts.add(k5NE7);
                constraintLayouts.add(k5NE8);
                constraintLayouts.add(k5NE9);
                constraintLayouts.add(k5NE10);
                constraintLayouts.add(k5NE11);
                constraintLayouts.add(k5NE12);
                constraintLayouts.add(k5NE13);
                constraintLayouts.add(k5NE14);
                constraintLayouts.add(k5NE15);
            }
            case 4 -> {
                constraintLayouts.add(k40);
                constraintLayouts.add(k41);
                constraintLayouts.add(k42);
                constraintLayouts.add(k43);
                constraintLayouts.add(k44);
                constraintLayouts.add(k45);
                constraintLayouts.add(k46);
                constraintLayouts.add(k47);
                constraintLayouts.add(k48);
                constraintLayouts.add(k49);
                constraintLayouts.add(k410);
                constraintLayouts.add(k411);
                constraintLayouts.add(k412);
                constraintLayouts.add(k413);
                constraintLayouts.add(k414);
                constraintLayouts.add(k415);
            }
            case 3 -> {
                constraintLayouts.add(k30);
                constraintLayouts.add(k31);
                constraintLayouts.add(k32);
                constraintLayouts.add(k33);
                constraintLayouts.add(k34);
                constraintLayouts.add(k35);
                constraintLayouts.add(k36);
                constraintLayouts.add(k37);
            }
            case 2 -> {
                constraintLayouts.add(k20);
                constraintLayouts.add(k21);
                constraintLayouts.add(k22);
                constraintLayouts.add(k23);
            }
        }

        return constraintLayouts;
    }

    private void updateButtons() {
        // Buttons
        Button four = findViewById(R.id.four);
        Button five = findViewById(R.id.five);
        Button three = findViewById(R.id.three);
        Button two = findViewById(R.id.two);

        View k2 = findViewById(R.id.k2);
        View k3 = findViewById(R.id.k3);
        View k4 = findViewById(R.id.k4);
        View k5 = findViewById(R.id.k5);

        TextView result = findViewById(R.id.result);
        final Animation bounceAnim = AnimationUtils.loadAnimation(this, R.anim.bounce);

        result.setText(null);

        switch (vars) {
            case 4 -> {
                four.startAnimation(bounceAnim);
                four.setBackground(ContextCompat.getDrawable(this,
                        R.drawable.ripple_effect_blue_buttons_res));
                five.setBackground(ContextCompat.getDrawable(this,
                        R.drawable.ripple_effect_main_background));
                three.setBackground(ContextCompat.getDrawable(this,
                        R.drawable.ripple_effect_main_background));
                two.setBackground(ContextCompat.getDrawable(this,
                        R.drawable.ripple_effect_main_background));
                Animations.bounceOutAnim(k2, this);
                Animations.bounceOutAnim(k3, this);
                Animations.bounceOutAnim(k5, this);
                Animations.bounceInAnim(k4, this);
                vars = 4;
            }
            case 5 -> {
                five.startAnimation(bounceAnim);
                five.setBackground(ContextCompat.getDrawable(this,
                        R.drawable.ripple_effect_blue_buttons_res));
                four.setBackground(ContextCompat.getDrawable(this,
                        R.drawable.ripple_effect_main_background));
                three.setBackground(ContextCompat.getDrawable(this,
                        R.drawable.ripple_effect_main_background));
                two.setBackground(ContextCompat.getDrawable(this,
                        R.drawable.ripple_effect_main_background));
                Animations.bounceOutAnim(k2, this);
                Animations.bounceOutAnim(k3, this);
                Animations.bounceOutAnim(k4, this);
                Animations.bounceInAnim(k5, this);
                vars = 5;
            }
            case 3 -> {
                three.startAnimation(bounceAnim);
                three.setBackground(ContextCompat.getDrawable(this,
                        R.drawable.ripple_effect_blue_buttons_res));
                four.setBackground(ContextCompat.getDrawable(this,
                        R.drawable.ripple_effect_main_background));
                five.setBackground(ContextCompat.getDrawable(this,
                        R.drawable.ripple_effect_main_background));
                two.setBackground(ContextCompat.getDrawable(this,
                        R.drawable.ripple_effect_main_background));
                Animations.bounceOutAnim(k2, this);
                Animations.bounceOutAnim(k4, this);
                Animations.bounceOutAnim(k5, this);
                Animations.bounceInAnim(k3, this);
                vars = 3;
            }
            case 2 -> {
                two.startAnimation(bounceAnim);
                two.setBackground(ContextCompat.getDrawable(this,
                        R.drawable.ripple_effect_blue_buttons_res));
                four.setBackground(ContextCompat.getDrawable(this,
                        R.drawable.ripple_effect_main_background));
                five.setBackground(ContextCompat.getDrawable(this,
                        R.drawable.ripple_effect_main_background));
                three.setBackground(ContextCompat.getDrawable(this,
                        R.drawable.ripple_effect_main_background));
                Animations.bounceOutAnim(k4, this);
                Animations.bounceOutAnim(k3, this);
                Animations.bounceOutAnim(k5, this);
                Animations.bounceInAnim(k2, this);
                vars = 2;
            }
        }
        setConstraintsListeners();
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