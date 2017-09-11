package animator.phantom.gui.timeline;

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

import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import animator.phantom.controller.ParamEditController;
import animator.phantom.controller.TimeLineController;
import animator.phantom.gui.AnimFrameGUIParams;
import animator.phantom.gui.GUIColors;
import animator.phantom.gui.GUIResources;
import animator.phantom.renderer.ImageOperation;

public class TimeLineIOPBox
{
	//--- Image opearation that this gui component represents.
	private ImageOperation iop;
	//--- GUI parameters
	private static final int NAME_DRAW_X = 15;
	private static final int NAME_DRAW_Y = 15;
	private static final int LOCK_DRAW_X = 3;
	private static final int LOCK_DRAW_Y = 4;
	private static final int EDIT_DRAW_X = 157; 
	private static final int EDIT_DRAW_Y = 4;

	private static final BufferedImage lockIcon = GUIResources.getResourceBufferedImage( GUIResources.lockIcon );
	private static final BufferedImage editIcon = GUIResources.getResourceBufferedImage( GUIResources.editTargetInFlow );

	//--- Font
	private static Font boxFont = GUIResources.EDITOR_COLUMN_ITEM_FONT;
	
	public TimeLineIOPBox( ImageOperation iop )
	{
		this.iop = iop;
	}
	
	public ImageOperation getIop(){ return iop; }

	public void paint( Graphics g, int x, int y )
	{
		int rowHeight = AnimFrameGUIParams.TE_ROW_HEIGHT;
		int leftColumn = AnimFrameGUIParams.TE_LEFT_COLUMN_WIDTH;

		//--- Draw bg for selected boxes.
		boolean selected = TimeLineController.clipForIopIsSelected( iop );
		if( selected )
		{
			g.setColor( GUIColors.MEDIA_ITEM_SELECTED_BG );
			g.fillRect( x,y, leftColumn, rowHeight + 1 );
		}
		//--- Draw lines.
		g.setColor( GUIColors.lineBorderColor );
		g.drawLine( x, y, x + leftColumn, y );
		g.drawLine( x, y, x, y + rowHeight +1 );
		g.drawLine( x + leftColumn, y, x + leftColumn, y + rowHeight + 1 );

		//--- Draw lock
		if( iop.getLocked() )
			g.drawImage( lockIcon, LOCK_DRAW_X, y + LOCK_DRAW_Y, null );

		//--- Draw text
		g.setFont( boxFont );
		g.setColor( GUIColors.timeLineFontColor );
		g.drawString( iop.getName(), NAME_DRAW_X, y + NAME_DRAW_Y );

		//--- FilterStackController holds ref to edit target iop
		if( ParamEditController.getEditTarget() == iop )
			g.drawImage( editIcon, EDIT_DRAW_X, y + EDIT_DRAW_Y, null );
	}
	
}//end class