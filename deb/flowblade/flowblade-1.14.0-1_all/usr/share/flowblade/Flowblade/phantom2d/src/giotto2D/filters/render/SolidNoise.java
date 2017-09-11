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

import giotto2D.core.GiottoMath;
import giotto2D.core.GiottoVector2;
import giotto2D.filters.AbstractFilter;

import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.util.Random;

//--- Draws a b/w noise pattern image.
//--- gimp file: /plug-ins/common/plasma.c
public class SolidNoise extends AbstractFilter
{
	private int TABLE_SIZE = 64;
	private double MIN_SIZE = 0.1;
	private double MAX_SIZE = 16.0;

	private boolean tilable = false;
	private boolean turbulent = false;
	private int seed = 0;
	private int detail = 1;
	private double uxsize = 8.0;
	private double uysize = 8.0;

	private int xclip, yclip;
	private double offset, factor;
	private double xsize, ysize;
	private int[] perm_tab = new int[TABLE_SIZE];
	private GiottoVector2[] grad_tab = new GiottoVector2[TABLE_SIZE];

	private Random random;

	public SolidNoise(){}

	public void setDetail( int d ){ detail = GiottoMath.CLAMP( d, 1,15 ); }
	public void setXSize( double s ){ uxsize = GiottoMath.CLAMP( s, 0.1, 16.0 ); }
	public void setYSize( double s ){ uysize = GiottoMath.CLAMP( s, 0.1, 16.0 ); }
	public void setTurbulence( boolean b ){ turbulent = b; }
	public void setTilable( boolean b ){ tilable = b; }
	public void setSeed( int s ){ seed = s; }

	public BufferedImage filter( BufferedImage destinationImg )
	{
		WritableRaster destination = destinationImg.getRaster();

  		int bytes = destination.getNumBands();

		int x1 = 0;
		int y1 = 0;
		int x2 = destination.getWidth();
		int y2 = destination.getHeight();

		int width  = x2 - x1;
		int height = y2 - y1;

		/*  Initialization  */
		solidNoiseInit ();

		boolean has_alpha = ( bytes == 4 );

		//--- Alpha ignored
  		if (has_alpha) bytes--;

		solidNoiseDraw( destination, (double)width, (double) height, x1, y1, bytes, has_alpha);
		
		return destinationImg;
        }

	
	private void solidNoiseDraw( 	WritableRaster dest_rgn,
					double       width,
					double       height,
					int          xoffset,
					int          yoffset,
					int          chns,
					boolean      has_alpha)
	{
		int   row, col, i;
		int   val;
		int[] dPixel;
		
		if( has_alpha ) dPixel = new int[ 4 ];
		else dPixel = new int[ 3 ];
		
		for (row = yoffset; row < (yoffset + height); row++)
		{
			for (col = xoffset; col < (xoffset + width); col++)
			{
				val = (int) Math.floor( 255.0 * noise ((col - xoffset) / width,
								(row - yoffset) / height));
			
				for (i = 0; i < chns; i++) dPixel[ i ] = val;
			
				if (has_alpha) dPixel[ 3 ] = 255;

				dest_rgn.setPixel( col, row, dPixel );
			}
		}
	}

	private void solidNoiseInit()
	{
		int     i, j, k, t;
		double  m;
	
		random = new Random( seed );
	
		/*  Force sane parameters  */
		detail = GiottoMath.CLAMP ( detail, 0, 15);
		uxsize = GiottoMath.CLAMP ( uxsize, MIN_SIZE, MAX_SIZE);
		uysize = GiottoMath.CLAMP ( uysize, MIN_SIZE, MAX_SIZE);
	
		/*  Set scaling factors  */
		if ( tilable)
		{
			xsize = Math.ceil( uxsize );
			ysize = Math.ceil( uysize );
			xclip = (int) xsize;
			yclip = (int) ysize;
		}
		else
		{
			xsize = uxsize;
			ysize = uysize;
		}
	
		/*  Set totally empiric normalization values  */
		if (turbulent)
		{
			offset = 0.0;
			factor = 1.0;
		}
		else
		{
			offset = 0.94;
			factor = 0.526;
		}
	
		/*  Initialize the permutation table  */
		for (i = 0; i < TABLE_SIZE; i++) perm_tab[i] = i;
	
		for (i = 0; i < (TABLE_SIZE >> 1); i++)
		{
			j = GiottoMath.g_rand_int_range( 0, TABLE_SIZE, random );
			k = GiottoMath.g_rand_int_range( 0, TABLE_SIZE, random );
			t = perm_tab[j];
			perm_tab[j] = perm_tab[k];
			perm_tab[k] = t;
		}
	
		/*  Initialize the gradient table  */
		for (i = 0; i < TABLE_SIZE; i++)
		{
			grad_tab[i] = new GiottoVector2();
			do
			{
				grad_tab[i].x =  GiottoMath.g_rand_double_range( -1, 1, random );
				grad_tab[i].y =  GiottoMath.g_rand_double_range( -1, 1, random );
				m = grad_tab[i].x * grad_tab[i].x + grad_tab[i].y * grad_tab[i].y;
			}
			while( m == 0.0 || m > 1.0);
			
			m = 1.0 / Math.sqrt(m);
			grad_tab[i].x *= m;
			grad_tab[i].y *= m;
		}
	

	}

	public double plainNoise (double x, double y, int s)
	{
		GiottoVector2 v = new GiottoVector2();
		int        a, b, i, j, n;
		double     sum;
		
		sum = 0.0;
		x *= s;
		y *= s;
		a = (int) Math.floor(x);
		b = (int) Math.floor(y);
		
		for (i = 0; i < 2; i++)
			for (j = 0; j < 2; j++)
			{
				if(tilable)
					n = perm_tab[(((a + i) % (xclip * s)) + perm_tab[((b + j) % (yclip * s)) % TABLE_SIZE]) % TABLE_SIZE];
				else
					n = perm_tab[(a + i + perm_tab[(b + j) % TABLE_SIZE]) % TABLE_SIZE];

				v.x = x - a - i;
				v.y = y - b - j;
				sum += WEIGHT(v.x) * WEIGHT(v.y) * (grad_tab[n].x * v.x + grad_tab[n].y * v.y);
			}
		
		return sum / s;
	}


	public double noise( double x, double y)
	{
		int i;
		int s;
		double sum;
		
		s = 1;
		sum = 0.0;
		x *= xsize;
		y *= ysize;
		
		for (i = 0; i <= detail; i++)
		{
			if (turbulent)
				sum += Math.abs( plainNoise (x, y, s) );
			else
				sum += plainNoise( x, y, s);
			s <<= 1;
		}
		
		return (sum+offset)*factor;
	}


	public double WEIGHT( double T )
	{
		return ((2.0*Math.abs(T)-3.0)*(T)*(T)+1.0);
	}

}//end class 