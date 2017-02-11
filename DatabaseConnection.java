import java.sql.*;

public class DatabaseConnection 
{
	private Connection connection =null;
	
	public DatabaseConnection()
	{
	}
	
	public Connection Connect()
	{
		String dbname = "gym";
		String username = "postgres";
		String password = "2281380g";
		
		try
		{
		connection =
		DriverManager.getConnection("jdbc:postgresql://localhost:5432/" + dbname,username, password);
		}
		catch (SQLException e)
		{
		System.err.println("Connection Failed!");
		e.printStackTrace();
		
		}
		if (connection != null)
		{
		System.out.println("Connection successful");
		}
		else
		{
		System.err.println("Failed to make connection!");
		}
		return connection;
	}
	
	
	public void Close()
	{
		try
		{
		connection.close();
		System.out.println("Connection closed");
		}
		catch (SQLException e)
		{
		e.printStackTrace();
		System.out.println("Connection could not be closed – SQL exception");
		}		
	}
	
	
	
//	public void executeQuery() {
		
//		Statement stmt = null;
//		String query = " SELECT * FROM Courses";
//		try {
//		 stmt = connection.createStatement();
//		 ResultSet rs = stmt.executeQuery(query);
		//the next method of ResultSet allows you to iterate through the results
//		 while (rs.next())
//		 {
		// the getString method of the ResultSet object allows you to access the value for the given column name for the current row in the result set as a String. If the value is an integer you can use getInt(“col_name”)
//		 String instructors_instructorID = rs.getString("instructorID");
//		 System.out.println(instructors_instructorID);
//		 }
//		}
//		catch (SQLException e)
//		{
//		e.printStackTrace();
//		System.err.println("error executing query " + query);
//		}		
//	}
	
//	public void insertInstructors(int id, String fname, String lname, String email, String skill){
//		
//		Statement stmt = null;
//		try
//		{
//			stmt = connection.createStatement();
//			stmt.execute("INSERT INTO instructors VALUES (" + id + ", '" + fname + "', '" + lname + "', '" + email + "', '" + skill + "')");
//			stmt.close();
//		}
//		catch (SQLException e)
//		{
//			System.err.println("error executing inert query ");
//		}
//	}
	
	
// http://www.homeandlearn.co.uk/java/databases_and_java_forms.html
//	public void selectCourses() {
//		Statement stmt = null;
//		try
//		{
//			stmt = connection.createStatement();
//			ResultSet rs = stmt.executeQuery("SELECT * FROM Courses");
//			ResultSetMetaData rsmd = rs.getMetaData();
//			System.out.println(rs.getMetaData());
//			int numberCols = rsmd.getColumnCount();
//			for (int i = 1; i <= numberCols; i++)
//			{
//				//print column names
//				System.out.print(rsmd.getColumnLabel(i) + "\t\t");
//			}
//			System.err.println("\n-------------------------");
//			
//			while (rs.next())
//			{
//				int id = rs.getInt(1);
//				String fname = rs.getString(2);
//				String lname = rs.getString(3);
//				String cost = rs.getString(4);
//				int instructornumber = rs.getInt(5);
//				String data = id + "\t\t" + fname + "\t\t" + lname + "\t\t" + cost + "\t\t" + instructornumber;
//				System.err.println(data);
//			}
//			rs.close();
//			stmt.close();
//		}
//		catch (SQLException e)
//		{
//			System.out.println("error executin query in selectCourses");
//		}
//	}
	
//	public void executeUpdate() {
		
//		Statement stmt = null;
//		String query = "INSERT INTO facilities VALUES('Gym')"; 
//		try {
//		 stmt = connection.createStatement();
//		 int result = stmt.executeUpdate(query);
//		 System.out.println("Query successfull");
//		}
//		catch (SQLException e )
//		{
//		e.printStackTrace();
//		System.err.println("error executing query " + query);
//		}
//	} // close executeUpdate()
	

}
