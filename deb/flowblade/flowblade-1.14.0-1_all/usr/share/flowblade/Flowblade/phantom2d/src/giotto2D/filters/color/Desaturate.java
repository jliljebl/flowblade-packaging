package giotto2D.filters.color;

/*
    Copyright Janne Liljeblad

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
import java.awt.image.WritableRaster;

public class Desaturate extends AbstractFilter
{
	public static void filter( BufferedImage img )
	{
		WritableRaster imgRaster = img.getRaster();

		int[] pixel = new int[ 4 ];
		int valueDivider = 3 * 255;
		int value;
		int i, j;

		for( i = 0; i < img.getWidth(); i++ )
		{
			for( j = 0; j < img.getHeight(); j++ )
			{
				imgRaster.getPixel( i, j, pixel );
				value = (( pixel[ RED ] + pixel[ BLUE ] + pixel[ GREEN ] ) * 255 ) / valueDivider;
				pixel[ RED ] = value;
				pixel[ GREEN ] = value;
				pixel[ BLUE ] = value;
				imgRaster.setPixel(i, j, pixel );
			}
		}
	}

}//end class