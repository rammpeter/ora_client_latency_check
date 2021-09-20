// Test zur Ermittlung der Latenz bei wiederholter Ausfuehrung eines einfachen SQLs
// Ronny Wels, Peter Ramm, 23.03.2017 OSP Dresden
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.PreparedStatement;
 
public class DBConnectionSpeedTest
{
    // JDBC driver name and database URL
    private static final String DB_URL = "jdbc:oracle:thin:noa/noa@ramm.osp-dd.de:1521:RAMM";
    //private static final String DB_URL = "jdbc:oracle:thin:noalyze/noalyze@dm04-scan.puc.ov.otto.de:1521/NOADB_PROCESS";
    //private static final String DB_URL = "jdbc:oracle:thin:noalyze/noalyze@dm10-scan.ov.otto.de:1521/KNOTEN1";
    private static final String QUERY = "SELECT 2 FROM Dual";
    
    public static void main(String[] args) {
           
    	int requestCounts = args.length > 0 ? Integer.parseInt(args[0]) : 1;
    	
    	//System.out.println("Query : " + QUERY);
    	//System.out.println("DB_URL: " + DB_URL);
    	
    	try {
    	    Connection conn = null;  
	    long timeStart = 0;
	    long timeEnd = 0;
	    PreparedStatement stmt = null;
	    @SuppressWarnings("unused")
	    int iresult = 0;
	    String prepStmt = QUERY; 
            
	    // create the connection object  
            conn = DriverManager.getConnection(DB_URL);  	
		    
            //System.out.println("Connected to DB");
  	   
	    DatabaseMetaData dbmd = conn.getMetaData();
	    //System.out.println("Driver Name: "+dbmd.getDriverName());  
	    //System.out.println("Driver Version: "+dbmd.getDriverVersion());  
	    //System.out.println("UserName: "+dbmd.getUserName());  
	    //System.out.println("Database Product Name: "+dbmd.getDatabaseProductName());  
	    //System.out.println("Database Product Version: "+dbmd.getDatabaseProductVersion()); 
	    //System.out.println("\n-------------------------------------------------------------\n");
		    
	    // prepare
	    stmt = conn.prepareStatement(prepStmt);

            // Execute one time to parse SQL and open cursor
    	    ResultSet firstResultSet = stmt.executeQuery(prepStmt); 
    	    while(firstResultSet.next()) {
    	       iresult = firstResultSet.getInt("2");
    	    }	    
    	    firstResultSet.close();
		    
	    /*** start measurement ***/
	    timeStart = System.currentTimeMillis();   
	    for(int request = 0; request < requestCounts; request++) {    	        
    	       ResultSet resultSet = stmt.executeQuery(prepStmt); 
    	       
    	       while(resultSet.next()) {
    	           iresult = resultSet.getInt("2");
    	       }	    
    	   
    	       resultSet.close();
	    }	        
	    timeEnd = System.currentTimeMillis(); 
	    /*** end time, disconnect to db ***/ 
		    
	    stmt.close(); // close PrepStmt
	    conn.close(); // disconnect from db
		    
	    //System.out.println("Total time          : " + (timeEnd - timeStart) + " ms");
	    //System.out.println("Time per exec       : " + (float)(timeEnd - timeStart)/requestCounts + " ms");
	    System.out.println((float)(timeEnd - timeStart)/requestCounts + " ms");
	    //System.out.println("Number of executions: " + requestCounts);
	    //System.out.println("Resultset: " + iresult);
    		  
    	} catch(SQLException sqle) {
    		sqle.printStackTrace();
    	} catch(Exception e) {
    		e.printStackTrace();
     	}      
    }  
}
