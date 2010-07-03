/*
 * Observable.java
 *
 * Copyright (C) 2010 Irving Bunton, Jr
 * http://www.substanceofcode.com
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 * You can redistribute it and/or modify
 * it under the terms the GNU Lessor General Public License as published by
 * the Free Software Foundation; either version 2.1 of the License, or
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
 * IB 2010-06-15 1.11.5Dev2 Use observer pattern for feed parsing to prevent hangs from spotty networks and bad URLs.
 * IB 2010-06-15 1.11.5Dev2 Use version that can be distributed as GPL or LGPL.
 */

// Expand to define MIDP define
//#define DMIDP20
//#ifdef DMIDP20
package net.yinlight.j2me.observable;

public interface Observable {

	ObservableHandler getObservableHandler();

}
//#endif
