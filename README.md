# EDA/LEDA - Análise de Algoritmos de Busca e Ordenação

Atividade da **Unidade 1** da disciplina de Estrutura de Dados / Laboratório de Estrutura de Dados (2026.2), **UEPB** - Departamento de Computação.

O projeto implementa algoritmos de ordenação e de busca em Java, **exclusivamente com `int[]`**, e mede o tempo de execução de cada um com `System.nanoTime()` em vários tamanhos de vetor e cenários de entrada. O objetivo é comparar a teoria (complexidade assintótica) com a prática (tempos medidos).

**Integrantes:** _(preencher com os nomes do grupo)_

---

## Sumário

- [Pré-requisitos](#pré-requisitos)
- [Como compilar e executar](#como-compilar-e-executar)
- [Estrutura do projeto](#estrutura-do-projeto)
- [Algoritmos implementados](#algoritmos-implementados)
- [Metodologia dos experimentos](#metodologia-dos-experimentos)
- [Observações sobre a recursão](#observações-sobre-a-recursão)
- [Resultados e entrega](#resultados-e-entrega)

---

## Pré-requisitos

| Ferramenta | Versão mínima | Verificar |
|------------|---------------|-----------|
| Java (JDK) | 21            | `java -version` |
| Maven      | 3.8           | `mvn -version` |
| Git        | qualquer      | `git --version` |

---

## Como compilar e executar

```bash
# 1. Clonar o repositório
git clone <link-do-repositório>
cd <pasta-do-projeto>

# 2. Compilar
mvn clean package -DskipTests

# 3. Executar o experimento completo (uma única main)
java -Xss512m -cp target/classes br.edu.uepb.eda.Main
```

> **Por que `-Xss512m`?** A busca linear recursiva tem profundidade de recursão igual a `n`, e o QuickSort (versão do slide) também chega a profundidade `n` em vetores já ordenados ou em ordem inversa. Sem aumentar a pilha da JVM, esses casos geram `StackOverflowError` nos vetores maiores.

Toda a execução (ordenação e busca, todos os tamanhos e cenários) é feita pela **classe `Main`**, sem necessidade de rodar nada separadamente.

---

## Estrutura do projeto

```
src/main/java/br/edu/uepb/eda/
├── Main.java                  # Executa todo o experimento
├── sorting/
│   ├── BubbleSort.java        # Slide + Otimizado
│   ├── SelectionSort.java     # Slide + Estável
│   ├── InsertionSort.java
│   ├── MergeSort.java         # Clássico
│   ├── QuickSort.java         # Slide + Slide com Shuffle
│   └── CountingSort.java
├── search/
│   └── SearchAlgorithms.java  # Buscas lineares (3) e binárias (2)
└── util/
    └── DataGenerator.java     # Geração dos vetores de cada cenário
```

---

## Algoritmos implementados

### Ordenação

Todos operam sobre `int[]`. Para comparação de desempenho também é usado `Arrays.sort(int[])` (Dual-Pivot QuickSort do Java), que serve apenas como referência e não foi implementado por nós.

| Algoritmo | Variante | Complexidade (pior caso) | Estável |
|-----------|----------|--------------------------|---------|
| BubbleSort | Slide | O(n²) | Sim |
| BubbleSort | Otimizado (flag de troca) | O(n²); melhor caso O(n) | Sim |
| SelectionSort | Slide | O(n²) | Não |
| SelectionSort | Estável (shift em vez de troca) | O(n²) | Sim |
| InsertionSort | - | O(n²); melhor caso O(n) | Sim |
| MergeSort | Clássico (recursivo) | O(n log n) | Sim |
| QuickSort | Slide (Lomuto, pivô = último elemento) | O(n²); médio O(n log n) | Não |
| QuickSort | Slide + Shuffle (Fisher-Yates antes) | O(n log n) esperado | Não |
| CountingSort | - | O(n + k), com k = 100.000 | Sim |

### Busca

As buscas consideram o vetor **previamente ordenado** e procuram sempre o elemento `-1`, que não pertence ao intervalo dos valores e, portanto, está **ausente** (pior caso: o vetor é percorrido por completo).

| Algoritmo | Complexidade |
|-----------|--------------|
| Linear iterativa | O(n) |
| Linear recursiva | O(n) |
| Linear iterativa duas pontas | O(n) |
| Binária iterativa | O(log n) |
| Binária recursiva | O(log n) |

Todas retornam o índice do elemento encontrado ou `-1` se ele não existir.

---

## Metodologia dos experimentos

- **Valores:** inteiros no intervalo **[1, 100.000]**, com repetições permitidas.
- **Tamanhos:** três tamanhos de vetor, definidos nas constantes da `Main` e iguais para todos os algoritmos comparados diretamente.
- **Cenários de ordenação:**
  1. **Aleatório**
  2. **Já ordenado** (crescente)
  3. **Ordem inversa** (decrescente)
  4. **Parcialmente ordenado:** parte de um vetor ordenado; ~15% das posições são sorteadas e **apenas os elementos dessas posições** são embaralhados.
- **Execuções:** para cada combinação de algoritmo, tamanho e cenário são feitas **25 execuções**; as **5 primeiras são descartadas** como warm-up da JVM e as estatísticas são calculadas apenas com as 20 restantes.
- **Entradas idênticas:** para cada combinação de tamanho e cenário é gerada uma única entrada original, e cada algoritmo recebe uma **cópia** dela. A saída ordenada de um algoritmo nunca é usada como entrada de outro.
- **Medição:** feita com `System.nanoTime()` somente em volta da chamada do algoritmo. A cópia do vetor e a verificação ficam **fora** da medição.
- **Verificação:** após cada execução, confere-se se o vetor ficou ordenado; as buscas verificam se o resultado é `-1`. Os tempos dessa verificação não entram nas estatísticas.

---

## Observações sobre a recursão

Relações de recorrência dos algoritmos recursivos e resultado pelo Método Mestre (ou pelo método iterativo, quando o Mestre não se aplica):

| Algoritmo | Recorrência | Resultado |
|-----------|-------------|-----------|
| MergeSort | T(n) = 2T(n/2) + Θ(n) | Θ(n log n) (Mestre, caso 2) |
| Busca binária recursiva | T(n) = T(n/2) + Θ(1) | Θ(log n) (Mestre, caso 2) |
| Busca linear recursiva | T(n) = T(n-1) + Θ(1) | Θ(n) (iterativo; não se aplica o Mestre) |
| QuickSort, melhor caso | T(n) = 2T(n/2) + Θ(n) | Θ(n log n) |
| QuickSort, pior caso | T(n) = T(n-1) + Θ(n) | Θ(n²) |

Na prática, a recursão também consome **pilha**: a busca linear recursiva e o QuickSort sem shuffle em entradas já ordenadas/invertidas chegam a profundidade `n`, enquanto o MergeSort e a busca binária ficam em profundidade O(log n).

---

## Resultados e entrega

Os tempos medidos, as tabelas, os gráficos e a discussão sobre a compatibilidade entre os resultados e a complexidade assintótica esperada estão no **relatório em PDF** entregue junto com este repositório, além do **vídeo de apresentação**.

- Relatório (PDF) e vídeo: _(link da pasta do Google Drive / vídeo no YouTube)_
