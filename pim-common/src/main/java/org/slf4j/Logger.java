/**
 * Created: 06.04.2017
 */

package org.slf4j;

import java.util.function.Supplier;

/**
 * Erweiterung des {@link org.slf4j.Logger} mit Default-Methoden für einen {@link Supplier}.
 *
 * @author Thomas Freese
 */
public interface Logger
{
    /**
     * Case insensitive String constant used to retrieve the name of the root logger.
     *
     * @since 1.3
     */
    public final String ROOT_LOGGER_NAME = "ROOT";

    /**
     * Log a message with the specific Marker at the DEBUG level.
     *
     * @param marker the marker data specific to this log statement
     * @param msg the message string to be logged
     */
    public void debug(Marker marker, String msg);

    /**
     * This method is similar to {@link #debug(String, Object)} method except that the marker data is also taken into consideration.
     *
     * @param marker the marker data specific to this log statement
     * @param format the format string
     * @param arg the argument
     */
    public void debug(Marker marker, String format, Object arg);

    /**
     * This method is similar to {@link #debug(String, Object...)} method except that the marker data is also taken into consideration.
     *
     * @param marker the marker data specific to this log statement
     * @param format the format string
     * @param arguments a list of 3 or more arguments
     */
    public void debug(Marker marker, String format, Object...arguments);

    /**
     * This method is similar to {@link #debug(String, Object, Object)} method except that the marker data is also taken into consideration.
     *
     * @param marker the marker data specific to this log statement
     * @param format the format string
     * @param arg1 the first argument
     * @param arg2 the second argument
     */
    public void debug(Marker marker, String format, Object arg1, Object arg2);

    /**
     * This method is similar to {@link #debug(String, Throwable)} method except that the marker data is also taken into consideration.
     *
     * @param marker the marker data specific to this log statement
     * @param msg the message accompanying the exception
     * @param t the exception (throwable) to log
     */
    public void debug(Marker marker, String msg, Throwable t);

    /**
     * Log a message at the DEBUG level.
     *
     * @param msg the message string to be logged
     */
    public void debug(String msg);

    /**
     * Log a message at the DEBUG level according to the specified format and argument.
     * <p/>
     * <p>
     * This form avoids superfluous object creation when the logger is disabled for the DEBUG level.
     * </p>
     *
     * @param format the format string
     * @param arg the argument
     */
    public void debug(String format, Object arg);

    /**
     * Log a message at the DEBUG level according to the specified format and arguments.
     * <p/>
     * <p>
     * This form avoids superfluous string concatenation when the logger is disabled for the DEBUG level. However, this variant incurs the hidden (and
     * relatively small) cost of creating an <code>Object[]</code> before invoking the method, even if this logger is disabled for DEBUG. The variants taking
     * {@link #debug(String, Object) one} and {@link #debug(String, Object, Object) two} arguments exist solely in order to avoid this hidden cost.
     * </p>
     *
     * @param format the format string
     * @param arguments a list of 3 or more arguments
     */
    public void debug(String format, Object...arguments);

    /**
     * Log a message at the DEBUG level according to the specified format and arguments.
     * <p/>
     * <p>
     * This form avoids superfluous object creation when the logger is disabled for the DEBUG level.
     * </p>
     *
     * @param format the format string
     * @param arg1 the first argument
     * @param arg2 the second argument
     */
    public void debug(String format, Object arg1, Object arg2);

    /**
     * Log an exception (throwable) at the DEBUG level with an accompanying message.
     *
     * @param msg the message accompanying the exception
     * @param t the exception (throwable) to log
     */
    public void debug(String msg, Throwable t);

    /**
     * Log a message at the DEBUG level, if DEBUG is enabled.
     *
     * @param msg {@link Supplier}
     */
    public default void debug(final Supplier<String> msg)
    {
        if (isDebugEnabled())
        {
            debug(Utils.getNullSafe(msg));
        }
    }

    /**
     * Log a message with the specific Marker at the ERROR level.
     *
     * @param marker The marker specific to this log statement
     * @param msg the message string to be logged
     */
    public void error(Marker marker, String msg);

    /**
     * This method is similar to {@link #error(String, Object)} method except that the marker data is also taken into consideration.
     *
     * @param marker the marker data specific to this log statement
     * @param format the format string
     * @param arg the argument
     */
    public void error(Marker marker, String format, Object arg);

    /**
     * This method is similar to {@link #error(String, Object...)} method except that the marker data is also taken into consideration.
     *
     * @param marker the marker data specific to this log statement
     * @param format the format string
     * @param arguments a list of 3 or more arguments
     */
    public void error(Marker marker, String format, Object...arguments);

    /**
     * This method is similar to {@link #error(String, Object, Object)} method except that the marker data is also taken into consideration.
     *
     * @param marker the marker data specific to this log statement
     * @param format the format string
     * @param arg1 the first argument
     * @param arg2 the second argument
     */
    public void error(Marker marker, String format, Object arg1, Object arg2);

    /**
     * This method is similar to {@link #error(String, Throwable)} method except that the marker data is also taken into consideration.
     *
     * @param marker the marker data specific to this log statement
     * @param msg the message accompanying the exception
     * @param t the exception (throwable) to log
     */
    public void error(Marker marker, String msg, Throwable t);

    /**
     * Log a message at the ERROR level.
     *
     * @param msg the message string to be logged
     */
    public void error(String msg);

    /**
     * Log a message at the ERROR level according to the specified format and argument.
     * <p/>
     * <p>
     * This form avoids superfluous object creation when the logger is disabled for the ERROR level.
     * </p>
     *
     * @param format the format string
     * @param arg the argument
     */
    public void error(String format, Object arg);

    /**
     * Log a message at the ERROR level according to the specified format and arguments.
     * <p/>
     * <p>
     * This form avoids superfluous string concatenation when the logger is disabled for the ERROR level. However, this variant incurs the hidden (and
     * relatively small) cost of creating an <code>Object[]</code> before invoking the method, even if this logger is disabled for ERROR. The variants taking
     * {@link #error(String, Object) one} and {@link #error(String, Object, Object) two} arguments exist solely in order to avoid this hidden cost.
     * </p>
     *
     * @param format the format string
     * @param arguments a list of 3 or more arguments
     */
    public void error(String format, Object...arguments);

    /**
     * Log a message at the ERROR level according to the specified format and arguments.
     * <p/>
     * <p>
     * This form avoids superfluous object creation when the logger is disabled for the ERROR level.
     * </p>
     *
     * @param format the format string
     * @param arg1 the first argument
     * @param arg2 the second argument
     */
    public void error(String format, Object arg1, Object arg2);

    /**
     * Log an exception (throwable) at the ERROR level with an accompanying message.
     *
     * @param msg the message accompanying the exception
     * @param t the exception (throwable) to log
     */
    public void error(String msg, Throwable t);

    /**
     * Log a message at the ERROR level, if ERROR is enabled.
     *
     * @param msg {@link Supplier}
     */
    public default void error(final Supplier<String> msg)
    {
        if (isErrorEnabled())
        {
            error(Utils.getNullSafe(msg));
        }
    }

    /**
     * Return the name of this <code>Logger</code> instance.
     *
     * @return name of this logger instance
     */
    public String getName();

    /**
     * Log a message with the specific Marker at the INFO level.
     *
     * @param marker The marker specific to this log statement
     * @param msg the message string to be logged
     */
    public void info(Marker marker, String msg);

    /**
     * This method is similar to {@link #info(String, Object)} method except that the marker data is also taken into consideration.
     *
     * @param marker the marker data specific to this log statement
     * @param format the format string
     * @param arg the argument
     */
    public void info(Marker marker, String format, Object arg);

    /**
     * This method is similar to {@link #info(String, Object...)} method except that the marker data is also taken into consideration.
     *
     * @param marker the marker data specific to this log statement
     * @param format the format string
     * @param arguments a list of 3 or more arguments
     */
    public void info(Marker marker, String format, Object...arguments);

    /**
     * This method is similar to {@link #info(String, Object, Object)} method except that the marker data is also taken into consideration.
     *
     * @param marker the marker data specific to this log statement
     * @param format the format string
     * @param arg1 the first argument
     * @param arg2 the second argument
     */
    public void info(Marker marker, String format, Object arg1, Object arg2);

    /**
     * This method is similar to {@link #info(String, Throwable)} method except that the marker data is also taken into consideration.
     *
     * @param marker the marker data for this log statement
     * @param msg the message accompanying the exception
     * @param t the exception (throwable) to log
     */
    public void info(Marker marker, String msg, Throwable t);

    /**
     * Log a message at the INFO level.
     *
     * @param msg the message string to be logged
     */
    public void info(String msg);

    /**
     * Log a message at the INFO level according to the specified format and argument.
     * <p/>
     * <p>
     * This form avoids superfluous object creation when the logger is disabled for the INFO level.
     * </p>
     *
     * @param format the format string
     * @param arg the argument
     */
    public void info(String format, Object arg);

    /**
     * Log a message at the INFO level according to the specified format and arguments.
     * <p/>
     * <p>
     * This form avoids superfluous string concatenation when the logger is disabled for the INFO level. However, this variant incurs the hidden (and relatively
     * small) cost of creating an <code>Object[]</code> before invoking the method, even if this logger is disabled for INFO. The variants taking
     * {@link #info(String, Object) one} and {@link #info(String, Object, Object) two} arguments exist solely in order to avoid this hidden cost.
     * </p>
     *
     * @param format the format string
     * @param arguments a list of 3 or more arguments
     */
    public void info(String format, Object...arguments);

    /**
     * Log a message at the INFO level according to the specified format and arguments.
     * <p/>
     * <p>
     * This form avoids superfluous object creation when the logger is disabled for the INFO level.
     * </p>
     *
     * @param format the format string
     * @param arg1 the first argument
     * @param arg2 the second argument
     */
    public void info(String format, Object arg1, Object arg2);

    /**
     * Log an exception (throwable) at the INFO level with an accompanying message.
     *
     * @param msg the message accompanying the exception
     * @param t the exception (throwable) to log
     */
    public void info(String msg, Throwable t);

    /**
     * Log a message at the INFO level, if INFO is enabled.
     *
     * @param msg {@link Supplier}
     */
    public default void info(final Supplier<String> msg)
    {
        if (isInfoEnabled())
        {
            info(Utils.getNullSafe(msg));
        }
    }

    /**
     * Is the logger instance enabled for the DEBUG level?
     *
     * @return True if this Logger is enabled for the DEBUG level, false otherwise.
     */
    public boolean isDebugEnabled();

    /**
     * Similar to {@link #isDebugEnabled()} method except that the marker data is also taken into account.
     *
     * @param marker The marker data to take into consideration
     * @return True if this Logger is enabled for the DEBUG level, false otherwise.
     */
    public boolean isDebugEnabled(Marker marker);

    /**
     * Is the logger instance enabled for the ERROR level?
     *
     * @return True if this Logger is enabled for the ERROR level, false otherwise.
     */
    public boolean isErrorEnabled();

    /**
     * Similar to {@link #isErrorEnabled()} method except that the marker data is also taken into consideration.
     *
     * @param marker The marker data to take into consideration
     * @return True if this Logger is enabled for the ERROR level, false otherwise.
     */
    public boolean isErrorEnabled(Marker marker);

    /**
     * Is the logger instance enabled for the INFO level?
     *
     * @return True if this Logger is enabled for the INFO level, false otherwise.
     */
    public boolean isInfoEnabled();

    /**
     * Similar to {@link #isInfoEnabled()} method except that the marker data is also taken into consideration.
     *
     * @param marker The marker data to take into consideration
     * @return true if this logger is warn enabled, false otherwise
     */
    public boolean isInfoEnabled(Marker marker);

    /**
     * Is the logger instance enabled for the TRACE level?
     *
     * @return True if this Logger is enabled for the TRACE level, false otherwise.
     * @since 1.4
     */
    public boolean isTraceEnabled();

    /**
     * Similar to {@link #isTraceEnabled()} method except that the marker data is also taken into account.
     *
     * @param marker The marker data to take into consideration
     * @return True if this Logger is enabled for the TRACE level, false otherwise.
     * @since 1.4
     */
    public boolean isTraceEnabled(Marker marker);

    /**
     * Is the logger instance enabled for the WARN level?
     *
     * @return True if this Logger is enabled for the WARN level, false otherwise.
     */
    public boolean isWarnEnabled();

    /**
     * Similar to {@link #isWarnEnabled()} method except that the marker data is also taken into consideration.
     *
     * @param marker The marker data to take into consideration
     * @return True if this Logger is enabled for the WARN level, false otherwise.
     */
    public boolean isWarnEnabled(Marker marker);

    /**
     * Log a message with the specific Marker at the TRACE level.
     *
     * @param marker the marker data specific to this log statement
     * @param msg the message string to be logged
     * @since 1.4
     */
    public void trace(Marker marker, String msg);

    /**
     * This method is similar to {@link #trace(String, Object)} method except that the marker data is also taken into consideration.
     *
     * @param marker the marker data specific to this log statement
     * @param format the format string
     * @param arg the argument
     * @since 1.4
     */
    public void trace(Marker marker, String format, Object arg);

    /**
     * This method is similar to {@link #trace(String, Object...)} method except that the marker data is also taken into consideration.
     *
     * @param marker the marker data specific to this log statement
     * @param format the format string
     * @param argArray an array of arguments
     * @since 1.4
     */
    public void trace(Marker marker, String format, Object...argArray);

    /**
     * This method is similar to {@link #trace(String, Object, Object)} method except that the marker data is also taken into consideration.
     *
     * @param marker the marker data specific to this log statement
     * @param format the format string
     * @param arg1 the first argument
     * @param arg2 the second argument
     * @since 1.4
     */
    public void trace(Marker marker, String format, Object arg1, Object arg2);

    /**
     * This method is similar to {@link #trace(String, Throwable)} method except that the marker data is also taken into consideration.
     *
     * @param marker the marker data specific to this log statement
     * @param msg the message accompanying the exception
     * @param t the exception (throwable) to log
     * @since 1.4
     */
    public void trace(Marker marker, String msg, Throwable t);

    /**
     * Log a message at the TRACE level.
     *
     * @param msg the message string to be logged
     * @since 1.4
     */
    public void trace(String msg);

    /**
     * Log a message at the TRACE level according to the specified format and argument.
     * <p/>
     * <p>
     * This form avoids superfluous object creation when the logger is disabled for the TRACE level.
     * </p>
     *
     * @param format the format string
     * @param arg the argument
     * @since 1.4
     */
    public void trace(String format, Object arg);

    /**
     * Log a message at the TRACE level according to the specified format and arguments.
     * <p/>
     * <p>
     * This form avoids superfluous string concatenation when the logger is disabled for the TRACE level. However, this variant incurs the hidden (and
     * relatively small) cost of creating an <code>Object[]</code> before invoking the method, even if this logger is disabled for TRACE. The variants taking
     * {@link #trace(String, Object) one} and {@link #trace(String, Object, Object) two} arguments exist solely in order to avoid this hidden cost.
     * </p>
     *
     * @param format the format string
     * @param arguments a list of 3 or more arguments
     * @since 1.4
     */
    public void trace(String format, Object...arguments);

    /**
     * Log a message at the TRACE level according to the specified format and arguments.
     * <p/>
     * <p>
     * This form avoids superfluous object creation when the logger is disabled for the TRACE level.
     * </p>
     *
     * @param format the format string
     * @param arg1 the first argument
     * @param arg2 the second argument
     * @since 1.4
     */
    public void trace(String format, Object arg1, Object arg2);

    /**
     * Log an exception (throwable) at the TRACE level with an accompanying message.
     *
     * @param msg the message accompanying the exception
     * @param t the exception (throwable) to log
     * @since 1.4
     */
    public void trace(String msg, Throwable t);

    /**
     * Log a message at the TRACE level, if TRACE is enabled.
     *
     * @param msg {@link Supplier}
     */
    public default void trace(final Supplier<String> msg)
    {
        if (isTraceEnabled())
        {
            trace(Utils.getNullSafe(msg));
        }
    }

    /**
     * Log a message with the specific Marker at the WARN level.
     *
     * @param marker The marker specific to this log statement
     * @param msg the message string to be logged
     */
    public void warn(Marker marker, String msg);

    /**
     * This method is similar to {@link #warn(String, Object)} method except that the marker data is also taken into consideration.
     *
     * @param marker the marker data specific to this log statement
     * @param format the format string
     * @param arg the argument
     */
    public void warn(Marker marker, String format, Object arg);

    /**
     * This method is similar to {@link #warn(String, Object...)} method except that the marker data is also taken into consideration.
     *
     * @param marker the marker data specific to this log statement
     * @param format the format string
     * @param arguments a list of 3 or more arguments
     */
    public void warn(Marker marker, String format, Object...arguments);

    /**
     * This method is similar to {@link #warn(String, Object, Object)} method except that the marker data is also taken into consideration.
     *
     * @param marker the marker data specific to this log statement
     * @param format the format string
     * @param arg1 the first argument
     * @param arg2 the second argument
     */
    public void warn(Marker marker, String format, Object arg1, Object arg2);

    /**
     * This method is similar to {@link #warn(String, Throwable)} method except that the marker data is also taken into consideration.
     *
     * @param marker the marker data for this log statement
     * @param msg the message accompanying the exception
     * @param t the exception (throwable) to log
     */
    public void warn(Marker marker, String msg, Throwable t);

    /**
     * Log a message at the WARN level.
     *
     * @param msg the message string to be logged
     */
    public void warn(String msg);

    /**
     * Log a message at the WARN level according to the specified format and argument.
     * <p/>
     * <p>
     * This form avoids superfluous object creation when the logger is disabled for the WARN level.
     * </p>
     *
     * @param format the format string
     * @param arg the argument
     */
    public void warn(String format, Object arg);

    /**
     * Log a message at the WARN level according to the specified format and arguments.
     * <p/>
     * <p>
     * This form avoids superfluous string concatenation when the logger is disabled for the WARN level. However, this variant incurs the hidden (and relatively
     * small) cost of creating an <code>Object[]</code> before invoking the method, even if this logger is disabled for WARN. The variants taking
     * {@link #warn(String, Object) one} and {@link #warn(String, Object, Object) two} arguments exist solely in order to avoid this hidden cost.
     * </p>
     *
     * @param format the format string
     * @param arguments a list of 3 or more arguments
     */
    public void warn(String format, Object...arguments);

    /**
     * Log a message at the WARN level according to the specified format and arguments.
     * <p/>
     * <p>
     * This form avoids superfluous object creation when the logger is disabled for the WARN level.
     * </p>
     *
     * @param format the format string
     * @param arg1 the first argument
     * @param arg2 the second argument
     */
    public void warn(String format, Object arg1, Object arg2);

    /**
     * Log an exception (throwable) at the WARN level with an accompanying message.
     *
     * @param msg the message accompanying the exception
     * @param t the exception (throwable) to log
     */
    public void warn(String msg, Throwable t);

    /**
     * Log a message at the WARN level, if WARN is enabled.
     *
     * @param msg {@link Supplier}
     */
    public default void warn(final Supplier<String> msg)
    {
        if (isWarnEnabled())
        {
            warn(Utils.getNullSafe(msg));
        }
    }
}
