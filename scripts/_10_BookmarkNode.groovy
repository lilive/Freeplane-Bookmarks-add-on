// Create a bookmark (or delete an existing one) in this node

// Clean the bookmarks if needed
import lilive.bookmarks.Bookmarks as BM
def namedBookmarks = BM.loadNamedBookmarks( node.map )
namedBookmarks = BM.fixNamedBookmarksInconsistency( namedBookmarks, node.map )

// Display the dialog window
lilive.bookmarks.CreateOrDeleteGUI.show( node, namedBookmarks )
