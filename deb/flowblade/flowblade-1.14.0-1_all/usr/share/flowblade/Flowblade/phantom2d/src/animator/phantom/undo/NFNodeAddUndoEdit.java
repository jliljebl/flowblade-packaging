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

import animator.phantom.controller.EditorsController;
import animator.phantom.controller.FlowController;
import animator.phantom.controller.GUIComponents;
import animator.phantom.controller.TimeLineController;
import animator.phantom.gui.flow.FlowBox;
import animator.phantom.renderer.ImageOperation;
import animator.phantom.renderer.RenderNode;

//--- Adds node action with no FlowEditor
public class NFNodeAddUndoEdit extends PhantomUndoableEdit
{
	private RenderNode node;

	public NFNodeAddUndoEdit( RenderNode node )
	{
		super();
		this.node = node;
	}

	public void undo()
	{
		Vector<RenderNode> vec = new Vector<RenderNode>();
		vec.add( node );

		EditorsController.removeLayers( vec );
		EditorsController.clearKFEditIfNecessery( vec );


		ImageOperation iop = node.getImageOperation();
		Vector<ImageOperation> addClips = new  Vector<ImageOperation>();
		addClips.add( iop );

		TimeLineController.loadClips();
		TimeLineController.initClipEditorGUI();
	}

	public void redo()
	{

		//FlowController.addRenderNode( node );

		ImageOperation iop = node.getImageOperation();


		Vector<ImageOperation> addClips = new  Vector<ImageOperation>();
		addClips.add( iop );

		TimeLineController.loadClips();
		TimeLineController.initClipEditorGUI();

	}

}//end class
