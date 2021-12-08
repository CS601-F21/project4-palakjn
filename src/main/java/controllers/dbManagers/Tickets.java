package controllers.dbManagers;

import controllers.DataSource;
import models.Event;
import models.Ticket;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Tickets {

    public static boolean insert(Ticket ticket) {
        boolean isInserted = false;

        try(Connection con = DataSource.getConnection()) {
            String query = "INSERT INTO tickets VALUES (?,?,?,?);";
            PreparedStatement statement = con.prepareStatement(query);
            statement.setString(1, ticket.getId());
            statement.setString(2, ticket.getEventId());
            statement.setString(3, ticket.getUserId());
            statement.setInt(4, ticket.getNumOfTickets());
            statement.executeUpdate();

            isInserted = true;
        } catch (SQLException sqlException) {
            System.err.printf("Error while inserting ticket %s info to the table. %s\n", ticket.getId(), sqlException.getMessage());
        }

        return isInserted;
    }

    public static List<Ticket> getTickets(String userId, boolean upcoming) {
        List<Ticket> tickets = new ArrayList<>();

        try(Connection con = DataSource.getConnection()) {
            String query = "SELECT * FROM tickets JOIN events ON tickets.eventId = events.id WHERE tickets.userId = ? AND events.date ";

            if(upcoming) {
                query += ">= CURDATE();";
            } else {
                query += "< CURDATE();";
            }

            PreparedStatement statement = con.prepareStatement(query);
            statement.setString(1, userId);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                Ticket ticket = new Ticket();
                ticket.setId(resultSet.getString("id"));
                ticket.setEventId(resultSet.getString("eventId"));
                ticket.setUserId(resultSet.getString("userId"));
                ticket.setNumOfTickets(resultSet.getInt("numOfTickets"));

                tickets.add(ticket);
            }
        } catch (SQLException sqlException) {
            System.err.printf("Error while getting tickets bought by user %s. %s\n", userId, sqlException.getMessage());
            tickets = null;
        }

        return tickets;
    }
}
