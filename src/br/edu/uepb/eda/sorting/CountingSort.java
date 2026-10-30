package br.edu.uepb.eda.sorting;

/**
 * CountingSort sobre int[] com valores no intervalo [1, 100.000]
 * (dominio exigido pelo enunciado).
 *
 * Nao e um algoritmo de comparacao: conta a frequencia de cada valor,
 * acumula as contagens e reconstroi o vetor.
 *
 *   - Complexidade: Theta(n + k), com k = 100.000 (amplitude do dominio).
 *     Muito rapido quando n >> k; com n pequeno, o custo de percorrer o
 *     array de contagem (k posicoes) domina.
 *   - Memoria auxiliar: O(n + k).
 *   - Estavel (saida montada da direita para a esquerda), embora para int
 *     primitivo isso nao seja observavel.
 *   - Limitacao: so funciona com inteiros de amplitude conhecida e pequena.
 *
 * Ordem: crescente.
 */
public class CountingSort {

    public static final int MIN_VALOR = 1;
    public static final int MAX_VALOR = 100_000;

    private CountingSort() {}

    public static void sort(int[] arr) {
        int n = arr.length;
        int k = MAX_VALOR - MIN_VALOR + 1;
        int[] contagem = new int[k];

        // 1. Frequencia de cada valor
        for (int v : arr) {
            contagem[v - MIN_VALOR]++;
        }

        // 2. Acumula: contagem[i] = quantos elementos sao <= (i + MIN_VALOR)
        for (int i = 1; i < k; i++) {
            contagem[i] += contagem[i - 1];
        }

        // 3. Monta a saida da direita para a esquerda (estabilidade)
        int[] saida = new int[n];
        for (int i = n - 1; i >= 0; i--) {
            saida[--contagem[arr[i] - MIN_VALOR]] = arr[i];
        }

        // 4. Copia de volta
        System.arraycopy(saida, 0, arr, 0, n);
    }
}
