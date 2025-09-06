package classi;

import javax.swing.*;

public class Main {

    //private static final String DB_URL = "jdbc:sqlite:/Users/giuseppelepore/Downloads/progetto2.db";

    public static void main(String[] args) {
        Organigramma o = Organigramma.getInstance();
        SwingUtilities.invokeLater(() -> {ParallelOrganigramGUI22 gui = new ParallelOrganigramGUI22(o);gui.setVisible(true);});
    }
}
