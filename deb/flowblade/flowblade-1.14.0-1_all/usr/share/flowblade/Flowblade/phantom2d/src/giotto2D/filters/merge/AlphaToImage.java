package giotto2D.filters.merge;

import giotto2D.filters.AbstractFilter;

import java.awt.image.BufferedImage;

//--- Paints b/w image to destination using sources alpha.
public class AlphaToImage extends AbstractFilter
{
	//--- Will fail if source has some dimension bigger then destinion.
	public static void filter( BufferedImage source,  BufferedImage destination )
	{
		int a;

		int[] sPix = getBank( source );
		int[] dPix = getBank( destination );

		for( int i = 0; i < sPix.length; i++ )
		{
			a = ( sPix[ i ] >> 24 ) & 0xff;
			dPix[ i ] = 255 << 24 | a << 16 | a << 8 | a;
		}

	}

}//end class

