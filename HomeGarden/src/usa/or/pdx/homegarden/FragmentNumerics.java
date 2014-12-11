package usa.or.pdx.homegarden;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import usa.or.pdx.homegarden.DBData.Actuator;
import usa.or.pdx.homegarden.DBData.Sample;
import usa.or.pdx.homegarden.DBData.Sensor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;


/**
 *This class implements a ListView, where each item in the List corresponds to a sensor or actuator
 */
public class FragmentNumerics extends Fragment implements OnClickListener,
                                                          AsyncTaskGetRecentData.OnAsyncTaskGetRecentDataListener{

    private ListView mListView;
    private ImageButton btnRefreshNumerics;
    private List<NumericItem> mNumericItems;
    private NumericsArrayAdapter mNumericsAdapter;
    private ProgressBar pbNumerics;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_numerics, container, false);
		mListView = (ListView)v.findViewById(R.id.listViewNumerics);		
		btnRefreshNumerics = (ImageButton)v.findViewById(R.id.btnRefreshNumerics);
		btnRefreshNumerics.setOnClickListener(this);
		pbNumerics = (ProgressBar)v.findViewById(R.id.pbNumerics);
		pbNumerics.setVisibility(ProgressBar.INVISIBLE);

		mNumericItems = new ArrayList<NumericItem>();
		mNumericsAdapter = new NumericsArrayAdapter(getActivity(), R.layout.fragment_numeric, mNumericItems);
		mListView.setAdapter(mNumericsAdapter);
		
		onPopulateListViewFromCache();
		
		
		
		return v;
	}
    private void onPopulateListView() {
        ArrayList<String> settings = CacheHelpers.loadSettings();
		AsyncTaskGetRecentData tgrd = new AsyncTaskGetRecentData(this,  settings.get(0), Integer.parseInt(settings.get(1)));
		tgrd.execute();
	}
    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
        case R.id.btnRefreshNumerics:
            onPopulateListView();
            break;
        }
    }
    @Override
    public void getRecentDataStarting() {
        // TODO Auto-generated method stub
        if (pbNumerics != null)
            pbNumerics.setVisibility(ProgressBar.VISIBLE);
    }
    @Override
    public void getRecentDataFinished(List<Actuator> actuators,
            List<Sensor> sensors, List<DBData.Threshold> thresholds, Map<Integer, List<Sample>> data) {
        CacheHelpers.saveActuators(actuators);
        CacheHelpers.saveSensors(sensors);
        CacheHelpers.saveData(data);
        CacheHelpers.saveThresholds(thresholds);
        
        if (pbNumerics != null){
            onPopulateListViewFromCache();
            pbNumerics.setVisibility(ProgressBar.INVISIBLE);
        }
    }
    private void onPopulateListViewFromCache() {
        List<Actuator> actuators;
        List<Sensor> sensors;
        Map<Integer, List<Sample>> data;
        sensors = CacheHelpers.loadSensors();
        if (sensors == null)
            return;
        actuators = CacheHelpers.loadActuators();
        data = CacheHelpers.loadData();
        if (data == null)
            return;
        
        mNumericItems.clear();
        for (Sensor s : sensors){
            List<DBData.Sample> samples = data.get(s.getSensorId());
            if (samples.size() > 0)
                mNumericItems.add(new NumericItem(s.getSensorName(), String.valueOf(samples.get(samples.size()-1).getData())));
            else
                mNumericItems.add(new NumericItem(s.getSensorName(), "0"));
        }
        
        for (DBData.Actuator a: actuators){
            mNumericItems.add(new NumericItem(a.getActuatorName(), (a.getState()==0 ? "Off" : "On")));
        }
        mNumericsAdapter.notifyDataSetChanged();
    }

}
