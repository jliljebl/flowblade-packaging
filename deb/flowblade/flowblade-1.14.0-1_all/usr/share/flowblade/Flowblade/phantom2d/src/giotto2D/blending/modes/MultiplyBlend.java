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
import java.awt.image.WritableRaster;

//--- This class does darken blend between two images 
public class MultiplyBlend extends AbstractBlender
{
	
	public void doBlend(	WritableRaster destination,
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

		int rVal, gVal, bVal;
		int aS, aD;
		int i, j;

		//--- Blend loop.
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
				
				//--- Do blend
				///a * b, in normalized space
				rVal = sPixel[ RED ] * dPixel[ RED ] / 255;

				gVal = sPixel[ GREEN ] * dPixel[ GREEN ] / 255;

				bVal = sPixel[ BLUE ] * dPixel[ BLUE ] / 255;

				aS = sPixel[ ALPHA ] * intOpacity / 255;
				aD = 255 - aS;
		
                		dPixel[ RED ] = rVal * aS / 255 + dPixel[ RED ] * aD / 255;
                		dPixel[ GREEN ] = gVal * aS / 255 + dPixel[ GREEN ] * aD / 255;
                		dPixel[ BLUE ] = bVal * aS / 255 + dPixel[ BLUE ] * aD / 255;
				
				destination.setPixel(i, j, dPixel );
			}
		}
		
	}

}//end class