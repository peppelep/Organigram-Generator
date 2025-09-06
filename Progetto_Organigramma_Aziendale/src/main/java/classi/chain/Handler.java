package classi.chain;

import classi.ruolo;
import classi.unita;

import java.util.List;

/**
 * Interfaccia che definisce il contratto per un gestore nella catena di responsabilità.
 */

public interface Handler {

    /**
     * Gestisce la richiesta specificata, eventualmente passandola al gestore successivo nella catena.
     *
     * @param unitaOrganizzative la lista di unità organizzative
     * @param ruolo              il ruolo da cercare
     * @param units              la lista di unità corrispondenti al ruolo
     * @param s                  la stringa che ci indichera le informazioni finali
     * @return una stringa contenente l'output della gestione della richiesta
     */
    String handleRequest(List<unita> unitaOrganizzative, ruolo ruolo, List<unita> units , String s);
}
