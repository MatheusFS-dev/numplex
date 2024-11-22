package com.app.numplex.resistor;

import android.content.res.Resources;

import com.app.numplex.R;

import java.util.ArrayList;
import java.util.List;

public class Data_temperature {
    private static Resources res;

    public static void setRes(Resources res) {
        Data_temperature.res = res;
    }

    public static List<Colors> getColorList() {
        List<Colors> colorsList = new ArrayList<>();

        Colors Black = new Colors();
        Black.setName(res.getString(R.string.Black_temp));
        Black.setImage(R.drawable.spinner_icon_black);
        colorsList.add(Black);

        Colors Brown = new Colors();
        Brown.setName(res.getString(R.string.Brown_temp));
        Brown.setImage(R.drawable.spinner_icon_brown);
        colorsList.add(Brown);

        Colors Red = new Colors();
        Red.setName(res.getString(R.string.Red_temp));
        Red.setImage(R.drawable.spinner_icon_red);
        colorsList.add(Red);

        Colors Orange = new Colors();
        Orange.setName(res.getString(R.string.Orange_temp));
        Orange.setImage(R.drawable.spinner_icon_orange);
        colorsList.add(Orange);

        Colors Yellow = new Colors();
        Yellow.setName(res.getString(R.string.Yellow_temp));
        Yellow.setImage(R.drawable.spinner_icon_yellow);
        colorsList.add(Yellow);

        Colors Green = new Colors();
        Green.setName(res.getString(R.string.Green_temp));
        Green.setImage(R.drawable.spinner_icon_green);
        colorsList.add(Green);

        Colors Blue = new Colors();
        Blue.setName(res.getString(R.string.Blue_temp));
        Blue.setImage(R.drawable.spinner_icon_blue);
        colorsList.add(Blue);

        Colors Violet = new Colors();
        Violet.setName(res.getString(R.string.Violet_temp));
        Violet.setImage(R.drawable.spinner_icon_violet);
        colorsList.add(Violet);

        Colors Grey = new Colors();
        Grey.setName(res.getString(R.string.Grey_temp));
        Grey.setImage(R.drawable.spinner_icon_grey);
        colorsList.add(Grey);

        return colorsList;
    }

}
