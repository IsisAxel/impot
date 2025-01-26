package mg.trano.util;

import java.sql.Timestamp;
import java.util.Calendar;

public class Util 
{
    public static Timestamp generateTimestamp(int annee, int mois , int jour,int heure) 
    {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, annee);
        calendar.set(Calendar.MONTH, mois);
        calendar.set(Calendar.DAY_OF_MONTH, jour);
        calendar.set(Calendar.HOUR_OF_DAY, heure);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return new Timestamp(calendar.getTimeInMillis());
    }    
}
