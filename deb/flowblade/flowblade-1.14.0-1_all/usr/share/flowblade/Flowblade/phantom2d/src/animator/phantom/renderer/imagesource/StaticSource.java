package animator.phantom.renderer.imagesource;

/*
    Copyright Janne Liljeblad 

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

import java.awt.image.BufferedImage;

import animator.phantom.blender.Blender;
import animator.phantom.plugin.PluginUtils;
import animator.phantom.renderer.ImageOperation;
import animator.phantom.renderer.param.AnimatedValue;

//--- Base class for unanimated sources
public abstract class StaticSource extends ImageOperation
{
	//--- holds rendered image to be used whenrendering filter stack.
	private BufferedImage filterStackImage;

	public void initBlendParams()
	{
		opacity = new AnimatedValue( this, 100.0f, 0.0f, 100.0f );
		registerParameter( opacity );
		registerParameter( inputMaskOp );
		setAsSource();
	}

	public BufferedImage consumeFilterStackImage()
	{
		BufferedImage retImg = filterStackImage;
		filterStackImage = null;
		return retImg;
	}


	public void processStaticSource( BufferedImage source, int frame )
	{
		if( isFilterStackIop == true )
		{
			filterStackImage = source;
			return;
		}

	
		if( renderedImage == null )
		{
			renderedImage = source;
			cropRenderedImageToScreenSize();
			return;
		}

		float opacityVal = opacity.getValue( frame );
		int blendMode = getBlendMode();
		BufferedImage mask = getInputMask();

		//--- ScreenSize is max size for renderedImage ( = destination )
		cropRenderedImageToScreenSize();

		//--- Non-null mask must be made same size as blend destination ( = renderedImage ) 
		if( mask != null )
		{
			if( mask.getWidth() != renderedImage.getWidth() 
				|| mask.getHeight() != renderedImage.getHeight() )
			{
				mask = PluginUtils.getAlphaCopy(renderedImage.getWidth(), renderedImage.getHeight(), mask );
			}
		}

		Blender.doAlignedBlend( renderedImage, source, opacityVal / 100.0f , blendMode, mask );
	}

}//end class