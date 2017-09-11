package giotto2D.filters.merge;

import giotto2D.filters.AbstractFilter;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;

public class AlphaUnion extends AbstractFilter
{
	public static void filter( BufferedImage dest, BufferedImage source )
	{
		WritableRaster destRaster = dest.getRaster();
		WritableRaster sourceRaster = source.getRaster();

		Dimension d = getIntersectionDimension( dest, source );

		int[] dPixel = new int[ 4 ];
		int[] sPixel = new int[ 4 ];
		
		int i, j;

		for( i = 0; i < d.width; i++ )
		{
			for( j = 0; j < d.height; j++ )
			{
				sourceRaster.getPixel( i, j, sPixel );
				destRaster.getPixel( i, j, dPixel );
				dPixel[ ALPHA ] = sPixel[ ALPHA ] | dPixel[ ALPHA ];
				destRaster.setPixel( i, j, dPixel );
			}
		}

	}

}//end class