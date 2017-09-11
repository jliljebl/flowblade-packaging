package giotto2D.filters.merge;

import giotto2D.filters.AbstractFilter;

import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;

public class ValueShift extends AbstractFilter
{

	public static BufferedImage createShiftedImage( 	BufferedImage source,
								float opacity, //normalized
								float softness ) // slope width in pixels
	{
		BufferedImage dest = new BufferedImage( source.getWidth(), source.getHeight(), BufferedImage.TYPE_INT_ARGB ); 

		//--- Create look up table
		int[] valueLook = new int[256];

		int white = (int)( 255.0f + softness - ( 255.0f + softness ) * opacity );
		int black = (int)( 255.0f - (255.0f + softness) * opacity );
		for( int j = 0; j < 256; j++ )
		{
			if( j < black )
				valueLook[ j ]  = 0;
			else if( j > white )
				valueLook[ j ]  = 255;
			else
				valueLook[ j ] = (int) (( (float)( j - black ) / softness ) * 255.0f);
		}

		WritableRaster destRaster = dest.getRaster();
		WritableRaster sourceRaster = source.getRaster();

		int lumaValue;
		int outLuma;
		int i, j;
		int[] dPixel = new int[ 4 ];
		int[] sPixel = new int[ 4 ];
		int w = source.getWidth();
		int h = source.getHeight();
		for( i = 0; i < w; i++ )
		{
			for( j = 0; j < h; j++ )
			{
				sourceRaster.getPixel( i, j, sPixel );
				destRaster.getPixel( i, j, dPixel );

				lumaValue = (( sPixel[ RED ] + sPixel[ BLUE ] + sPixel[ GREEN ] ) * 255 ) / MAX_RGB;
				outLuma = valueLook[ lumaValue ];

				dPixel[ RED ] = outLuma;
				dPixel[ GREEN ] = outLuma;
				dPixel[ BLUE ] = outLuma;
				dPixel[ ALPHA ] = 255;
				destRaster.setPixel( i, j, dPixel );
			}
		}

		return dest;
	}

}//end class
