#include "DHT.h"
#include <ESP8266WiFi.h>
#include <EEPROM.h>
#include <ESP8266WebServer.h>

#define DHT_TYPE DHT22
#define BAUD 115200

#define HTTP_PORT 80
#define SSID_MAX_LENGTH 32
#define PASSWORD_MAX_LENGTH 63

const char* SSID = "FRITZ!Box 7530 LZ";
const char* PSK = "51318071831496347138";

const char* OWN_SSID = "WLAN-ESP"; // max 
const char* OWN_PWD = "WLAN-ESP"; // min 8 char  or NULL

int address = 0;
uint8_t value;

int counter = 0;

const int DHT_PIN = 5;

DHT dht(DHT_PIN, DHT_TYPE);

ESP8266WebServer server(HTTP_PORT);

void setup() {
  Serial.begin(BAUD);

  dht.begin();
  // initialize inbuilt LED pin as an output.
  pinMode(LED_BUILTIN, OUTPUT);

  digitalWrite(LED_BUILTIN, HIGH);
	initWiFi();

  EEPROM.begin(4);

  initServer();

  Serial.println("start program");
}

void loop()
{
  Serial.println("loop");

  server.handleClient();

  if(!isWifiConnected())
  {
    if(counter++ >= 10)
    {
      counter = 0;
      connectWiFi();
    }
  }

  float h = dht.readHumidity();
	float t = dht.readTemperature();
  value = EEPROM.read(address);
  Serial.print("Temperatur: ");
  Serial.println(t);
  Serial.print("Feuchtigkeit: ");
  Serial.println(h);
  Serial.print("value: ");
  Serial.println(value);
  
  delay(1000);

  if(value == 0)
  {
    EEPROM.write(address, 1);
    Serial.println("write EEPROM to 1");
    if(EEPROM.commit())
    {
      Serial.println("commit succeeded");
    }
    else
    {
      Serial.println("commit failed");
    }
  }
  /*
  blink(1, 1000);
  Serial.print("Wert: ");
  Serial.println(value);

  EEPROM.write(address, value + 1);

  WiFiClient client = server.available();
	if(!client){
		return;
	}

  blink(1, 1000);
	
	if(WiFi.status() != WL_CONNECTED){
		initWiFi(); 
	}
	
  blink(1, 1000);
	String request = client.readStringUntil('\r');
  blink(1, 1000);
	client.flush();
	if(request==""){

    blink(2, 500);

		client.stop();
		return;
	}

  blink(5, 50);
	//Ausgabe erzeugen
	String output;
  output += "<html>";
	output += "<h1>Temperatur: </h1>";
  //output += String(t);
	output += "<br>";
  output += "<h2>Luftfeuchtigkeit: </h2>";
  //output += String(h);
  output += "</html>";
	client.print(output);
	client.stop();

  blink(3, 100);
  */
}

void blink(int amount, int delta)
{
  for(int i = 0; i < amount; i++)
    {
      digitalWrite(LED_BUILTIN, HIGH);
      delay(delta);
      digitalWrite(LED_BUILTIN, LOW);
      delay(delta);
    }

    digitalWrite(LED_BUILTIN, HIGH);
}

void initWiFi()
{
  Serial.println("Init WiFi");

  // enable connection to a network (STAtion) and an own network (AP) as well
  WiFi.mode(WIFI_AP_STA);

  IPAddress localIP(192,168,1,1);
  IPAddress gateway(192,168,1,0);
  IPAddress subnet(255,255,255,0);

  WiFi.softAPConfig(localIP, gateway, subnet);

  WiFi.softAP(OWN_SSID, OWN_PWD);

  IPAddress IP = WiFi.softAPIP();
  Serial.print("AP IP address: ");
  Serial.println(IP);

  //WiFi.softAPdisconnect(wifioff);
}

void connectWiFi()
{
  Serial.println("connect Wifi");

  //Wifi.disconnect();
	WiFi.begin(SSID, PSK);
	
	if(isWifiConnected())
  {
    Serial.print("My IP: ");
    Serial.println(WiFi.localIP());
	}
}

inline bool isWifiConnected()
{
  return WiFi.status() == WL_CONNECTED;
}

void initServer()
{
  server.on("/", handle_root);
  server.on("/test", handle_test);
  server.begin();
}

void handle_root()
{
  // get body
  //server.arg("plain");
  Serial.println("received request");
  float h = dht.readHumidity();
	float t = dht.readTemperature();

  String data = "{\"humidity\": ";
  data += h;
  data +=", \"temperature\": ";
  data += t;
  data +="}";

  server.send(200, "text/json", data);
}

void handle_test()
{
  Serial.println("received request test");

  String s1 = server.arg("test1");
  String s2 = server.arg("test2");

  Serial.print("test1: ");
  Serial.println(s1);
  Serial.print("test2: ");
  Serial.println(s2);
}