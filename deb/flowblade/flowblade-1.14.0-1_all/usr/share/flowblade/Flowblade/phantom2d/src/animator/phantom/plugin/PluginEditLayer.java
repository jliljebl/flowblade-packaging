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

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.util.Vector;

import animator.phantom.gui.view.EditPoint;
import animator.phantom.gui.view.EditPointShape;
import animator.phantom.gui.view.ViewRenderUtils;
import animator.phantom.gui.view.editlayer.PluginViewEditorLayer;

/**
* Base class for edit layers used to edit plugins other then those that produce animated rectangular shapes.
* <p>
*  A class representing edited shape that extends class <code>EditPointShape</code> must be created an registered to read and write 
* values from / to the parameters of the edited plugin.
* See examples: <code> NullShape.java, TwoPointShape,java, PolyLineShape.java </code> all in directory
* /animator/phantom/gui/view/editlayer of application source code.
* <h2>Creating functionality</h2>
* <p>
* Basic functionality is created by overriding methods to handle event callbacks.
* <p>
* Method <code>frameChanged(int frame)</code> must be overridden to set <code>EditPoints</code> in <code>EditShapa</code>
* to positions corresponding current frame.
* <p>
* Method <code> modeChanged(int newMode)</code> may be overridden update view for different edit modes.
* <p>
* Method <code> paintLayer(java.awt.Graphics2D g)</code> must be overridden to draw edit layer.
* <p> 
* Methods <code> mousePressed( int frame, Point2D.Float startPoint ), 
* mouseDragged( int frame, Point2D.Float startPoint, Point2D.Float dragPoint ){}
* mouseReleased( int frame, Point2D.Float startPoint, Point2D.Float relesePoint ){}</code>
* may be overridden to handle mouse events.
* <p> 
* See examples: <code> GradientPluginEditLayer.java </code>
*/
public abstract class PluginEditLayer extends AbstractPluginEditLayer
{
	/**
	* Constructor with plugin.
	* @param plugin Plugin that has its parameters' values edited with this.
	*/
	public PluginEditLayer( PhantomPlugin plugin )
	{
		layer = new PluginViewEditorLayer( this, plugin.getIOP() );
		layer.setName( plugin.getName() );
	}

	/**
	* Register reference to edit shape being edited in this layer. This should only be called once when creating layer.
	* @param shape The one and only edit shape in a edit layer.
	*/
	public void registerShape( EditPointShape shape )
	{
		layer.registerShape( shape ); 
	}
	/**
	* Rerturns edit mode.
	*/
	public int getMode()
	{
		return layer.getMode(); 
	}
	/**
	* Returns current displayed frame in editor. Use 
	*/
	public int getCurrentFrame()
	{
		return layer.getCurrentFrame();
	}
	/**
	* Sets Vector of button identifiers default selected mode.
	* @param buttons Vector of Integers representing buttons, use field values to create these.
	* @param selIndex Index of default selected button,determines default edit mode also.
	*/
	public void setButtonsData( Vector<Integer> buttons, int selIndex )
	{
		((PluginViewEditorLayer)layer).setButtonsData( buttons, selIndex );
	}
	/**
	* Extending classes must override this method to update view when current frame has changed. 
	* This gets called when user has changes current frame.
	* @param frame Current frame
	*/
	public abstract void frameChanged( int frame );
	/**
	* Extending classes may override this method to update view when edit mode has changed.
	* @param newMode New edit mode.
	*/
	public void modeChanged( int newMode ){}
	/**
	* Returns <code>EditPoint</code> that has point in its hit area or null if no such point exists.
	* @param p Test point for edit point on the registered shape
	*/
 	public EditPoint getEditPoint( Point2D.Float p )
	{
		return layer.getEditPoint( p );
	}
	/**
	* Returns color in ViewEditor JPanel at movie point. Used for color pickers.
	* @param moviePoint Point in <b>Movie Space</b>.
	* @return Picked color.
	*/
	public Color getPanelColor(  Point2D.Float moviePoint )
	{
		return layer.getPanelColor( moviePoint );
	}
	/**
	* Converts points in movie space to panel space and draws handles to graphics object with provided color.
	* Use this to draw handles.
	* @param g Graphics context from View Editor JPanel that that will be drawn on.
	* @param moviePoints Points that will be drawn after coordinate space conversion to Panel Space.
	* @param color Color that will be used for drawing.
	*/
	protected void drawEditPoints( Graphics2D g, Vector<EditPoint> moviePoints, Color color )
	{
		Vector<EditPoint> panelPoints = layer.getPanelCoordinatesEditPoints( moviePoints );
		ViewRenderUtils.drawPoints( g, panelPoints, color );
	}
	/**
	* Converts points in movie space to panel space and draws connecting lines to graphics object with provided color.
	* Use this to draw line polygons.
	* @param g Graphics context from View Editor JPanel that that will be drawn on.
	* @param moviePoints Points that will be connected with lines after coordinate space conversion to Panel Space.
	* @param color Color that will be used for drawing.
	*/
	protected void drawPolygon( Graphics2D g, Vector<EditPoint> moviePoints, Color color )
	{
		Vector<EditPoint> panelPoints = layer.getPanelCoordinatesEditPoints( moviePoints );
		ViewRenderUtils.drawPolygon( g, panelPoints, color );
	}
	/**
	* Return correct draw color for shape in current frame and layer active state.
	*/
	protected Color getDrawColor()
	{
		return layer.getDrawColor();
	}
	/** 
	* Override to handle mouse press event.
	* @param frame Current frame
	* @param startPoint Mouse move start point in Movie Space.
	*/
	public void mousePressed( int frame, Point2D.Float startPoint ){}
	/** 
	* Override to handle mouse drag event.
	* @param frame Current frame
	* @param startPoint Mouse move start point in Movie Space.
	* @param dragPoint Mouse position after drag in Movie Space.
	*/
	public void mouseDragged( int frame, Point2D.Float startPoint, Point2D.Float dragPoint ){}
	/** 
	* Override to handle mouse release event.
	* @param frame Current frame
	* @param startPoint Mouse move start point in Movie Space.
	* @param relesePoint Mouse position after release in Movie Space.
	*/
	public void mouseReleased( int frame, Point2D.Float startPoint, Point2D.Float relesePoint ){}
	/**
	* Returns mouse movement delta in <b>Movie Space</b> since last mouse press event. Convenience method, this can be calculated from
	* info provided in mouseDragged and mouseReleased methods.
	*/
	public Point2D.Float getMouseDelta()
	{
		return layer.getMouseDelta();
	}
	/**
	* Extending classes must override to paint the edited shape.
	* @param g Graphics context from View Editor JPanel that that will be drawn on.
	*/
	public abstract void paintLayer( Graphics2D g );

}//end class
