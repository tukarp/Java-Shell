import java.util.function.Function;
import java.util.Optional;
import java.util.Arrays;
import java.io.*;

// ShellCLI class
public class ShellCLI {
    // Allocate variables
    // Buffered output stream variable
    private BufferedOutputStream outputStream;

    // Buffered reader and writer variables
    private final BufferedWriter writer;
    private final BufferedReader reader;

    // Shell variable
    private final Shell shell;

    // Constructor
    public ShellCLI(InputStream in, OutputStream out) {
        // Initialize variables
        this.writer = new BufferedWriter(new OutputStreamWriter(out));
        this.reader = new BufferedReader(new InputStreamReader(in));
        this.outputStream = new BufferedOutputStream(outputStream);
        this.shell = new Shell();
    }

    // Event loop method
    public void eventLoop(){
        // Infinite loop
        while(true){
            try {
                // Assign line to next line in reader
                String line = reader.readLine();
                // Run command
                runCommand(line);
                // Flush writer
                writer.flush();
            } catch(IOException e){
                // Catch IOException
                e.printStackTrace();
            }
        }
    }

    // Run command method
    public void runCommand(String command) throws IOException {
        // Split command into array
        String[] commandArr = command.split(" ");

        // Add command to history
        shell.historyAdd(command);

        // Check if command is "history clear"
        if(command.equals("history -c")) {
            // Clear history
            shell.historyClear();
        } else {
            // Switch statement for commands
            switch(commandArr[0]) {
                case "clear" -> shell.clear();  // Clear screen
                case "history" -> shell.history();  // Print history
                case "rm" -> shell.rm(commandArr[1]);  // Remove file
                case "cd" -> shell.cd(commandArr[1]);  // Change directory
                case "cat" -> shell.cat(commandArr[1]);  // Print file
                case "tac" -> shell.tac(commandArr[1]); // Print file with reversed lines
                case "touch" -> shell.touch(commandArr[1]);  // Create file
                case "mkdir" -> shell.mkdir(commandArr[1]);  // Create directory
                case "rmdir" -> shell.rmdir(commandArr[1]);  // Remove directory
                case "pwd" -> writer.write(shell.pwd() + "\n");  // Print working directory
                case "date" -> writer.write(shell.date().toString() + "\n");  // Print current date
                case "echo" -> writer.write(shell.echo(command) + "\n");  // Print given string
                case "find" -> writer.write(shell.find(commandArr[1]).toString() + "\n");  // Find file
                case "ls" -> writer.write(runLs(Arrays.copyOfRange(commandArr, 1, commandArr.length)) + "\n");  // List files

                // Default case
                default -> writer.write("Unknown command\n");
            }
        }
    }

    // Format printing files method
    private final String filterPrefix = "--filter=";
    public String runLs(String[] parameters) {
        // Check if parameters contain "--filter="
        Optional<String> filterParameter = Arrays.stream(parameters)
                .filter(entry -> entry.startsWith(filterPrefix))
                .findFirst();

        // Get substring of filter parameter
        String substring = filterParameter.map(s -> s.substring(filterPrefix.length())).orElse(null);

        // Create function for formatting directories
        Function<String, String> dirFormatFunction;

        // Check if parameters contain "--color"
        if(Arrays.asList(parameters).contains("--color")) {
            // Format directory color
            dirFormatFunction = Shell::formatDirColor;
        } else {
            // Format directory braces
            dirFormatFunction = Shell::formatDirBraces;
        }
        // Return formatted entries
        return shell.ls(dirFormatFunction, substring).toString();
    }
}
