package br.edu.uepb.eda.search;

/**
 * PARTE RECURSIVA das buscas, adaptada de Estudante[] para int[] (enunciado: apenas int[]).
 *
 * COMO USAR: copie os dois metodos publicos (e seus helpers) para dentro da SearchAlgorithms
 * da dupla. Os nomes e a assinatura (arr, alvo) seguem o padrao do projeto antigo, entao
 * funcionam como sobrecarga ao lado das versoes de Estudante[].
 *
 * PRE-CONDICAO: para a busca binaria, o array deve estar previamente ordenado (crescente).
 * Retorno: indice do elemento encontrado, ou -1 se nao presente.
 */
public class SearchAlgorithms {

    private SearchAlgorithms() {}

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
