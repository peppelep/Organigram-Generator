package dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import classi.componente;
import classi.dipendente;
import classi.ruolo;
import classi.unita;

import javax.management.relation.Role;

public class JDBCunitaDAO implements unitaDAO {
    private String DB_URL ;


    public JDBCunitaDAO(String url){
        this.DB_URL = url ;
    }

    @Override
    public List<unita> getAllunita() {
        List<unita> utenti = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(DB_URL);
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT * FROM unita")) {

            while (resultSet.next()) {
                String nome = resultSet.getString("nome");
                String rs = resultSet.getString("role");
                String padre = resultSet.getString("padre");

                unita utente = new unita(nome);
                ruolo r = new ruolo(rs);
                unita p = new unita(padre);

                utente.setPadre(p);
                utente.addRuolo(r);

                utenti.add(utente);

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return utenti;
    }


    @Override
    public void addunita(unita unita) {
        try (Connection connection = DriverManager.getConnection(DB_URL);
             PreparedStatement selectStatement = connection.prepareStatement("SELECT COUNT(*) FROM unita WHERE nome = ? AND role = ? AND padre = ?");
             PreparedStatement insertStatement = connection.prepareStatement("INSERT INTO unita (nome,role,padre) VALUES (?, ? ,?)")) {

            for (ruolo r : unita.ruoli()) {
                selectStatement.setString(1, unita.getName());
                selectStatement.setString(2, r.toString());
                selectStatement.setString(3, unita.getPadre().getName());

                // Esegui la query per contare i record corrispondenti
                ResultSet resultSet = selectStatement.executeQuery();
                resultSet.next();
                int count = resultSet.getInt(1);

                // Verifica se esiste già un valore simile nel database
                if (count == 0) {
                    // Se non esiste, esegui l'inserimento
                    insertStatement.setString(1, unita.getName());
                    insertStatement.setString(2, r.toString());
                    insertStatement.setString(3, unita.getPadre().getName());
                    insertStatement.executeUpdate();
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        for (componente c : unita.getComposizione()) {
            if(c instanceof unita){
                addunita((unita) c);
            }
            else{
                JDBCdipendenteDAO j = new JDBCdipendenteDAO(DB_URL);
                j.adddipendente((dipendente) c);
            }
        }
    }

    @Override
    public void addRole(unita unita,ruolo r) {
        try (Connection connection = DriverManager.getConnection(DB_URL);
             PreparedStatement selectStatement = connection.prepareStatement("SELECT COUNT(*) FROM unita WHERE nome = ? AND role = ? AND padre = ?");
             PreparedStatement insertStatement = connection.prepareStatement("INSERT INTO unita (nome, role, padre) VALUES (?, ?, ?)")) {

            selectStatement.setString(1, unita.getName());
            selectStatement.setString(2, r.toString());
            selectStatement.setString(3, unita.getPadre().getName());

            // Esegui la query per contare i record corrispondenti
            ResultSet resultSet = selectStatement.executeQuery();
            resultSet.next();
            int count = resultSet.getInt(1);

            // Verifica se esiste già un valore simile nel database
            if (count == 0) {
                // Se non esiste, esegui l'inserimento
                insertStatement.setString(1, unita.getName());
                insertStatement.setString(2, r.toString());
                insertStatement.setString(3, unita.getPadre().getName());
                insertStatement.executeUpdate();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void removeRole(unita unita,ruolo r) {
        try (Connection connection = DriverManager.getConnection(DB_URL);
             PreparedStatement statement = connection.prepareStatement("DELETE FROM unita WHERE nome = ? AND role = ? AND padre = ?")) {

            statement.setString(1, unita.getName());
            statement.setString(2, r.toString());
            statement.setString(3,unita.getPadre().getName());

            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        for (componente c : unita.getComposizione()) {
            if(c instanceof unita){
                deleteunita((unita) c);
            }
            else{
                JDBCdipendenteDAO j = new JDBCdipendenteDAO(DB_URL);
                j.deletedipendente((dipendente) c);
            }
        }
    }

    @Override
    public void deleteunita(unita unita) {
        try (Connection connection = DriverManager.getConnection(DB_URL);
             PreparedStatement statement = connection.prepareStatement("DELETE FROM unita WHERE nome = ? AND padre = ?")) {

            statement.setString(1, unita.getName());
            statement.setString(2,unita.getPadre().getName());

            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        for (componente c : unita.getComposizione()) {
            if(c instanceof unita){
                deleteunita((unita) c);
            }
            else{
                JDBCdipendenteDAO j = new JDBCdipendenteDAO(DB_URL);
                j.deletedipendente((dipendente) c);



            }
        }
    }
}