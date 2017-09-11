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

//--dis probaply aint working correctly
public class ScatterRGB extends AbstractFilter
{
	private boolean independent = true;
	private boolean correlated  = false;
	private boolean animated  = true;
	private double[]  noise = { 0.20, 0.20, 0.20, 0.0 };     /*  channel  */
	private long seed = 12345678;// random number generator.
	private Random random;

	public ScatterRGB(){}

	public void setCorrelated( boolean correlated ){ this.correlated = correlated; }
	public void setIndependent( boolean independent ){ this.independent = independent; }
	public void setAnimated( boolean animated ){ this.animated = animated; }
	public void setNoise( double r, double g, double b )
	{
		noise[ 0 ] = r;
		noise[ 1 ] = g;
		noise[ 2 ] = b;
	}

	public BufferedImage filter( BufferedImage sourceImg, BufferedImage destinationImg )
	{
		WritableRaster destination = destinationImg.getRaster();
		WritableRaster source = sourceImg.getRaster();

  		int bpp = source.getNumBands();

		int x1 = 0;
		int y1 = 0;
		int x2 = source.getWidth();
		int y2 = source.getHeight();

		int width  = x2 - x1;
		int height = y2 - y1;

		int[] src = new int[ bpp * width * height ];
		int[] dst = new int[ bpp * width * height ];

  		source.getPixels( x1, y1, width, height, src);
  		destination.getPixels( x1, y1, width, height, dst);

		random = new Random( seed );// random number generator.

		int byteNoise = 0;
		int sample;
		int p;

		//--- iterate pixels.
		for (int i = 0; i < width * height * bpp - bpp; i++)
		{
			for (int b = 0; b < bpp; b++)//b == channel
			{
				sample = i + b;
				if (b == 0 || independent || (b == 1 && bpp == 2) || (b == 3 && bpp == 4))
					byteNoise = (int) ( noise[b] * gauss () * 127.0);
				
				if ( noise[b] > 0.0)
				{
					if ( correlated )
					{
						p = (int) (src[sample] + (src[ sample ] * (byteNoise / 127.0)));
					}
					else
					{
						p = src[ sample ] + byteNoise;
					}
					dst[sample] = GiottoMath.CLAMP( p, 0, 255 );
				}
				else
				{
					dst[sample] = src[sample];
				}
			}
		}
  		destination.setPixels( x1, y1, width, height, dst);
  		if( animated == true )
  			seed = random.nextLong();
		return destinationImg;
	}

	private double gauss()
	{
  		double u, v, x;

  		do
    		{
      			v = random.nextDouble();

      			do u = random.nextDouble();
      			while (u == 0);

      			/* Const 1.715... = sqrt(8/e) */
      			x = 1.71552776992141359295 * (v - 0.5) / u;
    		}
  		while ( x * x > -4.0 * Math.log(u) );

  		return x;
	}

}//end class