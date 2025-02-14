package Database;
import Action.SQLQuery;
import Holders.*;

import java.sql.*;
import java.util.ArrayList;

public class UserManager extends DatabaseCore {

    public static ArrayList<User> getUsers(String SQLquery, String[] params){
        ArrayList<User> users = new ArrayList<>();
        try (   Connection connection = DriverManager.getConnection(databaseURL, databaseUsername,
                databasePassword);
                PreparedStatement pst = connection.prepareStatement(SQLquery)
             ){
            for(int i =0; i< params.length; i++){
                pst.setString(i+1, params[i]);
            }
            ResultSet rs = pst.executeQuery();
            while(rs.next()){
                String userName = rs.getString("UserName");
                String userLogin = rs.getString("UserLogin");
                users.add(new User(userName, userLogin));
            }
            connection.close();
            return users;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean doesUserExists(String userLogin){
        return doesSingleExist(SQLQuery.CHECK_IF_USER_EXISTS.toString(), new String[]{userLogin});
    }

    public static boolean addUser(User user, String userPassword){
        if(doesUserExists(user.getUserLogin())) {
            return false;
        }
        try ( Connection connection = DriverManager.getConnection(databaseURL, databaseUsername,
                databasePassword);
                PreparedStatement pst = connection.prepareStatement(SQLQuery.CREATE_USER.toString(),
                Statement.RETURN_GENERATED_KEYS)
        )
        {
            pst.setString(1, user.getUserName());
            pst.setString(2, user.getUserLogin());
            pst.setString(3, userPassword);

            int rowsAffected = pst.executeUpdate();

            return rowsAffected > 0;
        }catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean eraseUser(String userLogin){
        if(!doesUserExists(userLogin)) return false;

        try ( Connection connection = DriverManager.getConnection(databaseURL, databaseUsername,
                databasePassword);
              PreparedStatement pst = connection.prepareStatement(SQLQuery.DELETE_USER.toString())
        )
        {
            pst.setString(1, userLogin);
            int rowsAffected = pst.executeUpdate();

            return rowsAffected > 0;
        }catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}
