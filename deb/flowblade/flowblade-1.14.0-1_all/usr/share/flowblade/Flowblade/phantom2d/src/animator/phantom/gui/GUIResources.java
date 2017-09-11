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

import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import javax.swing.AbstractButton;
import javax.swing.ImageIcon;

import animator.phantom.controller.Application;


public class GUIResources
{
	//--- PATHS
	public static String resourcePath = Application.getResourcePath();

	//--- FONTS
	public static String FREE_SANS_PATH = resourcePath + "font/FreeSans.ttf";
	public static final Font BASIC_FONT_18 = new Font( "SansSerif", Font.PLAIN, 18 );
	public static final Font BASIC_FONT_14 = new Font( "SansSerif", Font.PLAIN, 14 );
	public static final Font BASIC_FONT_13 = new Font( "SansSerif", Font.PLAIN, 13 );
	public static final Font BASIC_FONT_12 = new Font( "SansSerif", Font.PLAIN, 12 );
	public static final Font BASIC_FONT_11 = new Font( "SansSerif", Font.PLAIN, 11 );
	public static final Font BASIC_FONT_10 = new Font( "SansSerif", Font.PLAIN, 10 );
	public static final Font BASIC_FONT_ITALIC_11 = new Font( "SansSerif", Font.PLAIN|Font.ITALIC, 11 );
	public static final Font BASIC_FONT_ITALIC_14 = new Font( "SansSerif", Font.PLAIN|Font.ITALIC, 14 );
	public static final Font BOLD_FONT_11 = new Font( "SansSerif", Font.BOLD, 11 );
	public static final Font BOLD_FONT_12 = new Font( "SansSerif", Font.BOLD, 12 );
	public static final Font BOLD_FONT_14 = new Font( "SansSerif", Font.BOLD, 14 );
	public static final Font BOLD_FONT_ITALIC_11 = new Font( "SansSerif", Font.BOLD|Font.ITALIC, 11 );
	public static final Font TC_FONT = new Font( "SansSerif", Font.BOLD, 15 );
	public static Font PARAM_EDIT_LABEL_FONT = BOLD_FONT_11;
	public static Font BIG_BUTTONS_FONT = BASIC_FONT_12;
	public static Font TOP_LEVEL_COMBO_FONT = BASIC_FONT_12;
	public static Font EDITOR_COLUMN_ITEM_FONT = new Font( "SansSerif", Font.PLAIN, 11 );

	//--- Bin area icons
	public static String bmicon = resourcePath + "bmicon.png";
	public static String bmseriesicon = resourcePath + "bmseriesicon.png";
	public static String movieicon = resourcePath + "movieicon.png";

	//--- Project buttons.
	public static String openMediaSmall = resourcePath + "openMediaSmall.png";
	public static String openFileSeqSmall = resourcePath + "openFileSeqSmall.png";
	public static String deleteFileSmall = resourcePath + "deleteFileSmall.png";

	//--- RenderFlowViewButtons
	public static String deleteBoxes = resourcePath + "deleteBoxes.png";
	public static String lineUpBoxes = resourcePath + "lineUpBoxes.png";
	public static String connectBoxes = resourcePath + "connectBoxes.png";
	public static String disConnectBoxes = resourcePath + "disConnectBoxes.png";
	public static String viewTargetInFlow = resourcePath + "viewTargetInFlow.png";
	public static String editTargetInFlow = resourcePath + "editTargetInFlow.png";
	public static String showGrid = resourcePath + "showGrid.png";

	//--- Buttons for IOP groups
 	public static String transformGroup = resourcePath + "transformGroup.png";

	//--- Key frame editor.
	public static String lTriActive = resourcePath + "lTriActive.png";
	public static String lTriActiveDark = resourcePath + "lTriActiveDark.png";
	public static String lTriActiveTheme = lTriActive;
	public static String lTriNotActive = resourcePath + "lTriNotActive.png";
	public static String rTriActive = resourcePath + "rTriActive.png";
	public static String rTriActiveDark = resourcePath + "rTriActiveDark.png";
	public static String rTriActiveTheme = rTriActive;
	public static String rTriNotActive = resourcePath + "rTriNotActive.png";
	public static String kfOn = resourcePath + "kfOn.png";
	public static String kfOff = resourcePath + "kfOff.png";
	public static String kfOffDark = resourcePath + "kfOffDark.png";
	public static String kfOffTheme = kfOff;
	public static String stepped = resourcePath + "stepped.png";

	public static String addClip = resourcePath + "addClip.png";
	public static String deleteClip = resourcePath + "deleteClip.png";
	public static String clipDown = resourcePath + "clipDown.png";
	public static String clipUp = resourcePath + "clipUp.png";

	//--- Timeline edit buttons.
	public static String clipOutToCurrent = resourcePath + "clipOutToCurrent.png";
	public static String clipInToCurrent = resourcePath + "clipInToCurrent.png";
	public static String clipTailToCurrent = resourcePath + "clipTailToCurrent.png";
	public static String clipHeadToCurrent = resourcePath + "clipHeadToCurrent.png";

	public static String zoomIn = resourcePath + "zoomOut.png";
	public static String zoomOut = resourcePath + "zoomIn.png";

	//--- TimeLine navi and preview buttons
	public static String toPreviousFrameNavi = resourcePath + "toPreviousFrameNavi.png";
	public static String toNextFrameNavi = resourcePath + "toNextFrameNavi.png";
	public static String play = resourcePath + "play.png";
	public static String pause = resourcePath + "pause.png";
	public static String renderPreview = resourcePath + "renderPreview.png";
	public static String renderLaunch = resourcePath + "renderLaunch.png";
	public static String loop = resourcePath + "loop.png";
	public static String loopPressed = resourcePath + "loopPressed.png";
	public static String stopPreviewRender = resourcePath + "stopPreviewRender.png";
	public static String trashPreviewRender = resourcePath + "trashPreview.png";
	public static String noPreview = resourcePath +	"noPreview.png";
	public static String lockIcon = resourcePath + "lockIcon.png";
	public static String noFileSource = resourcePath + "noFileSource.png";

	//--- Switches + source buttons
	public static String motionBlurLabel = resourcePath + "motionBlur.png";
	public static String alfaLineLabel = resourcePath + "alphaLine.png";
	public static String iris = resourcePath + "iris.png";
	public static String bilinear = resourcePath + "bilinear.png";
	public static String bicubic = resourcePath + "bicubic.png";
	public static String nearest = resourcePath + "nearest.png";
	public static String centerAnchor = resourcePath + "centerAnchor.png";
	public static String leafTrans  = resourcePath + "leafTrans.png";
	public static String leafTransPressed  = resourcePath + "leafTransPressed.png";
	public static String filterStack = resourcePath + "filterStack.png";
	public static String parentLabel = resourcePath + "parentLabel.png";
	public static String filterStackLabel = resourcePath + "filterStackLabel.png";

	//--- View editor
	public static String showViewEditUpdates = resourcePath + "showViewEditUpdates.png";
	public static String showViewEditUpdatesOff = resourcePath + "showViewEditUpdatesOff.png";
	public static String viewImagePressed = resourcePath + "viewImagePressed.png";
	public static String viewAlphaPressed = resourcePath + "viewAlphaPressed.png";
	public static String viewTarget = resourcePath + "viewTarget.png";
	public static String viewTargetPressed = resourcePath + "viewTargetPressed.png";
	public static String viewFlow = resourcePath + "viewFlow.png";
	public static String viewFlowPressed = resourcePath + "viewFlowPressed.png";
	public static String viewSelected = resourcePath + "viewSelected.png";
	public static String viewSelectedPressed = resourcePath + "viewSelectedPressed.png";
	//public static String viewLayer = resourcePath + "viewLayer.png";
	//public static String viewLayerPressed = resourcePath + "viewLayerPressed.png";
	public static String kfEdit = resourcePath + "kfEdit.png";
	public static String kfEditPressed = resourcePath + "kfEditPressed.png";
	public static String kfAdd = resourcePath + "kfAdd.png";
	public static String kfAddPressed = resourcePath + "kfAddPressed.png";
	public static String kfRemove = resourcePath + "kfRemove.png";
	public static String kfRemovePressed = resourcePath + "kfRemovePressed.png";
	public static String viewEditorLabel = resourcePath + "viewEditorLabel.png";
	public static String noViewEditorLabel = resourcePath + "noViewEditorLabel.png";
	public static String allBoxes = resourcePath + "allBoxes.png";
	public static String allBoxesPressed = resourcePath + "allBoxesPressed.png";
	public static String pickColor = resourcePath + "pickColor.png";
	public static String pickColorPressed = resourcePath + "pickColorPressed.png";
	public static String pickFGColor = resourcePath + "pickFGColor.png";
	public static String pickFGColorPressed = resourcePath + "pickFGColorPressed.png";
	public static String pickBGColor = resourcePath + "pickBGColor.png";
	public static String pickBGColorPressed = resourcePath + "pickBGColorPressed.png";
	public static String renderClock = resourcePath + "renderClock.png";
	public static String renderClockDark = resourcePath + "renderClockDark.png";
	public static String renderClockTheme = renderClock;
	public static String customButton = resourcePath + "custombutton.png";
	public static String customPressed = resourcePath + "custombuttonpressed.png";
	public static String panelSizes = resourcePath + "panelSizes.png";

	//--- View editor action buttons
	public static String move = resourcePath + "move.png";
	public static String movePressed = resourcePath + "movePressed.png";
	public static String rotate = resourcePath + "rotate.png";
	public static String rotatePressed = resourcePath + "rotatePressed.png";
	public static String layerUp = resourcePath + "layerUp.png";

	//--- KeyFrame editor
	public static String scaleZoomIn = resourcePath + "scaleZoomIn.png";
	public static String scaleZoomOut = resourcePath + "scaleZoomOut.png";
	public static String addKF = resourcePath + "addKF.png";
	public static String deleteKF = resourcePath + "deleteKF.png";

	public static String colorWheel = resourcePath + "colorWheel.png";

	//--- Flow edit
	public static String flowBoxBG = resourcePath + "flowboxbg.png";
	public static String flowBoxBGSelected = resourcePath + "flowboxbgselected.png";
	public static String flowBoxBGFilter = resourcePath + "flowboxbgfilter.png";
	public static String flowBoxBGMerge = resourcePath + "flowboxbgmerge.png";
	public static String flowBGAlpha = resourcePath + "flowboxbgalpha.png";
	public static String flowBGMedia = resourcePath + "flowboxbgmedia.png";
	//--- logo
	public static String phantomLogoSmall = resourcePath + "phantom_logo_color_tiny.png";

	//--- icon
	public static String phantomIcon = resourcePath + "phantom_icon.png";
	public static String folderIcon = resourcePath + "folder.png";
	public static String fileIcon = resourcePath + "file.png";

	//--- Value editor label
	public static final String keyFrameSmall = resourcePath + "keyFrameSmall.png";

	public static final String keyframeProperties = resourcePath + "keyframeProperties.png";

	public static final String keyframePropertiesDisabled  = resourcePath + "keyframePropertiesDisabled.png";

	//--- Dialogs
	public static final String gtk_dialog_warning = resourcePath + "gtk-dialog-warning.png";

 	public static final Dimension mediumButton = new Dimension( 27, 27 );
	public static String emptyIcon = resourcePath + "emptyIcon.png";
	public static String draggedNode = resourcePath + "draggedNode.png";

	public static ImageIcon getIcon( String path )
	{
		return new ImageIcon( path );
	}

	public static BufferedImage getResourceBufferedImage( String path )
	{
		return GUIUtils.getBufferedImageFromFile( new File( path ) );
	}

	public static Font getFont( String path )
	{
		Font font = null;

		try {
			InputStream is = null;
			is = new FileInputStream( path );
			font = Font.createFont(Font.TRUETYPE_FONT, is);
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			System.err.println( path + " font was not loaded.");
		}
		return font;
	}

	public static void prepareMediumButton( AbstractButton button,
						ActionListener listener,
						String toolTipText )
	{
		button.addActionListener( listener );
		button.setToolTipText( toolTipText );
		setButtonSizeMediumButton( button );
	}

	public static void prepareMediumMediumButton( AbstractButton button,
						ActionListener listener,
						String toolTipText )
	{
		button.addActionListener( listener );
		button.setToolTipText( toolTipText );
		setButtonSizeMediumButton( button );
		Dimension medWide = new Dimension( 47, 27 );
		button.setPreferredSize( medWide );
		button.setMaximumSize( medWide );
	}

	public static void setButtonSizeMediumButton( AbstractButton button )
	{
		button.setPreferredSize( mediumButton );
		button.setMaximumSize( mediumButton );
	}

}//end class
