package giotto2D.blending;

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

import giotto2D.blending.modes.AbstractBlender;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import javax.swing.SwingUtilities;

public class BigImageStaticBlender extends AbstractImageBlender
{
	//--- Used to create motion blurs.
	private BufferedImage blendMask;

	public BigImageStaticBlender( Dimension destinationSize )
	{
		//--- create help images
		blendMask = new BufferedImage( 	destinationSize.width,
						destinationSize.height,
						BufferedImage.TYPE_INT_ARGB );
	}

	//--- Blends images, with top left corners aligned.
	public void blendImages( 	BufferedImage destinationImage,
					BufferedImage sourceImage,
					float opacity, //0.0 - 1.0
					int mode )
	{
		//--- Calculate int opacity 0 - 255 from float opacity 0 - 1
		int intOpacity = Math.round( opacity * 255 );

		//--- Get are a in whtch blend is performed.
		Rectangle blendArea = new Rectangle( 0, 0, sourceImage.getWidth(), sourceImage.getHeight());
		blendArea = SwingUtilities.computeIntersection( 0,
								0,
								destinationImage.getWidth(),
								destinationImage.getHeight(),
								blendArea );

		//--- Paint blend mask white in blend area, black else where
		Graphics2D gm = blendMask.createGraphics();

		//--- Paint blend area white
		gm.setColor( Color.white );
		gm.fillRect( 0,0, blendArea.width + 2,  blendArea.height + 2 );// +2 because using method designed for different use, hack
		gm.dispose();

		//--- Get blender
		AbstractBlender blender = getBlender( mode );

		//--- Do blend.
		blender.blendImages(	destinationImage,
					sourceImage,
					blendMask,
					intOpacity,
					blendArea );
	}

}//end class