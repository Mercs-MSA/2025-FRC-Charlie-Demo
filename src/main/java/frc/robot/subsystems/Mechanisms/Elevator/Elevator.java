package frc.robot.subsystems.Mechanisms.Elevator;

import com.ctre.phoenix6.hardware.TalonFX;
import com.ctre.phoenix6.signals.FeedbackSensorSourceValue;
import com.ctre.phoenix6.signals.SensorDirectionValue;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.Constants.Elevator1Constants;
import frc.robot.Constants.Elevator2Constants;
import frc.robot.Constants.elevatorMMConstants;
import com.ctre.phoenix6.configs.CANcoderConfiguration;
import com.ctre.phoenix6.hardware.CANcoder;
import frc.robot.subsystems.SubsystemUtils.CanDeviceId;
// import frc.robot.subsystems.SensorSubsystems.ElevatorBeambreak;
import frc.robot.subsystems.SubsystemUtils.SubsystemLib;
import frc.robot.subsystems.SubsystemUtils.TalonFXFactory;
import frc.robot.Constants;


public class Elevator extends SubsystemLib {
    public class TestSubsystemConfig extends Config {
     
        public final int CANcoderID = 62;

        public final double velocityKp = Elevator1Constants.kP;
        public final double velocityKs = Elevator1Constants.kS;
        public final double velocityKv = 0;

        public TestSubsystemConfig() {
            super("ELevatorMotor1", Elevator1Constants.id, "rio");  //It is on rio, but make sure that you change the id
            configPIDGains(velocityKp, 0, 0);
            configForwardGains(velocityKs, velocityKv, 0, 0);
            configGearRatio(1);
            configNeutralBrakeMode(true);
            configFeedbackSensorSource(FeedbackSensorSourceValue.RemoteCANcoder);
            configFeedbackSensorID(CANcoderID);
            isClockwise(false); //true if you want it to spin clockwise
            configMotionMagic(elevatorMMConstants.speed, elevatorMMConstants.acceleration, elevatorMMConstants.jerk);
     
        }
    }


    public static boolean isPositioning = false;
    public static double positionTo = 0;


    public TestSubsystemConfig config;

    private boolean hasTared = false;

    public TalonFX followerMotor; // Declare the follower motor



    // }
    public Elevator(boolean attached){
        super(attached);
        if(attached){
            motor = TalonFXFactory.createConfigTalon(config.id, config.talonConfig);
            followerMotor = TalonFXFactory.createPermanentFollowerTalon(new CanDeviceId(Elevator2Constants.id, "rio"), motor, true);
        }

    }

    public void motorToPosMM(double pos) {
        isPositioning = true;
        positionTo = pos;
        setMMPosition(pos);
    }

    public void elevatorToPos(double pos){
        SetPositionVoltage(pos);
    }

    public double elev1MotorGetPosition() {
        return GetPosition();
    }

    
   

    @Override
    protected Config setConfig() {
        config = new TestSubsystemConfig();
        return config;
    }


    @Override 
    public void periodic(){

      

        // if (isPositioning)
        // {
        //     if (Constants.isWithinTol(positionTo, elev1MotorGetPosition(), 0.5)) {isPositioning = false;}
        // }

        SmartDashboard.putNumber("elevator Pos", motor.getPosition().getValueAsDouble());

    }

    

}