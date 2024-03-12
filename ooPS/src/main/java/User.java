import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class User {
    private String name;
    private String password;
    private int userID;
    private String email;

    public User(int userID, String name, String password, String email){
        this.name = name;
        this.password = password;
        this.userID = userID;
        this.email = email;
    }

    // Getters and setters for userID, name, password, and email
    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public static User getUserByID(int userId) {
        User user = null;
        ResultSet resultSet = null;
        PreparedStatement statement = null;
        try {
            DBConnection.establishConnection();
            String sqlQuery = "SELECT * FROM user WHERE user_id = ?";
            statement = DBConnection.getConnection().prepareStatement(sqlQuery);
            statement.setInt(1, userId);
            resultSet = statement.executeQuery();
    
            if (resultSet.next()) {
                int userID = resultSet.getInt("user_id");
                String name = resultSet.getString("name");
                String password = resultSet.getString("password");
                String email = resultSet.getString("email");
                user = new User(userID, name, password, email);
            }
        } catch (SQLException | ClassNotFoundException se) {
            se.printStackTrace();
        } finally {
            try {
                if (resultSet != null) {
                    resultSet.close();
                }
                if (statement != null) {
                    statement.close();
                }
                DBConnection.closeConnection();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return user;
    }
    
    public static User login(String name, String password) {
        ResultSet resultSet = null;
        PreparedStatement statement = null;
        try {
            DBConnection.establishConnection();
            String sqlQuery = "SELECT * FROM user WHERE name = ? AND password = ?";
            statement = DBConnection.getConnection().prepareStatement(sqlQuery);
            statement.setString(1, name);
            statement.setString(2, password);
            resultSet = statement.executeQuery();
    
            if (resultSet.next()) {
                int userID = resultSet.getInt("user_id");
                String userName = resultSet.getString("name");
                String userEmail = resultSet.getString("email");
                String userPassword = resultSet.getString("password");
                String role = resultSet.getString("role");
    
                if ("ticketing officer".equals(role)) {
                    // If the role is "ticketing officer", return a TicketOfficer object
                    // Additional attributes specific to TicketOfficer may need to be retrieved
                    return new TicketOfficer(userID, userName, userPassword, userEmail);
                } else if ("event manager".equals(role)) {
                    return new EventManager(userID, userName, userPassword, userEmail);
                } else {
                    // Otherwise, return a Customer object
                    return new Customer(userID, userName, userPassword, userEmail);
                }
            } else {
                // No matching user found
                return null;
            }
        } catch (SQLException | ClassNotFoundException se) {
            se.printStackTrace();
            return null;
        } finally {
            try {
                if (resultSet != null) {
                    resultSet.close();
                }
                if (statement != null) {
                    statement.close();
                }
                DBConnection.closeConnection();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    
    public static String register(String name, String password, String email) {
        PreparedStatement checkStatement = null;
        ResultSet checkResultSet = null;
        PreparedStatement insertStatement = null;

        try {
            // Check if the username already exists
            DBConnection.establishConnection();
            String checkQuery = "SELECT * FROM user WHERE name = ?";
            checkStatement = DBConnection.getConnection().prepareStatement(checkQuery);
            checkStatement.setString(1, name);  // sets to first parameter of register(), which is name
            checkResultSet = checkStatement.executeQuery();

            if (checkResultSet.next()) {
                // Username already exists
                return "Username already exists. Please choose a different one.";
            } else {
                // Check if the email is valid
                if (!isValidEmail(email)) {
                    return "Invalid email format. Please enter a valid email address.";
                }
                // Username is available, proceed with registration
                // Insert the new user into the database, init.sql already specified default amount to be 1000, role to be default customer
                String insertQuery = "INSERT INTO user (name, password, email) VALUES (?, ?, ?)";
                insertStatement = DBConnection.getConnection().prepareStatement(insertQuery);
                insertStatement.setString(1, name);
                insertStatement.setString(2, password);
                insertStatement.setString(3, email);
                insertStatement.executeUpdate();

                return "Registered successfully!";
            }
        } catch (SQLException | ClassNotFoundException se) {
            se.printStackTrace();
            return "An error occurred while attempting to register.";
        } finally {
            try {
                if (checkResultSet != null) {
                    checkResultSet.close();
                }
                if (checkStatement != null) {
                    checkStatement.close();
                }
                if (insertStatement != null) {
                    insertStatement.close();
                }
                DBConnection.closeConnection();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static boolean isValidEmail(String email) {
        String regex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    // toString method to represent User object as string
    @Override
    public String toString() {
        return "User{" +
                "userID=" + userID +
                ", name='" + name + '\'' +
                ", password='" + password + '\'' +
                ", email='" + email + '\'' +
                '}';    
    }
}
