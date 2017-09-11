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
import java.awt.Graphics2D;
import java.awt.Polygon;

//--- FlowBoxConnectionPoint is a triangle area into whitch 
//--- FlowConnectionArrows can be attached to.
public class FlowBoxConnectionPoint
{
	//--- The FlowBox that this connectionpoint belongs to. 
	FlowBox parentBox;

	//--- The place of FlowBoxConnectionPoint
	private int x;
	private int y;
	
	//--- The type of this connection box
	private int type;
	private boolean isMaskInput = false;

	//--- The index of source or target in sources or targets vectors
	//--- in RenderNode that parent FlowBox represents.
	private int index;

	//--- The connection arrow that is connected to this 
	private FlowConnectionArrow arrow = null;

	//--- Active state
	private boolean isActive = false;

	//--- The size of FlowBoxConnectionPoint
	public static final int WIDTH = 6;
	public static final int HEIGHT = 6;
	public static final int ARROW_OFFSET = 3;
	
	//--- normal inputs
	private static final int[] xpoints = { 0,6,3 };
	private static final int[] ypoints = { 0,0,6 };
	
	//--- mask input
	private static final int[] xMpoints = { 6,6,0 };
	private static final int[] yMpoints = { 0,6,3 };

	private int[] xp = { 0,0,0 };
	private int[] yp = { 0,0,0 };
	
	//--- The possible types of a connection point.
	//---- INPUT to box and OUTPUT from box.
	public static final int INPUT = 1;
	public static final int OUTPUT = 2;

	private static final Color activeColor = Color.BLUE;
	private static final Color maskInputColor = Color.black;// new Color( 129, 160, 234 );
	private static final Color firstInputColor = Color.yellow;
	private static final Color otherInputColor = Color.yellow;
	private static final Color outputColor = Color.white;

	private Polygon shape;

	public FlowBoxConnectionPoint( FlowBox parentBox, int type,  int index )
	{
		this.parentBox = parentBox;
		this.type = type;
		this.index = index;
	}

	public int getType(){ return type; }
	public void setAsMaskInput( boolean val ){ isMaskInput = val; }
	public boolean isMaskInput(){ return isMaskInput; }
	public int getX(){ return x; }
	public int getY(){ return y; }
	public int getIndex(){ return index; }
	public void setPos( int x, int y )
	{
		this.x = x;
		this.y = y;
	}
	public void setArrow( FlowConnectionArrow arrow )  { this.arrow = arrow; }
	public FlowConnectionArrow getArrow(){ return arrow; }
	public FlowBox getParentBox(){ return parentBox; }
	public void setActive( boolean activeState ){ isActive = activeState; }
	public boolean isActive(){ return isActive; }

	private void translatePoints( int[] xpt, int[] ypt  )
	{
		xp[ 0 ] = xpt[ 0 ] + x;
		xp[ 1 ] = xpt[ 1 ] + x;
		xp[ 2 ] = xpt[ 2 ] + x;

		yp[ 0 ] = ypt[ 0 ] + y;
		yp[ 1 ] = ypt[ 1 ] + y;
		yp[ 2 ] = ypt[ 2 ] + y;
	}

	//--------------------------------------------- GRAPHICS
	public void draw( Graphics2D g2 )
	{

		if( isActive) g2.setColor( activeColor );
		else if( type == OUTPUT ) g2.setColor( outputColor );
		else if( isMaskInput ) g2.setColor( maskInputColor );
		else if( index == 0 ) g2.setColor( firstInputColor );
		else g2.setColor( otherInputColor );

		if( isMaskInput ) translatePoints( xMpoints, yMpoints );
		else translatePoints( xpoints, ypoints );
		shape = new  Polygon( xp, yp, 3 );
		g2.fill( shape );
	}
	
	//--- Used to redraw into parent box when active state has changed.
	public void redrawIntoParentBox()
	{
		parentBox.redrawConnectionPoints();
	}

}//end class






