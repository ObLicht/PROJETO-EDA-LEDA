package br.edu.uepb.eda.sorting;

/**
 * SelectionSort sobre int[] em duas versoes:
 *
 * VERSAO SLIDE: acha o minimo do sub-array restante e troca com a posicao
 * atual. O(n^2) em todos os casos; no maximo n-1 trocas. NAO estavel, pois
 * a troca de longa distancia pode passar um elemento por cima de um igual.
 *
 * VERSAO ESTAVEL: em vez de trocar, desloca (shift) os elementos entre a
 * posicao atual e o minimo uma casa para a direita e insere o minimo na
 * posicao atual. Preserva a ordem relativa de iguais. Continua O(n^2),
 * mas com O(n^2) escritas no pior caso.
 *
 * Ordem: crescente.
 */
public class SelectionSort {

    private SelectionSort() {}

    // --- Versao Slide ---------------------------------------------------------

    public static void sortSlide(int[] arr) {
        int n = arr.length;
        for (int i = 0; i < n - 1; i++) {
            int minIdx = i;
            for (int j = i + 1; j < n; j++) {
                if (arr[j] < arr[minIdx]) {
                    minIdx = j;
                }
            }
            if (minIdx != i) {
                int tmp = arr[i];
                arr[i] = arr[minIdx];
                arr[minIdx] = tmp;
            }
        }
    }

    // --- Versao Estavel -------------------------------------------------------

    public static void sortEstavel(int[] arr) {
        int n = arr.length;
        for (int i = 0; i < n - 1; i++) {
            int minIdx = i;
            // '<' estrito: em empate, fica o primeiro (mais a esquerda)
            for (int j = i + 1; j < n; j++) {
                if (arr[j] < arr[minIdx]) {
                    minIdx = j;
                }
            }
            int min = arr[minIdx];
            while (minIdx > i) {
                arr[minIdx] = arr[minIdx - 1];
                minIdx--;
            }
            arr[i] = min;
        }
    }
}
