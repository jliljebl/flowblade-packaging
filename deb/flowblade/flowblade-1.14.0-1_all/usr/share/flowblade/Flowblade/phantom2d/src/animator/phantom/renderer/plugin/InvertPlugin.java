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

import giotto2D.filters.color.Invert;

import java.awt.image.BufferedImage;

import animator.phantom.paramedit.NoParamsPanel;
import animator.phantom.plugin.PhantomPlugin;

public class InvertPlugin extends PhantomPlugin
{
	public InvertPlugin()
	{
		initPlugin( FILTER );
	}

	public void buildDataModel()
	{
		setName( "Invert" );
	}

	public void buildEditPanel()
	{
		addEditor( new NoParamsPanel("Invert") );
	}

	public void doImageRendering( int frame )
	{
		BufferedImage flowImg = getFlowImage();

		Invert.filter( flowImg );
		sendFilteredImage( flowImg, frame );
	}

}//end class
