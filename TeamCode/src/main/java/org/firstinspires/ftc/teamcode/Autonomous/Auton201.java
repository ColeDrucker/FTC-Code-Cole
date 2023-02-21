package org.firstinspires.ftc.teamcode.Autonomous;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.teamcode.Logic.AutonomousLogic.AprilTag;
import org.firstinspires.ftc.teamcode.Systems.Autonomous.Threaded_Motor;
import org.firstinspires.ftc.teamcode.Systems.Logic_Base;
import org.firstinspires.ftc.teamcode.Systems.RobotHardware;

@Autonomous(name = "Autonomous 201")
public class Auton201 extends LinearOpMode {

    RobotHardware r = new RobotHardware();
    AprilTag apriltag = new AprilTag();

    @Override
    public void runOpMode() throws InterruptedException {
        r.init(hardwareMap, telemetry);
        apriltag.init(hardwareMap, telemetry);

        apriltag.startScanning();

        waitForStart();


        sleep(3000);

        apriltag.stopScanning();
        int id = apriltag.getMostDetected();

        telemetry.addData("april tag: ", id);
        telemetry.update();

        if (id == 1) {
            strafe(0.455);
            sleep(1360);
            forward(0.455);
            sleep(100);
            setSpeed(0);

        } else if (id == 2) {
            strafe(0.455);
            sleep(1360);
           setSpeed(0);

        } else {

        }
        sleep(1000);
        setSpeed(0);

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