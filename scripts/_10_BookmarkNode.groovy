// Create a bookmark (or delete an existing one) in this node

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
import bookmarks.Bookmarks as BM

def namedBookmarks = BM.loadNamedBookmarks( node.map )
namedBookmarks = BM.fixNamedBookmarksInconsistency( namedBookmarks, node.map )
def isAnonymousBookmark = BM.isAnonymousBookmarked( node )
def isNamedBookmark = BM.isNamedBookmarked( node, namedBookmarks )

// Create the graphical user interface to display
def gui
groovy.swing.SwingBuilder.build
{
    gui = dialog(
        title: BM.gtt( 'T_BM_win_title' ),
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

// Center the gui over the freeplane window
gui.setLocationRelativeTo( ui.frame )
gui.visible = true

