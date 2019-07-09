// Jump to next bookmark

anonymousIcon = "bookmarks/Bookmark 1"
namedIcon = "bookmarks/Bookmark 2"

// Return is a node is bookmarked
def isBookmarked( node )
{
    if( node == null ) return false
    return (
        node.icons.contains( anonymousIcon )
        || node.icons.contains( namedIcon )
    )
}

def start = node
n = node.getNext()
while( n != start && !isBookmarked( n ) ) n = n.getNext()

if( n != start && isBookmarked( n ) )
{
    c.select( n  )
    c.centerOnNode( n )
}
else
{
    c.setStatusInfo( 'standard', textUtils.getText( 'addons.bookmarks.T_no_next_BM' ) + " !", 'messagebox_warning' )
}
