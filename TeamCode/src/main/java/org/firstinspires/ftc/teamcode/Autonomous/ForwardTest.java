package org.firstinspires.ftc.teamcode.Autonomous;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.Logic.AutonomousLogic.DriveDirection;

@Autonomous(name="test forward")
public class ForwardTest extends LinearOpMode {
    @Override
    public void runOpMode() {
        Auton201Logic logic = new Auton201Logic();
        logic.init(hardwareMap, telemetry);

        waitForStart();

        logic.driveInches(24, DriveDirection.FORWARD);

        sleep(1000);
    }
}