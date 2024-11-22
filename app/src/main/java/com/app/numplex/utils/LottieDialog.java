package com.app.numplex.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.view.LayoutInflater;

import com.app.numplex.R;

public class LottieDialog {

    private final Activity activity;
    private AlertDialog dialog;

    public LottieDialog(Activity activity){
        this.activity = activity;
    }

    @SuppressLint("InflateParams")
    public void startDeleteDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        LayoutInflater inflater = activity.getLayoutInflater();
        builder.setView(inflater.inflate(R.layout.lottie_delete_files, null)); // Select layout from lottie
        builder.setCancelable(false);

        dialog = builder.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        dialog.show();

        new Handler().postDelayed(this::dismissDialog, 3000);
    }

    public void startDialog(int resources, int delay){
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        LayoutInflater inflater = activity.getLayoutInflater();
        builder.setView(inflater.inflate(resources, null)); // Select layout from lottie
        builder.setCancelable(false);

        dialog = builder.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        dialog.show();

        new Handler().postDelayed(this::dismissDialog, delay);
    }

    public void dismissDialog(){
        dialog.dismiss();
    }
}