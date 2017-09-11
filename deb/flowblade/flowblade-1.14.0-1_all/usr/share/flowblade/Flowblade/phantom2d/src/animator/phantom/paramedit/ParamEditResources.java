package animator.phantom.paramedit;

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

import java.awt.Dimension;

/**
* GUI constants for drawing parameter editor components.
*/
public class ParamEditResources
{
	/**
	* Width of a columns, label column or editor column.
	*/
	public static int PARAM_COLUMN_WIDTH = 148;
	/**
	* Height of editor component row.
	*/
	public static int PARAM_ROW_HEIGHT = 22;
	/**
	* Size of half of editor row.
	*/	
	public static Dimension EDIT_ROW_HALF_SIZE = new Dimension( PARAM_COLUMN_WIDTH, PARAM_ROW_HEIGHT);
	/**
	* Size of an editor row.
	*/
	public static Dimension EDIT_ROW_SIZE = new Dimension(PARAM_COLUMN_WIDTH * 2, PARAM_ROW_HEIGHT );
	/**
	* Size of an editor row with slider editor component.
	*/
	public static Dimension EDIT_SLIDER_ROW_SIZE = new Dimension(PARAM_COLUMN_WIDTH*2,PARAM_ROW_HEIGHT*3 - 25);
	/**
	* Gap on top of the top most editor row in tabbed parameter editor panel.
	*/
	public static int TABS_TOP_GAP = 6;
	/**
	* Gap between label and editor.
	*/
	public static int PARAM_MID_GAP = 4;
// 
}//end class