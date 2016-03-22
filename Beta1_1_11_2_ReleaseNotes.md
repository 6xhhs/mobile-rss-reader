#Release note history
# Mobile RSS Reader Release Notes #

Copyright (c) 2006-2007 Tommi Laukkanen
http://www.substanceofcode.com

Contributors:
Irving Bunton Jr

## Release Notes ##

=v1.5 (July 23th 2007)
  * Open links in device browser
  * Allow long feed URLs

=v1.9 (August 14rd 2007)
  * Support ISO8859-1, windows-1252, UTF-8, UTF-16 (partial support
  * Fixed import feeds replacing current feed and importing only 1 feed.

=v1.10 (August 23rd 2007)
  * Reduced memory overflows while saving
  * Fix '|' in header name and item
  * Prompt to delete feed
  * Fix delete of feed and add with the same name
  * Full/fixed support for ISO8859-1, windows-1252, UTF-8, UTF-16
  * Support for some redirects
  * Optionally mark unread items in feed
  * Show memory usage
  * Prepare to allow saving of log level for test version.
  * Replace HTML entities and numeric entitites from OPML title
  * Replace more HTML entities and hex numeric entitites
  * Read all unread items in list
  * Update only modified feeds.
  * Use summary if no description in atoms.
  * Use dates in atoms.
  * Support more date formats.
  * Save if opening link closes the application
  * Save without exit
  * Show error for import feed parsing error
  * Show and open enclosure
  * Support more time formats

=v1.10.1 (August 24rd 2007)
  * Fixed about not being shown the first time of use
  * Fixed intermittent exit problem

=v1.10.2 (August 24rd 2007)
  * Fixed infinate loop in emulator on exit.
  * Take version from mainifest file.

=v1.10.3 (August 28th 2007)
  * Fixed date formatting if it has '-' for RSS date.
  * Use alternate link for atoms of self and related are not present.
  * Rename New Items form to Unread Items form.
  * Remove unused test logging classes.

=v1.10.4 (September 4th 2007)
  * Fixed allowing pasting for some devices.  (Other devices may not have had
> any problem.)
  * Fixed redirect problem with capitalized HTML tags.
  * Show used/available memory in addition to DB memory used/available.
  * Allow copy of enclosure.
  * Give alert for out of memroy that is there until it is dismissed.
  * Fixed initial run problem with bookmarks shown before about screen.
  * Fixed intermittent start hang with MIDP 1.0 version.
  * Added more logging for test version.
  * Set focus when setting copy link/enclosure (Focus does not work
on all devices including the emulator.)

=v1.10.5 (September 17th 2007)
  * Allow non utf-16 to be faster.
  * Allow windows single quote for attributes.
  * Fixed attributes not handling encoding.
  * Allow descending sort of unread items dates.
  * Don't give error if missing title.
  * Allow reading of missing titles from OPML.
  * Get redirection from URLs with redirection statuses.
  * More error messages for feed problems.
  * Test logging changes (not in released binaries.  In test only).
  * Fixed add new bookmark not showing previous user name/password.
  * Increased size of bookmarks that can be restored.
  * Logging changes (for test only).

=v1.10.6 (September 20th 2007)
  * Fix serious problem with import not working

=v1.10.7 (October 2nd 2007)
  * Give error for forbidden and not found
  * Allow optional prevention of redirect for bad HTML mime types
  * Allow files in memory for feed files and import files
  * Allow paste for MIDP-1.0 (dependent on device)
  * Show program and device midp, cldc, jsr75 settings on settings form

=v1.11
  * New river of news options to sort by date and feed for all items, only unread,
> or only read.
  * Mark item as read or unread on river of news
  * Fix install problem on some MIDP 1.0 phones
  * Allow import to insert, add to current or end.
  * Allow add bookmark to insert, add to current or end.
  * Use allow paste from regular command for MIDP 2.0 instead of item command
  * Create forms only when needed to reduce memory usage
  * Support metadata re-direct
  * Support HTML RSS/Atom autolinks
  * Minor performance enhancements
  * Fix prologue handling to fix styleset handling
  * Itunes version for phones with lots of memory (high end smartphones).
> This gets subtitle, summary, author, explicit, language, and duration
  * Support description and for date (name space other than dc) from Dublin Core
  * Support two namespaces if present only itunes and Dublin core
  * Support link for feed.  Use link of feed if no link for item for
> Itunes version (to save on memory since Itunes vers if for phones with
> more memory)
  * Support future user defined category for feed.
  * Support accessing memory without JSR-75 if phone supports, but the
> file path needs to be know as browser is not possible, so this may
> not be very useful.  Memory URL must begin with file://
  * Support opening of files in the jar (jar:///) for future use of
> files in the jar file
  * Do not add feeds from HTML links which have 0 length
  * Allow passwords to contain '|'
  * Fix problem where textinput description is used instead of item description
  * Fix problem where update of redirected feed twice while on the bookmark/feed
> caused an error.  (The previous workaround was to go back to bookmarks and
> then open the book mark and update)
  * Fix problem with redirect not working for OPML files
  * Fix problem with some HTML imports not giving any values
  * Fix problem with some &nbsp; not being removed
  * Have error screen for errors during feed import.
  * Give error when imporing and existing feed that it is not updated
> (workaround is to delete and then re-import)
  * Support HTML links without quotes for href
  * Better error when redirect is a second time
  * Allow logging of input/output with TESTUI define for test version.
  * Allow testing of compatibilty of the record store from previous versions
  * Allow change in order of back and open for headers/items list
  * Testing form to show encoding
  * Convert more entities.  Also, convert hex numeric entities
  * Support dates with non-breaking space
  * Prevent infinate loop when removing HTML which has unmatched '<'
  * Fix handling of out of memory while storing bookmarks
  * Stop thread when exiting
  * Memory not updated after first time viewed on settings.
  * Allow items without a title.  Use 1st 10 words (configurable in settings)
> of the title for the item in headers list and title of item form
  * Give correct error message if JSR75 not supported on the phone
  * Don't have open/copy link command if no link
  * Give out of memory errors when applicable
  * Support phones with encoding aliases not documented in J2SE list
> (e.g. ISO-8859-1 instead of ISO8859\_1).

=v1.11.1 (August 13th 2008)
  * Fix bug when opening feeds on Sony Ericsson emulators and possiblely
> > affecting Sony Ericsson devices
  * Fix bug with storing Itunes attributes
  * Workaround VM problems with synchronized methods that prevent update of
> > settings
  * Allow choice to use standard exit or not in exit.  (Some phones the user may
> > not know which key is the exit key).
  * Only remove new lines from title if there is a description.
  * If atom has summary, but no title or description, use the summary as the
> > description
  * Support CLDC 1.1 versions all MIDP 2.0 versions.
  * Minor performance improvement for creating items, reading stored feeds.
  * Allow feeds with '^' in title, description, subtitle, summary,
> > and author.
  * Workaround loading form not being shown on N95.  With the workaround the
> > problem is fixed.
  * Prepare for future store of items without encoding
  * Show microedition platform.
  * Workaround problem with some Sony Ericsson phones with dynamic retrieval
> > of display.  Need to use setCurrent using original display for alerts.  This
> > problem prevented exit/delete of feed, but is not fixed.
  * Update exit key of when change of standard exit enabling.
  * Allow feeds with '^' in title, description, subtitle, summary, and author.
  * Use gauge to show progress of saving/loading.
  * Fix dates in yyyy-mm-dd format
  * Fix standard dates with 2 or more consecutive spaces
  * Workaround Sony Ericsson T637 problem with Exit and Delete (Alerts on MIDP
    1. 0 device)
  * Handle Content module
  * Add to disclamer
  * Include jadsrc and manifestsrc directories in source zip
  * Allow '|' in feed name, password, and podcast attributes author, subtitle,
> > summary.
  * Fix bugs in wtk-build.xml when properties suffixes not used.
  * Allow creation of jar using Free software:  Phone ME, ECJ (Eclipse compiler),
> > GNU Classpath, and JamVM or GNU JavaVM (gcj)
  * Workaround for MicroEmulator
  * Optionally create PRC if present.
  * Put in correct URL for jad for jar midlets.
  * Put in optional midlet permissions
  * Make creation of RIM optional
  * Put missing directories into released zip
  * Allow build of novice version with dist.midp.2.0.novice.  Can take novice.txt
> > from a different file using ant -Dnovice.txt=(full path) dist.midp.2.0.novice
> > The novice version has novice property true in manifest disables add, edit,
> > delete, and import and reads from /data/novice.txt line by line file.  The
> > novice version is not the default, normally all functions are enabled.
  * Only copy to device bin if midlet.home exists during build.
  * Remove html from link for rss like from EBay.
  * Allow override in place of existing feeds that are also in the import file.
> > This means that the feed in the import file will be used instead of what
> > was loaded earlier.  Override in place means that the overrided feed will
> > be in the same place as it was before.  This is different from the
> > non-overrided feeds which will be added based on command used either insert,
> > add, or append.
  * Support RSS Atoms without published tag (version 0.3, 1,0 e.g. from google)
> > by using updated or modified for date if not published tag.

=v1.11.2 Development 2 (dev2) (Dec 12th 2008)
  * Check in the rest of novice enhancement
  * Fix OPML with line feed/carriage return immediately after 'outline' tag.
  * Combine if statements
  * Changes from code analysis:  Remove unnecessary modifiers. Put stack trace
> > into empty catch statements.  Combine if statements.  Remove unused equals.
  * Fix problems with hangs (not present on most devices).  Some methods are
> > required to not spend much time.  As features are added to these methods, they
> > become too long and cause a hang as J2ME has limited resources.  This change
> > worksaround this by doing operations in a separate thread.
  * Allow options to change font size for lists.
  * Turn on/off wraparound for lists
  * Need serial version id for serializable classes.
  * Fix river of news sort problem.
  * Allow ascending and descending for river of news.
  * Encode passwords
  * Fix handling passwords for feeds
  * Allow findbugs and PMD analyzers
  * Give error if having problem doing find files
  * Fix development 1 introduced problem with find files aborting (this problem
> > is not in stable versions)
  * Allow bookmark/feed name at the beginning of river of news items.

=v1.11.2 Development 3 (dev2) (Dec 22th 2008)
  * Allow export of files on devices with JSR-75.  This is not usable on most
> > devices as they require that the user accept the write of each BYTE.  Yes,
> > each byte so this takes forever to the point that I gave up.
  * Attempts to fix problem on some devices where River of News leaves cursor is
> > put in the middle of the screen
  * Progress bars for large memory version (future)
  * Braces around optional feed names in River of News
  * Better handling of forms/lists wakeup for command and other events.
  * Show device locale in settings
  * Fix problem of loss of data when going to link.  This fixes a possibly
> > introduced defect into dev-2.
  * Changes for next release of JLogMicro
  * Fix turning on/off wraparound for lists

=v1.11.2 Beta 1 (beta1) (Feb 2nd 2008)
  * Show language for podcast version
  * Allow sorting, etc from River of News for showing headers/items screen
  * Have semi-automated tests go from the last item down to the first item
  * Fixed problem where if sort yielded 0 items, the loading form is displayed
> > without showing the list with 0 items.
  * Fix if CDATA and a tag is embedded in it, the text following the last tag is

> now displayed
  * Fix problem where if using "River of News" or Header sort of the number of
> > items is 0, but a previous command displayed some results, there are items
> > shown instead of 0 items.
  * Synchronize sort variables to prevent unintended results
  * Enhance to replace p, td, and br tags with a space.
  * Fix test ui logging
  * Have separate RIM (cod) jad file to prevent conversion of jar from
> > corrupting the main jad file.
  * Make building in windows and linux easier to switch between the two choices
  * Change load for to have it's own logging methods to allow future putting
> > load of form in a separate thread
  * Combine classes for future enhancements.
  * Fix description problem introduced in previous Alpha release
  * Log command for testui for semi-automated tests
  * More logging

## Known problems ##

  * Phone must support MP3 links, etc to play enclosures (many phones do not
> > allow an MP3 to be read from a URL, but play them from the file system)
