#Documentations for Mobile RSS Reader
# Introduction #

## Summary ##

The free RSS feed reader / agregator is a cell phone (mobile) J2ME MIDP  application that
is able to read most RSS/Atom servers/feed versions.
RSS is a lightweight XML format
designed for sharing headlines and other content like blogs and news.
Data
is stored on the device and updated on request.  This uses the internet which
service providers charge extra for, so you would best get an unlimited data
from your service provider.
This is licensed under GPL.  The program can be built and run using either
Free and
Open Source Software (FOSS) or proprietary compilers and J2ME.
Source code originally authored by Tommi Laukkanen is at
> http://code.google.com/p/mobile-rss-reader/
Modified GPL software from other projects are also used.

> ## Application Features: ##
  * Add RSS feed bookmarks
  * Parse RSS feeds
  * Browse RSS feed headers
  * Read topics
  * River of news
    1. Read all unread topics sorted by date and feed
    1. Read all read topics sorted by date and feed
    1. Read all topics sorted by date and feed
    1. Mark item as read
    1. Mark item as unread
  * Supports ISO8859\_1, windows-1252, UTF-8 , and UTF-16
  * Update all/updated feeds with single command
  * Open item link or enclosure in device browser
  * Update all/updated feeds with single command
  * Open item link or enclosure in device browser (Only on phones which support it)
  * Save on exit or request
  * Import and synchronize RSS feed lists from internet or phone memory using formats and filter feeds based on name and URL (except v1.10.5)
    1. OPML
    1. OPML autolinks
    1. HTML autolinks
    1. HTML hyperlinks
    1. Line by line
  * Future and or in development enhancements for v1.12
    1. Allow keypad to page/go back from some fields
    1. Do HTML emphasis and new line tags (e.g. <br>)<br>
<ol><li>Start of file to allow localization<br>
</li><li>Better error reporting<br>
</li><li>Memory save option<br>
</li><li>Redirect if HTTP Other is received</li></ol></li></ul>

<blockquote>## Which program to choose ##

These instructions are for 1.11.1 Release Candidate 2 and later.
If you want to start quickly, you can choose the program that should work
for most phones, midp20\_RSSReader.jar.
This allows you to have the device
browser go to the link if requested.  All but one of the versions of the
program allow this.
Otherwise, read what follows.
Java ME (J2ME) has a flaw in that many features are optional.  The problem
occurs in that if a program uses an feature that is not present
on the phone, then trying to run that program may not give an error, but
the program will not run as it requires an feature not present on the phone.
This can be frustrating for a user as it will appear that the program does
not work when in fact is the phone that is not giving a proper error message.
Sometimes a short error message is given such as invalid jar file when in
fact the jar is valid, but not for the phone that one is attempting to be
installed.
For example, say a program has 2 versions.  One which can read the phone's
memory and one that cannot.  The ability to read the phone's memory is called
JSR-75.  If you run a program with JSR-75 on a phone that does not have that
feature, it may not come up and may not give an error message.  So, in this
case one needs to pick the phone version that does not read from phone memory.


The easiest and best way to install and get the program with all the features
that your phone is capable of using is to go to
http://www.getjar.com/products/11333/RSSReader.  On the left hand side under
home (a few inches from the top), select your phone manufacture and model.
This causes the download information to change to the appropriate program
for your phone.  Follow the instructions there and you cannot go wrong.

These instructions are for 95% of the phones (it's a little bit more work
than the previous paragraph above which uses getjar.com.)  DO NOT USE
the download with ...-expert... in it as it is too complicated for 95% of users.
Instead use the one with -latest in it.
So, it is important to see which features the phone has and install the
program that uses those same features.
There are two sets of programs those that run on Blackberry and those that
run on other operating systems.  All the Blackberry phones have programs ending
in '.cod'.  The other phones have '.jar'.  The info below is for '.jar' however,
it can also be used for Blackberry by changing the '.jar' to '.cod'.
The features that
are important to know are MIDP-1.0 or MIDP-2.0, and JSR-75.  To find
the features go to
http://www.mobref.com/device.
Find your phone and go to it's web page.
Or you can find this out by looking at technical specs for your phone.
Look for MIDP version.  It may
have 1, 2, or 3 numbers 1.0 and/or 2.0 and/or 2.1.  Use the the higher number this gives
MIDP-2.0 or MIDP-2.1.
If this is the MIDP version (e.g. MIDP-2.0 or MIDP-2.1),
use a program which has midp20
(e.g. midp20\_RSSReader.jar)
If version 1.0, use program without midp10  (e.g. midp10\_RSSReader.jar).
Look for the Specific JSR.  If it has JSR-75, choose the program with jsr75
(e.g. midp20\_jsr75\_RSSReader).  If it does not have JSR-75, choose the program
without jsr75 (e.g. midp20\_RSSReader).

If your phone is not listed on the link above, you can run the
midp10\_RSSReader.jar and go to settings.
Near the bottom it will say
phone MIDP.  Take that number or numbers there and use it as above for
picking the MIDP
version program to install.
The specifications also has phone jsr75.  If that has true, the phone has JSR-75.
Use it as above for the JSR-75 version program to install.

> ## Install/copy Mobile RSS Reader to phone instructions ##

To install/copy RSSReader to the phone, you will need some communication
method between your computer and your phone to be working (e-mail, cable,
bluetooth, or infrared), and copy the midp10\_RSSReader.jar,
midp20\_RSSReader.jar, midp20\_jsr75\_RSSReader.jar,
file
(that is in the
[(zip file)](http://code.google.com/p/mobile-rss-reader/downloads/list) ) from the
computer to the phone. If you have an old phone (that uses MIDP
1.0), copy midp10\_RSSReader.jar. The detailed steps are different
from one phone to other, so please read the manual of your phone
and the help of your communication program.

Alternatively, you can use your phone browser to download
RSSReader directly from this web.

Just enter this url for MIDP-2.0 (see above):
http://mobile-rss-reader.googlecode.com/files/midp20_RSSReader-1_11_1.jar

Just enter this url for MIDP-2.0, and JSR-74 (see above):
http://mobile-rss-reader.googlecode.com/files/midp20_jsr75_RSSReader-1_11_1.jar

If your phone is old (has MIDP-1.0), use this url:
http://mobile-rss-reader.googlecode.com/files/midp10_RSSReader-1_11_1.jar

> ## Detailed Features ##

> ### Reading from phone memory ###

Feeds and imports of feeds can be done from phone memory with phones with
JSR-75.  To see if your phone supports JSR-75 see
Which program to install.

> ### Importing Feeds ###

The import feature has filters.  This is because there are some OPML files
that have 1,500 (SIC) links or HTML files with lots of links.
This would cause the program to
run out of memory or to be unmanageble.
Say you only want the ones that are about Tech.
Use the name filter with 'Tech' in it to get just the ones with Tech.
Some sites have an HTML file (URL) with links to RSS.  You can use the
HTML Links format to import from this file.  This file will
likely have both links for RSS and other links.  Usually, the RSS
links have a pattern in them like rss or xml.  Put rss or xml in the
link filter to just import these.
You will need to open the page with view source and see what
to use as the filter.

## Build instructions ##


> ### All builds ###


You need to install the software (needed for build only)

  * [ant](http://ant.apache.org)
  * [antenna](http://antenna.sourceforge.net)version 1.1.0-beta.  Some of the previous versions don't run on Linux or have //#elif bug.
  * [ant-contrib](http://sourceforge.net/projects/ant-contrib)
  * Java VM either Sun's, JamVM, or GNU GIJ
  * Either compiler below
    1. Either [JDK 1.5 or later](http://download.java.net/jdk6/binaries)
    1. ECJ Eclipse compiler at [Eclipse downloads](http://download.eclipse.org/eclipse/downloads) then, go to a release >= 3.2.  Under JDT Core Batch an ecj.jar (usually contains a version)
  * Either Wireless Toolkit (WTK) below
    1. [phone ME](https://meapplicationdevelopers.dev.java.net/how_to_run.html)
    1. [Sun Wireless Toolkit](http://java.sun.com/products/sjwtoolkit/download-2_5.html)
    1. [Netbeans Mobility](http://www.netbeans.org/features/javame/index.html)
  * [proguard](http://sourceforge.net/projects/proguard)

To build using antenna, go to wtkbuild directory, copy the files ending in
.template to the file name without '.template'.
In wtk-build.properties, change antenna.home value to point to directory
of the antenna jar.  Set antenna.jar to the name of the antenna jar.
Change antcontrib.home to point to location of ant-contrib-1.0b3.jar.
In wtk-build.properties file for linux, change value wtk.home to
"<PHONEME\_INSTALL\_DIR</bin/linux\_x86\_fb\_chameleon\_mvm".
Comment out wtk.wme.home value for linux.
In wtk-build.properties file for windows, change value wtk.home to
"<PHONEME\_INSTALL\_DIR</bin/win32\_x86\_javacall\_mv".
Set wtk.wme.home value to localtion of Tools subdirectory under Websphere
Micro Edition (WME) install directory.  If you do not have WME, comment
out wtk.wme.home in wtk-build.properties.
out.
The following is for linux and windows.
Copy files under i386 to bin with -rp.
Uncomment property wtk.midpapi with value "${wtk.home}/classes".


To compile and build everything,
```


export ANT_HOME=(location of ant)
export JAVA_HOME=(one directory above location of java)
export JAVACMD=(location of java)
export PATH=$PATH:$ANT_HOME/bin
ant -f wtk-build.xml dist

```

> ## Run instructions ##

The program can be run on J2ME implementations, some GPL phone ME, or on
java emulators MicroEmulator (LGPL) and
kobject's (me4se) [GPL](GPL.md).