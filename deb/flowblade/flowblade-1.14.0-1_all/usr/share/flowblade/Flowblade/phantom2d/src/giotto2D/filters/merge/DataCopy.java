package giotto2D.filters.merge;

import giotto2D.filters.AbstractFilter;

import java.awt.image.BufferedImage;


public class DataCopy extends AbstractFilter
{
	public static void copy( BufferedImage src, BufferedImage dest )
	{
		int[] dPix = getBank( dest );
		int[] sPix = getBank( src );

		int a, r, g, b;

		for( int i = 0; i < dPix.length; i++ )
		{
			a = ( sPix[ i ] >> alpha ) & 0xff;
			r = ( sPix[ i ] >> red ) & 0xff;
			g = ( sPix[ i ] >> green ) & 0xff;
			b = sPix[ i ] & 0xff;
		
			dPix[ i ] = a << alpha | r << red | g << green | b;
		}
	}

}//end class