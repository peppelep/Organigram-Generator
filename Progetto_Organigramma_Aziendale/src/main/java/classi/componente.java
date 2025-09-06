package classi;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.io.Serializable;

/**
 * Interfaccia che rappresenta un componente dell'organigramma.
 */


public interface componente extends Observer{

    /**
     * Aggiunge un componente alla propria composizione.
     *
     * @param c componente da aggiungere
     */
    void addComponente(componente c);

    /**
     * Rimuove un componente dalla propria composizione.
     *
     * @param c componente da rimuovere
     * @param url del database
     */
    void removeComponente(componente c, String url);

    /**
     * Metodo per ottenere la composizione del componente
     *
     * @return composizione
     */
    List<componente> getComposizione();

    /**
     * Stampa il nome e la composizione del componente
     */
    void visioneMembri();

    /**
     * Metodo per ottenere l'elenco dei ruoli del componente.
     *
     * @return Elenco dei ruoli del componente
     */
    List<ruolo> ruoli();


    /**
     * Aggiunge un ruolo ad un componente.
     *
     * @param ruolo da aggiungere
     */
    void addRuolo(ruolo ruolo);

    /**
     * Rimuove un ruolo ad un componente.
     *
     * @param ruolo da rimuovere
     * @param url del database
     */
    void removeRuolo(ruolo ruolo , String url);

    /**
     * Restituisce il nome del componente.
     *
     * @return Nome del componente
     */
    String getName();


    /**
     * Restituisce il componente di cui il componente fa parte ;
     *
     * @return componente padre
     */
    unita getPadre();

    /**
     * Imposta il nome del componente di cui il componente fa parte .
     *
     * @param padre padre del componente
     */
    void setPadre(unita padre);

    public void update();
}
