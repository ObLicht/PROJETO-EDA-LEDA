package br.edu.uepb.eda.search;

/**
 * Cinco algoritmos de busca sobre int[].
 *
 * PRE-CONDICAO: o enunciado considera o vetor previamente ordenado (crescente).
 * As buscas lineares nao exigem ordenacao, mas a binaria sim: busca binaria em
 * vetor nao ordenado produz resultado indefinido.
 *
 * Retorno: indice do elemento encontrado, ou -1 se nao presente.
 */
public class SearchAlgorithms {

    private SearchAlgorithms() {}

    // -------------------------------------------------------------------------
    // Busca Linear Iterativa
    // -------------------------------------------------------------------------

    /**
     * Percorre o array posicao a posicao do inicio ao fim.
     * Complexidade: O(n) em todos os casos.
     * Nao exige vetor ordenado.
     */
    public static int linearIterativa(int[] arr, int alvo) {
        for (int i = 0; i < arr.length; i++) {
            if (arr[i] == alvo) return i;
        }
        return -1;
    }

    // -------------------------------------------------------------------------
    // Busca Linear Recursiva
    // -------------------------------------------------------------------------

    /**
     * Variante recursiva da busca linear. Recorrencia: T(n) = T(n-1) + Theta(1) -> O(n).
     * Consome O(n) na pilha de recursao (risco de StackOverflow para n grande;
     * rodar a JVM com pilha aumentada, ex.: -Xss512m).
     * Nao exige vetor ordenado.
     */
    public static int linearRecursiva(int[] arr, int alvo) {
        return linearRecursivaHelper(arr, alvo, 0);
    }

    private static int linearRecursivaHelper(int[] arr, int alvo, int idx) {
        if (idx >= arr.length) return -1;
        if (arr[idx] == alvo) return idx;
        return linearRecursivaHelper(arr, alvo, idx + 1);
    }

    // -------------------------------------------------------------------------
    // Busca Linear Iterativa Duas Pontas
    // -------------------------------------------------------------------------

    /**
     * Percorre o array simultaneamente a partir das duas extremidades.
     * Encontra o elemento em no maximo n/2 iteracoes.
     *
     * Complexidade: O(n) - a constante e reduzida pela metade na media,
     * mas a ordem assintotica permanece linear.
     * Nao exige vetor ordenado.
     *
     * ATENCAO: se ambas as pontas apontarem para o alvo ao mesmo tempo,
     * retorna o indice da ponta esquerda (prioridade no if).
     */
    public static int linearDuasPontas(int[] arr, int alvo) {
        int esq = 0, dir = arr.length - 1;
        while (esq <= dir) {
            if (arr[esq] == alvo) return esq;
            if (arr[dir] == alvo) return dir;
            esq++;
            dir--;
        }
        return -1;
    }

    // -------------------------------------------------------------------------
    // Busca Binaria Iterativa
    // -------------------------------------------------------------------------

    /**
     * Divide o intervalo de busca ao meio a cada iteracao.
     * Complexidade: O(log n) - exige vetor ordenado.
     * Memoria auxiliar: O(1).
     */
    public static int binariaIterativa(int[] arr, int alvo) {
        int esq = 0, dir = arr.length - 1;
        while (esq <= dir) {
            int meio = esq + (dir - esq) / 2; // evita overflow vs (esq+dir)/2
            if (arr[meio] == alvo)     return meio;
            else if (arr[meio] < alvo) esq = meio + 1;
            else                       dir = meio - 1;
        }
        return -1;
    }

    // -------------------------------------------------------------------------
    // Busca Binaria Recursiva
    // -------------------------------------------------------------------------

    /**
     * Variante recursiva da busca binaria. Recorrencia: T(n) = T(n/2) + Theta(1) -> O(log n)
     * (Metodo Mestre, caso 2). Exige vetor ordenado.
     * Memoria auxiliar: O(log n) na pilha de recursao.
     */
    public static int binariaRecursiva(int[] arr, int alvo) {
        return binariaRecursivaHelper(arr, alvo, 0, arr.length - 1);
    }

    private static int binariaRecursivaHelper(int[] arr, int alvo, int esq, int dir) {
        if (esq > dir) return -1;
        int meio = esq + (dir - esq) / 2;
        if (arr[meio] == alvo)     return meio;
        else if (arr[meio] < alvo) return binariaRecursivaHelper(arr, alvo, meio + 1, dir);
        else                       return binariaRecursivaHelper(arr, alvo, esq, meio - 1);
    }
}
