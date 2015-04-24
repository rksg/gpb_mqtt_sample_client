package com.ruckuswireless.lbs;

/**
 * Created by yeongsheng on 13/4/15.
 */
import java.net.URISyntaxException;
import java.security.SecureRandom;
import java.security.Provider;
import java.security.Security;
import javax.xml.bind.DatatypeConverter;

import com.ruckuswireless.spot.proto.SpotLocations;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.crypto.tls.PSKTlsClient;
import org.bouncycastle.crypto.tls.TlsPSKIdentity;

import org.fusesource.mqtt.client.*;
import org.fusesource.hawtbuf.UTF8Buffer;

public class PushApiClient {
    public static void main(String[] args){
        try {
            // Setup TLS-PSK
            //TLSPSKClient lbsTLSPSKClient = new TLSPSKClient("rksg-dev.venue.ruckuslbs.com", "rksg-dev".getBytes(), "abcdefghijklmnopqrstuvw".getBytes());

            PushApiClient pac = new PushApiClient();
            // Establish MQTT connection
            FutureConnection mqtt_conn = pac.setupMqttConnection("localhost", 1883);
            // Subscribe to interested topic
            String[] interestedTopics = {"0.1.0/LOC/SPOT_GPB/rksg-dev/GPB_LOCR"};
            pac.subscribeTopics(mqtt_conn, interestedTopics);
            pac.receiveMessages(mqtt_conn);
            // Get the messages.
            while(true) {
                Message msg = pac.receiveMessages(mqtt_conn);
                System.out.println(msg);
                msg.ack();
                SpotLocations.Locations locr = SpotLocations.Locations.parseFrom(msg.getPayload());
                System.out.println(locr);
            }
        }catch(Exception e) {
            System.out.println(e.getStackTrace());
        }
    }

    public FutureConnection setupMqttConnection(String host, int port) throws Exception {
        MQTT mqtt = new MQTT();
        // Attempt to connect to MQTT using the PSK TLS client
        //mqtt.setHost("tlsv1.0://" + host, port);
        mqtt.setHost(host, port);
        FutureConnection connection = mqtt.futureConnection();
        Future<Void> futureMqttConn = connection.connect();
        futureMqttConn.await();
        return connection;
    }

    public void subscribeTopics(FutureConnection connection, String[] topics) throws Exception {
        for(String topic : topics) {
            Future<byte[]> f = connection.subscribe(new Topic[]{new Topic(new UTF8Buffer(topic), QoS.AT_LEAST_ONCE)});
            byte[] qoses = f.await();
        }
    }

    public Message receiveMessages(FutureConnection connection) throws Exception {
        // Begin to receive messages from above topic
        Future<Message> receive = connection.receive();
        Message msg = receive.await();
        return msg;
    }
}
