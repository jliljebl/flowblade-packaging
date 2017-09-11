package giotto2D.filters.merge;

import giotto2D.filters.AbstractFilter;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;

//--- Creates union of alpha channels on destination image.
public class AlphaIntersection extends AbstractFilter
{
	public static void filter( BufferedImage dest, BufferedImage source )
	{
		System.out.println("alpha intersection");
		WritableRaster destRaster = dest.getRaster();
		WritableRaster sourceRaster = source.getRaster();
		//--- Get intersecting dimensions.
		Dimension d = getIntersectionDimension( dest, source );

		//--- Create pixel objects.
		int[] dPixel = new int[ 4 ];
		int[] sPixel = new int[ 4 ];
		
		int i, j;

		//--- Draw loops.
		for( i = 0; i < d.width; i++ )
		{
			for( j = 0; j < d.height; j++ )
			{
				//Read pixels
				sourceRaster.getPixel( i, j, sPixel );
				destRaster.getPixel( i, j, dPixel );
				dPixel[ ALPHA ] = sPixel[ ALPHA ] & dPixel[ ALPHA ];
				destRaster.setPixel( i, j, dPixel );
			}
		}

	}//end filter

	public static void filterToImage( BufferedImage dest, BufferedImage source )
	{
		System.out.println("alpha intersection f img");
		WritableRaster destRaster = dest.getRaster();
		WritableRaster sourceRaster = source.getRaster();
		//--- Get intersecting dimensions.
		Dimension d = getIntersectionDimension( dest, source );

		//--- Create pixel objects.
		int[] dPixel = new int[ 4 ];
		int[] sPixel = new int[ 4 ];
		
		int i, j, val;

		//--- Draw loops.
		for( i = 0; i < d.width; i++ )
		{
			for( j = 0; j < d.height; j++ )
			{
				//Read pixels
				sourceRaster.getPixel( i, j, sPixel );
				destRaster.getPixel( i, j, dPixel );
				val = sPixel[ ALPHA ] & dPixel[ RED ];//assumes gray scale img
				dPixel[ RED ] = val;
				dPixel[ GREEN ] = val;
				dPixel[ BLUE ] = val;
				destRaster.setPixel( i, j, dPixel );
			}
		}

	}//end fil

}//end class