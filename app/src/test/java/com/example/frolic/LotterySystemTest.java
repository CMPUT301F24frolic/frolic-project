package com.example.frolic;
import org.junit.Before;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

public class LotterySystemTest {
    private LotterySystem lotterySystem;

    @BeforeEach
    public void setUp() {
        lotterySystem = new LotterySystem("lottery1", "event1", 3, 5);
    }

    @Test
    public void testAddToWaitingList_Success() {
        assertTrue(lotterySystem.addToWaitingList("entrant1"));
        assertTrue(lotterySystem.getWaitingListIds().contains("entrant1"));
    }

    @Test
    public void testRemoveFromWaitingList_Success() {
        lotterySystem.addToWaitingList("entrant1");
        assertTrue(lotterySystem.removeFromWaitingList("entrant1"));
        assertFalse(lotterySystem.getWaitingListIds().contains("entrant1"));
    }


    @Test
    public void testDrawLottery_InvitesUpToMaxAttendees() {
        lotterySystem.addToWaitingList("entrant1");
        lotterySystem.addToWaitingList("entrant2");
        lotterySystem.addToWaitingList("entrant3");
        lotterySystem.addToWaitingList("entrant4");

        ArrayList<String> invitedList = lotterySystem.drawLottery();

        assertEquals(3, invitedList.size());
        assertTrue(invitedList.contains("entrant1") || invitedList.contains("entrant2") ||
                invitedList.contains("entrant3") || invitedList.contains("entrant4"));
        assertEquals(1, lotterySystem.getWaitingListIds().size());
    }

    @Test
    public void testDrawLottery_WithLessThanMaxAttendees() {
        lotterySystem.addToWaitingList("entrant1");
        lotterySystem.addToWaitingList("entrant2");

        ArrayList<String> invitedList = lotterySystem.drawLottery();

        assertEquals(2, invitedList.size());
        assertEquals(0, lotterySystem.getWaitingListIds().size());
    }

    @Test
    public void testToMap_ReturnsCorrectMap() {
        lotterySystem.addToWaitingList("entrant1");
        lotterySystem.addToWaitingList("entrant2");

        Map<String, Object> lotteryMap = lotterySystem.toMap();

        assertEquals("lottery1", lotteryMap.get("lotterySystemId"));
        assertEquals("event1", lotteryMap.get("eventId"));
        assertEquals(3, lotteryMap.get("maxAttendees"));
        assertEquals(5, lotteryMap.get("maxWaiting"));
        assertEquals(lotterySystem.getWaitingListIds(), lotteryMap.get("waitingListIds"));
        assertEquals(lotterySystem.getInvitedListIds(), lotteryMap.get("invitedListIds"));
        assertEquals(lotterySystem.getConfirmedListIds(), lotteryMap.get("confirmedListIds"));
        assertEquals(lotterySystem.getCanceledListIds(), lotteryMap.get("canceledListIds"));
    }

    @Test
    public void testSetMaxAttendees() {
        lotterySystem.setMaxAttendees(4);
        assertEquals(4, lotterySystem.getMaxAttendees());
    }

    @Test
    public void testSetMaxWaiting() {
        lotterySystem.setMaxWaiting(10);
        assertEquals(10, lotterySystem.getMaxWaiting());
    }
}
