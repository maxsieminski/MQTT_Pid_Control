package maxsieminski;

public class PID {
    private double output;
    private final double P;
    private final double I;
    private final double D;
    private final double setPoint;
    private double pGain, iGain, dGain;
    private double currentTime;
    private double savedTime;
    private double savedError;

    public PID(double p, double i, double d, double setPoint) {
        this.P = p;
        this.I = i;
        this.D = d;
        this.setPoint = setPoint;

        currentTime = (double)System.currentTimeMillis() / 1000;
        System.out.println(currentTime);

        pGain = 0.0;
        iGain = 0.0;
        dGain = 0.0;

        savedError = 0.0;
        output = 0.0;
    }

    public double getOutput() {
        return output;
    }

    public double getSetPoint() {
        return setPoint;
    }

    public void execute(double output) {
        currentTime = (double)System.currentTimeMillis() / 1000;

        double outputError = setPoint - output;

        double errorDiff = outputError - savedError;
        double timeDiff = currentTime - savedTime;

        double sampleTime = 0.01;

        if (timeDiff >= sampleTime) {
            pGain = P * outputError;
            iGain += outputError * timeDiff;

            // Windup protection - if Integral value is too high,
            // it is forced down to prevent windup.
            double windupShield = 10.0;
            if (iGain < -windupShield) {
                iGain = -windupShield;
            }
            else if(iGain > windupShield) {
                iGain = windupShield;
            }

            dGain = 0.0;

            if (timeDiff > 0) {
                dGain = errorDiff / timeDiff;
            }

            savedTime = currentTime;
            savedError = outputError;

            this.output = pGain + (I * iGain) + (D * dGain);
        }
    }
}
