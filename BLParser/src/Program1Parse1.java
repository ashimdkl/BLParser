import components.program.Program;
import components.program.Program1;
import components.queue.Queue;
import components.simplereader.SimpleReader;
import components.simplereader.SimpleReader1L;
import components.simplewriter.SimpleWriter;
import components.simplewriter.SimpleWriter1L;
import components.statement.Statement;
import components.utilities.Tokenizer;

/**
 * Layered implementation of secondary method {@code parse} for {@code Program}.
 *
 * @author Ashim Dhakal | Szcheng Chen
 *
 */
public final class Program1Parse1 extends Program1 {

    /*
     * Private members --------------------------------------------------------
     */

    /**
     * Parses a single BL instruction from {@code tokens} returning the
     * instruction name as the value of the function and the body of the
     * instruction in {@code body}.
     *
     * @param tokens
     *            the input tokens
     * @param body
     *            the instruction body
     * @return the instruction name
     * @replaces body
     * @updates tokens
     * @requires <pre>
     * [<"INSTRUCTION"> is a prefix of tokens]  and
     *  [<Tokenizer.END_OF_INPUT> is a suffix of tokens]
     * </pre>
     * @ensures <pre>
     * if [an instruction string is a proper prefix of #tokens]  and
     *    [the beginning name of this instruction equals its ending name]  and
     *    [the name of this instruction does not equal the name of a primitive
     *     instruction in the BL language] then
     *  parseInstruction = [name of instruction at start of #tokens]  and
     *  body = [Statement corresponding to the block string that is the body of
     *          the instruction string at start of #tokens]  and
     *  #tokens = [instruction string at start of #tokens] * tokens
     * else
     *  [report an appropriate error message to the console and terminate client]
     * </pre>
     */
    private static String parseInstruction(Queue<String> tokens,
            Statement body) {
        assert tokens != null : "Violation of: tokens is not null";
        assert body != null : "Violation of: body is not null";
        assert tokens.length() > 0 && tokens.front().equals("INSTRUCTION") : ""
                + "Violation of: <\"INSTRUCTION\"> is proper prefix of tokens";

        tokens.dequeue();

        String tempVal = tokens.dequeue();

        tokens.dequeue();

        for (int numOfOccurences = 0; numOfOccurences < tokens.length();) {
            if (tokens.front().equals("IF") || tokens.front().equals("WHILE")) {
                numOfOccurences = tokens.length();
            } else if (tokens.front() != "IF" || tokens.front() != "WHILE") {
                body.assembleCall(tokens.dequeue());
                numOfOccurences = numOfOccurences + 1;
            }
        }

        return tempVal;

    }

    /*
     * Constructors -----------------------------------------------------------
     */

    /**
     * No-argument constructor.
     */
    public Program1Parse1() {
        super();
    }

    /*
     * Public methods ---------------------------------------------------------
     */

    @Override
    public void parse(SimpleReader in) {
        assert in != null : "Violation of: in is not null";
        assert in.isOpen() : "Violation of: in.is_open";
        Queue<String> tokens = Tokenizer.tokens(in);
        this.parse(tokens);
    }

    @Override
    public void parse(Queue<String> tokens) {
        assert tokens != null : "Violation of: tokens is not null";
        assert tokens.length() > 0 : ""
                + "Violation of: Tokenizer.END_OF_INPUT is a suffix of tokens";

        Statement newBody = this.newBody();
        String programName = "";

        if (tokens.front().equals("PROGRAM")) {
            tokens.dequeue(); // Consume "PROGRAM"
            programName = tokens.dequeue(); // Capture the program name
            if (!tokens.dequeue().equals("IS")) {
                System.err.println("Expected 'IS' after program name.");
                System.exit(1); //  error
            }
        } else {
            System.err.println("Expected 'PROGRAM' at the beginning.");
            System.exit(1);
        }

        // Instruction parsing loop
        while (!tokens.front().equals("BEGIN")
                && !tokens.front().equals(Tokenizer.END_OF_INPUT)) {
            if (tokens.front().equals("INSTRUCTION")) {
                parseInstruction(tokens, newBody);

            } else {
                System.err.println("Unexpected token: " + tokens.front());
                System.exit(1);
            }
        }

        // Parse the program body
        if (tokens.front().equals("BEGIN")) {
            tokens.dequeue();
            parseInstruction(tokens, newBody);
        }

        // Validate program structure and END token
        if (!tokens.dequeue().equals("END")
                || !tokens.dequeue().equals(programName)) {
            System.err.println("Program structure validation failed.");
            System.exit(1); // Proper error handling
        }
        if (!tokens.dequeue().equals(Tokenizer.END_OF_INPUT)) {
            System.err.println("Extra tokens after program end.");
            System.exit(1); // Proper error handling
        }

        this.setName(programName);
        this.swapBody(newBody);
    }

    /*
     * Main test method -------------------------------------------------------
     */

    /**
     * Main method.
     *
     * @param args
     *            the command line arguments
     */
    public static void main(String[] args) {
        SimpleReader in = new SimpleReader1L();
        SimpleWriter out = new SimpleWriter1L();
        /*
         * Get input file name
         */
        out.print("Enter valid BL program file name: ");
        String fileName = in.nextLine();
        /*
         * Parse input file
         */
        out.println("*** Parsing input file ***");
        Program p = new Program1Parse1();
        SimpleReader file = new SimpleReader1L(fileName);
        Queue<String> tokens = Tokenizer.tokens(file);
        file.close();
        p.parse(tokens);
        /*
         * Pretty print the program
         */
        out.println("*** Pretty print of parsed program ***");
        p.prettyPrint(out);

        in.close();
        out.close();
    }

}
