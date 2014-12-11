package usa.or.pdx.homegarden;

import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;

/**
 * This class is responsible for asynchronously retrieving all data
 * from the database. This includes Sensors, Actuators, Thresholds, and Data
 * 
 * The host object must implement the OnAsyncTaskGetRecentDataListener interface.
 * The methods of the listener interface gives the fetched data to the host.
 */
public class AsyncTaskGetRecentData extends AsyncTask<String, String, Void> {


    private Fragment mParentFragment;
    private String mIP;
    private int mPort;

    private Map<Integer, List<DBData.Sample>> mData = null;
    private List<DBData.Sensor> mSensors = null;
    private List<DBData.Actuator> mActuators = null;
    private List<DBData.Threshold> mThresholds = null;

    public interface OnAsyncTaskGetRecentDataListener{
        public void getRecentDataStarting();
        public void getRecentDataFinished(List<DBData.Actuator> actuators, List<DBData.Sensor> sensors, List<DBData.Threshold> thresholds, Map<Integer, List<DBData.Sample>> data);

    }

    AsyncTaskGetRecentData(Fragment fragment, String ip, int port){
        mParentFragment = fragment;
        mIP = ip;
        mPort = port;
    }

    @Override
    protected void onPreExecute() {		
        super.onPreExecute();
        if (mParentFragment != null)
            ((OnAsyncTaskGetRecentDataListener)mParentFragment).getRecentDataStarting();

    }

    @Override
    protected void onPostExecute(Void result) {		
        super.onPostExecute(result);
        if (mParentFragment != null)
            ((OnAsyncTaskGetRecentDataListener)mParentFragment).getRecentDataFinished(mActuators, mSensors, mThresholds, mData);
    }

    protected Void doInBackground(String... string) {
        getRecentData();
        return null;
    }

    /**
     * gets data that is newer than a previously saved timestamp. 
     * If a timestamp is not found, it gets ALL data.
     * Timestamps are stored in the cache.
     */
    @SuppressLint("UseSparseArrays")
    private boolean getRecentData(){
        java.sql.Connection conn = DatabaseHelpers.openConnection(mIP, mPort, "DBHomeGrown", "mysql", null);
        if (conn == null)
            return false;

        Statement statement = DatabaseHelpers.createStatement(conn);
        mActuators = DatabaseHelpers.getAllActuators(statement);
        mSensors = DatabaseHelpers.getAllSensors(statement);
        mThresholds = DatabaseHelpers.getAllThresholds(statement);
        mData = CacheHelpers.loadData();
        if (mData == null)
            mData = new HashMap<Integer, List<DBData.Sample>>();
        
        Map<Integer, Timestamp> lastTimestamps = CacheHelpers.loadLastDataTimestamps();
        if (lastTimestamps != null){
            for (Entry<Integer, Timestamp> entry : lastTimestamps.entrySet()){
                List<DBData.Sample> newSamples = DatabaseHelpers.getAllDataBySensor(statement, entry.getKey(), entry.getValue());
                
                List<DBData.Sample> allSamples = mData.get(entry.getKey());
                if (allSamples == null)
                    allSamples = new ArrayList<DBData.Sample>();
                allSamples.addAll(newSamples);

                mData.put(entry.getKey(), allSamples);
            }
        }else{
            for (DBData.Sensor s : mSensors){
                List<DBData.Sample> samples = DatabaseHelpers.getAllDataBySensor(statement, s.getSensorId(), null); 
                mData.put(s.getSensorId(), samples);
            }
        }
        DatabaseHelpers.closeConnection(conn);


        return true;
    }


}
