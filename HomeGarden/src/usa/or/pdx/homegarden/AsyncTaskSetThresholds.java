package usa.or.pdx.homegarden;

import java.util.List;

import android.os.AsyncTask;
import android.support.v4.app.Fragment;

/**
 * This class is responsible for asynchronously updating the Thresholds
 * in the database
 * 
 * The host object must implement the OnAsyncTaskSetThresholdsListener interface.
 * The methods of the listener interface notify host if when the update is starting
 * and when the update finished.
 */
public class AsyncTaskSetThresholds extends AsyncTask<String, String, Void> {

	
	private Fragment mParentFragment;
	private String mIP;
	private int mPort;
	private List<DBData.Threshold> mThresholds;
	
	private boolean mGood = false;
	
	public interface OnAsyncTaskSetThresholdsListener{
		public void setThresholdsStarting();
		public void setThresholdsFinished(boolean good);
	}
	
	AsyncTaskSetThresholds(Fragment fragment, String ip, int port, List<DBData.Threshold> thresholds){
		mParentFragment = fragment;
		mIP = ip;
		mPort = port;
		mThresholds = thresholds;
	}
	
	@Override
	protected void onPreExecute() {		
		super.onPreExecute();
		((OnAsyncTaskSetThresholdsListener)mParentFragment).setThresholdsStarting();
		
	}

	@Override
	protected void onPostExecute(Void result) {		
		super.onPostExecute(result);
		((OnAsyncTaskSetThresholdsListener)mParentFragment).setThresholdsFinished(mGood);
	}
	
	protected Void doInBackground(String... string) {
	    mGood = setThresholds();
		return null;
	}

	protected void onProgressUpdate(String... s) {

	}
	/**
	 * updates the database with new threshold values
	 */
	private boolean setThresholds(){
		java.sql.Connection conn = DatabaseHelpers.openConnection(mIP, mPort, "DBHomeGrown", "mysql", null);
		if (conn == null){
			return false;
		}
		
		CacheHelpers.saveThresholds(mThresholds);
		DatabaseHelpers.updateThresholds(conn, mThresholds);
		DatabaseHelpers.closeConnection(conn);
		return true;
	}
	
	
}
