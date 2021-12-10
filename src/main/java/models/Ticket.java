package models;

/**
 * Holds the information of ticket
 *
 * @author Palak Jain
 */
public class Ticket {

    private String id;
    private String eventId;
    private String userId;
    private int numOfTickets;
    private Event event;

    /**
     * Get the ticket Id
     */
    public String getId() {
        return id;
    }

    /**
     * Set the ticket Id
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Get the event Id
     */
    public String getEventId() {
        return eventId;
    }

    /**
     * Set the event Id
     */
    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    /**
     * Get the user Id
     */
    public String getUserId() {
        return userId;
    }

    /**
     * Set the user Id
     */
    public void setUserId(String userId) {
        this.userId = userId;
    }

    /**
     * Get the number of tickets
     */
    public int getNumOfTickets() {
        return numOfTickets;
    }

    /**
     * Set the number of tickets
     */
    public void setNumOfTickets(int numOfTickets) {
        this.numOfTickets = numOfTickets;
    }

    /**
     * Get the event object
     */
    public Event getEvent() {
        return event;
    }

    /**
     * Set the event object
     */
    public void setEvent(Event event) {
        this.event = event;
    }
}
