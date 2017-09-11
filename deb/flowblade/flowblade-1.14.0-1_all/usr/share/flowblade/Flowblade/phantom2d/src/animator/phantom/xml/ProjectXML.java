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

import java.awt.Dimension;

import org.w3c.dom.Element;

import animator.phantom.project.Project;

public class ProjectXML extends AbstractXML
{
	public static String ELEMENT_NAME = "project";

	public static Project getObject( Element e )
	{
		Project project = new Project();
		project.setName( e.getAttribute( "name" ) );
		int w = getInt( e,"width" );
		int h = getInt( e, "height" );
		project.setScreenDimensions( new Dimension( w, h ) );
		project.setLength( getInt( e, "length" ) );
		project.setFramesPerSecond( getInt( e, "fps" ) );
		project.setFormatName(  e.getAttribute( "fname" ) );
		return project;
	}

	public static Element getElement( Project project )
	{
		Element e = doc.createElement( ELEMENT_NAME );
		e.setAttribute( "name", project.getName()  );
		e.setAttribute( "width" , intStr( project.getScreenDimensions().width  ) );
		e.setAttribute( "height", intStr(  project.getScreenDimensions().height )  );
		e.setAttribute( "length", intStr( project.getLength() ) );
		e.setAttribute( "fps", intStr( project.getFramesPerSecond() ) );
		e.setAttribute( "fname",  project.getFormatName() );
		return e;
	}

}//end class
