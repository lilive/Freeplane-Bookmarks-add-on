// Jump to a bookmark

// Clean the bookmarks if needed
import lilive.bookmarks.Bookmarks as BM
def namedBookmarks = BM.loadNamedBookmarks( node.map )
namedBookmarks = BM.fixNamedBookmarksInconsistency( namedBookmarks, node.map )

// Display the jump window
bookmarks.JumpGUI.show( node )
