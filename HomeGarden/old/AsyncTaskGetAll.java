package usa.or.pdx.homegarden;

import java.sql.Statement;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.os.AsyncTask;
import android.support.v4.app.Fragment;


public class AsyncTaskGetAll extends AsyncTask<String, String, Void> {

	
	private Fragment mParentFragment;
	private String mIP;
	private int mPort;
	private Timestamp mMinTimestamp;//Only retrieve data that is older than this..
	
	private Map<Integer, List<DBData.Sample>> mData = null;
	private List<DBData.Sensor> mSensors = null;
	private List<DBData.Actuator> mActuators = null;
	
	public interface OnAsyncTaskGetAllDataListener{
		public void getAllDataStarting();
		public void getAllDataFinished(List<DBData.Actuator> actuators, List<DBData.Sensor> sensors, Map<Integer, List<DBData.Sample>> data);
		
	}
	
	AsyncTaskGetAll(Fragment fragment, String ip, int port){//, java.sql.Timestamp minTimestamp){
		mParentFragment = fragment;
		mIP = ip;
		mPort = port;
		//mMinTimestamp=minTimestamp;
	}
	
	@Override
	protected void onPreExecute() {		
		super.onPreExecute();
		((OnAsyncTaskGetAllDataListener)mParentFragment).getAllDataStarting();
		
	}

	@Override
	protected void onPostExecute(Void result) {		
		super.onPostExecute(result);
		((OnAsyncTaskGetAllDataListener)mParentFragment).getAllDataFinished(mActuators, mSensors, mData);
	}
	
	protected Void doInBackground(String... string) {
		getAllData();
		return null;
	}

	private boolean getAllData(){
		java.sql.Connection conn = DatabaseHelpers.openConnection(mIP, mPort, "DBHomeGrown", "mysql", null);
		if (conn == null)
			return false;
		
		Statement statement = DatabaseHelpers.createStatement(conn);
		mActuators = DatabaseHelpers.getAllActuators(statement);
		mSensors = DatabaseHelpers.getAllSensors(statement);
		mData = new HashMap<Integer, List<DBData.Sample>>();
		for (DBData.Sensor s : mSensors){
			List<DBData.Sample> samples = DatabaseHelpers.getAllDataBySensor(statement, s.getSensorId(), null); 
			mData.put(s.getSensorId(), samples);
		}
		DatabaseHelpers.closeConnection(conn);
		
		
		return true;
	}
	
	
}
