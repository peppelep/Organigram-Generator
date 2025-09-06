package classi.chain;

import classi.ruolo;
import classi.unita;

import java.util.List;

/**
 * Implementazione del gestore Unita nella catena di responsabilità.
 */
public class UnitaHandler implements Handler{
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
    public String handleRequest(List<unita> unitaOrganizzative, ruolo ruolo, List<unita> units , String s) {

        for (unita unita : unitaOrganizzative) {

            /*
            if (unita.ruoli().contains(ruolo)) {
                //System.out.println(ruolo.toString());
                units.add(unita);
            }
             */

            for(ruolo r : unita.ruoli()){
                if(r.getName().equalsIgnoreCase(ruolo.getName())){
                    units.add(unita);
                    break ;
                }
            }
        }
        return passToNextHandler(unitaOrganizzative, ruolo, units);
    }

    /**
     * Passa la richiesta al gestore successivo nella catena.
     *
     * @param unitaOrganizzative la lista di unità organizzative
     * @param ruolo              il ruolo da cercare
     * @param units              la lista di unità corrispondenti al ruolo
     * @return una stringa contenente l'output della gestione della richiesta
     */
    protected String passToNextHandler(List<unita> unitaOrganizzative, ruolo ruolo, List<unita> units) {
        if(units.size() == 0 ){
            return "ruolo non presente";
        }

        StringBuilder s = new StringBuilder();
        s.append(": ");
        for(unita u : units){
            s.append(u.getName());
            s.append("  ");
        }
        s.append("\n");

        if (nextHandler != null) {
            return nextHandler.handleRequest(unitaOrganizzative, ruolo, units,s.toString());
        }

        return s.toString();
    }
}
