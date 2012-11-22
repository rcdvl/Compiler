package core;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;


public class CodeGenerator {
    private static CodeGenerator instance = null;
    private final File outputFile;
    private PrintStream writer;
	private String line;

    private CodeGenerator() {
        outputFile = new File(Lexic.getInstance().getSourceFile().getAbsolutePath().split("\\.")[0] + ".obj");
        try {
            writer = new PrintStream(outputFile);
        } catch (IOException e) {
            writer = null;
            e.printStackTrace();
        }
    }

    public static CodeGenerator getInstance() {
        if (instance == null) {
            instance = new CodeGenerator();
        }

        return instance;
    }

    public <T> void generate(T label, T command, T arg1, T arg2) {
        String line;
        line = String.format("%1$-" + 5 + "s", label.toString());
        line += String.format("%1$-" + 10 + "s", command.toString());
        line += String.format("%1$-" + 4 + "s", arg1.toString());
        line += String.format("%1$-" + 4 + "s", arg2.toString());

        writer.println(line);
        writer.flush();

    }

	public static void newInstance() {		
		instance = new CodeGenerator();
	}
	
	public void clearFile() {
		FileOutputStream erasor;
		try {
			erasor = new FileOutputStream(outputFile);
			erasor.write((new String()).getBytes());
			erasor.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void postponeGenerate(String string, String string2, int label,
			String string3) {
        line = String.format("%1$-" + 5 + "s", string);
        line += String.format("%1$-" + 10 + "s", string2);
        line += String.format("%1$-" + 4 + "s", label);
        line += String.format("%1$-" + 4 + "s", string3);
		
	}

	public void doPostponeGenerate(String string, String string2,
			int currentAddress, int i) {
		String args[] = line.split("\\s+");
		if (string2 != null) {
			generate(string, string2, currentAddress, i);
		}
		generate(args[0], args[1], args[2], "");
		line = "";
	}
}
