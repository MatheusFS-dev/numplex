package com.app.numplex.utils;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.SpannableStringBuilder;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import com.app.numplex.R;
import com.app.numplex.complex_calculator.ComplexLogic;
import com.app.numplex.complex_calculator.NumplexComplex;

import org.apache.commons.math3.complex.Complex;

import java.util.Arrays;
import java.util.List;

public class Dialogs {

    public static void showPremiumDialog(Context context) {
        final Animation bounceAnim = AnimationUtils.loadAnimation(context, R.anim.bounce);
        final Dialog dialog = new Dialog(context, R.style.DialogAnimation);

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.dialog_premium_feature);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        Button button = dialog.findViewById(R.id.tryAgain);

        button.setOnClickListener(view -> {
            button.startAnimation(bounceAnim);
            dialog.dismiss();
            //TODO: premium app
        });

        dialog.show();
    }
    
    @SuppressLint("ClickableViewAccessibility")
    public static void showComplexKeyboard(Context context, OnComplexSaveClickListener onComplexSaveClickListener) {
        final Animation bounceAnim = AnimationUtils.loadAnimation(context, R.anim.bounce);
        final Dialog dialog = new Dialog(context, R.style.DialogAnimation);
        final boolean[] degree = {false};

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.activity_mini_calc_complex);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        EditText textView = dialog.findViewById(R.id.txt);
        textView.setShowSoftInputOnFocus(false);

        //Button delete:
        Button del = dialog.findViewById(R.id.del);
        del.setOnTouchListener(new RepeatListener(400, 150, view -> del(textView)));

        //Back
        TextView back = dialog.findViewById(R.id.back);
        back.setOnClickListener(view -> dialog.dismiss());

        //Unit
        TextView unit = dialog.findViewById(R.id.unit);
        unit.setOnClickListener(v -> {
            degree[0] = !degree[0];
            updateUnit(unit, degree[0]);
        });
        updateUnit(unit, degree[0]);

        //Other buttons:
        Button negative = dialog.findViewById(R.id.negative);
        Button complexNeg = dialog.findViewById(R.id.complexNeg);
        Button plus = dialog.findViewById(R.id.plus);
        Button multiplication = dialog.findViewById(R.id.multiplication);
        Button division = dialog.findViewById(R.id.division);
        Button polar = dialog.findViewById(R.id.polar);
        Button imag = dialog.findViewById(R.id.i);
        Button seven = dialog.findViewById(R.id.seven);
        Button eight = dialog.findViewById(R.id.eight);
        Button nine = dialog.findViewById(R.id.nine);
        Button four = dialog.findViewById(R.id.four);
        Button five = dialog.findViewById(R.id.five);
        Button six = dialog.findViewById(R.id.six);
        Button one = dialog.findViewById(R.id.one);
        Button two = dialog.findViewById(R.id.two);
        Button three = dialog.findViewById(R.id.three);
        Button zero = dialog.findViewById(R.id.zero);
        Button point = dialog.findViewById(R.id.point);
        Button AC = dialog.findViewById(R.id.AC);
        ImageButton save = dialog.findViewById(R.id.save);

        List<Button> buttonList = Arrays.asList(negative, multiplication, division, polar, plus, complexNeg, imag, one, two, three, four, five,
                six, seven, eight, nine, zero, point, AC);

        for (int i = 0; i < buttonList.size(); i++) {
            Button currentButton = buttonList.get(i);

            currentButton.setOnClickListener(v -> buttonPressed(currentButton, textView, context, dialog));
        }

        save.setOnClickListener(v -> {
            save.startAnimation(bounceAnim);
            if(onComplexSaveClickListener != null){
                try {
                    if(!textView.getText().toString().equals("")) {
                        /* ------------------------------------------------------------------------------- */
                        // From matrixComplex:
                        double real = ComplexLogic.evaluate(textView.getText().toString()).getReal();
                        double img = ComplexLogic.evaluate(textView.getText().toString()).getImaginary();

                        Complex value = Complex.valueOf(real, img);

                        onComplexSaveClickListener.onSaveClickListener(value);

                        //Toast.makeText(context, context.getString(R.string.saved), Toast.LENGTH_SHORT).show();
                    }
                    /* ------------------------------------------------------------------------------- */
                } catch (Exception ignored) {
                    Toast.makeText(context, context.getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
                }
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    @SuppressLint("ClickableViewAccessibility")
    public static void showKeyboard(Context context, OnSaveClickListener onSaveClickListener) {
        final Animation bounceAnim = AnimationUtils.loadAnimation(context, R.anim.bounce);
        final Dialog dialog = new Dialog(context, R.style.DialogAnimation);

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.activity_mini_calc);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        EditText textView = dialog.findViewById(R.id.txt);
        textView.setShowSoftInputOnFocus(false);

        //Button delete:
        Button del = dialog.findViewById(R.id.del);
        del.setOnTouchListener(new RepeatListener(400, 150, view -> del(textView)));

        //Back
        TextView back = dialog.findViewById(R.id.back);
        back.setOnClickListener(view -> dialog.dismiss());

        //Other buttons:
        Button complexNeg = dialog.findViewById(R.id.complexNeg);
        Button multiplication = dialog.findViewById(R.id.multiplication);
        Button division = dialog.findViewById(R.id.division);
        Button seven = dialog.findViewById(R.id.seven);
        Button eight = dialog.findViewById(R.id.eight);
        Button nine = dialog.findViewById(R.id.nine);
        Button four = dialog.findViewById(R.id.four);
        Button five = dialog.findViewById(R.id.five);
        Button six = dialog.findViewById(R.id.six);
        Button one = dialog.findViewById(R.id.one);
        Button two = dialog.findViewById(R.id.two);
        Button three = dialog.findViewById(R.id.three);
        Button zero = dialog.findViewById(R.id.zero);
        Button point = dialog.findViewById(R.id.point);
        Button AC = dialog.findViewById(R.id.AC);
        ImageButton save = dialog.findViewById(R.id.save);

        List<Button> buttonList = Arrays.asList(complexNeg, multiplication, division, one, two, three, four, five,
                six, seven, eight, nine, zero, point, AC);

        for (int i = 0; i < buttonList.size(); i++) {
            Button currentButton = buttonList.get(i);

            currentButton.setOnClickListener(v -> buttonPressed(currentButton, textView, context, dialog));
        }

        save.setOnClickListener(v -> {
            save.startAnimation(bounceAnim);
            if(onSaveClickListener != null){
                try {
                    if(!textView.getText().toString().equals("")) {
                        /* ------------------------------------------------------------------------------- */
                        onSaveClickListener.onSaveClickListener(ComplexLogic.evaluate(textView.getText().toString()).toString());

                        //Toast.makeText(context, context.getString(R.string.saved), Toast.LENGTH_SHORT).show();
                    }
                    /* ------------------------------------------------------------------------------- */
                } catch (Exception ignored) {
                    Toast.makeText(context, context.getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
                }
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    //Delete button method:
    private static void del(EditText textView) {
        int cursorPos = textView.getSelectionStart();
        int textLen = textView.length();
        SpannableStringBuilder selection = (SpannableStringBuilder) textView.getText();

        try {
            //Deleting cases:
            if (cursorPos == 0 && textLen != 0) {
                selection.replace(0, 1, "");
                textView.setText(selection);
                textView.setSelection(0);
            }
            else if (textLen != 0) {
                selection.replace(cursorPos - 1, cursorPos, "");
                textView.setText(selection);
                textView.setSelection(cursorPos - 1);
            }
            /*------------------------------------------------------------------------*/
        } catch (Exception ignored) {}
    }

    // Buttons:
    private static void buttonPressed(View view, EditText textView, Context context, Dialog dialog) {
        final Animation bounceAnim = AnimationUtils.loadAnimation(context, R.anim.bounce);



        view.startAnimation(bounceAnim);
        int id = view.getId();
        if (id == R.id.negative)
            writeText("﹣", textView);
        else if (id == R.id.complexNeg)
            writeText("-", textView);
        else if (id == R.id.polar)
            writeText("∠", textView);
        else if (id == R.id.multiplication)
            writeText("×", textView);
        else if (id == R.id.division)
            writeText("÷", textView);
        else if (id == R.id.plus)
            writeText("+", textView);
        else if (id == R.id.i)
            writeText("i", textView);
        else if (id == R.id.seven)
            writeText("7", textView);
        else if (id == R.id.eight)
            writeText("8", textView);
        else if (id == R.id.nine)
            writeText("9", textView);
        else if (id == R.id.four)
            writeText("4", textView);
        else if (id == R.id.five)
            writeText("5", textView);
        else if (id == R.id.six)
            writeText("6", textView);
        else if (id == R.id.one)
            writeText("1", textView);
        else if (id == R.id.two)
            writeText("2", textView);
        else if (id == R.id.three)
            writeText("3", textView);
        else if (id == R.id.zero)
            writeText("0", textView);
        else if (id == R.id.point)
            writeText(".", textView);
        else if (id == R.id.AC) {
            if(!textView.getText().toString().equals("")) {
                final View myView = dialog.findViewById(R.id.linearLayout3);
                final int originalColor = ContextCompat.getColor(context, R.color.secondary_background_color);
                final int newColor = ContextCompat.getColor(context, R.color.wave_effect);

                Animations.clearAnimation(myView, originalColor, newColor);
            }

            textView.setText(null);
            textView.setSelection(textView.getText().length());
        }
    }

    public static void writeText(String string, EditText textView) {
        int cursorPos = textView.getSelectionStart();
        textView.getText().insert(cursorPos, string);
    }

    private static void updateUnit(TextView unitBtn, boolean degree) {
        if (degree) {
            NumplexComplex.setPolarunit(1);
            unitBtn.setText(R.string.Radians);
        } else {
            unitBtn.setText(R.string.Degrees);
            NumplexComplex.setPolarunit(57.296);
        }
    }

    public interface OnComplexSaveClickListener {
        void onSaveClickListener(Complex value);
    }

    public interface OnSaveClickListener {
        void onSaveClickListener(String value);
    }
}
