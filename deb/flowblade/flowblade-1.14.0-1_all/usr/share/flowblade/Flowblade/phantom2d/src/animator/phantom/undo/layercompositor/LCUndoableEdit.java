package animator.phantom.undo.layercompositor;


import animator.phantom.controller.AppData;
import animator.phantom.project.LayerCompositorProject;
import animator.phantom.undo.PhantomUndoManager;
import animator.phantom.undo.PhantomUndoableEdit;

public abstract class LCUndoableEdit extends PhantomUndoableEdit
{
	public void doEdit()
	{
		redo();
		PhantomUndoManager.addUndoEdit( this );
	}
	
	
	public LayerCompositorProject layerProject()
	{
		return AppData.getLayerProject();
	}
	
}//end class
