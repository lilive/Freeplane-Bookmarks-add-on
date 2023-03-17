// Jump to next bookmark

import lilive.bookmarks.Bookmarks as BM

def namedBookmarks = BM.loadNamedBookmarks( node.map )

def start = node
n = node.getNext()
while( n != start && ! BM.isBookmarked( n, namedBookmarks ) ) n = n.getNext()

if( n != start && BM.isBookmarked( n, namedBookmarks ) )
{
    c.select( n  )
    c.centerOnNode( n )
}
else
{
    Utils.setStatusInfo(
        BM.gtt( 'T_no_next_BM' ) + " !",
        'messagebox_warning'
    )
}
