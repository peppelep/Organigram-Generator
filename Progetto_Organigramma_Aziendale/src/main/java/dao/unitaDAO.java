package dao;

import classi.ruolo;
import classi.unita;

import java.util.List;

public interface unitaDAO {
    List<unita> getAllunita();
    void addunita(unita unita);
    void addRole(unita unita, ruolo r);
    void removeRole(unita unita, ruolo r);
    void deleteunita(unita unita);
}
