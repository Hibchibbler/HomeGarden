package usa.or.pdx.homegarden;

import android.os.AsyncTask;
import android.support.v4.app.Fragment;

/**
 * This class is responsible for asynchronously testing the connection
 * to the database using the supplied IP and port. 
 * 
 * The host object must implement the OnAsyncTaskTestConnectionListener interface.
 * The methods of the listener interface notify host if the connection test was
 * successful or not.
 */
public class AsyncTaskTestConnection extends AsyncTask<String, String, Void> {

	
	private Fragment mParentFragment;
	private String mIP;
	private int mPort;
	
	private boolean mConnectable = false;
	
	public interface OnAsyncTaskTestConnectionListener{
		public void testConnectionStarting();
		public void testConnectionFinished(boolean connectable);
	}
	
	AsyncTaskTestConnection(Fragment fragment, String ip, int port){
		mParentFragment = fragment;
		mIP = ip;
		mPort = port;
	}
	
	@Override
	protected void onPreExecute() {		
		super.onPreExecute();
		((OnAsyncTaskTestConnectionListener)mParentFragment).testConnectionStarting();
		
	}

	@Override
	protected void onPostExecute(Void result) {		
		super.onPostExecute(result);
		((OnAsyncTaskTestConnectionListener)mParentFragment).testConnectionFinished(mConnectable);
	}
	
	protected Void doInBackground(String... string) {
		mConnectable = testConnection();
		return null;
	}

	protected void onProgressUpdate(String... s) {

	}
	private boolean testConnection(){
		java.sql.Connection conn = DatabaseHelpers.openConnection(mIP, mPort, "DBHomeGrown", "mysql", null);
		if (conn == null)
			return false;
		DatabaseHelpers.closeConnection(conn);
		return true;
	}
	
	
}
