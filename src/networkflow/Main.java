package networkflow;

//20231686
//SaroathJahan


import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner consoleScanner = new Scanner(System.in);
        System.out.print("Enter the benchmark file name (e.g., bridge_1.txt): ");
        String filename = consoleScanner.nextLine();

        try {
            FlowNetwork network = MaxFlow.readNetwork(filename);
            MaxFlow.computeMaxFlow(network, filename);
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        } finally {
            consoleScanner.close();
        }
    }
}