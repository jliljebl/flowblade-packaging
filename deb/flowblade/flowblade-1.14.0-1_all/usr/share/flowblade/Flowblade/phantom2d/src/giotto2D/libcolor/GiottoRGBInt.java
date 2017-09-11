package giotto2D.libcolor;

/*
    Copyright Janne Liljeblad 2008.

    This file is part of JFilters.

    JFilters is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    JFilters is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with JFilters.  If not, see <http://www.gnu.org/licenses/>.
*/

import java.awt.Color;

//--- Color in RGBA-space.
public class GiottoRGBInt
{
  	public int r = 0;
	public int g = 0;
	public int b = 0;
	//public int a = 0;

	public GiottoRGBInt(){}

	public GiottoRGBInt( int r, int g, int b )
	{
		this( r, g, b, 255 );
	}

	public GiottoRGBInt( Color c )
	{
		this( c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha() );
	}

	public GiottoRGBInt( int r, int g, int b, int a )
	{
		this.r = r;
		this.g = g;
		this.b = b;
		//this.a = a;
	}

}//end class