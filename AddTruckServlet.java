package Controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class AddTruckServlet extends HttpServlet {
    private Connection conn;

    @Override
    public void init() throws ServletException {
        try {
            // Initialize database connection
            String dbUrl = getServletContext().getInitParameter("dbUrl");
            String dbUsername = getServletContext().getInitParameter("dbUsername");
            String dbPassword = getServletContext().getInitParameter("dbPassword");

            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection(dbUrl, dbUsername, dbPassword);
        } catch (SQLException | ClassNotFoundException e) {
            throw new ServletException("Database connection failed", e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Retrieve form data
        String truckModel = request.getParameter("truckModel");
        String truckEngine = request.getParameter("truckEngine");
        String truckLicensePlate = request.getParameter("truckLicensePlate");
        String truckManufacturer = request.getParameter("truckManufacturer");
        int truckManufacturerYear = Integer.parseInt(request.getParameter("truckManufacturerYear"));
        String truckLastMaintenance = request.getParameter("truckLastMaintenance");
        String truckNextMaintenance = request.getParameter("truckNextMaintenance");

        // Debug: Print form data
        System.out.println("Truck Model: " + truckModel);
        System.out.println("Truck Engine: " + truckEngine);
        System.out.println("Truck License Plate: " + truckLicensePlate);
        System.out.println("Truck Manufacturer: " + truckManufacturer);
        System.out.println("Truck Manufacturer Year: " + truckManufacturerYear);
        System.out.println("Truck Last Maintenance: " + truckLastMaintenance);
        System.out.println("Truck Next Maintenance: " + truckNextMaintenance);

        // Insert truck into the database
        boolean isSuccess = insertTruckIntoDatabase(truckModel, truckEngine, truckLicensePlate, truckManufacturer, truckManufacturerYear, truckLastMaintenance, truckNextMaintenance);

        if (isSuccess) {
            // Redirect to the manage fleet page with a success message
            response.sendRedirect("manageFleet.jsp?status=success");
        } else {
            // Redirect to the manage fleet page with an error message
            response.sendRedirect("manageFleet.jsp?status=error");
        }
    }

    private boolean insertTruckIntoDatabase(String truckModel, String truckEngine, String truckLicensePlate, String truckManufacturer, int truckManufacturerYear, String truckLastMaintenance, String truckNextMaintenance) {
        String query = "INSERT INTO truck_fleet (TRUCK_MODEL, TRUCK_ENGINE, TRUCK_LICENSEPLATE, TRUCK_MANUFACTURER, TRUCK_MANUFACTURERYEAR, TRUCK_LASTMAINTENANCE, TRUCK_NEXTMAINTENANCE) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, truckModel);
            stmt.setString(2, truckEngine);
            stmt.setString(3, truckLicensePlate);
            stmt.setString(4, truckManufacturer);
            stmt.setInt(5, truckManufacturerYear);
            stmt.setString(6, truckLastMaintenance);
            stmt.setString(7, truckNextMaintenance);

            // Debug: Print the SQL query and parameters
            System.out.println("Executing query: " + query);
            System.out.println("Parameters: " + truckModel + ", " + truckEngine + ", " + truckLicensePlate + ", " + truckManufacturer + ", " + truckManufacturerYear + ", " + truckLastMaintenance + ", " + truckNextMaintenance);

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error inserting truck: " + e.getMessage());
            return false;
        }
    }

    @Override
    public void destroy() {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                System.err.println("Error closing database connection: " + e.getMessage());
            }
        }
    }
}