import org.freeplane.plugin.script.proxy.Convertible
import groovy.json.JsonBuilder
import groovy.json.JsonSlurper

storageKey = "BookmarksKeys"
anonymousIcon = "bookmarks/Bookmark 1"
namedIcon = "bookmarks/Bookmark 2"

def Map loadNamedBookmarks()
{
    // Create a HashMap where :
    // - the keys are the keyboard keys assigned to a bookmarked node;
    // - the values are the id of the corresponding bookmarked node.
    // This HashMap is read from the freeplane map storage area.

    // Read the datas from the map storage
    def marksString = new Convertible( '{}' )
    def stored = node.map.storage.getAt( storageKey )
    if( stored ) marksString = stored;

    // Convert these datas to an HashMap
    def namedBookmarks = new JsonSlurper().parseText( marksString.getText() )

    return namedBookmarks as Map
}

def saveNamedBookmarks( namedBookmarks )
{
    // Store the HashMap namedBookmarks in the freeplane map storage area.

    def builder = new JsonBuilder()
    builder( namedBookmarks )
    node.map.storage.putAt( storageKey, builder.toString() )
}

def createNamedBookmark( node, keyCharCode, namedBookmarks )
{
    // Create a named bookmark for this node. The name of the
    // bookmark is a single keyboard key.

    String s = String.valueOf( keyCharCode )

    // If another node has this named bookmark, remove it
    if( namedBookmarks.containsKey( s ) )
    {
        def n = map.node( namedBookmarks[ s ] )
        if( n && n != node ) deleteNamedBookmark( n, namedBookmarks )
    }
    
    // Delete the existing named bookmarks assigned to the current node
    namedBookmarks.removeAll{ k, v -> v == node.id }

    // Assign the key pressed to the current node
    namedBookmarks[ s ] = node.id

    // Save the new namedBookmarks map
    saveNamedBookmarks( namedBookmarks )

    // Add the icon
    node.icons.add( namedIcon )
}

def deleteNamedBookmark( node, namedBookmarks )
{
    // Delete an existing named bookmark

    if( namedBookmarks.containsValue( node.id ) )
        {
        // Clear the map
        namedBookmarks.removeAll{ key, value -> value == node.id }

        // Save the new namedBookmarks map
        saveNamedBookmarks( namedBookmarks )
    }

    // Remove the icon
    node.icons.remove( namedIcon )
}

def createAnonymousBookmark( node )
{
    // Create an anonymous bookmark for this node.
    node.icons.add( anonymousIcon )
}

def deleteAnonymousBookmark( node )
{
    node.icons.remove( anonymousIcon )
}


namedBookmarks = loadNamedBookmarks()

// Make sure namedBookmarks is up-to-date
def change = false
namedBookmarks.removeAll
{
    // Remove from the map all the node that don't exist
    // and add icon for each bookmarked node
    key, id ->
    def n = map.node( id )
    def missing = n == null
    if( ! missing )
	{
	if( ! n.icons.contains( namedIcon ) ) n.icons.add( namedIcon )
    } else {
        change = true
    }
    missing
}
if( change ) saveNamedBookmarks( namedBookmarks )

isAnonymousBookmark = node.icons.contains( anonymousIcon )
isNamedBookmark = namedBookmarks.containsValue( node.id )

if( isNamedBookmark )
{
    // Remove the named bookmark and put a regular bookmark instead
    deleteNamedBookmark( node, namedBookmarks )
    createAnonymousBookmark( node )
    c.setStatusInfo( 'standard', 'This node now has a regular bookmark', 'button_ok' )
}
else if( isAnonymousBookmark )
    {
    // Remove the named bookmark
    deleteAnonymousBookmark( node )
    c.setStatusInfo( 'standard', 'This node has no bookmark anymore', 'button_cancel' )
}
else
    {
    // Create a regular bookmark
    createAnonymousBookmark( node )
    c.setStatusInfo( 'standard', 'This node now has a regular bookmark', 'button_ok' )
}

