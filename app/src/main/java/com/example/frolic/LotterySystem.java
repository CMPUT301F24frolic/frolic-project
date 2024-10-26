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

    public boolean addToWaitingList(Entrant entrant) {
        if (maxWaiting == -1 || waitingList.size() < maxWaiting) {
            return waitingList.add(entrant);
        } else {
            Log.e("LotterySystem", "Waiting list is full. Entrant could not be added.");
            return false;
        }
    }

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
