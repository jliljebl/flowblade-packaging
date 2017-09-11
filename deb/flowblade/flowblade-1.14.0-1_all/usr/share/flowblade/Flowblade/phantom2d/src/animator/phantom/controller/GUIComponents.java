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

import java.util.Vector;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import animator.phantom.gui.AnimatorFrame;
import animator.phantom.gui.MenuBarCallbackInterface;
import animator.phantom.gui.NodesPanel;
import animator.phantom.gui.flow.FlowEditPanel;
//import animator.phantom.gui.flow.RenderFlowViewButtons;
import animator.phantom.gui.keyframe.KFColumnPanel;
import animator.phantom.gui.keyframe.KFToolButtons;
import animator.phantom.gui.keyframe.KeyFrameEditorPanel;
import animator.phantom.gui.modals.FilterStackEdit;
import animator.phantom.gui.modals.render.RenderWindow;
import animator.phantom.gui.preview.PreViewControlPanel;
import animator.phantom.gui.preview.PreViewUpdater;
import animator.phantom.gui.timeline.TCDisplay;
import animator.phantom.gui.timeline.TimeLineControls;
import animator.phantom.gui.timeline.TimeLineDisplayPanel;
import animator.phantom.gui.timeline.TimeLineEditorPanel;
import animator.phantom.gui.timeline.TimeLineIOPColumnPanel;
import animator.phantom.gui.timeline.TimeLineIOPColumnPanel2;
import animator.phantom.gui.view.component.ViewControlButtons;
import animator.phantom.gui.view.component.ViewEditor;
import animator.phantom.gui.view.component.ViewSizeSelector;
import animator.phantom.paramedit.FilterStackPanel;

//--- Class to hold references to all GUI components.
public class GUIComponents
{
	//--- GUI COMPONENTS, GUI COMPONENTS, GUI COMPONENTS, GUI COMPONENTS, GUI COMPONENTS
	//--- The editor frame.
	public static AnimatorFrame animatorFrame;
	public static MenuBarCallbackInterface animatorMenu;
	//--- GUI component used to edit composition.
	public static FlowEditPanel renderFlowPanel;
	//--- GUI component for editing mainly image positions on movie screen.
	public static ViewEditor viewEditor;
	//--- GUI component for buttons used to control render flow
	//public static RenderFlowViewButtons renderFlowButtons;
	//--- GUI component that displays names of components in timeline editing.
	public static TimeLineIOPColumnPanel2 timeLineIOPColumnPanel;
	//--- GUI component for doing timeline editing
	public static TimeLineEditorPanel timeLineEditorPanel;
	//--- Gui components (2) for displaying timeline scale
	public static Vector<TimeLineDisplayPanel> timeLineScaleDisplays
						= new Vector<TimeLineDisplayPanel>();
	//--- Gui components for timecode display.
	public static TCDisplay tcDisplay;
	//--- Gui component for selecting params for key frame editing.
	public static KFColumnPanel kfColumnPanel;
 	//--- Gui component for editing keyframes
	public static KeyFrameEditorPanel keyFrameEditPanel;
	//--- Panel that displays the name of IOP currently edited in keyframe editor.
	public static JPanel kfNamePanel;
	public static KFToolButtons kfControl;
	//--- Gui component for managin bins of project.
 	//public static BinsAreaPanel projectPanel;
	//--- Preview
	public static PreViewUpdater previewUpdater;
	public static PreViewControlPanel previewControls;
	//--- Static view control buttons.
	public static ViewControlButtons viewControlButtons;
	public static ViewSizeSelector viewSizeSelector;
	//--- toplevel for rendering info and progrewss
	public static RenderWindow renderWindow;
	//--- Top level for editing pre-transform filter stack
	public static FilterStackEdit filterStackEdit;
	//--- KEyframe editors container
	public static JPanel keyEditorContainerPanel;
	//--- View editor scroll pane
	public static JScrollPane viewScrollPane;
	//---
	public static FilterStackPanel filterStackPanel;

	//---
	public static TimeLineControls tlineControls;
	public static NodesPanel nodesPanel;
	public static JLabel projectInfoLabel;
	public static JScrollPane filterStackTablePane;

	public static void reset()
	{
		animatorMenu = null;
		renderFlowPanel = null;
		viewEditor = null;
		//renderFlowButtons = null;
		ParamEditController.paramEditFrame = null;
		timeLineIOPColumnPanel = null;
		timeLineEditorPanel = null;
		timeLineScaleDisplays = new Vector<TimeLineDisplayPanel>();
		tcDisplay = null;
		filterStackEdit = null;
	}
	

	public static void LCReset()
	{
		animatorMenu = null;
		viewEditor = null;
		ParamEditController.paramEditFrame = null;
		timeLineIOPColumnPanel = null;
		timeLineEditorPanel = null;
		timeLineScaleDisplays = new Vector<TimeLineDisplayPanel>();
		tcDisplay = null;
		filterStackEdit = null;
	}
	
	//--- Legacy
	public static AnimatorFrame getAnimatorFrame(){ return animatorFrame; }
	//public static FlowEditPanel getFlowEditPanel(){ return renderFlowPanel; }
	public static ViewEditor getViewEditor(){ return viewEditor; }

 }//end class
