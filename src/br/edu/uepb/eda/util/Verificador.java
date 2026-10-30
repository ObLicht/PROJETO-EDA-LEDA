package br.edu.uepb.eda.util;

/**
 * Verificacao de correcao dos resultados. Chame FORA do trecho cronometrado.
 */
public class Verificador {

    private Verificador() {}

    /** true se arr esta em ordem crescente (nao decrescente). */
    public static boolean estaOrdenado(int[] arr) {
        for (int i = 1; i < arr.length; i++) {
            if (arr[i - 1] > arr[i]) return false;
        }
        return true;
    }
}
