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
import java.awt.Point;
import java.awt.image.BufferedImage;

import animator.phantom.paramedit.AnimValueNumberEditor;
import animator.phantom.paramedit.IntegerComboBox;
import animator.phantom.paramedit.IntegerValueSliderEditor;
import animator.phantom.plugin.PhantomPlugin;
import animator.phantom.plugin.PluginUtils;
import animator.phantom.renderer.param.AnimatedValue;
import animator.phantom.renderer.param.IntegerParam;

import com.jhlabs.image.GradientFilter;
import com.jhlabs.image.HalftoneFilter;

public class HalfTonePlugin extends PhantomPlugin
{
	private static final int LINEAR = 0;
	private static final int RADIAL = 1;

	private IntegerParam lineWidth;
	private AnimatedValue bwScreenAngle;
	private IntegerParam softness;
	private IntegerParam pattern;
	private AnimatedValue centerX;
	private AnimatedValue centerY;

	public HalfTonePlugin()
	{
		initPlugin( FILTER );
	}

	public void buildDataModel()
	{
		setName( "HalfTone" );

		lineWidth = new IntegerParam( 5, 3, 20 );
		bwScreenAngle = new AnimatedValue( 90 );
		softness = new IntegerParam( 1, 0, 10 );
		pattern = new IntegerParam( LINEAR );
		centerX = new AnimatedValue( 0 );
		centerY = new AnimatedValue( 0 );

		registerParameter( lineWidth );
		registerParameter( bwScreenAngle );
		registerParameter( softness );
		registerParameter( pattern );
		registerParameter( centerX );
		registerParameter( centerY );
	}

	public void buildEditPanel()
	{
		IntegerValueSliderEditor lEdit = new  IntegerValueSliderEditor( "Line width", lineWidth );
		IntegerValueSliderEditor sEdit = new  IntegerValueSliderEditor( "Softness", softness );
		AnimValueNumberEditor bwEdit = new AnimValueNumberEditor( "Angle", bwScreenAngle );
		String[] options = {"Line", "Circle", "Square" };
		IntegerComboBox pEdit = new IntegerComboBox( pattern, "Pattern", options );
		AnimValueNumberEditor centerXEdit = new AnimValueNumberEditor( "Center X", centerX );
		AnimValueNumberEditor centerYEdit = new AnimValueNumberEditor( "Center Y", centerY );

		addEditor( lEdit );
		addRowSeparator();
		addEditor( bwEdit );
		addRowSeparator();
		addEditor( sEdit );
		addRowSeparator();
		addEditor( pEdit );
		addRowSeparator();
		addEditor( centerXEdit );
		addRowSeparator();
		addEditor( centerYEdit );
	}

	public void doImageRendering( int frame )
	{
		BufferedImage img = getFlowImage();
		BufferedImage mask = PluginUtils.createCanvas( img.getWidth(), img.getHeight() );

		GradientFilter gf = getGradientFilter( frame );
		gf.filter( mask, mask );

		HalftoneFilter f = new HalftoneFilter();
		f.setMonochrome( true );
		f.setMask( mask );
		f.setSoftness( (float) softness.get() / 10.0f );

		applyFilter( f );
	}

	private GradientFilter getGradientFilter( int frame )
	{
		int lw = (int) lineWidth.get();
		int x = (int) centerX.get( frame );
		int y = (int) centerY.get( frame );
		Point p1 = new Point( x, y );
		Point p2 = new Point( x + lw, y );
		GradientFilter gf = null;
		if( pattern.get() == LINEAR )
		{
			gf = new GradientFilter( new Point( 0, 0 ),  new Point( lw, 0 ), Color.black.getRGB(), 
						Color.white.getRGB(), true, GradientFilter.LINEAR, GradientFilter.INT_LINEAR );
			gf.setAngleWithWidth( (float) Math.toRadians( (double) bwScreenAngle.get( frame ) ), (float) lw );
			return gf;
		}
		else if( pattern.get() == RADIAL )
		{
			gf = new GradientFilter( p1, p2, Color.black.getRGB(), 
						Color.white.getRGB(), true, GradientFilter.RADIAL, GradientFilter.INT_LINEAR );
			return gf;
		}
		else
		{
			gf = new GradientFilter( p1, p2, Color.black.getRGB(), 
						Color.white.getRGB(), true, GradientFilter.SQUARE, GradientFilter.INT_LINEAR );
			return gf;
		}
	}

}//end class
