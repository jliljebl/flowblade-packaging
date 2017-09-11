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

import animator.phantom.gui.timeline.TimeLineDisplayPanel;
import animator.phantom.renderer.ImageOperation;

//--- Layer for updates that concern multiple GUI components.
public class UpdateController
{
	//--- Value change update sources.
	public static final int PARAM_EDIT = 1;
	public static final int VIEW_EDIT = 2;
	public static final int KF_EDIT = 3;

	private static ImageOperation targetIOP = null;

	//--- Theres only one iop that is target of editing.
	public static void editTargetIOPChangedFromClipEditor( ImageOperation iop )
	{
		EditorsController.initKeyFrameEditor( iop );
		//GUIComponents.renderFlowPanel.setAsOnlySelected( iop );
		EditorsController.setEditorLayerForIop( iop );
		targetIOP = iop;
	}
	//--- Theres only one iop that is target of editing. This is called from flow editor.
	public static void editTargetIOPChanged( ImageOperation iop )
	{
		EditorsController.initKeyFrameEditor( iop );
		TimeLineController.targetIopChanged( iop );
		EditorsController.setEditorLayerForIop( iop );
		targetIOP = iop;
	}
	//--- Theres only one iop that is target of editing. This is called from view editor.
	public static void editTargetIOPChangedFromViewEditor( ImageOperation iop )
	{
		EditorsController.initKeyFrameEditor( iop );
		TimeLineController.targetIopChanged( iop );
		//GUIComponents.renderFlowPanel.setAsOnlySelected( iop );
		targetIOP = iop;
	}
	//--- Theres only one iop that is target of editing. This is called from Stack editor.
	//--- Stack iops are not part of timeline or flow and cannot edited in those editors
	public static void editTargetIOPChangedFromStackEditor( ImageOperation iop )
	{
		EditorsController.initKeyFrameEditor( iop );
		EditorsController.setEditorLayerForIop( null );
		TimeLineController.targetIopChanged( null );
		//GUIComponents.renderFlowPanel.setAsOnlySelected( null );
		targetIOP = iop;
	}
	//--- Called by for eg. PreViewPanel	
	public static ImageOperation getTargetIOP(){ return targetIOP; }	
	//--- Called after param value changed, without source.
	public static void valueChangeUpdate()
	{
		valueChangeUpdate( -1, false );
	}
	//--- Called after param value changed, with specified source.
	public static void valueChangeUpdate( int source )
	{
		valueChangeUpdate( source, false );
	}
	//--- Called after param value changed during drag, with specified source.
	public static void valueChangeUpdate( int source, boolean dragInProgress )
	{
		if( source != PARAM_EDIT ) ParamEditController.updateEditFrame();
		if( source != KF_EDIT ) EditorsController.updateKFForValueChange();
		EditorsController.displayCurrentInViewEditor( dragInProgress );
	}
	//--- Called after current frame changed.
	//--- Less gui is updated if mouse still pressed to make make dragging
	//--- frame pointer in in timeline faster.
	public static void updateCurrentFrameDisplayers( boolean dragInProgress )
	{
		ParamEditController.updateEditFrame();
		PreviewController.currentFrameChanged();
		EditorsController.displayCurrentInViewEditor( dragInProgress );//this causes preview frame render
		TimeLineController.scaleOrPosChanged();
	}
	//--- Update so that timeline dragging showes movement, always when dragging.
	public static void updateMovementDisplayers() 
	{
		PreviewController.currentFrameChanged();
		EditorsController.displayCurrentInViewEditor( true );
		TimeLineController.scaleOrPosChanged();
	}

	public static void repaintFramePosititionDisplayers()
	{
		for( TimeLineDisplayPanel scaleDisp : GUIComponents.timeLineScaleDisplays )
			scaleDisp.repaint();
		GUIComponents.tlineControls.update();
		GUIComponents.tcDisplay.repaint();
	}

}//end class
