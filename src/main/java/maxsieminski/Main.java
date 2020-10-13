package maxsieminski;

public class Main {
    public static void main(String[] args) throws InterruptedException {

        MqttAdmin admin = new MqttAdmin(10);
        MqttPidController pidController = new MqttPidController(new PID(0.85, 2.5, 0.0001, 10.0));

        Thread adminThread = new Thread(admin::initializeAdministrator);
        Thread pidThread = new Thread(pidController::getSetPoint);

        Thread pidOut = new Thread(pidController::executePID);
        Thread adminOut = new Thread(admin::getPidOutput);

        adminThread.start();
        pidThread.start();

        pidOut.start();
        adminOut.start();

        adminThread.join();
        pidThread.join();

//        PID pid = new PID(0.85, 2.5, 0.0001, 10.0);
//
//        double pidFeedback;
//        double output = 0;
//
//        for(int i = 0; i < 100; i++) {
//            pid.execute(output);
//            pidFeedback = pid.getOutput();
//
//            if (pid.getSetPoint() > 0) {
//                output += pidFeedback;
//                System.out.printf("SETPOINT : %f    OUTPUT : %f\n", pid.getSetPoint(), output);
//            }
//            try {
//                Thread.sleep(200);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        }
    }
}
