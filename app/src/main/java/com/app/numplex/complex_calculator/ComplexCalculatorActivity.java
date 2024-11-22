package com.app.numplex.complex_calculator;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.view.MenuInflater;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.BounceInterpolator;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import com.airbnb.lottie.LottieAnimationView;
import com.app.numplex.R;
import com.app.numplex.application.AppRater;
import com.app.numplex.application.AppStatus;
import com.app.numplex.utils.Animations;
import com.app.numplex.utils.LottieDialog;
import com.app.numplex.utils.DuoDrawerSetter;
import com.app.numplex.utils.Functions;
import com.app.numplex.utils.HistoryAnimationHelper;
import com.app.numplex.utils.MultipleClickListener;
import com.app.numplex.utils.RepeatListener;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;
import java.util.Objects;

import nl.psdcompany.duonavigationdrawer.views.DuoDrawerLayout;

public class ComplexCalculatorActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {
    private DuoDrawerLayout drawerLayout;
    private static int textsize = 24; //Text size

    private boolean degree = true; //Auxiliary variable
    private boolean equals = false; //Auxiliary variable

    private static NumplexComplex ANS = null; //Saves last result

    private EditText textViewOut;
    private EditText textView;

    private static String savedExpression = ""; //Auxiliary variable
    private static boolean frequency = true;

    private ArrayList<HistoryData> historyData = new ArrayList<>();
    public static boolean isHistoryAnimationEnabled = false;
    private BottomSheetDialog bottomDialog;

    /*------------------------------------------------------------------------*/
    //Starting app:
    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /*----------------------------------------------------------------------------------------*/
        // Loading configs:
        Functions.updateConfigs(this, this);
        /*----------------------------------------------------------------------------------------*/

        setContentView(R.layout.activity_complex_calc);

        AppStatus.checkAccess(this);

        loadData();

        Button unitBtn = findViewById(R.id.unit);
        if (NumplexComplex.getPolarunit() == 57.296) {
            unitBtn.setText(R.string.Degrees);
            degree = true;
        } else {
            unitBtn.setText(R.string.Radians);
            degree = false;
        }

        textViewOut = findViewById(R.id.txtOut);
        textViewOut.setKeyListener(null);

        textView = findViewById(R.id.txt);
        textView.setShowSoftInputOnFocus(false);
        textView.setText(savedExpression);
        textView.setSelection(textView.length());
        textView.setTextSize(textsize);
        textViewOut.setTextSize(textsize+4);

        highlightParentheses();
        calculate();

        /*------------------------------------------------------------------------*/
        //Navigation drawer
        setDuoNavigationDrawer();
        /*------------------------------------------------------------------------*/

        //Checking cursor position:
        textView.setOnClickListener(v -> checkCursorPosition());

        final Animation bounceAnim = AnimationUtils.loadAnimation(this, R.anim.bounce);
        //Calling navigation window:
        ImageButton navigationButton = findViewById(R.id.nav);
        navigationButton.setOnClickListener(v -> {
            if (drawerLayout.isDrawerOpen())
                drawerLayout.closeDrawer();
            else
                drawerLayout.openDrawer();
        });

        //Calling history:
        ImageButton history = findViewById(R.id.history);
        history.setOnClickListener(new MultipleClickListener() {
            @Override
            public void onDoubleClick() {
                // Do nothing
            }

            @Override
            public void onSingleClick() {
                history.startAnimation(bounceAnim);
                openHistory();
            }
        });

        //Calling more window:
        textViewOut.setOnClickListener(v -> {
            if (!textViewOut.getText().toString().equals(getResources().getString(R.string.Syntax_Error)) &&
                    !textViewOut.getText().toString().equals("")) {
                textViewOut.startAnimation(bounceAnim);
                openMoreWindow();
            }
        });

        RepeatListener.setBounceAnim(bounceAnim);
        //Button delete:
        Button del = findViewById(R.id.del);
        del.setOnTouchListener(new RepeatListener(400, 150, view -> ComplexCalculatorActivity.this.del()));

        //Button move left
        Button moveleft = findViewById(R.id.left);
        moveleft.setOnTouchListener(new RepeatListener(400, 150, view -> {
            textView = findViewById(R.id.txt);
            int cursorPos = textView.getSelectionStart();
            char[] input = textView.getText().toString().toCharArray();
            try {
                if (cursorPos - 6 >= 0 && input[cursorPos - 1] == 's' && input[cursorPos - 6] == 'P')
                    textView.setSelection(cursorPos - 6);
                else if (cursorPos - 1 >= 0 && input[cursorPos - 1] == 's')
                    textView.setSelection(cursorPos - 3);
                else if (cursorPos - 3 >= 0 && input[cursorPos - 1] == '(' && (input[cursorPos - 3] == 'a' || input[cursorPos - 2] == 'd')) {
                    textView.setSelection(cursorPos - 4);
                } else if (cursorPos - 1 >= 0 && input[cursorPos - 1] == ' ')
                    textView.setSelection(cursorPos - 2);
                else
                    textView.setSelection(cursorPos - 1);
            } catch (Exception ignored) {
            }
            highlightParentheses();
            equals = false;
        }));

        //Button move right
        Button moveright = findViewById(R.id.right);
        moveright.setOnTouchListener(new RepeatListener(400, 150, view -> {
            textView = findViewById(R.id.txt);
            int cursorPos = textView.getSelectionStart();
            char[] input = textView.getText().toString().toCharArray();
            try {
                if (input[cursorPos] == 'P')
                    textView.setSelection(cursorPos + 6);
                else if (input[cursorPos] == 'A')
                    textView.setSelection(cursorPos + 3);
                else if ((input[cursorPos] == 'C' || input[cursorPos] == 'I')
                        && (input[cursorPos + 1] == 'a' || input[cursorPos + 2] == 'd')) {
                    textView.setSelection(cursorPos + 4);
                } else if (cursorPos + 1 < textView.length() && input[cursorPos + 1] == ' ')
                    textView.setSelection(cursorPos + 2);
                else
                    textView.setSelection(cursorPos + 1);
            } catch (Exception ignored) {
            }
            highlightParentheses();
            equals = false;
        }));

        //Input copy and paste buttons
        textView.setOnLongClickListener(v -> {
            PopupMenu popup = new PopupMenu(this, v);
            MenuInflater inflater = popup.getMenuInflater();
            inflater.inflate(R.menu.copy_paste_menu, popup.getMenu());

            popup.setOnMenuItemClickListener(item -> {
                ClipboardManager clipboardManager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);

                int itemId = item.getItemId();
                if (itemId == R.id.copy) {
                    try {
                        //EvaluateInput.evaluate(textView.getText().toString());
                        if (!textView.getText().toString().equals("")) {
                            ClipData clipData = ClipData.newPlainText("text", textView.getText().toString());
                            clipboardManager.setPrimaryClip(clipData);

                            Toast.makeText(getApplicationContext(), R.string.copied, Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.Syntax_Error), Toast.LENGTH_SHORT).show();
                    }
                    return true;
                } else if (itemId == R.id.paste) {
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
            });
            popup.show();
            return true;
        });

        //App rater:
        AppRater.app_launched(this, this);
    }

    /*------------------------------------------------------------------------*/
    //Method to highlight parentheses
    public void highlightParentheses() {
        String txt;
        char[] input = String.valueOf(textView.getText()).toCharArray();
        int cursorPos = textView.getSelectionStart();
        char value;

        txt = textView.getText().toString();
        if (txt.contains("□"))
            txt = txt.replace("□", "<font color=#9A9A9A>□</font>");
        if (txt.contains("+"))
            txt = txt.replace("+", "<font color=#05C2E3>+</font>");
        if (txt.contains("﹣"))
            txt = txt.replace("﹣", "<font color=#05C2E3>﹣</font>");
        if (txt.contains("×"))
            txt = txt.replace("×", "<font color=#05C2E3>×</font>");
        if (txt.contains("÷"))
            txt = txt.replace("÷", "<font color=#05C2E3>÷</font>");
        if (txt.contains("‖"))
            txt = txt.replace("‖", "<font color=#05C2E3>‖</font>");
        if (txt.contains("i"))
            txt = txt.replace("i", "<i>i</i>");

        textView.setText(Html.fromHtml(txt));
        textView.setSelection(cursorPos);

        try {
            if (input[cursorPos] == ')') {
                txt = "<font color=#05C2E3>)</font>";
                textView.getText().replace(cursorPos, cursorPos + 1, Html.fromHtml(txt));
                int i = cursorPos - 1;
                int count = 0;
                while (i >= 0) {
                    value = input[i];

                    if (value == ')')
                        count++;
                    else if (value == '(') {
                        if (count != 0)
                            count--;
                        else {
                            txt = "<font color=#05C2E3>(</font>";
                            textView.getText().replace(i, i + 1, Html.fromHtml(txt));
                            break;
                        }
                    }
                    i--;
                }
            } else if (input[cursorPos - 1] == '(') {
                txt = "<font color=#05C2E3>(</font>";
                textView.getText().replace(cursorPos - 1, cursorPos, Html.fromHtml(txt));
                int i = cursorPos;
                int count = 0;
                while (i <= input.length) {
                    value = input[i];

                    if (value == '(')
                        count++;
                    else if (value == ')') {
                        if (count != 0)
                            count--;
                        else {
                            txt = "<font color=#05C2E3>)</font>";
                            textView.getText().replace(i, i + 1, Html.fromHtml(txt));
                            break;
                        }
                    }
                    i++;
                }
            }
        } catch (Exception ignored) {
        }
    }

    /*------------------------------------------------------------------------*/
    //Delete button method:
    private void del() {
        textView = findViewById(R.id.txt);
        int cursorPos = textView.getSelectionStart();
        int textLen = textView.length();

        SpannableStringBuilder selection = (SpannableStringBuilder) textView.getText();
        char[] input = selection.toString().toCharArray();

        try {
            /*------------------------------------------------------------------------*/
            //Deleting cases:
            if (equals && cursorPos == textLen)
                textView.setText("");
            else if (cursorPos == 0 && textLen != 0) {
                if (input[cursorPos] == 'P')
                    selection.replace(0, 6, "");
                else if (input[cursorPos] == 'A')
                    selection.replace(0, 3, "");
                else if (input[cursorPos] == 'C' || input[cursorPos] == 'I') {
                    if (cursorPos + 7 < textLen && input[cursorPos + 4] == '□' && input[cursorPos + 7] == '□')
                        selection.replace(0, 9, "");
                    else
                        selection.replace(0, 4, "");
                } else if (cursorPos + 1 < textLen && input[cursorPos] == '□' && input[cursorPos + 1] == ')')
                    selection.replace(0, 2, "");
                    //(□)
                else if (cursorPos + 2 < textLen && input[cursorPos] == '(' && input[cursorPos + 1] == '□' && input[cursorPos + 2] == ')') {
                    selection.replace(cursorPos, cursorPos + 3, "");
                    textView.setText(selection);
                    textView.setSelection(cursorPos);
                }
                //, □)
                else if (cursorPos + 1 < textLen && input[cursorPos] == ',' && input[cursorPos + 1] == ' ') {
                    selection.replace(cursorPos, cursorPos + 2, "");
                    textView.setText(selection);
                    textView.setSelection(cursorPos);
                } else {
                    selection.replace(0, 1, "");
                }
                textView.setText(selection);
                textView.setSelection(0);
            }
            /*------------------------------------------------------------------------------------*/
            // Deleting all Cap and Ind template:
            //Cap(□, □)|
            else if (cursorPos - 5 >= 0 && input[cursorPos - 2] == '□' && input[cursorPos - 3] == ' ' && input[cursorPos - 5] == '□') {
                selection.replace(cursorPos - 9, cursorPos, "");
                textView.setText(selection);
                textView.setSelection(cursorPos - 9);
            }
            //Cap(|□, □)
            else if (cursorPos + 3 < textLen && input[cursorPos + 3] == '□' && input[cursorPos] == '□' && input[cursorPos + 2] == ' ') {
                selection.replace(cursorPos - 4, cursorPos + 5, "");
                textView.setText(selection);
                textView.setSelection(cursorPos - 4);
            }
            /*------------------------------------------------------------------------------------*/
            else if (cursorPos - 6 >= 0 && input[cursorPos - 6] == 'P' && input[cursorPos - 1] == 's') {
                selection.replace(cursorPos - 6, cursorPos, "");
                textView.setText(selection);
                textView.setSelection(cursorPos - 6);
            } else if (cursorPos - 1 >= 0 && input[cursorPos - 1] == 's') {
                selection.replace(cursorPos - 3, cursorPos, "");
                textView.setText(selection);
                textView.setSelection(cursorPos - 3);
            } else if (cursorPos - 3 >= 0 && input[cursorPos - 1] == '(' && (input[cursorPos - 3] == 'a' || input[cursorPos - 2] == 'd')) {
                if (cursorPos < textLen && input[cursorPos] == '□')
                    selection.replace(cursorPos - 4, cursorPos + 1, "");
                else
                    selection.replace(cursorPos - 4, cursorPos, "");
                textView.setText(selection);
                textView.setSelection(cursorPos - 4);
            }

            /*------------------------------------------------------------------------*/
            //Deleting (□):
            //(|□)
            else if ((cursorPos - 1 >= 0 && cursorPos + 1 < textLen) &&
                    input[cursorPos - 1] == '(' && input[cursorPos] == '□' && input[cursorPos + 1] == ')') {
                selection.replace(cursorPos - 1, cursorPos + 2, "");
                textView.setText(selection);
                textView.setSelection(cursorPos - 1);
            }
            //|(□)
            else if (cursorPos + 2 < textLen && input[cursorPos + 1] == '□' && input[cursorPos] == '(' && input[cursorPos + 2] == ')') {
                selection.replace(cursorPos, cursorPos + 3, "");
                textView.setText(selection);
                textView.setSelection(cursorPos);
            }
            //(□|)
            else if (cursorPos - 2 >= 0 && cursorPos < textLen && input[cursorPos - 2] == '(' && input[cursorPos - 1] == '□' && input[cursorPos] == ')') {
                selection.replace(cursorPos - 2, cursorPos + 1, "");
                textView.setText(selection);
                textView.setSelection(cursorPos - 2);
            }
            //(□)|
            else if (cursorPos - 3 >= 0 && input[cursorPos - 3] == '(' && input[cursorPos - 2] == '□' && input[cursorPos - 1] == ')') {
                selection.replace(cursorPos - 2, cursorPos, "");
                textView.setText(selection);
                textView.setSelection(cursorPos - 2);
            }
            /*------------------------------------------------------------------------*/
            // □)|
            else if (cursorPos - 3 >= 0 && input[cursorPos - 1] == ')' && input[cursorPos - 2] == '□' && input[cursorPos - 3] == ' ') {
                selection.replace(cursorPos - 3, cursorPos, "");
                textView.setText(selection);
                textView.setSelection(cursorPos - 3);
            }

            //□)|
            else if (cursorPos - 2 >= 0 && input[cursorPos - 1] == ')' && input[cursorPos - 2] == '□') {
                selection.replace(cursorPos - 2, cursorPos, "");
                textView.setText(selection);
                textView.setSelection(cursorPos - 2);
            }

            /*------------------------------------------------------------------------*/
            //Cap(□|, □) or Ind(□|, □)
            else if (cursorPos - 1 >= 0 && input[cursorPos - 1] == '□')
                textView.setSelection(cursorPos - 1);
                /*------------------------------------------------------------------------*/
            else if (cursorPos - 1 >= 0 && input[cursorPos - 1] == ' ') {
                textView.setSelection(cursorPos - 2);
            } else if (textLen != 0) {
                selection.replace(cursorPos - 1, cursorPos, "");
                textView.setText(selection);
                textView.setSelection(cursorPos - 1);
            }
            /*------------------------------------------------------------------------*/
            equals = false;
            calculate();
        } catch (Exception ignored) {
        }

        /*------------------------------------------------------------------------*/
        //Returning '□':
        try {
            input = selection.toString().toCharArray();
            cursorPos = textView.getSelectionStart();
            if (input[cursorPos - 1] == '(' && input[cursorPos] == ')') {
                selection.insert(cursorPos, "□");
                textView.setText(selection.toString());
                textView.setSelection(cursorPos + 1);
            } else if (input[cursorPos] != '□' && input[cursorPos - 1] == '(' && (input[cursorPos - 2] == 'p' || input[cursorPos - 2] == 'd') && input[cursorPos] == ',') {
                selection.insert(cursorPos, "□");
                textView.setText(selection.toString());
                textView.setSelection(cursorPos + 1);
            } else if (input[cursorPos] == ')' && input[cursorPos - 1] == ' ') {
                selection.insert(cursorPos, "□");
                textView.setText(selection.toString());
                textView.setSelection(cursorPos + 1);
            }
            String str = textView.getText().toString();
            cursorPos = textView.getSelectionStart();
            if (str.contains("□")) {
                str = str.replace("□", "<font color=#9A9A9A>□</font>");
                textView.setText(Html.fromHtml(str));
                textView.setSelection(cursorPos);
            }
        } catch (Exception ignored) {
        }

        highlightParentheses();
        /*------------------------------------------------------------------------*/
    }

    /*------------------------------------------------------------------------*/
    //Cursor method to set cursor position and write string:
    public void writeText(String string) {
        SpannableStringBuilder selection_aux = (SpannableStringBuilder) textView.getText();
        String selection = String.valueOf(textView.getText());
        char[] input = selection.toCharArray();
        int cursorPos = textView.getSelectionStart();

        if (string.contains("□"))
            string = string.replace("□", "<font color=#9A9A9A>□</font>");

        /*------------------------------------------------------------------------*/
        //Character '□':
        try {
            if (input[textView.getSelectionStart()] == '□') {
                cursorPos++;
                selection_aux.replace(cursorPos - 1, cursorPos, "");
                textView.setText(selection_aux);
                textView.setSelection(cursorPos - 1);
            } else if (input[textView.getSelectionStart() - 1] == '□') {
                selection_aux.replace(cursorPos - 1, cursorPos, "");
                textView.setText(selection_aux);
                textView.setSelection(cursorPos - 1);
            }
            cursorPos = textView.getSelectionStart();
        } catch (Exception ignored) {
        }
        /*------------------------------------------------------------------------*/

        if (equals) {
            textView.setText(Html.fromHtml(string));
            textView.setSelection(textView.getText().length());
            equals = false;
        } else
            textView.getText().insert(cursorPos, Html.fromHtml(string));

        //() cases:
        if (string.equals("(<font color=#9A9A9A>□</font>)"))
            textView.setSelection(textView.getSelectionStart() - 2);

            //Cap and ind cases:
        else if (string.equals("Cap(<font color=#9A9A9A>□</font>, <font color=#9A9A9A>□</font>)") ||
                string.equals("Ind(<font color=#9A9A9A>□</font>, <font color=#9A9A9A>□</font>)"))
            textView.setSelection(textView.getSelectionStart() - 5);

        highlightParentheses();
    }

    /*------------------------------------------------------------------------*/
    //Method called when an operator is pressed:
    public void operatorPressed() {
        textView = findViewById(R.id.txt);
        int cursorPos = textView.getSelectionStart();
        SpannableStringBuilder selection = (SpannableStringBuilder) textView.getText();
        char[] input = selection.toString().toCharArray();

        try {
            if (equals) {
                textView.setText(R.string.Ans);
                textView.setSelection(3);
                equals = false;
            }
            if (input[cursorPos - 1] == '+' || input[cursorPos - 1] == '﹣' || input[cursorPos - 1] == '×'
                    || input[cursorPos - 1] == '÷' || input[cursorPos - 1] == '‖') {
                selection.replace(cursorPos - 1, cursorPos, "");
                textView.setText(selection);
                textView.setSelection(cursorPos - 1);
            }
        } catch (Exception ignored) {
        }
    }

    /*------------------------------------------------------------------------*/
    //Method that checks cursor position:
    public void checkCursorPosition() {
        SpannableStringBuilder selection = (SpannableStringBuilder) textView.getText();
        char[] input = selection.toString().toCharArray();
        int cursorPos = textView.getSelectionStart();
        equals = false;

        try {
            //An|s
            if (cursorPos - 1 >= 0 && input[cursorPos] == 's' && input[cursorPos - 1] == 'n')
                textView.setSelection(cursorPos + 1);
                //A|ns
            else if (cursorPos - 1 >= 0 && input[cursorPos] == 'n' && input[cursorPos - 1] == 'A')
                textView.setSelection(cursorPos + 2);
                //Pre|Ans
            else if (cursorPos - 1 >= 0 && input[cursorPos] == 'A' && input[cursorPos - 1] == 'e')
                textView.setSelection(cursorPos + 3);
                //Pr|eAns
            else if (cursorPos - 1 >= 0 && input[cursorPos] == 'e' && input[cursorPos - 1] == 'r')
                textView.setSelection(cursorPos + 4);
                //P|reAns
            else if (cursorPos - 1 >= 0 && input[cursorPos] == 'r' && input[cursorPos - 1] == 'P')
                textView.setSelection(cursorPos + 5);
                //Cap|(
            else if (cursorPos - 1 >= 0 && (input[cursorPos] == '(' &&
                    (input[cursorPos - 1] == 'p' || input[cursorPos - 1] == 'd')))
                textView.setSelection(cursorPos + 1);
                //Cap(□,| □
            else if (cursorPos - 1 >= 0 && input[cursorPos] == ' ' && input[cursorPos - 1] == ',')
                textView.setSelection(cursorPos - 1);
                //Ca|p(
            else if (input[cursorPos] == 'p' || input[cursorPos] == 'd')
                textView.setSelection(cursorPos + 2);
                //C|ap(
            else if (input[cursorPos] == 'a' || input[cursorPos] == 'n')
                textView.setSelection(cursorPos + 3);
        } catch (Exception ignored) {
        }
        highlightParentheses();
    }

    /*------------------------------------------------------------------------*/
    //Method that keeps trying to calculate:
    public void calculate() {
        String result;

        textView = findViewById(R.id.txt);
        textViewOut = findViewById(R.id.txtOut);

        if (textViewOut.getText().toString().equals(getResources().getString(R.string.Syntax_Error)))
            textViewOut.setText(null);

        try {
            if (!textView.getText().toString().equals("")) {
                double realPart = ComplexLogic.evaluate(textView.getText().toString()).getReal();
                double imgPart = ComplexLogic.evaluate(textView.getText().toString()).getImaginary();

                BigDecimal real = new BigDecimal(realPart);
                BigDecimal imag = new BigDecimal(imgPart);

                result = ComplexLogic.evaluate(textView.getText().toString()).toString();

                if (result.contains("E")) {
                    result = scientificFormat(
                            String.format(Locale.ENGLISH, "%.6E", real),
                            String.format(Locale.ENGLISH, "%.6E", imag));

                    result = result.replace("00x", "x");
                    result = result.replace("0x", "x");
                    result = result.replace("x10⁰", "");
                }

                textViewOut.setText(result);
            } else
                textViewOut.setText(null);

            ComplexCalculatorActivity.setSavedExpression(textView.getText().toString());
        } catch (Exception ignored) {
            textViewOut.setText(null);
        }
    }

    /*------------------------------------------------------------------------*/
    // Buttons:
    public void buttonPressed(View view) {
        final Animation bounceAnim = AnimationUtils.loadAnimation(this, R.anim.bounce);
        textView = findViewById(R.id.txt);
        textViewOut = findViewById(R.id.txtOut);
        char[] input = textView.getText().toString().toCharArray();
        int cursorPos = textView.getSelectionStart();

        view.startAnimation(bounceAnim);
        int id = view.getId();
        if (id == R.id.openingParentheses) {
            if (textView.getSelectionStart() == textView.length())
                writeText("(□)");
            else {
                if (input[textView.getSelectionStart()] == '□')
                    writeText("(□)");
                else
                    writeText("(");
            }
        } else if (id == R.id.closingParentheses)
            writeText(")");
        else if (id == R.id.comma)
            writeText(",");
        else if (id == R.id.negative) {
            try {
                if (isNumber(input[cursorPos]) && isNumber(input[cursorPos - 1])) {
                    int count = cursorPos - 1;
                    while (count >= 0 && isNumber(input[count]))
                        count--;
                    textView.setSelection(count + 1);
                }
            } catch (Exception ignored) {
            }
            writeText("-");
        } else if (id == R.id.pico)
            writeText("p");
        else if (id == R.id.nano)
            writeText("n");
        else if (id == R.id.micro)
            writeText("μ");
        else if (id == R.id.mili)
            writeText("m");
        else if (id == R.id.kilo)
            writeText("k");
        else if (id == R.id.mega)
            writeText("M");
        else if (id == R.id.giga)
            writeText("G");
        else if (id == R.id.pi)
            writeText("π");
        else if (id == R.id.division) {
            operatorPressed();
            writeText("÷");
        } else if (id == R.id.multiplication) {
            operatorPressed();
            writeText("×");
        } else if (id == R.id.polar)
            writeText("∠");
        else if (id == R.id.AC) {
            if(!textView.getText().toString().equals("")) {
                final View myView = findViewById(R.id.linearLayout3);
                final int originalColor = ContextCompat.getColor(this, R.color.secondary_background_color);
                final int newColor = ContextCompat.getColor(this, R.color.wave_effect);

                Animations.clearAnimation(myView, originalColor, newColor);
            }

            textViewOut.setText(null);
            textView.setText(null);
            textView.setSelection(textView.getText().length());
            equals = false;
        } else if (id == R.id.seven)
            writeText("7");
        else if (id == R.id.eight)
            writeText("8");
        else if (id == R.id.nine)
            writeText("9");
        else if (id == R.id.ind)
            writeText("Ind(□, □)");
        else if (id == R.id.subtraction) {
            operatorPressed();
            writeText("﹣");
        } else if (id == R.id.four)
            writeText("4");
        else if (id == R.id.five)
            writeText("5");
        else if (id == R.id.six)
            writeText("6");
        else if (id == R.id.cap)
            writeText("Cap(□, □)");
        else if (id == R.id.plus) {
            operatorPressed();
            writeText("+");
        } else if (id == R.id.one)
            writeText("1");
        else if (id == R.id.two)
            writeText("2");
        else if (id == R.id.three)
            writeText("3");
        else if (id == R.id.i)
            writeText("i");
        else if (id == R.id.parallel) {
            operatorPressed();
            writeText("‖");
        } else if (id == R.id.zero)
            writeText("0");
        else if (id == R.id.point)
            writeText(".");
        else if (id == R.id.E)
            writeText("E");
        else if (id == R.id.ans) {
            if (ANS != null)
                writeText("Ans");
        } else if (id == R.id.zoomin) {
            if (textsize != 48) {
                textsize += 4;
                textView.setTextSize(textsize);
                textViewOut.setTextSize(textsize+4);
                saveData();
            }
        } else if (id == R.id.zoomout) {
            if (textsize != 8) {
                textsize -= 4;
                textView.setTextSize(textsize);
                textViewOut.setTextSize(textsize+4);
                saveData();
            }
        }
        else if (id == R.id.help) {
            openHelp();
        } else if (id == R.id.unit)
            updateUnit();
        else if (id == R.id.frequency) {
            Button frequencyBtn = (Button) view;
            if (frequency) {
                frequencyBtn.setText("ω");
                frequency = false;
            } else {
                frequencyBtn.setText("f");
                frequency = true;
            }
        }
        calculate();
    }

    public void equals(View view) {
        final Animation bounceAnim = AnimationUtils.loadAnimation(this, R.anim.bounce);
        view.startAnimation(bounceAnim);

        textView = findViewById(R.id.txt);
        textViewOut = findViewById(R.id.txtOut);
        try {
            if (!textView.getText().toString().equals("")) {
                if (!equals) {
                    calculate();
                    if (textView.getText().toString().contains("Ans")) {
                        String aux = textView.getText().toString();
                        if (textView.getText().toString().contains("Ans"))
                            aux = aux.replace("Ans", ANS.toString());
                        ComplexCalculatorActivity.setSavedExpression(aux);
                    }
                    ANS = ComplexLogic.evaluate(textView.getText().toString());
                    equals = true;

                    // Saving result in history
                    HistoryData auxObject = new HistoryData();
                    auxObject.setExpression(textView.getText().toString());
                    auxObject.setResult(String.valueOf(ANS));
                    historyData.add(auxObject);
                    saveData();
                }
            }
        } catch (Exception e) {
            textViewOut.setText(R.string.Syntax_Error);
            Animation shake = AnimationUtils.loadAnimation(ComplexCalculatorActivity.this, R.anim.shake);
            textViewOut.startAnimation(shake);
        }
    }

    /* --------------------------------- History -------------------------------- */
    // More window:
    private void openMoreWindow() {
        final Animation bounceAnim = AnimationUtils.loadAnimation(this, R.anim.bounce);
        BottomSheetDialog moreDialog = new BottomSheetDialog(this);
        moreDialog.setContentView(R.layout.bottom_dialog_complex_results);
        setValues(moreDialog);

        // Listeners:
        ImageButton copyScientific = moreDialog.findViewById(R.id.copyScientific);
        ImageButton copyEngineer = moreDialog.findViewById(R.id.copyEngineer);
        ImageButton copyPolar = moreDialog.findViewById(R.id.copyPolar);
        ImageButton copyTrigonometric = moreDialog.findViewById(R.id.copyTrigonometric);
        ImageButton copyExponential = moreDialog.findViewById(R.id.copyExponential);
        ImageButton copyAdmittance = moreDialog.findViewById(R.id.copyAdmittance);
        ImageButton copyBig = moreDialog.findViewById(R.id.copyBig);

        Objects.requireNonNull(copyScientific).setOnClickListener(view -> copyBtn(view, moreDialog));
        Objects.requireNonNull(copyEngineer).setOnClickListener(view -> copyBtn(view, moreDialog));
        Objects.requireNonNull(copyPolar).setOnClickListener(view -> copyBtn(view, moreDialog));
        Objects.requireNonNull(copyTrigonometric).setOnClickListener(view -> copyBtn(view, moreDialog));
        Objects.requireNonNull(copyExponential).setOnClickListener(view -> copyBtn(view, moreDialog));
        Objects.requireNonNull(copyAdmittance).setOnClickListener(view -> copyBtn(view, moreDialog));
        Objects.requireNonNull(copyBig).setOnClickListener(view -> copyBtn(view, moreDialog));

        //Unit listener:
        TextView unit1 = moreDialog.findViewById(R.id.unit);
        Objects.requireNonNull(unit1).setOnClickListener(v -> {
            updateUnit();
            setValues(moreDialog);
            unit1.startAnimation(bounceAnim);
            calculate();
        });

        moreDialog.show();
    }

    private void setValues(BottomSheetDialog moreDialog) {
        TextView scientific = moreDialog.findViewById(R.id.textViewScientific);
        TextView engineer = moreDialog.findViewById(R.id.textViewEngineer);
        TextView trigonometric = moreDialog.findViewById(R.id.textViewTrigonometric);
        TextView exponential = moreDialog.findViewById(R.id.textViewExponential);
        TextView polar = moreDialog.findViewById(R.id.textViewPolar);
        TextView admittance = moreDialog.findViewById(R.id.textViewAdmittance);
        TextView big = moreDialog.findViewById(R.id.textViewBig);

        TextView unit1 = moreDialog.findViewById(R.id.unit);
        TextView unit2 = moreDialog.findViewById(R.id.unit2); // Completely useless, only to maintain symmetry

        if (NumplexComplex.getPolarunit() == 1) {
            Objects.requireNonNull(unit1).setText(R.string.RAD);
            Objects.requireNonNull(unit2).setText(R.string.RAD);
        } else {
            Objects.requireNonNull(unit1).setText(R.string.DEG);
            Objects.requireNonNull(unit2).setText(R.string.DEG);
        }

        String expression = ComplexCalculatorActivity.getSavedExpression();
        NumplexComplex result = ComplexLogic.evaluate(expression);
        BigDecimal real = BigDecimal.valueOf(result.getReal());
        BigDecimal imag = BigDecimal.valueOf(result.getImaginary());

        //Output:
        if (result.getImaginary() == 0 && result.getReal() == 0) {
            Objects.requireNonNull(scientific).setText("0");
            Objects.requireNonNull(engineer).setText("0");
            Objects.requireNonNull(polar).setText("0");
            Objects.requireNonNull(trigonometric).setText("0");
            Objects.requireNonNull(exponential).setText("0");
            Objects.requireNonNull(admittance).setText("∞");
            Objects.requireNonNull(big).setText("0");
        } else {
            String scientificText = scientificFormat(String.format(Locale.ENGLISH, "%.4E", real), String.format(Locale.ENGLISH, "%.4E", imag));
            scientificText = scientificText.replace("00x", "x");
            scientificText = scientificText.replace("0x", "x");
            Objects.requireNonNull(scientific).setText(scientificText);

            String engineerText;
            if (result.getImaginary() < 0) {
                engineerText = convert(result.getReal()) + " - " + convert(result.getImaginary() * -1.0) + "i";
                engineerText = engineerText.replace("0 - ", "-");
                engineerText = engineerText.replace(" - 0i", "");
            } else {
                engineerText = convert(result.getReal()) + " + " + convert(result.getImaginary()) + "i";
                engineerText = engineerText.replace("0 + ", "");
                engineerText = engineerText.replace(" + 0i", "");
            }
            Objects.requireNonNull(engineer).setText(engineerText);

            Objects.requireNonNull(polar).setText(NumplexComplex.polar(result));
            Objects.requireNonNull(trigonometric).setText(NumplexComplex.trigonometric(result));
            Objects.requireNonNull(exponential).setText(Html.fromHtml(NumplexComplex.exponential(result)));
            Objects.requireNonNull(admittance).setText(
                    ComplexLogic.convertImpedanceToAdmittance(real.doubleValue(), imag.doubleValue()));

            String bigDecimal;
            if (result.getImaginary() < 0) {
                bigDecimal = real.toPlainString() + " - " + imag.toPlainString().replace("-", "") + "i";
                if(real.doubleValue() == 0)
                    bigDecimal = bigDecimal.replace("0.0 - ", "-");
                bigDecimal = bigDecimal.replace(" - 0.0i", "");
            } else {
                bigDecimal = real.toPlainString() + " + " + imag.toPlainString() + "i";
                if(real.doubleValue() == 0)
                    bigDecimal = bigDecimal.replace("0.0 + ", "");
                bigDecimal = bigDecimal.replace(" + 0.0i", "");
            }
            if (result.getImaginary() == 0 && bigDecimal.endsWith(".0"))
                bigDecimal = bigDecimal.replace(".0", " ");
            else
                bigDecimal = bigDecimal.replace(".0 ", " ");
            bigDecimal = bigDecimal.replace(".0i", "i");

            Objects.requireNonNull(big).setText(bigDecimal);
        }
    }

    //Copy buttons:
    private void copyBtn(View view, BottomSheetDialog moreDialog) {
        final Animation bounceAnim = AnimationUtils.loadAnimation(this, R.anim.bounce);
        view.startAnimation(bounceAnim);
        ClipboardManager clipboardManager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        TextView target = null;
        int id = view.getId();

        if (id == R.id.copyScientific)
            target = moreDialog.findViewById(R.id.textViewScientific);
        else if (id == R.id.copyEngineer)
            target = moreDialog.findViewById(R.id.textViewEngineer);
        else if (id == R.id.copyPolar)
            target = moreDialog.findViewById(R.id.textViewPolar);
        else if (id == R.id.copyTrigonometric)
            target = moreDialog.findViewById(R.id.textViewTrigonometric);
        else if (id == R.id.copyExponential)
            target = moreDialog.findViewById(R.id.textViewExponential);
        else if (id == R.id.copyAdmittance)
            target = moreDialog.findViewById(R.id.textViewAdmittance);
        else if (id == R.id.copyBig)
            target = moreDialog.findViewById(R.id.textViewBig);

        ClipData clipData = ClipData.newPlainText("text", target != null ? target.getText().toString() : null);
        clipboardManager.setPrimaryClip(clipData);
        Toast.makeText(getApplicationContext(), R.string.copied, Toast.LENGTH_SHORT).show();
    }

    /* --------------------------------- History -------------------------------- */
    private void openHistory() {
        loadData();
        final Animation bounceAnim = AnimationUtils.loadAnimation(this, R.anim.bounce_in);
        isHistoryAnimationEnabled = true;
        bottomDialog = new BottomSheetDialog(this);
        bottomDialog.setContentView(R.layout.bottom_dialog_history);
        SwipeMenuListView listView = bottomDialog.findViewById(R.id.listHistory);
        Button clear = bottomDialog.findViewById(R.id.clear);

        ArrayList<HistoryData> values = historyData;
        Collections.reverse(values); // The reverse is the correct order

        HistoryListAdapter listAdapter = new HistoryListAdapter(this, values);

        /* --------------------------------- Swipe option -------------------------------- */
        SwipeMenuCreator creator = menu -> {
            // create "delete" item
            SwipeMenuItem deleteItem = new SwipeMenuItem(
                    getApplicationContext());
            // set item background
            deleteItem.setBackground(ContextCompat.getDrawable(this, R.drawable.bg_swipe_delete));
            // set item width
            deleteItem.setWidth(170);
            // set a icon
            deleteItem.setIcon(R.drawable.ic_baseline_delete_24);
            // add to menu
            menu.addMenuItem(deleteItem);
        };

        // set creator
        Objects.requireNonNull(listView).setMenuCreator(creator);
        Objects.requireNonNull(listView).setCloseInterpolator(new BounceInterpolator());
        Objects.requireNonNull(listView).setOpenInterpolator(new BounceInterpolator());
        Objects.requireNonNull(listView).setSwipeDirection(SwipeMenuListView.DIRECTION_LEFT);

        listView.setOnMenuItemClickListener((position, menu, index) -> {
            if (index == 0) {// delete
                HistoryAnimationHelper helper = new HistoryAnimationHelper(listAdapter, listView, values);
                helper.animateRemoval(listView, listView.getChildAt(position));
                isHistoryAnimationEnabled = false;

                if (values.isEmpty()) {
                    LottieAnimationView empty = bottomDialog.findViewById(R.id.emptyHistory);
                    Objects.requireNonNull(empty).setVisibility(View.VISIBLE);
                    empty.startAnimation(bounceAnim);
                }
                saveData();
            }
            return true;
        });
        /* ------------------------------------------------------------------------------- */

        Objects.requireNonNull(listView).setAdapter(listAdapter);
        listView.setOnItemClickListener(this);
        listView.setOnItemLongClickListener((arg0, v, index, arg3) -> {
            PopupMenu popup = new PopupMenu(ComplexCalculatorActivity.this, v);
            MenuInflater inflater = popup.getMenuInflater();
            inflater.inflate(R.menu.copy_delete_menu, popup.getMenu());

            popup.setOnMenuItemClickListener(item -> {
                ClipboardManager clipboardManager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);

                int itemId = item.getItemId();
                if (itemId == R.id.copyExpression) {
                    ClipData clipData = ClipData.newPlainText("text", historyData.get(index).getExpression());
                    clipboardManager.setPrimaryClip(clipData);

                    Toast.makeText(getApplicationContext(), R.string.copied, Toast.LENGTH_SHORT).show();
                    return true;
                } else if (itemId == R.id.copyResult) {
                    ClipData clipData = ClipData.newPlainText("text", historyData.get(index).getResult());
                    clipboardManager.setPrimaryClip(clipData);

                    Toast.makeText(getApplicationContext(), R.string.copied, Toast.LENGTH_SHORT).show();
                    return true;
                } else if (itemId == R.id.delete) {
                    HistoryAnimationHelper helper = new HistoryAnimationHelper(listAdapter, listView, values);
                    helper.animateRemoval(listView, v);
                    isHistoryAnimationEnabled = false;

                    if (values.isEmpty()) {
                        LottieAnimationView empty = bottomDialog.findViewById(R.id.emptyHistory);
                        Objects.requireNonNull(empty).setVisibility(View.VISIBLE);
                        empty.startAnimation(bounceAnim);
                    }
                    saveData();

                    return true;
                }
                return false;
            });
            popup.show();
            return true;
        });

        Objects.requireNonNull(clear).setOnClickListener(view -> {
            if (!values.isEmpty()) {
                historyData = new ArrayList<>();
                LottieDialog customDialog = new LottieDialog(this);
                customDialog.startDeleteDialog();
                bottomDialog.dismiss();
                saveData();
            }
        });

        if (values.isEmpty()) {
            LottieAnimationView empty = bottomDialog.findViewById(R.id.emptyHistory);
            Objects.requireNonNull(empty).setVisibility(View.VISIBLE);
        }

        bottomDialog.show();
    }

    private void openHelp() {
        bottomDialog = new BottomSheetDialog(this);
        bottomDialog.setContentView(R.layout.bottom_dialog_help_complex);

        bottomDialog.show();
    }

    private ArrayList<HistoryData> stringToData(ArrayList<String> array) {
        ArrayList<HistoryData> output = new ArrayList<>();

        HistoryData auxObject = new HistoryData();
        for (int i = 0; i < array.size(); i += 2) {
            auxObject.setExpression(array.get(i));
            auxObject.setResult(array.get(i + 1));
            output.add(auxObject);
            auxObject = new HistoryData();
        }

        return output;
    }

    private ArrayList<String> dataToString(ArrayList<HistoryData> array) {
        ArrayList<String> output = new ArrayList<>();

        for (int i = 0; i < array.size(); i++) {
            output.add(array.get(i).getExpression());
            output.add(array.get(i).getResult());
        }

        return output;
    }

    private void loadData() {
        try {
            SharedPreferences sharedPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE);
            Type type = new TypeToken<ArrayList<String>>() {
            }.getType();
            Gson gson = new Gson();

            String json = sharedPreferences.getString("history", null);
            historyData = stringToData(gson.fromJson(json, type));

            if(!sharedPreferences.getString("textsize", null).equals(""))
                textsize = Integer.parseInt(sharedPreferences.getString("textsize", null));

            //Toast.makeText(this, "Load.", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            //Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void saveData() {
        try {
            SharedPreferences sharedPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            Gson gson = new Gson();

            String json = gson.toJson(dataToString(historyData));
            editor.putString("history", json);
            editor.apply();

            editor.putString("textsize", String.valueOf(textsize));
            editor.apply();

            //Toast.makeText(this, "Saved", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            //Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    public void showDialog(Activity activity, String expression) {
        final Animation bounceAnim = AnimationUtils.loadAnimation(this, R.anim.bounce_in);
        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.dialog_insert_expression);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        Button noButton = dialog.findViewById(R.id.cancelDialog);
        noButton.setOnClickListener(v -> dialog.dismiss());

        Button yesButton = dialog.findViewById(R.id.yesDialog);
        yesButton.setOnClickListener(v -> {
            dialog.dismiss();
            writeText(expression);
            bottomDialog.dismiss();
            equals = false;
            calculate();
        });

        dialog.show();

        RelativeLayout dialogContainer = dialog.findViewById(R.id.dialogContainer);
        dialogContainer.startAnimation(bounceAnim);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        HistoryData itemClicked = (HistoryData) parent.getItemAtPosition(position);
        final Animation bounce = AnimationUtils.loadAnimation(this, R.anim.bounce);
        view.startAnimation(bounce);
        showDialog(this, itemClicked.getExpression());
    }

    /*------------------------------------------------------------------------*/
    // Some other methods:
    private void updateUnit() {
        Button unitBtn = findViewById(R.id.unit);
        if (degree) {
            NumplexComplex.setPolarunit(1);
            unitBtn.setText(R.string.Radians);
            degree = false;
        } else {
            unitBtn.setText(R.string.Degrees);
            NumplexComplex.setPolarunit(57.296);
            degree = true;
        }
    }

    //Converting to scientific format:
    public static String scientificFormat(String realPart, String imagPart) {
        boolean negative = false; //Avoids + -imaginary value
        boolean noRealPart = false; // Real part = 0
        if (imagPart.startsWith("-")) {
            imagPart = imagPart.replaceFirst("-", "");
            negative = true;
        }

        char[] real = realPart.toCharArray();
        char[] imag = imagPart.toCharArray();

        String numbers = "";
        String result;
        String exp;

        StringBuilder data_numbers = new StringBuilder();
        StringBuilder data_exponent = new StringBuilder();
        StringBuilder output = new StringBuilder();

        int expo = 0;
        int j = 0;
        while (j < real.length) {
            if (real[j] == 'E') {
                numbers = realPart.substring(0, j - 1);
                exp = realPart.substring(j + 1, real.length);
                data_numbers = new StringBuilder(numbers);
                data_numbers.append("x10");

                data_exponent.append(exp);
                expo = Integer.parseInt(data_exponent.toString());
                break;
            }
            j++;
        }

        if (Double.parseDouble(numbers) != 0.0) {
            output.append(data_numbers);
            output.append(exponent(expo));
        } else noRealPart = true;

        data_numbers = new StringBuilder();
        data_exponent = new StringBuilder();
        j = 0;
        while (j < imag.length) {
            if (imag[j] == 'E') {
                numbers = imagPart.substring(0, j - 1);
                exp = imagPart.substring(j + 1, imag.length);
                data_numbers = new StringBuilder(numbers);
                data_numbers.append("x10");

                data_exponent.append(exp);
                expo = Integer.parseInt(data_exponent.toString());
                break;
            }
            j++;
        }

        if (Double.parseDouble(numbers) != 0.0) {
            if (negative && !noRealPart)
                output.append(" - ");
            else if (!noRealPart)
                output.append(" + ");

            output.append(data_numbers);
            output.append(exponent(expo));
            output.append("i");
        }

        result = output.toString();
        //result = result.replace("x10⁰", "");
        result = result.replace(".000", "");

        if (noRealPart && negative)
            result = "-" + result;

        return result;
    }

    //Transforms exponent into exponential text
    public static String exponent(int value) {
        StringBuilder data = new StringBuilder();
        String power = String.valueOf((long) value);

        int len = power.length();
        for (int i = 0; i < len; i++) {
            char c = power.charAt(i);
            switch (c) {
                case '-':
                    data.append('\u207b');
                    break;
                case '1':
                    data.append('\u00b9');
                    break;
                case '2':
                    data.append('\u00b2');
                    break;
                case '3':
                    data.append('\u00b3');
                    break;
                default:
                    data.append((char) (0x2070 + (c - '0')));
                    break;
            }
        }
        return data.toString();
    }

    //Converting to engineer format:
    public static String convert(double val) {
        int PREFIX_OFFSET = 5;
        String[] PREFIX_ARRAY = {"f", "p", "n", "µ", "m", "", "k", "M", "G", "T"};

        String result;
        // If the value is zero, then simply return 0 with the correct number of dp
        if (val == 0)
            return "0";

        // If the value is negative, make it positive so the log10 works
        double posVal = (val < 0) ? -val : val;
        double log10 = Math.log10(posVal);

        // Determine how many orders of 3 magnitudes the value is
        int count = (int) Math.floor(log10 / 3);

        // Calculate the index of the prefix symbol
        int index = count + PREFIX_OFFSET;

        // Scale the value into the range 1<=val<1000
        val /= Math.pow(10, count * 3);

        if (index >= 0 && index < PREFIX_ARRAY.length) {
            // If a prefix exists use it to create the correct string
            result = String.format(Locale.ENGLISH, "%." + 3 + "f%s", val, PREFIX_ARRAY[index]);
        } else {
            // If no prefix exists just make a string of the form 000e000
            result = String.format(Locale.ENGLISH, "%." + 3 + "fe%d", val, count * 3);
        }

        result = result.replace(".000", "");
        return result;
    }

    /*------------------------------------------------------------------------*/
    //Getters and setters:
    public static String getSavedExpression() {
        return savedExpression;
    }

    public static boolean getFrequency() {
        return frequency;
    }

    public static NumplexComplex getANS() {
        return ANS;
    }

    public static void setSavedExpression(String savedExpression) {
        ComplexCalculatorActivity.savedExpression = savedExpression;
    }

    /*------------------------------------------------------------------------*/
    // Method to check if x is a number
    private boolean isNumber(char x) {
        return (x >= '0' && x <= '9') ||
                x == '.' ||
                x == 'π';
    }

    /*--------------------------------------------------------------------------------------------*/
    //Navigation menu:
    private void setDuoNavigationDrawer() {
        drawerLayout = findViewById(R.id.drawer);

        ConstraintLayout content = findViewById(R.id.content);
        ImageButton navigationButton = findViewById(R.id.nav);

        DuoDrawerSetter drawerSetter = new DuoDrawerSetter(drawerLayout, this);
        drawerSetter.setDuoNavigationDrawer(this, navigationButton, content);
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen())
            drawerLayout.closeDrawer();
        else
            super.onBackPressed();
    }
}