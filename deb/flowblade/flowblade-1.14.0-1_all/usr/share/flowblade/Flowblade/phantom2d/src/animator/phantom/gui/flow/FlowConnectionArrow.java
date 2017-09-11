package animator.phantom.gui.flow;

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

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;

import animator.phantom.gui.GUIColors;

//--- An arrow that connects two FlowBoxes by their connection points.
public class FlowConnectionArrow implements FlowGraphic
{
	//--- The flowbox from where arrow begins 
	FlowBox sourceBox;
	//--- The flowbox from where arrow ends 
	FlowBox targetBox;
	//--- Source connection point in sourceBox
	FlowBoxConnectionPoint sourceCP;
	//--- Source connection point in targetBox
	FlowBoxConnectionPoint targetCP;
		
	//--- Positions of arrow's ends.
	private int startX;//in sourceBox
	private int startY;
	private int endX;//in targetBix
	private int endY;
	
	//--- Arrow is legal if its head is below its tail
	private boolean arrowIsLegal = true;

	//--- Arrows colors
	private static Color LEGAL = GUIColors.ARROW_LEGAL;
	private static Color ILLEGAL = GUIColors.ARROW_ILLEGAL;
	private static Color MOVING = GUIColors.ARROW_LEGAL;

	//--- Moving state
	private boolean isMoving = false;

	//----------------------------------------------- CONSTRUCTOR
	public FlowConnectionArrow( FlowBox sourceBox, FlowBox targetBox )
	{
		this.sourceBox = sourceBox;
		this.targetBox = targetBox;
	}
	
	public void setSourceBox( FlowBox sourceBox ){ this.sourceBox = sourceBox; }
	public FlowBox getSourceBox(){ return sourceBox; }
	public void setTargetBox( FlowBox targetBox ){ this.targetBox = targetBox; }
	public FlowBox getTargetBox(){ return targetBox; }	
	public FlowBoxConnectionPoint getSourceCP(){ return sourceCP; }
	public FlowBoxConnectionPoint getTargetCP(){ return targetCP; }
	public boolean isLegal(){ return arrowIsLegal; }
	public void setIsMoving( boolean value ){ isMoving = value; }
	
	public void setStartPos( int x, int y )
	{
		startX = x;
		startY = y;
		checkDirectionLegality();
	}
	
	public void setEndPos( int x, int y )
	{
		endX = x;
		endY = y;
		checkDirectionLegality();
	}

	public void setConnectionPoints( FlowBoxConnectionPoint sourceCP,
						FlowBoxConnectionPoint targetCP )
	{
		this.sourceCP = sourceCP;
		this.targetCP = targetCP;
	}

	public void setConnectionPointsSelected( boolean value )
	{
		sourceCP.setActive( value );
		targetCP.setActive( value );
	}
	
	//--- Set start and end position based boxes and connection points.
	public void updatePosition()
	{
		setStartPos( sourceBox.getX() + sourceCP.getX() - 1 + FlowBoxConnectionPoint.ARROW_OFFSET,
			sourceBox.getY() + sourceCP.getY() + 2 + FlowBoxConnectionPoint.ARROW_OFFSET );
		
		setEndPos( targetBox.getX() + targetCP.getX() +  FlowBoxConnectionPoint.ARROW_OFFSET,
			targetBox.getY() + targetCP.getY() +  - 4 + FlowBoxConnectionPoint.ARROW_OFFSET );
	}

	//Arrows head must always be lower than it's tail.
	private void checkDirectionLegality()
	{
		if( endY <= startY ) arrowIsLegal = false;
		else arrowIsLegal = true;
	}

	//---------------------------------------- GRAPHICS
	public void draw( Graphics gr )
	{
		Graphics2D g = ( Graphics2D ) gr;
		g.setRenderingHint( 	RenderingHints.KEY_ANTIALIASING,
					RenderingHints.VALUE_ANTIALIAS_ON );
		g.setStroke( new BasicStroke( 2 ) );
		if( isMoving ) g.setColor( MOVING );
		else if( arrowIsLegal ) g.setColor( LEGAL );
		else g.setColor( ILLEGAL );

		g.drawLine( startX, startY, endX, endY );
	}

	public Rectangle getArea()
	{
		int rx;
		int ry;
		int rwidth;
		int rheight;
		
		if( startX < endX ) rx = startX;
		else rx = endX;
		
		if( startY < endY ) ry = startY;
		else ry = endY;
	
		rwidth = Math.abs( startX - endX );
		rheight = Math.abs( startY - endY );
	
		return new Rectangle( rx, ry, rwidth, rheight );
	}

}//end class