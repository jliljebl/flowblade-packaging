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

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import animator.phantom.paramedit.ParamColorSelect;
import animator.phantom.plugin.PhantomPlugin;
import animator.phantom.plugin.PluginUtils;
import animator.phantom.renderer.param.ColorParam;

public class ColorSolidPlugin extends PhantomPlugin
{
	private ColorParam bgColor = new ColorParam( new Color( 100,100,100 ) );;

	public ColorSolidPlugin()
	{
		initPlugin( STATIC_SOURCE );
	}

	public void buildDataModel()
	{
		setName( "ColorSolid" );
		
		registerParameter( bgColor );
	}

	public void buildEditPanel()
	{
		ParamColorSelect colorEditor = new ParamColorSelect( bgColor, "Select Color" );
		addEditor( colorEditor );
	 }
	
	public void doImageRendering( int frame )
	{
		Color drawC = bgColor.get();

		BufferedImage source = PluginUtils.createScreenCanvas();
		Graphics2D gc = source.createGraphics();
		gc.setColor( drawC );
		gc.fillRect( 0, 0, source.getWidth(), source.getHeight()  );
		gc.dispose();

		sendStaticSource( source, frame );
	}

}//end class