package models;

public class ClientInfo {
    private String email;
    private String name;

    /**
     * Constructor
     * @param name
     */
    public ClientInfo(String name, String email) {
        this.name = name;
        this.email = email;
    }

    /**
     * return name
     * @return
     */
    public String getName() {
        return name;
    }

    /**
     * Set the name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * return email
     * @return
     */
    public String getEmail() {
        return email;
    }

    /**
     * Set the email
     */
    public void setEmail(String email) {
        this.email = email;
    }
}
