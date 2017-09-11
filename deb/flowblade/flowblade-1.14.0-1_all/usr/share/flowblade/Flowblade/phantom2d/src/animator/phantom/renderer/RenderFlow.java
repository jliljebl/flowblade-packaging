package animator.phantom.renderer;

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

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.Hashtable;
import java.util.Vector;

//--- This the render flow of a movie.
public class RenderFlow
{
	private static final int IMAGES_TABLE_INIT_CAPACITY = 500;

	//--- All the nodes in this flow.
	private Vector<RenderNode> renderNodes = new Vector<RenderNode>();
	private Hashtable<RenderNode, BufferedImage> renderedImages = new Hashtable<RenderNode, BufferedImage>( IMAGES_TABLE_INIT_CAPACITY );
	private int nextNodeID = 0;
	private boolean isCyclic = false;

	private boolean DEBUG = true;
	
	//-------------------------------------------- CONSTRUCTOR
	public RenderFlow(){}
	
	//--- Returns the number of nodes in this flow.
	public int getSize(){ return renderNodes.size(); }

	//--- Returns all nodes.
	public Vector<RenderNode> getNodes(){ return renderNodes; }
	
	//--- Set nodes vec, used when loading.
	public void setNodes( Vector<RenderNode> rNodes )
	{ 
		renderNodes = rNodes;
		for( RenderNode node : renderNodes )
			node.setFlow( this );
	}

	//--- Creates empty renderedImages table before and after rendering.
	public void emptyRenderedImages()
	{
		renderedImages = new Hashtable<RenderNode, BufferedImage>( IMAGES_TABLE_INIT_CAPACITY );
	}
	public BufferedImage getRenderedImage( RenderNode node )
	{
		return renderedImages.get( node );
	}
	public void setRenderedImage( RenderNode node, BufferedImage img )
	{
		renderedImages.put( node, img );
	}

	//--- Returns id of next created node.
	public int getNextNodeID(){ return nextNodeID; }

	//--- Set next node id
	public void setNextNodeID( int id_ ){ nextNodeID = id_; }

	//--- Returns all RenderNodes that have no source node and are thus leafs.
	public Vector<RenderNode> getLeafs()
	{
		return getLeafsFromVec( renderNodes );
	}

	public RenderNode getOutputNode()
	{
		for (RenderNode node : renderNodes)
			if (node.getImageOperation().isOutput() == true)
				return node;
	
		return null;
	}

	public static Vector<RenderNode> getLeafsFromVec( Vector<RenderNode> nodes )
	{
		Vector<RenderNode> leafs = new Vector<RenderNode>();
		for( int i = 0; i < nodes.size(); i++ )
		{
			RenderNode node = (RenderNode) nodes.elementAt( i );
			Vector<RenderNode> sources = node.getSources();
			boolean isLeaf = true;
			for( int j = 0; j < sources.size(); j++ )
			{
				if( sources.elementAt( j ) != null ) isLeaf = false;
			}
			if( isLeaf ) leafs.add( node );

		}
		return leafs;
	}

	//--- Adds a node to flow.
	public void addNode( RenderNode node )
	{
		//--- Node might be brand new or from ondo / redo. New gets ID.
		if( node.getID() == - 1 )
		{
			node.setID( nextNodeID );
			nextNodeID++;
		}

		renderNodes.add( node );
		node.setFlow( this );

		//--- Call flowChanged() of all iops in flow.
		tellAllIopsFlowChanged();

		if( DEBUG ) System.out.println( "Node added." + renderNodes.size() + " nodes in flow." );
	}
	
	//--- Removes all the nodes in vector from flow.
	public void removeNodes( Vector<RenderNode> removeNodes )
	{
		for( int i = 0; i < removeNodes.size(); i++ )
		{
			Object node = removeNodes.elementAt( i );
			renderNodes.removeElement( node );
		}

		//--- Call flowChanged() of all iops in flow.
		tellAllIopsFlowChanged();

		if( DEBUG ) System.out.println("Node removed. " + renderNodes.size() + " nodes in flow." );
	}
	
	//--- Connects given nodes with each other.
	public void connectNodes( 	RenderNode source,
					RenderNode target,
					int sourceCIndex,
					int targetCIndex )
	{
		source.addTarget( target, sourceCIndex );
		target.addSource( source, targetCIndex );

		//--- Call flowChanged() of all iops in flow.
		tellAllIopsFlowChanged();
	}

	//--- Disonnects given nodes from each other.
	public void disconnectNodes( 	RenderNode source,
					RenderNode target,
					int sourceCIndex,
					int targetCIndex)
	{	
		source.removeTarget( sourceCIndex );
		target.removeSource( targetCIndex );

		//--- Call flowChanged() of all iops in flow.
		tellAllIopsFlowChanged();
	}
	
	//--- Returns the node that contains iop or null if no node contains it.
	public RenderNode getNode( ImageOperation iop )
	{
		for( RenderNode node : renderNodes )
			if( node.getImageOperation() == iop ) return node;

		return null;
	}
	//--- Returns node with id.
	public RenderNode getNode( int nodeID )
	{
		for( RenderNode node : renderNodes )
			if( node.getID() == nodeID ) return node;
		return null;
	}
	//--- Sets all nodes as unrendered.
	public void setAllNodesUnrendered()
	{
		for( RenderNode node : renderNodes )
			node.setIsRendered( false );
	}

	//--- Tells all ImageOperations that flow is changed. Some may update GUI, most ignore.
	private void tellAllIopsFlowChanged()
	{
		/* NOTHING USES FEATURE CURRENTLY, REMOVE THIS NEXT TIME YOU SEE THIS
		for( RenderNode node : renderNodes )
			node.getImageOperation().flowChanged();
		*/
	}

	//--- Returns all animateble iops.
	public Vector <ImageOperation> getAnimatebleIops()
	{
		Vector <ImageOperation> animatableIops = new Vector<ImageOperation>();
		for( RenderNode node: renderNodes )
		{
			if( node.getImageOperation().getCoords() != null )
				animatableIops.add( node.getImageOperation() );
		}

		return animatableIops;
	}

	//--- Returns all nodes that are above given node in flow.
	public Vector<RenderNode> getNodesAboveNode( RenderNode node )
	{
		Vector<RenderNode> retNodes = new Vector<RenderNode>();
		Vector<RenderNode> branches = new Vector<RenderNode>();
		boolean stop = false;
		while( !stop )
		{
			Vector<RenderNode> sourcesVec = node.getActiveSources();
			//--- if one source, add to ret list do next round.
			if( sourcesVec.size() == 1 )
			{
				node = (RenderNode) sourcesVec.elementAt( 0 );
				retNodes.add( node );
			}
			//--- if more then one, add first and put rest into branches.
			else if( sourcesVec.size() > 1 )
			{
				node = (RenderNode) sourcesVec.elementAt( 0 );
				retNodes.add( node );
				Vector<RenderNode> addvec = new Vector<RenderNode>( sourcesVec.subList( 1, sourcesVec.size() ) );
				for( int i = 0; i < addvec.size(); i++ )
				{
					RenderNode addNode = (RenderNode) addvec.elementAt( i );
					branches.add( addNode );
				}
			}
			//--- sources == 0, if something in branches put into list and do next round.
			else if( branches.size() > 0 )
			{
				node = branches.elementAt( 0 );
				retNodes.add( node );
				branches.removeElementAt( 0 );
			}
			//--- sources and branches == 0
			else stop = true;
		}

		return retNodes;
	}

	public Vector<RenderNode> getNodesWithFileSource( FileSource fs )
	{
		Vector<RenderNode> retVec = new Vector<RenderNode>();
		for( RenderNode node : renderNodes )
			if( node.getImageOperation().getFileSource() ==  fs ) retVec.add( node );

		return retVec;
	}

	public boolean isCyclic()
	{
		isCyclic = false;

		Vector<RenderNode> leafs = getLeafs();
		if( leafs.size() == 0 && renderNodes.size() != 0 )
			return true;

		for( RenderNode leaf : leafs )
		{
			//--- Clear all nodes
			for( RenderNode node : renderNodes )
				node.setColor( Color.white );

			//--- Search for cycles starting from leaf
			visitDFS( leaf );
		}

		return isCyclic;
	}

	private void visitDFS( RenderNode node )
	{
		node.setColor( Color.gray );

		Vector<RenderNode> targets = node.getActiveTargets();

		for( RenderNode target : targets )
		{
			if( target.getColor() == Color.gray )
				isCyclic = true;

			if( target.getColor() == Color.white )
				visitDFS( target );
		}

		node.setColor( Color.black );
	}

	public RenderFlow cloneFlow()
	{
		RenderFlow clone = new RenderFlow();
		Vector<RenderNode> cloneNodes = new Vector<RenderNode>();
		for( RenderNode node : this.renderNodes )
		{
			RenderNode cloneNode = node.cloneNode();
			cloneNodes.add( cloneNode );
		}
		clone.setNodes( cloneNodes );

		//--- Build connections
		Hashtable<Integer,RenderNode> nodesTable = new Hashtable<Integer,RenderNode>( IMAGES_TABLE_INIT_CAPACITY );//"max" number of nodes needed amount in both
		for( RenderNode cloneNode : cloneNodes )
			nodesTable.put( new Integer( cloneNode.getID() ), cloneNode );

		for( RenderNode cloneNode : cloneNodes )
			cloneNode.buildConnectionsFaster( nodesTable );

		return clone;
	}

	/*
	public void printDebugInfo()
	{
		System.out.println("//-------------------------------------- Render flow has " + renderNodes.size() + " nodes." );
		for( int i = 0; i < renderNodes.size(); i++ )
		{
			
			RenderNode node = renderNodes.elementAt( i );
			node.printDebugInfo();
		}
			
	}
	*/

}//end class