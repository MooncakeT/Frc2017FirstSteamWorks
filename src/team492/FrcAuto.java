/*
 * Copyright (c) 2017 Titan Robotics Club (http://www.titanrobotics.com)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package team492;

import java.text.SimpleDateFormat;
import java.util.Date;

import hallib.HalDashboard;
import trclib.TrcRobot;

public class FrcAuto implements TrcRobot.RobotMode
{
    private static final boolean USE_TRACELOG = true;

    public enum AutoStrategy
    {
        X_TIMED_DRIVE,
        Y_TIMED_DRIVE,
        X_DISTANCE_DRIVE,
        Y_DISTANCE_DRIVE,
        TURN_DEGREES,
        DO_NOTHING
    }

    private Robot robot;

    private AutoStrategy autoStrategy;
    private double delay;
    private double driveTime;
    private double drivePower;
    private double driveDistance;
    private double turnDegrees;

    private TrcRobot.RobotCommand autoCommand;

    public FrcAuto(Robot robot)
    {
        this.robot = robot;
    }   //FtcAuto

    //
    // Implements TrcRobot.RunMode.
    //

    @Override
    public void startMode()
    {
        HalDashboard.getInstance().clearDisplay();
        autoStrategy = robot.autoStrategyMenu.getCurrentChoiceObject();
        delay = robot.delayMenu.getCurrentValue();
        driveTime = robot.driveTimeMenu.getCurrentValue();
        drivePower = robot.drivePowerMenu.getCurrentValue();
        driveDistance = robot.driveDistanceMenu.getCurrentValue()*12.0;
        turnDegrees = robot.turnDegreesMenu.getCurrentValue();

        Date now = new Date();

        if (USE_TRACELOG)
        {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_hh:mm");
            robot.tracer.openTraceLog(dateFormat.format(now) + ".log");
        }

        robot.tracer.traceInfo(Robot.programName, "%s: ***** Starting autonomous *****", now.toString());

        robot.driveBase.resetPosition();

        switch (autoStrategy)
        {
            case X_TIMED_DRIVE:
                autoCommand = new CmdTimedDrive(robot, delay, driveTime, drivePower, 0.0, 0.0);
                break;

            case Y_TIMED_DRIVE:
                autoCommand = new CmdTimedDrive(robot, delay, driveTime, 0.0, drivePower, 0.0);
                break;

            case X_DISTANCE_DRIVE:
                autoCommand = new CmdPidDrive(robot, delay, driveDistance, 0.0, 0.0);
                break;

            case Y_DISTANCE_DRIVE:
                autoCommand = new CmdPidDrive(robot, delay, 0.0, driveDistance, 0.0);
                break;

            case TURN_DEGREES:
                autoCommand = new CmdPidDrive(robot, delay, 0.0, 0.0, turnDegrees);
                break;

            default:
            case DO_NOTHING:
                autoCommand = null;
                break;
        }
    }   //startMode

    @Override
    public void stopMode()
    {
        robot.driveBase.stop();
        if (USE_TRACELOG)
        {
            robot.tracer.closeTraceLog();
        }
    }   //stopMode

    @Override
    public void runPeriodic(double elapsedTime)
    {
    }   //runPeriodic

    @Override
    public void runContinuous(double elapsedTime)
    {
        if (autoCommand != null)
        {
            autoCommand.cmdPeriodic(elapsedTime);
        }
    }   //runContinuous

}   //class FtcAuto
