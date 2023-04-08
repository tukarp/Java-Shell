import java.io.*;
import java.util.Arrays;
import java.util.Optional;
import java.util.function.Function;

public class TerminalCLI {
    private BufferedOutputStream outputStream;
    private BufferedReader reader;
    private BufferedWriter writer;
    private Terminal terminal;

    public TerminalCLI(InputStream in, OutputStream out){
        this.reader = new BufferedReader(new InputStreamReader(in));
        this.writer = new BufferedWriter(new OutputStreamWriter(out));
        this.outputStream = new BufferedOutputStream(outputStream);
        this.terminal = new Terminal();
    }

    public void eventLoop(){
        while(true){
            try {
                String line = reader.readLine();
                runCommand(line);
                writer.flush();
            } catch(IOException e){
                e.printStackTrace();
            }
        }
    }

    public void runCommand(String command)throws IOException{
        String[] commandArr = command.split(" ");
        terminal.historyAdd(command);

        switch (command) {
            case "history -c" -> terminal.historyClear();

            default -> {
                switch (commandArr[0]) {
                    case "clear" -> terminal.clear();
                    case "history" -> terminal.history();
                    case "rm" -> terminal.rm(commandArr[1]);
                    case "cd" -> terminal.cd(commandArr[1]);
                    case "cat" -> terminal.cat(commandArr[1]);
                    case "tac" -> terminal.tac(commandArr[1]);
                    case "touch" -> terminal.touch(commandArr[1]);
                    case "mkdir" -> terminal.mkdir(commandArr[1]);
                    case "rmdir" -> terminal.rmdir(commandArr[1]);
                    case "pwd" -> writer.write(terminal.pwd() + "\n");
                    case "date" -> writer.write(terminal.date().toString() + "\n");
                    case "echo" -> writer.write(terminal.echo(commandArr[1]) + "\n");
                    case "find" -> writer.write(terminal.find(commandArr[1]).toString() + "\n");
                    case "ls" -> writer.write(runLs(Arrays.copyOfRange(commandArr, 1, commandArr.length)) + "\n");

                    default -> writer.write("Unknown command\n");
                }
            }
        }
    }

    private final String filterPrefix = "--filter=";
    public String runLs(String[] parameters) {
        Optional<String> filterParameter = Arrays.stream(parameters)
                .filter(entry -> entry.startsWith(filterPrefix))
                .findFirst();

        String substring = filterParameter.map(s -> s.substring(filterPrefix.length())).orElse(null);

        Function<String, String> dirFormatFunction;
        if(Arrays.asList(parameters).contains("--color")) {
            dirFormatFunction = Terminal::formatDirColor;
        } else {
            dirFormatFunction = Terminal::formatDirBraces;
        }
        return terminal.ls(dirFormatFunction, substring).toString();
    }
}