package animator.phantom.renderer.plugin;

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

import giotto2D.filters.artistic.Oilify;

import java.awt.image.BufferedImage;

import animator.phantom.paramedit.FloatNumberEditor;
import animator.phantom.plugin.PhantomPlugin;
import animator.phantom.plugin.PluginUtils;
import animator.phantom.renderer.param.FloatParam;

public class OilifyPlugin extends PhantomPlugin
{
	private FloatParam maskSize;

	public OilifyPlugin()
	{
		initPlugin( FILTER );
	}

	public void buildDataModel()
	{
		setName( "Oilify" );

		maskSize = new FloatParam( 7.0f );

		registerParameter( maskSize );
	}

	public void buildEditPanel()
	{
		FloatNumberEditor maskSizeEdit = new FloatNumberEditor( "Size", maskSize );

		addEditor( maskSizeEdit );
	}

	public void doImageRendering( int frame )
	{
		BufferedImage flowImg = getFlowImage();
		BufferedImage filteredImage = PluginUtils.createScreenCanvas();

		Oilify of = new Oilify();
		of.setMaskSize( maskSize.get() );
		of.filter( flowImg, filteredImage );

		sendFilteredImage( filteredImage, frame );
	}

}//end class
