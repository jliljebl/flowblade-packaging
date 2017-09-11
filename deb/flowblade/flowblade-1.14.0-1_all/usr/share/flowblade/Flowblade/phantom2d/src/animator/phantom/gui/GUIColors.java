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

import java.awt.Color;

import javax.swing.JPanel;
import javax.swing.plaf.ColorUIResource;

//--- Collection of GUI clor definitions
public class GUIColors
{
	//--- Panel colors.
	public static Color bgColor = ( new JPanel() ).getBackground();
	public static Color darkBgColor = new Color( 56, 60, 74 );
	public static Color darker = new ColorUIResource( new Color( 51, 55, 69 ) );
	public static Color selectedColor = new Color( 169, 192, 205 );
	public static Color notSelectedColor = ( new JPanel() ).getBackground();
	public static Color lineBorderColor = new Color( 169, 192, 205 );
	public static Color frameBorder = selectedColor;
	public static Color grayTitle = new Color( 120, 120, 120 );

	//--- Flow Editor
	public static Color flowBGColor = new Color( 56, 60, 74 );// new Color( 166, 166, 166 );
	public static Color flowGridColor = new Color( 52, 52, 52 );// new Color( 166, 166, 166 );
	
	//--- View Editor
	public static Color viewEditorBGColor = new Color( 128, 128, 128 );// new Color( 166, 166, 166 );
	
	//--- FlowBox
	public static Color BOX_textColor = Color.WHITE;

	//--- FloWConnectionArrow
	public static Color ARROW_LEGAL = Color.WHITE;
	public static Color ARROW_ILLEGAL = Color.WHITE;

	//--- Bin
	public static Color BIN_BG = bgColor;
	public static Color MEDIA_ITEM_TEXT_COLOR = Color.black;
	public static Color MEDIA_ITEM_SELECTED_BG = GUIColors.selectedColor;
	public static Color MEDIA_ITEM_BG = bgColor;
	public static Color THUMB_BORDER = selectedColor;

	//--- Param Editor
	public static Color PARAM_EDIT_NAME = Color.black;
	public static Color SEPARATOR_BG = GUIColors.bgColor;
	public static Color SEPARATOR_LINE = GUIColors.selectedColor;
	public static Color filterStackColor = new Color(210, 212, 60 );

	//--- Timeline editor colors.
	public static Color clipEditorBGColor = darkBgColor;
	public static Color movingClipColor =  new Color( 185, 227, 247 );
	public static Color clipOutLineColor = new Color( 110, 110, 110 );
	public static Color trackLineColor = clipEditorBGColor;
	public static Color timeLineScaleColor = Color.white;
	public static Color timeLineColumnColor = GUIColors.notSelectedColor;
	public static Color timeLineFontColor = Color.black;
	public static Color sourceClipColor = new Color( 149, 152, 79 );
	public static Color mergeClipColor = new Color( 59, 89, 91 );
	public static Color filterClipColor = new Color( 119, 123, 190 );
	public static Color alphaClipColor = new Color( 50, 50, 50 );
	public static Color mediaClipColor = new Color( 149, 83, 131 );

	//--- Keyframe editor
	public static Color KF_LINES_COLOR = Color.gray;
	public static Color KF_NUMBERS_COLOR = Color.white;
	public static Color KF_COLOR = new Color( 220, 220, 220 );
	public static Color KF_FOCUS_COLOR = new Color( 115, 143, 212 );//Color.blue;//Color.yellow;
	public static Color KF_VALUE_COLOR = Color.yellow;//Color.white;//new Color( 220, 220, 220 );//Color.blue;
	public static Color KF_BEZ_VALUE_COLOR = Color.orange;
	public static Color KF_NON_CLIP_COLOR = new Color( 100, 100, 100 );//new Color( 121, 39, 39 );

}//end class
