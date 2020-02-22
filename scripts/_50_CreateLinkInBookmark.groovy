// Create a link to this node.
// The node where the link is created is a bookmark, or the memorized node if it exists.

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
import org.freeplane.features.link.LinkController
import org.freeplane.features.link.mindmapmode.MLinkController
import bookmarks.Bookmarks as BM

map = node.map
gui = null

// Create a link to another node in a node
def createLink( src, dst, message )
{
    dst.link.node = src
    c.setStatusInfo( 'standard', message, 'button_ok' )
}

def JList getNamedBookmarksJList( List bookmarks )
{
    if( ! bookmarks ) return null

    // Create the component
    JList component
    SwingBuilder.build{
        component = list(
            items: bookmarks.collect
            {
                // Display for each bookmark the name of the bookmark,
                // followed by the text of the node
                "<html><font color='red'><b><i>$it.name</i></b></font> : $it.text</html>"
            },
            visibleRowCount: Integer.min( 20, bookmarks.size() ),
            border: emptyBorder( 5 )
        )
    }
    component.setSelectedIndex( 0 )
    
    // Set the user interactions
    
    component.addKeyListener(
        new KeyAdapter()
        {
            public void keyReleased( KeyEvent e )
            {
                int code = e.getKeyCode()
                char chr = e.getKeyChar()
                int idx = (int) chr
                if( code == KeyEvent.VK_ENTER )
                {
                    int i = component.getSelectedIndex()
                    if( i >= 0 )
                    {
                        def bm = bookmarks[ i ]
                        def target = map.node( bm.id )
                        createLink( node, target, "${BM.gtt( 'T_created_link_in_NBM' )} \"$bm.name\"" )
                        gui.dispose()
                    }
                }
                else if( idx > 32 && idx != 127 && idx < 256 )
                {
                    // Try to link to the corresponding node id
                    String s = String.valueOf( chr )
                    def bm = bookmarks.find{ it.name == s }
                    if( bm )
                    {
                        def target = map.node( bm.id )
                        createLink( node, target, "${BM.gtt( 'T_created_link_in_NBM' )} \"${s}\"" )
                        gui.dispose()
                    }
                    else
                    {
                        c.setStatusInfo( 'standard', "${BM.gtt( 'T_no_node_with_key' )} \"${chr}\"", 'messagebox_warning' )
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
                    createLink( node, target, "${BM.gtt( 'T_created_link_in_NBM' )} \"$bm.name\"" )
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
            // Row 0 : A label
            // Row 1 : A label and a question mark label with a tooltip
            // Row 2 : A label
            // Row 3 : The list of the named bookmarks

            // Row 0
            label(
                "${BM.gtt( 'T_create_link_in' )}.",
                constraints: gbc( gridx:0, gridy:0, anchor:GridBagConstraints.LINE_START )
            )

            // Row 1
            label(
                "${BM.gtt( 'T_select_node_to_link_in' )}.",
                constraints: gbc( gridx:0, gridy:1, anchor:GridBagConstraints.LINE_START )
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
                        ${BM.gtt( 'T_tip_create_link_in_NBM' )}:
                        <ul>
                            <li>${BM.gtt( 'T_press_red_key' )}</li>
                            <li>${BM.gtt( 'T_click_BM' )}</li>
                            <li>${BM.gtt( 'T_arrow_select' )}</li>
                        </ul>
                    </html>""",
                constraints: gbc( gridx:1, gridy:1, anchor:GridBagConstraints.LINE_START, weightx:1, insets:[0,10,0,0] )
            )

            // Row 2
            if( showTabTip ) label(
                "<html>${BM.gtt( 'T_tab_to_display_SBM' )}.</html>",
                constraints: gbc( gridx:0, gridy:2, gridwidth:2, anchor:GridBagConstraints.LINE_START )
            )

            // Row 3
            scrollPane(
                border: emptyBorder( [10,0,0,0] ),
                constraints: gbc( gridx:0, gridy:3, gridwidth:2, fill:GridBagConstraints.HORIZONTAL, anchor:GridBagConstraints.LINE_START )
            )
            {
                widget( bookmarksJList )
            }
        }
    }
    
    return jPane
}

def JList getAnonymousBookmarksJList( List bookmarks )
{
    if( ! bookmarks ) return null

    // Create the component
    JList component
    SwingBuilder.build{
        component = list(
            items: bookmarks.collect{ it.text },
            visibleRowCount: Integer.min( 20, bookmarks.size() )
        )
    }
    component.setSelectedIndex( 0 )

    // Set the user interactions
    component.addKeyListener(
        new KeyAdapter()
        {
            @Override public void keyReleased(KeyEvent e)
            {
                int code = e.getKeyCode()
                if( code == KeyEvent.VK_ENTER )
                {
                    int idx = component.getSelectedIndex()
                    if( idx >= 0 )
                    {
                        def bm = bookmarks[ idx ]
                        def target = node.map.node( bm.id )
                        createLink( node, target, BM.gtt( 'T_created_link_in_SBM' ) )
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
                    createLink( node, target, BM.gtt( 'T_created_link_in_SBM' ) )
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
            // Row 0 : A label
            // Row 1 : A label and a question mark label with a tooltip
            // Row 2 : A label
            // Row 3 : The list of the named bookmarks

            // Row 0
            label(
                "${BM.gtt( 'T_create_link_to' )}.",
                constraints: gbc( gridx:0, gridy:0, anchor:GridBagConstraints.LINE_START )
            )
            
            // Row 1
            label(
                "${BM.gtt( 'T_select_BM_to_link_in' )}.",
                constraints: gbc( gridx:0, gridy:1, anchor:GridBagConstraints.LINE_START )
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
                        ${BM.gtt( 'T_tip_create_link_in_SBM' )}:
                        <ul>
                            <li>${BM.gtt( 'T_click_BM' )}</li>
                            <li>${BM.gtt( 'T_arrow_select' )}</li>
                        </ul>
                    </html>""",
                constraints: gbc( gridx:1, gridy:1, anchor:GridBagConstraints.LINE_START, weightx:1, insets:[0,10,0,0] )
            )
            
            // Row 2
            if( showTabTip ) label(
                "<html>${BM.gtt( 'T_tab_to_display_NBM' )}.</html>",
                constraints: gbc( gridx:0, gridy:2, gridwidth:2, anchor:GridBagConstraints.LINE_START )
            )
            
            // Row 3
            scrollPane(
                border: emptyBorder( [10,0,0,0] ),
                constraints: gbc( gridx:0, gridy:3, gridwidth:2, fill:GridBagConstraints.HORIZONTAL, anchor:GridBagConstraints.LINE_START )
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
    def memorizedNodeID = ((MLinkController)(LinkController.getController())).getAnchorID()

    def gui
    SwingBuilder.build{
        gui = dialog(
            title: BM.gtt( 'T_create_link_in_win_title' ),
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
                vbox( constraints: BorderLayout.PAGE_END)
                {
                    if( memorizedNodeID ) label( text: "<html>${BM.gtt( 'T_space_to_link_in_memorized' )}.</html>" )
                    label( text: "<html>${BM.gtt( 'T_press_esc_cancel' )}.</html>" )
                }
            }
        }
    }

    // Set Esc key to close the script
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
                c.setStatusInfo( 'standard', BM.gtt( 'T_link_aborded' ), 'button_cancel' )
	        }
        }
    )

    if( memorizedNodeID )
    { 
        // Set Space key to create the link to the standard freeplane memorized node
        def onSpacePressID = "onSpacePress"
        spaceStroke = KeyStroke.getKeyStroke( KeyEvent.VK_SPACE, 0 )
        inputMap.put( spaceStroke, onSpacePressID )
        gui.getRootPane().getActionMap().put(
            onSpacePressID,
            new AbstractAction()
            {
	            @Override
	            public void actionPerformed( ActionEvent e )
                {
	                menuUtils.executeMenuItems( [ 'MakeLinkFromAnchorAction' ] )
                    gui.dispose()
	            }
            }
        )
        if( namedJList ) namedJList.getInputMap().put( spaceStroke, "none" )
        if( anonymJList ) anonymJList.getInputMap().put( spaceStroke, "none" )
    }

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
def namedBookmarks = BM.getAllNameBookmarkedNodes( map, [ node.id ] )
def anonymousBookmarks = BM.getAllAnonymousBookmarkedNodes( map, [ node.id ] )

// Quit the script if there is no bookmarks
if( ! namedBookmarks && ! anonymousBookmarks )
{
    ui.informationMessage( ui.frame, "${BM.gtt( 'T_no_bookmarks' )} !", BM.gtt( 'T_BM_win_title' ) )
    return
}

// Create the gui
JList namedBookmarksJList = getNamedBookmarksJList( namedBookmarks )
JPanel namedBookmarksJPanel = getNamedBookmarksJPanel(
    namedBookmarks, namedBookmarksJList, (boolean)anonymousBookmarks
)
JList anonymousBookmarksJList = getAnonymousBookmarksJList( anonymousBookmarks )
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
