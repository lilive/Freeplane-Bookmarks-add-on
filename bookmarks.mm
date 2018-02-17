<map version="freeplane 1.6.0">
<!--To view this file, download free mind mapping software Freeplane from http://freeplane.sourceforge.net -->
<node TEXT="Bookmarks" LOCALIZED_STYLE_REF="styles.topic" FOLDED="false" ID="ID_1723255651" CREATED="1283093380553" MODIFIED="1518883140358" LINK="https://github.com/lilive/Freeplane-Bookmarks-add-on" BORDER_COLOR_LIKE_EDGE="false" BORDER_COLOR="#000000"><hook NAME="MapStyle" background="#a0a0a0">
    <properties show_icon_for_attributes="true" fit_to_viewport="false;" show_note_icons="true" edgeColorConfiguration="#353535ff,#353535ff,#353535ff,#353535ff,#353535ff"/>

<map_styles>
<stylenode LOCALIZED_TEXT="styles.root_node" STYLE="oval" UNIFORM_SHAPE="true" VGAP_QUANTITY="24.0 pt">
<font SIZE="24"/>
<stylenode LOCALIZED_TEXT="styles.predefined" POSITION="right" STYLE="bubble">
<stylenode LOCALIZED_TEXT="default" MAX_WIDTH="600.0 px" BACKGROUND_COLOR="#cccccc" STYLE="bubble" SHAPE_HORIZONTAL_MARGIN="4.0 pt">
<font NAME="Raleway" SIZE="14"/>
<edge COLOR="#353535" WIDTH="thin"/>
</stylenode>
<stylenode LOCALIZED_TEXT="defaultstyle.details"/>
<stylenode LOCALIZED_TEXT="defaultstyle.attributes">
<font SIZE="9"/>
</stylenode>
<stylenode LOCALIZED_TEXT="defaultstyle.note"/>
<stylenode LOCALIZED_TEXT="defaultstyle.floating">
<edge STYLE="hide_edge"/>
<cloud COLOR="#f0f0f0" SHAPE="ROUND_RECT"/>
</stylenode>
</stylenode>
<stylenode LOCALIZED_TEXT="styles.user-defined" POSITION="right" STYLE="bubble">
<stylenode LOCALIZED_TEXT="styles.topic" STYLE="oval" SHAPE_HORIZONTAL_MARGIN="6.0 pt" SHAPE_VERTICAL_MARGIN="6.0 pt" UNIFORM_SHAPE="true" BORDER_WIDTH="4.0 px" BORDER_DASH="DASHES">
<font NAME="Raleway" SIZE="14" BOLD="true"/>
</stylenode>
<stylenode LOCALIZED_TEXT="styles.subtopic" COLOR="#cc3300" STYLE="fork">
<font NAME="Liberation Sans" SIZE="10" BOLD="true"/>
</stylenode>
<stylenode LOCALIZED_TEXT="styles.subsubtopic" COLOR="#669900">
<font NAME="Liberation Sans" SIZE="10" BOLD="true"/>
</stylenode>
<stylenode LOCALIZED_TEXT="styles.important">
<icon BUILTIN="yes"/>
</stylenode>
<stylenode TEXT="Ligne 1" BACKGROUND_COLOR="#cccccc" STYLE="bubble" SHAPE_HORIZONTAL_MARGIN="4.0 pt" MAX_WIDTH="600.0 px" BORDER_WIDTH="3.0 px" BORDER_COLOR_LIKE_EDGE="true" BORDER_COLOR="#808080">
<font NAME="Raleway" SIZE="14"/>
<edge COLOR="#ff4a4a" WIDTH="3"/>
</stylenode>
<stylenode TEXT="Ligne 2" BACKGROUND_COLOR="#cccccc" STYLE="bubble" SHAPE_HORIZONTAL_MARGIN="4.0 pt" MAX_WIDTH="600.0 px" BORDER_WIDTH="3.0 px" BORDER_COLOR_LIKE_EDGE="true" BORDER_COLOR="#ffc265">
<font NAME="Raleway" SIZE="14"/>
<edge COLOR="#ffa14a" WIDTH="3"/>
</stylenode>
<stylenode TEXT="Ligne 3" BACKGROUND_COLOR="#cccccc" STYLE="bubble" SHAPE_HORIZONTAL_MARGIN="4.0 pt" MAX_WIDTH="600.0 px" BORDER_WIDTH="3.0 px" BORDER_COLOR_LIKE_EDGE="true" BORDER_COLOR="#feff62">
<font NAME="Raleway" SIZE="14"/>
<edge COLOR="#fff24a" WIDTH="3"/>
</stylenode>
<stylenode TEXT="Ligne 4" BACKGROUND_COLOR="#cccccc" STYLE="bubble" SHAPE_HORIZONTAL_MARGIN="4.0 pt" MAX_WIDTH="600.0 px" BORDER_WIDTH="3.0 px" BORDER_COLOR_LIKE_EDGE="true" BORDER_COLOR="#7dff72">
<font NAME="Raleway" SIZE="14"/>
<edge COLOR="#69ff4a" WIDTH="3"/>
</stylenode>
<stylenode TEXT="Ligne 5" BACKGROUND_COLOR="#cccccc" STYLE="bubble" SHAPE_HORIZONTAL_MARGIN="4.0 pt" MAX_WIDTH="600.0 px" BORDER_WIDTH="3.0 px" BORDER_COLOR_LIKE_EDGE="true" BORDER_COLOR="#61fff2">
<font NAME="Raleway" SIZE="14"/>
<edge COLOR="#4affe4" WIDTH="3"/>
</stylenode>
<stylenode TEXT="Ligne 6" BACKGROUND_COLOR="#cccccc" STYLE="bubble" SHAPE_HORIZONTAL_MARGIN="4.0 pt" MAX_WIDTH="600.0 px" BORDER_WIDTH="3.0 px" BORDER_COLOR_LIKE_EDGE="true" BORDER_COLOR="#6973ff">
<font NAME="Raleway" SIZE="14"/>
<edge COLOR="#6973ff" WIDTH="3"/>
</stylenode>
<stylenode TEXT="Ligne 7" BACKGROUND_COLOR="#cccccc" STYLE="bubble" SHAPE_HORIZONTAL_MARGIN="4.0 pt" MAX_WIDTH="600.0 px" BORDER_WIDTH="3.0 px" BORDER_COLOR_LIKE_EDGE="true" BORDER_COLOR="#ff6df2">
<font NAME="Raleway" SIZE="14"/>
<edge COLOR="#ff4af8" WIDTH="3"/>
</stylenode>
</stylenode>
<stylenode LOCALIZED_TEXT="styles.AutomaticLayout" POSITION="right" STYLE="bubble">
<stylenode LOCALIZED_TEXT="AutomaticLayout.level.root" COLOR="#000000">
<font SIZE="18"/>
</stylenode>
<stylenode LOCALIZED_TEXT="AutomaticLayout.level,1" COLOR="#0033ff">
<font SIZE="16"/>
</stylenode>
<stylenode LOCALIZED_TEXT="AutomaticLayout.level,2" COLOR="#00b439">
<font SIZE="14"/>
</stylenode>
<stylenode LOCALIZED_TEXT="AutomaticLayout.level,3" COLOR="#990000">
<font SIZE="12"/>
</stylenode>
<stylenode LOCALIZED_TEXT="AutomaticLayout.level,4" COLOR="#111111">
<font SIZE="10"/>
</stylenode>
</stylenode>
</stylenode>
</map_styles>
</hook>
<hook NAME="AutomaticEdgeColor" COUNTER="39" RULE="ON_BRANCH_CREATION"/>
<edge WIDTH="thin"/>
<attribute NAME="name" VALUE="bookmarks"/>
<attribute NAME="version" VALUE="v0.4.2"/>
<attribute NAME="author" VALUE="lilive"/>
<attribute NAME="freeplaneVersionFrom" VALUE="1.6.6"/>
<attribute NAME="freeplaneVersionTo" VALUE=""/>
<attribute NAME="updateUrl" VALUE=""/>
<richcontent TYPE="NOTE">

<html>
  <head>
    
  </head>
  <body>
    <p>
      The homepage of this add-on should be set as the link of the root node.
    </p>
    <p>
      The basic properties of this add-on. They can be used in script names and other attributes, e.g. &quot;${name}.groovy&quot;.
    </p>
    <ul>
      <li>
        name: The name of the add-on, normally a technically one (no spaces, no special characters except _.-).
      </li>
      <li>
        author: Author's name(s) and (optionally) email adresses.
      </li>
      <li>
        version: Since it's difficult to protect numbers like 1.0 from Freeplane's number parser it's advised to prepend a 'v' to the number, e.g. 'v1.0'.
      </li>
      <li>
        freeplane-version-from: The oldest compatible Freeplane version. The add-on will not be installed if the Freeplane version is too old.
      </li>
      <li>
        freeplane-version-to: Normally empty: The newest compatible Freeplane version. The add-on will not be installed if the Freeplane version is too new.
      </li>
      <li>
        updateUrl: URL of the file containing information (version, download url) on the latest version of this add-on. By default: &quot;${homepage}/version.properties&quot;
      </li>
    </ul>
  </body>
</html>

</richcontent>
<node TEXT="description" POSITION="left" ID="ID_166806415" CREATED="1518707376058" MODIFIED="1518883140371">
<edge COLOR="#353535"/>
<richcontent TYPE="NOTE">

<html>
  <head>
    
  </head>
  <body>
    <p>
      Description would be awkward to edit as an attribute.
    </p>
    <p>
      So you have to put the add-on description as a child of the <i>'description'</i>&#160;node.
    </p>
    <p>
      To translate the description you have to define a translation for the key 'addons.${name}.description'.
    </p>
  </body>
</html>

</richcontent>
<node TEXT="Add bookmarks to nodes, and easily navigate beetween them." ID="ID_1183587619" CREATED="1518707454288" MODIFIED="1518815727777"/>
</node>
<node TEXT="changes" POSITION="left" ID="ID_1808338119" CREATED="1518707376068" MODIFIED="1518883140372">
<edge COLOR="#353535"/>
<richcontent TYPE="NOTE">

<html>
  <head>
    
  </head>
  <body>
    <p>
      Change log of this add-on: append one node for each noteworthy version and put the details for each version into a child node.
    </p>
  </body>
</html>

</richcontent>
<node TEXT="v0.3" ID="ID_1947541306" CREATED="1518881580800" MODIFIED="1518881585971">
<node TEXT="First released version" ID="ID_1150257103" CREATED="1518881587391" MODIFIED="1518881596914"/>
</node>
<node TEXT="v0.4" ID="ID_1236674265" CREATED="1518881417536" MODIFIED="1518881420019">
<node TEXT="Clean the map storage each time ToggleBookmarkSelectionView is used (to avoid increasing size of the file)" ID="ID_955061404" CREATED="1518881423311" MODIFIED="1518881534034"/>
</node>
<node TEXT="v0.4.1" ID="ID_1277201401" CREATED="1518881390240" MODIFIED="1518881395099">
<node TEXT="The bookmark icon is now SVG" ID="ID_1361390652" CREATED="1518881396976" MODIFIED="1518881414405"/>
</node>
<node TEXT="v0.4.2" ID="ID_490789856" CREATED="1518883033697" MODIFIED="1518883037789">
<node TEXT="Sources files cleanup" ID="ID_655524488" CREATED="1518883038976" MODIFIED="1518883131764"/>
</node>
</node>
<node TEXT="license" POSITION="left" ID="ID_80501004" CREATED="1518707376071" MODIFIED="1518883140373">
<edge COLOR="#353535"/>
<richcontent TYPE="NOTE">

<html>
  <head>
    
  </head>
  <body>
    <p>
      The add-ons's license that the user has to accept before she can install it.
    </p>
    <p>
      
    </p>
    <p>
      The License text has to be entered as a child of the <i>'license'</i>&#160;node, either as plain text or as HTML.
    </p>
  </body>
</html>

</richcontent>
<node TEXT="This add-on is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, version 2 of the License.&#xa;&#xa;This program is distributed in the hope that it will be useful,&#xa;but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details." ID="ID_1169466095" CREATED="1518707376074" MODIFIED="1518813393943"/>
</node>
<node TEXT="preferences.xml" POSITION="left" ID="ID_293444210" CREATED="1518707376077" MODIFIED="1518883140374">
<edge COLOR="#353535"/>
<richcontent TYPE="NOTE">

<html>
  <head>
    
  </head>
  <body>
    <p>
      <font color="#000000" face="SansSerif, sans-serif">The child node contains the add-on configuration as an extension to mindmapmodemenu.xml (in Tools-&gt;Preferences-&gt;Add-ons). </font>
    </p>
    <p>
      <font color="#000000" face="SansSerif, sans-serif">Every property in the configuration should receive a default value in <i>default.properties</i>&#160;node.</font>
    </p>
  </body>
</html>

</richcontent>
</node>
<node TEXT="default.properties" POSITION="left" ID="ID_1290130454" CREATED="1518707376081" MODIFIED="1518883140376">
<edge COLOR="#353535"/>
<richcontent TYPE="NOTE">

<html>
  <head>
    
  </head>
  <body>
    <p>
      These properties play together with the preferences: Each property defined in the preferences should have a default value in the attributes of this node.
    </p>
  </body>
</html>

</richcontent>
</node>
<node TEXT="translations" POSITION="left" ID="ID_1364843549" CREATED="1518707376084" MODIFIED="1518883140377">
<edge COLOR="#353535"/>
<richcontent TYPE="NOTE">

<html>
  <head>
    
  </head>
  <body>
    <p>
      The translation keys that this script uses. Define one child node per supported locale. The attributes contain the translations. Define at least
    </p>
    <ul>
      <li>
        'addons.${name}' for the add-on's name
      </li>
      <li>
        'addons.${name}.description' for the description, e.g. in the add-on overview dialog (not necessary for English)
      </li>
      <li>
        'addons.${name}.&lt;scriptname&gt;' for each script since it will be the menu title.
      </li>
    </ul>
  </body>
</html>

</richcontent>
<node TEXT="en" ID="ID_13651212" CREATED="1518707376086" MODIFIED="1518881262795">
<attribute_layout NAME_WIDTH="195.74999416619556 pt" VALUE_WIDTH="209.24999376386424 pt"/>
<attribute NAME="addons.${name}" VALUE="Bookmarks"/>
<attribute NAME="addon.${name}.ToggleBookmark" VALUE="Add / Remove a Bookmark"/>
<attribute NAME="addon.${name}.JumpToPreviousBookmark" VALUE="Go to previous bookmark"/>
<attribute NAME="addon.${name}.JumpToNextBookmark" VALUE="Go to next bookmark"/>
<attribute NAME="addon.${name}.ToggleBookmarkSelectionView" VALUE="Display only bookmarks / Return to previous view"/>
</node>
<node TEXT="fr" ID="ID_68795515" CREATED="1518707376086" MODIFIED="1518881268883">
<attribute_layout NAME_WIDTH="176.24999474734085 pt" VALUE_WIDTH="338.24998991936474 pt"/>
<attribute NAME="addons.${name}" VALUE="Marque-pages"/>
<attribute NAME="${name}" VALUE="Marque-pages"/>
<attribute NAME="addons.${name}.description" VALUE="Mettre des marque-pages sur des noeuds et naviguer facilement de l&apos;un a l&apos;autre."/>
<attribute NAME="addon.${name}.ToggleBookmark" VALUE="Ajouter / Retirer un marque-page"/>
<attribute NAME="addon.${name}.JumpToPreviousBookmark" VALUE="Aller au marque-page precedent"/>
<attribute NAME="addon.${name}.JumpToNextBookmark" VALUE="Aller au marque-page suivant"/>
<attribute NAME="addon.${name}.ToggleBookmarkSelectionView" VALUE="Voir les marque-pages / Revenir a la vue precedente"/>
</node>
</node>
<node TEXT="deinstall" POSITION="left" ID="ID_1462169331" CREATED="1518707376090" MODIFIED="1518883140378">
<edge COLOR="#353535"/>
<richcontent TYPE="NOTE">

<html>
  <head>
    
  </head>
  <body>
    <p>
      List of files and/or directories to remove on uninstall
    </p>
  </body>
</html>

</richcontent>
<attribute_layout NAME_WIDTH="28.499999150633837 pt" VALUE_WIDTH="332.24999009817867 pt"/>
<attribute NAME="delete" VALUE="${installationbase}/addons/${name}.script.xml"/>
<attribute NAME="delete" VALUE="${installationbase}/addons/${name}/scripts/ToggleBookmark.groovy"/>
<attribute NAME="delete" VALUE="${installationbase}/addons/${name}/scripts/JumpToPreviousBookmark.groovy"/>
<attribute NAME="delete" VALUE="${installationbase}/addons/${name}/scripts/JumpToNextBookmark.groovy"/>
<attribute NAME="delete" VALUE="${installationbase}/addons/${name}/scripts/ToggleBookmarkSelectionView.groovy"/>
<attribute NAME="delete" VALUE="${installationbase}/icons/bookmark.svg"/>
</node>
<node TEXT="scripts" POSITION="right" ID="ID_1153649867" CREATED="1518707376097" MODIFIED="1518883140382">
<edge COLOR="#353535"/>
<richcontent TYPE="NOTE">

<html>
  <head>
    
  </head>
  <body>
    <p>
      An add-on may contain multiple scripts. The node text defines the script name (e.g. inserInlineImage.groovy). The name must have a suffix of a supported script language like .groovy or .js and may only consist of letters and digits. The script properties have to be configured via attributes:
    </p>
    <p>
      
    </p>
    <p>
      * menuLocation: &lt;locationkey&gt;
    </p>
    <p>
      &#160;&#160;&#160;- Defines where the menu location.
    </p>
    <p>
      &#160;&#160;&#160;-&#160;See mindmapmodemenu.xml for how the menu locations look like.
    </p>
    <p>
      &#160;&#160;&#160;- http://freeplane.bzr.sf.net/bzr/freeplane/freeplane_program/trunk/annotate/head%3A/freeplane/resources/xml/mindmapmodemenu.xml
    </p>
    <p>
      &#160;&#160;&#160;- This attribute is mandatory
    </p>
    <p>
      
    </p>
    <p>
      * menuTitleKey: &lt;key&gt;
    </p>
    <p>
      &#160;&#160;&#160;- The menu item title will be looked up under the translation key &lt;key&gt; - don't forget to define its translation.
    </p>
    <p>
      &#160;&#160;&#160;- This attribute is mandatory
    </p>
    <p>
      
    </p>
    <p>
      * executionMode: &lt;mode&gt;
    </p>
    <p>
      &#160;&#160;&#160;- The execution mode as described in the Freeplane wiki (http://freeplane.sourceforge.net/wiki/index.php/Scripting)
    </p>
    <p>
      &#160;&#160;&#160;- ON_SINGLE_NODE: Execute the script once. The <i>node</i>&#160;variable is set to the selected node.
    </p>
    <p>
      &#160;&#160;&#160;- ON_SELECTED_NODE: Execute the script n times for n selected nodes, once for each node.
    </p>
    <p>
      &#160;&#160;&#160;- ON_SELECTED_NODE_RECURSIVELY: Execute the script on every selected node and recursively on all of its children.
    </p>
    <p>
      &#160;&#160;&#160;- In doubt use ON_SINGLE_NODE.
    </p>
    <p>
      &#160;&#160;&#160;- This attribute is mandatory
    </p>
    <p>
      
    </p>
    <p>
      * keyboardShortcut: &lt;shortcut&gt;
    </p>
    <p>
      &#160;&#160;&#160;- Optional: keyboard combination / accelerator for this script, e.g. control alt I
    </p>
    <p>
      &#160;&#160;&#160;- Use lowercase letters for modifiers and uppercase for letters. Use no + signs.
    </p>
    <p>
      &#160;&#160;&#160;- The available key names are listed at http://download.oracle.com/javase/1.4.2/docs/api/java/awt/event/KeyEvent.html#VK_0
    </p>
    <p>
      &#160;&#160;&#160;&#160;&#160;In the list only entries with a 'VK_' prefix count. Omit the prefix in the shortcut definition.
    </p>
    <p>
      
    </p>
    <p>
      * Permissions&#160;that the script(s) require, each either false or true:
    </p>
    <p>
      &#160;&#160;&#160;- execute_scripts_without_asking
    </p>
    <p>
      &#160;&#160;&#160;- execute_scripts_without_file_restriction: permission to read files
    </p>
    <p>
      &#160;&#160;&#160;- execute_scripts_without_write_restriction: permission to create/change/delete files
    </p>
    <p>
      &#160;&#160;&#160;- execute_scripts_without_exec_restriction: permission to execute other programs
    </p>
    <p>
      &#160;&#160;&#160;- execute_scripts_without_network_restriction: permission to access the network
    </p>
    <p>
      &#160;&#160;Notes:
    </p>
    <p>
      &#160;&#160;- The set of permissions is fixed.
    </p>
    <p>
      &#160;&#160;- Don't change the attribute names, don't omit one.
    </p>
    <p>
      &#160;&#160;- Set the values either to true or to false
    </p>
    <p>
      &#160;&#160;- In any case set execute_scripts_without_asking to true unless you want to annoy users.
    </p>
  </body>
</html>

</richcontent>
<node TEXT="ToggleBookmark.groovy" ID="ID_479598607" CREATED="1518730062703" MODIFIED="1518730087832">
<attribute_layout NAME_WIDTH="188.99999436736124 pt" VALUE_WIDTH="188.99999436736124 pt"/>
<attribute NAME="menuTitleKey" VALUE="addon.${name}.ToggleBookmark"/>
<attribute NAME="menuLocation" VALUE="/menu_bar/${name}"/>
<attribute NAME="executionMode" VALUE="on_single_node"/>
<attribute NAME="keyboardShortcut" VALUE=""/>
<attribute NAME="execute_scripts_without_asking" VALUE="true"/>
<attribute NAME="execute_scripts_without_file_restriction" VALUE="true"/>
<attribute NAME="execute_scripts_without_write_restriction" VALUE="false"/>
<attribute NAME="execute_scripts_without_exec_restriction" VALUE="false"/>
<attribute NAME="execute_scripts_without_network_restriction" VALUE="false"/>
</node>
<node TEXT="JumpToPreviousBookmark.groovy" ID="ID_1347963609" CREATED="1518709818338" MODIFIED="1518712222840">
<attribute_layout NAME_WIDTH="188.99999436736124 pt" VALUE_WIDTH="188.99999436736124 pt"/>
<attribute NAME="menuTitleKey" VALUE="addon.${name}.JumpToPreviousBookmark"/>
<attribute NAME="menuLocation" VALUE="/menu_bar/${name}"/>
<attribute NAME="executionMode" VALUE="on_single_node"/>
<attribute NAME="keyboardShortcut" VALUE=""/>
<attribute NAME="execute_scripts_without_asking" VALUE="true"/>
<attribute NAME="execute_scripts_without_file_restriction" VALUE="true"/>
<attribute NAME="execute_scripts_without_write_restriction" VALUE="false"/>
<attribute NAME="execute_scripts_without_exec_restriction" VALUE="false"/>
<attribute NAME="execute_scripts_without_network_restriction" VALUE="false"/>
</node>
<node TEXT="JumpToNextBookmark.groovy" ID="ID_158699171" CREATED="1518707711200" MODIFIED="1518712176693">
<attribute_layout NAME_WIDTH="188.99999436736124 pt" VALUE_WIDTH="188.99999436736124 pt"/>
<attribute NAME="menuTitleKey" VALUE="addon.${name}.JumpToNextBookmark"/>
<attribute NAME="menuLocation" VALUE="/menu_bar/${name}"/>
<attribute NAME="executionMode" VALUE="on_single_node"/>
<attribute NAME="keyboardShortcut" VALUE=""/>
<attribute NAME="execute_scripts_without_asking" VALUE="true"/>
<attribute NAME="execute_scripts_without_file_restriction" VALUE="true"/>
<attribute NAME="execute_scripts_without_write_restriction" VALUE="false"/>
<attribute NAME="execute_scripts_without_exec_restriction" VALUE="false"/>
<attribute NAME="execute_scripts_without_network_restriction" VALUE="false"/>
</node>
<node TEXT="ToggleBookmarkSelectionView.groovy" ID="ID_397345320" CREATED="1518811350572" MODIFIED="1518881244636">
<attribute_layout NAME_WIDTH="188.99999436736124 pt" VALUE_WIDTH="195.74999416619556 pt"/>
<attribute NAME="menuTitleKey" VALUE="addon.${name}.ToggleBookmarkSelectionView"/>
<attribute NAME="menuLocation" VALUE="/menu_bar/${name}"/>
<attribute NAME="executionMode" VALUE="on_single_node"/>
<attribute NAME="keyboardShortcut" VALUE=""/>
<attribute NAME="execute_scripts_without_asking" VALUE="true"/>
<attribute NAME="execute_scripts_without_file_restriction" VALUE="true"/>
<attribute NAME="execute_scripts_without_write_restriction" VALUE="false"/>
<attribute NAME="execute_scripts_without_exec_restriction" VALUE="false"/>
<attribute NAME="execute_scripts_without_network_restriction" VALUE="false"/>
</node>
</node>
<node TEXT="lib" POSITION="right" ID="ID_888233347" CREATED="1518707376102" MODIFIED="1518883140424">
<edge COLOR="#353535"/>
<richcontent TYPE="NOTE">

<html>
  <head>
    
  </head>
  <body>
    <p>
      An add-on may contain any number of nodes containing binary files (normally .jar files) to be added to the add-on's classpath.
    </p>
    <p>
      
    </p>
    <p>
      &#160;- The immediate child nodes contain the name of the file, e.g. 'mysql-connector-java-5.1.25.jar'). Put the file into a 'lib' subdirectory of the add-on base directory.
    </p>
    <p>
      
    </p>
    <p>
      &#160;- The child nodes of these nodes contain the actual files.
    </p>
    <p>
      
    </p>
    <p>
      &#160;- Any lib file will be extracted in &lt;installationbase&gt;/&lt;addonname&gt;/lib.
    </p>
    <p>
      
    </p>
    <p>
      &#160;- The files will be processed in the sequence as seen in the map.
    </p>
  </body>
</html>

</richcontent>
</node>
<node TEXT="zips" POSITION="right" ID="ID_1960927192" CREATED="1518707376105" MODIFIED="1518883140426">
<edge COLOR="#353535"/>
<richcontent TYPE="NOTE">

<html>
  <head>
    
  </head>
  <body>
    <p>
      An add-on may contain any number of nodes containing zip files.
    </p>
    <p>
      
    </p>
    <p>
      &#160;- The immediate child nodes contain a description of the zip. The devtools script releaseAddOn.groovy allows automatic zip creation if the name of this node matches a directory in the current directory.
    </p>
    <p>
      
    </p>
    <p>
      &#160;- The child nodes of these nodes contain the actual zip files.
    </p>
    <p>
      
    </p>
    <p>
      &#160;- Any zip file will be extracted in the &lt;installationbase&gt;. Currently, &lt;installationbase&gt; is always Freeplane's &lt;userhome&gt;, e.g. ~/.freeplane/1.3.
    </p>
    <p>
      
    </p>
    <p>
      &#160;- The files will be processed in the sequence as seen in the map.
    </p>
  </body>
</html>

</richcontent>
<node TEXT="icons" ID="ID_1789080137" CREATED="1518707848641" MODIFIED="1518707851682"/>
</node>
<node TEXT="images" POSITION="right" ID="ID_1792028464" CREATED="1518707376108" MODIFIED="1518883140437">
<edge COLOR="#353535"/>
<richcontent TYPE="NOTE">

<html>
  <head>
    
  </head>
  <body>
    <p>
      An add-on may define any number of images as child nodes of the images node. The actual image data has to be placed as base64 encoded binary data into the text of a subnode.
    </p>
    <p>
      The images are saved to the <i>${installationbase}/resources/images</i>&#160;directory.
    </p>
    <p>
      
    </p>
    <p>
      The following images should be present:
    </p>
    <ul>
      <li>
        <i>${name}-icon.png</i>, like <i>oldicons-theme-icon.png</i>. This will be used in the app-on overview.
      </li>
      <li>
        <i>${name}-screenshot-1.png</i>, like <i>oldicons-theme-screenshot-1.png</i>. This will be used in the app-on details dialog. Further images can be included but they are not used yet.
      </li>
    </ul>
    <p>
      Images can be added automatically by releaseAddOn.groovy or must be uploaded into the map via the script <i>Tools-&gt;Scripts-&gt;Insert Binary</i>&#160;since they have to be (base64) encoded as simple strings.
    </p>
  </body>
</html>

</richcontent>
</node>
</node>
</map>
