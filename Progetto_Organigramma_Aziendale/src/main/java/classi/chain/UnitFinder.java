package classi.chain;

import classi.ruolo;
import classi.unita;

import java.util.ArrayList;
import java.util.List;

/**
 * Classe che gestisce la ricerca di unità organizzative per ruolo utilizzando una catena di gestori.
 */
public class UnitFinder {
    private Handler handler;

    /**
     * Costruttore di UnitFinder che costruisce la catena di gestori.
     */
    public UnitFinder() {
        buildChain();
    }

    /**
     * Costruisce la catena di gestori.
     */
    private void buildChain() {
        DipendenteHandler dipendenteHandler = new DipendenteHandler();
        UnitaHandler unitaHandler = new UnitaHandler();

        // Configurazione della catena
        unitaHandler.setNextHandler(dipendenteHandler);
        handler = unitaHandler;
    }

    /**
     * Trova le unità organizzative corrispondenti al ruolo specificato.
     *
     * @param unitaOrganizzative la lista di unità organizzative
     * @param ruolo              il ruolo da cercare
     * @return una stringa contenente l'output della ricerca
     */
    public String trovareUnitaOrganizzativePerRuolo(List<unita> unitaOrganizzative, ruolo ruolo) {
        List<unita> list = new ArrayList<>() ;
        return handler.handleRequest(unitaOrganizzative, ruolo, list,"");
    }
}

