package cli;

import bitonicSort.BitonicSort;
import logging.ILogger;
import logging.Logger;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;

/**
 * Command line application to allow sorting of integers.
 * Input may come from files or standard input.
 * Direction of sort may be reversed.
 */
public class SortCLI implements AutoCloseable {
    private final InputStream in;
    private final PrintStream out;
    private final PrintStream err;
    private final ILogger logger;
    // OPTIONS
    boolean reversed = false;
    boolean quiet = false;
    boolean sourceFromFile = false;
    String sourceFile = "-";
    String inDelimiter = null;
    String outDelimiter = " ";
    List<Integer> list = new ArrayList<>();


    private SortCLI(InputStream in, PrintStream out, PrintStream err, Logger logger) {
        this.in = in;
        this.out = out;
        this.err = err;
        this.logger = logger;
    }

    /**
     * Factory method for constructing SortCLI
     *
     * @return SortCLI object
     * @throws FactoryException if we do not have it's dependencies
     */
    public static SortCLI createDefaultSortCLI() throws FactoryException {
        SortCLI sortCLI = new SortCLI(
                System.in,
                System.out,
                System.err,
                Logger.getSingletonInstance(Logger.Level.ERROR)
        );
        if (sortCLI.logger == null)
            throw new FactoryException("no logger available");
        if (sortCLI.out == null || sortCLI.err == null)
            throw new FactoryException("no console available");
        return sortCLI;
    }

    public static void main(String[] args) {

        try (SortCLI sortCLI = SortCLI.createDefaultSortCLI()) {

            try {
                sortCLI.processArgs(args);
            } catch (BadParamatersException e) {
                sortCLI.commandError("Illegal argument");
                return;
            }

            try {
                sortCLI.readListFromSource();
            } catch (IOException e) {
                sortCLI.commandError("Could not read file.");
                return;
            }

            sortCLI.sort();

            sortCLI.print();

        } catch (FactoryException e) {
            System.err.printf("Could not create Sort object. %n%s", e);
        } catch (Exception e) {
            System.err.printf("Error.%n");
        }
    }

    /**
     * Prints help message to out
     */
    void helpMessage() {
        String head = "Sort [OPTION]... [FILE]%n" +
                "%n" +
                "Sort a list of integers from FILE, and write result to standard output.%n" +
                "%n" +
                "When FILE is -, read standard input." +
                "%n";
        out.printf(head);

        out.printf(" Options:%n");
        String option = "  %-20.20s %-60.60s%n";
        out.printf(option, "-r, --reverse", "Reverse sort direction.");
        out.printf(option, "-h, --help", "Display this help and exit.");
    }

    /**
     * Method to handle and report back an error when running
     * a command i.e syntax error, list error, file error
     *
     * @param e Error message
     */
    void commandError(String e) {
        logger.log(Logger.Level.ERROR, e);
        if (!quiet) helpMessage();
    }

    /**
     * Given raw arguments from command, will check for validity
     * and extract information, setting appropriate variables in Sort to reflect that.
     *
     * @param args String of arguments to command supplied from command line.
     * @throws BadParamatersException If args is null empty or contains invalid/unsupported arguments
     */
    void processArgs(String[] args) throws BadParamatersException {
        if (args == null || args.length == 0) throw new BadParamatersException("invalid argument");

        for (int i = 0; i < args.length - 1; i++) {
            switch (args[i]) {
                case "-r":
                case "--reverse":
                    reversed = true;
                    break;
                case "-h":
                case "--help":
                    helpMessage();
                    break;
                default:
                    throw new BadParamatersException("invalid argument");
            }
        }

        String source = args[args.length - 1];
        sourceFromFile = !"-".equals(source);

        if (sourceFromFile) {
            sourceFile = source;
        }
    }

    /**
     * Reads integers from input stream and adds them to list.
     *
     * @param inputStream Input stream to extract integer values for list
     */
    void readListFromInputStream(InputStream inputStream) {

        Scanner sc = new Scanner(inputStream);
        if (inDelimiter != null)
            sc.useDelimiter(inDelimiter);

        while (sc.hasNextInt()) {
            list.add(sc.nextInt());
        }

        if (list.isEmpty())
            commandError("List empty.");
    }

    /**
     * Reads integers from file and adds them to list.
     *
     * @param filename File name and path to read integers from
     * @throws IOException
     */
    void getListFromFile(String filename) throws IOException {
        try (FileInputStream fileInputStream = new FileInputStream(filename)) {
            readListFromInputStream(fileInputStream);
        }
    }

    /**
     * Reads integers from standard input and adds them to list.
     */
    void getListFromStandardInput() {
        readListFromInputStream(in);
    }

    /**
     * Reads integers from correct source (as was specified in command's arguments)
     *
     * @throws IOException
     */
    void readListFromSource() throws IOException {
        if (sourceFromFile) {
            getListFromFile(sourceFile);
        } else {
            getListFromStandardInput();
        }
    }

    /**
     * Performs sort on list.
     */
    void sort() {
        BitonicSort.sort(list, reversed ? Comparator.reverseOrder() : Comparator.naturalOrder());
    }

    /**
     * Prints list to out.
     */
    void print() {
        for (int i = 0; i < list.size(); i++) {
            out.print(list.get(i));
            if (i != list.size() - 1)
                out.print(outDelimiter);
        }
    }

    @Override
    public void close() throws Exception {

    }

}
