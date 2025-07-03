package controller;

import connector.SQLConnector;

import java.sql.DriverManager;
import java.sql.Connection;

public class Main {
    public static void main(String[] args) {
        String dbUrl = "jdbc:postgresql://localhost:5432/practise";
        String dbUser = "sambhav";
        String dbPassword = "";
        final String apiUrl = "https://api.openweathermap.org/data/2.5/weather";
        final String apiKey = "8747f876c30bafb32ab7291044b1f399";
        String city = "Milpitas";
        String units = "imperial"; // imperail for (F) or metric for (C) or standard for (K)

        try(Connection conn = DriverManager.getConnection(dbUrl, dbUser,dbPassword)) {
            conn.setAutoCommit(false);
            try {
                SQLConnector connector = new SQLConnector(apiUrl, apiKey, conn);
                connector.syncData(city, units);
                conn.commit();
            } catch (Exception e) {
                conn.rollback();
                System.out.println("There is an exception : " + e);
            } finally {
                conn.setAutoCommit(true);
            }
        }
        catch (Exception e ) {
            System.out.println("There is an exception : " + e);
        }
    }
}
