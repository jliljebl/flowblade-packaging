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

import org.w3c.dom.Element;

import animator.phantom.gui.flow.FlowBox;
import animator.phantom.project.Project;
import animator.phantom.renderer.RenderNode;

public class FlowBoxXML extends AbstractXML
{
	public static String ELEMENT_NAME = "flowbox";

	public static FlowBox getObject( Element e, Project projectObj )
	{
		int x = getInt( e, "x" );
		int y = getInt( e, "y");
		FlowBox box = new FlowBox( x,y );
		RenderNode node = projectObj.getRenderFlow().getNode( getInt( e, "nodeid" ));
		box.setRenderNode(node);
		box.createConnectionPoints();
		box.preRender();
		return box;
	}

	public static Element getElement( FlowBox box )
	{
		Element e = doc.createElement( ELEMENT_NAME );
		e.setAttribute( "x", intStr( box.getX() ));
		e.setAttribute( "y", intStr( box.getY() ));
		e.setAttribute( "nodeid", intStr( box.getRenderNode().getID() ));
		return e;
	}

}//end class