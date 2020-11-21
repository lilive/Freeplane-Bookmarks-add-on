package lilive.bookmarks

import lilive.bookmarks.Bookmarks as BM
import groovy.swing.SwingBuilder
import java.awt.BorderLayout
import java.awt.Component
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
import org.freeplane.plugin.script.proxy.Proxy.Controller as ProxyController
import org.freeplane.plugin.script.proxy.Proxy.Node as ProxyNode
import org.freeplane.plugin.script.proxy.ScriptUtils


/**
 * Display a window for jumping to an existing bookmark
 */
public class JumpGUI
{
    static private Object gui

    // Build and display the window
    static public void show( ProxyNode node )
    {
        MindMap map = node.map
        gui = null
        SwingBuilder swing = new SwingBuilder()

        // Find all the bookmarks
        List namedBookmarks = BM.getAllNameBookmarkedNodes( map )
        List anonymousBookmarks = BM.getAllAnonymousBookmarkedNodes( map )

        // Quit the script if there is no bookmarks
        if( ! namedBookmarks && ! anonymousBookmarks )
        {
            ui.informationMessage( ui.frame, BM.gtt( 'T_no_bookmarks' ) + " !", BM.gtt( 'T_BM_win_title' ) )
            return
        }

        // Create the gui
        gui = createWindow(
            swing, node, map,
            namedBookmarks, anonymousBookmarks
        )
        
        // Center the gui over the freeplane window
        gui.pack()
        gui.setLocationRelativeTo( ui.frame )
        gui.visible = true
    }

    // Jump to a node after the gui close
    static private void jumpToNodeAfterGuiDispose( ProxyNode target, String message )
    {
        // If the code to jump to a node is executed before the gui close,
        // it leave freeplane in a bad focus state.
        // This is solved by putting this code in a listener executed
        // after the gui destruction:

        ProxyController c = ScriptUtils.c()
        
        gui.addWindowListener(
            new WindowAdapter()
            {
                @Override
                public void windowClosed( WindowEvent event )
                {
                    c.select( target )
                    c.centerOnNode( target )
                    c.setStatusInfo( 'standard', message )
                }
            }
        )
    }

    // Build a GUI list component that display the named bookmarks and
    // that allow to jump to them.
    static private SwingList getNamedBookmarksSwingList(
        SwingBuilder swing,
        ProxyNode node, MindMap map,
        List bookmarks // List of the bookmarks
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
        int selectedIdx = bookmarks.findIndexOf{ it.id == node.id }
        if( selectedIdx < 0 ) selectedIdx = 0;
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
                            jumpToNodeAfterGuiDispose( target, "${BM.gtt( 'T_jumped_to_NBM' )} \"$bm.name\"" )
                            gui.dispose()
                        }
                    }
                    else if( idx > 32 && idx != 127 && idx < 256 )
                        {
                        // Try to jump to the corresponding node id
                        String s = String.valueOf( chr )
                        JMap bm = bookmarks.find{ it.name == s }
                        if( bm )
                        {
                            Node target = map.node( bm.id )
                            jumpToNodeAfterGuiDispose( target, "${BM.gtt( 'T_jumped_to_NBM' )} \"${s}\"" )
                            gui.dispose()
                        }
                        else
                        {
                            c.setStatusInfo( 'standard', "${BM.gtt( 'T_no_node_with_key' )} \"${chr}\"", "messagebox_warning" )
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
                        jumpToNodeAfterGuiDispose( target, "${BM.gtt( 'T_jump_to_NBM' )} \"$bm.name\"" )
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
        boolean showTabTip              // Do we need to add a comment about the Tab key ?
    ){
        if( ! bookmarks ) return null
        
        // List all the named bookmarks
        JPanel jPane
        swing.build{
            jPane = swing.panel(
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
                    "${BM.gtt( 'T_select_NBM_to_jump' )}.",
                    constraints: gbc( gridx:0, gridy:0, anchor:GridBagConstraints.LINE_START )
                )
                label(
                    icon: BM.getQuestionMarkIcon(),
                    toolTipText:
                    """<html>
                        ${ BM.gtt( 'T_tip_jump_to_NBM' )}:
                        <ul>
                            <li>${BM.gtt( 'T_press_red_key' )}</li>
                            <li>${BM.gtt( 'T_click_BM' )}</li>
                            <li>${BM.gtt( 'T_arrow_select' )}</li>
                        </ul>
                    </html>""",
                    constraints: gbc( gridx:1, gridy:0, anchor:GridBagConstraints.LINE_START, weightx:1, insets:[0,10,0,0] )
                )

                // Row 1
                if( showTabTip ) label(
                    "<html>${BM.gtt( 'T_tab_to_display_SBM' )}.</html>",
                    constraints: gbc( gridx:0, gridy:1, gridwidth:2, anchor:GridBagConstraints.LINE_START )
                )

                // Row 2
                scrollPane(
                    border: emptyBorder( [10,0,0,0] ),
                    constraints: gbc( gridx:0, gridy:2, gridwidth:2, fill:GridBagConstraints.HORIZONTAL, anchor:GridBagConstraints.LINE_START )
                )
                {
                    widget( bookmarksSwingList )
                }
            }
        }
        
        return jPane
    }

    // Build a GUI list component that display the anonymous bookmarks and
    // that allow to jump to them.
    static private SwingList getAnonymousBookmarksSwingList(
        SwingBuilder swing,
        ProxyNode node, MindMap map,
        List bookmarks // List of the bookmarks
    ){
        if( ! bookmarks ) return null

        ArrayList< String > labels = [] // Label for each row
        ArrayList< String > ids = []    // Node id for each row
        // Create labels and ids
        bookmarks.each{
            bm ->
            
            String label
            // Bold for the currently selected node
            if( node.id == bm.id ) label = "<html><b>$bm.text</b></html>"
            else label = bm.text

            // Do we have clones ?
            Node n = map.node( bm.id )
            List clones = n.getNodesSharingContent()
            if( clones ){
                // Does any other clone already inserted in the list ?
                List i = ids.intersect( clones.collect{ it.id } )
                if( i ){
                    // Mark this node as a clone in the list
                    List indices = i.collect{ id1 -> ids.findIndexOf( id2 -> id1 == id2 ) }
                    int index = indices.max()
                    labels.addAll( index + 1, [ "         [clone] ${bm.text}" ] )
                    ids.addAll( index + 1, [ bm.id ] )
                }
                else {
                    // Insert in the list the text of this first clone
                    labels << label
                    ids << bm.id
                }
            } else {
                // Insert the text of this node in the list
                labels << label
                ids << bm.id
            }
        }
        
        // Create the component
        SwingList component = swing.list(
            items: labels,
            visibleRowCount: Integer.min( 20, labels.size() )
        )

        // Initialize the component state
        int selectedIdx = ids.findIndexOf{ it == node.id }
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
                    if( code == KeyEvent.VK_ENTER )
                    {
                        int idx = component.getSelectedIndex()
                        if( idx >= 0 )
                        {
                            Node target = map.node( ids[ idx ] )
                            jumpToNodeAfterGuiDispose( target, BM.gtt( 'T_jumped_to_SBM' ) )
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
                        Node target = map.node( ids[ idx ] )
                        jumpToNodeAfterGuiDispose( target, BM.gtt( 'T_jumped_to_SBM' ) )
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
        boolean showTabTip              // Do we need to add a comment about the Tab key ?
    ){
        if( ! bookmarks ) return null
        
        // List all the named bookmarks
        JPanel jPane
        swing.build{
            jPane = swing.panel(
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
                    "${BM.gtt( 'T_select_BM_to_jump' )}.",
                    constraints: gbc( gridx:0, gridy:0, anchor:GridBagConstraints.LINE_START )
                )
                label(
                    icon: BM.getQuestionMarkIcon(),
                    toolTipText:
                    """<html>
                        ${BM.gtt( 'T_tip_jump_to_SBM' )}:
                        <ul>
                            <li>${BM.gtt( 'T_click_BM' )}</li>
                            <li>${BM.gtt( 'T_arrow_select' )}</li>
                        </ul>
                    </html>""",
                    constraints: gbc( gridx:1, gridy:0, anchor:GridBagConstraints.LINE_START, weightx:1, insets:[0,10,0,0] )
                )
                
                // Row 1
                if( showTabTip ) label(
                    "<html>${BM.gtt( 'T_tab_to_display_NBM' )}.</html>",
                    constraints: gbc( gridx:0, gridy:1, gridwidth:2, anchor:GridBagConstraints.LINE_START )
                )
                
                // Row 2
                scrollPane(
                    border: emptyBorder( [10,0,0,0] ),
                    constraints: gbc( gridx:0, gridy:2, gridwidth:2, fill:GridBagConstraints.HORIZONTAL, anchor:GridBagConstraints.LINE_START )
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
        JPanel anonymPanel, SwingList anonymSwingList
    )
    {
        ProxyController c = ScriptUtils.c()
        Object gui
        swing.build{
            gui = dialog(
                title: BM.gtt( 'T_jump_to_SBM' ),
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
                    label( text: "<html>${BM.gtt( 'T_press_esc_cancel' )}</html>.", constraints: BorderLayout.PAGE_END )
                }
            }
        }

        // Set Esc key to close the script
        String onEscPressID = "onEscPress"
        InputMap inputMap = gui.getRootPane().getInputMap( JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT )
        inputMap.put( KeyStroke.getKeyStroke( KeyEvent.VK_ESCAPE, 0 ), onEscPressID )
        gui.getRootPane().getActionMap().put(
            onEscPressID,
            new AbstractAction()
            {
                @Override
                public void actionPerformed( ActionEvent e )
                {
                    gui.dispose()
                    c.setStatusInfo( 'standard', BM.gtt( 'T_jump_aborded' ), 'button_cancel' )
                }
            }
        )

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
                        gui.setLocationRelativeTo( ui.frame )
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
        List namedBookmarks, List anonymousBookmarks
    )
    {
        SwingList namedBookmarksSwingList = getNamedBookmarksSwingList(
            swing, node, map, namedBookmarks
        )
        JPanel namedBookmarksJPanel = getNamedBookmarksJPanel(
            swing, namedBookmarks, namedBookmarksSwingList, (boolean)anonymousBookmarks
        )
        SwingList anonymousBookmarksSwingList = getAnonymousBookmarksSwingList(
            swing, node, map, anonymousBookmarks
        )
        JPanel anonymousBookmarksJPanel = getAnonymousBookmarksJPanel(
            swing, anonymousBookmarks, anonymousBookmarksSwingList, (boolean)namedBookmarks
        )
        Object gui = createGui(
            swing,
            namedBookmarksJPanel, namedBookmarksSwingList,
            anonymousBookmarksJPanel, anonymousBookmarksSwingList
        )
        return gui
    }
}
