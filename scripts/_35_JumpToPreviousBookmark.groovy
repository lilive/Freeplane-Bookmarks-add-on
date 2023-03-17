// Jump to previous bookmark

import lilive.bookmarks.Bookmarks as BM

def namedBookmarks = BM.loadNamedBookmarks( node.map )

def start = node
n = node.getPrevious()
while( n != start && ! BM.isBookmarked( n, namedBookmarks ) ) n = n.getPrevious()

if( n != start && BM.isBookmarked( n, namedBookmarks ) )
{
    c.select( n  )
    c.centerOnNode( n )
}
else
{
    Utils.setStatusInfo(
        BM.gtt( 'T_no_prev_BM' ) + " !",
        'messagebox_warning'
    )
}

