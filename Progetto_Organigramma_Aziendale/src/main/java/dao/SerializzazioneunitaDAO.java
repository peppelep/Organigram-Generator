package dao ;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import classi.dipendente;
import classi.ruolo;
import classi.unita;

public class SerializzazioneunitaDAO implements unitaDAO {
    private static final String FILE_PATH = "dipendenti.ser";

    @Override
    public List<unita> getAllunita() {
        List<unita> units = new ArrayList<>();

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(FILE_PATH))) {
            while (true) {
                try {
                    unita unita = (unita) ois.readObject();
                    units.add(unita);
                } catch (EOFException e) {
                    break;
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        return units;
    }

    @Override
    public void addunita(unita unita) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_PATH, true))) {
            oos.writeObject(unita);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void addRole(unita unita, ruolo r) {
    }

    @Override
    public void removeRole(unita unita,ruolo r) {
    }


    @Override
    public void deleteunita(unita unita) {
        List<unita> units = getAllunita();

        for (int i = 0; i < units.size(); i++) {
            if (units.get(i).getName().equals(unita.getName())) {
                units.remove(i);
                break;
            }
        }

        saveAll(units);
    }

    private void saveAll(List<unita> units) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_PATH))) {
            for (unita unita : units) {
                oos.writeObject(unita);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}