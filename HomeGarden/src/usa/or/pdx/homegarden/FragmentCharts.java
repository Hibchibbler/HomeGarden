package usa.or.pdx.homegarden;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import usa.or.pdx.homegarden.DBData.Actuator;
import usa.or.pdx.homegarden.DBData.Sample;
import usa.or.pdx.homegarden.DBData.Sensor;
import usa.or.pdx.homegarden.FragmentChart.OnFragmentChartListener;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;

/**
 * This class implements a ViewPager that contains all the charts. 
 * The catch, is that each chart is a fragment. So each chart is a nested fragment. Because
 * it's parent is this fragment.
 */
public class FragmentCharts extends Fragment implements OnFragmentChartListener, View.OnClickListener, AsyncTaskGetRecentData.OnAsyncTaskGetRecentDataListener{
	private ViewPager mViewPagerCharts;
	private ImageButton btnRefreshCharts;
	private ProgressBar pbCharts;
	
	private MyFragPageAdapter adapt;
	public interface OnFragmentChartsListener{
		public Map<Integer, List<DBData.Sample>> onGetDataMap();
	}
	
	public static final String ARG_INTNUMCHARTS = "ARG_INTNUMCHARTS";
	
	public static Fragment createInstance(){
		Fragment f = new FragmentCharts();
		Bundle b = new Bundle();

		f.setArguments(b);
		return f;
	}
	
	/**
	 * 
	 * 
	 * Another method employed here..
	 * We have declared and defined the adapter as an inner class.
	 * This makes this adapter feel more simple.
	 *
	 */
	public class MyFragPageAdapter extends FragmentStatePagerAdapter{
	    
	    @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }
        public MyFragPageAdapter(FragmentManager fm){
	        super(fm);
	    }
        @Override
        public android.support.v4.app.Fragment getItem(int arg0) {
            return FragmentChart.createInstance(arg0);
        }

        @Override
        public int getCount() {
            List<DBData.Sensor> sensors = CacheHelpers.loadSensors();
            if (sensors != null)
                return sensors.size();
            else
                return 0;
        }
        
    }
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View v = inflater.inflate(R.layout.fragment_charts, container, false);		
		mViewPagerCharts = (ViewPager)v.findViewById(R.id.viewPagerCharts);
		btnRefreshCharts = (ImageButton)v.findViewById(R.id.btnRefreshCharts);
		pbCharts = (ProgressBar)v.findViewById(R.id.pbCharts);
		pbCharts.setVisibility(ProgressBar.INVISIBLE);
		btnRefreshCharts.setOnClickListener(this);
		
		FragmentManager fm = getChildFragmentManager();
		
		adapt = new MyFragPageAdapter(fm);
		mViewPagerCharts.setAdapter(adapt);
		
		return v;
	}

	@Override
	public Map<Integer, List<Sample>> onGetDataMap() {
		return ((OnFragmentChartsListener)getActivity()).onGetDataMap();
	}

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId())
        {
        case R.id.btnRefreshCharts:
            ArrayList<String> settings = CacheHelpers.loadSettings();
            AsyncTaskGetRecentData grd = new AsyncTaskGetRecentData(this,
                                                                    settings.get(0), 
                                                                    Integer.parseInt(settings.get(1)));
            grd.execute();
            break;
        default:
            break;
        }
    }

    @Override
    public void getRecentDataStarting() {
        if (pbCharts != null)
            pbCharts.setVisibility(ProgressBar.VISIBLE);
        
    }

    @Override
    public void getRecentDataFinished(List<Actuator> actuators,
            List<Sensor> sensors, List<DBData.Threshold> thresholds, Map<Integer, List<Sample>> data) {
        CacheHelpers.saveActuators(actuators);
        CacheHelpers.saveSensors(sensors);
        CacheHelpers.saveData(data);
        
        if (adapt != null && pbCharts != null){
            adapt.notifyDataSetChanged();
            pbCharts.setVisibility(ProgressBar.INVISIBLE);
        }
    }

	
}
