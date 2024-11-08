package com.example.frolic;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import android.util.Log;

public class LotterySystem {
    private String lotterySystemId;
    private String eventId;
    private ArrayList<String> waitingListIds = new ArrayList<>();
    private ArrayList<String> invitedListIds = new ArrayList<>();
    private ArrayList<String> confirmedListIds = new ArrayList<>();
    private ArrayList<String> canceledListIds = new ArrayList<>();
    private int maxAttendees;
    private int maxWaiting;

    // TODO: Add Javadocs

    /**
     * Constructs a Lottery System as required by Firebase (No-argument constructor needed)
     */
    public LotterySystem(){

    }

    /**
     * Constructs a LotterySystem with the specified event and capacity limits.
     *
     * @param lotterySystemId the unique identifier for this lottery system
     * @param eventId         the unique identifier for the associated event
     * @param maxAttendees    the maximum number of attendees for the event
     * @param maxWaiting      the maximum number of entrants allowed on the waitlist
     */
    public LotterySystem(String lotterySystemId, String eventId, int maxAttendees, int maxWaiting) {
        this.lotterySystemId = lotterySystemId;
        this.eventId = eventId;
        this.maxAttendees = maxAttendees;
        this.maxWaiting = maxWaiting;
    }

    /**
     * Randomly selects entrants from the waiting list and moves them to the invited list.
     *
     * @return a list of entrant IDs that have been invited to the event
     */
    public ArrayList<String> drawLottery() {
        Collections.shuffle(waitingListIds);
        int drawLimit = Math.min(maxAttendees - (invitedListIds.size() + confirmedListIds.size()), Math.min(waitingListIds.size(), maxAttendees));

        for (int i = 0; i < drawLimit; i++) {
            invitedListIds.add(waitingListIds.get(i));
        }

        // Remove invited entrants from waiting list
        waitingListIds.removeAll(invitedListIds);
        return invitedListIds;
    }

    // TODO: Add method that notifies users when they've been drawn for the lottery

    /**
     * Adds an entrant to the event's waiting list if the maximum capacity has not been reached.
     *
     * @param entrantId the id of the entrant to be added to the waiting list
     * @return {@code true} if the entrant was successfully added to the waiting list;
     *         {@code false} if the waiting list has reached its maximum capacity
     */
    public boolean addToWaitingList(String entrantId) {
        if (maxWaiting == -1 || waitingListIds.size() < maxWaiting) {
            return waitingListIds.add(entrantId);
        } else {
            Log.e("LotterySystem", "Waiting list is full. Entrant could not be added.");
            return false;
        }
    }

    /**
     * Removes an entrant from the event's waiting list if they exist on the list.
     *
     * @param entrantId the id of the entrant to be removed from the waiting list
     * @return {@code true} if the entrant was successfully removed;
     *         {@code false} if the entrant was not found in the waiting list
     * @throws AssertionError if the entrant is not in the waiting list,
     *                        logs an error indicating the removal attempt for a non-existent entrant
     */
    public boolean removeFromWaitingList(String entrantId) {
        try { assert waitingListIds.contains(entrantId); }
        catch (AssertionError e) { Log.e("LotterySystem.java", "Tried to remove a user that doesn't exist form the waiting list", e); }
        return waitingListIds.remove(entrantId);
    }

    /**
     * Removes an entrant from the event's invited list if they exist on the list.
     *
     * @param entrantId the id of the entrant to be removed from the invited list
     * @return {@code true} if the entrant was successfully removed;
     *         {@code false} if the entrant was not found in the invited list
     * @throws AssertionError if the entrant is not in the invited list,
     *                        logs an error indicating the removal attempt for a non-existent entrant
     */
    public boolean removeFromInvitedList(String entrantId) {
        try { assert invitedListIds.contains(entrantId); }
        catch (AssertionError e) { Log.e("LotterySystem.java", "Tried to remove a user that doesn't exist form the invited list", e); }
        return invitedListIds.remove(entrantId);
    }

    /**
     * Adds an entrant to the event's canceled list.
     *
     * @param entrantId the id of the entrant to be added to the waiting list
     */
    public boolean addToCanceledList(String entrantId) {
        return canceledListIds.add(entrantId);
    }

    /**
     * Converts this LotterySystem object to a map representation for Firebase storage.
     *
     * @return a map containing the lottery system's attributes and their values
     */
    public Map<String, Object> toMap() {
        Map<String, Object> lotteryMap = new HashMap<>();
        lotteryMap.put("lotterySystemId", lotterySystemId);
        lotteryMap.put("eventId", eventId);
        lotteryMap.put("waitingListIds", waitingListIds);
        lotteryMap.put("invitedListIds", invitedListIds);
        lotteryMap.put("confirmedListIds", confirmedListIds);
        lotteryMap.put("canceledListIds", canceledListIds);
        lotteryMap.put("maxAttendees", maxAttendees);
        lotteryMap.put("maxWaiting", maxWaiting);
        return lotteryMap;
    }

    public ArrayList<String> getWaitingListIds() {
        return waitingListIds;
    }

    public ArrayList<String> getInvitedListIds() {
        return invitedListIds;
    }

    public ArrayList<String> getConfirmedListIds() {
        return confirmedListIds;
    }

    public ArrayList<String> getCanceledListIds() {
        return canceledListIds;
    }

    public String getEventId() {
        return eventId;
    }

    public String getLotterySystemId() {
        return lotterySystemId;
    }

    public int getMaxAttendees() {
        return maxAttendees;
    }

    public void setMaxAttendees(int maxAttendees) {
        this.maxAttendees = maxAttendees;
    }

    public int getMaxWaiting() {
        return maxWaiting;
    }

    public void setMaxWaiting(int maxWaiting) {
        this.maxWaiting = maxWaiting;
    }
}
