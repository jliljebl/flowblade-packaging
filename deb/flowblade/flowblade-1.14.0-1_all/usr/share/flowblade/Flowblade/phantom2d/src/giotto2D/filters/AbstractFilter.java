package giotto2D.filters;

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

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.awt.image.WritableRaster;

//--- Base class for pixelmanipulator classes.
public abstract class AbstractFilter
{
	//--- Channels.
	public static final int RED = 0;
	public static final int GREEN = 1;
	public static final int BLUE = 2;
	public static final int ALPHA = 3;
	//--- For bitwise ops. blue is 0 and does not need to be bit shifted.
	public static final int alpha = 24;
	public static final int red = 16;
	public static final int green = 8;
	public static final int blue = 0;
	public static final int alpha_mask = 0x00ffffff;
	//public static final int red_only_mask = 0x00ff0000;
	//public static final int green_only_mask = 0x0000ff00;
	//public static final int blue_only_mask = 0x000000ff;
	//--- Tonerange enums
	public static final int SHADOWS = 0;
	public static final int MIDTONES = 1;
	public static final int HIGHLIGHTS = 2;
	//--- Visual intensities of colors.
	//public static final float RGB_INTENSITY_RED = 0.30f;
	//public static final float RGB_INTENSITY_GREEN = 0.59f;
	//public static final float RGB_INTENSITY_BLUE = 0.11f;

	//--- Constant for converting 
	public static float DEGREES_TO_RADIANS = (float) Math.PI / 180;

	public static final int MAX_RGB = 3 * 255;

	//--- Retunrs dimension that is intersection of images when placed top left matched.
	protected static Dimension getIntersectionDimension( BufferedImage img1, BufferedImage img2 )
	{
		int width, height;

		if( img1.getWidth() < img2.getWidth() ) width = img1.getWidth();
		else width = img2.getWidth();

		if( img1.getHeight() < img2.getHeight() ) height = img1.getHeight();
		else height = img2.getHeight();

		return new Dimension( width, height );
	}

	public static int[] getBank( BufferedImage img )
	{
		WritableRaster imgRaster = img.getRaster();
		DataBufferInt dbuf = (DataBufferInt) imgRaster.getDataBuffer();
		return dbuf.getData( 0 );
	}

	protected int clamp255( int v )
	{
		if( v < 0 ) return 0;
		if( v > 255 ) return 255;
		return v;
	}

	protected double clamp01( double v )
	{
		if( v < 0.0 ) return 0.0;
		if( v > 1.0 ) return 1.0;
		return v;
	}

	protected void printTable( String label, int[] table )
	{
		for( int i = 0; i < table.length; i++ )
		{
			System.out.println( label + i + ":" + table[i] );
		}
	} 

}//end class
