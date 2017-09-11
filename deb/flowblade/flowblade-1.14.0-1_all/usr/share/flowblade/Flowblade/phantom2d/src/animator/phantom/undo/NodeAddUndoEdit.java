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

//--- Adds node to flow
public class NodeAddUndoEdit extends PhantomUndoableEdit
{
	private RenderNode node;
	private FlowBox box;

	public NodeAddUndoEdit( RenderNode node, FlowBox box )
	{
		super();
		this.node = node;
		this.box = box;
	}

	public void undo()
	{
		Vector<RenderNode> vec = new Vector<RenderNode>();
		vec.add( node );

		EditorsController.removeLayers( vec );
		EditorsController.clearKFEditIfNecessery( vec );
		GUIComponents.renderFlowPanel.boxes.remove( box );
		GUIComponents.renderFlowPanel.selectedBoxes.remove( box );
		GUIComponents.renderFlowPanel.lookUpGrid.removeFlowGraphicFromGridInArea( box, box.getArea() );	
		//FlowController.deleteRenderNodes( vec );
			
		GUIComponents.renderFlowPanel.repaint();

		ImageOperation iop = node.getImageOperation();
		//if( IOPLibrary.getBoxType( iop ) == IOPLibrary.BOX_SOURCE )
		//{
			Vector<ImageOperation> addClips = new  Vector<ImageOperation>();
			addClips.add( iop );
		
			TimeLineController.loadClips();
			TimeLineController.initClipEditorGUI();
		//}
	}

	public void redo()
	{
		GUIComponents.renderFlowPanel.boxes.add( box );
		GUIComponents.renderFlowPanel.lookUpGrid.addFlowGraphicToGrid( box );
		//FlowController.addRenderNode( node );

		GUIComponents.renderFlowPanel.repaint();
		
		ImageOperation iop = node.getImageOperation();
		
		//if( IOPLibrary.getBoxType( iop ) == IOPLibrary.BOX_SOURCE )
		//{
			Vector<ImageOperation> addClips = new  Vector<ImageOperation>();
			addClips.add( iop );
			
			TimeLineController.loadClips();
			TimeLineController.initClipEditorGUI();
		//}
	}

}//end class