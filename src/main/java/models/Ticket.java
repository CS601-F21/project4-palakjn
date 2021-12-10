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
    private String hostId;
    private Event event;
    private String sharedMsg;

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
     * Get the id of the host
     */
    public String getHostId() {
        return hostId;
    }

    /**
     * Set the id of the host of the ticket
     */
    public void setHostId(String hostId) {
        this.hostId = hostId;
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

    /**
     * Get the shared message
     */
    public String getSharedMsg() {
        return sharedMsg;
    }

    /**
     * Set the shared message
     */
    public void setSharedMsg(String sharedMsg) {
        this.sharedMsg = sharedMsg;
    }
}
