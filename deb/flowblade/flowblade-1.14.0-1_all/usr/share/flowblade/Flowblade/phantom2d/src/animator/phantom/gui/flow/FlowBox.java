package animator.phantom.gui.flow;

/*
    Copyright Janne Liljeblad

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
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.Vector;

import animator.phantom.gui.GUIColors;
import animator.phantom.gui.GUIResources;
import animator.phantom.renderer.IOPLibrary;
import animator.phantom.renderer.ImageOperation;
import animator.phantom.renderer.RenderNode;


//--- Objects of this class render the selectable boxes of render nodes in FlowEditPanel.
//--- They display state information that tells if the object is beign edited and/or is selected
public class FlowBox implements FlowGraphic, Comparable<Object>
{	
	//--- The render node that this box stands for
	private RenderNode renderNode;
	
	//--- Dimensions (not final?)
	public static int width = 130;
	public static int height = 23;

	//--- Font
	private static final Font boxFont = new Font( "SansSerif", Font.PLAIN, 11 );
	
	//--- Draw parameters
 	private static final int NAME_DRAW_X = 10;
	private static final int NAME_DRAW_Y = 15;
	public static final int ARROW_CP_OFF_X = 2;
	public static final int ARROW_CP_OFF_Y = 2; 

	//--- Prerendrered images of states of box
	private BufferedImage normal;
	private BufferedImage normalSelected;
	private BufferedImage moving;
	
	//--- Coordinates
	private int x;
	private int y;
	
	//--- Connection points of this box.
	private Vector<FlowBoxConnectionPoint> connectionPoints;
	
	//--- Y positions of input and output points.
	private static final int INPUT_Y_POSITION = 0;
	private static final int OUTPUT_Y_POSITION = 18;
	private static final int MASK_INPUT_X = width - 6;
	private static final int MASK_INPUT_Y = 9;

	private static BufferedImage flowBG = GUIResources.getResourceBufferedImage( GUIResources.flowBoxBG );
	private static BufferedImage flowBGSelected = GUIResources.getResourceBufferedImage( GUIResources.flowBoxBGSelected );
	private static BufferedImage flowBGFilter  = GUIResources.getResourceBufferedImage( GUIResources.flowBoxBGFilter );
	private static BufferedImage flowBGMerge  = GUIResources.getResourceBufferedImage( GUIResources.flowBoxBGMerge );
	private static BufferedImage flowBGAlpha  = GUIResources.getResourceBufferedImage( GUIResources.flowBGAlpha );
	private static BufferedImage flowBGMedia = GUIResources.getResourceBufferedImage( GUIResources.flowBGMedia );
	private static BufferedImage noFileSource  = GUIResources.getResourceBufferedImage( GUIResources.noFileSource );
	
	//--- Vector for x pos for different numbers of connection points. 
	private static Vector<int[]> POINTS_X_POSITIONS = new Vector<int[]>();

	//--- Creates table for connection point positions.
	static
	{
		int[] onePoints = { 62 };
		int[] twoPoints = { 42, 82 };
		int[] threePoints = { 38, 62, 86 };
		int[] fourPoints = { 17, 47, 77, 107 };
		POINTS_X_POSITIONS.addElement( onePoints );
		POINTS_X_POSITIONS.addElement( twoPoints );
		POINTS_X_POSITIONS.addElement( threePoints );
		POINTS_X_POSITIONS.addElement( fourPoints );
	}

	//--- Name
	private String name;
	//--- Selected state
	private boolean isSelected = false;
	//--- Moving state
	private boolean isMoving = false;

	//--- For loading.
	public FlowBox(){}

	//-------------------------------------------- Constructor with render node and default size
	public FlowBox( int x,
			int y,
			RenderNode node )
	{
		this.x = x;
		this.y = y;
		this.renderNode = node;
		this.name = node.getIOPName();
		createConnectionPoints();
		
		preRender();
	}

	//--- Constructor with only position
	public FlowBox( int x,
			int y )
	{
		this.x = x;
		this.y = y;
	}

	public int getX(){ return x; }
	public int getY(){ return y; }
	public int getWidth(){ return width; }
	public int getHeight(){ return height; }
	public RenderNode getRenderNode(){ return renderNode; }
	public void setRenderNode( RenderNode newNode )
	{ 
		renderNode = newNode;
		this.name = renderNode.getIOPName();
	}
	public ImageOperation getImageOperation(){ return renderNode.getImageOperation(); }
	public void setSelected( boolean value ){ isSelected = value; }	
	public boolean isSelected(){ return isSelected; }
	public void setIsMoving( boolean value ){ isMoving = value; }
	public void setPlace( int x, int y )
	{ 
		this.x = x;
		this.y = y;
	}

	//--- Return all arrows connected to thisBox
	public Vector<FlowConnectionArrow> getAllArrows()
	{
		Vector<FlowConnectionArrow> retV = new Vector<FlowConnectionArrow>();
		for( FlowBoxConnectionPoint cp : connectionPoints )
		{
			FlowConnectionArrow arrow = cp.getArrow();
			if( arrow != null ) retV.addElement( arrow );
		}
		return retV;
	}
	public Vector<FlowBoxConnectionPoint> getOutputConnectionPoints()
	{
		Vector<FlowBoxConnectionPoint> retVec = new Vector<FlowBoxConnectionPoint>();
		for( FlowBoxConnectionPoint cp : connectionPoints )
			if( cp.getType() == FlowBoxConnectionPoint.OUTPUT ) retVec.add( cp );

		return retVec;
	}
	//--- Returns output connection point for index.
	public FlowBoxConnectionPoint getOutputCP( int index )
	{
		Vector<FlowBoxConnectionPoint> ocps = getOutputConnectionPoints();
		return ocps.elementAt( index );
	}	
	//--- Returs input connection point for index.
	public FlowBoxConnectionPoint getInputCP( int index )
	{
		Vector<FlowBoxConnectionPoint> icps = new Vector<FlowBoxConnectionPoint>();
		for( FlowBoxConnectionPoint cp : connectionPoints )
			if( cp.getType() == FlowBoxConnectionPoint.INPUT ) icps.add( cp );

		return icps.elementAt( index );
	}
	//--- Changes outputs number and recalculates positiones.
	public void changeOutputsNumber( int outputsNumber )
	{
		//--- Delete connectionPoints not in range.
		Vector<FlowBoxConnectionPoint> outputPoints = getOutputConnectionPoints();
		for( FlowBoxConnectionPoint cp : outputPoints )
		{
			int index = cp.getIndex();
			if( index >= outputsNumber ) connectionPoints.remove( cp );
		}

		//--- Add connection points if needed.

		if( outputPoints.size() < outputsNumber )
		{
			for( int i = 0; i < outputsNumber - outputPoints.size(); i++ ) 
				connectionPoints.addElement(
					new FlowBoxConnectionPoint( 	this,
									FlowBoxConnectionPoint.OUTPUT,
									i + outputPoints.size() ) );
		}

		//--- Recalculate connactionpoints places.
		calculateConnectionPointPositions();

		//--- Re-render display images 
		preRender();
	}
	
	public void setOutputArrowsToPoints()
	{
		for( FlowBoxConnectionPoint cp : connectionPoints )
		{
			if( cp.getType() == FlowBoxConnectionPoint.OUTPUT )
			{
				if( cp.getArrow() != null ) 
				{
					cp.getArrow().setStartPos( cp.getX() + x + ARROW_CP_OFF_X, cp.getY() + y + ARROW_CP_OFF_Y );
				}
			}
		}
	}
	//--- Returns a connection point if it is beign hit.
	public FlowBoxConnectionPoint getConnectionPoint( int testX, int testY )
	{
		for( FlowBoxConnectionPoint cp : connectionPoints )
		{			
			if( 	( testX >= ( x + cp.getX()  ) ) &&
				( testX <= ( x + cp.getX() + FlowBoxConnectionPoint.WIDTH ) )&&
				( testY >= ( y + cp.getY() ) ) &&
				( testY <= ( y + cp.getY() + FlowBoxConnectionPoint.HEIGHT ) ) )
			
			return cp;
		}
		return null;
	}
	//--- Clears references for given arrow in connection points.
	public void removeArrow( FlowConnectionArrow rArrow )
	{
		for( FlowBoxConnectionPoint cp : connectionPoints )
			if( cp.getArrow() == rArrow ) cp.setArrow( null );
	}
	//--- Creates connection points.
	public void createConnectionPoints()
	{
		//--- Create data structure.
		connectionPoints = new Vector<FlowBoxConnectionPoint>();

		//--- Compensate for mask input if necessery.
		int less = 0;
		if( renderNode.getImageOperation().hasMaskInput() ) less = 1;

		//--- Create inputs
		for( int i = 0; i < renderNode.getNumberOfSources() - less; i++ )
		{
			connectionPoints.addElement( 
				new FlowBoxConnectionPoint( this, FlowBoxConnectionPoint.INPUT, i ) );
		}
		//--- Create mask input is exists.
		if( renderNode.getImageOperation().hasMaskInput() )
		{
			FlowBoxConnectionPoint minput = 
				new FlowBoxConnectionPoint( this, FlowBoxConnectionPoint.INPUT, renderNode.getNumberOfSources() - 1 );
			minput.setPos( MASK_INPUT_X, MASK_INPUT_Y );
			minput.setAsMaskInput( true );
			connectionPoints.addElement( minput );
		}

		//--- Create outputs
		for( int i = 0; i < renderNode.getNumberOfTargets(); i++ )
		{
			connectionPoints.addElement( 
				new FlowBoxConnectionPoint( this, FlowBoxConnectionPoint.OUTPUT, i ) );
		}
		calculateConnectionPointPositions();
	}

	private void calculateConnectionPointPositions()
	{
		//--- Separate sources and targets, exclude mask input.
		Vector<FlowBoxConnectionPoint> inputs = new Vector<FlowBoxConnectionPoint> ();
		Vector<FlowBoxConnectionPoint>  outputs = new Vector<FlowBoxConnectionPoint> ();
		for( FlowBoxConnectionPoint cp : connectionPoints )
		{
			if( cp.getType() == FlowBoxConnectionPoint.INPUT && !cp.isMaskInput() ) inputs.addElement( cp );
			else if( !cp.isMaskInput() ) outputs.addElement( cp );
		}
		//--- Set input positions
		int[] xPos = ( int[] ) POINTS_X_POSITIONS.elementAt( inputs.size() - 1 );
		for( int i = 0; i < inputs.size(); i++ )
		{
			FlowBoxConnectionPoint cp = (FlowBoxConnectionPoint) inputs.elementAt( i );
			cp.setPos( xPos[ i ], INPUT_Y_POSITION );
		}
		//--- Set output positions
		xPos = ( int[] ) POINTS_X_POSITIONS.elementAt( outputs.size() - 1 );
		for( int i = 0; i < outputs.size(); i++ )
		{
			FlowBoxConnectionPoint cp = (FlowBoxConnectionPoint) outputs.elementAt( i );
			cp.setPos( xPos[ i ], OUTPUT_Y_POSITION );
		}
	}	

	//--- Prerendres all the images used to represent this box.
	public void preRender()
	{
		int boxType = IOPLibrary.getBoxType( renderNode.getImageOperation() );
	
		System.out.println("boxType");
		System.out.println(boxType);
		//--- normal
		normal = new BufferedImage( width, height, BufferedImage.TYPE_4BYTE_ABGR );
		Graphics2D gc = normal.createGraphics();
		if ( boxType == IOPLibrary.BOX_SOURCE )
			paintBoxImg( gc, flowBG, GUIColors.BOX_textColor);
		else if ( boxType == IOPLibrary.BOX_MERGE )
			paintBoxImg( gc, flowBGMerge, GUIColors.BOX_textColor);
		else if ( boxType == IOPLibrary.BOX_ALPHA )
			paintBoxImg( gc, flowBGAlpha, GUIColors.BOX_textColor);
		else if ( boxType == IOPLibrary.BOX_MEDIA )
			paintBoxImg( gc, flowBGMedia, GUIColors.BOX_textColor);
		else
			paintBoxImg( gc, flowBGFilter, GUIColors.BOX_textColor);

		//--- normalSelected
		normalSelected = new BufferedImage( width, height, BufferedImage.TYPE_4BYTE_ABGR );
		gc = normalSelected.createGraphics();
		paintBoxImg( gc, flowBGSelected, GUIColors.BOX_textColor);

		//--- moving
		moving = new BufferedImage( width, height, BufferedImage.TYPE_4BYTE_ABGR  );
		gc = moving.createGraphics();
		paintBoxImg( gc, flowBGSelected, GUIColors.BOX_textColor);

		gc.dispose();
	}
	
	private void paintBoxImg( Graphics2D gc, BufferedImage bgImg, Color text )
	{
		gc.drawImage( bgImg, 0, 0, null );

		gc.setRenderingHint( 	RenderingHints.KEY_ANTIALIASING,
					RenderingHints.VALUE_ANTIALIAS_ON );
		drawConnectionPoints( gc );
		gc.setColor( text );
		gc.setRenderingHint( 	RenderingHints.KEY_ANTIALIASING,
					RenderingHints.VALUE_ANTIALIAS_OFF );
		drawName( gc );
	}

	//------------------------------------------------------------- GRAPHICS
	//--- Draws the box in correct state on given Graphics object.
	public void draw( Graphics g )
	{
		if( isMoving ) g.drawImage( moving, x, y, null );
		else if( isSelected ) g.drawImage( normalSelected, x, y, null );
		else g.drawImage( normal, x, y, null );
		
		if (renderNode.getImageOperation().getFileSource() != null )
		{
			if (renderNode.getImageOperation().getFileSource().hasResourceAvailable() == false)
			{
				 g.drawImage( noFileSource, x - 2, y - 5, null );
			}
			
		}
	}

	public Rectangle getArea()
	{
		return new Rectangle( x, y, width, height );
	}

	//--- Used when connection point active state is changed.
	public void redrawConnectionPoints()
	{
		Graphics2D g = normal.createGraphics();
		g.setRenderingHint( 	RenderingHints.KEY_ANTIALIASING,
					RenderingHints.VALUE_ANTIALIAS_ON );
		drawConnectionPoints( g );
		g = normalSelected.createGraphics();
		drawConnectionPoints( g );
		g.dispose();
	}
	
	//--- Used when prerendering box images.
	private void drawConnectionPoints( Graphics2D g )
	{
		for( FlowBoxConnectionPoint cp : connectionPoints ) cp.draw( g );
	}

	//--- Used when prerendering box images.
	private void drawName( Graphics2D g )
	{
		g.setFont( boxFont );
		Rectangle2D rect = new Rectangle2D.Float();

		rect.setRect(0, 0, 122, 30);
		g.clip(rect);
		g.drawString( name, NAME_DRAW_X, NAME_DRAW_Y );
	}

	//-------------------------------------------------- HIT TESTS	
	//--- Test if given point is in this boxes bounding box.
	public boolean pointInBoxArea( int testX, int testY )
	{
		if( testX >= x && testX <= (x + width ) && 
				testY >= y && testY <= ( y + height ) )return true;
		else return false;
	}

	//--- Tests if given area intersects box area.
	public boolean rectIntersectsBox( Rectangle rect )
	{
		return areaIntersectsBoxArea( rect.x,rect.y,rect.x + rect.width,rect.y + rect.height );
	}

	//--- Tests if given area intersects box area.
	public boolean areaIntersectsBoxArea( int x1, int y1, int x2, int y2 )
	{
		//--- Quarantee that x1 <= x2 and y1 <= y2 
		if( x1 > x2 )
		{
			int temp = x1;
			x1 = x2;
			x2 = temp;
		}
		if( y1 > y2 )
		{
			int temp = y1;
			y1 = y2;
			y2 = temp;
		}
		
		//--- Test box corners in area
		if ( x >= x1 && x <= x2 && y >= y1 && y <= y2 ) return true;
		if ( (x + width) >= x1 && (x + width) <= x2 && y >= y1 && y <= y2 ) return true;
		if ( (x + width) >= x1 && (x + width) <= x2 && (y  + height) >= y1 && (y + height ) <= y2 ) return true;
		if ( x >= x1 && x <= x2 && (y  + height ) >= y1 && (y + height ) <= y2 ) return true;
		
		//--- Test area corners in box.
		if ( pointInBoxArea( x1, y1 ) ) return true;
		if ( pointInBoxArea( x1, y2 ) ) return true;
		if ( pointInBoxArea( x2, y1 ) ) return true;
		if ( pointInBoxArea( x2, y2 ) ) return true;
		
		//--- Test lines intersect
		if( linesIntersect( x1, x2, y1, y, y + height, x ) ) return true;
		if( linesIntersect( x1, x2, y2, y, y + height, x ) ) return true;
		
		if( linesIntersect( x1, x2, y1, y, y + height, x + width ) ) return true;
		if( linesIntersect( x1, x2, y2, y, y + height, x  + width ) ) return true;
		
		if( linesIntersect( x, x + width, y, y1, y2, x1 ) ) return true;
		if( linesIntersect( x, x + width, y, y1, y2, x2 ) ) return true;
		
		if( linesIntersect( x, x + width, y + height, y1, y2, x1 ) ) return true;
		if( linesIntersect( x, x + width, y + height, y1, y2, x2 ) ) return true;
		
		//--- Areas don't intersect.
		return false;
	}	
	//--- Helper method for areaIntersectsBoxArea.
	//--- NOTE: Works only if X1 < X2 and Y1 < Y2
	private boolean linesIntersect( int horX1, int horX2, int horY,
				     int vertY1, int vertY2, int vertX )
	{
		if( vertX >= horX1 && vertX <= horX2 && horY >= vertY1 && horY<= vertY2 ) return true;
		return false;
	}

	//----------------------------------------------------------- ALPHABETIC SORTING
	public int compareTo( Object anotherBox )
	{
		int y2 = ((FlowBox) anotherBox ).getY();
		if( y2 == y ) return 0;
		if( y2 > y ) return -1;
		return 1;
	}

}//end class
