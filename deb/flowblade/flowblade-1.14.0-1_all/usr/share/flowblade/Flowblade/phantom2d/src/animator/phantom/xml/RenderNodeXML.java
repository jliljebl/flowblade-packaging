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

import animator.phantom.renderer.RenderNode;

public class RenderNodeXML extends AbstractXML
{
	public static String ELEMENT_NAME = "rendernode";
	public static String SOURCE_ELEMENT_NAME = "source";
	public static String TARGET_ELEMENT_NAME = "target";

	public static RenderNode getObject( Element e )
	{
		NodeList slist = e.getElementsByTagName( SOURCE_ELEMENT_NAME );
		Vector<Integer> sourcesTempIDs = new Vector<Integer>();
		Vector <RenderNode> sources = new Vector<RenderNode>();
		for( int i = 0; i < slist.getLength(); i++ )
		{
			sources.add( null );
			sourcesTempIDs.add( null );
		}
		for( int i = 0; i < slist.getLength(); i++ )
		{
			Element sourceE = (Element) slist.item( i );
			int id = getInt( sourceE, "id" );
			int index = getInt( sourceE, "index" );
			sourcesTempIDs.set( index, new Integer( id ));
		}
		NodeList tlist = e.getElementsByTagName( TARGET_ELEMENT_NAME );
		Vector<Integer> targetIDs = new Vector<Integer>();
		Vector <RenderNode> targets = new Vector<RenderNode>();
		for( int i = 0; i < tlist.getLength(); i++ )
		{
			targets.add( null );
			targetIDs.add( null );
		}
		for( int i = 0; i < tlist.getLength(); i++ )
		{
			Element targetE = (Element) tlist.item( i );
			int id = getInt( targetE, "id" );
			int index = getInt( targetE, "index" );
			targetIDs.set( index, new Integer( id ));
		}
		RenderNode node = new RenderNode();
		node.setID( getInt( e, "id" ) );
		node.loadSources( sourcesTempIDs, sources );
		node.loadTargets( targetIDs, targets );
		return node;
	}

	public static Element getElement( RenderNode nObj )
	{
		Element e = doc.createElement( ELEMENT_NAME );
		e.setAttribute( "id", intStr( nObj.getID() ) );

		Vector<RenderNode> sObjs =  nObj.getSources();
		for( int i = 0; i < sObjs.size(); i++ )
		{
			Element source = doc.createElement( SOURCE_ELEMENT_NAME );
			RenderNode sObj = (RenderNode) sObjs.elementAt( i );
			int id = -1;
			if( sObj != null ) id = sObj.getID();
			source.setAttribute( "id", intStr( id ));
			source.setAttribute( "index", intStr( i ) );
			e.appendChild( source );
		}
		
		Vector<RenderNode> tObjs =  nObj.getTargetsVector();
		for( int i = 0; i < tObjs.size(); i++ )
		{
			Element target = doc.createElement( TARGET_ELEMENT_NAME );
			RenderNode tObj = (RenderNode) tObjs.elementAt( i );
			int id = -1;
			if( tObj != null ) id = tObj.getID();
			target.setAttribute( "id", intStr( id ));
			target.setAttribute( "index", intStr( i ) );
			e.appendChild( target );
		}
		return e;
	}

}//end class