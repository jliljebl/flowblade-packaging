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

import animator.phantom.paramedit.AnimValueSliderEditor;
import animator.phantom.paramedit.IntegerComboBox;
import animator.phantom.paramedit.IntegerNumberEditor;
import animator.phantom.plugin.PhantomPlugin;
import animator.phantom.renderer.param.AnimatedValue;
import animator.phantom.renderer.param.IntegerParam;

import com.jhlabs.image.SmearFilter;

public class SmearPlugin extends PhantomPlugin
{
	private AnimatedValue angle;
	private AnimatedValue density;
	private IntegerParam distance;
	private IntegerParam shape;
	private AnimatedValue mix;

	public SmearPlugin()
	{
		initPlugin( FILTER );
	}

	public void buildDataModel()
	{
		setName( "Smear" );

		angle = new AnimatedValue( 0, 0, 90 );
		density = new AnimatedValue( 50, 0, 100 );
		distance = new IntegerParam ( 8 );
		shape = new IntegerParam( SmearFilter.LINES );
		mix = new AnimatedValue( 50, 0 , 100 );

		registerParameter( angle );
		registerParameter( density );
		registerParameter( distance );
		registerParameter( shape );
		registerParameter( mix );
	}

	public void buildEditPanel()
	{
		AnimValueSliderEditor aEdit = new  AnimValueSliderEditor( "Angle" , angle );
		AnimValueSliderEditor dEdit = new  AnimValueSliderEditor( "Density", density );
		AnimValueSliderEditor mEdit = new  AnimValueSliderEditor( "Mix", mix );

		IntegerNumberEditor diEdit = new IntegerNumberEditor( "Distance", distance );
		String[] options = {"Crosses", "Lines", "Circles", "Squares" };
		IntegerComboBox shEdit = new IntegerComboBox( shape, "Shape", options );

		addEditor( aEdit );
		addRowSeparator();
		addEditor( dEdit );
		addRowSeparator();
		addEditor( mEdit );
		addRowSeparator();
		addEditor( shEdit );
		addRowSeparator();
		addEditor( diEdit );
	}

	public void doImageRendering( int frame )
	{
		SmearFilter f = new SmearFilter();
		f.setShape( shape.get() );
		f.setDistance( distance.get() );
		f.setDensity( density.get( frame ) / 100.0f );
		f.setAngle( angle.get( frame ) );
		f.setMix( mix.get( frame ) / 100.0f );

		applyFilter( f );
	}

}//end class
