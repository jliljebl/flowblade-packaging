package animator.phantom.project;

import java.util.Vector;

import animator.phantom.controller.AppData;
import animator.phantom.renderer.ImageOperation;
import animator.phantom.renderer.RenderNode;

public class LayerCompositorLayer 
{
	private RenderNode node;
	private Vector<RenderNode> layerMasks;
	
	public LayerCompositorLayer( RenderNode node )
	{
		this.node = node;
		this.setLayerMasks(new Vector<RenderNode>());
	}

	public RenderNode getNode(){ return this.node; }

	public ImageOperation getIop(){ return this.node.getImageOperation(); }

	public Vector<RenderNode> getLayerMasks() { return layerMasks; }

	public void setLayerMasks(Vector<RenderNode> layerMasks) { this.layerMasks = layerMasks; }
	
	public void addLayerMask( ImageOperation maskIop )
	{
		disconnectMasks();
		
		RenderNode maskNode = new RenderNode( maskIop );
		this.layerMasks.add( maskNode );
		AppData.getFlow().addNode( maskNode );

		connectMasks();
	}
	
	private void connectMasks()
	{
		if ( this.layerMasks.size() > 1 )
		{
			for( int i = 0; i < this.layerMasks.size() - 1; i++ )
			{
				RenderNode maskNode1 = this.layerMasks.elementAt( i );
				RenderNode maskNode2 = this.layerMasks.elementAt( i + 1 );
				AppData.getFlow().connectNodes( maskNode1, maskNode2, 0, 0);
			}
		}

		if ( this.layerMasks.size() > 0 )
		{
			int maskInput = node.getImageOperation().getMaskInputIndex();
			RenderNode masksOutNode = layerMasks.elementAt( layerMasks.size() - 1 );
			AppData.getFlow().connectNodes( masksOutNode, node, 0, maskInput);
		}
	}

	private void disconnectMasks()
	{
		if ( this.layerMasks.size() > 1 )
		{
			for( int i = 0; i < this.layerMasks.size() - 1; i++ )
			{
				RenderNode maskNode1 = this.layerMasks.elementAt( i );
				RenderNode maskNode2 = this.layerMasks.elementAt( i + 1 );
				AppData.getFlow().disconnectNodes( maskNode1, maskNode2, 0, 0);
			}
		}
	}

}//end class

