package animator.phantom.project;

import java.util.Vector;

import animator.phantom.controller.LayerCompositorUpdater;
import animator.phantom.controller.ProjectController;
import animator.phantom.renderer.ImageOperation;
import animator.phantom.renderer.RenderFlow;
import animator.phantom.renderer.RenderNode;

public class LayerCompositorProject 
{
	
	private Vector<LayerCompositorLayer> layers;
	
	
	public LayerCompositorProject()
	{
		this.layers = new Vector<LayerCompositorLayer>();
	}
	
	//------------------------------------------------------------------------ edits
	public LayerCompositorLayer addLayer( ImageOperation iop )
	{
		disconnectLayers();
		
		RenderNode addNode = new RenderNode( iop );
		LayerCompositorLayer addLayer = new LayerCompositorLayer( addNode );
		this.layers.add( addLayer );
		
		getFlow().addNode( addLayer.getNode() );

		connectLayers();

		LayerCompositorUpdater.layerAddUpdate( iop );
		
		return addLayer;
	}
	
	public void insertLayer( LayerCompositorLayer insertLayer, int index )
	{
		disconnectLayers();
		
		this.layers.insertElementAt( insertLayer, index );
		
		getFlow().addNode( insertLayer.getNode() );

		connectLayers();

		LayerCompositorUpdater.layerAddUpdate( insertLayer.getIop() );
	}
	
	public void deleteLayer( LayerCompositorLayer deleteLayer )
	{
		disconnectLayers();
		
		RenderNode deleteNode = deleteLayer.getNode();
		Vector<RenderNode> deleteVec = new Vector<RenderNode>();
		deleteVec.add( deleteNode );

		this.layers.removeElement( deleteLayer );
		
		getFlow().removeNodes( deleteVec );

		connectLayers();
		
		LayerCompositorUpdater.layerDeleteUpdate( deleteVec );
			
	}

	
	public void addLayerMask( ImageOperation maskIop, ImageOperation layerIop )
	{
	
		maskIop.copyTimeParams( layerIop );
		LayerCompositorLayer layer = getLayer( layerIop );
		layer.addLayerMask( maskIop );
		
		LayerCompositorUpdater.layerMaskAddUpdate( maskIop, layerIop );
	}
	
	//-------------------------------------------------------------------- interface
	public LayerCompositorLayer getLayer( ImageOperation iop )
	{
		for( LayerCompositorLayer layer : layers )
		{
			if( layer.getIop() == iop)
				return layer;
		}
		
		return null;
	}
	
	public int getLayerIndex( LayerCompositorLayer layer ){ return layers.indexOf( layer ); }
	
	public Vector<ImageOperation> getLayerGUIIops()
	{
		Vector<ImageOperation> layersIops = new Vector<ImageOperation>();
		for( int i = this.layers.size() - 1; i > - 1; i-- )
		{
			layersIops.add( layers.elementAt(i).getIop() );
		}
		return layersIops;
	}
	
	//--------------------------------------------------------------------- internal
	private Project getNodeProject()
	{
		return ProjectController.getProject();
	}
	
	private RenderFlow getFlow()
	{
		return getNodeProject().getRenderFlow();
	}
		
	private void connectLayers()
	{
		if ( this.layers.size() < 2 ) return;
		
		for( int i = 0; i < this.layers.size() - 1; i++ )
		{
			RenderNode layerNode1 = this.layers.elementAt( i ).getNode();
			RenderNode layerNode2 = this.layers.elementAt( i + 1 ).getNode();
			getFlow().connectNodes( layerNode1, layerNode2, 0, 0);
		}	
	}

	private void disconnectLayers()
	{
		if ( this.layers.size() < 2 ) return;
		
		for( int i = 0; i < this.layers.size() - 1; i++ )
		{
			RenderNode layerNode1 = this.layers.elementAt( i ).getNode();
			RenderNode layerNode2 = this.layers.elementAt( i + 1 ).getNode();
			getFlow().disconnectNodes( layerNode1, layerNode2, 0, 0);
		}	
	}
	
}//end class
