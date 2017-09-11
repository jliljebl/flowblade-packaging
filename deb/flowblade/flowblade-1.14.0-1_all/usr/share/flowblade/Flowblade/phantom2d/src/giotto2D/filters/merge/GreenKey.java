package giotto2D.filters.merge;

import giotto2D.filters.AbstractFilter;

import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;

public class GreenKey extends AbstractFilter
{

	public static void filter( 	BufferedImage img,
					float k0,
					float k1,
					float k2 )
	{
		int[] pixel = new int[ 4 ];	
		WritableRaster raster = img.getRaster();

		int a, i, j, width, height;
		width = img.getWidth();
		height = img.getHeight();

		for( i = 0; i < width; i++ )
		{
			for( j = 0; j < height; j++ )
			{
				//--- Read pixel
				raster.getPixel( i, j, pixel );

				//k0*b - k1 * g + k2	
				a = (int)(255.0f * ((( pixel[ BLUE ] / 255.0f ) * k0 ) - (( pixel[ GREEN ] / 255.0f ) * k1 ) + k2));
				if( a < 0 )
					a = 0;
				else if( a > 255 -1 )
					a = 255;

				//--- Write alpha
				pixel[ ALPHA ] = a;
				raster.setPixel( i, j, pixel );
			}
		}
	}

}//end class