// Jump to previous bookmark

anonymousIcon = "bookmarks/Bookmark 1"
namedIcon = "bookmarks/Bookmark 2"

def gtt( key )
{
    // gt = Get Translated Text
    return textUtils.getText( 'addons.bookmarks.' + key )
}

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
n = node.getPrevious()
while( n != start && !isBookmarked( n ) ) n = n.getPrevious()

if( n != start && isBookmarked( n ) )
{
    c.select( n  )
    c.centerOnNode( n )
}
else
{
    c.setStatusInfo( 'standard', gtt( 'T_no_prev_BM' ) + " !", 'messagebox_warning' )
}

