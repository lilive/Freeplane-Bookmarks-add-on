package lilive.bookmarks

import lilive.bookmarks.Bookmarks as BM
import groovy.swing.SwingBuilder
import java.awt.BorderLayout
import java.awt.GridBagConstraints
import java.awt.event.*
import java.util.Map as JMap
import javax.swing.AbstractAction
import javax.swing.InputMap
import javax.swing.JComponent
import javax.swing.JFrame
import javax.swing.JList as SwingList
import javax.swing.JPanel
import javax.swing.KeyStroke
import org.freeplane.api.MindMap as MindMap
import org.freeplane.api.Node as Node
import org.freeplane.core.ui.components.UITools as ui
import org.freeplane.features.link.LinkController
import org.freeplane.features.link.mindmapmode.MLinkController
import org.freeplane.plugin.script.proxy.Proxy.Controller as ProxyController
import org.freeplane.plugin.script.proxy.Proxy.Node as ProxyNode
import org.freeplane.plugin.script.proxy.ScriptUtils
import org.freeplane.core.util.MenuUtils

/**
 * Display a window that allow to create a link in or from a bookmarked node
 */
public class CreateLinkGUI
{
    static private Object gui

    static public enum Mode {
        IN, // The target of the link is the selected node, the link is created in a bookmarked node
        TO  // The target of the link is a bookmarked node, the link is created in the selected node
    }

    // Build and display the window
    static public void show( ProxyNode node, Mode mode  )
    {
        MindMap map = node.map
        SwingBuilder swing = new SwingBuilder()

        // Find all the bookmarks
        List namedBookmarks = BM.getAllNameBookmarkedNodes( map )
        List anonymousBookmarks = BM.getAllAnonymousBookmarkedNodes( map )

        // Quit the script if there is no bookmarks
        if( ! namedBookmarks && ! anonymousBookmarks )
            {
            ui.informationMessage( ui.currentFrame, "${BM.gtt( 'T_no_bookmarks' )} !", BM.gtt( 'T_BM_win_title' ) )
            return
        }

        // Create the gui
        gui = createWindow(
            swing, node, map,
            namedBookmarks, anonymousBookmarks,
            mode
        )

        // Center the gui over the freeplane window
        gui.pack()
        gui.setLocationRelativeTo( ui.currentFrame )
        gui.visible = true
    }

    // Create a link to another node in a node
    static private void createLink(
        ProxyNode n1,
        ProxyNode n2,
        String message,
        Mode mode
    )
    {
        if( mode == Mode.IN ) n2.link.node = n1
        else n1.link.node = n2
        Utils.setStatusInfo( message, 'button_ok' )
    }

    // Build a GUI list component that display the named bookmarks and
    // that allow to use them.
    static private SwingList getNamedBookmarksSwingList(
        SwingBuilder swing,
        ProxyNode node, MindMap map,
        List bookmarks, // List of the bookmarks
        Mode mode
    ){
        if( ! bookmarks ) return null

        // Create the component
        SwingList component
        swing.build{
            component = list(
                items: bookmarks.collect
                {
                    // Display for each bookmark the name of the bookmark,
                    // followed by the text of the node
                    if( it.id != node.id ) "<html><font color='red'><b><i>$it.name</i></b></font> : $it.text</html>"
                    else "<html><font color='red'><b><i>$it.name</i></b></font><b> : $it.text</b></html>"
                },
                visibleRowCount: Integer.min( 20, bookmarks.size() ),
                border: emptyBorder( 5 )
            )
        }

        // Initialize the component state
        int selectedIdx = 0
        component.setSelectedIndex( selectedIdx )
        component.ensureIndexIsVisible( selectedIdx )
        
        // Set the user interactions
        
        ProxyController c = ScriptUtils.c()
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
                            JMap bm = bookmarks[ i ]
                            Node target = map.node( bm.id )
                            if( mode == Mode.IN )
                                createLink( node, target, "${BM.gtt( 'T_created_link_in_NBM' )} \"$bm.name\"", mode )
                            else
                                createLink( node, target, "${BM.gtt( 'T_created_link_to_NBM' )} \"$bm.name\"", mode )
                            gui.dispose()
                        }
                    }
                    else if( idx > 32 && idx != 127 && idx < 256 )
                        {
                        // Try to create the link with the corresponding node id
                        String s = String.valueOf( chr )
                        JMap bm = bookmarks.find{ it.name == s }
                        if( bm )
                            {
                            Node target = map.node( bm.id )
                            if( mode == Mode.IN )
                                createLink( node, target, "${BM.gtt( 'T_created_link_in_NBM' )} \"${s}\"", mode )
                            else
                                createLink( node, target, "${BM.gtt( 'T_created_link_to_NBM' )} \"${s}\"", mode )
                            gui.dispose()
                        }
                        else
                            {
                            Utils.setStatusInfo(
                                "${BM.gtt( 'T_no_node_with_key' )} \"${chr}\"",
                                "messagebox_warning"
                            )
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
                        JMap bm = bookmarks[ idx ]
                        Node target = map.node( bm.id )
                        String t = mode == Mode.IN ? 'T_created_link_in_NBM' : 'T_created_link_to_NBM'
                        createLink( node, target, "${BM.gtt(t)} \"$bm.name\"", mode )
                        gui.dispose()
                    }
                }
            }
        )
        
        return component
    }

    // Create a JPanel for the named bookmarks
    static private JPanel getNamedBookmarksJPanel(
        SwingBuilder swing,
        List bookmarks,                 // List of the bookmarks
        SwingList bookmarksSwingList,   // Component that display this list
        boolean showTabTip,             // Do we need to add a comment about the Tab key ?
        Mode mode
    ){
        if( ! bookmarks ) return null
        String t1 = mode == Mode.IN ? 'T_create_link_in' : 'T_create_link_to'
        String t2 = mode == Mode.IN ? 'T_select_node_to_link_in' : 'T_select_BM_to_link_to'
        String t3 = mode == Mode.IN ? 'T_tip_create_link_in_NBM' : 'T_tip_create_link_to_NBM'
        
        // List all the named bookmarks
        JPanel jPane
        swing.build{
            jPane = swing.panel(
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
                    "${BM.gtt(t1)}.",
                    constraints: gbc( gridx:0, gridy:0, anchor:GridBagConstraints.LINE_START )
                )

                // Row 1
                label(
                    "${BM.gtt(t2)}.",
                    constraints: gbc( gridx:0, gridy:1, anchor:GridBagConstraints.LINE_START )
                )
                label(
                    icon: BM.getQuestionMarkIcon(),
                    toolTipText:
                    """<html>
                        ${BM.gtt(t3)}:
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
                    widget( bookmarksSwingList )
                }
            }
        }
        
        return jPane
    }

    // Build a GUI list component that display the anonymous bookmarks and
    // that allow to use them.
    static private SwingList getAnonymousBookmarksSwingList(
        SwingBuilder swing,
        ProxyNode node, MindMap map,
        List bookmarks, // List of the bookmarks
        Mode mode
    ){
        if( ! bookmarks ) return null

        // Create the component
        SwingList component
        swing.build{
            component = list(
                items: bookmarks.collect{ it.text },
                visibleRowCount: Integer.min( 20, bookmarks.size() )
            )
        }

        // Initialize the component state
        int selectedIdx = 0
        component.setSelectedIndex( selectedIdx )
        component.ensureIndexIsVisible( selectedIdx )

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
                            JMap bm = bookmarks[ idx ]
                            Node target = map.node( bm.id )
                            String t = mode == Mode.IN ? 'T_created_link_in_SBM' : 'T_created_link_to_SBM'
                            createLink( node, target, BM.gtt(t), mode )
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
                        JMap bm = bookmarks[ idx ]
                        Node target = map.node( bm.id )
                        String t = mode == Mode.IN ? 'T_created_link_in_SBM' : 'T_created_link_to_SBM'
                        createLink( node, target, BM.gtt(t), mode )
                        gui.dispose()
                    }
                }
            }
        )

        return component
    }

    // Create a JPanel for the anonymous bookmarks
    static private JPanel getAnonymousBookmarksJPanel(
        SwingBuilder swing,
        List bookmarks,                 // List of the bookmarks
        SwingList bookmarksSwingList,   // Component that display this list
        boolean showTabTip,             // Do we need to add a comment about the Tab key ?
        Mode mode
    ){
        if( ! bookmarks ) return null
        String t1 = mode == Mode.IN ? 'T_create_link_in' : 'T_create_link_to'
        String t2 = mode == Mode.IN ? 'T_select_BM_to_link_in' : 'T_select_BM_to_link_to'
        String t3 = mode == Mode.IN ? 'T_tip_create_link_in_SBM' : 'T_tip_create_link_to_SBM'
        
        // List all the named bookmarks
        JPanel jPane
        swing.build{
            jPane = swing.panel(
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
                    "${BM.gtt(t1)}.",
                    constraints: gbc( gridx:0, gridy:0, anchor:GridBagConstraints.LINE_START )
                )
                
                // Row 1
                label(
                    "${BM.gtt(t2)}.",
                    constraints: gbc( gridx:0, gridy:1, anchor:GridBagConstraints.LINE_START )
                )
                label(
                    icon: BM.getQuestionMarkIcon(),
                    toolTipText:
                    """<html>
                        ${BM.gtt(t3)}:
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
                    widget( bookmarksSwingList )
                }
            }
        }

        return jPane
    }

    // Create the content of the window
    static private Object createGui(
        SwingBuilder swing,
        JPanel namedPanel, SwingList namedSwingList,
        JPanel anonymPanel, SwingList anonymSwingList,
        Mode mode
    )
    {
        String memorizedNodeID = ((MLinkController)(LinkController.getController())).getAnchorID()
        String t1 = mode == Mode.IN ? 'T_create_link_in_win_title' : 'T_create_link_to_win_title'
        String t2 = mode == Mode.IN ? 'T_space_to_link_in_memorized' : 'T_space_to_link_to_memorized'
        
        Object gui
        swing.build{
            gui = dialog(
                title: BM.gtt(t1),
                modal:true,
                owner: ui.currentFrame,
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
                        if( memorizedNodeID ) label( text: "<html>${BM.gtt(t2)}.</html>" )
                        label( text: "<html>${BM.gtt( 'T_press_esc_cancel' )}.</html>" )
                    }
                }
            }
        }

        // Set Esc key to close the script
        String onEscPressID = "onEscPress"
        InputMap inputMap = gui
            .getRootPane()
            .getInputMap( JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT )
        inputMap.put( KeyStroke.getKeyStroke( KeyEvent.VK_ESCAPE, 0 ), onEscPressID )
        gui.getRootPane().getActionMap().put(
            onEscPressID,
            new AbstractAction()
            {
                @Override
                public void actionPerformed( ActionEvent e )
                {
	                gui.dispose()
                    Utils.setStatusInfo(
                        BM.gtt( 'T_link_aborded' ),
                        'button_cancel'
                    )
	            }
            }
        )

        if( memorizedNodeID )
            { 
            // Set Space key to create the link to the standard freeplane memorized node
            String onSpacePressID = "onSpacePress"
            KeyStroke spaceStroke = KeyStroke.getKeyStroke( KeyEvent.VK_SPACE, 0 )
            inputMap.put( spaceStroke, onSpacePressID )
            gui.getRootPane().getActionMap().put(
                onSpacePressID,
                new AbstractAction()
                {
	                @Override
	                public void actionPerformed( ActionEvent e )
                    {
                        String item = mode == Mode.IN ? 'MakeLinkFromAnchorAction' : 'MakeLinkToAnchorAction'
	                    MenuUtils.executeMenuItems( [ item ] )
                        gui.dispose()
	                }
                }
            )
            if( namedSwingList ) namedSwingList.getInputMap().put( spaceStroke, "none" )
            if( anonymSwingList ) anonymSwingList.getInputMap().put( spaceStroke, "none" )
        }

        if( namedSwingList )
            {
            if( anonymPanel ) anonymPanel.visible = false
            namedSwingList.requestFocus()
        }
        else if( anonymSwingList )
            {
            if( namedPanel ) namedPanel.visible = false
            anonymSwingList.requestFocus()
        }
        
        // Set the Tab key to switch between the 2 bookmarks types
        if( namedPanel && namedSwingList && anonymPanel && anonymSwingList )
            {
            namedSwingList.setFocusTraversalKeysEnabled( false )
            anonymSwingList.setFocusTraversalKeysEnabled( false )
            String onTabPressID = "onTabPress"
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
                            anonymSwingList.requestFocus()
                        }
                        else
                            {
                            namedPanel.visible = true
                            anonymPanel.visible = false
                            namedSwingList.requestFocus()
                        }                
                        gui.pack()
                        gui.setLocationRelativeTo( ui.currentFrame )
                    }
                }
            )
        }
        return gui
    }

    // Create the window
    static private Object createWindow( 
        SwingBuilder swing,
        ProxyNode node, MindMap map,
        List namedBookmarks, List anonymousBookmarks,
        Mode mode
    )
    {
        SwingList namedBookmarksSwingList = getNamedBookmarksSwingList(
            swing, node, map, namedBookmarks, mode
        )
        JPanel namedBookmarksJPanel = getNamedBookmarksJPanel(
            swing, namedBookmarks, namedBookmarksSwingList, (boolean)anonymousBookmarks, mode
        )
        SwingList anonymousBookmarksSwingList = getAnonymousBookmarksSwingList(
            swing, node, map, anonymousBookmarks, mode
        )
        JPanel anonymousBookmarksJPanel = getAnonymousBookmarksJPanel(
            swing, anonymousBookmarks, anonymousBookmarksSwingList, (boolean)namedBookmarks, mode
        )
        Object gui = createGui(
            swing,
            namedBookmarksJPanel, namedBookmarksSwingList,
            anonymousBookmarksJPanel, anonymousBookmarksSwingList,
            mode
        )
        return gui
    }
}
