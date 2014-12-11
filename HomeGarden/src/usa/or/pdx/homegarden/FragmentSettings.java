package usa.or.pdx.homegarden;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import usa.or.pdx.homegarden.DBData.Actuator;
import usa.or.pdx.homegarden.DBData.Sample;
import usa.or.pdx.homegarden.DBData.Sensor;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

/**
 *This class implements the functionality to initiate saving settings, 
 *clear cache, synchronizing cache, and testing the DB connection
 */
public class FragmentSettings extends Fragment implements OnClickListener,
                                                          AsyncTaskTestConnection.OnAsyncTaskTestConnectionListener,
                                                          AsyncTaskGetRecentData.OnAsyncTaskGetRecentDataListener{

    Button btnTestConnection, btnSaveSettings, btnSynchronizeCache, btnClearCache;
    TextView textIP, textPort;
    LinearLayout laySettings;
    ProgressBar mProgressBar;
    List<DBData.Sensor> mSensors;

    public interface OnFragmentSettingsListener{
        public void onGetSensors(List<DBData.Sensor> sensors);
        public void onGetActuators(List<DBData.Actuator> actuators); 
        public void onGetData(Map<Integer, List<DBData.Sample>> data);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_settings, container, false);

        btnTestConnection = (Button)v.findViewById(R.id.btnTestConnection);
        btnSaveSettings = (Button)v.findViewById(R.id.btnSaveSettings);
        btnSynchronizeCache = (Button)v.findViewById(R.id.btnSynchronizeCache);
        btnClearCache = (Button)v.findViewById(R.id.btnClearCache);
        
        textIP = (TextView)v.findViewById(R.id.textIP);
        textPort = (TextView)v.findViewById(R.id.textPort);
        laySettings = (LinearLayout)v.findViewById(R.id.laySettings);
        mProgressBar = (ProgressBar)v.findViewById(R.id.progressBar1);

        btnTestConnection.setOnClickListener(this);
        btnSaveSettings.setOnClickListener(this);
        btnSynchronizeCache.setOnClickListener(this);
        btnClearCache.setOnClickListener(this);
        
        mProgressBar.setVisibility(View.INVISIBLE);

        if (savedInstanceState == null){
            File file = new File(Environment.getExternalStorageDirectory(), CacheHelpers.CACHE_SETTINGS_FILE_NAME );
            if (file.exists()) {								
                onLoadSettings(v);
            }else{

            }
        }

        return v;
    }

    public void onClick(View v){
        switch (v.getId())
        {
        case R.id.btnTestConnection:
            onTestConnection(v);
            break;
        case R.id.btnSaveSettings:
            onSaveSettings(v);
            break;
        case R.id.btnSynchronizeCache:
            onSynchronizeCache(v);
            break;
        case R.id.btnClearCache:
            onClearCache(v);
            break;
        }
    }

    public void onTestConnection(View v){
        String port = textPort.getText().toString();
        AsyncTaskTestConnection tc = new AsyncTaskTestConnection(this,
                textIP.getText().toString(), 
                Integer.parseInt(port));
        tc.execute();
    }

    public void onClearCache(View v){
        CacheHelpers.clearCache();
        Toast.makeText(v.getContext(),"Cleared Cache!", Toast.LENGTH_SHORT).show();
    }
    
    public void onSaveSettings(View v){

        try {
            CacheHelpers.saveSettings(textIP.getText().toString(), Integer.parseInt(textPort.getText().toString()));
            Toast.makeText(v.getContext(),"Saved settings", Toast.LENGTH_SHORT).show();
        }
        catch (Exception e)
        {
            Toast.makeText(v.getContext(), e.getMessage(),Toast.LENGTH_SHORT).show();
        }
    }

    public void onLoadSettings(View v){

        try {
            ArrayList<String> settings = CacheHelpers.loadSettings();
            textIP.setText(settings.get(0));
            textPort.setText(settings.get(1));
            Toast.makeText(v.getContext(),"Loaded settings", Toast.LENGTH_SHORT).show();
        } 
        catch (Exception e) 
        {
            Toast.makeText(v.getContext(), e.getMessage(),Toast.LENGTH_SHORT).show();
        }
    }

    public void onSynchronizeCache(View v){

        AsyncTaskGetRecentData tgrd = new AsyncTaskGetRecentData(this,
                textIP.getText().toString(), 
                Integer.parseInt(textPort.getText().toString()));
        tgrd.execute();
    }	

    @Override
    public void getRecentDataStarting() {
        mProgressBar.setVisibility(View.VISIBLE);		
    }

    @Override
    public void getRecentDataFinished(List<Actuator> actuators,
            List<Sensor> sensors, List<DBData.Threshold> thresholds, Map<Integer, List<Sample>> data) {
        mProgressBar.setVisibility(View.INVISIBLE);

        CacheHelpers.saveActuators(actuators);
        CacheHelpers.saveSensors(sensors);
        CacheHelpers.saveData(data);
        CacheHelpers.saveThresholds(thresholds);
        
        Toast.makeText(getActivity(), "Cache Updated!", Toast.LENGTH_LONG).show();
    }

    @Override
    public void testConnectionStarting() {
        mProgressBar.setVisibility(View.VISIBLE);		
    }

    @Override
    public void testConnectionFinished(boolean connectable) {
        mProgressBar.setVisibility(View.INVISIBLE);
        if (connectable)
            Toast.makeText(getActivity(), "Connection Suceeded!", Toast.LENGTH_LONG).show();
        else
            Toast.makeText(getActivity(), "Connection Failed!", Toast.LENGTH_LONG).show();
    }
}
