package com.app.numplex.karnaugh;

import java.util.Arrays;

public class Quine_Mccluskey {
    public static String calculate(int[] min, int vars) {
        int[] full = {0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,28,29,30,31};
        if(Arrays.equals(min, full)) return "1";
        int i;
        int j;
        int k;
        int x;
        int y;
        int pos = 0;
        int flag;
        int count;
        int c;
        int flag2 = 0;
        int c2;
        int[] dash;
        int c1;
        int c3;
        int no9;
        int nmin = min.length;
        boolean check;
        int[][] a = new int[5000][vars]; // Fixed size, make it larger if needed
        int[][] b = new int[5000][vars]; // Fixed size, make it larger if needed
        int[][] pi = new int[5000][vars]; // Fixed size, make it larger if needed
        int[] checker = new int[5000]; // Fixed size, make it larger if needed
        initialise(a, -1);//initialises the matrix a with all -1
        for (i = 0; i < nmin; i++)
            for (j = 0; j < vars; j++)
                a[i][j] = 0;
        //store binary form of each minterm in matrix a[][]
        for (i = 0; i < nmin; i++) {
            x = min[i];
            pos = vars - 1;
            while (x > 0) {
                a[i][pos] = x % 2;
                pos--;
                x /= 2;
            }
        }
        ////System.out.println("The minterms entered :-");
        // for (i = 0; i < nmin; i++) {
        //     System.out.print(min[i] + " ");
        // }
        //System.out.println("\n----------------------------------------------------------------------------------------------------");
        while (true) {
            count = 0;
            flag = 0;// flag is row contrller for matrix b
            initialise(b, -1);//creating new empty matrix b at the beginning of every pass
            initialise_single(checker, -1);
            for (i = 0; i < a.length; i++) {
                if (a[i][0] == -1)
                    break;
                for (j = i + 1; j < a.length; j++) {
                    c = 0;
                    if (a[j][0] == -1)
                        break;
                    for (k = vars - 1; k >= 0; k--)
                        if (a[i][k] != a[j][k]) {
                            pos = k;
                            c++;
                        }
                    if (c == 1) {
                        count++;
                        checker[i]++;
                        checker[j]++;
                        for (k = vars - 1; k >= 0; k--)
                            b[flag][k] = a[i][k];
                        b[flag][pos] = 9;
                        flag++;
                    }
                }
            }
            for (j = 0; j < i; j++) {
                if (checker[j] == -1) {
                    for (k = 0; k < vars; k++)
                        pi[flag2][k] = a[j][k];
                    c3 = 0;
                    //now we will check if there is any repetation of pi s ; if repetation is found we will ignore
                    for (x = flag2 - 1; x >= 0; x--) {
                        c1 = 0;
                        for (y = 0; y < vars; y++) {
                            if (pi[x][y] != pi[flag2][y])
                                c1++;
                        }
                        if (c1 == 0) {
                            c3++;
                            break;
                        }
                    }
                    if (c3 == 0)
                        flag2++;
                }
            }
            if (count == 0)//if in a table there is no term carried forward then we will stop
                break;//count 0 signifies that no elements are combined to move to next pass so process will terminate
            for (i = 0; i < b.length; i++)

                for (j = 0; j < b[i].length; j++)
                    a[i][j] = b[i][j];
            //copy the matrix b to a so that b is ready to be initialised at the beginning of every pass
        }
        dash = new int[vars];//this will store the value of dash of each pi
        initialise_single(dash, -1);
        a = new int[flag2][nmin];//this will now hold the pi coverage table
        initialise(a, 0);
        for (i = 0; i < flag2; i++) {
            for (j = 0; j < nmin; j++) {
                check = match(min[j], pi, i, vars);
                if (check)
                    a[i][j] = 1;
            }
        }
        checker = new int[flag2];
        dash = new int[nmin];
        initialise_single(checker, -1);
        initialise_single(dash, -1);
        for (j = 0; j < nmin; j++) {
            count = 0;
            for (i = 0; i < flag2; i++) {
                if (a[i][j] == 1) {
                    pos = i;
                    count++;
                }
            }
            if (count == 1)
                checker[pos]++;
        }
        for (i = 0; i < flag2; i++) {
            if (checker[i] != -1) {
                for (j = 0; j < nmin; j++) {
                    if (a[i][j] == 1)
                        dash[j]++;
                }
                for (j = 0; j < nmin; j++)
                    a[i][j] = -1;
            }
        }
        for (j = 0; j < nmin; j++) {
            if (dash[j] != -1) {
                for (i = 0; i < flag2; i++)
                    a[i][j] = -1;
            }
        }
        //if there is no more row or column dominance we will stop
        //count =0 signifies there is no existing column or row dominance so process will terminate
        do {
            count = 0;
            //remove column dominance
            for (j = 0; j < nmin; j++) {
                for (k = j + 1; k < nmin; k++) {
                    c1 = 0;
                    c2 = 0;
                    c3 = 0;
                    for (i = 0; i < flag2; i++) {
                        if (a[i][j] == 1 && a[i][k] == 1)
                            c1++;
                        if (a[i][j] == 1 && a[i][k] == 0)
                            c2++;
                        if (a[i][j] == 0 && a[i][k] == 1)
                            c3++;
                    }
                    if (c2 > 0 && c3 > 0) {
                        break;
                    }
                    if (c1 > 0 && c2 > 0 && c3 == 0) {
                        for (no9 = 0; no9 < flag2; no9++)
                            a[no9][j] = -1;
                        count++;
                    }
                    if (c1 > 0 && c3 > 0 && c2 == 0) {
                        for (no9 = 0; no9 < flag2; no9++)
                            a[no9][k] = -1;
                        count++;
                    }
                    if (c1 > 0 && c2 == 0 && c3 == 0) {
                        for (no9 = 0; no9 < flag2; no9++)
                            a[no9][j] = -1;
                        count++;
                    }
                }
            }
            //remove row dominance
            for (i = 0; i < flag2; i++) {
                for (j = i + 1; j < flag2; j++) {
                    c1 = 0;
                    c2 = 0;
                    c3 = 0;
                    for (k = 0; k < nmin; k++) {
                        if (a[i][k] == 1 && a[j][k] == 1)
                            c1++;
                        if (a[i][k] == 1 && a[j][k] == 0)
                            c2++;
                        if (a[i][k] == 0 && a[j][k] == 1)
                            c3++;
                    }
                    if (c2 > 0 && c3 > 0)
                        break;
                    if (c1 > 0 && c2 > 0 && c3 == 0) {
                        for (no9 = 0; no9 < nmin; no9++)
                            a[j][no9] = -1;
                        count++;
                    }
                    if (c1 > 0 && c3 > 0 && c2 == 0) {
                        for (no9 = 0; no9 < nmin; no9++)
                            a[i][no9] = -1;
                        count++;
                    }
                    if (c1 > 0 && c2 == 0 && c3 == 0) {
                        for (no9 = 0; no9 < nmin; no9++)
                            a[j][no9] = -1;
                        count++;
                    }
                }
            }
        } while (count != 0);
        for (i = 0; i < a.length; i++) {
            for (j = 0; j < nmin; j++)
                if (a[i][j] == 1)
                    checker[i]++;
        }

        StringBuilder result = new StringBuilder();

        char[] bitvar = new char[vars];
        for (i = 0; i < vars; i++) {
            bitvar[i] = (char) (65 + i);
        }

        // Reverse if 5 variables
        if(vars == 5) {
            char temp = bitvar[0];
            bitvar[0] = bitvar[4];
            bitvar[4] = temp;
            temp = bitvar[1];
            bitvar[1] = bitvar[3];
            bitvar[3] = temp;
        }

        for (i = 0; i < flag2; i++) {
            if (checker[i] != -1) {
                result.append(decode(pi, i, bitvar));
                result.append("+");
            }
        }

        if (result.length() > 0) {
            result.deleteCharAt(result.length() - 1);  // Remove trailing '+'
        }

        return result.toString();
    }

    public static void initialise(int[][] a, int val) {
        int i;
        int j;
        for (i = 0; i < a.length; i++)
            for (j = 0; j < a[i].length; j++)
                a[i][j] = val;
    }

    public static void initialise_single(int[] a, int val) {
        int i;
        for (i = 0; i < a.length; i++)
            a[i] = val;
    }

    public static String decode(int[][] a, int row, char[] bitvar)//will convert the final essential pi and pi into switching variables
    {
        int i;
        StringBuilder s = new StringBuilder();
        for (i = 0; i < a[row].length; i++) {
            if (a[row][i] == 9) {
                //HUH?
            }
            else if (a[row][i] == 1)
                s.append(bitvar[i]);
            else
                s.append(bitvar[i]).append("'");
        }
        return s.toString();
    }

    public static boolean match(int min, int[][] a, int row, int nvar)//this will identify the prime implicants with the minterms
    {
        int[] b = new int[nvar];
        int i = nvar - 1;
        int c = 0;
        initialise_single(b, 0);
        while (min > 0) {
            b[i] = min % 2;
            min /= 2;
            i--;
        }
        for (i = 0; i < nvar; i++) {
            if (a[row][i] == 9)
                continue;
            if (a[row][i] != b[i])
                c++;
        }
        return c == 0;
    }
}