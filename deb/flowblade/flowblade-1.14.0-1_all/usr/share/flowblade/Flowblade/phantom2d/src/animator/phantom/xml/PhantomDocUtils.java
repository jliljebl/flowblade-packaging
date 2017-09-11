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

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

//--- Collection of methods that are frequntly needed when handling XML docs.
//--- THE FACT THAT ROOT AND ITERATOR USE STATIC MEMBERS MAKES THIS INHERENTLY UNSAFE FOR THREADS
//--- (and a garbage desing too)
public class PhantomDocUtils
{
	//--- Root
	//protected static Element root = null;

	//--- Iterator
	protected static NodeList iterList = null;
	protected static int iterIndex = 0;

	public static Document loadXMLDoc( String path )
	{
		Document doc = null;
		try 
		{
			DocumentBuilderFactory factory = 
				DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			doc = builder.parse( path );
			//System.out.println("XML document read: " + path );
		}
		catch (Exception e) { System.out.println("loadXMLDoc(path), Exception"); }

		
		return doc;
	}

	public static Document loadXMLDoc( InputStream is )
	{
		Document doc = null;
		try 
		{
			DocumentBuilderFactory factory = 
				DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			doc = builder.parse( is );
			System.out.println("XML document from stream read and parsed");
		}
		catch (Exception e) { System.out.println("loadXMLDoc(is), Exception"); }

		return doc;
	}

	/*
	public static void writeXMLFile( Document doc, OutputStream outs )
	{
		try
		{
			//--- Source
			DOMSource source = new DOMSource(doc);

			//--- Transformer
			TransformerFactory transfac = TransformerFactory.newInstance();
			Transformer trans = transfac.newTransformer();
			trans.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
			trans.setOutputProperty(OutputKeys.INDENT, "yes");
		
			//--- Streams
			BufferedOutputStream buf = new BufferedOutputStream( outs );
			StreamResult result = new StreamResult(buf);
			
			//--- Do transform
			trans.transform(source, result);

			//--- Write to disk.
			outs.flush();
			outs.close();
		}
		catch( Exception e )
		{
			System.out.println("PhantomDocUtils.writeXMLFile() error:");
			System.out.println(e);
		}
	}
	*/
	public static void writeXMLFile( Document doc, String path )
	{
		try
		{
			//--- Source
			DOMSource source = new DOMSource(doc);

			//--- Transformer
			TransformerFactory transfac = TransformerFactory.newInstance();
			Transformer trans = transfac.newTransformer();
			trans.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
			trans.setOutputProperty(OutputKeys.INDENT, "yes");
		
			//--- Streams
			FileOutputStream fos = new FileOutputStream( path );
			BufferedOutputStream buf = new BufferedOutputStream( fos );
			StreamResult result = new StreamResult(buf);
			
			//--- Do transform
			trans.transform(source, result);

			//--- Write to disk.
			fos.flush();
			fos.close();
		}
		catch( Exception e )
		{
			System.out.println("PhantomDocUtils.writeXMLFile() error:");
			System.out.println(e);
		}
	}

	public static Document createDocument()
	{
		Document doc = null;
		try
		{
			DocumentBuilderFactory dbfac = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = dbfac.newDocumentBuilder();
			doc = docBuilder.newDocument();
		}
		catch (Exception e) 
		{
			System.out.println(e);
        	}
		return doc;
	}

	protected static Element getFirstChild( Element from, String elemName )
	{ 
		NodeList list = from.getElementsByTagName( elemName );
		if( list.getLength() == 0 ) return null;
		return (Element) list.item( 0 );
	}
	protected static void initIter( Element from, String tagName )
	{
		iterList = from.getElementsByTagName( tagName );
		iterIndex = 0;
	}
	protected static boolean iterMore()
	{
		if( iterIndex < iterList.getLength() ) return true;
		return false;
	}
	protected static Element iterNext()
	{
		iterIndex++;
		return (Element) iterList.item( iterIndex - 1 );
	}

	public static int getInt( Element e, String attr ){ return Integer.parseInt( e.getAttribute( attr ) );}
	//public static String intStr( int i ){ return Integer.toString( i ); }

	//public static long getLong( Element e, String attr ){ return Long.parseLong( e.getAttribute( attr ) );}
	//public static String longStr( long l ){ return Long.toString( l ); }

	//public static boolean getBoolean( Element e, String attr ){ return ( new Boolean( e.getAttribute( attr ))).booleanValue(); }
	//public static String booleanStr( boolean val ){ return Boolean.toString( val ); }

	//public static float getFloat( Element e, String attr ){ return (new Float( e.getAttribute( attr ))).floatValue(); }
	//public static String floatStr( float f ){ return Float.toString( f ); }

}//end class
