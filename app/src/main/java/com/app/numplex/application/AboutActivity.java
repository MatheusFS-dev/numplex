package com.app.numplex.application;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.app.numplex.R;

public class AboutActivity extends AppCompatActivity {
    public static SharedPreferences sharedPreferences = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        /*----------------------------------------------------------------------------------------*/
        // Night or light mode:
        sharedPreferences = getSharedPreferences("night", 0);
        boolean booleanValue = sharedPreferences.getBoolean("night_mode", false);
        if (booleanValue)
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        else
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        /*----------------------------------------------------------------------------------------*/

        TextView matheus = findViewById(R.id.Matheus);
        TextView forza = findViewById(R.id.Forza);
        TextView alvaro = findViewById(R.id.alvaro);
        TextView ewel = findViewById(R.id.ewel);
        TextView email = findViewById(R.id.email);

        /*----------------------------------------------------------------------------------------*/
        //Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(ContextCompat.getDrawable(this, R.drawable.ic_baseline_arrow_back_24_white));
        toolbar.setNavigationOnClickListener(view -> onBackPressed());
        /*----------------------------------------------------------------------------------------*/

        final Animation bounceAnim = AnimationUtils.loadAnimation(this,R.anim.bounce);
        matheus.setOnClickListener(v -> {
            matheus.startAnimation(bounceAnim);
            try {
                Uri uri = Uri.parse("https://www.linkedin.com/in/matheus-ferreira-silva");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            } catch (Exception e) {
                Toast.makeText(AboutActivity.this, R.string.error_opening_uri_1,
                        Toast.LENGTH_LONG).show();
            }
        });

        forza.setOnClickListener(v -> {
            forza.startAnimation(bounceAnim);
            try {
                Uri uri = Uri.parse("https://www.linkedin.com/in/felipe-lorenzo-sossai-silva-forza-321930219");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            } catch (Exception e) {
                Toast.makeText(AboutActivity.this, R.string.error_opening_uri_1,
                        Toast.LENGTH_LONG).show();
            }
        });

        alvaro.setOnClickListener(v -> {
            alvaro.startAnimation(bounceAnim);
            try {
                Uri uri = Uri.parse("https://www.linkedin.com/in/alvaro-lucio-almeida-ribeiro");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            } catch (Exception e) {
                Toast.makeText(AboutActivity.this, R.string.error_opening_uri_1,
                        Toast.LENGTH_LONG).show();
            }
        });

        ewel.setOnClickListener(v -> {
            alvaro.startAnimation(bounceAnim);
            try {
                Uri uri = Uri.parse("https://www.linkedin.com/in/ewel");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            } catch (Exception e) {
                Toast.makeText(AboutActivity.this, R.string.error_opening_uri_1,
                        Toast.LENGTH_LONG).show();
            }
        });

        //Email listener:
        email.setOnClickListener(v -> {
            email.startAnimation(bounceAnim);
            try {
                startActivity(new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:exoticnumbers.feedback@gmail.com" +
                        "?cc=" + "" +
                        "&subject=" + Uri.encode(""))));
            } catch (Exception e) {
                Toast.makeText(AboutActivity.this, R.string.error_opening_uri_1,
                        Toast.LENGTH_LONG).show();
            }
        });

    }
}