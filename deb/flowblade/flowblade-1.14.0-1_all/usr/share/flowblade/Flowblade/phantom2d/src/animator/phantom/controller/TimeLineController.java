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

import java.util.Collections;
import java.util.Vector;

import animator.phantom.gui.AnimFrameGUIParams;
import animator.phantom.gui.timeline.TimeLineDisplayPanel;
import animator.phantom.renderer.ImageOperation;
import animator.phantom.renderer.RenderNode;
import animator.phantom.renderer.param.AnimationKeyFrame;
import animator.phantom.renderer.param.KeyFrameParam;
import animator.phantom.undo.ClipAddUndoEdit;
import animator.phantom.undo.PhantomUndoManager;
import animator.phantom.undo.TimeLineUndoEdit;

//--- Handles timeline logic and state.
public class TimeLineController
{
	//--- The frame that is target of time editor operations and start point for playback etc..
	private static int currentFrame = 0;
	//--- First frame of displayed timeline view.
	private static int timeLinePosition = 0;
	//--- Value in pixels per frame in current zoom level.
	private static float pixPerFrame;
	//--- Tells how many pix per frame when zoomed closet.
	private static final int PIXELS_PER_FRAME_IN_MAX_SCALE = 30;
	private static final float ZOOM_OUT_MULT = 0.66f;
	private static final float ZOOM_IN_MULT = 1.0f / ZOOM_OUT_MULT;
	//--- Clips in timeline editor, marked by iops they contain.
	private static Vector<ImageOperation> timelineClips = new Vector<ImageOperation>();
	//--- Selected timeline clips, marked by iops they contain.
	private static Vector<ImageOperation> selectedClips = new Vector<ImageOperation>();

	//--- INIT METHODS
	//--- Sets timeline values to original values.
	public static void reset()
	{
		pixPerFrame = PIXELS_PER_FRAME_IN_MAX_SCALE;
		timeLinePosition = 0;
		currentFrame = 0;
		selectedClips = new Vector<ImageOperation>();
		timelineClips = new Vector<ImageOperation>();
	}
	//--- Timeline init for project. Frame multipliers depend on project length.
	public static void init()
	{
		pixPerFrame = getPixPerFrameMin();
	}

	//----------------------------------------------- FRAME
	//--- Current focus frame on the timeline.
	public static int getCurrentFrame(){ return currentFrame; }

	//--- Convenience method
	public static void changeCurrentFrame( int change ){ setCurrentFrame( currentFrame + change ); }
	//--- Setting current frame. This updates timeline editor gui too.
	//--- Updating other current frame depending gui done in UpdateController.
	public static void setCurrentFrame( int frame )
	{ 
		currentFrame = getBoundSafeFrame( frame );
	}

	//--- Moves current frame to frame that has the previous key frame in given parameter.
	public static void  moveCurrentFrameToPreviousKeyFrame( KeyFrameParam param )
	{
		int moveToFrame = getPreviousKeyFrameFromCurrent( param );
		if( moveToFrame != -1 )
		{
			setCurrentFrame( moveToFrame );
			UpdateController.updateCurrentFrameDisplayers( false );
		}
	}
	//--- Returns frame that has the previous key frame in given parameter.
	public static int getPreviousKeyFrameFromCurrent( KeyFrameParam param )
	{
		//--- Get key frames and the frame that iop starts from.
		Vector <AnimationKeyFrame> keyFrames = param.getKeyFrames();
		ImageOperation iop = param.getIOP();
		int iopBeginFrame = iop.getBeginFrame();

		//--- Look for previous keyframe.
		int moveToFrame = -1;
		boolean found = false;
		int lastCheckedFrame = -1;
		
		//--- Handle case: current frame between some keyframes.
		for( AnimationKeyFrame keyFrame: keyFrames )	
		{
			int movieFrame = iopBeginFrame + keyFrame.getFrame();
			if( movieFrame >= currentFrame && 
				lastCheckedFrame < currentFrame 
				&& found == false )
			{
				
				moveToFrame = lastCheckedFrame;
				found = true;
			}
			lastCheckedFrame = movieFrame;
		}

		//--- Handle case: currentFrame > lastkeyframe.getFrame()
		if( moveToFrame == -1 )
		{
			int lastKeyFrame = keyFrames.lastElement().getFrame() + iopBeginFrame;
			if( currentFrame > lastKeyFrame ) moveToFrame = lastKeyFrame;
		}

		return moveToFrame;
	}
	//--- Moves current frame to the frame that has the next keyframe in given parameter.
	public static void moveCurrentFrameToNextKeyFrame( KeyFrameParam param )
	{
		int moveToFrame = getNextKeyFrameFromCurrent( param );
		if( moveToFrame != -1 )
		{
			setCurrentFrame( moveToFrame );
			UpdateController.updateCurrentFrameDisplayers( false );
		}
	}
	//--- Returns next keyframe in param after current
	private static int getNextKeyFrameFromCurrent( KeyFrameParam param )
	{
		//--- Get key frames and the frame that iop starts from.
		Vector <AnimationKeyFrame> keyFrames = param.getKeyFrames();
		ImageOperation iop = param.getIOP();
		int iopBeginFrame = iop.getBeginFrame();

		//--- Look for previous keyframe.
		int moveToFrame = -1;
		boolean found = false;
		int lastCheckedFrame = -1;
		
		//--- Handle case: current frame between some keyframes.
		for( AnimationKeyFrame keyFrame: keyFrames )	
		{
			int movieFrame = iopBeginFrame + keyFrame.getFrame();
			if( movieFrame > currentFrame && 
				lastCheckedFrame <= currentFrame 
				&& found == false )
			{
				
				moveToFrame = movieFrame;
				found = true;
			}
			lastCheckedFrame = movieFrame;
		}

		//--- Handle case: currentFrame < firtsKeyframe.getFrame()
		if( moveToFrame == -1 )
		{
			int firstKeyFrame = keyFrames.elementAt(0).getFrame() + iopBeginFrame;
			if( currentFrame < firstKeyFrame ) moveToFrame = firstKeyFrame;
		}

		return moveToFrame;
	}

	//--- Bounds check.
	private static int getBoundSafeFrame( int frame )
	{
		//--- bounds check
		if( frame < 0 ) 
			frame = 0;
		if( frame >= ProjectController.getLength() - 1 ) 
			frame = ProjectController.getLength() - 1;
		return frame;
	}

	public static void zoomIn()
	{
		pixPerFrame *= ZOOM_IN_MULT;
		if( pixPerFrame > PIXELS_PER_FRAME_IN_MAX_SCALE )
			pixPerFrame = PIXELS_PER_FRAME_IN_MAX_SCALE;
	}

	public static void zoomOut()
	{
		pixPerFrame *= ZOOM_OUT_MULT;
		float pixPerFrameMin = getPixPerFrameMin();
		if( pixPerFrame < pixPerFrameMin )
			pixPerFrame = pixPerFrameMin;
	}

	//--- Returns current scale multiplier
	public static float getCurrentScaleMultiplier(){ return pixPerFrame; }
	// Returns pixels per frame when whole movie is just diplsyed on timeline
	private static float getPixPerFrameMin()
	{
		float scalewidth = AnimFrameGUIParams.getTimeEditRightColWidth();
		float val = scalewidth / (float) ProjectController.getLength();
		return val;
	}
		
	//----------------------------------------------- POSITION
	//--- Set first displayed frame.
	public static void setTimeLinePosition( int newPosition )
	{
		timeLinePosition = legalizeTimeLinePosition( newPosition );
	}
	public static int getTimeLinePosition() { return timeLinePosition; }
	//--- Last displayed frame
	public static int getLastFrame()
	{
		float widthInFrames = AnimFrameGUIParams.getTimeEditRightColWidth() / pixPerFrame;
		return timeLinePosition + (int) widthInFrames;
	}

	public static int legalizeTimeLinePosition( int pos )
	{
		if( pos < 0 )
			pos = 0;
		
		int maxPos = ProjectController.getLength() - (int) ((float) AnimFrameGUIParams.getTimeEditRightColWidth() / pixPerFrame );
		if( pos > maxPos )
			pos = maxPos;
		
		return pos;
	}

	//----------------------------------------------- SCALE AND POS UPDATE
	public static void scaleOrPosChanged()
	{
		GUIComponents.tlineControls.update();
		GUIComponents.timeLineEditorPanel.scaleOrPositionChanged();
		GUIComponents.keyFrameEditPanel.scaleOrPositionChanged();

		for( TimeLineDisplayPanel scaleDisp : GUIComponents.timeLineScaleDisplays )
			scaleDisp.repaint();
		
		GUIComponents.tcDisplay.repaint();
	}

	//-----------------------------------------------  EDITORS UPDATES
	public static void initClipEditorGUI()
	{
		GUIComponents.timeLineIOPColumnPanel.initGUI();
		GUIComponents.timeLineEditorPanel.initGUI();
	}
	public static void initClipsGUI()
	{
		GUIComponents.timeLineEditorPanel.initGUI();
	}
	public static void clipEditorRepaint()
	{
		GUIComponents.timeLineIOPColumnPanel.repaint();
		GUIComponents.timeLineEditorPanel.repaint();
	}
	public static void repaintTimeLineScaleDisplay()
	{
		for( TimeLineDisplayPanel scaleDisp :  GUIComponents.timeLineScaleDisplays )
			scaleDisp.repaint();
	}

	//----------------------------------------------- TIMELINE CLIP ACTIONS
	//--- Clips being edited in timeline
	public static Vector<ImageOperation> getClips(){ return timelineClips; }
	
	public static void loadClips()
	{
		timelineClips = AppData.getLayerProject().getLayerGUIIops();
	}

	//--- Return true if clips contain iop
	public static boolean clipsContain( ImageOperation iop )
	{
		return timelineClips.contains( iop );
	}

	public static void targetIopChanged( ImageOperation iop )
	{
		//--- If no clip for new target clear clip editor selections.
		if( !clipsContain( iop ) )
		{
			selectedClips.clear();
			clipEditorRepaint();
			return;
		}
		//--- if ctrl is not pressed, set as only selected.
		if( !KeyStatus.ctrlIsPressed() ) 
			setAsSingleSelectedClip( iop );
		//--- if ctrl is pressed, add to selected.
		else
			addToSelectedClips( iop );

		clipEditorRepaint();
	}
	public static void setAsSingleSelectedClip( ImageOperation iop )
	{
		selectedClips.clear();
		selectedClips.add( iop );
	}
	public static void addToSelectedClips( ImageOperation iop )
	{
		selectedClips.add( iop );
	}
	public static void unselectAllClips()
	{
		selectedClips.clear();
	}
	public static boolean clipForIopIsSelected( ImageOperation iop )
	{
		return selectedClips.contains( iop );
	}
	public static void trimSelectedStartToCurrent()
	{
		for( ImageOperation clip : selectedClips )
		{
			TimeLineUndoEdit undoEdit = new TimeLineUndoEdit( clip );
			if( clip.isFreeLength() ) clip.setClipStartFrame( currentFrame );
			else
				if( clip.frameInProgramArea( currentFrame  ) )
					clip.setClipStartFrame( currentFrame );
			saveButtonEditUndo( undoEdit, clip );
		}

		//--- Repaint
		initClipEditorGUI();
	}

	public static void trimSelectedEndToCurrent()
	{
		for( ImageOperation clip : selectedClips )
		{
			TimeLineUndoEdit undoEdit = new TimeLineUndoEdit( clip );
			if( clip.isFreeLength() ) clip.setClipEndFrame( currentFrame - 1 );
			else
				if( clip.frameInProgramArea( currentFrame  ) )
					clip.setClipEndFrame( currentFrame - 1);
			saveButtonEditUndo( undoEdit, clip );
		}

		//--- Repaint
		initClipEditorGUI();
	}
	public static void moveClipStartToCurrent()
	{
		for( ImageOperation clip : selectedClips )
		{
			TimeLineUndoEdit undoEdit = new TimeLineUndoEdit( clip );
			clip.setBeginFrame( currentFrame );
			saveButtonEditUndo( undoEdit, clip );
		}

		//--- Repaint
		initClipEditorGUI();
	}
	public static void moveClipEndToCurrent()
	{
		for( ImageOperation clip : selectedClips )
		{
			TimeLineUndoEdit undoEdit = new TimeLineUndoEdit( clip );
			clip.setEndFrame( currentFrame - 1 );
			saveButtonEditUndo( undoEdit, clip );
		}

		//--- Repaint
		initClipEditorGUI();
	}

	private static void saveButtonEditUndo( TimeLineUndoEdit undoEdit, ImageOperation iop )
	{
		undoEdit.setAfterState( iop );
		PhantomUndoManager.addUndoEdit( undoEdit );
	}

	public static void nextFrame()
	{
		TimeLineController.setCurrentFrame( TimeLineController.getCurrentFrame() + 1 );
		UpdateController.updateCurrentFrameDisplayers( false );
	}
	public static void previousFrame()
	{
		TimeLineController.setCurrentFrame( TimeLineController.getCurrentFrame() - 1 );
		UpdateController.updateCurrentFrameDisplayers( false );
	}

	public static void moveSelectedClipsUp()
	{
		Vector<ImageOperation> selectedInOrder = getSelectedInOrder();
		for( int i = 0; i < selectedInOrder.size(); i++ )
		{
			
			int index = timelineClips.indexOf( selectedInOrder.elementAt( i ) );
			if( index > 0 )
				index --;
			timelineClips.remove( selectedInOrder.elementAt( i ) );
			timelineClips.add( index, selectedInOrder.elementAt( i ) ); 
		}
		initClipEditorGUI();
	}

	public static void moveSelectedClipsDown()
	{
		Vector<ImageOperation> selectedInOrder = getSelectedInOrder();
		Collections.reverse(selectedInOrder);
		for( int i = 0; i < selectedInOrder.size(); i++ )
		{
			
			int index = timelineClips.indexOf( selectedInOrder.elementAt( i ) );
			if( index < timelineClips.size() - 1 )
				index ++;
			timelineClips.remove( selectedInOrder.elementAt( i ) );
			timelineClips.add( index, selectedInOrder.elementAt( i ) ); 
		}
		initClipEditorGUI();
	}

	public static Vector<ImageOperation> getSelectedInOrder()
	{
		Vector<ImageOperation> selectedInOrder = new Vector<ImageOperation>();
		for( int i = 0; i < timelineClips.size(); i++ )
		{
			if( selectedClips.contains( timelineClips.elementAt( i )) )
				selectedInOrder.add( timelineClips.elementAt( i ) );
		}
		
		return selectedInOrder;
	}

}//end class
