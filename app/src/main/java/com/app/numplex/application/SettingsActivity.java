package com.app.numplex.application;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;
import androidx.preference.PreferenceManager;

import com.airbnb.lottie.LottieAnimationView;
import com.app.numplex.R;
import com.app.numplex.utils.Animations;
import com.mahfa.dnswitch.DayNightSwitch;
import com.mahfa.dnswitch.DayNightSwitchAnimListener;

import java.util.Locale;

public class SettingsActivity extends AppCompatActivity {
    private boolean isNight = false;
    private int changedLanguage = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        LinearLayout credits = findViewById(R.id.credits);
        DayNightSwitch darkSwitch = findViewById(R.id.darkModeSwitch);
        TextView textDark = findViewById(R.id.textDark);

        //Toolbar
        ImageButton toolbarButton = findViewById(R.id.toolbarButton);
        TextView toolbarTitle = findViewById(R.id.toolbarTitle);
        toolbarTitle.setText(R.string.settings);
        toolbarButton.setImageDrawable(ContextCompat.getDrawable(
                this, R.drawable.ic_baseline_arrow_back_24));
        toolbarButton.setOnClickListener(view -> onBackPressed());

        //Switch
        darkSwitch.setDuration(450);

        final Animation bounce = AnimationUtils.loadAnimation(this, R.anim.bounce);
        //Credits listener:
        credits.setOnClickListener(v -> {
            credits.startAnimation(bounce);

            new Handler().postDelayed(() -> {
                Intent intent = new Intent(this, AboutActivity.class);
                startActivity(intent);

                /*------------------------------------------------------------------------*/
                // Night or light mode:
                SharedPreferences sharedPreferences = getSharedPreferences("night", 0);
                boolean booleanValue = sharedPreferences.getBoolean("night_mode", false);
                if (booleanValue)
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                else
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                /*------------------------------------------------------------------------*/
            }, 200);
        });

        LottieAnimationView lottieAnimationView =  findViewById(R.id.dayNight); // 330 frames
        isNight = AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES;
        if(isNight) {
            textDark.setText(getResources().getString(R.string.dark_mode));
            lottieAnimationView.setMinAndMaxFrame(165,330); //to play the second half
        }
        else {
            textDark.setText(getResources().getString(R.string.light_mode));
            lottieAnimationView.setMinAndMaxFrame(0,165); //to play the first half
        }
        darkSwitch.setIsNight(isNight, true);

        // Language
        Spinner language = findViewById(R.id.language);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.language, R.layout.item_spinner_grey);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        language.setAdapter(adapter);
        language.setSelection(loadSpinner(this, this));

        SharedPreferences sharedPreferences = getSharedPreferences("night", 0);
        language.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedLanguage = parent.getItemAtPosition(position).toString();
                if (position == 0) {
                    setAppLocale(SettingsActivity.this, "");
                } else {
                    // Set language to selected option
                    String languageCode = selectedLanguage.equals("English") ? "en" : "pt";
                    setAppLocale(SettingsActivity.this, languageCode);
                }
                updateText(sharedPreferences.getBoolean("night_mode", false));
                changedLanguage++;
            }

            public void onNothingSelected(AdapterView<?> parent) {
                //Do nothing
            }
        });

        // Dark mode:
        darkSwitch.setListener(is_night -> isNight = is_night);
        darkSwitch.setAnimListener(new DayNightSwitchAnimListener() {
            @Override
            public void onAnimStart() {
            }

            @Override
            public void onAnimEnd() {
                SharedPreferences sharedPreferences = getSharedPreferences("night", 0);
                SharedPreferences.Editor editor = sharedPreferences.edit();

                if (isNight) {
                    editor.putBoolean("night_mode", true);
                    editor.apply();

                    updateDarkLightMode(true, "#FFFFFF", "#242328",
                            ColorStateList.valueOf(Color.parseColor("#242328")),
                            ContextCompat.getColorStateList(SettingsActivity.this, R.color.white));
                }else{
                    editor.putBoolean("night_mode", false);
                    editor.apply();

                    updateDarkLightMode(false, "#000000", "#FFFFFF",
                            ContextCompat.getColorStateList(SettingsActivity.this, R.color.white),
                            ContextCompat.getColorStateList(SettingsActivity.this, R.color.black));
                }
            }

            @Override
            public void onAnimValueChanged(float value) {
            }
        });

        updateText(sharedPreferences.getBoolean("night_mode", false));
    }

    @Override
    protected void onPause() {
        /*------------------------------------------------------------------------*/
        // Night or light mode:
        SharedPreferences sharedPreferences = getSharedPreferences("night", 0);
        boolean booleanValue = sharedPreferences.getBoolean("night_mode", false);
        if (booleanValue)
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        else
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        /*------------------------------------------------------------------------*/

        super.onPause();
    }

    @Override
    public void onBackPressed() {
        /*------------------------------------------------------------------------*/
        // Night or light mode:
        SharedPreferences sharedPreferences = getSharedPreferences("night", 0);
        boolean booleanValue = sharedPreferences.getBoolean("night_mode", false);
        if (booleanValue)
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        else
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        /*------------------------------------------------------------------------*/

        if(changedLanguage > 1){
            Intent intent = getBaseContext().getPackageManager().getLaunchIntentForPackage(getBaseContext().getPackageName());
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }
        else
            super.onBackPressed();
    }

    // Method to load the saved language preference from SharedPreferences
    public static int loadSpinner(Context context, Activity activity) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String language = prefs.getString("language", "");

        setAppLocale(activity, language);

        if (language.isEmpty()) {
            // If the saved language preference is an empty string, it means the language is set to the device default
            return 0;
        }
        return language.equals("en") ? 1 : 2;
    }

    // Method to set the app's locale to the specified language
    public static void setAppLocale(Activity activity, String languageCode) {
        // Load the current language preference from SharedPreferences
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(activity);

        // Save the selected language preference to SharedPreferences
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("language", languageCode);
        editor.apply();

        if(languageCode.equals(""))
            languageCode = Locale.getDefault().getLanguage();

        // Set the app's locale to the selected language
        Resources res = activity.getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration config = res.getConfiguration();
        config.setLocale(new Locale(languageCode));
        res.updateConfiguration(config, dm);
    }

    private void updateText(boolean dark){
        TextView credits1 = findViewById(R.id.credits1);
        TextView credits2 = findViewById(R.id.credits2);
        TextView textDark = findViewById(R.id.textDark);
        TextView textLanguage = findViewById(R.id.textLanguage);
        TextView toolbarTitle = findViewById(R.id.toolbarTitle);

        if(!dark)
            textDark.setText(getResources().getString(R.string.light_mode));
        else
            textDark.setText(getResources().getString(R.string.dark_mode));

        credits1.setText(R.string.credits);
        credits2.setText(R.string.no_one_clicks_me);
        textLanguage.setText(R.string.language);
        toolbarTitle.setText(R.string.settings);
    }

    private void updateDarkLightMode(boolean dark, String color1, String color2, ColorStateList color3, ColorStateList color4){
        ImageView darkModeImage = findViewById(R.id.darkModeImage);
        ImageView languageImage = findViewById(R.id.languageImage);
        LinearLayout linearLayout1 = findViewById(R.id.linear1);
        LinearLayout linearLayout2 = findViewById(R.id.linear2);
        LinearLayout spinnerContainer = findViewById(R.id.spinnerContainer);
        LinearLayout credits = findViewById(R.id.credits);
        TextView credits1 = findViewById(R.id.credits1);
        TextView textDark = findViewById(R.id.textDark);
        TextView textLanguage = findViewById(R.id.textLanguage);
        LinearLayout toolbar = findViewById(R.id.toolbar);
        ImageButton toolbarButton = findViewById(R.id.toolbarButton);
        TextView toolbarTitle = findViewById(R.id.toolbarTitle);
        LottieAnimationView lottieAnimationView =  findViewById(R.id.dayNight); // 330 frames

        credits.setBackgroundTintList(color3);
        darkModeImage.setImageTintList(color4);
        languageImage.setImageTintList(color4);
        linearLayout1.setBackgroundTintList(color3);
        linearLayout2.setBackgroundTintList(color3);
        spinnerContainer.setBackgroundTintList(color3);
        textDark.setTextColor(Color.parseColor(color1));
        textLanguage.setTextColor(Color.parseColor(color1));
        if(!dark)
            textDark.setText(getResources().getString(R.string.light_mode));
        else
            textDark.setText(getResources().getString(R.string.dark_mode));
        credits1.setTextColor(Color.parseColor(color1));
        toolbar.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor(color2)));
        toolbarTitle.setTextColor(Color.parseColor(color1));
        if(!dark)
            toolbarButton.setImageDrawable(ContextCompat.getDrawable(
                    SettingsActivity.this, R.drawable.ic_baseline_arrow_back_24_black));
        else
            toolbarButton.setImageDrawable(ContextCompat.getDrawable(
                    SettingsActivity.this, R.drawable.ic_baseline_arrow_back_24_white));
        toolbarButton.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor(color2)));
        Animations.enterReveal(toolbar);
        Animations.enterReveal(linearLayout1);
        Animations.enterReveal(linearLayout2);
        Animations.enterReveal(credits);
        if(!dark)
            lottieAnimationView.setMinAndMaxFrame(165,330); //to play the second half
        else
            lottieAnimationView.setMinAndMaxFrame(0,165); //to play the first half
        lottieAnimationView.playAnimation();
    }
}