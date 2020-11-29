package de.hs.lucidityLog.testing;

import androidx.test.espresso.idling.CountingIdlingResource;
/**
 * Klasse MyIdlingRessource stellt CountingIdlingResource zur Verfügung.
 * Zur Synchronisation zwischen Tests und Threads
 * @author Hauptverantwortlich: Patrick Behrens, Mitwirkend: Pascal Piora
 */
public class MyIdlingRessource {
    private static CountingIdlingResource idlingResource;

    /**
     * Erzeugung und Rückgabe einer neuen CountingIdlingResource
     * @return CountingIdlingResource
     */
    public static CountingIdlingResource getIdlingResource() {
        if(idlingResource == null) {
            idlingResource = new CountingIdlingResource("idlingRessource");
        }
        return idlingResource;
    }
}
