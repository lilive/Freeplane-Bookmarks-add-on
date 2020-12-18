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
import javax.swing.ButtonGroup
import org.freeplane.api.MindMap as MindMap
import org.freeplane.api.Node as Node
import org.freeplane.core.ui.components.UITools as ui
import org.freeplane.features.link.LinkController
import org.freeplane.features.link.mindmapmode.MLinkController
import org.freeplane.plugin.script.proxy.Proxy.Controller as ProxyController
import org.freeplane.plugin.script.proxy.Proxy.Node as ProxyNode
import org.freeplane.plugin.script.proxy.ScriptUtils
import org.freeplane.core.util.MenuUtils
import javax.swing.JRadioButton
import java.awt.KeyboardFocusManager
import javax.swing.JOptionPane

/**
 * Display a window that allow to delete all bookmarks in a branch
 */
public class ClearBranchGUI
{
    static private Object gui

    // Build and display the window
    static public void show( ProxyNode node )
    {
        MindMap map = node.map
        SwingBuilder swing = new SwingBuilder()

        // Create the gui
        gui = createWindow( swing, node, map )

        // Center the gui over the freeplane window
        gui.pack()
        gui.setLocationRelativeTo( ui.frame )
        gui.visible = true
    }

    // Create the window
    static private Object createWindow( 
        SwingBuilder swing,
        ProxyNode node, MindMap map
    )
    {
        JRadioButton allBookmarksBtn
        JRadioButton anonymousBookmarksBtn
        JRadioButton namedBookmarksBtn
        Object gui = swing.dialog(
            title: BM.gtt( 'T_clear_branch_win_title' ),
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
                gridLayout( rows:5, vgap:10 )
                label( BM.gtt( 'T_clear_branch_message' ) )
                button(
                    text: BM.gtt( 'T_clear_branch_all_BMS' ),
                    actionPerformed: {
                        BM.deleteSubTreeBookmarks( node, true, true )
                        gui.dispose()
                        ui.informationMessage(
                            ui.frame,
                            BM.gtt( 'T_clear_branch_all_BMs_deleted' ),
                            BM.gtt( 'T_clear_branch_win_title' ),
                            JOptionPane.INFORMATION_MESSAGE
                        )
                    }
                )
                button(
                    text: BM.gtt( 'T_clear_branch_only_SBMs' ),
                    actionPerformed: {
                        BM.deleteSubTreeBookmarks( node, true, false )
                        gui.dispose()
                        ui.informationMessage(
                            ui.frame,
                            BM.gtt( 'T_clear_branch_only_SBMs_deleted' ),
                            BM.gtt( 'T_clear_branch_win_title' ),
                            JOptionPane.INFORMATION_MESSAGE
                        )
                    }
                )
                button(
                    text: BM.gtt( 'T_clear_branch_only_NBMs' ),
                    actionPerformed: {
                        BM.deleteSubTreeBookmarks( node, false, true )
                        gui.dispose()
                        ui.informationMessage(
                            ui.frame,
                            BM.gtt( 'T_clear_branch_only_NBMs_deleted' ),
                            BM.gtt( 'T_clear_branch_win_title' ),
                            JOptionPane.INFORMATION_MESSAGE
                        )
                    }
                )
                button(
                    text: BM.gtt( 'T_clear_branch_cancel' ),
                    actionPerformed: {
                        gui.dispose()
                        ui.informationMessage(
                            ui.frame,
                            BM.gtt( 'T_clear_branch_canceled' ),
                            BM.gtt( 'T_clear_branch_win_title' ),
                            JOptionPane.ERROR_MESSAGE
                        )
                    }
                )
            }
        }
        
        // Set Esc key to close the gui
        String onEscPressID = "onEscPress"
        InputMap inputMap = gui.getRootPane().getInputMap( JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT )
        inputMap.put( KeyStroke.getKeyStroke( KeyEvent.VK_ESCAPE, 0 ), onEscPressID )
        gui.getRootPane().getActionMap().put(
            onEscPressID,
            new AbstractAction(){
                @Override
                public void actionPerformed( ActionEvent e ){ gui.dispose() }
            }
        )
        
        return gui
    }
}
