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

import giotto2D.filters.render.Checkerboard;

import java.awt.Color;
import java.awt.image.BufferedImage;

import animator.phantom.paramedit.AnimColorRGBEditor;
import animator.phantom.paramedit.CheckBoxEditor;
import animator.phantom.paramedit.IntegerNumberEditor;
import animator.phantom.plugin.PhantomPlugin;
import animator.phantom.plugin.PluginUtils;
import animator.phantom.renderer.param.AnimatedValue;
import animator.phantom.renderer.param.BooleanParam;
import animator.phantom.renderer.param.IntegerParam;

public class CheckerboardPlugin extends PhantomPlugin
{
	private AnimatedValue red1;
	private AnimatedValue green1;
	private AnimatedValue blue1;

	private AnimatedValue red2;
	private AnimatedValue green2;
	private AnimatedValue blue2;

 	private IntegerParam size;
	private BooleanParam isVariable;

	public CheckerboardPlugin()
	{
		initPlugin( STATIC_SOURCE );
		makeAvailableInFilterStack( this );
	}

	public void buildDataModel()
	{
		setName( "CheckerBoard" );

		red1 = new AnimatedValue( 255.0f, 0.0f, 255.0f );
		green1 = new AnimatedValue( 255.0f, 0.0f, 255.0f );
		blue1 = new AnimatedValue( 255.0f, 0.0f, 255.0f );

		red2 = new AnimatedValue( 0.0f, 0.0f, 255.0f );
		green2 = new AnimatedValue( 0.0f, 0.0f, 255.0f );
		blue2 = new AnimatedValue( 0.0f, 0.0f, 255.0f );

		red1.setParamName( "Red 1" );
		green1.setParamName( "Green 1" );
		blue1.setParamName( "Blue 1" );
		
		red2.setParamName( "Red 2" );
		green2.setParamName( "Green 2" );
		blue2.setParamName( "Blue 2" );
		
		size = new IntegerParam( 20 );
		isVariable = new BooleanParam( false );
	
		registerParameter( red1 );
		registerParameter( green1 );
		registerParameter( blue1 );
		registerParameter( red2 );
		registerParameter( green2 );
		registerParameter( blue2 );

		registerParameter( size );
		registerParameter( isVariable );
	}

	public void buildEditPanel()
	{
		AnimColorRGBEditor colorEditor1 = new AnimColorRGBEditor( "Color 1" ,  red1, green1, blue1 );
		AnimColorRGBEditor colorEditor2  = new AnimColorRGBEditor(  "Color 2", red2, green2, blue2 );

		IntegerNumberEditor sizeEdit = new IntegerNumberEditor( "Size", size );
		CheckBoxEditor variableBox = new CheckBoxEditor( isVariable, "Variable pattern", true );

		addEditor( colorEditor1 );
		addRowSeparator();
		addEditor( colorEditor2 );
		addRowSeparator();
		addEditor( sizeEdit );
		addRowSeparator();
		addEditor( variableBox );
	}

	public void doImageRendering( int frame )
	{
		BufferedImage img = PluginUtils.createFilterStackableCanvas( this );
	
		Color color1 = new Color((int)red1.get(frame), (int)green1.get(frame), (int)blue1.get(frame) );
		Color color2 = new Color((int)red2.get(frame), (int)green2.get(frame), (int)blue2.get(frame) );
		
		Checkerboard cb = new Checkerboard();
		int mode = 0;
		if( isVariable.get() )
			mode = 1;
		cb.setFilterValues( mode, size.get(), color1, color2 );
		cb.filter( img );

		sendStaticSource( img, frame );
	}

}//end class
