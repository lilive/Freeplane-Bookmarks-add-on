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

oldStorageKey = "MarksKeys"
oldNamedIcon = "bookmark-named"

storageKey = "BookmarksKeys"
namedIcon = "bookmarks/Bookmark 2"
anonymousIcon = "bookmarks/Bookmark 1"

globalKey = "Bookmarks"
monitorKey = "monitorMap"

numFixedBookmarks = 0
numAnonymizedBookmarks = 0

winTitle = gtt( 'T_convert_v050_NMBs_win_title' )

def gtt( key )
{
    // gt = Get Translated Text
    return textUtils.getText( 'addons.bookmarks.' + key )
}

def messageBox( message, icon )
{
    ui.informationMessage( ui.frame, message, winTitle, icon )
}

def pauseMonitor()
{
    // Pause the add-on node changes monitoring feature
    
    // Read the datas from the map storage
    def varsString = new Convertible( '{}' )
    def stored = node.map.storage.getAt( globalKey )
    if( stored ) varsString = stored;

    // Convert these datas to an HashMap
    def vars = new JsonSlurper().parseText( varsString.getText() ) as Map

    // Set monitoring var to disable
    vars[ monitorKey ] = false
    
    // Save the vars in map local storage
    def builder = new JsonBuilder()
    builder( vars )
    node.map.storage.putAt( globalKey, builder.toString() )
}

def resumeMonitor()
{
    // Resume the add-on node changes monitoring feature
    
    // Read the datas from the map storage
    def varsString = new Convertible( '{}' )
    def stored = node.map.storage.getAt( globalKey )
    if( stored ) varsString = stored;

    // Convert these datas to an HashMap
    def vars = new JsonSlurper().parseText( varsString.getText() ) as Map

    // Set monitoring var to enabled
    vars[ monitorKey ] = true
    
    // Save the vars in map local storage
    def builder = new JsonBuilder()
    builder( vars )
    node.map.storage.putAt( globalKey, builder.toString() )
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
    
    return namedBookmarks
}

def Map loadNamedBookmarks()
{
    // Load the named bookmarks stored in the storageKey entry in map local storage
    def marksString = new Convertible( '{}' )
    def stored = node.map.storage.getAt( storageKey )
    if( stored ) marksString = stored;
    def namedBookmarks = new JsonSlurper().parseText( marksString.getText() )
    return namedBookmarks as Map
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
    return [ uptodateBookmarks, conflicts ]
}

def saveNamedBookmarks( namedBookmarks )
{
    // Save the named bookmarks to the storageKey entry in map local storage
    def builder = new JsonBuilder()
    builder( namedBookmarks )
    node.map.storage.putAt( storageKey, builder.toString() )
}

def cancel = ui.showConfirmDialog(
    node.delegate,
    gtt( 'T_convert_v050_NBMs_warning' ),
    winTitle, JOptionPane.WARNING_MESSAGE
)
if( cancel != 0 )
{
    messageBox( gtt( "T_op_canceled" ), JOptionPane.WARNING_MESSAGE )
    return
}

pauseMonitor()

// Get the actual version bookmarks
namedBookmarks = loadNamedBookmarks()

// Look for named bookmarks in the local map storage, to find them as defined
// by previous version of the addon
oldNamedBookmarks = loadOldNamedBookmarks()
numFixedBookmarks = oldNamedBookmarks.size()

// Move old bookmarks to new bookmarks
if( numFixedBookmarks )
{
    // Delete them from the local storage
    deleteOldNamedBookmarksDatas()

    // Merge the two
    ( namedBookmarks, conflicts ) = mergeNamedBookmarks( oldNamedBookmarks, namedBookmarks )

    // Save them with the new settings
    saveNamedBookmarks( namedBookmarks )
    
    // If an actual bookmark as the same key than an old one,
    // convert this bookmark to anonymous bookmark
    conflicts.each
    {
        key, id ->
        def n = map.node( id )
        if( n )
        {
            n.icons.remove( namedIcon )
            n.icons.add( anonymousIcon )
            numAnonymizedBookmarks++;
        }
    }
}

// Now replace the icons
c.findAll().each
{
    def update = false
    while( it.icons.remove( oldNamedIcon ) ) update = true
    if( update )
    {
        if( namedBookmarks.containsValue( it.id ) )
        {
            it.icons.add( namedIcon )
            numFixedBookmarks++;
        }
    }
}

resumeMonitor()

def message = ''
def icon = JOptionPane.INFORMATION_MESSAGE
if( numFixedBookmarks )
{
    message = "${numFixedBookmarks} ${gtt( 'T_v050_NBMs_converted')}"
    if( numAnonymizedBookmarks ) message += "\n${gtt( 'T_and' )} ${numAnonymizedBookmarks} ${gtt( 'T_NBMs_anonymized')}."
    else message += "."
}
else
{
    message = "${gtt( 'T_no_v050_NBMs_converted' ) } !"
    icon = JOptionPane.ERROR_MESSAGE
}

messageBox( message, icon )


