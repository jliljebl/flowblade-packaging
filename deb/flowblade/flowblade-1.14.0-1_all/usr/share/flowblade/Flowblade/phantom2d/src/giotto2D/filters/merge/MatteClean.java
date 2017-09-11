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

public class MatteClean extends AbstractFilter
{
	private Vector<GiottoRGBInt> fgColors;
	private Vector<GiottoRGBInt> bgColors;

	private int fg_r_look[] = new int[256];
	private int fg_g_look[] = new int[256];
	private int fg_b_look[] = new int[256];

	private int bg_r_look[] = new int[256];
	private int bg_g_look[] = new int[256];
	private int bg_b_look[] = new int[256];

	public MatteClean()
	{
		clearTables();
	}

	public void clearTables()
	{
		clearTable( fg_r_look );
		clearTable( fg_g_look );
		clearTable( fg_b_look );
		clearTable( bg_r_look );
		clearTable( bg_g_look );
		clearTable( bg_b_look );
	}

	private void clearTable( int[] table )
	{
		for( int i = 0; i < 256; i++ ) table[ i ] = 128;
	}

	public void setColorPoints( Vector<GiottoRGBInt> bgColors, Vector<GiottoRGBInt> fgColors )
	{
		this.fgColors = fgColors;
		this.bgColors = bgColors;
		clearTables();
		createLookUps();
	}

	public void filter( BufferedImage dest )
	{
		int a,r,g,b;
		int[] dPix = getBank( dest );

		for( int i = 0; i < dPix.length; i++ )
		{
			a = ( dPix[ i ] >> alpha ) & 0xff;
			r = ( dPix[ i ] >> red ) & 0xff;
			g = ( dPix[ i ] >> green ) & 0xff;
			b = dPix[ i ] & 0xff;
			
			if( fg_r_look[ r ] == 255 && fg_g_look[ g ] == 255 && fg_b_look[ b ] == 255 )
				a = 255;


			if( bg_r_look[ r ] == 0 && bg_g_look[ g ] == 0 && bg_b_look[ b ] == 0 )
				a = 0;

			dPix[ i ] = ( a << alpha ) | (r << red ) | g << green | b;
		}
	}

	//--- These are for speed-up hack
	public void createLookUps()
	{
		for( int i = 0; i < fgColors.size(); i++ )
		{
			GiottoRGBInt c = fgColors.elementAt( i );
			fg_r_look[ c.r ] = 255;
			fg_r_look[ clamp255(c.r - 1) ] = 255;
			fg_r_look[ clamp255(c.r + 1) ] = 255;
			fg_r_look[ clamp255(c.r - 2) ] = 255;
			fg_r_look[ clamp255(c.r + 2) ] = 255;
			fg_b_look[ clamp255(c.b - 3) ] = 255;
			fg_b_look[ clamp255(c.b + 3) ] = 255;
			fg_g_look[ c.g ] = 255;
			fg_g_look[ clamp255(c.g - 1) ] = 255;
			fg_g_look[ clamp255(c.g + 1) ] = 255;
			fg_g_look[ clamp255(c.g - 2) ] = 255;
			fg_g_look[ clamp255(c.g + 2) ] = 255;
			fg_b_look[ clamp255(c.b - 3) ] = 255;
			fg_b_look[ clamp255(c.b + 3) ] = 255;
			fg_b_look[ c.b ] = 255;
			fg_b_look[ clamp255(c.b - 1) ] = 255;
			fg_b_look[ clamp255(c.b + 1) ] = 255;
			fg_b_look[ clamp255(c.b - 2) ] = 255;
			fg_b_look[ clamp255(c.b + 2) ] = 255;
			fg_b_look[ clamp255(c.b - 3) ] = 255;
			fg_b_look[ clamp255(c.b + 3) ] = 255;
		}

		for( int i = 0; i <bgColors.size(); i++ )
		{
			GiottoRGBInt c = bgColors.elementAt( i );
			bg_r_look[ c.r ] = 0;
			bg_r_look[ clamp255(c.r - 1) ] = 0;
			bg_r_look[ clamp255(c.r + 1) ] = 0;
			bg_r_look[ clamp255(c.r - 2) ] = 0;
			bg_r_look[ clamp255(c.r + 2) ] = 0;
			bg_r_look[ clamp255(c.b - 3) ] = 0;
			bg_r_look[ clamp255(c.b + 3) ] = 0;
			bg_r_look[ c.g ] = 0;
			bg_r_look[ clamp255(c.g - 1) ] = 0;
			bg_r_look[ clamp255(c.g + 1) ] = 0;
			bg_r_look[ clamp255(c.g - 2) ] = 0;
			bg_r_look[ clamp255(c.g + 2) ] = 0;
			bg_r_look[ clamp255(c.b - 3) ] = 0;
			bg_r_look[ clamp255(c.b + 3) ] = 0;
			bg_r_look[ c.b ] = 0;
			bg_r_look[ clamp255(c.b - 1) ] = 0;
			bg_r_look[ clamp255(c.b + 1) ] = 0;
			bg_r_look[ clamp255(c.b - 2) ] = 0;
			bg_r_look[ clamp255(c.b + 2) ] = 0;
			bg_r_look[ clamp255(c.b - 3) ] = 0;
			bg_r_look[ clamp255(c.b + 3) ] = 0;
		}
	}

}//end class
