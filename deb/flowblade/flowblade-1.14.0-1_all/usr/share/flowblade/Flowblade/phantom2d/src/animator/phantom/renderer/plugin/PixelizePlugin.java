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

import giotto2D.filters.blur.Pixelize;

import java.awt.image.BufferedImage;

import animator.phantom.paramedit.AnimValueNumberEditor;
import animator.phantom.plugin.PhantomPlugin;
import animator.phantom.renderer.param.AnimatedValue;

public class PixelizePlugin extends PhantomPlugin
{
	private AnimatedValue sizeX;
	private AnimatedValue sizeY;

	public PixelizePlugin()
	{
		initPlugin( FILTER );
	}

	public void buildDataModel()
	{
		setName( "Pixelize" );

		sizeX = new AnimatedValue( 10 );
		sizeY = new AnimatedValue( 10 );

		registerParameter( sizeX );
		registerParameter( sizeY );
	}

	public void buildEditPanel()
	{
		AnimValueNumberEditor amountXEdit = new  AnimValueNumberEditor( "X Size", sizeX );
		AnimValueNumberEditor amountYEdit = new  AnimValueNumberEditor( "Y Size", sizeY );

		addEditor( amountXEdit );
		addRowSeparator();
		addEditor( amountYEdit );
	}

	public void doImageRendering( int frame )
	{
		BufferedImage flowImg = getFlowImage();

		Pixelize filter = new Pixelize();
		filter.setXSize( (int) sizeX.get(frame) );
		filter.setYSize( (int) sizeY.get(frame) );
		filter.filter( flowImg );

		sendFilteredImage( flowImg, frame );
	}

}//end class
