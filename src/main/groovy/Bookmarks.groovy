package lilive.bookmarks

import groovy.json.JsonBuilder
import groovy.json.JsonSlurper
import java.awt.Graphics2D
import java.awt.Image
import java.awt.image.BufferedImage
import java.util.Map as JMap
import javax.swing.Icon
import javax.swing.ImageIcon
import javax.swing.UIManager
import org.freeplane.api.MindMap as MindMap
import org.freeplane.api.Node
import org.freeplane.api.Properties
import org.freeplane.core.util.TextUtils
import org.freeplane.plugin.script.proxy.Convertible
import org.freeplane.plugin.script.proxy.ScriptUtils

class Bookmarks
{ 

    final static String storageKey = "BookmarksKeys"
    final static String anonymousIcon = "bookmarks/Bookmark 1"
    final static String namedIcon = "bookmarks/Bookmark 2"

    final static String globalKey = "Bookmarks"
    final static String monitorKey = "monitorMap"

    static void test()
    {
        ScriptUtils.c().statusInfo = 'Test successful !'
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
    static JMap loadNamedBookmarks( MindMap map )
    {
        // Read the datas from the map storage
        Convertible stored = map.storage.getAt( storageKey )
        
        // Convert these datas to an java.util.Map
        if( stored ) return new JsonSlurper().parseText( stored.getText() ) as JMap
        else return [:]
    }

    // Store the java.util.Map namedBookmarks in the freeplane map storage area.
    static void saveNamedBookmarks( JMap namedBookmarks, MindMap map )
    {
        JsonBuilder builder = new JsonBuilder()
        builder( namedBookmarks )
        map.storage.putAt( storageKey, builder.toString() )
    }

    // Erase the stored named bookmarks.
    // Return true is something change.
    static Boolean eraseNamedBookmarksStorage( MindMap map )
    {
        Convertible storage = map.storage.getAt( storageKey )
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
    // Return the currated bookmarks map.
    static JMap fixNamedBookmarksInconsistency( JMap namedBookmarks, MindMap map )
    {
        Boolean changed = namedBookmarks.removeAll
        {
            key, id ->
            return ( map.node( id ) == null )
        }
        if( changed ) saveNamedBookmarks( namedBookmarks, map )

        namedBookmarks.each
        {
            key, id ->
            Node n = map.node( id )
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

    static String getNodeShortPlainText( Node node )
    {
        String text = node.plainText
        if( text.length() > 30 ) text = text[0..27] + " ..."
        return text
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
            Node n = node.map.node( namedBookmarks[ s ] )
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
    static List getAllAnonymousBookmarkedNodes( MindMap map, List excludeIds = null )
    {
        List bookmarks = []
        map.root.findAll().each
        {
            n ->
            if( isAnonymousBookmarked( n ) )
            {
                if( excludeIds && excludeIds.contains( n.id ) ) return
                String text = getNodeShortPlainText( n )
                bookmarks << [ "id": n.id, "text": text ]
            }
        }
        return bookmarks
    }

    // Create a list of all the nodes with a named bookmarks.
    // Each element of this list is a map [ id, name, text ] where:
    // - id is the node id,
    // - name is the bookmark key (string)
    // - text is the node text, possibly truncated, without html format.
    // It is possible to exclude some node from this list.
    static List getAllNameBookmarkedNodes( MindMap map, List excludeIds = null )
    {
        JMap bmksMap = loadNamedBookmarks( map )
        bmksMap = fixNamedBookmarksInconsistency( bmksMap, map )

        if( excludeIds )
        { 
            bmksMap.removeAll{ key, id -> excludeIds.contains( id ) }
        }
    
        List bmksList = bmksMap.collect
        {
            key, id ->
            Node node = map.node( id )
            return [
                "id": id,
                "name": String.valueOf( (char) Integer.parseInt( key ) ),
                "text": getNodeShortPlainText( node )
            ]
        }

        return bmksList;
    }
    
    // Pause the add-on node changes monitoring feature
    static void pauseMonitor()
    {
        ChangeListener.pauseMonitor()
    }

    // Resume the add-on node changes monitoring feature
    static void resumeMonitor()
    {
        ChangeListener.resumeMonitor()
    }

    static ImageIcon getQuestionMarkIcon()
    {
        // Get a small question mark icon from the theme.
        // We can't simply call icon.getImage().getScaledInstance() because some themes (ie Nimbus)
        // do not return a suitable icon.getImage(). That's why we paint the icon.
        Icon srcIcon = UIManager.getIcon("OptionPane.questionIcon")
        int w = srcIcon.getIconWidth()
        int h = srcIcon.getIconHeight()
        BufferedImage bufferedImage = new BufferedImage( w, h, BufferedImage.TYPE_INT_ARGB )
        Graphics2D g = bufferedImage.createGraphics()
        srcIcon.paintIcon( null, g, 0, 0 );
        g.dispose()
        h = h / w * 16
        w = 16
        ImageIcon icon = new ImageIcon( bufferedImage.getScaledInstance( w, h, Image.SCALE_SMOOTH ) )
        return icon
    }
    
}
