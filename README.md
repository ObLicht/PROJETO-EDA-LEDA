# Análise de Algoritmos de Busca e Ordenação

Projeto da Unidade 1 de EDA/LEDA 2026.2 (UEPB). Implementação e benchmark de algoritmos de
ordenação e busca em Java, usando apenas `int[]` com valores em [1, 100.000] (repetições permitidas).

## Estrutura

```
src/main/java/br/edu/uepb/eda/
├── Main.java                   # Executa todo o experimento
├── sorting/
│   ├── BubbleSort.java         # Slide + Otimizado
│   ├── SelectionSort.java      # Slide + Estável
│   ├── InsertionSort.java
│   ├── MergeSort.java          # Clássico
│   ├── QuickSort.java          # Slide + Slide com Shuffle
│   └── CountingSort.java
├── search/
│   └── SearchAlgorithms.java   # Buscas lineares (3) e binárias (2)
└── util/
    ├── Cenario.java            # ALEATORIO, ORDENADO, INVERSO, PARCIALMENTE_ORDENADO
    ├── DataGenerator.java      # Geração dos vetores de cada cenário
    └── Verificador.java        # Confere se o vetor ficou ordenado
```

`Arrays.sort(int[])` entra no benchmark como referência da biblioteca padrão (sem implementação própria).

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
java -cp out br.edu.uepb.eda.Main
```

Opções:

| Opção | O que faz | Padrão |
|---|---|---|
| `--ord=a,b,c` | tamanhos dos vetores nas ordenações | `5000,15000,50000` |
| `--busca=a,b,c` | tamanhos dos vetores nas buscas | `50000,150000,500000` |
| `--csv=arquivo.csv` | arquivo de saída | `resultados.csv` |
| `--rapido` | teste rápido com tamanhos pequenos (só para validar) | - |

Exemplo:
```
java -cp out br.edu.uepb.eda.Main --ord=2000,6000,20000 --csv=meus_resultados.csv
```

## Metodologia

- Tempo medido com `System.nanoTime()`.
- 25 execuções por combinação (algoritmo × tamanho × cenário). As 5 primeiras são warm-up e
  **não entram** nas estatísticas; média, desvio padrão, mínimo e máximo são das 20 restantes.
- 4 cenários: aleatório, ordenado, inverso e parcialmente ordenado (vetor ordenado com ~15% das
  posições sorteadas e embaralhadas entre si).
- O vetor original é gerado uma vez por (tamanho, cenário), com semente fixa (42), e cada execução
  recebe uma **cópia** dele. Nenhum algoritmo recebe a saída ordenada de outro.
- Só a chamada do algoritmo é cronometrada. A cópia e a verificação de correção ficam fora do tempo.
- **Buscas:** vetor ordenado e alvo `-1` (ausente). Como cada busca dura micro/nanossegundos, cada
  execução repete a busca várias vezes e o resultado é o tempo **por busca**.

## Tamanhos usados (e por que foram reduzidos)

| Experimento | Tamanhos |
|---|---|
| Ordenação | 5.000 / 15.000 / 50.000 |
| Busca | 50.000 / 150.000 / 500.000 |

O enunciado sugere 50.000 / 150.000 / 500.000, mas permite ajustar os tamanhos quando o tempo de
execução inviabiliza os experimentos. Nos algoritmos O(n²) (Bubble, Selection e Insertion), multiplicar
n por 10 multiplica o tempo por ~100, e cada combinação roda 25 vezes em 4 cenários. Com 150.000 e
500.000 elementos a execução ficou longa demais, então as **ordenações** foram reduzidas para 1/10 do
sugerido, mantendo a proporção 1 : 3 : 10 e os mesmos tamanhos para todos os algoritmos. As **buscas**
(O(n) e O(log n)) mantiveram os tamanhos originais.

### Como alterar os tamanhos

Pela linha de comando, sem recompilar:
```
java -cp out br.edu.uepb.eda.Main --ord=2000,6000,20000 --busca=20000,60000,200000
```
Ou mudando os valores padrão no início do `Main.java`:
```java
static int[] tamanhosOrdenacao = {5_000, 15_000, 50_000};
static int[] tamanhosBusca     = {50_000, 150_000, 500_000};
```
Dicas: use sempre 3 tamanhos, mantenha a proporção ~1 : 3 : 10, e lembre que o tempo dos algoritmos
O(n²) cresce com o quadrado de n (dobrar n quadruplica o tempo).

## Observações

- **Pilha:** `QuickSort (slide)` em vetor ordenado/inverso e a busca linear recursiva têm recursão de
  profundidade n. O `Main` já executa tudo numa `Thread` com pilha de 512 MB, então não é preciso
  passar `-Xss`. Se usar as classes fora do `Main`, rode com `-Xss512m`.
- **Memória:** se der `OutOfMemoryError` em tamanhos grandes, rode com `-Xmx2g` (ou mais).
- **Saída:** `resultados.csv` com as colunas
  `tipo, algoritmo, tamanho, cenario, unidade, media, desvio_padrao, minimo, maximo`
  (ordenação em ms; busca em µs por busca).
