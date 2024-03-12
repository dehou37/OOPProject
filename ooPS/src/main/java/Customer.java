import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Customer extends User{
    // private ArrayList<Booking> bookings;  need to implement bookings class first
    private int amountAvail;
    
    public Customer(int userID, String name, String password, String email){
        super(userID, name, password, email);
    }
    public int getAmountAvail(){
        retrieveAmountAvailFromDB();
        return amountAvail;
    }

    public void setAmountAvail(int amountAvail){
        this.amountAvail = amountAvail;
    }

    public void retrieveAmountAvailFromDB() {
        try {
            DBConnection.establishConnection();
            String sqlQuery = "SELECT amount_avail FROM user WHERE user_id = ?";
            PreparedStatement statement = DBConnection.getConnection().prepareStatement(sqlQuery);
            statement.setInt(1, this.getUserID()); // Assuming user_id is the primary key
            ResultSet resultSet = statement.executeQuery();
            
            if(resultSet.next()) {
                this.setAmountAvail(resultSet.getInt("amount_avail"));
            }
            
            resultSet.close();
            statement.close();
            DBConnection.closeConnection();
        } catch (SQLException | ClassNotFoundException se) {
            se.printStackTrace();
        }
    }


    /* public ArrayList<Booking> getBookingList(){        ========== do only after bookings class is implemented========
        return 
    } */

    /* public void setAmountAvail(){            ====== will  do next time, prolly take in some args========
        this.amountAvail = amountAvail;
    } */
}
