// Jump to next bookmark

// Return is a node is bookmarked
bookmarkIcon = "bookmark"
def isBookmarked( node )
{
    return ( node != null && node.getIcons().contains( bookmarkIcon ) )
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
    c.setStatusInfo( "standard", "Pas de marque page suivant trouvé !" )
}
