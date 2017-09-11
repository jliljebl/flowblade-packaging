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

import animator.phantom.paramedit.AnimColorRGBEditor;
import animator.phantom.plugin.PluginUtils;
import animator.phantom.renderer.param.AnimatedValue;

public class ShapeGridPlugin extends GridPlugin
{
	private AnimatedValue red1;
	private AnimatedValue green1;
	private AnimatedValue blue1;

	public ShapeGridPlugin()
	{
		initPlugin( FULL_SCREEN_MOVING_SOURCE );
	}

	public void buildDataModel()
	{
 		setName( "ShapeGrid" );
		registerGridParams();
		red1 = new AnimatedValue( 255.0f, 0.0f, 255.0f );
		green1 = new AnimatedValue( 255.0f, 0.0f, 255.0f );
		blue1 = new AnimatedValue( 255.0f, 0.0f, 255.0f );
		red1.setParamName( "Fill Red" );
		green1.setParamName( "Fill Green" );
		blue1.setParamName( "Fill Blue" );
		registerParameter( red1 );
		registerParameter( green1 );
		registerParameter( blue1 );
	}

	public void buildEditPanel()
	{
		addGridEditors( true );
		addRowSeparator();
		AnimColorRGBEditor colorEditor1 = new AnimColorRGBEditor( "Shape Color", red1, green1, blue1 );
		addEditor( colorEditor1 );
	}

	public void renderFullScreenMovingSource( float frameTime, Graphics2D g, int width, int height )
	{
		BufferedImage source = getFlowImage();
		if( shapeType.get() == 0 && source == null )
			source = PluginUtils.createScreenCanvas();
		Color color1 = new Color((int)red1.get(frameTime), (int)green1.get(frameTime), (int)blue1.get(frameTime) );
		drawGrid( g, width, height, source, color1, frameTime, true );
	}

}//end class
