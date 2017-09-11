package animator.phantom.gui.keyframe;

import java.awt.event.MouseEvent;
import java.util.Collections;
import java.util.Vector;

import animator.phantom.controller.EditorsController;
import animator.phantom.controller.UpdateController;
import animator.phantom.renderer.ImageOperation;
import animator.phantom.renderer.param.AnimationKeyFrame;

public class MultiMoveKFMode  extends KFEditMode
{
	private float startYVal = 0.0f;
	private float[] startVals = null;
	private int[] startFrames = null;
	
	public void mousePressed( MouseEvent e, KeyFrameEditorPanel kfPanel, AnimationKeyFrame pressKf, int iopBeginFrame, ImageOperation iop )
	{
		this.kfPanel = kfPanel;
		this.iop = iop;
		startYVal = kfPanel.getValueForY( e.getY() );
		startFrame = pressKf.getFrame() + iopBeginFrame;//so startframe is in timeline space, not clip space
		startX = e.getX();
		lastFrameDelta = 0;//--- Last frame delta changes always when frame for kf changes.
		Vector<AnimationKeyFrame> selectedKfs = EditorsController.getSelectedKeyFrames();
		startVals = new float[selectedKfs.size()];
		startFrames = new int[selectedKfs.size()];
		for (int i = 0; i < selectedKfs.size(); i++)
		{
			startVals[i] = selectedKfs.elementAt(i).getValue();
			startFrames[i] = selectedKfs.elementAt(i).getFrame();
		}
	}

	public void mouseDragged( MouseEvent e )
	{
		kfMouseUpdate( e );
	}

	public void mouseReleased( MouseEvent e )
	{
		kfMouseUpdate( e );
		UpdateController.valueChangeUpdate( UpdateController.KF_EDIT, false );
	}
	
	public void kfMouseUpdate( MouseEvent e )
	{
		float valDelta = kfPanel.getValueForY( e.getY() ) - startYVal;
		int frameDelta = getFrameDelta( e );
		
		Vector<AnimationKeyFrame> selectedKfs = EditorsController.getSelectedKeyFrames();
		
		// Set new values and frames for selected keyframes
		for (int i = 0; i < selectedKfs.size(); i++)
		{
			selectedKfs.elementAt(i).setValue( startVals[i] + valDelta );
			selectedKfs.elementAt(i).setFrame( startFrames[i]  + frameDelta );
		}
	
		// Create vec without kfs in frames that are occupied by selected frames
		Vector<AnimationKeyFrame> allkFs = kfPanel.getEditValue().getKeyFrames();
		Vector<AnimationKeyFrame> newFramesKfs = new Vector<AnimationKeyFrame>();
		for (AnimationKeyFrame oldkf : allkFs )
		{
			boolean unOccupFrame = true;
			for (AnimationKeyFrame selkf : selectedKfs )
				if (selkf.getFrame() == oldkf.getFrame())
					unOccupFrame = false;
			
			if (unOccupFrame == true)
				newFramesKfs.add(oldkf);
		}
		for (AnimationKeyFrame selkf : selectedKfs )
			newFramesKfs.add(selkf);
		
		Collections.sort(newFramesKfs);
		
		// add 0 frame kf if needed
		if (newFramesKfs.elementAt(0).getFrame() != 0)
		{
			float val = newFramesKfs.elementAt(0).getValue(); 
			AnimationKeyFrame newKF=  AnimationKeyFrame.createNewKeyframe( 0, val );
			newFramesKfs.add(0, newKF);
		}
	
		//add new keyframes
		kfPanel.getEditValue().setKeyFrames(newFramesKfs);
	}

}//end class
