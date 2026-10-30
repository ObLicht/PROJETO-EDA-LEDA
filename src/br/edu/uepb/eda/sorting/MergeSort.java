package br.edu.uepb.eda.sorting;

/**
 * MergeSort classico sobre int[]: divide ao meio, ordena cada metade
 * recursivamente e intercala (merge) com arrays auxiliares.
 *
 *   - Recorrencia: T(n) = 2T(n/2) + Theta(n)  ->  Theta(n log n) em todos
 *     os casos (Metodo Mestre, caso 2).
 *   - Memoria auxiliar: O(n) (copias L e R a cada merge).
 *   - Estavel: o '<=' no merge prefere o elemento da esquerda em empate.
 *
 * Ordem: crescente.
 */
public class MergeSort {

    private MergeSort() {}

    public static void sortClassico(int[] arr) {
        if (arr.length <= 1) return;
        mergeSort(arr, 0, arr.length - 1);
    }

    private static void mergeSort(int[] arr, int esq, int dir) {
        if (esq >= dir) return;
        int meio = esq + (dir - esq) / 2;
        mergeSort(arr, esq, meio);
        mergeSort(arr, meio + 1, dir);
        merge(arr, esq, meio, dir);
    }

    private static void merge(int[] arr, int esq, int meio, int dir) {
        int tamEsq = meio - esq + 1;
        int tamDir = dir - meio;

        int[] L = new int[tamEsq];
        int[] R = new int[tamDir];
        System.arraycopy(arr, esq, L, 0, tamEsq);
        System.arraycopy(arr, meio + 1, R, 0, tamDir);

        int i = 0, j = 0, k = esq;
        while (i < tamEsq && j < tamDir) {
            if (L[i] <= R[j]) arr[k++] = L[i++];
            else              arr[k++] = R[j++];
        }
        while (i < tamEsq) arr[k++] = L[i++];
        while (j < tamDir) arr[k++] = R[j++];
    }
}
