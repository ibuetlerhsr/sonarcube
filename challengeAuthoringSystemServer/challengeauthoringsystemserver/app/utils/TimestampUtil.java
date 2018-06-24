package utils;

import play.Logger;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class TimestampUtil {
    public static Timestamp getTimestamp(String input) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd. MMMM yyyy", Locale.US);
        Date parsedDate = null;
        try {
            parsedDate = dateFormat.parse(input);
        } catch (ParseException e) {
            Logger.info(e.getMessage());
        }
        if(parsedDate == null) {
            return new Timestamp(new Date().getTime());
        }
        return new Timestamp(parsedDate.getTime());
    }

    public static Timestamp getTimestampForFilter(String input) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy", Locale.US);
        Date parsedDate = null;
        if(input == null) {
            return new Timestamp(new Date().getTime());
        }
        try {
            parsedDate = dateFormat.parse(input);
        } catch (ParseException e) {
            Logger.info(e.getMessage());
        }
        if(parsedDate == null) {
            return new Timestamp(new Date().getTime());
        }
        return new Timestamp(parsedDate.getTime());
    }

    public static String getString(Timestamp timestamp) {
        if(timestamp == null)
            return null;
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd. MMMM yyyy", Locale.US);
        String timestampString = null;
        return dateFormat.format(new Date(timestamp.getTime()));
    }
}
