package com.app.numplex.network;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.app.numplex.R;
import com.app.numplex.application.AppRater;
import com.app.numplex.application.AppStatus;
import com.app.numplex.utils.DuoDrawerSetter;
import com.app.numplex.utils.Functions;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import nl.psdcompany.duonavigationdrawer.views.DuoDrawerLayout;

public class NetworkActivity extends AppCompatActivity {

    public static String[][] previousDataRows = null;
    private static String ipString = "";
    private static String subnetMask = "";
    private DuoDrawerLayout drawerLayout;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /*----------------------------------------------------------------------------------------*/
        // Loading configs:
        Functions.updateConfigs(this, this);
        /*----------------------------------------------------------------------------------------*/

        setContentView(R.layout.activity_network);

        /*----------------------------------------------------------------------------------------*/
        // Navigation menu
        setDuoNavigationDrawer();

        //Toolbar
        Functions.setToolbar(this, getResources().getString(R.string.home_network), drawerLayout, null);

        final Animation bounceAnim = AnimationUtils.loadAnimation(this, R.anim.bounce);
        ImageButton help = findViewById(R.id.help);

        EditText ip = findViewById(R.id.ipv4);
        Spinner mask = findViewById(R.id.mask);

        help.setOnClickListener(view -> {
            help.startAnimation(bounceAnim);

            new Handler().postDelayed(() -> Functions.showSpotlightMessage(this, ip,
                    getString(R.string.home_network),
                    getString(R.string.enter_ip),
                    200f, () -> Functions.showSpotlightMessage(NetworkActivity.this, mask,
                            getString(R.string.home_network),
                            getString(R.string.enter_mask),
                            200f, null)), 300);
        });

        /*----------------------------------------------------------------------------------------*/
        // Fill the Spinner with subnet masks
        List<String> subnetMasks = new ArrayList<>();
        for (int i = 32; i >= 1; i--)
            subnetMasks.add(convertCIDRtoSubnetMask(i) + " /" + i);

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(this, R.layout.item_spinner_text_network, subnetMasks);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mask.setAdapter(dataAdapter);

        mask.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                InputMethodManager inputMethodManager = (InputMethodManager)
                        v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(ip.getWindowToken(), 0);
                ip.clearFocus();
            }
            return false;
        });

        mask.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                subnetMask = "/" + (32 - position);
                calculate();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        ip.addTextChangedListener(new TextWatcher() {
            private boolean isUpdating;
            private String previousText = "";

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (!isUpdating)
                    previousText = s.toString();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // do nothing
            }

            @Override
            public void afterTextChanged(Editable s) {
                ipString = s.toString();
                calculate();

                // if the text length has decreased (i.e., a character was deleted), don't run any logic
                if (s.length() < previousText.length()) {
                    previousText = s.toString();
                    return;
                }

                if (isUpdating) {
                    isUpdating = false;
                    return;
                }

                // count the number of dots already present
                int numDots = 0;
                for (int i = 0; i < s.length(); i++) {
                    if (s.charAt(i) == '.') {
                        numDots++;
                    }
                }

                // if string length increased and less than 3 dots, proceed
                if (numDots <= 3) {
                    String[] split = s.toString().split("\\.", -1); // keep trailing empty strings
                    StringBuilder result = new StringBuilder();
                    int selection = ip.getSelectionStart(); // get current selection position

                    for (int i = 0; i < split.length; i++) {
                        // If the length of split[i] is greater than 3 or it's the last element, then don't append a dot.
                        if (split[i].length() > 3) {
                            split[i] = split[i].substring(0, 3);
                            if (selection > i * 4) {
                                selection--;
                            }
                        }
                        result.append(split[i]);
                        if (i < split.length - 1 || (split[i].length() == 3 && numDots < 3)) {
                            result.append(".");
                            // check for specific condition where the last segment has exactly 3 digits and selection is at the end
                            if ((selection == i * 4 + 3) || (i == split.length - 1 && selection == s.length())) {
                                selection++;
                            }
                        }
                    }

                    isUpdating = true;
                    ip.setText(result.toString());
                    selection = Math.min(selection, result.length()); // Ensure selection index is not greater than text length
                    ip.setSelection(selection); // set the selection at the correct position
                }
                previousText = s.toString();
            }
        });

        /*----------------------------------------------------------------------------------------*/
        // Subnet divider
        Button divide = findViewById(R.id.divide);
        TableLayout table = findViewById(R.id.table);
        divide.setOnClickListener(v -> {
            AppStatus.checkFeatureAccess(this, () -> {
                if (!subnetMask.equals("/32") && isValidIPv4(ipString)) {
                    divide.startAnimation(bounceAnim);

                    String[][] dataRows = NetworkIP.divideSubnets(ipString + subnetMask, this);

                    if (previousDataRows == null || !Arrays.deepEquals(previousDataRows, dataRows)) {
                        if (table.getVisibility() == View.GONE) {
                            String[] header = getResources().getStringArray(R.array.table_header);
                            addRow(header, true);  // Adding header row
                            table.setVisibility(View.VISIBLE);
                        }

                        // Adding data rows from the string matrix
                        for (String[] dataRow : dataRows) {
                            addRow(dataRow, false);
                        }

                        previousDataRows = dataRows;
                    }
                }
            });
        });

        Button clearButton = findViewById(R.id.clear);
        clearButton.setOnClickListener(v -> {
            clearButton.startAnimation(bounceAnim);
            table.removeAllViews();
            table.setVisibility(View.GONE);
            previousDataRows = null;
        });

        /*----------------------------------------------------------------------------------------*/
        //App rater:
        AppRater.app_launched(this, this);
    }

    private void calculate() {
        TextView network_address = findViewById(R.id.network_address);
        TextView broadcast_address = findViewById(R.id.broadcast_address);
        TextView usable_hosts = findViewById(R.id.usable_hosts);
        TextView total_usable_hosts = findViewById(R.id.total_usable_hosts);
        TextView number_usable_hosts = findViewById(R.id.number_usable_hosts);

        try {
            String ipStringMask;
            if (isValidIPv4(ipString))
                ipStringMask = ipString + subnetMask;
            else
                throw new IllegalArgumentException("Invalid IP address");

            network_address.setText(NetworkIP.getEnderecoRede(ipStringMask));
            broadcast_address.setText(NetworkIP.getEnderecoBroadcast(ipStringMask));
            usable_hosts.setText(NetworkIP.getUsableHostIPRange(ipStringMask, this));
            total_usable_hosts.setText(String.valueOf(NetworkIP.getNumeroTotalHosts(ipStringMask)));
            number_usable_hosts.setText(String.valueOf(NetworkIP.getNumeroHostsUtilizaveis(ipStringMask)));
        } catch (Exception e) {
            network_address.setText(null);
            broadcast_address.setText(null);
            usable_hosts.setText(null);
            total_usable_hosts.setText(null);
            number_usable_hosts.setText(null);
        }
    }

    private void addRow(String[] data, boolean isHeader) {
        ScrollView scrollView = findViewById(R.id.content);  // replace with the actual id of your ScrollView
        TableLayout table = findViewById(R.id.table);

        TableRow row = new TableRow(this);

        // Add border lines between columns in the row
        row.setDividerDrawable(new ColorDrawable(Color.BLACK));
        row.setShowDividers(LinearLayout.SHOW_DIVIDER_MIDDLE);

        // Add starting vertical divider
        View startDivider = new View(this);
        startDivider.setLayoutParams(new TableRow.LayoutParams(1, TableRow.LayoutParams.MATCH_PARENT));
        startDivider.setBackgroundColor(Color.BLACK);
        row.addView(startDivider);

        for (String s : data) {
            TextView cell = new TextView(this);
            cell.setText(s);
            cell.setPadding(15, 10, 15, 10);
            cell.setGravity(Gravity.CENTER);

            if (isHeader) {
                cell.setBackgroundColor(Color.LTGRAY);
                cell.setTextColor(Color.BLACK);
            } else {
                cell.setTextColor(ContextCompat.getColor(this, R.color.text_color));
            }
            row.addView(cell);
        }

        // Add ending vertical divider, unless it's the header row
        if (!isHeader) {
            View endDivider = new View(this);
            endDivider.setLayoutParams(new TableRow.LayoutParams(1, TableRow.LayoutParams.MATCH_PARENT));
            endDivider.setBackgroundColor(Color.BLACK);
            row.addView(endDivider);
        }

        table.addView(row);

        // Add a divider view for rows
        View divider = new View(this);
        divider.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, 1));
        divider.setBackgroundColor(Color.BLACK);
        table.addView(divider);

        // Apply animation
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.slide_in_left);
        row.startAnimation(animation);

        // Schedule a runnable to be executed at some point in the future
        scrollView.post(() -> {
            // Scroll to the bottom of the table
            scrollView.fullScroll(View.FOCUS_DOWN);
        });
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

    private String convertCIDRtoSubnetMask(int cidr) {
        int value = 0xffffffff << (32 - cidr);
        byte[] bytes = new byte[]{(byte) (value >>> 24), (byte) (value >> 16 & 0xff), (byte) (value >> 8 & 0xff), (byte) (value & 0xff)};
        InetAddress netAddr = null;
        try {
            netAddr = InetAddress.getByAddress(bytes);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        assert netAddr != null;
        return netAddr.getHostAddress();
    }

    public boolean isValidIPv4(final String ip) {
        String regex = "^((25[0-5]|2[0-4][0-9]|1[0-9][0-9]{0,2}|0[0-9]{0,2}|[1-9]?[0-9]{0,2})\\.){3}(25[0-5]|2[0-4][0-9]|1[0-9][0-9]{0,2}|0[0-9]{0,2}|[1-9]?[0-9]{0,2})$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(ip);
        return matcher.matches();
    }

}