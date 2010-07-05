/*
 * RssReaderMIDlet.java
 *
 * Copyright (C) 2005-2006 Tommi Laukkanen
 * Copyright (C) 2007-2010 Irving Bunton, Jr
 * http://www.substanceofcode.com
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 */
/*
 * IB 2010-03-07 1.11.4RC1 Use observer pattern for feed parsing to prevent hangs from spotty networks and bad URLs.
 * IB 2010-03-14 1.11.5RC2 Fix logging.
 * IB 2010-03-14 1.11.5RC2 Synchronize use of opening or updating the feed.
 * IB 2010-03-14 1.11.5RC2 Fix previous page for loading screen in procPage.
 * IB 2010-03-14 1.11.5RC2 Combine if statements.
 * IB 2010-03-14 1.11.5RC2 Fix showing diagnostics.
 * IB 2010-05-24 1.11.5RC2 Code cleanup.
 * IB 2010-05-24 1.11.5RC2 Combine classes to save space.
 * IB 2010-05-24 1.11.5RC2 Log thread info for diagnostics.
 * IB 2010-05-24 1.11.5RC2 Use one thread for novice import.
 * IB 2010-05-24 1.11.5RC2 Fix mispelling of bookmarks.
 * IB 2010-05-24 1.11.5RC2 Only do export if signed.
 * IB 2010-05-28 1.11.5RC2 Use threads and CmdReceiver for MIDP 2.0 only.
 * IB 2010-05-29 1.11.5RC2 Fix opening of feed that causes parsing error with MIDP 1.0.
 * IB 2010-05-29 1.11.5RC2 Don't use HTML in small memory MIDP 1.0 to save space.
 * IB 2010-05-30 1.11.5RC2 Do export only for signed, Itunes and JSR-75.
 * IB 2010-05-31 1.11.5RC2 Move display of loading form before loading of settings to allow reporting of settings errors.
 * IB 2010-05-31 1.11.5RC2 Keep better track of loading finished.
 * IB 2010-05-31 1.11.5RC2 Change setCurrentNotes so that it will set loading finished if need be and use the current displayable if it's a LoadingForm or use a LoadingForm paramater.
 * IB 2010-06-01 1.11.5RC2 If we are finished loading or exiting, but there is no back screen, add quit.  Also allow quit if exiting with error.
 * IB 2010-06-02 1.11.5RC2 Use settings instance from RssReaderSettings to make sure that we share the same one.
 * IB 2010-06-27 1.11.5Dev2 Use ObservableHandler, Observer, and Observable re-written to use observer pattern without GPL code.  This is dual licensed as GPL and LGPL.
 * IB 2010-06-27 1.11.5Dev2 Make LoadingForm an independent class to remove dependency on RssReaderMIDlet for better testing.
 * IB 2010-06-27 1.11.5Dev2 Use volatile for m_firstTime in RssReaderMIDlet.
 * IB 2010-06-27 1.11.5Dev2 Have test reload db.
 * IB 2010-06-27 1.11.5Dev2 Make sure m_loadForm is not null when using RssReaderMIDlet.
 * IB 2010-06-27 1.11.5Dev2 Have static methods to load/save settings and bookmarks for better testing.
 * IB 2010-06-27 1.11.5Dev2 Have set current allow displayable as alert to handle alert bugs in the future.
 * IB 2010-06-27 1.11.5Dev2 Have static initSettingsEnabled to load app and general settings to help with testing.
 * IB 2010-06-27 1.11.5Dev2 Have static loadBookmarkList load bookmarks from settings DB to help with testing.
 * IB 2010-06-27 1.11.5Dev2 Make sure m_appSettings is not null when using RssReaderMIDlet.
 * IB 2010-06-27 1.11.5Dev2 Change command priorities to be in the right order and have update mod ahead of udpate all.
 * IB 2010-06-27 1.11.5Dev2 Set gauge based on max value to be more flexible.
 * IB 2010-06-27 1.11.5Dev2 Have procBookmarkExc to handle exceptions for init/load of bookmarks.
 * IB 2010-07-05 1.11.5Dev6 Use null pattern using nullPtr.
*/

// Expand to define test define
@DTESTDEF@
// Expand to define test ui define
@DTESTUIDEF@
// Expand to define MIDP define
@DMIDPVERS@
// Expand to define DJSR75 define
@DJSR75@
// Expand to define itunes define
@DITUNESDEF@
// Expand to define memory size define
@DMEMSIZEDEF@
// Expand to define signed define
@DSIGNEDDEF@
// Expand to define logging define
@DLOGDEF@

package com.substanceofcode.rssreader.presentation;

import java.lang.SecurityException;
//#ifdef DJSR75
import org.kablog.kgui.KFileSelectorMgr;
//#endif
import com.substanceofcode.rssreader.businessentities.RssItunesFeed;
import com.substanceofcode.rssreader.businessentities.RssFeed;
import com.substanceofcode.rssreader.businessentities.RssItunesItem;
import com.substanceofcode.rssreader.businessentities.RssReaderSettings;
import com.substanceofcode.rssreader.businesslogic.FeedListParser;
import com.substanceofcode.rssreader.businesslogic.LineByLineParser;
import com.substanceofcode.rssreader.businesslogic.RssFormatParser;
import com.substanceofcode.rssreader.businesslogic.RssFeedParser;
import com.substanceofcode.rssreader.presentation.AllNewsList;
import com.substanceofcode.utils.Settings;
import com.substanceofcode.utils.MiscUtil;
import com.substanceofcode.utils.CauseException;
import com.substanceofcode.utils.CauseRecStoreException;
import java.util.*;
import java.io.IOException;
import java.io.InputStream;
import javax.microedition.midlet.*;
import javax.microedition.io.ConnectionNotFoundException;
import javax.microedition.rms.*;
import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.AlertType;
import javax.microedition.lcdui.Choice;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Gauge;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.Item;
// If not using the test UI define the J2ME UI's
//#ifndef DTESTUI
import javax.microedition.lcdui.ChoiceGroup;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.List;
import javax.microedition.lcdui.TextBox;
import javax.microedition.lcdui.TextField;
import javax.microedition.lcdui.StringItem;
//#else
// If using the test UI define the Test UI's
import com.substanceofcode.testlcdui.ChoiceGroup;
import com.substanceofcode.testlcdui.Form;
import com.substanceofcode.testlcdui.List;
import com.substanceofcode.testlcdui.TextBox;
import com.substanceofcode.testlcdui.TextField;
import com.substanceofcode.testlcdui.StringItem;
//#endif
//#ifdef DTESTUI
import com.substanceofcode.testutil.presentation.TestingForm;
import com.substanceofcode.testutil.TestOutput;
//#endif

//#ifdef DJSR238
import javax.microedition.global.ResourceManager;
//#endif

//#ifdef DMIDP20
import net.yinlight.j2me.observable.Observer;
import net.yinlight.j2me.observable.Observable;
//#endif

//#ifdef DLOGGING
import net.sf.jlogmicro.util.logging.Logger;
import net.sf.jlogmicro.util.logging.LogManager;
import net.sf.jlogmicro.util.logging.Level;
import net.sf.jlogmicro.util.presentation.LoggerRptForm;
import net.sf.jlogmicro.util.logging.FormHandler;
import net.sf.jlogmicro.util.logging.RecStoreHandler;
//#endif

/**
 * RSS feed reader MIDlet
 *
 * RssReaderMIDlet is an application that can read RSS feeds. User can store
 * multiple RSS feeds as bookmarks into application's record store.
 *
 * @author  Tommi Laukkanen
 * @version 1.0
 */
public class RssReaderMIDlet extends MIDlet
implements 
//#ifdef DMIDP20
			Observer,
//#endif
	CommandListener,
	Runnable {
    
	final       Object nullPtr = null;
    final static public char CFEED_SEPARATOR = (char)4;
    final static public char OLD_FEED_SEPARATOR = '^';
    // Attributes
    private Display     m_display;          // The display for this MIDlet
    private Displayable m_prevDisp;         // The displayable to return to
    private Settings    m_settings = null;         // The settings
    private RssReaderSettings m_appSettings = null;// The application settings
    private Hashtable   m_rssFeeds = new Hashtable(); // The bookmark URLs
    private RssReaderMIDlet m_midlet;       // The display for this MIDlet
    final static public boolean JSR75_ENABLED =
	          (System.getProperty(
			"microedition.io.file.FileConnection.version") != null);
	//#ifdef DTEST
    private boolean     m_debugOutput = true; // Flag to write to output for test
	//#endif
    private boolean     m_getPage;          // The noticy flag for HTTP
    private boolean     m_openPage;         // Open the headers
    private boolean     m_saveBookmarks;    // The save bookmarks flag
    private boolean     m_exit;             // The exit application flag
    private boolean     m_saving;           // The saving settings flag
    private boolean     m_stored;           // The data stored flag
    private boolean     m_about;            // The about flag
    private boolean     m_getModPage;       // The noticy flag for modified HTTP
    private boolean     m_getSettingsForm;  // Flag to get settings form
    private boolean     m_getAddBMForm;     // Flag to get add bookmark form
    private boolean     m_getEditBMForm;    // Flag to get edit bookmark form
    private boolean     m_refreshAllFeeds;  // The notify flag for all feeds
    private boolean     m_refreshUpdFeeds;  // The notify flag for updated feeds
    private boolean     m_getImportForm;    // The noticy flag for going to Import Feed list
	//#ifdef DSIGNED
	//#ifdef DITUNES
	//#ifdef DJSR75
    private boolean     m_getExportForm;    // The noticy flag for going to Export Feed list
	//#endif
	//#endif
	//#endif
    private boolean     m_getFile;          // The noticy flag for getting find files form
    private boolean     m_selectDir;          // The noticy flag for selecting directories
    private boolean     m_runNews = false;  // Run AllNewsList form.
	//#ifdef DTEST
    // Get import form using URL from current bookmark
    private boolean     m_getTestImportForm = false; // Get import form 
	//#endif
	//#ifdef DTESTUI
	boolean m_headerNext = false; // Flag to control opening the next header
	boolean m_itemNext = false; // Flag to control opening the next item
	//#endif
	//#ifdef DTESTUI
	private int         m_headerIndex = -1; // Index in headers to auto test
    // Index in bookmarks to auto test by opening in edit
	// This counts up until the bookmark size is reached.
    private int         m_bookmarkIndex = -1;
    private int         m_bookmarkLastIndex = -1; // Last place when import current was selected
	//#endif
	// Tells us if this is the first time program was used.  This is
	// done by seeing if max item count is set.  We also set it after
	// showing the about.
    volatile private boolean m_firstTime = false;
    private boolean     m_novice = false;
    private boolean     m_itunesEnabled = false;
	//#ifdef DMIDP20
    private boolean     m_parseBackground = false;
	//#endif
	//#ifdef DLOGGING
    private boolean fineLoggable;
    private boolean finestLoggable;
	//#endif
	// This is a mark (icon) next to unread items (except on unread items
	// screen).  Given that many screens are small, it is optional as 
	// we don't want to reduce space for text.
    private Image           m_unreadImage = null;
    
    // Currently selected bookmark
    private int             m_curBookmark;  // The currently selected item
	//#ifdef DMIDP20
    private Observable      m_backGrParser = null; // The currently selected RSS in background
	//#endif
    
    // GUI items
    private FeatureList  m_bookmarkList;     // The bookmark list
	//#ifdef DTESTUI
    private HeaderList  m_headerTestList;       // The header list
    private AllNewsList m_allNewsTestList; // The test header list for unread items
	//#endif
    private Displayable m_itemRtnList;      // The list to return from for item
    private ItemForm    m_itemForm;         // The item form
    private LoadingForm m_loadForm;         // The "loading..." form
    private TextField   m_fileURL;          // The file URL field from a form
    private Form        m_fileRtnForm;      // The form to return to for file
	//#ifdef DTESTUI
    private TestingForm m_testingForm;    // The testing form
	//#endif
    
    // Commands
	//#ifdef DTESTUI
	private Command     m_testBMCmd;        // Test UI bookmarks list command
	private Command     m_testRtnCmd;       // Test UI return to prev command
	//#endif
    private Command     m_exitCommand = null;// The exit command
    private Command     m_saveCommand;      // The save without exit command
    private Command     m_addNewBookmark;   // The add new bookmark command
    private Command     m_openBookmark;     // The open bookmark command
    private Command     m_readUnreadItems;  // The read unread items command
    private Command     m_editBookmark;     // The edit bookmark command
    private Command     m_delBookmark;      // The delete bookmark command
    static public Command m_backCommand = null; // The back to header list command
    private Command     m_importFeedListCmd;// The import feed list command
	//#ifdef DSIGNED
	//#ifdef DMIDP20
	//#ifdef DJSR75
    private Command     m_exportFeedListCmd;// The export feed list command
	//#endif
	//#endif
	//#endif
	//#ifdef DTEST
    private Command     m_importCurrFeedListCmd; // The import feed list command and default current seleected feed
    private Command     m_reloadDbCmd; // The close and reload the database.
	//#endif
	//#ifdef DTESTUI
    private Command     m_testEncCmd;     // The test encoding
	//#endif
	//#ifdef DLOGGING
    private Command     m_debugCmd; // The back to bookmark list command
	                                      // from debug form
    private Command     m_backFrDebugCmd; // The back to bookmark list command
    private Command     m_clearDebugCmd; // The back to bookmark list command
	                                      // from debug form
	//#endif
    private Command     m_settingsCmd;      // The show settings command
    private Command     m_aboutCmd;      // The show About
    private Command     m_updateAllCmd;     // The update all command
    private Command     m_updateAllModCmd;  // The update all modified command
    
    private int citemLnkNbr = -1;
    private int citemEnclNbr = -1;
    private RssItunesItem citem = null;
	//#ifdef DLOGGING
    private LoggerRptForm m_debug;
    private Logger logger;
	//#endif
    
    public RssReaderMIDlet()
	throws SecurityException {
        m_display = Display.getDisplay(this);
		m_midlet = this;
        
		//#ifdef DTESTUI
		TestOutput.init(System.out, "UTF-8");
		//#endif

		//#ifdef DLOGGING
		try {
			initializeLoadingForm("Loading items...", null);
			m_loadForm.addQuit();
		} catch (Throwable t) {
			if (m_loadForm != null) {
				m_loadForm.recordExcForm("Internal error loading form.",
						t);
			}
		}
		try {
			LogManager logManager = LogManager.getLogManager();
			logManager.readConfiguration(this);
			logger = Logger.getLogger("RssReaderMIDlet");
			for (Enumeration eHandlers = logger.getParent().getHandlers().elements();
					eHandlers.hasMoreElements();) {
				Object ohandler = eHandlers.nextElement();
				if (ohandler instanceof FormHandler) {
					Form oform = (Form)((FormHandler)ohandler).getView();
					logger.finest("form=" + oform);
				}
			}
			logger = Logger.getLogger("RssReaderMIDlet");
			logger.info("RssReaderMIDlet started.");
			logger.info("RssReaderMIDlet has form handler=" + (m_debug != null));
			m_debug = new LoggerRptForm(logManager, this,
						this, "net.sf.jlogmicro.util.logging.FormHandler");
		} catch (Throwable t) {
			m_loadForm.appendMsg("Error initiating logging " +
					t.getClass().getName() + "," + t.getMessage());
			String [] msgs = LogManager.getLogManager().getStartMsgs();
			m_loadForm.addExc("msgs.length" + msgs.length, t);
			for (int ic = 0; ic < msgs.length; ic++) {
				m_loadForm.appendMsg(msgs[ic]);
			}
			System.out.println("Error initiating logging" + t);
			t.printStackTrace();
			return;
		}
		//#endif

		try {

			if (m_loadForm == null) {
				try {
					initializeLoadingForm("Loading items...", null);
					m_loadForm.addQuit();
				} catch (Throwable t) {
					if (m_loadForm != null) {
						m_loadForm.recordExcForm("Internal error loading form.",
								t);
					}
				}
			}

			//#ifdef DLOGGING
			fineLoggable = logger.isLoggable(Level.FINE);
			logger.fine("obj,fineLoggable=" + this + "," + fineLoggable);
			//#endif
			Object[] arrsettings = FeatureMgr.initSettingsEnabled(
					this, m_loadForm
				    //#ifdef DLOGGING
					,logger
					,fineLoggable
					//#endif
					);
			m_appSettings = (RssReaderSettings)arrsettings[0];
			m_settings = (Settings)arrsettings[1];
			m_firstTime = ((Boolean)arrsettings[2]).booleanValue();
			m_itunesEnabled = ((Boolean)arrsettings[3]).booleanValue();

			/** Initialize commands */
			//#ifdef DTESTUI
			m_testBMCmd         = new Command("Test bookmarks shown", Command.SCREEN, 9);
			m_testRtnCmd        = new Command("Test go back to last", Command.SCREEN, 10);
			//#endif
			if (m_backCommand == null) {
				m_backCommand       = new Command("Back", Command.BACK, 1);
			}
			initExit();
			m_openBookmark      = new Command("Open feed", Command.SCREEN, 2);
			m_addNewBookmark    = new Command("Add new feed", Command.SCREEN, 3);
			m_readUnreadItems   = new Command("River of news", Command.SCREEN, 4);
			m_editBookmark      = new Command("Edit feed", Command.SCREEN, 5);
			m_delBookmark       = new Command("Delete feed", Command.SCREEN, 6);
			m_importFeedListCmd = new Command("Import feeds", Command.SCREEN, 7);
			//#ifdef DSIGNED
			//#ifdef DITUNES
			//#ifdef DJSR75
			m_exportFeedListCmd = new Command("Export feeds", Command.SCREEN, 8);
			//#endif
			//#endif
			//#endif
			//#ifdef DTEST
			m_importCurrFeedListCmd = new Command("Import current feeds", Command.SCREEN, 9);
			//#endif
			m_updateAllModCmd   = new Command("Update modified all",
											  Command.SCREEN, 10);
			m_updateAllCmd      = new Command("Update all", Command.SCREEN, 11);
			//#ifdef DTEST
			m_reloadDbCmd       = new Command("Reload DB", Command.SCREEN, 12);
			//#endif
			m_settingsCmd       = new Command("Settings", Command.SCREEN, 13);
			m_saveCommand       = new Command("Save without exit", Command.SCREEN, 14);
			m_aboutCmd          = new Command("About", Command.SCREEN, 17);
			//#ifdef DTESTUI
			m_testEncCmd        = new Command("Testing Form", Command.SCREEN, 4);
			//#endif

		//#ifdef DLOGGING
			m_debugCmd          = new Command("Debug Log", Command.SCREEN, 4);
			m_clearDebugCmd     = new Command("Clear", Command.SCREEN, 1);
			m_backFrDebugCmd    = new Command("Back", Command.BACK, 2);
			//#endif
			
			m_getPage = false;
			m_exit = false;
			m_stored = false;
			m_saving = false;
			m_about = false;
			m_saveBookmarks = false;
			m_openPage = false;
			m_getModPage = false;
			m_getSettingsForm = false;
			m_getAddBMForm = false;
			m_getEditBMForm = false;
			m_refreshAllFeeds = false;
			m_refreshUpdFeeds = false;
			m_getImportForm = false;
			//#ifdef DSIGNED
			//#ifdef DITUNES
			//#ifdef DJSR75
			m_getExportForm = false;
			//#endif
			//#endif
			//#endif
			m_getFile = false;
			m_selectDir = false;
			m_curBookmark = -1;
			CauseException ce = null;
			
			// To get proper initialization, need to 
			//#ifdef DLOGGING
			if (m_appSettings != null) {
				if (m_appSettings.getLogLevel().length() == 0) {
					m_appSettings.setLogLevel(
							logger.getParent().getLevel().getName());
				} else {
					logger.getParent().setLevel(
					Level.parse(m_appSettings.getLogLevel()));
				}
			}
			//#endif

			//#ifdef DLOGGING
			fineLoggable = logger.isLoggable(Level.FINE);
			logger.fine("obj,fineLoggable=" + this + "," + fineLoggable);
			finestLoggable = logger.isLoggable(Level.FINEST);
			logger.fine("obj,finestLoggable=" + this + "," + finestLoggable);
			//#endif

			if ((m_appSettings != null) && m_appSettings.getMarkUnreadItems()) {
				try {
					try {
						// createImage("/icons/unread.png") does not always work
						// with the emulator.  so, I do an alternate which is
						// effectively the same thing.
						m_unreadImage = Image.createImage("/icons/unread.png");
					} catch(IOException e) {
						//#ifdef DMIDP20
						InputStream is =
								this.getClass().getResourceAsStream("/icons/unread.png");
						m_unreadImage = Image.createImage(is);
						is.close();
						//#endif
					}
				} catch(Throwable e) {
					ce = new CauseException("Error while getting mark image: " +
							e.toString(), e);
					System.err.println(ce.getMessage());
				}
			}
			
			if (ce != null) {
				m_loadForm.addExc(ce.getMessage(), ce);
			}

		} catch(Throwable t) {
			//#ifdef DLOGGING
			logger.severe("RssReaderMIDlet constructor ", t);
			//#endif
			/** Error while executing constructor */
			System.out.println("RssReaderMIDlet constructor " + t.getMessage());
			t.printStackTrace();
			if (m_loadForm == null) {
				initializeLoadingForm("Loading items...", null);
			}
            m_loadForm.addExc("Internal error starting applicaiton.", t);
		}
    }
    
	/* Create exit command based on if it's a standard exit. */
	final public void initExit() {
		boolean prevExit = (m_exitCommand != null);
		if (prevExit) {
			m_bookmarkList.removeCommand(m_exitCommand);
		}
		if (m_appSettings != null) {
			m_exitCommand       = new Command("Exit",
				(m_appSettings.getUseStandardExit() ? Command.EXIT
				 : Command.SCREEN), 15);
		} else {
			m_exitCommand       = new Command("Exit", Command.SCREEN, 15);
		}
		if (prevExit) {
			m_bookmarkList.addPromptCommand(m_exitCommand,
					"Are you sure you want to exit?");
		}
	}

	/* Initialize the forms that are not dynamic. */
	final private void initForms() {
		try {
			// Get here so that bookmarklist knows to not use some commands
			m_novice = (m_appSettings != null) && m_appSettings.getNovice();
			/** Initialize GUI items */
			initializeBookmarkList();
			//initializeLoadingForm();
			//#ifdef DLOGGING
			if (m_debug != null) {
				initializeDebugForm();
			}
			//#endif
			//#ifdef DTEST
			System.gc();
			long beginMem = Runtime.getRuntime().freeMemory();
			//#endif
			//#ifdef DTESTUI
			m_testingForm = new TestingForm(this);
			//#endif
			//#ifdef DTEST
			System.gc();
			System.out.println("TestingForm size=" + (beginMem - Runtime.getRuntime().freeMemory()));
			//#endif
			
			//#ifdef DLOGGING
			if (fineLoggable) {logger.fine("m_novice=" + m_novice);}
			//#endif
			if( m_firstTime ) {
				try {
					// Set Max item count to default so that it is initialized.
					if (m_appSettings != null) {
						m_appSettings.setMaximumItemCountInFeed(
								m_appSettings.getMaximumItemCountInFeed());
					}
					saveBkMrkSettings("Initializing database...",
							System.currentTimeMillis(), m_firstTime, false,
							m_rssFeeds, m_bookmarkList, m_loadForm, m_settings
							//#ifdef DLOGGING
							,logger
							,finestLoggable
							//#endif
							);
					// If novice, show about later.
					if (!m_novice) {
						m_firstTime = false;
						Alert m_about = getAbout();
						setCurrentNotes( m_about, m_bookmarkList );
					}
				} catch(Exception e) {
					System.err.println("Error while getting/updating settings: " + e.toString());
					m_loadForm.replaceRef(null, m_bookmarkList);
					m_loadForm.recordExcForm("Internal error.  Unable to initialize forms",
							e);
				}
			} else {
				// If not novice, show bookmark.  If we are novice,
				// we only show novice if we have already loaded the
				// novice bookmarks.
				if (!m_novice) {
					setCurrentNotes( m_bookmarkList );
				}
			}

		}catch(Throwable t) {
			//#ifdef DLOGGING
			logger.severe("initForms ", t);
			//#endif
			/** Error while initializing forms */
			System.out.println("initForms " + t.getMessage());
			t.printStackTrace();
			m_loadForm.replaceRef(null, m_bookmarkList);
			m_loadForm.recordExcForm("Internal error.  Unable to initialize forms",
					t);
		}
		//#ifdef DTEST
		System.gc();
		System.out.println("Initial memory size=" + (Runtime.getRuntime().freeMemory() / 1024L) + "kb");
		//#endif
    }
    
    /** Get application settings */
    final public RssReaderSettings getSettings() {
        return m_appSettings;
    }
    
    /** Show bookmark list */
    final public void showBookmarkList() {
		//#ifdef DLOGGING
		if (fineLoggable) {logger.fine("before m_itunesEnabled=" + m_itunesEnabled);}
		//#endif
		m_itunesEnabled = (m_appSettings != null) &&
			m_appSettings.getItunesEnabled();
		//#ifdef DLOGGING
		if (fineLoggable) {logger.fine("after m_itunesEnabled=" + m_itunesEnabled);}
		//#endif
		setCurrent( m_bookmarkList );
    }
    
    /** Load bookmarks from record store */
    final private void initializeBookmarkList() {
		//#ifdef DTEST
		System.gc();
		long beginMem = Runtime.getRuntime().freeMemory();
		//#endif
		int nbrRegions = m_firstTime ? (m_novice ? 2 : 1) : (Settings.MAX_REGIONS + 1);
		Gauge gauge = new Gauge("Initializing bookmarks...",
				false, nbrRegions, 0);
		//#ifdef DLOGGING
		if (finestLoggable) {logger.finest("Settings.MAX_REGIONS,gauge.getMaxValue()=" + Settings.MAX_REGIONS + "," + gauge.getMaxValue());}
		//#endif
        try {
            m_bookmarkList = new FeatureList(this, "Bookmarks", List.IMPLICIT);
			//#ifdef DMIDP20
			// If font is wrong, it can cause an exception for some
			// devices.  This leaves some of the data not loaded.
			// So, we'll update this later
            m_bookmarkList.setFont(null);
			//#endif
			if (!m_novice) {
				m_bookmarkList.addCommand( m_addNewBookmark );
			}
            m_bookmarkList.addCommand( m_openBookmark );
            m_bookmarkList.addCommand( m_readUnreadItems );
			if (!m_novice) {
				m_bookmarkList.addCommand( m_editBookmark );
				m_bookmarkList.addPromptCommand( m_delBookmark,
											 "Are you sure you want to delete?" );
			}
			if (!m_novice) {
				m_bookmarkList.addCommand( m_importFeedListCmd );
				//#ifdef DSIGNED
				//#ifdef DJSR75
				//#ifdef DITUNES
				m_bookmarkList.addCommand( m_exportFeedListCmd );
				//#endif
				//#endif
				//#endif
				//#ifdef DTEST
				m_bookmarkList.addCommand( m_importCurrFeedListCmd );
				//#endif
			}
            m_bookmarkList.addCommand( m_updateAllModCmd );
            m_bookmarkList.addCommand( m_updateAllCmd );
			//#ifdef DTEST
            m_bookmarkList.addCommand( m_reloadDbCmd );
			//#endif
            m_bookmarkList.addCommand( m_saveCommand );
            m_bookmarkList.addCommand( m_settingsCmd );
            m_bookmarkList.addPromptCommand( m_exitCommand,
					                         "Are you sure you want to exit?" );
            m_bookmarkList.addCommand( m_aboutCmd );
			//#ifdef DTESTUI
            m_bookmarkList.addCommand( m_testBMCmd );
            m_bookmarkList.addCommand( m_testRtnCmd );
			//#endif
			//#ifdef DTESTUI
			m_bookmarkList.addCommand( m_testEncCmd );
			//#endif
	//#ifdef DLOGGING
			if (m_debug != null) {
				m_bookmarkList.addCommand( m_debugCmd );
			}
	//#endif
            m_bookmarkList.setCommandListener( this, true );
			//#ifdef DTEST
			System.gc();
			System.out.println("empty bookmarkList size=" + (beginMem - Runtime.getRuntime().freeMemory()));
			//#endif
            
			loadBookmarkList(gauge, m_rssFeeds, m_bookmarkList, m_loadForm,
							 m_settings,
							 m_firstTime
						//#ifdef DTEST
						,m_debugOutput
						//#endif
						//#ifdef DLOGGING
						,logger
						,fineLoggable
						//#endif
					);

			//#ifdef DMIDP20
			if ((m_appSettings != null) &&
					(m_appSettings.getFontChoice() !=
					RssReaderSettings.DEFAULT_FONT_CHOICE)) {
				final int len = m_bookmarkList.size();
				m_bookmarkList.initFont(this);
				final Font font = m_bookmarkList.getFont();
				for (int ic = 0; ic < len; ic++) {
					m_bookmarkList.setFont(ic, font);
				}
			}
			//#endif
		} catch (Throwable t) {
			procBookmarkExc("Error while initializing bookmark list", t,
					m_loadForm
						//#ifdef DLOGGING
						,logger
						//#endif
					);
		}
	}

    /** Load bookmarks from record store */
    static final public void loadBookmarkList(Gauge gauge,
			Hashtable rssFeeds, final Choice bookmarkList,
			final LoadingForm loadForm,
			final Settings settings,
			final boolean firstTime
					//#ifdef DTEST
					,boolean debugOutput
					//#endif
				//#ifdef DLOGGING
				,Logger logger
				,boolean fineLoggable
				//#endif
			) {

		//#ifdef DTEST
		System.gc();
		long beginMem = Runtime.getRuntime().freeMemory();
		//#endif
		//#ifdef DLOGGING
		if (fineLoggable) {logger.fine("loadBookmarkList firstTime=" + firstTime);}
		//#endif
		int pl = -1;
		try {
			if (gauge != null) {
				pl = loadForm.append(gauge);
			}
			if (!firstTime) {
				for (int ic = 1; ic < Settings.MAX_REGIONS; ic++) {
					boolean stop = false;
					String bms = settings.getStringProperty(ic, Settings.BOOKMARKS_NAME, "");
					//#ifdef DLOGGING
					if (fineLoggable) {logger.fine("loadBookmarkList bms.length()=" + bms.length());}
					//#endif
					try {
						if(bms.length() == 0) {
							continue;
						}
						final String vers = settings.getStringProperty(ic,
								Settings.SETTINGS_NAME, "");
						final boolean firstSettings =
							 vers.equals(Settings.FIRST_SETTINGS_VERS);
						final boolean itunesCapable = ((vers.length() > 0) &&
							 (vers.compareTo(Settings.ITUNES_CAPABLE_VERS) >= 0));
						final boolean encodingSettings = ((vers.length() > 0) &&
							 (vers.compareTo(Settings.ENCODING_VERS) >= 0));
						final boolean modifiedSettings = vers.equals(
								Settings.MODIFIED_VERS);
						settings.getBooleanProperty(Settings.ITEMS_ENCODED,
									true);
						/* FUTURE
						final long storeDate = settings.getLongProperty(
								Settings.STORE_DATE, 0L);
							*/
						final char feedSeparator =
							encodingSettings ? CFEED_SEPARATOR : OLD_FEED_SEPARATOR;
						//#ifdef DLOGGING
						if (fineLoggable) {logger.fine("loadBookmarkList region,vers,firstSettings,itunesCapable,encodingSettings,modifiedSettings=" + ic + "," + vers + "," + firstSettings + "," + itunesCapable + "," + encodingSettings + "," + modifiedSettings);}
						//#endif
						//#ifdef DTEST
						if (debugOutput) System.out.println("loadBookmarkList region,vers,firstSettings,itunesCapable,encodingSettings,modifiedSettings=" + ic + "," + vers + "," + firstSettings + "," + itunesCapable + "," + encodingSettings + "," + modifiedSettings);
						//#endif
						// Save memory by setting bookmarks to "" now that
						// we will convert them to objects.
						settings.setStringProperty(Settings.BOOKMARKS_NAME, "");
						
						do{
							
							String part = "";
							int pos = bms.indexOf(feedSeparator);
							if(pos > 0) {
								part = bms.substring(0, pos);
							}
							bms = bms.substring(pos+1);
							if(part.length()>0) {
								RssItunesFeed bm = null;
								if (itunesCapable) {
									bm = RssItunesFeed.deserialize(modifiedSettings,
											true, part );
								} else {
									bm = new RssItunesFeed(new RssFeed(
												firstSettings, true, part ));
								}
								if(bm.getName().length()>0){
									bookmarkList.append(bm.getName(),null);
									rssFeeds.put(bm.getName(), bm);
								}
							}
							if( part.length()==0)
								stop = true;
						}while(!stop);
					} finally {
						if (gauge != null) {
							gauge.setValue(ic);
						}
					}
				}
				//#ifdef DTEST
				System.gc();
				System.out.println("full bookmarkList size=" + (beginMem - Runtime.getRuntime().freeMemory()));
				//#endif
			}
			pl = -1;
			if (gauge != null) {
				gauge.setValue(gauge.getMaxValue());
			}
			// Reset internal region to 0.
			settings.getStringProperty(0, Settings.BOOKMARKS_NAME, "");
			//#ifdef DTEST
			System.gc();
			System.out.println("full bookmarkList size=" + (beginMem - Runtime.getRuntime().freeMemory()));
			//#endif
		} catch(Throwable t) {
			procBookmarkExc("Error while loading bookmark list", t, loadForm
						//#ifdef DLOGGING
						,logger
						//#endif
					);
		} finally {
			if (pl >= 0) {
				loadForm.delete(pl);
			}
		}
    }
    
	static public void procBookmarkExc(String excMsg, Throwable t,
								  final LoadingForm loadForm
								  //#ifdef DLOGGING
								  ,Logger logger
								  //#endif
			) {
		if (t instanceof Exception) {
			Exception e = (Exception)t;
			loadForm.recordExcForm(excMsg, e);
		} else if (t instanceof OutOfMemoryError) {
			OutOfMemoryError e = (OutOfMemoryError)t;
			CauseException ce = new CauseException(excMsg, e);
			loadForm.recordExcForm(
					"Out Of Memory Error initializing/loading form", ce);
		} else {
			CauseException ce = new CauseException(excMsg, t);
			loadForm.recordExcForm(
					"Internal error initializing/loading form", ce);
		}
	}

					/** Show loading form */
					final public void showLoadingForm() {
						setCurrent( m_loadForm );
					}
					
					/** Initialize loading form */
					final public LoadingForm initializeLoadingForm(final String desc,
													   Displayable disp,
													   //#ifdef DMIDP20
													   Observable observable
													   //#else
													   Object observable
													   //#endif
							)
					{
		m_loadForm = LoadingForm.getLoadingForm(desc, disp, m_bookmarkList,
				observable, m_midlet);
		setCurrent( m_loadForm );
		return m_loadForm;
    }

    final public LoadingForm initializeLoadingForm(final String desc,
									   Displayable disp) {
		return initializeLoadingForm(desc, disp, null);
	}

	//#ifdef DLOGGING
    final public void initializeDebugForm() {
        m_debug.addCommand( m_backFrDebugCmd );
        m_debug.addCommand( m_clearDebugCmd );
        m_debug.setCommandListener(this);
	}
	//#endif

    /** Run method is used to get RSS feed with HttpConnection, etc */
    public void run(){
		try {

			//#ifdef DTESTUI
			// If there are headers, and the header index is >= 0,
			// open the header so that it's items can be listed
			// with test UI classes.
			// Need to change the selection to match the m_headerIndex.
			if (m_headerNext && (m_headerIndex >= 0) &&
					(m_headerTestList != null) &&
				(m_headerIndex < m_headerTestList.size()) &&
				(m_display.getCurrent() == m_headerTestList)) {
				m_headerNext = false;
				if (m_headerTestList.getSelectedIndex() >= 0) {
					m_headerTestList.setSelectedIndex(
							m_headerTestList.getSelectedIndex(), false);
				}
				m_headerTestList.setSelectedIndex(m_headerIndex, true);
				m_headerTestList.commandAction(List.SELECT_COMMAND,
						m_headerTestList);
			}
			// After intializing the form (which was already logged by
			// testui classes), simulate the back command
			if (m_itemNext && (m_headerIndex >= 0) &&
					(m_headerTestList != null) &&
				(m_headerIndex < m_headerTestList.size()) &&
				(m_display.getCurrent() == m_itemForm )) {
				m_itemNext = false;
				m_itemForm.commandAction( m_backCommand, m_itemForm );
				m_headerIndex++;
				if (m_headerIndex >= m_headerTestList.size()) {
					System.out.println("Test UI Test Rss items last");
					m_headerIndex = -1;
				}
			}
			//#endif

			/* Handle going to settings form. */
			if( m_getSettingsForm ) {
				m_getSettingsForm = false;
				initializeLoadingForm("Loading settings...", m_bookmarkList);
				try{
					//#ifdef DTEST
					System.gc();
					long beginMem = Runtime.getRuntime().freeMemory();
					//#endif
					final SettingsForm settingsForm = new SettingsForm(this);
					settingsForm.setCommandListener( settingsForm, false );
        
					settingsForm.updateForm();
					//#ifdef DTEST
					System.gc();
					System.out.println("SettingsForm size=" +
							(beginMem - Runtime.getRuntime().freeMemory()));
					//#endif
					setCurrent( settingsForm );
				} catch(OutOfMemoryError t) {
					m_loadForm.recordExcForm("\nOut Of Memory Error " +
							"loading settings form", t);
				} catch(Throwable t) {
					m_loadForm.recordExcForm("\nInternal error loading settings " +
							"form", t);
				}
			}

			/* Handle going to bookmark form. */
			if( m_getAddBMForm || m_getEditBMForm ) {
				RssItunesFeed bm = null;
				try{
					if( m_getAddBMForm ) {
						initializeLoadingForm("Loading add bookmark...",
								m_bookmarkList);
					} else {
						initializeLoadingForm("Loading edit bookmark...",
								m_bookmarkList);
					}
					//#ifdef DTEST
					System.gc();
					long beginMem = Runtime.getRuntime().freeMemory();
					//#endif
					BMForm bmForm = null;
					if (m_getEditBMForm) {
						bm = (RssItunesFeed)m_rssFeeds.get(
								m_bookmarkList.getString(m_curBookmark));
						bmForm = new BMForm(this, m_rssFeeds, m_appSettings,
								m_bookmarkList, m_loadForm, bm);
					} else {
						bmForm = new BMForm(this, m_rssFeeds, m_appSettings,
								m_bookmarkList, m_loadForm);
					}
					bmForm.setCommandListener( bmForm, false );
					//#ifdef DTEST
					System.gc();
					System.out.println("BMForm size=" +
							(beginMem - Runtime.getRuntime().freeMemory()));
					//#endif
					setCurrent( bmForm );
				} catch(OutOfMemoryError t) {
					m_loadForm.recordExcForm("\nOut Of Memory Error loading " +
							"bookmark form", t);
				} catch(Throwable t) {
					m_loadForm.recordExcForm("\nInternal error loading bookmark " +
							"form" + ((m_getEditBMForm ? (" " + bm.getName()) : " ")), t);
				} finally {
					m_getAddBMForm = false;
					m_getEditBMForm = false;
				}
			}

			if( m_refreshAllFeeds || m_refreshUpdFeeds ) {
				LoadingForm cloadForm = initializeLoadingForm(
						"Updating all " +
						(m_refreshUpdFeeds ? "modified " : "") +
						"feeds...", m_bookmarkList);
				try{
					boolean errFound = false;
					final int maxItemCount = m_appSettings.getMaximumItemCountInFeed();
					Enumeration feedEnum = m_rssFeeds.elements();
					while(feedEnum.hasMoreElements()) {
						RssItunesFeed feed = (RssItunesFeed)feedEnum.nextElement();
						try{
							cloadForm.appendMsg(feed.getName() + "...");
							RssFeedParser parser = new RssFeedParser( feed );
							parser.parseRssFeed( m_refreshUpdFeeds,
									maxItemCount);
							cloadForm.appendMsg("ok\n");
						} catch(Exception ex) {
							CauseException ce = new CauseException(
									"Error parsing feed " + feed.getName(),
									ex);
							//#ifdef DLOGGING
							logger.severe(ce.getMessage(), ex);
							//#endif
							cloadForm.addExc("Error\n", ce);
							System.out.println(ce.getMessage());
							errFound = true;
						}
					}
					if (errFound) {
						cloadForm.setLoadingFinished(
								"Finished with one or more exceptions " +
								"or errors.",
								"Updating finished with one or more " +
								"exceptions or errors..");
						setCurrent( cloadForm );
					} else {
						cloadForm.setLoadingFinished("Updating finished",
								"Updating finished use back to return.");
						showBookmarkList();
					}
				} catch(Exception ex) {
					cloadForm.recordExcForm("Error parsing feeds\n", ex);
				} catch(OutOfMemoryError ex) {
					cloadForm.recordExcForm("Out Of Memory Error parsing feeds \n",
							ex);
				} catch(Throwable t) {
					cloadForm.recordExcForm("Internal error parsing feeds from:\n",
							t);
				} finally {
					m_refreshAllFeeds = false;
					m_refreshUpdFeeds = false;
				}
			}

			// Go to import feed form
			if( m_getImportForm
				//#ifdef DSIGNED
				//#ifdef DJSR75
				//#ifdef DITUNES
					|| m_getExportForm
				//#endif
				//#endif
				//#endif
					) {
				try {
					initializeLoadingForm("Loading " +
							(m_getImportForm ? "import" : "export") +
							" form...",
							m_bookmarkList);
					//#ifdef DTEST
					System.gc();
					long beginMem = Runtime.getRuntime().freeMemory();
					//#endif
					ImportFeedsForm importFeedsForm;
					//#ifdef DTEST
					if (m_getTestImportForm) {
						RssItunesFeed bm = (RssItunesFeed)m_rssFeeds.get(
								m_bookmarkList.getString(m_curBookmark));
						importFeedsForm = new ImportFeedsForm(this,
								m_bookmarkList, m_getImportForm, m_rssFeeds,
								m_appSettings, m_loadForm, bm.getUrl());
					} else
					//#endif
					{
						importFeedsForm = new ImportFeedsForm(this,
								m_bookmarkList, m_getImportForm, m_rssFeeds,
								m_appSettings,
								m_loadForm, m_appSettings.getImportUrl());
					}
					importFeedsForm.setCommandListener(importFeedsForm, true);
					//#ifdef DTEST
					System.gc();
					System.out.println("ImportForm size=" + (beginMem - Runtime.getRuntime().freeMemory()));
					//#endif
					setCurrent( importFeedsForm );
				} catch(Exception ex) {
					m_loadForm.recordExcForm("Error parsing feeds from:\n",
							ex);
				} catch(OutOfMemoryError ex) {
					m_loadForm.recordExcForm("Out Of Memory Error parsing feeds \n",
							ex);
				} catch(Throwable t) {
					m_loadForm.recordExcForm("Internal error parsing feeds from:\n",
							t);
				} finally {
					m_getImportForm = false;
					//#ifdef DSIGNED
					//#ifdef DITUNES
					//#ifdef DJSR75
					m_getExportForm = false;
					//#endif
					//#endif
					//#endif
					//#ifdef DTEST
					m_getTestImportForm = false;
					//#endif
				}
			}

			//#ifdef DTESTUI
			if ((m_bookmarkIndex < m_bookmarkList.size()) &&
				(m_bookmarkIndex >= 0)) {
				if (m_bookmarkList.getSelectedIndex() >= 0) {
					m_bookmarkList.setSelectedIndex(
							m_bookmarkList.getSelectedIndex(), false);
				}
				m_bookmarkList.setSelectedIndex(m_bookmarkIndex, true);
				commandAction(m_editBookmark, m_bookmarkList);
				m_bookmarkIndex++;
				if (m_bookmarkIndex >= m_bookmarkList.size()) {
					m_bookmarkIndex = -1;
					System.out.println("Test UI Test Rss feeds last");
				}
			}

			//#endif


			/* Sort the read or unread items. */
			if ( m_runNews ) {
				try {
					initializeLoadingForm("Sorting items...",
							m_bookmarkList);
					AllNewsList allNewsList = new AllNewsList(this,
							AllNewsList.TITLE, List.IMPLICIT, 0,
						m_bookmarkList.size(), m_bookmarkList, m_rssFeeds,
								m_unreadImage, m_loadForm, 3);
					// Need to do this before the tread starts to avoid
					// race conditions
					allNewsList.sortUnreadItems( true,
							m_bookmarkList, m_rssFeeds );
					allNewsList.setCommandListener(allNewsList, true);
					//#ifdef DTESTUI
					m_allNewsTestList = allNewsList;
					//#endif
					setCurrentNotes( allNewsList );
				}catch(OutOfMemoryError t) {
					m_loadForm.recordExcForm("\nOut Of Memory Error sorting items", t);
				}catch(Throwable t) {
					m_loadForm.recordExcForm("\nInternal error sorting items", t);
				} finally {
					m_runNews = false;
				}
			}

			if ( m_about ) {
				m_about = false;
				final Alert aboutAlert = getAbout();
				// Because of problems with alerts on T637, need to
				// show a form before we show the alert, or it never
				// appears.
				initializeLoadingForm(aboutAlert.getString(),
						m_bookmarkList);
				setCurrent( aboutAlert, m_bookmarkList );
			}

			synchronized(this) {
				if ( m_exit || m_saveBookmarks ) {
					exitApp();
				}
			}

		} catch (Throwable t) {
			try {
				if (m_loadForm == null) {
					synchronized(this) {
						if (m_loadForm == null) {
							initializeLoadingForm("Processing...",
									m_bookmarkList);
						}
					}
				}
				CauseException ce = new CauseException(
						"\nInternal error while processing", t);
				//#ifdef DLOGGING
				logger.severe(ce.getMessage(), t);
				//#endif
				/** Error while parsing RSS feed */
				System.out.println("Throwable Error: " + t.getMessage());
				t.printStackTrace();
				m_loadForm.addExc(ce.getMessage(), ce);
				setCurrent( m_loadForm );
			} catch (Throwable e) {
				t.printStackTrace();
				final Alert internalAlert = new Alert(
						"Internal error", 
						"Internal error while processing",
						null,
						AlertType.WARNING);
				internalAlert.setTimeout(Alert.FOREVER);
				setCurrent( internalAlert );
			}
		}
    }
	
	//#ifdef DTESTUI
	final public void updHeaderNext() {
		if (m_headerIndex >= 0) {
			m_headerNext = true;
		}
	}
	//#endif

	// Open existing bookmark and show headers (items).
	final public void setPageInfo(boolean openPage, boolean getPage,
		boolean getModPage, Displayable prevDisp) {
		synchronized(this) {
			m_openPage = openPage;
			m_getPage = getPage;
			m_getModPage = getModPage;
			m_prevDisp = prevDisp;
		}
	}

	// Open existing bookmark and show headers (items).
	final public void procPage(RssItunesFeed feed) {

		Displayable cprevDisp = null;
		boolean     copenPage = false;
		synchronized(this) {
			cprevDisp = m_prevDisp;
			copenPage = m_openPage;
		}

		//#ifdef DLOGGING
		if (finestLoggable) {logger.finest("procPage copenPage,cprevDisp,feed=" + copenPage + "," + cprevDisp + "," + feed);}
		//#endif

		// Open existing bookmark and show headers (items).
		try {
			/* Loading feed... */
			initializeLoadingForm("Loading feed...", cprevDisp);
			if(feed.getUrl().length() == 0) {
				m_loadForm.recordExcFormFin("Unable to open feed.  No URL.",
						new Exception(
						"Feed has no URL cannot load."));
				return;
			}
			procHeader(feed);
		}catch(Throwable e) {
			procPageExc(feed, copenPage, e);
		} finally {
			synchronized(this) {
				m_openPage = false;
			}
		}
	}

	//#ifdef DMIDP20
	// Open existing bookmark and show headers (items).
	final public RssFeedParser procBackPage(RssItunesFeed feed, Observer obs1) {

		RssFeedParser cbackGrParser = new RssFeedParser(new RssItunesFeed(feed));
		Displayable cprevDisp = null;
		boolean     cgetPage = false;
		boolean     cgetModPage = false;
		boolean     cparseBackground = false;
		synchronized(this) {
			cprevDisp = m_prevDisp;
			cgetPage = m_getPage;
			cgetModPage = m_getModPage;
			cparseBackground = m_parseBackground;
		}

		//#ifdef DLOGGING
		if (finestLoggable) {logger.finest("procBackPage cgetPage,cgetModPage,cprevDisp,feed=" + cgetPage + "," + cgetModPage + "," + cprevDisp + "," + feed);}
		//#endif

		// Open existing bookmark and show headers (items).
		if( cgetPage || cgetModPage ) {
			try {
				/* Updating feed... */
				initializeLoadingForm(
						cgetModPage ? "Updating modified feed..." :
						"Updating feed..." , cprevDisp, cbackGrParser);
				if(feed.getUrl().length() == 0) {
					m_loadForm.recordExcFormFin("Unable to open feed.  No URL.",
							new Exception(
							"Feed has no URL cannot load."));
					return null;
				}
				if (cparseBackground) {
					m_midlet.getLoadForm().appendNote("Note: feed is still parsing.  Wait for it to finish.");
					return null;
				}
				synchronized(this) {
					m_parseBackground = true;
				}
				/** Get RSS feed */
				final int maxItemCount =
					m_appSettings.getMaximumItemCountInFeed();
				//#ifdef DTEST
				System.gc();
				long beginMem = Runtime.getRuntime().freeMemory();
				//#endif
				cbackGrParser.makeObserable(this,
						cgetModPage, maxItemCount);
				m_loadForm.addPromptCommand(m_backCommand,
									"Are you sure that you want to go back? Parsing has not finished.");
				if (obs1 != null) {
					cbackGrParser.getObservableHandler().addObserver(obs1);
				}
				cbackGrParser.getObservableHandler().addObserver(this);
				((RssFeedParser)cbackGrParser).getParsingThread().start();
				synchronized(this) {
					m_backGrParser = cbackGrParser;
				}
			}catch(Throwable e) {
				procPageExc(feed, false, e);
			} finally {
				synchronized(this) {
					m_getPage = false;
					m_openPage = false;
					m_getModPage = false;
				}
			}
		}
		return cbackGrParser;
	}
	//#endif

	// Open existing bookmark and show headers (items).
	final public void procHeader(RssItunesFeed feed) {

		//#ifdef DLOGGING
		if (finestLoggable) {logger.finest("procHeader feed=" + feed);}
		//#endif
		// Open existing bookmark and show headers (items).

		try {

			//#ifdef DTEST
			System.gc();
			long beginMem = Runtime.getRuntime().freeMemory();
			//#endif
			HeaderList hdrList = new HeaderList(this, m_bookmarkList,
				m_curBookmark, m_rssFeeds,
				m_unreadImage, m_itunesEnabled, m_loadForm,
				feed);
			//#ifdef DTEST
			System.out.println("headerList size=" + (beginMem - Runtime.getRuntime().freeMemory()));
			//#endif
			hdrList.sortAllItems( false, m_bookmarkList, m_rssFeeds );
			hdrList.setCommandListener(hdrList, true);
			setCurrent( hdrList );
			m_loadForm.replaceRef(null, hdrList);
		}catch(Exception e) {
			m_loadForm.recordExcFormFin(
					"\nError loading feed on:\n" +
					feed.getUrl(), e);

		}catch(OutOfMemoryError e) {
			m_loadForm.recordExcFormFin(
					"\nOut of memory loading feed on:\n" +
					feed.getUrl(), e);
		}catch(Throwable t) {
			m_loadForm.recordExcFormFin(
					"\nInternal error loading " +
						"feed on:\n" +
					feed.getUrl(), t);
		} finally {
			synchronized(this) {
				m_openPage = false;
			}
		}
	}

    //#ifdef DMIDP20
	public RssFeedParser checkActive(Observable observable) {
		synchronized(this) {
			observable = observable.getObservableHandler().checkActive(m_parseBackground,
					m_backGrParser, observable);
		}
		if ((observable == null) || !(observable instanceof RssFeedParser)) {
			return null;
		}
		return (RssFeedParser)observable;
	}

	// Open existing bookmark and show headers (items).
	final public void procBackHeader(RssFeedParser cbackGrRssParser,
			Observable observable) {

		cbackGrRssParser = checkActive(observable);
		if (cbackGrRssParser == null) {
			return;
		}

		//#ifdef DLOGGING
		if (finestLoggable) {logger.finest("procBackHeader m_parseBackground=" + m_parseBackground);}
		//#endif
		procUpdHeader(cbackGrRssParser);
	}
	//#endif

	final public void procUpdHeader(RssFeedParser parser) {
		// Open existing bookmark and show headers (items).
		//#ifdef DLOGGING
		if (finestLoggable) {logger.finest("procUpdHeader m_getPage,m_getModPage,parser=" + m_getPage + "," + m_getModPage + "," + parser);}
		//#endif
		// Open existing bookmark and show headers (items).

		RssItunesFeed feed = parser.getRssFeed();
		try {

			if(!parser.isSuccessfull()) {
				throw parser.getEx();
			}
			//#ifdef DTEST
			System.gc();
			long beginMem = Runtime.getRuntime().freeMemory();
			//#endif
			m_rssFeeds.put(feed.getName(), feed);
			HeaderList hdrList = null;
			if (m_prevDisp instanceof HeaderList) {
				hdrList = (HeaderList)m_prevDisp;
			} else {
				hdrList = new HeaderList(this, m_bookmarkList,
					m_curBookmark, m_rssFeeds,
					m_unreadImage, m_itunesEnabled, m_loadForm,
					feed);
			}
			//#ifdef DTEST
			System.out.println("headerList size=" + (beginMem - Runtime.getRuntime().freeMemory()));
			//#endif
			hdrList.sortAllItems( false, m_bookmarkList, m_rssFeeds );
			hdrList.setCommandListener(hdrList, true);
			setCurrent( hdrList );
			m_loadForm.replaceRef(null, hdrList);
		}catch(Exception e) {
			m_loadForm.recordExcFormFin(
					"\nError parsing feed on:\n" +
					parser.getRssFeed().getUrl(), e);

		}catch(OutOfMemoryError e) {
			m_loadForm.recordExcFormFin(
					"\nOut of memory parsing feed on:\n" +
					parser.getRssFeed().getUrl(), e);
		}catch(Throwable t) {
			m_loadForm.recordExcFormFin(
					"\nInternal error parsing feed on:\n" +
					parser.getRssFeed().getUrl(), t);
		} finally {
			synchronized(this) {
				if (parser != null) {
					m_loadForm.removeCommandPrompt(m_backCommand);
					//#ifdef DMIDP20
					m_parseBackground = false;
					//#ifdef DLOGGING
					if (finestLoggable) {logger.finest("procUpdHeader m_parseBackground=" + m_parseBackground);}
					//#endif
					//#endif
				}
			}
		}
	}

  /**
   * Get file in file system
   * Constructor
   * @author Irv Bunton
   */
	final public void getFile() {
		//#ifdef DJSR75
		/* Find files in the file system to get for bookmark or
		   import from. */
		boolean cgetFile = false;
		boolean cselectDir = false;
		Form cfileRtnForm = null;
		TextField   cfileURL;
		synchronized(this) {
			cselectDir = m_selectDir;
			cfileRtnForm = m_fileRtnForm;
			cfileURL = m_fileURL;
			cgetFile = m_getFile;
		}
		if( cgetFile ) {
			try {
				if (cfileRtnForm instanceof ImportFeedsForm) {
					initializeLoadingForm(
							"Loading files to " + (cselectDir ? "import" :
								"export") + " from...",
							cfileRtnForm);
				} else {
					initializeLoadingForm(
							"Loading files to bookmark from...",
							cfileRtnForm);
				}
				final KFileSelectorMgr fileSelectorMgr =
					new KFileSelectorMgr();
				//#ifdef DLOGGING
				if (finestLoggable) {logger.finest("cselectDir,cfileRtnForm,cfileURL=" + cselectDir + "," + cfileRtnForm + "," + cfileURL);}
				//#endif
				fileSelectorMgr.doLaunchSelector(this,
							cselectDir, cfileRtnForm, cfileURL);
			} catch(OutOfMemoryError ex) {
				m_loadForm.recordExcForm("Out Of Memory Error getting " +
						"file form.", ex);
			} catch (Throwable t) {
				m_loadForm.recordExcForm("Internal error getting file " +
						"form.", t);
			} finally {
				synchronized(this) {
					m_getFile = false;
				}
			}
		}
		//#endif
	}

	/** Save data and exit the application. This accesses the database,
	    so it must not be called by commandAction as it may hang.  It must
	    be called by a separate thread.  */
	final private void exitApp() {
		synchronized(this) {
			if ( (m_exit || m_saveBookmarks) && !m_saving ) {
				if (m_exit && m_stored) {
					return;
				}
				try {
					m_saving = true;
					//#ifdef DLOGGING
					if (fineLoggable) {logger.fine("m_exit,m_saveBookmarks=" + m_exit + "," + m_saveBookmarks);}
					//#endif
					if ( m_exit ) {
						initializeLoadingForm("Exiting saving data...",
								m_bookmarkList);
						m_loadForm.addQuit();
					} else if ( m_saveBookmarks ) {
						initializeLoadingForm("Saving data...",
								m_bookmarkList);
					} else {
						return;
					}
					saveBkMrkSettings("Saving items to database...",
							System.currentTimeMillis(), m_firstTime, m_exit,
							m_rssFeeds, m_bookmarkList, m_loadForm, m_settings
							//#ifdef DLOGGING
							,logger
							,fineLoggable
							//#endif
							);
					if (m_exit) {
						try {
							destroyApp(true);
						} catch (MIDletStateChangeException e) {
							//#ifdef DLOGGING
							if (fineLoggable) {logger.fine("MIDletStateChangeException=" + e.getMessage());}
							//#else
							e.printStackTrace();
							//#endif
						}
						super.notifyDestroyed();
						m_exit = false;
					} else {
						m_loadForm.appendMsg(
								"Finished saving.  Use back to return.");
						showBookmarkList();
					}
				} finally {
					m_stored = m_exit;
					m_exit = false;
					m_saveBookmarks = false;
					m_saving = false;
				}
			}
		}
	}

	/* Notify us that we are finished. */
	final public void wakeup(int loop) {
    
		if (m_bookmarkList != null) {
			m_bookmarkList.getFeatureMgr().wakeup(loop);
		}
	}

	//#ifdef DMIDP20
	final public void setCurrentItem(Item item) {
		// To prevent loading form from being displayed instead of the
		// next form when that form has no items, show the load form
		// again as a workaround.
		if ((m_loadForm != null) &&
				(m_display.getCurrent() == m_loadForm)) {
			m_display.setCurrent(m_loadForm);
		}
		m_display.setCurrentItem(item);
		// Prevents loading screen Display.getDisplay(this).setCurrentItem(item);
		wakeup(2);
	}
	//#endif

	/* Set current displayable and wake up the thread. */
	final public void setCurrent(Displayable disp) {

		//#ifdef DTESTUI
		String title = "";
		if (disp instanceof Form) {
			title = ((Form)disp).getTitle();
		} else if (disp instanceof List) {
			title = ((List)disp).getTitle();
		}
		System.out.println("Test UI setCurrent " + disp.getClass().getName() + "," + title);
		//#endif
		m_display.setCurrent( disp );
		// Prevents loading screen Display.getDisplay(this).setCurrent( disp );
		wakeup(2);
	}

	/* Set current displayable and wake up the thread. */
	final public void setCurrentNotes(Displayable disp) {
		setCurrentNotes(null, disp);
	}

	/* Set current displayable and wake up the thread. */
	final public void setCurrentNotes(Displayable alert, Displayable disp) {

		setCurrentNotes(alert, disp, null);
	}

	/* Set current displayable and wake up the thread. */
	final public void setCurrentNotes(Displayable alert, Displayable disp,
			LoadingForm cloadForm) {
		//#ifdef DTESTUI
		String title = "";
		if (disp instanceof Form) {
			title = ((Form)disp).getTitle();
		} else if (disp instanceof List) {
			title = ((List)disp).getTitle();
		}
		System.out.println("Test UI setCurrentNotes " + disp.getClass().getName() + "," + title);
		//#endif
		if (cloadForm == null) {
			cloadForm = (disp instanceof LoadingForm) ? (LoadingForm)disp : m_loadForm;
		}
		if (cloadForm.hasNotes() || cloadForm.hasExc()) {
			cloadForm.replaceRef(null, disp);
			if (!cloadForm.isLoadFinished()) {
				cloadForm.recordFin();
			}
			if (alert != null) {
				setCurrent( alert, cloadForm );
			} else {
				setCurrent( cloadForm );
			}
		} else {
			if (alert != null) {
				setCurrent( alert, disp );
			} else {
				setCurrent( disp );
			}
		}
	}

	//#ifdef DTESTUI
	/* Get current displayable. */
	final public Displayable getCurrent() {
		return m_display.getCurrent();
	}
	//#endif

	/* Set current displayable and wake up the thread. */
	final public void setCurrent(Displayable alert, Displayable disp) {
		m_display.setCurrent( (Alert)alert, disp );
		// Prevents loading screen Display.getDisplay(this).setCurrent( alert, disp );
		wakeup(2);
	}

    /** Show item form */
    final public void showItemForm() {
        setCurrent( m_itemForm );
    }
    
	//#ifdef DTESTUI
	/** Cause item form to go back to the prev form. */
    final public void backFrItemForm() {
		m_itemForm.commandAction( m_backCommand, m_itemForm );
    }
    
    /** Show item form */
    final public boolean isItemForm() {
        return (m_display.getCurrent() == m_itemForm);
    }
	//#endif
    
    /** Initialize RSS item form */
    final public void initializeItemForm(final RssItunesFeed feed,
								   final RssItunesItem item,
								   List prevList) {
        System.out.println("Create new item form");
		//#ifdef DTEST
		System.gc();
		long beginMem = Runtime.getRuntime().freeMemory();
		//#endif
		final String title = item.getTitle();
		m_itemRtnList = prevList;
		if (title.length() > 0) {
			m_itemForm = new ItemForm( title, title, feed, item);
		} else {
			m_itemForm = new ItemForm( getItemDescription(item), title,
					feed, item);
		}
		m_itemForm.setCommandListener(m_itemForm, false);
		//#ifdef DTEST
		System.out.println("itemForm size=" + (beginMem - Runtime.getRuntime().freeMemory()));
		//#endif
		setCurrent( m_itemForm );
    }

	//#ifdef DITUNES
    /** Initialize RSS item form */
    final public void initializeDetailForm(final RssItunesFeed feed,
								   List prevList) {
		//#ifdef DTEST
		System.gc();
		long beginMem = Runtime.getRuntime().freeMemory();
		//#endif
		DetailForm displayDtlForm = new DetailForm( feed, prevList );
		displayDtlForm.setCommandListener(displayDtlForm, false);
		//#ifdef DTEST
		System.out.println("displayDtlForm size=" + (beginMem - Runtime.getRuntime().freeMemory()));
		//#endif
		setCurrent( displayDtlForm );
    }
	//#endif

	/** Get the max words configured from the descritption. */
	final public String getItemDescription( final RssItunesItem item ) {
		final String [] parts = MiscUtil.split(item.getDescription(), " ");
		StringBuffer sb = new StringBuffer();
        final int wordCount = Math.min(parts.length,
				m_appSettings.getMaxWordCountInDesc());
		for (int ic = 0; ic < wordCount; ic++) {
			if (ic > 0) {
				sb.append(" ");
			}
			sb.append(parts[ic]);
		}
		return sb.toString();
	}
    
    /**
     * Start up the Hello MIDlet by creating the TextBox and associating
     * the exit command and listener.
     */
    public void startApp()
	throws MIDletStateChangeException {
		// Initialize bookmarks here since it does some work.
		if (m_bookmarkList == null) {
			synchronized (this) {
				if (m_bookmarkList == null) {
					initForms();
				}
				if (m_novice) {
					if (m_bookmarkList.size() == 0) {
						initializeLoadingForm("Loading items...",
								m_bookmarkList);
						try {
							FeedListParser listParser =
								new LineByLineParser(
									"jar:///data/novice.txt", "", "");
							listParser.setGetFeedTitleList(true);
							listParser.setFeedNameFilter(null);
							listParser.setFeedURLFilter(null);
							//#ifndef DSMALLMEM
							listParser.setRedirectHtml(false);
							//#endif
							listParser.run();
							ImportFeedsForm.addFeedLists(listParser,
									0,
									m_appSettings.getMaximumItemCountInFeed(),
									true, m_rssFeeds, m_bookmarkList,
									m_loadForm);
						}catch(Throwable e) {
							m_loadForm.recordExcForm(
									"\nError loading intial bookmarks\n", e);
						}
						if (m_loadForm.size() > 0) {
							Item item = m_loadForm.get(
									m_loadForm.size() - 1);
							//#ifdef DLOGGING
							if (finestLoggable) {logger.finest("item=" + item.getClass().getName());}
							//#endif
							if (item instanceof Gauge) {
								((Gauge)item).setValue(
									m_settings.MAX_REGIONS + 1);
							}
						}
						if (!m_firstTime && !m_loadForm.hasExc()) {
							showBookmarkList();
						}
					}
					if (m_firstTime) {
						m_firstTime = false;
						final Alert aboutAlert = getAbout();
						setCurrent( aboutAlert, 
								(m_loadForm.hasExc() ?
								 (Displayable)m_loadForm :
								 (Displayable)m_bookmarkList) );
					}
				}

			}
		}
    }
    
    /**
	 * Create about alert.
	 * @author  Irving Bunton
	 * @version 1.0
	 */
	final private Alert getAbout() {
		final Alert about = new Alert("About RssReader",
 "RssReader v" + super.getAppProperty("MIDlet-Version") + "-" +
 super.getAppProperty("Program-Version") +
 " Copyright (C) 2005-2006 Tommi Laukkanen, " +
 " Copyright (c) 2001-2005 Todd C. Stellanova, rawthought, " +
 " (C)1999 Romain Guy, Osvaldo Pinali Doederlein, " +
 "http://code.google.com/p/mobile-rss-reader/.  " +
 "This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 2 of the License, or (at your option) any later version." +
 "" +
 "THE SOFTWARE IS PROVIDED \"AS IS\", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR " +
 "IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, " +
 "FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE " +
 "AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER " +
 "LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING " +
 "FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS " +
 "IN THE SOFTWARE.  " +
 "See the GNU General Public License for more details." +
 "" +
 "You should have received a copy of the GNU General Public License along with this program; if not, write to the:" +
 "Free Software Foundation, Inc." +
 "51 Franklin Street, Fifth Floor" +
 "Boston, MA" +
 "02110-1301 USA" +
 "Using this software means that you accept this license and agree to" +
 "not use this program to break any laws.",
  null, AlertType.INFO);
		about.setTimeout(Alert.FOREVER);
 
		return about;
	}

    /**
     * Pause is a no-op since there are no background activities or
     * record stores that need to be closed.
     */
    public void pauseApp() {
    }
    
    /**
     * Destroy must cleanup everything not handled by the garbage collector.
     * In this case we need to save the bookmarks/feeds:w
     */
    public void destroyApp(boolean unconditional)
		throws MIDletStateChangeException {
    	if (unconditional && (m_bookmarkList != null)) {
			m_bookmarkList.getFeatureMgr().setBackground(false);
		}
    }
    
    /** Save bookmarks to record store
        releaseMemory use true if exiting as we do not need
		the rss feeds anymore, so we can save memory and avoid
		having extra memory around.  */
    static final public void saveBookmarks(final long storeDate,
			int region, boolean releaseMemory,
			Hashtable rssFeeds, final Choice bookmarkList,
			final LoadingForm loadForm,
			final Settings settings
			//#ifdef DLOGGING
			,final Logger logger
			,final boolean finestLoggable
			//#endif
			) {
		System.gc();
		StringBuffer bookmarks = new StringBuffer();
		settings.setStringProperty(Settings.BOOKMARKS_NAME,
				bookmarks.toString());
		settings.setLongProperty(Settings.STORE_DATE, storeDate);
		final int bsize = bookmarkList.size();
		if (bsize == 0) {
			return;
		}
		//#ifdef DTEST
		int storeTime = 0;
		//#endif
		final int bookRegion = region - 1;
		final int iparts = Settings.MAX_REGIONS - 1;
		final int firstIx = bookRegion * bsize / iparts;
		final int endIx = (bookRegion + 1) * bsize / iparts - 1;
        try {
			//#ifdef DLOGGING
			if (finestLoggable) {logger.finest("firstIx,endIx=" + firstIx + "," + endIx);}
			//#endif
			Vector vstored = new Vector();
			try {
				/** Try to save feeds including items */
				for( int i=firstIx; i<=endIx; i++) {
					final String name = bookmarkList.getString(i);
					//#ifdef DLOGGING
					if (finestLoggable) {logger.finest("i,name=" + i + "," + name);}
					//#endif
					if (!rssFeeds.containsKey( name )) {
						continue;
					}
					if( name.length()>0) {
						final RssItunesFeed rss =
							(RssItunesFeed)rssFeeds.get( name );
						//#ifdef DTEST
						long beginStore = System.currentTimeMillis();
						//#endif
						bookmarks.append(rss.getStoreString(true, true));
						//#ifdef DTEST
						storeTime += System.currentTimeMillis() - beginStore;
						//#endif
						bookmarks.append(CFEED_SEPARATOR);
						if (releaseMemory) {
							vstored.addElement( name );
						}
					}
				}
			} catch(OutOfMemoryError error) {
	//#ifdef DLOGGING
				logger.severe("saveBookmarks could not save.", error);
	//#endif
				System.out.println("Error saveBookmarks could not save.  " +
						error + " " + error.getMessage());
				final Alert memoryAlert = new Alert(
						"Out of memory", 
						"Saving bookmarks without updated news items.",
						null,
						AlertType.WARNING);
				memoryAlert.setTimeout(Alert.FOREVER);
				loadForm.getFeatureMgr().showMe( memoryAlert );
				
				/** Save feeds without items */
				bookmarks.setLength(0);
				for( int i=firstIx; i<=endIx; i++) {
					final String name = bookmarkList.getString(i);
					if( name.length() == 0) {
						continue;
					}
					final RssItunesFeed rss = (RssItunesFeed)rssFeeds.get( name );
					bookmarks.append(rss.getStoreString(false, true));
					bookmarks.append(CFEED_SEPARATOR);
					if (releaseMemory) {
						vstored.addElement( name );
					}
				}
			} finally {
				if (releaseMemory) {
					final int vslen = vstored.size();
					for (int ic = 0; ic < vslen; ic++) {
						rssFeeds.remove( (String)vstored.elementAt( ic ));
					}
				}
			}
			//#ifdef DTEST
			System.out.println("storeTime=" + storeTime);
			//#endif
            settings.setStringProperty(Settings.BOOKMARKS_NAME, bookmarks.toString());
		} catch (Throwable t) {
            settings.setStringProperty(Settings.BOOKMARKS_NAME, bookmarks.toString());
			//#ifdef DTEST
			System.out.println("storeTime=" + storeTime);
			//#endif
//#ifdef DLOGGING
			logger.severe("saveBookmarks could not save.", t);
//#endif
			System.out.println("saveBookmarks could not save." + t + " " +
					           t.getMessage());
			t.printStackTrace();
        }
    }

	//#ifdef DJSR75
	/* Set flag to show find files list.
	   fileRtnForm - Form to return to after file finished.
	   fileURL - Text field that has URL to put file URL into as well
	   			 as field to go back to if 2.0 is valid.
	*/
	final public void reqFindFiles( final boolean selectDir,
			final Form fileRtnForm, final TextField fileURL) {
		synchronized(this) {
			m_fileRtnForm = fileRtnForm;
			m_fileURL = fileURL;
			m_getFile = true;
			m_selectDir = selectDir;
		}
	}
	//#endif

	/* Save the current bookmarks and other properties.
	   releaseMemory - true if memory used is to be released as the
	   				   bookmarks are saved.  Used when exitiing as true.
	*/
	static final public synchronized void saveBkMrkSettings(String guageTxt,
			final long storeDate,
			final boolean firstTime,
			final boolean releaseMemory,
			Hashtable rssFeeds, final Choice bookmarkList,
			final LoadingForm loadForm,
			final Settings settings
			//#ifdef DLOGGING
			,final Logger logger
			,final boolean finestLoggable
			//#endif
			) {
		int nbrRegions = firstTime ? 1 : Settings.MAX_REGIONS + 1;
		Gauge gauge = new Gauge(guageTxt, false, nbrRegions, 0);
		int pl = loadForm.append(gauge);
		loadForm.getFeatureMgr().showMe();
		try {
			if (!firstTime) {
				settings.setLongProperty(Settings.STORE_DATE, storeDate);
				settings.save(0, false);
			}
			gauge.setValue(1);
			if (!firstTime) {
				for (int ic = 1; ic < Settings.MAX_REGIONS; ic++) {
					saveBookmarks(storeDate, ic, releaseMemory, rssFeeds,
							bookmarkList, loadForm, settings
							//#ifdef DLOGGING
							,logger
							,finestLoggable
							//#endif
							);
					settings.save(ic, false);
					gauge.setValue(ic + 1);
				}
				// Set internal region back to 0.
				settings.setStringProperty(Settings.BOOKMARKS_NAME, "");
				settings.save(0, false);
			}
			pl = -1;
			gauge.setValue(gauge.getMaxValue());
		} catch(CauseRecStoreException e) {
			if ((e.getFirstCause() != null) &&
				!(e.getFirstCause() instanceof RecordStoreFullException)) {
				/* Error saving feeds to database.  Database error. */
				loadForm.recordExcForm(
						"Error saving feeds to database.  Database error. ", e);
			} else {
				/* Error saving feeds to database.  Database full. */
				loadForm.recordExcForm("Error saving feeds to database.  Database full. ", e);
			}
		} catch(Exception e) {
			loadForm.recordExcForm("Internal error saving feeds.", e);
		} catch(Throwable t) {
			loadForm.recordExcForm("Internal error saving feeds.", t);
		} finally {
			if (pl >= 0) {
				loadForm.delete(pl);
			}
		}
	}

	/** Remove the ref to this displayable so that the memory can be freed. */
	final public void replaceRef(final Displayable disp,
			final Displayable newDisp) {
		m_loadForm.replaceRef(disp, newDisp);
	}

	//#ifdef DMIDP20
	public void changed(Observable observable, Object arg) {

		RssFeedParser cbackGrRssParser = null;
		cbackGrRssParser = checkActive(observable);
		if (cbackGrRssParser == null) {
			return;
		}
		try {
			if (!cbackGrRssParser.getObservableHandler().isCanceled()) {
				RssItunesFeed feed = cbackGrRssParser.getRssFeed();
				m_rssFeeds.put(feed.getName(), feed);
				procBackHeader(cbackGrRssParser, observable);
			}
		} finally {
			stopRssBackground((Observable)cbackGrRssParser);
		}
	}
	//#endif

	public void procPageExc(RssItunesFeed feed, boolean copenPage, Throwable e) {
		if (e instanceof Exception) {
			/* Error loading/parsing  feed on:\n \1 */
			m_loadForm.recordExcFormFin(
					(copenPage ? "Error loading " : "Error parsing ") +
					feed.getUrl(), e);

		} else if (e instanceof OutOfMemoryError) {
			System.gc();
			if (feed != null) {
				//#ifdef DLOGGING
				logger.severe("Out of memory for feed setting to 0 items:" + feed.getName());
				//#endif
				feed.setItems(new Vector());
			}
			m_loadForm.recordExcFormFin(
					/* Out of memory loading/parsing feed on:\n */
					(copenPage ? "Out of memory loading " : "Out of memory parsing ") +
					feed.getUrl(), e);
		} else {
			m_loadForm.recordExcForm(
					/* Internal error loading/parsing feed on:\n */
					(copenPage ? "Out of memory loading " : "Out of memory parsing ") +
					feed.getUrl(), e);
		}
	}

    /** Respond to commands */
    public void commandAction(Command c, Displayable s) {
		int ctype = c.getCommandType();
		//#ifdef DLOGGING
		//#ifdef DMIDP20
		if (finestLoggable) {logger.finest("command,ctype,displayable=" + c.getLabel() + "," + ctype + "," + s.getTitle());}
		//#else
		if (finestLoggable) {logger.finest("command,ctype,displayable=" + c.getLabel() + "," + ctype);}
		//#endif
		//#endif
        /** Add new RSS feed bookmark */
        if( c == m_addNewBookmark ){
			m_curBookmark = m_bookmarkList.getSelectedIndex();
			m_getAddBMForm = true;
        }
        
        /** Exit from MIDlet and save bookmarks */
        if( c == m_exitCommand ){
			synchronized(this) {
				m_exit = true;
			}
        }
        
        /** Save bookmarks without exit (don't free up bookmarks)  */
        if( c == m_saveCommand ){
			m_saveBookmarks = true;
        }
        
        /** Edit currently selected RSS feed bookmark */
        if( c == m_editBookmark ){
			try {
				if( m_bookmarkList.size()>0 ){
					m_curBookmark = m_bookmarkList.getSelectedIndex();
					if (m_curBookmark < 0) {
						m_curBookmark = 0;
						m_bookmarkList.setSelectedIndex(0, true);
					}
					m_getEditBMForm = true;
				}
			}catch(Throwable t) {
				//#ifdef DLOGGING
				logger.severe("Editing feeds.", t);
				//#endif
				/** Error while parsing RSS feed */
				System.out.println("Error editing feeds: " + t.getMessage());
			}
        }
        
        /** Delete currently selected RSS feed bookmark */
        if(( c == m_delBookmark ) && ( m_bookmarkList.size()>0 )){
			m_curBookmark = m_bookmarkList.getSelectedIndex();
			String name = m_bookmarkList.getString(m_curBookmark);
			m_bookmarkList.delete( m_curBookmark );
			if (m_rssFeeds.containsKey( name )) {
				m_rssFeeds.remove( name );
            }
        }
        
        /** Open RSS feed bookmark */
        if( ((c == m_openBookmark) || (c == List.SELECT_COMMAND &&
                (s==m_bookmarkList)))){
			m_curBookmark = FeatureMgr.getSelectedIndex(m_bookmarkList);
			if( m_curBookmark >= 0 ){
				boolean copenPage = false;
				boolean cgetPage = false;
				boolean cgetModPage = false;
				String parm = m_bookmarkList.getString(
						m_curBookmark);
				RssItunesFeed feed = null;
				try {
					feed = (RssItunesFeed)m_rssFeeds.get(parm);
					if (feed == null) {
						return;
					}
					copenPage = ( feed.getItems().size() > 0 );
				} catch (Throwable e) {
					procPageExc(feed, copenPage, e);
					return;
				}
				cgetPage = !copenPage;
				cgetModPage = false;

				setPageInfo(copenPage, cgetPage, cgetModPage, m_bookmarkList);
				// Open existing bookmark and show headers (items).
				if (copenPage) {
					procPage(feed);
				} else {
					//#ifdef DMIDP20
					procBackPage(feed, null);
					//#else
					try {
						RssFeedParser parser = new RssFeedParser( feed );
						/* Updating feed... */
						initializeLoadingForm(
								"Updating feed..." , m_bookmarkList);
						setPageInfo(false, false, false, m_bookmarkList);
						final int maxItemCount =
							m_appSettings.getMaximumItemCountInFeed();
						parser.parseRssFeed( false, maxItemCount);
						procUpdHeader(parser);
					}catch(Throwable e) {
						procPageExc(feed, false, e);
					} finally {
						synchronized(this) {
							m_getPage = false;
							m_openPage = false;
							m_getModPage = false;
						}
					}
					//#endif
				}
			}
		}
        
        /** Read unread items date sorted */
        if(( c == m_readUnreadItems ) && (m_bookmarkList.size() > 0)) {
			m_runNews = true;
        }
        
        /** Open RSS feed's selected topic */
        /** Get back to RSS feed headers */
        if( (s instanceof Form) &&
            (((Form)s) == ((Form)m_itemForm)) && (ctype == Command.BACK) &&
			( m_itemRtnList != null)) {
			setCurrent( m_itemRtnList );
			m_itemRtnList  = (Displayable)nullPtr;
		}

		//#ifdef DTESTUI
		if( (s instanceof Form) &&
			(((Form)s) == ((Form)m_itemForm)) && (ctype == Command.BACK) ){
			if (m_headerIndex >= 0) {
				m_headerNext = true;
			} else if (m_allNewsTestList != null) {
				m_allNewsTestList.gotoNews();
			}
		}
		//#endif
        
        /** Update all RSS feeds */
        if( c == m_updateAllCmd ) {
			m_refreshAllFeeds = true;
        }
        
        /** Update all modified RSS feeds */
        if( c == m_updateAllModCmd ) {
			m_refreshUpdFeeds = true;
        }
        
        /** Show import feed list form */
        if( c == m_importFeedListCmd ) {
			// Set current bookmark so that the added feeds go after
			// the current boolmark.
			m_curBookmark = m_bookmarkList.getSelectedIndex();
			m_getImportForm = true;
        }
        
		//#ifdef DSIGNED
		//#ifdef DITUNES
		//#ifdef DJSR75
        /** Show export feed list form */
        if( c == m_exportFeedListCmd ) {
			// Set current bookmark so that the added feeds go after
			// the current boolmark.
			m_curBookmark = m_bookmarkList.getSelectedIndex();
			m_getExportForm = true;
        }
		//#endif
		//#endif
		//#endif
        
		//#ifdef DTEST
		/** Show import feed list form and default file */
		if( c == m_importCurrFeedListCmd ) {
			m_curBookmark = FeatureMgr.getSelectedIndex(m_bookmarkList);
			if( m_curBookmark >= 0 ) {
				//#ifdef DTESTUI
				m_bookmarkLastIndex = m_curBookmark;
				System.out.println("TESTUI Import Current:  " + m_bookmarkList.getString(m_curBookmark));
				//#endif
				m_getTestImportForm = true;
				m_getImportForm = true;
			}
        }
		//#endif

        /** Update all modified RSS feeds */
		//#ifdef DTEST
        if( c == m_reloadDbCmd ) {
			m_appSettings.deleteSettings();
			m_appSettings = null;
			m_settings = null;
			Object[] arrsettings = FeatureMgr.initSettingsEnabled(
					this, m_loadForm
					//#ifdef DLOGGING
					,logger
					,fineLoggable
					//#endif
					);
			m_appSettings = (RssReaderSettings)arrsettings[0];
			m_settings = (Settings)arrsettings[1];
			m_firstTime = ((Boolean)arrsettings[2]).booleanValue();
			m_itunesEnabled = ((Boolean)arrsettings[3]).booleanValue();
        }
		//#endif
        
		//#ifdef DTESTUI
        /** Auto edit feeds/bookmarks to */
        if( c == m_testBMCmd ) {
			m_bookmarkIndex = 0;
			System.out.println("Test UI Test Rss feeds m_bookmarkIndex=" + m_bookmarkIndex);
		}
		//#endif

		//#ifdef DTESTUI
        /** Go back to last position */
        if( c == m_testRtnCmd ) {
			if (m_bookmarkLastIndex != 1) {
				if (m_bookmarkList.getSelectedIndex() >= 0) {
					m_bookmarkList.setSelectedIndex(
							m_bookmarkList.getSelectedIndex(), false);
				}
				m_bookmarkList.setSelectedIndex( m_bookmarkLastIndex, true );
			}
		}
		//#endif

        /** Settings form */
        if( c == m_settingsCmd ) {
			m_getSettingsForm = true;
        }
        
        /** Show about */
		if( c == m_aboutCmd ) {
			m_about = true;
		}

		//#ifdef DTESTUI
        /** Show encodings list */
		if( c == m_testEncCmd ) {
			try {
				initializeLoadingForm("Loading test form...", m_bookmarkList);
				setCurrent( m_testingForm );
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		//#endif

	//#ifdef DLOGGING
        /** Show about */
		if( c == m_debugCmd ) {
			setCurrent( m_debug );
		}

        /** Clear form */
		if( c == m_clearDebugCmd ) {
			while(m_debug.size()>0) {
				m_debug.delete(0);
			}
		}

        /** Back to bookmarks */
		if( c == m_backFrDebugCmd ) {
			showBookmarkList();
		}

		//#endif

    }
    
	//#ifdef DMIDP20
	public void stopRssBackground(Observable cbackGrParser) {
		synchronized(this) {
			if (m_parseBackground && (cbackGrParser == m_backGrParser)) {
				m_parseBackground = false;
				setPageInfo(false, false, false, m_prevDisp);
				m_backGrParser.getObservableHandler().deleteObserver(this);
				m_loadForm.removeCommandPrompt(m_backCommand);
			}
		}
		//#ifdef DLOGGING
		if (finestLoggable) {logger.finest("m_parseBackground,cbackGrParser,m_backGrParser=" + m_parseBackground + "," + cbackGrParser + "," + m_backGrParser);}
		//#endif
	}
	//#endif

	//#ifdef DTESTUI
    public void setBookmarkIndex(int bookmarkIndex) {
        this.m_bookmarkIndex = bookmarkIndex;
    }

    public int getBookmarkIndex() {
        return (m_bookmarkIndex);
    }
	//#endif

	//#ifdef DITUNES
	/* Form to look at item. */
	final public class DetailForm extends FeatureForm
		implements CommandListener {
		private Displayable        m_rtn;

		private DetailForm (final RssItunesFeed feed, Displayable rtn ) {
			super(m_midlet, feed.getName());
			this.m_rtn = rtn;
			super.addCommand( m_backCommand );
			if (m_itunesEnabled && feed.isItunes()) {
				final String language = feed.getLanguage();
				if (language.length() > 0) {
					super.append(new StringItem("Language:", language));
				}
				final String author = feed.getAuthor();
				if (author.length() > 0) {
					super.append(new StringItem("Author:", author));
				}
				final String subtitle = feed.getSubtitle();
				if (subtitle.length() > 0) {
					super.append(new StringItem("Subtitle:", subtitle));
				}
				final String summary = feed.getSummary();
				if (summary.length() > 0) {
					super.append(new StringItem("Summary:", summary));
				}
				super.append(new StringItem("Explicit:", feed.getExplicit()));
				final String title = feed.getTitle();
				if (title.length() > 0) {
					super.append(new StringItem("title:", title));
				}
				final String description = feed.getDescription();
				if (description.length() > 0) {
					super.append(new StringItem("Description:", description));
				}
			}
			final String link = feed.getLink();
			if (link.length() > 0) {
				//#ifdef DMIDP20
				StringItem slink = new StringItem("Link:", link,
												  Item.HYPERLINK);
				//#else
				StringItem slink = new StringItem("Link:", link);
				//#endif
				super.append(slink);
			}
			final Date feedDate = feed.getDate();
			if (feedDate != null) {
				super.append(new StringItem("Date:",
							feedDate.toString()));
			}
		}

		public void commandAction(Command c, Displayable s) {
			/* Back from details form. */
			if( c == m_backCommand ){
				setCurrent( m_rtn );
			}
		}
			
	}
	//#endif

	/* Form to look at item. */
	final private class ItemForm extends FeatureForm
		implements CommandListener {
		private boolean     m_platformReq;    // Flag to get platform req open link
		private String m_platformURL;         // Platform request URL
		//#ifdef DMIDP20
		private Command     m_openLinkCmd;      // The open link command
		private Command     m_openEnclosureCmd; // The open enclosure command
		//#endif
		private Command     m_nextItemCmd;      // The next item
		private Command     m_copyEnclosureCmd; // The copy enclosure command
		private Command     m_copyLinkCmd;    // The copy link command

		private ItemForm(final String title, final String actTitle,
								final RssItunesFeed feed,
								   final RssItunesItem item) {
			super(m_midlet, title);
			m_platformReq = false;
			m_nextItemCmd = new Command("Next Item", Command.SCREEN, 2);
			//#ifdef DMIDP20
			m_openLinkCmd       = new Command("Open link", Command.SCREEN, 3);
			m_openEnclosureCmd  = new Command("Open enclosure", Command.SCREEN, 2);
			//#endif
			m_copyLinkCmd       = new Command("Copy link", Command.SCREEN, 4);
			m_copyEnclosureCmd  = new Command("Copy enclosure", Command.SCREEN, 5);
			super.addCommand( m_backCommand );
			final String sienclosure = item.getEnclosure();
			final String desc = item.getDescription();
			if ((actTitle.length()>0) && (desc.length()>0)) {
				super.append(new StringItem(actTitle + "\n", desc));
			} else if (actTitle.length()>0) {
				super.append(new StringItem("Title\n", actTitle));
			} else {
				super.append(new StringItem("Description\n", desc));
			}
			citem = item;
			if (m_itunesEnabled && (item.isItunes() || feed.isItunes())) {
				final String author = item.getAuthor();
				if (author.length() > 0) {
					super.append(new StringItem("Author:", author));
				}
				final String subtitle = item.getSubtitle();
				if (subtitle.length() > 0) {
					super.append(new StringItem("Subtitle:", subtitle));
				}
				final String summary = item.getSummary();
				if (summary.length() > 0) {
					super.append(new StringItem("Summary:", summary));
				}
				final String duration = item.getDuration();
				if (duration.length() > 0) {
					super.append(new StringItem("Duration:", duration));
				}
				String expLabel = "Explicit:";
				String explicit = item.getExplicit();
				if (explicit.equals(RssItunesItem.UNSPECIFIED)) {
					expLabel = "Feed explicit:";
					explicit = feed.getExplicit();
				}
				super.append(new StringItem(expLabel, explicit));
			}
			String linkLabel = "Link:";
			String link = item.getLink();
			//#ifdef DITUNES
			if (link.length() == 0) {
				link = feed.getLink();
				linkLabel = "Feed link:";
			}
			//#endif
			if (link.length() > 0) {
				//#ifdef DMIDP20
				StringItem slink = new StringItem(linkLabel, link, Item.HYPERLINK);
				//#else
				StringItem slink = new StringItem(linkLabel, link);
				//#endif
				citemLnkNbr  = super.append(slink);
			} else {
				citemLnkNbr  = -1;
			}
			if (sienclosure.length() > 0) {
				//#ifdef DMIDP20
				StringItem senclosure = new StringItem("Enclosure:", sienclosure,
														  Item.HYPERLINK);
				//#else
				StringItem senclosure = new StringItem("Enclosure:", sienclosure);
				//#endif
				citemEnclNbr = super.append(senclosure);
			} else {
				citemEnclNbr  = -1;
			}
			
			// Add item's date if it is available
			String dateLabel = "Date:";
			Date itemDate = item.getDate();
			//#ifdef DITUNES
			if(itemDate==null) {
				itemDate = feed.getDate();
				dateLabel = "Feed date:";
			}
			//#endif
			if(itemDate!=null) {
				//#ifdef DMIDP10
				// MIDP 1.0 does not require toString to produce a date.
				// It may give the hash of the string (e.g. Sony Ericsson T637).
				final String sdate = RssFormatParser.stdDate(itemDate, "GMT");
				//#else
				final String sdate = itemDate.toString();
				//#endif
				super.append(new StringItem(dateLabel, sdate));
			}

			super.addCommand( m_nextItemCmd );
			if (link.length() > 0) {
				super.addCommand( m_copyLinkCmd );
			}
			if (sienclosure.length() > 0) {
				super.addCommand( m_copyEnclosureCmd );
			}
			//#ifdef DMIDP20
			if (link.length() > 0) {
				super.addCommand( m_openLinkCmd );
			}
			if (sienclosure.length() > 0) {
				super.addCommand( m_openEnclosureCmd );
			}
			//#endif
		}

		public void commandAction(Command c, Displayable s) {
			/** Get back to RSS feed headers */
			if( c == m_backCommand ){
				if ((m_itemRtnList != null) &&
					((m_itemRtnList instanceof HeaderList) ||
					(m_itemRtnList instanceof AllNewsList))) {
					((AllNewsList)m_itemRtnList).nextItem(false);
				}
				setCurrent( m_itemRtnList );
				//#ifdef DTESTUI
				m_midlet.updHeaderNext();
				//#endif
			}
			
			/** Copy link to clipboard.  */
			if( c == m_copyLinkCmd ){
				String link = citem.getLink();
				super.set(citemLnkNbr, new TextField("Link:", link,
						link.length(), TextField.URL));
				//#ifdef DMIDP10
				setCurrent(this);
				//#else
				setCurrentItem(m_itemForm.get(citemLnkNbr));
				//#endif
			}
			
			/** Copy enclosure to clipboard.  */
			if( c == m_copyEnclosureCmd ){
				final String link = citem.getEnclosure();
				super.set(citemEnclNbr, new TextField("Enclosure:",
					link, link.length(), TextField.URL));
				//#ifdef DMIDP10
				setCurrent(m_itemForm);
				//#else
				setCurrentItem(m_itemForm.get(citemEnclNbr));
				//#endif
			}
			
			//#ifdef DMIDP20
			/** Go to link and get back to RSS feed headers */
			if( c == m_openLinkCmd ){
				synchronized(this) {
					final String link = citem.getLink();
					m_platformURL = link;
					m_platformReq = true;
				}
			}
			//#endif

			//#ifdef DMIDP20
			/** Go to link and get back to RSS feed headers */
			if( c == m_openEnclosureCmd ){
				m_platformURL = citem.getEnclosure();
				m_platformReq = true;
			}
			//#endif
			
			if ((c == m_nextItemCmd) && (m_itemRtnList != null) &&
					((m_itemRtnList instanceof HeaderList) ||
					(m_itemRtnList instanceof AllNewsList))) {
				((AllNewsList)m_itemRtnList).nextItem(true);
			}

			execute();

		}

		public void execute() {

			/* Handle going to link (platform request.). */
			//#ifdef DMIDP20
			if ( m_platformReq ) {
				try {

					initializeLoadingForm("Loading web page...",
							m_itemForm);
					if( m_midlet.platformRequest(m_platformURL) ) {
						initializeLoadingForm("Exiting saving data...",
								m_itemRtnList);
						synchronized(this) {
							m_exit = true;
							exitApp();
						}
					} else {
						setCurrent( m_itemRtnList );
					}
				} catch (ConnectionNotFoundException e) {
					//#ifdef DLOGGING
					logger.severe("Error opening link " + m_platformURL, e);
					//#endif
					final Alert badLink = new Alert("Could not connect to link",
							"Bad link:  " + m_platformURL,
							null, AlertType.ERROR);
					badLink.setTimeout(Alert.FOREVER);
					setCurrent( badLink, m_itemRtnList );
				} finally {
					m_platformReq = false;
				}
			}
		//#endif

		}

	}

    public LoadingForm getLoadForm() {
        return (m_loadForm);
    }

}
