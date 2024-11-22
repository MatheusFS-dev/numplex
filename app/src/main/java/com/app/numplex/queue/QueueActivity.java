package com.app.numplex.queue;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.app.numplex.R;
import com.app.numplex.application.AppRater;
import com.app.numplex.application.AppStatus;
import com.app.numplex.utils.Animations;
import com.app.numplex.utils.DuoDrawerSetter;
import com.app.numplex.utils.Functions;

import java.util.ArrayList;
import java.util.List;

import nl.psdcompany.duonavigationdrawer.views.DuoDrawerLayout;

public class QueueActivity extends AppCompatActivity{

    private DuoDrawerLayout drawerLayout;

    private EditText currentSelection = null;
    private int queue_type;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /*----------------------------------------------------------------------------------------*/
        // Loading configs:
        Functions.updateConfigs(this, this);
        /*----------------------------------------------------------------------------------------*/

        setContentView(R.layout.activity_queueing);

        AppStatus.checkAccess(this);

        /*----------------------------------------------------------------------------------------*/
        // Navigation menu
        setDuoNavigationDrawer();

        //Toolbar
        Functions.setToolbar(this, getResources().getString(R.string.queue), drawerLayout, null);

        final Animation bounceAnim = AnimationUtils.loadAnimation(this, R.anim.bounce);
        ImageButton help = findViewById(R.id.help);
        Spinner queue = findViewById(R.id.spinner);

        help.setOnClickListener(view -> {
            help.startAnimation(bounceAnim);
            //TODO
        });

        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Code to handle before text changed
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Code to handle text changed
            }

            @Override
            public void afterTextChanged(Editable s) {
                calculate(queue_type);
            }
        };

        EditText lambdaEditText = findViewById(R.id.lambda);
        EditText muEditText = findViewById(R.id.mu);
        EditText serversEditText = findViewById(R.id.servers);
        EditText jEditText = findViewById(R.id.j);
        EditText kEditText = findViewById(R.id.k);
        EditText sEditText = findViewById(R.id.s);
        EditText xEditText = findViewById(R.id.x);

        View.OnFocusChangeListener focusChangeListener = (v, hasFocus) -> {
            if (hasFocus)
                currentSelection = (EditText) v;
        };

        lambdaEditText.setOnFocusChangeListener(focusChangeListener);
        lambdaEditText.addTextChangedListener(textWatcher);

        muEditText.setOnFocusChangeListener(focusChangeListener);
        muEditText.addTextChangedListener(textWatcher);

        serversEditText.setOnFocusChangeListener(focusChangeListener);
        serversEditText.addTextChangedListener(textWatcher);

        jEditText.setOnFocusChangeListener(focusChangeListener);
        jEditText.addTextChangedListener(textWatcher);

        kEditText.setOnFocusChangeListener(focusChangeListener);
        kEditText.addTextChangedListener(textWatcher);

        sEditText.setOnFocusChangeListener(focusChangeListener);
        sEditText.addTextChangedListener(textWatcher);

        xEditText.setOnFocusChangeListener(focusChangeListener);
        xEditText.addTextChangedListener(textWatcher);

        /*----------------------------------------------------------------------------------------*/
        // Fill the Spinner with subnet masks
        List<String> queues = new ArrayList<>();
        queues.add("M/M/1/∞/∞/∞/FIFO");
        queues.add("M/M/1/J/J+1/∞/FIFO");
        queues.add("M/M/m/0/m/∞/FIFO");
        queues.add("M/M/m/∞/∞/∞/FIFO");
        queues.add("M/M/m/J/K/∞/FIFO");
        //queues.add("M/M/m/0/m/S/FIFO");
        //queues.add("M/M/m/J/K/S/FIFO");

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(this, R.layout.item_spinner_text_network, queues);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        queue.setAdapter(dataAdapter);

        queue.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                InputMethodManager inputMethodManager = (InputMethodManager)
                        v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);

                if(currentSelection != null) {
                    inputMethodManager.hideSoftInputFromWindow(currentSelection.getWindowToken(), 0);
                    currentSelection.clearFocus();
                }
            }
            return false;
        });

        LinearLayout serversBody = findViewById(R.id.serversBody);
        LinearLayout jBody = findViewById(R.id.jBody);
        LinearLayout kBody = findViewById(R.id.kBody);
        LinearLayout sBody = findViewById(R.id.sBody);

        queue.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                queue_type = position;

                switch (queue_type) {
                    case 0 -> { // M/M/1/∞/∞/∞/FIFO
                        Animations.bounceOutAnim(serversBody, QueueActivity.this);
                        Animations.bounceOutAnim(jBody, QueueActivity.this);
                        Animations.bounceOutAnim(kBody, QueueActivity.this);
                        Animations.bounceOutAnim(sBody, QueueActivity.this);
                    }
                    case 1 -> { // M/M/1/J/J+1/∞/FIFO
                        Animations.bounceOutAnim(serversBody, QueueActivity.this);
                        Animations.bounceOutAnim(kBody, QueueActivity.this);
                        Animations.bounceOutAnim(sBody, QueueActivity.this);
                        Animations.bounceInAnim(jBody, QueueActivity.this);
                    } // M/M/m/0/m/∞/FIFO
                    case 2, 3 -> { // M/M/m/∞/∞/∞/FIFO
                        Animations.bounceOutAnim(kBody, QueueActivity.this);
                        Animations.bounceOutAnim(sBody, QueueActivity.this);
                        Animations.bounceOutAnim(jBody, QueueActivity.this);
                        Animations.bounceInAnim(serversBody, QueueActivity.this);
                    }
                    case 4 -> { // M/M/m/J/K/∞/FIFO
                        Animations.bounceInAnim(kBody, QueueActivity.this);
                        Animations.bounceInAnim(jBody, QueueActivity.this);
                        Animations.bounceInAnim(serversBody, QueueActivity.this);
                    }
                }

                calculate(queue_type);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        /*----------------------------------------------------------------------------------------*/
        //App rater:
        AppRater.app_launched(this, this);
    }

    private void calculate(int queue_type) {
        TextView p0 = findViewById(R.id.p0);
        TextView pb = findViewById(R.id.pb);
        TextView ew = findViewById(R.id.ew);
        TextView eq = findViewById(R.id.eq);
        TextView es = findViewById(R.id.es);
        TextView etw = findViewById(R.id.etw);
        TextView etq = findViewById(R.id.etq);
        TextView ets = findViewById(R.id.ets);
        TextView px = findViewById(R.id.px);

        try {
            switch (queue_type) {
                case 0 -> { // M/M/1/∞/∞/∞/FIFO
                    p0.setText(String.valueOf(MM1InfiniteFIFOQueue.getEmptyQueueProbability(getLambdaText(), getMuText())));
                    pb.setText("0");
                    ew.setText(String.valueOf(MM1InfiniteFIFOQueue.getAverageNumberInWait(getLambdaText(), getMuText())));
                    eq.setText(String.valueOf(MM1InfiniteFIFOQueue.getAverageNumberInQueue(getLambdaText(), getMuText())));
                    es.setText(String.valueOf(MM1InfiniteFIFOQueue.getAverageNumberInSystem(getLambdaText(), getMuText())));
                    etw.setText(String.valueOf(MM1InfiniteFIFOQueue.getAverageTimeInWait(getLambdaText(), getMuText())));
                    etq.setText(String.valueOf(MM1InfiniteFIFOQueue.getAverageTimeInQueue(getLambdaText(), getMuText())));
                    ets.setText(String.valueOf(MM1InfiniteFIFOQueue.getAverageTimeInSystem(getLambdaText(), getMuText())));
                    if (hasX())
                        px.setText(String.valueOf(MM1InfiniteFIFOQueue.getProbabilityKCustomers(getXText(), getLambdaText(), getMuText())));
                    else
                        px.setText(null);
                }
                case 1 -> { // M/M/1/J/J+1/∞/FIFO
                    p0.setText(String.valueOf(MM1J1InfiniteFIFOQueue.getEmptyQueueProbability(getLambdaText(), getMuText(), getJText())));
                    pb.setText(String.valueOf(MM1J1InfiniteFIFOQueue.getBlockingProbability(getLambdaText(), getMuText(), getJText())));
                    ew.setText(String.valueOf(MM1J1InfiniteFIFOQueue.getAverageNumberInWait(getLambdaText(), getMuText(), getJText())));
                    eq.setText(String.valueOf(MM1J1InfiniteFIFOQueue.getAverageNumberInQueue(getLambdaText(), getMuText(), getJText())));
                    es.setText(String.valueOf(MM1J1InfiniteFIFOQueue.getAverageNumberInSystem(getLambdaText(), getMuText(), getJText())));
                    etw.setText(String.valueOf(MM1J1InfiniteFIFOQueue.getAverageTimeInWait(getLambdaText(), getMuText(), getJText())));
                    etq.setText(String.valueOf(MM1J1InfiniteFIFOQueue.getAverageTimeInQueue(getLambdaText(), getMuText(), getJText())));
                    ets.setText(String.valueOf(MM1J1InfiniteFIFOQueue.getAverageTimeInSystem(getLambdaText(), getMuText(), getJText())));
                    if (hasX())
                        px.setText(String.valueOf(MM1J1InfiniteFIFOQueue.getProbabilityKCustomers(getXText(), getLambdaText(), getMuText(), getJText())));
                    else
                        px.setText(null);
                }
                case 2 -> { // M/M/m/0/m/∞/FIFO
                    p0.setText(String.valueOf(MMm0mInfiniteFIFOQueue.getEmptyQueueProbability(getLambdaText(), getMuText(), getServersText())));
                    pb.setText(String.valueOf(MMm0mInfiniteFIFOQueue.getBlockingProbability(getLambdaText(), getMuText(), getServersText())));
                    ew.setText(String.valueOf(MMm0mInfiniteFIFOQueue.getAverageNumberInWait(getLambdaText(), getMuText(), getServersText())));
                    eq.setText(String.valueOf(MMm0mInfiniteFIFOQueue.getAverageNumberInQueue(getLambdaText(), getMuText(), getServersText())));
                    es.setText(String.valueOf(MMm0mInfiniteFIFOQueue.getAverageNumberInSystem(getLambdaText(), getMuText(), getServersText())));
                    etw.setText(String.valueOf(MMm0mInfiniteFIFOQueue.getAverageTimeInWait(getMuText())));
                    etq.setText(String.valueOf(MMm0mInfiniteFIFOQueue.getAverageTimeInQueue(getMuText())));
                    ets.setText(String.valueOf(MMm0mInfiniteFIFOQueue.getAverageTimeInSystem(getMuText())));
                    if (hasX())
                        px.setText(String.valueOf(MMm0mInfiniteFIFOQueue.getProbabilityKCustomers(getXText(), getLambdaText(), getMuText(), getServersText())));
                    else
                        px.setText(null);
                }
                case 3 -> { // M/M/m/∞/∞/∞/FIFO
                    p0.setText(String.valueOf(MMmInfiniteFIFOQueue.getEmptyQueueProbability(getLambdaText(), getMuText(), getServersText())));
                    pb.setText("0");
                    ew.setText(String.valueOf(MMmInfiniteFIFOQueue.getAverageNumberInWait(getLambdaText(), getMuText(), getServersText())));
                    eq.setText(String.valueOf(MMmInfiniteFIFOQueue.getAverageNumberInQueue(getLambdaText(), getMuText(), getServersText())));
                    es.setText(String.valueOf(MMmInfiniteFIFOQueue.getAverageNumberInSystem(getLambdaText(), getMuText())));
                    etw.setText(String.valueOf(MMmInfiniteFIFOQueue.getAverageTimeInWait(getLambdaText(), getMuText(), getServersText())));
                    etq.setText(String.valueOf(MMmInfiniteFIFOQueue.getAverageTimeInQueue(getLambdaText(), getMuText(), getServersText())));
                    ets.setText(String.valueOf(MMmInfiniteFIFOQueue.getAverageTimeInSystem(getMuText())));
                    if (hasX())
                        px.setText(String.valueOf(MMmInfiniteFIFOQueue.getProbabilityKCustomers(getXText(), getLambdaText(), getMuText(), getServersText())));
                    else
                        px.setText(null);
                }
                case 4 -> { // M/M/m/J/K/∞/FIFO
                    p0.setText(String.valueOf(MMmJKInfiniteFIFOQueue.getEmptyQueueProbability(getLambdaText(), getMuText(), getServersText(), getJText())));
                    pb.setText("0");
                    ew.setText(String.valueOf(MMmJKInfiniteFIFOQueue.getAverageNumberInWait(getLambdaText(), getMuText(), getServersText(), getJText(), getKText())));
                    eq.setText(String.valueOf(MMmJKInfiniteFIFOQueue.getAverageNumberInQueue(getLambdaText(), getMuText(), getServersText(), getJText(), getKText())));
                    es.setText(String.valueOf(MMmJKInfiniteFIFOQueue.getAverageNumberInSystem(getLambdaText(), getMuText(), getServersText(), getJText(), getKText())));
                    etw.setText(String.valueOf(MMmJKInfiniteFIFOQueue.getAverageTimeInWait(getLambdaText(), getMuText(), getServersText(), getJText(), getKText())));
                    etq.setText(String.valueOf(MMmJKInfiniteFIFOQueue.getAverageTimeInQueue(getLambdaText(), getMuText(), getServersText(), getJText(), getKText())));
                    ets.setText(String.valueOf(MMmJKInfiniteFIFOQueue.getAverageTimeInSystem(getMuText())));
                    if (hasX())
                        px.setText(String.valueOf(MMmJKInfiniteFIFOQueue.getProbabilityKCustomers(getXText(), getLambdaText(), getMuText(), getServersText(), getJText())));
                    else
                        px.setText(null);
                }
            }

        }
        catch (Exception e) {
            p0.setText(null);
            ew.setText(null);
            eq.setText(null);
            es.setText(null);
            etw.setText(null);
            etq.setText(null);
            ets.setText(null);
        }
    }

    /*----------------------------------------------------------------------------------------*/
    public double getLambdaText() {
        EditText lambdaEditText = findViewById(R.id.lambda);
        return Double.parseDouble(lambdaEditText.getText().toString());
    }

    public double getMuText() {
        EditText muEditText = findViewById(R.id.mu);
        return Double.parseDouble(muEditText.getText().toString());
    }

    public int getServersText() {
        EditText serversEditText = findViewById(R.id.servers);
        return Integer.parseInt(serversEditText.getText().toString());
    }

    public int getJText() {
        EditText jEditText = findViewById(R.id.j);
        return Integer.parseInt(jEditText.getText().toString());
    }

    public int getXText() {
        EditText xEditText = findViewById(R.id.x);
        return Integer.parseInt(xEditText.getText().toString());
    }

    public int getKText() {
        EditText kEditText = findViewById(R.id.k);
        return Integer.parseInt(kEditText.getText().toString());
    }

    public int getSText() {
        EditText sEditText = findViewById(R.id.s);
        return Integer.parseInt(sEditText.getText().toString());
    }

    public boolean hasX() {
        EditText xEditText = findViewById(R.id.x);
        return !xEditText.getText().toString().equals("");
    }

    /*------------------------------------------------------------------------*/
    //Navigation menu:
    private void setDuoNavigationDrawer() {
        LinearLayout toolbar = findViewById(R.id.toolbar);
        ImageButton toolbarButton = findViewById(R.id.toolbarButton);
        drawerLayout = findViewById(R.id.drawer);
        ScrollView content = findViewById(R.id.content);

        DuoDrawerSetter drawerSetter = new DuoDrawerSetter(drawerLayout, this);
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