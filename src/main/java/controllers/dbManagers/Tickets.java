package controllers.dbManagers;

import controllers.DataSource;
import models.Ticket;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * This class holds all the queries being made to the Tickets table.
 *
 * @author Palak Jain
 */
public class Tickets {

    /**
     * Insert ticket details to the table
     * @param ticket Ticket object with the values
     * @return true if successful else false
     */
    public static boolean insert(Ticket ticket) {
        boolean isInserted = false;

        try(Connection con = DataSource.getConnection()) {
            String query = "INSERT INTO tickets VALUES (?,?,?,?,?);";
            PreparedStatement statement = con.prepareStatement(query);
            statement.setString(1, ticket.getId());
            statement.setString(2, ticket.getEventId());
            statement.setString(3, ticket.getUserId());
            statement.setInt(4, ticket.getNumOfTickets());
            statement.setString(5, ticket.getHostId());
            statement.executeUpdate();

            isInserted = true;
        } catch (SQLException sqlException) {
            System.err.printf("Error while inserting ticket %s info to the table. %s\n", ticket.getId(), sqlException.getMessage());
        }

        return isInserted;
    }

    /**
     * Get the tickets for particular user
     * @param userId User Unique Identifier
     * @return null if error occurs else list of tickets
     */
    public static List<Ticket> getTickets(String userId) {
        List<Ticket> tickets = new ArrayList<>();

        try(Connection con = DataSource.getConnection()) {
            String query = "SELECT * FROM tickets JOIN events ON tickets.eventId = events.id WHERE tickets.userId = ? AND events.date >= CURDATE();";

            PreparedStatement statement = con.prepareStatement(query);
            statement.setString(1, userId);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                Ticket ticket = new Ticket();
                ticket.setId(resultSet.getString("id"));
                ticket.setEventId(resultSet.getString("eventId"));
                ticket.setUserId(resultSet.getString("userId"));
                ticket.setNumOfTickets(resultSet.getInt("numOfTickets"));
                ticket.setHostId(resultSet.getString("hostId"));

                tickets.add(ticket);
            }
        } catch (SQLException sqlException) {
            System.err.printf("Error while getting tickets bought by user %s. %s\n", userId, sqlException.getMessage());
            tickets = null;
        }

        return tickets;
    }

    /**
     * Get the ticket by Id
     * @param ticketId Ticket Unique Identifier
     * @return ticket object if found else null
     */
    public static Ticket getTicket(String ticketId) {
        Ticket ticket = null;

        try(Connection con = DataSource.getConnection()) {
            String query = "SELECT * FROM tickets WHERE id = ?;";

            PreparedStatement statement = con.prepareStatement(query);
            statement.setString(1, ticketId);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                ticket = new Ticket();
                ticket.setId(resultSet.getString("id"));
                ticket.setEventId(resultSet.getString("eventId"));
                ticket.setUserId(resultSet.getString("userId"));
                ticket.setNumOfTickets(resultSet.getInt("numOfTickets"));
                ticket.setHostId(resultSet.getString("hostId"));
            }
        } catch (SQLException sqlException) {
            System.err.printf("Error while getting ticket %s information. %s\n", ticketId, sqlException.getMessage());
            ticket = null;
        }

        return ticket;
    }

    /**
     * Updating the existing ticket with the provided number of tickets.
     * @param ticketId Ticket Unique Identifier
     * @param numberOfTickets Number of tickets
     * @return true if successful else false
     */
    public static boolean updateTicket(String ticketId, int numberOfTickets) {
        boolean isupdated = false;

        try(Connection cone = DataSource.getConnection()) {
            String query = "UPDATE tickets SET numOfTickets = ? WHERE id = ?";

            PreparedStatement statement = cone.prepareStatement(query);
            statement.setInt(1, numberOfTickets);
            statement.setString(2, ticketId);
            statement.executeUpdate();

            isupdated = true;
        } catch (SQLException sqlException) {
            System.err.printf("Error while updating the ticket %s information with new number of tickets %s. Error %s.\n", ticketId, numberOfTickets, sqlException);
            isupdated = false;
        }

        return isupdated;
    }

    /**
     * Delete the ticket by Id
     * @param ticketId Ticket Unique identifier
     * @return truse if successful else false
     */
    public static boolean deleteTicket(String ticketId) {
        boolean isDeleted = false;

        try(Connection con = DataSource.getConnection()) {
            String query = "DELETE FROM tickets WHERE id = ?";
            PreparedStatement statement = con.prepareStatement(query);
            statement.setString(1, ticketId);
            statement.executeUpdate();

            isDeleted = true;
        } catch (SQLException sqlException) {
            System.err.printf("Error while deleting an ticket with id as %s. %s.\n", ticketId, sqlException.getMessage());
        }

        return isDeleted;
    }
}
