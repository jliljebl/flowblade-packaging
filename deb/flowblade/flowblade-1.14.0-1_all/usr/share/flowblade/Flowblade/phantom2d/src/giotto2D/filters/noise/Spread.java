package giotto2D.filters.noise;

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

import giotto2D.core.GiottoMath;
import giotto2D.filters.AbstractFilter;

import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.util.Random;

//---- from gimp
public class Spread extends AbstractFilter
{
	//--- User settable params.
	private double spread_amount_x = 5.0;
	private double spread_amount_y = 5.0;

	public Spread(){}

	public void setAmountX( int amount ){ spread_amount_x = (double)amount; }
	public void setAmountY( int amount ){ spread_amount_y = (double)amount; }

	public BufferedImage filter( BufferedImage sourceImg, BufferedImage destinationImg )
	{
		WritableRaster destination = destinationImg.getRaster();
		WritableRaster source = sourceImg.getRaster();

		int x1 = 0;
		int y1 = 0;
		int x2 = source.getWidth();
		int y2 = source.getHeight();

		Random random = new Random( 123456789 );

		int x_amount = (int)(( spread_amount_x + 1 ) / 2.0);
		int y_amount = (int)(( spread_amount_y + 1 ) / 2.0);
		int width  = x2 - x1;
		int height = y2 - y1;

		int[] sPixel = new int[4];
		double angle;
		int xdist, ydist;
		int xi, yi;

		for( int x = 0; x < width; x++)
		{
			for( int y = 0; y < height; y++ )
			{
				/* get random angle, x distance, and y distance */
				xdist = ( x_amount > 0
					? GiottoMath.g_rand_int_range( -x_amount, x_amount, random )
					: 0);
				ydist = ( y_amount > 0
					? GiottoMath.g_rand_int_range( -y_amount, y_amount, random )
					: 0);
				angle = GiottoMath.g_rand_double_range( -Math.PI, Math.PI, random );
				
				xi = x + (int) Math.floor( Math.sin(angle) * xdist);
				yi = y + (int) Math.floor( Math.cos(angle) * ydist);

				/* Only displace the pixel if it's within the bounds of the image. */
				if (xi >= 0 && xi < width && yi >= 0 && yi < height)
					source.getPixel( xi, yi, sPixel );
				else /* Else just copy it */
					source.getPixel( x, y, sPixel );
				
				destination.setPixel( x, y, sPixel );
			}
		}
		return destinationImg;
	}

}//end class