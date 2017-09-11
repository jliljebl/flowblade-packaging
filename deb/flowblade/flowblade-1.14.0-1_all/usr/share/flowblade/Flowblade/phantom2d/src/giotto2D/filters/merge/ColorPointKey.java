package giotto2D.filters.merge;

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

import giotto2D.filters.AbstractFilter;
import giotto2D.libcolor.GiottoRGBInt;

import java.awt.image.BufferedImage;
import java.util.Vector;

public class ColorPointKey extends AbstractFilter
{
	private Vector<GiottoRGBInt> colors;

	private int r_look[] = new int[256];
	private int g_look[] = new int[256];
	private int b_look[] = new int[256];

	private int valToAlpha[] = new int[256];

	private int slope_width = 8;

	public ColorPointKey()
	{
		clearPoints();
	}

	public void clearTables()
	{
		clearTable( r_look );
		clearTable( g_look );
		clearTable( b_look );
		clearValToAlpha();
	}

	private void clearTable( int[] table )
	{
		for( int i = 0; i < 256; i++ ) table[ i ] = 255;
	}

	private void clearValToAlpha()
	{
		for( int i = 0; i < 256; i++ ) valToAlpha[ i ] = i;
	}

	public void clearPoints()
	{
		colors = new Vector<GiottoRGBInt>();
		clearTables();
	}

	public void setColorPoints( Vector<GiottoRGBInt> newColors )
	{
		colors = newColors;
	}

	public void setValToAlphaTable( int[] table )
	{
		valToAlpha = table;
	} 

	public void setSlopeWidth( int w )
	{
		slope_width = w;
	}

	public void filter( BufferedImage dest )
	{
		int a,r,g,b;
		int ar, ag, ab;
		int[] dPix = getBank( dest );

		for( int i = 0; i < dPix.length; i++ )
		{
			r = ( dPix[ i ] >> red ) & 0xff;
			g = ( dPix[ i ] >> green ) & 0xff;
			b = dPix[ i ] & 0xff;
			
			ar = r_look[ r ];
			ag = g_look[ g ];
			ab = b_look[ b ];

			a = valToAlpha[ (ar + ag + ab)/3 ];

			dPix[ i ] = ( a << alpha ) | (r << red ) | g << green | b;
		}
	}

	public void createLookUps()
	{
		if( colors.size() == 0)	
		{
			clearPoints();
			return;
		}
		int r_max = 0;
		int r_min = 255;
		int g_max = 0;
		int g_min = 255;
		int b_max = 0;
		int b_min = 255;

		for( int i = 0; i < colors.size(); i++ )
		{
			GiottoRGBInt c = colors.elementAt( i );
			if( r_max < c.r ) r_max = c.r;
			if( r_min > c.r ) r_min = c.r;
			if( g_max < c.g ) g_max = c.g;
			if( g_min > c.g ) g_min = c.g;
			if( b_max < c.b ) b_max = c.b;
			if( b_min > c.b ) b_min = c.b;
		}

		writeLookUp( r_look, r_min, r_max, slope_width  );
		writeLookUp( g_look, g_min, g_max, slope_width  );
		writeLookUp( b_look, b_min, b_max, slope_width  );
	}

	private void writeLookUp( int[] table, int min, int max, int slope )
	{
		for( int i = 0; i < 256; i++ )
		{
			if( i < ( min - slope) ) table[i] = 255;
			else if( i < min ) table[i] = 128; //(int) ((((float) min - i)/((float) slope)) * (float)255);
			else if( i <= max ) table[i] = 0;
			else if( i < max + slope ) table[i] = 128;// 255 - (int) ((((float) max + slope - i)/((float) slope)) * (float)255);
			else table[i] = 255;
		}
	}

}//end class
