package classi;

import dao.JDBCdipendenteDAO;
import dao.JDBCunitaDAO;

import java.io.BufferedReader;
import java.io.Serializable;
import java.sql.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

//classe composta da dipendenti e unità
public class unita implements componente , Serializable {
    private List<componente> composizione ;
    private String nome ;
    private List<ruolo> ruoli ;
    private unita padre ;

    /**
     * Costruttore della classe unità.
     *
     * @param nome  Nome dell'unità ;
     */
    public unita(String nome){
        this.nome = nome ;
        this.composizione = new LinkedList<componente>();
        this.ruoli = new LinkedList<ruolo>();
    }

    /**
     * Restituisce il nome dell'unità.
     *
     * @return Nome dell' unità
     */
    public String getName(){
        return nome ;
    }

    /**
     * Restituisce l'unità di cui l'unità fa parte ;
     *
     * @return unità padre
     */
    @Override
    public unita getPadre() {
        return padre;
    }

    /**
     * Imposta il nome dell'unità di cui l'unità fa parte .
     *
     * @param padre unità superiore
     */
    public void setPadre(unita padre){
        this.padre = padre ;
    }

    /**
     * Aggiunge un componente alla propria composizione.
     * @param c componente da aggiungere
     */
    public void addComponente(componente c) {
        if(c instanceof dipendente){
            boolean condizione = false ;
            dipendente d = (dipendente) c ;
            for(ruolo r : d.ruoli()){
                if(this.ruoli.contains(r)) {
                    condizione = true;
                }
            }
            if(condizione){
                composizione.add(c);
            }
            else {
                return;
            }
        }
        else{
            boolean ok = true ;
            for(ruolo r : c.ruoli()){
                if (this.ruoli().contains(r)){
                    ok = false;
                }
            }
            if(!ok){
                System.out.println("non posso aggiungere unita contenente un ruolo del padre");
                return;
            }
            composizione.add(c);
        }
    }

    /**
     * Rimuove un componente dalla propria composizione.
     * @param c componente da rimuovere
     */
    public void removeComponente(componente c , String url) {
        if(c.getName().equals("root")){
            return;
        }
        composizione.remove(c);

        if(c instanceof unita){
            JDBCunitaDAO j = new JDBCunitaDAO(url) ;
            j.deleteunita((unita) c);
        }
        else{
            dipendente d = (dipendente) c;
            d.removeComponente(d,url);
        }

    }

    /**
     * Stampa il nome dell'unità e i ruoli
     */
    public void visioneMembri() {
        System.out.println("Nome Unita : "+this.nome + "| Ruoli "+this.ruoli);
        for(int i = 0 ; i < composizione.size() ; i++){
            composizione.get(i).visioneMembri();
        }
    }

    /**
     * Metodo per ottenere l'elenco dei ruoli dell'unità.
     *
     * @return Elenco dei ruoli dell'unità
     */
    public List<ruolo> ruoli() {
        return ruoli;
    }

    /**
     * Assegna un ruolo all'unità.
     *
     * @param ruolo da assegnare
     */
    public void addRuolo(ruolo ruolo) {
        if(verificaCorrettezza(this,ruolo)) {
            this.ruoli.add(ruolo);
        }
    }

    @Override
    public void removeRuolo(ruolo ruolo , String url) {
        this.visioneMembri();

        if(ruoli.contains(ruolo)) {
            ruoli.remove(ruolo);
            JDBCunitaDAO j = new JDBCunitaDAO(url);
            j.removeRole(this,ruolo);
        }
        List<componente> y = new LinkedList<>(this.composizione);
        for(componente c : y){
            if(c instanceof  dipendente){
                dipendente d = (dipendente) c ;
                System.out.println(d.getName());
                d.removeRuolo(ruolo,url);
            }
        }

        if(this.ruoli.size()==0){
            this.getPadre().removeComponente(this , url);
        }
    }

    /**
     * Metodo per ottenere la composizione dell'unità
     * @return composizione
     */
    public List<componente> getComposizione(){
        return this.composizione ;
    }

    @Override
    public void update  () {
        StringBuffer s = new StringBuffer();
        for(componente c : this.getComposizione()){
            s.append(c.getName());
            s.append("  ");
        }
        System.out.println(this.getName()+" Composizione: "+s.toString());
    }

    private boolean verificaCorrettezza(unita u , ruolo r){
        if(u.getPadre() == null){
            return true ;
        }
        if(u.getPadre().getName().equals("root")){
            return true;
        }
        if(u.getPadre().ruoli.contains(r)){
            return false;
        }
        return verificaCorrettezza(u.getPadre(),r);
    }

}
