package connectK.utils;

import org.slf4j.Logger;
import org.slf4j.MDC;
import org.slf4j.event.Level;

public final class LogUtils {
	private final static String key = "logFileName";
	public static void logAI(Logger logger, Level level, String aiName, String message, Throwable throwable){
		//Individual logs
		MDC.put(key, aiName);
		switch (level) {
			case DEBUG:
				logger.debug(message);
				break;
			case INFO:
				logger.debug(message);
				break;
			case TRACE:
				logger.debug(message);
				break;
			case WARN:
				logger.debug(message);
				break;
			case ERROR:
				logger.debug(message, throwable);
			default:
				break;
		}
		MDC.remove(key);
		
		//Cumulative logs
		switch (level) {
		case DEBUG:
			logger.debug(message);
			break;
		case INFO:
			logger.debug(message);
			break;
		case TRACE:
			logger.debug(message);
			break;
		case WARN:
			logger.debug(message);
			break;
		case ERROR:
			logger.debug(message, throwable);
		default:
			break;
	}
	}
	
	public static void logAIs(Logger logger, Level level, String message, Throwable throwable, String... aiNames){
		//Individual logs
		for (String ainame : aiNames)
			if (ainame != null)
				logAI(logger, level, ainame, message, throwable);
	}
}
