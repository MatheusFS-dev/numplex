package com.app.numplex.network;

import android.content.Context;

import com.app.numplex.R;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class NetworkIP {

    public static String[][] divideSubnets(String enderecoIPCIDR, Context context) {
        // Separa o endereço IP e o prefixo CIDR
        String[] parts = enderecoIPCIDR.split("/");
        String enderecoIP = parts[0];
        int mascaraBits = Integer.parseInt(parts[1]);
        String novaMascaraBits = String.valueOf((mascaraBits + 1));
        String enderecoRede1 = getEnderecoRede(enderecoIP + "/" + mascaraBits);
        assert enderecoRede1 != null;
        String enderecoRede2 = addHostNumber(enderecoRede1, getNumeroTotalHosts(enderecoRede1 + "/" + mascaraBits) / 2);

        if(mascaraBits == 0){
            novaMascaraBits = String.valueOf((mascaraBits + 1));
            enderecoRede1 = "0.0.0.0";
            enderecoRede2 = addHostNumber(enderecoRede1, getNumeroTotalHosts(enderecoRede1 + "/" + mascaraBits) / 2);
        }

        return new String[][] {
                {enderecoRede1 + "/" + novaMascaraBits, enderecoRede1 + " - " + getEnderecoBroadcast(enderecoRede1 + "/" + novaMascaraBits), getUsableHostIPRange(enderecoRede1 + "/" + novaMascaraBits, context), String.valueOf(getNumeroHostsUtilizaveis(enderecoRede1 + "/" + novaMascaraBits))},
                {enderecoRede2 + "/" + novaMascaraBits, enderecoRede2 + " - " + getEnderecoBroadcast(enderecoRede2 + "/" + novaMascaraBits), getUsableHostIPRange(enderecoRede2 + "/" + novaMascaraBits, context), String.valueOf(getNumeroHostsUtilizaveis(enderecoRede2 + "/" + novaMascaraBits))}
        };
    }

    public static String addHostNumber(String ipAddress, long valueToAdd) {
        String[] components = ipAddress.split("\\.");
        int[] ipArray = new int[4];

        // Convert components to integers
        for (int i = 0; i < 4; i++) {
            ipArray[i] = Integer.parseInt(components[i]);
        }

        // Perform addition
        long carry = valueToAdd;
        for (int i = 3; i >= 0; i--) {
            long sum = ipArray[i] + carry;
            ipArray[i] = (int) (sum % 256);
            carry = sum / 256;
        }

        // Handle overflow
        if (carry > 0) {
            // Define your desired behavior here
            // For example, you might throw an exception or reset the IP to all zeros
            // Here, we will just return a special string to indicate overflow
            return "Overflow occurred";
        }

        // Build the resulting IP address
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < 4; i++) {
            result.append(ipArray[i]);
            if (i < 3) {
                result.append(".");
            }
        }

        return result.toString();
    }

    public static String getEnderecoRede(String enderecoIPCIDR) {
        try {
            // Separa o endereço IP e o prefixo CIDR
            String[] parts = enderecoIPCIDR.split("/");
            String enderecoIP = parts[0];
            int prefixo = Integer.parseInt(parts[1]);

            // Converte o endereço IP em bytes
            byte[] enderecoIPBytes = InetAddress.getByName(enderecoIP).getAddress();

            // Calcula a máscara de rede a partir do prefixo CIDR
            byte[] mascaraRedeBytes = getMascaraRedeCIDR(prefixo);

            // Calcula o endereço de rede
            byte[] enderecoRedeBytes = new byte[enderecoIPBytes.length];
            for (int i = 0; i < enderecoIPBytes.length; i++) {
                enderecoRedeBytes[i] = (byte) (enderecoIPBytes[i] & mascaraRedeBytes[i]);
            }

            // Retorna o endereço de rede como string
            return InetAddress.getByAddress(enderecoRedeBytes).getHostAddress();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getEnderecoBroadcast(String enderecoIPCIDR) {
        try {
            // Separa o endereço IP e o prefixo CIDR
            String[] parts = enderecoIPCIDR.split("/");
            String enderecoIP = parts[0];
            int prefixo = Integer.parseInt(parts[1]);

            // Converte o endereço IP em bytes
            byte[] enderecoIPBytes = InetAddress.getByName(enderecoIP).getAddress();

            // Calcula a máscara de rede a partir do prefixo CIDR
            byte[] mascaraRedeBytes = getMascaraRedeCIDR(prefixo);

            // Calcula o endereço de broadcast
            byte[] enderecoBroadcastBytes = new byte[enderecoIPBytes.length];
            for (int i = 0; i < enderecoIPBytes.length; i++) {
                enderecoBroadcastBytes[i] = (byte) ((enderecoIPBytes[i] & mascaraRedeBytes[i]) | (~mascaraRedeBytes[i] & 0xFF));
            }

            // Retorna o endereço de broadcast como string
            return InetAddress.getByAddress(enderecoBroadcastBytes).getHostAddress();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getUsableHostIPRange(String enderecoIPCIDR, Context context) {
        try {
            // Separa o endereço IP e o prefixo CIDR
            String[] parts = enderecoIPCIDR.split("/");
            String enderecoIP = parts[0];
            int prefixo = Integer.parseInt(parts[1]);

            // Converte o endereço IP em bytes
            byte[] enderecoIPBytes = InetAddress.getByName(enderecoIP).getAddress();

            // Calcula a máscara de rede a partir do prefixo CIDR
            byte[] mascaraRedeBytes = getMascaraRedeCIDR(prefixo);

            // Calcula o endereço de rede
            byte[] enderecoRedeBytes = new byte[enderecoIPBytes.length];
            for (int i = 0; i < enderecoIPBytes.length; i++) {
                enderecoRedeBytes[i] = (byte) (enderecoIPBytes[i] & mascaraRedeBytes[i]);
            }

            // Verifica se é uma máscara /31 ou /32
            if (prefixo == 31 || prefixo == 32) {
                return context.getString(R.string.usable_range_none);
            }

            // Calcula o endereço de broadcast
            byte[] enderecoBroadcastBytes = new byte[enderecoIPBytes.length];
            for (int i = 0; i < enderecoIPBytes.length; i++) {
                enderecoBroadcastBytes[i] = (byte) ((enderecoIPBytes[i] & mascaraRedeBytes[i]) | (~mascaraRedeBytes[i] & 0xFF));
            }

            // Obtém o primeiro endereço IP utilizável
            byte[] primeiroEnderecoUtilizavelBytes = enderecoRedeBytes.clone();
            primeiroEnderecoUtilizavelBytes[enderecoRedeBytes.length - 1]++;

            // Obtém o último endereço IP utilizável
            byte[] ultimoEnderecoUtilizavelBytes = enderecoBroadcastBytes.clone();
            ultimoEnderecoUtilizavelBytes[enderecoBroadcastBytes.length - 1]--;

            // Obtém o range de IPs utilizáveis em formato de string
            String primeiroEnderecoUtilizavel = InetAddress.getByAddress(primeiroEnderecoUtilizavelBytes).getHostAddress();
            String ultimoEnderecoUtilizavel = InetAddress.getByAddress(ultimoEnderecoUtilizavelBytes).getHostAddress();
            return primeiroEnderecoUtilizavel + " - " + ultimoEnderecoUtilizavel;
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static long getNumeroTotalHosts(String enderecoIPCIDR) {
        try {
            // Separa o endereço IP e o prefixo CIDR
            String[] parts = enderecoIPCIDR.split("/");
            int prefixo = Integer.parseInt(parts[1]);

            // Calcula o número de hosts possíveis na rede
            return (long) Math.pow(2, 32 - prefixo);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static long getNumeroHostsUtilizaveis(String enderecoIPCIDR) {
        try {
            // Separa o endereço IP e o prefixo CIDR
            String[] parts = enderecoIPCIDR.split("/");
            int prefixo = Integer.parseInt(parts[1]);

            // Verifica se o prefixo é /32
            if (prefixo == 32) {
                return 0;
            }

            // Calcula o número de hosts possíveis na rede, desconsiderando o endereço de rede e o de broadcast
            long numeroHostsPossiveis = (long) Math.pow(2, 32 - prefixo);
            return numeroHostsPossiveis - 2;
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return 0;
    }

    private static byte[] getMascaraRedeCIDR(int prefixo) {
        int numBitsMascara = 0xFFFFFFFF << (32 - prefixo);
        byte[] mascaraRedeBytes = new byte[4];
        mascaraRedeBytes[0] = (byte) ((numBitsMascara & 0xFF000000) >> 24);
        mascaraRedeBytes[1] = (byte) ((numBitsMascara & 0x00FF0000) >> 16);
        mascaraRedeBytes[2] = (byte) ((numBitsMascara & 0x0000FF00) >> 8);
        mascaraRedeBytes[3] = (byte) (numBitsMascara & 0x000000FF);
        return mascaraRedeBytes;
    }
}