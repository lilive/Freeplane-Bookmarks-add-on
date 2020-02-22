// Convert all bookmarks to buitin freeplane icon "Excellent"

import javax.swing.JOptionPane
import bookmarks.Bookmarks as BM

winTitle = BM.gtt( 'T_BMs_to_FP_win_title' )

def messageBox( message, icon )
{
    ui.informationMessage( ui.frame, message, winTitle, icon )
}

// Ask for confirmation
def cancel = ui.showConfirmDialog(
    node.delegate,
    BM.gtt( 'T_convert_BM_FP_warning' ),
    winTitle, JOptionPane.WARNING_MESSAGE
)
if( cancel != 0 )
{
    messageBox( BM.gtt( "T_op_canceled" ), JOptionPane.WARNING_MESSAGE )
    return
}

// Clear the records for the named bookmarks
def storageErased = BM.eraseNamedBookmarksStorage( node.map )

// Convert thie icons
def numIconsConverted = 0
c.findAll().each
{
    n ->
    if( BM.hasAnonymousBookmarkIcon( n ) || BM.hasNamedBookmarkIcon( n ) )
    {
        def iconsNames = n.icons.icons
        n.icons.clear()
        iconsNames.each
        {
            icon ->
            if( icon == BM.anonymousIcon || icon == BM.namedIcon )
            {
                n.icons.add( "bookmark" )
                numIconsConverted ++;
            }
            else
            {
                n.icons.add( icon )
            }
        }
    }
}

// Display result
if( numIconsConverted > 0 ) messageBox( "${numIconsConverted} ${BM.gtt( 'T_icons_converted' )} !", JOptionPane.INFORMATION_MESSAGE )
else if( storageErased ) messageBox( "${BM.gtt( 'T_storage_erased' )} !", JOptionPane.INFORMATION_MESSAGE )
else messageBox( "${BM.gtt( 'T_no_conversion_required' )} !", JOptionPane.ERROR_MESSAGE )


