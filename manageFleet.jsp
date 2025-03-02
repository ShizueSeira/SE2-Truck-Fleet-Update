<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ page import="Models.truck_fleet, java.util.List, Models.users" %>

<!DOCTYPE html>
<html lang="en">
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Manage Fleet</title>
        <link rel="stylesheet" type="text/css" href="styles.css">
        <!-- Include Font Awesome for icons -->
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0-beta3/css/all.min.css">
        
        <!-- Add JavaScript to handle modal functionality -->
<script>
        document.addEventListener("DOMContentLoaded", function () {
            const modal = document.getElementById("addModal");
            const btn = document.querySelector(".add-button");
            const close = document.querySelector(".close");
    
            btn.addEventListener("click", function () {
                modal.style.display = "block";
            });
    
            close.addEventListener("click", function () {
                modal.style.display = "none";
            });
    
            window.addEventListener("click", function (event) {
                if (event.target === modal) {
                    modal.style.display = "none";
                }
            });
        });
</script>
    </head>

    <body>
        <%
            // Prevent caching of this page
            response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
            response.setHeader("Pragma", "no-cache");
            response.setHeader("Expires", "0");

            // Check session for user status
            String sessionId = session.getId();
            users current = (users) session.getAttribute(sessionId);

            if (current == null || !current.getStatus()) {
                response.sendRedirect("login.jsp");
                return;
            }

            // Retrieve the truck data from the session
            List<truck_fleet> trucks = (List<truck_fleet>) session.getAttribute("trucks");

            // Check for errors
            String errorMessage = (String) session.getAttribute("errorMessage");
            if (errorMessage != null) {
                out.println("<p style='color: red;'>" + errorMessage + "</p>");
                session.removeAttribute("errorMessage"); // Clear the error message after displaying it
            }
        %>

        <header>
            <div class="new-path">
                <a href="dashboard.jsp" class="newpathformat">Dashboard</a>
                <p class="new-patharrow">&gt;</p>
                <a href="#" class="newpathformat">Manage Fleet</a>
            </div>
        </header>

        <div class="parts-content">
            <div class="header-container">
                <a href="dashboard.jsp" class="parts-back">&lt; Back</a>
                <h1 class="h1-parts">Manage Fleet</h1>
            </div>

            <div class="parts-container">
                <div class="filtering">
                    <div class="filter-option">
                        <h3 class="filtering-desc">Search Plate Number:</h3>
                        <input type="text" class="filter-search">
                    </div>
                    <div class="filter-option">
                        <h3 class="filtering-desc">Search Engine Model:</h3>
                        <input type="text" class="filter-search">
                    </div>
                    <div class="filter-option">
                        <h3 class="filtering-desc">Select Make & Model:</h3>
                        <select type="text" class="filter-select">
                            <!-- Populate this dropdown if needed -->
                        </select>
                    </div>
                </div>

                <!-- Display the truck list -->
                <div class="parts-list">
                    <table class="gen-table">
                        <thead>
                            <tr>
                                <th class="gen-columnname">Plate Number</th>
                                <th class="gen-columnname">Make</th>
                                <th class="gen-columnname">Model</th>
                                <th class="gen-columnname">Manufacturer Year</th>
                                <th class="gen-columnname">Last Maintenance</th>
                                <th class="gen-columnname">Next Maintenance</th>
                                <th class="gen-columnname">Actions</th> <!-- New column for actions -->
                            </tr>
                        </thead>
                        <tbody class="itemlist">
                            <%
                                if (trucks != null && !trucks.isEmpty()) {
                                    for (truck_fleet truck : trucks) {
                            %>
                            <tr>
                                <td><%= truck.getTruckLicensePlate() %></td>
                                <td><%= truck.getTruckManufacturer() %></td>
                                <td><%= truck.getTruckModel() %></td>
                                <td><%= truck.getTruckManufacturerYear() %></td>
                                <td><%= truck.getTruckLastMaintenance() %></td>
                                <td><%= truck.getTruckNextMaintenance() %></td>
                                <td>
                                    <!-- Pencil icon for editing -->
                                    <a href="EditTruckServlet?licensePlate=<%= truck.getTruckLicensePlate() %>">
                                        <i class="fas fa-pencil-alt"></i> <!-- Font Awesome pencil icon -->
                                    </a>
                                    &nbsp; <!-- Add space between icons -->
                                    <!-- Trash can icon for deletion -->
                                    <a href="DeleteTruckServlet?licensePlate=<%= truck.getTruckLicensePlate() %>" 
                                       onclick="return confirm('Are you sure you want to delete this truck?');">
                                        <i class="fas fa-trash"></i> <!-- Font Awesome trash can icon -->
                                    </a>
                                </td>
                            </tr>
                            <%
                                    }
                                } else {
                            %>
                            <tr>
                                <td colspan="7">No trucks available</td>
                            </tr>
                            <%
                                }
                            %>
                        </tbody>
                    </table>
                </div>
            </div>

            <!-- Action buttons -->
            <div class="button-container">
                <div class="left-buttons">
                    <button class="export-button">Export as Excel</button>
                    <button class="export-button">Export as PDF</button>
                </div>
                <div class="right-buttons">
                    <button class="add-button">Add Truck</button>
                    <button class="edit-button">Edit</button>
                </div>
            </div>
        </div>

<!-- Add Truck Modal -->
<div id="addModal" class="add-modal">
    <div class="addmodal-box">
        <div class="addmodal-content">
            <span class="close">&times;</span>
            <h1 style="color: #5271ff;">Add Truck</h1>
            
            <!-- Form to submit truck details -->
            <form id="addTruckForm" action="AddTruckServlet" method="POST">
                <div class="add-details-textbox">
                    <label class="option-label">Truck Model</label>
                    <input type="text" name="truckModel" class="item-options" placeholder="Enter Truck Model" required>

                    <label class="option-label">Truck Engine</label>
                    <input type="text" name="truckEngine" class="item-options" placeholder="Enter Truck Engine" required>

                    <label class="option-label">License Plate</label>
                    <input type="text" name="truckLicensePlate" class="item-options" placeholder="Enter License Plate" required>

                    <label class="option-label">Truck Manufacturer</label>
                    <input type="text" name="truckManufacturer" class="item-options" placeholder="Enter Truck Manufacturer" required>

                    <label class="option-label">Manufacturer Year</label>
                    <input type="number" name="truckManufacturerYear" class="item-options" placeholder="Enter Manufacturer Year" required>

                    <label class="option-label">Last Maintenance Date</label>
                    <input type="date" name="truckLastMaintenance" class="item-options" required>

                    <label class="option-label">Next Maintenance Date</label>
                    <input type="date" name="truckNextMaintenance" class="item-options" required>
                </div>

                <button type="submit" id="addButton" class="addbutton">Add Truck</button>
            </form>
        </div>
    </div>
</div>
        
    </body>
</html>