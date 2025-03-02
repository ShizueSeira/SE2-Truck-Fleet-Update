package Controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import javax.servlet.ServletException;
//import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

//@WebServlet("/DeleteTruckServlet")
public class DeleteTruckServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String licensePlate = request.getParameter("licensePlate");

        if (licensePlate != null && !licensePlate.isEmpty()) {
            try {
                // Establish database connection
                String dbUrl = "jdbc:mysql://localhost:3306/SoftEng?useTimeZone=true&serverTimezone=UTC&autoReconnect=true&useSSL=false";
                String dbUsername = "root";
                String dbPassword = "happyworld@1";
                Connection conn = DriverManager.getConnection(dbUrl, dbUsername, dbPassword);

                // Delete the truck record
                String query = "DELETE FROM truck_fleet WHERE TRUCK_LICENSEPLATE = ?";
                try (PreparedStatement stmt = conn.prepareStatement(query)) {
                    stmt.setString(1, licensePlate);
                    stmt.executeUpdate();
                }

                // Close the connection
                conn.close();

                // Redirect back to the manageFleet.jsp page
                response.sendRedirect("manageFleet.jsp");
            } catch (Exception e) {
                e.printStackTrace();
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error deleting truck record.");
            }
        } else {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid license plate.");
        }
    }
}