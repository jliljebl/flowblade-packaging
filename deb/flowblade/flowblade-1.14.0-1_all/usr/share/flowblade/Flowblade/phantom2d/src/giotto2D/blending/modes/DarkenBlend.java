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

public class DarkenBlend extends AbstractBlender
{
	
	public void doBlend(	WritableRaster destination,
				WritableRaster source,
				WritableRaster blendMask,
				int intOpacity,
				Rectangle blendArea )
	{
		int xStart = blendArea.x;
		int xEnd = blendArea.x + blendArea.width;
		int yStart = blendArea.y;
		int yEnd = blendArea.y + blendArea.height;
		
		//
		if( destination == null )System.out.println( "dest null" );

		//--- Create pixel objects outside loop.
		int[] dPixel = new int[ 4 ];
		int[] sPixel = new int[ 4 ];
		int[] bmPixel = new int[ 4 ];
		int i, j;
		int rVal, gVal, bVal;
		int aS, aD;

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
				
				//Do darken.
				if( dPixel[ RED ] > sPixel[ RED ] ) rVal = sPixel[ RED ];
				else rVal = dPixel[ RED ];

				if( dPixel[ GREEN ] > sPixel[ GREEN ] ) gVal = sPixel[ GREEN ];
				else gVal = dPixel[ GREEN ];

				if( dPixel[ BLUE ] > sPixel[ BLUE ] ) bVal = sPixel[ BLUE ];
				else bVal = dPixel[ BLUE ];

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