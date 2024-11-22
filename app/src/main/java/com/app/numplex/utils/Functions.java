package com.app.numplex.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;
import androidx.preference.PreferenceManager;

import com.app.numplex.R;
import com.app.numplex.application.SettingsActivity;
import com.takusemba.spotlight.OnTargetStateChangedListener;
import com.takusemba.spotlight.SimpleTarget;
import com.takusemba.spotlight.Spotlight;

import java.util.List;

import nl.psdcompany.duonavigationdrawer.views.DuoDrawerLayout;

public class Functions {

    public static void updateConfigs(Activity activity, Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

        updateNightMode(activity);
        SettingsActivity.setAppLocale(activity, prefs.getString("language", ""));
    }

    public static void updateNightMode(Activity activity) {
        SharedPreferences sharedPreferences = activity.getSharedPreferences("night", 0);
        boolean isNightMode = sharedPreferences.getBoolean("night_mode", false);
        if (isNightMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
    }

    public static void setToolbar(Activity activity, String title, DuoDrawerLayout drawerLayout, ButtonClickListener helpListener) {
        //Call setDuoNavigationDrawer() before setting the toolbar
        final Animation bounceAnim = AnimationUtils.loadAnimation(activity, R.anim.bounce);
        ImageButton toolbarButton = activity.findViewById(R.id.toolbarButton);
        ImageButton help = activity.findViewById(R.id.help);
        TextView toolbarTitle = activity.findViewById(R.id.toolbarTitle);
        toolbarTitle.setText(title);

        toolbarButton.setOnClickListener(v -> {
            if (drawerLayout.isDrawerOpen())
                drawerLayout.closeDrawer();
            else
                drawerLayout.openDrawer();
        });

        help.setOnClickListener(view -> {
            help.startAnimation(bounceAnim);

            new Handler().postDelayed(() -> {
                if (helpListener != null) {
                    helpListener.ButtonClicked();
                }
            }, 300);
        });
    }

    public static void isCursorBetweenSpecificWords(EditText editText, List<String> words) {
        String text = editText.getText().toString();
        int cursor = editText.getSelectionEnd();
        int startIndex = -1;
        int endIndex = -1;
        for (String word : words) {
            int index = text.lastIndexOf(word, cursor - 1);
            if (index > startIndex) {
                startIndex = index;
                endIndex = startIndex + word.length();
                break; // exit loop on first match
            }
        }
        if (startIndex != -1 && endIndex >= cursor) {
            editText.setSelection(endIndex);
        }
    }

    public static void deleteSpecificWordBeforeCursor(EditText editText, List<String> words) {
        int cursor = editText.getSelectionEnd();
        String text = editText.getText().toString();
        int startIndex = -1;
        int endIndex = -1;
        for (String word : words) {
            int index = text.lastIndexOf(word, cursor - 1);
            if (index > startIndex) {
                startIndex = index;
                endIndex = startIndex + word.length();
                break;
            }
        }
        if (startIndex != -1 && endIndex == cursor) {
            editText.getText().delete(startIndex, cursor);
        }
    }

    public static void deleteSpecificWordAfterCursor(EditText editText, List<String> words) {
        int cursor = editText.getSelectionEnd();
        String text = editText.getText().toString();
        int startIndex = -1;
        int endIndex = -1;
        for (String word : words) {
            int index = text.indexOf(word, cursor);
            if (index != -1 && (startIndex == -1 || index < startIndex)) {
                startIndex = index;
                endIndex = startIndex + word.length();
            }
        }
        if (startIndex != -1 && endIndex > cursor) {
            editText.getText().delete(startIndex, endIndex);
        }
    }

    public static void deleteWholeWordIfCursorAtSpecificPosition(EditText editText, String word, int position) {
        String text = editText.getText().toString();
        int cursor = editText.getSelectionEnd();
        int startIndex = text.lastIndexOf(word, cursor - 1);
        if (startIndex != -1 && cursor - 1 == startIndex + position) {
            int endIndex = startIndex + word.length();
            editText.getText().delete(startIndex, endIndex);
            editText.setSelection(startIndex);
        }
    }

    public static boolean skipSpecificWordBeforeCursor(EditText editText, List<String> words) {
        int cursor = editText.getSelectionEnd();
        String text = editText.getText().toString();
        int startIndex = -1;
        int i = 0;
        for (; i < words.size(); i++) {
            String word = words.get(i);
            int index = text.lastIndexOf(word, cursor - 1);
            if (index > startIndex) {
                startIndex = index;
                break;
            }
        }
        if (startIndex != -1 && startIndex + words.get(i).length() == cursor) {
            editText.setSelection(startIndex);
            return true;
        }else
            return false;
    }

    public static boolean skipSpecificWordAfterCursor(EditText editText, List<String> words) {
        int cursor = editText.getSelectionEnd();
        String text = editText.getText().toString();
        int startIndex = -1;
        int endIndex = -1;
        for (String word : words) {
            int index = text.indexOf(word, cursor);
            if (index != -1 && (startIndex == -1 || index < startIndex)) {
                startIndex = index;
                endIndex = startIndex + word.length();
            }
        }
        if (startIndex != -1 && endIndex > cursor) {
            editText.setSelection(endIndex);
            return true;
        }else
            return false;
    }

    public static void showSpotlightMessage(Activity activity, View targetView, String title, String message, Float radius, ButtonClickListener buttonClickListener) {
        SimpleTarget simpleTarget = new SimpleTarget.Builder(activity)
                .setPoint(targetView) // position of the Target. setPoint(Point point), setPoint(View view) will work too.
                .setRadius(radius) // radius of the Target
                .setTitle(title) // title
                .setDescription(message) // description
                .setOnSpotlightStartedListener(new OnTargetStateChangedListener<>() {
                    @Override
                    public void onStarted(SimpleTarget target) {
                        // do something
                    }

                    @Override
                    public void onEnded(SimpleTarget target) {
                        // do something
                    }
                })
                .build();

        // callback when Spotlight starts
        // callback when Spotlight ends
        Spotlight.with(activity)
                .setOverlayColor(ContextCompat.getColor(activity, R.color.spotlightColor)) // background overlay color
                .setDuration(200L) // duration of Spotlight emerging and disappearing in ms
                .setAnimation(new DecelerateInterpolator(1.5f)) // animation of Spotlight
                .setTargets(simpleTarget) // set targets. see below for more info
                .setClosedOnTouchedOutside(true) // set if target is closed when touched outside
                .setOnSpotlightStartedListener(() -> {})
                .setOnSpotlightEndedListener(() -> {
                    if (buttonClickListener != null) {
                        buttonClickListener.ButtonClicked();
                    }
                })
                .start(); // start Spotlight

    }

}
