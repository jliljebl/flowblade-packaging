package giotto2D.filters.merge;

import giotto2D.filters.AbstractFilter;
import giotto2D.filters.color.Desaturate;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;

//--- Copies specified channel ( or desaturates image first ) of source to destination alpha.
public class ImageToAlpha extends AbstractFilter
{
	public static void filterFull( BufferedImage dest, BufferedImage source )
	{
		int[] sPix = getBank( source );
		int[] dPix = getBank( dest );

		int r;

		for( int i = 0; i < sPix.length; i++ )
		{
			r = ( sPix[ i ] >> red ) & 0xff;
			dPix[ i ] = ( r << alpha ) | ( dPix[ i ] & alpha_mask );
		}

	}//end filter

	public static void filterFromRed( BufferedImage dest, BufferedImage source, Rectangle area )
	{
		int xStart = area.x;
		int xEnd = area.x + area.width;
		int yStart = area.y;
		int yEnd = area.y + area.height;
		int width = dest.getWidth();

		int[] sPix = getBank( source );
		int[] dPix = getBank( dest );
	
		int r, index;

		for( int i = xStart; i < xEnd; i++ )
		{
			for( int j = yStart; j < yEnd; j++ )
			{
				index = width * j + i;
				r = ( sPix[ index ] >> red ) & 0xff;
				dPix[ index ] = ( r << alpha ) | ( dPix[ index ] & alpha_mask );
			}
		}
	}

	public static void filter( BufferedImage dest, BufferedImage source )
	{
		filterChannel( dest, source, RED );
	}

	public static void filter( 	BufferedImage dest,
					BufferedImage source,
					int channel,
					boolean desaturateFirst )
	{
		if( desaturateFirst )
		{
			Desaturate.filter( source );
			filterChannel( dest, source, RED );
		}
		else
		{
			filterChannel( dest, source, channel );
		}
	}

	private static void filterChannel( BufferedImage dest, BufferedImage source, int channel )
	{
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
				dPixel[ ALPHA ] = sPixel[ channel ];
				destRaster.setPixel( i, j, dPixel );
			}
		}

	}//end filter

}//end class