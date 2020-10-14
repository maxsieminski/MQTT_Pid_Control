package maxsieminski;

public class Main {
    public static void main(String[] args) throws InterruptedException {

        Thread adminThread = new MqttAdmin(10);
        Thread pidThread = new MqttPidController(new PID(0.85, 2.5, 0.0001));

        adminThread.start();
        pidThread.start();

        adminThread.join();
        pidThread.join();
    }
}
