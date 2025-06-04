# 🧩 Nonogram Puzzle Solver

A high-performance Nonogram (Picross) puzzle solver written in Java. It parses standard clue input files and outputs the fully solved board using advanced solving techniques.

## 🚀 Features

- **File-Based Input**: Reads board size and row/column clues from a plain text file.
- **Advanced Solving Logic**: Supports edge deduction, overlap reasoning, and clue consistency checking.
- **Permutation Pruning**: Efficiently generates and filters only valid line permutations using early pruning and bitwise filtering.
- **Custom Performance Benchmarking**: Tracks detailed timing stats for key stages (generation, filtering, solving).
- **Optimized Memory Usage**: Uses pre-counting and dynamic allocation strategies to reduce overhead.
- **Scalable**: Designed to handle both small and large puzzles (e.g. 50x50 and beyond).

## 🛠️ How It Works

1. Loads a text file describing the puzzle:
   ```
   <height> <width>
   <row clues...>
   <column clues...>
   ```
2. Generates permutations for each row and column based on clues.
3. Iteratively applies deduction, overlap logic, and filtering to solve the grid.
4. Outputs the solved board to the console.

## 📁 Sample Input File

```
5 5
1
3
1 1
3
1
2
1 1
3
1
```

## 💡 Optimizations

- Bitmask-based representation of permutations (`long[]` instead of `Long[]`).
- Early pruning using partial solutions to cut unnecessary branches.
- Force-generation fallback for complex constraint groups.
- Performance statistics available after each run for tuning and profiling.

## 📌 Future Work

- Additional edge deduction improvements using partial permutations.
- Potential port to C for further speed gains.
- Optional multithreading for large puzzles (reserved as a final optimization).

## 🧪 Example Output

```
#..##
##..#
.#.##
..###
##..#
```
