package giotto2D.filters.merge;

import giotto2D.filters.AbstractFilter;

import java.awt.image.BufferedImage;

public class AlphaPaint extends AbstractFilter
{
	public static void filter( BufferedImage dest, int alphaValue )
	{
		int[] dPix = getBank( dest );

		for( int i = 0; i < dPix.length; i++ )
		{
			dPix[ i ] = ( alphaValue << alpha ) | ( dPix[ i ] & alpha_mask );
		}
	}

}//end class
