// Example testing sketch for various DHT humidity/temperature sensors
// Written by ladyada, public domain

// Modified 1 Dec 2014
// by Mitch Barton  

// CD: modified to run in an asynch sample loop and act as a serial interface for the DHT22

#include "DHT.h"

#define DHTPIN 2     // what pin we're connected to

// Uncomment whatever type you're using!
//#define DHTTYPE DHT11   // DHT 11 
#define DHTTYPE DHT22   // DHT 22  (AM2302)
//#define DHTTYPE DHT21   // DHT 21 (AM2301)

// these are the counter and offset to use the millis function as a sample timer
long sampleCt = 0;      // counter for millis until sample
long sampleCtBase = 0;  // start of program base to use...

int led = 13;    // led pin assignment

// serial string parser function variables
char inData[20]; // Allocate some space for the string
char inChar=-1; // Where to store the character read
byte index = 0; // Index into array; where to store the character

// data variables
float h = 0;
float t = 0;
float f = 0;
float hi = 0;

// Connect pin 1 (on the left) of the sensor to +5V
// NOTE: If using a board with 3.3V logic like an Arduino Due connect pin 1
// to 3.3V instead of 5V!
// Connect pin 2 of the sensor to whatever your DHTPIN is
// Connect pin 4 (on the right) of the sensor to GROUND
// Connect a 10K resistor from pin 2 (data) to pin 1 (power) of the sensor

// Initialize DHT sensor for normal 16mhz Arduino
DHT dht(DHTPIN, DHTTYPE);
// NOTE: For working with a faster chip, like an Arduino Due or Teensy, you
// might need to increase the threshold for cycle counts considered a 1 or 0.
// You can do this by passing a 3rd parameter for this threshold.  It's a bit
// of fiddling to find the right value, but in general the faster the CPU the
// higher the value.  The default for a 16mhz AVR is a value of 6.  For an
// Arduino Due that runs at 84mhz a value of 30 works.
// Example to initialize DHT sensor for Arduino Due:
//DHT dht(DHTPIN, DHTTYPE, 30);

void setup() {
  Serial.begin(9600); 
  
  // init the DHT sensor
  dht.begin();
  
  // init the sample offset to the start of program (or close to it)
  sampleCtBase = millis();
  
  pinMode(led, OUTPUT);
}

void loop() {
  
  // increment the timer relative to the base, since millis() can't ever be reset in program
  sampleCt =+ (millis() - sampleCtBase);

  // once the timer reaches 3 sec 
  if (sampleCt >= 3000) {
  
  // turn the led on to indicate sampling
  digitalWrite(led, HIGH);
  
  // Reading temperature or humidity takes about 250 milliseconds!
  // Sensor readings may also be up to 2 seconds 'old' (its a very slow sensor)
  h = dht.readHumidity();
  // Read temperature as Celsius
  t = dht.readTemperature();
  // Read temperature as Fahrenheit
  f = dht.readTemperature(true);
  
  // Check if any reads failed and exit early (to try again).
  if (isnan(h) || isnan(t) || isnan(f)) {
    //Serial.println("data invalid!");
    return;
  }
  
  digitalWrite(led, LOW);

  // Compute heat index
  // Must send in temp in Fahrenheit!
  hi = dht.computeHeatIndex(f, h);
  
  // reset the base to now and timer to 0 so that we retain a (rough) 2Hz sample rate
  sampleCtBase = millis();
  sampleCt = 0;
  }  
  
  // comp statements to send data on serial 1 port after requests
  if (Comp("tempC")==0) {
    Serial.write(int(t));
    //Serial.println(t);
  }
  
  if (Comp("tempF")==0) {
    Serial.write(int(f));
    //Serial.println(f);

  }
  
  if (Comp("hum")==0) {
    Serial.write(int(h));
    //Serial.println(int(h));

  }
  
  if (Comp("hInd")==0) {
    Serial.write(int(hi));
  }
    
}

//--------------------------------------------------------------------Comp()

// function to read in a string over serial(.write() not .print()) 
// returns a 0 if the string input over serial matches the argument 
// and a 1 if not
char Comp(char* This) {
    while (Serial.available() > 0) // Don't read unless
                                   // there you know there is data
    {
        if(index < 19) // One less than the size of the array
        {
            inChar = Serial.read(); // Read a character
            inData[index] = inChar; // Store it
            index++; // Increment where to write next
            inData[index] = '\0'; // Null terminate the string
        }
    }

    if (strcmp(inData,This)  == 0) {
        for (int i=0;i<19;i++) {
            inData[i]=0;
        }
        index=0;
        return(0);
    }
    else {
        return(1);
    }
}
//--------------------------------------------------------------------END Comp()


