public class Main {
    public static void main(String[] args) {
        TerminalCLI cli = new TerminalCLI(System.in, System.out);
        cli.eventLoop();
    }
}