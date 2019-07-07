import groovy.json.JsonBuilder
import groovy.json.JsonSlurper
import groovy.swing.SwingBuilder
import java.awt.BorderLayout
import java.awt.Color
import java.awt.Component
import java.awt.FlowLayout
import java.awt.GridBagConstraints
import java.awt.Image
import java.awt.KeyboardFocusManager
import java.awt.event.*
import javax.swing.AbstractAction
import javax.swing.ActionMap
import javax.swing.BoxLayout
import javax.swing.ImageIcon
import javax.swing.InputMap
import javax.swing.JComponent
import javax.swing.JFrame
import javax.swing.JList
import javax.swing.JPanel
import javax.swing.KeyStroke
import javax.swing.SwingConstants
import javax.swing.UIManager
import org.freeplane.plugin.script.proxy.Convertible

storageKey = "BookmarksKeys"
anonymousIcon = "bookmarks/Bookmark 1"
namedIcon = "bookmarks/Bookmark 2"
map = node.map
namedBookmarks = []
anonymousBookmarks = []

gui = null

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
    def namedBookmarksMap = new JsonSlurper().parseText( marksString.getText() )

    return namedBookmarksMap as Map
}

def saveNamedBookmarks( Map namedBookmarksMap )
{
    // Store the HashMap namedBookmarks in the freeplane map storage area.

    def builder = new JsonBuilder()
    builder( namedBookmarksMap )
    node.map.storage.putAt( storageKey, builder.toString() )
}

def populateAnonymousBookmarksList( node, List bookmarks )
{
    if( node.icons.contains( anonymousIcon ) ){
        def text = node.text
        if( text.length() > 30 ) text = text[0..27] + "..."
        bookmarks << [ "id": node.id, "text": text ]
    }
    node.children.each{ populateAnonymousBookmarksList( it, bookmarks ) }
}

def putIconAtFirstPosition( node, icon )
{
    // Add an icon at the first position for this node
    // Ensure this icon is unique

    def icons = node.icons
    
    // Do nothing else if there is already this icon as fisrt icon
    def num = icons.iterator().count( icon )
    if( num == 1 && icons.first == icon ) return

    def oldIcons = icons.iterator().toList()

    // Remove all the same icons if necessary
    if( num > 0 ) oldIcons.removeAll{ it == icon }

    def newIcons = [ icon ]
    newIcons.addAll( oldIcons )
    icons.clear()
    icons.addAll( newIcons )
}

def addNamedBookmarkIcon( node )
{
    // Add the named bookmark icon to this node, if needed.
    // Remove anonymous bookmarks icons.
    // Ensure the icon is the first icon of the node

    def icons = node.getIcons()

    // Remove anonymous bookmarks icons
    while( icons.contains( anonymousIcon ) ) icons.remove( anonymousIcon )

    putIconAtFirstPosition( node, namedIcon )
}

// Jump to a node after the gui close
def jumpToNodeAfterGuiDispose( target, message )
{
    // If the code to jump to a node is executed before the gui close,
    // it leave freeplane in a bad focus state.
    // This is solved by putting this code in a listener executed
    // after the gui destruction:
    gui.addWindowListener(
        new WindowAdapter()
        {
            @Override
            public void windowClosed( WindowEvent event )
            {
                c.select(  target )
                c.centerOnNode( target )
                c.statusInfo = message
            }
        }
    )
}

// Create a list of all the named bookmarks
def List initNamedBookmarks()
{
    // Create a HashMap for the named bookmarks
    def bmksMap = loadNamedBookmarks()

    // Make sure namedBookmarks is up-to-date
    def change = false
    bmksMap.removeAll
    {
        // Remove from the map all the node that don't exist
        // and add icon for each bookmarked node
        key, id ->
        def n = map.node( id )
        def missing = n == null
        if( ! missing ) addNamedBookmarkIcon( n )
        else {
            change = true
        }
        missing
    }
    if( change ) saveNamedBookmarks( bmksMap )

    // Refactor the map to make a list, better suited for the rest of the script
    def bmksList = bmksMap.collect{
        key, id ->
        def target = map.node( id )
        def text = target.text
        if( text.length() > 30 ) text = text[0..27] + "..."
        [
            "id": id,
            "name": String.valueOf( (char) Integer.parseInt( key ) ),
            "text": text
        ]
    }
    
    return bmksList
}

// Create a list of all the anonymous bookmarks
def List initAnonymousBookmarks()
{
    def bookmarks = []
    populateAnonymousBookmarksList( map.root, bookmarks )
    return bookmarks
}

def JList getNamedBookmarksJList(
    List bookmarks,         // List of the bookmarks
    String currentNodeId
){
    if( ! bookmarks ) return null

    // Create the component
    JList component
    SwingBuilder.build{
        component = list(
            items: bookmarks.collect{
                // Display for each bookmark the name of the bookmark,
                // followed by the text of the node
                if( it.id != currentNodeId ) "<html><font color='red'><b><i>$it.name</i></b></font> : $it.text</html>"
                else "<html><font color='red'><b><i>$it.name</i></b></font><b> : $it.text</b></html>"
            },
            visibleRowCount: Integer.min( 20, bookmarks.size() ),
            border: emptyBorder( 5 )
        )
    }

    // Initialize the component state
    int selectedIdx = bookmarks.findIndexOf{ it.id == currentNodeId }
    if( selectedIdx < 0 ) selectedIdx = 0;
    component.setSelectedIndex( selectedIdx )
    component.ensureIndexIsVisible( selectedIdx )
    
    // Set the user interactions
    
    component.addKeyListener(
        new KeyAdapter()
        {
            public void keyReleased( KeyEvent e )
            {
                int code = e.getKeyCode()
                char chr = e.getKeyChar()
                int idx = (int) chr
                if( code == KeyEvent.VK_ENTER ){
                    int i = component.getSelectedIndex()
                    if( i >= 0 )
                        {
                        def bm = bookmarks[ i ]
                        def target = map.node( bm.id )
                        jumpToNodeAfterGuiDispose( target, "Jumped to bookmark named \"$bm.name\"" )
                        gui.dispose()
                    }
                }
                else if( idx > 32 && idx != 127 && idx < 256 ){
                    // Try to jump to the corresponding node id
                    String s = String.valueOf( chr )
                    def bm = bookmarks.find{ it.name == s }
                    if( bm )
                        {
                        def target = map.node( bm.id )
                        jumpToNodeAfterGuiDispose( target, "Jumped to bookmark named \"${s}\"" )
                        gui.dispose()
                    }
                    else
                        {
                        c.statusInfo = 'There is no node marked with the key ' + chr
                    }
                }
            }
        }
    )
    
    component.addMouseListener(
        new MouseAdapter()
        {
            @Override public void mouseClicked(MouseEvent e)
            {
                int idx = component.getSelectedIndex()
                if( idx >= 0 )
                    {
                    def bm = bookmarks[ idx ]
                    def target = node.map.node( bm.id )
                    jumpToNodeAfterGuiDispose( target, "Jump to bookmark named \"$bm.name\"" )
                    gui.dispose()
                }
            }
        }
    )
    
    return component
}

// Create a JPanel for the named bookmarks
def JPanel getNamedBookmarksJPanel(
    List bookmarks,         // List of the bookmarks
    JList bookmarksJList,   // Component that display this list
    boolean showTabTip      // Do we need to add a comment about the Tab key ?
){
    if( ! bookmarks ) return null
    
    // List all the named bookmarks
    JPanel jPane
    SwingBuilder.build{
        jPane = panel(
            border: emptyBorder( [0,0,10,0] )
        )
        {
            gridBagLayout()
            // This gridbag will contains 4 items
            // Row 0 : A label and a question mark label with a tooltip
            // Row 1 : A label
            // Row 2 : The kist of the named bookmarks

            // Row 0
            label(
                "Select the named bookmark you want to jump to.",
                constraints: gbc( gridx:0, gridy:0, anchor:GridBagConstraints.LINE_START )
            )
            def icon = UIManager.getIcon("OptionPane.questionIcon")
            Image img = icon.getImage()
            int w = icon.getIconWidth() / 2
            int h = icon.getIconHeight() / 2
            def newimg = img.getScaledInstance( w, h, Image.SCALE_SMOOTH )
            icon = new ImageIcon( newimg )
            label(
                icon: icon,
                toolTipText:
                    """<html>
                        To jump to a named bookmark either:
                        <ul>
                            <li>Press one of the red keys</li>
                            <li>Click a bookmark</li>
                            <li>Select a bookmark with the <i>arrow keys</i> and press <i>Enter</i></li>
                        </ul>
                    </html>""",
                constraints: gbc( gridx:1, gridy:0, anchor:GridBagConstraints.LINE_START, weightx:1, insets:[0,10,0,0] )
            )

            // Row 1
            if( showTabTip ) label(
                "<html>To jump to a standard bookmark press <i>Tab</i>.</html>",
                constraints: gbc( gridx:0, gridy:1, gridwidth:2, anchor:GridBagConstraints.LINE_START )
            )

            // Row 2
            scrollPane(
                border: emptyBorder( [10,0,0,0] ),
                constraints: gbc( gridx:0, gridy:2, gridwidth:2, fill:GridBagConstraints.HORIZONTAL, anchor:GridBagConstraints.LINE_START )
            )
            {
                widget( bookmarksJList )
            }
        }
    }
    
    return jPane
}

def JList getAnonymousBookmarksJList(
    List bookmarks,         // List of the bookmarks
    String currentNodeId
){
    if( ! bookmarks ) return null

    // Create the component
    JList component
    SwingBuilder.build{
        component = list(
            items: bookmarks.collect{
                if( currentNodeId == it.id ) "<html><b>$it.text</b></html>"
                else it.text
            },
            visibleRowCount: Integer.min( 20, bookmarks.size() )
        )
    }

    // Initialize the component state
    int selectedIdx = bookmarks.findIndexOf{ it.id == currentNodeId }
    if( selectedIdx < 0 ) selectedIdx = 0
    component.setSelectedIndex( selectedIdx )
    component.ensureIndexIsVisible( selectedIdx )

    // Set the user interactions
    component.addKeyListener(
        new KeyAdapter()
        {
            @Override public void keyReleased(KeyEvent e)
            {
                int code = e.getKeyCode()
                if( code == KeyEvent.VK_ENTER ){
                    int idx = component.getSelectedIndex()
                    if( idx >= 0 )
                        {
                        def bm = bookmarks[ idx ]
                        def target = node.map.node( bm.id )
                        jumpToNodeAfterGuiDispose( target, "Jump to bookmark" )
                        gui.dispose()
                    }
                }
            }
        }
    )
        
    component.addMouseListener(
        new MouseAdapter()
        {
            @Override public void mouseClicked(MouseEvent e)
            {
                int idx = component.getSelectedIndex()
                if( idx >= 0 )
                    {
                    def bm = bookmarks[ idx ]
                    def target = node.map.node( bm.id )
                    jumpToNodeAfterGuiDispose( target, "Jump to bookmark" )
                    gui.dispose()
                }
            }
        }
    )

    return component
}

// Create a JPanel for the anonymous bookmarks
def JPanel getAnonymousBookmarksJPanel(
    List bookmarks,         // List of the bookmarks
    JList bookmarksJList,   // Component that display this list
    boolean showTabTip      // Do we need to add a comment about the Tab key ?
){
    if( ! bookmarks ) return null
    
    // List all the named bookmarks
    JPanel jPane
    SwingBuilder.build{
        jPane = panel(
            border: emptyBorder( [0,0,10,0] )
        )
        {
            gridBagLayout()
            // This gridbag will contains 4 items
            // Row 0 : A label and a question mark label with a tooltip
            // Row 1 : A label
            // Row 2 : The kist of the named bookmarks
            
            // Row 0
            label(
                "Select the bookmark you want to jump to.",
                constraints: gbc( gridx:0, gridy:0, anchor:GridBagConstraints.LINE_START )
            )
            def icon = UIManager.getIcon("OptionPane.questionIcon")
            Image img = icon.getImage()
            int w = icon.getIconWidth() / 2
            int h = icon.getIconHeight() / 2
            def newimg = img.getScaledInstance( w, h, Image.SCALE_SMOOTH )
            icon = new ImageIcon( newimg )
            label(
                icon: icon,
                toolTipText:
                    """<html>
                        To jump to a bookmark either:
                        <ul>
                            <li>Click a bookmark</li>
                            <li>Select a bookmark with the <i>arrow keys</i> and press <i>Enter</i></li>
                        </ul>
                    </html>""",
                constraints: gbc( gridx:1, gridy:0, anchor:GridBagConstraints.LINE_START, weightx:1, insets:[0,10,0,0] )
            )
            
            // Row 1
            if( showTabTip ) label(
                "<html>To jump to a named bookmark press <i>Tab</i>.</html>",
                constraints: gbc( gridx:0, gridy:1, gridwidth:2, anchor:GridBagConstraints.LINE_START )
            )
            
            // Row 2
            scrollPane(
                border: emptyBorder( [10,0,0,0] ),
                constraints: gbc( gridx:0, gridy:2, gridwidth:2, fill:GridBagConstraints.HORIZONTAL, anchor:GridBagConstraints.LINE_START )
            )
            {
                widget( bookmarksJList )
            }
        }
    }

    return jPane
}

// Create the graphical user interface to display
def createGui(
    JPanel namedPanel, JList namedJList,
    JPanel anonymPanel, JList anonymJList
)
{
    def gui
    SwingBuilder.build{
        gui = dialog(
            title: 'Jump to bookmark',
            modal:true,
            owner: ui.frame,
            defaultCloseOperation: JFrame.DISPOSE_ON_CLOSE
        ){
            borderLayout()
            // A panel for all the compontents (needed to add an inner padding)
            panel(
                border: emptyBorder( 10 ),
                constraints: BorderLayout.PAGE_START
            ){
                borderLayout()

                // First element of the main panel :
                // A panel which can contains 2 panels:
                // - one for the named bookmarks
                // - one for the anonymous bookmarks
                panel( constraints: BorderLayout.LINE_START )
                {
                    borderLayout()
                    if( namedPanel ) widget( namedPanel, constraints: BorderLayout.PAGE_START )
                    if( anonymPanel ) widget( anonymPanel, constraints: BorderLayout.PAGE_END )
                }

                // Second element of the main panel :
                label( text: "<html>Press <i>Esc</i> to cancel</html>.", constraints: BorderLayout.PAGE_END )
            }
        }
    }

    // Set est Esc ket to close the script
    def onEscPressID = "onEscPress"
    def inputMap = gui.getRootPane().getInputMap( JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT )
    inputMap.put( KeyStroke.getKeyStroke( KeyEvent.VK_ESCAPE, 0 ), onEscPressID )
    gui.getRootPane().getActionMap().put(
        onEscPressID,
        new AbstractAction()
        {
	    @Override
	    public void actionPerformed( ActionEvent e )
            {
	        gui.dispose()
                c.statusInfo = 'Jump to bookmark aborded'
	    }
        }
    )

    if( namedJList )
        {
        if( anonymPanel ) anonymPanel.visible = false
        namedJList.requestFocus()
    }
    else if( anonymJList )
        {
        if( namedPanel ) namedPanel.visible = false
        anonymJList.requestFocus()
    }
    
    // Set the Tab key to switch between the 2 bookmarks types
    if( namedPanel && namedJList && anonymPanel && anonymJList )
        {
        namedJList.setFocusTraversalKeysEnabled( false )
        anonymJList.setFocusTraversalKeysEnabled( false )
        def onTabPressID = "onTabPress"
        inputMap.put( KeyStroke.getKeyStroke( KeyEvent.VK_TAB, 0 ), onTabPressID )
        gui.getRootPane().getActionMap().put(
            onTabPressID,
            new AbstractAction()
            {
                public void actionPerformed( ActionEvent e )
                {
                    if( namedPanel.visible )
                        {
                        namedPanel.visible = false
                        anonymPanel.visible = true
                        anonymJList.requestFocus()
                    }
                    else
                        {
                        namedPanel.visible = true
                        anonymPanel.visible = false
                        namedJList.requestFocus()
                    }                
                    gui.pack()
                    gui.setLocationRelativeTo( ui.frame )
                }
            }
        )
    }

    return gui
}

// Find all the bookmarks
namedBookmarks = initNamedBookmarks()
anonymousBookmarks = initAnonymousBookmarks()

// Detect if the current node has a named bookmark
isNamedBookmark = namedBookmarks.any{ it[ "id" ] == node.id }
// Detect if the current node has a anonymous bookmark
isAnonymousBookmark = node.icons.contains( anonymousIcon )

// Quit the script if there is no bookmarks
if( ! namedBookmarks && ! anonymousBookmarks )
    {
    ui.informationMessage( ui.frame, "There is no bookmarks !", "Bookmarks" )
    return
}

// Create the gui
JList namedBookmarksJList = getNamedBookmarksJList( namedBookmarks, node.id )
JPanel namedBookmarksJPanel = getNamedBookmarksJPanel(
    namedBookmarks, namedBookmarksJList, (boolean)anonymousBookmarks
)
JList anonymousBookmarksJList = getAnonymousBookmarksJList( anonymousBookmarks, node.id )
JPanel anonymousBookmarksJPanel = getAnonymousBookmarksJPanel(
    anonymousBookmarks, anonymousBookmarksJList, (boolean)namedBookmarks
)
gui = createGui(
    namedBookmarksJPanel, namedBookmarksJList,
    anonymousBookmarksJPanel, anonymousBookmarksJList
)

// Center the gui over the freeplane window
gui.pack()
gui.setLocationRelativeTo( ui.frame )
gui.visible = true

