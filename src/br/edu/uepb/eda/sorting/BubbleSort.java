package br.edu.uepb.eda.sorting;

/**
 * BubbleSort sobre int[] em duas versoes:
 *
 * VERSAO SLIDE: sempre executa n-1 passagens completas, mesmo com o vetor
 * ja ordenado. O(n^2) em todos os casos. Estavel.
 *
 * VERSAO OTIMIZADA: usa a flag 'trocou' para parar assim que uma passagem
 * termina sem nenhuma troca. Melhor caso O(n) (vetor ja ordenado);
 * medio e pior caso continuam O(n^2). Estavel.
 *
 * Ordem: crescente.
 */
public class BubbleSort {

    private BubbleSort() {}

    // --- Versao Slide ---------------------------------------------------------

    public static void sortSlide(int[] arr) {
        int n = arr.length;
        for (int i = 0; i < n - 1; i++) {
            for (int j = 0; j < n - 1 - i; j++) {
                if (arr[j] > arr[j + 1]) {
                    swap(arr, j, j + 1);
                }
            }
        }
    }

    // --- Versao Otimizada -----------------------------------------------------

    public static void sortOtimizado(int[] arr) {
        int n = arr.length;
        for (int i = 0; i < n - 1; i++) {
            boolean trocou = false;
            for (int j = 0; j < n - 1 - i; j++) {
                if (arr[j] > arr[j + 1]) {
                    swap(arr, j, j + 1);
                    trocou = true;
                }
            }
            if (!trocou) break; // nenhuma troca: vetor ja ordenado
        }
    }

    private static void swap(int[] arr, int i, int j) {
        int tmp = arr[i];
        arr[i] = arr[j];
        arr[j] = tmp;
    }
}
