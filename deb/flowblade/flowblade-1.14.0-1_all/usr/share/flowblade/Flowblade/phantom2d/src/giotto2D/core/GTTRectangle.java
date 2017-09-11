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

import java.awt.geom.Rectangle2D;

public class GTTRectangle extends GTTObject
{
	protected Rectangle2D.Float rect;

	public GTTRectangle( int width, int height )
	{
		init();
		rect = new Rectangle2D.Float( 0, 0, (float) width, (float) height );
	}

	public GTTRectangle( float width, float height )
	{
		init();
		rect = new Rectangle2D.Float( 0, 0, (float) width, (float) height );
	}

	public void drawUnTransformedObject()
	{
		setFillAttributes();
		if( fillIsVisible() )
		{
			setFillAttributes();
			g.fill( rect );
		}
		if( strokeIsVisible() )
		{
			setStrokeAtrributes();
			g.draw( rect );
		}
	}
}//end class