// Java Shell CLI
// Made by github.com/tukarp

// Main class
public class Main {
    public static void main(String[] args) {
        // Create a new ShellCLI object and pass in the input and output streams
        ShellCLI shellCLI = new ShellCLI(System.in, System.out);
        // Start program
        shellCLI.eventLoop();
    }
}
