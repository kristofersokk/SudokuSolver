package sudokusolver;

import javax.swing.*;
import java.awt.*;

/**
 * Created by Kristofer on 18/02/2017.
 */
public class FileChooser extends JFrame {

    public FileChooser() throws HeadlessException {

    }

    public void openFile() {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        chooser.setMultiSelectionEnabled(false);
        int eval = chooser.showOpenDialog(FileChooser.this);
        if (eval == JFileChooser.APPROVE_OPTION){
            System.out.println(chooser.getSelectedFile());
        }
        if (eval == JFileChooser.CANCEL_OPTION){
            System.out.println("Opening canceled");

        }
    }


}
