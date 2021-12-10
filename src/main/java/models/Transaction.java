package models;

/**
 * Holds the information of Transaction
 */
public class Transaction extends Ticket {

    private String status;

    /**
     * Get the status of transaction
     */
    public String getStatus() {
        return status;
    }

    /**
     * Set the transaction status
     */
    public void setStatus(String status) {
        this.status = status;
    }
}
