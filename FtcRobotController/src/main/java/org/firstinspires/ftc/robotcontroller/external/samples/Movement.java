package org.firstinspires.ftc.robotcontroller.external.samples;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.Range;

@TeleOp(name = "Chudy movement")
public class Movement extends LinearOpMode {
    //Motors
    public DcMotor leftMotor, rightMotor;
    public double leftPower, rightPower;

    //Basic stuff
    public double maxSpeed = 1;

    //Just for Chudy
    public int gaymeter = 0;
    /**
     * This sample contains the bare minimum Blocks for any regular OpMode. The 3 blue
     * Comment Blocks show where to place Initialization code (runs once, after touching the
     * DS INIT button, and before touching the DS Start arrow), Run code (runs once, after
     * touching Start), and Loop code (runs repeatedly while the OpMode is active, namely not
     * Stopped).
     */
    @Override
    public void runOpMode()
    {
        //Initialization
        leftMotor = hardwareMap.get(DcMotor.class, "leftMotor");
        rightMotor = hardwareMap.get(DcMotor.class, "rightMotor");

        waitForStart();

        if(opModeIsActive())
        {
            // Put run blocks here.
            while(opModeIsActive())
            {
                //(totally not copy-pasted) movement
                double drive = -gamepad1.left_stick_y;
                double turn = gamepad1.right_stick_x;
                leftPower = Range.clip(drive + turn, -maxSpeed, maxSpeed);
                rightPower = Range.clip(drive - turn, -maxSpeed, maxSpeed);

                //Assigning these speeds
                leftMotor.setPower(leftPower);
                rightMotor.setPower(rightPower);

                //Gaymeter
                gaymeter++;
                telemetry.addData("Gaymeter", gaymeter);
                telemetry.update();
            }
        }
    }
}