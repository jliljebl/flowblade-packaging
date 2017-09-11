package animator.phantom.plugin;

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

import animator.phantom.gui.view.editlayer.PluginAnchorRectViewEditorLayer;

/**
* Base class for edit layers used to edit position, rotation and scaling of an animated rectangular shape that has an anchor point.
* <p> 
* All plugins using classes extending this as edit layer must call <code>PhantomPlugin.registerCoords()</code>
* or they must be of type <code>MOVING_SOURCE</code> to have parameters that have their values set using classes extending this.
* <p>
* Size of <b>untransformed</b> rectangular image source must provided when extending class calls its super constructor here. 
* That size cannot be changed later. No other parameters can be set.
*<p>
* Default edit modes are <b>MOVE_MODE, ROTATE_MODE</b> Override <code> setButtonsData( Vector<Integer> buttons, int defaultMode )</code>
* to set different edit modes.
*/
public abstract class PluginAnchorRectEditLayer  extends AbstractPluginEditLayer
{

	/**
	* Creates edit layer for plugin that animates a rectangular source image. 
	* @param plugin Reference to plugin that has its data edited by this.
	* @param untransformedRectSize Size of rectangular image source before transforming in pixels.
	*/
	public PluginAnchorRectEditLayer( PhantomPlugin plugin, Dimension untransformedRectSize )
	{
		layer = new PluginAnchorRectViewEditorLayer( plugin, untransformedRectSize );
		layer.setName( plugin.getName() );
	}

	/**
	* Sets Vector of mode button identifiers and default mode.
	*/
	public void setButtonsData( Vector<Integer> buttons, int defaultMode )
	{
		((PluginAnchorRectViewEditorLayer )layer).setButtonsData( buttons, defaultMode );
	}

}//end class
