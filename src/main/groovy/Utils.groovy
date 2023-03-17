package lilive.bookmarks

import org.freeplane.plugin.script.proxy.ScriptUtils
import java.util.TimerTask

public class Utils
{
    private static Timer timer = new Timer()
    private static TimerTask timerTask = null
    
    public static void setStatusInfo( String message, String icon = '' )
    {
        if( timerTask ) timerTask.cancel()

        if( icon ){
            ScriptUtils.c().setStatusInfo( 'lilive bookmarks',  message , icon )
        } else {
            ScriptUtils.c().setStatusInfo( 'lilive bookmarks',  message )
        }
        timerTask = timer.runAfter(
            5000,
            { ScriptUtils.c().setStatusInfo('lilive bookmarks', '' ) }
        )
    }
}
