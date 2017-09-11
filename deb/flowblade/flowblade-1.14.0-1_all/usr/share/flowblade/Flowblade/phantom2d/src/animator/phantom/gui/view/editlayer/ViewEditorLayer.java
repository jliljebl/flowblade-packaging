package animator.phantom.gui.view.editlayer;

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

import giotto2D.core.GeometricFunctions;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Vector;

import javax.swing.JLabel;

import animator.phantom.controller.AppData;
import animator.phantom.controller.TimeLineController;
import animator.phantom.controller.UpdateController;
import animator.phantom.gui.view.EditPoint;
import animator.phantom.gui.view.EditPointShape;
import animator.phantom.gui.view.ViewRenderUtils;
import animator.phantom.gui.view.component.ViewControlButtons;
import animator.phantom.gui.view.component.ViewEditor;
import animator.phantom.renderer.FileSource;
import animator.phantom.renderer.ImageOperation;
import animator.phantom.renderer.RenderNode;


/**
* All editor layers for Phanom plugins are created by extending this 
* class and creating a shape object by extending class <code>EditPointShape</code>.
* <p>
* <h2>Coordinate spaces</h2>
* There are three coordinate spaces involved when creating editor layers. 
* <p>
* <b>PANEL SPACE</b> is coordinate space of JPanel component used to display ViewEditor. 
* This can be ignored when writing editor layers because all input and output coordinates are converted 
* from/to this coordinate space automatically by class  <code>ViewEditor</code> whitch is not part of
* plugin API.
* <p>
* <b>MOVIE SPACE</b> is coordinate space of the output movie. Origo is at the top left corner and postive 
* values go down and right from there. This ids the space displayed to user. This is also the space input
* values are provided, all computations are done and all output is converted to this space by using
* provided methods.
* <p>
* <b>PARENT TRANSFORM SPACE</b> is the coordinate space after parent transform is applied. Only class 
* <code>AnchorRectEditLayer<code> is using this space internally to translate input and output for parented 
* spapes. Plese read soure code for further info.  
*/
public abstract class ViewEditorLayer
{
	//--- Possible edit modes.
	public static final int MOVE_MODE = 0;
	public static final int ROTATE_MODE = 1;
	public static final int KF_EDIT_MODE = 2;
	public static final int KF_ADD_MODE = 3;
	public static final int KF_REMOVE_MODE = 4;
	public static final int PICK_COLOR_MODE = 5;
	public static final int PICK_FG_COLOR_MODE = 6;
	public static final int PICK_BG_COLOR_MODE = 7;
	public static final int CUSTOM_EDIT_MODE_1  = 8;
	public static final int CUSTOM_EDIT_MODE_2  = 9;
	public static final int CUSTOM_EDIT_MODE_3  = 10;
	public static final int CUSTOM_EDIT_MODE_4  = 11;
	public static final int CUSTOM_EDIT_MODE_5  = 12;
	public static final int CUSTOM_EDIT_MODE_6  = 13;
	public static final int CUSTOM_EDIT_MODE_7  = 14;
	public static final int CUSTOM_EDIT_MODE_8  = 15;

	//------------------------------------------- MEMBER VARIABLES
	/**
	* The shape being edited by this layer.
	*/
	protected EditPointShape editPointShape;
	/**
	* User visible name for this edit layer.
	*/
	protected String name = "unnamed layer";
	/**
	* If this is true layer can be interacted with.
	*/
	protected boolean isActive = false;
	/**
	* Flag used to disable/enable controls.
	*/
	protected boolean controlsActive = true;
	/**
	* Flag used to blog for drawing when needed.
	*/
	protected boolean drawingEnabled = true;
	/**
	* ImageOperation beigh edited. May NOT be null, extending must set using constructor.
	*/
	protected ImageOperation iop = null;
	/**
	* Last EditPoint pressed by user.
	*/
	protected EditPoint lastPressedPoint = null;
	/**
	* Point where mouse button was pressed down.
	*/
	protected Point2D.Float mouseStartPoint = null;
	/**
	* Point where mouse is after last mouse event.
	*/
	protected Point2D.Float mouseCurrentPoint = null;


	//--- Reference to editor object.
	private ViewEditor editor;
	//--- Used to calculate rotations.
	//private Point2D.Float mouseRotationStart = null;
	//--- Used to calculate rotations.
	//private Point2D.Float mouseRotationEnd = null;
	//--- Last draw rect. For erasing
	private Rectangle2D.Float lastDrawRect = null;
	//--- FileSource for caching
	private FileSource fileSource;
	//--- Used internally when calculating mouse edit rotations
	private float mouseRotationLast = 0.0f;

	//--- Constructor, called by extending classes.
	protected ViewEditorLayer( ImageOperation iop  )
	{
		this.editor = ViewEditor.getInstance();
		this.iop = iop;
	}

	//------------------------------------------- INTERFACE
	/** 
	* Sets used displayed name for layer. This may be clalled repetedly if user renames nodes.
	* @param newName New name for layer.
	*/
	public void setName( String newName )
	{ 
		RenderNode node = AppData.getFlow().getNode( iop );
		String idStr = "#" + Integer.toString( node.getID() ) + " ";
		name = idStr + newName; 
	}
	/**
	* Returns name of edit layer. 
	* @return Layer name.
	*/
	public String getName(){ return name; }
	/**
	* Returns <code>ImageOperation</code> being edited with this layer.
	*/
	public ImageOperation getIOP(){ return iop; }
	/**
	* Sets edit mode for layer. This causes a callback to method <code>modeChanged()</code>. 
	*/
	public void setMode( int mode ){ editor.setMode( mode ); }
	/**
	* Rerturns edit mode.
	*/
	public int getMode(){ return editor.getMode(); }
	/**
	* Register reference to edit shape being edited in this layer. This should only be called once when creating layer.
	* @param newName Single <code>EditPointShape</code> for layer.
	*/
	public void registerShape( EditPointShape shape ){ editPointShape = shape; }
	/**
	* Returns true if layer is active. Layer is active if it's the one being edited.
	*/
	public boolean isActive(){ return isActive; }
	/**
	* Sets layer active state. Layer is active if it's the one being edited currently.
	* @param state Active state value for layer.
	*/
	public void setActiveState( boolean state ){ isActive = state; }
	/**
	* Sets controls active state. If this set <code>false</code> mouse events will be discarted. Used internally.
	*/
	public void setControlsActive( boolean val ){ controlsActive = val; }
	/**
	* Blocks paint events to <code>paintLayer( Graphics2D g2 )</code>. Used internally.
	*/
	public void setDrawingEnabled( boolean val){ drawingEnabled = val; }
	/**
	* Returns drawing enabled state. Used internally.
	*/
	public boolean getDrawingEnabled( ){ return drawingEnabled; }
	/**
	* Extending classes must override this method to update view when current frame has changed. 
	* This always happens after user has changed current frame.
	*/
	public abstract void frameChanged();
	/**
	* Extending classes must override this method to update view when edit mode has changed.
	* This happens if user changes edit mode or when default mode is set at layer creation time.
	*/
	public abstract void modeChanged();

	/**
	* Extending classes may aoverride this to set buttons using the </code>ViewControlButtons.setModeButtons( Vector<Integer> ) </code>on
	* the provided </code>ViewControlButtons</code> instance.
	* @see PolyLineEditLayer
	* @param buttons Instance of user buttons GUI component
	*/
	public void setLayerButtons( ViewControlButtons buttons )
	{
		buttons.setModeButtons( new Vector<Integer>() );
	}
	/**
	* Returns current displayed frame in editor. Use 
	*/
	public int getCurrentFrame()
	{
		return TimeLineController.getCurrentFrame();
	}
	//--- Extending can overwrite this to return FileSource of iop for caching.
	/**
	* Internal method. Do NOT call from extending edit layer classes.
	*/
	protected void registerFileSource( FileSource fs ){ fileSource = fs; }
	/**
	* Internal method. Do NOT call from extending edit layer classes.
	*/
	public FileSource getFileSource(){ return fileSource; }
	//--------------------------------------------------- COORDINATE CONVERSION MOVIE <-> PANEL
	//--- Returns new points that are translated from real space to panel space.
	public Vector<EditPoint> getPanelCoordinatesEditPoints( Vector<EditPoint> realPoints )
	{
		Vector<EditPoint> panelPoints = new Vector<EditPoint>();
		for( EditPoint p : realPoints )
		{
			EditPoint panelPoint = 
				new EditPoint( editor.getScaledPanelPoint( p.getPos() ) );
			panelPoint.setRotation( p.getRotation() );
			panelPoint.setDisplayType( p.getDisplayType() );
			panelPoints.add( panelPoint );
		}
		return panelPoints;
	}

	//--- Translates point from real space to panel space
	public EditPoint getPanelCoordinatesEditPoint( EditPoint p )
	{
		EditPoint panelPoint = new EditPoint( editor.getScaledPanelPoint( p.getPos() ) );
		panelPoint.setRotation( p.getRotation() );
		return panelPoint;
	}
	//--- 
	public float getScaledLength( float originalLength )
	{
		return editor.getScaledLength( originalLength );
	}


	//----------------------------------- COLOR DETECTION
	/**
	* Returns color in ViewEditor JPanel at movie point. Used for color pickers.
	* @param moviePoint Point in <b>Movie Space</b>.
	* @return Picked color.
	*/
	public Color getPanelColor(  Point2D.Float moviePoint )
	{
		return editor.getColor( moviePoint );
	}

	//------------------------------------------------- HIT TESTS
	//--- Tests if press hit layer
	/**
	* Internal method. Do NOT call from extending edit layer classes.
	*/
	public boolean hit( Point2D.Float p )
	{
		if( getEditPoint( p.x, p.y ) != null ) return true;
		if( shapeAreaHit( p ) == true ) return true;
		
		return false;
	}	
	//--- Some shapes have areas that can be hit, some dont. EditPointShape extending overrides pointInShape()
	//--- for correct behiour.
	private boolean shapeAreaHit( Point2D.Float p )
	{ 
		if( editPointShape.pointInArea( p ) ) return true;
		return false;
	}
	//--- Get EditPoint for pos. In real space.
	private EditPoint getEditPoint( float x, float y )
	{
		Vector<EditPoint> editPoints = getAllPoints();
		for( EditPoint point : editPoints )
			if( point.hit( x, y ) ) return point;
	
		return null;
	}
	/**
	* Returns <code>EditPoint</code> that has point in its hit area or null if no such point exists. 
	*/
 	public EditPoint getEditPoint( Point2D.Float p )
	{
		return getEditPoint( p.x, p.y );
	}
	/**
	* Returns <code>Vector</code> with all <code>EditPoints</code> in <code>EditPointShape</code> being edited. 
	*/
	public Vector<EditPoint> getAllPoints()
	{
		return editPointShape.getAllPoints();
	}	

	//---------------------------------------------------- MOUSE EVENT HANDLING
	//--- Mouse point in movie space.
	/**
	* Internal method. Do NOT call from extending edit layer classes.
	*/
	public void handleMousePress( Point2D.Float p )
	{
		if( !controlsActive ) return;

		mouseStartPoint = p;
		mouseCurrentPoint = p;
		//mouseRotationStart = p;
		//mouseRotationEnd = p;
		mouseRotationLast = 0.0f;
		mousePressed();
	}

	//--- Mouse point in movie space
	/**
	* Internal method. Do NOT call from extending edit layer classes.
	*/
	public void handleMouseDrag( Point2D.Float p )
	{
		if( !controlsActive ) return;

		mouseCurrentPoint = p;
		//mouseRotationEnd = p;
		mouseDragged();
	}

	//--- Mouse point in movie space
	/**
	* Internal method. Do NOT call from extending edit layer classes.
	*/
	public void handleMouseRelease( Point2D.Float p )
	{
		if( !controlsActive ) return;

		mouseCurrentPoint = p;
		//mouseRotationEnd = p;
		mouseReleased();
	}
	/** 
	* Extending class must override to handle press mouse event.
	*/
	public abstract void mousePressed();
	/** 
	* Extending class must override to handle drag mouse event.
	*/
	public abstract void mouseDragged();
	/** 
	* Extending class must override to handle release mouse event.
	*/
	public abstract void mouseReleased();
	/**
	* Returns mouse movement delta in <b>Movie Space</b> since last mouse press event.
	*/
	public Point2D.Float getMouseDelta()
	{
		return new  Point2D.Float( mouseCurrentPoint.x - mouseStartPoint.x,
						mouseCurrentPoint.y - mouseStartPoint.y );
	}
	/**
	* Returns mouse drag rotation angle around provided anchor point since last press event. 
	* Multiple rotations while dragging do not give bigger angles then in range -360 to 360.
	* @param anchor Rotation angle corner point. 
	* @return Value in range -360 to 360.
	*/
	/*
	protected float getMouseRotationAngle( EditPoint anchor )
	{
		float angle = 
			GeometricFunctions.getAngleInDeg(		mouseRotationStart,
									anchor.getPos(),
									mouseRotationEnd );
		//--- Test if angle is clock wise or not
		boolean cw = GeometricFunctions.pointsClockwise(	mouseRotationStart,
									anchor.getPos(),
									mouseRotationEnd );
		if( !cw ) angle = -angle;

		//--- Crossed angle for 180 -> 181... range
		float crossedAngle = angle + 360.0f;

		//--- Crossed angle for -180 -> 181 ...range.
		if( angle > 0 ) crossedAngle = -360.0f + angle;

		//--- See if crossed angle closer to last angle.
		//--- If crossed angle is closer to last angle set angle crossed angle
		if( Math.abs( mouseRotationLast - crossedAngle  ) <
			Math.abs( mouseRotationLast - angle ) )
				angle = crossedAngle;

		//--- Set last to get good results next time.
		mouseRotationLast = angle;
		
		return angle;
	}
	*/
	/**
	* Returns mouse drag rotation angle around provided anchor, start and end points.
	* @param anchor Rotation angle corner point. 
	* @param mrStart Rotation angle start point. 
	* @param mrEnd Rotation angle end point.
	* @return Value in range -360 to 360.
	*/
	protected float getMouseRotationAngle( EditPoint anchor, Point2D.Float mrStart, Point2D.Float mrEnd  )
	{
		float angle = 
			GeometricFunctions.getAngleInDeg(		mrStart,
									anchor.getPos(),
									mrEnd );
		boolean cw = GeometricFunctions.pointsClockwise(	mrStart,
									anchor.getPos(),
									mrEnd );
		if( !cw ) angle = -angle;
		//--- Crossed angle for 180 -> 181... range
		float crossedAngle = angle + 360.0f;
		//--- Crossed angle for -180 -> 181 ...range.
		if( angle > 0 ) crossedAngle = -360.0f + angle;
		//--- See if crossed angle closer to last angle.
		if( Math.abs( mouseRotationLast - crossedAngle  ) <
			Math.abs( mouseRotationLast - angle ) )
				angle = crossedAngle;
		//--- Set last to get good results next time.
		mouseRotationLast = angle;
		
		return angle;
	}
	/**
	* Clears all saved mouse actions data.
	*/
	public void clearMouseMoveData()
	{
		lastPressedPoint = null;
		mouseStartPoint = null;
		mouseCurrentPoint = null;
		//mouseRotationStart = null;
		//mouseRotationEnd = null;
	}
	/**
	* Returns true if last mouse press was done using left mouse button.
	*/
	public boolean lastPressWasLeftMouse()
	{
		return editor.lastPressWasLeftMouse();
	}
	//-------------------------------------------------------- UPDATE
	//--- Updates display of other editor after edit mouse release.
	//--- YRitä pakottaa kaikkin ja tee private methodiksi.
	/**
	* Internal method. Do NOT call from extending edit layer classes.
	*/
	protected void mouseReleaseUpdate()
	{
		//--- kf diamonds update
		iop.createKeyFramesDrawVector();
		TimeLineController.initClipsGUI();
		//--- value displayers update
		UpdateController.valueChangeUpdate( UpdateController.VIEW_EDIT );
	}

	//--------------------------------------------------------- PAINT
	/**
	* Internal method. Do NOT call from extending edit layer classes.
	*/
	public void paint( Graphics2D g2 )
	{
		if( !drawingEnabled ) return;

		if( editPointShape != null )
			lastDrawRect = editPointShape.getBoundingBox();

		paintLayer( g2 );
	}

	/**
	* Extending classes must override to paint the edited shape.
	*/
	protected abstract void paintLayer( Graphics2D g2 );

	/**
	* Converts points in movie space to panel space and draws handles to graphics object with provided color.
	* Use this to draw handles.
	*/
	protected void drawEditPoints( Graphics2D g, Vector<EditPoint> realPoints, Color color )
	{
		//--- Create EditPoints vector in panel space for drawing
		g.setColor( color );
		Vector<EditPoint> panelPoints = 
				getPanelCoordinatesEditPoints( realPoints );
		ViewRenderUtils.drawPoints( g, panelPoints, color );
	}

	/**
	* Converts points in movie space to panel space and draws connecting lines to graphics object with provided color.
	* Use this to draw line polygons.
	*/
	protected void drawPolygon( Graphics2D g, Vector<EditPoint> realPoints, Color color )
	{
		//--- Create EditPoints vector in panel space for drawing
		g.setColor( color );
		Vector<EditPoint> panelPoints = 
				getPanelCoordinatesEditPoints( realPoints );
		ViewRenderUtils.drawPolygon( g, panelPoints, color );
	}

	/**
	* Return correct draw color for shape in current frame and layer active state.
	*/
	public Color getDrawColor()
	{
		Color color = Color.white;
		if( !isActive )
			color = Color.darkGray;
		if( !iop.frameInClipArea(  getCurrentFrame() ) && isActive ) 
			color = Color.red;
		return color;
	}

	//---------------------------------------------------------- ERASE HELP
	/**
	* Internal method. Do NOT call from extending edit layer classes.
	*/
	public void setLastDrawRect( Rectangle2D.Float r ){ lastDrawRect = r; }
	/**
	* Internal method. Do NOT call from extending edit layer classes.
	*/
	public Rectangle2D.Float getLastDrawRect(){ return lastDrawRect; }

}//end class
