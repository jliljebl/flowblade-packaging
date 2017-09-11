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

import giotto2D.filters.transform.FlipFilter;

import java.awt.image.BufferedImage;

import animator.phantom.paramedit.CheckBoxEditor;
import animator.phantom.plugin.PhantomPlugin;
import animator.phantom.renderer.param.BooleanParam;

public class FlipPlugin extends PhantomPlugin
{
	public BooleanParam flipH = new BooleanParam( false );
	public BooleanParam flipV = new BooleanParam( false );

	public FlipPlugin()
	{
		initPlugin( FILTER );
	}

	public void buildDataModel()
	{
		setName( "Flip" );
		registerParameter( flipH );
		registerParameter( flipV );
	}

	public void buildEditPanel()
	{
		CheckBoxEditor flipHEdit = new CheckBoxEditor( flipH, "Flip Horizontal", true );
		CheckBoxEditor flipVEdit = new CheckBoxEditor( flipV, "Flip Vertical", true );

		addEditor( flipHEdit );
		addRowSeparator();
		addEditor( flipVEdit );
	}

	public void doImageRendering( int frame )
	{
		BufferedImage flowImg = getFlowImage();

		if( flipH.get() && !flipV.get() ) flowImg = FlipFilter.filter( flowImg, false, true );
		else if( !flipH.get() && flipV.get()  ) flowImg = FlipFilter.filter( flowImg, true, false );
		else if( flipH.get() && flipV.get()  ) flowImg = FlipFilter.filter( flowImg, true, true );

		sendFilteredImage( flowImg, frame );
	}

}//end class
