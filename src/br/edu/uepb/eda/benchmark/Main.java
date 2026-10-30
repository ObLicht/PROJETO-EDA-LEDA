package br.edu.uepb.eda.benchmark;

import br.edu.uepb.eda.search.SearchAlgorithms;
import br.edu.uepb.eda.sorting.BubbleSort;
import br.edu.uepb.eda.sorting.CountingSort;
import br.edu.uepb.eda.sorting.InsertionSort;
import br.edu.uepb.eda.sorting.MergeSort;
import br.edu.uepb.eda.sorting.QuickSort;
import br.edu.uepb.eda.sorting.SelectionSort;
import br.edu.uepb.eda.util.Cenario;
import br.edu.uepb.eda.util.DataGenerator;
import br.edu.uepb.eda.util.Verificador;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Benchmark do Projeto da Unidade 1 (EDA/LEDA 2026.2).
 *
 * Mede com System.nanoTime(), 25 execucoes por combinacao
 * (algoritmo x tamanho x cenario), descarta as 5 primeiras (warm-up) e
 * calcula media, desvio padrao, minimo e maximo das 20 restantes.
 *
 * Uso:
 *   java -Xss512m -cp out br.edu.uepb.eda.benchmark.Main
 *   java -Xss512m -cp out br.edu.uepb.eda.benchmark.Main --rapido
 *   java -Xss512m -cp out br.edu.uepb.eda.benchmark.Main --ord=5000,15000,50000 --busca=50000,150000,500000
 *   java -Xss512m -cp out br.edu.uepb.eda.benchmark.Main --csv=resultados.csv
 *
 * Tudo roda dentro de uma Thread com pilha grande (QuickSort slide em vetor
 * ordenado/inverso e a busca linear recursiva tem recursao de profundidade n).
 */
public class Main {

    static final int EXECUCOES = 25;
    static final int WARMUP = 5;

    // Tamanhos padrao
    static int[] tamanhosOrdenacao = {5_000, 15_000, 50_000};
    static int[] tamanhosBusca = {50_000, 150_000, 500_000};
    static String arquivoCsv = "resultados.csv";

    // Evita que o JIT elimine as chamadas de busca por "codigo morto"
    static volatile long sumidouro = 0;

    interface Ordenacao { void ordenar(int[] arr); }
    interface Busca { int buscar(int[] arr, int alvo); }

    /** Resultado estatistico de uma combinacao. Tempos em nanossegundos. */
    static class Estatistica {
        final String algoritmo, cenario, tipo;
        final int tamanho;
        final double media, desvio, min, max;

        Estatistica(String algoritmo, String cenario, String tipo, int tamanho, double[] ns) {
            this.algoritmo = algoritmo;
            this.cenario = cenario;
            this.tipo = tipo;
            this.tamanho = tamanho;
            double soma = 0, mn = Double.MAX_VALUE, mx = -Double.MAX_VALUE;
            for (double v : ns) {
                soma += v;
                mn = Math.min(mn, v);
                mx = Math.max(mx, v);
            }
            this.media = soma / ns.length;
            double q = 0;
            for (double v : ns) q += (v - media) * (v - media);
            this.desvio = ns.length > 1 ? Math.sqrt(q / (ns.length - 1)) : 0;
            this.min = mn;
            this.max = mx;
        }
    }

    public static void main(String[] args) throws Exception {
        for (String a : args) {
            if (a.equals("--rapido")) {
                tamanhosOrdenacao = new int[]{500, 1_500, 5_000};
                tamanhosBusca = new int[]{5_000, 15_000, 50_000};
            } else if (a.startsWith("--ord=")) {
                tamanhosOrdenacao = parseTamanhos(a.substring(6));
            } else if (a.startsWith("--busca=")) {
                tamanhosBusca = parseTamanhos(a.substring(8));
            } else if (a.startsWith("--csv=")) {
                arquivoCsv = a.substring(6);
            } else {
                System.err.println("Argumento desconhecido: " + a);
                System.exit(1);
            }
        }

        final Throwable[] erro = new Throwable[1];
        Thread t = new Thread(null, () -> {
            try {
                executar();
            } catch (Throwable e) {
                erro[0] = e;
            }
        }, "benchmark", 1L << 29); // pilha de 512 MB
        t.start();
        t.join();
        if (erro[0] != null) {
            erro[0].printStackTrace();
            System.exit(1);
        }
    }

    static int[] parseTamanhos(String s) {
        String[] p = s.split(",");
        int[] r = new int[p.length];
        for (int i = 0; i < p.length; i++) r[i] = Integer.parseInt(p[i].trim());
        return r;
    }

    static void executar() throws IOException {
        List<Estatistica> resultados = new ArrayList<>();

        System.out.println("=== PROJETO UNIDADE 1 - EDA/LEDA 2026.2 ===");
        System.out.println("Execucoes por combinacao: " + EXECUCOES + " (" + WARMUP
                + " de warm-up descartadas; estatisticas sobre " + (EXECUCOES - WARMUP) + ")");
        System.out.println("Tamanhos (ordenacao): " + Arrays.toString(tamanhosOrdenacao));
        System.out.println("Tamanhos (busca):     " + Arrays.toString(tamanhosBusca));
        System.out.println();

        benchmarkOrdenacao(resultados);
        benchmarkBusca(resultados);

        salvarCsv(resultados);
        System.out.println("\nResultados salvos em: " + arquivoCsv);
    }

    // =========================================================================
    // Ordenacao
    // =========================================================================

    static Map<String, Ordenacao> algoritmosOrdenacao() {
        Map<String, Ordenacao> m = new LinkedHashMap<>();
        m.put("BubbleSort (slide)", BubbleSort::sortSlide);
        m.put("BubbleSort (otimizado)", BubbleSort::sortOtimizado);
        m.put("SelectionSort (slide)", SelectionSort::sortSlide);
        m.put("SelectionSort (estavel)", SelectionSort::sortEstavel);
        m.put("InsertionSort", InsertionSort::sort);
        m.put("MergeSort (classico)", MergeSort::sortClassico);
        m.put("QuickSort (slide)", QuickSort::sortSlide);
        m.put("QuickSort (slide+shuffle)", QuickSort::sortShuffle);
        m.put("CountingSort", CountingSort::sort);
        m.put("Arrays.sort (referencia)", Arrays::sort);
        return m;
    }

    static void benchmarkOrdenacao(List<Estatistica> out) {
        Map<String, Ordenacao> algoritmos = algoritmosOrdenacao();

        for (int n : tamanhosOrdenacao) {
            for (Cenario cenario : Cenario.values()) {
                // Vetor mestre: gerado UMA vez por (tamanho, cenario)
                int[] mestre = DataGenerator.gerar(cenario, n);
                System.out.println("--- ORDENACAO | n = " + n + " | " + cenario + " ---");

                for (Map.Entry<String, Ordenacao> e : algoritmos.entrySet()) {
                    double[] tempos = new double[EXECUCOES - WARMUP];
                    for (int exec = 0; exec < EXECUCOES; exec++) {
                        int[] arr = DataGenerator.copyOf(mestre);   // fora do tempo

                        long t0 = System.nanoTime();
                        e.getValue().ordenar(arr);
                        long dt = System.nanoTime() - t0;

                        if (!Verificador.estaOrdenado(arr)) {         // fora do tempo
                            throw new IllegalStateException("Nao ordenou: " + e.getKey()
                                    + " n=" + n + " cenario=" + cenario);
                        }
                        if (exec >= WARMUP) tempos[exec - WARMUP] = dt;
                    }
                    Estatistica est = new Estatistica(e.getKey(), cenario.name(), "ordenacao", n, tempos);
                    out.add(est);
                    System.out.printf(Locale.US, "  %-28s media = %12.3f ms  (dp %10.3f | min %10.3f | max %10.3f)%n",
                            est.algoritmo, est.media / 1e6, est.desvio / 1e6, est.min / 1e6, est.max / 1e6);
                }
                System.out.println();
            }
        }
    }

    // =========================================================================
    // Busca
    // =========================================================================

    static Map<String, Busca> algoritmosBusca() {
        Map<String, Busca> m = new LinkedHashMap<>();
        m.put("Linear iterativa", SearchAlgorithms::linearIterativa);
        m.put("Linear recursiva", SearchAlgorithms::linearRecursiva);
        m.put("Linear duas pontas", SearchAlgorithms::linearDuasPontas);
        m.put("Binaria iterativa", SearchAlgorithms::binariaIterativa);
        m.put("Binaria recursiva", SearchAlgorithms::binariaRecursiva);
        return m;
    }

    /**
     * Cada busca dura micro/nanossegundos, entao cada execucao cronometrada
     * repete a busca 'reps' vezes e o tempo reportado e o tempo por busca.
     * Lineares: ~5e7 acessos por execucao; binarias: 1.000.000 de buscas.
     */
    static int repeticoes(String nome, int n) {
        if (nome.startsWith("Binaria")) return 1_000_000;
        return Math.max(1, 50_000_000 / n);
    }

    static void benchmarkBusca(List<Estatistica> out) {
        Map<String, Busca> algoritmos = algoritmosBusca();

        for (int n : tamanhosBusca) {
            int[] ordenado = DataGenerator.ordenado(n);
            System.out.println("--- BUSCA | n = " + n + " | vetor ordenado | alvo = "
                    + DataGenerator.ALVO_AUSENTE + " ---");

            for (Map.Entry<String, Busca> e : algoritmos.entrySet()) {
                int reps = repeticoes(e.getKey(), n);
                double[] tempos = new double[EXECUCOES - WARMUP];

                for (int exec = 0; exec < EXECUCOES; exec++) {
                    long acc = 0;
                    long t0 = System.nanoTime();
                    for (int r = 0; r < reps; r++) {
                        acc += e.getValue().buscar(ordenado, DataGenerator.ALVO_AUSENTE);
                    }
                    long dt = System.nanoTime() - t0;
                    sumidouro += acc;

                    // Verificacao (fora do tempo): alvo ausente -> -1
                    if (e.getValue().buscar(ordenado, DataGenerator.ALVO_AUSENTE) != -1) {
                        throw new IllegalStateException("Busca incorreta: " + e.getKey() + " n=" + n);
                    }
                    if (exec >= WARMUP) tempos[exec - WARMUP] = (double) dt / reps; // ns por busca
                }
                Estatistica est = new Estatistica(e.getKey(), "ORDENADO", "busca", n, tempos);
                out.add(est);
                System.out.printf(Locale.US, "  %-20s media = %12.3f us/busca  (dp %9.3f | min %9.3f | max %9.3f)  [reps=%d]%n",
                        est.algoritmo, est.media / 1e3, est.desvio / 1e3, est.min / 1e3, est.max / 1e3, reps);
            }
            System.out.println();
        }
    }

    // =========================================================================
    // CSV
    // =========================================================================

    /** Ordenacao em ms; busca em us por busca (coluna 'unidade'). */
    static void salvarCsv(List<Estatistica> resultados) throws IOException {
        try (PrintWriter pw = new PrintWriter(arquivoCsv, "UTF-8")) {
            pw.println("tipo,algoritmo,tamanho,cenario,unidade,media,desvio_padrao,minimo,maximo");
            for (Estatistica e : resultados) {
                boolean ord = e.tipo.equals("ordenacao");
                double div = ord ? 1e6 : 1e3;
                String un = ord ? "ms" : "us_por_busca";
                pw.printf(Locale.US, "%s,\"%s\",%d,%s,%s,%.6f,%.6f,%.6f,%.6f%n",
                        e.tipo, e.algoritmo, e.tamanho, e.cenario, un,
                        e.media / div, e.desvio / div, e.min / div, e.max / div);
            }
        }
    }
}