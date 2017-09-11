package giotto2D.filters.render;

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

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;

//--- Draws a checkerboar pattern, either regular or psychobilly variation.
//--- gimp file: /plug-ins/common/checkerboard.c
public class Checkerboard extends AbstractFilter
{
	private static final int MOD_PSYCHO = 1;

	private int mode = 1;//--- mode == 1 is psychobilly, any other 
	private int size = 25;//--- grid size
	private Color fg = new Color( 255, 255, 255, 255 );
	private Color bg = new Color( 0, 0, 0, 255 );
	
	private int[] in = null;

	public Checkerboard(){}

	//--- mode == 1 is psychobilly, any other value is normal grid.
	//--- size = grid size
	//--- fg,bg are foreground and background colors.
	public void setFilterValues( int mode_, int size_, Color fg_, Color bg_ )
	{
		mode = mode_;
		size = size_;
		fg = fg_;
		bg = bg_;
	}
	
	public BufferedImage filter( BufferedImage drawable )
	{
		/* make size 1 to prevent division by zero */
 		if ( size < 1) 
			size = 1;

		WritableRaster raster = drawable.getRaster();
		int width = raster.getWidth();
		int height = raster.getHeight();
	
		int val, xp, yp;
		int[] dPixel = new int[ 4 ];
		dPixel[ ALPHA ] = 255;

		for( int x = 0; x < width; x++ )
		{
			for( int y = 0; y < height; y++ )
			{

				if (mode == MOD_PSYCHO )
				{
					/* Psychobilly Mode */
					if( inblock (x, size) != inblock (y, size)) val = 1;
					else val = 0;
				}
				else
				{
					/* Normal, regular checkerboard mode.
					* Determine base factor (even or odd) of block
					* this x/y position is in.
					*/
					xp = x / size;
					yp = y / size;
					
					/* if both even or odd, color sqr */
					if( (xp & 1) != (yp & 1) ) val = 1;
					else val = 0;
				}

				if( val == 0 )
				{
					dPixel[ RED ] = fg.getRed();
					dPixel[ GREEN ] = fg.getGreen();
					dPixel[ BLUE ] = fg.getBlue();

				}
				else
				{
					dPixel[ RED ] = bg.getRed();
					dPixel[ GREEN ] = bg.getGreen();
					dPixel[ BLUE ] = bg.getBlue();
				}
				
				raster.setPixel(x, y, dPixel );
			}
		}

		return drawable;
	}

	private int inblock ( int pos, int size)
	{

		int len;
		
		// avoid a FP exception (????)
		if (size == 1)
		size = 2;
		
		len = size*size;

		// Initialize the array; since we'll be called thousands of
		// times with the same size value, precompute the array.
		if (in == null )
		{
			int cell = 1;// cell value 
			int i, j, k;
			
			in = new int[ len ];
			
			
			// i is absolute index into in[]
			// j is current number of blocks to fill in with a 1 or 0.
			// k is just counter for the j cells.
			
			i=0;
			for (j=1; j<=size; j++ )
			{ 
				// first half 
				for (k=0; k<j; k++ )
				{
					in[i++] = cell;
				}
				cell = 1 - cell;
			}

			for ( j=size-1; j>=1; j--)
			{ 
				// second half 
				for (k=0; k<j; k++ )
				{
					in[i++] = cell;
				}
				cell = 1 - cell;
			}
		}
		
		// place pos within 0..(len-1) grid and return the value. 
		return in[ pos % (len-1) ];
	}

}//end class