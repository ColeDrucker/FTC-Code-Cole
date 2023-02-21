package org.firstinspires.ftc.teamcode.Systems.Autonomous;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.Logic.AutonomousLogic.AprilTag;

@Autonomous(name="april tag vision test")
public class Test extends LinearOpMode {
    AprilTag aprilTag = new AprilTag();
    @Override
    public void runOpMode() throws InterruptedException {
        aprilTag.init(hardwareMap, telemetry);

        aprilTag.startScanning();

        waitForStart();
        telemetry.addLine("" + aprilTag.getMostDetected());
        telemetry.update();
        aprilTag.stopScanning();
    }
}
