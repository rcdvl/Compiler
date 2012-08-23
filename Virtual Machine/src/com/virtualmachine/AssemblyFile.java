package com.virtualmachine;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.StringTokenizer;

public class AssemblyFile {
	
	private LinkedList<String> identifiers;
	private LinkedList<String> commands;
	private LinkedList<String> firstAttributes;
	private LinkedList<String> secondAttribuets;
	private File file;
	
	public AssemblyFile(String filePath) {
		identifiers = new LinkedList<String>();
		commands = new LinkedList<String>();
		firstAttributes = new LinkedList<String>();
		secondAttribuets = new LinkedList<String>();
		
		file = new File(filePath);
		parse(filePath);
	}
	
	private void parse(String path) {
		String line;
		StringTokenizer st;
		
		try {
			BufferedReader br = new BufferedReader(new FileReader(path));
			
			while ((line = br.readLine()) != null) {
				st = new StringTokenizer(line, " ");
				if (st.countTokens() == 3) {
					while (st.hasMoreTokens()) {
						identifiers.add("");
						commands.add(st.nextToken());
						firstAttributes.add(st.nextToken());
						secondAttribuets.add(st.nextToken());
					}
				} else if (st.countTokens() == 2) {
					while (st.hasMoreTokens()) {
						String nt = st.nextToken();
						if (Arrays.asList(Core.COMMANDS).contains(nt)) {
							identifiers.add("");
							commands.add(nt);
							firstAttributes.add(st.nextToken());
							secondAttribuets.add("");
						} else {
							identifiers.add(nt);
							commands.add(st.nextToken());
							firstAttributes.add("");
							secondAttribuets.add("");
						}
					}
				} else if (st.countTokens() == 1) {
					identifiers.add("");
					commands.add(st.nextToken());
					firstAttributes.add("");
					secondAttribuets.add("");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public LinkedList<String> getIdentifiers() {
		return identifiers;
	}
	
	public LinkedList<String> getCommands() {
		return commands;
	}
	
	public LinkedList<String> getFirstAttributes() {
		return firstAttributes;
	}
	
	public LinkedList<String> getSecondAttributes() {
		return secondAttribuets;
	}
	
	public File getFile() {
		return file;
	}
}
