package animator.phantom.gui.keyframe;

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

import java.awt.event.MouseEvent;

import animator.phantom.controller.ProjectController;
import animator.phantom.renderer.ImageOperation;
import animator.phantom.renderer.param.AnimationKeyFrame;

public abstract class KFEditMode
{
	protected KeyFrameEditorPanel kfPanel;
	protected int startX;
	protected int lastFrameDelta;
	protected int startFrame;
	protected ImageOperation iop;
	
	//--- Called when edit in this mode is started by mouse button press.
	public abstract void mousePressed( MouseEvent e, KeyFrameEditorPanel kfPanel, AnimationKeyFrame kf, int beginFrame, ImageOperation iop );

	//--- Called when edit in this mode is ongoing and mouse is dragged.
	public abstract void mouseDragged( MouseEvent e );

	//--- Called when edit in this mode is ongoing and mouse is button is released.
	public abstract void mouseReleased( MouseEvent e );

	public int getFrameDelta( MouseEvent e )
	{
		int editDelta = e.getX() - startX;
		int frameDelta = Math.round( editDelta / kfPanel.getPixPerFrame() );
		/*
		if( startFrame + frameDelta < 0 
			|| startFrame + frameDelta >  ProjectController.getLength() - 1 ) frameDelta = lastFrameDelta;
		if(  startFrame + frameDelta < iop.getClipStartFrame() ) frameDelta = iop.getClipStartFrame() - startFrame;
		if(  startFrame + frameDelta > iop.getClipEndFrame() ) frameDelta = iop.getClipEndFrame() - startFrame;
		*/
		
		if( startFrame + frameDelta < 0 
				|| startFrame + frameDelta >  ProjectController.getLength() - 1 ) frameDelta = lastFrameDelta;
		if(  startFrame + frameDelta < iop.getClipStartFrame() ) frameDelta = startFrame - iop.getClipStartFrame();
		if(  startFrame + frameDelta > iop.getClipEndFrame() ) frameDelta = iop.getClipEndFrame() - startFrame;
			
		return  frameDelta;
	}
}//end class