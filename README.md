[![Published on Vaadin  Directory](https://img.shields.io/badge/Vaadin%20Directory-published-00b4f0.svg)](https://vaadin.com/directory/component/gridscrollextension-add-on)
[![Stars on Vaadin Directory](https://img.shields.io/vaadin-directory/star/gridscrollextension-add-on.svg)](https://vaadin.com/directory/component/gridscrollextension-add-on)


# GridScrollExtension Add-on for Vaadin 8

GridScrollExtension is a Grid Extension add-on for Vaadin 8.

## Download release

Official releases of this add-on are available at Vaadin Directory. For Maven instructions, download and reviews, go to 

## Building and running demo

git clone <url of the MyComponent repository>
mvn clean install
cd demo
mvn jetty:run

To see the demo, navigate to http://localhost:8080/

## Development with Eclipse IDE

For further development of this add-on, the following tool-chain is recommended:
- Eclipse IDE
- m2e wtp plug-in (install it from Eclipse Marketplace)
- Vaadin Eclipse plug-in (install it from Eclipse Marketplace)
- JRebel Eclipse plug-in (install it from Eclipse Marketplace)
- Chrome browser

### Importing project

Choose File > Import... > Existing Maven Projects

Note that Eclipse may give "Plugin execution not covered by lifecycle configuration" errors for pom.xml. Use "Permanently mark goal resources in pom.xml as ignored in Eclipse build" quick-fix to mark these errors as permanently ignored in your project. Do not worry, the project still works fine. 

### Debugging server-side

If you have not already compiled the widgetset, do it now by running vaadin:install Maven target for ComboBoxOpener-root project.

If you have a JRebel license, it makes on the fly code changes faster. Just add JRebel nature to your ComboBoxOpener-demo project by clicking project with right mouse button and choosing JRebel > Add JRebel Nature

To debug project and make code modifications on the fly in the server-side, right-click the ComboBoxOpener-demo project and choose Debug As > Debug on Server. Navigate to http://localhost:8080/ComboBoxOpener-demo/ to see the application.

### Debugging client-side

Debugging client side code in the ComboBoxOpener-demo project:
  - run "mvn vaadin:run-codeserver" on a separate console while the application is running
  - activate Super Dev Mode in the debug window of the application or by adding ?superdevmode to the URL
  - You can access Java-sources and set breakpoints inside Chrome if you enable source maps from inspector settings.
 
## Release notes

### Version 2.5.0
- Improved robustness of getColumnWidth methods
- Rebuild for not to give warnings due deprecated listener use with Vaadin 8.12.0

### Version 2.4.3
- Fixed bug with hidden columns
- Made delayed column snapping thread safe

### Version 2.4.2
- Added a method to disable widths guard feature, GridScrollExtension#setWidthGuardDisabled(boolean)

### Version 2.4.1
- Fixing possible issue with undefined column widths

### Version 2.4.0
- Added support for hidden columns

### Version 2.3.5
- Improved workaround to overcome issues with maximum widths of the columns, also fixes the issue #10 
- Added getColumn() to GridColumnsResizedEvent
 
### Version 2.3.4
- Fixing typo in method set/getColumnResizeCompensationMode name
- Improved JavaDocs

### Version 2.3.3
- Improved JavaDocs
- Improved type safety
- added setRestorePosition(..) to workaround issue #5

### Version 2.3.2
- Fixing issues with compensation used when Column.setMaximumWidth(..) used. Works as partial workaround for some generic Grid Column.setMaximumWidth(..) bugs

### Version 2.3.1
- ColumnResizeCompensationMode.RESIZE_COLUMN is now applied to fill the empty space also when Grid is resized

### Version 2.3.0
- Added ColumnResizeCompensationMode to choose between resizing Grid and resizing last column to compensate resizing of columns. 

### Version 2.2.2
- Fix: Size adjustment after sorting should be applied only when setAutoResizeWidth(true) 
- Fix: Scroll bar adjustment needs to be handled differently on IE11/Edge 

### Version 2.2.1
- Added workaround for Grid getting resized after sorting issue

### Version 2.2.0
- Adding events: GridRenderedEvent, GridResizedEvent, GridScrolledEvent and GridColumnsResizedEvent
- Added adjustGridWidth() API to force Grid resize according to column widths
- Added getWidth(), getHeight() get actual width and height of Grid (see issue #4)
- Fixed a bug: Extension did not work with hidden columns properly
- Fixed a bug: Vertical scroll bar adjustment was not correctly implemented 

### Version 2.1.1
- Adding setAutoResizeWidth(..) for Grid to automatically adjust width according to column widths.

### Version 2.1.0
- Adding getColumnWidth(..) to get actual width of a given column

### Version 2.0.1
- Fixing issue #2, Server side position was off sync after setPosition(..)
- Updated demo

### Version 2.0.0
- First version for Vaadin 8

## Roadmap

This component is developed without no public roadmap or any guarantees of upcoming releases. That said, the following features are planned for upcoming releases:
- Version 2.0 for Vaadin 8


## Issue tracking

The issues for this add-on are tracked on its github.com page. All bug reports and feature requests are appreciated. 

## Contributions

Contributions are welcome, but there are no guarantees that they are accepted as such. Process for contributing is the following:
- Fork this project
- Create an issue to this project about the contribution (bug or feature) if there is no such issue about it already. Try to keep the scope minimal.
- Develop and test the fix or functionality carefully. Only include minimum amount of code needed to fix the issue.
- Refer to the fixed issue in commit
- Send a pull request for the original project
- Comment on the original issue that you have implemented a fix for it

## License & Author

Add-on is distributed under Apache License 2.0. For license terms, see LICENSE.txt.

MyComponent is written by <...>

