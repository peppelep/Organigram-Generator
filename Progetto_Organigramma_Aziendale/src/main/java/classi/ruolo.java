package classi;

import java.io.Serializable;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe che rappresenta un ruolo.
 */
public class ruolo implements Serializable {
    private String name;

    /**
     * Costruttore della classe ruolo.
     *
     * @param name il nome del ruolo
     */
    public ruolo(String name) {
        this.name = name;
    }

    /**
     * Restituisce il nome del ruolo.
     *
     * @return il nome del ruolo
     */
    public String getName() {
        return name ;
    }

    /**
     * Restituisce una rappresentazione in stringa del ruolo.
     *
     * @return la rappresentazione in stringa del ruolo
     */
    public String toString(){
        return this.name ;
    }

    /**
     * Verifica se il ruolo è uguale all'oggetto specificato.
     *
     * @param o l'oggetto da confrontare
     * @return true se il ruolo è uguale all'oggetto specificato, false altrimenti
     */
    public boolean equals(Object o){
        if(o == null){
            return false ;
        }
        if(o == this){
            return true ;
        }
        if(!(o instanceof ruolo)){
            return false;
        }

        ruolo r = (ruolo) o ;
        return this.getName().equals(r.getName());
    }

}
