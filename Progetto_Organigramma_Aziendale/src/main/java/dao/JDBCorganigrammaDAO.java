package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import classi.Organigramma ;
import classi.dipendente;
import classi.unita;

import java.util.List;

public class JDBCorganigrammaDAO implements organigrammaDAO {
    private String DB_URL;
    private JDBCunitaDAO root ;
    private JDBCdipendenteDAO x ;
    private Connection connection ;

    public JDBCorganigrammaDAO(String url){
        this.DB_URL = url ;
        root = new JDBCunitaDAO(DB_URL);
        x = new JDBCdipendenteDAO(DB_URL);

        try {
            connection = DriverManager.getConnection(url) ;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void createTables() throws SQLException {
        String createUnitsTable = "CREATE TABLE IF NOT EXISTS unita (nome TEXT , role TEXT , padre TEXT)";
        String createEmployeesTable = "CREATE TABLE IF NOT EXISTS dipendente (nome TEXT, role TEXT , padre TEXT)";


        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate(createUnitsTable);
            //statement.executeUpdate(createRolesTable);
            statement.executeUpdate(createEmployeesTable);
        }
    }

    public void saveToDatabase() throws SQLException {
        // Salvataggio delle unità organizzative
        root.addunita((unita)Organigramma.getInstance().getRadice());
    }

    public List<dipendente> dipendenti(){
        return x.getAlldipendente();
    }

    public List<unita> unita(){
        return root.getAllunita();
    }
}
