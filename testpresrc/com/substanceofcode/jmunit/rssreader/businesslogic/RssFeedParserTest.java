/*
 * RssFeedParserTest.java
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
 * IB 2010-03-07 1.11.4RC1 Don't use observer pattern for MIDP 1.0 as it increases size.
 * IB 2010-05-24 1.11.5RC2 Unit test RssFeedParser class.
*/

// Expand to define MIDP define
@DMIDPVERS@
// Expand to define test define
@DTESTDEF@
// Expand to define JMUnit test define
@DJMTESTDEF@
// Expand to define logging define
@DLOGDEF@

//#ifdef DJMTEST
package com.substanceofcode.jmunit.rssreader.businesslogic;

import java.util.Date;

import jmunit.framework.cldc10.TestCase;

import com.substanceofcode.rssreader.businessentities.RssItunesFeed;
import com.substanceofcode.rssreader.businessentities.RssItunesItem;
import com.substanceofcode.rssreader.businesslogic.RssFeedParser;
import com.substanceofcode.rssreader.presentation.RssReaderMIDlet;
//#ifdef DMIDP20
import net.eiroca.j2me.observable.Observer;
import net.eiroca.j2me.observable.Observable;
//#endif

import com.substanceofcode.jmunit.utilities.BaseTestCase;

final public class RssFeedParserTest extends BaseTestCase
//#ifdef DMIDP20
implements Observer
//#endif
{

	private boolean ready = false;

	public RssFeedParserTest() {
		super(3, "RssFeedParserTest");
	}

	public void test(int testNumber) throws Throwable {
		switch (testNumber) {
			case 0:
				testFeedParse1();
				break;
			case 1:
				testFeedParse2();
				break;
			case 2:
				testFeedParse3();
				break;
			default:
				Exception e = new Exception(
						"No such test testNumber=" + testNumber);
				//#ifdef DLOGGING
				logger.severe("test no switch case failure #" +
						testNumber, e);
				//#endif
				throw e;
		}
	}

	//#ifdef DMIDP20
	public void changed(Observable observable) {
		ready = true;
	}
	//#endif

	public boolean isReady() {
		return ready;
	}

	/* Test parse Itunes. */
	public void testFeedParse1() throws Throwable {
		String mname = "testFeedParse1";
		RssItunesFeed feed = new RssItunesFeed(
			"test1", "jar:///rss-german-itunes-utf8.xml", "", "");
		RssItunesFeed cmfeed = new RssItunesFeed(feed);
		cmfeed.modifyItunes(
			true, "title1", "description1", "language1", "author1", "subtitle1",
					"summary1", RssItunesItem.convExplicit("no"));
		feedParserTestSub(mname, feed, cmfeed, false, 20);
	}

	/* Test parse Itunes. */
	public void testFeedParse2() throws Throwable {
		String mname = "testFeedParse2";
		RssItunesFeed feed = new RssItunesFeed(
			"test2", "http://mobilerssreader.sourceforge.net/testdata/rss2.xml", "", "");
		RssItunesFeed cmfeed = new RssItunesFeed(feed);
		cmfeed.modifyItunes(
			true, "title2", "description2", "language2", "author2", "subtitle2",
					"summary2", RssItunesItem.convExplicit("yes"));
		feedParserTestSub(mname, feed, cmfeed, false, 20);
		feedParserTestSub(mname, feed, cmfeed, true, 20);
	}

	/* Test parse Itunes. */
	public void testFeedParse3() throws Throwable {
		String mname = "testFeedParse3";
		RssItunesFeed feed = new RssItunesFeed(
			"test3", "jar:///rss-1252.xml", "", "");
		RssItunesFeed cmfeed = new RssItunesFeed(feed);
		cmfeed.modifyItunes(
			true, "title2", "description2", "language2", "author2", "subtitle2",
					"summary2", RssItunesItem.convExplicit("clean"));
		feedParserTestSub(mname, feed, cmfeed, false, 20);
		feedParserTestSub(mname, feed, cmfeed, true, 20);
	}

	private void waitReady() throws Throwable {
		while (!isReady()) {
			synchronized(this) {
				wait(1000L);
			}
		}
	}

    public void feedParserTestSub(final String mname, RssItunesFeed feed,
			RssItunesFeed cmpfeed,
			boolean updFeed, int maxItemCount)
	throws Throwable {
		try {
			//#ifdef DLOGGING
			logger.info("Started " + mname);
			if (finestLoggable) {logger.finest(mname + " feed=" + feed);}
			if (finestLoggable) {logger.finest(mname + " updFeed,maxItemCount=" + updFeed + "," + maxItemCount);}
			//#endif
			RssFeedParser fparser = new RssFeedParser(new RssItunesFeed(feed));
			//#ifdef DMIDP20
			fparser.makeObserable(null, updFeed, maxItemCount);
			fparser.getObserverManager().addObserver(this);
			//#else
			fparser.parseRssFeed( false, 10);
			//#endif
			fparser.getParsingThread().start();
			//#ifdef DMIDP20
			waitReady();
			//#else
			fparser.getParsingThread().join();
			//#endif
			RssItunesFeed nfeed = fparser.getRssFeed();
			//#ifdef DLOGGING
			if (finestLoggable) {logger.finest(mname + " nfeed=" + nfeed);}
			//#endif
			assertTrue(mname + " feedParserTestSub ", !cmpfeed.equals(nfeed));
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
