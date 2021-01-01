~

### v0.1.0 (2020-01-01) [First Release]
* an add-on app that uses the current Suntimes configuration (location, timezone, theme, locale, and UI options). The minimum Suntimes version is `v0.10.3` (but without access to UI options); the recommended version is `v0.12.6`.
* provides an ActionBar that displays configured Location (lat, lon, alt), Suntimes icon (opens Suntimes activity), and overflow menu (Colors, Options, Help, and About).
* provides a BottomBar that displays the configured timezone ("Apparent Solar", "Local Mean", "Suntimes", "System"), and the time format (12-hr / 24-hr). 
* provides a RecyclerView that displays a series of cards (days centered on today); each card displays the date and a clock face (with divisions for roman hours, twilight periods, and noon).
* announces the current roman hour (and given time) when the clock face is clicked.
* provides quick navigation to the dates of the solstices and equinoxes, or the current date.
* provides a BottomSheet that allows selecting and editing clock face color schemes (Add, Edit, Delete, Share).  
* provides an options Activity; natural hour definition (starts at sunrise/sunrise, starts at sunset),  orientation (midnight-at-top, center-on-noon), clock numerals (Arabic, Attic, Armenian, Etruscan, Greek, Hebrew, Roman), night watches, and other options (background, labels, ticks, etc).  
* provides a home screen widget version of the clock face (resizable 3x2, 4x3, and 5x3 widgets); clicking a widget opens the app.