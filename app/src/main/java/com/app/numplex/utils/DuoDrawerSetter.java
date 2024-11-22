package com.app.numplex.utils;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.app.numplex.R;
import com.app.numplex.application.DrawerItems;
import com.etebarian.meowbottomnavigation.MeowBottomNavigation;

import nl.psdcompany.duonavigationdrawer.views.DuoDrawerLayout;
import nl.psdcompany.duonavigationdrawer.widgets.DuoDrawerToggle;

public class DuoDrawerSetter implements View.OnClickListener{
    private final DuoDrawerLayout drawerLayout;
    private final Context context;
    private boolean contentNotRounded = false;
    private boolean allowAnim = true;

    public DuoDrawerSetter(DuoDrawerLayout drawerLayout, Context context) {
        this.drawerLayout = drawerLayout;
        this.context = context;
    }

    public void setDuoNavigationDrawer(Activity activity, LinearLayout toolbar,
                                       ImageButton toolbarButton, View content) {
        DuoDrawerToggle drawerToggle = new DuoDrawerToggle(activity, drawerLayout, null,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);

        drawerLayout.setDrawerListener(drawerToggle);
        drawerToggle.syncState();

        View menuView = drawerLayout.getMenuView();

        setElements(menuView, this);

        drawerLayout.setDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {
                // Respond when the drawer's position changes
                content.setBackground(ContextCompat.getDrawable(context,
                        R.drawable.bg_drawer_content_first));
                if(contentNotRounded)
                    toolbar.setBackground(ContextCompat.getDrawable(context,
                            R.drawable.bg_drawer_content_top_first));
                else
                    toolbar.setBackground(ContextCompat.getDrawable(context,
                            R.drawable.bg_drawer_content_main));
            }

            @SuppressLint("ClickableViewAccessibility")
            @Override
            public void onDrawerOpened(@NonNull View drawerView) {
                // Respond when the drawer is opened
                content.setBackground(ContextCompat.getDrawable(context,
                        R.drawable.bg_drawer_content_first));
                if(contentNotRounded)
                    toolbar.setBackground(ContextCompat.getDrawable(context,
                            R.drawable.bg_drawer_content_top_first));
                else
                    toolbar.setBackground(ContextCompat.getDrawable(context,
                            R.drawable.bg_drawer_content_main));
                content.setOnTouchListener((view, motionEvent) -> {
                    drawerLayout.closeDrawer();
                    return false;
                });

                if(allowAnim) {
                    startTransformationAnimation(true, toolbarButton);
                    allowAnim = false;
                }
            }

            @Override
            public void onDrawerClosed(@NonNull View drawerView) {
                // Respond when the drawer is closed
                content.setBackground(ContextCompat.getDrawable(context,
                        R.color.main_background_color));
                if(contentNotRounded)
                    toolbar.setBackground(ContextCompat.getDrawable(context,
                            R.color.main_color));
                else
                    toolbar.setBackground(ContextCompat.getDrawable(context,
                            R.drawable.rounded_toolbar));
                content.setOnTouchListener(null);

                if(!allowAnim) {
                    startTransformationAnimation(false, toolbarButton);
                    allowAnim = true;
                }
            }

            @Override
            public void onDrawerStateChanged(int newState) {
                // Respond when the drawer motion state changes
            }
        });
    }

    private static void setElements(View menuView, View.OnClickListener listener) {
        TextView home = menuView.findViewById(R.id.home);
        TextView calculator = menuView.findViewById(R.id.calculator);
        TextView complex_calculator = menuView.findViewById(R.id.impCalc);
        TextView numeric_base = menuView.findViewById(R.id.digital);
        TextView resistor = menuView.findViewById(R.id.resistor);
        TextView algebra = menuView.findViewById(R.id.algebra);
        TextView matrix = menuView.findViewById(R.id.matrix);
        TextView matrixComplex = menuView.findViewById(R.id.matrixComplex);
        TextView settings = menuView.findViewById(R.id.settings);
        TextView about = menuView.findViewById(R.id.about);
        TextView bug = menuView.findViewById(R.id.report_bug);
        TextView share = menuView.findViewById(R.id.share);
        TextView rateUs = menuView.findViewById(R.id.rateus);
        TextView mmc = menuView.findViewById(R.id.mmc);
        TextView randomizer = menuView.findViewById(R.id.randomizer);
        TextView series = menuView.findViewById(R.id.series);
        TextView boole = menuView.findViewById(R.id.boole);
        TextView kmap = menuView.findViewById(R.id.kmap);
        TextView network = menuView.findViewById(R.id.network);
        TextView distribution = menuView.findViewById(R.id.distribution);
        TextView partial_fractions = menuView.findViewById(R.id.partial_fractions);
        TextView queue = menuView.findViewById(R.id.queue);
        TextView dig_com = menuView.findViewById(R.id.dig_com);

        home.setOnClickListener(listener);
        calculator.setOnClickListener(listener);
        complex_calculator.setOnClickListener(listener);
        numeric_base.setOnClickListener(listener);
        resistor.setOnClickListener(listener);
        algebra.setOnClickListener(listener);
        matrix.setOnClickListener(listener);
        settings.setOnClickListener(listener);
        about.setOnClickListener(listener);
        bug.setOnClickListener(listener);
        share.setOnClickListener(listener);
        rateUs.setOnClickListener(listener);
        mmc.setOnClickListener(listener);
        randomizer.setOnClickListener(listener);
        series.setOnClickListener(listener);
        boole.setOnClickListener(listener);
        matrixComplex.setOnClickListener(listener);
        kmap.setOnClickListener(listener);
        network.setOnClickListener(listener);
        distribution.setOnClickListener(listener);
        partial_fractions.setOnClickListener(listener);
        queue.setOnClickListener(listener);
        dig_com.setOnClickListener(listener);
    }

    public void setDuoNavigationDrawer(Activity activity, ImageButton navigationButton, View content) {
        DuoDrawerToggle drawerToggle = new DuoDrawerToggle(activity, drawerLayout, null,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);

        drawerLayout.setDrawerListener(drawerToggle);
        drawerToggle.syncState();

        View menuView = drawerLayout.getMenuView();

        setElements(menuView, this);

        drawerLayout.setDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {
                // Respond when the drawer's position changes
                content.setBackground(ContextCompat.getDrawable(context,
                        R.drawable.bg_drawer_content_second));
            }

            @Override
            public void onDrawerOpened(@NonNull View drawerView) {
                // Respond when the drawer is opened
                content.setBackground(ContextCompat.getDrawable(context,
                        R.drawable.bg_drawer_content_second));

                if (allowAnim) {
                    startTransformationAnimationCalc(true, navigationButton);
                    allowAnim = false;
                }
            }

            @Override
            public void onDrawerClosed(@NonNull View drawerView) {
                // Respond when the drawer is closed
                content.setBackground(ContextCompat.getDrawable(context,
                        R.color.secondary_background_color));
                if (!allowAnim) {
                    startTransformationAnimationCalc(false, navigationButton);
                    allowAnim = true;
                }
            }

            @Override
            public void onDrawerStateChanged(int newState) {
                // Respond when the drawer motion state changes
            }
        });
    }

    public void setDuoNavigationDrawer(Activity activity, Toolbar toolbar, MeowBottomNavigation menu, View content) {
        DuoDrawerToggle drawerToggle = new DuoDrawerToggle(activity, drawerLayout, toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);

        drawerToggle.setDrawerIndicatorEnabled(false);

        drawerLayout.setDrawerListener(drawerToggle);
        drawerToggle.syncState();

        View menuView = drawerLayout.getMenuView();

        setElements(menuView, this);

        drawerLayout.setDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {
                // Respond when the drawer's position changes
                content.setBackground(ContextCompat.getDrawable(context,
                        R.drawable.bg_drawer_content_first));
                toolbar.setBackground(ContextCompat.getDrawable(context,
                        R.drawable.bg_drawer_content_main));
            }

            @Override
            public void onDrawerOpened(@NonNull View drawerView) {
                // Respond when the drawer is opened
                content.setBackground(ContextCompat.getDrawable(context,
                        R.drawable.bg_drawer_content_first));
                toolbar.setBackground(ContextCompat.getDrawable(context,
                        R.drawable.bg_drawer_content_main));
                if (!menu.isShowing(1))
                    menu.show(1, true);
            }

            @Override
            public void onDrawerClosed(@NonNull View drawerView) {
                // Respond when the drawer is closed
                content.setBackground(ContextCompat.getDrawable(context,
                        R.color.main_background_color));
                toolbar.setBackground(ContextCompat.getDrawable(context,
                        R.drawable.rounded_toolbar));
                menu.show(2, true);
            }

            @Override
            public void onDrawerStateChanged(int newState) {
                // Respond when the drawer motion state changes
            }
        });
    }

    private void startTransformationAnimation(boolean open, ImageButton menuButton) {
        //Create rotation animator
        float start = open ? 180 : 0;
        float end = open ? 0 : 180;
        ObjectAnimator rotateAnimator = ObjectAnimator.ofFloat(menuButton, "rotation", start, end);
        rotateAnimator.setDuration(200);

        // Create icon change animator
        Drawable arrowIcon = ContextCompat.getDrawable(context, R.drawable.ic_baseline_arrow_back_24);
        Drawable hamburgerIcon = ContextCompat.getDrawable(context, R.drawable.ic_baseline_menu_24);

        // Play both animators together
        rotateAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
                if(open)
                    menuButton.setImageDrawable(arrowIcon);
                else
                    menuButton.setImageDrawable(hamburgerIcon);
            }

            @Override
            public void onAnimationEnd(Animator animator) {

            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
        rotateAnimator.start();
    }

    private void startTransformationAnimationCalc(boolean open, ImageButton menuButton) {
        //Create rotation animator
        float start = open ? 180 : 0;
        float end = open ? 0 : 180;
        ObjectAnimator rotateAnimator = ObjectAnimator.ofFloat(menuButton, "rotation", start, end);
        rotateAnimator.setDuration(200);

        // Create icon change animator
        Drawable arrowIcon = ContextCompat.getDrawable(context, R.drawable.ic_round_arrow_back_24);
        Drawable hamburgerIcon = ContextCompat.getDrawable(context, R.drawable.ic_round_menu_12);

        // Play both animators together
        rotateAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
                if (open)
                    menuButton.setImageDrawable(arrowIcon);
                else
                    menuButton.setImageDrawable(hamburgerIcon);
            }

            @Override
            public void onAnimationEnd(Animator animator) {

            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
        rotateAnimator.start();
    }

    @Override
    public void onClick(View view) {
        drawerLayout.closeDrawer();
        DrawerItems drawerItems = new DrawerItems();
        drawerItems.onDrawerItemSelected(view.getId(), context);
    }

    public void setContentNotRounded(boolean contentNotRounded) {
        this.contentNotRounded = contentNotRounded;
    }
}
