package giotto2D.filters.merge;

import giotto2D.filters.AbstractFilter;

import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;

//--- Divides color channels by alpha
public class DivideByAlpha extends AbstractFilter
{

	public static void filter( BufferedImage source )
	{
		WritableRaster sourceRaster = source.getRaster();

		int[] sPixel = new int[ 4 ];
		
		int width = source.getWidth();
		int height = source.getHeight();

		//--- Draw loops.
		int i, j;
		for( i = 0; i < width; i++ )
		{
			for( j = 0; j < height; j++ )
			{
				//Read pixels
				sourceRaster.getPixel( i, j, sPixel );
				if( sPixel[ ALPHA ] == 0 )
				{
					sPixel[ RED ] = 0;
					sPixel[ GREEN ]	= 0;
					sPixel[ BLUE ] = 0;
				}
				else
				{
					//---val = val / a
					sPixel[ RED ] = (sPixel[ RED ] * 255 * 255)  / (sPixel[ ALPHA ] * 255 );
					if( sPixel[ RED ] > 255 ) sPixel[ RED ] = 255;

					sPixel[GREEN] = (sPixel[GREEN] * 255 * 255)  / (sPixel[ ALPHA ] * 255);
					if( sPixel[ GREEN ] > 255 ) sPixel[ GREEN ] = 255;

					sPixel[ BLUE ] = (sPixel[ BLUE ] * 255 * 255)  / (sPixel[ ALPHA ] * 255);
					if( sPixel[ BLUE ] > 255 ) sPixel[ BLUE ] = 255;
				}
				sourceRaster.setPixel( i, j, sPixel );
			}
		}

	}//end filter

}//end class