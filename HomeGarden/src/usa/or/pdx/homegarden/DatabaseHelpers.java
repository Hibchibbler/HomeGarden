package usa.or.pdx.homegarden;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import usa.or.pdx.homegarden.DBData.Threshold;
import android.util.Log;


/**
 * This class provides a simple and clean api to interact with the database.
 * Most of these methods are called from within an AsyncTask
 * 
 * Currently, we do not make, create, delete tables.
 * There is a lot of room for SQL optimization, like
 * joins.
 *
 */
public class DatabaseHelpers {
	// Logcat tag
	private static final String LOG = "DatabaseHelper";

	// Database Version
	//private static final int    DATABASE_VERSION 		= 1;

	// Database Name
	//private static final String DATABASE_NAME 		= "DBHomeGrown";

	// Table Names
	private static final String TABLE_SENSORS 		= "sensors";
	private static final String TABLE_ACTUATORS		= "actuators";
	private static final String TABLE_DATA			= "data";
	private static final String TABLE_THRESHOLDS    = "thresholds";

	// Primary Keys
	private static final String SENSOR_ID			= "sensor_id";
	private static final String ACTUATOR_ID			= "actuator_id";
	private static final String THRESHOLD_ID        = "threshold_id";
	private static final String DATA_ID             = "data_id";
	private static final String CREATED_AT			= "created_at";
	
	//SENSORS specific - column nmaes
	private static final String SENSOR_NAME			= "sensor_name";
	private static final String SAMPLE_SIZE			= "sample_size";
	private static final String SAMPLE_RATE			= "sample_rate";

	// ACTUATORS specific - column names
	private static final String ACTUATOR_NAME		= "actuator_name";
	private static final String STATE               = "state";
	private static final String OVERRIDE            = "override";
	
	// THRESHOLDS specific - column names
	private static final String MIN                 = "min";
	private static final String MAX                 = "max";
	// DATA specific - column names	
	private static final String DATA_DATA			= "data";

	// Table Create Statements
	// Sensors table create statement
	private static final String CREATE_TABLE_SENSORS 
		= "CREATE TABLE "
		+ TABLE_SENSORS 
		+ "(" 
		+ SENSOR_ID + " INTEGER PRIMARY KEY," 
		+ SENSOR_NAME + " TEXT," 
		+ SAMPLE_SIZE + " INTEGER,"
		+ SAMPLE_RATE + " INTEGER,"
		+ CREATED_AT  + " DATETIME" 
		+ ")";

	// Tag table create statement
	private static final String CREATE_TABLE_ACTUATOR 
		= "CREATE TABLE " 
		+ TABLE_ACTUATORS 
		+ "(" 
		+ ACTUATOR_ID + " INTEGER PRIMARY KEY," 
		+ ACTUATOR_NAME + " TEXT," 
		+ STATE + " INTEGER,"
		+ OVERRIDE + " INTEGER,"
		+ CREATED_AT + " DATETIME"
		+ ")";

	// todo_tag table create statement
	private static final String CREATE_TABLE_DATA 
		= "CREATE TABLE "
		+ TABLE_DATA 
		+ "(" 
		+ DATA_ID + " INTEGER AUTO_INCREMENT PRIMARY KEY,"
		+ SENSOR_ID + " INTEGER,"
		+ DATA_DATA + " INTEGER," 
		+ CREATED_AT + " DATETIME,"
		+ "FOREIGN KEY " + SENSOR_ID + " REFERENCES " + TABLE_SENSORS +"("+ SENSOR_ID +")"
		+ ")";
	
	private static final String CREATE_TABLE_THRESHOLDS 
       = "CREATE TABLE "
       + TABLE_THRESHOLDS 
       + "(" 
       + THRESHOLD_ID + " INTEGER AUTO_INCREMENT PRIMARY KEY,"
       + ACTUATOR_ID + " INTEGER,"
       + SENSOR_ID + " INTEGER,"
       + MIN + " INTEGER,"
       + MAX + " INTEGER,"
       + CREATED_AT + " DATETIME,"
       + "FOREIGN KEY " + ACTUATOR_ID + " REFERENCES " + TABLE_ACTUATORS +"("+ ACTUATOR_ID +")"
       + ")";
	
	public static java.sql.Statement createStatement(java.sql.Connection conn){
		try {
			return conn.createStatement();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public static java.sql.Connection openConnection(String ip, Integer port, String dbname, String user, String pass){
		java.sql.Connection conn = null;
		
		try {
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			conn = DriverManager.getConnection("jdbc:mysql://"+ ip +":"+ port +"/"+ dbname, 
												user,
												pass);
		}		
		catch (ClassNotFoundException e) {
			//publishProgress("TestConnection Exception: " + e.getMessage());
			e.printStackTrace();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e){
			
		}
		
		return conn;
	}
	
	public static void closeConnection(java.sql.Connection conn){
		try {
			conn.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	



		public static void createTables(java.sql.Statement transmission) {

			// creating required tables
			try{
			    transmission.executeQuery(CREATE_TABLE_SENSORS);
				transmission.executeQuery(CREATE_TABLE_ACTUATOR);
				transmission.executeQuery(CREATE_TABLE_DATA);				
				transmission.executeQuery(CREATE_TABLE_THRESHOLDS);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}


		public static void dropTables(java.sql.Statement transmission) {
			// on upgrade drop older tables
			try{
			    transmission.executeQuery("DROP TABLE IF EXISTS " + TABLE_SENSORS);
			    transmission.executeQuery("DROP TABLE IF EXISTS " + TABLE_ACTUATORS);
				transmission.executeQuery("DROP TABLE IF EXISTS " + TABLE_DATA);				
				transmission.executeQuery("DROP TABLE IF EXISTS " + TABLE_THRESHOLDS);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}

		public static List<DBData.Sample> getAllDataBySensor(java.sql.Statement transmission,int sensor_id, Timestamp lastTimestamp) {

			String selectQuery;
			if (lastTimestamp != null){
			    selectQuery = "SELECT * FROM " + TABLE_DATA + " WHERE " + SENSOR_ID + " = " + sensor_id + " AND " + CREATED_AT + " > STR_TO_DATE('"+ lastTimestamp + "', '%Y-%m-%d %H:%i:%s') ORDER BY " + DATA_ID + " ASC";
			}else{
			    selectQuery = "SELECT * FROM " + TABLE_DATA + " WHERE " + SENSOR_ID + " = " + sensor_id + " ORDER BY " + DATA_ID + " ASC";
			}

			Log.e(LOG, selectQuery);
			
			List<DBData.Sample> samples = new ArrayList<DBData.Sample>(); 
			try{
				java.sql.ResultSet selectResult;
				selectResult=  transmission.executeQuery(selectQuery);

				if (selectResult != null){
					//it can not be more than row
					while (selectResult.next()) {
						DBData.Sample S = new DBData.Sample();
						S.setDataId(selectResult.getInt(DATA_ID));
						S.setSensorId(selectResult.getInt(SENSOR_ID));
						S.setData(selectResult.getInt(DATA_DATA));	
						S.setTimestamp(selectResult.getTimestamp(CREATED_AT));
						samples.add(S);
					}
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			return samples;
		}

		/**sensors
		 * */
		public static List<DBData.Sensor> getAllSensors(java.sql.Statement transmission) {
			List<DBData.Sensor> sensors = null;
			String selectQuery = "SELECT * FROM " + TABLE_SENSORS;

			Log.e(LOG, selectQuery);

			try{
				java.sql.ResultSet selectResult;
				selectResult= transmission.executeQuery(selectQuery);

				if (selectResult != null){
					//it can not be more than row
				    sensors = new ArrayList<DBData.Sensor>();
					while (selectResult.next()) {

						DBData.Sensor S=new DBData.Sensor();
						S.setSensorId(selectResult.getInt(SENSOR_ID));
						S.setSensorName(selectResult.getString(SENSOR_NAME));
						S.setSampleSize(selectResult.getInt(SAMPLE_SIZE));
						S.setSampleRate(selectResult.getInt(SAMPLE_RATE));
						S.setTimestamp(selectResult.getTimestamp(CREATED_AT));
						sensors.add(S);
					}
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			

			return sensors;
		}



		/**
		 * 
		 * */
		public static List<DBData.Actuator> getAllActuators(java.sql.Statement transmission) {
			List<DBData.Actuator> actuators = new ArrayList<DBData.Actuator>();
			String selectQuery = "SELECT * FROM " + TABLE_ACTUATORS;

			Log.e(LOG, selectQuery);

			try{
				java.sql.ResultSet selectResult;
				selectResult= transmission.executeQuery(selectQuery);

				if (selectResult != null){
					//it can not be more than row
					while (selectResult.next()) {

						DBData.Actuator A=new DBData.Actuator();
						A.setActuatorId(selectResult.getInt(ACTUATOR_ID));
						A.setActuatorName(selectResult.getString(ACTUATOR_NAME));
						A.setState(selectResult.getInt(STATE));
						A.setOverride(selectResult.getInt(OVERRIDE));
						A.setTimestamp(selectResult.getTimestamp(CREATED_AT));
						actuators.add(A);
					}
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			

			return actuators;
		}

        /**
         * 
         * */
        public static List<DBData.Threshold> getAllThresholds(java.sql.Statement transmission) {
            List<DBData.Threshold> thresholds = new ArrayList<DBData.Threshold>();
            String selectQuery = "SELECT * FROM " + TABLE_THRESHOLDS;

            Log.e(LOG, selectQuery);

            try{
                java.sql.ResultSet selectResult;
                selectResult= transmission.executeQuery(selectQuery);

                if (selectResult != null){
                    //it can not be more than row
                    while (selectResult.next()) {

                        DBData.Threshold T=new DBData.Threshold();
                        T.setThresholdId(selectResult.getInt(THRESHOLD_ID));
                        T.setActuatorId(selectResult.getInt(ACTUATOR_ID));
                        T.setSensorId(selectResult.getInt(SENSOR_ID));
                        T.setMin(selectResult.getInt(MIN));
                        T.setMax(selectResult.getInt(MAX));
                        T.setTimestamp(selectResult.getTimestamp(CREATED_AT));
                        thresholds.add(T);
                    }
                }
            } catch (SQLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            

            return thresholds;
        }
	
      /*
      * Insert into Actuators Table
      */
     public static void updateThresholds(Connection conn,List<DBData.Threshold> thresholds) {
         for (Threshold t : thresholds){
             String query = "UPDATE thresholds SET min=" + t.getMin() +", max=" + t.getMax() + " WHERE actuator_id=" + t.getActuatorId();    
             try {
                 Statement transmission = conn.createStatement();
                 transmission.executeUpdate(query);
             } catch (SQLException e) {
                 // TODO Auto-generated catch block
                 e.printStackTrace();
             }
         }
         
     }
     
     /*
      * Insert into Actuators Table
      */
     public static void deleteData(Connection conn) {

         String query = "DELETE FROM data";    
         try {
             Statement transmission = conn.createStatement();
             transmission.executeQuery(query);
         } catch (SQLException e) {
             // TODO Auto-generated catch block
             e.printStackTrace();
         }

         
     }
}
