package Controllers;

import Models.truck_fleet;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class TruckFleetManager extends HttpServlet {
    private ServletContext context;
    private String dbUrl;
    private String dbUsername;
    private String dbPassword;
    private Connection conn;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        try {
            context = getServletContext();
            dbUrl = context.getInitParameter("dbUrl");
            dbUsername = context.getInitParameter("dbUsername");
            dbPassword = context.getInitParameter("dbPassword");

            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection(dbUrl, dbUsername, dbPassword);
        } catch (SQLException | ClassNotFoundException e) {
            throw new ServletException("Database connection failed", e);
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        HttpSession session = request.getSession();
        String source = request.getParameter("source");

        if (source != null) {
            System.out.println(source + " form submitted");

            // Fetch truck data from the database
            List<truck_fleet> trucks = fetchTrucksFromDatabase();

            if (trucks != null && !trucks.isEmpty()) {
                // Store truck data in the session
                session.setAttribute("trucks", trucks);
                System.out.println("Truck data stored in session: " + trucks.size() + " records.");
            } else {
                System.err.println("Error retrieving truck data.");
                session.setAttribute("errorMessage", "Error retrieving truck data. Please try again later.");
            }

            // Forward to the source (e.g., manageFleet.jsp)
            request.getRequestDispatcher(source).forward(request, response);
        } else {
            System.out.println("Source parameter is missing!");
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Source parameter is missing.");
        }
    }

    private List<truck_fleet> fetchTrucksFromDatabase() {
        List<truck_fleet> trucks = new ArrayList<>();
        String query = "SELECT * FROM truck_fleet";

        try (PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                truck_fleet truck = new truck_fleet();
                truck.setTruckModel(rs.getString("TRUCK_MODEL"));
                truck.setTruckManufacturer(rs.getString("TRUCK_MANUFACTURER"));
                truck.setTruckManufacturerYear(rs.getInt("TRUCK_MANUFACTURERYEAR"));
                truck.setTruckLicensePlate(rs.getString("TRUCK_LICENSEPLATE"));
                truck.setTruckLastMaintenance(rs.getString("TRUCK_LASTMAINTENANCE"));
                truck.setTruckNextMaintenance(rs.getString("TRUCK_NEXTMAINTENANCE"));
                trucks.add(truck);
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving trucks: " + e.getMessage());
        }

        return trucks;
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