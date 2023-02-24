package org.firstinspires.ftc.teamcode.Autonomous;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.teamcode.Logic.AutonomousLogic.AprilTag;
import org.firstinspires.ftc.teamcode.Logic.AutonomousLogic.DriveDirection;
import org.firstinspires.ftc.teamcode.Systems.Autonomous.Threaded_Motor;
import org.firstinspires.ftc.teamcode.Systems.Logic_Base;
import org.firstinspires.ftc.teamcode.Systems.RobotHardware;

@Autonomous(name = "Autonomous 201")
public class Auton201 extends LinearOpMode {

    RobotHardware r = new RobotHardware();
    AprilTag apriltag = new AprilTag();
    Auton201Logic logic = new Auton201Logic();

    // distance from the camera lens to the back of your robot
    public final double camera_from_back = 1.5;
    // how long the robit is
    public final double robot_length = 17.5;

    public final double robot_width = 10;

    // starts on the bottom left corner at all times
    @Override
    public void runOpMode() throws InterruptedException {
        r.init(hardwareMap, telemetry);
        apriltag.init(hardwareMap, telemetry);
        logic.init(hardwareMap, telemetry);

        apriltag.startScanning();

        waitForStart();
        apriltag.gameStarted();
        // center in square
        logic.driveInches(12 - robot_length / 2, DriveDirection.BACKWARD);
        sleep(500);
        logic.driveInches(camera_from_back, DriveDirection.FORWARD);
        sleep(6000);
        // finish scanning

        apriltag.stopScanning();
        int id = apriltag.getMostDetected();
        logic.driveInches(camera_from_back, DriveDirection.BACKWARD);

        telemetry.addData("april tag: ", id);
        telemetry.update();
        sleep(500);

        // center of 3rd square
        logic.driveInches(12 - robot_width / 2 + 48, DriveDirection.RIGHT);

        switch (id) {
            case 1:
                logic.driveInches(24, DriveDirection.FORWARD);
                break;
            case 2:
                break;
            case 3:
                logic.driveInches(24, DriveDirection.BACKWARD);
                break;
        }
    }


    public void setSpeed(double power) {
        for (int i = 0; i < 3; i++) {
            r.wheel_list[i].setPower(power);
        }
    }


    public void strafe(double power) {
        r.wheel_list[0].setPower(-power);
        r.wheel_list[1].setPower(power);
        r.wheel_list[2].setPower(-power);
        r.wheel_list[3].setPower(power);
    }
    public void forward(double power) {
        r.wheel_list[0].setPower(power);
        r.wheel_list[1].setPower(power);
        r.wheel_list[2].setPower(power);
        r.wheel_list[3].setPower(power);
    }

}