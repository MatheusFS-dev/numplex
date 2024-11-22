package com.app.numplex.calculator;

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
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import com.airbnb.lottie.LottieAnimationView;
import com.app.numplex.R;
import com.app.numplex.application.AppRater;
import com.app.numplex.complex_calculator.HistoryData;
import com.app.numplex.complex_calculator.HistoryListAdapter;
import com.app.numplex.utils.Animations;
import com.app.numplex.utils.DuoDrawerSetter;
import com.app.numplex.utils.Functions;
import com.app.numplex.utils.HistoryAnimationHelper;
import com.app.numplex.utils.LottieDialog;
import com.app.numplex.utils.MultipleClickListener;
import com.app.numplex.utils.RepeatListener;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import nl.psdcompany.duonavigationdrawer.views.DuoDrawerLayout;

public class CalculatorActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {
    private DuoDrawerLayout drawerLayout;
    private static int textsize = 24; //Text size

    private boolean degree = true; //Auxiliary variable
    private boolean equals = false; //Auxiliary variable

    private static String ANS = null; //Saves last result

    private EditText textViewOut;
    private EditText textView;

    private static String savedExpression = ""; //Auxiliary variable

    private ArrayList<HistoryData> historyData = new ArrayList<>();
    public static boolean isHistoryAnimationEnabled = false;
    private BottomSheetDialog bottomDialog;

    private boolean _2nd = false;

    private final List<String> words = Arrays.asList("atan(", "asin(", "acos(", "sin(", "cos(",
            "tan(", "Ans", "log(", "log2(", "ln(");

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

        setContentView(R.layout.activity_calculator);
        loadData();

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

        RepeatListener.setBounceAnim(bounceAnim);
        //Button delete:
        Button del = findViewById(R.id.del);
        del.setOnTouchListener(new RepeatListener(400, 150, view -> CalculatorActivity.this.del()));

        //Button move left
        Button moveleft = findViewById(R.id.left);
        moveleft.setOnTouchListener(new RepeatListener(400, 150, view -> {
            textView = findViewById(R.id.txt);
            int cursorPos = textView.getSelectionStart();
            try {
                if(!Functions.skipSpecificWordBeforeCursor(textView, words))
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
            try {
                if(!Functions.skipSpecificWordAfterCursor(textView, words))
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
        if (txt.contains("-"))
            txt = txt.replace("-", "<font color=#05C2E3>-</font>");
        if (txt.contains("×"))
            txt = txt.replace("×", "<font color=#05C2E3>×</font>");
        if (txt.contains("÷"))
            txt = txt.replace("÷", "<font color=#05C2E3>÷</font>");

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
                Functions.deleteSpecificWordAfterCursor(textView, words);

                //(□)
                if (cursorPos + 2 < textLen
                        && input[cursorPos] == '('
                        && input[cursorPos + 1] == '□'
                        && input[cursorPos + 2] == ')') {
                    selection.replace(cursorPos, cursorPos + 3, "");
                    textView.setText(selection);
                    textView.setSelection(cursorPos);
                }
                else {
                    selection.replace(0, 1, "");
                }
                textView.setText(selection);
                textView.setSelection(0);
            }

            /*------------------------------------------------------------------------------------*/
            //Deleting specific words:
            Functions.deleteWholeWordIfCursorAtSpecificPosition(textView, "asin(□)", 4);
            Functions.deleteWholeWordIfCursorAtSpecificPosition(textView, "acos(□)", 4);
            Functions.deleteWholeWordIfCursorAtSpecificPosition(textView, "atan(□)", 4);
            Functions.deleteWholeWordIfCursorAtSpecificPosition(textView, "sin(□)", 3);
            Functions.deleteWholeWordIfCursorAtSpecificPosition(textView, "cos(□)", 3);
            Functions.deleteWholeWordIfCursorAtSpecificPosition(textView, "tan(□)", 3);
            Functions.deleteWholeWordIfCursorAtSpecificPosition(textView, "log(□)", 3);
            Functions.deleteWholeWordIfCursorAtSpecificPosition(textView, "log2(□)", 4);
            Functions.deleteWholeWordIfCursorAtSpecificPosition(textView, "ln(□)", 2);
            Functions.deleteSpecificWordBeforeCursor(textView, words);

            //Deleting (□) and any other characters:
            //(|□)
            if ((cursorPos - 1 >= 0 && cursorPos + 1 < textLen) &&
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
                textView.setSelection(cursorPos - 1);
            }
            //(□)|
            else if (cursorPos - 3 >= 0 && input[cursorPos - 3] == '(' && input[cursorPos - 2] == '□' && input[cursorPos - 1] == ')') {
                selection.replace(cursorPos - 2, cursorPos, "");
                textView.setText(selection);
                textView.setSelection(cursorPos - 2);
            }
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
            //Any char:
            else if (textLen != 0) {
                selection.replace(cursorPos - 1, cursorPos, "");
                textView.setText(selection);
                textView.setSelection(cursorPos - 1);
            }
            /*------------------------------------------------------------------------------------*/
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

        //***(□)
        else if (string.equals("sin(<font color=#9A9A9A>□</font>)") ||
                string.equals("cos(<font color=#9A9A9A>□</font>)") ||
                string.equals("tan(<font color=#9A9A9A>□</font>)") ||
                string.equals("log(<font color=#9A9A9A>□</font>)") ||
                string.equals("asin(<font color=#9A9A9A>□</font>)") ||
                string.equals("acos(<font color=#9A9A9A>□</font>)") ||
                string.equals("atan(<font color=#9A9A9A>□</font>)") ||
                string.equals("log2(<font color=#9A9A9A>□</font>)") ||
                string.equals("ln(<font color=#9A9A9A>□</font>)"))
            textView.setSelection(textView.getSelectionStart() - 2);
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
                    || input[cursorPos - 1] == '÷' || input[cursorPos - 1] == '^') {
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
        equals = false;

        try {
            Functions.isCursorBetweenSpecificWords(textView, words);
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
                result = CalculatorLogic.evaluate(textView.getText().toString());
                textViewOut.setText(result);
            } else
                textViewOut.setText(null);

            CalculatorActivity.setSavedExpression(textView.getText().toString());
        } catch (Exception ignored) {
            textViewOut.setText(null);
        }
    }

    @SuppressLint("SetTextI18n")
    private void set2nd(){
        Button sin = findViewById(R.id.sin);
        Button cos = findViewById(R.id.cos);
        Button tan = findViewById(R.id.tan);

        if(_2nd){
            sin.setText("asin");
            cos.setText("acos");
            tan.setText("atan");
        }
        else{
            sin.setText("sin");
            cos.setText("cos");
            tan.setText("tan");
        }
    }

    /*------------------------------------------------------------------------*/
    // Buttons:
    public void buttonPressed(View view) {
        final Animation bounceAnim = AnimationUtils.loadAnimation(this, R.anim.bounce);
        textView = findViewById(R.id.txt);
        textViewOut = findViewById(R.id.txtOut);
        char[] input = textView.getText().toString().toCharArray();

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
        }
        else if (id == R.id.closingParentheses)
            writeText(")");
        else if (id == R.id.pi)
            writeText("π");
        else if (id == R.id.shift) {
            _2nd = !_2nd;
            set2nd();
        }
        else if (id == R.id.exponential) {
            operatorPressed();
            writeText("^");
        }
        else if (id == R.id.percentage) {
            operatorPressed();
            writeText("%");
        }
        else if (id == R.id.radical) {
            writeText("√");
        }
        else if (id == R.id.log)
            writeText("log(□)");
        else if (id == R.id.log2)
            writeText("log2(□)");
        else if (id == R.id.ln)
            writeText("ln(□)");
        else if (id == R.id.sin) {
            if(!_2nd)
                writeText("sin(□)");
            else
                writeText("asin(□)");
        }
        else if (id == R.id.cos){
            if(!_2nd)
                writeText("cos(□)");
            else
                writeText("acos(□)");
        }
        else if (id == R.id.tan){
            if(!_2nd)
                writeText("tan(□)");
            else
                writeText("atan(□)");
        }
        else if (id == R.id.cubicRadical)
            writeText("\u221B");
        else if (id == R.id.inverse)
            writeText("^-1");
        else if (id == R.id.factorial)
            writeText("!");
        else if (id == R.id.division) {
            operatorPressed();
            writeText("÷");
        }
        else if (id == R.id.multiplication) {
            operatorPressed();
            writeText("×");
        }
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
        }
        else if (id == R.id.seven)
            writeText("7");
        else if (id == R.id.eight)
            writeText("8");
        else if (id == R.id.nine)
            writeText("9");
        else if (id == R.id.subtraction)
            writeText("-");
        else if (id == R.id.four)
            writeText("4");
        else if (id == R.id.five)
            writeText("5");
        else if (id == R.id.six)
            writeText("6");
        else if (id == R.id.plus) {
            operatorPressed();
            writeText("+");
        }
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
        else if (id == R.id.E)
            writeText("E");
        else if (id == R.id.ans) {
            if (ANS != null)
                writeText("Ans");
        }
        else if (id == R.id.zoomin) {
            if (textsize != 48) {
                textsize += 4;
                textView.setTextSize(textsize);
                textViewOut.setTextSize(textsize+4);
                saveData();
            }
        }
        else if (id == R.id.zoomout) {
            if (textsize != 8) {
                textsize -= 4;
                textView.setTextSize(textsize);
                textViewOut.setTextSize(textsize+4);
                saveData();
            }
        }
        else if (id == R.id.unit)
            updateUnit();

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
                            aux = aux.replace("Ans", ANS);
                        CalculatorActivity.setSavedExpression(aux);
                    }
                    ANS = textViewOut.getText().toString();
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
            Animation shake = AnimationUtils.loadAnimation(CalculatorActivity.this, R.anim.shake);
            textViewOut.startAnimation(shake);
        }
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
            PopupMenu popup = new PopupMenu(CalculatorActivity.this, v);
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

            String json = sharedPreferences.getString("history_calculator", null);
            historyData = stringToData(gson.fromJson(json, type));

            if(!sharedPreferences.getString("textsize_calculator", null).equals(""))
                textsize = Integer.parseInt(sharedPreferences.getString("textsize_calculator", null));

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
            editor.putString("history_calculator", json);
            editor.apply();

            editor.putString("textsize_calculator", String.valueOf(textsize));
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
            unitBtn.setText(R.string.Radians);
            CalculatorLogic.setRad(true);
            degree = false;
        } else {
            unitBtn.setText(R.string.Degrees);
            CalculatorLogic.setRad(false);
            degree = true;
        }
        calculate();
    }

    /*------------------------------------------------------------------------*/
    //Getters and setters:
    public static void setSavedExpression(String savedExpression) {
        CalculatorActivity.savedExpression = savedExpression;
    }

    public static String getANS() {
        return ANS;
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