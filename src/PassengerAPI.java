import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpExchange;
import org.json.JSONObject;
import java.io.*;
import java.net.InetSocketAddress;
import java.sql.*;

public class PassengerAPI {
    private static final String URL = "jdbc:postgresql://localhost:5432/airressys"; // Updated to airressys
    private static final String USER = "postgres";
    private static final String PASSWORD = "AbdaNazar2006";

    public static void main(String[] args) throws Exception {
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);

        server.createContext("/passenger", PassengerAPI::handlePassengerRequests);
        server.createContext("/flight", PassengerAPI::handleFlightRequests);
        server.createContext("/reservation", PassengerAPI::handleReservationRequests);

        server.setExecutor(null);
        server.start();
        System.out.println("Server started at http://localhost:8080");
    }

    // --- Passenger Handlers ---
    private static void handlePassengerRequests(HttpExchange exchange) throws IOException {
        try (Connection conn = getConnection()) {
            String method = exchange.getRequestMethod();
            if ("GET".equals(method)) {
                sendResponse(exchange, getAllPassengers(conn));
            } else if ("POST".equals(method)) {
                JSONObject json = getRequestBody(exchange);
                if (!json.has("name") || !json.has("passport_number") || !json.has("nationality") || !json.has("date_of_birth")) {
                    sendResponse(exchange, "Missing required fields in JSON payload.", 400);
                    return;
                }
                insertPassenger(conn, json.getString("name"), json.getString("passport_number"),
                        json.getString("nationality"), json.getString("date_of_birth"));
                sendResponse(exchange, "Passenger added successfully!");
            } else if ("PUT".equals(method)) {
                JSONObject json = getRequestBody(exchange);
                if (!json.has("id") || !json.has("name")) {
                    sendResponse(exchange, "Missing required fields in JSON payload.", 400);
                    return;
                }
                updatePassenger(conn, json.getInt("id"), json.getString("name"));
                sendResponse(exchange, "Passenger updated.");
            } else if ("DELETE".equals(method)) {
                String query = exchange.getRequestURI().getQuery();
                if (query == null || !query.startsWith("id=")) {
                    sendResponse(exchange, "Missing or invalid 'id' parameter.", 400);
                    return;
                }
                int id = Integer.parseInt(query.split("=")[1]);
                deletePassenger(conn, id);
                sendResponse(exchange, "Passenger deleted.");
            } else {
                sendResponse(exchange, "Unsupported method.", 405);
            }
        } catch (Exception e) {
            sendResponse(exchange, "Error: " + e.getMessage(), 500);
        }
    }

    private static String getAllPassengers(Connection conn) throws SQLException {
        StringBuilder result = new StringBuilder("[");
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM Passenger")) {
            while (rs.next()) {
                result.append(String.format("{\"id\": %d, \"name\": \"%s\", \"passport_number\": \"%s\"},",
                        rs.getInt("id"), rs.getString("name"), rs.getString("passport_number")));
            }
        }
        return result.toString().replaceAll(",$", "") + "]";
    }

    private static void insertPassenger(Connection conn, String name, String passport, String nationality, String dob) throws SQLException {
        String sql = "INSERT INTO Passenger (name, passport_number, nationality, date_of_birth) VALUES (?, ?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.setString(2, passport);
            pstmt.setString(3, nationality);
            pstmt.setDate(4, Date.valueOf(dob));
            pstmt.executeUpdate();
        }
    }

    private static void updatePassenger(Connection conn, int id, String name) throws SQLException {
        String sql = "UPDATE Passenger SET name = ? WHERE id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.setInt(2, id);
            pstmt.executeUpdate();
        }
    }

    private static void deletePassenger(Connection conn, int id) throws SQLException {
        String sql = "DELETE FROM Passenger WHERE id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        }
    }

    // --- Flight Handlers ---
    private static void handleFlightRequests(HttpExchange exchange) throws IOException {
        try (Connection conn = getConnection()) {
            String method = exchange.getRequestMethod();
            if ("GET".equals(method)) {
                sendResponse(exchange, getAllFlights(conn));
            } else if ("POST".equals(method)) {
                JSONObject json = getRequestBody(exchange);
                if (!json.has("flight_number") || !json.has("origin") || !json.has("destination") || !json.has("departure_time")) {
                    sendResponse(exchange, "Missing required fields in JSON payload.", 400);
                    return;
                }
                insertFlight(conn, json.getString("flight_number"), json.getString("origin"), json.getString("destination"), json.getString("departure_time"));
                sendResponse(exchange, "Flight added successfully!");
            } else if ("PUT".equals(method)) {
                JSONObject json = getRequestBody(exchange);
                if (!json.has("id") || !json.has("flight_number") || !json.has("origin") || !json.has("destination") || !json.has("departure_time")) {
                    sendResponse(exchange, "Missing required fields in JSON payload.", 400);
                    return;
                }
                updateFlight(conn, json.getInt("id"), json.getString("flight_number"), json.getString("origin"), json.getString("destination"), json.getString("departure_time"));
                sendResponse(exchange, "Flight updated successfully!");
            } else if ("DELETE".equals(method)) {
                String query = exchange.getRequestURI().getQuery();
                if (query == null || !query.startsWith("id=")) {
                    sendResponse(exchange, "Missing or invalid 'id' parameter.", 400);
                    return;
                }
                int id = Integer.parseInt(query.split("=")[1]);
                deleteFlight(conn, id);
                sendResponse(exchange, "Flight deleted.");
            } else {
                sendResponse(exchange, "Unsupported method.", 405);
            }
        } catch (Exception e) {
            sendResponse(exchange, "Error: " + e.getMessage(), 500);
        }
    }

    private static String getAllFlights(Connection conn) throws SQLException {
        StringBuilder result = new StringBuilder("[");
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM Flight")) {
            while (rs.next()) {
                result.append(String.format("{\"id\": %d, \"flight_number\": \"%s\", \"origin\": \"%s\", \"destination\": \"%s\", \"departure_time\": \"%s\"},",
                        rs.getInt("id"), rs.getString("flight_number"), rs.getString("origin"), rs.getString("destination"), rs.getString("departure_time")));
            }
        }
        return result.toString().replaceAll(",$", "") + "]";
    }

    private static void insertFlight(Connection conn, String flightNumber, String origin, String destination, String departureTime) throws SQLException {
        String sql = "INSERT INTO Flight (flight_number, origin, destination, departure_time) VALUES (?, ?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, flightNumber);
            pstmt.setString(2, origin);
            pstmt.setString(3, destination);
            pstmt.setString(4, departureTime);
            pstmt.executeUpdate();
        }
    }

    private static void updateFlight(Connection conn, int id, String flightNumber, String origin, String destination, String departureTime) throws SQLException {
        String sql = "UPDATE Flight SET flight_number = ?, origin = ?, destination = ?, departure_time = ? WHERE id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, flightNumber);
            pstmt.setString(2, origin);
            pstmt.setString(3, destination);
            pstmt.setString(4, departureTime);
            pstmt.setInt(5, id);
            pstmt.executeUpdate();
        }
    }

    private static void deleteFlight(Connection conn, int id) throws SQLException {
        String sql = "DELETE FROM Flight WHERE id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        }
    }

    // --- Reservation Handlers ---
    private static void handleReservationRequests(HttpExchange exchange) throws IOException {
        try (Connection conn = getConnection()) {
            String method = exchange.getRequestMethod();
            if ("GET".equals(method)) {
                sendResponse(exchange, getAllReservations(conn));
            } else if ("POST".equals(method)) {
                JSONObject json = getRequestBody(exchange);
                if (!json.has("passenger_id") || !json.has("flight_id") || !json.has("seat_number")) {
                    sendResponse(exchange, "Missing required fields in JSON payload.", 400);
                    return;
                }
                insertReservation(conn, json.getInt("passenger_id"), json.getInt("flight_id"), json.getString("seat_number"));
                sendResponse(exchange, "Reservation added successfully!");
            } else if ("PUT".equals(method)) {
                JSONObject json = getRequestBody(exchange);
                if (!json.has("id") || !json.has("passenger_id") || !json.has("flight_id") || !json.has("seat_number")) {
                    sendResponse(exchange, "Missing required fields in JSON payload.", 400);
                    return;
                }
                updateReservation(conn, json.getInt("id"), json.getInt("passenger_id"), json.getInt("flight_id"), json.getString("seat_number"));
                sendResponse(exchange, "Reservation updated successfully!");
            } else if ("DELETE".equals(method)) {
                String query = exchange.getRequestURI().getQuery();
                if (query == null || !query.startsWith("id=")) {
                    sendResponse(exchange, "Missing or invalid 'id' parameter.", 400);
                    return;
                }
                int id = Integer.parseInt(query.split("=")[1]);
                deleteReservation(conn, id);
                sendResponse(exchange, "Reservation deleted.");
            } else {
                sendResponse(exchange, "Unsupported method.", 405);
            }
        } catch (Exception e) {
            sendResponse(exchange, "Error: " + e.getMessage(), 500);
        }
    }

    private static String getAllReservations(Connection conn) throws SQLException {
        StringBuilder result = new StringBuilder("[");
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM Reservation")) {
            while (rs.next()) {
                result.append(String.format("{\"id\": %d, \"passenger_id\": %d, \"flight_id\": %d, \"seat_number\": \"%s\"},",
                        rs.getInt("id"), rs.getInt("passenger_id"), rs.getInt("flight_id"), rs.getString("seat_number")));
            }
        }
        return result.toString().replaceAll(",$", "") + "]";
    }

    private static void insertReservation(Connection conn, int passengerId, int flightId, String seatNumber) throws SQLException {
        String sql = "INSERT INTO Reservation (passenger_id, flight_id, seat_number) VALUES (?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, passengerId);
            pstmt.setInt(2, flightId);
            pstmt.setString(3, seatNumber);
            pstmt.executeUpdate();
        }
    }

    private static void updateReservation(Connection conn, int id, int passengerId, int flightId, String seatNumber) throws SQLException {
        String sql = "UPDATE Reservation SET passenger_id = ?, flight_id = ?, seat_number = ? WHERE id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, passengerId);
            pstmt.setInt(2, flightId);
            pstmt.setString(3, seatNumber);
            pstmt.setInt(4, id);
            pstmt.executeUpdate();
        }
    }

    private static void deleteReservation(Connection conn, int id) throws SQLException {
        String sql = "DELETE FROM Reservation WHERE id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        }
    }

    // --- Utility Methods ---
    private static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    private static JSONObject getRequestBody(HttpExchange exchange) throws IOException {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(exchange.getRequestBody()))) {
            StringBuilder requestBody = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                requestBody.append(line);
            }
            return new JSONObject(requestBody.toString());
        }
    }

    private static void sendResponse(HttpExchange exchange, String response) throws IOException {
        sendResponse(exchange, response, 200);
    }

    private static void sendResponse(HttpExchange exchange, String response, int statusCode) throws IOException {
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.sendResponseHeaders(statusCode, response.length());
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(response.getBytes());
        }
    }
}