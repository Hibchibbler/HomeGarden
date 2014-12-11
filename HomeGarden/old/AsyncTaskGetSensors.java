package usa.or.pdx.homegarden;

import java.sql.Statement;
import java.util.List;

import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.widget.Toast;


public class AsyncTaskGetSensors extends AsyncTask<String, String, Void> {

	
	private Fragment mParentFragment;
	private String mIP;
	private int mPort;
	
	private List<DBData.Sensor> mSensors;
	
	public interface OnAsyncTaskGetSensorsListener{
		public void getSensorsStarting();
		public void getSensorsFinished(List<DBData.Sensor> sensors);
	}
	
	AsyncTaskGetSensors(Fragment fragment, String ip, int port){
		mParentFragment = fragment;
		mIP = ip;
		mPort = port;
	}
	
	@Override
	protected void onPreExecute() {		
		super.onPreExecute();
		((OnAsyncTaskGetSensorsListener)mParentFragment).getSensorsStarting();
		
	}

	@Override
	protected void onPostExecute(Void result) {		
		super.onPostExecute(result);
		((OnAsyncTaskGetSensorsListener)mParentFragment).getSensorsFinished(mSensors);
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
		mSensors = DatabaseHelpers.getAllSensors(statement);
		DatabaseHelpers.closeConnection(conn);
		return true;
	}
	
	
}
