package usa.or.pdx.homegarden;

import java.sql.Statement;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.os.AsyncTask;
import android.support.v4.app.Fragment;


public class AsyncTaskGetData extends AsyncTask<String, String, Void> {

	
	private Fragment mParentFragment;
	private String mIP;
	private int mPort;
	
	private Map<Integer, List<DBData.Sample>> mData;
	private List<DBData.Sensor> mSensors;
	
	public interface OnAsyncTaskGetDataListener{
		public void getDataStarting();
		public void getDataFinished(Map<Integer, List<DBData.Sample>> data);
		
	}
	
	AsyncTaskGetData(Fragment fragment, String ip, int port, List<DBData.Sensor> sensors){
		mParentFragment = fragment;
		mIP = ip;
		mPort = port;
		mSensors = sensors;
	}
	
	@Override
	protected void onPreExecute() {		
		super.onPreExecute();
		((OnAsyncTaskGetDataListener)mParentFragment).getDataStarting();
		
	}

	@Override
	protected void onPostExecute(Void result) {		
		super.onPostExecute(result);
		((OnAsyncTaskGetDataListener)mParentFragment).getDataFinished(mData);
	}
	
	protected Void doInBackground(String... string) {
		getAllSensors();
		return null;
	}

	protected void onProgressUpdate(String... s) {

	}
	private boolean getAllSensors(){
		java.sql.Connection conn = DatabaseHelpers.openConnection(mIP, mPort, "DBHomeGrown", "mysql", null);
		if (conn == null)
			return false;
		
		Statement statement = DatabaseHelpers.createStatement(conn);
		mData = new HashMap<Integer, List<DBData.Sample>>();
		for (DBData.Sensor s : mSensors){
			List<DBData.Sample> samples = DatabaseHelpers.getAllDataBySensor(statement, s.getSensorId()); 
			mData.put(s.getSensorId(), samples);
		}
		DatabaseHelpers.closeConnection(conn);
		
		
		return true;
	}
	
	
}
