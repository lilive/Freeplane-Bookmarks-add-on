// Open the help file in the user browser

import java.awt.Desktop;
import javax.swing.JOptionPane
import lilive.bookmarks.Bookmarks as BM

winTitle = BM.gtt( 'T_help_win_title' )

def messageBox( message, icon )
{
    ui.informationMessage( ui.frame, message, winTitle, icon )
}

// Check if desktop is supported
if( ! Desktop.isDesktopSupported() )
{
    messageBox( BM.gtt( "T_help_desktop_error" ), JOptionPane.ERROR_MESSAGE )
    return
}

// create desktop instance
 Desktop desktop = Desktop.getDesktop();

// ensure that desktop is supported
if( !desktop.isSupported( java.awt.Desktop.Action.BROWSE ) )
{
    messageBox( BM.gtt( "T_help_desktop_error" ), JOptionPane.ERROR_MESSAGE )
    return
}

// now, show page in external browser
def path = c.getUserDirectory().toPath()
path = path.resolve( "doc" ).resolve( "bookmarks" ).resolve( "help_en.html" )
try
{
    desktop.browse( path.toUri() );
}
catch ( Exception e )
{
    messageBox( "${BM.gtt( 'T_help_browse_error' )} '${path.toString()}': ${e.message}.", JOptionPane.ERROR_MESSAGE )
}

