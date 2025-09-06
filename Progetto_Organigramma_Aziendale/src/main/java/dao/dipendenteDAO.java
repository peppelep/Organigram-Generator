package dao;

import classi.dipendente;
import classi.ruolo;
import classi.unita;

import java.util.List;

public interface dipendenteDAO {
    List<dipendente> getAlldipendente();
    void adddipendente(dipendente dipendente);
    void addRole(dipendente dipendente, ruolo r);
    void removeRole(dipendente dipendente, ruolo r);
    void deletedipendente(dipendente dipendente);

}
