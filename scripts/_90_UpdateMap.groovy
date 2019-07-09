// Update the bookmarks if they where defined by a previous version of the addon

import org.freeplane.plugin.script.proxy.Convertible
import groovy.json.JsonBuilder
import groovy.json.JsonSlurper

storageKey = "BookmarksKeys"
anonymousIcon = "bookmarks/Bookmark 1"
namedIcon = "bookmarks/Bookmark 2"
updated = false

def Map loadOldNamedBookmarks()
{
    // Load the named bookmarks stored in the "MarksKeys" entry in map local storage
    def marksString = new Convertible( '{}' )
    def stored = node.map.storage.getAt( "MarksKeys" )
    if( stored ) marksString = stored;
    def namedBookmarks = new JsonSlurper().parseText( marksString.getText() )
    return namedBookmarks as Map
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
    node.map.storage.putAt( "MarksKeys", null )
}

def mergeNamedBookmarks( oldBookmarks, uptodateBookmarks )
{
    // Merge the old bookmarks in the already up to date bookmarks

    conflicts = [:]
    oldBookmarks.each
    {
        key, id ->
        if( uptodateBookmarks.containsKey( key ) ) conflicts[ key ] = uptodateBookmarks[ key ]
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

// Look for named bookmarks in the local map storage, to find them as defined
// by previous version of the addon
oldNamedBookmarks = loadOldNamedBookmarks()
if( oldNamedBookmarks )
{
    // Delete them from the local storage
    deleteOldNamedBookmarksDatas()
    
    // Get already updated bookmarks
    namedBookmarks = loadNamedBookmarks()
    
    // Merge the two
    ( namedBookmarks, conflicts ) = mergeNamedBookmarks( oldNamedBookmarks, namedBookmarks )
    
    // Save them with the new settings
    saveNamedBookmarks( namedBookmarks )
    
    // If an already uptodate bookmark as the same key than an old one,
    // convert this bookmark to anonymous bookmark
    conflicts.each
    {
        key, id ->
        def n = map.node( id )
        if( n )
        {
            n.icons.remove( namedIcon )
            n.icons.add( anonymousIcon )
        }
    }

    updated = true
}

def gtt( key )
{
    // gt = Get Translated Text
    return textUtils.getText( 'addons.bookmarks.' + key )
}

// Now replace the old bookmarks icons names
c.findAll().each
{
    def update = false
    while( it.icons.remove( "bookmark-named" ) )
    {
        update = true
        updated = true
    }
    if( update )
    {
        it.icons.add( namedIcon )
        return
    }
    update = false
    while( it.icons.remove( "bookmark" ) )
    {
        update = true
        updated = true
    }
    if( update ) it.icons.add( anonymousIcon )
}

if( updated ) c.setStatusInfo( 'standard', "${gtt( 'T_BMs_updated' )} !", 'button_ok' )
else c.setStatusInfo( 'standard', "${gtt( 'T_no_update_required' )} !", 'button_cancel' )


