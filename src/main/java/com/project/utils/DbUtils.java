package com.project.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.*;

/**
 * DbUtils handles JDBC connections for SQL-based backend validation.
 * After an API call, run a SQL query here to verify data was persisted
 * correctly — this is the end-to-end validation mentioned in the resume.
 *
 * Example interview answer: "After creating a patient via API,
 * I'd query the DB to verify the record was inserted with correct fields."
 */
public class DbUtils {

    private static final Logger log = LogManager.getLogger(DbUtils.class);
    private static Connection connection;

    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            String url      = ConfigReader.get("db.url",      "jdbc:mysql://localhost:3306/testdb");
            String user     = ConfigReader.get("db.username", "root");
            String password = ConfigReader.get("db.password", "");

            connection = DriverManager.getConnection(url, user, password);
            log.info("DB connection established");
        }
        return connection;
    }

    /**
     * Runs a SQL query and returns the value of a single column from row 1.
     * Use for cross-validation: "does the API response match the DB value?"
     */
    public static String getSingleValue(String query, String columnName) {
        try (Statement stmt = getConnection().createStatement();
             ResultSet rs   = stmt.executeQuery(query)) {

            if (rs.next()) {
                String value = rs.getString(columnName);
                log.info("DB query result [{}]: {}", columnName, value);
                return value;
            }
        } catch (SQLException e) {
            log.error("DB query failed: {}", query, e);
        }
        return null;
    }

    /**
     * Returns the count of rows for a given query.
     * Useful for: "verify exactly 1 record was inserted"
     */
    public static int getRowCount(String query) {
        try (Statement stmt = getConnection().createStatement();
             ResultSet rs   = stmt.executeQuery(query)) {

            int count = 0;
            while (rs.next()) count++;
            return count;

        } catch (SQLException e) {
            log.error("DB count query failed: {}", query, e);
            return -1;
        }
    }

    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                log.info("DB connection closed");
            }
        } catch (SQLException e) {
            log.warn("Error closing DB connection", e);
        }
    }
}
