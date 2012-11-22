package com.virtualmachine.window;

import java.io.File;
import java.util.LinkedList;

import javax.swing.JFileChooser;
import javax.swing.table.DefaultTableModel;

import com.virtualmachine.AssemblyFile;
import com.virtualmachine.Core;

/**
 *
 * @author Renan Cadaval
 */
@SuppressWarnings("serial")
public class MainWindow extends javax.swing.JFrame {

    private Core core;
	private String lastPath = null;

    /** Creates new form Interface */
    public MainWindow() {
        initComponents();
    }

    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        executar = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        cod_maq = new javax.swing.JTable();
        jScrollPane2 = new javax.swing.JScrollPane();
        memoria = new javax.swing.JTable();
        jScrollPane3 = new javax.swing.JScrollPane();
        saida = new javax.swing.JTextArea();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        execucaoPAP = new javax.swing.JRadioButton();
        jScrollPane4 = new javax.swing.JScrollPane();
        execLog = new javax.swing.JTextArea();
        execucaoCompleta = new javax.swing.JRadioButton();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        menu = new javax.swing.JMenuBar();
        arquivo = new javax.swing.JMenu();
        abrir = new javax.swing.JMenuItem();
        fechar = new javax.swing.JMenuItem();
        sair = new javax.swing.JMenuItem();
        ajuda = new javax.swing.JMenu();
        comandos = new javax.swing.JMenuItem();
        sobre = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Máquina Virtual");
        setResizable(false);

        executar.setText("Executar");
        executar.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                executarActionPerformed(evt);
            }
        });

        cod_maq.setModel(new javax.swing.table.DefaultTableModel(
                new Object [][] {

                },
                new String [] {
                        "I", "Rótulo", "Instrução", "Atributo 1", "Atributo 2"
                }
                ));
        cod_maq.setShowHorizontalLines(false);
        cod_maq.setShowVerticalLines(false);
        jScrollPane1.setViewportView(cod_maq);

        memoria.setModel(new javax.swing.table.DefaultTableModel(
                new Object [][] {

                },
                new String [] {
                        "Índice", "Valor"
                }
                ));
        jScrollPane2.setViewportView(memoria);

        jScrollPane3.setAutoscrolls(true);

        saida.setColumns(20);
        saida.setEditable(false);
        saida.setLineWrap(true);
        saida.setRows(5);
        jScrollPane3.setViewportView(saida);

        jLabel1.setText("Código de Máquina");

        jLabel2.setText("Memória (Pilha)");

        jLabel3.setText("Saída");

        buttonGroup1.add(execucaoPAP);
        execucaoPAP.setText("Passo-a-passo");

        execLog.setColumns(20);
        execLog.setEditable(false);
        execLog.setRows(5);
        jScrollPane4.setViewportView(execLog);

        buttonGroup1.add(execucaoCompleta);
        execucaoCompleta.setSelected(true);
        execucaoCompleta.setText("Completo");

        jLabel4.setText("Log de execução");

        jLabel5.setText("Execução");

        arquivo.setText("Arquivo");

        abrir.setText("Abrir");
        abrir.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                abrirActionPerformed(evt);
            }
        });
        arquivo.add(abrir);

        fechar.setText("Fechar");
        fechar.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fecharActionPerformed(evt);
            }
        });
        arquivo.add(fechar);

        sair.setText("Sair");
        sair.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                sairActionPerformed(evt);
            }
        });
        arquivo.add(sair);

        menu.add(arquivo);

        ajuda.setText("Ajuda");

        comandos.setText("Comandos");
        comandos.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                comandosActionPerformed(evt);
            }
        });
        ajuda.add(comandos);

        sobre.setText("Sobre");
        sobre.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                sobreActionPerformed(evt);
            }
        });
        ajuda.add(sobre);

        menu.add(ajuda);

        setJMenuBar(menu);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(layout.createSequentialGroup()
                                        .addComponent(jLabel1)
                                        .addGap(406, 406, 406))
                                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                                        .addComponent(jLabel4)
                                                                        .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 309, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                                                .addGroup(layout.createSequentialGroup()
                                                                                        .addComponent(jLabel3)
                                                                                        .addGap(208, 208, 208))
                                                                                        .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 245, Short.MAX_VALUE)))
                                                                                        .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 566, Short.MAX_VALUE))
                                                                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)))
                                                                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                                                                .addComponent(jLabel2)
                                                                                                .addComponent(executar)
                                                                                                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 227, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                                                .addComponent(execucaoCompleta)
                                                                                                .addComponent(execucaoPAP)
                                                                                                .addGroup(layout.createSequentialGroup()
                                                                                                        .addGap(10, 10, 10)
                                                                                                        .addComponent(jLabel5)))
                                                                                                        .addContainerGap())
                );
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel1)
                                .addComponent(jLabel2))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                        .addComponent(jScrollPane1)
                                        .addComponent(jScrollPane2))
                                        .addGap(18, 18, 18)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                                .addComponent(jLabel3)
                                                .addComponent(jLabel5)
                                                .addComponent(jLabel4))
                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 126, Short.MAX_VALUE)
                                                        .addGroup(layout.createSequentialGroup()
                                                                .addGap(15, 15, 15)
                                                                .addComponent(execucaoCompleta)
                                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                                                .addComponent(execucaoPAP)
                                                                .addGap(18, 18, 18)
                                                                .addComponent(executar))
                                                                .addComponent(jScrollPane4, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 126, Short.MAX_VALUE))
                                                                .addGap(32, 32, 32))
                );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void executarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_executarActionPerformed
        cod_maq.setRowSelectionAllowed(true);
        if (execucaoCompleta.isSelected()) {
        	saida.setText("");
            while (!core.isExecutionFinished() && !core.printingIO) {
                core.runStep();
            }
        } else {
            core.runStep();
        }

    }//GEN-LAST:event_executarActionPerformed

    private void abrirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_abrirActionPerformed
        core = Core.getInstance();
        core.reset();
        core.setParenteWindow(this);
        //Create a file chooser

        final JFileChooser fc;
        if (lastPath != null) {
        	fc = new JFileChooser(lastPath);
        } else {
        	fc = new JFileChooser("./sample");
        }

        //In response to a button click:
        int returnVal = fc.showOpenDialog(this);

        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile();

            lastPath = file.getAbsolutePath();
            core.setCurrentAssembly(new AssemblyFile(file.getAbsolutePath()));
            //This is where a real application would open the file.
            populateInterface(core.getCurrentAssembly());
            //        	log.append("Opening: " + file.getName() + "." + newline);
        } else {
            //        	log.append("Open command cancelled by user." + newline);
        }
    }//GEN-LAST:event_abrirActionPerformed


    private void fecharActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fecharActionPerformed

    }//GEN-LAST:event_fecharActionPerformed

    private void sairActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_sairActionPerformed

    }//GEN-LAST:event_sairActionPerformed

    private void sobreActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_sobreActionPerformed

    }//GEN-LAST:event_sobreActionPerformed

    private void comandosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_comandosActionPerformed



    }//GEN-LAST:event_comandosActionPerformed

    /**
     * Populates commands table from an AssemblyFile's attributes (commands i.e).
     *
     * @param af AssemblyFile used to retrieve attributes
     */
    public void populateInterface(AssemblyFile af) {
        if (af != null) {
            DefaultTableModel dtm = (DefaultTableModel) cod_maq.getModel();
            dtm.getDataVector().removeAllElements();

            LinkedList<String> commands = af.getCommands();
            LinkedList<String> firstAttributes = af.getFirstAttributes();
            LinkedList<String> secondAttributes = af.getSecondAttributes();
            LinkedList<String> identifiers = af.getIdentifiers();

            for (int i=0; i < commands.size(); i++) {
                dtm.addRow(new String[]{String.valueOf(i), identifiers.get(i), commands.get(i), firstAttributes.get(i), secondAttributes.get(i)});
            }
        }
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenuItem abrir;
    private javax.swing.JMenu ajuda;
    private javax.swing.JMenu arquivo;
    private javax.swing.ButtonGroup buttonGroup1;
    public javax.swing.JTable cod_maq;
    private javax.swing.JMenuItem comandos;
    private javax.swing.JTextArea execLog;
    private javax.swing.JRadioButton execucaoCompleta;
    private javax.swing.JRadioButton execucaoPAP;
    private javax.swing.JButton executar;
    private javax.swing.JMenuItem fechar;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    public javax.swing.JTable memoria;
    private javax.swing.JMenuBar menu;
    public javax.swing.JTextArea saida;
    private javax.swing.JMenuItem sair;
    private javax.swing.JMenuItem sobre;
    // End of variables declaration//GEN-END:variables
}