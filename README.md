# ![logo](doc/images/bookmark.png) Bookmarks for Freeplane

[Voir en français](README-fr.md) ![french flag](doc/images/french_flag_small.png)

This add-on allows to bookmark some nodes in a Freeplane map, and to easily navigate between them.

There are two kinds of bookmarks :

- **Standard bookmarks**, flagged by the purple bookmark icon.
- **Named bookmarks** (binded to a keyboard shortcut), flagged by the green bookmark icon.

![demo](doc/images/demo.gif)

This add-on create a new "Bookmarks" entry in the menu bar, which give access to the differents actions :

- **Add / Remove a bookmark** : Add a bookmark to the selected node, or remove it. Standard and named bookmarks can be defined here.
- **Jump to bookmark** : Reach a bookmarked node.
- **Toggle the bookmark** : Depending on the selected note state :
  - convert a named bookmark to a standard bookmark
  - delete a standard bookmark
  - create a standard bookmark

Apart form these 3 main actions, it is also possible to create links *to* or *from* the bookmarked nodes.
  
## Feedback and contributions

Any feedback will be very appreciated and will help to improve this add-on :smile:
- You can post messages on [the dedicated discussion](https://sourceforge.net/p/freeplane/discussion/758437/thread/ec280c4e/) in the Freeplane forum.
- You can also submit issues or make feature requests [here](../../issues).

Pull requests are also very welcome. The code is documented. Do not hesitate to ask for any guidance, if needed.

## Installation

- First, open the Freeplane preferences window (menu `Tools > Preferences`). Go to the tab `Plugins`, section `Scripting`, and set "script execution enabled" to "Yes".
- Download the *bookmarks-vX.X.X.addon.mm* file from [the latest release](../../releases).
- Open this file with Freeplane and follow the instructions.

## Update

To update the add-on, just install its newer version. No need to uninstall it first.

**Important :** If you have created bookmarks with a version of this add-on prior to v0.5.1, you have to update your maps that contain bookmarks. Use the *"Boomarks>Tools>Update map with bookmarks defined by a previous version of the addon"* feature.

## Disclaimer

I use this addon-on for myself since a year on a daily basis, and I've never got any problems. Up to the 29 June 2020, the add-on has been downloaded 60 times (all versions together) and no one has reported problems. I think it's ok, but still I prefer to warn: use this add-on at your own risks.

If you want more informations consider these 2 points :

- The add-on modify a little bit how Freeplane works. It introduce a monitoring function that prevent the user to add a named bookmark icon the regular way. This is needed to ensure the named bookmarks consistency. It also prevent a named bookmark to be copied when a node with a named bookmark is copied, because a named bookmark must be unique. In this case, the node is copied without its bookmark.  
Then, each time a icon is modified by the user, or each time a node is created, an extra piece of code is executed for this monitoring. This *may* have some negative side effects.

- The add-on write the datas about the named bookmarks within the map storage area, which is saved within the map file. In the worse scenario, a bug in the add-on may corrupt the map file.

Again, I've never notice any problem, but feedbacks about these points will be appreciated !

## TODO - Ideas

*Legend:  
`[ ]` = To do  
`[?]` = To do, but is it a good idea ?  
`[n]` = (n is a number) To do, lower number means higher priority*  

---

`[1]` [Add to context menu](https://sourceforge.net/p/freeplane/discussion/758437/thread/ec280c4e/?page=1&limit=25#5f30)

`[?]` Implement temporary bookmarks (vanish when map is closed)
`[?]` A "jump to link anchor" feature
`[?]` Move/Copy nodes to bookmark (or link anchor)
`[?]` Global bookmarks (can be used to reach closed maps)
`[?]` Move menu into Navigation menu
`[?]` Search bookmark by name
`[?]` Bookmark name edition

`[2]` Allow to sort the named bookmarks list by name
`[2]` Allow to display anonymous bookmarks first when dialogs open, or remember the last state, or select the state according to the currently selected node
`[2]` Add an icon to the menu and the package

`[3]` Continue to reduce duplicated code
`[3]` Remove JMap return from functions that modify namedBookmarks

`[ ]` Allow to add and remove named bookmarks directly with icon manipulation
`[ ]` Add option to warn when deleting a named bookmark
`[ ]` Add a warning on creation of a named bookmark with an already used name

## Build

If you want to build the add-on installation file `bookmarks-vX.X.X.addon.mm` yourself, you have to build the library before to package the addon.

- Install Freeplane (of course !)
- Download the source
- Install gradle
- Open `build.gradle` with a text editor and modify the paths in `repositories.dirs[]` to point to your Freeplane installation
- Get a command prompt at the root of the sources folder
- `gradle build` will create the file lib/bookmarks.jar

Now you can open `bookmarks.mm` with Freeplane and package the addon with [Tools > Developer Tools > Package add-on for publication](https://freeplane.sourceforge.io/wiki/index.php/Add-ons_(Develop)). This will create the file `bookmarks-vx.x.x.addon.mm`. Open this file with Freeplane install the add-on.
