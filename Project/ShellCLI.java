import java.util.function.Function;
import java.util.Optional;
import java.util.Arrays;
import java.io.*;

// ShellCLI class
public class ShellCLI {
    // Constant logo variable
    private static final String LOGO = """
                    _____ __         ____
                   / ___// /_  ___  / / /
                   \\__ \\/ __ \\/ _ \\/ / /\s
                  ___/ / / / /  __/ / / \s
                 /____/_/ /_/\\___/_/_/  \s
                                   \s""";

    // Allocate variables
    private BufferedOutputStream outputStream;  // Output stream variable
    private final BufferedWriter writer;  // Writer variable
    private final BufferedReader reader;  // Reader variable
    private final Shell shell;  // Shell variable

    // Constructor
    public ShellCLI(InputStream in, OutputStream out) {
        // Initialize variables
        this.writer = new BufferedWriter(new OutputStreamWriter(out));  // Initialize writer
        this.reader = new BufferedReader(new InputStreamReader(in));  // Initialize reader
        this.outputStream = new BufferedOutputStream(outputStream);  // Initialize output stream
        this.shell = new Shell();  // Initialize shell
    }

    // Event loop method
    public void eventLoop(){
        // Print logo
        System.out.println(LOGO);

        // Infinite loop
        while(true){
            try {
                // Print prompt
                System.out.print(shell.pwd() + " $ ");
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
        if(command.equals("history clear")) {
            // Clear history
            shell.historyClear();
        // Otherwise
        } else {
            // Switch statement for commands
            switch(commandArr[0]) {
                case "help" -> shell.help();  // Print help
                case "exit" -> shell.exit();  // Exit program
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

    // Filter prefix
    private final String filterPrefix = "--filter=";

    // Run list files method
    public String runLs(String[] parameters) {
        // Check if parameters contain "--filter="
        Optional<String> filterParameter = Arrays.stream(parameters)  // Convert parameters to stream
                .filter(entry -> entry.startsWith(filterPrefix))  // Filter parameters
                .findFirst();  // Find first parameter

        // Get substring of filter parameter
        String substring = filterParameter.map(s -> s.substring(filterPrefix.length())).orElse(null);

        // Create function for formatting directories
        Function<String, String> dirFormatFunction;

        // If parameters contain "--color"
        if(Arrays.asList(parameters).contains("--color")) {
            // Format directory color
            dirFormatFunction = Shell::formatDirColor;
        // Otherwise
        } else {
            // Format directory braces
            dirFormatFunction = Shell::formatDirBraces;
        }
        // Return formatted entries
        return shell.ls(dirFormatFunction, substring).toString();
    }
}
