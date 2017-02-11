import java.awt.Color;
import java.awt.event.*; 
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.sql.*;

public class GUI extends JFrame implements ActionListener
{
	
	private JButton buttonCourseAll, buttonCourseDetails, buttonBook;
	private JLabel label1, label2;
	private JTextField tfield1;
	private JTextArea tarea1;
	private JComboBox dropdownCourses1, dropdownCourses2; 

	DatabaseConnection dbc = new DatabaseConnection();		
	
	public GUI()
	{
		
		//available courses
		String coursesS = getCourses();
		String[] courses = coursesS.split(",");	
		
		//Create window for GUI
		setTitle("University Gym Booking Tool");
		setSize(1600,400);
		setLocation(200,150);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		//create and add buttons, text fields, text area and dropdowns
		buttonCourseAll = new JButton("View all courses");
		buttonCourseDetails = new JButton("View course details ");
		buttonBook = new JButton("Book into course");
		label1 = new JLabel("Member ID ");
		label2 = new JLabel("Course ");
		tfield1 = new JTextField(5);
		dropdownCourses1 = new JComboBox(courses);
		dropdownCourses2 = new JComboBox(courses);
		tarea1 = new JTextArea();
        
		JPanel pantop = new JPanel();
		JPanel pancenter = new JPanel();
		JPanel panbottom = new JPanel();
		
		pantop.add(label1);
		pantop.add(tfield1);
		pantop.add(label2);
		pantop.add(dropdownCourses1);
		pantop.add(buttonBook);
		pancenter.add(dropdownCourses2);
		pancenter.add(buttonCourseDetails);
		panbottom.add(buttonCourseAll);
		pancenter.add(tarea1);
		
		add(pantop, "North");
		add(pancenter, "Center");
		add(panbottom, "South");
		
		pantop.setBackground(Color.LIGHT_GRAY);
		pancenter.setBackground(Color.WHITE);
		panbottom.setBackground(Color.LIGHT_GRAY);
		
		buttonCourseAll.addActionListener(this);
		buttonCourseDetails.addActionListener(this);
		buttonBook.addActionListener(this);
		dropdownCourses1.addActionListener(this);
		dropdownCourses2.addActionListener(this);
		tfield1.addActionListener(this);
		
		setVisible(true);
		tfield1.requestFocus();
	
	}
	
 
	//handle events when user pushes button and checks input errors       
    public void actionPerformed(ActionEvent e)
    {
    	//button "View all courses"
		if (e.getSource() == buttonCourseAll)
		{
			clear(); //clears text area to make space for a new view
			viewAllCourses();
		}
		
		//button "View course details"
		else if (e.getSource() == buttonCourseDetails)
		{
			clear();
			String selectedCourseDetail = (String) dropdownCourses2.getSelectedItem(); //get selected item from dropdown
			viewCourseDetails(selectedCourseDetail);
		}
		
		//button "Book into course"
		else if (e.getSource() == buttonBook)
		{
			clear();
			//get input
			String selectedCourse = (String) dropdownCourses1.getSelectedItem(); 
			String idMString = tfield1.getText();
			int idMember = 0;
			try
			{
				idMember = Integer.parseInt(idMString);
				//check if input fields are not empty. Otherwise show error message
				if (selectedCourse.equals("Please select course") || idMString.equals(""))
				{
					tarea1.append("To book a member into a course please make sure a Member ID is entered and a course is selected.");
				}
				//check if there is free capacity in the course. Otherwise show error message
				else if (fullCourse(selectedCourse))
				{
					tarea1.append("Course is already full. Memeber couldn't be booked into course.");
				}
				//check if member isn't already on course. If yes show error message
				else if (checkDoubleEntry(selectedCourse, idMember)) 
				{
					tarea1.append("Member is already booked into course " + selectedCourse + ". No transaction.");
				}
				else if (notExisting(selectedCourse, idMember))
				{
					tarea1.append("Member doesn't exist in database. Please enter a valid Member ID.");
				}
				//else - everything is fine, book member into course
				else
				{
					bookMember(idMember, selectedCourse);
				}
			}
			catch (NumberFormatException e2)
			{
				tarea1.append("Wrong input. Please enter a number for Member ID");
			}
		}
    } 
    
    
    //SQL to see all courses	
    public void viewAllCourses()
    {
    	Connection connection = dbc.Connect();
    	Statement stmt = null;
		try
		{
			stmt = connection.createStatement();
//SQL:  SELECT "courseID", "name", "instructors"."fname", "instructors"."lname", "maxNum", (SELECT COUNT("coursebooking"."courseID") FROM coursebooking WHERE "coursebooking"."courseID" = "courses"."courseID") FROM courses INNER JOIN instructors ON "courses"."instructorID"="instructors"."instructorID";
			ResultSet rs = stmt.executeQuery("SELECT \"courseID\", \"name\", \"instructors\".\"fname\", \"instructors\".\"lname\", \"maxNum\", \"time\", \"day\", (SELECT COUNT(\"coursebooking\".\"courseName\") FROM coursebooking WHERE \"coursebooking\".\"courseName\" = \"courses\".\"name\") FROM courses INNER JOIN instructors ON \"courses\".\"instructorID\"=\"instructors\".\"instructorID\";");
			//print header and parting line
			tarea1.append("Course ID \t\t Course Name \t\t Instr FName \t\t Instr LName \t\t Max Members \t\t Booked Members \t day \t\t time");
			tarea1.append("\n-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------\n");
			
			while (rs.next())
			{
				//get and print data
				int courseID = rs.getInt(1);
				String name = rs.getString(2);
				String fname = rs.getString(3);
				String lname = rs.getString(4);
				int maxNum = rs.getInt(5);
				String time = rs.getString(6);
				String day = rs.getString(7);
				int bookedNum = rs.getInt(8);
				tarea1.append(courseID + "\t\t" + name + "\t\t" + fname + "\t\t" + lname + "\t\t" + maxNum + "\t\t" + bookedNum + "\t\t" + time + "\t\t" + day + "\n");
			}
			rs.close();
			stmt.close();
		}
		catch (SQLException e)
		{
			System.out.println("Error executing query in viewAllCourses");
			e.printStackTrace();
		}
		dbc.Close();
    }

    
//SQL to see data about a specific course    
    public void viewCourseDetails(String selectedCourse)
    {
    	Connection connection = dbc.Connect();
    	Statement stmt = null;
		try 
		{
			stmt = connection.createStatement();
			//check if a course was selected or if it still the default value "Please select course"
			if (selectedCourse.equals("Please select course"))
			{
				tarea1.append("To see the course details please select a course.");
		    }
			else 
			{
//SQL: SELECT "courseName", "members"."fname", "members"."lname", "members"."membership" FROM "coursebooking" INNER JOIN "members" ON "coursebooking"."memberID" = "members"."memberID"  WHERE "courseName" = 'Yoga';	
			ResultSet rs = stmt.executeQuery("SELECT \"courseName\", \"members\".\"memberID\", \"members\".\"fname\", \"members\".\"lname\", \"members\".\"membership\" FROM \"coursebooking\" INNER JOIN \"members\" ON \"coursebooking\".\"memberID\" = \"members\".\"memberID\"  WHERE \"courseName\" = '" + selectedCourse + "';");
			//print header and parting line
			tarea1.append("Course Name \t\t Member ID \t\t First Name \t\t Last Name \t\t Membership Type");
			tarea1.append("\n----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------\n");
			//get and print course details
			while (rs.next())
			{
				String courseName = rs.getString(1);
				int mID = rs.getInt(2);
				String fname = rs.getString(3);
				String lname = rs.getString(4);
				String membership = rs.getString(5);
				tarea1.append(courseName + "\t\t"+ mID + "\t\t" + fname + "\t\t" + lname + "\t\t" + membership + "\n");
			}
			rs.close();
			stmt.close();
			}
		}
		catch (SQLException e)
		{
			System.out.println("Error executing query in viewCourseDetails()");
			tarea1.append("Something went wrong. Please check Member ID entry and selected course are correct.");
		}
		dbc.Close();
    }
    
    
//SQL to book a member into a course
	public void bookMember(int idMember, String selectedCourse) 
	{
		Connection connection = dbc.Connect();
		Statement stmt = null;
		try 
		{
			int bookingID = getNewCbookingID();
			stmt = connection.createStatement();			
			stmt.executeUpdate("INSERT INTO \"coursebooking\" VALUES(" + bookingID + ", " + idMember + ", '"+ selectedCourse + "')");
			tarea1.append("Member with ID " + idMember + " was sucessfully booked into course " + selectedCourse + ". Booking ID of this transaction is " + bookingID + ".");
		}
		catch (SQLException e) 
		{
//			tarea1.append("Wrong Input - member wasn't booked on a course. Please make sure that the entered member ID really exists.");
			System.out.println("Error executing query while booking member into a course");
			e.printStackTrace();
		}
		dbc.Close();
	} 

	
	
	//create booking IDs: get highest existing booking ID and increase it by 1 to create the new booking ID
	public int getNewCbookingID() 
	{
		Connection connection = dbc.Connect();
		Statement stmt = null;
		int newCbookingID = 0;
		try 
		{
			stmt = connection.createStatement();
			//get max booking number and increase it by one
			ResultSet rs = stmt.executeQuery("SELECT MAX(\"cbookingID\") FROM coursebooking");
			while (rs.next()) {
				int maxCbookingID = rs.getInt(1);
				newCbookingID = maxCbookingID + 1;
			}
		}
		catch (SQLException e) 
		{
			System.out.println("Error executing query in getNewCbookingID()");
		}
		dbc.Close();
		return newCbookingID;
	}
		
		
	//check capacity of a course. True = course is full
	public boolean fullCourse(String course)
	{
//SQL : e.g. SELECT "name", "maxNum", (SELECT COUNT("coursebooking"."courseName") FROM coursebooking WHERE "coursebooking"."courseName" = "courses"."name") FROM courses WHERE "name" = 'Yoga';
		Connection connection = dbc.Connect();
		Statement stmt = null;
		boolean fullCourse = true;
		int currentBooking = 0;
		int maxBooking = 0;
		try
		{
			stmt = connection.createStatement();
			//get max booking and count how many members are already taking that course
			ResultSet rs = stmt.executeQuery("SELECT \"name\", \"maxNum\", (SELECT COUNT(\"coursebooking\".\"courseName\") FROM coursebooking WHERE \"coursebooking\".\"courseName\" = \"courses\".\"name\") FROM courses WHERE \"name\" = '" + course + "';");
			while (rs.next()) 
			{
				maxBooking = rs.getInt(2);
				currentBooking = rs.getInt(3);
			}
		}
		catch (SQLException e)
		{
			System.out.println("Error executing query in fullCourse()");
		}
		//if current booking is below max booking course isn't full --> boolean fullCourse = false
		if (currentBooking < maxBooking)
		{
			fullCourse = false;
		}
		dbc.Close();
		return fullCourse;
	}
	

	//check if member isn't already taking a course. True = double entry	
	public boolean checkDoubleEntry(String course, int idMember)
	{
//SQL : e.g. SELECT "memberID" FROM coursebooking WHERE "courseName" = 'Yoga';
		Connection connection = dbc.Connect();
		Statement stmt = null;
		boolean doubleEntry  = false;
		int memberID = 0;
		try 
		{
			stmt = connection.createStatement();
			//get all memberIDs from a the bookings of a selected course
			ResultSet rs = stmt.executeQuery("SELECT \"memberID\" FROM coursebooking WHERE \"courseName\" = '" + course + "';");
			while (rs.next())
			{
				memberID = rs.getInt(1);
			}
		}
		catch (SQLException e)
		{
			System.out.println("Error executing query in checkDoubleEntry()");
		}
		//compare if memberID from GUI matches any member IDs that are already in the booking table
		if (memberID == idMember)
		{
			doubleEntry = true;
		}
		dbc.Close();
		return doubleEntry;
	}
	
	//check if member ID exists in database	
	public boolean notExisting(String course, int idMember)
	{
//SQL : e.g. SELECT "memberID" FROM members;
		Connection connection = dbc.Connect();
		Statement stmt = null;
		boolean notExisting  = true;
		int memberID = 0;
		try 
		{
			stmt = connection.createStatement();
			//get all memberIDs
			ResultSet rs = stmt.executeQuery("SELECT \"memberID\" FROM members;");
			while (rs.next())
			{
				memberID = rs.getInt(1);
				//check if member id entered user interface exists in database
				if (idMember == memberID)
				{
					notExisting = false;
					break;
				}
				else 
				{
					notExisting = true;
				}
			}
		}
		catch (SQLException e)
		{
			System.out.println("Error executing query in checkEntry()");
		}
		dbc.Close();
		return notExisting;
	}
		
		
	//returns String of all course currently available in database. Used in the two dropdowns of the GUI
	public String getCourses() 
	{
//SQL: SELECT name FROM courses;
		String course = "";
		String allCourses = "Please select course";		
		Connection connection = dbc.Connect();
		Statement stmt = null;
		try 
		{
			stmt = connection.createStatement();
			//get all course names from the databse
			ResultSet rs = stmt.executeQuery("SELECT \"name\" FROM courses;");
			while (rs.next())
			{
				course = rs.getString(1);
				allCourses = allCourses + "," + course;
			}
		}
		catch (SQLException e) 
		{
			System.out.println("Error executing query in getCourses()");
		}
		dbc.Close();
		return allCourses;	
	}
	
	
	//clear text area by entering an empty string
	public void clear() 
	{
		tarea1.setText(" ");
	}
	
}
