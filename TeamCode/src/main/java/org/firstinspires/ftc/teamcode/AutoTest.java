//https://pedropathing.com/

package org.firstinspires.ftc.teamcode;

import static com.pedropathing.api.Paths.*;
import com.pedropathing.api.Paths;

import com.pedropathing.api.PoseFactory;
import com.pedropathing.follower.Follower;
import com.pedropathing.math.Pose;
import com.pedropathing.paths.Path;
import com.pedropathing.ivy.Command;
import com.pedropathing.ivy.Scheduler;
import static com.pedropathing.ivy.Scheduler.schedule;
import static com.pedropathing.ivy.commands.Commands.*;
import static com.pedropathing.ivy.groups.Groups.sequential;
import static com.pedropathing.ivy.pedro.PedroCommands.follow;

import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.LLResultTypes;
import com.qualcomm.hardware.limelightvision.LLStatus;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.robotcore.external.navigation.Pose3D;
import org.firstinspires.ftc.teamcode.pedro.Constants;

@Autonomous(name = "AutoTest", group = "Autonomous")
public class AutoTest extends LinearOpMode {

    private Follower follower;

    //Imma also add the limelight into the autonomus
    private Limelight3A limelight;
    private final String limelightName = "limelight";

    private final PoseFactory poseFactory = PoseFactory.degrees();

    private final Pose start = poseFactory.of(56, 8, 90);
    private final Pose path1 = poseFactory.of(56, 36, 180);


    //Variables for the test command
    private int elapsed = 0;
    private final int finish = 500;

    // Autonomous routine
    public Command autoRoutine() {
        return sequential(
                //Example of a Command
                Command.build()
                        .requiring(elapsed, finish)
                        //.setStart(() -> {})
                        .setExecute(() -> {
                            elapsed++;
                            telemetry.addData("test command", elapsed);
                            //telemetry.update();
                        })
                        .setDone(() -> elapsed >= finish)
                //        .setEnd(endCondition -> {})
                ,

                goTo(path1),
                goTo(start)
        );
    }

    @Override
    public void runOpMode() {
        //Follower
        Scheduler.reset();
        follower = Constants.create(hardwareMap);

        //Yeah so it can crash here...
        try
        {
            follower.setPose(start);
            follower.update();
        } catch (Exception e) {
            telemetry.addData("Error", e);
            telemetry.update();
        }

        //Limelight
        limelight = hardwareMap.get(Limelight3A.class, limelightName);
        telemetry.setMsTransmissionInterval(10);
        limelight.pipelineSwitch(0);
        limelight.start();

        waitForStart();
        schedule(autoRoutine());

        while (opModeIsActive()) {
            follower.update();
            //Scheduler.execute();

            //Follower prints
            telemetry.addData("x", follower.pose().x());
            telemetry.addData("y", follower.pose().y());
            telemetry.addData("heading", follower.pose().heading());

            if (follower.currentPath() != null) {
                telemetry.addData("Current path distance remaining", follower.distanceToEndpoint());
                telemetry.addData("Path number", follower.pathIndex());
            }

            //Limelight
            LLResult result = limelight.getLatestResult();
            if(result.isValid())
            {
                Pose3D botpose = result.getBotpose();

                //AprilTags
                for(LLResultTypes.FiducialResult aprilTag : result.getFiducialResults())
                {
                    //Prints
                    telemetry.addData("AprilTag ID", aprilTag.getFiducialId());
                    telemetry.addData("AprilTag Position", aprilTag.getRobotPoseFieldSpace().toString());
                }

                //Prints
                telemetry.addData("tx", result.getTx());
                telemetry.addData("txnc", result.getTxNC());
                telemetry.addData("ty", result.getTy());
                telemetry.addData("tync", result.getTyNC());

                telemetry.addData("Botpose", botpose.toString());
            }

            telemetry.update();
        }
    }

    //Just turns the follow command into a neat lil function
    public Command goTo(Pose pos) {
        Path target = Paths.line(follower.pose(), pos).linear(follower.pose(), pos);
        return follow(follower, target);
    }
}
