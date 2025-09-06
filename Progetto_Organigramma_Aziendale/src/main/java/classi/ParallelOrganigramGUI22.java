package classi;

import dao.JDBCorganigrammaDAO;
import dao.SerializableOrganigrammaDAO;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.*;
import java.util.List;

/**
 * GUI per la visualizzazione dell'organigramma.
 */
public class ParallelOrganigramGUI22 extends JFrame {
    private Organigramma chart; // Riferimento all'organigramma
    private Connection connection;
    private String selectedFile; // File selezionato dall'utente
    private JTextArea consoleTextArea;
    private Rectangle guideRectangle; // Rettangolo guida

    /**
     * Costruttore della classe ParallelOrganigramGUI22.
     *
     * @param chart      L'organigramma da visualizzare.
     */
    public ParallelOrganigramGUI22(Organigramma chart) {

        this.chart = chart;

        setTitle("Organigramma Parallelo");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setPreferredSize(new Dimension(800, 600));

        // Interfaccia per la selezione del file di lavoro
        selectedFile = showFileDialog();

        selectedFile = "jdbc:sqlite:"+selectedFile;

        try (Connection connection = DriverManager.getConnection(selectedFile)) {
            JDBCorganigrammaDAO j = new JDBCorganigrammaDAO(selectedFile);

            // Creazione delle tabelle se non esistono
            j.createTables();

            List<dipendente> dipendenti = j.dipendenti();
            List<unita> unita = j.unita();


            List<unita> presenti = new ArrayList<>();
            List<dipendente> presenti2 = new ArrayList<>();

            for(unita u : unita){
                boolean giaPresente = false ;
                unita y = null;
                for(unita x : presenti){
                    if (u.getName().equals(x.getName()) && u.getPadre().getName().equals(x.getPadre().getName())){
                        giaPresente = true ;
                        y = x ;

                    }
                }

                if(giaPresente){
                    y.addRuolo(u.ruoli().get(0));
                }
                else{
                    presenti.add(u);
                }
            }

            for(dipendente d : dipendenti){
                boolean giaPresente = false ;
                dipendente y = null ;

                for(dipendente x : presenti2){
                    if (d.getName().equals(x.getName()) && d.getPadre().getName().equals(x.getPadre().getName())){
                        giaPresente = true ;
                        y = d ;
                    }
                }
                if(giaPresente){
                    y.addRuolo(d.ruoli().get(0));
                }
                else{
                    presenti2.add(d);
                }
            }

            for(unita u : presenti){
                chart.addComponente(u.getPadre(),u);
            }

            for(dipendente d : presenti2){
                chart.addComponente(d.getPadre(),d);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }


        // Crea il pannello principale
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());

        // Crea il pannello per visualizzare l'organigramma
        OrganigramPanel organigramPanel = new OrganigramPanel();

        // Crea uno JScrollPane che avvolge il pannello dell'organigramma
        JScrollPane scrollPane = new JScrollPane(organigramPanel);

        // Imposta le opzioni di visualizzazione della scrollbar
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);

        mainPanel.add(scrollPane, BorderLayout.CENTER);

        JPanel guidePanel = createGuidePanel();
        // Aggiungi il pannello guida al contenitore principale
        mainPanel.add(guidePanel, BorderLayout.NORTH);


        // Aggiungi il pannello principale alla finestra
        setContentPane(mainPanel);

        // Crea il pannello per la console di output
        JPanel consolePanel = new JPanel(new BorderLayout());

        consoleTextArea = new JTextArea();
        consoleTextArea.setEditable(false);
        JScrollPane consoleScrollPane = new JScrollPane(consoleTextArea);
        consolePanel.add(consoleScrollPane, BorderLayout.CENTER);

        JTextField ruoloField = new JTextField();
        JButton searchButton = new JButton("Cerca");
        searchButton.addActionListener(e -> {
            String ruolo = ruoloField.getText();
            if (!ruolo.isEmpty()) {
                searchByRuolo(new ruolo(ruolo));
            }
        });

        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new BoxLayout(inputPanel, BoxLayout.X_AXIS));
        inputPanel.add(new JLabel("Ruolo:"));
        inputPanel.add(ruoloField);
        inputPanel.add(searchButton);

        consolePanel.add(inputPanel, BorderLayout.NORTH);

        mainPanel.add(consolePanel, BorderLayout.EAST);

        consolePanel.add(consoleScrollPane, BorderLayout.CENTER);

        // Aggiungi uno spazio extra alla larghezza preferita del pannello della text area
        consolePanel.setPreferredSize(new Dimension(consolePanel.getPreferredSize().width + 50, consolePanel.getPreferredSize().height));

        pack();

        setLocationRelativeTo(null);
        setVisible(true);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                try {
                    saveDataToDatabase();
                    saveDataFile();
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });

    }


    /**
     * Interfaccia per la selezione del file di lavoro.
     *
     * @return Il percorso del file selezionato.
     */
    private String showFileDialog() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Seleziona il file del Database");
        int result = fileChooser.showOpenDialog(this);

        if (result == JFileChooser.APPROVE_OPTION) {
            return fileChooser.getSelectedFile().getAbsolutePath();
        } else {
            // L'utente ha annullato la selezione del file
            System.exit(0);
            return null;
        }
    }
    
    private JPanel createGuidePanel() {
        StringBuilder guida = new StringBuilder();
        guida.append("=============================================");
        guida.append("\n");
        guida.append("Guida all'interfaccia ParallelOrganigramGUI:\n\n");
        guida.append("1. Clic destro su una casella per rimuoverla.\n");
        guida.append("2. Clic sinistro su una casella per:\n");
        guida.append("   - Visualizzare i ruoli associati ad una casella.\n");
        guida.append("   - Aggiungere un ruolo ad una casella.\n");
        guida.append("   - Rimuovere un ruolo da una casella.\n");
        guida.append("   - Creare una nuova unità o dipendente(ovviamente solo nelle caselle unità).\n");
        guida.append("3. Nella console di output a destra puoi:\n");
        guida.append("   - Inserire un ruolo da cercare e premere il pulsante 'Cerca'.\n");
        guida.append("   - Visualizzare le unità organizzative associate ad un ruolo cercato.\n");
        guida.append("4. Quando si chiude la finestra, i dati dell'organigramma vengono salvati nel database e su file organigramma.ser.\n");
        guida.append("=============================================");

        JPanel guidePanel = new JPanel(new BorderLayout());

        JButton guideButton = new JButton("Guida");
        guideButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Crea un JDialog per mostrare la nota
                JDialog noteDialog = new JDialog();
                noteDialog.setTitle("Guida");
                noteDialog.setModalityType(Dialog.ModalityType.APPLICATION_MODAL);

                // Crea un JTextArea per visualizzare il testo della nota
                JTextArea noteTextArea = new JTextArea(guida.toString());
                noteTextArea.setEditable(false);

                // Aggiungi il JTextArea al JDialog
                noteDialog.add(noteTextArea);

                // Imposta le dimensioni del JDialog
                noteDialog.setSize(600, 300);
                noteDialog.setLocationRelativeTo(guidePanel); // Posiziona il dialogo al centro rispetto al pannello guida

                // Mostra il JDialog
                noteDialog.setVisible(true);
            }
        });

        guidePanel.add(guideButton, BorderLayout.EAST);

        return guidePanel;
    }

    /**
     * Salva i dati dell'organigramma nel database.
     *
     * @throws SQLException Eccezione lanciata in caso di errore nell'accesso al database.
     */
    private void saveDataToDatabase() throws SQLException {
        JDBCorganigrammaDAO dao = new JDBCorganigrammaDAO(selectedFile);
        dao.saveToDatabase();
    }

    /**
     * Salva i dati dell'organigramma nel file.
     *
     * @throws SQLException Eccezione lanciata in caso di errore nell'accesso al database.
     */
    private void saveDataFile() throws SQLException {
        SerializableOrganigrammaDAO dao = new SerializableOrganigrammaDAO();
        dao.saveToDatabase();
    }

    /**
     * Ricerca e visualizza il ruolo nell'organigramma.
     *
     * @param ruolo Il ruolo da cercare.
     */
    private void searchByRuolo(ruolo ruolo) {
        String unitsWithRuolo = chart.trovareUnitaOrganizzativePerRuolo(ruolo);

        consoleTextArea.append("Riguardo il ruolo di "+ruolo+" :" + "\n");
        consoleTextArea.append(unitsWithRuolo);
        consoleTextArea.append("\n");
    }

    private class OrganigramPanel extends JPanel {
        private Map<Rectangle, componente> unitRectangles;

        public OrganigramPanel() {
            setPreferredSize(new Dimension(800, 600));
            unitRectangles = new HashMap<>();


            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {

                    int mouseX = e.getX();
                    int mouseY = e.getY();

                    // Verifica se il tocco del mouse è avvenuto all'interno di una casella valida
                    boolean validClick = false;
                    for (Rectangle rectangle : unitRectangles.keySet()) {
                        if (rectangle.contains(mouseX, mouseY)) {
                            validClick = true;
                            break;
                        }
                    }

                    if (validClick) {
                        // Il tocco del mouse è avvenuto all'interno di una casella valida
                        if (SwingUtilities.isRightMouseButton(e)) {
                            mouseX = e.getX();
                            mouseY = e.getY();
                            rimuoviComponente(mouseX, mouseY);
                        } else if (SwingUtilities.isLeftMouseButton(e)) {
                            mouseX = e.getX();
                            mouseY = e.getY();
                            CheckAndDisplayRoles1(mouseX, mouseY);

                            if(!(checkAndDisplayRoles2(mouseX, mouseY)))
                                return;
                            if(!(checkAndDisplayRoles3(mouseX, mouseY)))
                                return ;
                            showCreateDialog(mouseX, mouseY);
                        }
                    } else {
                        System.out.println("casella non selezionata");
                    }

                    repaint();
                }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            setBackground(Color.WHITE);

            int startX = 50;
            int startY = 50;
            int unitSpacing = 150;
            int employeeSpacing = 100;

            int chartWidth = drawUnit(g, startX, startY, chart.getRadice(), unitSpacing, employeeSpacing);
            int chartHeight = calculateChartHeight(chart.getRadice(), employeeSpacing);

            setPreferredSize(new Dimension(chartWidth + startX + 50, chartHeight + startY + 50));
            revalidate();
        }

        private void rimuoviComponente(int mouseX, int mouseY) {
            Optional<Rectangle> optionalUnitRect = unitRectangles.keySet().stream()
                    .filter(rectangle -> rectangle.contains(mouseX, mouseY))
                    .findFirst();

            optionalUnitRect.ifPresent(unitRect -> {
                componente unit = unitRectangles.get(unitRect);
                int dialogResult = JOptionPane.showConfirmDialog(this, "Sei sicuro di voler rimuovere questa casella?", "Conferma Rimozione", JOptionPane.YES_NO_OPTION);
                if (dialogResult == JOptionPane.YES_OPTION) {
                    chart.removeComponente(unit , selectedFile);


                    unitRectangles.remove(unitRect);
                }
            });
        }

        private void showCreateDialog(int mouseX, int mouseY) {
            componente component = getComponent(mouseX, mouseY);
            if(component instanceof dipendente){
                return;
            }

            String[] options = {"Unità", "Dipendente"};
            int choice = JOptionPane.showOptionDialog(this, "Scegli il tipo di componente da creare:", "Crea componente",
                    JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, options, options[0]);

            if (choice == 0) {
                showCreateUnitDialog(mouseX, mouseY);
            } else if (choice == 1) {
                showCreateEmployeeDialog(mouseX, mouseY);
            }
        }

        private void showCreateUnitDialog(int mouseX, int mouseY) {
            Optional<Rectangle> optionalUnitRect = unitRectangles.keySet().stream()
                    .filter(rectangle -> rectangle.contains(mouseX, mouseY))
                    .findFirst();

            optionalUnitRect.ifPresent(unitRect -> {
                componente unit = unitRectangles.get(unitRect);

                JTextField nomeComponenteField = new JTextField();
                JTextField nomeRuolo = new JTextField();

                JPanel inputPanel = new JPanel(new GridLayout(2, 2));
                inputPanel.add(new JLabel("Nome componente:"));
                inputPanel.add(nomeComponenteField);
                inputPanel.add(new JLabel("Ruolo:"));
                inputPanel.add(nomeRuolo);

                int result = JOptionPane.showConfirmDialog(this, inputPanel, "Aggiungi componente",
                        JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

                if (result == JOptionPane.OK_OPTION) {
                    String nomeComponente = nomeComponenteField.getText();
                    String ruolo = nomeRuolo.getText();

                    if (nomeComponente != null && !nomeComponente.isEmpty() && ruolo != null && !ruolo.isEmpty()) {
                        componente nuovoComponente = new unita(nomeComponente);
                        nuovoComponente.addRuolo(new ruolo(ruolo));
                        chart.addComponente(unit, nuovoComponente);
                        repaint();
                    }
                    else{
                        System.out.println(("nome"+nomeComponente.toString()));
                        JOptionPane.showMessageDialog(null, "Parametri Non Inseriti", "Errore", JOptionPane.ERROR_MESSAGE);

                    }
                }
            });
        }

        private void showCreateEmployeeDialog(int mouseX, int mouseY) {

            Optional<Rectangle> optionalUnitRect = unitRectangles.keySet().stream()
                    .filter(rectangle -> rectangle.contains(mouseX, mouseY))
                    .findFirst();

            optionalUnitRect.ifPresent(unitRect -> {
                componente unit = unitRectangles.get(unitRect);

                JTextField nomeComponenteField = new JTextField();
                JComboBox<ruolo> ruoloComboBox = new JComboBox<>();

                JPanel inputPanel = new JPanel(new GridLayout(2, 2));
                inputPanel.add(new JLabel("Nome componente:"));
                inputPanel.add(nomeComponenteField);
                inputPanel.add(new JLabel("Ruolo:"));
                inputPanel.add(ruoloComboBox);

                //componente superiore = unit.getPadre();
                if (unit != null) {
                    unita u = (unita) unit ;
                    List<ruolo> roles = u.ruoli();
                    if (!roles.isEmpty()) {
                        for (ruolo ruolo : roles) {
                            ruoloComboBox.addItem(ruolo);
                        }
                    }
                    else{
                        JOptionPane.showMessageDialog(null, "Nessun Ruolo Presente", "Errore", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                }

                int result = JOptionPane.showConfirmDialog(this, inputPanel, "Aggiungi componente",
                        JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

                if (result == JOptionPane.OK_OPTION) {
                    String nomeComponente = nomeComponenteField.getText();
                    ruolo selectedRuolo = (ruolo) ruoloComboBox.getSelectedItem();

                    if (nomeComponente != null && !nomeComponente.isEmpty() && selectedRuolo != null) {
                        componente nuovoComponente = new dipendente(nomeComponente);
                        nuovoComponente.addRuolo(selectedRuolo);
                        chart.addComponente(unit, nuovoComponente);
                        repaint();
                    }
                    else{
                        JOptionPane.showMessageDialog(null, "Parametri Non Inseriti", "Errore", JOptionPane.ERROR_MESSAGE);

                    }
                }
            });
        }

        private void createRectangle(int x, int y, componente type, componente component) {
            Rectangle rectangle = new Rectangle(x, y, 150, 50);
            unitRectangles.put(rectangle, component);
        }

        private int calculateChartHeight(componente unit, int employeeSpacing) {
            int height = 0;
            int childY = employeeSpacing + 100;

            for (componente childUnit : unit.getComposizione()) {
                int childHeight = calculateChartHeight(childUnit, employeeSpacing);
                height = Math.max(height, childY + childHeight);
                childY += employeeSpacing;
            }

            return height;
        }

        private int drawUnit(Graphics g, int x, int y, componente unit, int unitSpacing, int employeeSpacing) {
            Rectangle unitRect = new Rectangle(x, y, 150, 50);
            unitRectangles.put(unitRect, unit);

            g.setColor(Color.ORANGE); // Modifica il colore delle caselle relative ai dipendenti
            if (unit instanceof dipendente) {
                g.setColor(Color.ORANGE);
            } else if (unit instanceof unita) {
                g.setColor(Color.GREEN);
            }
            g.fillRect(x, y, 150, 50);

            g.setColor(Color.BLACK);
            g.drawString(unit.getName(), x + 10, y + 30);

            int childX = x;
            int childY = y + 100;
            int lastChildX = x;

            for (componente childUnit : unit.getComposizione()) {
                int parentX = x + 75;
                int parentY = y + 50;
                int childMidX = childX + 75;

                g.drawLine(parentX, parentY, parentX, parentY + 25);
                g.drawLine(parentX, parentY + 25, childMidX, parentY + 25);
                g.drawLine(childMidX, parentY + 25, childMidX, childY);
                g.drawLine(childMidX, childY, childX + 75, childY);

                int childWidth = drawUnit(g, childX, childY, childUnit, unitSpacing, employeeSpacing);
                lastChildX = Math.max(lastChildX, childX + childWidth);

                childX = lastChildX + unitSpacing;
            }

            return lastChildX - x + 150;
        }

        private boolean checkAndDisplayRoles2(int mouseX, int mouseY) {

            componente component = getComponent(mouseX, mouseY);

            if(component != null){
                if(component instanceof unita){
                    List<ruolo> roles = component.ruoli();

                    JTextField ruoloField = new JTextField();

                    JPanel inputPanel = new JPanel(new GridLayout(2, 1));
                    inputPanel.add(new JLabel("Aggiungi ruolo:"));
                    inputPanel.add(ruoloField);

                    int result = JOptionPane.showConfirmDialog(this, inputPanel, "Aggiungi ruolo",
                            JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

                    if (result == JOptionPane.OK_OPTION) {
                        String ruolo = ruoloField.getText();

                        if (!ruolo.isEmpty()) {
                            component.addRuolo(new ruolo(ruolo));
                            repaint();
                        }
                    }
                    if(result == JOptionPane.DEFAULT_OPTION){
                        return false ;
                    }
                }
            }

            unita superiore = component.getPadre();
            if (superiore != null && component != null) {
                if(component instanceof dipendente){
                    List<ruolo> roles = superiore.ruoli();

                    if (!roles.isEmpty()) {
                        Object[] roleOptions = roles.toArray();
                        Object selectedRole = JOptionPane.showInputDialog(this, "Seleziona un ruolo da aggiungere:", "Ruoli disponibili", JOptionPane.QUESTION_MESSAGE, null, roleOptions, roleOptions[0]);

                        if (selectedRole != null) {
                            String ruolo = selectedRole.toString();
                            component.addRuolo(new ruolo(ruolo));
                            repaint();
                        }
                        else {
                            int result = JOptionPane.showConfirmDialog(this, "Vuoi Continuare ?", "Chiusura", JOptionPane.YES_NO_OPTION);
                            if (result == JOptionPane.YES_OPTION) {
                                return true;
                            } else {
                                return false;
                            }
                        }
                    }
                }
            }
            return true ;
        }

        private boolean checkAndDisplayRoles3(int mouseX, int mouseY) {

            componente component = getComponent(mouseX, mouseY);
            //unita superiore = component.getPadre();
            if (component != null) {
                List<ruolo> roles = component.ruoli();
                if (!roles.isEmpty()) {
                    Object[] roleOptions = roles.toArray();
                    Object selectedRole = JOptionPane.showInputDialog(this, "Seleziona un ruolo da rimuovere:", "Ruoli presenti", JOptionPane.QUESTION_MESSAGE,null, roleOptions, roleOptions[0]);
                    if (selectedRole != null) {
                        String ruolo = selectedRole.toString();
                        component.removeRuolo(new ruolo(ruolo),selectedFile);
                        if(component.ruoli().size() == 0 ){
                            return true;
                        }
                        repaint();
                    }
                    else {
                        int result = JOptionPane.showConfirmDialog(this, "Vuoi Continuare ?", "Chiusura", JOptionPane.YES_NO_OPTION);
                        if (result == JOptionPane.YES_OPTION) {
                            return true;
                        } else {
                            return false;
                        }
                    }
                }
            }

            return true;

        }



        private void CheckAndDisplayRoles1(int mouseX, int mouseY) {
            for (Rectangle unitRect : unitRectangles.keySet()) {
                if (unitRect.contains(mouseX, mouseY)) {
                    componente unit = unitRectangles.get(unitRect);
                    List<ruolo> roles = unit.ruoli();
                    if (!roles.isEmpty()) {
                        StringBuilder message = new StringBuilder();
                        message.append("Ruoli:\n");
                        for (ruolo role : roles) {
                            message.append(role.getName()).append("\n");
                        }
                        JOptionPane.showMessageDialog(this, message.toString(), "Ruoli", JOptionPane.INFORMATION_MESSAGE);
                    }
                }
            }
        }

        @Nullable
        private componente getComponent(int mouseX, int mouseY) {
            for (Rectangle unitRect : unitRectangles.keySet()) {
                if (unitRect.contains(mouseX, mouseY)) {
                    return unitRectangles.get(unitRect);
                }
            }
            return null;
        }
    }
}