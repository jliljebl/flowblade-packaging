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

import javax.swing.ImageIcon;

import animator.phantom.controller.GUIComponents;
import animator.phantom.gui.view.editlayer.ViewEditorLayer;

/**
*Base class for edit layers. 
*<p>
* Edit layers for <b>plugins</b> are created by <b>extending a subclass of this class</b> 
* and creating and registering a <b>shape object</b> that extends class <code>EditPointShape</code>.
* <p>
* Edit layers are displayed in View Editor where the user can use mouse to move, scale, rotate and manipulate
* edit point shapes. These actions are interpreted as parameter value changes in the current frame.
* 
* <p>
* <h2>Coordinate spaces</h2>
* When creating edit layers it is essential to understad the coordinate spaces involved.
* <p>
* <b>MOVIE SPACE</b> is the coordinate space of the output movie screen. Origo is at the top left corner of the output image and postive 
* values go down and right from there. The user of the editor gives and receives all input and output values in this
* coordinate space. Editor layers receive all mouse event input values in this coordinate space and all parameter values 
* of plugins are in this coordinate space too. When drawing output into the provided graphics context object
* layers must convert values into Panel Space of the context object using provided draw methods. 
* <p>
* <b>PANEL SPACE</b> is the coordinate space of the JPanel component used to display View Editor. 
* Graphics context objects provided for drawing output are in this coordinate space. When drawing
* into these, provided draw methods must be used, as these methods do the coordinate space transform from
* Movie Space tp Panel Space.
* <p>
* 
* <h2>Edit modes</h2>
* There are 5 predefined edit modes with buttons: <b>MOVE_MODE, ROTATE_MODE, POINT_EDIT_MODE, POINT_ADD_MODE, POINT_REMOVE_MODE</b>
* and possibility to define up to 6 custom edit modes per plugin. Note that implementing functionality for edit
* modes is up plugin creators, predefined edit modes are just names with corresponding buttons. 
*/
public abstract class AbstractPluginEditLayer
{
	//--- Possible edit modes.
	/**
	* Edit mode for moving and scaling an object.
	*/
	public static final int MOVE_MODE = ViewEditorLayer.MOVE_MODE;
	/**
	* Edit mode for rotating an object.
	*/
	public static final int ROTATE_MODE =  ViewEditorLayer.ROTATE_MODE;
	/**
	* Edit mode for moving points.
	*/
	public static final int POINT_EDIT_MODE = ViewEditorLayer.KF_EDIT_MODE;
	/**
	* Edit mode for adding points.
	*/
	public static final int POINT_ADD_MODE = ViewEditorLayer.KF_ADD_MODE;
	/**
	* Edit mode for removing points.
	*/
	public static final int POINT_REMOVE_MODE = ViewEditorLayer.KF_REMOVE_MODE;
	/**
	* Edit mode for removing points.
	*/
	public static final int COLOR_PICK_MODE = ViewEditorLayer.PICK_COLOR_MODE;
	/**
	* A plugin defined custom edit mode with plugin defined button graphic.
	*/
	public static final int CUSTOM_EDIT_MODE_1  = ViewEditorLayer.CUSTOM_EDIT_MODE_1;
	/**
	* A plugin defined custom edit mode with plugin defined button graphic.
	*/
	public static final int CUSTOM_EDIT_MODE_2  = ViewEditorLayer.CUSTOM_EDIT_MODE_2;
	/**
	* A plugin defined custom edit mode with plugin defined button graphic.
	*/
	public static final int CUSTOM_EDIT_MODE_3  = ViewEditorLayer.CUSTOM_EDIT_MODE_3;
	/**
	* A plugin defined custom edit mode with plugin defined button graphic.
	*/
	public static final int CUSTOM_EDIT_MODE_4  = ViewEditorLayer.CUSTOM_EDIT_MODE_4;
	/**
	* A plugin defined custom edit mode with plugin defined button graphic.
	*/
	public static final int CUSTOM_EDIT_MODE_5  = ViewEditorLayer.CUSTOM_EDIT_MODE_5;
	/**
	* A plugin defined custom edit mode with plugin defined button graphic.
	*/
	public static final int CUSTOM_EDIT_MODE_6  = ViewEditorLayer.CUSTOM_EDIT_MODE_6;
	/**
	* A plugin defined custom edit mode with plugin defined button graphic.
	*/
	public static final int CUSTOM_EDIT_MODE_7  = ViewEditorLayer.CUSTOM_EDIT_MODE_7;
	/**
	* A plugin defined custom edit mode with plugin defined button graphic.
	*/
	public static final int CUSTOM_EDIT_MODE_8  = ViewEditorLayer.CUSTOM_EDIT_MODE_8;
	/**
	* Layer object for application internal use. 
	*/
	protected ViewEditorLayer layer = null;
	/**
	* Returns layer object used internally by application.
	*/
	public ViewEditorLayer getLayerObject()
	{
		return layer;
	}
	/**
	* Returns true if last mouse press was done using left mouse button.
	*/
	public boolean lastPressWasLeftMouse()
	{
		return layer.lastPressWasLeftMouse();
	}
	/**
	* Sets mode buttons default and pressed icon
	*/
	public void setModeButtonIcons( int mode, ImageIcon defaultIcon, ImageIcon selectedIcon )
	{
		GUIComponents.viewControlButtons.setButtonIcons( mode, defaultIcon, selectedIcon );
	}
	/**
	* Returns current edit mode.
	*/	
	public int getMode(){ return layer.getMode(); }

}//end class
