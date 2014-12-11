package usa.or.pdx.homegarden;

import java.util.ArrayList;
import java.util.List;

import usa.or.pdx.homegarden.DBData.Actuator;
import usa.or.pdx.homegarden.DBData.Sensor;
import usa.or.pdx.homegarden.DBData.Threshold;
import usa.or.pdx.homegarden.DialogModifyThreshold.DialogModifyThresholdListener;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

/**
 * This class implements a ListView where each item in the list is a threshold corresponding
 * to an indiviual actuator.
 * 
 * There will be as many items in the list, as there are the number of sensors plus the number of actuators.
 */
public class FragmentThresholds extends Fragment implements View.OnClickListener, AsyncTaskSetThresholds.OnAsyncTaskSetThresholdsListener, DialogModifyThreshold.DialogModifyThresholdListener {

    List<ThresholdItem> mThresholdItems;
    ThresholdsArrayAdapter mArrayAdapter;
    ListView mListViewThresholds;
    //Button btnThresholdsSubmit;
    int mCurrentPosition;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_thresholds, container, false);	
		//btnThresholdsSubmit = (Button)v.findViewById(R.id.btnThresholdsSubmit);
		mListViewThresholds = (ListView)v.findViewById(R.id.listViewThresholds);
		
		//btnThresholdsSubmit.setOnClickListener(this);
		
		mThresholdItems = new ArrayList<ThresholdItem>();
		mArrayAdapter = new ThresholdsArrayAdapter(getActivity(), R.layout.fragment_threshold, mThresholdItems);
		
		mListViewThresholds.setAdapter(mArrayAdapter);
		mListViewThresholds.setOnItemClickListener(new AdapterView.OnItemClickListener() {
		    public void onItemClick(AdapterView<?> parent, View view,
                    int position, long id) {

		        DialogModifyThreshold dmt = new DialogModifyThreshold();
		        dmt.show(getActivity().getSupportFragmentManager(), "boom");
		        mCurrentPosition = position;
		        
            }
        });
		populateThresholds();
		return v;
	}
	
	private void populateThresholds(){
        List<Actuator> actuators;
        List<Sensor> sensors;
        List<Threshold> thresholds;
        
        sensors = CacheHelpers.loadSensors();
        if (sensors == null)
            return;
        actuators = CacheHelpers.loadActuators();
        if (actuators == null)
            return;
        
        thresholds = CacheHelpers.loadThresholds();
        if (thresholds == null)
            return;
        
        mThresholdItems.clear();
        for (Threshold t : thresholds){
            //List<DBData.Sample> samples = data.get(s.getSensorId());
            mThresholdItems.add(new ThresholdItem(getActuatorName(actuators, t.getActuatorId()),
                                                  t.getActuatorId(),
                                                  getSensorName(sensors, t.getSensorId()),
                                                  t.getSensorId(),
                                                  t.getMin(),
                                                  t.getMax()
                                                  )
                               );
        }

        mArrayAdapter.notifyDataSetChanged();
	}
	
	private String getActuatorName(List<Actuator> actuators, int aid){
	    for (Actuator a : actuators){
	        if (a.getActuatorId() == aid)
	            return a.getActuatorName();
	    }
	    return null;
	}
	
	private String getSensorName(List<Sensor> sensors, int sid){
        for (Sensor s : sensors){
            if (s.getSensorId() == sid)
                return s.getSensorName();
        }
        return null;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
        //case R.id.btnThresholdsSubmit:

        //    break;
        }
        
    }

    @Override
    public void setThresholdsStarting() {

        
    }

    @Override
    public void setThresholdsFinished(boolean good) {

        
    }

    @Override
    public void onDialogModifyThresholdPositiveClick(DialogFragment dialog,
            int min, int max) {
        List<String> settings = CacheHelpers.loadSettings();
        List<DBData.Threshold> thresholds = CacheHelpers.loadThresholds();
        
        thresholds.get(mCurrentPosition).setMin(min);
        thresholds.get(mCurrentPosition).setMax(max);
        mThresholdItems.get(mCurrentPosition).min = min;
        mThresholdItems.get(mCurrentPosition).max = max;
        
        mArrayAdapter.notifyDataSetChanged();
        AsyncTaskSetThresholds tst = new AsyncTaskSetThresholds(this,  settings.get(0), Integer.parseInt(settings.get(1)), thresholds);
        tst.execute();
        
    }

    @Override
    public void onDialogModifyThresholdNegativeClick(DialogFragment dialog) {
        // TODO Auto-generated method stub
        
    }

}
