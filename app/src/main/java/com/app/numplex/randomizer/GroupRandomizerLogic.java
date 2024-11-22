package com.app.numplex.randomizer;

import java.util.*;

public class GroupRandomizerLogic {
    private static final Random gerador = new Random();

    public static List<List<String>> divideIntoGroups(List<String> elementos, int numeroDeGrupos) {
        if (elementos == null || elementos.isEmpty()) {
            throw new IllegalArgumentException("A lista de elementos não pode ser nula ou vazia");
        }
        if (numeroDeGrupos <= 0) {
            throw new IllegalArgumentException("O número de grupos deve ser maior que zero");
        }
        if (numeroDeGrupos > elementos.size()) {
            throw new IllegalArgumentException("O número de grupos não pode ser maior que o número de elementos");
        }

        List<List<String>> grupos = new ArrayList<>();
        for (int i = 0; i < numeroDeGrupos; i++) {
            grupos.add(new ArrayList<>());
        }

        Collections.shuffle(elementos, gerador);
        int indiceDoGrupo = 0;
        for (String elemento : elementos) {
            grupos.get(indiceDoGrupo).add(elemento);
            indiceDoGrupo = (indiceDoGrupo + 1) % numeroDeGrupos;
        }

        return grupos;
    }
}


