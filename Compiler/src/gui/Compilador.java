package gui;
import javax.swing.UIManager;


/**
 *
 * @author 08062671
 */
public class Compilador {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
    	try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			e.printStackTrace();
		}
        PGrafica p = new PGrafica();
        p.setVisible(true);
        
    }
}
