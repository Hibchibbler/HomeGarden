/*
  IoT Remote Gardener v1.0.0
  
  Sketch for using a Galileo gen2 to read/control sensors and actuators.  
  The Galileo gen2 is running linux on an SD card and has a MySQL database 
  which is accessed with system commands. Additionally, system commands 
  manage the physical layer of the WIFI. An Arduino Uno is running a seperate
  program and acting as an asynchronous serial interface board for the DHT22
  sensor. 
  
  Circuit:
  * 100K potentiometer - A0, 5V, GND
  * Sparkfun MOSFET PWR CNTRLR INPUT - pin D3 
  * Intel 6205 WIFI - mPCIe card slot
  * Arduino Uno - pin D0 (Rx), pin D1 (Tx)
  
  References:
  * http://www.intel.com/content/www/us/en/blogs-communities-social.html
  * https://gist.github.com/carlynorama/9316252
  
  created 26 Nov 2014
  by Mitch Barton
  
  */
  
 #include <SD.h>
 #include <WiFi.h>
 
 // have a hard coded sleep in there now #define SAMPLEPERIOD 3000  // sample period in ms, must be greater than 2000 for DHT22
 #define SAVESAMPLECT 10    // save to DB period relative to sample period, i.e. value of 10 means every 10th sample we save
 #define CHECKFETCHCT 20    // check for threshold update period relative to sample period
 #define FAN1PIN 7          // fan 1 digital output
 
 int POTPIN = A0;        // analog input
 int tempValue = 0;       // init temp sensor reading to 0
 int humValue = 0;          // init humidity sensor to 0
 int saveCounter = 0;       // save to DB counter
 int checkCounter = 0;      // check for update counter
 int fan1Thresh = 80;      // fan 1 threshold value
 int historyInterval = 2;  // interval that data is saved through, in minutes...
 int inByte = 0;            // incoming serial byte

 char ssid[] = "Fire";     //  your network SSID (name) 
 char pass[] = "Sm0ke2013";  // your network password
 int status = WL_IDLE_STATUS;     // the Wifi radio's status

void setup() {
  
  // time stuff
  time_t t = time(NULL);
  struct tm tm = *localtime(&t);
  char buf[100]; 
  
  // serial connections
  Serial.begin(9600);
  Serial1.begin(9600);
  
  // init actuators
  pinMode(FAN1PIN, OUTPUT);
  
  // print the formatted time
  sprintf(buf, "Date: %04d%02d%02d %02d:%02d:%02d",tm.tm_year+1900,tm.tm_mon+1,tm.tm_mday,tm.tm_hour,tm.tm_min,tm.tm_sec);
  Serial.println(buf);
  
  // start wifi if already up
  //system("ifup wlan0");
 
  // attempt to connect to Wifi network:
  while ( status != WL_CONNECTED) { 
    Serial.print("Attempting to connect to WPA SSID: ");
    Serial.println(ssid);
    // Connect to WPA/WPA2 network:    
    status = WiFi.begin(ssid, pass);

    // wait 10 seconds for connection:
    delay(10000);
  }
   
  // you're connected now, so print out the data:
  Serial.print("You're connected to the network");
  printCurrentNet();
  printWifiData();
 
  
  // init the SD card for read/write
  initSDCard();
  
  // clear out any existing data files on the SD
  deleteFile("insert_data1.sql");
  deleteFile("delete_data1.sql");
  
  // create file for sending DB command to delete all before interval, need to delete it too once we want to change it...
  galileoCreateFile("delete_data1.sql");
  addFileContent("delete_data1.sql", "DELETE FROM data WHERE created_at < SUBTIME(NOW(), '0:" + String(historyInterval) + ":0');");
}

void loop() {
  
  // sample data
  sampleData();
 
  // save to SD if sample 'period' is up
  if (saveCounter < SAVESAMPLECT) { 
    saveData();
    saveCounter++;
  }
  else {
    //write to DB
    writeDB();
    //reset counter
    saveCounter = 0;
  }
 
  // todo read thresholds from DB

  // update control 
  updateControl();
}

//----------------------------------------------------------- writeDB()
void writeDB() {
  // delete existing entries before time of historyInterval
  //system("mysql DBHomeGrown </media/mmcblk0p1/delete_data1.sql");
  
  // issue commands in SD file to DB
  system("mysql DBHomeGrown </media/mmcblk0p1/insert_data1.sql");
  
  // delete the old SD file
  deleteFile("insert_data1.sql");
  
}

//----------------------------------------------------------- sampleData()
void sampleData() {
  // hardcoded 2sec system sleep. Didn't use delay because it ties up the processor.
  // todo implement paramaterized sample rate
  system("sleep 2");
  
  // Call for the temp in farenheit from the Uno/DHT22
  Serial1.write("tempF");
  
  // if we get a valid byte, read analog ins:
  if (Serial1.available() > 0) {
    // get incoming byte:
    tempValue = Serial1.read();           
  }
 
 /* this will read the humidity data but it was unimpressive..
    Serial1.write("hum");
  
  // if we get a valid byte, read analog ins:
  if (Serial1.available() > 0) {
    // get incoming byte:
    humValue = Serial1.read();           
  }
  */
  
  // humidity was lame so for demo we used the potentiometer
  humValue = analogRead(POTPIN);
}
//----------------------------------------------------------- END sampleData()

//----------------------------------------------------------- saveData()
void saveData() {
  
  // hard coding these for now, could be dynamic 
  galileoCreateFile("insert_data1.sql");
  
  // adding batch file mySQL commands to insert the temp and humidity data into existing DB
  addFileContent("insert_data1.sql", ("INSERT INTO data(sensor_id, data, created_at) VALUES (0, " + String(tempValue) + ", NOW());"));
  addFileContent("insert_data1.sql", ("INSERT INTO data(sensor_id, data, created_at) VALUES (1, " + String(humValue) + ", NOW());")); 
 
}
//----------------------------------------------------------- END saveData()

//----------------------------------------------------------- updateControl()
void updateControl() {
  if (tempValue > fan1Thresh) {
    digitalWrite(FAN1PIN, HIGH);
    return;
  }
    digitalWrite(FAN1PIN, LOW);
}
//----------------------------------------------------------- END updateControl()

//----------------------------------------------------------- initSDCard()
void initSDCard() {
  Serial.print("Initializing SD card...");
  
  // try if (!SD.begin()) { if on the Galileo, does not need a pin
  // to initialize correctly
  if (!SD.begin()) {
    Serial.println("initialization failed!");
    return;
  }
  Serial.println("initialization done.");
}
//------------------------------------------------------ END initSDCard()
 
//---------------------------------------------------------- deleteFile()
void deleteFile(String fileName) {
  Serial.println("\n*****Removal started.*****");
  String status_message = String();
  status_message = fileName;
  char charFileName[fileName.length() + 1];
  fileName.toCharArray(charFileName, sizeof(charFileName));
 
  if (SD.exists(charFileName)) { 
    status_message += " was found.";
    Serial.println(status_message);
    SD.remove(charFileName);
    if (SD.exists(charFileName)) {
      status_message += " Removal failed.";
    } 
    else {
      status_message += " Removal a success.";
    }
 
  }
  else {
    status_message += " already doesn't exist. Perhaps you meant a different file?";
 
  }
  Serial.println(status_message);
}
//----------------------------------------------------- END deleteFile()
 
//--------------------------------------------------- printFileContent()
void printFileContent(String fileName) {
  Serial.println("\n*****Printing Started*****");
  String status_message = String();
  status_message = fileName;
  char charFileName[fileName.length() + 1];
  fileName.toCharArray(charFileName, sizeof(charFileName));
 
  if (SD.exists(charFileName)) { 
    File myFile = SD.open(charFileName);
    if (myFile) {
      status_message += " found... hold for content: \n\n";
      while (myFile.available()) {
        status_message += (char)myFile.read();
      }
      status_message += "\nDone printing.";
      myFile.close();
    }
    else {
      status_message += " found, error with content";
    } 
  }
  else {
    status_message += " does not exist.";
  }
  Serial.println(status_message);
}
//----------------------------------------------- END printFileContent()
 
//----------------------------------------------------- addFileContent()
//SD.open retrieves the file in append more. 
void addFileContent(String fileName, String content) {
  Serial.println("\n*****Add Started*****");
  String status_message = String();
  status_message = fileName;
  char charFileName[fileName.length() + 1];
  fileName.toCharArray(charFileName, sizeof(charFileName));
 
  if (SD.exists(charFileName)) { 
    status_message += " was found.";
    File targetFile = SD.open(charFileName, FILE_WRITE);
    targetFile.println(content);
    targetFile.close();
 
    status_message += "\n\nFile now reads:\n\n";
    File readFile = SD.open(charFileName);
    while (readFile.available()) {
      status_message += (char)readFile.read();
    }
    status_message += "\nDone printing.";
    readFile.close();
  }
  else {
    status_message += " does not exist. Perhaps you meant to add the information somewhere else.";
  }
  Serial.println(status_message);
}
//------------------------------------------------- END addFileContent() 
 
//-------------------------------------------------- galileoCreateFile()
//There appears to ba a bug with fopen() in the C standard library stio.h on
// the Galileo board. The Galileo SD Library calls fopen correctly, but it 
// butfopen does not behave as expected. This function works around that. 
void galileoCreateFile(String fileName) {
  Serial.println("\n*****Creation Started*****");
  String status_message = String();
  status_message = fileName;
  char charFileName[fileName.length() + 1];
  fileName.toCharArray(charFileName, sizeof(charFileName));
 
  if (SD.exists(charFileName)) { 
    status_message += " exists already.";
  }
  else {
    char system_message[256];
    char directory[] = "/media/realroot";
    sprintf(system_message, "touch %s/%s", directory, charFileName);
    system(system_message);
    if (SD.exists(charFileName)) {
      status_message += " created.";
    } 
    else {
      status_message += " creation tried and failed.";
    }
  }
  Serial.println(status_message);
}
//---------------------------------------------- END galileoCreateFile()

//---------------------------------------------- printWifiData()
void printWifiData() {
  // print your WiFi shield's IP address:
  IPAddress ip = WiFi.localIP();
    Serial.print("IP Address: ");
  Serial.println(ip);
  Serial.println(ip);
  
  // print your MAC address:
  byte mac[6];  
  WiFi.macAddress(mac);
  Serial.print("MAC address: ");
  Serial.print(mac[5],HEX);
  Serial.print(":");
  Serial.print(mac[4],HEX);
  Serial.print(":");
  Serial.print(mac[3],HEX);
  Serial.print(":");
  Serial.print(mac[2],HEX);
  Serial.print(":");
  Serial.print(mac[1],HEX);
  Serial.print(":");
  Serial.println(mac[0],HEX);
 
}
//-----------------------------------------------END printWifiData()

//-----------------------------------------------printCurrentNet()
void printCurrentNet() {
  // print the SSID of the network you're attached to:
  Serial.print("SSID: ");
  Serial.println(WiFi.SSID());

  // print the MAC address of the router you're attached to:
  byte bssid[6];
  WiFi.BSSID(bssid);    
  Serial.print("BSSID: ");
  Serial.print(bssid[5],HEX);
  Serial.print(":");
  Serial.print(bssid[4],HEX);
  Serial.print(":");
  Serial.print(bssid[3],HEX);
  Serial.print(":");
  Serial.print(bssid[2],HEX);
  Serial.print(":");
  Serial.print(bssid[1],HEX);
  Serial.print(":");
  Serial.println(bssid[0],HEX);

  // print the received signal strength:
  long rssi = WiFi.RSSI();
  Serial.print("signal strength (RSSI):");
  Serial.println(rssi);

  // print the encryption type:
  byte encryption = WiFi.encryptionType();
  Serial.print("Encryption Type:");
  Serial.println(encryption,HEX);
  Serial.println();
}

//-----------------------------------------------END printCurrentNet()
