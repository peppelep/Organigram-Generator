package classi;

import classi.chain.UnitFinder;
import java.io.ObjectStreamException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;


public class Organigramma implements Serializable {
    private static Organigramma instance;
    private componente radice;
    private List<Observer> observers;


    /**
     * Costruttore della classe Organigramma.
     */
    private Organigramma() {
        radice = new unita("root");
        radice.setPadre(new unita(""));
        this.observers = new LinkedList<>();
    }

    /**
     * Restituisce un'istanza singola della classe Organigramma utilizzando il design pattern Singleton.
     *
     * @return un'istanza di Organigramma
     */
    //
    public synchronized static Organigramma getInstance() {
        if (instance == null) {
            synchronized (Organigramma.class) {
                if (instance == null) {
                    instance = new Organigramma();
                }
            }
        }
        return instance;
    }

    private Object readResolve() throws ObjectStreamException {
        return getInstance(); // Restituisce l'istanza esistente dell'oggetto singleton
    }

    /**
     * Aggiunge un osservatore alla lista degli osservatori dell'organigramma.
     *
     * @param observer l'osservatore da aggiungere
     */
    private void addObserver(Observer observer) {
        observers.add(observer);
    }

    /**
     * Rimuove un osservatore dalla lista degli osservatori dell'organigramma.
     *
     * @param observer l'osservatore da rimuovere
     */
    private void removeObserver(Observer observer) {
        observers.remove(observer);
    }

    /**
     * Notifica tutti gli osservatori registrati dell'organigramma.
     */
    private void notifyObservers() {
        for (Observer observer : observers) {
            observer.update();
        }
    }

    /**
     * Restituisce l'oggetto componente che rappresenta la radice dell'organigramma.
     *
     * @return l'oggetto componente radice
     */
    public componente getRadice(){
        return radice;
    }

    /**
     * Aggiunge una sotto-unità a un'unità specifica nell'organigramma.
     *
     * @param unita l'unità a cui aggiungere la sotto-unità
     * @param sottoUnita la sotto-unità da aggiungere
     */
    public void addComponente(componente unita , componente sottoUnita){
        if(radice.getName().equals(unita.getName()) && (!radice.getComposizione().contains(sottoUnita)) ){
            sottoUnita.setPadre((unita) radice);
            radice.addComponente(sottoUnita);
            addObserver((Observer) sottoUnita);
            notifyObservers();
            return;
        }
        for (componente x : radice.getComposizione()){
            addComponenteRicorsivo(x,unita,sottoUnita);
        }
    }

    private void addComponenteRicorsivo(componente radicenuova , componente unita , componente sottounita){
        if(radicenuova.getName().equals(unita.getName()) && (!radicenuova.getComposizione().contains(sottounita))){
            sottounita.setPadre((unita)radicenuova);
            radicenuova.addComponente(sottounita);
            addObserver((Observer) sottounita);
            notifyObservers();
            return;
        }
        for (componente x : radicenuova.getComposizione()){
            addComponenteRicorsivo(x,unita,sottounita);
        }
    }

    /**
     * Rimuove una sotto-unità dall'organigramma.
     *
     * @param sottoUnita la sotto-unità da rimuovere
     * @param url del database
     */
    public void removeComponente(componente sottoUnita , String url ){
        if(radice.getComposizione().contains(sottoUnita) ){
            removeObserver((Observer) sottoUnita);
            radice.removeComponente(sottoUnita , url);
            notifyObservers();
        }
        for (componente x : radice.getComposizione()){
            removeComponenteRicorsivo(x,sottoUnita , url);
        }
    }

    private void removeComponenteRicorsivo(componente radicenuova ,componente sottounita , String url){
        if(radicenuova.getComposizione().contains(sottounita)){
            removeObserver((Observer) sottounita);
            radicenuova.removeComponente(sottounita , url);
            notifyObservers();
        }
        for (componente x : radicenuova.getComposizione()){
            removeComponenteRicorsivo(x,sottounita , url);
        }
    }

    /**
     * Trova le unità organizzative corrispondenti a un ruolo specifico.
     *
     * @param r il ruolo da cercare
     * @return una stringa rappresentante le unità organizzative trovate
     */

    public String trovareUnitaOrganizzativePerRuolo(ruolo r) {
        UnitFinder u = new UnitFinder();

        List<unita> list = new ArrayList<>();
        list.addAll(listaunita(radice));
        return u.trovareUnitaOrganizzativePerRuolo(list,r);
    }

    private ArrayList<unita> listaunita(componente u){
        List<unita> l = new ArrayList<>();

        for(componente c : u.getComposizione()){
            if(c instanceof unita){
                l.add((unita)c);
                l.addAll(listaunita(c));
            }
        }
        return new ArrayList<>(l);
    }


}
