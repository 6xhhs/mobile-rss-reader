//--Need to modify--#preprocess
/*
 * RssReaderSettings.java
 *
 * Copyright (C) 2005-2006 Tommi Laukkanen
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
 * IB 2010-06-01 1.11.5RC2 Have back be the default first command instead of open to be standard.
 * IB 2010-06-02 1.11.5RC2 Have getSettingsInstance to get the Settings instance.
 * IB 2010-06-02 1.11.5RC2 Have deleteSettings to allow tests to delete/null settings.
 * IB 2010-06-02 1.11.5RC2 Need to use logging define to set logging.
 * IB 2010-06-02 1.11.5RC2 Save error from loading settings.
 * IB 2010-06-02 1.11.5RC2 More logging.
 * IB 2010-06-27 1.11.5RC2 If CLDC 1.1, use synchronized class statement vs static synchronized modifier.
 * IB 2010-06-27 1.11.5Dev2 Use volatile for instance vars.
 * IB 2010-09-27 1.11.5Dev8 Don't use midlet directly for RssReaderSettings.
 * IB 2010-10-12 1.11.5Dev9 Add --Need to modify--#preprocess to modify to become //#preprocess for RIM preprocessor.
 */

// Expand to define MIDP define
//#define DMIDP20
// Expand to define CLDC define
//#define DCLDC10
// Expand to define itunes define
//#define DNOITUNES
// Expand to define test define
//#define DNOTEST
// Expand to define logging define
//#define DNOLOGGING
package com.substanceofcode.rssreader.businessentities;

import com.substanceofcode.utils.Settings;
import java.io.IOException;
import javax.microedition.rms.RecordStoreException;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.List;

import com.substanceofcode.rssreader.presentation.FeatureMgr;

//#ifdef DLOGGING
//@import net.sf.jlogmicro.util.logging.Logger;
//#endif

/**
 * RssFeedReaderSettings contains application's settings.
 *
 * @author Tommi Laukkanen
 */
final public class RssReaderSettings {
    
    volatile private Settings m_settings;
    volatile private static RssReaderSettings m_singleton = null;
	//#ifdef DMIDP20
    public static final int DEFAULT_FONT_CHOICE = 0;
	//#endif
    
    private static final String MAX_ITEM_COUNT = "max-item-count";
    private static final String MAX_WORD_COUNT = "max-word-count";
    private static final String IMPORT_URL = "import-url";
    private static final String IMPORT_USERNAME = "import-username";
    private static final String IMPORT_PASSWORD = "import-password";
    private static final String MARK_UNREAD_ITEMS = "mark-unread-items";
    private static final String FEED_LIST_OPEN = "feed-list-open";
    private static final String ITUNES_ENABLED = "itunes-enabled";
	//#ifdef DMIDP20
    private static final String FONT_CHOICE = "font-choice";
    private static final String FIT_POLICY = "fit-policy";
    private static final String BOOKMARK_NAME_NEWS = "bookmark-name-news";
	//#endif
    private static final String USE_TEXT_BOX = "use-text-box";
    private static final String USE_STANDARD_EXIT = "use-standard-exit";
    private static final String NOVICE = "novice";
    public static final int INIT_MAX_ITEM_COUNT = 10;
    private Throwable m_loadExc = null;
	//#ifdef DLOGGING
//@    private static final String LOG_LEVEL = "log-level";
	//#endif
    
    /** Creates a new instance of RssReaderSettings */
    private RssReaderSettings() {
        try {
			//#ifdef DLOGGING
//@			Logger.getLogger("RssReaderSettings").info("Constructor midlet=" +
//@					FeatureMgr.getMidlet());
			//#endif
            m_settings = Settings.getInstance();
        } catch (Throwable e) {
			m_loadExc = e;
            e.printStackTrace();
        }
    }
    
    /** Get instance */
	//#ifdef DCLDCV11
//@    public static RssReaderSettings getInstance()
	//#else
    public static synchronized RssReaderSettings getInstance()
	//#endif
	{
		//#ifdef DCLDCV11
//@		synchronized(Settings.class) {
		//#endif
		//#ifdef DLOGGING
//@		Logger.getLogger("RssReaderSettings").info(
//@				"Constructor midlet,m_singleton=" + FeatureMgr.getMidlet() + "," + m_singleton);
		//#endif
        if(m_singleton==null) {
            m_singleton = new RssReaderSettings();
        }
        return m_singleton;
		//#ifdef DCLDCV11
//@        }
		//#endif
    }
    
    /** Get maximum item count */
    public int getMaximumItemCountInFeed() {
        int maxCount = m_settings.getIntProperty(MAX_ITEM_COUNT,
				INIT_MAX_ITEM_COUNT);
        return maxCount;
    }
    
    /** Set maximum item count in feed */
    public void setMaximumItemCountInFeed(int maxCount) {
        m_settings.setIntProperty(MAX_ITEM_COUNT, maxCount);
    }
    
    /** Get maximum word count in description */
    public int getMaxWordCountInDesc() {
        int maxCount = m_settings.getIntProperty(MAX_WORD_COUNT, 10);
        return maxCount;
    }
    
    /** Set maximum word count in description */
    public void setMaxWordCountInDesc(int maxCount) {
        m_settings.setIntProperty(MAX_WORD_COUNT, maxCount);
    }
    
    /** Get import URL address */
    public String getImportUrl() {
        String url = m_settings.getStringProperty(0, IMPORT_URL, "");
        return url;
    }
    
    /** Set import URL address */
    public void setImportUrl(String url) {
        m_settings.setStringProperty( IMPORT_URL, url);
    }
    
    /** Get import URL username */
    public String getImportUrlUsername() {
        String username = m_settings.getStringProperty(0, IMPORT_USERNAME, "");
        return username;
    }
    
    /** Set import URL username */
    public void setImportUrlUsername(String username) {
        m_settings.setStringProperty( IMPORT_USERNAME, username);
    }
    
    /** Get import URL password */
    public String getImportUrlPassword() {
        String password = m_settings.getStringProperty(0, IMPORT_PASSWORD, "");
        return password;
    }
    
    /** Set import URL password */
    public void setImportUrlPassword(String password) {
        m_settings.setStringProperty( IMPORT_PASSWORD, password);
    }
    
    /** Get mark unread items */
    public boolean getMarkUnreadItems() {
		//#ifdef DMIDP20
        return m_settings.getBooleanProperty( MARK_UNREAD_ITEMS, true);
		//#else
//@        return m_settings.getBooleanProperty( MARK_UNREAD_ITEMS, false);
		//#endif
    }
    
    /** Set import URL password */
    public void setMarkUnreadItems(boolean markUnreadItems) {
        m_settings.setBooleanProperty( MARK_UNREAD_ITEMS, markUnreadItems);
    }
    
    /** Get feed list back is first command */
    public boolean getFeedListOpen() {
        return m_settings.getBooleanProperty( FEED_LIST_OPEN, false);
    }
    
    /** Set feed list back is first command */
    public void setFeedListOpen(boolean feedListOpen) {
        m_settings.setBooleanProperty( FEED_LIST_OPEN, feedListOpen);
    }
    
    /** Get itunes enabled */
    public boolean getItunesEnabled() {
		//#ifdef DITUNES
//@        return m_settings.getBooleanProperty( ITUNES_ENABLED, true);
		//#else
        return m_settings.getBooleanProperty( ITUNES_ENABLED, false);
		//#endif
    }
    
    /** Set feed list back is first command */
    public void setItunesEnabled(boolean itunesEnabled) {
        m_settings.setBooleanProperty( ITUNES_ENABLED, itunesEnabled);
    }
    
	//#ifdef DMIDP20
    /** Get font choice */
    public int getFontChoice() {
        return m_settings.getIntProperty( FONT_CHOICE, DEFAULT_FONT_CHOICE);
    }
    
	/* Get the font size. This is the actual size of the font */
	public int getFontSize() {
		int fontSize;
		switch (getFontChoice()) {
			case 1:
				fontSize = Font.SIZE_SMALL;
				break;
			case 2:
				fontSize = Font.SIZE_MEDIUM;
				break;
			case 3:
				fontSize = Font.SIZE_LARGE;
				break;
			case DEFAULT_FONT_CHOICE:
			default:
				fontSize = Font.getDefaultFont().getSize();
				break;
		}
		return fontSize;
	}

    /** Set font size */
    public void setFontChoice(int fontChoice) {
        m_settings.setIntProperty( FONT_CHOICE, fontChoice);
    }
    
    /** Get fit policy */
    public int getFitPolicy() {
        return m_settings.getIntProperty( FIT_POLICY, List.TEXT_WRAP_DEFAULT);
    }
    
    /** Set fit policy */
    public void setFitPolicy(int fitPolicy) {
        m_settings.setIntProperty( FIT_POLICY, fitPolicy);
    }
    
    /** Get put bookmark name in news item list.*/
    public boolean getBookmarkNameNews() {
        return m_settings.getBooleanProperty( BOOKMARK_NAME_NEWS, false);
    }
    
    /** Set put bookmark name in news item list. */
    public void setBookmarkNameNews(boolean bookmarkNameNews) {
        m_settings.setBooleanProperty( BOOKMARK_NAME_NEWS, bookmarkNameNews);
    }
    
    /** Get use text box */
    public boolean getUseTextBox() {
        return m_settings.getBooleanProperty( USE_TEXT_BOX, false);
    }
    
    /** Set use text box */
    public void setUseTextBox(boolean useTextBox) {
        m_settings.setBooleanProperty( USE_TEXT_BOX, useTextBox);
    }
	//#endif
    
    /** Get use standard exit */
    public boolean getUseStandardExit() {
        return m_settings.getBooleanProperty( USE_STANDARD_EXIT, false);
    }
    
    /** Set standard exit */
    public void setUseStandardExit(boolean useStandardExit) {
        m_settings.setBooleanProperty( USE_STANDARD_EXIT, useStandardExit);
    }
    
    /** Get novice user */
    public boolean getNovice() {
        return m_settings.getBooleanProperty( NOVICE, false);
    }
    
    /** Set novice user */
    public void setNovice(boolean novice) {
        m_settings.setBooleanProperty( NOVICE, novice);
    }
    
	//#ifdef DLOGGING
//@    /** Get log level */
//@    public String getLogLevel() {
//@        String logLevel = m_settings.getStringProperty(0, LOG_LEVEL, "");
//@        return logLevel;
//@    }
//@    
//@    /** Set import URL password */
//@    public void setLogLevel(String logLevel) {
//@        m_settings.setStringProperty( LOG_LEVEL, logLevel);
//@    }
	//#endif
    
    /** Get settings version */
    public String getSettingsVers() {
        return m_settings.getStringProperty( 0, Settings.SETTINGS_NAME, "");
    }
    
    public Throwable getLoadExc() {
        return (m_loadExc);
    }

    public Settings getSettingsInstance() {
        return (m_settings);
    }

	//#ifdef DTEST
//@	final public void deleteSettings() {
//@		if (m_settings != null) {
//@			m_settings.deleteSettings();
//@			m_settings = null;
//@		}
//@		m_singleton = null;    
//@	}
	//#endif
    
}
