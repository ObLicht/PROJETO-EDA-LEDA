# Análise de Algoritmos de Busca e Ordenação

Projeto da Unidade 1 de EDA/LEDA 2026.2 — Universidade Estadual da Paraíba (UEPB), Campus I,
Curso de Ciência da Computação.

Implementação e benchmark de algoritmos de ordenação e busca em Java, usando apenas `int[]` com
valores em [1, 100.000] (repetições permitidas). A análise completa dos resultados está no relatório
(`relatorio_projeto_eda.pdf`).

## Autoras

- Ana Sophias de Lima Lins
- Gabriela Ferreira Pereira
- Lara Hadassa Beserra Torres Pereira
- Lillya Maria Mélo de Lima

## Algoritmos implementados

**Ordenação (9):** BubbleSort (slide e otimizado), SelectionSort (slide e estável), InsertionSort,
MergeSort (clássico), QuickSort (slide e slide com shuffle) e CountingSort.

**Busca (5):** Linear iterativa, Linear recursiva, Linear iterativa de duas pontas, Binária iterativa
e Binária recursiva.

`Arrays.sort(int[])` entra no benchmark como referência da biblioteca padrão (sem implementação própria)
e aparece no CSV como `Arrays.sort (referencia)`.

| Algoritmo | Melhor caso | Pior caso | Estável? |
|---|---|---|---|
| BubbleSort (slide) | O(n²) | O(n²) | Sim |
| BubbleSort (otimizado) | O(n) | O(n²) | Sim |
| SelectionSort (slide) | O(n²) | O(n²) | Não |
| SelectionSort (estável) | O(n²) | O(n²) | Sim |
| InsertionSort | O(n) | O(n²) | Sim |
| MergeSort (clássico) | O(n log n) | O(n log n) | Sim |
| QuickSort (slide) | O(n log n) | O(n²) | Não |
| QuickSort (slide+shuffle) | O(n log n) esperado | O(n²) | Não |
| CountingSort | O(n + k) | O(n + k) | Sim* |

\* A estabilidade do CountingSort depende da implementação.

## Estrutura

```
src/main/java/br/edu/uepb/eda/
├── benchmark/
│   └── Main.java               # Executa todo o experimento
├── search/
│   └── SearchAlgorithms.java   # Buscas lineares (3) e binárias (2)
├── sorting/
│   ├── BubbleSort.java         # Slide + Otimizado
│   ├── SelectionSort.java      # Slide + Estável
│   ├── InsertionSort.java
│   ├── MergeSort.java          # Clássico
│   ├── QuickSort.java          # Slide + Slide com Shuffle
│   └── CountingSort.java
└── util/
    ├── Cenario.java            # ALEATORIO, ORDENADO, INVERSO, PARCIALMENTE_ORDENADO
    ├── DataGenerator.java      # Geração dos vetores de cada cenário
    └── Verificador.java        # Confere se o vetor ficou ordenado
```

## Como compilar

Linux/macOS:
```
javac -d out $(find src/main/java -name "*.java")
```
Windows (PowerShell):
```
javac -d out (Get-ChildItem -Recurse src/main/java -Filter *.java).FullName
```

## Como executar

```
java -cp out br.edu.uepb.eda.benchmark.Main
```

Opções:

| Opção | O que faz | Padrão |
|---|---|---|
| `--ord=a,b,c` | tamanhos dos vetores nas ordenações | `5000,15000,50000` |
| `--busca=a,b,c` | tamanhos dos vetores nas buscas | `50000,150000,500000` |
| `--csv=arquivo.csv` | arquivo de saída | `resultados.csv` |
| `--rapido` | teste rápido com tamanhos pequenos (ordenação `500,1500,5000`; busca `5000,15000,50000`), só para validar | - |

### Reproduzir os resultados do relatório

O relatório usou os mesmos tamanhos nas ordenações e nas buscas:
```
java -cp out br.edu.uepb.eda.benchmark.Main --ord=5000,15000,50000 --busca=5000,15000,50000
```

## Metodologia

- Tempo medido com `System.nanoTime()`.
- 25 execuções por combinação (algoritmo × tamanho × cenário). As 5 primeiras são warm-up (reduzem a
  variabilidade da JVM) e **não entram** nas estatísticas; média, **mediana**, desvio padrão, mínimo e
  máximo são das 20 restantes.
- A **mediana** é a medida principal nas comparações do relatório, por ser menos sensível a valores
  atípicos.
- 4 cenários:
  - **Aleatório:** elementos gerados aleatoriamente, sem padrão.
  - **Ordenado:** ordem crescente.
  - **Parcialmente ordenado:** vetor ordenado em que ~15% das posições são sorteadas e embaralhadas
    entre si.
  - **Inverso:** ordem decrescente.
- O vetor original é gerado uma vez por (tamanho, cenário), com semente fixa (42), e cada execução
  recebe uma **cópia** dele. Nenhum algoritmo recebe a saída ordenada de outro.
- Só a chamada do algoritmo é cronometrada. A cópia e a verificação de correção ficam fora do tempo.
- **Buscas:** vetor ordenado e alvo `-1` (ausente), então todas as buscas lineares percorrem o vetor
  inteiro. Como cada busca dura micro/nanossegundos, cada execução repete a busca várias vezes
  (buscas binárias: 1.000.000 de repetições; lineares: cerca de 50.000.000 / n repetições) e o
  resultado é o tempo **por busca**.

## Tamanhos usados (e por que foram reduzidos)

| Experimento | Sugerido no enunciado | Usado no relatório |
|---|---|---|
| Ordenação | 50.000 / 150.000 / 500.000 | 5.000 / 15.000 / 50.000 |
| Busca | 50.000 / 150.000 / 500.000 | 5.000 / 15.000 / 50.000 |

O enunciado permite ajustar os tamanhos quando o tempo de execução inviabiliza os experimentos. Nos
algoritmos O(n²) (Bubble, Selection e Insertion), multiplicar n por 10 multiplica o tempo por ~100, e
cada combinação roda 25 vezes em 4 cenários. Nos testes iniciais, os tamanhos sugeridos tornaram as
medições demoradas demais, então foram reduzidos para 1/10, mantendo a proporção 1 : 3 : 10. No
relatório, ordenação e busca usam os **mesmos tamanhos**, para todos os algoritmos.

Os valores padrão do `Main.java` são `5.000 / 15.000 / 50.000` para ordenação e
`50.000 / 150.000 / 500.000` para busca; por isso a busca do relatório foi executada com
`--busca=5000,15000,50000` (comando acima).

### Como alterar os tamanhos

Pela linha de comando, sem recompilar:
```
java -cp out br.edu.uepb.eda.benchmark.Main --ord=2000,6000,20000 --busca=2000,6000,20000
```
Ou mudando os valores padrão no início do `Main.java`:
```java
static int[] tamanhosOrdenacao = {5_000, 15_000, 50_000};
static int[] tamanhosBusca     = {50_000, 150_000, 500_000};
```
Dicas: use sempre 3 tamanhos, mantenha a proporção ~1 : 3 : 10, e lembre que o tempo dos algoritmos
O(n²) cresce com o quadrado de n (dobrar n quadruplica o tempo).

## Saída

`resultados.csv` com as colunas
`tipo, algoritmo, tamanho, cenario, unidade, media, mediana, desvio_padrao, minimo, maximo`
(ordenação em `ms`; busca em `us_por_busca`, isto é, µs por busca). As buscas aparecem com cenário
`ORDENADO`.

## Observações

- **Pilha:** `QuickSort (slide)` em vetor ordenado/inverso e a busca linear recursiva têm recursão de
  profundidade n. O `Main` já executa tudo numa `Thread` com pilha de 512 MB, então não é preciso
  passar `-Xss`. Se usar as classes fora do `Main`, rode com `-Xss512m`.
- **Memória:** se der `OutOfMemoryError`, rode com `-Xmx2g` (ou mais).
