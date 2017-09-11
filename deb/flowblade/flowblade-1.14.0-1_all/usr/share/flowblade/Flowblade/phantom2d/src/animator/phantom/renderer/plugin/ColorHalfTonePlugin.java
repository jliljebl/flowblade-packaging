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

import animator.phantom.paramedit.AnimValueNumberEditor;
import animator.phantom.paramedit.IntegerNumberEditor;
import animator.phantom.plugin.PhantomPlugin;
import animator.phantom.renderer.param.AnimatedValue;
import animator.phantom.renderer.param.IntegerParam;

import com.jhlabs.image.ColorHalftoneFilter;

public class ColorHalfTonePlugin extends PhantomPlugin
{

	private IntegerParam dotRadius;
	private AnimatedValue cyanScreenAngle;
	private AnimatedValue magentaScreenAngle;
	private AnimatedValue yellowScreenAngle;

	public ColorHalfTonePlugin()
	{
		initPlugin( FILTER );
	}

	public void buildDataModel()
	{
		setName( "ColorHalfTone" );

		dotRadius = new IntegerParam( 2 );
		cyanScreenAngle = new AnimatedValue( 108 );
		magentaScreenAngle = new AnimatedValue( 162 );
		yellowScreenAngle = new AnimatedValue( 90 );

		registerParameter( dotRadius );
		registerParameter( cyanScreenAngle );
		registerParameter( magentaScreenAngle );
		registerParameter( yellowScreenAngle );
	}

	public void buildEditPanel()
	{
		AnimValueNumberEditor cEdit = new AnimValueNumberEditor( "Cyan angle", cyanScreenAngle );
		AnimValueNumberEditor mEdit = new AnimValueNumberEditor( "Magenta angle", magentaScreenAngle );
		AnimValueNumberEditor yEdit = new AnimValueNumberEditor( "Yellow angle", yellowScreenAngle );
		IntegerNumberEditor rEdit = new  IntegerNumberEditor( "Dot radius", dotRadius );

		addEditor(cEdit  );
		addRowSeparator();
		addEditor( mEdit );
		addRowSeparator();
		addEditor( yEdit );
		addRowSeparator();
		addEditor( rEdit );
	}

	public void doImageRendering( int frame )
	{
		ColorHalftoneFilter f = new ColorHalftoneFilter();
		f.setdotRadius( dotRadius.get() );
		f.setCyanScreenAngle( (float) Math.toRadians( (double) cyanScreenAngle.get( frame ) ) );
		f.setMagentaScreenAngle( (float) Math.toRadians( (double)  magentaScreenAngle.get( frame ) ) );
		f.setYellowScreenAngle( (float) Math.toRadians( (double)  yellowScreenAngle.get( frame ) ) );

		applyFilter( f );
	}

}//end class
