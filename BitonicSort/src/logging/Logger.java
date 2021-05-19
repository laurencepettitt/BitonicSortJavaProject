package logging;

/**
 * Logger handles common logging at various levels.
 */
public class Logger implements ILogger {

    /**
     * Log level
     */
    public enum Level {
        ALL(0), TRACE(1), DEBUG(2), INFO(3), WARN(4), ERROR(5), FATAL(6), OFF(7);
        private final Integer level;
        Level(int level) { this.level = level; }
        public boolean isWorseThan(Level other) { return this.level >= other.level;}
    }

    private static volatile Logger instance = null;
    private final Level level;

    /**
     * Construct logger (which will not output logs below level level)
     * @param level
     */
    public Logger(Level level) {
        this.level = level == null ? Level.ERROR : level;
    }

    /**
     * Returns (or constructs if first call) common Logger singleton for specified level
     * @param level log level
     * @return Logger singleton
     */
    public static Logger getSingletonInstance(Level level) {
        if (instance == null) {
            synchronized (Logger.class) {
                if (instance == null) {
                    instance = new Logger(level);
                }
            }
        }
        return instance;
    }

    /**
     * Logs message to level
     * Message will be output if level more severe than this.level
     * @param level     Severity of log
     * @param message   Message of log
     */
    public void log(Level level, String message) {
        if (level.isWorseThan(this.level)) {
            if (System.err != null && level.isWorseThan(Level.ERROR))
                System.err.println(message);
            else
                System.out.println(message);
        }
    }
}
