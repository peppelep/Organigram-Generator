package dao;

import classi.Organigramma;
import classi.unita;

import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.io.IOException;

import java.sql.Connection;
import java.sql.SQLException;

public class SerializableOrganigrammaDAO implements organigrammaDAO {

    @Override
    public void createTables() throws SQLException {
        // No table creation required for serialization.
        // You can leave this method empty.
    }

    @Override
    public void saveToDatabase() throws SQLException {
        try {
            // Saving the organizational units using serialization
            FileOutputStream fileOut = new FileOutputStream("organigramma.ser");
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(Organigramma.getInstance());
            out.close();
            fileOut.close();
            System.out.println("Organigramma serialized and saved to organigramma.ser");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}