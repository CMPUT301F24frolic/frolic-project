package com.example.frolic;
import java.util.ArrayList;

public class Entrant {
    private Identity identity;
    private ArrayList<Event> eventsEntered;

    /**
     * This constructor takes in an identity parameter and sets a default Entrant.
     * Note that all Entrants must have an "Identity" to identify them.
     * @param identity
     * This parameter acts as a unique Identity to specify the entrant.
     */
    public Entrant(Identity identity) {
        this.identity = identity;
        eventsEntered = new ArrayList<Event>();
    }

    // Getters and Setters
    // Some of these may be redundant. Feel free to remove later if they seem that way
    // TODO: Add Javadocs

    public ArrayList<Event> getEventsEntered() {
        return eventsEntered;
    }

    public void setEventsEntered(ArrayList<Event> eventsEntered) {
        this.eventsEntered = eventsEntered;
    }


    public Identity getIdentity() {
        return identity;
    }

    public void setIdentity(Identity identity) {
        this.identity = identity;
    }
}
