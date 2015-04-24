# mqtt_gpb_client
MQTT client for parsing GPB message packets

The intent of this sample set of codes is to guide would-be third-party
developers that intend to consume SPoT/vSPoT data via Push API over MQTT with
messages packed in Google Protobuf format.

Pre-requisites to get started:
------------------------------
1. A Unix compatible Operating System (Debian Wheezy, CentOS 6.6 or Mac OSX 10.10)
1. Download and install a compatible JDK version 1.7 (OpenJDK7, Oracle JDK 1.7.0u76, etc) (http://www.oracle.com/technetwork/java/javase/downloads/jdk7-downloads-1880260.html) [works with JDK8 as well]
1. Download and install gradle 2.3.x (https://gradle.org/)
1. Download and install mosquitto 1.3.5 or 1.4.1 and above broker, client and requisite libraries (http://mosquitto.org/download/)
1. Open firewall to ports 1883 and 8883
1. Startup local mosquitto broker using the sample mosquitto bridge configuration file under mqtt_bridge_conf directory as a starting point (the TLS PSK connectivity is provided via MQTT bridge connection to reduce client code complexity)
1. Test that you can locally subscribe to the mqtt-bridged TLS PSK connection to venue server using the `mosquitto_sub` utility
   1. e.g. `mosquitto_sub -p 1883 -t "+/LOC/SPoT_GPB/#"`
1. You should see encoded messages streaming into your standard output terminal if the above mqtt bridge setup and topic subscription connection works

Compile Google Protobuf Binding classes or source files to begin your client development:
-----------------------------------------------------------------------------------------
1. Ensure that gradle 2.3 is installed and configured to work with JDK7 and that you have internet connectivity on the development machine
1. Change directory into protobuf_bindings
1. Issue `gradle` and observe that you receive no error messages in your standard output terminal
1. Compiled binding classes / source files for C++, java and python should now be generated and available under
   1. C++   : protobuf_bindings/build/generated/cpp
   1. java  : protobuf_bindings/build/generated/java
   1. python: protobuf_bindings/build/generated/python
1. Copy the requisite technology stack GPB binding classes / source files to your Push API client project source folder (under the intended directory structure)

Compile and run sample Push API Java client:
--------------------------------------------
1. Ensure that gradle 2.3 is installed and configured to work with JDK7 and that you have internet connectivity on the development machine
1. Change directory into sample_clients/pushapi_sample_java_client
1. Issue `gradle` and observe that you receive no error messages in your standard output terminal
1. Compiled and executable code is available at sample_clients/pushapi_sample_java_client/build/install/pushapi_sample_java_client directory
1. Ensure local mqtt bridged broker is setup and running (see above section "Pre-requisites to get started" - get the relevant venue server FQDN, PSK Identity and PSK to correctly bridge to the Push API MQTT publisher)
1. Change directory to sample_clients/pushapi_sample_java_client/build/install/pushapi_sample_java_client
1. Startup the client using `bin/pushapi_sample_java_client`
1. Observe the incoming MQTT decoded GPB messages received from the Push API MQTT publisher streaming in your standard output terminal
   1. Sample data stream:
      ```ruby
      org.fusesource.mqtt.client.Message@204f30ec
      locations {
        venue_id: "rksg-dev"
        mac: "E8802EF065CB"
        x: 0.0
        y: 0.0
        floor_number: 0
        timestamp: 1429003890
      }

      org.fusesource.mqtt.client.Message@e25b2fe
      locations {
        venue_id: "rksg-dev"
        mac: "FCE9982A9DB0"
        x: 1164.999
        y: 693.9553
        floor_number: 1
        timestamp: 1429003890
      }
      ```
