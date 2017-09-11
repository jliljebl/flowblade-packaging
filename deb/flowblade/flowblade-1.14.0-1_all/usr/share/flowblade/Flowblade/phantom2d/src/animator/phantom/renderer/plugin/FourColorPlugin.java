package animator.phantom.renderer.plugin;

/*
    Copyright Janne Liljeblad.

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
import java.awt.image.BufferedImage;

import animator.phantom.paramedit.AnimColorRGBEditor;
import animator.phantom.plugin.PhantomPlugin;
import animator.phantom.plugin.PluginUtils;
import animator.phantom.renderer.param.AnimatedValue;

import com.jhlabs.image.FourColorFilter;

public class FourColorPlugin extends PhantomPlugin
{
	private AnimatedValue red1;
	private AnimatedValue green1;
	private AnimatedValue blue1;

	private AnimatedValue red2;
	private AnimatedValue green2;
	private AnimatedValue blue2;
	
	private AnimatedValue red3;
	private AnimatedValue green3;
	private AnimatedValue blue3;

	private AnimatedValue red4;
	private AnimatedValue green4;
	private AnimatedValue blue4;
	
	public FourColorPlugin()
	{
		initPlugin( STATIC_SOURCE );
		makeAvailableInFilterStack( this );
	}

	public void buildDataModel()
	{
		setName( "FourColor" );

		red1 = new AnimatedValue( 255.0f, 0.0f, 255.0f );
		green1 = new AnimatedValue( 100.0f, 0.0f, 255.0f );
		blue1 = new AnimatedValue( 100.0f, 0.0f, 255.0f );

		red2 = new AnimatedValue( 100.0f, 0.0f, 255.0f );
		green2 = new AnimatedValue( 255.0f, 0.0f, 255.0f );
		blue2 = new AnimatedValue( 100.0f, 0.0f, 255.0f );
		
		red3 = new AnimatedValue( 100.0f, 0.0f, 255.0f );
		green3 = new AnimatedValue( 100.0f, 0.0f, 255.0f );
		blue3 = new AnimatedValue( 255.0f, 0.0f, 255.0f );
		
		red4 = new AnimatedValue( 255.0f, 0.0f, 255.0f );
		green4 = new AnimatedValue( 100.0f, 0.0f, 255.0f );
		blue4 = new AnimatedValue( 255.0f, 0.0f, 255.0f );

		red1.setParamName( "Red 1" );
		green1.setParamName( "Green 1" );
		blue1.setParamName( "Blue 1" );
		
		red2.setParamName( "Red 2" );
		green2.setParamName( "Green 2" );
		blue2.setParamName( "Blue 2" );
		
		red3.setParamName( "Red " );
		green3.setParamName( "Green 3" );
		blue3.setParamName( "Blue 3" );
		
		red4.setParamName( "Red 4" );
		green4.setParamName( "Green 4" );
		blue4.setParamName( "Blue 4" );
		
		registerParameter( red1 );
		registerParameter( green1 );
		registerParameter( blue1 );
		registerParameter( red2 );
		registerParameter( green2 );
		registerParameter( blue2 );
		registerParameter( red3 );
		registerParameter( green3 );
		registerParameter( blue3 );
		registerParameter( red4 );
		registerParameter( green4 );
		registerParameter( blue4 );
	}

	public void buildEditPanel()
	{
		AnimColorRGBEditor colorEditor1 = new AnimColorRGBEditor( "Top left", red1, green1, blue1 );
		AnimColorRGBEditor colorEditor2 = new AnimColorRGBEditor( "Top right", red2, green2, blue2 );
		AnimColorRGBEditor colorEditor3 = new AnimColorRGBEditor( "Bottom left", red3, green3, blue3 );
		AnimColorRGBEditor colorEditor4 = new AnimColorRGBEditor( "Bottom right", red4, green4, blue4 );
		
		addEditor( colorEditor1 );
		addRowSeparator();
		addEditor( colorEditor2 );
		addRowSeparator();
		addEditor( colorEditor3 );
		addRowSeparator();
		addEditor( colorEditor4 );
	 }
	
	public void doImageRendering( int frame )
	{
		BufferedImage img = PluginUtils.createFilterStackableCanvas( this );

		Color color1 = new Color((int)red1.get(frame), (int)green1.get(frame), (int)blue1.get(frame) );
		Color color2 = new Color((int)red2.get(frame), (int)green2.get(frame), (int)blue2.get(frame) );
		Color color3 = new Color((int)red3.get(frame), (int)green3.get(frame), (int)blue3.get(frame) );
		Color color4 = new Color((int)red4.get(frame), (int)green4.get(frame), (int)blue4.get(frame) );
		
		FourColorFilter filter = new FourColorFilter();
		filter.setColorNW( color1.getRGB() );
		filter.setColorNE( color2.getRGB() );
		filter.setColorSW( color3.getRGB() );
		filter.setColorSE( color4.getRGB() );
		filter.setDimensions( img.getWidth(), img.getHeight() );

		PluginUtils.filterImage( img, filter );

		sendStaticSource( img, frame );
	}

}//end class