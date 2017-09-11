package animator.phantom.undo.layercompositor;

import animator.phantom.controller.LayerCompositorUpdater;
import animator.phantom.project.LayerCompositorLayer;
import animator.phantom.renderer.ImageOperation;
import animator.phantom.undo.PhantomUndoManager;


public class LCAddLayerMask extends LCUndoableEdit
{
	private LayerCompositorLayer addLayer;
	private ImageOperation layerIop;
	
	public LCAddLayerMask( ImageOperation filteIOP, ImageOperation layerIop )
	{
		this.iop = filteIOP;
		this.iop.initIOPTimelineValues();
		PhantomUndoManager.newIOPCreated( iop ); //--- Create initial state for Paramvalue change  undos
		
		this.layerIop = layerIop;
	}
	
	public void undo()
	{
		
		layerProject().deleteLayer( this.addLayer );
	}
	
	public void redo()
	{
		layerProject().addLayerMask( this.iop, this.layerIop );
	}

}//end class
