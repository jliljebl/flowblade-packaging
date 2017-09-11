package animator.phantom.renderer.plugin.editlayer;

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

import java.awt.Dimension;
import java.util.Vector;

import animator.phantom.plugin.PluginAnchorRectEditLayer;
import animator.phantom.renderer.plugin.ShapeMaskPlugin;

public class ShapeMaskPluginEditLayer extends PluginAnchorRectEditLayer
{

	public ShapeMaskPluginEditLayer( ShapeMaskPlugin plugin )
	{
		super( plugin, new Dimension( ShapeMaskPlugin.WIDTH, ShapeMaskPlugin.HEIGHT ) );

		Vector<Integer> buttons = new Vector<Integer>();
		buttons.add( new Integer( MOVE_MODE ));
		buttons.add( new Integer( ROTATE_MODE ));
		setButtonsData( buttons, 0 );
	}

}//end class
