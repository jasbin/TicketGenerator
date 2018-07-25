package victory;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DBQuery {

	public static void ticketDB() {

		Connection con = DBHelper.getConnection();
		try {
			Statement stmt = con.createStatement();
			String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS ticket( ticket_id INT(6) UNSIGNED AUTO_INCREMENT PRIMARY KEY,flag boolean not null default 0)";
			stmt.execute(CREATE_TABLE);
			System.out.println("TABLE_CREATED");
			con.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("ERROR");
		}
	}

	public static boolean insertRecord() {
		Connection con = DBHelper.getConnection();
		
		
		try {
			Statement stmt = con.createStatement();
			String INSERT_RECORD = "INSERT INTO ticket(flag) VALUES(0)";
			stmt.executeUpdate(INSERT_RECORD);
			System.out.println("TABLE_CREATED");
			con.close();
			return true;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			
			System.out.println("ERROR");
			return false;
		}
	}


	public static int getMaxTicketID() {
		Connection con = DBHelper.getConnection();
		int maxID = 0;
		try {
			String query = "SELECT MAX(ticket_id) as id FROM ticket";
			Statement st = con.createStatement();
			ResultSet rs = st.executeQuery(query);
			while(rs.next()) {
				maxID = rs.getInt(1);
			}
			return maxID;
		}catch(Exception ex) {
			// TODO Auto-generated catch block
						ex.printStackTrace();

		}
		return 0;
		
	}

}
