package com.app.numplex.matrix;

public class Matrix {
    private String name;
    double[][] values;
    private boolean isChecked = false;
    private String selectionNumber;
    private int rows;
    private int col;

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

    public double[][] getValues() {
        return values;
    }

    public void setValues(double[][] values) {
        this.values = values;
    }

    public String getSelectionNumber() {
        return selectionNumber;
    }

    public void setSelectionNumber(String selectionNumber) {
        this.selectionNumber = selectionNumber;
    }

    public int getRows() {
        return rows;
    }

    public void setRows(int rows) {
        this.rows = rows;
    }

    public int getCol() {
        return col;
    }

    public void setCol(int col) {
        this.col = col;
    }
}