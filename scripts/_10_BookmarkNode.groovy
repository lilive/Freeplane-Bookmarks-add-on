// Create a bookmark (or delete an existing one) in this node

import lilive.bookmarks.Bookmarks
import lilive.bookmarks.CreateOrDeleteGUI

// Clean the bookmarks if needed
def namedBookmarks = Bookmarks.loadNamedBookmarks( node.map )
namedBookmarks = Bookmarks.fixNamedBookmarksInconsistency( namedBookmarks, node.map )

// Display the dialog window
CreateOrDeleteGUI.show( node, namedBookmarks )
