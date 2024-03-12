import java.io.FileInputStream;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import com.google.gson.Gson;



import spark.Spark;

import static spark.Spark.*;

public class DBConnection {
    private static String MYSQL_JDBC_DRIVER_CLASS = "com.mysql.cj.jdbc.Driver";
    private static String MYSQL_DB_URL = "jdbc:mysql://localhost:3306/ticketmistress";
    private static String MYSQL_DB_USER = "root";
    private static String MYSQL_DB_USER_PASSWORD = "";

    private static Connection connection; // Declare the connection variable at the class level

    // Establish the database connection
    public static void establishConnection() throws ClassNotFoundException, SQLException {
        Class.forName(MYSQL_JDBC_DRIVER_CLASS);
        connection = DriverManager.getConnection(MYSQL_DB_URL, MYSQL_DB_USER, MYSQL_DB_USER_PASSWORD);
    }

    // Getter method to provide access to the connection
    public static Connection getConnection() {
        return connection;
    }

    // Close the database connection
    public static void closeConnection() {
        try {
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public static void main(String[] args) {

        /// ==================== Testing of User/TicketOfficer class =======================================
        System.out.println("=============================Start OF TESTING FOR USER CLASS====================");
        try {
            // Usage example: retrieve user with ID 1
            User user = User.getUserByID(1);
            User customer = null;

            if (user != null) {
                System.out.println(user.toString());    // prints object
                System.out.println(user.getName()); // prints User 1
                System.out.println(user.getUserID());   // prints 1
                System.out.println(user.getPassword());  // prints password1
                System.out.println(user.getEmail());    // prints user1@abc

                customer = User.login("user 2","password2");        //customer login, returns object
                System.out.println(User.login("user 1","password2"));            // login fail, returns null because login() returns object

                System.out.println(User.login("ticket man","password5"));     // ticket officer  login, returns user object
                System.out.println(User.register("Dehou","pwpwpw","Dehou@gmail.com"));  // Register successfully if u run the first time. Else, username exists
                System.out.println(User.register("Dehouhehexd","asd","haha"));           // invalid email


                if(customer instanceof Customer){
                    Customer c = (Customer) customer;
                    System.out.println(c.getAmountAvail());          // class cast, testing getAmountAvail() for customer 
            }
                
            } else {
                System.out.println("User not found.");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("=============================END OF TESTING FOR USER CLASS====================");
        // ====================== END OF TESTING USER CLASS ==================================
        // ======================== start testing of event manager class ====================
        System.out.println("=============================START OF TESTING FOR EVENT MANAGER CLASS===========");
        User eventManager = null;
        try {
            eventManager=User.login("event man","password4");     // event manager login, returns user object

            if (eventManager != null){
                if(eventManager instanceof EventManager){
                    EventManager em = (EventManager) eventManager;
                    String eventType = "Concert";
                    String eventName = "Taylor Swift Concert";
                    String venue = "National Stadium";
                    LocalDateTime eventDateTime = LocalDateTime.of(2024, 12, 31, 20, 0); // Example datetime
                    int numTotalTickets = 1000;
                    int numTicketsAvailable = 1000;
                    String eventDetails = "A typical  Event";
                    int ticketPrice = 90;
                    String result = em.createEvent(eventType, eventName, venue, eventDateTime, numTotalTickets, numTicketsAvailable,eventDetails,ticketPrice);          
                    System.out.println(result);            //creates new event in DB, will print "event exists" if you run it a 2nd time

                    // update taylor swift event
                    String updateResult = em.updateEvent(5, eventType,eventName, "my house", eventDateTime, numTotalTickets, 998, eventDetails, ticketPrice);
                    System.out.println(updateResult);
                  
                    /* // delete taylor swift event      ==================== uncomment this part to test delete =============
                    String deleteResult = em.deleteEvent(5);
                    System.out.println(deleteResult);  */

                    //======================================= event manager adding ticket officer============================
                    String addTicketingOfficerResult = em.addTicketingOfficer("Jeremy", "123", "jeremy@hotmail.com");
                    System.out.println(addTicketingOfficerResult);

                    // view sale statistics test, output is in readable format for testing, will amend output next time
                    System.out.println(em.viewSaleStatistics());
                }
            }

//update event parameters: int eventID, String eventName, String venue, LocalDateTime dateTime, int numTotalTickets, int numTicketsAvailable, String eventDetails, int ticketPrice

            if (eventManager instanceof EventManager){   // FOR TESTING ONLY/ can change to check instanceof Customer, it won't print "pass". 
                System.out.println("pass");            //Verifies access control, means customer wont access eventManager etc
            }
            
        } catch (Exception e){
            e.printStackTrace();
        }
        System.out.println("=============================END OF TESTING FOR EVENT MANAGER CLASS===========");
        // =========================== END TESTING OF EVENT MANAGER CLASS====================


        // =============================START OF TESTING FOR EVENT CLASS=========================================

        System.out.println("=============================START OF TESTING FOR EVENT CLASS================");
        Event testEvent =  Event.getEventByID(4);
        System.out.println(testEvent);
        System.out.println(testEvent.getEventID());
        System.out.println(testEvent.getEventType());
        System.out.println(testEvent.getEventName());
        System.out.println(testEvent.getVenue());
        System.out.println(testEvent.getEventDateTime());
        System.out.println(testEvent.getTotalTickets());
        System.out.println(testEvent.getTicketsAvailable());
        System.out.println(testEvent.getEventDetails());
        System.out.println(testEvent.getTicketPrice());

        System.out.println("------ Start of testing get Alll bookable events-------");
         ArrayList<Event> allEvents= Event.getAllBookableEvents();
        for (Event event:allEvents){
            System.out.println(event.getEventName());
            System.out.println("Number of tickets sold is " + event.numTicketsSold());
            System.out.println("Revenue is " + event.revenueEarned()+ "\n");
        }

        System.out.println("------ Start of testing get upcoming bookable events-------");
        ArrayList<Event> upcomingEvents = Event.getUpcomingEvents();
        for (Event event:upcomingEvents){
            System.out.println(event.getEventName());
        }
    
        // ==============================END  TESTING FOR EVENT CLASS =========================================

        // ============================== TESTING OF ROUTING WITH SPARK =======================================
        System.out.println("---------------------------SPARK ROUTING TEST------------------------------");
        // Set up Spark server on port 4567
        port(4567);

    // Serve static files from the resources/public folder
        staticFiles.externalLocation("src/main/resources/public");

        // Define routes
        // Serve the default login page
        get("/", (req, res) -> {
            res.redirect("/login");
            return null;
        });

        post("/login", (req, res) -> {
            System.out.println("Request: " + req.body());
        
            // Retrieve the request body as a string
            String requestBody = req.body();
        
            // Split the request body into individual parameters
            String[] params = requestBody.split("&");
        
            // Initialize variables to hold username and password
            String username = null;
            String password = null;
        
            // Loop through the parameters and extract username and password
            for (String param : params) {
                String[] keyValue = param.split("=");
                if (keyValue.length == 2) {
                    String key = keyValue[0];
                    String value = keyValue[1];
            
                    // Decode the value using URLDecoder
                    value = URLDecoder.decode(value, StandardCharsets.UTF_8.toString());
            
                    if (key.equals("username")) {
                        username = value;
                    } else if (key.equals("password")) {
                        password = value;
                    }
                }
            }
        
            // Print the retrieved username and password (for debugging purposes)
            System.out.println("Username: " + username);
            System.out.println("Password: " + password);
            
            User user = User.login(username, password);
            if (user != null) {
                // Store user information in the session
                req.session().attribute("user", user);
        
                // Redirect to the user information page
                res.redirect("/user_info");
            } else {
                // Invalid credentials, display error message
                res.status(401);
                return "Invalid username or password";
            }
            return "";
        });
        
        //Gson gson = new Gson();

         /* get("/user_info", (req, res) -> {
            // Read the HTML file content
            User user = req.session().attribute("user");

            String htmlContent = "";
            try {
                FileInputStream fileInputStream = new FileInputStream("src/main/resources/public/user_info.html");
                byte[] data = new byte[fileInputStream.available()];
                fileInputStream.read(data);
                fileInputStream.close();
                htmlContent = new String(data, StandardCharsets.UTF_8);
            } catch (IOException e) {
                e.printStackTrace();
            }
        
            // Set response type to HTML
            res.type("text/html");
        
            // Return the HTML content
            return htmlContent;
        });  */ 
        
        /* get("/user_info", (req, res) -> {
            // Retrieve user information from the session
            User user = req.session().attribute("user");
        
            // Serialize the User object to JSON
            String jsonResponse = gson.toJson(user);
        
            // Set response type to JSON
            res.type("application/json");
        
            // Return JSON response
            return jsonResponse;
        });   */

        get("/user_info", (req, res) -> {
            // Retrieve user information from the session
            User user = req.session().attribute("user");
        
            // Read the HTML file content
            String htmlContent = "";
            try {
                FileInputStream fileInputStream = new FileInputStream("src/main/resources/public/user_info.html");
                byte[] data = new byte[fileInputStream.available()];
                fileInputStream.read(data);
                fileInputStream.close();
                htmlContent = new String(data, StandardCharsets.UTF_8);
            } catch (IOException e) {
                e.printStackTrace();
            }
        
            // Inject user information into the HTML content
            htmlContent = htmlContent.replace("{{userId}}", String.valueOf(user.getUserID()));
            htmlContent = htmlContent.replace("{{userEmail}}", user.getEmail());
        
            // Set response type to HTML
            res.type("text/html");
        
            // Return the modified HTML content
            return htmlContent;
        }); 


// ...
        
        // Additional routes and testing code can go here

        // Stop Spark server when the program exits
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            closeConnection(); // Close database connection
            Spark.stop(); // Stop Spark server
        }));
        

        System.out.println("----------------------END OF PARK ROUTING TEST------------------------------");
        // ============================== END TESTING OF ROUTING WITH SPARK =======================================

    }
}