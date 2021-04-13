// Note: This code has been modified from https://learn.adafruit.com/thermistor/overview by Limor Fried, Adafruit Industries.

// Initializing the analog-to-digital pins
#define ADC_0 A0
#define ADC_1 A1
#define ADC_2 A2
#define ADC_3 A3
#define ADC_4 A4

// Average thermisor resistance & resistor's resistance at 25 degrees C
#define THERMISTORNOMINAL 10500
#define SERIESRESISTOR 9950

// The temperature for the nominal resistance (~ 25 C)
#define TEMPERATURENOMINAL 25

// Number of samples per thermisor - Controls the length of the measurement
#define NUMSAMPLES 10

// Beta coefficient for thermal calculation (3000-4000)
#define BCOEFFICIENT 3950

// Number of thermisors used
#define NUMTHERMISTORS 5

// Battery pin to measure voltage
#define VBATPIN A9

// Max & min battery voltages
#define MAXBAT 4.22
#define MINBAT 3.70

// Delay in ms between calculation & data transmission (This will change the frequency of updates to phone)
#define localDelay 8000

// Delay in ms between each sample taken (Increase to spread distance between samples, this will make for a larger average, instead of more instantaneous measurements)
#define intermitentDelay 50
