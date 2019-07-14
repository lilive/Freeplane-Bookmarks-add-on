// Convert all builtin freeplane icon "Excellent" to standard bookmarks

import javax.swing.JOptionPane
import bookmarks.Bookmarks as BM

winTitle = BM.gtt( 'T_FP_to_BM_win_title' )

def messageBox( message, icon )
{
    ui.informationMessage( ui.frame, message, winTitle, icon )
}

// Ask for confirmation
def cancel = ui.showConfirmDialog(
    node.delegate,
    BM.gtt( 'T_convert_FP_BM_warning' ),
    winTitle, JOptionPane.WARNING_MESSAGE
)
if( cancel != 0 )
{
    messageBox( BM.gtt( "T_op_canceled" ), JOptionPane.WARNING_MESSAGE )
    return
}

// Convert the icons
def numBookmarksCreated = 0
c.findAll().each
{
    n ->
    if( n.icons.contains( "bookmark" ) )
    {
        numBookmarksCreated ++;
        while( n.icons.remove( "bookmark" ) ){}
        BM.createAnonymousBookmark( n )
    }
}

// Display result
if( numBookmarksCreated > 0 ) messageBox( "${numBookmarksCreated} ${BM.gtt( 'T_BMs_created' )} !", JOptionPane.INFORMATION_MESSAGE )
else messageBox( "${BM.gtt( 'T_no_BMs_created' )} !", JOptionPane.ERROR_MESSAGE )


