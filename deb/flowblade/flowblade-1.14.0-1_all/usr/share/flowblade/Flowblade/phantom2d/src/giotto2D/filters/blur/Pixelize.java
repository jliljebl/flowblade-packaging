package giotto2D.filters.blur;

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
    along with Phantom2D.  If not, see <http://www.gnu.org/licenses/>
*/

import giotto2D.filters.AbstractFilter;

import java.awt.Color;
import java.awt.image.BufferedImage;

public class Pixelize extends AbstractFilter
{
	private int xSize = 10;
	private int ySize = 10;

	private int imgWidth;
	private int imgHeight;

	public Pixelize(){}

	public void setXSize( int s )
	{
		if( xSize < 2 ) 
			xSize = 2;
		xSize = s; 
	}
	public void setYSize( int s )
	{
		if( ySize < 2 ) 
			ySize = 2;
		ySize = s; 
	}

	public BufferedImage filter( BufferedImage dest )
	{
		int[] pixels = getBank( dest );
		imgWidth = dest.getWidth();
		imgHeight = dest.getHeight();
		int xEnd = imgWidth / xSize + 1;
		int yEnd = imgHeight / ySize + 1;

		Color blockColor;
		for( int x = 0; x < xEnd; x++ )
		{
			for( int y = 0; y < yEnd; y++ )
			{
				blockColor = getBlockColor( pixels, x * xSize, y * ySize );
				writeBlockColor( pixels, x * xSize, y * ySize, blockColor );
			}
		}
		
		return dest;
	}

	private Color getBlockColor( int[] pixels, int x, int y )
	{
		//--- get x bounds
		int width = xSize;
		if( x + xSize >= imgWidth )
		{
			width = imgWidth - x;
		}
		int xEnd = x + width;
		
		//--- get y bounds
		int height = ySize;
		if( y + ySize >= imgHeight )
		{
			height = imgHeight - y;
		}
		int yEnd = y + height;

		int pix = 0;
		int r = 0;
		int g = 0;
		int b = 0;
		for( int xi = x; xi < xEnd; xi++ )
		{
			for( int yi = y; yi < yEnd; yi++ )
			{
				pix = pixels[ yi * imgWidth + xi ];
				r += ( pix >> red ) & 0xff;
				g += ( pix >> green ) & 0xff;
				b += pix & 0xff;
			}
		}
		
		int colorDiv = 255 * width * height;
		if( colorDiv == 0 )
			colorDiv = 1;
		return new Color( (r * 255) / colorDiv, (g *255) / colorDiv, (b * 255) / colorDiv );
	}

	private void writeBlockColor( int[] pixels, int x, int y, Color c )
	{
		//--- get x bounds
		int width = xSize;
		if( x + xSize >= imgWidth )
			width = imgWidth - x;
		int xEnd = x + width;
		
		//--- get y bounds
		int height = ySize;
		if( y + ySize >= imgHeight )
			height = imgHeight - y;
		int yEnd = y + height;

		int pix, a = 0;
		int r = c.getRed();
		int g = c.getGreen();
		int b = c.getBlue();
		for( int xi = x; xi < xEnd; xi++ )
		{
			for( int yi = y; yi < yEnd; yi++ )
			{
				pix = pixels[ yi * imgWidth + xi ];
				a = ( pix >> alpha ) & 0xff;
				pixels[ yi * imgWidth + xi ] = a << alpha |  r << red | g << green | b;
			}
		}
	}

}//end class
