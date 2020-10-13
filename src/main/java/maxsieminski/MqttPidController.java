package maxsieminski;

import com.hivemq.client.mqtt.mqtt5.Mqtt5Client;
import java.util.concurrent.TimeUnit;

public class MqttPidController extends Thread {

    private final PID pid;
    private double setPoint;

    final Mqtt5Client listener = Mqtt5Client.builder()
            .identifier("example-pid-controller")
            .serverHost("broker.hivemq.com")
            .automaticReconnectWithDefaultConfig()
            .build();

    public MqttPidController(PID pid) {
        this.pid = pid;

        listener.toBlocking().connectWith()
                .cleanStart(false)
                .sessionExpiryInterval(TimeUnit.HOURS.toSeconds(1))
                .send();

    }

    private double convertAndSavePayload(byte[] array) {
        StringBuilder charArray = new StringBuilder();

        for (byte b : array) {
            charArray.append((char) b);
        }
        setPoint = Double.parseDouble(charArray.toString());
        return setPoint;
    }

    public void getSetPoint() {
        listener.toAsync().subscribeWith()
                .topicFilter("home/maxsi/pidadmin")
                .callback(publish ->
                        System.out.println("Found setpoint on topic " + publish.getTopic() + ": " +
                                convertAndSavePayload(publish.getPayloadAsBytes()))).send();
    }

    public void executePID() {
        double pidFeedback, output = 0;

        for(int i = 0; i < 50; i++) {
            pid.execute(output);
            pidFeedback = pid.getOutput();

            if (pid.getSetPoint() > 0) {
                output += pidFeedback;
                System.out.printf("SETPOINT : %f    OUTPUT : %f\n", pid.getSetPoint(), output);
                listener.toBlocking().publishWith().topic("home/maxsi/pidoutput").payload(Double.toString(output).getBytes()).send();
            }
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
