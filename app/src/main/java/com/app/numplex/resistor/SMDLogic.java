package com.app.numplex.resistor;

public class SMDLogic {
    public static double getOhmValue3Digits(String eiaCode) {
        double value;
        // Verifica se o código EIA tem 3 dígitos

        if (eiaCode.length() == 3) {
            // Pega os valores dos dígitos
            try {
                int firstDigit = Integer.parseInt(eiaCode.substring(0, 1));
                String secondDigit = eiaCode.substring(1, 2);
                int thirdDigit = Integer.parseInt(eiaCode.substring(2, 3));

                // Calcula o valor em ohms
                if ("R".equals(secondDigit)) {
                    value = (firstDigit) + (thirdDigit * 0.1);
                } else {
                    value = (firstDigit * 100) + (Integer.parseInt(secondDigit) * 10);
                    value *= Math.pow(10, thirdDigit - 1);
                }
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Letra somente na 2ª Casa");
            }
        } else {
            // Caso o código EIA não tenha 3 dígito
            throw new IllegalArgumentException("Necessário 3 Argumentos");
        }
        return value;
    }

    public static String getOhmValue4Digits(String eiaCode) {
        StringBuilder retorno = new StringBuilder();
        double value;

        // Verifica se o código EIA tem 4 dígitos
        if (eiaCode.length() == 4) {
            try {
                // Pega os valores dos dígitos
                String firstDigit = eiaCode.substring(0, 1);
                String secondDigit = eiaCode.substring(1, 2);
                String thirdDigit = eiaCode.substring(2, 3);
                String fourthDigit = eiaCode.substring(3);

                if (secondDigit.equals("R")) {
                    value = Integer.parseInt(firstDigit) + (Integer.parseInt(thirdDigit) * 0.1) + (Integer.parseInt(fourthDigit) * 0.01);
                    if (fourthDigit.equals("0")) {
                        retorno.append(value).append("0");
                    } else
                        retorno.append(value);
                } else if (thirdDigit.equals("R")) {
                    value = (Integer.parseInt(firstDigit) * 10) + (Integer.parseInt(secondDigit)) + (Integer.parseInt(fourthDigit) * 0.1);
                    if (firstDigit.equals("0")) {
                        retorno.append("0").append(value);
                    } else
                        retorno.append(value);
                } else {
                    value = (Integer.parseInt(firstDigit) * 100) + (Integer.parseInt(secondDigit) * 10) + Integer.parseInt(thirdDigit);
                    value *= Math.pow(10, Integer.parseInt(fourthDigit));
                    retorno.append(value);
                }
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Letra somente na 2ª ou 3ª Casa");
            }
        } else {
            // Caso o código EIA não tenha 4 dígito
            throw new IllegalArgumentException("Necessário 4 Argumentos e somente uma Letra");
        }
        return String.valueOf(retorno);
    }

    public static double getOhmValueEIA90(String eiaCode) {
        double value;

        // Verifica se o código EIA tem 3 dígitos
        if (eiaCode.length() == 3) {
            try {
                double secondDigitAux = 0;
                // Pega os valores dos dígitos
                int firstDigit = Integer.parseInt(eiaCode.substring(0, 2));
                String secondDigit = eiaCode.substring(2, 3);

                switch (firstDigit) {
                    case 1:
                        firstDigit = 100;
                        break;
                    case 2:
                        firstDigit = 102;
                        break;
                    case 3:
                        firstDigit = 105;
                        break;
                    case 4:
                        firstDigit = 107;
                        break;
                    case 5:
                        firstDigit = 110;
                        break;
                    case 6:
                        firstDigit = 113;
                        break;
                    case 7:
                        firstDigit = 115;
                        break;
                    case 8:
                        firstDigit = 118;
                        break;
                    case 9:
                        firstDigit = 121;
                        break;
                    case 10:
                        firstDigit = 124;
                        break;
                    case 11:
                        firstDigit = 127;
                        break;
                    case 12:
                        firstDigit = 130;
                        break;
                    case 13:
                        firstDigit = 133;
                        break;
                    case 14:
                        firstDigit = 137;
                        break;
                    case 15:
                        firstDigit = 140;
                        break;
                    case 16:
                        firstDigit = 143;
                        break;
                    case 17:
                        firstDigit = 147;
                        break;
                    case 18:
                        firstDigit = 150;
                        break;
                    case 19:
                        firstDigit = 154;
                        break;
                    case 20:
                        firstDigit = 158;
                        break;
                    case 21:
                        firstDigit = 162;
                        break;
                    case 22:
                        firstDigit = 165;
                        break;
                    case 23:
                        firstDigit = 169;
                        break;
                    case 24:
                        firstDigit = 174;
                        break;
                    case 25:
                        firstDigit = 178;
                        break;
                    case 26:
                        firstDigit = 182;
                        break;
                    case 27:
                        firstDigit = 187;
                        break;
                    case 28:
                        firstDigit = 191;
                        break;
                    case 29:
                        firstDigit = 196;
                        break;
                    case 30:
                        firstDigit = 200;
                        break;
                    case 31:
                        firstDigit = 205;
                        break;
                    case 32:
                        firstDigit = 210;
                        break;
                    case 33:
                        firstDigit = 215;
                        break;
                    case 34:
                        firstDigit = 221;
                        break;
                    case 35:
                        firstDigit = 226;
                        break;
                    case 36:
                        firstDigit = 232;
                        break;
                    case 37:
                        firstDigit = 237;
                        break;
                    case 38:
                        firstDigit = 243;
                        break;
                    case 39:
                        firstDigit = 249;
                        break;
                    case 40:
                        firstDigit = 255;
                        break;
                    case 41:
                        firstDigit = 261;
                        break;
                    case 42:
                        firstDigit = 267;
                        break;
                    case 43:
                        firstDigit = 274;
                        break;
                    case 44:
                        firstDigit = 280;
                        break;
                    case 45:
                        firstDigit = 287;
                        break;
                    case 46:
                        firstDigit = 294;
                        break;
                    case 47:
                        firstDigit = 301;
                        break;
                    case 48:
                        firstDigit = 309;
                        break;
                    case 49:
                        firstDigit = 316;
                        break;
                    case 50:
                        firstDigit = 324;
                        break;
                    case 51:
                        firstDigit = 332;
                        break;
                    case 52:
                        firstDigit = 340;
                        break;
                    case 53:
                        firstDigit = 348;
                        break;
                    case 54:
                        firstDigit = 357;
                        break;
                    case 55:
                        firstDigit = 365;
                        break;
                    case 56:
                        firstDigit = 374;
                        break;
                    case 57:
                        firstDigit = 383;
                        break;
                    case 58:
                        firstDigit = 392;
                        break;
                    case 59:
                        firstDigit = 402;
                        break;
                    case 60:
                        firstDigit = 412;
                        break;
                    case 61:
                        firstDigit = 422;
                        break;
                    case 62:
                        firstDigit = 432;
                        break;
                    case 63:
                        firstDigit = 442;
                        break;
                    case 64:
                        firstDigit = 453;
                        break;
                    case 65:
                        firstDigit = 464;
                        break;
                    case 66:
                        firstDigit = 475;
                        break;
                    case 67:
                        firstDigit = 487;
                        break;
                    case 68:
                        firstDigit = 499;
                        break;
                    case 69:
                        firstDigit = 511;
                        break;
                    case 70:
                        firstDigit = 523;
                        break;
                    case 71:
                        firstDigit = 536;
                        break;
                    case 72:
                        firstDigit = 549;
                        break;
                    case 73:
                        firstDigit = 562;
                        break;
                    case 74:
                        firstDigit = 576;
                        break;
                    case 75:
                        firstDigit = 590;
                        break;
                    case 76:
                        firstDigit = 604;
                        break;
                    case 77:
                        firstDigit = 619;
                        break;
                    case 78:
                        firstDigit = 634;
                        break;
                    case 79:
                        firstDigit = 649;
                        break;
                    case 80:
                        firstDigit = 665;
                        break;
                    case 81:
                        firstDigit = 681;
                        break;
                    case 82:
                        firstDigit = 698;
                        break;
                    case 83:
                        firstDigit = 715;
                        break;
                    case 84:
                        firstDigit = 732;
                        break;
                    case 85:
                        firstDigit = 750;
                        break;
                    case 86:
                        firstDigit = 768;
                        break;
                    case 87:
                        firstDigit = 787;
                        break;
                    case 88:
                        firstDigit = 806;
                        break;
                    case 89:
                        firstDigit = 825;
                        break;
                    case 90:
                        firstDigit = 845;
                        break;
                    case 91:
                        firstDigit = 866;
                        break;
                    case 92:
                        firstDigit = 887;
                        break;
                    case 93:
                        firstDigit = 909;
                        break;
                    case 94:
                        firstDigit = 931;
                        break;
                    case 95:
                        firstDigit = 953;
                        break;
                    case 96:
                        firstDigit = 976;
                        break;
                }
                switch (secondDigit) {
                    case "A":
                        secondDigitAux = 1;
                        break;
                    case "B":
                    case "H":
                        secondDigitAux = 10;
                        break;
                    case "C":
                        secondDigitAux = 100;
                        break;
                    case "D":
                        secondDigitAux = 1000;
                        break;
                    case "E":
                        secondDigitAux = 10000;
                        break;
                    case "F":
                        secondDigitAux = 100000;
                        break;
                    case "X":
                    case "S":
                        secondDigitAux = 0.1;
                        break;
                    case "Y":
                    case "R":
                        secondDigitAux = 0.01;
                        break;
                    case "Z":
                        secondDigitAux = 0.001;
                        break;
                }
                // Calcula o valor em ohms
                value = firstDigit;
                value *= secondDigitAux;
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Letra somente na última Casa");
            }
        } else {
            // Caso o código EIA não tenha 3 dígito
            throw new IllegalArgumentException("Necessário 3 Argumentos");
        }
        return value;
    }

    public static String getCodeEIA3(String eiaCode) {
        StringBuilder retorno = new StringBuilder();

        if(eiaCode.endsWith(".0"))
            eiaCode = eiaCode.replace(".0", "");
        if(eiaCode.length() == 1)
            eiaCode = eiaCode + ".0";
        try {
            if (eiaCode.length() >= 1 && eiaCode.length() < 12) {
                // Pega os valores dos dígitos
                String firstDigit = eiaCode.substring(0, 1);
                String secondDigit = eiaCode.substring(1, 2);
                String thirdDigit = eiaCode.substring(2);

                if (secondDigit.equals(".")) {
                    retorno.append(firstDigit).append("R").append(thirdDigit);
                } else {
                    // Calcula o valor em ohms
                    switch (thirdDigit) {
                        case "":
                            thirdDigit = "0";
                            break;
                        case "0":
                            thirdDigit = "1";
                            break;
                        case "00":
                            thirdDigit = "2";
                            break;
                        case "000":
                            thirdDigit = "3";
                            break;
                        case "0000":
                            thirdDigit = "4";
                            break;
                        case "00000":
                            thirdDigit = "5";
                            break;
                        case "000000":
                            thirdDigit = "6";
                            break;
                        case "0000000":
                            thirdDigit = "7";
                            break;
                        case "00000000":
                            thirdDigit = "8";
                            break;
                        case "000000000":
                            thirdDigit = "9";
                            break;
                        default:
                            throw new IllegalArgumentException("ERRO!");
                    }
                    retorno.append(firstDigit).append(secondDigit).append(thirdDigit);
                }
            } else {
                // Caso o código EIA não tenha 3 dígito
                throw new IllegalArgumentException("Necessário um ou mais Argumentos");
            }
        } catch (StringIndexOutOfBoundsException e) {
            throw new IllegalArgumentException("ERRO!");
        }
        return String.valueOf(retorno);
    }

    public static String getCodeEIA4(String eiaCode) {
        StringBuilder retorno = new StringBuilder();
        if(eiaCode.length() == 3)
            eiaCode = eiaCode + "0";

        try {
            if (eiaCode.length() >= 4 && eiaCode.length() < 13) {
                if(eiaCode.endsWith(".0") && eiaCode.length() >= 5)
                    eiaCode = eiaCode.replace(".0", "");

                // Pega os valores dos dígitos
                String firstDigit = eiaCode.substring(0, 1);
                String secondDigit = eiaCode.substring(1, 2);
                String thirdDigit = eiaCode.substring(2, 3);
                String fourthDigit = eiaCode.substring(3);

                if (secondDigit.equals(".")) {
                    retorno.append(firstDigit).append("R").append(thirdDigit).append(fourthDigit);
                } else if (thirdDigit.equals(".")) {
                    retorno.append(firstDigit).append(secondDigit).append("R").append(fourthDigit);
                } else {
                    // Calcula o valor em ohms
                    switch (fourthDigit) {
                        case "0":
                        case "0.0":
                            fourthDigit = "1";
                            break;
                        case "00":
                            fourthDigit = "2";
                            break;
                        case "000":
                            fourthDigit = "3";
                            break;
                        case "0000":
                            fourthDigit = "4";
                            break;
                        case "00000":
                            fourthDigit = "5";
                            break;
                        case "000000":
                            fourthDigit = "6";
                            break;
                        case "0000000":
                            fourthDigit = "7";
                            break;
                        case "00000000":
                            fourthDigit = "8";
                            break;
                        case "000000000":
                            fourthDigit = "9";
                            break;
                        default:
                            fourthDigit = "";
                            break;
                    }
                    retorno.append(firstDigit).append(secondDigit).append(thirdDigit).append(fourthDigit);
                }
            } else {
                // Caso o código EIA não tenha 4 dígito
                throw new IllegalArgumentException("Necessário possuir 4 Argumentos");
            }
        } catch (StringIndexOutOfBoundsException e) {
            throw new IllegalArgumentException("ERRO!");
        }
        return String.valueOf(retorno);
    }

    public static String getCodeEIA90(String eiaCode) {
        if(eiaCode.endsWith(".0"))
            eiaCode = eiaCode.replace(".0","");
        if(eiaCode.startsWith("0"))
            eiaCode = eiaCode.replaceFirst("0", "");

        StringBuilder retorno = new StringBuilder();
        if (eiaCode.length() >= 1 && eiaCode.length() < 12) {
            // Pega os valores dos dígitos
            String firstDigitDefault = eiaCode.substring(0, 3);
            String secondDigit = eiaCode.substring(3);

            // Abaixo usado auxiliar para printar resultados com ponto flutuante
            String firstDigitOne;
            String firstDigitTwo;
            String firstDigitThree;

            if(eiaCode.contains(".")){
                firstDigitOne = eiaCode.substring(0, 4);
                firstDigitTwo = eiaCode.substring(0, 4);
                firstDigitThree = eiaCode.substring(0, 4);
            }else{
                firstDigitOne = eiaCode.substring(0, 3);
                firstDigitTwo = eiaCode.substring(0, 3);
                firstDigitThree = eiaCode.substring(0, 3);
            }

            String PositionThree = String.valueOf(firstDigitDefault.charAt(2));
            String PositionTwo = String.valueOf(firstDigitDefault.charAt(1));
            String PositionOne = String.valueOf(firstDigitDefault.charAt(0));

            switch (firstDigitDefault) {
                case "100":
                    firstDigitDefault = "01";
                    break;
                case "102":
                    firstDigitDefault = "02";
                    break;
                case "105":
                    firstDigitDefault = "03";
                    break;
                case "107":
                    firstDigitDefault = "04";
                    break;
                case "110":
                    firstDigitDefault = "05";
                    break;
                case "113":
                    firstDigitDefault = "06";
                    break;
                case "115":
                    firstDigitDefault = "07";
                    break;
                case "118":
                    firstDigitDefault = "08";
                    break;
                case "121":
                    firstDigitDefault = "09";
                    break;
                case "124":
                    firstDigitDefault = "10";
                    break;
                case "127":
                    firstDigitDefault = "11";
                    break;
                case "130":
                    firstDigitDefault = "12";
                    break;
                case "133":
                    firstDigitDefault = "13";
                    break;
                case "137":
                    firstDigitDefault = "14";
                    break;
                case "140":
                    firstDigitDefault = "15";
                    break;
                case "143":
                    firstDigitDefault = "16";
                    break;
                case "147":
                    firstDigitDefault = "17";
                    break;
                case "150":
                    firstDigitDefault = "18";
                    break;
                case "154":
                    firstDigitDefault = "19";
                    break;
                case "158":
                    firstDigitDefault = "20";
                    break;
                case "162":
                    firstDigitDefault = "21";
                    break;
                case "165":
                    firstDigitDefault = "22";
                    break;
                case "169":
                    firstDigitDefault = "23";
                    break;
                case "174":
                    firstDigitDefault = "24";
                    break;
                case "178":
                    firstDigitDefault = "25";
                    break;
                case "182":
                    firstDigitDefault = "26";
                    break;
                case "187":
                    firstDigitDefault = "27";
                    break;
                case "191":
                    firstDigitDefault = "28";
                    break;
                case "196":
                    firstDigitDefault = "29";
                    break;
                case "200":
                    firstDigitDefault = "30";
                    break;
                case "205":
                    firstDigitDefault = "31";
                    break;
                case "210":
                    firstDigitDefault = "32";
                    break;
                case "215":
                    firstDigitDefault = "33";
                    break;
                case "221":
                    firstDigitDefault = "34";
                    break;
                case "226":
                    firstDigitDefault = "35";
                    break;
                case "232":
                    firstDigitDefault = "36";
                    break;
                case "237":
                    firstDigitDefault = "37";
                    break;
                case "243":
                    firstDigitDefault = "38";
                    break;
                case "249":
                    firstDigitDefault = "39";
                    break;
                case "255":
                    firstDigitDefault = "40";
                    break;
                case "261":
                    firstDigitDefault = "41";
                    break;
                case "267":
                    firstDigitDefault = "42";
                    break;
                case "274":
                    firstDigitDefault = "43";
                    break;
                case "280":
                    firstDigitDefault = "44";
                    break;
                case "287":
                    firstDigitDefault = "45";
                    break;
                case "294":
                    firstDigitDefault = "46";
                    break;
                case "301":
                    firstDigitDefault = "47";
                    break;
                case "309":
                    firstDigitDefault = "48";
                    break;
                case "316":
                    firstDigitDefault = "49";
                    break;
                case "324":
                    firstDigitDefault = "50";
                    break;
                case "332":
                    firstDigitDefault = "51";
                    break;
                case "340":
                    firstDigitDefault = "52";
                    break;
                case "348":
                    firstDigitDefault = "53";
                    break;
                case "357":
                    firstDigitDefault = "54";
                    break;
                case "365":
                    firstDigitDefault = "55";
                    break;
                case "374":
                    firstDigitDefault = "56";
                    break;
                case "383":
                    firstDigitDefault = "57";
                    break;
                case "392":
                    firstDigitDefault = "58";
                    break;
                case "402":
                    firstDigitDefault = "59";
                    break;
                case "412":
                    firstDigitDefault = "60";
                    break;
                case "422":
                    firstDigitDefault = "61";
                    break;
                case "432":
                    firstDigitDefault = "62";
                    break;
                case "442":
                    firstDigitDefault = "63";
                    break;
                case "453":
                    firstDigitDefault = "64";
                    break;
                case "464":
                    firstDigitDefault = "65";
                    break;
                case "475":
                    firstDigitDefault = "66";
                    break;
                case "487":
                    firstDigitDefault = "67";
                    break;
                case "499":
                    firstDigitDefault = "68";
                    break;
                case "511":
                    firstDigitDefault = "69";
                    break;
                case "523":
                    firstDigitDefault = "70";
                    break;
                case "536":
                    firstDigitDefault = "71";
                    break;
                case "549":
                    firstDigitDefault = "72";
                    break;
                case "562":
                    firstDigitDefault = "73";
                    break;
                case "576":
                    firstDigitDefault = "74";
                    break;
                case "590":
                    firstDigitDefault = "75";
                    break;
                case "604":
                    firstDigitDefault = "76";
                    break;
                case "619":
                    firstDigitDefault = "77";
                    break;
                case "634":
                    firstDigitDefault = "78";
                    break;
                case "649":
                    firstDigitDefault = "79";
                    break;
                case "665":
                    firstDigitDefault = "80";
                    break;
                case "681":
                    firstDigitDefault = "81";
                    break;
                case "698":
                    firstDigitDefault = "82";
                    break;
                case "715":
                    firstDigitDefault = "83";
                    break;
                case "732":
                    firstDigitDefault = "84";
                    break;
                case "750":
                    firstDigitDefault = "85";
                    break;
                case "768":
                    firstDigitDefault = "86";
                    break;
                case "787":
                    firstDigitDefault = "87";
                    break;
                case "806":
                    firstDigitDefault = "88";
                    break;
                case "825":
                    firstDigitDefault = "89";
                    break;
                case "845":
                    firstDigitDefault = "90";
                    break;
                case "866":
                    firstDigitDefault = "91";
                    break;
                case "887":
                    firstDigitDefault = "92";
                    break;
                case "909":
                    firstDigitDefault = "93";
                    break;
                case "931":
                    firstDigitDefault = "94";
                    break;
                case "953":
                    firstDigitDefault = "95";
                    break;
                case "976":
                    firstDigitDefault = "96";
                    break;
                default:
                    break;
            }
            switch (firstDigitTwo) {
                case "1.00":
                    firstDigitTwo = "01";
                    break;
                case "1.02":
                    firstDigitTwo = "02";
                    break;
                case "1.05":
                    firstDigitTwo = "03";
                    break;
                case "1.07":
                    firstDigitTwo = "04";
                    break;
                case "1.10":
                    firstDigitTwo = "05";
                    break;
                case "1.13":
                    firstDigitTwo = "06";
                    break;
                case "1.15":
                    firstDigitTwo = "07";
                    break;
                case "1.18":
                    firstDigitTwo = "08";
                    break;
                case "1.21":
                    firstDigitTwo = "09";
                    break;
                case "1.24":
                    firstDigitTwo = "10";
                    break;
                case "1.27":
                    firstDigitTwo = "11";
                    break;
                case "1.30":
                    firstDigitTwo = "12";
                    break;
                case "1.33":
                    firstDigitTwo = "13";
                    break;
                case "1.37":
                    firstDigitTwo = "14";
                    break;
                case "1.40":
                    firstDigitTwo = "15";
                    break;
                case "1.43":
                    firstDigitTwo = "16";
                    break;
                case "1.47":
                    firstDigitTwo = "17";
                    break;
                case "1.50":
                    firstDigitTwo = "18";
                    break;
                case "1.54":
                    firstDigitTwo = "19";
                    break;
                case "1.58":
                    firstDigitTwo = "20";
                    break;
                case "1.62":
                    firstDigitTwo = "21";
                    break;
                case "1.65":
                    firstDigitTwo = "22";
                    break;
                case "1.69":
                    firstDigitTwo = "23";
                    break;
                case "1.74":
                    firstDigitTwo = "24";
                    break;
                case "1.78":
                    firstDigitTwo = "25";
                    break;
                case "1.82":
                    firstDigitTwo = "26";
                    break;
                case "1.87":
                    firstDigitTwo = "27";
                    break;
                case "1.91":
                    firstDigitTwo = "28";
                    break;
                case "1.96":
                    firstDigitTwo = "29";
                    break;
                case "2.00":
                    firstDigitTwo = "30";
                    break;
                case "2.05":
                    firstDigitTwo = "31";
                    break;
                case "2.10":
                    firstDigitTwo = "32";
                    break;
                case "2.15":
                    firstDigitTwo = "33";
                    break;
                case "2.21":
                    firstDigitTwo = "34";
                    break;
                case "2.26":
                    firstDigitTwo = "35";
                    break;
                case "2.32":
                    firstDigitTwo = "36";
                    break;
                case "2.37":
                    firstDigitTwo = "37";
                    break;
                case "2.43":
                    firstDigitTwo = "38";
                    break;
                case "2.49":
                    firstDigitTwo = "39";
                    break;
                case "2.55":
                    firstDigitTwo = "40";
                    break;
                case "2.61":
                    firstDigitTwo = "41";
                    break;
                case "2.67":
                    firstDigitTwo = "42";
                    break;
                case "2.74":
                    firstDigitTwo = "43";
                    break;
                case "2.80":
                    firstDigitTwo = "44";
                    break;
                case "2.87":
                    firstDigitTwo = "45";
                    break;
                case "2.94":
                    firstDigitTwo = "46";
                    break;
                case "3.01":
                    firstDigitTwo = "47";
                    break;
                case "3.09":
                    firstDigitTwo = "48";
                    break;
                case "3.16":
                    firstDigitTwo = "49";
                    break;
                case "3.24":
                    firstDigitTwo = "50";
                    break;
                case "3.32":
                    firstDigitTwo = "51";
                    break;
                case "3.40":
                    firstDigitTwo = "52";
                    break;
                case "3.48":
                    firstDigitTwo = "53";
                    break;
                case "3.57":
                    firstDigitTwo = "54";
                    break;
                case "3.65":
                    firstDigitTwo = "55";
                    break;
                case "3.74":
                    firstDigitTwo = "56";
                    break;
                case "3.83":
                    firstDigitTwo = "57";
                    break;
                case "3.92":
                    firstDigitTwo = "58";
                    break;
                case "4.02":
                    firstDigitTwo = "59";
                    break;
                case "4.12":
                    firstDigitTwo = "60";
                    break;
                case "4.22":
                    firstDigitTwo = "61";
                    break;
                case "4.32":
                    firstDigitTwo = "62";
                    break;
                case "4.42":
                    firstDigitTwo = "63";
                    break;
                case "4.53":
                    firstDigitTwo = "64";
                    break;
                case "4.64":
                    firstDigitTwo = "65";
                    break;
                case "4.75":
                    firstDigitTwo = "66";
                    break;
                case "4.87":
                    firstDigitTwo = "67";
                    break;
                case "4.99":
                    firstDigitTwo = "68";
                    break;
                case "5.11":
                    firstDigitTwo = "69";
                    break;
                case "5.23":
                    firstDigitTwo = "70";
                    break;
                case "5.36":
                    firstDigitTwo = "71";
                    break;
                case "5.49":
                    firstDigitTwo = "72";
                    break;
                case "5.62":
                    firstDigitTwo = "73";
                    break;
                case "5.76":
                    firstDigitTwo = "74";
                    break;
                case "5.90":
                    firstDigitTwo = "75";
                    break;
                case "6.04":
                    firstDigitTwo = "76";
                    break;
                case "6.19":
                    firstDigitTwo = "77";
                    break;
                case "6.34":
                    firstDigitTwo = "78";
                    break;
                case "6.49":
                    firstDigitTwo = "79";
                    break;
                case "6.65":
                    firstDigitTwo = "80";
                    break;
                case "6.81":
                    firstDigitTwo = "81";
                    break;
                case "6.98":
                    firstDigitTwo = "82";
                    break;
                case "7.15":
                    firstDigitTwo = "83";
                    break;
                case "7.32":
                    firstDigitTwo = "84";
                    break;
                case "7.50":
                    firstDigitTwo = "85";
                    break;
                case "7.68":
                    firstDigitTwo = "86";
                    break;
                case "7.87":
                    firstDigitTwo = "87";
                    break;
                case "8.06":
                    firstDigitTwo = "88";
                    break;
                case "8.25":
                    firstDigitTwo = "89";
                    break;
                case "8.45":
                    firstDigitTwo = "90";
                    break;
                case "8.66":
                    firstDigitTwo = "91";
                    break;
                case "8.87":
                    firstDigitTwo = "92";
                    break;
                case "9.09":
                    firstDigitTwo = "93";
                    break;
                case "9.31":
                    firstDigitTwo = "94";
                    break;
                case "9.53":
                    firstDigitTwo = "95";
                    break;
                case "9.76":
                    firstDigitTwo = "96";
                    break;
            }
            switch (firstDigitThree) {
                case "10.0":
                    firstDigitThree = "01";
                    break;
                case "10.2":
                    firstDigitThree = "02";
                    break;
                case "10.5":
                    firstDigitThree = "03";
                    break;
                case "10.7":
                    firstDigitThree = "04";
                    break;
                case "11.0":
                    firstDigitThree = "05";
                    break;
                case "11.3":
                    firstDigitThree = "06";
                    break;
                case "11.5":
                    firstDigitThree = "07";
                    break;
                case "11.8":
                    firstDigitThree = "08";
                    break;
                case "12.1":
                    firstDigitThree = "09";
                    break;
                case "12.4":
                    firstDigitThree = "10";
                    break;
                case "12.7":
                    firstDigitThree = "11";
                    break;
                case "13.0":
                    firstDigitThree = "12";
                    break;
                case "13.3":
                    firstDigitThree = "13";
                    break;
                case "13.7":
                    firstDigitThree = "14";
                    break;
                case "14.0":
                    firstDigitThree = "15";
                    break;
                case "14.3":
                    firstDigitThree = "16";
                    break;
                case "14.7":
                    firstDigitThree = "17";
                    break;
                case "15.0":
                    firstDigitThree = "18";
                    break;
                case "15.4":
                    firstDigitThree = "19";
                    break;
                case "15.8":
                    firstDigitThree = "20";
                    break;
                case "16.2":
                    firstDigitThree = "21";
                    break;
                case "16.5":
                    firstDigitThree = "22";
                    break;
                case "16.9":
                    firstDigitThree = "23";
                    break;
                case "17.4":
                    firstDigitThree = "24";
                    break;
                case "17.8":
                    firstDigitThree = "25";
                    break;
                case "18.2":
                    firstDigitThree = "26";
                    break;
                case "18.7":
                    firstDigitThree = "27";
                    break;
                case "19.1":
                    firstDigitThree = "28";
                    break;
                case "19.6":
                    firstDigitThree = "29";
                    break;
                case "20.0":
                    firstDigitThree = "30";
                    break;
                case "20.5":
                    firstDigitThree = "31";
                    break;
                case "21.0":
                    firstDigitThree = "32";
                    break;
                case "21.5":
                    firstDigitThree = "33";
                    break;
                case "22.1":
                    firstDigitThree = "34";
                    break;
                case "22.6":
                    firstDigitThree = "35";
                    break;
                case "23.2":
                    firstDigitThree = "36";
                    break;
                case "23.7":
                    firstDigitThree = "37";
                    break;
                case "24.3":
                    firstDigitThree = "38";
                    break;
                case "24.9":
                    firstDigitThree = "39";
                    break;
                case "25.5":
                    firstDigitThree = "40";
                    break;
                case "26.1":
                    firstDigitThree = "41";
                    break;
                case "26.7":
                    firstDigitThree = "42";
                    break;
                case "27.4":
                    firstDigitThree = "43";
                    break;
                case "28.0":
                    firstDigitThree = "44";
                    break;
                case "28.7":
                    firstDigitThree = "45";
                    break;
                case "29.4":
                    firstDigitThree = "46";
                    break;
                case "30.1":
                    firstDigitThree = "47";
                    break;
                case "30.9":
                    firstDigitThree = "48";
                    break;
                case "31.6":
                    firstDigitThree = "49";
                    break;
                case "32.4":
                    firstDigitThree = "50";
                    break;
                case "33.2":
                    firstDigitThree = "51";
                    break;
                case "34.0":
                    firstDigitThree = "52";
                    break;
                case "34.8":
                    firstDigitThree = "53";
                    break;
                case "35.7":
                    firstDigitThree = "54";
                    break;
                case "36.5":
                    firstDigitThree = "55";
                    break;
                case "37.4":
                    firstDigitThree = "56";
                    break;
                case "38.3":
                    firstDigitThree = "57";
                    break;
                case "39.2":
                    firstDigitThree = "58";
                    break;
                case "40.2":
                    firstDigitThree = "59";
                    break;
                case "41.2":
                    firstDigitThree = "60";
                    break;
                case "42.2":
                    firstDigitThree = "61";
                    break;
                case "43.2":
                    firstDigitThree = "62";
                    break;
                case "44.2":
                    firstDigitThree = "63";
                    break;
                case "45.3":
                    firstDigitThree = "64";
                    break;
                case "46.4":
                    firstDigitThree = "65";
                    break;
                case "47.5":
                    firstDigitThree = "66";
                    break;
                case "48.7":
                    firstDigitThree = "67";
                    break;
                case "49.9":
                    firstDigitThree = "68";
                    break;
                case "51.1":
                    firstDigitThree = "69";
                    break;
                case "52.3":
                    firstDigitThree = "70";
                    break;
                case "53.6":
                    firstDigitThree = "71";
                    break;
                case "54.9":
                    firstDigitThree = "72";
                    break;
                case "56.2":
                    firstDigitThree = "73";
                    break;
                case "57.6":
                    firstDigitThree = "74";
                    break;
                case "59.0":
                    firstDigitThree = "75";
                    break;
                case "60.4":
                    firstDigitThree = "76";
                    break;
                case "61.9":
                    firstDigitThree = "77";
                    break;
                case "63.4":
                    firstDigitThree = "78";
                    break;
                case "64.9":
                    firstDigitThree = "79";
                    break;
                case "66.5":
                    firstDigitThree = "80";
                    break;
                case "68.1":
                    firstDigitThree = "81";
                    break;
                case "69.8":
                    firstDigitThree = "82";
                    break;
                case "71.5":
                    firstDigitThree = "83";
                    break;
                case "73.2":
                    firstDigitThree = "84";
                    break;
                case "75.0":
                    firstDigitThree = "85";
                    break;
                case "76.8":
                    firstDigitThree = "86";
                    break;
                case "78.7":
                    firstDigitThree = "87";
                    break;
                case "80.6":
                    firstDigitThree = "88";
                    break;
                case "82.5":
                    firstDigitThree = "89";
                    break;
                case "84.5":
                    firstDigitThree = "90";
                    break;
                case "86.6":
                    firstDigitThree = "91";
                    break;
                case "88.7":
                    firstDigitThree = "92";
                    break;
                case "90.9":
                    firstDigitThree = "93";
                    break;
                case "93.1":
                    firstDigitThree = "94";
                    break;
                case "95.3":
                    firstDigitThree = "95";
                    break;
                case "97.6":
                    firstDigitThree = "96";
                    break;
            }

            if (PositionOne.equals(".")) {
                switch (firstDigitOne) {
                    case ".100":
                        firstDigitOne = "01";
                        break;
                    case ".102":
                        firstDigitOne = "02";
                        break;
                    case ".105":
                        firstDigitOne = "03";
                        break;
                    case ".107":
                        firstDigitOne = "04";
                        break;
                    case ".110":
                        firstDigitOne = "05";
                        break;
                    case ".113":
                        firstDigitOne = "06";
                        break;
                    case ".115":
                        firstDigitOne = "07";
                        break;
                    case ".118":
                        firstDigitOne = "08";
                        break;
                    case ".121":
                        firstDigitOne = "09";
                        break;
                    case ".124":
                        firstDigitOne = "10";
                        break;
                    case ".127":
                        firstDigitOne = "11";
                        break;
                    case ".130":
                        firstDigitOne = "12";
                        break;
                    case ".133":
                        firstDigitOne = "13";
                        break;
                    case ".137":
                        firstDigitOne = "14";
                        break;
                    case ".140":
                        firstDigitOne = "15";
                        break;
                    case ".143":
                        firstDigitOne = "16";
                        break;
                    case ".147":
                        firstDigitOne = "17";
                        break;
                    case ".150":
                        firstDigitOne = "18";
                        break;
                    case ".154":
                        firstDigitOne = "19";
                        break;
                    case ".158":
                        firstDigitOne = "20";
                        break;
                    case ".162":
                        firstDigitOne = "21";
                        break;
                    case ".165":
                        firstDigitOne = "22";
                        break;
                    case ".169":
                        firstDigitOne = "23";
                        break;
                    case ".174":
                        firstDigitOne = "24";
                        break;
                    case ".178":
                        firstDigitOne = "25";
                        break;
                    case ".182":
                        firstDigitOne = "26";
                        break;
                    case ".187":
                        firstDigitOne = "27";
                        break;
                    case ".191":
                        firstDigitOne = "28";
                        break;
                    case ".196":
                        firstDigitOne = "29";
                        break;
                    case ".200":
                        firstDigitOne = "30";
                        break;
                    case ".205":
                        firstDigitOne = "31";
                        break;
                    case ".210":
                        firstDigitOne = "32";
                        break;
                    case ".215":
                        firstDigitOne = "33";
                        break;
                    case ".221":
                        firstDigitOne = "34";
                        break;
                    case ".226":
                        firstDigitOne = "35";
                        break;
                    case ".232":
                        firstDigitOne = "36";
                        break;
                    case ".237":
                        firstDigitOne = "37";
                        break;
                    case ".243":
                        firstDigitOne = "38";
                        break;
                    case ".249":
                        firstDigitOne = "39";
                        break;
                    case ".255":
                        firstDigitOne = "40";
                        break;
                    case ".261":
                        firstDigitOne = "41";
                        break;
                    case ".267":
                        firstDigitOne = "42";
                        break;
                    case ".274":
                        firstDigitOne = "43";
                        break;
                    case ".280":
                        firstDigitOne = "44";
                        break;
                    case ".287":
                        firstDigitOne = "45";
                        break;
                    case ".294":
                        firstDigitOne = "46";
                        break;
                    case ".301":
                        firstDigitOne = "47";
                        break;
                    case ".309":
                        firstDigitOne = "48";
                        break;
                    case ".316":
                        firstDigitOne = "49";
                        break;
                    case ".324":
                        firstDigitOne = "50";
                        break;
                    case ".332":
                        firstDigitOne = "51";
                        break;
                    case ".340":
                        firstDigitOne = "52";
                        break;
                    case ".348":
                        firstDigitOne = "53";
                        break;
                    case ".357":
                        firstDigitOne = "54";
                        break;
                    case ".365":
                        firstDigitOne = "55";
                        break;
                    case ".374":
                        firstDigitOne = "56";
                        break;
                    case ".383":
                        firstDigitOne = "57";
                        break;
                    case ".392":
                        firstDigitOne = "58";
                        break;
                    case ".402":
                        firstDigitOne = "59";
                        break;
                    case ".412":
                        firstDigitOne = "60";
                        break;
                    case ".422":
                        firstDigitOne = "61";
                        break;
                    case ".432":
                        firstDigitOne = "62";
                        break;
                    case ".442":
                        firstDigitOne = "63";
                        break;
                    case ".453":
                        firstDigitOne = "64";
                        break;
                    case ".464":
                        firstDigitOne = "65";
                        break;
                    case ".475":
                        firstDigitOne = "66";
                        break;
                    case ".487":
                        firstDigitOne = "67";
                        break;
                    case ".499":
                        firstDigitOne = "68";
                        break;
                    case ".511":
                        firstDigitOne = "69";
                        break;
                    case ".523":
                        firstDigitOne = "70";
                        break;
                    case ".536":
                        firstDigitOne = "71";
                        break;
                    case ".549":
                        firstDigitOne = "72";
                        break;
                    case ".562":
                        firstDigitOne = "73";
                        break;
                    case ".576":
                        firstDigitOne = "74";
                        break;
                    case ".590":
                        firstDigitOne = "75";
                        break;
                    case ".604":
                        firstDigitOne = "76";
                        break;
                    case ".619":
                        firstDigitOne = "77";
                        break;
                    case ".634":
                        firstDigitOne = "78";
                        break;
                    case ".649":
                        firstDigitOne = "79";
                        break;
                    case ".665":
                        firstDigitOne = "80";
                        break;
                    case ".681":
                        firstDigitOne = "81";
                        break;
                    case ".698":
                        firstDigitOne = "82";
                        break;
                    case ".715":
                        firstDigitOne = "83";
                        break;
                    case ".732":
                        firstDigitOne = "84";
                        break;
                    case ".750":
                        firstDigitOne = "85";
                        break;
                    case ".768":
                        firstDigitOne = "86";
                        break;
                    case ".787":
                        firstDigitOne = "87";
                        break;
                    case ".806":
                        firstDigitOne = "88";
                        break;
                    case ".825":
                        firstDigitOne = "89";
                        break;
                    case ".845":
                        firstDigitOne = "90";
                        break;
                    case ".866":
                        firstDigitOne = "91";
                        break;
                    case ".887":
                        firstDigitOne = "92";
                        break;
                    case ".909":
                        firstDigitOne = "93";
                        break;
                    case ".931":
                        firstDigitOne = "94";
                        break;
                    case ".953":
                        firstDigitOne = "95";
                        break;
                    case ".976":
                        firstDigitOne = "96";
                        break;
                    default:
                        break;
                }
                retorno.append(firstDigitOne).append("Z");
            } else if (PositionTwo.equals(".")) {
                retorno.append(firstDigitTwo).append("Y or R");
            } else if (PositionThree.equals(".")) {
                retorno.append(firstDigitThree).append("X or S");
            } else {
                switch (secondDigit) {
                    case "":
                        secondDigit = "A";
                        break;
                    case "0":
                        secondDigit = "B or H";
                        break;
                    case "00":
                        secondDigit = "C";
                        break;
                    case "000":
                        secondDigit = "D";
                        break;
                    case "0000":
                        secondDigit = "E";
                        break;
                    case "00000":
                        secondDigit = "F";
                        break;
                }
                retorno.append(firstDigitDefault).append(secondDigit);
            }
        } else {
            throw new IllegalArgumentException("Necessário de 1 a 10 Argumentos");
        }
        return String.valueOf(retorno);
    }
}