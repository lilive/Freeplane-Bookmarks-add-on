package bookmarks

import java.util.Map as JMap
import groovy.json.JsonBuilder
import groovy.json.JsonSlurper
import org.freeplane.plugin.script.proxy.ScriptUtils
import org.freeplane.plugin.script.proxy.Convertible
import org.freeplane.api.Map as FPMap
import org.freeplane.api.Node
import org.freeplane.core.util.TextUtils

class Bookmarks
{ 

    final static String storageKey = "BookmarksKeys"
    final static String anonymousIcon = "bookmarks/Bookmark 1"
    final static String namedIcon = "bookmarks/Bookmark 2"

    final static String globalKey = "Bookmarks"
    final static String monitorKey = "monitorMap"

    static void test()
    {
        ScriptUtils.c().statusInfo = 'Test !'
    }

    // gtt = Get Translated Text
    static String gtt( String key )
    {
        return TextUtils.getText( 'addons.bookmarks.' + key )
    }
    
    // Create a java.util.Map where :
    // - the keys are the keyboard keys assigned to a bookmarked node;
    // - the values are the id of the corresponding bookmarked node.
    // This java.util.Map is read from the freeplane map storage area.
    static JMap loadNamedBookmarks( FPMap map )
    {
        // Read the datas from the map storage
        def stored = map.storage.getAt( storageKey )
        
        // Convert these datas to an java.util.Map
        if( stored ) return new JsonSlurper().parseText( stored.getText() ) as JMap
        else return [:]
    }

    // Store the java.util.Map namedBookmarks in the freeplane map storage area.
    static void saveNamedBookmarks( JMap namedBookmarks, FPMap map )
    {
        def builder = new JsonBuilder()
        builder( namedBookmarks )
        map.storage.putAt( storageKey, builder.toString() )
    }

    // Erase the stored named bookmarks.
    // Return true is something change.
    static Boolean eraseNamedBookmarksStorage( FPMap map )
    {
        def storage = map.storage.getAt( storageKey )
        if( storage != null )
        { 
            map.storage.putAt( storageKey, null )
            return true
        }
        else
        {
            return false
        }
    }
    
    // Remove in namedBookmarks all the node that don't exist,
    // and add the named bookmark icon to each referenced node that miss it.
    static JMap fixNamedBookmarksInconsistency( JMap namedBookmarks, FPMap map )
    {
        def changed = namedBookmarks.removeAll
        {
            key, id ->
            return ( map.node( id ) == null )
        }
        if( changed ) saveNamedBookmarks( namedBookmarks, map )

        namedBookmarks.each
        {
            key, id ->
            def n = map.node( id )
            if( ! Bookmarks.hasNamedBookmarkIcon( n ) ) n.icons.add( Bookmarks.namedIcon )
        }
        
        return namedBookmarks
    }

    // Return true if the node has the anonymous bookmark icon
    static Boolean hasAnonymousBookmarkIcon( Node node )
    {
        return node.icons.contains( anonymousIcon )
    }

    // Return true if the node is bookmarked with an anonymous bookmark icon
    // (actually it's the same than hasAnonymousBookmarkIcon()
    static Boolean isAnonymousBookmarked( Node node )
    {
        return node.icons.contains( anonymousIcon )
    }

    // Return true if the node has the named bookmark icon
    static Boolean hasNamedBookmarkIcon( Node node )
    {
        return node.icons.contains( namedIcon )
    }

    // Return true if the node appear in the namedBookmarks map
    static Boolean hasBookmarkName( Node node, JMap namedBookmarks )
    {
        return namedBookmarks.containsValue( node.id )
    }

    // Return true if the node is bookmarked with a named bookmark
    static Boolean isNamedBookmarked( Node node, JMap namedBookmarks )
    {
        return hasNamedBookmarkIcon( node ) && hasBookmarkName( node, namedBookmarks )
    }

    // Return true if the node is bookmarked (anonymous or named bookmark)
    static Boolean isBookmarked( Node node, JMap namedBookmarks )
    {
        return isAnonymousBookmarked( node ) || isNamedBookmarked( node, namedBookmarks )
    }

    // Create an anonymous bookmark for this node.
    static void createAnonymousBookmark( Node node )
    {
        if( ! node.icons.contains( anonymousIcon ) )
        { 
            node.icons.add( anonymousIcon )
        }
    }

    // Remove an anonymous bookmark for this node.
    static void deleteAnonymousBookmark( Node node )
    {
        while( node.icons.remove( anonymousIcon ) ){}
    }
    
    // Create a named bookmark for this node.
    // The name of the bookmark is a single keyboard key.
    static JMap createNamedBookmark( Node node, keyCharCode, JMap namedBookmarks, Boolean saveChange = true )
    {
        String s = String.valueOf( keyCharCode )

        // If another node has this named bookmark, remove it
        if( namedBookmarks.containsKey( s ) )
        {
            def n = node.map.node( namedBookmarks[ s ] )
            if( n && n != node ) deleteNamedBookmark( n, namedBookmarks )
        }
    
        // Delete the existing named bookmarks assigned to the current node
        namedBookmarks.removeAll{ k, v -> v == node.id }

        // Assign the key pressed to the current node
        namedBookmarks[ s ] = node.id
        namedBookmarks = namedBookmarks.sort()
    
        // Save the new namedBookmarks map
        if( saveChange ) saveNamedBookmarks( namedBookmarks, node.map )

        // Add the icon
        node.icons.add( namedIcon )

        return namedBookmarks
    }

    // Delete an existing named bookmark
    static JMap deleteNamedBookmark( Node node, JMap namedBookmarks, Boolean saveChange = true )
    {
        if( namedBookmarks.containsValue( node.id ) )
        {
            // Clear the map
            namedBookmarks.removeAll{ key, value -> value == node.id }

            // Save the new namedBookmarks map
            if( saveChange ) saveNamedBookmarks( namedBookmarks, node.map )
        }

        // Remove the icon
        while( node.icons.remove( namedIcon ) ){}

        return namedBookmarks
    }

    // Create a list of all the nodes with an anonymous bookmarks.
    // Each element of this list is a map [ id, text ] where:
    // - id is the node id,
    // - text is the node text, possibly truncated.
    // It is possible to exclude some node from this list.
    static List getAllAnonymousBookmarkedNodes( FPMap map, List excludeIds = null )
    {
        def bookmarks = []
        map.root.findAll().each
        {
            n ->
            if( isAnonymousBookmarked( n ) )
            {
                if( excludeIds && excludeIds.contains( n.id ) ) return
                def text = n.text
                if( text.length() > 30 ) text = text[0..27] + "..."
                bookmarks << [ "id": n.id, "text": text ]
            }
        }
        return bookmarks
    }

    // Create a list of all the nodes with a named bookmarks.
    // Each element of this list is a map [ id, name, text ] where:
    // - id is the node id,
    // - name is the bookmark key (string)
    // - text is the node text, possibly truncated.
    // It is possible to exclude some node from this list.
    static List getAllNameBookmarkedNodes( FPMap map, List excludeIds = null )
    {
        def bmksMap = loadNamedBookmarks( map )
        bmksMap = fixNamedBookmarksInconsistency( bmksMap, map )

        if( excludeIds )
        { 
            bmksMap.removeAll{ key, id -> excludeIds.contains( id ) }
        }
    
        def bmksList = bmksMap.collect
        {
            key, id ->
            def node = map.node( id )
            def text = node.text
            if( text.length() > 30 ) text = text[0..27] + "..."
            return [
                "id": id,
                "name": String.valueOf( (char) Integer.parseInt( key ) ),
                "text": text
            ]
        }

        return bmksList;
    }
    
    // Pause the add-on node changes monitoring feature
    static void pauseMonitor( FPMap map )
    {
        def vars = [:]
        def storage = map.storage
        
        // Read the datas from the map storage
        def stored = storage.getAt( globalKey )
        // Convert these datas to an java.util.Map
        if( stored ) vars = new JsonSlurper().parseText( stored.getText() ) as JMap

        // Set monitoring var to disable
        vars[ monitorKey ] = false
        
        // Save the vars in map local storage
        def builder = new JsonBuilder()
        builder( vars )
        storage.putAt( globalKey, builder.toString() )
    }

    // Resume the add-on node changes monitoring feature
    static void resumeMonitor( FPMap map )
    {
        def storage = map.storage
        
        // Read the datas from the map storage
        def stored = storage.getAt( globalKey )
        if( ! stored ) return
        // Convert these datas to an java.util.Map
        def vars = new JsonSlurper().parseText( stored.getText() ) as JMap

        // Set monitoring var to disable
        vars[ monitorKey ] = true
        
        // Save the vars in map local storage
        def builder = new JsonBuilder()
        builder( vars )
        storage.putAt( globalKey, builder.toString() )
    }

}
