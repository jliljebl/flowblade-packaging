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

import java.util.Vector;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import animator.phantom.project.Bin;
import animator.phantom.project.Project;
import animator.phantom.renderer.FileSource;

public class BinXML extends AbstractXML
{
	public static String ELEMENT_NAME = "bin";
	public static String FS_ELEMENT_NAME = "fs";

	public static Bin getObject( Element e, Project projectObj )
	{
		Bin bin = new Bin();
		bin.setName( e.getAttribute( "name" ) );
		//---
		NodeList fsources = e.getElementsByTagName( FS_ELEMENT_NAME );
		Vector<FileSource> fsObjs = new Vector<FileSource>();
		for( int i = 0; i < fsources.getLength(); i++ )
		{
			Element fs = (Element) fsources.item( i );
			int id = getInt( fs, "id" );
			fsObjs.add( projectObj.getFileSource( id ) );
		}
		bin.addFileSourceVector( fsObjs );
		return bin;
	}

	public static Element getElement( Bin bin )
	{
		Element e = doc.createElement( ELEMENT_NAME );
		e.setAttribute( "name", bin.getName() );
		Vector<FileSource> fsObjs = bin.getFileSources();	
		for( FileSource fs : fsObjs )
		{
			Element fsElem = doc.createElement( FS_ELEMENT_NAME );
			fsElem.setAttribute( "id", intStr( fs.getID() ));
			e.appendChild( fsElem );
		}
		return e;
	}

}//end class