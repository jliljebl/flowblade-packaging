package giotto2D.filters.color;

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

import java.awt.image.BufferedImage;

//--- gimp file:/app/base/colorize.c
public class MaxRGB extends AbstractFilter
{
	private boolean holdMax = true;

	public MaxRGB()
	{
	}

	public void setHoldMax( boolean value )
	{
		holdMax = value;
	}

	public BufferedImage filter( BufferedImage dest )
	{
		if( holdMax )
			return holdMaxFilter( dest );
		else
			return holdMinFilter( dest );
	}

	private BufferedImage holdMaxFilter(  BufferedImage dest )
	{
		int[] d = getBank( dest );

		int da, r, g, b, max, ch;
	
		for( int i = 0; i < d.length; i++ )
		{
			r = ( d[ i ] >> red ) & 0xff;
			g = ( d[ i ] >> green ) & 0xff;
			b = d[ i ] & 0xff;
			da = ( d[ i ] >> alpha ) & 0xff;
			
			max = r;
			ch = red;
			if( g > max )
			{
				max = g;
				ch = green;
			}
			if( b > max )
			{
				ch = blue;
			}

			switch( ch )
			{
				case red:
					b = 0;
					g = 0;
					break;
				case green:
					r = 0;
					b = 0;
					break;
				case blue:
					r = 0;
					g = 0;
					break;
			}

			d[i] =  da << alpha | 
				r << red |
				g << green | 
				b;
		}
		
		return dest;
	}

	private BufferedImage holdMinFilter( BufferedImage dest )
	{
		int[] d = getBank( dest );

		int da, r, g, b, min, ch;
	
		for( int i = 0; i < d.length; i++ )
		{
			r = ( d[ i ] >> red ) & 0xff;
			g = ( d[ i ] >> green ) & 0xff;
			b = d[ i ] & 0xff;
			da = ( d[ i ] >> alpha ) & 0xff;
			
			min = r;
			ch = red;
			if( g < min )
			{
				min = g;
				ch = green;
			}
			if( b < min )
			{
				ch = blue;
			}

			switch( ch )
			{
				case red:
					b = 0;
					g = 0;
					break;
				case green:
					r = 0;
					b = 0;
					break;
				case blue:
					r = 0;
					g = 0;
					break;
			}

			d[i] =  da << alpha | 
				r << red |
				g << green | 
				b;
		}
		
		return dest;
	}


}//end class
