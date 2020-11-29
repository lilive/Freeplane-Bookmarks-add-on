package lilive.bookmarks

import org.freeplane.features.map.INodeChangeListener
import org.freeplane.features.map.NodeChangeEvent
import org.freeplane.features.map.IMapChangeListener
import org.freeplane.features.map.NodeModel
import org.freeplane.features.mode.Controller
import org.freeplane.features.icon.factory.IconStoreFactory
import org.freeplane.features.icon.IconStore
import org.freeplane.features.icon.MindIcon
import org.freeplane.plugin.script.proxy.Convertible
import org.freeplane.plugin.script.proxy.ScriptUtils
import java.lang.StringBuilder
import java.util.Enumeration
import org.freeplane.features.map.MapController

public class ChangeListener implements INodeChangeListener, IMapChangeListener
{
	public void nodeChanged(NodeChangeEvent event)
	{
		// When an icon is added or removed form a node, we must take
		// care about the bookmarks icons :
		// - A bookmark icon must be unique in a node
		// - A bookmark icon is the first icon of a node
		// - A named-bookmark icon can be set only for a node which is
		//   referenced to be a named-bookmarked node
		// - It is forbidden to remove named-bookmark icon for a node
		//   which is referenced to be a named-bookmarked node

		Object property = event.property
		NodeModel node = event.node

		// An icon has been added or deleted
		if (NodeModel.NODE_ICON.equals(property)){

			// An icon has been deleted
			if( event.oldValue ){

				String oldIcon = event.oldValue.name
				if( oldIcon == Bookmarks.namedIcon ){
					// Prevent named bookmark icon to be removed from named bookmarked nodes,
					// and be sure it is at the first position
					if( isNodeHasNamedBookmark( node ) ) putUniqueIconAsFirstIcon( node, Bookmarks.namedIcon )
				}
			}

			// An icon has been added
			if( event.newValue ){

                String newIcon = event.newValue.name
                if( newIcon == Bookmarks.namedIcon ){
					// If this is a named bookmark icon

					// Be sure a named bookmark icon is at the first position ...
					if( isNodeHasNamedBookmark( node ) ) putUniqueIconAsFirstIcon( node, Bookmarks.namedIcon )
					// ... and that a named bookmark icon is added only to named bookmarked nodes
					else removeIcon( node, Bookmarks.namedIcon )

				} else if( newIcon == Bookmarks.anonymousIcon ){
					// If this is a regular bookmark icon

					if( isNodeHasNamedBookmark( node ) ){
						// Prevent regular bookmark icon to be placed on named bookmarked nodes
						removeIcon( node, Bookmarks.anonymousIcon )
						// Be sure the named bookmark icon is present at the first position
						putUniqueIconAsFirstIcon( node, Bookmarks.namedIcon )
					} else {
						// Be sure the bookmark icon is present at the first position, and unique
						putUniqueIconAsFirstIcon( node, Bookmarks.anonymousIcon )
					}
				}
			}
		}
	}

	public void onNodeInserted( NodeModel parent, NodeModel child, int newIndex )
	{
		// When a node is inserted, we must take care about its named-bookmark
		// icon, because it can be a copy of another node named-bookmarked.
		// But only one node can be named-bookmarked with a single keyboard key,
        // so we must remove the named-bookmark icon from the copy.

		IconStore iconStore = IconStoreFactory.ICON_STORE
		MindIcon icon = iconStore.getMindIcon( Bookmarks.namedIcon )

		def map = ScriptUtils.node().map
		StringBuilder named = new StringBuilder()
		def stored = map.storage.getAt( Bookmarks.storageKey )
		if( stored ) named = new StringBuilder( stored.getString() )

		purgeBranchFromBadNamedIcons( child, icon, named )
	}

	// Return true if a node is referenced as a name-bookmarked node
	private boolean isNodeHasNamedBookmark( NodeModel node )
	{
		def map = ScriptUtils.node().map
		String mapStorage = ""
		def stored = map.storage.getAt( Bookmarks.storageKey )
		if( stored ) mapStorage = stored.getString();
		return mapStorage.contains( '"' + node.getID() + '"' )
	}

	// Add an icon at the first position for this node
	// Ensure this icon is unique
	private void putUniqueIconAsFirstIcon( NodeModel node, String iconName )
	{
		def icons = node.icons
		int i = icons.size() - 1
		while( i > 0 ){
			MindIcon icon = icons.get( i )
			if( icon.name == iconName ) node.removeIcon( i )
			i--
		}

		if( icons.size() > 0 && icons.get( 0 ).name == iconName ) return

		IconStore iconStore = IconStoreFactory.ICON_STORE
		MindIcon iconToAdd = iconStore.getMindIcon( iconName )
		node.addIcon( iconToAdd, 0 )
	}

	// Add an icon at the first position for this node
	// Ensure this icon is unique
	private void putUniqueIconAsFirstIcon( NodeModel node, MindIcon icon )
	{
		def icons = node.icons
		int i = icons.size() - 1
		while( i > 0 ){
			MindIcon ic = icons.get( i )
			if( ic.name == icon.name ) node.removeIcon( i )
			i--
		}

		if( icons.size() > 0 && icons.get( 0 ).name == icon.name ) return
		node.addIcon( icon, 0 )
	}

	// Remove an icon from the node
	private void removeIcon( NodeModel node, String iconName )
	{
		def icons = node.icons
		int i = icons.size() - 1
		while( i >= 0 ){
			MindIcon icon = icons.get( i )
			if( icon.name == iconName ) node.removeIcon( i )
			i--
		}
	}

	// Remove an icon from the node, and fire a change event
	private void removeIcon( NodeModel node, MindIcon icon, boolean fireEvent )
	{
		def icons = node.icons
		boolean changed = false
		int i = icons.size() - 1
		while( i >= 0 ){
			MindIcon ic = icons.get( i )
			if( ic.name == icon.name ){
				node.removeIcon( i )
				changed = true
			}
			i--
		}
		if( changed && fireEvent ){
			NodeChangeEvent event = new NodeChangeEvent(
				node, NodeModel.NODE_ICON, icon, null, true, true
			)
			node.fireNodeChanged( event )
		}
	}

	// Remove the named bookmark icon for all the nodes in this branch
	// which are not referenced as bookmarked nodes
	private void purgeBranchFromBadNamedIcons( NodeModel node, MindIcon icon, StringBuilder named )
	{
		boolean isNamed = named.indexOf( '"' + node.getID() + '"' ) >= 0
		if( ! isNamed ){
			// Remove the icon and fire the event (to refresh the view)
			removeIcon( node, icon, true )
		}
		Enumeration<NodeModel> children = node.children()
		while( children.hasMoreElements() ){
			purgeBranchFromBadNamedIcons( children.nextElement(), icon, named )
		}
	}

    // Put this listener on node change events and map change events
    static public void startMonitor()
    {
        MapController mapController = Controller.currentModeController.mapController
        boolean started = mapController.nodeChangeListeners.any{ it.class.name == ChangeListener.class.name }
        if( started ) return
        ChangeListener listener = new ChangeListener()
        mapController.addNodeChangeListener( listener )
        mapController.addMapChangeListener( listener )
    }

    // Remove listener on node change events and map change events
    static public void stopMonitor()
    {
        MapController mapController = Controller.currentModeController.mapController
        mapController.nodeChangeListeners
            .findAll{ it.class.name == ChangeListener.class.name }
            .each { mapController.removeNodeChangeListener(it) }
        mapController.mapChangeListeners
            .findAll{ it.class.name == ChangeListener.class.name }
            .each { mapController.removeMapChangeListener(it) }
    }
}

