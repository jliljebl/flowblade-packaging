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
import animator.phantom.plugin.PhantomPlugin;
import animator.phantom.renderer.param.AnimatedValue;

import com.jhlabs.image.HighPassFilter;

public class HighPassPlugin extends PhantomPlugin
{
	private AnimatedValue radius;

	public HighPassPlugin()
	{
		initPlugin( FILTER );
	}

	public void buildDataModel()
	{
		setName( "HighPass" );

		radius = new AnimatedValue( 10, 1, 100 );
		registerParameter( radius );
	}

	public void buildEditPanel()
	{
		AnimValueNumberEditor rEdit = new AnimValueNumberEditor( "Radius", radius );
		addEditor( rEdit );
	}

	public void doImageRendering( int frame )
	{
		HighPassFilter f = new HighPassFilter();
		f.setRadius( radius.get( frame ) );
		applyFilter( f );
	}

}//end class

