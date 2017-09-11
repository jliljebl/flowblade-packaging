package giotto2D.core;

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

import java.awt.geom.Ellipse2D;

public class GTTOval extends GTTObject
{
	protected Ellipse2D.Float oval;

	public GTTOval( int width, int height )
	{
		init();
		oval = new Ellipse2D.Float( 0, 0, (float) width, (float) height );
	}

	public GTTOval( float width, float height )
	{
		init();
		oval = new Ellipse2D.Float( 0, 0, (float) width, (float) height );
	}

	public void drawUnTransformedObject()
	{
		setFillAttributes();
		if( fillIsVisible() )
		{
			setFillAttributes();
			g.fill( oval );
		}
		if( strokeIsVisible() )
		{
			setStrokeAtrributes();
			g.draw( oval );
		}
	}

}//end class