/*********************************************************************
 This is an example for our nRF51822 based Bluefruit LE modules

 Pick one up today in the adafruit shop!

 Adafruit invests time and resources providing this open source code,
 please support Adafruit and open-source hardware by purchasing
 products from Adafruit!

 MIT license, check LICENSE for more information
 All text above, and the splash screen below must be included in
 any redistribution
*********************************************************************/

/*
    ETG firmware code for thermistors and bluetooth application
*/

#include <Arduino.h>
#include <SPI.h>
#include "Adafruit_BLE.h"
#include "Adafruit_BluefruitLE_SPI.h"
#include "Adafruit_BluefruitLE_UART.h"

#include "BluefruitConfig.h"
#include "ThermistorConfig.h"

#if SOFTWARE_SERIAL_AVAILABLE
  #include <SoftwareSerial.h>
#endif

/* ...or hardware serial, which does not need the RTS/CTS pins. Uncomment this line */
// Adafruit_BluefruitLE_UART ble(BLUEFRUIT_HWSERIAL_NAME, BLUEFRUIT_UART_MODE_PIN);

/* ...hardware SPI, using SCK/MOSI/MISO hardware SPI pins and then user selected CS/IRQ/RST */
Adafruit_BluefruitLE_SPI ble(BLUEFRUIT_SPI_CS, BLUEFRUIT_SPI_IRQ, BLUEFRUIT_SPI_RST);

/* ...software SPI, using SCK/MOSI/MISO user-defined SPI pins and then user selected CS/IRQ/RST */
//Adafruit_BluefruitLE_SPI ble(BLUEFRUIT_SPI_SCK, BLUEFRUIT_SPI_MISO,
//                             BLUEFRUIT_SPI_MOSI, BLUEFRUIT_SPI_CS,
//                             BLUEFRUIT_SPI_IRQ, BLUEFRUIT_SPI_RST);

// A small helper
void error(const __FlashStringHelper*err) {
  Serial.println(err);
  while (1);
}

/* The service information */
int32_t etgServiceId;
int32_t etgMeasureCharId1, etgMeasureCharId2, etgMeasureCharId3, etgMeasureCharId4, etgMeasureCharId5; // Our thermistors
int32_t etgBatteryCharId;

/**************************************************************************/
/*!
    @brief  Sets up the HW an the BLE module (this function is called
            automatically on startup)
*/
/**************************************************************************/
void setup(void)
{
  delay(500);
  boolean success;

  Serial.begin(115200);
  Serial.println(F("Equine Thermal Glove BLE"));
  Serial.println(F("---------------------------------------------------"));

  randomSeed(micros());

  /* Initialise the module */
  Serial.print(F("Initialising the Bluefruit LE module: "));

  if ( !ble.begin(VERBOSE_MODE) )
  {
    error(F("Couldn't find Bluefruit, make sure it's in CoMmanD mode & check wiring?"));
  }

  /* Perform a factory reset to make sure everything is in a known state */
  Serial.println(F("Performing a factory reset: "));
  if (! ble.factoryReset() ){
       error(F("Couldn't factory reset"));
  }

  /* Disable command echo from Bluefruit */
  ble.echo(false);

  Serial.println("Requesting Bluefruit info:");
  /* Print Bluefruit information */
  ble.info();

  // this line is particularly required for Flora, but is a good idea
  // anyways for the super long lines ahead!
  // ble.setInterCharWriteDelay(5); // 5 ms

  /* Change the device name to make it easier to find */
  Serial.println(F("Setting device name to 'Equine Thermal Glove': "));

  if (! ble.sendCommandCheckOK(F("AT+GAPDEVNAME=Equine Thermal Glove")) ) {
    error(F("Could not set device name?"));
  }

  /* Add the ETG Service definition */
  /* Service ID should be 1 */
  Serial.println(F("Adding the ETG Service definition (UUID128 = 08-99-15-A8-15-28-43-7A-B3-78-F7-31-19-0C-07-45): "));
  success = ble.sendCommandWithIntReply( F("AT+GATTADDSERVICE=UUID128=08-99-15-A8-15-28-43-7A-B3-78-F7-31-19-0C-07-45"), &etgServiceId);
  if (! success) {
    error(F("Could not add ETG service"));
  }

  /* Add the Temperature Measurement characteristic for each sensor*/
  Serial.println(F("Adding the ETG Temperature Measurement characteristic (UUID128 = 08-99-00-0X-15-28-43-7A-B3-78-F7-31-19-0C-07-45): "));
  success = ble.sendCommandWithIntReply( F("AT+GATTADDCHAR=UUID128=08-99-00-01-15-28-43-7A-B3-78-F7-31-19-0C-07-45, PROPERTIES=0x10, MIN_LEN=1, MAX_LEN=8, VALUE=0"), &etgMeasureCharId1);
    if (! success) {
    error(F("Could not add ETG_TM1 characteristic 1"));
  }
  success = ble.sendCommandWithIntReply( F("AT+GATTADDCHAR=UUID128=08-99-00-02-15-28-43-7A-B3-78-F7-31-19-0C-07-45, PROPERTIES=0x10, MIN_LEN=1, MAX_LEN=8, VALUE=0"), &etgMeasureCharId2);
    if (! success) {
    error(F("Could not add ETG_TM2 characteristic 2"));
  }
  success = ble.sendCommandWithIntReply( F("AT+GATTADDCHAR=UUID128=08-99-00-03-15-28-43-7A-B3-78-F7-31-19-0C-07-45, PROPERTIES=0x10, MIN_LEN=1, MAX_LEN=8, VALUE=0"), &etgMeasureCharId3);
    if (! success) {
    error(F("Could not add ETG_TM characteristic 3"));
  }
  success = ble.sendCommandWithIntReply( F("AT+GATTADDCHAR=UUID128=08-99-00-04-15-28-43-7A-B3-78-F7-31-19-0C-07-45, PROPERTIES=0x10, MIN_LEN=1, MAX_LEN=8, VALUE=0"), &etgMeasureCharId4);
    if (! success) {
    error(F("Could not add ETG_TM characteristic 4"));
  }
  success = ble.sendCommandWithIntReply( F("AT+GATTADDCHAR=UUID128=08-99-00-05-15-28-43-7A-B3-78-F7-31-19-0C-07-45, PROPERTIES=0x10, MIN_LEN=1, MAX_LEN=8, VALUE=0"), &etgMeasureCharId5);
    if (! success) {
    error(F("Could not add ETG_TM characteristic 5"));
  }

  /* Add the Battery Life characteristic */
  success = ble.sendCommandWithIntReply( F("AT+GATTADDCHAR=UUID128=08-99-00-06-15-28-43-7A-B3-78-F7-31-19-0C-07-45, PROPERTIES=0x10, MIN_LEN=1, VALUE=0"), &etgBatteryCharId);
    if (! success) {
    error(F("Could not add ETG_BL characteristic"));
  }

//  This is for adding our service to the advertising packet. But our service is unoffical and unlicensed, which means we can't do this!
//  The original byte array below advertises that this device supports the Heart Rate Measurement service, which it doesn't.
//  We'd need a registered service (basically get a shortened 16-bit UUID) to put in one of the two allowed advertised service spots,
//  which is what the last 4 bytes of the array are (for heart rate measurement in the below case. It's like heart rate and location of 
//  device I believe).
//  /* Add the ETG Service to the advertising data (needed for Nordic apps to detect the service) */
//  Serial.print(F("Adding ETG Service UUID to the advertising payload: "));
//  ble.sendCommandCheckOK( F("AT+GAPSETADVDATA=02-01-06-05-02-0d-18-0a-18") );

  /* Reset the device for the new service setting changes to take effect */
  Serial.print(F("Performing a SW reset (service changes require a reset): "));
  ble.reset();

  Serial.println();
}

// Defining the arrays that will hold each thermal sensors sample measurement
int sample[NUMTHERMISTORS][NUMSAMPLES];

/** Send randomized heart rate data continuously **/
void loop(void)
{ 
  uint8_t i, j;
  float average[NUMTHERMISTORS];
  
   /* Checks connection, note it is called twice due to a bug that returns okay innstead of '1' or '0'
    * on first attempt after a successful connection has been made */
   ble.println("AT+GAPGETCONN");
   ble.println("AT+GAPGETCONN");
   ble.readline();
   
   /* Only take measurement while connected via BT to central */
   if ( (strcmp(ble.buffer, "1") == 0) ) {
      Serial.print(F("Connected!!\n"));
      
      /* Takes a specified number of samples per each thermisor */
      for (i=0; i< NUMSAMPLES; i++) {
       sample[0][i] = analogRead(ADC_0);
       sample[1][i] = analogRead(ADC_1);
       sample[2][i] = analogRead(ADC_2);
       sample[3][i] = analogRead(ADC_3);
       sample[4][i] = analogRead(ADC_4);
       delay(intermitentDelay);
      }
      
      /* Calculate average resustabce for each thermistor */
      for (i=0; i< NUMTHERMISTORS; i++) {
        
        /* Ensure the average is cleared for the new calculation */
        average[i] = 0;
    
        /* Average all the samples per thermistor */
        for (j=0; j< NUMSAMPLES; j++) {
           average[i] += sample[i][j];
        }
        average[i] /= NUMSAMPLES;
        
        /* Convert the ADC reading to resistance in ohms */
        average[i] = 1023 / average[i] - 1;
        average[i] = SERIESRESISTOR / average[i];
      }
      
    
      /* Convert our resistance into temperature for each thermistor */
      float steinhart[NUMTHERMISTORS];
      float resolut;
      long rounded[NUMTHERMISTORS];
      for (i=0; i< NUMTHERMISTORS; i++) {
        steinhart[i] = average[i] / THERMISTORNOMINAL;       // (R/Ro)
        steinhart[i] = log(steinhart[i]);                    // ln(R/Ro)
        steinhart[i] /= BCOEFFICIENT;                        // 1/B * ln(R/Ro)
        steinhart[i] += 1.0 / (TEMPERATURENOMINAL + 273.15); // + (1/To)
        steinhart[i] = 1.0 / steinhart[i];                   // Invert
        steinhart[i] -= 273.15;                              // convert absolute temp to C
    
        // Set resolution to +/- 0.5 *C
        resolut = round(steinhart[i] * 2);
        steinhart[i] = resolut / 2;
    
        // Round to 1 decimal place
        rounded[i] = (long) (steinhart[i] * 10L);
        steinhart[i] = (float) rounded[i] / 10.0;
      }
      
      /* Measure our battery */
      float measuredvbat = analogRead(VBATPIN);
      measuredvbat *= 2;     // we divided by 2, so multiply back
      measuredvbat *= 3.3;   // Multiply by 3.3V, our reference voltage
      measuredvbat /= 1024;  // convert to voltage
      float referenceVbat = MAXBAT - MINBAT;
      int percentvbat = round ( ( (measuredvbat - 3.7) / referenceVbat ) * 100);
      Serial.print(measuredvbat);
    
      /* Our % measurement is relative to our battery, this is in the event of a new bat with a slightly higher max value */
      if (percentvbat > 100) {
        percentvbat = 100;
      }
    
      /* Command is sent when \n (\r) or println is called */
      /* AT+GATTCHAR=CharacteristicID,value */
      // --------------------------
      ble.print( F("AT+GATTCHAR=") );
      ble.print( etgMeasureCharId1 );
      ble.print( F(",") );
      ble.println(steinhart[0]);          // Thumb
      // --------------------------
      ble.print( F("AT+GATTCHAR=") );
      ble.print( etgMeasureCharId2 );
      ble.print( F(",") );
      ble.println(steinhart[1]);          // Index
      // --------------------------
      ble.print( F("AT+GATTCHAR=") );
      ble.print( etgMeasureCharId3 );
      ble.print( F(",") );
      ble.println(steinhart[2]);          // Middle
      // --------------------------
      ble.print( F("AT+GATTCHAR=") );
      ble.print( etgMeasureCharId4 );
      ble.print( F(",") );
      ble.println(steinhart[3]);          // Ring
      // --------------------------
      ble.print( F("AT+GATTCHAR=") );
      ble.print( etgMeasureCharId5 );
      ble.print( F(",") );
      ble.println(steinhart[4]);          // Pinky
      // --------------------------
      ble.print( F("AT+GATTCHAR=") );
      ble.print( etgBatteryCharId );
      ble.print( F(",") );
      ble.println(percentvbat);           // Battery

      // Check if command executed (Recieving OK from app)
      if ( !ble.waitForOK() ) {
        Serial.println(F("Failed to get response!"));
      }
  
  } else { // Do nothing!
    Serial.print(F("Not Connected\n"));
  }
  
  /* Delay before next measurement update */
  delay(localDelay);
}
