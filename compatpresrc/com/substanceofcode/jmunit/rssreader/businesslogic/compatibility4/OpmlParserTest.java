/*
 * OpmlParserTest.java
 *
 * Copyright (C) 2009 Irving Bunton
 * http://code.google.com/p/mobile-rss-reader/
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
 * IB 2010-05-28 1.11.5RC2 Do comparison test using OpmlParser with links-opml.xml.
 * IB 2010-06-29 1.11.5RC2 Use ObservableHandler, Observer, and Observable re-written to use observer pattern without GPL code.  This is dual licensed as GPL and LGPL.
 */

// Expand to define MIDP define
@DMIDPVERS@
// Expand to define memory size define
@DMEMSIZEDEF@
// Expand to define test define
@DTESTDEF@
// Expand to define JMUnit test define
@DJMTESTDEF@
// Expand to define logging define
@DLOGDEF@

//#ifdef DJMTEST
package com.substanceofcode.jmunit.rssreader.businesslogic.compatibility4;

import java.util.Date;

import jmunit.framework.cldc10.TestCase;

import com.substanceofcode.rssreader.businessentities.RssItunesFeedInfo;
//#ifndef DSMALLMEM
import com.substanceofcode.utils.HTMLParser;
//#endif
import com.substanceofcode.rssreader.businesslogic.compatibility4.RssFeedParser;
import com.substanceofcode.rssreader.businessentities.compatibility4.RssItunesFeed;
import com.substanceofcode.rssreader.businesslogic.compatibility4.OpmlParser;
//#ifdef DMIDP20
import net.eiroca.j2me.observable.compatibility4.Observer;
import net.eiroca.j2me.observable.compatibility4.Observable;
//#endif

import com.substanceofcode.jmunit.utilities.BaseTestCase;

//#ifdef DLOGGING
import net.sf.jlogmicro.util.logging.Level;
//#endif

final public class OpmlParserTest extends BaseTestCase
//#ifdef DMIDP20
implements Observer, net.yinlight.j2me.observable.Observer
//#endif
{

	//#ifdef DMIDP20
	private boolean ready = false;
	//#endif
	private int nextIx = 0;
	//#ifdef DLOGGING
	private boolean alterLogLevel = false;
	private boolean endAlterLogLevel = false;
	private int alterix = 37;
	private int endAlterix = 39;
	private String newLogLevel = Level.FINEST.getName();
	private Level svLogLevel = null;
	//#endif

	public OpmlParserTest() {
		super(5, "compatibility4.OpmlParserTest");
	}

	public void test(int testNumber) throws Throwable {
		switch (testNumber) {
			case 0:
				testOpmlParse1();
				break;
			case 1:
				testOpmlParse2();
				break;
			case 2:
				testOpmlParse3();
				break;
			case 3:
				testOpmlParse4();
				break;
			case 4:
				testOpmlParse5();
				break;
			default:
				fail("Bad number for switch testNumber=" + testNumber);
				break;
		}
	}

	//#ifdef DMIDP20
	public void changed(Observable observable) {
		ready = true;
	}

	public void changed(net.yinlight.j2me.observable.Observable observable, Object arg) {
		ready = true;
	}

	public boolean isReady() {
		return ready;
	}
	//#endif

	/* Test parse Opml. */
	public void testOpmlParse1() throws Throwable {
		String mname = "testOpmlParse1";
		com.substanceofcode.rssreader.businesslogic.OpmlParser OpmlParser =
			new com.substanceofcode.rssreader.businesslogic.OpmlParser(
				"jar:///links-opml.xml", "", "");
		OpmlParser cmpOpmlParser =
			new OpmlParser("jar:///links-opml.xml", "", "");

		compatibilityOpmlParserTestSub(mname, OpmlParser, cmpOpmlParser, false);
	}

	/* Test parse Opml. */
	public void testOpmlParse2() throws Throwable {
		String mname = "testOpmlParse2";
		com.substanceofcode.rssreader.businesslogic.OpmlParser OpmlParser =
			new com.substanceofcode.rssreader.businesslogic.OpmlParser(
				"jar:///links-opml.xml", "", "");
		OpmlParser cmpOpmlParser =
			new OpmlParser("jar:///links-opml.xml", "", "");

		compatibilityOpmlParserTestSub(mname, OpmlParser, cmpOpmlParser, false);
	}

	/* Test parse Opml. */
	public void testOpmlParse3() throws Throwable {
		String mname = "testOpmlParse3";
		com.substanceofcode.rssreader.businesslogic.OpmlParser OpmlParser =
			new com.substanceofcode.rssreader.businesslogic.OpmlParser(
				"jar:///links-opml.xml", "", "");
		OpmlParser cmpOpmlParser =
			new OpmlParser("jar:///links-opml.xml", "", "");

		compatibilityOpmlParserTestSub(mname, OpmlParser, cmpOpmlParser, false);
	}

	/* Test parse Opml. */
	public void testOpmlParse4() throws Throwable {
		String mname = "testOpmlParse4";
		com.substanceofcode.rssreader.businesslogic.OpmlParser OpmlParser =
			new com.substanceofcode.rssreader.businesslogic.OpmlParser(
				"jar:///links-opml.xml", "", "");
		OpmlParser cmpOpmlParser =
			new OpmlParser("jar:///links-opml.xml", "", "");

		compatibilityOpmlParserTestSub(mname, OpmlParser, cmpOpmlParser, false);
	}

	/* Test parse Opml. */
	public void testOpmlParse5() throws Throwable {
		String mname = "testOpmlParse5";
		com.substanceofcode.rssreader.businesslogic.OpmlParser OpmlParser =
			new com.substanceofcode.rssreader.businesslogic.OpmlParser(
				"jar:///links-opml.xml", "", "");
		OpmlParser cmpOpmlParser =
			new OpmlParser("jar:///links-opml.xml", "", "");

		compatibilityOpmlParserTestSub(mname, OpmlParser, cmpOpmlParser, true);
	}

    public void compatibilityOpmlParserTestSub(final String mname,
			final com.substanceofcode.rssreader.businesslogic.OpmlParser OpmlParser,
			final OpmlParser compatibilityOpmlParser, boolean endFeeds)
	throws Throwable {
		try {
			//#ifdef DLOGGING
			logger.info("Started " + mname);
			if (finestLoggable) {logger.finest(mname + " compatibilityOpmlParserTestSub OpmlParser=" + OpmlParser);}
			//#endif
			//#ifdef DMIDP20
			ready = false;
			OpmlParser.getObservableHandler().addObserver(this);
			//#endif
			OpmlParser.startParsing();
			//#ifdef DMIDP20
			while (!isReady()) {
				synchronized(this) {
					wait(1000L);
				}
			}
			//#else
			OpmlParser.join();
			//#endif
			//#ifdef DLOGGING
			if (fineLoggable) {logger.fine(mname + " OpmlParser.isSuccessfull()=" + OpmlParser.isSuccessfull());}
			//#endif
			RssItunesFeedInfo[] rssfeeds = OpmlParser.getFeeds();
			compatibilityOpmlParser.startParsing();
			compatibilityOpmlParser.join();
			if (fineLoggable) {logger.fine(mname + " compatibilityOpmlParser.isSuccessfull()=" + compatibilityOpmlParser.isSuccessfull());}
			RssItunesFeed[] cmpRssFeeds =
				(RssItunesFeed[])compatibilityOpmlParser.getFeeds();
			assertTrue(mname + " rssfeeds feed length should be > 0",
					rssfeeds.length > 0);
			assertTrue(mname + " cmpRssFeeds feed length should be > 0",
					cmpRssFeeds.length > 0);
			int endIx = endFeeds ? rssfeeds.length : (nextIx + (rssfeeds.length / 5));
			for (; (nextIx < endIx) && (nextIx < rssfeeds.length) && (nextIx < cmpRssFeeds.length);
					nextIx++) {
				//#ifdef DLOGGING
				if (alterLogLevel && (nextIx >= alterix) && (alterix >= 0)) {
					endAlterLogLevel = true;
					svLogLevel = logger.getParent().getLevel();
					logger.getParent().setLevel(Level.parse(newLogLevel));
					alterLogLevel = false;
					logger.info(mname + " altering level nextIx,svLogLevel,newLevel=" + nextIx + "," + svLogLevel + "," + logger.getParent().getLevel());
				} else if (endAlterLogLevel && (nextIx >= endAlterix)) {
					endAlterLogLevel = false;
					logger.getParent().setLevel(svLogLevel);
					logger.info(mname + " reverting level nextIx,svLogLevel,newLevel=" + nextIx + "," + svLogLevel + "," + logger.getParent().getLevel());
				}
				//#endif
				RssItunesFeedInfo feed = rssfeeds[nextIx];
				//#ifdef DLOGGING
				if (finestLoggable) {logger.finest(mname + " nextIx,feed 1=" + nextIx + "," + feed.toString());}
				//#endif
				RssItunesFeed cmpfeed = cmpRssFeeds[nextIx];
				//#ifdef DLOGGING
				if (finestLoggable) {logger.finest(mname + " nextIx,cmpfeed 1=" + nextIx + "," + cmpfeed.toString());}
				//#endif
				String assertInfo = new String("nextIx,name,url=" + nextIx + "," + feed.getName() + "," +  feed.getUrl());
				assertTrue("Original feed must equal expected feed " + assertInfo, cmpfeed.equals(feed));
				if (((feed.getUrl().indexOf("http://") >= 0) &&
					//#ifndef DSMALLMEM
					HTMLParser.isHtml(feed.getUrl()) ||
					//#endif
				   (feed.getName().indexOf("Russian") >= 0)) ||
				   (feed.getUrl().indexOf("rss-gnu-utf8.xml") >= 0)) {
					//#ifdef DLOGGING
					if (fineLoggable) {logger.fine(mname + " skipping http, html, or gnu XML URL nextIx,feed.getName(),feed.getUrl() 2=" + nextIx + "," + feed.getName() + "," + feed.getUrl());}
					//#endif
					continue;
				} else if (!feed.getUrl().endsWith(".xml") && !feed.getUrl().endsWith(".sh")) {
					//#ifdef DLOGGING
					if (fineLoggable) {logger.fine(mname + " skipping unknown feed.getUrl()=" + feed.getUrl());}
					//#endif
					continue;
				}
				com.substanceofcode.rssreader.businesslogic.RssFeedParser fparser =
					new com.substanceofcode.rssreader.businesslogic.RssFeedParser(
							(com.substanceofcode.rssreader.businessentities.RssItunesFeed)feed);
				//#ifdef DMIDP20
				fparser.makeObserable(null, true, 10);
				ready = false;
				fparser.getObservableHandler().addObserver(this);
				fparser.getParsingThread().start();
				while (!isReady()) {
					synchronized(this) {
						wait(1000L);
					}
				}
				boolean successful = fparser.isSuccessfull();
				Throwable fexc = fparser.getEx();
				//#else
				boolean successful = true;
				Throwable fexc = null;
				try {
					fparser.parseRssFeed( false, 10);
				} catch(Throwable e) {
					successful = false;
					fexc = e;
				}
				//#endif
				RssFeedParser cmpFparser = new RssFeedParser(cmpfeed);
				//#ifdef DMIDP20
				cmpFparser.makeObserable(null, true, 10);
				ready = false;
				cmpFparser.getObserverManager().addObserver(this);
				cmpFparser.getParsingThread().start();
				while (!isReady()) {
					synchronized(this) {
						wait(1000L);
					}
				}
				boolean cmpSuccessful = cmpFparser.isSuccessfull();
				Throwable cmpFexc = cmpFparser.getEx();
				//#else
				boolean cmpSuccessful = true;
				Throwable cmpFexc = null;
				try {
					cmpFparser.parseRssFeed( false, 10);
				} catch(Throwable e) {
					cmpSuccessful = false;
					cmpFexc = e;
				}
				//#endif
				if (successful && (cmpSuccessful != successful) &&
						feed.getName().startsWith("48 nexus")) {
					//#ifdef DLOGGING
					logger.info(mname + " compatibilityOpmlParserTestSub difference is OK, skipping nextIx,feed.getName(),fexc,cmpFexc=" + nextIx + "," + feed.getName() + "," + fexc + "," + cmpFexc, cmpFexc);
					//#endif
					continue;
				}
				assertEquals("compatibilityOpmlParserTestSub successful must equal nextIx=" + nextIx, cmpSuccessful, successful);
				if (!cmpSuccessful) {
					//#ifdef DLOGGING
					if (fineLoggable) {logger.fine(mname + " compatibilityOpmlParserTestSub not successful nextIx,feed.getName(),fexc,cmpFexc=" + nextIx + "," + feed.getName() + "," + fexc + "," + cmpFexc, fexc);}
					//#endif
					continue;
				}
				RssItunesFeedInfo nfeed = fparser.getRssFeed();
				//#ifdef DLOGGING
				if (finestLoggable) {logger.finest(mname + " nextIx,feed 3=" + nextIx + "," + feed.toString());}
				//#endif
				RssItunesFeed ncmpfeed = (RssItunesFeed)cmpFparser.getRssFeed();
				//#ifdef DLOGGING
				if (finestLoggable) {logger.finest(mname + " nextIx,ncmpfeed 3=" + nextIx + "," + ncmpfeed.toString());}
				//#endif
				assertTrue("Feed must equal expected feed " + assertInfo, ncmpfeed.equals(nfeed));
				// Free up memory.
				rssfeeds[nextIx] = null;
				// Free up memory.
				cmpRssFeeds[nextIx] = null;

			}
			//#ifdef DLOGGING
			if (fineLoggable) {logger.fine(mname + " finished.");}
			//#endif
		} catch (Throwable e) {
			//#ifdef DLOGGING
			logger.severe(mname + " failure ",e);
			//#endif
			e.printStackTrace();
			throw e;
		}
	}

}
//#endif
