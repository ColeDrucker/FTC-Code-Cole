package org.firstinspires.ftc.teamcode.Autonomous;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.Logic.AutonomousLogic.AprilTag;

@Autonomous(name="april tag vision test")
public class VisionTest extends LinearOpMode {
    AprilTag aprilTag = new AprilTag();
    @Override
    public void runOpMode() throws InterruptedException {
        aprilTag.init(hardwareMap, telemetry);

        aprilTag.startScanning();
        aprilTag.gameStarted();

        waitForStart();
        aprilTag.stopScanning();
    }
}