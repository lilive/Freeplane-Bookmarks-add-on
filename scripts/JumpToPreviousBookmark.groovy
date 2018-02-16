// Jump to previous bookmark

// Return is a node is bookmarked
bookmarkIcon = "bookmark"
def isBookmarked( node )
{
    return ( node != null && node.getIcons().contains( bookmarkIcon ) )
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
    c.setStatusInfo( "standard", "Pas de marque page précédent trouvé !" )
}

