//--Need to modify--#preprocess
/*
 * URLForm.java
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
 * IB 2010-06-27 1.11.5Dev2 Make LoadingForm an independent class to remove dependency on RssReaderMIDlet for better testing.
 * IB 2010-09-26 1.11.5Dev8 Remove midlet which is now not used directly.
 * IB 2010-09-26 1.11.5Dev8 Repalce setCurrent with showMe.
 * IB 2010-09-26 1.11.5Dev8 Use loadingForm from FeatureMgr.
 * IB 2010-10-12 1.11.5Dev9 Add --Need to modify--#preprocess to modify to become //#preprocess for RIM preprocessor.
 */

// Expand to define MIDP define
//#define DMIDP20
// Expand to define DJSR75 define
//#define DNOJSR75
// Expand to define itunes define
//#define DNOITUNES
// Expand to define logging define
//#define DNOLOGGING
// Expand to define test ui define
//#define DNOTESTUI

package com.substanceofcode.rssreader.presentation;

import java.util.Hashtable;

import com.substanceofcode.rssreader.businessentities.RssReaderSettings;
import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.AlertType;
import javax.microedition.lcdui.Choice;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
//#ifndef DTESTUI
import javax.microedition.lcdui.ChoiceGroup;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.TextField;
import javax.microedition.lcdui.StringItem;
//#else
//@// If using the test UI define the Test UI's
//@import com.substanceofcode.testlcdui.ChoiceGroup;
//@import com.substanceofcode.testlcdui.Form;
//@import com.substanceofcode.testlcdui.List;
//@import com.substanceofcode.testlcdui.TextBox;
//@import com.substanceofcode.testlcdui.TextField;
//@import com.substanceofcode.testlcdui.StringItem;
//#endif
import javax.microedition.lcdui.Item;

import com.substanceofcode.utils.CauseException;
import com.substanceofcode.rssreader.businessentities.RssItunesFeed;
import com.substanceofcode.rssreader.businesslogic.FeedListParser;
import com.substanceofcode.rssreader.businesslogic.LineByLineParser;
import com.substanceofcode.rssreader.businesslogic.OpmlParser;
import com.substanceofcode.rssreader.businesslogic.RssFeedParser;

//#ifdef DJSR75
//@import org.kablog.kgui.KFileSelectorMgr;
//#endif
import com.substanceofcode.rssreader.presentation.RssReaderMIDlet;
import com.substanceofcode.rssreader.presentation.LoadingForm;

//#ifdef DLOGGING
//@import net.sf.jlogmicro.util.logging.Logger;
//@import net.sf.jlogmicro.util.logging.LogManager;
//@import net.sf.jlogmicro.util.logging.Level;
//#endif

/**
 *
 * @author Tommi Laukkanen
 */
public class URLForm extends FeatureForm
	implements CommandListener {

	protected boolean     m_ok = false;      // The ok flag
	protected boolean     m_cancel = false;      // The cancel flag
	protected boolean     m_clear = false;      // The clear flag
	protected boolean     m_last = false;      // The last flag
	//#ifdef DJSR75
//@	protected boolean     m_selectDir = false;  // The select directory flag
//@	protected boolean     m_fileReq = false;      // The get file flag
	//#endif
	protected int         m_addBkmrk = 0;// Place to add (insert) imported bookmarks
	protected TextField   m_url;      // The feed list URL field
	protected TextField   m_UrlUsername; // The feed list username
	protected TextField   m_UrlPassword; // The feed list password
	protected Command     m_addCmd = null; // The add command 
	protected Command     m_insCmd = null;   // The import before the current point?
	protected Command     m_appndCmd = null; // The import append
	protected Command     m_lastCmd = null; // The last data used
	protected Command     m_clearCmd; // The clear
//#ifdef DJSR75
//@	protected Command     m_fileCmd;    // The find files command for importing
//#endif
	//#ifdef DMIDP20
	protected Command     m_pasteURLCmd;// The paste command
	//#endif
	protected Hashtable m_rssFeeds;         // The bookmark URLs
	protected RssReaderSettings m_appSettings;// The application settings
	// The loading form
	protected Thread m_thread = null; // The thread

	//#ifdef DLOGGING
//@	private Logger m_logger = Logger.getLogger("URLForm");
	//#endif

	/** Initialize import form */
	public URLForm(String formName, boolean selectDir,
			Hashtable rssFeeds,
			RssReaderSettings appSettings,
			LoadingForm loadForm) {
		super(formName, loadForm);
		//#ifdef DJSR75
//@		m_selectDir = selectDir;
		//#endif
		m_rssFeeds = rssFeeds;
		m_appSettings = appSettings;
	}

  /**
   *
   * Initialize UI elements
   *
   * @author Irv Bunton
   */
	public void initUrlUI(String url, boolean hasOK, String okPrompt,
			int initPriority) {
		if(url.length()==0) {
			url = "http://";
		}
		m_url = new TextField("URL", url, 256, TextField.URL);
		super.append(m_url);
		
		Command cancelCmd = new Command("Cancel", Command.CANCEL,
				initPriority++);
		if (hasOK) {
			if (okPrompt != null) {
				super.addPromptCommand(new Command("OK", Command.OK, initPriority++),
						okPrompt);
			} else {
				super.addCommand(new Command("OK", Command.OK, initPriority++));
			}
		}

		//#ifdef DJSR75
//@		/* Find files */
//@		m_fileCmd     = new Command("Find files", Command.SCREEN,
//@				initPriority++);
		//#endif
		//#ifdef DMIDP20
		/* Allow paste */
		m_pasteURLCmd = new Command("Allow paste", Command.SCREEN,
				initPriority++);
		//#endif
		// Show last data entered
		m_lastCmd = new Command("Last", Command.SCREEN, initPriority++);
		
		super.addCommand( cancelCmd );
		//#ifdef DJSR75
//@		super.addCommand( m_fileCmd );
		//#endif
		//#ifdef DMIDP20
		if (m_appSettings.getUseTextBox()) {
			super.addCommand(m_pasteURLCmd);
		}
		//#endif
		super.addCommand( m_lastCmd );
	}

  /**
   *
   * Initialize UI elements
   *
   * @author Irv Bunton
   */
	public void initCommonInputUI(String url, String username, String password,
			boolean hasOK, String okPrompt, int initPriority) {
		initUrlUI(url, hasOK, okPrompt, initPriority);
		
		m_UrlUsername  = new TextField("Username (optional)", username, 64, TextField.ANY);
		super.append(m_UrlUsername);
		
		m_UrlPassword  = new TextField("Password (optional)", password, 64, TextField.PASSWORD);
		super.append(m_UrlPassword);
		
	}

	public void initAddUI(String url, String username, String password,
			boolean hasOK, String okPrompt, int initPriority,
			String insLabel, String insLongLabel,
			String addLabel, String addLongLabel,
			String appendLabel, String appendLongLabel) {

		//#ifdef DMIDP20
		m_insCmd      = new Command(insLabel, insLongLabel,
				Command.SCREEN, initPriority++);
		m_addCmd      = new Command(addLabel, addLongLabel,
				Command.SCREEN, initPriority++);
		m_appndCmd    = new Command(appendLabel, appendLongLabel,
				Command.SCREEN, initPriority++);
		//#else
//@		m_insCmd      = new Command(insLabel,
//@				Command.SCREEN, initPriority++);
//@		m_addCmd      = new Command(addLabel,
//@				Command.SCREEN, initPriority++);
//@		m_appndCmd    = new Command(appendLabel,
//@				Command.SCREEN, initPriority++);
		//#endif

		/* Clear */ m_clearCmd   = new Command("Clear", Command.SCREEN,
					initPriority++);

		super.addCommand( m_insCmd );
		super.addCommand( m_addCmd );
		super.addCommand( m_appndCmd );
		super.addCommand( m_clearCmd );

		initCommonInputUI(url, username, password, hasOK, okPrompt, initPriority);
	}

	/** Respond to commands */
	public void execute() {

		if (m_clear) {
			m_clear = false;
			m_url.setString("http://");
			m_UrlUsername.setString("");
			m_UrlPassword.setString("");
		}
		if (m_cancel) {
			m_cancel = false;
			featureMgr.getLoadForm().replaceRef(this, null);
			featureMgr.getMidlet().showBookmarkList();
		}

		//#ifdef DJSR75
//@		if (m_fileReq) {
//@			m_fileReq = false;
//@			if (!featureMgr.getMidlet().JSR75_ENABLED) {
//@				Alert invalidAlert = new Alert(
//@						"JSR-75 not enabled", 
//@						"Find files (JSR-75) not enabled on the phone.",
//@						null,
//@						AlertType.WARNING);
//@				invalidAlert.setTimeout(Alert.FOREVER);
//@				featureMgr.showMe( invalidAlert );
//@				return;
//@			}
//@			try {
//@				RssReaderMIDlet midlet = featureMgr.getMidlet();
//@				if (midlet != null) {
//@					midlet.reqFindFiles( m_selectDir, this, m_url);
//@					midlet.getFile();
//@				}
//@			}catch(Throwable t) {
				//#ifdef DLOGGING
//@				m_logger.severe("URLForm find files ", t);
				//#endif
//@				/** Error while executing find files */
//@				System.out.println("URLForm find files " + t.getMessage());
//@				t.printStackTrace();
//@			}
//@		}
		//#endif
				
	}

	/** Respond to commands */
	public void commandAction(Command c, Displayable s) {
		int commandType = c.getCommandType();

		/** Clear data. */
		if ( c == m_clearCmd ) {
			m_clear = true;
		}

		/** OK accept input */
		if ( commandType == Command.OK ) {
			m_ok = true;
		}
		
		/** Cancel currently edited (or added) RSS feed's properties */
		if ( commandType == Command.CANCEL ) {
			m_cancel = true;
		}
		
		//#ifdef DMIDP20
		/** Put current bookmark URL into URL box.  */
		if( c == m_pasteURLCmd ) {
			FeatureMgr.initializeURLBox( m_url.getString(), this, m_url );
		}
		//#endif

		//#ifdef DJSR75
//@		/** Find bookmark file in file system */
//@		if( c == m_fileCmd ) {
//@			m_fileReq = true;
//@		}
		//#endif
				
		if( c == m_lastCmd ) {
			m_last = true;
		}

	}

}
