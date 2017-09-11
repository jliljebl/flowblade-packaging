package animator.phantom.undo;

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

import animator.phantom.controller.TimeLineController;
import animator.phantom.renderer.ImageOperation;

public class TimeLineUndoEdit extends PhantomUndoableEdit 
{
	protected int beginFrameBefore;
	protected int clipStartFrameBefore;
	protected int clipEndFrameBefore;

	protected int beginFrameAfter;
	protected int clipStartFrameAfter;
	protected int clipEndFrameAfter;

	public TimeLineUndoEdit( ImageOperation iop )
	{
		this.iop = iop;

		this.beginFrameBefore = iop.getBeginFrame();
		this.clipStartFrameBefore = iop.getClipStartFrame();
		this.clipEndFrameBefore = iop.getClipEndFrame();
	}

	//--- This is initialized before edit values are set and this is called after edit values are
	//--- so that this captures both states.
	public void setAfterState( ImageOperation iop )
	{
		this.beginFrameAfter = iop.getBeginFrame();
		this.clipStartFrameAfter = iop.getClipStartFrame();
		this.clipEndFrameAfter = iop.getClipEndFrame();
	}

	public void undo()
	{
		iop.setBeginFrame( beginFrameBefore );
		iop.setClipStartFrame( clipStartFrameBefore );
		iop.setClipEndFrame( clipEndFrameBefore );

		TimeLineController.clipEditorRepaint();
	}

	public void redo()
	{
		iop.setBeginFrame( beginFrameAfter );
		iop.setClipStartFrame( clipStartFrameAfter );
		iop.setClipEndFrame( clipEndFrameAfter );

		TimeLineController.clipEditorRepaint();
	}

}//end class
