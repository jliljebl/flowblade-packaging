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

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;

//--- GUI component that represent the selection box in FlowEditPanel
public class FlowSelectionBox implements FlowGraphic
{
	private int startX = 0;  // where mouse first pressed
	private int startY = 0;
	private int endX = 0;  // where dragged to or released
	private int endY = 0;

	public FlowSelectionBox(){}

	//--- Where mouse first pressed
	public void setStartPos( int x, int y )
	{
		startX = x;
		startY = y;

		endX = x;  // where dragged to or released
		endY = y;
	}
	//--- Where dragged to or released
	public void setEndPos( int x, int y )
	{
		endX = x;  // where dragged to or released
		endY = y;
	}
	
	public void draw( Graphics g )
	{
		g.setColor( Color.LIGHT_GRAY );
		//--- quadr. 1
		if( startX < endX&& startY < endY) 
			g.drawRect( startX, startY, endX- startX, endY - startY);
		//--- quadr. 2
		else if( startX < endX&& startY > endY)
			g.drawRect( startX, endY, endX- startX, startY - endY);
		//--- quadr. 3
		else if( startX > endX&& startY > endY)
			g.drawRect( endX, endY, startX - endX, startY - endY);
		//--- quadr. 4
		else g.drawRect( endX, startY, startX - endX, endY - startY);
	}

	//--- Returns area of selection.
	public Rectangle getArea()
	{ 

		//--- quadr. 1
		if( startX < endX&& startY < endY) 
			return new Rectangle( startX, startY, endX- startX, endY - startY);
		//--- quadr. 2
		else if( startX < endX&& startY > endY) 
			return new Rectangle( startX, endY, endX- startX, startY - endY);
		//--- quadr. 3
		else if( startX > endX&& startY > endY) 
			return new Rectangle( endX, endY, startX - endX, startY - endY);
		//--- quadr. 4
		else return new Rectangle( endX, startY, startX - endX, endY - startY);
	}
	
}//end class