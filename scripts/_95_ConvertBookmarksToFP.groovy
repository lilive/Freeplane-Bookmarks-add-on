// Convert all bookmarks to buitin freeplane icon "Excellent"

import javax.swing.JOptionPane

storageKey = "BookmarksKeys"
anonymousIcon = "bookmarks/Bookmark 1"
namedIcon = "bookmarks/Bookmark 2"

winTitle = gtt( 'T_BMs_to_FP_win_title' )

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
    gtt( 'T_convert_BM_FP_warning' ),
    winTitle, JOptionPane.WARNING_MESSAGE
)
if( cancel != 0 )
{
    messageBox( gtt( "T_op_canceled" ), JOptionPane.WARNING_MESSAGE )
    return
}

def storageErased = false
def storage = node.map.storage.getAt( storageKey )
if( storage != null )
{ 
    node.map.storage.putAt( storageKey, null )
    storageErased = true;
}

def numIconsConverted = 0
c.findAll().each
{
    if( it.icons.contains( anonymousIcon ) || it.icons.contains( namedIcon ) )
    {
        def iconsNames = it.icons.icons
        it.icons.clear()
        iconsNames.each
        {
            it2 ->
            if( it2 == anonymousIcon || it2 == namedIcon )
            {
                it.icons.add( "bookmark" )
                numIconsConverted ++;
            }
            else
            {
                it.icons.add( it2 )
            }
        }
    }
}

if( numIconsConverted > 0 ) messageBox( "${numIconsConverted} ${gtt( 'T_icons_converted' )} !", JOptionPane.INFORMATION_MESSAGE )
else if( storageErased ) messageBox( "${gtt( 'T_storage_erased' )} !", JOptionPane.INFORMATION_MESSAGE )
else messageBox( "${gtt( 'T_no_conversion_required' )} !", JOptionPane.ERROR_MESSAGE )


