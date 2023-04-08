import java.io.*;
import java.util.*;
import java.nio.file.Path;
import java.nio.file.Files;
import java.util.stream.Stream;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Terminal {
    private List<String> History = new ArrayList<String>();
    private Path path;

    public Terminal() {
        this.path = Path.of(System.getProperty("user.home"));
    }
    public String pwd(){
        return path.toString();
    }
    public void cd(String path){
        this.path = this.path.resolve(path);
    }
    public static String formatDirBraces(String text) {
        return "[" + text + "]";
    }
    public static String formatDirColor(String text) {
        return ConsoleColors.BLUE + text + ConsoleColors.RESET;
    }

    public Path getActualPath(String fileName) {
        if(fileName.contains("\\")) {
           return Path.of(fileName);
        } else {
            return Path.of((this.path + "\\" + fileName));
        }
    }

    public static String formatFiltered(String text, String substring) {
        String result = "";
        int begin = text.indexOf(substring);
        int end = begin + substring.length();

        String endMarker = text.startsWith(ConsoleColors.BLUE) ? ConsoleColors.BLUE : ConsoleColors.RESET;

        result += text.substring(0, begin);
        result+=ConsoleColors.RED_BOLD;
        result += text.substring(begin, end);
        result+=endMarker;
        result += text.substring(end);

        return result;
    }

    public List<String> ls(Function<String, String> formatDir, String substring){
        Comparator<Path> c = (p1, p2) ->
                Boolean.compare(Files.isDirectory(p2),Files.isDirectory(p1));
        c = c.thenComparing(p -> p.getFileName().toString());

        boolean isFiltered = substring!=null;

        try (Stream<Path> stream = Files.list(path)) {
            return stream
                    .filter(isFiltered ? currentPath -> currentPath.getFileName().toString().contains(substring) : currentPath -> true)
                    .sorted(c)
                    .map(path -> {
                        String spath = path.getFileName().toString();
                        if(Files.isDirectory(path)) {
                            return formatDir.apply(spath);
                        }
                        else return spath;
                    })
                    .map(isFiltered ? spath -> formatFiltered(spath, substring) : spath -> spath)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            return new ArrayList<>();
        }
    }

    public List<String> find(String s) {
        try(Stream<Path> stream = Files.walk(path)){
            return stream.filter(currentPath -> currentPath.getFileName().toString().contains(s))
                    .map(Path::toString)
                    .collect(Collectors.toList());
        }catch (IOException e){
            e.printStackTrace();
        }
        return Collections.emptyList();
    }

    public String echo(String line) {
        String output = line.substring(5);
        return output;
    }

    public void cat(String fileName) throws FileNotFoundException {
        File file = new File(String.valueOf(getActualPath(fileName)));
        Scanner reader = new Scanner(file);
        while(reader.hasNextLine()) {
            String line = reader.nextLine();
            System.out.println(line);
        }
        reader.close();
    }

    public void tac(String fileName) throws FileNotFoundException {
        File file = new File(String.valueOf(getActualPath(fileName)));
        Scanner reader = new Scanner(file);
        List<String> output = new ArrayList<String>();
        while(reader.hasNextLine()) {
            String line = reader.nextLine();
            output.add(line);
        } Collections.reverse(output);
        for(var line : output) {
            System.out.println(line);
        }
        reader.close();
    }

    public Date date() {
        Date date = new Date(System.currentTimeMillis());
        return date;
    }

    public void historyAdd(String string) {
        History.add(string);
    }

    public void historyClear() {
        History.clear();
    }

    public void history() {
        for(var string : History) {
            System.out.println(string);
        }
    }

    public void clear() {
        System.out.println(new String(new char[50]).replace("\0", "\r\n"));
    }

    public void touch(String fileName) {
        File file = new File(String.valueOf(getActualPath(fileName)));
        try {
            file.createNewFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void mkdir(String fileName) {
        File file = new File(String.valueOf(getActualPath(fileName)));
        try {
            Files.createDirectory(Path.of(file + "\\"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void rm(String fileName) {
        File file = new File(String.valueOf(getActualPath(fileName)));
        file.delete();
    }

    public void rmdir(String directoryName) {
        System.out.println("here");
        File directory = new File(String.valueOf(getActualPath(directoryName)));
        deleteDirectory(directory);
    }

    public void deleteDirectory(File file) {
        File[] files = file.listFiles();
        if(files != null) {
            for(File f : files) {
                deleteDirectory(f);
            }
        }
        file.delete();
    }
}
