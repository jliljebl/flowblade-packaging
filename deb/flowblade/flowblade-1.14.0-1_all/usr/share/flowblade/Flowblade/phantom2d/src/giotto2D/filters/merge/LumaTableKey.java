package giotto2D.filters.merge;

import giotto2D.filters.AbstractFilter;

import java.awt.image.BufferedImage;

public class LumaTableKey extends AbstractFilter
{
	private int look[] = new int[256];

	public LumaTableKey()
	{
		clearTable();
	}

	public void setTable( int[] table )
	{
		look = table;
	}

	public void filter( BufferedImage dest )
	{
		int r,g,b;
		int lumaValue;
		int[] dPix = getBank( dest );

		for( int i = 0; i < dPix.length; i++ )
		{
			r = ( dPix[ i ] >> red ) & 0xff;
			g = ( dPix[ i ] >> green ) & 0xff;
			b = dPix[ i ] & 0xff;

			lumaValue = (( r + g + b ) * 255 ) / MAX_RGB;

			dPix[ i ] = ( look[lumaValue] << alpha ) | ( r << red ) |  g << green | b ;
		}
	}

	private void clearTable()
	{
		for( int i = 0; i < 256; i++ ) look[ i ] = 255;
	}

}