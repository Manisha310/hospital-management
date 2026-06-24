package com.server;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.sql.*;
import java.util.*;
import java.util.Properties;

public class HospitalServer {

  private static int PORT = 8080;
  private static String DB_URL = "jdbc:mysql://localhost:3306/hospital_management";
  private static String DB_USER = "root";
  private static String DB_PASS = "";

  public static void main(String[] args) throws Exception {
    // Load database.properties
    Properties props = new Properties();
    File propsFile = new File("database.properties");
    if (propsFile.exists()) {
      try (FileInputStream fis = new FileInputStream(propsFile)) {
        props.load(fis);
        if (props.containsKey("url")) DB_URL = props.getProperty("url");
        if (props.containsKey("username")) DB_USER = props.getProperty("username");
        if (props.containsKey("password")) DB_PASS = props.getProperty("password");
        System.out.println("Loaded database.properties: " + DB_URL + " user=" + DB_USER);
      }
    } else {
      System.out.println("database.properties not found, using defaults");
    }

    Class.forName("com.mysql.cj.jdbc.Driver");
    HttpServer server = HttpServer.create(new InetSocketAddress(PORT), 0);

    server.createContext("/api/dashboard", new DashboardHandler());
    server.createContext("/api/patients/out", new OutPatientHandler());
    server.createContext("/api/patients/in", new InPatientHandler());
    server.createContext("/api/doctors", new DoctorHandler());
    server.createContext("/api/appointments", new AppointmentHandler());
    server.createContext("/api/allocations", new AllocationHandler());
    server.createContext("/api/payments", new PaymentHandler());

    File frontendDir = new File("frontend");
    if (frontendDir.exists()) {
      server.createContext("/", new StaticFileHandler(frontendDir));
    }

    server.setExecutor(null);
    System.out.println("Server started at http://localhost:" + PORT);
    System.out.println("Open your browser and go to http://localhost:8080/index.html");
    System.out.println("");

    // Test DB connection
    try {
      Connection testCon = getCon();
      testCon.close();
      System.out.println("Database connection: SUCCESS");
    } catch (Exception e) {
      System.out.println("Database connection: FAILED - " + e.getMessage());
      System.out.println("Frontend will use localStorage fallback mode.");
    }

    server.start();
  }

  static Connection getCon() throws SQLException {
    return DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
  }

  static void sendJson(HttpExchange ex, int code, String json) throws IOException {
    byte[] b = json.getBytes(StandardCharsets.UTF_8);
    ex.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
    ex.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
    ex.getResponseHeaders().set("Access-Control-Allow-Methods", "GET,POST,PUT,DELETE,OPTIONS");
    ex.getResponseHeaders().set("Access-Control-Allow-Headers", "Content-Type");
    ex.sendResponseHeaders(code, b.length);
    ex.getResponseBody().write(b);
    ex.getResponseBody().close();
  }

  static void sendError(HttpExchange ex, int code, String msg) throws IOException {
    sendJson(ex, code, "{\"error\":\"" + JsonUtil.escape(msg) + "\"}");
  }

  static void handleCors(HttpExchange ex) throws IOException {
    ex.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
    ex.getResponseHeaders().set("Access-Control-Allow-Methods", "GET,POST,PUT,DELETE,OPTIONS");
    ex.getResponseHeaders().set("Access-Control-Allow-Headers", "Content-Type");
    ex.sendResponseHeaders(204, -1);
  }

  static String body(HttpExchange ex) throws IOException {
    try (InputStream is = ex.getRequestBody()) {
      return new String(is.readAllBytes(), StandardCharsets.UTF_8);
    }
  }

  // --- Dashboard ---
  static class DashboardHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange ex) throws IOException {
      if ("OPTIONS".equals(ex.getRequestMethod())) { handleCors(ex); return; }
      try (Connection c = getCon()) {
        int out = 0, in = 0, docs = 0, appts = 0, allocs = 0, pays = 0;
        List<Map<String, Object>> recentAppts = new ArrayList<>();
        List<Map<String, Object>> doctorsList = new ArrayList<>();

        Statement st = c.createStatement();
        ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM outpatient");
        if (rs.next()) out = rs.getInt(1);
        rs = st.executeQuery("SELECT COUNT(*) FROM inpatient");
        if (rs.next()) in = rs.getInt(1);
        rs = st.executeQuery("SELECT COUNT(*) FROM doctor");
        if (rs.next()) docs = rs.getInt(1);
        rs = st.executeQuery("SELECT COUNT(*) FROM appointment");
        if (rs.next()) appts = rs.getInt(1);
        try { rs = st.executeQuery("SELECT COUNT(*) FROM allocation"); if (rs.next()) allocs = rs.getInt(1); } catch (Exception ign) {}
        try { rs = st.executeQuery("SELECT COUNT(*) FROM payment"); if (rs.next()) pays = rs.getInt(1); } catch (Exception ign) {}

        rs = st.executeQuery("SELECT * FROM appointment ORDER BY appointment_date DESC LIMIT 5");
        while (rs.next()) {
          Map<String, Object> m = new LinkedHashMap<>();
          m.put("appointmentId", rs.getString("appointment_id"));
          m.put("patientId", rs.getString("patient_id"));
          m.put("doctorId", rs.getString("doctor_id"));
          m.put("appointmentDate", rs.getString("appointment_date"));
          recentAppts.add(m);
        }

        rs = st.executeQuery("SELECT * FROM doctor LIMIT 5");
        while (rs.next()) {
          Map<String, Object> m = new LinkedHashMap<>();
          m.put("doctorId", rs.getString("doctor_id"));
          m.put("doctorName", rs.getString("doctor_name"));
          m.put("specialization", rs.getString("specialization"));
          m.put("availableDate", rs.getString("available_date"));
          doctorsList.add(m);
        }
        st.close();

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("outPatients", out);
        result.put("inPatients", in);
        result.put("doctors", docs);
        result.put("appointments", appts);
        result.put("allocations", allocs);
        result.put("payments", pays);
        result.put("recentAppointments", recentAppts);
        result.put("doctorsList", doctorsList);
        sendJson(ex, 200, toJson(result));
      } catch (Exception e) {
        System.out.println("Dashboard API error: " + e.getMessage());
        e.printStackTrace();
        sendError(ex, 500, e.getMessage());
      }
    }

    private String toJson(Map<String, Object> m) {
      StringBuilder sb = new StringBuilder("{");
      boolean first = true;
      for (Map.Entry<String, Object> e : m.entrySet()) {
        if (!first) sb.append(",");
        first = false;
        sb.append("\"").append(e.getKey()).append("\":");
        Object v = e.getValue();
        if (v instanceof Number || v instanceof Boolean) sb.append(v);
        else if (v instanceof List) {
          sb.append("[");
          boolean f2 = true;
          for (Object item : (List) v) {
            if (!f2) sb.append(",");
            f2 = false;
            if (item instanceof Map) sb.append(toJson((Map) item));
            else sb.append("\"").append(item).append("\"");
          }
          sb.append("]");
        } else sb.append("\"").append(JsonUtil.escape(String.valueOf(v))).append("\"");
      }
      sb.append("}");
      return sb.toString();
    }
  }

   // --- OutPatient ---
  static class OutPatientHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange ex) throws IOException {
      if ("OPTIONS".equals(ex.getRequestMethod())) { handleCors(ex); return; }
      String method = ex.getRequestMethod();
      String path = ex.getRequestURI().getPath();
      String query = ex.getRequestURI().getQuery();

      try (Connection c = getCon()) {
        if ("GET".equals(method) && query != null && query.startsWith("q=")) {
          String q = URLDecoder.decode(query.substring(2), "UTF-8");
          PreparedStatement ps = c.prepareStatement(
            "SELECT * FROM outpatient WHERE patient_id LIKE ? OR patient_name LIKE ?");
          ps.setString(1, "%" + q + "%");
          ps.setString(2, "%" + q + "%");
          sendJson(ex, 200, listToJson(ps.executeQuery(),
            new String[]{"patientId","patientName","phNo","age","gender","medicalHistory","specialist"},
            new String[]{"patient_id","patient_name","phone_number","age","gender","medical_history","preferred_specialist"}));
          ps.close();
        } else if ("GET".equals(method)) {
          Statement st = c.createStatement();
          sendJson(ex, 200, listToJson(st.executeQuery("SELECT * FROM outpatient"),
            new String[]{"patientId","patientName","phNo","age","gender","medicalHistory","specialist"},
            new String[]{"patient_id","patient_name","phone_number","age","gender","medical_history","preferred_specialist"}));
          st.close();
        } else if ("POST".equals(method)) {
          Map<String, String> data = JsonUtil.parseJson(body(ex));
          String id = generateId(c, "outpatient", "PT");
          PreparedStatement ps = c.prepareStatement(
            "INSERT INTO outpatient(patient_id,patient_name,phone_number,age,gender,medical_history,preferred_specialist) VALUES(?,?,?,?,?,?,?)");
          ps.setString(1, id);
          ps.setString(2, data.getOrDefault("patientName", ""));
          ps.setLong(3, Long.parseLong(data.getOrDefault("phNo", data.getOrDefault("phoneNumber", "0"))));
          ps.setInt(4, Integer.parseInt(data.getOrDefault("age", "0")));
          ps.setString(5, data.getOrDefault("gender", "Male"));
          ps.setString(6, data.getOrDefault("medicalHistory", ""));
          ps.setString(7, data.getOrDefault("specialist", data.getOrDefault("specialisation", "")));
          ps.executeUpdate();
          ps.close();
          sendJson(ex, 201, "{\"message\":\"Created\",\"id\":\"" + id + "\"}");
        } else {
          String id = path.substring(path.lastIndexOf('/') + 1);
          if (id.isEmpty()) { sendError(ex, 400, "Missing ID"); return; }
          if ("PUT".equals(method)) {
            Map<String, String> data = JsonUtil.parseJson(body(ex));
            PreparedStatement ps = c.prepareStatement(
              "UPDATE outpatient SET patient_name=?,phone_number=?,age=?,gender=?,medical_history=?,preferred_specialist=? WHERE patient_id=?");
            ps.setString(1, data.getOrDefault("patientName", ""));
            ps.setLong(2, Long.parseLong(data.getOrDefault("phNo", data.getOrDefault("phoneNumber", "0"))));
            ps.setInt(3, Integer.parseInt(data.getOrDefault("age", "0")));
            ps.setString(4, data.getOrDefault("gender", "Male"));
            ps.setString(5, data.getOrDefault("medicalHistory", ""));
            ps.setString(6, data.getOrDefault("specialist", data.getOrDefault("specialisation", "")));
            ps.setString(7, id);
            ps.executeUpdate();
            ps.close();
            sendJson(ex, 200, "{\"message\":\"Updated\"}");
          } else if ("DELETE".equals(method)) {
            PreparedStatement ps = c.prepareStatement("DELETE FROM outpatient WHERE patient_id=?");
            ps.setString(1, id);
            ps.executeUpdate();
            ps.close();
            sendJson(ex, 200, "{\"message\":\"Deleted\"}");
          }
        }
      } catch (Exception e) {
        System.out.println("OutPatient API error: " + e.getMessage());
        e.printStackTrace();
        sendError(ex, 500, e.getMessage());
      }
    }
  }

  // --- InPatient ---
  static class InPatientHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange ex) throws IOException {
      if ("OPTIONS".equals(ex.getRequestMethod())) { handleCors(ex); return; }
      String method = ex.getRequestMethod();
      String path = ex.getRequestURI().getPath();

      try (Connection c = getCon()) {
        if ("GET".equals(method)) {
          Statement st = c.createStatement();
          sendJson(ex, 200, listToJson(st.executeQuery("SELECT * FROM inpatient"),
            new String[]{"patientId","patientName","phoneNumber","age","gender","medicalHistory","specialist","treatment","roomType","food","days"},
            new String[]{"patient_id","patient_name","phone","age","gender","history","specialist","treatment","room_type","food","days"}));
          st.close();
        } else if ("POST".equals(method)) {
          Map<String, String> data = JsonUtil.parseJson(body(ex));
          String id = generateId(c, "inpatient", "IP");
          PreparedStatement ps = c.prepareStatement(
            "INSERT INTO inpatient(patient_id,patient_name,phone,age,gender,history,specialist,treatment,days,room_type,food) VALUES(?,?,?,?,?,?,?,?,?,?,?)");
          ps.setString(1, id);
          ps.setString(2, data.getOrDefault("patientName", ""));
          ps.setLong(3, Long.parseLong(data.getOrDefault("phoneNumber", "0")));
          ps.setInt(4, Integer.parseInt(data.getOrDefault("age", "0")));
          ps.setString(5, data.getOrDefault("gender", "Male"));
          ps.setString(6, data.getOrDefault("medicalHistory", ""));
          ps.setString(7, data.getOrDefault("specialist", ""));
          ps.setString(8, data.getOrDefault("treatment", ""));
          ps.setInt(9, Integer.parseInt(data.getOrDefault("days", "1")));
          ps.setString(10, data.getOrDefault("roomType", "NONAC"));
          ps.setString(11, data.getOrDefault("food", "NO"));
          ps.executeUpdate();
          ps.close();
          sendJson(ex, 201, "{\"message\":\"Created\",\"id\":\"" + id + "\"}");
        } else {
          String id = path.substring(path.lastIndexOf('/') + 1);
          if (id.isEmpty()) { sendError(ex, 400, "Missing ID"); return; }
          if ("PUT".equals(method)) {
            Map<String, String> data = JsonUtil.parseJson(body(ex));
            PreparedStatement ps = c.prepareStatement(
              "UPDATE inpatient SET patient_name=?,phone=?,age=?,gender=?,history=?,specialist=?,treatment=?,days=?,room_type=?,food=? WHERE patient_id=?");
            ps.setString(1, data.getOrDefault("patientName", ""));
            ps.setLong(2, Long.parseLong(data.getOrDefault("phoneNumber", "0")));
            ps.setInt(3, Integer.parseInt(data.getOrDefault("age", "0")));
            ps.setString(4, data.getOrDefault("gender", "Male"));
            ps.setString(5, data.getOrDefault("medicalHistory", ""));
            ps.setString(6, data.getOrDefault("specialist", ""));
            ps.setString(7, data.getOrDefault("treatment", ""));
            ps.setInt(8, Integer.parseInt(data.getOrDefault("days", "1")));
            ps.setString(9, data.getOrDefault("roomType", "NONAC"));
            ps.setString(10, data.getOrDefault("food", "NO"));
            ps.setString(11, id);
            ps.executeUpdate();
            ps.close();
            sendJson(ex, 200, "{\"message\":\"Updated\"}");
          } else if ("DELETE".equals(method)) {
            PreparedStatement ps = c.prepareStatement("DELETE FROM inpatient WHERE patient_id=?");
            ps.setString(1, id);
            ps.executeUpdate();
            ps.close();
            sendJson(ex, 200, "{\"message\":\"Deleted\"}");
          }
        }
      } catch (Exception e) {
        System.out.println("InPatient API error: " + e.getMessage());
        e.printStackTrace();
        sendError(ex, 500, e.getMessage());
      }
    }
  }

  // --- Doctor ---
  static class DoctorHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange ex) throws IOException {
      if ("OPTIONS".equals(ex.getRequestMethod())) { handleCors(ex); return; }
      String method = ex.getRequestMethod();
      String path = ex.getRequestURI().getPath();

      try (Connection c = getCon()) {
        if ("GET".equals(method)) {
          Statement st = c.createStatement();
          sendJson(ex, 200, listToJson(st.executeQuery("SELECT * FROM doctor"),
            new String[]{"doctorId","doctorName","specialization","availableDate","availableTime"},
            new String[]{"doctor_id","doctor_name","specialization","available_date","available_time"}));
          st.close();
        } else if ("POST".equals(method)) {
          Map<String, String> data = JsonUtil.parseJson(body(ex));
          String id = generateId(c, "doctor", "DR");
          PreparedStatement ps = c.prepareStatement(
            "INSERT INTO doctor VALUES(?,?,?,?,?)");
          ps.setString(1, id);
          ps.setString(2, data.getOrDefault("doctorName", ""));
          ps.setString(3, data.getOrDefault("specialization", ""));
          ps.setString(4, data.getOrDefault("availableDate", ""));
          ps.setString(5, data.getOrDefault("availableTime", ""));
          ps.executeUpdate();
          ps.close();
          sendJson(ex, 201, "{\"message\":\"Created\",\"id\":\"" + id + "\"}");
        } else {
          String id = path.substring(path.lastIndexOf('/') + 1);
          if (id.isEmpty()) { sendError(ex, 400, "Missing ID"); return; }
          if ("PUT".equals(method)) {
            Map<String, String> data = JsonUtil.parseJson(body(ex));
            PreparedStatement ps = c.prepareStatement(
              "UPDATE doctor SET doctor_name=?,specialization=?,available_date=?,available_time=? WHERE doctor_id=?");
            ps.setString(1, data.getOrDefault("doctorName", ""));
            ps.setString(2, data.getOrDefault("specialization", ""));
            ps.setString(3, data.getOrDefault("availableDate", ""));
            ps.setString(4, data.getOrDefault("availableTime", ""));
            ps.setString(5, id);
            ps.executeUpdate();
            ps.close();
            sendJson(ex, 200, "{\"message\":\"Updated\"}");
          } else if ("DELETE".equals(method)) {
            PreparedStatement ps = c.prepareStatement("DELETE FROM doctor WHERE doctor_id=?");
            ps.setString(1, id);
            ps.executeUpdate();
            ps.close();
            sendJson(ex, 200, "{\"message\":\"Deleted\"}");
          }
        }
      } catch (Exception e) {
        System.out.println("Doctor API error: " + e.getMessage());
        e.printStackTrace();
        sendError(ex, 500, e.getMessage());
      }
    }
  }

  // --- Appointment ---
  static class AppointmentHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange ex) throws IOException {
      if ("OPTIONS".equals(ex.getRequestMethod())) { handleCors(ex); return; }
      String method = ex.getRequestMethod();
      String path = ex.getRequestURI().getPath();

      try (Connection c = getCon()) {
        if ("GET".equals(method)) {
          Statement st = c.createStatement();
          sendJson(ex, 200, listToJson(st.executeQuery("SELECT * FROM appointment"),
            new String[]{"appointmentId","patientId","doctorId","specialist","appointmentDate","appointmentTime"},
            new String[]{"appointment_id","patient_id","doctor_id","specialization","appointment_date","appointment_time"}));
          st.close();
        } else if ("POST".equals(method)) {
          Map<String, String> data = JsonUtil.parseJson(body(ex));
          String id = generateId(c, "appointment", "APT");
          PreparedStatement ps = c.prepareStatement(
            "INSERT INTO appointment(appointment_id,patient_id,doctor_id,specialization,appointment_date,appointment_time) VALUES(?,?,?,?,?,?)");
          ps.setString(1, id);
          ps.setString(2, data.getOrDefault("patientId", ""));
          ps.setString(3, data.getOrDefault("doctorId", ""));
          ps.setString(4, data.getOrDefault("specialist", ""));
          ps.setString(5, data.getOrDefault("appointmentDate", ""));
          ps.setString(6, data.getOrDefault("appointmentTime", ""));
          ps.executeUpdate();
          ps.close();
          sendJson(ex, 201, "{\"message\":\"Created\",\"id\":\"" + id + "\"}");
        } else {
          String id = path.substring(path.lastIndexOf('/') + 1);
          if (id.isEmpty()) { sendError(ex, 400, "Missing ID"); return; }
          if ("PUT".equals(method)) {
            Map<String, String> data = JsonUtil.parseJson(body(ex));
            PreparedStatement ps = c.prepareStatement(
              "UPDATE appointment SET patient_id=?,doctor_id=?,specialization=?,appointment_date=?,appointment_time=? WHERE appointment_id=?");
            ps.setString(1, data.getOrDefault("patientId", ""));
            ps.setString(2, data.getOrDefault("doctorId", ""));
            ps.setString(3, data.getOrDefault("specialist", ""));
            ps.setString(4, data.getOrDefault("appointmentDate", ""));
            ps.setString(5, data.getOrDefault("appointmentTime", ""));
            ps.setString(6, id);
            ps.executeUpdate();
            ps.close();
            sendJson(ex, 200, "{\"message\":\"Updated\"}");
          } else if ("DELETE".equals(method)) {
            PreparedStatement ps = c.prepareStatement("DELETE FROM appointment WHERE appointment_id=?");
            ps.setString(1, id);
            ps.executeUpdate();
            ps.close();
            sendJson(ex, 200, "{\"message\":\"Deleted\"}");
          }
        }
      } catch (Exception e) {
        System.out.println("Appointment API error: " + e.getMessage());
        e.printStackTrace();
        sendError(ex, 500, e.getMessage());
      }
    }
  }

  // --- Allocation ---
  static class AllocationHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange ex) throws IOException {
      if ("OPTIONS".equals(ex.getRequestMethod())) { handleCors(ex); return; }
      String method = ex.getRequestMethod();
      String path = ex.getRequestURI().getPath();

      try (Connection c = getCon()) {
        if ("GET".equals(method)) {
          Statement st = c.createStatement();
          sendJson(ex, 200, listToJson(st.executeQuery("SELECT * FROM allocation"),
            new String[]{"allocationId","patientId","roomNumber","noOfDays"},
            new String[]{"allocation_id","patient_id","room_number","no_of_days_admitted"}));
          st.close();
        } else if ("POST".equals(method)) {
          Map<String, String> data = JsonUtil.parseJson(body(ex));
          String id = generateId(c, "allocation", "ALC");
          PreparedStatement ps = c.prepareStatement(
            "INSERT INTO allocation(allocation_id,patient_id,room_number,no_of_days_admitted) VALUES(?,?,?,?)");
          ps.setString(1, id);
          ps.setString(2, data.getOrDefault("patientId", ""));
          ps.setInt(3, Integer.parseInt(data.getOrDefault("roomNumber", "0")));
          ps.setInt(4, Integer.parseInt(data.getOrDefault("noOfDays", "1")));
          ps.executeUpdate();
          ps.close();
          sendJson(ex, 201, "{\"message\":\"Created\",\"id\":\"" + id + "\"}");
        } else {
          String id = path.substring(path.lastIndexOf('/') + 1);
          if (id.isEmpty()) { sendError(ex, 400, "Missing ID"); return; }
          if ("PUT".equals(method)) {
            Map<String, String> data = JsonUtil.parseJson(body(ex));
            PreparedStatement ps = c.prepareStatement(
              "UPDATE allocation SET patient_id=?,room_number=?,no_of_days_admitted=? WHERE allocation_id=?");
            ps.setString(1, data.getOrDefault("patientId", ""));
            ps.setInt(2, Integer.parseInt(data.getOrDefault("roomNumber", "0")));
            ps.setInt(3, Integer.parseInt(data.getOrDefault("noOfDays", "1")));
            ps.setString(4, id);
            ps.executeUpdate();
            ps.close();
            sendJson(ex, 200, "{\"message\":\"Updated\"}");
          } else if ("DELETE".equals(method)) {
            PreparedStatement ps = c.prepareStatement("DELETE FROM allocation WHERE allocation_id=?");
            ps.setString(1, id);
            ps.executeUpdate();
            ps.close();
            sendJson(ex, 200, "{\"message\":\"Deleted\"}");
          }
        }
      } catch (Exception e) {
        System.out.println("Allocation API error: " + e.getMessage());
        e.printStackTrace();
        sendError(ex, 500, e.getMessage());
      }
    }
  }

  // --- Payment ---
  static class PaymentHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange ex) throws IOException {
      if ("OPTIONS".equals(ex.getRequestMethod())) { handleCors(ex); return; }
      String method = ex.getRequestMethod();
      String path = ex.getRequestURI().getPath();

      try (Connection c = getCon()) {
        if ("GET".equals(method)) {
          Statement st = c.createStatement();
          sendJson(ex, 200, listToJson(st.executeQuery("SELECT * FROM payment"),
            new String[]{"paymentId","patientId","patientName","patientType","paymentDate","modeOfPayment","billAmount","doctorFee","medicineFees","roomFees","registrationFees","totalBill"},
            new String[]{"PAYMENT_ID","PATIENT_ID","PATIENT_NAME","PATIENT_TYPE","PAYMENT_DATE","MODE_OF_PAYMENT","BILL_AMOUNT","DOCTOR_FEE","MEDICINE_FEES","ROOM_FEES","REGISTRATION_FEES","TOTAL_BILL"}));
          st.close();
        } else if ("POST".equals(method)) {
          Map<String, String> data = JsonUtil.parseJson(body(ex));
          String id = generateId(c, "payment", "PAY");
          double docFee = Double.parseDouble(data.getOrDefault("doctorFee", "0"));
          double medFee = Double.parseDouble(data.getOrDefault("medicineFees", "0"));
          double roomFee = Double.parseDouble(data.getOrDefault("roomFees", "0"));
          double regFee = Double.parseDouble(data.getOrDefault("registrationFees", "0"));
          double total = docFee + medFee + roomFee + regFee;
          PreparedStatement ps = c.prepareStatement(
            "INSERT INTO payment(PAYMENT_ID,PATIENT_ID,PATIENT_NAME,PATIENT_TYPE,PAYMENT_DATE,MODE_OF_PAYMENT,BILL_AMOUNT,DOCTOR_FEE,MEDICINE_FEES,ROOM_FEES,REGISTRATION_FEES,TOTAL_BILL) VALUES(?,?,?,?,?,?,?,?,?,?,?,?)");
          ps.setString(1, id);
          ps.setString(2, data.getOrDefault("patientId", ""));
          ps.setString(3, data.getOrDefault("patientName", ""));
          ps.setString(4, data.getOrDefault("patientType", "OutPatient"));
          ps.setString(5, data.getOrDefault("paymentDate", ""));
          ps.setString(6, data.getOrDefault("modeOfPayment", "Cash"));
          ps.setDouble(7, Double.parseDouble(data.getOrDefault("billAmount", "0")));
          ps.setDouble(8, docFee);
          ps.setDouble(9, medFee);
          ps.setDouble(10, roomFee);
          ps.setDouble(11, regFee);
          ps.setDouble(12, total);
          ps.executeUpdate();
          ps.close();
          sendJson(ex, 201, "{\"message\":\"Created\",\"id\":\"" + id + "\"}");
        } else {
          String id = path.substring(path.lastIndexOf('/') + 1);
          if (id.isEmpty()) { sendError(ex, 400, "Missing ID"); return; }
          if ("PUT".equals(method)) {
            Map<String, String> data = JsonUtil.parseJson(body(ex));
            double docFee = Double.parseDouble(data.getOrDefault("doctorFee", "0"));
            double medFee = Double.parseDouble(data.getOrDefault("medicineFees", "0"));
            double roomFee = Double.parseDouble(data.getOrDefault("roomFees", "0"));
            double regFee = Double.parseDouble(data.getOrDefault("registrationFees", "0"));
            double total = docFee + medFee + roomFee + regFee;
            PreparedStatement ps = c.prepareStatement(
              "UPDATE payment SET PATIENT_ID=?,PATIENT_NAME=?,PATIENT_TYPE=?,PAYMENT_DATE=?,MODE_OF_PAYMENT=?,BILL_AMOUNT=?,DOCTOR_FEE=?,MEDICINE_FEES=?,ROOM_FEES=?,REGISTRATION_FEES=?,TOTAL_BILL=? WHERE PAYMENT_ID=?");
            ps.setString(1, data.getOrDefault("patientId", ""));
            ps.setString(2, data.getOrDefault("patientName", ""));
            ps.setString(3, data.getOrDefault("patientType", "OutPatient"));
            ps.setString(4, data.getOrDefault("paymentDate", ""));
            ps.setString(5, data.getOrDefault("modeOfPayment", "Cash"));
            ps.setDouble(6, Double.parseDouble(data.getOrDefault("billAmount", "0")));
            ps.setDouble(7, docFee);
            ps.setDouble(8, medFee);
            ps.setDouble(9, roomFee);
            ps.setDouble(10, regFee);
            ps.setDouble(11, total);
            ps.setString(12, id);
            ps.executeUpdate();
            ps.close();
            sendJson(ex, 200, "{\"message\":\"Updated\"}");
          } else if ("DELETE".equals(method)) {
            PreparedStatement ps = c.prepareStatement("DELETE FROM payment WHERE PAYMENT_ID=?");
            ps.setString(1, id);
            ps.executeUpdate();
            ps.close();
            sendJson(ex, 200, "{\"message\":\"Deleted\"}");
          }
        }
      } catch (Exception e) {
        System.out.println("Payment API error: " + e.getMessage());
        e.printStackTrace();
        sendError(ex, 500, e.getMessage());
      }
    }
  }

  // --- Static File Handler ---
  static class StaticFileHandler implements HttpHandler {
    private final File base;

    StaticFileHandler(File base) { this.base = base; }

    @Override
    public void handle(HttpExchange ex) throws IOException {
      String path = ex.getRequestURI().getPath();
      if (path.equals("/")) path = "/index.html";
      File f = new File(base, path);
      if (!f.exists() || f.isDirectory()) {
        String msg = "Not Found";
        ex.getResponseHeaders().set("Content-Type", "text/plain");
        ex.sendResponseHeaders(404, msg.length());
        ex.getResponseBody().write(msg.getBytes());
        ex.getResponseBody().close();
        return;
      }
      String mime = Files.probeContentType(f.toPath());
      if (mime == null) mime = "application/octet-stream";
      ex.getResponseHeaders().set("Content-Type", mime);
      ex.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
      ex.sendResponseHeaders(200, f.length());
      Files.copy(f.toPath(), ex.getResponseBody());
      ex.getResponseBody().close();
    }
  }

  // --- Helpers ---
  static String generateId(Connection c, String table, String prefix) throws SQLException {
    Statement st = c.createStatement();
    ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM " + table);
    rs.next();
    int count = rs.getInt(1) + 1;
    st.close();
    return prefix + count;
  }

  static String listToJson(ResultSet rs, String[] cols, String[] dbCols) throws SQLException {
    StringBuilder sb = new StringBuilder("[");
    boolean first = true;
    while (rs.next()) {
      if (!first) sb.append(",");
      first = false;
      sb.append("{");
      for (int i = 0; i < cols.length; i++) {
        if (i > 0) sb.append(",");
        sb.append("\"").append(cols[i]).append("\":\"");
        String val = rs.getString(dbCols[i]);
        sb.append(val != null ? JsonUtil.escape(val) : "");
        sb.append("\"");
      }
      sb.append("}");
    }
    sb.append("]");
    return sb.toString();
  }
}
