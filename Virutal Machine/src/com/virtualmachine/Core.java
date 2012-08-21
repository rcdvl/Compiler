package com.virtualmachine;

import java.util.LinkedList;

import javax.swing.JOptionPane;

import com.virtualmachine.window.MainWindow;

public class Core {
	
	private static int LIST_SIZE = 10000;
	
	private static Core core = null;
	private AssemblyFile currentAssembly;
	private String[] stack;
	private int[] memory;
	private int stackTop;
	private int programCounter;

	private boolean executionFinished;
	
	private MainWindow parentWindow;
	
	private Core() {
		stack = new String[LIST_SIZE];
		memory = new int[LIST_SIZE];
		programCounter = 0;
	}
	
	public static Core getInstance() {
		if (core == null) {
			core = new Core();
		}
		return core;
	}

	public AssemblyFile getCurrentAssembly() {
		return currentAssembly;
	}
	
	public void setCurrentAssembly(AssemblyFile af) {
		this.currentAssembly = af;
	}
	
	public int getProgramCounter() {
		return programCounter;
	}

	public void setParenteWindow(MainWindow parent) {
		parentWindow = parent;
	}
	/**
	 * 
	 * <p>LDC k (Carregar constante):
	 * 		<ul><li>S:=s + 1 ; M [s]: = k</li></ul></p>
	 * <p>LDV n (Carregar valor):
	 * 		<ul><li>S:=s + 1 ; M[s]:=M[n]</li></ul></p>
	 * <p>ADD (Somar):
	 * 		<ul><li>M[s-1]:=M[s-1] + M[s]; s:=s - 1</li></ul></p>
	 * <p>SUB (Subtrair):
	 *  	<ul><li>M[s-1]:=M[s-1] - M[s]; s:=s - 1</li></ul></p>
	 * <p>MULT (Multiplicar):
	 * 		<ul><li>M[s-1]:=M[s-1] * M[s]; s:=s - 1</li></ul></p>
	 * <p>DIVI (Dividir):
	 * 		<ul><li>M[s-1]:=M[s-1] div M[s]; s:=s - 1</li></ul></p>
	 * <p>INV (Inverter sinal):
	 * 		<ul><li>M[s]:= -M[s]</li></ul></p>
	 * <p>AND (Conjunção):
	 * 		<ul><li>se M [s-1] = 1 e M[s] = 1 então M[s-1]:=1 senão M[s-1]:=0; s:=s - 1</li></ul></p>
	 * <p>OR (Disjunção):
	 * 		<ul><li>se M[s-1] = 1 ou M[s] = 1 então M[s-1]:=1 senão M[s-1]:=0; s:=s - 1</li></ul></p>
	 * <p>NEG (Negação):
	 * 		<ul><li>M[s]:=1 - M[s]</li></ul></p>
	 * <p>CME (Comparar menor):
	 * 		<ul><li>se M[s-1] < M[s] então M[s-1]:=1 senão M[s-1]:=0; s:=s - 1</li></ul></p>
	 * <p>CMA (Comparar maior):
	 * 		<ul><li>se M[s-1] > M[s] então M[s-1]:=1 senão M[s-1]:=0; s:=s - 1</li></ul></p>
	 * <p>CEQ (Comparar igual):
	 * 		<ul><li>se M[s-1] = M[s] então M[s-1]:=1 senão M[s-1]:=0; s:=s - 1</li></ul></p>
	 * <p>CDIF (Comparar desigual):
	 * 		<ul><li>se M[s-1] ¹ M[s] então M[s-1]:=1 senão M[s-1]:=0; s:=s - 1</li></ul></p>
	 * <p>CMEQ (Comparar menor ou igual):
	 * 		<ul><li>se M[s-1] £ M[s] então M[s-1]:=1 senão M[s-1]:=0; s:=s - 1</li></ul></p>
	 * <p>CMAQ (Comparar maior ou igual):
	 * 		<ul><li>se M[s-1] ³ M[s] então M[s-1]:=1 senão M[s-1]:=0; s:=s - 1</li></ul></p>
	 * <p>START (Iniciar programa principal):
	 * 		<ul><li>S:=-1</li></ul></p>
	 * <p>HLT (Parar):
	 * 		<ul><li>"Pára a execução da MVD"</li></ul></p>
	 * <p>STR n (Armazenar valor):
	 * 		<ul><li>M[n]:=M[s]; s:=s-1</li></ul></p>
	 * <p>JMP t (Desviar sempre):
	 * 		<ul><li>i:= t</li></ul></p>
	 * <p>JMPF t (Desviar se falso):
	 * 		<ul><li>se M[s] = 0 então i:=t senão i:=i + 1;</li></ul>
	 * 		<ul><li>s:=s-1</li></ul></p>
	 * <p>NULL (Nada)</p>
	 * <p>RD (Leitura):
	 * 		<ul><li>S:=s + 1; M[s]:= “próximo valor de entrada”.</li></ul></p>
	 * <p>PRN (Impressão):
	 * 		<ul><li>"Imprimir M[s]"; s:=s-1</li></ul></p>
	 * <p>ALLOC m,n (Alocar memória):
	 * 		<ul><li>Para k:=0 até n-1 faça</li>
	 * 			<ul><li>{s:=s + 1; M[s]:=M[m+k]}</li></ul></ul></p>
	 * <p>DALLOC m,n (Desalocar memória):
	 * 		<ul><li>Para k:=n-1 até 0 faça</li>
	 * 			<ul><li>{M[m+k]:=M[s]; s:=s - 1}</li></ul></ul></p>
	 * <p>CALL t (Chamar procedimento ou função):
	 * 		<ul><li>S:=s + 1; M[s]:=i + 1; i:=t</li></ul
	 * ></p>
	 * <p>RETURN (Retornar de procedimento):
	 * 		<ul><li>i:=M[s]; s:=s - 1</li></ul></p>
	 */
	public void runStep(int stepNumber) {
		LinkedList<String> commands = currentAssembly.getCommands();
		LinkedList<String> firstAttributes = currentAssembly.getFirstAttributes();
		LinkedList<String> secondAttributes = currentAssembly.getFirstAttributes();
		LinkedList<String> identifiers = currentAssembly.getIdentifiers();
		
		programCounter += 1;
		
		System.out.println("comando: " + commands.get(stepNumber));
		
		if (commands.get(stepNumber).equals("LDC")) {
			stackTop += 1;
			memory[stackTop] = Integer.parseInt(firstAttributes.get(stepNumber));
		} else if (commands.get(stepNumber).equals("LDV")) {
			stackTop += 1;
			memory[stackTop] = memory[Integer.parseInt(firstAttributes.get(stepNumber))];
		} else if (commands.get(stepNumber).equals("ADD")) {
			memory[stackTop-1] = memory[stackTop-1] + memory[stackTop];
			stackTop -= 1;
		} else if (commands.get(stepNumber).equals("SUB")) {
			memory[stackTop-1] = memory[stackTop-1] - memory[stackTop];
			stackTop -= 1;
		} else if (commands.get(stepNumber).equals("MULT")) {
			memory[stackTop-1] = memory[stackTop-1] * memory[stackTop];
			stackTop -= 1;
		} else if (commands.get(stepNumber).equals("DIVI")) {
			memory[stackTop-1] = memory[stackTop-1] / memory[stackTop];
			stackTop -= 1;
		} else if (commands.get(stepNumber).equals("INV")) {
			memory[stackTop] = memory[stackTop] - (memory[stackTop] * 2);
		} else if (commands.get(stepNumber).equals("AND")) {
			if (memory[stackTop-1] == 1 && memory[stackTop] == 1) {
				memory[stackTop-1] = 1;
			} else {
				memory[stackTop-1] = 0;
			}
			stackTop -= 1;
		} else if (commands.get(stepNumber).equals("OR")) {
			if (memory[stackTop-1] == 1 || memory[stackTop] == 1) {
				memory[stackTop-1] = 1;
			} else {
				memory[stackTop-1] = 0;
			}
			stackTop -= 1;			
		} else if (commands.get(stepNumber).equals("NEG")) {
			memory[stackTop] = 1 - memory[stackTop];
		} else if (commands.get(stepNumber).equals("CME")) {
			if (memory[stackTop-1] < memory[stackTop]) {
				memory[stackTop-1] = 1;
			} else {
				memory[stackTop-1] = 0;
			}
			stackTop -= 1;
		} else if (commands.get(stepNumber).equals("CMA")) {
			if (memory[stackTop-1] > memory[stackTop]) {
				memory[stackTop-1] = 1;
			} else {
				memory[stackTop-1] = 0;
			}
			stackTop -= 1;
		} else if (commands.get(stepNumber).equals("CEQ")) {
			if (memory[stackTop-1] == memory[stackTop]) {
				memory[stackTop-1] = 1;
			} else {
				memory[stackTop-1] = 0;
			}
			stackTop -= 1;
		} else if (commands.get(stepNumber).equals("CDIF")) {
			if (memory[stackTop-1] != memory[stackTop]) {
				memory[stackTop-1] = 1;
			} else {
				memory[stackTop-1] = 0;
			}
			stackTop -= 1;
		} else if (commands.get(stepNumber).equals("CMEQ")) {
			if (memory[stackTop-1] <= memory[stackTop]) {
				memory[stackTop-1] = 1;
			} else {
				memory[stackTop-1] = 0;
			}
			stackTop -= 1;
		} else if (commands.get(stepNumber).equals("CMAQ")) {
			if (memory[stackTop-1] >= memory[stackTop]) {
				memory[stackTop-1] = 1;
			} else {
				memory[stackTop-1] = 0;
			}
			stackTop -= 1;
		} else if (commands.get(stepNumber).equals("START")) {
			stackTop = -1;
		} else if (commands.get(stepNumber).equals("HLT")) {
			executionFinished = true;
		} else if (commands.get(stepNumber).equals("STR")) {
			memory[Integer.valueOf(firstAttributes.get(stepNumber))] = memory[stackTop];
			stackTop -= 1;
		} else if (commands.get(stepNumber).equals("JMP")) {
			programCounter = Integer.valueOf(identifiers.indexOf(firstAttributes.get(stepNumber)));
		} else if (commands.get(stepNumber).equals("JMPF")) {
			if (memory[stackTop] == 0) {
				programCounter = Integer.valueOf(identifiers.indexOf(firstAttributes.get(stepNumber)));
			}
			stackTop -= 1;
		} else if (commands.get(stepNumber).equals("NULL")) {
			;
		} else if (commands.get(stepNumber).equals("RD")) {
			stackTop += 1;
			String input = JOptionPane.showInputDialog("Entre com um valor");
			memory[stackTop] = Integer.valueOf(input);
		} else if (commands.get(stepNumber).equals("PRN")) {
			System.out.println("VOU TACAR:" + memory[stackTop]);
			parentWindow.saida.setText(parentWindow.saida.getText() + "\n" + memory[stackTop]);
			stackTop -= 1;
		} else if (commands.get(stepNumber).equals("ALLOC")) {
			int m = Integer.valueOf(firstAttributes.get(stepNumber)), n = Integer.valueOf(secondAttributes.get(stepNumber));
			
			for (int i=0; i<n; i++) {
				stackTop += 1;
				memory[stackTop] = memory[m+i];
			}
		} else if (commands.get(stepNumber).equals("DALLOC")) {
			int m = Integer.valueOf(firstAttributes.get(stepNumber)), n = Integer.valueOf(secondAttributes.get(stepNumber));
			
			for (int i = n-1; i >= 0; i--) {
				memory[m+i] = memory[stackTop];
				stackTop -= 1;
			}
		} else if (commands.get(stepNumber).equals("CALL")) {
			stackTop += 1;
			memory[stackTop] = programCounter + 1;
			programCounter = Integer.valueOf(firstAttributes.get(stepNumber));
		} else if (commands.get(stepNumber).equals("RETURN")) {
			programCounter = memory[stackTop];
			stackTop -= 1;
		}

		parentWindow.repaint();
	}
}
