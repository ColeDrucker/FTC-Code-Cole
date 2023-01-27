package org.firstinspires.ftc.teamcode.Autonomous;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.teamcode.Systems.Autonomous.Threaded_Motor;
import org.firstinspires.ftc.teamcode.Systems.Logic_Base;
import org.firstinspires.ftc.teamcode.Systems.RobotHardware;

@Autonomous(name = "Autonomous 201")
public class Auton201 extends LinearOpMode {

    RobotHardware r = new RobotHardware();

    @Override
    public void runOpMode() throws InterruptedException {
        r.init(hardwareMap, telemetry);

        waitForStart();
            //strafe to center block
        while (opModeIsActive()) {



            strafe(0.9);

            double time = System.currentTimeMillis() / 1000.0;

            while (System.currentTimeMillis() / 1000.0 - time < 1.15) {
                idle();
            }

           // setSpeed(-0.3);

          //  time = System.currentTimeMillis() / 1000.0;

           // while (System.currentTimeMillis() / 1000.0 - time < 1.15) {
          //      idle();
          //  }


          //  r.turnDegree(190);

           setSpeed(0);

           // time = System.currentTimeMillis() / 1000.0;
            //switch (result) {
            //
            //case 1:
            //  strafe(0.9);

            //  while (System.currentTimeMillis() / 1000.0 - time < 1.15){
            //       idle();
            //   }
            //    break;

            //  case 2:
            //    strafe(0.9);

            //    while (System.currentTimeMillis() / 1000.0 - time < 1.15) {
            //        idle();
            //     }

            //   r.turnDegree(-10);
            //    setSpeed(0.8);

            //   while (System.currentTimeMillis() / 1000.0 - time < 1.15){
            //    }

            // setSpeed(0);
            //  break;

            // case 3:
            // strafe(0.9);

            //  while (System.currentTimeMillis() / 1000.0 - time < 1.15) {
            //      idle();
            //   }

            //   r.turnDegree(-10);
            //  setSpeed(-0.8);

            //  double timex = System.currentTimeMillis() / 1000.0;
            //    while (System.currentTimeMillis() / 1000.0 - timex < 1.15){
            //    }
            //     setSpeed(0);
            //    break;
            //  default:
            //      break;
            //   }

            //  }

//turn cause robot bad
          //  r.turnDegree(-10);

            setSpeed(0);
            setArmTarget(500);
            time = System.currentTimeMillis() / 1000.0;

            while (System.currentTimeMillis() / 1000.0 - time < 0) {
                idle();
            }

            setArmTarget(50);
            time = System.currentTimeMillis() / 1000.0;

            while (System.currentTimeMillis() / 1000.0 - time < 0) {
                idle();
            }


            stop();
            idle();
            stop();

            //throw new IllegalArgumentException("lol");
            //a.quit();
            //stop();
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

    public void setArmTarget(int target) {
       // LeftMotor.set_position(target);
       // RightMotor.set_position(-target);
    }

}