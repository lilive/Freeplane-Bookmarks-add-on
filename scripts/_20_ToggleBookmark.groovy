// Switch between named bookmark / anonymous bookmark / no bookmark for this node

import lilive.bookmarks.Bookmarks as BM

def namedBookmarks = BM.loadNamedBookmarks( node.map )
namedBookmarks = BM.fixNamedBookmarksInconsistency( namedBookmarks, node.map )
def isAnonymousBookmark = BM.isAnonymousBookmarked( node )
def isNamedBookmark = BM.isNamedBookmarked( node, namedBookmarks )

if( isNamedBookmark )
{
    // Remove the named bookmark and put a regular bookmark instead
    BM.deleteNamedBookmark( node, namedBookmarks )
    BM.createAnonymousBookmark( node )
    c.setStatusInfo( 'standard', BM.gtt( 'T_node_now_SBM' ), 'button_ok' )
}
else if( isAnonymousBookmark )
    {
    // Remove the named bookmark
    BM.deleteAnonymousBookmark( node )
    c.setStatusInfo( 'standard', BM.gtt( 'T_node_no_BM_anymore' ), 'button_cancel' )
}
else
    {
    // Create a regular bookmark
    BM.createAnonymousBookmark( node )
    c.setStatusInfo( 'standard', BM.gtt( 'T_node_now_SBM' ), 'button_ok' )
}

