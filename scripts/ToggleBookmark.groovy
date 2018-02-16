bookmarkIcon = "bookmark"
def icons = node.getIcons()

if( icons.contains( bookmarkIcon ) )
{
    icons.remove( bookmarkIcon )
}
else
{
    icons.add( bookmarkIcon )
}
