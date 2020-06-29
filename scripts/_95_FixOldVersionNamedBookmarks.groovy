// Update the named bookmarks defined by a previous version of the addon (v0.5.0)

// These old bookmarks
// - have an icon named "bookmark-named"
// - are stored within the local map storage at key "MarksKeys"

// If an bookmark with the same name as an actual named bookmark is found in
// the old bookmarks, the actual one is converted to standard bookmark

import org.freeplane.plugin.script.proxy.Convertible
import groovy.json.JsonBuilder
import groovy.json.JsonSlurper
import javax.swing.JOptionPane
import lilive.bookmarks.Bookmarks as BM

oldStorageKey = "MarksKeys"
oldNamedIcon = "bookmark-named"

def numFixedBookmarks = 0
def numAnonymizedBookmarks = 0

winTitle = BM.gtt( 'T_convert_v050_NMBs_win_title' )

def messageBox( message, icon )
{
    ui.informationMessage( ui.frame, message, winTitle, icon )
}

def Map loadOldNamedBookmarks()
{
    // Load the named bookmarks stored in the "MarksKeys" entry in map local storage
    def marksString = new Convertible( '{}' )
    def stored = node.map.storage.getAt( oldStorageKey )
    if( stored ) marksString = stored;
    def namedBookmarks = new JsonSlurper().parseText( marksString.getText() ) as Map

    // Discard nodes that do not exist anymore, and nodes that haven't got the
    // old bookmark icon
    namedBookmarks.removeAll
    {
        key, id ->
        def n = map.node( id )
        return ( n == null || ! n.icons.contains( oldNamedIcon ) ) 
    }

    namedBookmarks = namedBookmarks.sort()
    
    return namedBookmarks
}

def deleteOldNamedBookmarksDatas()
{
    // Delete the bookmarks stored in the "MarksKeys" entry in map local storage
    node.map.storage.putAt( oldStorageKey, null )
}

def mergeNamedBookmarks( oldBookmarks, uptodateBookmarks )
{
    // Merge the old bookmarks in the already up to date bookmarks

    conflicts = [:]
    oldBookmarks.each
    {
        key, id ->
        if( uptodateBookmarks.containsKey( key ) && uptodateBookmarks[ key ] != id ) conflicts[ key ] = uptodateBookmarks[ key ]
        uptodateBookmarks[ key ] = id;
    }
    uptodateBookmarks = uptodateBookmarks.sort()
    return [ uptodateBookmarks, conflicts ]
}

def cancel = ui.showConfirmDialog(
    node.delegate,
    BM.gtt( 'T_convert_v050_NBMs_warning' ),
    winTitle, JOptionPane.WARNING_MESSAGE
)
if( cancel != 0 )
{
    messageBox( BM.gtt( "T_op_canceled" ), JOptionPane.WARNING_MESSAGE )
    return
}

// Suspend the monitoring to be able to manipulate bookmarks
BM.pauseMonitor( node.map )

// Get the actual version bookmarks
def namedBookmarks = BM.loadNamedBookmarks( node.map )

// Look for named bookmarks in the local map storage, to find them as defined
// by previous version of the addon
def oldNamedBookmarks = loadOldNamedBookmarks()
numFixedBookmarks = oldNamedBookmarks.size()

// Move old bookmarks to new bookmarks
if( numFixedBookmarks )
{
    // Delete them from the local storage
    deleteOldNamedBookmarksDatas()

    // Merge the two
    ( namedBookmarks, conflicts ) = mergeNamedBookmarks( oldNamedBookmarks, namedBookmarks )

    // Save them with the new settings
    BM.saveNamedBookmarks( namedBookmarks, node.map )
    
    // If an actual bookmark as the same key than an old one,
    // convert this bookmark to anonymous bookmark
    conflicts.each
    {
        key, id ->
        def n = map.node( id )
        if( n )
        {
            n.icons.remove( BM.namedIcon )
            BM.createAnonymousBookmark( n )
            numAnonymizedBookmarks++;
        }
    }
}

// Now replace the icons
c.findAll().each
{
    n ->
    def update = false
    while( n.icons.remove( oldNamedIcon ) ) update = true
    if( update )
    {
        if( BM.hasBookmarkName( n, namedBookmarks ) )
        {
            n.icons.add( BM.namedIcon )
            numFixedBookmarks++;
        }
    }
}

BM.resumeMonitor( node.map )

// display result
def message = ''
def icon = JOptionPane.INFORMATION_MESSAGE
if( numFixedBookmarks )
{
    message = "${numFixedBookmarks} ${BM.gtt( 'T_v050_NBMs_converted')}"
    if( numAnonymizedBookmarks ) message += "\n${BM.gtt( 'T_and' )} ${numAnonymizedBookmarks} ${BM.gtt( 'T_NBMs_anonymized')}."
    else message += "."
}
else
{
    message = "${BM.gtt( 'T_no_v050_NBMs_converted' ) } !"
    icon = JOptionPane.ERROR_MESSAGE
}

messageBox( message, icon )


