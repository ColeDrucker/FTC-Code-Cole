package org.firstinspires.ftc.teamcode.TeleOp;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.teamcode.Systems.RoadRunner.StandardTrackingWheelLocalizer;
import org.firstinspires.ftc.teamcode.Systems.Logic_Base;
import org.firstinspires.ftc.teamcode.Systems.RobotHardware;

class TeleOp201Logic extends Logic_Base {

    public void execute_non_driver_controlled() {

        robot.telemetry.addData("Left DCM", robot.dc_motor_list[0].getCurrentPosition());
        robot.telemetry.addData("Right DCM", robot.dc_motor_list[1].getCurrentPosition());
        robot.telemetry.addData("Virtual", robot.servo_list[0].getPosition());
        robot.telemetry.addData("Scissor", robot.servo_list[1].getPosition());
        robot.telemetry.addData("Angle?", robot.getAngle());
        robot.telemetry.addData("Angle V2", current_angle = 0 - robot.getAngle() - zero_angle); //Only different value if not starting robot straight ahead
        robot.telemetry.update();
        //target_positions[1] = target_positions[0]; may be harmful - check
        if (useRoadRunner) {
            position_tracker.update();
        }
        if (buttons[keys.indexOf("operator x")]) throw new IllegalArgumentException("Stop pressing random buttons bruh");
    }

    //Initialization

    public void init() {
        setZeroAngle(0);
        robot.dc_motor_list[1].setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT); //not necessary I think
    }

    public void init(StandardTrackingWheelLocalizer localizer) {
        init();
        initializeRoadRunner(45, 100, 90, localizer);
    }

    public void set_keybinds() {

        //arm
        new_keybind("Left", "operator left_stick_y", "default", 0.5, 0.8);
        new_keybind("Right", "operator left_stick_y", "default", 0.5, 0.8);

        //V4B
        new_keybind("Virtual", "operator right_stick_y", "default", 1.0, 1.0);

        //Scissor
        new_keybind("Scissor", "operator a", "default", 0.2, 0);
        new_keybind("Scissor", "operator b", "default", -0.2, 0);
        //new_keybind("Scissor", "operator a", "cycle", 1, new double[] {low, high});

    }

    public TeleOp201Logic(RobotHardware r) {
        super(r);
        set_keybinds();
        set_button_types();
    }
}

@TeleOp(name="TeleOp", group="Iterative Opmode")
public class TeleOp201 extends LinearOpMode {
    RobotHardware robot = new RobotHardware();
    TeleOp201Logic logic = new TeleOp201Logic(robot);
    @Override
    public void runOpMode() throws InterruptedException {
        robot.init(hardwareMap, telemetry);
        waitForStart();
        if (logic.useRoadRunner) {
            StandardTrackingWheelLocalizer localizer = new StandardTrackingWheelLocalizer(hardwareMap);
            logic.init(localizer);
        } else {
            logic.init();
        }
        while (opModeIsActive()) {
            logic.execute_controllers(gamepad1, gamepad2); //driver is gamepad1, operator is gamepad2
            logic.execute_non_driver_controlled();
        }
    }
}