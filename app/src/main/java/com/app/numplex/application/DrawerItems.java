package com.app.numplex.application;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.os.ConfigurationCompat;

import com.app.numplex.R;
import com.app.numplex.algebra.AlgebraActivity;
import com.app.numplex.booleanSimplifier.BooleanActivity;
import com.app.numplex.calculator.CalculatorActivity;
import com.app.numplex.complex_calculator.ComplexCalculatorActivity;
import com.app.numplex.digital.DigitalActivity;
import com.app.numplex.digital_communication.DigitalCommunicationActivity;
import com.app.numplex.distributions.DistributionsActivity;
import com.app.numplex.home.HomeActivity;
import com.app.numplex.karnaugh.KarnaughActivity;
import com.app.numplex.matrix.MatrixActivity;
import com.app.numplex.matrix_complex.MatrixComplexActivity;
import com.app.numplex.mmc_mdc.MMC_MDC_Activity;
import com.app.numplex.network.NetworkActivity;
import com.app.numplex.partial_fractions.PartialFractionActivity;
import com.app.numplex.queue.QueueActivity;
import com.app.numplex.randomizer.RandomizerActivity;
import com.app.numplex.resistor.ResistorActivity;
import com.app.numplex.series.SeriesActivity;
import com.app.numplex.utils.LottieDialog;

import java.util.Locale;

public class DrawerItems extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void onDrawerItemSelected(int id, Context context) {
        Intent intent;
        try {
            if (id == R.id.home) {
                intent = new Intent(context, HomeActivity.class);
                context.startActivity(intent);
            } else if (id == R.id.impCalc) {
                intent = new Intent(context, ComplexCalculatorActivity.class);
                context.startActivity(intent);
            }
            else if (id == R.id.calculator) {
                intent = new Intent(context, CalculatorActivity.class);
                context.startActivity(intent);
            }
            else if (id == R.id.dig_com) {
                intent = new Intent(context, DigitalCommunicationActivity.class);
                context.startActivity(intent);
            }
            else if (id == R.id.algebra) {
                intent = new Intent(context, AlgebraActivity.class);
                context.startActivity(intent);
            } else if (id == R.id.matrix) {
                intent = new Intent(context, MatrixActivity.class);
                context.startActivity(intent);
            }
            else if (id == R.id.matrixComplex) {
                intent = new Intent(context, MatrixComplexActivity.class);
                context.startActivity(intent);
            }
            else if (id == R.id.mmc) {
                intent = new Intent(context, MMC_MDC_Activity.class);
                context.startActivity(intent);
            }
            else if (id == R.id.randomizer) {
                intent = new Intent(context, RandomizerActivity.class);
                context.startActivity(intent);
            }
            else if (id == R.id.distribution) {
                intent = new Intent(context, DistributionsActivity.class);
                context.startActivity(intent);
            }
            else if (id == R.id.kmap) {
                intent = new Intent(context, KarnaughActivity.class);
                context.startActivity(intent);
            }
            else if (id == R.id.network) {
                intent = new Intent(context, NetworkActivity.class);
                context.startActivity(intent);
            }
            else if (id == R.id.partial_fractions) {
                intent = new Intent(context, PartialFractionActivity.class);
                context.startActivity(intent);
            }
            else if (id == R.id.queue) {
                intent = new Intent(context, QueueActivity.class);
                context.startActivity(intent);
            }
            else if (id == R.id.series) {
                intent = new Intent(context, SeriesActivity.class);
                context.startActivity(intent);
            }
            else if (id == R.id.boole) {
                intent = new Intent(context, BooleanActivity.class);
                context.startActivity(intent);
            }
            else if (id == R.id.digital) {
                intent = new Intent(context, DigitalActivity.class);
                context.startActivity(intent);
            } else if (id == R.id.resistor) {
                intent = new Intent(context, ResistorActivity.class);
                context.startActivity(intent);
            } else if (id == R.id.settings) {
                intent = new Intent(context, SettingsActivity.class);
                context.startActivity(intent);
            } else if (id == R.id.report_bug) {
                DisplayMetrics dm = new DisplayMetrics();
                ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(dm);
                int width = dm.widthPixels;
                int height = dm.heightPixels;

                Locale current = ConfigurationCompat.getLocales(context.getResources().getConfiguration()).get(0);
                try {
                    context.startActivity(new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:exoticnumbers.feedback@gmail.com" +
                            "?cc=" + "" +
                            "&subject=" + Uri.encode("") +
                            "&body=" + Uri.encode(
                            "Api: " + Build.VERSION.SDK_INT + "\n" +
                                    "Screen size: " + width + "x" + height + "\n" +
                                    "App locale: " + current
                    ))));
                } catch (Exception e) {
                    Toast.makeText(context, R.string.error_opening_uri_1,
                            Toast.LENGTH_LONG).show();
                }
            } else if (id == R.id.rateus) {
                LottieDialog customDialog = new LottieDialog((Activity) context);
                customDialog.startDialog(R.layout.lottie_rate_now, 2000);
                new Handler().postDelayed(() -> AppRater.initializePlayStore(context), 2200);
            } else if (id == R.id.about) {
                intent = new Intent(context, AboutActivity.class);
                context.startActivity(intent);
            } else if (id == R.id.share){
                intent = new Intent();
                intent.setAction(Intent.ACTION_SEND);
                intent.putExtra(Intent.EXTRA_TEXT, "https://play.google.com/store/apps/details?id=" + context.getPackageName());
                intent.setType("text/plain");
                context.startActivity(intent);
            }
        } catch (Exception e) {
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}
