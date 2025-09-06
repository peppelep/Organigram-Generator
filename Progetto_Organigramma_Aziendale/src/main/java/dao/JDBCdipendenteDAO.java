package dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import classi.dipendente;
import classi.ruolo;
import classi.unita;

public class JDBCdipendenteDAO implements dipendenteDAO {
    private String DB_URL ;

    public JDBCdipendenteDAO(String url){
        this.DB_URL = url ;
    }
    @Override
    public List<dipendente> getAlldipendente() {
        List<dipendente> dipendenti = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(DB_URL);
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT * FROM dipendente")) {

            while (resultSet.next()) {
                String nome = resultSet.getString("nome");
                String rs = resultSet.getString("role");
                String padre = resultSet.getString("padre");

                dipendente dipendente = new dipendente(nome);
                ruolo r = new ruolo(rs);

                dipendente.addRuolo(r);
                dipendente.setPadre(new unita(padre));

                dipendenti.add(dipendente);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return dipendenti;
    }


    @Override
    public void adddipendente(dipendente dipendente) {
        try (Connection connection = DriverManager.getConnection(DB_URL);
             PreparedStatement selectStatement = connection.prepareStatement("SELECT COUNT(*) FROM dipendente WHERE nome = ? AND role = ? AND padre = ?");
             PreparedStatement insertStatement = connection.prepareStatement("INSERT INTO dipendente (nome, role, padre) VALUES (?, ?, ?)")) {

            for (ruolo r : dipendente.ruoli()) {
                selectStatement.setString(1, dipendente.getName());
                selectStatement.setString(2, r.toString());
                selectStatement.setString(3, dipendente.getPadre().getName());

                // Esegui la query per contare i record corrispondenti
                ResultSet resultSet = selectStatement.executeQuery();
                resultSet.next();
                int count = resultSet.getInt(1);

                // Verifica se esiste già un valore simile nel database
                if (count == 0) {
                    // Se non esiste, esegui l'inserimento
                    insertStatement.setString(1, dipendente.getName());
                    insertStatement.setString(2, r.toString());
                    insertStatement.setString(3, dipendente.getPadre().getName());
                    insertStatement.executeUpdate();
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }



    @Override
    public void addRole(dipendente dipendente,ruolo r) {
        try (Connection connection = DriverManager.getConnection(DB_URL);
             PreparedStatement selectStatement = connection.prepareStatement("SELECT COUNT(*) FROM dipendente WHERE nome = ? AND role = ? AND padre = ?");
             PreparedStatement insertStatement = connection.prepareStatement("INSERT INTO dipendente (nome, role, padre) VALUES (?, ?, ?)")) {

            selectStatement.setString(1, dipendente.getName());
            selectStatement.setString(2, r.toString());
            selectStatement.setString(3, dipendente.getPadre().getName());

            // Esegui la query per contare i record corrispondenti
            ResultSet resultSet = selectStatement.executeQuery();
            resultSet.next();
            int count = resultSet.getInt(1);

            // Verifica se esiste già un valore simile nel database
            if (count == 0) {
                // Se non esiste, esegui l'inserimento
                insertStatement.setString(1, dipendente.getName());
                insertStatement.setString(2, r.toString());
                insertStatement.setString(3, dipendente.getPadre().getName());
                insertStatement.executeUpdate();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void removeRole(dipendente dipendente, ruolo r) {
        try (Connection connection = DriverManager.getConnection(DB_URL);
             PreparedStatement statement = connection.prepareStatement("DELETE FROM dipendente WHERE nome = ? AND role = ? AND padre = ?")) {

            statement.setString(1, dipendente.getName());
            statement.setString(1, r.toString());
            statement.setString(3,dipendente.getPadre().getName());

            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deletedipendente(dipendente u) {
        try (Connection connection = DriverManager.getConnection(DB_URL);
             PreparedStatement statement = connection.prepareStatement("DELETE FROM dipendente WHERE nome = ? AND padre = ?")) {

            statement.setString(1, u.getName());
            statement.setString(2,u.getPadre().getName());

            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}