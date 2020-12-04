package org.firstinspires.ftc.teamcode;

//hardware imports
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;
import org.firstinspires.ftc.robotcore.external.Telemetry;

//vuforia imports
import org.firstinspires.ftc.robotcore.external.ClassFactory;
import org.firstinspires.ftc.robotcore.external.matrices.OpenGLMatrix;
import org.firstinspires.ftc.robotcore.external.matrices.VectorF;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder;
import org.firstinspires.ftc.robotcore.external.navigation.AxesReference;
import org.firstinspires.ftc.robotcore.external.navigation.Orientation;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackable;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackableDefaultListener;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackables;
import org.firstinspires.ftc.teamcode.R;

//other imports
import java.lang.Math;
import java.util.ArrayList;
import java.util.List;

public class MXRFTCRobot {

    //Hardware
    public DcMotor fLeftDrive, fRightDrive, bLeftDrive, bRightDrive, flyWheel, intakeTop, intakeBot, lift;
    public Servo clawOpenCloseL, clawOpenCloseR, clawUpDownL, clawUpDownR, leftLinSlide, rightLinSlide, flyWheelPush, flyWheelRampL, flyWheelRampR;
    private double lastRingPush = 0;
    private double driveFwdBk = 0; //forward back motion in mm, + is forward
    private double driveLR = 0; //left right motion in mm, + is right
    private double driveRotate = 0; //rotating motion in degrees, + is ccw
    public static final double ROT_GAIN = 0.01; //rate at which we correct heading error (rotation) - VALUE NEEDS TO BE TESTED
    public static final double LR_GAIN = 0.02; //rate at which we correct off-axis error (left/right) - VALUE NEEDS TO BE TESTED
    public static final double FWDBK_GAIN = 0.02; //rate at which we correct distance error (forward/back) - VALUE NEEDS TO BE TESTED

    //Vuforia
    private static final VuforiaLocalizer.CameraDirection CAMERA_CHOICE = VuforiaLocalizer.CameraDirection.BACK; //what camera would you like to use? FRONT = facing user (same side as display), BACK = away from user

    //variables
    private Telemetry telem;

    private static final float mmPerInch = 25.4f;

    public double currentRampServoPosition = 0.333; //90 degrees

    public ElapsedTime runtime = new ElapsedTime();

    public double currentPosition = 0;

    public MXRFTCRobot(){
    }

    public void init(HardwareMap hwMap, Telemetry telemetry){ //both the HardwareMap and Telemetry variables allow for us to use these FTC functions within a class. otherwise, we cannot use them
        //Telemetry Declaration
        telem = telemetry;

        //===========Hardware Mapping===========
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

        //Set Motors to Encoder Mode
        fLeftDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        fRightDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        bLeftDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        bRightDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        flyWheel.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        intakeTop.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        intakeBot.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        lift.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        //Reset servo positions to normal [THESE VALUES ARE ARBITRARY FOR NOW, THEY NEED TESTING]
        flyWheelPush.setPosition(.9); //flywheel pusher is by default retracted
        flyWheelRampL.setPosition(0.5); //flywheel ramp by default starts horizontal (90 degrees)
        flyWheelRampR.setPosition(0.5);
        clawOpenCloseL.setPosition(0.5); //claw by default starts open
        clawOpenCloseR.setPosition(0.5); //claw by default starts open
        clawUpDownL.setPosition(0.5);//claw by default starts down
        clawUpDownR.setPosition(0.5);//claw by default starts down


        //===========Vuforia Initilaization===========
        VuforiaLocalizer.Parameters parameters = new VuforiaLocalizer.Parameters(); //set up a vuforia parameter object to take parameters
        parameters.vuforiaLicenseKey = "AdVSNpT/////AAABmd+SHN4W9UzliidNdqbjIU93QOYyh7J2/VolbtNyddazYgKps6LYF7lQ8S4Jn+dFJg4d2iPrcnDuVpFdP3jBuh6BBL/hzlWXnNDgcYdtvAcVt6dcHeCSMe49lnVSUcMpkvt1CI7IpaV/aUkcM5H10vFxeBEKb1mN1au5BPxeMyAlzWQ91G6Xdk/X55avO9DdOgWE1FcDBz11lELX6YL7nbLHjLnrEY0q4sTS9ly+WdMN0w5RfFLSVSP8h0zAuxpL1G+/6gbNJ2BQi2KZhwodHVThoAa4KyUw3KaEBZFv7TLY3Emg5t6JguK12SRekIMIf4cnD3RpPf/qvYJZm/R34gDch2c78Erh1AgOBKehXtq3"; //enter license key here

        parameters.cameraDirection = CAMERA_CHOICE; //update camera parameter
        parameters.useExtendedTracking = false; //disable extended tracking (inaccurate at high speeds)
        VuforiaLocalizer vuforia = ClassFactory.getInstance().createVuforia(parameters); //initialize a vuforia object with parameters defined above

        VuforiaTrackables targetsUltimateGoal = vuforia.loadTrackablesFromAsset("UltimateGoal"); //load the trackable targets from a database somewhere
        VuforiaTrackable blueTowerGoalTarget = targetsUltimateGoal.get(0);
        blueTowerGoalTarget.setName("Blue Tower Goal Target");
        VuforiaTrackable redTowerGoalTarget = targetsUltimateGoal.get(1);
        redTowerGoalTarget.setName("Red Tower Goal Target");
        VuforiaTrackable redAllianceTarget = targetsUltimateGoal.get(2);
        redAllianceTarget.setName("Red Alliance Target");
        VuforiaTrackable blueAllianceTarget = targetsUltimateGoal.get(3);
        blueAllianceTarget.setName("Blue Alliance Target");
        VuforiaTrackable frontWallTarget = targetsUltimateGoal.get(4);
        frontWallTarget.setName("Front Wall Target");

        List<VuforiaTrackable> allTrackables = new ArrayList<VuforiaTrackable>(); //create an arraylist to shove all of the targets into
        allTrackables.addAll(targetsUltimateGoal); //shove all of the above targets into an arraylist for easy use

        OpenGLMatrix targetOrientation = OpenGLMatrix
                .translation(0, 0, 150) //this moves the targets up 150cm (6 inches) above the floor so that the images are not inside of the floor (they spawn on 0,0,0)
                .multiplied(Orientation.getRotationMatrix(AxesReference.EXTRINSIC,AxesOrder.XYZ, AngleUnit.DEGREES, 90, 0, -90));//rotates the image so it is not flat but instead vertical, and is now facing the positive x axis

        final float CAMERA_FORWARD_DISPLACEMENT = -5 * mmPerInch; //center of the bot on the floor is zero, front of bot is +
        final float CAMERA_LEFT_DISPLACEMENT = -2 * mmPerInch; //center of the bot on the floor is the zero, right is +
        final float CAMERA_VERTICAL_DISPLACEMENT = 4.5f * mmPerInch; //upwards is the +z axis, zero being the field floor


        OpenGLMatrix phoneLocation = OpenGLMatrix
                .translation(CAMERA_FORWARD_DISPLACEMENT, CAMERA_LEFT_DISPLACEMENT, CAMERA_VERTICAL_DISPLACEMENT)
                .multiplied(Orientation.getRotationMatrix(AxesReference.EXTRINSIC,AxesOrder.XYZ, AngleUnit.DEGREES, 0, -90, 0)); //flip the phone 90 degrees on the y axis so that the front of the phone faces towards the field

        for(VuforiaTrackable trackable : allTrackables){
            trackable.setLocation(targetOrientation); //sets all of the targets to the same orientation designated on line 134
            ((VuforiaTrackableDefaultListener)trackable.getListener()).setPhoneInformation(phoneLocation, parameters.cameraDirection);//applies camera orientation as defined on line 143 to the phone
        }

        telem.addLine("Initialization Complete");
        telem.update();
    }
    //===========HARDWARE METHODS===========
    //Flywheel methods
    public void setRampAngle(double angleChange){
        double change = angleChange/270;
        currentRampServoPosition += change;

        if(currentRampServoPosition > 1.0)
            currentRampServoPosition = 1.0;
        else if(currentRampServoPosition < 0.0)
            currentRampServoPosition = 0.0;

        flyWheelRampL.setPosition(currentRampServoPosition);
        flyWheelRampR.setPosition(currentRampServoPosition);
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
    public void mecanumDrive(double leftJSY, double leftJSX, double rightJSX){ //currently only works for rotation for some odd reason
        //IMPORTANT! YOU HAVE TO REVERSE THE JOYSTICK INPUTS IN THE PARAMETERS FOR THIS TO WORK CORRECTLY
        //leftJSX - left/right movement, leftJSY - forward/backward movement, rightJSX - cw/ccw rotation
        double frontLeft, frontRight, backLeft, backRight, maximum;
        frontLeft = leftJSY+leftJSX+rightJSX;
        frontRight = leftJSY-leftJSX-rightJSX;
        backLeft = leftJSY-leftJSX+rightJSX;
        backRight = leftJSY+leftJSX-rightJSX;

        maximum = Math.max(Math.max(Math.abs(frontLeft), Math.abs(frontRight)), Math.max(Math.abs(backLeft),Math.abs(backRight))); //compares all 4 motor values and sees which one is the largest

        if (maximum > 1.0){
            frontLeft /= maximum;
            frontRight /= maximum;
            backLeft /= maximum;
            backRight /= maximum;
        }

        fLeftDrive.setPower(frontLeft);
        fRightDrive.setPower(frontRight);
        bLeftDrive.setPower(backLeft);
        bRightDrive.setPower(backRight);

        telem.addLine("Motor Speeds - FL: " + frontLeft + " FR " + frontRight + " BL " + backLeft + " BR :" + backRight);
        telem.update();
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
    //===========HARDWARE METHODS===========

    //===========VUFORIA METHODS===========

    //===========VUFORIA METHODS===========

    //UNFINISHED AUTON METHODS
    public void vuforiaMecanumAutonDrive(double driveFB, double driveStrafe, double driveRot){
        //this method takes three variables - (driveFB) forward backwards motion in milimeters, (driveStrafe) left right motion in milimeters, and (driveRot) rotation in degrees and moves towards a target
        double fwdBack= driveFB * FWDBK_GAIN;
        double strafe= driveStrafe * LR_GAIN;
        double rotation = driveRot * ROT_GAIN;
        mecanumDrive(fwdBack,0,0);
        mecanumDrive(0,strafe,0);
        mecanumDrive(0,0,rotation);

    }
}

