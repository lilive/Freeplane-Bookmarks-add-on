package bookmarks

import bookmarks.Bookmarks as BM
import groovy.swing.SwingBuilder
import java.awt.BorderLayout
import java.awt.Color
import java.awt.event.*
import java.util.Map as JMap
import javax.swing.BoxLayout
import javax.swing.JFrame
import org.freeplane.api.MindMap
import org.freeplane.core.ui.components.UITools
import org.freeplane.plugin.script.proxy.Proxy.Controller as ProxyController
import org.freeplane.plugin.script.proxy.Proxy.Node as ProxyNode
import org.freeplane.plugin.script.proxy.ScriptUtils

/**
 * Display a window for bookmark creation or deletion
 * Usage: CreateOrDeleteGUI.show()
 */
public class CreateOrDeleteGUI
{
    public static void show( ProxyNode node, JMap namedBookmarks )
    {
        // Collect informations about the current node
        Boolean isAnonymousBookmark = BM.isAnonymousBookmarked( node )
        Boolean isNamedBookmark = BM.isNamedBookmarked( node, namedBookmarks )

        // Buid the GUI
        Object gui = createWindow( node, isAnonymousBookmark, isNamedBookmark, namedBookmarks )
        addKeyListener( gui, node, isAnonymousBookmark, isNamedBookmark, namedBookmarks )

        // Center the GUI over the freeplane window
        gui.setLocationRelativeTo( UITools.frame )
        gui.visible = true
    }

    // Create the graphical user interface to display
    private static Object createWindow(
        ProxyNode node,
        Boolean isAnonymousBookmark, Boolean isNamedBookmark,
        JMap namedBookmarks
    )
    {
        MindMap map = node.map
        Object gui
        groovy.swing.SwingBuilder.build
        {
            gui = dialog(
                title: BM.gtt( 'T_BM_win_title' ),
                modal: true,
                owner: UITools.frame,
                defaultCloseOperation: JFrame.DISPOSE_ON_CLOSE,
                pack: true
            ){
                borderLayout()
                Color blue = new Color( 0, 0, 255 )
                
                // Fist panel : instructions
                panel(
                    border: emptyBorder( 10 ),
                    constraints: BorderLayout.PAGE_START,
                ){
                    boxLayout(axis: BoxLayout.Y_AXIS )
                    if( isAnonymousBookmark )
                    {
                        label(
                            text: BM.gtt( 'T_node_already_BM' ),
                            foreground: blue,
                            border: emptyBorder( 0, 0, 5, 0 )
                        )
                        label( "<html><b>${ BM.gtt( 'T_kbd_backspace' ) }</b> : ${ BM.gtt( 'T_delete_BM' ) }</html>" )
                        label( "<html><b>${ BM.gtt( 'T_kbd_other' ) }</b> : ${ BM.gtt( 'T_replace_by_NBM' ) }</html>" )
                    }
                    else if( isNamedBookmark )
                    {
                        label(
                            text: BM.gtt( 'T_node_already_NBM' ),
                            foreground: blue,
                            border: emptyBorder( 0, 0, 5, 0 )
                        )
                        label( "<html><b>${ BM.gtt( 'T_kbd_backspace' ) }</b> : ${ BM.gtt( 'T_delete_BM' ) }</html>" )
                        label( "<html><b>${ BM.gtt( 'T_kbd_space' ) }</b> : ${ BM.gtt( 'T_replace_by_SBM' ) }</html>" )
                        label( "<html><b>${ BM.gtt( 'T_kbd_other' ) }</b> : ${ BM.gtt( 'T_change_NBM_name' ) }</html>" )
                    }
                    else
                    {
                        label( "<html><b>${ BM.gtt( 'T_kbd_space' ) }</b> : ${ BM.gtt( 'T_create_SBM' ) }</html>" )
                        label( "<html><b>${ BM.gtt( 'T_kbd_other' ) }</b> : ${ BM.gtt( 'T_create_NBM' ) }</html>" )
                    }
                    label( "<html><b>${ BM.gtt( 'T_kbd_esc' ) }</b> ${ BM.gtt( 'T_to_cancel' ) }</html>" )
                }

                // Second panel : only if named bookmarks exists
                if( namedBookmarks.size() > 0 )
                {
                    panel(
                        border:titledBorder( BM.gtt( 'T_NBM_are' ) ),
                        constraints: BorderLayout.CENTER
                    ){
                        boxLayout( axis: BoxLayout.Y_AXIS )
                        namedBookmarks.each
                        {
                            key, id ->
                            // Display a line of text with the associated key
                            // and the 30 first caracters of the node
                            ProxyNode target = map.node( id )
                            String text =
                                "<html><font color='red'><b>" +
                                String.valueOf( (char) Integer.parseInt( key ) ) +
                                "</b></font> : " +
                                BM.getNodeShortPlainText( target ) +
                                "</html>"
                            if( id == node.id ) label( text: text, foreground: blue )
                            else label( text )
                        }
                    }
                }
            }
        }
        return gui
    }

    // Create the response to the user key press
    private static void addKeyListener(
        Object gui,
        ProxyNode node,
        Boolean isAnonymousBookmark, Boolean isNamedBookmark,
        JMap namedBookmarks
    )
    {
        ProxyController c = ScriptUtils.c()
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
                            namedBookmarks = BM.deleteNamedBookmark( node, namedBookmarks )
                            gui.dispose()
                            c.setStatusInfo( 'standard', BM.gtt( 'T_node_no_BM_anymore' ), 'button_cancel' )
                        }
                        else if( isAnonymousBookmark )
                        {
                            BM.deleteAnonymousBookmark( node )
                            gui.dispose()
                            c.setStatusInfo( 'standard', BM.gtt( 'T_node_no_BM_anymore' ), 'button_cancel' )
                        }
                    }
                    else if( keyCharCode == 32 )
                    {
                        if( isNamedBookmark )
                        {
                            namedBookmarks = BM.deleteNamedBookmark( node, namedBookmarks )
                            namedBookmarks = BM.createAnonymousBookmark( node )
                            gui.dispose()
                            c.setStatusInfo( 'standard', BM.gtt( 'T_node_now_SBM' ), 'button_ok' )
                        }
                        else if( ! isAnonymousBookmark )
                        {
                            BM.createAnonymousBookmark( node )
                            gui.dispose()
                            c.setStatusInfo( 'standard', BM.gtt( 'T_node_now_SBM' ), 'button_ok' )
                        }
                    }
                    else if( keyCharCode > 32 && keyCharCode != 127 && keyCharCode < 256 )
                    {
                        if( isAnonymousBookmark ) BM.deleteAnonymousBookmark( node )
                        namedBookmarks = BM.createNamedBookmark( node, keyCharCode, namedBookmarks )
                        gui.dispose()
                        c.setStatusInfo( 'standard', BM.gtt( 'T_node_now_NBM' ) + '"' + chr + "'", 'button_ok' )
                    }
                    else if( keyCharCode == 27 )
                    {
                        c.setStatusInfo( 'standard', BM.gtt( 'T_BM_operation_aborded' ), 'messagebox_warning' )
                        gui.dispose()
                    }
                }
            }
        )
    }
}
