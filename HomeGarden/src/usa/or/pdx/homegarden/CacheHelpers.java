package usa.or.pdx.homegarden;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import usa.or.pdx.homegarden.DBData.Sensor;
import android.annotation.SuppressLint;
import android.os.Environment;
import android.util.Log;

/**
 * This class provides a simple and clean api to interact with the Cache files.
 * There are save* and load* methods defined herein.
 * settings, data, sensors, actuators, and thresholds.
 */
public class CacheHelpers {
    public static final String CACHE_SETTINGS_FILE_NAME        ="home_grown_settings_cache.txt";
    public static final String CACHE_DATA_FILE_NAME            ="home_grown_data_cache.txt";
    public static final String CACHE_SENSORS_FILE_NAME         ="home_grown_sensors_cache.txt";
    public static final String CACHE_ACTUATORS_FILE_NAME       ="home_grown_actuators_cache.txt";
    public static final String CACHE_THRESHOLDS_FILE_NAME      ="home_grown_thresholds_cache.txt";
    public static final String CACHE_MOSTRECENTDATA_FILE_NAME  ="home_grown_mostrecentdata_cache.txt";
    
    public static ArrayList<String> loadSettings(){
        ArrayList<String> settings = new ArrayList<String>();
        try {
            File myFile = new File(Environment.getExternalStorageDirectory(), "home_grown_settings_cache.txt");
            FileInputStream fIn = new FileInputStream(myFile);
            BufferedReader myReader = new BufferedReader(new InputStreamReader(fIn));

            String inLine;
            
            while ((inLine = myReader.readLine()) != null){
                settings.add(inLine);
            }

            myReader.close();    

        } 
        catch (IOException e) 
        {
            e.printStackTrace();
        }
        return settings;
    }
    
    public static void saveSettings(String ip, int port){
        try {
            File myFile = new File(Environment.getExternalStorageDirectory(), "home_grown_settings_cache.txt");
            myFile.createNewFile();
            FileOutputStream fOut = new FileOutputStream(myFile);
            OutputStreamWriter myOutWriter =new OutputStreamWriter(fOut);
            
            myOutWriter.append(ip+"\n");
            myOutWriter.append(port+"\n");
            
            myOutWriter.close();
            fOut.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public static void clearCache(){
        File file = null;
        file = new File(Environment.getExternalStorageDirectory(), CacheHelpers.CACHE_ACTUATORS_FILE_NAME);
        if(file.exists())
        {
            file.delete();
        }
        file = new File(Environment.getExternalStorageDirectory(), CacheHelpers.CACHE_SENSORS_FILE_NAME);
        if(file.exists())
        {
            file.delete();
        }
        file = new File(Environment.getExternalStorageDirectory(), CacheHelpers.CACHE_DATA_FILE_NAME);
        if(file.exists())
        {
            file.delete();
        }
        file = new File(Environment.getExternalStorageDirectory(), CacheHelpers.CACHE_MOSTRECENTDATA_FILE_NAME);
        if(file.exists())
        {
            file.delete();
        }
        
        file = new File(Environment.getExternalStorageDirectory(), CacheHelpers.CACHE_SETTINGS_FILE_NAME);
        if(file.exists())
        {
            file.delete();
        }
    }
    public static List<DBData.Sensor> loadSensors(){
        List<DBData.Sensor> sensors = null;

        try {
            File myFile = new File(Environment.getExternalStorageDirectory(), CacheHelpers.CACHE_SENSORS_FILE_NAME);
            FileInputStream fIn = new FileInputStream(myFile);
            BufferedReader myReader = new BufferedReader(new InputStreamReader(fIn));

            sensors = new ArrayList<DBData.Sensor>();

            String inLine;
            while ((inLine = myReader.readLine()) != null){
                String[] lineComponents = inLine.split(",");
                Sensor s = new Sensor();
                s.setSensorId(Integer.parseInt(lineComponents[0].trim()));
                s.setSensorName(lineComponents[1]);
                s.setSampleSize(Integer.parseInt(lineComponents[2].trim()));
                s.setSampleRate(Integer.parseInt(lineComponents[3].trim()));
                sensors.add(s);
            }

            myReader.close();	 

        }
        catch (IOException e) 
        {
            e.printStackTrace();
        }
        return sensors;
    }

    public static List<DBData.Actuator> loadActuators(){
        List<DBData.Actuator> actuators = null;

        try {
            File myFile = new File(Environment.getExternalStorageDirectory(), CacheHelpers.CACHE_ACTUATORS_FILE_NAME);
            FileInputStream fIn = new FileInputStream(myFile);
            BufferedReader myReader = new BufferedReader(new InputStreamReader(fIn));

            actuators = new ArrayList<DBData.Actuator>();

            String inLine;
            while ((inLine = myReader.readLine()) != null){
                String[] lineComponents = inLine.split(",");
                DBData.Actuator a = new DBData.Actuator();
                a.setActuatorId(Integer.parseInt(lineComponents[0].trim()));
                a.setActuatorName(lineComponents[1]);			    	
                a.setState(Integer.parseInt(lineComponents[2]));
                a.setOverride(Integer.parseInt(lineComponents[3]));
                actuators.add(a);
            }
            myReader.close();	 
        }
        catch (IOException e) 
        {
            e.printStackTrace();
        }
        return actuators;
    }
    public static List<DBData.Threshold> loadThresholds(){
        List<DBData.Threshold> thresholds = null;

        try {
            File myFile = new File(Environment.getExternalStorageDirectory(), CacheHelpers.CACHE_THRESHOLDS_FILE_NAME);
            FileInputStream fIn = new FileInputStream(myFile);
            BufferedReader myReader = new BufferedReader(new InputStreamReader(fIn));

            thresholds = new ArrayList<DBData.Threshold>();

            String inLine;
            while ((inLine = myReader.readLine()) != null){
                String[] lineComponents = inLine.split(",");
                DBData.Threshold t = new DBData.Threshold();
                t.setThresholdId(Integer.parseInt(lineComponents[0].trim()));
                t.setActuatorId(Integer.parseInt(lineComponents[1].trim()));
                t.setSensorId(Integer.parseInt(lineComponents[2].trim()));
                t.setMin(Integer.parseInt(lineComponents[3].trim()));
                t.setMax(Integer.parseInt(lineComponents[4].trim()));
                thresholds.add(t);
            }

            myReader.close();    

        }
        catch (IOException e) 
        {
            e.printStackTrace();
        }
        return thresholds;
    }
    @SuppressLint("UseSparseArrays")
    public static Map<Integer, List<DBData.Sample>> loadData(){
        Map<Integer, List<DBData.Sample>> data = null;
        data = new HashMap<Integer, List<DBData.Sample>>();

        List<DBData.Sensor> sensors = CacheHelpers.loadSensors();
        if (sensors == null){
            Log.e("CacheHelpers", "loadSensors failed");
            return null;
        }

        for (Sensor s : sensors){
            data.put(s.getSensorId(), new ArrayList<DBData.Sample>());
        }

        try {
            File myFile = new File(Environment.getExternalStorageDirectory(), CacheHelpers.CACHE_DATA_FILE_NAME);
            FileInputStream fIn = new FileInputStream(myFile);
            BufferedReader myReader = new BufferedReader(new InputStreamReader(fIn));

            String inLine;
            while ((inLine = myReader.readLine()) != null){
                String[] lineComponents = inLine.split(",");
                DBData.Sample s = new DBData.Sample();
                s.setSensorId(Integer.parseInt(lineComponents[0].trim()));
                s.setData(Integer.parseInt(lineComponents[1].trim()));
                s.setTimestamp(Timestamp.valueOf(lineComponents[2].trim()));
                List<DBData.Sample> samples = data.get(s.getSensorId());
                samples.add(s);

            }
            myReader.close();	 
        }
        catch (IOException e) 
        {
            e.printStackTrace();
        }
        return data;
    }
    
    

    public static void saveSensors(List<DBData.Sensor> sensors){
        try {
            File myFile = new File(Environment.getExternalStorageDirectory(), CacheHelpers.CACHE_SENSORS_FILE_NAME);
            FileOutputStream fOut = new FileOutputStream(myFile);
            OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);

            for (DBData.Sensor s: sensors){
                myOutWriter.append(s.getSensorId() + ","+ s.getSensorName() +"," +s.getSampleSize() + ","+s.getSampleRate()+"\n");
            }

            myOutWriter.close();
            fOut.close();
        }catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void saveActuators(List<DBData.Actuator> actuators){
        try {
            File myFile = new File(Environment.getExternalStorageDirectory(), CacheHelpers.CACHE_ACTUATORS_FILE_NAME);
            FileOutputStream fOut = new FileOutputStream(myFile);
            OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);

            for (DBData.Actuator a: actuators){
                myOutWriter.append(a.getActuatorId()+ ","+ a.getActuatorName() + "," + a.getState() + "," + a.getOverride() + "\n");
            }

            myOutWriter.close();
            fOut.close();
        }catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public static void saveThresholds(List<DBData.Threshold> thresholds){
        try {
            File myFile = new File(Environment.getExternalStorageDirectory(), CacheHelpers.CACHE_THRESHOLDS_FILE_NAME);
            FileOutputStream fOut = new FileOutputStream(myFile);
            OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);

            for (DBData.Threshold t: thresholds){
                myOutWriter.append(t.getThresholdId() +"," +t.getActuatorId()+ ","+ t.getSensorId() +","+t.getMin() + "," + t.getMax() +"\n");
            }

            myOutWriter.close();
            fOut.close();
        }catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void saveData(Map<Integer, List<DBData.Sample>> data){
        try {
            
            // The actual data cache
            File myFile = new File(Environment.getExternalStorageDirectory(), CacheHelpers.CACHE_DATA_FILE_NAME);
            FileOutputStream fOut = new FileOutputStream(myFile);
            OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);
            
            
            for (Map.Entry<Integer,List<DBData.Sample>> entry: data.entrySet()){
                for (DBData.Sample s : entry.getValue()){
                    myOutWriter.append(entry.getKey()+ "," + s.getData() + "," + s.getTimestamp() + "\n");
                }
            }
            myOutWriter.close();
            fOut.close();
            
            
            saveLastDataTimestamps(data);
            
        }catch (IOException e) {
            e.printStackTrace();
        }
    }

    	@SuppressLint("UseSparseArrays")
        public static void saveLastDataTimestamps(Map<Integer, List<DBData.Sample>> data){
    	    try {
                
                // Only the most recent data cache...one line per sensor.
                Map<Integer, Timestamp> mostRecentData = new HashMap<Integer, Timestamp>();
                File myFile = new File(Environment.getExternalStorageDirectory(), CacheHelpers.CACHE_MOSTRECENTDATA_FILE_NAME);
                FileOutputStream fOut = new FileOutputStream(myFile);
                OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);

                for (Map.Entry<Integer,List<DBData.Sample>> entry: data.entrySet()){
                    for (DBData.Sample s : entry.getValue()){
                        mostRecentData.put(entry.getKey(), s.getTimestamp());
                    }
                }
                
                for (Map.Entry<Integer,Timestamp> entry : mostRecentData.entrySet())
                {
                    myOutWriter.append(entry.getKey() + "," + entry.getValue() +"\n");
                }
                myOutWriter.close();
                fOut.close();
                
            }catch (IOException e) {
                e.printStackTrace();
            }
    	}
    	
    	@SuppressLint("UseSparseArrays")
        public static Map<Integer, Timestamp> loadLastDataTimestamps(){
    	    Map<Integer, Timestamp> lastTimestamps = null;

            try {
                File file = new File(Environment.getExternalStorageDirectory(), "home_grown_settings_cache.txt" );
                if (!file.exists()) {
                   return null;
                }
                
                File myFile = new File(Environment.getExternalStorageDirectory(), CacheHelpers.CACHE_MOSTRECENTDATA_FILE_NAME);
                FileInputStream fIn = new FileInputStream(myFile);
                BufferedReader myReader = new BufferedReader(new InputStreamReader(fIn));

                lastTimestamps = new HashMap<Integer, Timestamp>();

                String inLine;
                while ((inLine = myReader.readLine()) != null){
                    String[] lineComponents = inLine.split(",");
                    
                    //a.setActuatorId(Integer.parseInt(lineComponents[0].trim()));
                    Integer sensorId = Integer.parseInt(lineComponents[0].trim());
                    Timestamp ts = Timestamp.valueOf(lineComponents[1].trim());   
                    lastTimestamps.put(sensorId, ts);
                    //actuators.add(a);
                }
                myReader.close();    
            }
            catch (IOException e) 
            {
                e.printStackTrace();
            }
            return lastTimestamps;
    	}


}
