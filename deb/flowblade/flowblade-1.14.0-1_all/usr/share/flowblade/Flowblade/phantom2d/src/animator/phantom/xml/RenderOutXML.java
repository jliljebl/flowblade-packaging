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

import java.io.File;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import animator.phantom.controller.RenderModeController;

public class RenderOutXML extends AbstractXML
{
	public static String ELEMENT_NAME = "renderoutput";
	public static String WF_ATTR = "writefolder";
	public static String FRAME_NAME_ATTR = "framename";

	public static void setRenderOutValues( Element projE )
	{
		RenderModeController.reset();
		
		NodeList eList = projE.getElementsByTagName( ELEMENT_NAME );
		if( eList.getLength() == 0 ) return;

		Element e = (Element) eList.item( 0 );
		String val = e.getAttribute( WF_ATTR );
		if( val.equals( "null" ) )
			RenderModeController.setWriteFolder( null );
		else
		{
			File f = new File( val );
			RenderModeController.setWriteFolder( f );
		}
		val = e.getAttribute( FRAME_NAME_ATTR );
		if( val.equals( "null" ) )
			val = "frame";
		RenderModeController.setFrameName( val );
	}

	public static Element getElement()
	{
		Element e = doc.createElement( ELEMENT_NAME );

		//--- Render output
		String val = null;
		if( RenderModeController.getWriteFolder() == null )
			val = "null";
		else
			val = RenderModeController.getWriteFolder().getAbsolutePath();

		e.setAttribute( WF_ATTR, val );
		if( RenderModeController.getFrameName() == null )
			val = "null";
		else
			val = RenderModeController.getFrameName();
		e.setAttribute( FRAME_NAME_ATTR, val );

		return e;
	}

}//end class