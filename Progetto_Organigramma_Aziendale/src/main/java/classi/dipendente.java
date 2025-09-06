package classi;

import dao.JDBCdipendenteDAO;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

//classe non composta
public class dipendente implements componente , Serializable {
    private String nome ;
    private List<ruolo> ruoli ;
    private unita padre ;


    /**
     * Costruttore della classe Dipendente.
     *
     * @param nome  Nome del dipendente
     */
    public dipendente(String nome){
        this.nome = nome ;
        this.ruoli = new LinkedList<ruolo>();
    }

    /**
     * Restituisce il nome del dipendente.
     *
     * @return Nome del dipendente
     */
    public String getName(){
        return nome ;
    }

    /**
     * Restituisce l'unita di cui il dipendente fa parte ;
     *
     * @return unita padre
     */
    @Override
    public unita getPadre() {
        return padre;
    }

    /**
     * Imposta il nome dell'unita di cui il dipendente fa parte .
     *
     * @param padre unita del dipendente
     */
    public void setPadre(unita padre){
        this.padre = padre ;
    }

    /**
     * Aggiunge un componente alla propria composizione.
     * Essendo una classe foglia , questo metodo non puo modificare la composizione
     *
     *
     * @param c componente da aggiungere
     */
    public void addComponente(componente c) {
        System.out.println("elemento foglia , non posso aggiungere");
    }

    /**
     * Rimuove un componente dalla propria composizione.
     * Essendo una classe foglia , questo metodo non puo modificare la composizione
     *
     * @param c componente da rimuovere
     */
    public void removeComponente(componente c , String url) {
        JDBCdipendenteDAO j = new JDBCdipendenteDAO(url);
        j.deletedipendente((dipendente) c);
        System.out.println(this.nome+" Licenziato");
    }

    /**
     * Stampa il nome del dipendente e i ruoli
     */
    public void visioneMembri() {
        System.out.println("Nome Dipendente "+this.nome+"| Ruoli "+this.ruoli);
    }


    /**
     * Metodo per ottenere l'elenco dei ruoli del dipendente.
     *
     * @return Elenco dei ruoli del dipendente
     */
    public List<ruolo> ruoli() {
        return ruoli;
    }

    /**
     * Assegna un ruolo al dipendente.
     *
     * @param ruolo da assegnare
     */
    public void addRuolo(ruolo ruolo) {
        if(this.ruoli.contains(ruolo)){
            return;
        }
        this.ruoli.add(ruolo);
    }

    /**
     * Rimuovi un ruolo dal dipendente.
     *
     * @param ruolo da rimuovere
     * @param url path database
     */
    @Override
    public void removeRuolo(ruolo ruolo , String url) {
        if(ruoli.contains(ruolo)){
            ruoli.remove(ruolo);
            JDBCdipendenteDAO j = new JDBCdipendenteDAO(url);
            j.removeRole(this,ruolo);


            if(ruoli.size() == 0 ){
                this.getPadre().removeComponente(this , url);
            }

        }
        else {
            System.out.println("ruolo non presente");
        }
    }

    /**
     * Metodo per ottenere la composizione del dipendente
     * Ritorna una lista vuota , dato che un dipendente non ha componenti interni
     *
     * @return composizione
     */
    public List<componente> getComposizione(){
        List<componente> l = new ArrayList<componente>();
        return l ;
    }


    @Override
    public void update() {
        System.out.println("Nome Dipendente "+this.nome+"| Ruoli "+this.ruoli);
    }
}
