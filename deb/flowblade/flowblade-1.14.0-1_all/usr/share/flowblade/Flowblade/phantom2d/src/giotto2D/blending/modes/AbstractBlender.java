package giotto2D.blending.modes;

/*
    Copyright Janne Liljeblad 2006,2007,2008

    This file is part of Phantom2D.

    Phantom2D is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    Phantom2D is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with Phantom2D.  If not, see <http://www.gnu.org/licenses/>.
*/

import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;

public abstract class AbstractBlender
{
	//--- Channels.
	public static final int RED = 0;
	public static final int GREEN = 1;
	public static final int BLUE = 2;
	public static final int ALPHA = 3;

	//--- For bitwise ops.
	//public static final int alpha = 24;
	//public static final int red = 16;
	//public static final int green = 8;
	//public static final int alpha_mask = 0x00ffffff;

	//--- This method is called by ImageBlender
	public void blendImages(	BufferedImage destination,
					BufferedImage source,
					BufferedImage blendMask,
					int intOpacity,
					Rectangle blendArea )
	{
		
		doBlend(	destination.getRaster(),
				source.getRaster(),
				blendMask.getRaster(),
				intOpacity,
				blendArea
				);
	}

	//--- Combines alpha channels if we are doing OVER rule combination
	public void doAlphaCombine( 	boolean isOverRuleComposite,
					boolean isMotionBlur,
					BufferedImage destination,
					BufferedImage source,
					BufferedImage blendMask,
					int intOpacity,
					Rectangle blendArea )
	{
		//--- Fo ATOP this is noop
		if( !isOverRuleComposite ) return;
		
		if( isMotionBlur )
			doOverAlphaMB(	destination.getRaster(),
					source.getRaster(),
					blendMask.getRaster(),
					intOpacity,
					blendArea
					);
		else
			doOverAlpha(	destination.getRaster(),
					source.getRaster(),
					blendMask.getRaster(),
					intOpacity,
					blendArea
					);
	}

	//--- This method is extended by classes for rendering different blend types.
	public abstract void doBlend(	WritableRaster destination,
					WritableRaster source,
					WritableRaster blendMask,
					int intOpacity,
					Rectangle blendArea );

	private void doOverAlpha(	WritableRaster destination,
					WritableRaster source,
					WritableRaster blendMask,
					int intOpacity,
					Rectangle blendArea )
	{
		//--- Get loop bounds.
		int xStart = blendArea.x;
		int xEnd = blendArea.x + blendArea.width;
		int yStart = blendArea.y;
		int yEnd = blendArea.y + blendArea.height;

		//--- Create pixel references.
		int[] dPixel = new int[ 4 ];
		int[] sPixel = new int[ 4 ];
		int[] bmPixel = new int[ 4 ];
		int aS;
		int i, j;

		for( i = xStart; i < xEnd; i++ )
		{
			for( j = yStart; j < yEnd; j++ )
			{
				//--- Check if blend done for this pixel.
				blendMask.getPixel( i, j, bmPixel );
				if( bmPixel[ RED ] == 0 ) continue;

				//--- Get source and dest pixels.
				destination.getPixel( i, j, dPixel );
				source.getPixel( i, j, sPixel );

				aS = sPixel[ ALPHA ] * intOpacity / 255;
				dPixel[ ALPHA ] =  aS + ( dPixel[ ALPHA ] * ( 255 - aS)) / 255;
				destination.setPixel( i, j, dPixel );
			}
		}
	}

	public void doOverAlphaMB(	WritableRaster destination,
					WritableRaster source,
					WritableRaster blendMask,
					int intOpacity,
					Rectangle blendArea )
	{
		//--- Get loop bounds.
		int xStart = blendArea.x;
		int xEnd = blendArea.x + blendArea.width;
		int yStart = blendArea.y;
		int yEnd = blendArea.y + blendArea.height;

		//--- Create pixel references.
		int[] dPixel = new int[ 4 ];
		int[] bmPixel = new int[ 4 ];
		int aS;
		int i, j;

		//--- Blend loop.
		for( i = xStart; i < xEnd; i++ )
		{
			for( j = yStart; j < yEnd; j++ )
			{
				//--- Check if blend done for this pixel.
				blendMask.getPixel( i, j, bmPixel );
				if( bmPixel[ RED ] == 0 ) continue;

				destination.getPixel( i, j, dPixel );

				aS = bmPixel[ RED ]  * intOpacity / 255;
				dPixel[ ALPHA ] =  aS + ( dPixel[ ALPHA ] * ( 255 - aS)) / 255;
				destination.setPixel(i, j, dPixel );
			}
		}
	}

}//end class