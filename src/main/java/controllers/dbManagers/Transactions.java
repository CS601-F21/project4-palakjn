package controllers.dbManagers;

import controllers.DataSource;
import models.Transaction;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * This class holds all the queries being made to the Transaction table.
 *
 * @author Palak Jain
 */
public class Transactions {

    /**
     * Insert transaction details to the table
     * @param transaction Transaction object with the values
     * @return true if successful else false
     */
    public static boolean insert(Transaction transaction) {
        boolean isInserted = false;

        try(Connection con = DataSource.getConnection()) {
            String query = "INSERT INTO transactions VALUES (?,?,?,?,?);";
            PreparedStatement statement = con.prepareStatement(query);
            statement.setString(1, transaction.getId());
            statement.setString(2, transaction.getEventId());
            statement.setString(3, transaction.getUserId());
            statement.setInt(4, transaction.getNumOfTickets());
            statement.setString(5, transaction.getStatus());
            statement.executeUpdate();

            isInserted = true;
        } catch (SQLException sqlException) {
            System.err.printf("Error while inserting transaction %s info to the table. %s\n", transaction.getId(), sqlException.getMessage());
        }

        return isInserted;
    }

    /**
     * Get all the transactions being made by the particular user
     * @param userId User Unique Identifier
     * @param upcoming True if it is a upcoming event else false
     * @return null if error occurs else list of transactions
     */
    public static List<Transaction> getTransactions(String userId, boolean upcoming) {
        List<Transaction> transactions = new ArrayList<>();

        try(Connection con = DataSource.getConnection()) {
            String query = "SELECT * FROM transactions JOIN events ON transactions.eventId = events.id WHERE transactions.userId = ? AND events.date ";

            if(upcoming) {
                query += ">= CURDATE();";
            } else {
                query += "< CURDATE();";
            }

            PreparedStatement statement = con.prepareStatement(query);
            statement.setString(1, userId);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                Transaction transaction = new Transaction();
                transaction.setId(resultSet.getString("id"));
                transaction.setEventId(resultSet.getString("eventId"));
                transaction.setUserId(resultSet.getString("userId"));
                transaction.setNumOfTickets(resultSet.getInt("numOfTickets"));
                transaction.setStatus(resultSet.getString("status"));

                transactions.add(transaction);
            }
        } catch (SQLException sqlException) {
            System.err.printf("Error while getting transactions bought by user %s. %s\n", userId, sqlException.getMessage());
            transactions = null;
        }

        return transactions;
    }

    /**
     * Get the transaction by Id
     * @param transactionId Transaction Unique Identifier
     * @return transaction object if found else null
     */
    public static Transaction getTransaction(String transactionId) {
        Transaction transaction = null;

        try(Connection con = DataSource.getConnection()) {
            String query = "SELECT * FROM transactions WHERE id = ?;";

            PreparedStatement statement = con.prepareStatement(query);
            statement.setString(1, transactionId);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                transaction = new Transaction();
                transaction.setId(resultSet.getString("id"));
                transaction.setEventId(resultSet.getString("eventId"));
                transaction.setUserId(resultSet.getString("userId"));
                transaction.setNumOfTickets(resultSet.getInt("numOfTickets"));
                transaction.setStatus(resultSet.getString("status"));
            }
        } catch (SQLException sqlException) {
            System.err.printf("Error while getting transaction %s information. %s\n", transactionId, sqlException.getMessage());
            transaction = null;
        }

        return transaction;
    }

    /**
     * Delete the transaction by Id
     * @param transactionId Transaction Unique identifier
     * @return true if successful else false
     */
    public static boolean deleteTicket(String transactionId) {
        boolean isDeleted = false;

        try(Connection con = DataSource.getConnection()) {
            String query = "DELETE FROM transactions WHERE id = ?";
            PreparedStatement statement = con.prepareStatement(query);
            statement.setString(1, transactionId);
            statement.executeUpdate();

            isDeleted = true;
        } catch (SQLException sqlException) {
            System.err.printf("Error while deleting an transaction with id as %s. %s.\n", transactionId, sqlException.getMessage());
        }

        return isDeleted;
    }
}
