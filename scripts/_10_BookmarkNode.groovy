import org.freeplane.plugin.script.proxy.Convertible
import groovy.json.JsonBuilder
import groovy.json.JsonSlurper
import groovy.swing.SwingBuilder
import javax.swing.JFrame
import javax.swing.BoxLayout
import java.awt.event.*
import java.awt.Color
import java.awt.Component
import java.awt.BorderLayout

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

// Create the graphical user interface to display
def gui
groovy.swing.SwingBuilder.build
{
    gui = dialog(
        title: 'Bookmarks',
        modal: true,
        owner: ui.frame,
        defaultCloseOperation: JFrame.DISPOSE_ON_CLOSE,
        pack: true
    ){
        borderLayout()
        def blue = new Color( 0, 0, 255 )

        // Fist panel : instructions
        panel(
            border: emptyBorder( 10 ),
            constraints: BorderLayout.PAGE_START,
        ){
            boxLayout(axis: BoxLayout.Y_AXIS )
            if( isAnonymousBookmark )
            {
                label(
                    text: "This node is already bookmarked.",
                    foreground: blue,
                    border: emptyBorder( 0, 0, 5, 0 )
                )
                label("<html><b>Backspace</b> : Delete this bookmark</html>")
                label("<html><b>Other key</b> : Create instead a named bookmark</html>")
            }
            else if( isNamedBookmark )
            {
                label(
                    text: "This node already has a named bookmark.",
                    foreground: blue,
                    border: emptyBorder( 0, 0, 5, 0 )
                )
                label("<html><b>Backspace</b> : Delete this bookmark</html>")
                label("<html><b>Space</b> : Create instead a regular bookmark</html>")
                label("<html><b>Other key</b> : Change the name of this bookmark")
            }
            else
            {
                label("<html><b>Space</b> : Create a regular bookmark</html>")
                label("<html><b>Other key</b> : Create a named bookmark</html>")
            }
            label("<html><b>Esc</b> to cancel</html>")
        }

        // Second panel : only if named bookmarks exists
        if( namedBookmarks.size() > 0 )
        {
            panel(
                border:titledBorder( "Already named bookmarks are" ),
                constraints: BorderLayout.CENTER
            ){
                boxLayout( axis: BoxLayout.Y_AXIS )
                namedBookmarks.each
                {
                    key, id ->
                    // Display a line of text with the associated key
                    // and the 30 first caracters of the node
                    def target = map.node( id )
                    def text = target.text
                    if( text.length() > 30 ) text = text[0..27] + "..."
                    text =
                        "<html><font color='red'><b>" +
                        String.valueOf( (char) Integer.parseInt( key ) ) +
                        "</b></font> : " +
                        text +
                        "</html>"
                    if( id == node.id ) label( text: text, foreground: blue )
                    else label( text )
                }
            }
        }
    }
}

// Create the response to the user key press
gui.setFocusable(true)
gui.addKeyListener(
    new java.awt.event.KeyAdapter()
    {
        @Override
        public void keyTyped(KeyEvent e)
        {
            // Get the key pressed
            char chr = e.getKeyChar()
            int keyCharCode = (int) chr

            if( keyCharCode == 8 )
            {
                if( isNamedBookmark )
                {
                    deleteNamedBookmark( node, namedBookmarks )
                    gui.dispose()
                    c.statusInfo = 'This node has no bookmark anymore'
                }
                else if( isAnonymousBookmark )
                {
                    deleteAnonymousBookmark( node )
                    gui.dispose()
                    c.statusInfo = 'This node has no bookmark anymore'
                }
            }
            else if( keyCharCode == 32 )
            {
                if( isNamedBookmark )
                {
                    deleteNamedBookmark( node, namedBookmarks )
                    createAnonymousBookmark( node )
                    gui.dispose()
                    c.statusInfo = 'This node now has a regular bookmark'
                }
                else if( ! isAnonymousBookmark )
                {
                    createAnonymousBookmark( node )
                    gui.dispose()
                    c.statusInfo = 'This node now has a regular bookmark'
                }
            }
            else if( keyCharCode > 32 && keyCharCode != 127 && keyCharCode < 256 )
            {
                if( isAnonymousBookmark ) deleteAnonymousBookmark( node )
                createNamedBookmark( node, keyCharCode, namedBookmarks )
                gui.dispose()
                c.statusInfo = 'This node now has a bookmark named "' + chr + "'"
            }
            else if( keyCharCode == 27 )
            {
                c.statusInfo = 'Bookmark operation aborded'
                gui.dispose()
            }
        }
    }
)

// Center the gui over the freeplane window
gui.setLocationRelativeTo( ui.frame )
gui.visible = true

