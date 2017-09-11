package giotto2D.filters.artistic;

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

import giotto2D.core.GiottoMath;
import giotto2D.filters.AbstractFilter;

import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.util.Arrays;

public class Oilify extends AbstractFilter
{
	/*Params*/
	private static final int HISTSIZE    = 256;

	/*User settable value*/
  	private double  mask_size = 7.0;

	public Oilify(){}

	public void setMaskSize( float mSize ){ mask_size = (double) mSize; }

	/*
	* For each RGB channel, replace the pixel at (x,y) with the
	* value that occurs most often in the n x n chunk centered
	* at (x,y).
	*/
	public BufferedImage filter( BufferedImage sourceImg, BufferedImage destinationImg )
	{
		int          x, y, c, xx, yy, n;
		int          x3, y3, x4, y4;
		int []       Cnt = new int[4];
		int [][]     Hist = new int[3][HISTSIZE];

		WritableRaster destination = destinationImg.getRaster();
		WritableRaster source = sourceImg.getRaster();

		int width  = source.getWidth();
		int height = source.getHeight();
		n = (int) mask_size / 2;

		int[] dPixel = new int[ 4 ];
		dPixel[ ALPHA ] = 255;
		dPixel[ RED ] = 255;
		int[] sPixel = new int[ 4 ];

		for (y = 0; y < height; y++)
		{

			for (x = 0; x < width; x++)
			{
				x3 = GiottoMath.CLAMP((x - n), 0, width );
				y3 = GiottoMath.CLAMP((y - n), 0, height );
				x4 = GiottoMath.CLAMP((x + n + 1), 0, width );
				y4 = GiottoMath.CLAMP((y + n + 1), 0, height );
			
				Arrays.fill(Cnt, 0);
				Arrays.fill(Hist[0], 0 );
				Arrays.fill(Hist[1], 0 );
				Arrays.fill(Hist[2], 0 );

				for (yy = y3 ; yy < y4 ; yy++)
				{
					for (xx = x3 ; xx < x4 ; xx++)
					{
						source.getPixel( xx, yy, sPixel );
						c = ++Hist[ RED ][ sPixel[ RED ] ];
						if( c > Cnt[ RED ] )
						{
							dPixel[ RED ] = sPixel[ RED ];
							Cnt[ RED ] = c;
						}
						c = ++Hist[ GREEN ][ sPixel[ GREEN ] ];
						if( c > Cnt[ GREEN ] )
						{
							dPixel[ GREEN ] = sPixel[ GREEN ];
							Cnt[ GREEN ] = c;
						}
						c = ++Hist[ BLUE ][ sPixel[ BLUE ] ];
						if( c > Cnt[ BLUE ] )
						{
							dPixel[ BLUE ] = sPixel[ BLUE ];
							Cnt[ BLUE ] = c;
						}
					}
				}
				destination.setPixel( x, y, dPixel );
			}
		}
	
		return destinationImg;
	}

}// end class

