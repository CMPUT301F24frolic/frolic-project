package com.example.frolic;
import java.util.ArrayList;
import java.util.Collections;

import android.util.Log;

import com.google.firebase.firestore.auth.User;

public class LotterySystem {
    private Event event;
    private ArrayList<Entrant> waitingList;
    private ArrayList<Entrant> invitedList;
    private ArrayList<Entrant> confirmedList;
    private int maxAttendees;
    private int maxWaiting;

    // TODO: Add Javadocs

    public LotterySystem() {
        waitingList = new ArrayList<Entrant>();
        invitedList = new ArrayList<Entrant>();
        confirmedList = new ArrayList<Entrant>();
    }

    public LotterySystem(Event event) {
        this.event = event;
        this.maxAttendees = event.getMaxConfirmed();
        this.maxWaiting = event.getWaitlistLimit();
        waitingList = new ArrayList<Entrant>();
        invitedList = new ArrayList<Entrant>();
        confirmedList = new ArrayList<Entrant>();
    }

    public ArrayList<Entrant> drawLottery() {
        Collections.shuffle(waitingList);
        for (int i = 0; i < Math.min(waitingList.size(), maxAttendees); i++) {
            invitedList.add(waitingList.get(i));
        }
        waitingList.removeAll(invitedList);
        return invitedList;
    }

    // TODO: Add method that notifies users when they've been drawn for the lottery

    /**
     * Adds an entrant to the event's waiting list if the maximum capacity has not been reached.
     *
     * @param entrant the entrant to be added to the waiting list
     * @return {@code true} if the entrant was successfully added to the waiting list;
     *         {@code false} if the waiting list has reached its maximum capacity
     */
    public boolean addToWaitingList(Entrant entrant) {
        if (maxWaiting == -1 || waitingList.size() < maxWaiting) {
            return waitingList.add(entrant);
        } else {
            Log.e("LotterySystem", "Waiting list is full. Entrant could not be added.");
            return false;
        }
    }

    /**
     * Removes an entrant from the event's waiting list if they exist on the list.
     *
     * @param entrant the entrant to be removed from the waiting list
     * @return {@code true} if the entrant was successfully removed;
     *         {@code false} if the entrant was not found in the waiting list
     * @throws AssertionError if the entrant is not in the waiting list,
     *                        logs an error indicating the removal attempt for a non-existent entrant
     */
    public boolean removeFromWaitingList(Entrant entrant) {
        try { assert waitingList.contains(entrant); }
        catch (AssertionError e) { Log.e("LotterySystem.java", "Tried to remove a user that doesn't exist form the waiting list", e); }
        return waitingList.remove(entrant);
    }

    public ArrayList<Entrant> getWaitingList() {
        return waitingList;
    }

    public void setWaitingList(ArrayList<Entrant> waitingList) {
        this.waitingList = waitingList;
    }


    public ArrayList<Entrant> getConfirmedList() {
        return confirmedList;
    }

    public void setConfirmedList(ArrayList<Entrant> confirmedList) {
        this.confirmedList = confirmedList;
    }


    public int getMaxAttendees() {
        return maxAttendees;
    }

    public void setMaxAttendees(int maxAttendees) {
        this.maxAttendees = maxAttendees;
    }


}
