package animator.phantom.undo;

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

import animator.phantom.controller.TimeLineController;
import animator.phantom.controller.UpdateController;
import animator.phantom.renderer.ImageOperation;

public class ClipAddUndoEdit extends PhantomUndoableEdit
{
	private Vector<ImageOperation> clips;

	public ClipAddUndoEdit( Vector<ImageOperation> clips )
	{
		super();
		this.clips = clips;
	}

	public void undo()
	{
		TimeLineController.loadClips();
		TimeLineController.initClipEditorGUI();
	}

	public void redo()
	{
		//TimeLineController.addClips( clips );
		TimeLineController.setAsSingleSelectedClip( UpdateController.getTargetIOP() );
		TimeLineController.initClipEditorGUI();
		//GUIComponents.animatorFrame.setEditorTabSelected( AnimatorFrame.CLIP_EDITOR_TAB_INDEX );
	}
}