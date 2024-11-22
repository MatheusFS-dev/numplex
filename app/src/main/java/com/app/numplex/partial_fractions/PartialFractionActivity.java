package com.app.numplex.partial_fractions;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.view.MenuInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.app.numplex.R;
import com.app.numplex.application.AppRater;
import com.app.numplex.application.AppStatus;
import com.app.numplex.complex_calculator.HistoryData;
import com.app.numplex.utils.DuoDrawerSetter;
import com.app.numplex.utils.Functions;
import com.app.numplex.utils.RepeatListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import nl.psdcompany.duonavigationdrawer.views.DuoDrawerLayout;

public class PartialFractionActivity extends AppCompatActivity {
    private DuoDrawerLayout drawerLayout;
    private static int textsize = 24; //Text size
    private boolean equals = false; //Auxiliary variable

    private static String ANS = null; //Saves last result

    private EditText textViewOut;
    private EditText currentText;
    private EditText numerator;
    private EditText denominator;

    private static final String[] savedExpressions = new String[2]; //Auxiliary variable

    private ArrayList<HistoryData> historyData = new ArrayList<>();

    private final List<String> words = List.of("Ans");

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

        setContentView(R.layout.activity_partial_fractions);
        AppStatus.checkAccess(this);
        loadData();

        textViewOut = findViewById(R.id.txtOut);
        textViewOut.setKeyListener(null);
        textViewOut.setTextSize(textsize+4);

        numerator = findViewById(R.id.numerator);
        numerator.setShowSoftInputOnFocus(false);
        numerator.setText(savedExpressions[0]);
        numerator.setSelection(numerator.length());
        numerator.setTextSize(textsize);

        denominator = findViewById(R.id.denominator);
        denominator.setShowSoftInputOnFocus(false);
        denominator.setText(savedExpressions[1]);
        denominator.setSelection(numerator.length());
        denominator.setTextSize(textsize);

        currentText = numerator;

        highlightParentheses(currentText);
        calculate();

        /*------------------------------------------------------------------------*/
        //Navigation drawer
        setDuoNavigationDrawer();
        /*------------------------------------------------------------------------*/

        //Checking cursor position:
        setupOnFocusChangeListener(numerator);
        setupOnFocusChangeListener(denominator);

        final Animation bounceAnim = AnimationUtils.loadAnimation(this, R.anim.bounce);
        //Calling navigation window:
        ImageButton navigationButton = findViewById(R.id.nav);
        navigationButton.setOnClickListener(v -> {
            if (drawerLayout.isDrawerOpen())
                drawerLayout.closeDrawer();
            else
                drawerLayout.openDrawer();
        });

        RepeatListener.setBounceAnim(bounceAnim);
        //Button delete:
        Button del = findViewById(R.id.del);
        del.setOnTouchListener(new RepeatListener(400, 150, view -> PartialFractionActivity.this.del(currentText)));

        //Button move left
        Button moveleft = findViewById(R.id.left);
        moveleft.setOnTouchListener(new RepeatListener(400, 150, view -> {
            int cursorPos = currentText.getSelectionStart();
            try {
                if(!Functions.skipSpecificWordBeforeCursor(currentText, words))
                    currentText.setSelection(cursorPos - 1);
            } catch (Exception ignored) {
            }
            highlightParentheses(currentText);
            equals = false;
        }));

        //Button move right
        Button moveright = findViewById(R.id.right);
        moveright.setOnTouchListener(new RepeatListener(400, 150, view -> {
            int cursorPos = currentText.getSelectionStart();
            try {
                if(!Functions.skipSpecificWordAfterCursor(currentText, words))
                    currentText.setSelection(cursorPos + 1);
            } catch (Exception ignored) {
            }
            highlightParentheses(currentText);
            equals = false;
        }));

        //Input copy and paste buttons
        currentText.setOnLongClickListener(v -> {
            PopupMenu popup = new PopupMenu(this, v);
            MenuInflater inflater = popup.getMenuInflater();
            inflater.inflate(R.menu.copy_paste_menu, popup.getMenu());

            popup.setOnMenuItemClickListener(item -> {
                ClipboardManager clipboardManager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);

                int itemId = item.getItemId();
                if (itemId == R.id.copy) {
                    try {
                        //EvaluateInput.evaluate(textView.getText().toString());
                        if (!currentText.getText().toString().equals("")) {
                            ClipData clipData = ClipData.newPlainText("text", currentText.getText().toString());
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
                        writeText(data.getText().toString(), currentText);
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

    private void setupOnFocusChangeListener(EditText editText) {
        editText.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                currentText = editText;
                checkCursorPosition(editText);
            }
        });
    }


    /*------------------------------------------------------------------------*/
    //Method to highlight parentheses
    public void highlightParentheses(EditText textView) {
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
    private void del(EditText textView) {
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

        highlightParentheses(textView);
        /*------------------------------------------------------------------------*/
    }

    /*------------------------------------------------------------------------*/
    //Cursor method to set cursor position and write string:
    public void writeText(String string, EditText textView) {
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
        highlightParentheses(textView);
    }

    /*------------------------------------------------------------------------*/
    //Method called when an operator is pressed:
    public void operatorPressed(EditText textView) {
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
    public void checkCursorPosition(EditText textView) {
        equals = false;

        try {
            Functions.isCursorBetweenSpecificWords(textView, words);
        } catch (Exception ignored) {
        }
        highlightParentheses(textView);
    }

    /*------------------------------------------------------------------------*/
    //Method that keeps trying to calculate:
    public void calculate() {
        String result;
        textViewOut = findViewById(R.id.txtOut);
        numerator = findViewById(R.id.numerator);
        denominator = findViewById(R.id.denominator);

        if (textViewOut.getText().toString().equals(getResources().getString(R.string.Syntax_Error)))
            textViewOut.setText(null);

        try {
            String numeratorText = numerator.getText().toString();
            String denominatorText = denominator.getText().toString();

            if (!numeratorText.equals("") && !denominatorText.equals("")) {
                //TODO
                //result = PartialFractionDecomposition.calculatePartialFraction("(" + numeratorText + ")/("+ denominatorText + ")");
                //textViewOut.setText(result);
            } else
                textViewOut.setText(null);

            PartialFractionActivity.setSavedExpression(0, numeratorText);
            PartialFractionActivity.setSavedExpression(1, denominatorText);
        } catch (Exception ignored) {
            textViewOut.setText(null);
        }
    }

    /*------------------------------------------------------------------------*/
    // Buttons:
    public void buttonPressed(View view) {
        final Animation bounceAnim = AnimationUtils.loadAnimation(this, R.anim.bounce);
        textViewOut = findViewById(R.id.txtOut);
        numerator = findViewById(R.id.numerator);
        denominator = findViewById(R.id.denominator);
        char[] input = currentText.getText().toString().toCharArray();

        view.startAnimation(bounceAnim);
        int id = view.getId();
        if (id == R.id.openingParentheses) {
            if (currentText.getSelectionStart() == currentText.length())
                writeText("(□)", currentText);
            else {
                if (input[currentText.getSelectionStart()] == '□')
                    writeText("(□)", currentText);
                else
                    writeText("(", currentText);
            }
        }
        else if (id == R.id.closingParentheses)
            writeText(")", currentText);
        else if (id == R.id.exponential) {
            operatorPressed(currentText);
            writeText("^", currentText);
        }
        else if (id == R.id.x) {
            writeText("x", currentText);
        }
        else if (id == R.id.division) {
            operatorPressed(currentText);
            writeText("÷", currentText);
        }
        else if (id == R.id.multiplication) {
            operatorPressed(currentText);
            writeText("×", currentText);
        }
        else if (id == R.id.AC) {
            textViewOut.setText(null);
            numerator.setText(null);
            denominator.setText(null);
            currentText.setSelection(currentText.getText().length());
            equals = false;
        }
        else if (id == R.id.seven)
            writeText("7", currentText);
        else if (id == R.id.eight)
            writeText("8", currentText);
        else if (id == R.id.nine)
            writeText("9", currentText);
        else if (id == R.id.subtraction)
            writeText("-", currentText);
        else if (id == R.id.four)
            writeText("4", currentText);
        else if (id == R.id.five)
            writeText("5", currentText);
        else if (id == R.id.six)
            writeText("6", currentText);
        else if (id == R.id.plus) {
            operatorPressed(currentText);
            writeText("+", currentText);
        }
        else if (id == R.id.one)
            writeText("1", currentText);
        else if (id == R.id.two)
            writeText("2", currentText);
        else if (id == R.id.three)
            writeText("3", currentText);
        else if (id == R.id.zero)
            writeText("0", currentText);
        else if (id == R.id.point)
            writeText(".", currentText);
        else if (id == R.id.E)
            writeText("E", currentText);
        else if (id == R.id.ans) {
            if (ANS != null)
                writeText("Ans", currentText);
        }
        else if (id == R.id.zoomin) {
            if (textsize != 48) {
                textsize += 4;
                numerator.setTextSize(textsize);
                denominator.setTextSize(textsize);
                textViewOut.setTextSize(textsize+4);
                saveData();
            }
        }
        else if (id == R.id.zoomout) {
            if (textsize != 8) {
                textsize -= 4;
                numerator.setTextSize(textsize);
                denominator.setTextSize(textsize);
                textViewOut.setTextSize(textsize+4);
                saveData();
            }
        }

        calculate();
    }

    public void equals(View view) {
        final Animation bounceAnim = AnimationUtils.loadAnimation(this, R.anim.bounce);
        view.startAnimation(bounceAnim);

        numerator = findViewById(R.id.numerator);
        denominator = findViewById(R.id.denominator);
        textViewOut = findViewById(R.id.txtOut);
        try {
            String numeratorText = numerator.getText().toString();
            String denominatorText = denominator.getText().toString();

            if (!numeratorText.equals("") && !denominatorText.equals("")) {
                if (!equals) {
                    calculate();
                    if (numeratorText.contains("Ans")) {
                        numeratorText = numeratorText.replace("Ans", ANS);

                        PartialFractionActivity.setSavedExpression(0, numeratorText);
                    }

                    if (denominatorText.contains("Ans")) {
                        denominatorText = denominatorText.replace("Ans", ANS);

                        PartialFractionActivity.setSavedExpression(1, denominatorText);
                    }

                    ANS = textViewOut.getText().toString();
                    equals = true;

                    // Saving result in history
                    HistoryData auxObject = new HistoryData();
                    auxObject.setExpression(""); //TODO
                    auxObject.setResult(String.valueOf(ANS));
                    historyData.add(auxObject);
                    saveData();
                }
            }
        } catch (Exception e) {
            textViewOut.setText(R.string.Syntax_Error);
            Animation shake = AnimationUtils.loadAnimation(PartialFractionActivity.this, R.anim.shake);
            textViewOut.startAnimation(shake);
        }
    }

    /*--------------------------------------------------------------------------------------------*/

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

            String json = sharedPreferences.getString("history_partial_fractions", null);
            historyData = stringToData(gson.fromJson(json, type));

            if(!sharedPreferences.getString("textsize_partial_fractions", null).equals(""))
                textsize = Integer.parseInt(sharedPreferences.getString("textsize_partial_fractions", null));

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
            editor.putString("history_partial_fractions", json);
            editor.apply();

            editor.putString("textsize_partial_fractions", String.valueOf(textsize));
            editor.apply();

            //Toast.makeText(this, "Saved", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            //Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    /*------------------------------------------------------------------------*/
    //Getters and setters:
    public static void setSavedExpression(int index, String savedExpression) {
        if (index < 0 || index >= savedExpressions.length) {
            throw new IllegalArgumentException("Index is out of bounds for savedExpressions array");
        }
        savedExpressions[index] = savedExpression;
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