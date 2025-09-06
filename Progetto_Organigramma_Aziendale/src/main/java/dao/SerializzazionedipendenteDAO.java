package dao ;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import classi.dipendente;
import classi.ruolo;

public class SerializzazionedipendenteDAO implements dipendenteDAO {
    private static final String FILE_PATH = "dipendenti.ser";

    @Override
    public List<dipendente> getAlldipendente() {
        List<dipendente> dipendenti = new ArrayList<>();

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(FILE_PATH))) {
            while (true) {
                try {
                    dipendente dipendente = (dipendente) ois.readObject();
                    dipendenti.add(dipendente);
                } catch (EOFException e) {
                    break;
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        return dipendenti;
    }



    @Override
    public void adddipendente(dipendente dipendente) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_PATH, true))) {
            oos.writeObject(dipendente);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void addRole(dipendente dipendente, ruolo r) {

    }

    @Override
    public void removeRole(dipendente dipendente, ruolo r) {

    }


    @Override
    public void deletedipendente(dipendente dipendente) {
        List<dipendente> dipendenti = getAlldipendente();

        for (int i = 0; i < dipendenti.size(); i++) {
            if (dipendenti.get(i).getName().equals(dipendente.getName())) {
                dipendenti.remove(i);
                break;
            }
        }

        saveAll(dipendenti);
    }

    private void saveAll(List<dipendente> dipendenti) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_PATH))) {
            for (dipendente dipendente : dipendenti) {
                oos.writeObject(dipendente);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}