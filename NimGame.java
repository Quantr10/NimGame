import java.io.*;
import java.util.*;

public class NimGame {
    static final int MAX_ACTION = 3;
    static final int MAX_STICKS = 10;
    static final String FILE_NAME = "qmatrix.txt";
    static double[][] qMatrix = new double[MAX_ACTION][MAX_STICKS]; // 0-indexed
    static List<int[]> computerMoves = new ArrayList<>();

    public static void main(String[] args) {
        loadQMatrix();
        Scanner sc = new Scanner(System.in);

        while (true) {
            int sticks = MAX_STICKS;
            computerMoves.clear();
            System.out.println("\nNew Game! Starting with 10 sticks.");
            boolean isComputerTurn = true;

            while (sticks > 0) {
                System.out.println("\nSticks remaining: " + sticks);
                int move;

                if (isComputerTurn) {
                    move = computerMove(sticks);
                    System.out.println("Computer takes " + move + " stick(s).");
                    computerMoves.add(new int[]{move - 1, sticks - 1});
                } else {
                    move = humanMove(sc, sticks);
                }

                sticks -= move;
                isComputerTurn = !isComputerTurn;
            }

            if (isComputerTurn) {
                System.out.println("\nYou win!");
                updateQMatrix(false); // computer lost
                printQMatrix();

            } else {
                System.out.println("\nComputer wins!");
                updateQMatrix(true); // computer won
                printQMatrix();
            }

            saveQMatrix();

            System.out.print("\nPlay again? (y/n): ");
            if (!sc.next().equalsIgnoreCase("y")) break;
        }

        sc.close();
    }

    static int computerMove(int sticks) {
        int column = sticks - 1;
        double maxQ = Double.NEGATIVE_INFINITY;
        List<Integer> candidates = new ArrayList<>();

        for (int i = 0; i < MAX_ACTION; i++) {
            if (i + 1 <= sticks) {
                if (qMatrix[i][column] > maxQ) {
                    maxQ = qMatrix[i][column];
                    candidates.clear();
                    candidates.add(i + 1);
                } else if (qMatrix[i][column] == maxQ) {
                    candidates.add(i + 1);
                }
            }
        }

        return candidates.get(new Random().nextInt(candidates.size()));
    }

    static int humanMove(Scanner sc, int sticks) {
        int move;
        while (true) {
            System.out.print("Your turn. Take 1, 2, or 3 sticks: ");
            move = sc.nextInt();
            if (move >= 1 && move <= 3 && move <= sticks) break;
            System.out.println("Invalid move.");
        }
        return move;
    }

    static void updateQMatrix(boolean win) {
        for (int[] move : computerMoves) {
            int row = move[0];
            int col = move[1];
            if (win) {
                qMatrix[row][col] += 1.0;
            } else {
                qMatrix[row][col] -= 1.0;
            }
        }
    }

    static void loadQMatrix() {
        try (BufferedReader br = new BufferedReader(new FileReader(FILE_NAME))) {
            for (int i = 0; i < MAX_ACTION; i++) {
                String[] values = br.readLine().split(" ");
                for (int j = 0; j < MAX_STICKS; j++) {
                    qMatrix[i][j] = Double.parseDouble(values[j]);
                }
            }
            System.out.println("Q-matrix loaded.");
        } catch (IOException e) {
            System.out.println("Q-matrix file not found. Starting fresh.");
            for (int i = 0; i < MAX_ACTION; i++)
                Arrays.fill(qMatrix[i], 0.0);
        }
    }

    static void saveQMatrix() {
        try (PrintWriter pw = new PrintWriter(new FileWriter(FILE_NAME))) {
            for (int i = 0; i < MAX_ACTION; i++) {
                for (int j = 0; j < MAX_STICKS; j++) {
                    pw.print(qMatrix[i][j] + " ");
                }
                pw.println();
            }
        } catch (IOException e) {
            System.out.println("Error saving Q-matrix.");
        }
    }

    static void printQMatrix() {
        System.out.println("\nQ-Matrix (after update):");
        System.out.print("     ");
        for (int j = 0; j < MAX_STICKS; j++) {
            System.out.printf("%6d", j + 1);
        }
        System.out.println();
    
        for (int i = 0; i < MAX_ACTION; i++) {
            System.out.printf("%2d |", i + 1);
            for (int j = 0; j < MAX_STICKS; j++) {
                System.out.printf("%6.1f", qMatrix[i][j]);
            }
            System.out.println();
        }
    }
    
}
