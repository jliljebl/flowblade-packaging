package animator.phantom.controller;

/*
    Copyright Janne Liljeblad

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
import java.util.Vector;

import javax.swing.JLabel;

import animator.phantom.gui.modals.DialogUtils;
import animator.phantom.gui.view.editlayer.ViewEditorLayer;
import animator.phantom.renderer.ImageOperation;
import animator.phantom.renderer.RenderNode;
import animator.phantom.renderer.param.AnimationKeyFrame;
import animator.phantom.renderer.param.KeyFrameParam;
import animator.phantom.renderer.param.Param;

//--- This class holds state and logic for keyframe editor and view editor.
public class EditorsController
{
	//--- Flag for ViewEditor updates
	private static boolean viewEditorOn = true;
	//--- Image resulting from whole flow displayed in view editor
	public static final int FLOW_VIEW = 0;
	//--- Currently selected edit layer displayed in view editor.
	public static final int LAYER_VIEW = 1;
	//--- Image resulting from rendering flow to currently selected IOP displayed in view editor.
	public static final int SELECT_VIEW = 2;
	//--- Image resulting from rendering flow to currently set target IOP displayed in view editor.
	public static final int TARGET_VIEW = 3;
	private static int viewMode = FLOW_VIEW;
	//--- State of flag switches between color and alpha displays.
	private static boolean alphaDisplay = false;
	//--- Currently edited stuff in key frame editor.
	private static Vector<AnimationKeyFrame> selectedKeyframes = new Vector<AnimationKeyFrame>();
 	private static KeyFrameParam currentKFParam = null;

 	private static Dimension viewEditorSize = null;

	//--- VIEW EDITOR, VIEW EDITOR, VIEW EDITOR, VIEW EDITOR, VIEW EDITOR, VIEW EDITOR
	//--- VIEW EDITOR, VIEW EDITOR, VIEW EDITOR, VIEW EDITOR, VIEW EDITOR, VIEW EDITOR
	//--- VIEW EDITOR, VIEW EDITOR, VIEW EDITOR, VIEW EDITOR, VIEW EDITOR, VIEW EDITOR
	//--- Updates view editor display to current frame.
	//--- Renders bg image only if mouse action stopped.(= mouse released )
	//--- NOTE: Mouse drag can also happen in view editor its self.
	public static void setViewEditorUpdatesOn( boolean value )
	{
		viewEditorOn = value;
	}

	public static void displayCurrentInViewEditor( boolean timelineDragInProgress )
	{
		//--- Move layer views ( bounding rects, handles etc.. )
		GUIComponents.viewEditor.frameChanged();
		//--- If updates are off, do nothing
		if( !viewEditorOn )
			return;
		//--- Only render and set bgimage if mouse move stopped in timeline or in
		//--- view editor.
		if( !GUIComponents.viewEditor.mouseActionUnderway()
			&& !timelineDragInProgress )
		{
			MovieRenderer movieRenderer = new MovieRenderer(  ProjectController.getFlow(), MovieRenderer.FULL_SIZE, 1 );

			/*
			if( viewMode == TARGET_VIEW )
			{
				if( FlowController.getViewTarget() != null )
					movieRenderer.setStopNode( FlowController.getViewTarget() );
				else
				{
					String[] tLines = { "Tag node with 'Crosshairs' button as target to use this view mode." };
					DialogUtils.showTwoStyleInfo( "View mode can't be used", tLines, DialogUtils.WARNING_MESSAGE );

					setViewMode( FLOW_VIEW );
					GUIComponents.viewControlButtons.setFlowViewButtonSelected();
					return;
				}
			}
			*/
			if( viewMode == SELECT_VIEW )
			{
				ImageOperation selectedIOP = ParamEditController.getParamEditIOP();
				//--- NOTE: worst case is for loop of all nodes to display a frame.
				RenderNode selectedNode = ProjectController.getFlow().getNode( selectedIOP );

				if( selectedNode!= null )
					movieRenderer.setStopNode( selectedNode );
				else
				{
					//--- TODO display some image in view editor
					//--- or info window
					String[] tLines = { "Select node with double-click for editing in Node Editor to use this view mode." };
					DialogUtils.showTwoStyleInfo( "View mode can't be used", tLines, DialogUtils.WARNING_MESSAGE );
					setViewMode( FLOW_VIEW );
					GUIComponents.viewControlButtons.setFlowViewButtonSelected();
					return;
				}
			}

			//--- Create and start render thread.
			ViewEditorRenderThread renderThread =
				new ViewEditorRenderThread ( movieRenderer,
											 GUIComponents.viewEditor,
											 TimeLineController.getCurrentFrame() );
			renderThread.start();
		}
	}
	//--- Puts all layers into view editor after load.
	public static void fillViewEditor()
	{
		GUIComponents.viewEditor.setBeingFilled( true );
		Vector<RenderNode> nodes = ProjectController.getFlow().getNodes();
		for( RenderNode node : nodes )
			addLayerForIop( node.getImageOperation() );
		GUIComponents.viewEditor.updateLayerSelector();
		GUIComponents.viewEditor.setBeingFilled( false );
	}
	//--- Add layer for iop.
	public static void addLayerForIop( ImageOperation iop )
	{
		ViewEditorLayer layer = iop.getEditorlayer();
		if( layer != null )
		{
			GUIComponents.viewEditor.addEditlayer( layer );
			if( !Application.isLoading() )
				EditorsController.displayCurrentInViewEditor( false );
		}
	}
	//--- Removes layers of iops.
	public static void removeLayers( Vector<RenderNode> nodes )
	{
		for( int i = 0; i < nodes.size(); i++ )
		{
			RenderNode node = (RenderNode)nodes.elementAt( i );
			GUIComponents.viewEditor.removeLayer( node.getImageOperation() );
		}
		displayCurrentInViewEditor( false );
	}
	//--- Changes edit layer up or down.
	public static void rotateNextLayer( boolean up )
	{
		GUIComponents.viewEditor.rotateNextLayer( up );
		//--- Display layer iop name.
		ImageOperation layerIOP = GUIComponents.viewEditor.getCurrentLayerIOP();
		if( layerIOP != null )
		{
			GUIComponents.viewControlButtons.layerText.setText( layerIOP.getName() );
			if( viewMode == LAYER_VIEW )  displayCurrentInViewEditor( false );
			else GUIComponents.viewEditor.repaint();
		}
		else GUIComponents.viewControlButtons.layerText.setText( "" );
	}
	//--- Sets active layer from layer selector
	public static void setActiveLayer( int selectorIndex )
	{
		//--- none, clear all selections
		if( selectorIndex == 0 )
		{
			GUIComponents.viewEditor.setEditLayer( null );
			GUIComponents.viewControlButtons.setModeButtons( new Vector<Integer>() );
			UpdateController.editTargetIOPChangedFromViewEditor( null );
		}
		else
		{
			Vector<ViewEditorLayer> layers = GUIComponents.viewEditor.getLayers();
			GUIComponents.viewEditor.setEditLayer( layers.elementAt( selectorIndex - 1 ) );
		}

		 displayCurrentInViewEditor( false );
	}
	//--- Sets view editor size
	public static void setViewSize( int size )
	{
		GUIComponents.viewEditor.quickChangeSize( size );

		//bar.setValue( (bar.getMaximum() - bar.getVisibleAmount() - bar.getMinimum()) / 2 );
		//--- we do the same again with rendering because if size is changed too soon after frame change
		//--- we might get something wrong
		GUIComponents.viewEditor.setScreenSize( size );
		Dimension scalesPos = GUIComponents.viewEditor.getScalesCenterPosition(GUIComponents.viewScrollPane.getSize());
		GUIComponents.viewScrollPane.getHorizontalScrollBar().setValue(scalesPos.width);
		GUIComponents.viewScrollPane.getVerticalScrollBar().setValue(scalesPos.height);
		displayCurrentInViewEditor( false );
	}

	/*
	public static int getMaxFullViewSelectionIndex()
	{
		float[] scales =  GUIComponents.viewSizeSelector.getSizeSelectionsScales();
		int maxSelIndex = 0;
		Dimension viewPortSize = GUIComponents.animatorFrame.getViewEditorSize();
		Dimension screenSize = ProjectController.getScreenSize();
		for (int i = 8; i >= 0; i--)
		{
			float scale = scales[i];
			if ((viewPortSize.width > screenSize.width * scale ) && (viewPortSize.height > screenSize.height  * scale))
			{
				maxSelIndex = i;
			}

		}

		return  maxSelIndex;
	}
	*/
	public static Dimension getViewEditorSize()
	{
		return viewEditorSize;
	}

	public static void setViewEditorSize(Dimension newSize)
	{
		viewEditorSize = newSize;
	}

	//--- Sets view mode: FLOW_VIEW, LAYER_VIEW, SELECT_VIEW or TARGET_VIEW
	public static void setViewMode( int newMode )
	{
		viewMode = newMode;
		displayCurrentInViewEditor( false );
	}
	//---
	//public static int getViewMode(){ return viewMode; }
	//--- Set color/alpha display
	public static void setAlphaDisplay( boolean showAlpha )
	{
		alphaDisplay = showAlpha;
		displayCurrentInViewEditor( false );
	}

	public static boolean displayAlpha(){ return alphaDisplay; }

	public static void setEditorLayerForIop( ImageOperation iop )
	{
		GUIComponents.viewEditor.setEditorLayerForIop( iop );
	}

	public static void displayRenderClock( boolean display )
	{
		GUIComponents.viewControlButtons.displayClock( display );
	}

	//--- KEYFRAME EDITOR, KEYFRAME EDITOR, KEYFRAME EDITOR, KEYFRAME EDITOR, KEYFRAME EDITOR
	//--- KEYFRAME EDITOR, KEYFRAME EDITOR, KEYFRAME EDITOR, KEYFRAME EDITOR, KEYFRAME EDITOR
	//--- KEYFRAME EDITOR, KEYFRAME EDITOR, KEYFRAME EDITOR, KEYFRAME EDITOR, KEYFRAME EDITOR
	//--- Updates keyframe editor's view for last selected IOP. Null is legal input value meaning nothing is selected
	public static void initKeyFrameEditor( ImageOperation iop )
	{
		GUIComponents.kfColumnPanel.initGUI( iop );
		if( iop != null )
		{
			Vector<KeyFrameParam> kfs = iop.getKeyFrameParams();

			if( kfs.size() == 0 )
			{
				GUIComponents.keyFrameEditPanel.initEditor( null, iop );
			}
			else
			{

				GUIComponents.keyFrameEditPanel.initEditor( kfs.elementAt( 0 ), iop );
				GUIComponents.kfColumnPanel.setSelected( 0 );
			}

			GUIComponents.keyFrameEditPanel.scaleOrPositionChanged();
			setKFEditorNameLabelText( iop );
		}
		else//set to nothing selected
		{
			GUIComponents.keyFrameEditPanel.initEditor( null, null );
			setKFEditorNameLabelText( null );
		}
	}
	//--- Updates keyframe editor for value change.
	public static void updateKFForValueChange()
	{
		GUIComponents.keyFrameEditPanel.repaint();
	}
	//--- Clears keyframe editor if currently edited iop is among nodes to be deleted
	public static void clearKFEditIfNecessery( Vector<RenderNode> deletedNodes )
	{
		for( int i = 0; i < deletedNodes.size(); i++ )
		{
			RenderNode node = (RenderNode)  deletedNodes.elementAt( i );
			if( node.getImageOperation() == GUIComponents.kfColumnPanel.getIOP() )
			{
				GUIComponents.kfColumnPanel.initGUI( null );
				GUIComponents.keyFrameEditPanel.initEditor( null, null );
				setKFEditorNameLabelText( null );
			}
		}
	}
	//--- Changes parameter that is being edited
	public static void setKFEditParam( KeyFrameParam editValue, ImageOperation iop )
	{
		GUIComponents.keyFrameEditPanel.initEditor( editValue, iop );
		selectedKeyframes = new Vector<AnimationKeyFrame>();
		GUIComponents.kfControl.setStepped( editValue.getStepped() );
		GUIComponents.kfColumnPanel.repaint();
	}
	//--- Set currently selected keyframe
	public static void setCurrentKeyFrame( AnimationKeyFrame kf )
	{
		selectedKeyframes = new Vector<AnimationKeyFrame>();
		selectedKeyframes.add(kf);
		GUIComponents.kfControl.setKeyFrame( kf );
	}
	public static void addSelectedKeyFrame(  AnimationKeyFrame kf )
	{
		selectedKeyframes.add(kf);
	}

	public static void selectFollowing()
	{
		AnimationKeyFrame ckf = getCurrentKeyFrame();
		if (ckf  == null)
			return;
		selectedKeyframes = new Vector<AnimationKeyFrame>();
		Vector<AnimationKeyFrame> allkFs = GUIComponents.keyFrameEditPanel.getEditValue().getKeyFrames();
		for (AnimationKeyFrame kf : allkFs)
			if (kf.getFrame() >= ckf.getFrame())
				selectedKeyframes.add(kf);
		GUIComponents.keyFrameEditPanel.repaint();
	}

	public static void selectPrevious()
	{
		AnimationKeyFrame ckf = getCurrentKeyFrame();
		if (ckf  == null)
			return;
		selectedKeyframes = new Vector<AnimationKeyFrame>();
		Vector<AnimationKeyFrame> allkFs = GUIComponents.keyFrameEditPanel.getEditValue().getKeyFrames();
		for (AnimationKeyFrame kf : allkFs)
			if (kf.getFrame() <= ckf.getFrame())
				selectedKeyframes.add(kf);
		GUIComponents.keyFrameEditPanel.repaint();
	}

	public static AnimationKeyFrame getCurrentKeyFrame()
	{
		if (selectedKeyframes.size() == 0) return null;
		return selectedKeyframes.elementAt(0);
	}
	public static Vector<AnimationKeyFrame> getSelectedKeyFrames()
	{
		return selectedKeyframes;
	}
	public static int[] getFocusKeyFrames( ImageOperation iop )
	{
		if (selectedKeyframes.size() == 0)
		{
			int[] empty = {-1};
			return empty;
		}
		int[] focusFrames = new int[selectedKeyframes.size()];
		for (int i = 0; i < selectedKeyframes.size(); i++)
		{
			AnimationKeyFrame kf = selectedKeyframes.elementAt(i);
			if  (kf != null )
				focusFrames[i] =  iop.getBeginFrame() + selectedKeyframes.elementAt(i).getFrame();
			else
				focusFrames[i] = -1;
		}
		return focusFrames;
	}
	public static void setCurrentKFParam( KeyFrameParam currentKFParam_ ){ currentKFParam = currentKFParam_; }
	public static KeyFrameParam getCurrentKFParam(){ return currentKFParam; }
	public static Param getCurrentKFEditorParam(){ return GUIComponents.kfColumnPanel.getCurrentParam(); }//--- hackish
	//--- Do zoom in for keyFrameEditPanel.
	public static void zoomInKeyFrameEditor(){ GUIComponents.keyFrameEditPanel.zoomIn(); }
	//--- Do zoom out for keyFrameEditPanel.
	public static void zoomOutKeyFrameEditor(){ GUIComponents.keyFrameEditPanel.zoomOut(); }
	//--- Sets keyframe editor name panel text
	public static void setKFEditorNameLabelText( ImageOperation iop )
	{
		String text;
		if( iop == null )
			text = "";
		else
		{
			text = iop.getName();
			if( iop.isFilterStackIOP() )
			text = iop.getName() + " <Stack>";
		}
		// 0 == index of label component after gui init
		((JLabel)GUIComponents.kfNamePanel.getComponent( 0 ) ).setText(text);
	}

	public static void addKeyFrame()
	{
		int currentFrame = TimeLineController.getCurrentFrame();
		KeyFrameParam param = getCurrentKFParam();
		if( param == null )
			return;
		param.addKeyFrame( currentFrame, param.getValue( currentFrame ) );
		updateKeyFrameParam( param );
	}

	public static void addKeyFrameForValue( float value )
	{
		int currentFrame = TimeLineController.getCurrentFrame();
		KeyFrameParam param = getCurrentKFParam();
		if( param == null )
			return;
		param.addKeyFrame( currentFrame, value );
		updateKeyFrameParam( param );
	}

	public static void updateKeyFrameParam( KeyFrameParam param )
	{
		param.getIOP().createKeyFramesDrawVector();
		UpdateController.updateCurrentFrameDisplayers( false );
		updateKFForValueChange();
		getCurrentKFEditorParam().registerUndo();
	}

	public static void deleteKeyFrame()
	{
		AnimationKeyFrame kf = EditorsController.getCurrentKeyFrame();
		if( kf == null ) return;
		KeyFrameParam param = EditorsController.getCurrentKFParam();
		if( param == null ) return;

		param.removeKeyFrame( kf.getFrame() );
		param.getIOP().createKeyFramesDrawVector();
		UpdateController.updateCurrentFrameDisplayers( false );
		EditorsController.updateKFForValueChange();
		EditorsController.getCurrentKFEditorParam().registerUndo();
	}

}//end class
