# EDA / LEDA 2026.2 — Projeto da Unidade 1
Análise de algoritmos de busca e ordenação em Java, exclusivamente com `int[]`
(valores em [1, 100.000], repetições permitidas).

## Estrutura

```
src/br/edu/uepb/eda/
├── sorting/
│   ├── BubbleSort.java       sortSlide, sortOtimizado
│   ├── SelectionSort.java    sortSlide, sortEstavel
│   ├── InsertionSort.java    sort
│   ├── MergeSort.java        sortClassico
│   ├── QuickSort.java        sortSlide, sortShuffle
│   └── CountingSort.java     sort
├── search/
│   └── SearchAlgorithms.java linearIterativa, linearRecursiva, linearDuasPontas,
│                             binariaIterativa, binariaRecursiva
├── util/
│   ├── Cenario.java          ALEATORIO, ORDENADO, INVERSO, PARCIALMENTE_ORDENADO
│   ├── DataGenerator.java    gerar(cenario, n), copyOf(int[]), ALVO_AUSENTE
│   └── Verificador.java      estaOrdenado(int[])
└── benchmark/                (a ser criado: Main com as medições)
```

`Arrays.sort(int[])` é a referência da biblioteca padrão e é chamado direto no
benchmark, sem implementação própria.

## Compilar

Linux/macOS:
```
javac -d out $(find src -name "*.java")
```
Windows (PowerShell):
```
javac -d out (Get-ChildItem -Recurse src -Filter *.java).FullName
```

## Para quem vai escrever o Main (pacote `benchmark`)

Pedidos do enunciado que o `Main` precisa cumprir:

- Medir com `System.nanoTime()` (não é JMH).
- 3 tamanhos de vetor, **os mesmos para todas as ordenações**; 4 cenários.
- 25 execuções por combinação (algoritmo × tamanho × cenário); as 5 primeiras são
  warm-up e **não entram** nas estatísticas (média, desvio, mín., máx. das 20 restantes).
- Gerar o vetor mestre uma vez por (tamanho, cenário) e passar
  `DataGenerator.copyOf(mestre)` a cada execução. Nunca reaproveitar saída ordenada.
- Cronometrar só a chamada do algoritmo. A cópia e a verificação
  (`Verificador.estaOrdenado`) ficam fora do tempo.
- Buscas: vetor ordenado (`DataGenerator.ordenado(n)`), alvo `DataGenerator.ALVO_AUSENTE` (-1).
  Como cada busca dura microssegundos, vale repetir a busca dentro de cada execução
  cronometrada e dividir pelo número de repetições.
- Incluir `Arrays.sort(int[])` como referência na comparação de ordenação.

Exemplo de uso:
```java
int[] mestre = DataGenerator.gerar(Cenario.PARCIALMENTE_ORDENADO, 50_000);

int[] arr = DataGenerator.copyOf(mestre);
long t0 = System.nanoTime();
MergeSort.sortClassico(arr);
long dt = System.nanoTime() - t0;          // tempo do algoritmo apenas

if (!Verificador.estaOrdenado(arr)) throw new IllegalStateException("nao ordenou");
```

### Atenção à pilha (StackOverflowError)

- `QuickSort.sortSlide` em vetor **ordenado ou inverso** tem recursão de profundidade n
  (testado: estoura com n = 50.000 na pilha padrão).
- `SearchAlgorithms.linearRecursiva` tem profundidade n.

Rode com `-Xss512m`:
```
java -Xss512m -cp out br.edu.uepb.eda.benchmark.Main
```
ou execute o benchmark dentro de uma `Thread` criada com pilha grande.

### Tamanhos

Medições aproximadas de uma execução em vetor aleatório (n = 50.000): BubbleSort ≈ 3,7 s,
SelectionSort ≈ 0,7 s (estável ≈ 1,5 s), InsertionSort ≈ 0,16 s. Com 25 execuções × 4 cenários,
os quadráticos ficam inviáveis em 150.000 e 500.000. Sugestão: 5.000 / 15.000 / 50.000 para
as ordenações (justificar na metodologia do relatório) e 50.000 / 150.000 / 500.000 para as buscas.
