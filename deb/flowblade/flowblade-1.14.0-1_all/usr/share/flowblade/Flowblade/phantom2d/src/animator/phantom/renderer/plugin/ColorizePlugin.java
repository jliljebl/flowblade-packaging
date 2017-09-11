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

import giotto2D.filters.color.Colorize;

import java.awt.image.BufferedImage;

import animator.phantom.paramedit.AnimValueSliderEditor;
import animator.phantom.plugin.PhantomPlugin;
import animator.phantom.renderer.param.AnimatedValue;

public class ColorizePlugin extends PhantomPlugin
{
	private AnimatedValue hue;
	private AnimatedValue saturation;
	private AnimatedValue lightness;

	public ColorizePlugin()
	{
		initPlugin( FILTER );
	}

	public void buildDataModel()
	{
		setName( "Colorize" );

		hue = new AnimatedValue( 180.0f, 0.0f, 360.0f );
		saturation = new AnimatedValue( 50.0f, 0.0f, 100.0f );
		lightness = new AnimatedValue( 0.0f, -100.0f, 100.0f );

		registerParameter( hue );
		registerParameter( saturation );
		registerParameter( lightness );
	}

	public void buildEditPanel()
	{
		AnimValueSliderEditor hueEdit = new AnimValueSliderEditor( "Hue", hue );
		AnimValueSliderEditor satEdit = new AnimValueSliderEditor( "Saturation", saturation );
		AnimValueSliderEditor lightEdit = new AnimValueSliderEditor( "Lightness", lightness );

		addEditor( hueEdit );
		addRowSeparator();
		addEditor( satEdit );
		addRowSeparator();
		addEditor( lightEdit );
	}

	public void doImageRendering( int frame )
	{
		BufferedImage flowImg = getFlowImage();

		Colorize z = new Colorize();
		z.setHSL( (double) hue.getValue(frame), (double) saturation.getValue(frame), (double) lightness.getValue(frame) );
		z.filter( flowImg );

		sendFilteredImage( flowImg, frame );
	}

}//end class
