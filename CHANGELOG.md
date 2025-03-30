~

### v0.3.0 (2025-03-30)
* adds quick settings tile that displays the current hour.
* adds daydream (screen saver) that shows a "wandering clock" (#25).
* adds fullscreen mode, ability to display over the lock screen, and home screen wallpaper option.
* adds clock option; show "seconds hand" and inner dial.
* adds clock option; "6hr time format" (Italian six-hour clock).
* adds clock option; equinoctial hours; "Babylonian", "Italic", "Italian Civil", or "Julian" (#29).
* adds clock option; temporal hours begin at "sunrise (24)", "civil dawn (24)", "noon (240", or "civil dusk (24)" (#29).
* adds clock option; night watches of five or more parts (#26).
* adds clock option; show "solar midnight" (#24).
* adds widget option; "on tap", "reconfigure widget" or "launch app".
* enhances color editor (adds support for color roles).
* adds to default colors; "Sun (dark)" (#9).
* updates build; updates SuntimesAddon dependency (v0.4.0 -> v0.4.2); replaces jitpack.io with git submodule.
* updates build; targetSdkVersion 28 -> 33; Gradle 5.6.4 -> 6.7.1; Android Gradle Plugin 3.6.1 -> 4.1.3; migrates from legacy support libraries to AndroidX.

### v0.2.3 (2024-11-20)
* fixes bug where "twilight periods are drawn incorrectly" (#30).
* fixes bug where "alarms times are incorrect when using hours begin at sunset (24)" (#28).
* fixes ANR when the Suntimes content-provider fails to respond.
* fixes poor contrast in themed app icon (api33+).
* updates default color schemes.

### v0.2.2 (2023-08-04)
* adds UTC to time zone settings.
* adds themed app icon (api33+).
* adds support for "text size" and "high contrast" app themes.
* fixes bug "watch face shows broken daylight" (#22).
* updates SuntimesAddon dependency (v0.3.0 -> v0.4.0).

### v0.2.1 (2023-01-09)
* adds support for system dark mode (night mode).
* fixes bug where Toast messages are unreadable on api33+.
* fixes bug "the widget does not update" (#15).
* fixes bug "broken UI - missing first hour - watches" (#14).
* fixes bug where the hour is sometimes announced incorrectly.
* fixes bug "unable to set alarm for all hours when using 'sunset (24)' (#21).
* misc layout tweaks (margins).

### v0.2.0 (2022-02-22)
* adds support for repeating alarms and notifications (#7); requires `Suntimes v0.14.0` or later.
* adds alarm UI (NaturalHourSelectFragment, NaturalHourAlarmFragment, and AlarmActivity); responds to `suntimes.action.PICK_EVENT`; extends content-provider to implement `suntimes.action.ADDON_EVENT`. 
* adds `EXTRA_SHOW_DATE`; the main activity now responds to `suntimes.action.SHOW_DATE`.
* updates SuntimesAddon dependency (v0.2.0 -> v0.3.0).

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