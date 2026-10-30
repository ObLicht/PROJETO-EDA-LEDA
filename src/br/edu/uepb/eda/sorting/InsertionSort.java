package br.edu.uepb.eda.sorting;

/**
 * InsertionSort sobre int[] (versao classica, com shift).
 *
 * Mantem o prefixo da esquerda ordenado e insere o proximo elemento na
 * posicao correta deslocando os maiores uma casa para a direita.
 *
 *   - Melhor caso O(n): vetor ja ordenado (nenhum shift).
 *   - Medio e pior caso O(n^2).
 *   - Estavel: o shift para ao encontrar elemento menor ou igual.
 *
 * Ordem: crescente.
 */
public class InsertionSort {

    private InsertionSort() {}

    public static void sort(int[] arr) {
        int n = arr.length;
        for (int i = 1; i < n; i++) {
            int chave = arr[i];
            int j = i - 1;
            while (j >= 0 && arr[j] > chave) {
                arr[j + 1] = arr[j];
                j--;
            }
            arr[j + 1] = chave;
        }
    }
}
