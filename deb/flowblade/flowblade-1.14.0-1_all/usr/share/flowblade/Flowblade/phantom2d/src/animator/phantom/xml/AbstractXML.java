package animator.phantom.xml;

/*
    Copyright Janne Liljeblad 2006,2007,2008

    This file is part of Phantom2D.

    Phantom2D is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    Phantom2D is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with Phantom2D.  If not, see <http://www.gnu.org/licenses/>.
*/

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class AbstractXML
{
	protected static Document doc;

	public static void setDoc( Document doc_ ){ doc = doc_; }

	public static int getInt( Element e, String attr ){ return Integer.parseInt( e.getAttribute( attr ) );}
	public static String intStr( int i ){ return Integer.toString( i ); }

	public static long getLong( Element e, String attr ){ return Long.parseLong( e.getAttribute( attr ) );}
	public static String longStr( long l ){ return Long.toString( l ); }

	public static boolean getBoolean( Element e, String attr ){ return ( new Boolean( e.getAttribute( attr ))).booleanValue(); }
	public static String booleanStr( boolean val ){ return Boolean.toString( val ); }

	public static float getFloat( Element e, String attr ){ return (new Float( e.getAttribute( attr ))).floatValue(); }
	public static String floatStr( float f ){ return Float.toString( f ); }

}//end class