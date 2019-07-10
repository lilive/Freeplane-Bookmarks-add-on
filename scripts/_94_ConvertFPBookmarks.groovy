// Convert all builtin freeplane icon "Excellent" to standard bookmarks

import javax.swing.JOptionPane

anonymousIcon = "bookmarks/Bookmark 1"

winTitle = gtt( 'T_FP_to_BM_win_title' )

def gtt( key )
{
    // gt = Get Translated Text
    return textUtils.getText( 'addons.bookmarks.' + key )
}

def messageBox( message, icon )
{
    ui.informationMessage( ui.frame, message, winTitle, icon )
}

def cancel = ui.showConfirmDialog(
    node.delegate,
    gtt( 'T_convert_FP_BM_warning' ),
    winTitle, JOptionPane.WARNING_MESSAGE
)
if( cancel != 0 )
{
    messageBox( gtt( "T_op_canceled" ), JOptionPane.WARNING_MESSAGE )
    return
}

def numBookmarksCreated = 0
c.findAll().each
{
    if( it.icons.contains( "bookmark" ) )
    {
        numBookmarksCreated ++;
        while( it.icons.remove( "bookmark" ) ){}
        it.icons.add( anonymousIcon )
    }
}

if( numBookmarksCreated > 0 ) messageBox( "${numBookmarksCreated} ${gtt( 'T_BMs_created' )} !", JOptionPane.INFORMATION_MESSAGE )
else messageBox( "${gtt( 'T_no_BMs_created' )} !", JOptionPane.ERROR_MESSAGE )


