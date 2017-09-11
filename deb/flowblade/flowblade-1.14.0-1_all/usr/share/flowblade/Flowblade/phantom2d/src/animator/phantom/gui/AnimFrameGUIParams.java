package animator.phantom.gui;

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

import java.awt.Toolkit;

import animator.phantom.controller.GUIComponents;

//--- Some draw params for AninmatorFrame
//--- This could maybe be re-factored somehow 
public class AnimFrameGUIParams
{
	//--- FRAME DRAW PARAMS
	public static int OUT_BORDER_WIDTH = 10;

	//--- TIME EDITORS DRAW PARAMS
	public static int TE_ROW_HEIGHT = 26;
	public static int TE_LEFT_COLUMN_WIDTH = 310;
	private static int TE_RIGHT_COLUMN_INSET = 55;
	public static int TE_SMALL_WINDOW_INSET = 315;
	public static int TE_SCALE_DISPLAY_HEIGHT = 22;
	public static int TE_HEIGHT_PAD = 25;
	public static int TE_WIDTH_PAD = 0;

	//--- Calculates right column based on screen size.
	public static int getTimeEditRightColWidth()
	{
		if( GUIComponents.timeLineEditorPanel == null || ( GUIComponents.timeLineEditorPanel.getWidth() == 0 ))
		{
			// HACK!
			// We need some sane value before GUIComponents.timeLineEditorPanel is available
			// and layed out properly. It is also not visible on startup, so there won't be proper values 
			// available until it is displayed at least once.
			//
			//numbers here are just tested to work on dev system
			return Toolkit.getDefaultToolkit().getScreenSize().width
				- TE_SMALL_WINDOW_INSET
				- TE_LEFT_COLUMN_WIDTH
				- TE_RIGHT_COLUMN_INSET
				- 168;
		}
		else
			return GUIComponents.timeLineEditorPanel.getWidth() - 2;
	}

}//end class