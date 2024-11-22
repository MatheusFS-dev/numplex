package com.app.numplex.resistor;

import android.content.res.Resources;

import com.app.numplex.R;

import java.util.ArrayList;
import java.util.List;

public class Data_Tolerance {
    private static Resources res;

    public static void setRes(Resources res) {
        Data_Tolerance.res = res;
    }

    public static List<Colors> getColorList() {
        List<Colors> colorsList = new ArrayList<>();

        Colors Brown = new Colors();
        Brown.setName(res.getString(R.string.Brown_tol));
        Brown.setImage(R.drawable.spinner_icon_brown);
        colorsList.add(Brown);

        Colors Red = new Colors();
        Red.setName(res.getString(R.string.Red_tol));
        Red.setImage(R.drawable.spinner_icon_red);
        colorsList.add(Red);

        Colors Orange = new Colors();
        Orange.setName(res.getString(R.string.Orange_tol));
        Orange.setImage(R.drawable.spinner_icon_orange);
        colorsList.add(Orange);

        Colors Yellow = new Colors();
        Yellow.setName(res.getString(R.string.Yellow_tol));
        Yellow.setImage(R.drawable.spinner_icon_yellow);
        colorsList.add(Yellow);

        Colors Green = new Colors();
        Green.setName(res.getString(R.string.Green_tol));
        Green.setImage(R.drawable.spinner_icon_green);
        colorsList.add(Green);

        Colors Blue = new Colors();
        Blue.setName(res.getString(R.string.Blue_tol));
        Blue.setImage(R.drawable.spinner_icon_blue);
        colorsList.add(Blue);

        Colors Violet = new Colors();
        Violet.setName(res.getString(R.string.Violet_tol));
        Violet.setImage(R.drawable.spinner_icon_violet);
        colorsList.add(Violet);

        Colors Grey = new Colors();
        Grey.setName(res.getString(R.string.Grey_tol));
        Grey.setImage(R.drawable.spinner_icon_grey);
        colorsList.add(Grey);

        Colors Gold = new Colors();
        Gold.setName(res.getString(R.string.Gold_tol));
        Gold.setImage(R.drawable.spinner_icon_gold);
        colorsList.add(Gold);

        Colors Silver = new Colors();
        Silver.setName(res.getString(R.string.Silver_tol));
        Silver.setImage(R.drawable.spinner_icon_silver);
        colorsList.add(Silver);

        Colors None = new Colors();
        None.setName(res.getString(R.string.None_tol));
        None.setImage(R.drawable.spinner_icon_none);
        colorsList.add(None);

        return colorsList;
    }

}
