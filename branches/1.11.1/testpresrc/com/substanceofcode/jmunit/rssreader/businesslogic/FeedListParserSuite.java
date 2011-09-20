//--Need to modify--#preprocess
/*
 * FeedListParserSuite.java
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
 * IB 2010-05-24 1.11.5RC2 Unit test FeedListParser and subclasses.
 * IB 2010-05-28 1.11.5RC2 Don't use HTMLParser and HtmlLinkParserTest in small memory MIDP 1.0 to save space.
 * IB 2010-10-12 1.11.5Dev9 Add --Need to modify--#preprocess to modify to become //#preprocess for RIM preprocessor.
 * IB 2011-01-14 1.11.5Alpha15 Only compile this if it is the full version.
 * IB 2011-01-14 1.11.5Alpha15 Use conditional preprocessed cldc11 code with modifications instead of cldc10 code.
 * IB 2011-03-08 1.11.5Dev17 Test HtmlLinkParser2Test.
 */

// Expand to define full vers define
@DFULLVERSDEF@
// Expand to define full vers define
@DINTLINKDEF@
// Expand to define memory size define
@DMEMSIZEDEF@
// Expand to define test define
@DTESTDEF@
// Expand to define JMUnit test define
@DJMTESTDEF@
// Expand to define test define
@DCOMPATDEF@
// Expand to define logging define
@DLOGDEF@

//#ifdef DJMTEST
//#ifdef DFULLVERS
package com.substanceofcode.jmunit.rssreader.businesslogic;

import jmunit.framework.cldc11.TestSuite;

final public class FeedListParserSuite extends TestSuite {

	public FeedListParserSuite() {
		super("FeedListParserSuite");
		//#ifndef DSMALLMEM
		add(new HtmlLinkParser3Test());
		add(new HtmlLinkParserTest());
		add(new HtmlLinkParser2Test());
		//#endif
	}
}
//#endif
//#endif
