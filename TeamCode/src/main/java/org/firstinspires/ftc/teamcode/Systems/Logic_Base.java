package org.firstinspires.ftc.teamcode.Systems;

import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.qualcomm.robotcore.hardware.Gamepad;

import org.firstinspires.ftc.teamcode.Systems.StandardTrackingWheelLocalizer;
import org.firstinspires.ftc.teamcode.Robot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Logic_Base implements Robot {

    public RobotHardware robot;
    public StandardTrackingWheelLocalizer position_tracker;

    public HashMap<String, ArrayList<Object>> keybinds = new HashMap<>();
    public String[] button_types = new String[27];

    public double[] times_started = new double[dc_motor_names.size() + servo_names.size()]; //in seconds
    public double[] target_positions = new double[dc_motor_names.size() + servo_names.size()];
    public double[] starting_positions = new double[dc_motor_names.size() + servo_names.size()]; //never use for dc_motors

    public int[] key_values = new int[27];
    public boolean[] buttons = new boolean[20];
    public double[] axes = new double[7];

    public double current_x = 0;
    public double current_y = 0;
    public double current_angle = 0;

    public double target_x = 0;
    public double target_y = 0;
    public double target_angle = 0;

    public double zero_angle = 0;

    public double current_error;
    public double previous_error;

    public long current_time;
    public long previous_time;

    public Logic_Base(RobotHardware r) {
        robot = r;
    }

    public void execute_controllers(Gamepad gamepad1, Gamepad gamepad2) {
        drive(gamepad1);
        update_buttons(gamepad1, gamepad2);
        update_robot();
    }

    public void update_button(boolean button_pressed, String button_name) {
        boolean button_active;
        int temp = keys.indexOf(button_name);
        if (("toggle").equals(button_types[temp])) {
            key_values[temp] += (button_pressed == (key_values[temp] % 2 == 0)) ? 1 : 0;
            button_active = (key_values[temp] % 4 != 0);
        } else if (("default").equals(button_types[temp])) {
            button_active = button_pressed;
        } else {
            button_active = (key_values[temp] % 2 == 0) && (button_pressed);
            key_values[temp] += (button_pressed == (key_values[temp] % 2 == 0)) ? 1 : 0;
        }
        buttons[temp] = button_active;
    }

    public void update_axis(double axis, String axis_name) {
        double axis_value;
        int temp = keys.indexOf(axis_name);
        if (("toggle").equals(button_types[temp])) {
            key_values[temp] += ((Math.abs(axis) > 0.1) == (key_values[temp] % 2 == 0)) ? 1 : 0;
            axis_value = key_values[temp] % 4 != 0 ? 1 : 0;
        } else if (("default").equals(button_types[temp])) {
            axis_value = axis;
        } else {
            axis_value = (key_values[temp] % 2 == 0) && (Math.abs(axis) > 0.1) ? 1 : 0;
            key_values[temp] += ((Math.abs(axis) > 0.1) == (key_values[temp] % 2 == 0)) ? 1 : 0;
        }
        axes[temp - 20] = axis_value;
    }

    public void update_buttons(Gamepad gamepad1, Gamepad gamepad2) {
        update_button(gamepad1.a, "driver a");
        update_button(gamepad1.b, "driver b");
        update_button(gamepad1.x, "driver x");
        update_button(gamepad1.y, "driver y");
        update_button(gamepad1.dpad_up, "driver dpad_up");
        update_button(gamepad1.dpad_down, "driver dpad_down");
        update_button(gamepad1.dpad_left, "driver dpad_left");
        update_button(gamepad1.dpad_right, "driver dpad_right");
        update_button(gamepad1.left_bumper, "driver left_bumper");
        update_button(gamepad1.right_bumper, "driver right_bumper");
        update_axis(gamepad1.left_trigger, "driver left_trigger");

        update_button(gamepad2.a, "operator a");
        update_button(gamepad2.b, "operator b");
        update_button(gamepad2.x, "operator x");
        update_button(gamepad2.y, "operator y");
        update_button(gamepad2.dpad_up, "operator dpad_up");
        update_button(gamepad2.dpad_down, "operator dpad_down");
        update_button(gamepad2.dpad_left, "operator dpad_left");
        update_button(gamepad2.dpad_right, "operator dpad_right");
        update_button(gamepad2.left_bumper, "operator left_bumper");
        update_button(gamepad2.right_bumper, "operator right_bumper");
        update_axis(gamepad2.left_stick_x, "operator left_stick_x");
        update_axis(gamepad2.left_stick_y, "operator left_stick_y");
        update_axis(gamepad2.right_stick_x, "operator right_stick_x");
        update_axis(gamepad2.right_stick_y, "operator right_stick_y");
        update_axis(gamepad2.left_trigger, "operator left_trigger");
        update_axis(gamepad2.right_trigger, "operator right_trigger");
    }

    public void update_robot() {
        for (Map.Entry<String, ArrayList<Object>> element : keybinds.entrySet()) { //for every element in keybinds

            ArrayList<Object> object_keys = element.getValue(); //object_keys = what the motor maps to
            int number_of_keys = object_keys.size() / 4; //number of keys that map to the motor
            boolean object_is_active = false; //object is active iff at least one key that maps to it is activated
            int temp_0;
            int temp_1;
            int temp_2;
            int temp_3;
            double temp_4;
            boolean increasing;

            for (int i = 0; i < number_of_keys; i++) { //for every key that maps to the button
                temp_0 = keys.indexOf((String) object_keys.get(4 * i));
                object_is_active = ((object_is_active) || ((temp_0 < 20) && (buttons[temp_0])) || ((temp_0 > 19) && (Math.abs(axes[temp_0 - 20]) > 0.1)));
            }

            if (dc_motor_names.contains(element.getKey())) { //if it's a dc motor

                temp_1 = dc_motor_names.indexOf(element.getKey()); //temp_1 = where the index is everywhere - it's all on the same naming system

                if (!object_is_active) { //if we aren't pressing any relevant buttons
                    times_started[temp_1] = -10.0; //reset it and make sure its staying where we want it to
                    robot.dc_motor_list[temp_1].setPower(Math.max(min_power[temp_1], Math.min((target_positions[temp_1] - robot.dc_motor_list[temp_1].getCurrentPosition()) * 0.01, max_power[temp_1])));
                } else {

                    if (times_started[temp_1] < 0) //if we're on and it's reset, un-reset it
                        times_started[temp_1] = (double) System.nanoTime() / 1000000000.0;

                    for (int i = 0; i < number_of_keys; i++) { //4 * i: button name; +1: button/default/toggle;
                        //+2: normal/gradient /  how much do we increase / power on way up
                        //+3: maximum power / position list / power on way down

                        temp_0 = keys.indexOf((String) object_keys.get(4 * i)); //where button is in list of keys; < 20 -> button, >= 20 -> axis

                        if ((temp_0 < 20 && buttons[temp_0]) || (temp_0 > 19 && Math.abs(axes[temp_0 - 20]) > 0.1)) {
                            if ((((String) object_keys.get(4 * i + 1)).equals("button")) || (((String) object_keys.get(4 * i + 1)).equals("cycle"))) { //4 * i + 2: what we change by; 4 * i + 3: positions
                                if (((int[]) object_keys.get(4 * i + 3)).length == 1) {
                                    target_positions[temp_1] = ((int[]) object_keys.get(4 * i + 3))[0];
                                } else {
                                    increasing = (((int[]) object_keys.get(4 * i + 3))[1] > ((int[]) object_keys.get(4 * i + 3))[0]);
                                    temp_3 = 0;
                                    while ((temp_3 < ((int[]) object_keys.get(4 * i + 3)).length) && ((((int[]) object_keys.get(4 * i + 3))[temp_3] < target_positions[temp_1]) || (!increasing)) && ((((int[]) object_keys.get(4 * i + 3))[temp_3] > target_positions[temp_1]) || (increasing))) {
                                        temp_3 += 1; //note it stops perfectly if it's equal, it lands one past if it skips over value
                                        //if increasing, then increase while index is less
                                    }
                                    if (((int) object_keys.get(4 * i + 2) > 0) && ((temp_3 + 1 > ((int[]) object_keys.get(4 * i + 3)).length)  || ((double) (((int[]) object_keys.get(4 * i + 3))[temp_3]) != target_positions[temp_1]))) {
                                        temp_3 -= 1; //subtract one if we're going up
                                    }
                                    if (((String) object_keys.get(4 * i + 1)).equals("cycle")) {
                                        if ((temp_3 + 2 > ((int[]) object_keys.get(4 * i + 3)).length) && ((int) object_keys.get(4 * i + 2) > 0)) {
                                            temp_3 = 0;
                                        } else if ((temp_3 < 1) && ((int) object_keys.get(4 * i + 2) < 0)) {
                                            temp_3 = ((int[]) object_keys.get(4 * i + 3)).length - 1;
                                        } else {
                                            temp_3 = Math.max(0, Math.min(temp_3 + (int) object_keys.get(4 * i + 2), ((int[]) object_keys.get(4 * i + 3)).length - 1));
                                        }
                                    } else {
                                        temp_3 = Math.max(0, Math.min(temp_3 + (int) object_keys.get(4 * i + 2), ((int[]) object_keys.get(4 * i + 3)).length - 1));
                                    }
                                    target_positions[temp_1] = ((int[]) object_keys.get(4 * i + 3))[temp_3]; //change the target position
                                }
                            } else {
                                if ((((String) object_keys.get(4 * i + 1)).equals("toggle")) || temp_0 < 20) {
                                    temp_4 = ((double) object_keys.get(4 * i + 3)) * (
                                            ((String) object_keys.get(4 * i + 2)).equals("normal") ? 1 : Math.min(1, ((double) System.nanoTime() / 1000000000.0 - times_started[temp_1]) / 0.75)
                                    );
                                } else {
                                    temp_4 = axes[temp_0 - 20] * ( //similar to button defaults, except no gradient option
                                            (temp_0 > 23) ? (double) object_keys.get(4 * i + 2) : //if it's a trigger, then set it to the first val
                                                    (temp_0 < 22) ? (axes[temp_0 - 20] < 0 ? (double) object_keys.get(4 * i + 2) : (double) object_keys.get(4 * i + 3)) : //it's x
                                                            -1 * (axes[temp_0 - 20] > 0 ? (double) object_keys.get(4 * i + 2) : (double) object_keys.get(4 * i + 3)) //else it's a y
                                    );
                                }
                                if ((robot.dc_motor_list[temp_1].getCurrentPosition() > motor_max_positions[temp_1]) && (temp_4 > 0)) {
                                    temp_4 = Math.max(min_power[temp_1], (motor_max_positions[temp_1] - robot.dc_motor_list[temp_1].getCurrentPosition()) * 0.01);
                                } else if (robot.dc_motor_list[temp_1].getCurrentPosition() < motor_min_positions[temp_1] && (temp_4 < 0)) {
                                    temp_4 = Math.min((motor_min_positions[temp_1] - robot.dc_motor_list[temp_1].getCurrentPosition()) * 0.01, max_power[temp_1]);
                                }
                                robot.dc_motor_list[temp_1].setPower(temp_4);
                                target_positions[temp_1] = Math.min(Math.max(robot.dc_motor_list[temp_1].getCurrentPosition(), motor_min_positions[temp_1]), motor_max_positions[temp_1]); //only update target position if we're moving - don't update if we are not
                            }
                        }
                    }
                }

            } else if (servo_names.contains(element.getKey())) { //servo

                temp_1 = servo_names.indexOf(element.getKey()) + dc_motor_names.size();
                temp_2 = servo_names.indexOf(element.getKey()); //for servos, theres 2 different things:
                //temp_2 used for getting/setting position of servo, temp_1 for everything else

                if (!object_is_active) {
                    times_started[temp_1] = -10.0;
                    robot.servo_list[temp_2].setPosition(target_positions[temp_1]);
                    //make sure its staying where we want it to, and update the starting position (don't update starting position if the servo should be moving, obviously)
                    starting_positions[temp_1] = target_positions[temp_1];
                } else {

                    if (times_started[temp_1] < 0) { //un-reset it
                        times_started[temp_1] = (double) System.nanoTime() / 1000000000.0;
                    }

                    for (int i = 0; i < number_of_keys; i++) { //for each one in the map

                        temp_0 = keys.indexOf((String) object_keys.get(4 * i));

                        if ((temp_0 < 20 && buttons[temp_0]) || (temp_0 > 19 && Math.abs(axes[temp_0 - 20]) > 0.1)) {
                            if ((((String) object_keys.get(4 * i + 1)).equals("button")) || (((String) object_keys.get(4 * i + 1)).equals("cycle"))) {
                                if (((double[]) object_keys.get(4 * i + 3)).length == 1) {
                                    robot.servo_list[temp_2].setPosition(((double[]) object_keys.get(4 * i + 3))[0]);
                                } else {
                                    increasing = (((double[]) object_keys.get(4 * i + 3))[1] > ((double[]) object_keys.get(4 * i + 3))[0]);
                                    temp_3 = 0;
                                    while ((temp_3 < ((double[]) object_keys.get(4 * i + 3)).length) && ((((double[]) object_keys.get(4 * i + 3))[temp_3] < target_positions[temp_1]) || (!increasing)) && ((((double[]) object_keys.get(4 * i + 3))[temp_3] > target_positions[temp_1]) || (increasing))) {
                                        temp_3 += 1; //note it stops perfectly if it's equal, it lands one past if it skips over value
                                        //if increasing, then increase while index is less
                                    }
                                    if (((int) object_keys.get(4 * i + 2) > 0) && ((temp_3 + 1 > ((double[]) object_keys.get(4 * i + 3)).length) || (((double[]) object_keys.get(4 * i + 3))[temp_3] != target_positions[temp_1]))) {
                                        temp_3 -= 1; //subtract one if we're going up
                                    }
                                    if (((String) object_keys.get(4 * i + 1)).equals("cycle")) {
                                        if ((temp_3 + 2 > ((double[]) object_keys.get(4 * i + 3)).length) && ((int) object_keys.get(4 * i + 2) > 0)) {
                                            temp_3 = 0;
                                        } else if ((temp_3 < 1) && ((int) object_keys.get(4 * i + 2) < 0)) {
                                            temp_3 = ((double[]) object_keys.get(4 * i + 3)).length - 1;
                                        } else {
                                            temp_3 = Math.max(0, Math.min(temp_3 + (int) object_keys.get(4 * i + 2), ((double[]) object_keys.get(4 * i + 3)).length - 1));
                                        }
                                    } else {
                                        temp_3 = Math.max(0, Math.min(temp_3 + (int) object_keys.get(4 * i + 2), ((double[]) object_keys.get(4 * i + 3)).length - 1));
                                    }
                                    robot.servo_list[temp_2].setPosition(((double[]) object_keys.get(4 * i + 3))[temp_3]); //change the target position
                                }
                            } else if ((((String) object_keys.get(4 * i + 1)).equals("toggle")) || temp_0 < 20) {
                                robot.servo_list[temp_2].setPosition(Math.max(servo_min_positions[temp_2], Math.min(servo_max_positions[temp_2], //we don't have gradients because that's pointless to add; we have to assign position
                                        starting_positions[temp_1] + (double) object_keys.get(4 * i + 2) * ((double) System.nanoTime() / 1000000000.0 - times_started[temp_1])
                                )));                   //position: initial position (remember, it stopped updating when we started moving) + speed * time
                            } else {
                                robot.servo_list[temp_2].setPosition(Math.max(servo_min_positions[temp_2], Math.min(servo_max_positions[temp_2],
                                        robot.servo_list[temp_2].getPosition() + //the expression below is seconds/tick, basically; current pos + seconds/tick * depth * angles/second
                                                ((double) System.nanoTime() / 1000000000.0 - times_started[temp_1]) * axes[temp_0 - 20] * (
                                                        (temp_0 > 23) ? (double) object_keys.get(4 * i + 2) : //if it's a trigger, then set it to the first val
                                                                (temp_0 < 22) ? (axes[temp_0 - 20] < 0 ? (double) object_keys.get(4 * i + 2) : (double) object_keys.get(4 * i + 3)) : //it's x
                                                                        -1 * (axes[temp_0 - 20] > 0 ? (double) object_keys.get(4 * i + 2) : (double) object_keys.get(4 * i + 3)) //else it's a y
                                                ))));
                                times_started[temp_1] = (double) System.nanoTime() / 1000000000.0;
                            }
                        }
                    }
                    target_positions[temp_1] = robot.servo_list[temp_2].getPosition(); //has to be after for servos
                }
            } else {
                for (int i = 0; i < number_of_keys; i++) {

                    temp_0 = keys.indexOf((String) object_keys.get(4 * i)); //where button is in list of keys; < 20 -> button, >= 20 -> axis

                    if ((temp_0 < 20 && buttons[temp_0]) || (temp_0 > 19 && Math.abs(axes[temp_0 - 20]) > 0.1)) {
                        target_x = (double) object_keys.get(4 * i + 1);
                        target_y = (double) object_keys.get(4 * i + 2);
                        try {
                            target_angle = (double) object_keys.get(4 * i + 3);
                            target_angle = Math.toRadians(target_angle);
                        } catch (ClassCastException e) { //if it's a string, we do NOT reset the target angle
                            target_angle = current_angle;
                        }
                    }
                }
            }
        }
    }

    //Driving

    public void drive(Gamepad gamepad) {
        double speedFactor = 1 + 2 * gamepad.right_trigger;

        double left_stick_magnitude = Math.sqrt(gamepad.left_stick_x * gamepad.left_stick_x + gamepad.left_stick_y * gamepad.left_stick_y);
        if (left_stick_magnitude <= 0.333) left_stick_magnitude = 0.0;
        double left_stick_angle =
                (left_stick_magnitude <= 0.333) ? -Math.PI / 2.0 :
                (gamepad.left_stick_x > 0) ? Math.atan(gamepad.left_stick_y/gamepad.left_stick_x) :
                (gamepad.left_stick_x < 0) ? Math.PI + Math.atan(gamepad.left_stick_y/gamepad.left_stick_x) :
                (gamepad.left_stick_y > 0) ? Math.PI / 2.0 : -Math.PI / 2.0;
        left_stick_angle += Math.PI/2.0;
        
        double right_stick_magnitude = Math.sqrt(gamepad.right_stick_x * gamepad.right_stick_x + gamepad.right_stick_y * gamepad.right_stick_y);
        if (right_stick_magnitude <= 0.333) right_stick_magnitude = 0.0;
        double right_stick_angle =
                (right_stick_magnitude <= 0.333) ? -Math.PI / 2.0 :
                (gamepad.right_stick_x > 0) ? Math.atan(gamepad.right_stick_y/gamepad.right_stick_x) :
                (gamepad.right_stick_x < 0) ? Math.PI + Math.atan(gamepad.right_stick_y/gamepad.right_stick_x) :
                (gamepad.right_stick_y > 0) ? Math.PI / 2.0 : -Math.PI / 2.0;
        right_stick_angle += Math.PI/2.0;

        left_stick_angle = modifiedAngle(left_stick_angle);
        right_stick_angle = modifiedAngle(right_stick_angle);

        //Positive angles --> clockwise
        //Zero --> vertical

        if (usePID) {
            current_angle = 0 - robot.getAngle() - zero_angle;
        } else if (useRoadRunner) {
            position_tracker.update();
            Pose2d currentPose = position_tracker.getPoseEstimate();

            current_x = currentPose.getX();
            current_y = currentPose.getY();
            
            current_angle = 0 - currentPose.getHeading() - zero_angle;
        } //current_angle: same angle system as left/right stick angle

        double distance_factor;
        double offset;
        double s = strafe;

        if (left_stick_magnitude != 0) {
            distance_factor = left_stick_magnitude;

            if (locked_motion) {
                offset = modifiedAngle(left_stick_angle - current_angle);
            } else {
                offset = left_stick_angle;
            }

            if (useRoadRunner) {
                target_x = current_x;
                target_y = current_y;
            }

        } else {
            s = 1;

            distance_factor = Math.sqrt((current_x - target_x) * (current_x - target_x) + (current_y - target_y) * (current_y - target_y)) * distance_weight_two;
                //zero by default if not using RoadRunner :)

            double line_angle = (target_x > current_x) ? Math.atan(((float) target_y - (float) current_y)/((float) target_x - (float) current_x)) :
            (target_x < current_x) ? Math.PI + Math.atan(((float) target_y - (float) current_y)/((float) target_x - (float) current_x)) :
            (target_y > current_y) ? Math.PI / 2.0 : -Math.PI / 2.0;
            
            line_angle += Math.PI / 2.0;
            offset = modifiedAngle(line_angle - current_angle);
        }

        double turning_factor = 0;

        if (right_stick_magnitude != 0) {
            if (locked_rotation) {
                target_angle = right_stick_angle;
            } else { //if we're driving normally
                target_angle = current_angle;
                turning_factor = gamepad.right_stick_x;
            }
        } //target angle remains constant if we aren't turning manually

        drive(turning_factor, distance_factor, offset, s, speedFactor);
    }

    public void drive(double turning_factor, double distance_factor, double offset, double strafe, double speedFactor) {
        double correction = getCorrection();
        turning_factor *= turning_weight;
        turning_factor += correction;
        distance_factor *= distance_weight;
        double[] power = new double[4];
        for (int i = 0; i < 4; i++) {
            power[i] = turning_factor * ((i > 1) ? -1 : 1) - distance_factor * (Math.cos(offset) + Math.sin(offset) * (i % 2 == 1 ? 1 : -1) * strafe);
        }
        double maximum = Math.max(1, Math.max(Math.max(Math.abs(power[0]), Math.abs(power[1])), Math.max(Math.abs(power[2]), Math.abs(power[3]))));
        for (int i = 0; i < 4; i++) {
            robot.wheel_list[i].setPower(power[i] / maximum / speedFactor);
        }
    }
    
    public double getCorrection() {
        current_time = System.currentTimeMillis();
        current_error = modifiedAngle(target_angle - current_angle);

        double p = current_error;
        double d = (current_error - previous_error) / (current_time - previous_time);

        previous_error = current_error;
        previous_time = current_time;

        return p_weight * p + d_weight * d;
    }

    public double modifiedAngle(double radians) {
        while (radians > Math.PI) {
            radians -= 2 * Math.PI;
        }
        while (radians < 0 - Math.PI) {
            radians += 2 * Math.PI;
        }
        return radians;
    }

    //Initialization

    public void new_keybind(String motor, Object button, Object modifier1, Object modifier2, Object modifier3) {
        Object temp2;
        if (!dc_motor_names.contains(motor) && !servo_names.contains(motor) &&!(motor.equals("goto"))) {
            throw new IllegalArgumentException("You misspelled " + motor + " - make sure its exactly as it's spelled in dc motor list or servo list, or it's \"goto\". Idiot");
        }
        if (!(keys.contains((String) button))) {
            throw new IllegalArgumentException("You misspelled " + button + "  - make sure its exactly as it's spelled in keys. ");
        }
        if (!keybinds.containsKey(motor)) {
            keybinds.put(motor, new ArrayList<>());
        }
        if (dc_motor_names.contains(motor) || servo_names.contains(motor)) {
            if (keybinds.get(motor).contains((Object) button)) {
                throw new IllegalArgumentException("You can't have \"" + button + "\" have 2 different functions for the same motor. The motor is " +  motor + ". ");
            } else if (((String) modifier1).equals("button") || ((String) modifier1).equals("cycle")) {
                try {
                    temp2 = (int) modifier2;
                } catch(ClassCastException e) {
                    throw new IllegalArgumentException("Increments have to be by an integer amount. Error was on key " + button + ". ");
                }
                if (servo_names.contains(motor)) {
                    try {
                        temp2 = (double[]) modifier3;
                    } catch(ClassCastException e) {
                        throw new IllegalArgumentException("For servos, the list has to be one of doubles. Error was on key " + button + ". ");
                    }
                } else {
                    try {
                        temp2 = (int[]) modifier3;
                    } catch(ClassCastException e) {
                        throw new IllegalArgumentException("For dc motors, the list has to be one of integers. Error was on key " + button + ". ");
                    }
                }
            } else if (((String) modifier1).equals("toggle") || ((String) modifier1).equals("default")) {
                if (((String) modifier1).equals("default") && (keys.indexOf((String) button) > 19)) {
                    try {
                        temp2 = (double) modifier2;
                        temp2 = (double) modifier3;
                    } catch(ClassCastException e) {
                        throw new IllegalArgumentException("Power has to be a double. Error was on key " + button + ". ");
                    }
                    if ((Math.max(Math.abs((double) modifier2), Math.abs((double) modifier3)) > 1) && dc_motor_names.contains(motor)) {
                        throw new IllegalArgumentException("DC Motor Power has to be between -1.0 and 1.0. Error was on key " + button + ". ");
                    }
                } else if (servo_names.contains(motor)) { //servo, default or toggle
                    try {
                        temp2 = (double) modifier2;
                    } catch(ClassCastException e) {
                        throw new IllegalArgumentException("Power has to be a double. Error was on key " + button + ". ");
                    }
                } else {
                    try {
                        temp2 = (String) modifier2;
                        if (!((String) modifier2).equals("normal") && !((String) modifier2).equals("gradient")) {
                            throw new ClassCastException();
                        }
                    } catch(ClassCastException e) {
                        throw new IllegalArgumentException("Button type has to be \"normal\" or \"gradient\". Error was on key " + button + ". ");
                    }
                    try {
                        temp2 = (double) modifier3;
                        if (Math.abs((double) modifier3) > 1) {
                            throw new ClassCastException();
                        }
                    } catch(ClassCastException e) {
                        throw new IllegalArgumentException("Power has to be a double between -1.0 and 1.0. Error was on key " + button + ". ");
                    }
                }
            } else {
                throw new IllegalArgumentException("You misspelled " + modifier1 + " in key " + button + " - make sure its \"default\", \"button\", \"cycle\" or \"toggle\".");
            }
        } else { //goto
            try {
                temp2 = (double) modifier1;
                temp2 = (double) modifier2;
            } catch(ClassCastException e) {
                throw new IllegalArgumentException("Target x/y must be doubles (integers are fine as well)");
            }
            try {
                temp2 = (double) modifier3;
            } catch(ClassCastException e) {
                if (!temp2.equals("none")) {
                    throw new IllegalArgumentException("target angle must be a double, or be labeled as \"none\"");
                }
            }
        }
        keybinds.get(motor).add(button);
        keybinds.get(motor).add(modifier1);
        keybinds.get(motor).add(modifier2);
        keybinds.get(motor).add(modifier3);
    }

    public void set_button_types() {
        Object temp2;
        for (Map.Entry<String, ArrayList<Object>> element : keybinds.entrySet()) { //for every entry in keybinds...
            for (int i = 0; i < (element.getValue()).size(); i += 4) {
                try {
                    temp2 = (double) element.getValue().get(i+1);
                    if (button_types[keys.indexOf((String) element.getValue().get(i))] == null) {
                        button_types[keys.indexOf((String) element.getValue().get(i))] = "button";
                    } else if (!((button_types[keys.indexOf((String) element.getValue().get(i))]).equals("button"))) {
                        throw new IllegalArgumentException("A button cannot have 2 types; however, you are setting \"" + element.getValue().get(i) +
                                "\" to be both a " + button_types[keys.indexOf((String) element.getValue().get(i))] + " and a button. (\"goto\" is, by default, a button) ");
                    }
                } catch(ClassCastException e) {
                    if (button_types[keys.indexOf((String) element.getValue().get(i))] == null) {
                        button_types[keys.indexOf((String) element.getValue().get(i))] = (String) element.getValue().get(i+1);
                    } else if (!((button_types[keys.indexOf((String) element.getValue().get(i))]).equals((String) element.getValue().get(i+1)))) {
                        throw new IllegalArgumentException("A button cannot have 2 types; however, you are setting \"" + element.getValue().get(i) +
                                "\" to be both a " + button_types[keys.indexOf((String) element.getValue().get(i))] + " and a " + element.getValue().get(i+1) + ". ");
                    }
                }
            }
        }
        for (int i = 0; i < 27; i++) {
            if (button_types[i] == null) {
                button_types[i] = "default";
            }
        }

        if ((useRoadRunner) && (usePID)) {
            throw new IllegalArgumentException("You cannot use both RoadRunner and the build-in PID");
        }
    }

    public void resetZeroAngle() {
        if (useRoadRunner) {
            zero_angle = 0 - position_tracker.getPoseEstimate().getHeading();
        } else if (usePID) {
            zero_angle = 0 - robot.getAngle();
        }
    }

    public void setZeroAngle(double angle) {
        zero_angle = 0 - Math.toRadians(angle);
    }

    public void initializeRoadRunner(double x, double y, double angle, StandardTrackingWheelLocalizer localizer) {
        position_tracker = localizer;
        position_tracker.setPoseEstimate(new Pose2d(x, y, Math.toRadians(angle)));
        current_x = x;
        current_y = y;
        current_angle = Math.toRadians(angle);
        target_x = x;
        target_y = y;
        target_angle = Math.toRadians(angle);
        zero_angle -= Math.toRadians(angle);
    }

    //RoadRunner

    public double angle() { return modifiedAngle(0 - zero_angle - current_angle); }

    public double[][] robot_hitbox() {
        return new double[][] {
                {current_x + robot_length * Math.cos(angle()) - robot_width * Math.sin(angle()), current_y + robot_length * Math.sin(angle()) + robot_width * Math.cos(angle())},
                {current_x + robot_length * Math.cos(angle()) + robot_width * Math.sin(angle()), current_y + robot_length * Math.sin(angle()) - robot_width * Math.cos(angle())},
                {current_x - robot_length * Math.cos(angle()) + robot_width * Math.sin(angle()), current_y - robot_length * Math.sin(angle()) - robot_width * Math.cos(angle())},
                {current_x - robot_length * Math.cos(angle()) - robot_width * Math.sin(angle()), current_y - robot_length * Math.sin(angle()) + robot_width * Math.cos(angle())}
        };
    }

    public double distance_from(double[] point) {
        return Math.sqrt((point[0] - current_x) * (point[0] - current_x) + (point[1] - current_y) * (point[1] - current_y));
    }

    public boolean point_above_line(double[] point, double[][] line) { //definition of "above": y above line, or if vertical, then x value greater
        //point format:{x, y}
        //line format: {{x1, y1}, {x2, y2}}
        if (line[0][0] == line[1][0]) {
            return point[0] > line[0][0];
        } else if (point[0] == line[0][0]) {
            return point[1] > line[0][1];
        } else if (point[0] > line[0][0]) {
            return (point[1] - line[0][1]) / (point[0] - line[0][0]) > (line[1][1] - line[0][1]) / (line[1][0] - line[0][0]);
        } else {
            return (point[1] - line[0][1]) / (point[0] - line[0][0]) < (line[1][1] - line[0][1]) / (line[1][0] - line[0][0]);
        }
    }

    public boolean point_on_line(double[] point, double[][] line) {
        if (Math.abs(line[1][0] - line[0][0]) == 0) {
            return (Math.abs(line[0][0] - point[0]) < 0.01);
        } else if (Math.abs(point[0] - line[0][0]) == 0) {
            return false;
        } else {
            return (point[1] - line[0][1]) / (point[0] - line[0][0]) == (line[1][1] - line[0][1]) / (line[1][0] - line[0][0]);
        }
    }

    public boolean intersect(double[][] line1, double[][] line2) {
        double[][] line_1 = line1;
        double[][] line_2 = line2;

        if ((Math.max(line_1[0][0], line_1[1][0]) < Math.min(line_2[0][0], line_2[1][0])) || (Math.min(line_1[0][0], line_1[1][0]) > Math.max(line_2[0][0], line_2[1][0])) ||
                (Math.max(line_1[0][1], line_1[1][1]) < Math.min(line_2[0][1], line_2[1][1])) || (Math.min(line_1[0][1], line_1[1][1]) > Math.max(line_2[0][1], line_2[1][1]))) {
            return false;
        }

        if (line_1[0][0] == line_1[1][0]) {
            line_1[1][0] += 0.01;
        }
        if (line_2[0][0] == line_2[1][0]) {
            line_2[1][0] += 0.01;
        }

        if (point_on_line(line_1[0], line_2) || point_on_line (line_1[1], line_2) || point_on_line(line_2[0], line_1) || point_on_line (line_2[1], line_1)) {
            return false;
        } else {
            return (((point_above_line(line_1[0], line_2) != point_above_line(line_1[1], line_2))) && ((point_above_line(line_2[0], line_1) != point_above_line(line_2[1], line_1))));
        }
    }

    public boolean point_inside_polygon(double[] point, double[][] polygon, int accuracy) { //we want the inside-ness to be the same for every line
        double min_x = 10000000;
        double min_y = 10000000;
        double max_x = -10000000;
        double max_y = -10000000;
        int intersections;
        double[][] radial_line = new double[2][2];
        double[][] next_line = new double[2][2];

        for (double[] i : polygon) {
            min_x = Math.min(min_x, i[0]);
            max_x = Math.max(max_x, i[0]);
            min_y = Math.min(min_y, i[1]);
            max_y = Math.max(max_y, i[1]);
        }
        double length = Math.sqrt((max_x - min_x) * (max_x - min_x) + (max_y - min_y) * (max_y - min_y));

        for (int i = 0; i < accuracy; i++) {
            intersections = 0;
            radial_line[0][0] = point[0];
            radial_line[0][1] = point[1];
            radial_line[1][0] = point[0] + length * Math.cos(2.0 * Math.PI / (double) accuracy * (double) i);
            radial_line[1][1] = point[1] + length * Math.sin(2.0 * Math.PI / (double) accuracy * (double) i);

            for (int j = 0; j < polygon.length; j++) { //add 1 if intersects line, 0.5 for each endpoint it touches
                next_line[0] = polygon[j];
                next_line[1] = polygon[(j + 1) % polygon.length];
                if (point_on_line(polygon[j], radial_line)) {
                    intersections += 1;
                }
                if (point_on_line(polygon[(j + 1) % polygon.length], radial_line)) {
                    intersections += 1;
                }
                if (intersect(radial_line, next_line)) {
                    intersections += 2;
                }
            }
            if (intersections % 4 != 2) {
                return false;
            }
        }
        return true;
    }

    public boolean completely_inside(double[][] polygon1, double[][] polygon2, int accuracy) {
        for (double[] point : polygon1) {
            if (!point_inside_polygon (point, polygon2, accuracy))
                return false;
        }
        return true;
    }

    public boolean shells_intersect(double[][] polygon1, double[][] polygon2) { //only seeing if the outer shells interesect each other
        for (int i = 0; i < polygon1.length; i++) {
            for (int j = 0; j < polygon2.length; j++) {
                if (intersect(new double[][] {polygon1[i], polygon1[(i + 1) % polygon1.length]}, new double[][] {polygon2[j], polygon2[(j + 1) % polygon2.length]}))
                    return true;
            }
        }
        return false;
    }

    public boolean polygons_intersect(double[][] polygon1, double[][] polygon2, int accuracy) {
        return (shells_intersect(polygon1, polygon2) || (completely_inside(polygon1, polygon2, accuracy) || completely_inside(polygon2, polygon1, accuracy)));
    }

    public boolean inside_polygon(double[][] polygon) {
        return completely_inside(robot_hitbox(), polygon, 6);
    }

    public boolean robot_on_point(double[] point) {
        return point_inside_polygon(point, robot_hitbox(), 6);
    }

    public boolean facing_polygon(double[][] polygon, double max_length, double offset) {
        double[][] radial_line = {{current_x, current_y}, {current_x + max_length * Math.cos(angle() + Math.toRadians(offset)), current_y + max_length * Math.sin(angle() + Math.toRadians(offset))}};
        double[][] next_line = new double[2][2];

        for (int j = 0; j < polygon.length; j++) { //add 1 if intersects line, 0.5 for each endpoint it touches
            next_line[0] = polygon[j];
            next_line[1] = polygon[(j + 1) % polygon.length];
            if (intersect(radial_line, next_line)) {
                return true;
            }
        }
        return false;
    }
}