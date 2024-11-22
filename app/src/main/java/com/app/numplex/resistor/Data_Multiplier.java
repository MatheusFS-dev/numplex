package com.app.numplex.resistor;

import android.content.res.Resources;

import com.app.numplex.R;

import java.util.ArrayList;
import java.util.List;

public class Data_Multiplier {
    private static Resources res;

    public static void setRes(Resources res) {
        Data_Multiplier.res = res;
    }

    public static List<Colors> getColorList() {
        List<Colors> colorsList = new ArrayList<>();

        Colors Black = new Colors();
        Black.setName(res.getString(R.string.Black_1));
        Black.setImage(R.drawable.spinner_icon_black);
        colorsList.add(Black);

        Colors Brown = new Colors();
        Brown.setName(res.getString(R.string.Brown_10));
        Brown.setImage(R.drawable.spinner_icon_brown);
        colorsList.add(Brown);

        Colors Red = new Colors();
        Red.setName(res.getString(R.string.Red_100));
        Red.setImage(R.drawable.spinner_icon_red);
        colorsList.add(Red);

        Colors Orange = new Colors();
        Orange.setName(res.getString(R.string.Orange_1k));
        Orange.setImage(R.drawable.spinner_icon_orange);
        colorsList.add(Orange);

        Colors Yellow = new Colors();
        Yellow.setName(res.getString(R.string.Yellow_10k));
        Yellow.setImage(R.drawable.spinner_icon_yellow);
        colorsList.add(Yellow);

        Colors Green = new Colors();
        Green.setName(res.getString(R.string.Green_100k));
        Green.setImage(R.drawable.spinner_icon_green);
        colorsList.add(Green);

        Colors Blue = new Colors();
        Blue.setName(res.getString(R.string.Blue_1M));
        Blue.setImage(R.drawable.spinner_icon_blue);
        colorsList.add(Blue);

        Colors Violet = new Colors();
        Violet.setName(res.getString(R.string.Violet_10M));
        Violet.setImage(R.drawable.spinner_icon_violet);
        colorsList.add(Violet);

        Colors Grey = new Colors();
        Grey.setName(res.getString(R.string.Grey_100M));
        Grey.setImage(R.drawable.spinner_icon_grey);
        colorsList.add(Grey);

        Colors White = new Colors();
        White.setName(res.getString(R.string.White_1G));
        White.setImage(R.drawable.spinner_icon_white);
        colorsList.add(White);

        Colors Gold = new Colors();
        Gold.setName(res.getString(R.string.Gold_01));
        Gold.setImage(R.drawable.spinner_icon_gold);
        colorsList.add(Gold);

        Colors Silver = new Colors();
        Silver.setName(res.getString(R.string.Silver_001));
        Silver.setImage(R.drawable.spinner_icon_silver);
        colorsList.add(Silver);

        return colorsList;
    }

}
