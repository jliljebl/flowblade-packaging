package animator.phantom.xml;

/*
    Copyright Janne Liljeblad

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

import org.w3c.dom.Element;

import animator.phantom.project.Project;
import animator.phantom.renderer.ImageOperation;
import animator.phantom.renderer.RenderNode;

public class TimeLineClipXML extends AbstractXML
{
	public static String ELEMENT_NAME = "clip";
	//public static String TLC_NODE_ELEMENT_NAME = "tlcnode";
	public static String NODEID_ATTR = "nodeid";
	//public static String OPEN_ATTR = "isopen";

	public static ImageOperation getObject( Element e, Project projectObj )
	{
		RenderNode node = projectObj.getRenderFlow().getNode( getInt( e, "nodeid" ));
		return node.getImageOperation();
	}

	public static Element getElement( RenderNode node )
	{
		Element e = doc.createElement( ELEMENT_NAME );
		e.setAttribute( NODEID_ATTR, intStr( node.getID() ));
		return e;
	}

}//end class
