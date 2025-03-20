package frc.robot.subsystems.SensorSubsystems;

import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants;

import com.ctre.phoenix.led.*;
import com.ctre.phoenix.led.CANdle.LEDStripType;
import com.ctre.phoenix.led.CANdle.VBatOutputMode;
import com.ctre.phoenix.led.ColorFlowAnimation.Direction;
import com.ctre.phoenix.led.LarsonAnimation.BounceMode;
import com.ctre.phoenix.led.TwinkleAnimation.TwinklePercent;
import com.ctre.phoenix.led.TwinkleOffAnimation.TwinkleOffPercent;

public class CANdle_LED extends SubsystemBase {
    private final CANdle m_candle = new CANdle(29, "rio");
    private final int LedCount = 100; //lower number decreases cycle time interval
    // private XboxController joystick;

    private Animation m_toAnimate = new FireAnimation(0.5, 0.7, LedCount, 0.7, 0.5);
    

    private boolean isRunningCustom = false;

    public enum AnimationTypes {
        ColorFlow,
        Fire,
        Larson,
        Rainbow,
        RgbFade,
        SingleFade,
        SolidWhite,
        StrobeGreen,
        StrobePurple,
        StrobeWhite,
        StrobeRed,
        Twinkle,
        TwinkleOff,
        SetAll
    }

    private AnimationTypes m_currentAnimation;

    public CANdle_LED() {
        // this.joystick = joy;
        // changeAnimation(AnimationTypes.SetAll);
        CANdleConfiguration configAll = new CANdleConfiguration();
        configAll.statusLedOffWhenActive = true;
        configAll.disableWhenLOS = false;
        configAll.stripType = LEDStripType.GRB;
        configAll.brightnessScalar = .3;
        configAll.vBatOutputMode = VBatOutputMode.Modulated;
        m_candle.configAllSettings(configAll, 100);
    }

    public void incrementAnimation() {
        switch(m_currentAnimation) {
            case ColorFlow: changeAnimation(AnimationTypes.SolidWhite); break;
            case SolidWhite: changeAnimation(AnimationTypes.Fire); break;
            case Fire: changeAnimation(AnimationTypes.Larson); break;
            case Larson: changeAnimation(AnimationTypes.Rainbow); break;
            case Rainbow: changeAnimation(AnimationTypes.RgbFade); break;
            case RgbFade: changeAnimation(AnimationTypes.SingleFade); break;
            case SingleFade: changeAnimation(AnimationTypes.StrobeGreen); break;
            case StrobeGreen: changeAnimation(AnimationTypes.Twinkle); break;
            case Twinkle: changeAnimation(AnimationTypes.TwinkleOff); break;
            case TwinkleOff: changeAnimation(AnimationTypes.ColorFlow); break;
            case SetAll: changeAnimation(AnimationTypes.ColorFlow); break;
        }
    }
    public void decrementAnimation() {
        switch(m_currentAnimation) {
            case ColorFlow: changeAnimation(AnimationTypes.TwinkleOff); break;
            case Fire: changeAnimation(AnimationTypes.ColorFlow); break;
            case Larson: changeAnimation(AnimationTypes.Fire); break;
            case Rainbow: changeAnimation(AnimationTypes.Larson); break;
            case RgbFade: changeAnimation(AnimationTypes.Rainbow); break;
            case SingleFade: changeAnimation(AnimationTypes.RgbFade); break;
            case StrobeGreen: changeAnimation(AnimationTypes.SingleFade); break;
            case Twinkle: changeAnimation(AnimationTypes.StrobeGreen); break;
            case TwinkleOff: changeAnimation(AnimationTypes.Twinkle); break;
            case SetAll: changeAnimation(AnimationTypes.ColorFlow); break;
        }
    }

    public void setColors() {
        changeAnimation(AnimationTypes.SetAll);
    }

    /* Wrappers so we can access the CANdle from the subsystem */
    public double getVbat() { return m_candle.getBusVoltage(); }
    public double get5V() { return m_candle.get5VRailVoltage(); }
    public double getCurrent() { return m_candle.getCurrent(); }
    public double getTemperature() { return m_candle.getTemperature(); }
    public void configBrightness(double percent) { m_candle.configBrightnessScalar(percent, 0); }
    public void configLos(boolean disableWhenLos) { m_candle.configLOSBehavior(disableWhenLos, 0); }
    public void configLedType(LEDStripType type) { m_candle.configLEDType(type, 0); }
    public void configStatusLedBehavior(boolean offWhenActive) { m_candle.configStatusLedState(offWhenActive, 0); }

    public void changeAnimation(AnimationTypes toChange) {
        isRunningCustom = false;
        m_currentAnimation = toChange;
        
        switch(toChange)
        {
            case ColorFlow:
                m_toAnimate = new ColorFlowAnimation(128, 20, 70, 0, 0.1, LedCount, Direction.Forward);
                break;
            case SolidWhite:
                m_toAnimate = new StrobeAnimation(255, 255, 255, 0, 0.1, LedCount);
                break;
            case Fire:
                m_toAnimate = new FireAnimation(0.5, 0.7, LedCount, 0.7, 0.5);
                break;
            case Larson:
                m_toAnimate = new LarsonAnimation(255, 0, 0, 0, 0.8, LedCount, BounceMode.Center, 3);
                break;
            case Rainbow:
                m_toAnimate = new RainbowAnimation(1, 0.1, LedCount);
                break;
            case RgbFade:
                m_toAnimate = new RgbFadeAnimation(0.7, 1, LedCount);
                break;
            case SingleFade:
                m_toAnimate = new SingleFadeAnimation(0, 255, 0, 0, 1, LedCount);
                break;
            case StrobeGreen:
                m_toAnimate = new StrobeAnimation(0, 255, 0, 0, 0.5, LedCount);
                break;
            case StrobePurple:
                m_toAnimate = new StrobeAnimation(128, 0, 128, 0, 0.1, LedCount);
                break;
            case StrobeRed:
                m_toAnimate = new StrobeAnimation(255, 0, 0, 0, 0.1, LedCount);
                break;
            case StrobeWhite:
                m_toAnimate = new StrobeAnimation(255, 255, 255, 0, 0.1, LedCount);
                break;
            case Twinkle:
                m_toAnimate = new TwinkleAnimation(255, 10, 10, 0, 1, LedCount, TwinklePercent.Percent42);
                break;
            case TwinkleOff:
                m_toAnimate = new TwinkleOffAnimation(70, 90, 175, 0, 0.8, LedCount, TwinkleOffPercent.Percent100);
                break;
            case SetAll:
                m_toAnimate = null;
                break;
        }
        System.out.println("Changed to " + m_currentAnimation.toString());
    }

    public void setCustomAnim(Animation animation)
    {
        isRunningCustom = true;
    }

    public void stopAnim()
    {
        m_candle.clearAnimation(0);
    }

    @Override
    public void periodic() {
        // This method will be called once per scheduler run
        // if(m_toAnimate == null) {
        //     m_candle.setLEDs((int)(joystick.getLeftTriggerAxis() * 255), 
        //                       (int)(joystick.getRightTriggerAxis() * 255), 
        //                       (int)(joystick.getLeftX() * 255));
        // } else {
        //     m_candle.animate(m_toAnimate);
        // }
        // m_candle.modulateVBatOutput(joystick.getRightY());
        m_candle.animate(m_toAnimate);
        SmartDashboard.putNumber("MAX ANIMATION SLOTS", m_candle.getMaxSimultaneousAnimationCount());
    }  
    
    
    public void setLEDs(int startIndex, int interval, int[] color)
    {
        for (int i = startIndex; i < LedCount; i += interval)
        {
            m_candle.setLEDs(color[0], color[1], color[2], 0, i, 1);
        }
    }


    @Override
    public void simulationPeriodic() {
        // This method will be called once per scheduler run during simulation
    }
}
