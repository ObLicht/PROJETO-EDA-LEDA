package br.edu.uepb.eda.sorting;

import java.util.Random;

/**
 * QuickSort sobre int[] em duas versoes (Arrays.sort(int[]) e usado direto
 * como referencia, sem implementacao propria):
 *
 * VERSAO SLIDE: pivo = ultimo elemento, particao de Lomuto.
 *   - Medio caso O(n log n).
 *   - Pior caso O(n^2) com vetor ja ordenado ou inverso: o pivo sempre cai
 *     na borda, a particao fica desbalanceada e a PROFUNDIDADE DA RECURSAO
 *     chega a n. Para n grande isso estoura a pilha: rode a JVM com -Xss
 *     maior (ex.: -Xss512m) ou execute dentro de uma Thread com stack grande.
 *   - NAO estavel.
 *
 * VERSAO SLIDE + SHUFFLE: embaralha (Fisher-Yates, O(n)) antes de ordenar,
 * tornando o pior caso improvavel mesmo em vetor ordenado ou inverso.
 * O custo do embaralhamento entra no tempo medido (faz parte do algoritmo).
 *
 * Ordem: crescente.
 */
public class QuickSort {

    private static final Random RNG = new Random(42L);

    private QuickSort() {}

    // --- Versao Slide ---------------------------------------------------------

    public static void sortSlide(int[] arr) {
        quickSort(arr, 0, arr.length - 1);
    }

    // --- Versao Slide + Shuffle -----------------------------------------------

    public static void sortShuffle(int[] arr) {
        shuffle(arr);
        quickSort(arr, 0, arr.length - 1);
    }

    // --- Nucleo ---------------------------------------------------------------

    private static void quickSort(int[] arr, int esq, int dir) {
        if (esq >= dir) return;
        int p = particionarLomuto(arr, esq, dir);
        quickSort(arr, esq, p - 1);
        quickSort(arr, p + 1, dir);
    }

    private static int particionarLomuto(int[] arr, int esq, int dir) {
        int pivo = arr[dir];
        int i = esq - 1;
        for (int j = esq; j < dir; j++) {
            if (arr[j] <= pivo) {
                i++;
                swap(arr, i, j);
            }
        }
        swap(arr, i + 1, dir);
        return i + 1;
    }

    private static void shuffle(int[] arr) {
        for (int i = arr.length - 1; i > 0; i--) {
            int j = RNG.nextInt(i + 1);
            swap(arr, i, j);
        }
    }

    private static void swap(int[] arr, int i, int j) {
        int tmp = arr[i];
        arr[i] = arr[j];
        arr[j] = tmp;
    }
}
