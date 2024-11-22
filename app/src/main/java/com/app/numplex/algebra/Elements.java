package com.app.numplex.algebra;

public class Elements {
    private String name;
    private String type;
    double[] values;
    private boolean isChecked = false;
    private String selectionNumber;

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public double[] getValues() {
        return values;
    }

    public void setValues(double[] values) {
        this.values = values;
    }

    public String getSelectionNumber() {
        return selectionNumber;
    }

    public void setSelectionNumber(String selectionNumber) {
        this.selectionNumber = selectionNumber;
    }
}
