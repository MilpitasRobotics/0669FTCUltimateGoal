package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;
import org.firstinspires.ftc.robotcore.external.Telemetry;

public class MXRFTCRobot {
    //Hardware
    public DcMotor fLeftDrive, fRightDrive, bLeftDrive, bRightDrive, flyWheel, intake, lift;
    public Servo claw, leftLinSlide, rightLinSlide, flyWheelPush, flyWheelRampL, flyWheelRampR, leftRampServo, rightRampServo;

    //variables
    public double currentRampAngle = 0.5; //90 degrees

    private ElapsedTime runtime = new ElapsedTime();

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
        intake = hwMap.get(DcMotor.class, "IN");
        lift = hwMap.get(DcMotor.class, "LI");

        //Intake and Flywheel Servos
        claw = hwMap.get(Servo.class, "CLW");
        leftLinSlide = hwMap.get(Servo.class, "LLS");
        rightLinSlide = hwMap.get(Servo.class, "RLS");
        flyWheelPush = hwMap.get(Servo.class, "FWP");
        flyWheelRampL = hwMap.get(Servo.class, "FRL");
        flyWheelRampR = hwMap.get(Servo.class, "FRR");
        leftRampServo = hwMap.get(Servo.class, "LRS");
        rightRampServo = hwMap.get(Servo.class, "RRS");

        //Direction Declaration
        fLeftDrive.setDirection(DcMotor.Direction.FORWARD);
        fRightDrive.setDirection(DcMotor.Direction.REVERSE);
        bLeftDrive.setDirection(DcMotor.Direction.FORWARD);
        bRightDrive.setDirection(DcMotor.Direction.REVERSE);
        flyWheel.setDirection(DcMotor.Direction.FORWARD);
        intake.setDirection(DcMotor.Direction.FORWARD);
        lift.setDirection(DcMotor.Direction.FORWARD);

        //Reset servo positions to normal
        flyWheelPush.setPosition(0);
        flyWheelRampL.setPosition(0.5);
        flyWheelRampR.setPosition(0.5);

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

    public void setFlywheelSpeed(double speed){
        flyWheel.setPower(speed);
    }

    public void pushRing(){
        flyWheelPush.setPosition(0.5);
        flyWheelPush.setPosition(0);
    }

    //Movement methods
    public void mecanumDrive(double leftJSY, double leftJSX, double rightJSX){
        //IMPORTANT! YOU HAVE TO REVERSE THE JOYSTICK INPUTS IN THE PARAMETERS FOR THIS TO WORK CORRECTLY
        //leftJSX - left/right movement, leftJSY - forward/backward movement, rightJSX - cw/ccw rotation
        fLeftDrive.setPower(leftJSY+leftJSX+rightJSX);
        fRightDrive.setPower(leftJSY-leftJSY-rightJSX);
        bLeftDrive.setPower(leftJSY-leftJSY+rightJSX);
        bRightDrive.setPower(leftJSY+leftJSY-rightJSX);
    }

    public void driveForward(double distance){

    }

    public void driveBackwards(double distance){

    }

    public void turnRight(int degrees){

    }

    public void turnLeft(int degrees){

    }

}
