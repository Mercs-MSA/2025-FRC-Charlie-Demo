// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import static edu.wpi.first.units.Units.*;

import java.util.HashMap;
import java.util.Map;

import com.ctre.phoenix6.swerve.SwerveModule.DriveRequestType;
import com.ctre.phoenix6.swerve.SwerveRequest;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import com.pathplanner.lib.auto.AutoBuilder;
import com.pathplanner.lib.auto.NamedCommands;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj2.command.ParallelCommandGroup;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import edu.wpi.first.wpilibj2.command.WaitCommand;
import edu.wpi.first.wpilibj2.command.button.CommandXboxController;
import edu.wpi.first.wpilibj2.command.sysid.SysIdRoutine.Direction;
import frc.robot.commands.CANdleCommands.CommandCandleSetAnimation;
import frc.robot.commands.CANdleCommands.CommandCandleSetCustomAnim;
// import frc.robot.commands.*;
// import frc.robot.commands.CANdleCommands.CommandCandleSetAnimation;
import frc.robot.commands.ClimberCommands.CommandClimbToggle;
import frc.robot.commands.DriveToPosCommands.CommandLoadDriveToPos;
import frc.robot.commands.DriveToPosCommands.CommandSetDriveToPos;
import frc.robot.commands.DriveToPosCommands.CommandToPos;
import frc.robot.commands.ElevatorCommands.CommandElevatorToStage;
import frc.robot.commands.FunnelCommands.CommandFunnelPivotToPos;
import frc.robot.commands.FunnelCommands.CommandFunnelToggle;
import frc.robot.commands.IntakeCommands.CommandIntakeCollect;
import frc.robot.commands.IntakeCommands.CommandIntakeOut;
import frc.robot.commands.ScoringModeCommands.CommandChangeScoreStage;
import frc.robot.commands.VisionCommands.SeedToMegaTag;
import frc.robot.Constants.ScoringStageVal;
import frc.robot.subsystems.Mechanisms.Climber.Climber;
import frc.robot.subsystems.Mechanisms.Elevator.Elevator;
import frc.robot.subsystems.Mechanisms.Funnel.FunnelPivot;
import frc.robot.subsystems.Mechanisms.Intake.IntakeFlywheels;
// import frc.robot.subsystems.SensorSubsystems.CANdle_LED;
import frc.robot.subsystems.SensorSubsystems.IntakeBeambreak;
import frc.robot.subsystems.SensorSubsystems.CustomAnim.CustomAnimation;
import frc.robot.subsystems.SensorSubsystems.CANdle_LED;
import frc.robot.subsystems.SensorSubsystems.CustomAnim;
import frc.robot.subsystems.Swerve.CommandSwerveDrivetrain;
import frc.robot.generated.TunerConstants;

public class RobotContainer {
    private double MaxSpeed = TunerConstants.kSpeedAt12Volts.in(MetersPerSecond); // kSpeedAt12Volts desired top speed
    private double MaxAngularRate = RotationsPerSecond.of(0.75).in(RadiansPerSecond); // 3/4 of a rotation per second max angular velocity

    /* Setting up bindings for necessary control of the swerve drive platform */
    private final SwerveRequest.FieldCentric drive = new SwerveRequest.FieldCentric()
            .withDeadband(MaxSpeed * 0.1).withRotationalDeadband(MaxAngularRate * 0.1) // Add a 10% deadband
            .withDriveRequestType(DriveRequestType.OpenLoopVoltage); // Use open-loop control for drive motors
    private final SwerveRequest.SwerveDriveBrake brake = new SwerveRequest.SwerveDriveBrake();
    private final SwerveRequest.PointWheelsAt point = new SwerveRequest.PointWheelsAt();

    private final Telemetry logger = new Telemetry(MaxSpeed);

    private final CommandXboxController driver = new CommandXboxController(0);
    private final CommandXboxController operator = new CommandXboxController(1);


    public final CommandSwerveDrivetrain drivetrain = TunerConstants.createDrivetrain();

    public final Elevator m_Elevator = new Elevator(true);


    public final Climber m_Climber = new Climber(true);

    public final IntakeFlywheels m_IntakeFlywheels = new IntakeFlywheels(true);

    public final IntakeBeambreak m_intakeBeamBreak = new IntakeBeambreak();

    public final FunnelPivot m_FunnelPivot = new FunnelPivot(true);

    public final CANdle_LED m_leds = new CANdle_LED();

    private final SendableChooser<Command> autoChooser;

       Map<String, Command> autonomousCommands = new HashMap<String, Command>() {
        {
            
            // put("Enter Command Name", new Command(m_));

            put("PathWithDriveToPos", new SequentialCommandGroup(
                new CommandSetDriveToPos("ReefTest"),
                new CommandToPos(drivetrain)
            ));


            put("Intake Collect", 
                new CommandIntakeCollect(m_IntakeFlywheels, m_intakeBeamBreak, MaxAngularRate)
                );
            
            /* Scoring */
            put("Score", 
                new CommandIntakeOut(m_IntakeFlywheels, m_intakeBeamBreak, MaxAngularRate)
                );
            
            put("L1", new SequentialCommandGroup(
                new CommandChangeScoreStage(ScoringStageVal.L1),
                new CommandElevatorToStage(m_intakeBeamBreak, m_Elevator)

            ));

            // put("L2", new SequentialCommandGroup(
            //     new CommandChangeScoreStage(ScoringStageVal.L2),
            //     new CommandElevatorToStage(m_intakeBeamBreak, m_Elevator, m_Elevator2)

            // ));

            // put("L3", new SequentialCommandGroup(
            //     new CommandChangeScoreStage(ScoringStageVal.L3),
            //     new CommandElevatorToStage(m_intakeBeamBreak, m_Elevator, m_Elevator2)

            // ));

            // put("L4", new SequentialCommandGroup(
            //     new CommandChangeScoreStage(ScoringStageVal.L4),
            //     new CommandElevatorToStage(m_intakeBeamBreak, m_Elevator, m_Elevator2)
            // ));

            put("ELEVIntakePos", new SequentialCommandGroup(
                new CommandChangeScoreStage(ScoringStageVal.INTAKEREADY),
                new CommandElevatorToStage(m_intakeBeamBreak, m_Elevator)
            ));

            put("MoveFunnel", new SequentialCommandGroup(
                new CommandChangeScoreStage(ScoringStageVal.INTAKEREADY),
                new CommandFunnelPivotToPos(Constants.FunnelPivotConstants.posUp)
            ));

    
    
            
            put("Reset All", new ParallelCommandGroup(
                
            ));
    
        }
        
    };
    public RobotContainer() {
        NamedCommands.registerCommands(autonomousCommands);

        autoChooser = AutoBuilder.buildAutoChooser("Do Nothing");
        SmartDashboard.putData("Auto Mode", autoChooser);
        configureBindings();
        driverControls();
        operatorControls();
        }

    private void configureBindings() {
        // Note that X is defined as forward according to WPILib convention,
        // and Y is defined as to the left according to WPILib convention.
        drivetrain.setDefaultCommand(
            // Drivetrain will execute this command periodically
            drivetrain.applyRequest(() ->
                drive.withVelocityX(-driver.getLeftY() * MaxSpeed) // Drive forward with negative Y (forward)
                    .withVelocityY(-driver.getLeftX() * MaxSpeed) // Drive left with negative X (left)
                    .withRotationalRate(-driver.getRightX() * MaxAngularRate) // Drive counterclockwise with negative X (left)
            )
        );

        driver.a().whileTrue(drivetrain.applyRequest(() -> brake));
        driver.b().whileTrue(drivetrain.applyRequest(() ->
            point.withModuleDirection(new Rotation2d(-driver.getLeftY(), -driver.getLeftX()))
        ));

        // Run SysId routines when holding back/start and X/Y.
        // Note that each routine should be run exactly once in a single log.
        driver.back().and(driver.y()).whileTrue(drivetrain.sysIdDynamic(Direction.kForward));
        driver.back().and(driver.x()).whileTrue(drivetrain.sysIdDynamic(Direction.kReverse));   

        driver.start().and(driver.y()).whileTrue(drivetrain.sysIdQuasistatic(Direction.kForward));
        driver.start().and(driver.x()).whileTrue(drivetrain.sysIdQuasistatic(Direction.kReverse));

        drivetrain.registerTelemetry(logger::telemeterize);

        // reset the field-centric heading on left bumper press
        // driver.leftBumper().onTrue(drivetrain.runOnce(() -> drivetrain.seedFieldCentric()));

    }

        public void driverControls() {

            // driver.x().onTrue(new CommandElevatorToPos(Constants.Elevator1Constants.positionUp));
            // driver.y().onTrue(new CommandElevatorToPos(Constants.Elevator1Constants.positionDown));



            driver.leftBumper().onTrue(new CommandIntakeOut(m_IntakeFlywheels, m_intakeBeamBreak, 8));


            driver.x().onTrue(drivetrain.runOnce(() -> drivetrain.seedFieldCentric()));



            // driver.back().onTrue(new CommandCandleSetCustomAnim(m_leds, new CustomAnim(CustomAnimation.MercsPattern, 5, 0)));
            driver.start().onTrue(new CommandCandleSetAnimation(m_leds, CANdle_LED.AnimationTypes.Twinkle));

            driver.a().whileTrue(
                new CommandSetDriveToPos("Source").andThen(
                new CommandChangeScoreStage(ScoringStageVal.INTAKEREADY)).andThen(
                new ParallelCommandGroup(
                    new CommandToPos(drivetrain), 
                    new CommandElevatorToStage(m_intakeBeamBreak, m_Elevator),
                    new CommandIntakeCollect(m_IntakeFlywheels, m_intakeBeamBreak, 1)
            )));


            driver.leftTrigger(0.8).whileTrue((new CommandLoadDriveToPos(() -> Constants.DriveToPosRuntime.autoTargets.get(0))).andThen(new ParallelCommandGroup (
                new CommandToPos(drivetrain)
                // new CommandElevatorToStage(m_intakeBeamBreak, m_Elevator)
                // new CommandCandleSetAnimation(m_leds, CANdle_LED.AnimationTypes.Strobe)
                )));
            driver.rightTrigger(0.8).whileTrue((new CommandLoadDriveToPos(() -> Constants.DriveToPosRuntime.autoTargets.get(1))).andThen(new ParallelCommandGroup (
                new CommandToPos(drivetrain)
                // new CommandElevatorToStage(m_intakeBeamBreak, m_Elevator)
                )));//keep

            // driver.leftTrigger(0.8).onFalse(new CommandCandleSetAnimation(m_leds, CANdle_LED.AnimationTypes.Twinkle));

            // driver.rightTrigger(0.8).onFalse(new CommandCandleSetAnimation(m_leds, CANdle_LED.AnimationTypes.Twinkle));

            driver.rightBumper().onTrue(new CommandElevatorToStage(m_intakeBeamBreak, m_Elevator));


            // driver.leftTrigger().whileTrue(new CommandSetDriveToPos("ReefTest").andThen(new ParallelCommandGroup (
            //     new CommandToPos(drivetrain),
            //     new CommandElevatorToStage()
            //     )));//keep

          
        }

        public void operatorControls(){


            operator.pov(180).onTrue(new CommandChangeScoreStage(ScoringStageVal.L1));

            operator.pov(270).onTrue(new CommandChangeScoreStage(ScoringStageVal.L2));

            operator.pov(0).onTrue(new CommandChangeScoreStage(ScoringStageVal.L3));

            operator.pov(90).onTrue(new CommandChangeScoreStage(ScoringStageVal.L4));


            operator.a().onTrue(new CommandChangeScoreStage(ScoringStageVal.CLIMBING));

            operator.b().onTrue(new SequentialCommandGroup(
                new CommandChangeScoreStage(ScoringStageVal.INTAKEREADY),
                new CommandElevatorToStage(m_intakeBeamBreak, m_Elevator)));


            operator.leftStick().onTrue(new CommandClimbToggle(m_Climber, m_FunnelPivot));

            operator.rightStick().onTrue(new CommandFunnelToggle(m_FunnelPivot));

            operator.leftBumper().onTrue(new CommandIntakeCollect(m_IntakeFlywheels, m_intakeBeamBreak, 4));


            driver.y().onTrue(new SeedToMegaTag(drivetrain, Constants.VisionConstants.limelightRightName));

            // SmartDashboard.putData("SeedToMegaTag1_Front", new SeedToMegaTag(drivetrain, Constants.VisionConstants.limelightFrontName));
            // SmartDashboard.putData("SeedToMegaTag1_Back", new SeedToMegaTag(drivetrain, Constants.VisionConstants.limelightBackName));

        

        }




    public Command getAutonomousCommand() {
        // return Commands.print("No autonomous command configured");
        return autoChooser.getSelected(); 
    }

}
