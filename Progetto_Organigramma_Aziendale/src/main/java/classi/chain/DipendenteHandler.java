package classi.chain;

import classi.componente;
import classi.dipendente;
import classi.ruolo;
import classi.unita;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementazione del gestore Dipendente nella catena di responsabilità.
 */
public class DipendenteHandler implements Handler {
    private Handler nextHandler;

    /**
     * Imposta il gestore successivo nella catena.
     *
     * @param handler il gestore successivo
     */
    public void setNextHandler(Handler handler) {
        this.nextHandler = handler;
    }

    @Override
    public String handleRequest(List<unita> unitaOrganizzative, ruolo ruolo, List<unita> units,String s1) {
        StringBuilder s = new StringBuilder();
        for(unita u : units){
            for(componente c : u.getComposizione()){
                if(c instanceof dipendente){
                    dipendente d = (dipendente) c;

                    /*
                    if(d.ruoli().contains(ruolo)){
                        s.append(d.getName()+" svolge questo ruolo in "+u.getName());
                        s.append("\n");
                    }

                     */
                    for(ruolo r : d.ruoli()){
                        if(r.getName().equalsIgnoreCase(ruolo.getName())){
                            s.append(d.getName()+" svolge questo ruolo in "+u.getName());
                            s.append("\n");
                            break ;
                        }
                    }
                }
            }
        }

        return passToNextHandler(unitaOrganizzative, ruolo,units , s1 , s.toString());
    }

    /**
     * Passa la richiesta al gestore successivo nella catena.
     *
     * @param unitaOrganizzative la lista di unità organizzative
     * @param ruolo              il ruolo da cercare
     * @param l                  la lista di unità corrispondenti al ruolo
     * @param s                  la stringa accumulata fino a questo punto
     * @param s1                 la stringa accumulata dalla catena precedente
     * @return una stringa contenente l'output della gestione della richiesta
     */
    protected String passToNextHandler(List<unita> unitaOrganizzative, ruolo ruolo, List<unita> l ,String s1 , String s) {
        String ret = "";
        for(unita u : l){
            ret ="Presente in "+s1;
        }
        if(s.equals("")){
            return ret + "ruolo non esercitato da nessuno";
        }

        return ret+s;
    }
}


