package com.app.numplex.matrix_complex;

import org.apache.commons.math3.complex.Complex;

public class ComplexMatrix {
    private String name;
    Complex[][] values;
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

    public Complex[][] getValues() {
        return values;
    }

    public void setValues(Complex[][] values) {
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