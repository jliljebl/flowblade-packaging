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
import java.awt.Point;
import java.awt.image.BufferedImage;

import animator.phantom.paramedit.AnimColorRGBEditor;
import animator.phantom.paramedit.AnimValueNumberEditor;
import animator.phantom.paramedit.CheckBoxEditor;
import animator.phantom.paramedit.IntegerComboBox;
import animator.phantom.plugin.AbstractPluginEditLayer;
import animator.phantom.plugin.PhantomPlugin;
import animator.phantom.plugin.PluginUtils;
import animator.phantom.renderer.param.AnimatedValue;
import animator.phantom.renderer.param.BooleanParam;
import animator.phantom.renderer.param.IntegerParam;
import animator.phantom.renderer.plugin.editlayer.GradientPluginEditLayer;

import com.jhlabs.image.GradientFilter;

public class PatternGradientPlugin extends PhantomPlugin
{
	//--- Gradient colors
	private AnimatedValue red1;
	private AnimatedValue green1;
	private AnimatedValue blue1;

	private AnimatedValue red2;
	private AnimatedValue green2;
	private AnimatedValue blue2;
	//--- x, and y of gradient start point.
	public AnimatedValue x1;
	public AnimatedValue y1;
	//--- x, and y of gradient end point.
	public AnimatedValue x2;
	public AnimatedValue y2;

 	public BooleanParam cyclic;
	public IntegerParam pattern;

	public PatternGradientPlugin()
	{
		initPlugin( STATIC_SOURCE );
		makeAvailableInFilterStack( this );
	}

	public void buildDataModel()
	{
		setName( "PatternGradient" );

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

		//--- NOTE: THESE ARE INITLIZED HERE BECAUSE PARAMETER DEFAULT
		//--- VALUES DEPEND ON SCREEN SIZE WHITCH IS NOT KNOW UNTIL
		//--- INSTANTIATION TIME
		x1 = new  AnimatedValue( PluginUtils.getScreenSize().width / 2 );
		y1 = new  AnimatedValue( 0 );
		x2 = new  AnimatedValue( PluginUtils.getScreenSize().width / 2 );
		y2 = new  AnimatedValue( PluginUtils.getScreenSize().height );

		cyclic = new BooleanParam( false );
		pattern = new IntegerParam( 0 );
	
		registerParameter( red1 );
		registerParameter( green1 );
		registerParameter( blue1 );
		registerParameter( red2 );
		registerParameter( green2 );
		registerParameter( blue2 );
		registerParameter( x1 );
		registerParameter( y1 );
		registerParameter( x2 );
		registerParameter( y2 );
		registerParameter( cyclic );
		registerParameter( pattern );
	}

	public void buildEditPanel()
	{
		AnimColorRGBEditor colorEditor1 = new AnimColorRGBEditor( "Color 1" ,  red1, green1, blue1 );
		AnimColorRGBEditor colorEditor2  = new AnimColorRGBEditor( "Color 2", red2, green2, blue2 );

		AnimValueNumberEditor xEdit1 = new AnimValueNumberEditor( "P1 X", x1 );
		AnimValueNumberEditor yEdit1 = new AnimValueNumberEditor( "P1 Y", y1 );
		AnimValueNumberEditor xEdit2 = new AnimValueNumberEditor( "P2 X", x2 );
		AnimValueNumberEditor yEdit2 = new AnimValueNumberEditor( "P2 Y", y2 );

		CheckBoxEditor cyclicBox = new CheckBoxEditor( cyclic, "Cyclic", true );
		String[] pOptions = {"Radial", "Conical", "Biconical","Square" };
		IntegerComboBox patternE = new IntegerComboBox( pattern, "Pattern", pOptions );

		addEditor( patternE );
		addRowSeparator();
		addEditor( colorEditor1 );
		addRowSeparator();
		addEditor( colorEditor2 );
		addRowSeparator();
		addEditor( xEdit1 );
		addRowSeparator();
		addEditor( yEdit1 );
		addRowSeparator();
		addEditor( xEdit2 );
		addRowSeparator();
		addEditor( yEdit2 );
		addRowSeparator();
		addEditor( cyclicBox );
	}

	public void doImageRendering( int frame )
	{
		//--- Create image
		BufferedImage img = PluginUtils.createFilterStackableCanvas( this );
		GradientFilter gf = getGradientFilter( frame );
		gf.filter( img, img );

		sendStaticSource( img, frame );
	}

	private GradientFilter getGradientFilter( int frame )
	{
		Color color1 = new Color((int)red1.get(frame), (int)green1.get(frame), (int)blue1.get(frame) );
		Color color2 = new Color((int)red2.get(frame), (int)green2.get(frame), (int)blue2.get(frame) );
		
		Point p1 = new Point( (int) x1.getValue( frame ),   (int) y1.getValue( frame ) );
		Point p2 = new Point( (int) x2.getValue( frame ),   (int) y2.getValue( frame ) );
		GradientFilter gf = new GradientFilter( p1, p2, color1.getRGB(), 
						color2.getRGB(), cyclic.get(), pattern.get() + 2, GradientFilter.INT_LINEAR );// pattern.get() + 2 makes combobox selections correspond with GradientFilter types
		return gf;
	}

	public AbstractPluginEditLayer getPluginEditLayer()
	{
		return new GradientPluginEditLayer( this );
	}

}//end class