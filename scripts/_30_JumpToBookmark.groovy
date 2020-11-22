// Jump to a bookmark

import lilive.bookmarks.Bookmarks
import lilive.bookmarks.JumpGUI

// Clean the bookmarks if needed
def namedBookmarks = Bookmarks.loadNamedBookmarks( node.map )
namedBookmarks = Bookmarks.fixNamedBookmarksInconsistency( namedBookmarks, node.map )

// Display the jump window
JumpGUI.show( node, config )
