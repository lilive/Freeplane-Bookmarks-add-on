// This script allow to display all the bookmarked nodes, and only them.
// After that the user can select a bookmarked node, as usual.
// Then this script allow him to restore the view in the state before,
// but centered on the selected bookmarked node.

// The script use the map storage to remember the folding state of each
// node.

map = node.map
root = map.root
store = map.getStorage()
bookmarkIcon = "bookmark"

// Return is a node is bookmarked
def isBookmarked( node )
{
    return ( node != null && node.getIcons().contains( bookmarkIcon ) )
}

// Save the current folding state of this node and its children (recursively)
def storeFoldState( node )
{
    store.putAt( "BmkStt_" + node.id, node.isFolded() ? "f" : "c" )
    node.getChildren().each{ storeFoldState( it ) }
}

// Save the current folding state of this map
def storeFoldState()
{
    storeFoldState( root )
    store.putAt( "BmkStt", "saved" )
}

// Restore the fold state previously saved for this node its children (recursively)
def restoreFoldState( node )
{
    def stt = store.getAt( "BmkStt_" + node.id ).toString()
    if( stt ) node.setFolded( stt == "f" )
    store.putAt( "BmkStt_" + node.id, null )
    
    node.getChildren().each{ restoreFoldState( it ) }
}

// Restore the fold state previously saved for the map, and erase the saved state.
def restoreFoldState()
{
    restoreFoldState( root )
    store.putAt( "BmkStt", null )
}

// Delete all folding state saved data
def clearFoldState()
{
    def keys = store.keySet().collect()
    keys.each{
        if( it.startsWith( "BmkStt" ) ) store.putAt( it, null )
    }
}

// Return all displayed children (recursively)
def findDisplayed( node )
{
    if( node.isFolded() ) return []
    def displayed = []
    def children = node.getChildren()
    displayed.addAll( children.findAll{ it.isVisible() } )
    children.each{ displayed.addAll( findDisplayed( it ) ) }
    return displayed
}

// Check if we are in bookmarks view mode
// (only the bookmarked nodes are displayed)
def isBookmarksViewState()
{
    if( store.getAt( "BmkStt" ).toString() != "saved" ) return false
    
    def displayed = findDisplayed( root )
    def notBmk = displayed.findAll{ !it.getIcons().contains( bookmarkIcon ) }
    return !notBmk
}

// Show all bookmarked nodes, and only them
def enterBookmarksView()
{
    clearFoldState()
    storeFoldState()

    // Set filter to display only nodes with a bookmark icon.
    // Use filtering, not displaying ancestors nor descendants
    node.map.filter( false, false ){ isBookmarked( it ) }

    // Expand all the map to reveal all the bookmarks
    root.setFolded( false )
    root.findAll().each{ it.setFolded( false ) }

    // Now select a bookmark node
    if( isBookmarked( node ) )
    {
        // If the node selected when this script is invoked is a bookmark, select it again
        c.centerOnNode( node )
    }
    else
    {
        // Else try to select the first bookmark
        def nodeToSelect = root.findAll().find{ isBookmarked( it ) }
        if( nodeToSelect )
        {
            c.select( nodeToSelect )
            c.centerOnNode( nodeToSelect )
        }
    }
}

// Return to previous state
def exitBookmarksView()
{
    map.undoFilter()
    restoreFoldState()
    clearFoldState()

    // But unfold, if needed, all the ancestor of the node
    // selected by the user during the ViewOnlyBookmarks mode
    // This will display the selected choosen bookmarked node
    def n = node
    while( n != root )
    {
        n = n.getParent()
        n.setFolded( false )
    }

    // Recenter on selected node
    c.select( node )
    c.centerOnNode( node )
}

if( isBookmarksViewState() ) exitBookmarksView()
else enterBookmarksView()
