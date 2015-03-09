/*******************************************************************************
 *
 *  Copyright (c) 2014 , Hookflash Inc.
 *  All rights reserved.
 *  
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions are met:
 *  
 *  1. Redistributions of source code must retain the above copyright notice, this
 *  list of conditions and the following disclaimer.
 *  2. Redistributions in binary form must reproduce the above copyright notice,
 *  this list of conditions and the following disclaimer in the documentation
 *  and/or other materials provided with the distribution.
 *  
 *  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 *  ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 *  WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 *  DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 *  ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 *  (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 *  LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 *  ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *  SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *  
 *  The views and conclusions contained in the software and documentation are those
 *  of the authors and should not be interpreted as representing official policies,
 *  either expressed or implied, of the FreeBSD Project.
 *******************************************************************************/
package com.openpeer.javaapi;

public class OPLogger {
    private static OPLogger instance;

    public static OPLogger getInstance() {
        if (instance == null) {
            instance = new OPLogger();
        }
        return instance;
    }

    private int appId;

    private int getAppId() {
        if (appId == 0) {
            appId = getApplicationSubsystemID();
        }
        return appId;
    }

    // public static final String moduleApplication = "openpeer_application";
    // public static final String moduleSDK = "openpeer_sdk";
    // public static final String moduleMedia = "openpeer_media";
    // public static final String moduleWebRTC = "openpeer_webrtc";
    // public static final String moduleCore = "openpeer_core";
    // public static final String moduleStackMessage = "openpeer_stack_message";
    // public static final String moduleStack = "openpeer_stack";
    // public static final String moduleServices = "openpeer_services";
    // public static final String moduleServicesWire = "openpeer_services_wire";
    // public static final String moduleServicesIce = "openpeer_services_ice";
    // public static final String moduleServicesTurn = "openpeer_services_turn";
    // public static final String moduleServicesRudp = "openpeer_services_rudp";
    // public static final String moduleServicesHttp = "openpeer_services_http";
    // public static final String moduleServicesMls = "openpeer_services_mls";
    // public static final String moduleServicesTcp = "openpeer_services_tcp_messaging";
    // public static final String moduleServicesTransport = "openpeer_services_transport_stream";
    // public static final String moduleZsLib = "zsLib";
    // public static final String moduleZsLibSocket = "zsLib_socket";
    // public static final String moduleJavaScript = "openpeer_javascript";
    // temporary solution for unit test workaround.
    private boolean mUnitTest;

    public static void setIsUnitTest(Boolean isUnitTest) {
        getInstance().mUnitTest = isUnitTest;
    }

    /**
     * Helper function to log through OpenPeer logging utility. This function will retrieve the class,method and line number so there's no
     * need to pass those in from application
     * 
     * @param level
     * @param message
     */
    public static void debug(OPLogLevel level, String message) {
        if (!getInstance().mUnitTest) {
            log(level, OPLogSeverity.Informational, message);
        }
    }

    /**
     * Helper function to log through OpenPeer logging utility. This function will retrieve the class,method and line number so there's no
     * need to pass those in from application
     * 
     * @param level
     * @param message
     */
    public static void error(OPLogLevel level, String message) {
        if (!getInstance().mUnitTest) {
            log(level, OPLogSeverity.Error, message);
        }
    }

    /**
     * Helper function to log through OpenPeer logging utility. This function will retrieve the class,method and line number so there's no
     * need to pass those in from application
     * 
     * @param level
     * @param message
     */
    public static void warning(OPLogLevel level, String message) {
        log(level, OPLogSeverity.Warning, message);
    }

    private static void log(OPLogLevel level, OPLogSeverity severity, String message) {
        StackTraceElement trace = Thread.currentThread().getStackTrace()[3];
        String fullClassName = trace.getClassName();
        String className = fullClassName.substring(fullClassName.lastIndexOf(".") + 1);
        String methodName = trace.getMethodName();
        int lineNumber = trace.getLineNumber();
        log(getInstance().getAppId(),
                severity,
                level,
                message,
                className + ":" + methodName,
                fullClassName,
                lineNumber);
    }

    // PURPOSE: Install a logger to output to the standard out
    public static native void installStdOutLogger(boolean colorizeOutput);

    // -----------------------------------------------------------------------
    // PURPOSE: Install a logger to output to a file.
    // NOTE: On a linux based system you can do this trick:
    //
    // From the command prompt type:
    // mkfifo /tmp/openpeer.fifo
    // while true; do cat /tmp/openpeer.fifo; sleep 1; done
    //
    // Pass in filename as: "/tmp/openpeer.fifo"
    //
    // Alternatively, pass in the name of a file and it will create
    // the file and output all logging to the file.
    public static native void installFileLogger(String fileName, boolean colorizeOutput);

    // -----------------------------------------------------------------------
    // PURPOSE: Install a logger to output to a telnet prompt.
    // NOTE: If listen port 59999 is used, then from the system type:
    //
    // telnet 1.2.3.4 59999
    //
    // (where 1.2.3.4 is the IP address of the client software)
    //
    public static native void installTelnetLogger(
            int listenPort, // what port to bind to on 0.0.0.0:port to listen for incoming telnet sessions
            long maxSecondsWaitForSocketToBeAvailable, // since the port might still be in use for a period of time between runs (TCP
                                                       // timeout), how long to wait for the port to come alive (recommend 60)
            boolean colorizeOutput
            );

    // -----------------------------------------------------------------------
    // PURPOSE: Install a logger that sends a telnet outgoing to a telnet
    // listening server.
    public static native void installOutgoingTelnetLogger(
            String serverToConnect,
            boolean colorizeOutput,
            String stringToSendUponConnection
            );

    // -----------------------------------------------------------------------
    // PURPOSE: Install a logger to output to the debugger window.
    public static native void installDebuggerLogger();

    // -----------------------------------------------------------------------
    // PURPOSE: Install a logger to monitor the functioning of the application
    // internally.
    public static native void installCustomLogger(OPLoggerDelegate delegate);

    // -----------------------------------------------------------------------
    // PURPOSE: Mirror methods to install routines to uninstall various
    // types of loggers.
    public static native void uninstallStdOutLogger();

    public static native void uninstallFileLogger();

    public static native void uninstallTelnetLogger();

    public static native void uninstallOutgoingTelnetLogger();

    public static native void uninstallDebuggerLogger();

    // -----------------------------------------------------------------------
    // PURPOSE: Checks if the telnet logger is listening for incoming telnet
    // connections.
    // RETURNS: true if telnet logger is listening for connections
    public static native boolean isTelnetLoggerListening();

    // -----------------------------------------------------------------------
    // PURPOSE: Checks if the telnet logger has a telnet client connected
    // to the telnet logger port
    // RETURNS: true if telnet client is connected to telnet logger port
    public static native boolean isTelnetLoggerConnected();

    // -----------------------------------------------------------------------
    // PURPOSE: Checks if the outgoing telnet logger is connected to a telnet
    // logging server.
    // RETURNS: true if outgoing telnet logger is connected to a telnet
    // logging server
    public static native boolean isOutgoingTelnetLoggerConnected();

    /**
     * PURPOSE: Gets the unique ID for the application's subsystem (to pass into the log routine for GUI application logging)
     */
    public static native int getApplicationSubsystemID();

    /**
     * PURPOSE: Gets the currently set log level for a particular subsystem.
     * 
     * @param subsystemUniqueID
     * @return
     */
    public static native OPLogLevel getLogLevel(int subsystemUniqueID);

    /**
     * PURPOSE: Filter all subsystems to a specific log level. The log level will log at this level and above.
     */

    public static native void setLogLevel(OPLogLevel level);

    /**
     * PURPOSE: Sets a particular subsystem's log level by unique ID. The log level will log at this level and above.
     */
    public static native void setLogLevel(
            int subsystemUniqueID,
            OPLogLevel level
            );

    /**
     * PURPOSE: Sets a particular subsystem's log level by its subsystem name.
     * 
     * @param subsystemName
     * @param level
     */
    public static native void setLogLevel(
            String subsystemName,
            OPLogLevel level
            );

    // -----------------------------------------------------------------------
    // PURPOSE: Sends a message to the logger(s) for a particular subsystem.
    public static native void log(
            int subsystemUniqueID,
            OPLogSeverity severity,
            OPLogLevel level,
            String message,
            String function,
            String filePath,
            long lineNumber
            );
}
