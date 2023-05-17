import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.io.*;

// Shell class
public class Shell {
    // Allocate variables
    // History string list variable
    private final List<String> History = new ArrayList<>();

    // Path variable
    private Path path;

    // Constructor
    public Shell() {
        // Initialize path variable
        this.path = Path.of(System.getProperty("user.home"));
    }

    // Format directory braces method
    public static String formatDirBraces(String text) {
        // Return text with braces for directory entries
        return "[" + text + "]";
    }

    // Format directory color method
    public static String formatDirColor(String text) {
        // Return text with blue color for directory entries
        return ConsoleColors.BLUE + text + ConsoleColors.RESET;
    }

    // Format filtered text method
    public static String formatFiltered(String text, String substring) {
        // Create result variable
        String result = "";

        // Get begin and end of substring
        int begin = text.indexOf(substring);
        int end = begin + substring.length();

        // Check if text starts with blue color
        String endMarker = text.startsWith(ConsoleColors.BLUE) ? ConsoleColors.BLUE : ConsoleColors.RESET;

        // Format text
        result += text.substring(0, begin);
        result += ConsoleColors.RED_BOLD;
        result += text.substring(begin, end);
        result += endMarker;
        result += text.substring(end);

        // Return result
        return result;
    }

    // Print path method
    public String pwd(){
        // Return path as string
        return path.toString();
    }

    // Change directory method
    public void cd(String path){
        // Change path
        this.path = this.path.resolve(path);
    }

    // Get actual path method
    public Path getActualPath(String fileName) {
        // Check if fileName contains backslash
        if(fileName.contains("\\")) {
            // Return path of fileName
           return Path.of(fileName);
        } else {
            // Return path of fileName in current directory
            return Path.of((this.path + "\\" + fileName));
        }
    }

    // Print files in current directory method
    public List<String> ls(Function<String, String> formatDir, String substring) {
        // Create comparator
        Comparator<Path> c = (p1, p2) ->
                Boolean.compare(Files.isDirectory(p2),Files.isDirectory(p1));
        c = c.thenComparing(p -> p.getFileName().toString());

        // Return list of files in current directory
        boolean isFiltered = substring != null;

        // Try to get list of files in current directory
        try (Stream<Path> stream = Files.list(path)) {
            // Return list of files in current directory
            return stream
                    .filter(isFiltered ? currentPath -> currentPath.getFileName().toString().contains(substring) : currentPath -> true)
                    .sorted(c)
                    .map(path -> {
                        String spath = path.getFileName().toString();
                        // Check if path is directory
                        if(Files.isDirectory(path)) {
                            // Return formatted directory
                            return formatDir.apply(spath);
                        } else {
                            // Return path
                            return spath;
                        }
                    })
                    // Check if path is filtered
                    .map(isFiltered ? spath -> formatFiltered(spath, substring) : spath -> spath)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            return new ArrayList<>();
        }
    }

    // Find file method
    public List<String> find(String s) {
        try(Stream<Path> stream = Files.walk(path)) {
            // Return list of files in current directory
            return stream.filter(currentPath -> currentPath.getFileName().toString().contains(s))
                    .map(Path::toString)
                    .collect(Collectors.toList());
        } catch (IOException e){
            e.printStackTrace();
        }
        // Return empty list
        return Collections.emptyList();
    }

    // Print text on screen method
    public String echo(String line) {
        // Return text starting after "echo"
        return line.substring(5);
    }

    // Print file contents on screen method
    public void cat(String fileName) throws FileNotFoundException {
        // Get actual path and assign it to file variable
        File file = new File(String.valueOf(getActualPath(fileName)));
        // Create scanner variable for reading file
        Scanner reader = new Scanner(file);
        // Read file line by line
        while(reader.hasNextLine()) {
            // Assign line to next line in file
            String line = reader.nextLine();
            // Print line on screen
            System.out.println(line);
        }
        // Close reader
        reader.close();
    }

    // Print file contents on screen with reversed lines method
    public void tac(String fileName) throws FileNotFoundException {
        // Get actual path and assign it to file variable
        File file = new File(String.valueOf(getActualPath(fileName)));
        // Create scanner variable for reading file
        Scanner reader = new Scanner(file);
        // Create output list
        List<String> output = new ArrayList<>();

        // Read file line by line
        while(reader.hasNextLine()) {
            // Assign line to next line in file
            String line = reader.nextLine();
            // Add line to output list
            output.add(line);

        // Reverse output list
        } Collections.reverse(output);

        // For each line in output list
        for(var line : output) {
            // Print line on screen
            System.out.println(line);
        }
        // Close reader
        reader.close();
    }

    // Get current date method
    public Date date() {
        // Return current date
        return new Date(System.currentTimeMillis());
    }

    // Add command to history method
    public void historyAdd(String command) {
        // Check if command is not empty
        if(!command.isEmpty()) {
            // Add command to history
            History.add(command);
        }
    }

    // Clear history method
    public void historyClear() {
        // Clear history
        History.clear();
    }

    // Print history on screen method
    public void history() {
        // For each command in history
        for(var command : History) {
            // Print command on screen
            System.out.println(command);
        }
    }

    // Clear screen method
    public void clear() {
        // Print 50 empty lines
        System.out.println(new String(new char[50]).replace("\0", "\r\n"));
    }

    // Create file method
    public void touch(String fileName) {
        // Get actual path and assign it to file variable
        File file = new File(String.valueOf(getActualPath(fileName)));
        try {
            // Create new file
            file.createNewFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // Create directory method
    public void mkdir(String fileName) {
        // Get actual path and assign it to file variable
        File file = new File(String.valueOf(getActualPath(fileName)));
        try {
            // Create new directory
            Files.createDirectory(Path.of(file + "\\"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // Remove file method
    public void rm(String fileName) {
        // Get actual path and assign it to file variable
        File file = new File(String.valueOf(getActualPath(fileName)));
        // Delete file
        file.delete();
    }

    // Remove directory method
    public void rmdir(String directoryName) {
        // Get actual path and assign it to file variable
        File directory = new File(String.valueOf(getActualPath(directoryName)));
        // Delete directory
        deleteDirectory(directory);
    }

    // Remove directory and its contents method
    public void deleteDirectory(File file) {
        // Create array of files in directory
        File[] files = file.listFiles();

        // Check if files is not null
        if(files != null) {
            // For each file in files
            for(File f : files) {
                // Delete file
                deleteDirectory(f);
            }
        }
        // Delete file itself
        file.delete();
    }
}
