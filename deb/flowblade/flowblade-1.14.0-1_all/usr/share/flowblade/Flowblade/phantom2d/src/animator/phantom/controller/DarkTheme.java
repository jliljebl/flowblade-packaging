package animator.phantom.controller;
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
import java.awt.Font;
import java.util.Arrays;
import java.util.List;

import javax.swing.UIDefaults;
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.FontUIResource;
import javax.swing.plaf.IconUIResource;
import javax.swing.plaf.InsetsUIResource;
import javax.swing.plaf.metal.OceanTheme;

import animator.phantom.gui.CurrentThemeButtonFactory;
import animator.phantom.gui.GUIColors;
import animator.phantom.gui.GUIResources;
import animator.phantom.gui.PHButtonFactory;

public class DarkTheme extends OceanTheme
{
	public static Font MENU_FONT = null;

	public static ColorUIResource darkBg = new ColorUIResource( new Color( 56, 60, 74 ) );
	public static ColorUIResource darkBgLighter = new ColorUIResource( new Color( 67, 75, 79 ) );
	public static ColorUIResource darker = new ColorUIResource( new Color( 51, 55, 69 ) );//new Color( 63,64,61 ) );
	public static ColorUIResource dark = new ColorUIResource( new Color( 56, 60, 74 )  );
	public static ColorUIResource middark = new ColorUIResource( new Color( 110,110,110 ) );
	public static ColorUIResource lighter = new ColorUIResource( new Color( 71, 75, 90 ) );//new Color( 80,80,90 ) );
	public static ColorUIResource midlight = new ColorUIResource( new Color( 170,170,170 ) );
	public static ColorUIResource light = new ColorUIResource( new Color( 200,200,200 ) );
	public static ColorUIResource lightest = new ColorUIResource( new Color( 245,245,245 ) );
	public static ColorUIResource phantomOrange = new ColorUIResource( new Color( 52, 80, 132 ) );
	public static ColorUIResource phantomOrangeLight = new ColorUIResource( new Color( 62, 90, 142 ) );
	public static ColorUIResource black = new ColorUIResource( Color.black );


	static
	{
		Font freeSans = GUIResources.getFont( GUIResources.FREE_SANS_PATH );
		if( freeSans != null )
			MENU_FONT = freeSans.deriveFont( Font.BOLD, 13.0f );
		else
			MENU_FONT = GUIResources.BASIC_FONT_13;

		GUIColors.MEDIA_ITEM_TEXT_COLOR = light;
		GUIColors.MEDIA_ITEM_SELECTED_BG = new Color( 48, 52, 65 );
		GUIColors.MEDIA_ITEM_BG = dark;
		GUIColors.lineBorderColor = new Color( 48, 52, 65 );
		GUIColors.SEPARATOR_BG = dark;
		GUIColors.SEPARATOR_LINE = darkBg;
		GUIColors.timeLineColumnColor = dark;
		GUIColors.timeLineFontColor = light;
		GUIColors.THUMB_BORDER = dark;
		GUIColors.frameBorder = darkBg;

		GUIResources.BIG_BUTTONS_FONT = MENU_FONT;
		GUIResources.TOP_LEVEL_COMBO_FONT = MENU_FONT;
		GUIResources.lTriActiveTheme = GUIResources.lTriActiveDark;
		GUIResources.rTriActiveTheme = GUIResources.rTriActiveDark;
		GUIResources.kfOffTheme = GUIResources.kfOffDark;
		GUIResources.renderClockTheme = GUIResources.renderClockDark;


		PHButtonFactory.factoryImpl = new CurrentThemeButtonFactory();
	}

	//--- Fonts
	private final FontUIResource menuFont = new FontUIResource( MENU_FONT );
	public FontUIResource getMenuTextFont() { return menuFont;}

	//--- Colors
	public ColorUIResource getControl(){ return dark; }
	public ColorUIResource getSystemTextColor(){ return light; }
	public ColorUIResource getWindowBackground()  { return darkBg; }

	public ColorUIResource getWhite() { return midlight; }
	public ColorUIResource getBlack() { return light; }

	public ColorUIResource getControlTextColor() { return light; }
	public ColorUIResource getControlHighlight(){ return lighter; }
	public ColorUIResource getControlShadow() { return darker; }
	public ColorUIResource getControlDarkShadow(){ return black; }

	public ColorUIResource getPrimaryControl() { return dark; }
	public ColorUIResource getPrimaryControlHighlight() { return lighter; }
	public ColorUIResource getPrimaryControlShadow() { return middark; } // non-selected tabs
	public ColorUIResource getPrimaryControlInfo() { return light; }

	public ColorUIResource getPrimary1() { return black; }
	public ColorUIResource getPrimary2() { return darkBg; }
	public ColorUIResource getPrimary3() { return darkBgLighter; }

	public ColorUIResource getSecondary2() { return dark; }

	public ColorUIResource getMenuBackground(){ return dark; }
	public ColorUIResource getMenuForeground(){ return light; }
	public ColorUIResource getMenuDisabledForeground(){ return light; }
	public ColorUIResource getMenuSelectedForeground() { return lightest; }
	public ColorUIResource getAcceleratorForeground() { return light; }
	public ColorUIResource getSeparatorBackground(){ return darker; }
	public ColorUIResource getSeparatorForeground() { return middark; }

	public void addCustomEntriesToTable(UIDefaults table)
	{
		super.addCustomEntriesToTable( table );

		Object toglleui = table.get("ToggleButtonUI");

		List<Object> buttonGradient = Arrays.asList(new Object[] { new Float(1f),
			new Float(0.7f), lighter, dark,
			dark });

		List<Object> darkerButtonGradient = Arrays.asList(new Object[] { new Float(1f),
			new Float(0.7f), lighter, darkBg,
			darkBg });

		List<Object> scrollBarGradient = Arrays.asList(new Object[] {
			new Float(1f), new Float(.7f), phantomOrangeLight, phantomOrange,
			phantomOrange});

		List<Object> sliderGradient = Arrays.asList(new Object[] {
			new Float(1f), new Float(.7f), phantomOrangeLight, phantomOrange,
			phantomOrange});

		List<Object> menuBarGradient = Arrays.asList(new Object[] { new Float(1f),
			new Float(0f), dark, dark,
			dark });

		Object[] myDefaults = new Object[] {
		"Button.gradient",
		buttonGradient,
		"Button.background",
		darker,

		"ToggleButtonUI", "animator.phantom.controller.DarkToggleUI",
		"ToggleButton.textShiftOffset", new Integer( 0 ),
		"ToggleButton.background",
		darker,
		"ToggleButton.margin",
		darker,
		"ToggleButton.gradient",
		buttonGradient,

		"ComboBox.background",
		darkBg,
		"ComboBox.selectionBackground",
		darkBgLighter,

		"CheckBox.gradient",
		darkerButtonGradient,

		"RadioButton.gradient",
		darkerButtonGradient,

                "ScrollBar.gradient",
                scrollBarGradient,

                "TabbedPane.borderHightlightColor",
                darker,
                "TabbedPane.contentAreaColor",
		dark,
		"TabbedPane.tabAreaBackground",
		dark,
		"TabbedPane.unselectedBackground",
		darker,
		"TabbedPane.selected",
		dark,

		"MenuBar.gradient",
		menuBarGradient,
		"MenuBar.borderColor",
		darkBg,

		"Menu.selectionBackground",
		darkBg,

		"Slider.altTrackColor",
		phantomOrangeLight,
		"Slider.gradient",
		sliderGradient,
		"Slider.focusGradient",
		sliderGradient,

		"FileView.directoryIcon",
		new IconUIResource( GUIResources.getIcon( GUIResources.folderIcon ) ),
		"FileView.fileIcon",
		new IconUIResource( GUIResources.getIcon( GUIResources.fileIcon ) ),

		"TextField.margin",
		new InsetsUIResource(0, 2, 0, 0)
		};
		table.putDefaults( myDefaults );
	}

}//end class
