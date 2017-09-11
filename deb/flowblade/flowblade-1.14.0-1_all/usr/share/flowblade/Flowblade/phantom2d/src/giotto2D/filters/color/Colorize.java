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
import giotto2D.libcolor.GiottoColorSpace;
import giotto2D.libcolor.GiottoHSL;
import giotto2D.libcolor.GiottoRGB;

import java.awt.image.BufferedImage;

//--- gimp file:/app/base/colorize.c
public class Colorize extends AbstractFilter
{
	private double hue = 180.0;
	private double saturation = 50.0;
	private double lightness = 0.0;

	private static int[] lum_red_lookup = new int[256];
	private static int[] lum_green_lookup = new int[256];
	private static int[] lum_blue_lookup = new int[256];

	private int[] final_red_lookup = new int[256];
	private int[] final_green_lookup = new int[256];
	private int[] final_blue_lookup = new int[256];

	static
	{
		for (int i = 0; i < 256; i ++)
		{
			lum_red_lookup[i]   = (int) ((double) i * GiottoColorSpace.RGB_INTENSITY_RED);
			lum_green_lookup[i] = (int) ((double) i * GiottoColorSpace.RGB_INTENSITY_GREEN);
			lum_blue_lookup[i]  = (int) ((double) i * GiottoColorSpace.RGB_INTENSITY_BLUE);
		}
	}

	public Colorize()
	{
		colorize_calculate();
	}

	//--- hue range 0 - 360
	//--- saturation 0 - 100, 50 normal
	//--- lightness -100 - 100, 0 normal
	public void setHSL( double h, double s, double l )
	{
		hue = h;
		saturation = s;
		lightness = l;
		colorize_calculate();
	}

	private void colorize_calculate()
	{
		GiottoHSL hsl = new GiottoHSL();
		GiottoRGB rgb = new GiottoRGB();
		
		hsl.h = hue / 360.0;
		hsl.s = saturation / 100.0;
		
		for ( int i = 0; i < 256; i ++)
		{
			hsl.l = (double) i / 255.0;
			
			GiottoColorSpace.hsl_to_rgb( hsl, rgb );
			
			final_red_lookup[i]   = (int)((double)i * rgb.r);
			final_green_lookup[i] = (int)((double)i * rgb.g);
			final_blue_lookup[i]  = (int)((double)i * rgb.b);
		}
	}

	public BufferedImage filter( BufferedImage dest )
	{
		int lum;
	
		int[] d = getBank( dest );

		int da;
	
		for( int i = 0; i < d.length; i++ )
		{
			lum = ( lum_red_lookup[ ( d[ i ] >> red ) & 0xff ] +
				lum_green_lookup[ ( d[ i ] >> green ) & 0xff ] +
				lum_blue_lookup[ d[ i ] & 0xff ] );
			da = ( d[ i ] >> alpha ) & 0xff;

			if ( lightness > 0)
			{
				lum = (int)((double) lum * (100.0 - lightness) / 100.0);
				lum += 255 - (100.0 - lightness) * 255.0 / 100.0;
			}
			else if ( lightness < 0)
			{
				lum = (int)((double) lum * ( lightness + 100.0) / 100.0);
			}

			d[i] =  da << alpha | 
				final_red_lookup[lum] << red |
				final_green_lookup[lum] << green | 
				final_blue_lookup[lum];
		}
		
		return dest;
	}

}//end class
