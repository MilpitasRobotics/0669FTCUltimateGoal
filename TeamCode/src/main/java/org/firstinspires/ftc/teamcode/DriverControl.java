package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

@TeleOp(name = "DriverControl")

public class DriverControl extends LinearOpMode {
    MXRFTCRobot robot = new MXRFTCRobot();

    @Override
    public void runOpMode(){
        robot.init(hardwareMap, telemetry);

        waitForStart();

        robot.runtime.reset();

        while(opModeIsActive()){ //write what you want the robot to do while it is running here!
            //motion controls
            robot.mecanumDrive(-gamepad1.left_stick_y, gamepad1.left_stick_x, gamepad1.right_stick_x); //this covers back and forth movement and rotation - refer to MXRFTCRobot.java for more details

            //claw operation - open/close claw
            if(gamepad1.a){ //if gamepad's a button is pressed...

            }

            //claw operation - flip claw up and down
            if(gamepad1.b){ //if gamepad's b button is pressed...

            }

            //flywheel and servo pusher activation/deactivation
            if(gamepad1.x){ //if gamepad's x button is pressed...

            }

            //lift linear slide up
            if(gamepad1.dpad_up){ //if gamepad's dpad up is pressed...

            }

            //drop linear slide down
            if(gamepad1.dpad_down){ //if gamepad's dpad down is pressed...

            }

            //intake [WILL BE CHANGED]
            if(gamepad1.left_trigger >= 0.5){ //if the gamepad's left trigger is at least halfway pressed...

            }

            //outtake [WILL BE CHANGED]
            if(gamepad1.right_trigger >= 0.5){ //if the gamepad's left trigger is at least halfway pressed...

            }
        }

    }
}
