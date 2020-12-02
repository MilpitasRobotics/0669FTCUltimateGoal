package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

@TeleOp(name = "DriverControl")

public class DriverControl extends LinearOpMode {
    MXRFTCRobot robot = new MXRFTCRobot();

    boolean clawToggleOC = false; //when clawToggleOC (OC - open closed) is false, the claw is open
    boolean clawToggleUD = false; //when clawToggleUD (UD - up down) is false, the claw is down
    boolean flyWheelToggle = false; //when flyWheelToggle is false, the flywheel system is deactivated
    boolean intakeToggle = false; //when intakeToggle is false, the intake system is deactivated
    boolean outtakeToggle = false; //when outtakeToggle is false, the outtake system is deactivated

    @Override
    public void runOpMode() {
        robot.init(hardwareMap, telemetry);

        waitForStart();

        robot.runtime.reset();

        while(opModeIsActive()){ //write what you want the robot to do while it is running here!
            //motion controls
            robot.mecanumDrive(-gamepad1.left_stick_y, gamepad1.left_stick_x, gamepad1.right_stick_x); //this covers back and forth movement and rotation - refer to MXRFTCRobot.java for more details

            if(gamepad1.right_stick_x == 0){
                robot.mecanumDrive(-gamepad1.left_stick_y, gamepad1.left_stick_x, 0); //stops the robot in the event that it is given too many commands
            }

            //claw operation - open/close claw
            if(gamepad1.a){ //if gamepad's a button is pressed...
                clawToggleOC = !clawToggleOC; //set the value of clawToggleOC to the opposite of itself
                robot.toggleClawOC(clawToggleOC); //refer to MXRFTCRobot.java for details
            }

            //claw operation - flip claw up and down
            if(gamepad1.b){ //if gamepad's b button is pressed...
                clawToggleUD = !clawToggleUD; //set the value of clawToggleUD to the opposite of itself
                robot.toggleClawUD(clawToggleUD); //refer to MXRFTCRobot.java for details
            }

            //flywheel and servo pusher activation/deactivation
            if(gamepad1.x){ //if gamepad's x button is pressed...
                robot.mecanumDrive(0,0, 0); //stop the robot so that it doesn't "autopilot" itself after pressing x
                flyWheelToggle = !flyWheelToggle; //set the value of flyWheelToggle to the opposite of itself
                robot.toggleFlyWheel(flyWheelToggle); //refer to MXRFTCRobot.java for details
            }

            //code to make the servo push rings into the flywheel every 2 seconds
            if(flyWheelToggle){
                sleep(1000);
                robot.flyWheelPush.setPosition(.5);
                sleep(1000);
                robot.flyWheelPush.setPosition(.9);
                sleep(2000);
            }


            //lift linear slide up
            while(gamepad1.dpad_up){ //if gamepad's dpad up is pressed...
                robot.linearSlideMoveUp();
            }

            //drop linear slide down
            while(gamepad1.dpad_down){ //if gamepad's dpad down is pressed...
                robot.linearSlideMoveDown();
            }
            //intake code
            if(gamepad1.left_trigger == 1) {
                intakeToggle = !intakeToggle;
                if (intakeToggle) {
                    robot.intake(1);
                }
                else{
                    robot.intake(0);
                }
            }
            //outtake code
            if(gamepad1.right_trigger == 1) {
                outtakeToggle = !outtakeToggle;
                if (outtakeToggle) {
                    robot.intake(-1);
                }
                else{
                    robot.intake(0);
                }
            }

        }

    }
}
