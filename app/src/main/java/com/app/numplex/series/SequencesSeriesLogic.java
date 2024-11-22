package com.app.numplex.series;

public class SequencesSeriesLogic {
    private final String arithmetic_sequence;
    private final String geometric_sequence;
    private final String constant_sequence;
    private final String ascending_sequence;
    private final String descending_sequence;
    private final String oscillating_sequence;

    public SequencesSeriesLogic(String arithmetic_sequence, String geometric_sequence, String constant_sequence, String ascending_sequence, String descending_sequence, String oscillating_sequence) {
        this.arithmetic_sequence = arithmetic_sequence;
        this.geometric_sequence = geometric_sequence;
        this.constant_sequence = constant_sequence;
        this.ascending_sequence = ascending_sequence;
        this.descending_sequence = descending_sequence;
        this.oscillating_sequence = oscillating_sequence;
    }

    public String identifySequence(double[] seq) {
        if (seq.length < 2)
            return null; // Error

        double d = seq[1] - seq[0];
        boolean isAritmetica = true;
        for (int i = 2; i < seq.length; i++) {
            if (seq[i] - seq[i - 1] != d) {
                isAritmetica = false;
                break;
            }
        }

        if (isAritmetica) {
            return arithmetic_sequence + " " + d + "\n" + seq[0] + " + " + d + " · (n - 1)";
        } else {
            double r = seq[1] / seq[0];
            boolean isGeometrica = true;
            for (int i = 2; i < seq.length; i++) {
                if (seq[i] / seq[i - 1] != r) {
                    isGeometrica = false;
                    break;
                }
            }

            if (isGeometrica) {
                return geometric_sequence + " " + r + "\n" + seq[0] + " · " + r + "^(n - 1)";
            }
            double direction = 0;
            for (int i = 1; i < seq.length - 1; i++) {
                double diff = seq[i + 1] - seq[i];
                if (diff > 0) {
                    if (direction == 0) {
                        direction = 1;
                    } else if (direction == -1) {
                        direction = 2;
                        break;
                    }
                } else if (diff < 0) {
                    if (direction == 0) {
                        direction = -1;
                    } else if (direction == 1) {
                        direction = 2;
                        break;
                    }
                }
            }

            if (direction == 0) {
                return constant_sequence;
            } else if (direction == 1) {
                return ascending_sequence;
            } else if (direction == -1) {
                return descending_sequence;
            } else {
                return oscillating_sequence;
            }
        }
    }
}
