package com.app.numplex.application;

import android.app.Activity;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Handler;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.app.numplex.R;
import com.app.numplex.utils.LottieDialog;

public class AppRater {
    private final static int LAUNCHES_UNTIL_PROMPT = 10;

    private static SharedPreferences prefs;
    private static SharedPreferences.Editor editor;

    public static void app_launched(Context mContext, Activity activity) {
        prefs = mContext.getSharedPreferences("apprater", Context.MODE_PRIVATE);
        if (prefs.getBoolean("dontshowagain", false))
            return;

        editor = prefs.edit();

        long EXTRA_LAUNCHES = prefs.getLong("extra_launches", 0);

        // Increment launch counter
        long launch_count = prefs.getLong("launch_count", 0) + 1;
        //Toast.makeText(mContext, String.valueOf(launch_count), Toast.LENGTH_SHORT).show();
        editor.putLong("launch_count", launch_count);

        if (launch_count >= (LAUNCHES_UNTIL_PROMPT + EXTRA_LAUNCHES))
                showRateDialog(mContext, activity);

        editor.apply();
    }

    public static void showRateDialog(Context context, Activity activity) {
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.dialog_rate_app);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        LottieDialog customDialog = new LottieDialog(activity);

        TextView never = dialog.findViewById(R.id.never);
        never.setOnClickListener(v -> {
            customDialog.startDialog(R.layout.lottie_never, 2000);
            dialog.cancel();

            if (editor != null) {
                editor.putBoolean("dontshowagain", true);
                editor.apply();
            }
        });

        TextView later = dialog.findViewById(R.id.later);
        later.setOnClickListener(v -> {
            customDialog.startDialog(R.layout.lottie_later, 2000);
            dialog.cancel();
            delayLaunches();
        });

        Button rateNow = dialog.findViewById(R.id.rateNow);
        rateNow.setOnClickListener(v -> {
            customDialog.startDialog(R.layout.lottie_rate_now, 2000);
            dialog.cancel();

            new Handler().postDelayed(() -> initializePlayStore(context), 2200);

            if (editor != null) {
                editor.putBoolean("dontshowagain", true);
                editor.apply();
            }
        });

        dialog.show();
    }


    public static void initializePlayStore(Context context){
        try {
            context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + context.getPackageName())));
        } catch (ActivityNotFoundException e) {
            context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + context.getPackageName())));
        }
    }

    private static void delayLaunches() {
        long extra_launches = prefs.getLong("extra_launches", 0) + AppRater.LAUNCHES_UNTIL_PROMPT;
        editor.putLong("extra_launches", extra_launches);
        editor.commit();
    }
}