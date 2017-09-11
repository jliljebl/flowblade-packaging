package animator.phantom.undo.layercompositor;

import animator.phantom.controller.LayerCompositorUpdater;
import animator.phantom.project.LayerCompositorLayer;
import animator.phantom.renderer.ImageOperation;
import animator.phantom.undo.PhantomUndoManager;


public class LCAddLayerEffect extends LCUndoableEdit
{
	private LayerCompositorLayer addLayer;
	private ImageOperation layerIop;
	
	public LCAddLayerEffect( ImageOperation filterIOP, ImageOperation layerIop )
	{
		this.iop = filterIOP;
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
		this.layerIop.getFilterStack().add( this.iop );
		this.iop.setFilterStackIOP( true );
		this.iop.copyTimeParams( this.layerIop );

		LayerCompositorUpdater.layerEffectAddUpdate( layerIop,  layerIop.getFilterStack().size() - 1 );
	}

}//end class
