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

import java.io.InputStream;
import java.util.Hashtable;
import java.util.Iterator;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

//--- A class for saving and loading prefence values in XML format.
public class XMLPrefDoc
{
	private static final String PREF_ELEM = "preference";

	private static final String VALUE_ATTR = "value";//--- attribute name for values
	private static final String KEY_ATTR = "key";//--- attribute name for keys

	private Document doc;
	private String path;
	private String rootElemName;
	private Hashtable<String,String> values = new Hashtable<String,String>();
	private Hashtable<String,String> defaultValues = new Hashtable<String,String>();
	private Element root = null;

	private NodeList iterList = null;
	private int iterIndex = 0;

	public XMLPrefDoc( String path, String rootElemName )
	{
		this.path = path;
		this.rootElemName = rootElemName;
	}

	public Document getDoc(){ return doc; }

	public Element getRootElement(){ return root; }

	public void loadPrefs( boolean fromJar )
	{
		if( !fromJar )
		{
			doc = PhantomDocUtils.loadXMLDoc( path );
		}
		else
		{
			InputStream is = getClass().getClassLoader().getResourceAsStream( path );
			doc = PhantomDocUtils.loadXMLDoc( is );
		}

		if( doc != null )
		{
			root = doc.getDocumentElement();
			fillValuesTable();
		}
	}

	public void createPrefsDoc()
	{
		doc = PhantomDocUtils.createDocument();
		root = doc.createElement(rootElemName);
		doc.appendChild(root);
		writeValuesToDoc();
	}

	public void writeXMLFile()
	{
		writeXMLFile( path );
	}
	public void writeXMLFile( String writePath )
	{
		PhantomDocUtils.writeXMLFile( doc, writePath );
	}

	public void initIter( Element from, String tagName )
	{
		iterList = from.getElementsByTagName( tagName );
		iterIndex = 0;
	}

	public boolean iterMore()
	{
		if( iterIndex < iterList.getLength() ) return true;
		return false;
	}
	public Element iterNext()
	{
		iterIndex++;
		return (Element) iterList.item( iterIndex - 1 );
	}

	public void putValue( String key, String value ){ values.put( key, value ); }
	public void putValue( String key, int value ){ values.put( key, Integer.toString( value ) ); }
	//public void putValue( String key, float value ){ values.put( key, Float.toString( value ) ); }
	public void putValue( String key, boolean value ){ values.put( key, Boolean.toString( value ) ); }

	public void putDefaultValue( String key, String value )
	{
		values.put( key, value );
		defaultValues.put( key, value ); 
	}
	public void putDefaultValue( String key, int value )
	{
		 values.put( key, Integer.toString( value ) );
		 defaultValues.put( key, Integer.toString( value ) ); 
	}
	public void putDefaultValue( String key, float value )
	{
		values.put( key, Float.toString( value ) ); 
		defaultValues.put( key, Float.toString( value ) );
	}
	public void putDefaultValue( String key, boolean value )
	{
		values.put( key, Boolean.toString( value ) );
		defaultValues.put( key, Boolean.toString( value ) );
	}

	/*
	public void restoreDefault( String key )
	{
		String defaultValue = defaultValues.get( key );
		values.put( key, defaultValue );
	}
	*/

	public int getInt( String key )
	{
		String value = values.get( key );
		return Integer.parseInt( value );
	}

	public String getString( String key )
	{
		return values.get( key );
	}

	public float getFloat( String key )
	{
		String value = values.get( key );
		return Float.parseFloat( value );
	}

	public boolean getBoolean( String key )
	{
		String value = values.get( key );
		return Boolean.parseBoolean( value );
	}

	private void writeValuesToDoc()
	{
		Iterator<String> i = values.keySet().iterator();
		
		while( i.hasNext() )
		{
			String key = (String) i.next();
			String value = values.get( key );
			Element e = doc.createElement( PREF_ELEM );
			e.setAttribute( KEY_ATTR, key );
			e.setAttribute( VALUE_ATTR, value );
			root.appendChild( e );
		}
	}

	private void fillValuesTable()
	{
		initIter( root, PREF_ELEM );
		while( iterMore() )
		{
			Element e = iterNext();
			String key = e.getAttribute( KEY_ATTR );
			String value = e.getAttribute( VALUE_ATTR );
			values.put( key, value );
		}
	}

}//end class
