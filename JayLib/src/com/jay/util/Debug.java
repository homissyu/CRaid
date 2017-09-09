/*************************************************************************
* INVENIO PROPRIETARY AND CONFIDENTIAL INFORMATION			 *
*									 *
* Copyright 1997-1998 Invenio Technologies Corporation as an unpublished *
* work. All rights reserved.					 	 *
*								       	 *
* This document and the information herein are Proprietary and     	 *
* Confidential information of Invenio and not for general release,       *
* Unauthorized use, reproduction, or dissemination is strictly           *
* prohibited.								 *
*************************************************************************/
// The Debug class allows for runtime Debugging with various verbosity levels.
//
// The Debug class writes to the standard out stream (System.out.println).
// An executable can also set a log file by calling setErrLog(<filename>). In
// this case, output is written to both standard out and the log file.
//
// The verbosity level can be changed by setting the config variable MAX_LOGFILE_SIZE
//      verbosity   - trace verbosity level at 1 to 4 (1 is less, 4 is max)
//
// To invoke the trace method, each module must instantiate a Debug object
// at startup time to initialize the object (make sure the Install object has
// already been created). Then you can use the trace() method. The
// entire class is static, so all you need to do is call Debug.trace() or
// Debug.traceError(). For example:
//
//      import Invenio.Util.Debug;
//
//      public static final String SUBSYSTEM = "DataPollingManager";
//
//      // Create a Debug object
//      new Debug();
//
//      Debug.trace(SUBSYSTEM,1,"MESSAGE TEXT");
//      Debug.trace(SUBSYSTEM,e,"MESSAGE TEXT");

package com.jay.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.StringTokenizer;
import java.util.TimeZone;

public class Debug {
    public static final String SOURCE_ID = "$Id: Debug.java,v v 1.0 2003/10/17 20:57:00 Jonathan Exp $";
    public static final String SUBSYSTEM = "Debug";

    private static final String BACKUPFILEEXT = ".bak";
    private static final int NUMWRITESFORTRUNC = 1; // 50;

    // when logfile gets bigger than this, move it
    private static long mMaxLogFileSize = 4194304; // 4MB

    private static boolean mIsMakingBySize = true;
    private static String mCurrdate = null;

    // Check the logfile size after this many writes to it...
    // set this to max so it checks size at startup
    private static int mNumWrites = NUMWRITESFORTRUNC;

    private static boolean mInitialized = false;
    private static ArrayList<String> mSubSystems = new ArrayList<String>();
    private static int mVerbosity = 1;

    // write to mLogFile to get text into the log file.
    private static PrintWriter mLogFile = null;
    private static Object mLogFile_sem = new Object();

    // an actual file object pointer to this file
    private static File mLogFileFile = null;
    // full path and name of the log file.
    private static String mLogFilePath = null;
    private static String mLogFileName = null;
    private static boolean mClosingLogfile = false;

    // PrintWriter wrapper for STDOUT
    private static PrintWriter mStdOut = null;
    // Do we print to stdout?
    private static boolean mStdOutPrintEnabled = false;

    private static SimpleDateFormat mDateFormat = null;
    static {
//        mDateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
        mDateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        mDateFormat.setTimeZone(TimeZone.getDefault());
    }

    // The maximum size of the logging log file
    private static final String LOGGING_LOGFILENAME = "Debug.log";
    private static final int LOGGING_LOGFILE_MAX_LENGTH = 1024 * 1024;
    private static final String LOGGING_LOGFILE_HEADER =
                "# This is the logfile of the Maxigent logging facility.\n";

    private static boolean mBatchMode = false;
    
    
    // Constructor, read environment variable and set data members
    //--------------------------------------------------
    public Debug(String sSubSystem, int iVerbosity, String sDebugType) throws Exception {
        if (mInitialized)
            return;

        mStdOut = new PrintWriter(System.out, true);

        String env = sSubSystem;
        addSubsystems(env);
        mVerbosity = iVerbosity;

        String tmp = sDebugType;
        if ( tmp.equals(CommonConst.LOG_TYPE_SIZE) ) {
            mIsMakingBySize = true;
        } else {
            mIsMakingBySize = false;
        }

        mMaxLogFileSize = CommonConst.Max_DEBUG_FILE_LENGTH;

        String fosFileName = CommonConst.CRAID_PATH + LOGGING_LOGFILENAME;
        File fosFile = new File(fosFileName);
        if (!fosFile.exists()) {
            debugLogfilePrint("");
        }

        mInitialized = true;
    }    
    
    public static void setStdOut() {;
    		mStdOut = new PrintWriter(System.out, true);
    }

    //--------------------------------------------------
    public static int getVerbosity() {
        return mVerbosity;
    }

    //--------------------------------------------------
    public static void setVerbosity(int aVerbosity) {
        trace(SUBSYSTEM, 1, "Setting trace verbosity to " + aVerbosity);
        mVerbosity = aVerbosity;
    }

    //--------------------------------------------------
    public static void addSubsystems(String aSubsystems) {
        trace(SUBSYSTEM, 1, "Adding trace subsystem " + aSubsystems);
        if (! aSubsystems.equals("ALL")) {
            StringTokenizer strkr = new StringTokenizer(aSubsystems, ":");
            while(strkr.hasMoreTokens()) {
                mSubSystems.add(strkr.nextToken());
            }
        } else {
            mSubSystems.add(aSubsystems);
        }
    }

    //--------------------------------------------------
    public static void setErrLog(String aErrLog) throws Exception {
        setErrLog(aErrLog, true);
    }

    //--------------------------------------------------
    private static void setErrLog(String aLogFileName, boolean isNew) throws Exception {
        if (mLogFile != null || aLogFileName == null ||
                                aLogFileName.length() == 0)
            return;

        try {
            synchronized (mLogFile_sem) {
                String logPath = CommonConst.CRAID_PATH;
                File log = new File(logPath);
                if (! log.exists()) {
                    log.mkdir();
                }

                if ( mIsMakingBySize ) {
                    mLogFileName = aLogFileName;
                } else {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
                    Date rightNow = new Date();
                    mCurrdate = sdf.format(rightNow);
                    mLogFileName = aLogFileName + "." + mCurrdate;
                }
//                mLogFileName = aLogFileName;
//                mLogFilePath = logPath + File.separator + aLogFileName;
//                mLogFilePath = logPath + File.separator + mLogFileName;
                mLogFileFile = new File(mLogFileName);
                FileOutputStream fos = new FileOutputStream(mLogFileName, true);
                mLogFile = new PrintWriter(fos, true);
                if (isNew) {
                    logfilePrint("\n\n\n" +
                               mDateFormat.format(new Date()).toString() +
                               ": *** BEGIN LOGFILE: " + mLogFileName, true);
                } else {
                    logfilePrint("\n\n\n" +
                               mDateFormat.format(new Date()).toString() +
                               ": *** CONTINUING LOGFILE: " + mLogFileName, true);
                }
            }
        } catch (IOException e) {
            throwException(e, "Unable to set logfile to " + mLogFileName);
        }
    }

    //--------------------------------------------------
    public static void closeErrLog() {
        if (mLogFile != null) {
            synchronized(mLogFile_sem) {
                trace(SUBSYSTEM, 1, "Closing Log File");
                mLogFile.close();
                mLogFile = null;
            }
        }
    }


    //--------------------------------------------------
    private static PrintWriter openDebugLogfile(String fosFileName) {
        FileOutputStream fos;
        File fosFile = new File(fosFileName);

        if (fosFile.exists() && fosFile.length() > LOGGING_LOGFILE_MAX_LENGTH) {
            // remove it if its big
            try {
                if (!fosFile.delete()) {
                    stdoutPrint("Unable to remove " + fosFileName);
                    return null;
                }
            } catch (Exception e) {
                stdoutPrint("Unable to remove " + fosFileName + ": " +
                                e.getLocalizedMessage());
                return null;
            }
        }

        boolean exists = false;
        if (fosFile.exists())
            exists = true;

        // append to it...
        try {
            fos = new FileOutputStream(fosFile);
        } catch (IOException e) {
            stdoutPrint("Unable to create output stream to " + fosFileName + ": " +
                                    e.getLocalizedMessage());
            return null;
        }

        // make it easier to write...
        PrintWriter pwos = new PrintWriter(fos, true);

        // print a header...
        if (!exists) {
            pwos.println(LOGGING_LOGFILE_HEADER);
        }

        return pwos;
    }

    //--------------------------------------------------
    private static void closeDebugLogfile(PrintWriter pwos, String fileName) {
        try {
            pwos.close();
        } catch (Exception e) {
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw, true));
            stdoutPrint("Error closing debug logfile " + fileName +
                                sw.toString());
            return;
        }

        if (pwos.checkError()) {
            stdoutPrint("Error writing to debug logfile " + fileName);
            return;
        }
    }

    //--------------------------------------------------
    private static String getDebugLogfileName() {
        return CommonConst.CRAID_PATH +
                        LOGGING_LOGFILENAME;
    }

    // These are loggging exceptions so we expect that mLogFile is not
    // set.
    // Print out the info to the DEBUGGING logfile.
    //--------------------------------------------------
    private static void throwException(Exception aException, String s) throws Exception {
        String fosFileName = getDebugLogfileName();

        PrintWriter pwos = openDebugLogfile(fosFileName);

        if (pwos == null)
            throw aException;

        // actually print the stack trace and message to the DEBUG logfile
        if (aException != null)
            aException.printStackTrace(pwos);

        pwos.println(mDateFormat.format(new Date()).toString() + ": " + s);

        closeDebugLogfile(pwos, fosFileName);

        throw aException;
    }

    // Print a message to a special debug logfile, then close it.
    //--------------------------------------------------
    private static void debugLogfilePrint(String s) {
        String fosFileName = getDebugLogfileName();
        PrintWriter pwos = openDebugLogfile(fosFileName);
        if (pwos == null)
            return;
        pwos.println(mDateFormat.format(new Date()).toString() + s);
        closeDebugLogfile(pwos, fosFileName);
    }

    // Print something to standard output if it is enabled
    //--------------------------------------------------
    private static void stdoutPrint(String s, boolean nl) {
    		if (mStdOut != null && mStdOutPrintEnabled) {
            synchronized(mStdOut) {
                if (nl)
                    mStdOut.println(s);
                else {
                    mStdOut.print(s);
                    mStdOut.flush();
                }
            }
        }
    }

    // Print something to standard output if it is enabled
    //--------------------------------------------------
    private static void stdoutPrint(String s) {
        stdoutPrint(s, true);
    }

    //--------------------------------------------------
    public static void logfilePrint(String s, boolean nl) {
        if (mLogFile != null) {
            String ss = mDateFormat.format(new Date()).toString() + ": " + s;
            synchronized (mLogFile_sem) {
                if (nl)
                    mLogFile.println(ss);
                else {
                    mLogFile.print(ss);
                    mLogFile.flush();
                }
            }
            doTrunc();
        }
    }

    //--------------------------------------------------
    private static void doTrunc() {
        mNumWrites++;

        if (mNumWrites >= NUMWRITESFORTRUNC) {
            mNumWrites = 0;
            if ( mIsMakingBySize ) {
                if (mLogFileFile.length() > mMaxLogFileSize) {
                    synchronized(mLogFile_sem) {
                        if (mClosingLogfile)
                            return;
                        mClosingLogfile = true;
                        stdoutPrint("TRUNCATING LOGFILE!");
                        closeErrLog();
                        File backupFile = new File(mLogFilePath + BACKUPFILEEXT);
                        backupFile.delete();

                        if (!mLogFileFile.renameTo(backupFile)) {
                            debugLogfilePrint("Unable to rename " +
                                                    mLogFileFile.getName() +
                                                    " to " +
                                                    backupFile.getName());
                        }

                        try {
                            setErrLog(mLogFileName, false);
                        } catch (Exception e) {
                            try {
                                throwException(e, "Unable to open log file!" +
                                                    mLogFileName);
                            } catch (Exception ee) {
                                // don't really want to throw this
                            }
                        }
                        mClosingLogfile = false;
                    } // synchronized
                } // if
            } else {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
                Date rightNow = new Date();
                String nowdate = sdf.format(rightNow);
                if ( ! nowdate.equals(mCurrdate) ) {
                    synchronized(mLogFile_sem) {
                        if (mClosingLogfile)
                            return;
                        mClosingLogfile = true;
                        stdoutPrint("TRUNCATING LOGFILE!");
                        closeErrLog();

                        try {
                            int idx = mLogFileName.lastIndexOf(".");
                            mLogFileName = mLogFileName.substring(0, idx);
                            setErrLog(mLogFileName, true);
                        } catch (Exception e) {
                            try {
                                throwException(e, "Unable to open log file!" +
                                                    mLogFileName);
                            } catch (Exception ee) {
                                // don't really want to throw this
                            }
                        }
                        mClosingLogfile = false;
                    } // synchronized
                }
            }

        }
    }

    //--------------------------------------------------
    public static void println(String s) {
        //    mStdOut.println(s);
    }

    //--------------------------------------------------
    public static void print(String s) {
        //    mStdOut.print(s);
    }

    //--------------------------------------------------
    public static void setBatch(boolean b) {
        mBatchMode = b;
    }

    //--------------------------------------------------
    public static void waitForExit() {
        if (!mBatchMode) {
            System.out.print("Hit return to continue...");
            try {
                System.in.read();
            } catch (Exception e) {
            }
        }
    }

    //--------------------------------------------------
    private static void trace(String aSubSystem, int aVerbosity,
                                    String aMsg, boolean nl) {
        // Print message to stream only if subsystem is specified and verbosity
        // is set appropriately
        if (mVerbosity >= aVerbosity &&
                    (mSubSystems.contains("ALL") || mSubSystems.contains(aSubSystem))) {
            String string = aSubSystem + ": " + aMsg;
            stdoutPrint(string, nl);
            logfilePrint(string, nl);
        }
    }


    // Method prints out trace messages
    //--------------------------------------------------
    public static void trace(String aSubSystem, int aVerbosity, String aMsg) {
        trace(aSubSystem, aVerbosity, aMsg, true);
    }

    //--------------------------------------------------
    public static void trace(int aVerbosity, String aMsg) {
        trace("", aVerbosity, aMsg);
    }

    // Method prints out trace messages
    //--------------------------------------------------
    public static void traceNnl(String aSubSystem, int aVerbosity, String aMsg) {
        trace(aSubSystem, aVerbosity, aMsg, false);
    }

    // Method prints out error tracing messages
    //--------------------------------------------------
    public static void traceError(String aSubSystem, Throwable t, String aMsg) {
        // Print message to stream only if subsystem is specified
        if ((mSubSystems.contains("ALL") || mSubSystems.contains(aSubSystem))) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw, true);

            pw.println("********* BEGIN DEBUG STACK TRACE *********");
            if (t != null)
                t.printStackTrace(pw);
            pw.println("********* END DEBUG STACK TRACE *********");
            pw.println(aMsg + (t != null ? t.getLocalizedMessage() + "\n" : ""));

            String s = sw.toString();

            stdoutPrint(s, true);
            logfilePrint(s, true);
        }
    }

    // Method prints out error tracing messages
    //--------------------------------------------------
    public static void traceFatalError(String aSubSystem, Throwable t, String aMsg) {

        traceError(aSubSystem, t, aMsg);
        closeErrLog();

        try {
            // Sometimes the text will not be streamed out in time, so sleep
            // before exiting
            Thread.sleep(3000);
        } catch (Exception ex) {
        } // do nothing

        System.exit(1);
    }

    public static String getLogfileName() {
        return mLogFileName;
    }

}


