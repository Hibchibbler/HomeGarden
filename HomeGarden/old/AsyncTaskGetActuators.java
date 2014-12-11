package usa.or.pdx.homegarden;

import java.sql.Statement;
import java.util.List;

import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.widget.Toast;


public class AsyncTaskGetActuators extends AsyncTask<String, String, Void> {

	
	private Fragment mParentFragment;
	private String mIP;
	private int mPort;
	
	private List<DBData.Actuator> mActuators;
	
	public interface OnAsyncTaskGetActuatorsListener{
		public void getActuatorsStarting();
		public void getActuatorsFinished(List<DBData.Actuator> actuators);
	}
	
	AsyncTaskGetActuators(Fragment fragment, String ip, int port){
		mParentFragment = fragment;
		mIP = ip;
		mPort = port;
	}
	
	@Override
	protected void onPreExecute() {		
		super.onPreExecute();
		((OnAsyncTaskGetActuatorsListener)mParentFragment).getActuatorsStarting();
		
	}

	@Override
	protected void onPostExecute(Void result) {		
		super.onPostExecute(result);
		((OnAsyncTaskGetActuatorsListener)mParentFragment).getActuatorsFinished(mActuators);
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
		mActuators = DatabaseHelpers.getAllActuators(statement);
		DatabaseHelpers.closeConnection(conn);
		return true;
	}
	
	
}
