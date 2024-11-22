package com.app.numplex.resistor;

import android.content.res.Resources;

import com.app.numplex.R;

import java.util.ArrayList;
import java.util.List;

public class Data_firstBand {
    private static Resources res;

    public static void setRes(Resources res) {
        Data_firstBand.res = res;
    }

    public static List<Colors> getColorList() {
        List<Colors> colorsList = new ArrayList<>();

        Colors Brown = new Colors();
        Brown.setName(res.getString(R.string.Brown_1));
        Brown.setImage(R.drawable.spinner_icon_brown);
        colorsList.add(Brown);

        Colors Red = new Colors();
        Red.setName(res.getString(R.string.Red_2));
        Red.setImage(R.drawable.spinner_icon_red);
        colorsList.add(Red);

        Colors Orange = new Colors();
        Orange.setName(res.getString(R.string.Orange_3));
        Orange.setImage(R.drawable.spinner_icon_orange);
        colorsList.add(Orange);

        Colors Yellow = new Colors();
        Yellow.setName(res.getString(R.string.Yellow_4));
        Yellow.setImage(R.drawable.spinner_icon_yellow);
        colorsList.add(Yellow);

        Colors Green = new Colors();
        Green.setName(res.getString(R.string.Green_5));
        Green.setImage(R.drawable.spinner_icon_green);
        colorsList.add(Green);

        Colors Blue = new Colors();
        Blue.setName(res.getString(R.string.Blue_6));
        Blue.setImage(R.drawable.spinner_icon_blue);
        colorsList.add(Blue);

        Colors Violet = new Colors();
        Violet.setName(res.getString(R.string.Violet_7));
        Violet.setImage(R.drawable.spinner_icon_violet);
        colorsList.add(Violet);

        Colors Grey = new Colors();
        Grey.setName(res.getString(R.string.Grey_8));
        Grey.setImage(R.drawable.spinner_icon_grey);
        colorsList.add(Grey);

        Colors White = new Colors();
        White.setName(res.getString(R.string.White_9));
        White.setImage(R.drawable.spinner_icon_white);
        colorsList.add(White);

        return colorsList;
    }

}
