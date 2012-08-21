package com.virtualmachine;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
		Pattern p = Pattern.compile("^(\\w*)\\s+(\\w+)\\s+(\\w*)\\s*(\\w*)\\s*$");
		Matcher m;
		
		try {
			BufferedReader br = new BufferedReader(new FileReader(path));
			
			while ((line = br.readLine()) != null) {
				m = p.matcher(line);
				m.matches();
				identifiers.add(m.group(1));
				commands.add(m.group(2));
				firstAttributes.add(m.group(3));
				secondAttribuets.add(m.group(4));
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
