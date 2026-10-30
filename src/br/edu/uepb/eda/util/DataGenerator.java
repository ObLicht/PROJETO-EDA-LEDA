package br.edu.uepb.eda.util;

import java.util.Arrays;
import java.util.Random;

/**
 * Gera int[] nos quatro cenarios do projeto, sempre com valores no
 * intervalo [1, 100.000] (repeticoes permitidas).
 *
 * Todos os cenarios de um mesmo tamanho partem da MESMA base aleatoria
 * (mesma semente), entao ORDENADO, INVERSO e PARCIALMENTE_ORDENADO contem
 * exatamente os mesmos valores do ALEATORIO, apenas em outra ordem.
 *
 * A semente fixa (SEED) garante reproducibilidade dos experimentos.
 *
 * Uso tipico: gere o vetor mestre UMA vez por (tamanho, cenario) e passe
 * copyOf(mestre) para cada algoritmo, nunca o mestre em si.
 */
public class DataGenerator {

    public static final long SEED = 42L;
    public static final int MIN_VALOR = 1;
    public static final int MAX_VALOR = 100_000;

    /** Fracao de posicoes embaralhadas no cenario parcialmente ordenado. */
    public static final double FRACAO_PARCIAL = 0.15;

    /** Alvo das buscas: fora de [1, 100.000], portanto sempre ausente. */
    public static final int ALVO_AUSENTE = -1;

    private DataGenerator() {}

    /** Ponto de entrada unico: gera o vetor de tamanho n no cenario pedido. */
    public static int[] gerar(Cenario cenario, int n) {
        switch (cenario) {
            case ORDENADO:              return ordenado(n);
            case INVERSO:               return inverso(n);
            case PARCIALMENTE_ORDENADO: return parcialmenteOrdenado(n);
            case ALEATORIO:
            default:                    return aleatorio(n);
        }
    }

    // --- Cenarios -------------------------------------------------------------

    /** Valores aleatorios uniformes em [1, 100.000]. */
    public static int[] aleatorio(int n) {
        Random rng = new Random(SEED);
        int[] arr = new int[n];
        for (int i = 0; i < n; i++) {
            arr[i] = MIN_VALOR + rng.nextInt(MAX_VALOR - MIN_VALOR + 1);
        }
        return arr;
    }

    /** Mesma base aleatoria, em ordem crescente. Tambem serve de entrada para as buscas. */
    public static int[] ordenado(int n) {
        int[] arr = aleatorio(n);
        Arrays.sort(arr);
        return arr;
    }

    /** Mesma base aleatoria, em ordem decrescente. */
    public static int[] inverso(int n) {
        int[] arr = ordenado(n);
        for (int i = 0, j = arr.length - 1; i < j; i++, j--) {
            int tmp = arr[i]; arr[i] = arr[j]; arr[j] = tmp;
        }
        return arr;
    }

    /**
     * Parte do vetor ordenado, sorteia ~15% das posicoes (exatamente
     * round(0.15 * n), sem repeticao) e embaralha apenas os valores dessas
     * posicoes entre si. As demais posicoes nao sao tocadas.
     */
    public static int[] parcialmenteOrdenado(int n) {
        int[] arr = ordenado(n);
        int k = (int) Math.round(FRACAO_PARCIAL * n);
        if (k < 2) return arr;

        // Semente derivada para nao correlacionar com a base aleatoria
        Random rng = new Random(SEED + 1);

        // Sorteio de k posicoes distintas: Fisher-Yates parcial sobre os indices
        int[] indices = new int[n];
        for (int i = 0; i < n; i++) indices[i] = i;
        for (int i = 0; i < k; i++) {
            int j = i + rng.nextInt(n - i);
            int tmp = indices[i]; indices[i] = indices[j]; indices[j] = tmp;
        }

        // Embaralha os valores dessas k posicoes (Fisher-Yates sobre o subconjunto)
        for (int i = k - 1; i > 0; i--) {
            int j = rng.nextInt(i + 1);
            int a = indices[i], b = indices[j];
            int tmp = arr[a]; arr[a] = arr[b]; arr[b] = tmp;
        }
        return arr;
    }

    // --- Utilitario -----------------------------------------------------------

    /** Copia defensiva: nunca reutilize a saida ordenada de um algoritmo como entrada de outro. */
    public static int[] copyOf(int[] src) {
        return Arrays.copyOf(src, src.length);
    }
}
