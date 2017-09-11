package animator.phantom.gui.keyframe;

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
import java.awt.Graphics2D;

import animator.phantom.gui.AnimFrameGUIParams;
import animator.phantom.gui.GUIColors;
import animator.phantom.gui.GUIResources;
import animator.phantom.renderer.param.KeyFrameParam;
import animator.phantom.renderer.param.Param;

//--- Represenst area in Keyframe editor left side column used to select the parameter being edited
public class KFParamBox
{
	//--- Param that has its keyframes edited here.
	private KeyFrameParam param;
	private Param p;
	private String paramName;
	//--- Selected state 
	private boolean isSelected = false;
	//--- GUI parameters
	private static final int NAME_DRAW_X = 40;
	private static final int NAME_DRAW_Y = 15;
	//--- Font
	private static Font boxFont = GUIResources.EDITOR_COLUMN_ITEM_FONT;

	public KFParamBox( Param p )
	{
		this.p = p;
		this.param = (KeyFrameParam) p;
		paramName = p.getParamName();
	}

	public void setSelected( boolean value ){ isSelected = value; }
	public boolean isSelected(){ return isSelected; }
	public KeyFrameParam getParam(){ return param; }
	public Param getParamAsParam(){ return p; }//nice going

	//--------------------------------------------- GRAPHICS
	public void paint( Graphics g1, int x, int y )
	{
		Graphics2D g = (Graphics2D) g1;

		int rowHeight = AnimFrameGUIParams.TE_ROW_HEIGHT;
		int leftColumn = AnimFrameGUIParams.TE_LEFT_COLUMN_WIDTH;

		System.out.println("dd");
		//--- Draw bg for selected boxes.
		if( isSelected )
		{
			System.out.println("selected");
			g.setColor( GUIColors.MEDIA_ITEM_SELECTED_BG );
			g.fillRect( x,y, leftColumn, rowHeight + 1 );
		}
		//--- Draw lines.
		g.setColor( GUIColors.lineBorderColor );
		g.drawLine( x, y, x + leftColumn, y );
		g.drawLine( x, y, x, y + rowHeight +1 );
		g.drawLine( x + leftColumn, y, x + leftColumn, y + rowHeight + 1 );

		//--- Draw text
		g.setFont( boxFont );
		g.setColor( GUIColors.timeLineFontColor );
		g.drawString( paramName, NAME_DRAW_X, y + NAME_DRAW_Y );
	}

}//end class