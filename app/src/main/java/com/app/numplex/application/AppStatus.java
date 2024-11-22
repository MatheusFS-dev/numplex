package com.app.numplex.application;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.app.numplex.R;

public class AppStatus {
    private static final boolean isUserPaid = true;

    public static void checkAccess(final Activity activity) {
        if (!isUserPaid) {
            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            builder.setTitle(activity.getString(R.string.access_denied))
                    .setMessage(activity.getString(R.string.paid_users_only))
                    .setPositiveButton(activity.getString(R.string.upgrade), (dialog, id) -> {
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse("https://play.google.com/store/apps/details?id=com.app.numplexpro"));
                        activity.startActivity(intent);

                        // Simply finish the current activity, returning to the previous one
                        activity.finish();
                    })
                    .setNegativeButton(activity.getString(R.string.cancel), (dialog, id) -> {
                        activity.finish(); // Close the activity if user hasn't paid
                    })
                    .setCancelable(false); // Make the dialog non-cancellable
            builder.create().show();
        }
    }


    public static void checkFeatureAccess(final Context context, Runnable onAccessGranted) {

        if (!isUserPaid) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle(context.getString(R.string.access_denied))
                    .setMessage(context.getString(R.string.paid_users_only))
                    .setPositiveButton(context.getString(R.string.upgrade), (dialog, id) -> {
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse("https://play.google.com/store/apps/details?id=com.app.numplexpro"));
                        context.startActivity(intent);
                    })
                    .setNegativeButton(context.getString(R.string.cancel), null)
                    .setCancelable(false);
            builder.create().show();
        } else {
            onAccessGranted.run(); // Execute the feature if access is granted
        }
    }

}
