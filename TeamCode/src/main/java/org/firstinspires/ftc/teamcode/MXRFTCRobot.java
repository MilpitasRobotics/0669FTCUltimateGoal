package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;
import org.firstinspires.ftc.robotcore.external.Telemetry;

public class MXRFTCRobot {
    //Hardware
    public DcMotor fLeftDrive, fRightDrive, bLeftDrive, bRightDrive, flyWheel, intakeTop, intakeBot, lift;
    public Servo clawOpenCloseL, clawOpenCloseR, clawUpDownL, clawUpDownR, leftLinSlide, rightLinSlide, flyWheelPush, flyWheelRampL, flyWheelRampR;
    public double lastRingPush = 0;

    //variables
    public double currentRampAngle = 0.5; //90 degrees

    public ElapsedTime runtime = new ElapsedTime();

    public double currentPosition = 0;

    public MXRFTCRobot(){
    }

    public void init(HardwareMap hwMap, Telemetry telemetry){ //both the HardwareMap and Telemetry variables allow for us to use these FTC functions within a class. otherwise, we cannot use them
        //Hardware Mapping
        //Chassis Motors
        fLeftDrive = hwMap.get(DcMotor.class, "FL");
        fRightDrive = hwMap.get(DcMotor.class, "FR");
        bLeftDrive = hwMap.get(DcMotor.class, "BL");
        bRightDrive = hwMap.get(DcMotor.class, "BR");

        //Intake and Flywheel Motors
        flyWheel = hwMap.get(DcMotor.class, "FW");
        intakeTop = hwMap.get(DcMotor.class, "IT");
        intakeBot = hwMap.get(DcMotor.class, "IB");
        lift = hwMap.get(DcMotor.class, "LI");

        //Intake and Flywheel Servos
        clawOpenCloseL = hwMap.get(Servo.class, "CLOL");
        clawOpenCloseR = hwMap.get(Servo.class, "CLOR");
        clawUpDownL = hwMap.get(Servo.class, "CLUL");
        clawUpDownR = hwMap.get(Servo.class, "CLUR");
        leftLinSlide = hwMap.get(Servo.class, "LLS");
        rightLinSlide = hwMap.get(Servo.class, "RLS");
        flyWheelPush = hwMap.get(Servo.class, "FWP");
        flyWheelRampL = hwMap.get(Servo.class, "FRL");
        flyWheelRampR = hwMap.get(Servo.class, "FRR");

        //Direction Declaration
        fLeftDrive.setDirection(DcMotor.Direction.FORWARD);
        fRightDrive.setDirection(DcMotor.Direction.REVERSE);
        bLeftDrive.setDirection(DcMotor.Direction.FORWARD);
        bRightDrive.setDirection(DcMotor.Direction.REVERSE);
        flyWheel.setDirection(DcMotor.Direction.REVERSE);
        intakeTop.setDirection(DcMotor.Direction.FORWARD);
        intakeBot.setDirection(DcMotor.Direction.REVERSE);
        lift.setDirection(DcMotor.Direction.FORWARD);

        //Reset servo positions to normal [THESE VALUES ARE ARBITRARY FOR NOW, THEY NEED TESTING]
        flyWheelPush.setPosition(.9); //flywheel pusher is by default retracted
        flyWheelRampL.setPosition(0.5); //flywheel ramp by default starts horizontal (90 degrees)
        flyWheelRampR.setPosition(0.5);
        clawOpenCloseL.setPosition(0.5); //claw by default starts open
        clawOpenCloseR.setPosition(0.5); //claw by default starts open
        clawUpDownL.setPosition(0.5);//claw by default starts down
        clawUpDownR.setPosition(0.5);//claw by default starts down

        telemetry.addLine("Initialization Complete");
        telemetry.update();
    }

    //Flywheel methods
    public void setRampAngle(double angleChange){
        double change = angleChange/180;
        currentRampAngle += change;

        if(currentRampAngle > 1.0)
            currentRampAngle = 1.0;
        else if(currentRampAngle < 0.0)
            currentRampAngle = 0.0;

        flyWheelRampL.setPosition(currentRampAngle);
        flyWheelRampR.setPosition(currentRampAngle);
    }

    public void toggleFlyWheel(boolean state){ //true - activated, false - deactivated
        if(state) {
            flyWheel.setPower(1.0); //set flywheel motor to max speed
            lastRingPush = runtime.seconds(); //lastRingPush is set to the current runtime so that we can calculate the gap between the current timestep and the last time the ring was pushed
        }
        else{
            flyWheel.setPower(0); //set flywheel power to zero speed
            flyWheelPush.setPosition(.9); //return pusher to original place
            lastRingPush = 0;
        }

    }

    public void setFlywheelSpeed(double speed){
        flyWheel.setPower(speed);
    }

    public void pushRing(boolean state){ //NOT SURE IF THIS WILL WORK, NEEDS TESTING!
        if(lastRingPush - runtime.seconds() >= 2 && state) {
            flyWheelPush.setPosition(.9);
            flyWheelPush.setPosition(0.5);
            lastRingPush = runtime.seconds();
        }
    }

    //Movement methods
    public void mecanumDrive(double leftJSY, double leftJSX, double rightJSX){
        //IMPORTANT! YOU HAVE TO REVERSE THE JOYSTICK INPUTS IN THE PARAMETERS FOR THIS TO WORK CORRECTLY
        //leftJSX - left/right movement, leftJSY - forward/backward movement, rightJSX - cw/ccw rotation
        fLeftDrive.setPower((leftJSY+leftJSX+rightJSX));
        fRightDrive.setPower((leftJSY-leftJSY-rightJSX));
        bLeftDrive.setPower((leftJSY-leftJSY+rightJSX));
        bRightDrive.setPower((leftJSY+leftJSY-rightJSX));
    }

    //claw controls
    public void toggleClawOC(boolean state){ //true - closed, false - open
        if(state){ //if the claw is toggled on (true), then the claw will close
            clawOpenCloseL.setPosition(1.0); //VALUE STILL ARBITRARY, NEEDS TO BE TESTED
            clawOpenCloseR.setPosition(0);
        }
        else{ //if the claw is toggled off (false), then the claw will open
            clawOpenCloseL.setPosition(0.5); //VALUE STILL ARBITRARY, NEEDS TO BE TESTED
            clawOpenCloseR.setPosition(0.5); //VALUE STILL ARBITRARY, NEEDS TO BE TESTED
        }
    }

    public void toggleClawUD(boolean state){ //true - up, false - down
        if(state){ //if the claw is toggled on (true), then the claw will flip up
            clawUpDownL.setPosition(1.0); //VALUE STILL ARBITRARY, NEEDS TO BE TESTED
            clawUpDownR.setPosition(0);
        }
        else{ //if the claw is toggled off (false), then the claw will flip down
            clawUpDownL.setPosition(0.5); //VALUE STILL ARBITRARY, NEEDS TO BE TESTED
            clawUpDownR.setPosition(0.5); //VALUE STILL ARBITRARY, NEEDS TO BE TESTED
        }
    }

    public void intake(double speed){
        intakeBot.setPower(speed);
        intakeTop.setPower(speed);
    }

    //UNFINISHED AUTON METHODS
    public void driveForward(double distance){

    }

    public void driveBackwards(double distance){

    }

    public void turnRight(int degrees){

    }

    public void turnLeft(int degrees){

    }

    //Linear slide methods
    public void linearSlideMoveUp() {
        currentPosition += 0.1;
        leftLinSlide.setPosition(currentPosition);
        rightLinSlide.setPosition(currentPosition);
    }

    public void linearSlideMoveDown(){
        double currentPosition = 0.1;
        leftLinSlide.setPosition(currentPosition);
        rightLinSlide.setPosition(currentPosition);
    }

}

