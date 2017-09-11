package giotto2D.filters.color;

import giotto2D.filters.AbstractFilter;

import java.awt.image.BufferedImage;

public class RedChannelGammaCurve extends AbstractFilter
{
	private int look[] = new int[256];

	public RedChannelGammaCurve()
	{
		clearTable();
	}

	public void setTable( int[] table )
	{
		look = table;
	}

	public void filter( BufferedImage dest )
	{
		int a,r;
		int lumaValue;
		int[] dPix = getBank( dest );

		for( int i = 0; i < dPix.length; i++ )
		{
			a = ( dPix[ i ] >> alpha ) & 0xff;
			r = ( dPix[ i ] >> red ) & 0xff;

			lumaValue = (( r + r + r ) * 255 ) / MAX_RGB;

			dPix[ i ] = ( a << alpha ) | ( look[ lumaValue ] << red ) | look[ lumaValue ] << green | look[ lumaValue ];
		}
	}

	private void clearTable()
	{
		for( int i = 0; i < 256; i++ ) look[ i ] = 255;
	}

}