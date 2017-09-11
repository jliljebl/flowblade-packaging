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
import java.awt.Graphics2D;

public class ShapeGridMergePlugin extends GridPlugin
{
	public ShapeGridMergePlugin()
	{
		initPlugin( MERGE, MERGE_INPUTS );
	}

	public void buildDataModel()
	{
 		setName( "ShapeGridMerge" );
		registerGridParams();
	}

	public void buildEditPanel()
	{
		addGridEditors( false );
	}

	public void renderMask( float frame, Graphics2D maskGraphics, int canvasWidth, int canvasHeight )
	{
		drawGrid(maskGraphics, canvasWidth, canvasHeight, null, Color.white, frame, false );
	}

}//end class