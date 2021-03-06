package com.inverce.mod.core;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RestrictTo;
import android.support.annotation.StringRes;

import com.inverce.mod.core.interfaces.LogListener;

/**
 * Wrapper around android Log class, with additional benefits.
 */
@SuppressWarnings({"unused", "WeakerAccess"})
public class Log {
    private static boolean DEBUG_MODE = true;
    private static final String BASE_TAG = "||";

    public static final int VERBOSE = 2, DEBUG = 3, INFO = 4, WARN = 5, ERROR = 6;
    public static final int EXCEPTION = 7, ASSERT = 8, NONE = Integer.MAX_VALUE;
    private static final int FULL = 1, SIMPLER = 2, SIMPLEST = 3;

    private static int LOGGING_LEVEL = VERBOSE;

    private static LogListener listener;

    @NonNull
    @SuppressWarnings("FieldCanBeLocal")
    private static String libraryPackage = "com.inverce.mod";
    private static String applicationPackage;

    /**
     * Sets log listener.
     *
     * @param listener the listener
     */
    public static void setListener(LogListener listener) {
        Log.listener = listener;
    }

    /**
     * Allows user to specify minimum log level to print.
     * Reduce log messages in production environment without removing all messages.
     *
     * @param LOGGING_LEVEL the logging level
     */
    public static void setLogLevel(int LOGGING_LEVEL) {
        Log.LOGGING_LEVEL = LOGGING_LEVEL;
    }

    /**
     * Sets debug mode.
     *
     * @param debugMode the debug mode
     */
    public static void setDebugMode(boolean debugMode) {
        DEBUG_MODE = debugMode;
    }

    public static boolean isLoggable(int level) {
        return DEBUG_MODE && LOGGING_LEVEL <= level;
    }

    @RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
    static boolean shouldPrint(@NonNull String className) {
        if (applicationPackage == null) {
            applicationPackage = IM.context().getPackageName();
        }
        return className.startsWith(libraryPackage) || className.contains(applicationPackage);
    }

    private static String getString(@StringRes int format, Object ... params) {
        return IM.resources().getString(format, params);
    }

    /**
     * Handle msg.
     *
     * @param lvl the lvl
     * @param tag the tag
     * @param msg the msg
     * @param o   the o
     */
    public static void handleMsg(int lvl, int tag, int msg, Object ... o) {
        if (!(DEBUG_MODE && LOGGING_LEVEL <= lvl)) {
            return;
        }

        if (tag == -1) {
            handleMsg(lvl, null, getString(msg, o));
        } else {
            handleMsg(lvl, getString(tag), getString(msg, o));
        }
    }

    /**
     * Handle message.
     *
     * @param lvl the level of message
     * @param tag the tag of message
     * @param msg the message
     * @param o   the list of additional parameters for String.format of message
     */
    public static void handleMsg(int lvl, String tag, @Nullable String msg, @NonNull Object ... o) {
        if (!(DEBUG_MODE && LOGGING_LEVEL <= lvl && msg != null)) {
            return;
        }

        tag = (tag != null) ? (BASE_TAG + "." + tag) : BASE_TAG;
        msg = (o.length > 0) ? String.format(msg, o) : msg;

        if (listener != null && lvl < EXCEPTION) {
            if (listener.handleMsg(lvl, tag, msg)) {
                return;
            }
        }

        dispatchMessage(lvl, tag, msg);
    }

    private static void dispatchMessage(int lvl, String tag, String msg) {
        switch (lvl) {
            case VERBOSE : android.util.Log.v(tag, msg); break;
            case DEBUG   : android.util.Log.d(tag, msg); break;
            case INFO    : android.util.Log.i(tag, msg); break;
            case WARN    : android.util.Log.w(tag, msg); break;
            case ERROR   : android.util.Log.e(tag, msg); break;
            case ASSERT  : handleExc(ASSERT, tag, msg, new AssertionError(msg)); break;
        }
    }

    /**
     * Handle exception.
     *
     * @param lvl the lvl of message
     * @param tag the tag used for this message
     * @param msg the message
     * @param o   the reported exception
     */
    public static void handleExc(int lvl, int tag, int msg, @NonNull Throwable o) {
        if (!(DEBUG_MODE && LOGGING_LEVEL <= lvl)) {
            return;
        }

        if (tag == -1) {
            handleExc(lvl, null, getString(msg), o);
        } else {
            handleExc(lvl, getString(tag), getString(msg), o);
        }
    }

    /**
     * Handle exception.
     *
     * @param simple_lvl the simple lvl of message
     * @param tag        the tag used for this message
     * @param msg        the message
     * @param o          the reported exception
     */
    public static void handleExc(int simple_lvl, String tag, String msg, @NonNull Throwable o) {
        if (!(DEBUG_MODE && LOGGING_LEVEL <= EXCEPTION)) {
            return;
        }

        if (listener != null) {
            listener.handleExc(simple_lvl, tag, msg, o);
        }

        tag = (tag != null) ? (BASE_TAG + "." + tag) : BASE_TAG;
        msg = (msg != null ? msg + ": " : "" ) + "<" + o.getClass().getSimpleName() + "> "+ o.getMessage();

        android.util.Log.e(tag, msg);

        switch (simple_lvl) {
            case FULL: o.printStackTrace(); break;
            case SIMPLER:
            default:
                android.util.Log.w("", msg);
                StackTraceElement[] stackTrace = o.getStackTrace();
                for (StackTraceElement aStackTrace : stackTrace) {
                    String className = aStackTrace.getClassName();
                    if (shouldPrint(className)) {
                        android.util.Log.w(tag, aStackTrace.toString());
                    }
                }

                if (o.getCause() != null)
                    android.util.Log.w(tag, "Caused by: " + "<" + o.getCause().getClass().getSimpleName() + "> "+ o.getCause().getMessage());
                break;
        }
    }

    public static void v(String tag, String message, Object ... o) {                                handleMsg(VERBOSE,      tag,    message,  o); }
    public static void v(String tag, String message) {                                              handleMsg(VERBOSE,      tag,    message    ); }
    public static void v(String message) {                                                          handleMsg(VERBOSE,      null,   message    ); }
    public static void v(String message, Object ... o) {                                            handleMsg(VERBOSE,      null,   message,  o); }
    public static void d(String tag, String message, Object ... o) {                                handleMsg(DEBUG,        tag,    message,  o); }
    public static void d(String tag, String message) {                                              handleMsg(DEBUG,        tag,    message    ); }
    public static void d(String message) {                                                          handleMsg(DEBUG,        null,   message    ); }
    public static void d(String message, Object ... o) {                                            handleMsg(DEBUG,        null,   message,  o); }
    public static void i(String tag, String message, Object ... o) {                                handleMsg(INFO, tag, message, o); }
    public static void i(String tag, String message) {                                              handleMsg(INFO,         tag,    message    ); }
    public static void i(String message) {                                                          handleMsg(INFO,         null,   message    ); }
    public static void i(String message, Object ... o) {                                            handleMsg(INFO,         null,   message,  o); }
    public static void w(String tag, String message, Object ... o) {                                handleMsg(WARN,         tag,    message,  o); }
    public static void w(String tag, String message) {                                              handleMsg(WARN,         tag,    message    ); }
    public static void w(String message) {                                                          handleMsg(WARN,         null,   message    ); }
    public static void w(String message, Object ... o) {                                            handleMsg(WARN,         null,   message,  o); }
    public static void e(String tag, String message, Object ... o) {                                handleMsg(ERROR,        tag,    message,  o); }
    public static void e(String tag, String message) {                                              handleMsg(ERROR,        tag,    message    ); }
    public static void e(String message) {                                                          handleMsg(ERROR,        null,   message    ); }
    public static void e(String message, Object ... o) {                                            handleMsg(ERROR,        null,   message,  o); }
    public static void a(String tag, String message, Object ... o) {                                handleMsg(ASSERT,       tag,    message,  o); }
    public static void a(String tag, String message) {                                              handleMsg(ASSERT,       tag,    message    ); }
    public static void a(String message) {                                                          handleMsg(ASSERT,       null,   message    ); }
    public static void a(String message, Object ... o) {                                            handleMsg(ASSERT,       null,   message,  o); }

    public static void v(@StringRes int tag, @StringRes int message, Object ... o) {                handleMsg(VERBOSE,      tag,    message,  o); }
    public static void v(@StringRes int tag, @StringRes int message) {                              handleMsg(VERBOSE,      tag,    message    ); }
    public static void v(@StringRes int message) {                                                  handleMsg(VERBOSE,      -1,     message    ); }
    public static void v(@StringRes int message, Object ... o) {                                    handleMsg(VERBOSE,      -1,     message,  o); }
    public static void d(@StringRes int tag, @StringRes int message, Object ... o) {                handleMsg(DEBUG,        tag,    message,  o); }
    public static void d(@StringRes int tag, @StringRes int message) {                              handleMsg(DEBUG,        tag,    message    ); }
    public static void d(@StringRes int message) {                                                  handleMsg(DEBUG,        -1,     message    ); }
    public static void d(@StringRes int message, Object ... o) {                                    handleMsg(DEBUG,        -1,     message,  o); }
    public static void i(@StringRes int tag, @StringRes int message, Object ... o) {                handleMsg(INFO,         tag,    message,  o); }
    public static void i(@StringRes int tag, @StringRes int message) {                              handleMsg(INFO,         tag,    message    ); }
    public static void i(@StringRes int message) {                                                  handleMsg(INFO,         -1,     message    ); }
    public static void i(@StringRes int message, Object ... o) {                                    handleMsg(INFO,         -1,     message,  o); }
    public static void w(@StringRes int tag, @StringRes int message, Object ... o) {                handleMsg(WARN,         tag,    message,  o); }
    public static void w(@StringRes int tag, @StringRes int message) {                              handleMsg(WARN,         tag,    message    ); }
    public static void w(@StringRes int message) {                                                  handleMsg(WARN,         -1,     message    ); }
    public static void w(@StringRes int message, Object ... o) {                                    handleMsg(WARN,         -1,     message,  o); }
    public static void e(@StringRes int tag, @StringRes int message, Object ... o) {                handleMsg(ERROR,        tag,    message,  o); }
    public static void e(@StringRes int tag, @StringRes int message) {                              handleMsg(ERROR,        tag,    message    ); }
    public static void e(@StringRes int message) {                                                  handleMsg(ERROR,        -1,     message    ); }
    public static void e(@StringRes int message, Object ... o) {                                    handleMsg(ERROR,        -1,     message,  o); }
    public static void a(@StringRes int tag, @StringRes int message, Object ... o) {                handleMsg(ASSERT,       tag,    message,  o); }
    public static void a(@StringRes int tag, @StringRes int message) {                              handleMsg(ASSERT, tag, message); }
    public static void a(@StringRes int message) {                                                  handleMsg(ASSERT, -1, message); }
    public static void a(@StringRes int message, Object ... o) {                                    handleMsg(ASSERT, -1, message, o); }

    public static void ex(@NonNull Throwable o) {                                                            handleExc(FULL,         null, null, o); }
    public static void exs(@NonNull Throwable o) {                                                           handleExc(SIMPLER,      null, null, o); }
    public static void exm(@NonNull Throwable o) {                                                           handleExc(SIMPLEST,     null, null, o); }

    public static void ex(String tag, String message, @NonNull Throwable o) {                                handleExc(FULL,         tag,  message, o); }
    public static void ex(String message, @NonNull Throwable o) {                                            handleExc(FULL,         null, message, o); }
    public static void exs(String tag, String message, @NonNull Throwable o) {                               handleExc(SIMPLER,      tag,  message, o); }
    public static void exs(String message, @NonNull Throwable o) {                                           handleExc(SIMPLER,      null, message, o); }
    public static void exm(String tag, String message, @NonNull Throwable o) {                               handleExc(SIMPLEST,     tag,  message, o); }
    public static void exm(String message, @NonNull Throwable o) {                                           handleExc(SIMPLEST,     null, message, o); }

    public static void ex(@StringRes int tag, @StringRes int message, @NonNull Throwable o) {                handleExc(FULL,         tag,  message, o); }
    public static void ex(@StringRes int message, @NonNull Throwable o) {                                    handleExc(FULL,         -1,   message, o); }
    public static void exs(@StringRes int tag, @StringRes int message, @NonNull Throwable o) {               handleExc(SIMPLER,      tag,  message, o); }
    public static void exs(@StringRes int message, @NonNull Throwable o) {                                   handleExc(SIMPLER,      -1,   message, o); }
    public static void exm(@StringRes int tag, @StringRes int message, @NonNull Throwable o) {               handleExc(SIMPLEST,     tag,  message, o); }
    public static void exm(@StringRes int message, @NonNull Throwable o) {                                   handleExc(SIMPLEST,     -1,   message, o); }

}