package com.app.numplex.digital;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.app.numplex.R;
import com.app.numplex.application.AppRater;
import com.app.numplex.application.AppStatus;
import com.app.numplex.utils.DoneOnEditorActionListener;
import com.app.numplex.utils.DuoDrawerSetter;
import com.app.numplex.utils.Functions;
import com.app.numplex.utils.RepeatListener;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.Objects;

import nl.psdcompany.duonavigationdrawer.views.DuoDrawerLayout;

public class DigitalActivity extends AppCompatActivity {
    private static final String STATE_EDIT_TEXT = "state_edit_text";
    private static final String SELECTION_EDIT_TEXT = "selection_edit_text";
    private static String number = "bin";
    private static String previousValue = "";
    private static boolean upDown = false; // Aux var to detect errors
    private DuoDrawerLayout drawerLayout;
    private EditText binary;
    private EditText decimal;
    private EditText octal;
    private EditText hexadecimal;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /*----------------------------------------------------------------------------------------*/
        // Loading configs:
        Functions.updateConfigs(this, this);
        /*----------------------------------------------------------------------------------------*/
        setContentView(R.layout.activity_digital);

        binary = findViewById(R.id.binary);
        decimal = findViewById(R.id.decimal);
        octal = findViewById(R.id.octal);
        hexadecimal = findViewById(R.id.hexadecimal);

        TextView bin = findViewById(R.id.bin);
        TextView dec = findViewById(R.id.dec);
        TextView octa = findViewById(R.id.octa);
        TextView hexa = findViewById(R.id.hexa);
        Button cpl = findViewById(R.id.cpl);
        /*----------------------------------------------------------------------------------------*/
        //Navigation drawer
        setDuoNavigationDrawer();

        //Toolbar
        Functions.setToolbar(this, getResources().getString(R.string.home_digital), drawerLayout, () ->
                Functions.showSpotlightMessage(
                        this,
                        bin,
                        getString(R.string.home_digital),
                        getString(R.string.digital_help),
                        120f,
                        () -> Functions.showSpotlightMessage(
                                DigitalActivity.this,
                                cpl,
                                getString(R.string.home_digital),
                                getString(R.string.digital_help2),
                                120f, null)));
        /*----------------------------------------------------------------------------------------*/
        //Setting elements:
        binary.setShowSoftInputOnFocus(false);
        decimal.setShowSoftInputOnFocus(false);
        octal.setShowSoftInputOnFocus(false);
        hexadecimal.setShowSoftInputOnFocus(false);

        //Button cpl:
        cpl.setEnabled(false);
        cpl.setClickable(false);

        /*----------------------------------------------------------------------------------------*/
        //Initializing:
        updateEditText();
        EditText currentEditText;
        if (!previousValue.equals("")) {
            currentEditText = findEdittext();
            currentEditText.setText(previousValue);
            currentEditText.setSelection(previousValue.length());
            calculate();
        }
        updateKeyboard();
        /*----------------------------------------------------------------------------------------*/
        //Listeners:
        final Animation bounceAnim = AnimationUtils.loadAnimation(this, R.anim.bounce);
        LinearLayoutCompat binaryLayout = findViewById(R.id.binaryLinear);
        LinearLayoutCompat decimalLayout = findViewById(R.id.decimalLinear);
        LinearLayoutCompat octalLayout = findViewById(R.id.octaLinear);
        LinearLayoutCompat hexaLayout = findViewById(R.id.hexaLinear);

        bin.setOnTouchListener((view, motionEvent) -> {
            if (!number.equals("bin")) {
                binaryLayout.startAnimation(bounceAnim);
                number = "bin";
                upDown = true;
                updateEditText();
                calculate();
            }
            return false;
        });

        dec.setOnTouchListener((view, motionEvent) -> {
            if (!number.equals("dec")) {
                decimalLayout.startAnimation(bounceAnim);
                number = "dec";
                upDown = true;
                updateEditText();
                calculate();
            }
            return false;
        });

        octa.setOnTouchListener((view, motionEvent) -> {
            if (!number.equals("octa")) {
                octalLayout.startAnimation(bounceAnim);
                number = "octa";
                upDown = true;
                updateEditText();
                calculate();
            }
            return false;
        });

        hexa.setOnTouchListener((view, motionEvent) -> {
            if (!number.equals("hexa")) {
                hexaLayout.startAnimation(bounceAnim);
                number = "hexa";
                upDown = true;
                updateEditText();
                calculate();
            }
            return false;
        });

        binary.setOnTouchListener((view, motionEvent) -> {
            if (!number.equals("bin")) {
                binaryLayout.startAnimation(bounceAnim);
                number = "bin";
                upDown = true;
                updateEditText();
                calculate();
            }
            return false;
        });

        decimal.setOnTouchListener((view, motionEvent) -> {
            if (!number.equals("dec")) {
                decimalLayout.startAnimation(bounceAnim);
                number = "dec";
                upDown = true;
                updateEditText();
                calculate();
            }
            return false;
        });

        octal.setOnTouchListener((view, motionEvent) -> {
            if (!number.equals("octa")) {
                octalLayout.startAnimation(bounceAnim);
                number = "octa";
                upDown = true;
                updateEditText();
                calculate();
            }
            return false;
        });

        hexadecimal.setOnTouchListener((view, motionEvent) -> {
            if (!number.equals("hexa")) {
                hexaLayout.startAnimation(bounceAnim);
                number = "hexa";
                upDown = true;
                updateEditText();
                calculate();
            }
            return false;
        });

        /*------------------------------------------------------------------------*/
        //Removing copy paste popup and cursor pointer:
        binary.setCustomSelectionActionModeCallback(new ActionMode.Callback() {
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                //Do nothing
                return false;
            }

            public void onDestroyActionMode(ActionMode mode) {
                //Do nothing
            }

            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                //Do nothing
                return false;
            }

            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                //Do nothing
                return false;
            }
        });

        decimal.setCustomSelectionActionModeCallback(new ActionMode.Callback() {
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                //Do nothing
                return false;
            }

            public void onDestroyActionMode(ActionMode mode) {
                //Do nothing
            }

            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                //Do nothing
                return false;
            }

            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                //Do nothing
                return false;
            }
        });

        octal.setCustomSelectionActionModeCallback(new ActionMode.Callback() {
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                //Do nothing
                return false;
            }

            public void onDestroyActionMode(ActionMode mode) {
                //Do nothing
            }

            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                //Do nothing
                return false;
            }

            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                //Do nothing
                return false;
            }
        });

        hexadecimal.setCustomSelectionActionModeCallback(new ActionMode.Callback() {
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                //Do nothing
                return false;
            }

            public void onDestroyActionMode(ActionMode mode) {
                //Do nothing
            }

            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                //Do nothing
                return false;
            }

            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                //Do nothing
                return false;
            }
        });
        /*------------------------------------------------------------------------*/
        //Input copy and paste buttons
        binary.setOnLongClickListener(v -> {
            binaryLayout.startAnimation(bounceAnim);
            PopupMenu popup = new PopupMenu(this, v);
            MenuInflater inflater = popup.getMenuInflater();
            inflater.inflate(R.menu.copy_paste_menu, popup.getMenu());

            popup.setOnMenuItemClickListener(item -> copyText(item.getItemId(), binary));
            popup.show();
            return true;
        });

        decimal.setOnLongClickListener(v -> {
            decimalLayout.startAnimation(bounceAnim);
            PopupMenu popup = new PopupMenu(this, v);
            MenuInflater inflater = popup.getMenuInflater();
            inflater.inflate(R.menu.copy_paste_menu, popup.getMenu());

            popup.setOnMenuItemClickListener(item -> copyText(item.getItemId(), decimal));
            popup.show();
            return true;
        });

        octal.setOnLongClickListener(v -> {
            octalLayout.startAnimation(bounceAnim);
            PopupMenu popup = new PopupMenu(this, v);
            MenuInflater inflater = popup.getMenuInflater();
            inflater.inflate(R.menu.copy_paste_menu, popup.getMenu());

            popup.setOnMenuItemClickListener(item -> copyText(item.getItemId(), octal));
            popup.show();
            return true;
        });

        hexadecimal.setOnLongClickListener(v -> {
            hexaLayout.startAnimation(bounceAnim);
            PopupMenu popup = new PopupMenu(this, v);
            MenuInflater inflater = popup.getMenuInflater();
            inflater.inflate(R.menu.copy_paste_menu, popup.getMenu());

            popup.setOnMenuItemClickListener(item -> copyText(item.getItemId(), hexadecimal));
            popup.show();
            return true;
        });
        /*------------------------------------------------------------------------*/
        //Button delete:
        RepeatListener.setBounceAnim(bounceAnim);
        Button del = findViewById(R.id.del);
        del.setOnTouchListener(new RepeatListener(400, 150, view -> del()));

        //Button move left
        Button moveleft = findViewById(R.id.left);
        moveleft.setOnTouchListener(new RepeatListener(400, 150, view -> {
            EditText text = findEdittext();
            int cursorPos = text.getSelectionStart();
            try {
                if (!text.getText().toString().equals(getResources().getString(R.string.Syntax_Error)))
                    text.setSelection(cursorPos - 1);
            } catch (Exception ignored) {
            }
        }));

        //Button move right
        Button moveright = findViewById(R.id.right);
        moveright.setOnTouchListener(new RepeatListener(400, 150, view -> {
            EditText text = findEdittext();
            int cursorPos = text.getSelectionStart();
            try {
                if (!text.getText().toString().equals(getResources().getString(R.string.Syntax_Error)))
                    text.setSelection(cursorPos + 1);
            } catch (Exception ignored) {
            }
        }));

        //App rater:
        AppRater.app_launched(this, this);

        calculate();
    }

    /*------------------------------------------------------------------------*/
    //Method to calculate ConversionLogic:
    private void calculate() {
        String value = "";
        binary = findViewById(R.id.binary);
        decimal = findViewById(R.id.decimal);
        octal = findViewById(R.id.octal);
        hexadecimal = findViewById(R.id.hexadecimal);
        Button cpl = findViewById(R.id.cpl);

        updateKeyboard();
        try {
            updateEditText();
            switch (number) {
                case "bin":
                    value = binary.getText().toString();
                    ConversionLogic.isBinary(value);

                    if (value.equals("-"))
                        throw new RuntimeException();

                    if (!value.equals("")) {
                        if (!value.contains("."))
                            value = value + ".0";

                        decimal.setText(ConversionLogic.BinToDecimal(value));
                        octal.setText(ConversionLogic.DecimalFloatToOctal(ConversionLogic.BinToDecimal(value)));
                        hexadecimal.setText(ConversionLogic.DecimalFloatToHexadecimal(ConversionLogic.BinToDecimal(value)));
                    } else
                        clear();
                    break;
                case "dec":
                    value = decimal.getText().toString();
                    ConversionLogic.isDecimal(value);

                    if (value.endsWith(".") || value.equals("-"))
                        throw new RuntimeException();

                    if (!value.equals("")) {
                        if (!value.contains("."))
                            value = value + ".0";

                        binary.setText(ConversionLogic.DecimalFloatToBinary(value));
                        octal.setText(ConversionLogic.DecimalFloatToOctal(value));
                        hexadecimal.setText(ConversionLogic.DecimalFloatToHexadecimal(value));
                    } else
                        clear();
                    break;
                case "octa":
                    value = octal.getText().toString();
                    ConversionLogic.isOctal(value);

                    if (value.equals("-"))
                        throw new RuntimeException();

                    if (!value.equals("")) {
                        if (!value.contains("."))
                            value = value + ".0";

                        decimal.setText(ConversionLogic.OctalFloatToDecimal(value));
                        binary.setText(ConversionLogic.DecimalFloatToBinary(ConversionLogic.OctalFloatToDecimal(value)));
                        hexadecimal.setText(ConversionLogic.DecimalFloatToHexadecimal(ConversionLogic.OctalFloatToDecimal(value)).toUpperCase());
                    } else
                        clear();
                    break;
                case "hexa":
                    value = hexadecimal.getText().toString();
                    ConversionLogic.isHexa(value);

                    if (value.equals("-"))
                        throw new RuntimeException();

                    if (!value.equals("")) {
                        if (!value.contains("."))
                            value = value + ".0";

                        decimal.setText(ConversionLogic.HexadecimalFloatToDecimal(value));
                        octal.setText(ConversionLogic.DecimalFloatToOctal(ConversionLogic.HexadecimalFloatToDecimal(value)));
                        binary.setText(ConversionLogic.DecimalFloatToBinary(ConversionLogic.HexadecimalFloatToDecimal(value)));
                    } else
                        clear();
                    break;
            }

            if (value.startsWith(".") || value.startsWith("-."))
                throw new RuntimeException();

            // Checking conditions for cpl:
            String aux = binary.getText().toString();
            if (aux.contains(".") || aux.equals("")) {
                cpl.setEnabled(false);
                cpl.setClickable(false);
                cpl.setBackgroundResource(R.drawable.bg_solid_button_off);
            } else {
                cpl.setEnabled(true);
                cpl.setClickable(true);
                cpl.setBackgroundResource(R.drawable.ripple_effect_black);
            }

            EditText output = findEdittext();
            previousValue = output.getText().toString();
        } catch (Exception e) {
            TextView bin = findViewById(R.id.bin);
            TextView dec = findViewById(R.id.dec);
            TextView octa = findViewById(R.id.octa);
            TextView hexa = findViewById(R.id.hexa);

            String error = "<font color=#9A9A9A>" + getResources().getString(R.string.Syntax_Error) + "</font>";
            switch (number) {
                case "bin":
                    decimal.setText(Html.fromHtml(error));
                    octal.setText(Html.fromHtml(error));
                    hexadecimal.setText(Html.fromHtml(error));
                    bin.setBackgroundResource(R.drawable.bg_textview_error);
                    dec.setBackgroundResource(R.drawable.bg_textview_off);
                    octa.setBackgroundResource(R.drawable.bg_textview_off);
                    hexa.setBackgroundResource(R.drawable.bg_textview_off);
                    break;
                case "dec":
                    binary.setText(Html.fromHtml(error));
                    octal.setText(Html.fromHtml(error));
                    hexadecimal.setText(Html.fromHtml(error));
                    bin.setBackgroundResource(R.drawable.bg_textview_off);
                    dec.setBackgroundResource(R.drawable.bg_textview_error);
                    octa.setBackgroundResource(R.drawable.bg_textview_off);
                    hexa.setBackgroundResource(R.drawable.bg_textview_off);
                    break;
                case "octa":
                    decimal.setText(Html.fromHtml(error));
                    binary.setText(Html.fromHtml(error));
                    hexadecimal.setText(Html.fromHtml(error));
                    bin.setBackgroundResource(R.drawable.bg_textview_off);
                    dec.setBackgroundResource(R.drawable.bg_textview_off);
                    octa.setBackgroundResource(R.drawable.bg_textview_error);
                    hexa.setBackgroundResource(R.drawable.bg_textview_off);
                    break;
                case "hexa":
                    decimal.setText(Html.fromHtml(error));
                    octal.setText(Html.fromHtml(error));
                    binary.setText(Html.fromHtml(error));
                    bin.setBackgroundResource(R.drawable.bg_textview_off);
                    dec.setBackgroundResource(R.drawable.bg_textview_off);
                    octa.setBackgroundResource(R.drawable.bg_textview_off);
                    hexa.setBackgroundResource(R.drawable.bg_textview_error);
                    break;
            }
            cpl.setEnabled(false);
            cpl.setClickable(false);
            cpl.setBackgroundResource(R.drawable.bg_solid_button_off);
        }
    }

    //Method to insert letter on edit texts:
    private void writeText(String string) {
        EditText text = findEdittext();
        if (text.getText().toString().equals(getResources().getString(R.string.Syntax_Error)))
            text.setText("");

        int cursorPos = text.getSelectionStart();

        text.getText().insert(cursorPos, string);
    }

    //Button delete:
    private void del() {
        EditText text = findEdittext();
        if (text.getText().toString().equals(getResources().getString(R.string.Syntax_Error)))
            text.setText("");
        int cursorPos = text.getSelectionStart();
        int textLen = text.length();
        SpannableStringBuilder selection = (SpannableStringBuilder) text.getText();

        try {
            /*------------------------------------------------------------------------*/
            //Deleting cases:
            if (cursorPos == 0 && textLen != 0) {
                selection.replace(0, 1, "");
                text.setText(selection);
                text.setSelection(0);
            } else if (textLen != 0) {
                selection.replace(cursorPos - 1, cursorPos, "");
                text.setText(selection);
                text.setSelection(cursorPos - 1);
            }
            /*------------------------------------------------------------------------*/
        } catch (Exception ignored) {
        }
        calculate();
    }

    //Method to find focused edit text:
    private EditText findEdittext() {
        EditText output;

        switch (number) {
            case "bin":
                output = findViewById(R.id.binary);
                break;
            case "dec":
                output = findViewById(R.id.decimal);
                break;
            case "octa":
                output = findViewById(R.id.octal);
                break;
            case "hexa":
                output = findViewById(R.id.hexadecimal);
                break;
            default:
                throw new IllegalStateException();
        }

        return output;
    }

    //Updating buttons:
    private void updateKeyboard() {
        Button two = findViewById(R.id.two);
        Button three = findViewById(R.id.three);
        Button four = findViewById(R.id.four);
        Button five = findViewById(R.id.five);
        Button six = findViewById(R.id.six);
        Button seven = findViewById(R.id.seven);
        Button eight = findViewById(R.id.eight);
        Button nine = findViewById(R.id.nine);
        Button a = findViewById(R.id.a);
        Button b = findViewById(R.id.b);
        Button c = findViewById(R.id.c);
        Button d = findViewById(R.id.d);
        Button e = findViewById(R.id.e);
        Button f = findViewById(R.id.f);

        switch (number) {
            case "bin":
                two.setEnabled(false);
                three.setEnabled(false);
                four.setEnabled(false);
                five.setEnabled(false);
                six.setEnabled(false);
                seven.setEnabled(false);
                eight.setEnabled(false);
                nine.setEnabled(false);
                a.setEnabled(false);
                b.setEnabled(false);
                c.setEnabled(false);
                d.setEnabled(false);
                e.setEnabled(false);
                f.setEnabled(false);

                two.setClickable(false);
                three.setClickable(false);
                four.setClickable(false);
                five.setClickable(false);
                six.setClickable(false);
                seven.setClickable(false);
                eight.setClickable(false);
                nine.setClickable(false);
                a.setClickable(false);
                b.setClickable(false);
                c.setClickable(false);
                d.setClickable(false);
                e.setClickable(false);
                f.setClickable(false);

                two.setBackgroundResource(R.drawable.bg_solid_button_off);
                three.setBackgroundResource(R.drawable.bg_solid_button_off);
                four.setBackgroundResource(R.drawable.bg_solid_button_off);
                five.setBackgroundResource(R.drawable.bg_solid_button_off);
                six.setBackgroundResource(R.drawable.bg_solid_button_off);
                seven.setBackgroundResource(R.drawable.bg_solid_button_off);
                eight.setBackgroundResource(R.drawable.bg_solid_button_off);
                nine.setBackgroundResource(R.drawable.bg_solid_button_off);
                a.setBackgroundResource(R.drawable.bg_solid_button_off_blue);
                b.setBackgroundResource(R.drawable.bg_solid_button_off_blue);
                c.setBackgroundResource(R.drawable.bg_solid_button_off_blue);
                d.setBackgroundResource(R.drawable.bg_solid_button_off_blue);
                e.setBackgroundResource(R.drawable.bg_solid_button_off_blue);
                f.setBackgroundResource(R.drawable.bg_solid_button_off_blue);
                break;
            case "dec":
                two.setEnabled(true);
                three.setEnabled(true);
                four.setEnabled(true);
                five.setEnabled(true);
                six.setEnabled(true);
                seven.setEnabled(true);
                eight.setEnabled(true);
                nine.setEnabled(true);
                a.setEnabled(false);
                b.setEnabled(false);
                c.setEnabled(false);
                d.setEnabled(false);
                e.setEnabled(false);
                f.setEnabled(false);

                two.setClickable(true);
                three.setClickable(true);
                four.setClickable(true);
                five.setClickable(true);
                six.setClickable(true);
                seven.setClickable(true);
                eight.setClickable(true);
                nine.setClickable(true);
                a.setClickable(false);
                b.setClickable(false);
                c.setClickable(false);
                d.setClickable(false);
                e.setClickable(false);
                f.setClickable(false);

                two.setBackgroundResource(R.drawable.ripple_effect_black);
                three.setBackgroundResource(R.drawable.ripple_effect_black);
                four.setBackgroundResource(R.drawable.ripple_effect_black);
                five.setBackgroundResource(R.drawable.ripple_effect_black);
                six.setBackgroundResource(R.drawable.ripple_effect_black);
                seven.setBackgroundResource(R.drawable.ripple_effect_black);
                eight.setBackgroundResource(R.drawable.ripple_effect_black);
                nine.setBackgroundResource(R.drawable.ripple_effect_black);
                a.setBackgroundResource(R.drawable.bg_solid_button_off_blue);
                b.setBackgroundResource(R.drawable.bg_solid_button_off_blue);
                c.setBackgroundResource(R.drawable.bg_solid_button_off_blue);
                d.setBackgroundResource(R.drawable.bg_solid_button_off_blue);
                e.setBackgroundResource(R.drawable.bg_solid_button_off_blue);
                f.setBackgroundResource(R.drawable.bg_solid_button_off_blue);
                break;
            case "octa":
                two.setEnabled(true);
                three.setEnabled(true);
                four.setEnabled(true);
                five.setEnabled(true);
                six.setEnabled(true);
                seven.setEnabled(true);
                eight.setEnabled(false);
                nine.setEnabled(false);
                a.setEnabled(false);
                b.setEnabled(false);
                c.setEnabled(false);
                d.setEnabled(false);
                e.setEnabled(false);
                f.setEnabled(false);

                two.setClickable(true);
                three.setClickable(true);
                four.setClickable(true);
                five.setClickable(true);
                six.setClickable(true);
                seven.setClickable(true);
                eight.setClickable(false);
                nine.setClickable(false);
                a.setClickable(false);
                b.setClickable(false);
                c.setClickable(false);
                d.setClickable(false);
                e.setClickable(false);
                f.setClickable(false);

                two.setBackgroundResource(R.drawable.ripple_effect_black);
                three.setBackgroundResource(R.drawable.ripple_effect_black);
                four.setBackgroundResource(R.drawable.ripple_effect_black);
                five.setBackgroundResource(R.drawable.ripple_effect_black);
                six.setBackgroundResource(R.drawable.ripple_effect_black);
                seven.setBackgroundResource(R.drawable.ripple_effect_black);
                eight.setBackgroundResource(R.drawable.bg_solid_button_off);
                nine.setBackgroundResource(R.drawable.bg_solid_button_off);
                a.setBackgroundResource(R.drawable.bg_solid_button_off_blue);
                b.setBackgroundResource(R.drawable.bg_solid_button_off_blue);
                c.setBackgroundResource(R.drawable.bg_solid_button_off_blue);
                d.setBackgroundResource(R.drawable.bg_solid_button_off_blue);
                e.setBackgroundResource(R.drawable.bg_solid_button_off_blue);
                f.setBackgroundResource(R.drawable.bg_solid_button_off_blue);
                break;
            case "hexa":
                two.setEnabled(true);
                three.setEnabled(true);
                four.setEnabled(true);
                five.setEnabled(true);
                six.setEnabled(true);
                seven.setEnabled(true);
                eight.setEnabled(true);
                nine.setEnabled(true);
                a.setEnabled(true);
                b.setEnabled(true);
                c.setEnabled(true);
                d.setEnabled(true);
                e.setEnabled(true);
                f.setEnabled(true);

                two.setClickable(true);
                three.setClickable(true);
                four.setClickable(true);
                five.setClickable(true);
                six.setClickable(true);
                seven.setClickable(true);
                eight.setClickable(true);
                nine.setClickable(true);
                a.setClickable(true);
                b.setClickable(true);
                c.setClickable(true);
                d.setClickable(true);
                e.setClickable(true);
                f.setClickable(true);

                two.setBackgroundResource(R.drawable.ripple_effect_black);
                three.setBackgroundResource(R.drawable.ripple_effect_black);
                four.setBackgroundResource(R.drawable.ripple_effect_black);
                five.setBackgroundResource(R.drawable.ripple_effect_black);
                six.setBackgroundResource(R.drawable.ripple_effect_black);
                seven.setBackgroundResource(R.drawable.ripple_effect_black);
                eight.setBackgroundResource(R.drawable.ripple_effect_black);
                nine.setBackgroundResource(R.drawable.ripple_effect_black);
                a.setBackgroundResource(R.drawable.ripple_effect_blue);
                b.setBackgroundResource(R.drawable.ripple_effect_blue);
                c.setBackgroundResource(R.drawable.ripple_effect_blue);
                d.setBackgroundResource(R.drawable.ripple_effect_blue);
                e.setBackgroundResource(R.drawable.ripple_effect_blue);
                f.setBackgroundResource(R.drawable.ripple_effect_blue);
                break;
        }
    }

    //Updating edit texts:
    private void updateEditText() {
        TextView bin = findViewById(R.id.bin);
        TextView dec = findViewById(R.id.dec);
        TextView octa = findViewById(R.id.octa);
        TextView hexa = findViewById(R.id.hexa);

        binary = findViewById(R.id.binary);
        decimal = findViewById(R.id.decimal);
        octal = findViewById(R.id.octal);
        hexadecimal = findViewById(R.id.hexadecimal);

        switch (number) {
            case "bin":
                binary.requestFocus();
                if (upDown) {
                    if (binary.getText().toString().equals(getResources().getString(R.string.Syntax_Error)))
                        binary.setText("");
                    binary.setSelection(binary.length());
                    upDown = false;
                }
                bin.setBackgroundResource(R.drawable.bg_textview_on);
                dec.setBackgroundResource(R.drawable.bg_textview_off);
                octa.setBackgroundResource(R.drawable.bg_textview_off);
                hexa.setBackgroundResource(R.drawable.bg_textview_off);
                break;
            case "dec":
                decimal.requestFocus();
                if (upDown) {
                    if (decimal.getText().toString().equals(getResources().getString(R.string.Syntax_Error)))
                        decimal.setText("");
                    decimal.setSelection(decimal.length());
                    upDown = false;
                }
                bin.setBackgroundResource(R.drawable.bg_textview_off);
                dec.setBackgroundResource(R.drawable.bg_textview_on);
                octa.setBackgroundResource(R.drawable.bg_textview_off);
                hexa.setBackgroundResource(R.drawable.bg_textview_off);
                break;
            case "octa":
                octal.requestFocus();
                if (upDown) {
                    if (octal.getText().toString().equals(getResources().getString(R.string.Syntax_Error)))
                        octal.setText("");
                    octal.setSelection(octal.length());
                    upDown = false;
                }
                bin.setBackgroundResource(R.drawable.bg_textview_off);
                dec.setBackgroundResource(R.drawable.bg_textview_off);
                octa.setBackgroundResource(R.drawable.bg_textview_on);
                hexa.setBackgroundResource(R.drawable.bg_textview_off);
                break;
            case "hexa":
                hexadecimal.requestFocus();
                if (upDown) {
                    if (hexadecimal.getText().toString().equals(getResources().getString(R.string.Syntax_Error)))
                        hexadecimal.setText("");
                    hexadecimal.setSelection(hexadecimal.length());
                    upDown = false;
                }
                bin.setBackgroundResource(R.drawable.bg_textview_off);
                dec.setBackgroundResource(R.drawable.bg_textview_off);
                octa.setBackgroundResource(R.drawable.bg_textview_off);
                hexa.setBackgroundResource(R.drawable.bg_textview_on);
                break;
        }
        updateKeyboard();
    }

    //Clear:
    private void clear() {
        binary = findViewById(R.id.binary);
        decimal = findViewById(R.id.decimal);
        octal = findViewById(R.id.octal);
        hexadecimal = findViewById(R.id.hexadecimal);

        binary.setText("");
        decimal.setText("");
        octal.setText("");
        hexadecimal.setText("");
    }

    /*------------------------------------------------------------------------*/
    //Buttons:
    @SuppressLint("SetTextI18n")
    public void buttonPressed(View view) {
        final Animation bounceAnim = AnimationUtils.loadAnimation(this, R.anim.bounce);
        int id = view.getId();

        view.startAnimation(bounceAnim);
        if (id == R.id.AC)
            clear();
        else if (id == R.id.del)
            del();
        else if (id == R.id.up) {
            upDown = true;
            switch (number) {
                case "bin":
                    number = "hexa";
                    updateEditText();
                    break;
                case "dec":
                    number = "bin";
                    updateEditText();
                    break;
                case "octa":
                    number = "dec";
                    updateEditText();
                    break;
                case "hexa":
                    number = "octa";
                    updateEditText();
                    break;
            }
        } else if (id == R.id.down) {
            upDown = true;
            switch (number) {
                case "bin":
                    number = "dec";
                    updateEditText();
                    break;
                case "dec":
                    number = "octa";
                    updateEditText();
                    break;
                case "octa":
                    number = "hexa";
                    updateEditText();
                    break;
                case "hexa":
                    number = "bin";
                    updateEditText();
                    break;
            }
        } else if (id == R.id.seven)
            writeText("7");
        else if (id == R.id.eight)
            writeText("8");
        else if (id == R.id.nine)
            writeText("9");
        else if (id == R.id.four)
            writeText("4");
        else if (id == R.id.five)
            writeText("5");
        else if (id == R.id.six)
            writeText("6");
        else if (id == R.id.one)
            writeText("1");
        else if (id == R.id.two)
            writeText("2");
        else if (id == R.id.three)
            writeText("3");
        else if (id == R.id.zero)
            writeText("0");
        else if (id == R.id.point)
            writeText(".");
        else if (id == R.id.a)
            writeText("A");
        else if (id == R.id.b)
            writeText("B");
        else if (id == R.id.c)
            writeText("C");
        else if (id == R.id.d)
            writeText("D");
        else if (id == R.id.e)
            writeText("E");
        else if (id == R.id.f)
            writeText("F");
        else if (id == R.id.negative) {
            EditText aux = findEdittext();
            if (!aux.getText().toString().contains("-")) {
                aux.setText("-" + aux.getText().toString());
                aux.setSelection(aux.length());
            }
        } else if (id == R.id.cpl)
            openComplements();

        calculate();
    }

    /*------------------------------------------------------------------------*/
    // Some other methods:
    private boolean copyText(int item, EditText editText) {
        ClipboardManager clipboardManager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);

        if (item == R.id.copy) {
            try {
                calculate();
                if (!octal.getText().toString().equals("")) {
                    ClipData clipData = ClipData.newPlainText("text", editText.getText().toString());
                    clipboardManager.setPrimaryClip(clipData);

                    Toast.makeText(getApplicationContext(), R.string.copied, Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                Toast.makeText(getApplicationContext(), R.string.Syntax_Error, Toast.LENGTH_SHORT).show();
            }
            return true;
        } else if (item == R.id.paste) {
            try {
                ClipData clipData = clipboardManager.getPrimaryClip();
                ClipData.Item data = clipData.getItemAt(0);
                writeText(data.getText().toString());
                calculate();
            } catch (Exception e) {
                Toast.makeText(getApplicationContext(), R.string.invalid_input, Toast.LENGTH_SHORT).show();
            }
            return true;
        }
        return false;
    }

    /*--------------------------------------------------------------------------------------------*/
    //Navigation menu:
    private void setDuoNavigationDrawer() {
        LinearLayout toolbar = findViewById(R.id.toolbar);
        ImageButton toolbarButton = findViewById(R.id.toolbarButton);
        drawerLayout = findViewById(R.id.drawer);
        ConstraintLayout content = findViewById(R.id.content);

        DuoDrawerSetter drawerSetter = new DuoDrawerSetter(drawerLayout, this);
        drawerSetter.setDuoNavigationDrawer(this, toolbar, toolbarButton, content);
    }

    /*--------------------------------------------------------------------------------------------*/
    // Complements:
    @SuppressLint("SetTextI18n")
    private void openComplements() {
        AppStatus.checkFeatureAccess(this, () -> {
            BottomSheetDialog digitalDialog = new BottomSheetDialog(this);
            digitalDialog.setContentView(R.layout.bottom_dialog_digital_complements);

            ImageButton copyOne = digitalDialog.findViewById(R.id.copyOne);
            ImageButton copyTwo = digitalDialog.findViewById(R.id.copyTwo);
            EditText bits = digitalDialog.findViewById(R.id.bits);
            TextView error = digitalDialog.findViewById(R.id.error);
            TextView one_cpl = digitalDialog.findViewById(R.id.one_cpl);
            TextView two_cpl = digitalDialog.findViewById(R.id.two_cpl);

            Objects.requireNonNull(copyOne).setOnClickListener(view -> {
                if (!Objects.requireNonNull(one_cpl).getText().toString().equals("")) {
                    ClipboardManager clipboardManager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                    ClipData clipData = ClipData.newPlainText("text", Objects.requireNonNull(one_cpl).getText().toString());
                    clipboardManager.setPrimaryClip(clipData);

                    Toast.makeText(getApplicationContext(), R.string.copied, Toast.LENGTH_SHORT).show();
                }
            });

            Objects.requireNonNull(copyTwo).setOnClickListener(view -> {
                if (!Objects.requireNonNull(one_cpl).getText().toString().equals("")) {
                    ClipboardManager clipboardManager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                    ClipData clipData = ClipData.newPlainText("text", Objects.requireNonNull(two_cpl).getText().toString());
                    clipboardManager.setPrimaryClip(clipData);

                    Toast.makeText(getApplicationContext(), R.string.copied, Toast.LENGTH_SHORT).show();
                }
            });

            Objects.requireNonNull(bits).setOnFocusChangeListener((v, hasFocus) -> {
                if (!hasFocus)
                    calculateComplements(bits, one_cpl, two_cpl, error);
            });
            bits.setOnEditorActionListener(new DoneOnEditorActionListener());

            digitalDialog.show();

            // Calculate first time:
            calculateComplements(bits, one_cpl, two_cpl, error);
        });
    }

    // Calculates 1's and 2's complements
    @SuppressLint("SetTextI18n")
    private void calculateComplements(EditText bits, TextView one_cpl, TextView two_cpl, TextView error) {
        final Animation bounceAnim = AnimationUtils.loadAnimation(this, R.anim.bounce);
        String binaryNumber = binary.getText().toString();

        int mBits;
        if (!bits.getText().toString().equals(""))
            mBits = Integer.parseInt(bits.getText().toString());
        else {
            if (binaryNumber.startsWith("-"))
                mBits = binaryNumber.length();
            else
                mBits = binaryNumber.length() + 1;
        }
        bits.setHint(String.valueOf(mBits));

        try {
            String oneCpl = BinaryComplements.firstComplement(binaryNumber, mBits);
            String twoCpl = BinaryComplements.secondComplement(binaryNumber, mBits);

            if (twoCpl.startsWith("1"))
                twoCpl = twoCpl.replaceFirst("1", "<font color=#05C2E3>1</font>");
            else
                twoCpl = twoCpl.replaceFirst("0", "<font color=#05C2E3>0</font>");

            Objects.requireNonNull(one_cpl).setText(oneCpl);
            Objects.requireNonNull(two_cpl).setText(Html.fromHtml(twoCpl));
            bounceOutAnim(Objects.requireNonNull(error));
        } catch (Exception e) {
            if (Objects.requireNonNull(error).getVisibility() == View.GONE) {
                Objects.requireNonNull(error).setVisibility(View.VISIBLE);
                Objects.requireNonNull(error).startAnimation(bounceAnim);
            }
            binaryNumber = binaryNumber.replace("-", "");
            if (binaryNumber.length() >= mBits)
                Objects.requireNonNull(error).setText(
                        getResources().getString(R.string.input_requires) +
                                " " + (binaryNumber.length() + 1) + " bits");
            else
                Objects.requireNonNull(error).setText(
                        getResources().getString(R.string.Syntax_Error));
        }
    }

    /*--------------------------------------------------------------------------------------------*/
    // Some other methods:
    private void bounceOutAnim(View view) {
        final Animation bounceOut = AnimationUtils.loadAnimation(this, R.anim.bounce_out);
        view.startAnimation(bounceOut);
        new Handler().postDelayed(() -> view.setVisibility(View.GONE), 200);
    }

    /*--------------------------------------------------------------------------------------------*/
    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen())
            drawerLayout.closeDrawer();
        else
            super.onBackPressed();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(STATE_EDIT_TEXT, findEdittext().getText().toString());
        outState.putString(SELECTION_EDIT_TEXT, String.valueOf(findEdittext().getSelectionStart()));
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        findEdittext().setText(savedInstanceState.getString(STATE_EDIT_TEXT));
        findEdittext().setSelection(Integer.parseInt(savedInstanceState.getString(SELECTION_EDIT_TEXT)));
        calculate();
    }
}