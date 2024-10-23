package com.example.frolic;
import java.util.ArrayList;
import java.util.Collections;

import android.util.Log;

public class LotterySystem {
    private Event event;
    private ArrayList<User> waitingList;
    private ArrayList<User> invitedList;
    private ArrayList<User> confirmedList;
    private int maxAttendees;
    private int maxWaiting;

    // TODO: Add Javadocs

    public LotterySystem() {
        waitingList = new ArrayList<User>();
        invitedList = new ArrayList<User>();
        confirmedList = new ArrayList<User>();
    }

    public LotterySystem(Event event, int maxAttendees, int maxConfirmed) {
        this.event = event;
        this.maxAttendees = maxAttendees;
        this.maxWaiting = maxConfirmed;
        waitingList = new ArrayList<User>();
        invitedList = new ArrayList<User>();
        confirmedList = new ArrayList<User>();
    }

    public ArrayList<User> drawLottery() {
        Collections.shuffle(waitingList);
        for (int i = 0; i < Math.min(waitingList.size(), maxAttendees); i++) {
            invitedList.add(waitingList.get(i));
        }
        waitingList.removeAll(invitedList);
        return invitedList;
    }

    // TODO: Add method that notifies users when they've been drawn for the lottery

    public boolean addToWaitingList(User user) {
        try { assert waitingList.size() + 1 <= maxWaiting; }
        catch (AssertionError e) { Log.e("LotterySystem.java", "Tried to add a user greater than the max size of the entrant list.", e); }
        return waitingList.add(user);
    }

    public boolean removeFromWaitingList(User user) {
        try { assert waitingList.contains(user); }
        catch (AssertionError e) { Log.e("LotterySystem.java", "Tried to remove a user that doesn't exist form the waiting list", e); }
        return waitingList.remove(user);
    }

    public ArrayList<User> getWaitingList() {
        return waitingList;
    }

    public void setWaitingList(ArrayList<User> waitingList) {
        this.waitingList = waitingList;
    }


    public ArrayList<User> getConfirmedList() {
        return confirmedList;
    }

    public void setConfirmedList(ArrayList<User> confirmedList) {
        this.confirmedList = confirmedList;
    }


    public int getMaxAttendees() {
        return maxAttendees;
    }

    public void setMaxAttendees(int maxAttendees) {
        this.maxAttendees = maxAttendees;
    }


}
