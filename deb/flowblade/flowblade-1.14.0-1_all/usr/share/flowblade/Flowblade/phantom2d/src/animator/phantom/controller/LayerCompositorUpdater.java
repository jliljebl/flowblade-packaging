package animator.phantom.controller;

import java.util.Vector;

import animator.phantom.renderer.ImageOperation;
import animator.phantom.renderer.RenderNode;

public class LayerCompositorUpdater 
{

	public static void layerAddUpdate( ImageOperation addIOP )
	{
		//--- Update GUI
		TimeLineController.targetIopChanged( addIOP );
		EditorsController.addLayerForIop( addIOP );
		//--- If new iop does not have edit layer we need render view editor
		//--- because layer add did not trigger render.
		if( addIOP.getEditorlayer() == null )
			 EditorsController.displayCurrentInViewEditor( false );

		//--- Request editpanel so params will be named.
		//--- Params are named by editors and names are used in save/load and kfeditor
		//--- Do this in thread because might take 500ms+
		final ImageOperation fholder = addIOP;
		new Thread()
		{
			public void run()
			{
				fholder.getEditFrame( false );
				EditorsController.initKeyFrameEditor( fholder );
			}
		}.start();
		
		Vector<ImageOperation> addClips = new  Vector<ImageOperation>();
		addClips.add( addIOP );
		
		TimeLineController.loadClips();
		TimeLineController.initClipEditorGUI();
		ParamEditController.displayEditFrame( addIOP );// ALSO TO INIT PARAM NAMES IN RAW IOPS, plugins do this by them selves
	}

	public static void layerDeleteUpdate( Vector<RenderNode> deleteVec )
	{
		EditorsController.removeLayers( deleteVec );
		EditorsController.clearKFEditIfNecessery( deleteVec );

		RenderNode deleteNode = deleteVec.elementAt(0);
		if( ParamEditController.getParamEditIOP() == deleteNode.getImageOperation() )
			 ParamEditController.clearEditframe();
		
		ImageOperation iop = deleteNode.getImageOperation();
		Vector<ImageOperation> deleteClips = new  Vector<ImageOperation>();
		deleteClips.add( iop );
		TimeLineController.loadClips();
		TimeLineController.initClipEditorGUI();		
	}
	
	public static void layerEffectAddUpdate( ImageOperation layerIop, int selectedEffectIndex )
	{
		UpdateController.updateCurrentFrameDisplayers( false );
		GUIComponents.filterStackPanel.initFilterStack( selectedEffectIndex );
	}
	
	
	public static void layerMaskAddUpdate( ImageOperation maskIop, ImageOperation layerIop )
	{
		UpdateController.updateCurrentFrameDisplayers( false );
	}
	
	/*
	EditorsController.setViewMode( EditorsController.FLOW_VIEW );
	ParamEditController.clearEditframe();
	
	RenderNode node = AppData.project.getRenderFlow().getNode( currentIOP );
	Vector<RenderNode> nodes = new Vector<RenderNode>();
	nodes.add( node );
	//--- Remove from editors.
	TimeLineController.removeClipsCorrespondingtoNodes( nodes );
	EditorsController.removeLayers( nodes );
	EditorsController.clearKFEditIfNecessery( nodes );
	*/
	
}//end class
