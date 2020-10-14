package maxsieminski;

import com.hivemq.client.mqtt.mqtt5.Mqtt5Client;
import java.util.concurrent.TimeUnit;

public class MqttAdmin extends Thread {

    private final double PIDSetpoint;
    private double output;

    final Mqtt5Client administrator = Mqtt5Client.builder()
            .identifier("example-admin")
            .serverHost("broker.hivemq.com")
            .automaticReconnectWithDefaultConfig()
            .build();

    public MqttAdmin(double PIDSetpoint) {
        this.PIDSetpoint = PIDSetpoint;
    }

    public void initializeAdministrator() {
        administrator.toBlocking().connectWith()
                .cleanStart(false)
                .sessionExpiryInterval(TimeUnit.HOURS.toSeconds(1))
                .send();

        administrator.toBlocking().publishWith().topic("home/maxsi/pidadmin").payload(Double.toString(PIDSetpoint).getBytes()).send();
        System.out.println("PID Setpoint set to " + PIDSetpoint);
    }

    private double convertPayload(byte[] array) {
        StringBuilder charArray = new StringBuilder();

        for (byte b : array) {
            charArray.append((char) b);
        }

        return Double.parseDouble(charArray.toString());
    }

    public void getPidOutput() {
        administrator.toAsync().subscribeWith()
                .topicFilter("home/maxsi/pidoutput")
                .callback(publish ->
                        System.out.println("Found output on topic " + publish.getTopic() + ": " +
                                convertPayload(publish.getPayloadAsBytes())))
                .send();
    }

    @Override
    public void run() {
        initializeAdministrator();
        getPidOutput();
    }
}
