package animator.phantom.undo.layercompositor;

import animator.phantom.controller.EditorsController;
import animator.phantom.controller.ProjectController;
import animator.phantom.controller.TimeLineController;
import animator.phantom.project.LayerCompositorLayer;
import animator.phantom.renderer.ImageOperation;
import animator.phantom.renderer.RenderNode;
import animator.phantom.undo.PhantomUndoManager;


public class LCAddLayer extends LCUndoableEdit
{
	private LayerCompositorLayer addLayer;
	
	
	public LCAddLayer( ImageOperation addIOP )
	{
		this.iop = addIOP;
		this.iop.initIOPTimelineValues();
		PhantomUndoManager.newIOPCreated( iop ); //--- Create initial state for Paramvalue change  undos
	}
	
	public void undo()
	{
		
		layerProject().deleteLayer( this.addLayer );
	}
	
	public void redo()
	{
		
		this.addLayer = layerProject().addLayer( this.iop );
	}

}//end class
