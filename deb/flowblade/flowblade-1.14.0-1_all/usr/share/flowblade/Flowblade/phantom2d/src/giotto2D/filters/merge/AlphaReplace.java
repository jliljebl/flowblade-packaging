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

import java.awt.Dimension;
import java.awt.image.BufferedImage;

//--- Replaces alpha channel of destination with alpha( or other) channel of source. 
public class AlphaReplace extends AbstractFilter
{
	public static void filter( BufferedImage dest, BufferedImage source )
	{
		filter( dest, source, ALPHA );
	}

	public static void filter( BufferedImage dest, BufferedImage source, int channel )
	{
		if( dest.getWidth() != source.getWidth() || dest.getHeight() != source.getHeight())
		{
			filterIntersection( dest, source );
			return;
		}

		int[] sPix = getBank( source );
		int[] dPix = getBank( dest );

		int a;
		for( int i = 0; i < sPix.length; i++ )
		{
			a = ( sPix[ i ] >> alpha ) & 0xff;
			dPix[ i ] = ( a << alpha ) | ( dPix[ i ] & alpha_mask );
		}

	}//end filter

	public static void filterIntersection( BufferedImage dest, BufferedImage source )
	{
		Dimension d = getIntersectionDimension( dest, source );

		int[] sPix = getBank( source );
		int[] dPix = getBank( dest );

		int indexD;
		int indexS;
		int dWidth = dest.getWidth();
		int sWidth = source.getWidth();
		int a;

		for( int i = 0; i < d.width; i++ )
		{
			for( int j = 0; j < d.height; j++)
			{
				indexD = j * dWidth + i;
				indexS = j * sWidth + i;
				a = sPix[ indexS ] >> alpha & 0xff;
				dPix[ indexD ] =  a << alpha  | ( dPix[ indexD ] & alpha_mask );
			}
		}

	}//end filter

}//end class