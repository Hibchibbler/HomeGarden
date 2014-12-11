package usa.or.pdx.homegarden;

import java.io.File;

import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

/**
 * MainActivity is the master, singular Activity in this project.
 * It controls which of the many fragments are shown at any given time.
 * It dynamically add/replaces fragments based on the item selected in the 
 * ActionBar menu.
 * 
 * Each fragment has a unique Tag. Very useful.
 * 
 * It has one policy:
 *  If cache exists, start off showing Charts. Otherwise, start off showing Settings.
 */
public class MainActivity extends ActionBarActivity {

	public static final String TAG_FRAGMENTSETTINGS = "TAG_FRAGMENTSETTINGS";
	public static final String TAG_FRAGMENTCHARTS = "TAG_FRAGMENTCHARTS";
	public static final String TAG_FRAGMENTNUMERICS = "TAG_FRAGMENTNUMERICS";
	public static final String TAG_FRAGMENTTHRESHOLDS = "TAG_FRAGMENTTHRESHOLDS";
	public static final String TAG_FRAGMENTOVERRIDES = "TAG_FRAGMENTOVERRIDES";
	
	public static final String SETTINGS_FILE_NAME="home_grown_settings.txt";

//	List<DBData.Sensor> mSensors;
//	List<DBData.Actuator> mActuators;
//	List<DBData.Threshold> mThresholds;
//	Map<Integer, DBData.Sample> mData;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		FragmentManager fm = getSupportFragmentManager();
		Fragment fragment  = fm.findFragmentById(R.id.fragment_container);
		if (fragment == null){
		    File file = new File(Environment.getExternalStorageDirectory(), CacheHelpers.CACHE_SETTINGS_FILE_NAME );
            if (file.exists()) {                                
                fragment = createFragmentCharts();
                fm.beginTransaction()
                    .add(R.id.fragment_container, fragment, MainActivity.TAG_FRAGMENTCHARTS)
                    .addToBackStack(null)
                    .commit();
            }else{
                fragment = createFragmentSettings();
                fm.beginTransaction()
                    .add(R.id.fragment_container, fragment, MainActivity.TAG_FRAGMENTSETTINGS)
                    .addToBackStack(null)
                    .commit();
            }
			
			
		}
	}

	private Fragment createFragmentCharts() {
		return FragmentCharts.createInstance();
	}
	
	private Fragment createFragmentNumerics() {
		return new FragmentNumerics();
	}
	
	private Fragment createFragmentThresholds() {
		return new FragmentThresholds();
	}
	
	private Fragment createFragmentSettings() {
		return new FragmentSettings();
	}
	
	private Fragment createFragmentOverrides() {
		return new FragmentOverrides();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		
		FragmentManager fm = getSupportFragmentManager();
		Fragment fragment = null;
		int id = item.getItemId();
		switch (id)
		{
			case R.id.action_settings:				
				fragment  = fm.findFragmentById(R.id.fragment_container);				

				fragment = createFragmentSettings();
				fm.beginTransaction()
					.replace(R.id.fragment_container, fragment, MainActivity.TAG_FRAGMENTSETTINGS)
					.addToBackStack(null)
					.commit();					
			
				return true;
			case R.id.action_charts:				
				fragment  = fm.findFragmentById(R.id.fragment_container);				

				fragment = createFragmentCharts();
				fm.beginTransaction()
					.replace(R.id.fragment_container, fragment, MainActivity.TAG_FRAGMENTCHARTS)
					.addToBackStack(null)
					.commit();	
				return true;
			case R.id.action_numerics:				
				fragment  = fm.findFragmentById(R.id.fragment_container);				

				fragment = createFragmentNumerics();
				fm.beginTransaction()
					.replace(R.id.fragment_container, fragment, MainActivity.TAG_FRAGMENTNUMERICS)
					.addToBackStack(null)
					.commit();	
				return true;
			case R.id.action_thresholds:				
				fragment  = fm.findFragmentById(R.id.fragment_container);				

				fragment = createFragmentThresholds();
				fm.beginTransaction()
					.replace(R.id.fragment_container, fragment, MainActivity.TAG_FRAGMENTTHRESHOLDS)
					.addToBackStack(null)
					.commit();	
				return true;
			case R.id.action_overrides:				
				fragment  = fm.findFragmentById(R.id.fragment_container);				

				fragment = createFragmentOverrides();
				fm.beginTransaction()
					.replace(R.id.fragment_container, fragment, MainActivity.TAG_FRAGMENTOVERRIDES)
					.addToBackStack(null)
					.commit();	
				return true;	
		}

		return super.onOptionsItemSelected(item);
	}

	
	
}
