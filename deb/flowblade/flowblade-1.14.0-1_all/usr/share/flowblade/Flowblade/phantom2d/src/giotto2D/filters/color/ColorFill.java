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
import java.awt.image.DataBufferInt;
import java.awt.image.WritableRaster;

public class ColorFill extends AbstractFilter
{
	public static void whiteFill( BufferedImage img )
	{
		WritableRaster imgRaster = img.getRaster();
		DataBufferInt dbuf = (DataBufferInt) imgRaster.getDataBuffer();
		int[] bank = dbuf.getData( 0 );
		for( int i = 0; i < bank.length; i++ )
		{
			bank[ i ] = -1;
		}
	}//end 

}//end class