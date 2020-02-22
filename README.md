# ![logo](doc/images/bookmark.png) Bookmarks for Freeplane

[Voir en français](README-fr.md)

This add-on allows to bookmark some nodes in a Freeplane map, and to easily navigate beetween them.

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

Appart form these 3 main actions, it is also possible to create links *to* or *from* the bookmarked nodes.
  
Feel free to contribute or to [notify issues](../../issues).

## Installation

- Download the *bookmarks.addon.mm* file from [the latest release](../../releases).
- Open this file with Freeplane and follow the instructions.

## Update

To update the add-on, just install its newer version. No need to uninstall it first.

**Important :** If you have created bookmarks with a version of this add-on prior to v0.5.1, you have to update your maps that contain bookmarks. Use the *"Boomarks>Tools>Update map with bookmarks defined by a previous version of the addon"* feature.

## Disclaimer

I use this addon-on for myself since a year, and I've never got any problems. I think it's ok. Anyway, I prefer to warn and say : use this add-on at your own risks.

If you want more informations consider these 2 points :

- The add-on modify a little bit how Freeplane works. It introduce a monitoring function that prevent the user to add a named bookmark icon the regular way. This is needed to ensure the named bookmarks consistency. It also prevent a named bookmark to be copied when a node with a named bookmark is copied, because a named bookmark must be unique. In this case, the node is copied without its bookmark.  
Then, each time a icon is modified by the user, or each time a node is created, an extra piece of code is executed for this monitoring. This *may* have some negative side effects.

- The add-on write the datas about the named bookmarks within the map storage area, which is saved within the map file. In the worse scenario, a bug in the add-on may corrupt the map file.

Again, I've never notice any problem, but feedbacks about these points will be appreciated !

