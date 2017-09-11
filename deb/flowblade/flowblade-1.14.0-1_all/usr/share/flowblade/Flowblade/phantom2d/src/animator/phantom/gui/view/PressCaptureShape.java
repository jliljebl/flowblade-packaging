package animator.phantom.gui.view;

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

import java.awt.geom.Point2D;

import animator.phantom.gui.view.editlayer.ViewEditorLayer;
import animator.phantom.plugin.PluginEditLayer;
/**
* A shape used to capture all mouse presses when layer is active. Shape accepts all mouse presses as hits, so 
* all mouse events are passed to layer that will handle them.
*/
public class PressCaptureShape extends EditPointShape
{
	/**
	* Edit layer.
	*/
	protected ViewEditorLayer layer;
	/**
	* Constructor with internal edit layer.
	*/
	public PressCaptureShape( ViewEditorLayer layer ){ this.layer = layer; }
	/**
	* Constructor with edit layer.
	*/
	public PressCaptureShape( PluginEditLayer editLayer ){ this.layer = editLayer.getLayerObject(); }
	/**
	* This returns true for all mouse presses if layer is active because shape is meant to 
	* be used with layers that collect sample points.
	*/
	public boolean pointInArea( Point2D.Float p )
	{ 
		if( layer.isActive() ) return true;
		return false;
	}

}//end class