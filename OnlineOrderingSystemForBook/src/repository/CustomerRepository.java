package repository;

import database.DataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class CustomerRepository {
    public boolean checkUserExists(String uid) {
        try {

            String sql = "SELECT * FROM Customer WHERE Uid=?";

            Connection conn = DataSource.getConnection();
            PreparedStatement statement = conn.prepareStatement(sql);
            statement.setString(1, uid);
            ResultSet rs = statement.executeQuery();

            int matchedUsers = 0;
            while (rs.next()) {
                matchedUsers += 1;
            }

            return matchedUsers > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
