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
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.util.Hashtable;
import java.util.Vector;

//--- Object of this class is a node in RenderFlow that wraps ImageOperation
//--- and provides it with input and passes on its output when requested. ImageOperation describes the
//--- operation performed on a recieved image or creates a new one and has no information of its targets.
//--- Source images for ImageOperation are provided by RendrNode.
//--- ImageOperation also does not know if it has been rendered or not.
public class RenderNode
{
	//--- The image opearation that that is performed in this node
	private ImageOperation iop = null;
	//--- During rendering renderedimages are saved per flow which is cloned per thread.
	private RenderFlow renderFlow = null;
	//--- Used when loading and nodes need to be reconnected.
	private int id = -1;
	//--- Flag for state of object, if it has been rendered or not
	private boolean isRendered = false;
	//--- The render tree node(s) that are the source(s) of this node
	private Vector<RenderNode> sources = new Vector<RenderNode>();
	//--- The render tree node(s) that are the targets of this RenderTreeNode
	private Vector <RenderNode> targets = new Vector<RenderNode>();
	//--- Copys of iops renderedImage for multiple outputs.
	private Vector <BufferedImage> renderedCopys = new Vector<BufferedImage>( 4 );
	//--- Used when loading node and later when building graph.
	private Vector<Integer> sourcesTempIDs;
	//--- Used when loading node and later when building graph.
	private Vector<Integer> targetsTempIDs;
	//--- Used to test cyclicity
	private Color color = Color.white; 

	public RenderNode(){}

	public RenderNode( ImageOperation iop )
	{
		this.iop = iop;
		//--- Create sources and targets vectors.
		for( int i = 0; i < iop.getDefaultNumberOfSources(); i++ ) sources.add( null );
		for( int i = 0; i < iop.getDefaultNumberOfTargets(); i++ ) targets.add( null );
	}
	
	//------------------------------------------------------- INTERFACE
	//--- Get / Set for iop and iop name.
	public ImageOperation getImageOperation(){ return iop; }
	public void setImageOperation( ImageOperation iop_ ){ this.iop = iop_; }
	public void setFlow( RenderFlow flow ){ this.renderFlow = flow; }
	public String getIOPName(){ return iop.getName(); }	
	//--- Get ands set for id.
	public int getID(){ return id; }
	public void setID( int newId ){ id = newId; }
	//--- This is used as the user connects the nodes.
	public void addTarget( RenderNode target, int index ){ targets.setElementAt( target, index ); }
	//--- This is used as the user adds sources 
	public void addSource( RenderNode source, int index ){ sources.setElementAt( source, index ); }
	//--- This is used when user deletes nodes or connections. 
	public void removeTarget( int index ){ targets.setElementAt( null, index ); }
	//--- This is used when user deletes nodes or connections. 
	public void removeSource( int index){ sources.setElementAt( null, index ); }
	//--- Used by FrameRenderer to keep track of rendering.
	public boolean isRendered(){ return isRendered; }
	public void setIsRendered( boolean isRendered ){ this.isRendered = isRendered; }
	//--- For testing cyclicity
	public void setColor( Color c ){ color = c; }
	public Color getColor(){ return color; }
	//--- Returns sources of this node.
	public Vector<RenderNode> getSources()
	{
		return sources;
	}
	//--- Returns non null sources of this node.
	public Vector<RenderNode> getActiveSources()
	{
		Vector<RenderNode> activeSources = new Vector<RenderNode>();
		for( RenderNode node : sources )
			if( node != null ) activeSources.add( node );

		return activeSources;
	}
	//--- Return index of first null source.
	public int getFreeSourceID()
	{
		for( int i = 0; i < sources.size(); i++ )
		{
			if( sources.elementAt( i ) == null) return i;
		}
		return -1;//no free source places.
	}
	//--- Returns non-null targets of this node.
	public Vector<RenderNode> getActiveTargets()
	{ 
		Vector<RenderNode> activeTargets = new Vector<RenderNode>();
		for( int i = 0; i < targets.size(); i++ )
		{
			RenderNode node = (RenderNode) targets.elementAt( i );
			if( node != null ) activeTargets.add( node );
		}
		return activeTargets;
	}
	//--- Returns targets vec for outside manipulation,
	//--- in for e.g. EditorOperations.outputsNumberChanged()
	public Vector<RenderNode> getTargetsVector(){ return targets; }
	//--- Return index of first null target.
	public int getFreeTargetID()
	{
		for( int i = 0; i < targets.size(); i++ )
		{
			if( targets.elementAt( i ) == null ) return i;
		}
		return -1;//no free target places.
	}

	//--- This is called by FrameRenderer for end nodes to get resulting image. RenderNodes use
	//--- getRenderedImage( RenderNode requestingTarget ) method.
	public BufferedImage getNodeRenderedImage(){ return renderFlow.getRenderedImage( this ); }
	//--- This is used by RenderNodes to get images from sources.
	public BufferedImage getNodeRenderedImage( RenderNode requestingTarget )
	{
		int targetIdex = targets.indexOf( requestingTarget );

		//--- targets( 0 ) gets renderedImage, others get copies.
		if( targetIdex == 0 ) return getNodeRenderedImage();
		else
		{
			BufferedImage retImg = renderedCopys.elementAt( targetIdex );
			//--- Clear image.
			renderedCopys.setElementAt( null, targetIdex );
			return retImg;
		}
	}
	//--- Get methods for sources and targets count.
	public int getNumberOfSources(){ return sources.size(); }
	public int getNumberOfTargets(){ return targets.size(); }
	//--- Called by EditorOperations.outputsNumberChanged(...)
	public int getSourceIndexForNode( RenderNode source )
	{
		for( int i = 0; i < sources.size(); i++ )
		{
			RenderNode node = (RenderNode) sources.elementAt( i );
			if( node == source ) return i;
		}
		return -1;
	}
	//--- Returns true if all sources are rendered or there are no sources. 
	//--- Called by FrameRenderer.
	public boolean allSourcesRendered( boolean debug, long threadID )
	{
		if( debug )
			System.out.println("threadID:" + threadID + " sources.size():" + sources.size() );

		if( sources.size() == 0 )
		{

			return true;
		}
		for( int i = 0; i < sources.size(); i ++ )
		{
			RenderNode source = (RenderNode) sources.elementAt( i );
			if( source == null )
			{
				if( debug )
				System.out.println("#### as " + i + " null" ); 
				continue;
			}
			if( !source.isRendered() )
			{
				if( debug )
				System.out.println("#### as " + i + " " + source.toString() + "NOT rendered" ); 
				return false;
			}
			else
			{
				if( debug )
				System.out.println("#### as " + i + " " + source.toString() + "rendered" ); 
			}
		}
		return true;
	}
	//--- This method is called by FrameRenderer which is responsible for 
	//--- walking through the RenderFlow to achieve correct rendering.
	public void renderNodeMovements( int frame )
	{
		synchronized( iop )// only one thread can work one a ImageOperation object at a time
		{
			iop.renderMoves( frame );
		}
	}
	//--- Collect all source images and render output image. This method is called by
	//--- FrameRenderer which is responsible for walking through the RenderFlow to
	//--- achieve correct rendering.
	public void renderNodeImage( int frame, boolean debug )
	{
		
		//System.out.println( "before iop:" + iop.getName() );
		synchronized( iop )// only one thread can work one a ImageOperation object at a time
		{
		//System.out.println( "after iop:" + iop.getName() );
			//--- Collect source images.
			Vector<BufferedImage> sourceImages = new Vector<BufferedImage>();
			for( int i = 0; i < sources.size() ; i++ )
			{
				RenderNode source = (RenderNode) sources.elementAt( i );

				if( source == null )
					sourceImages.add( null );
				else
					sourceImages.add( source.getNodeRenderedImage( this ) );
			}

			//--- Set mask input if exists.
			if( iop.hasMaskInput() )
				iop.setMaskInputImage( (BufferedImage) sourceImages.elementAt( iop.getMaskInputIndex() ) );
			
			//--- Render image.
			iop.renderImage( frame, sourceImages );
			BufferedImage renderedImage = iop.getRenderedImage();
			if( renderedImage != null )
				renderFlow.setRenderedImage( this, renderedImage );
			if( renderedImage == null  && debug )
				System.out.println("!!!!!!!!!!!!!!!!!!renderedimage was null" );

			//--- Create copys of renderedImage for ACTIVE outputs, but NOT for target 0.
			renderedCopys = new Vector<BufferedImage>();
			renderedCopys.add( null );

			for( int i = 1; i < targets.size(); i++ )
			{
				RenderNode node = (RenderNode) targets.elementAt( i );
				if( node != null && renderedImage != null )
				{
					Dimension screenSize = EditorRendererInterface.getScreenDimensions();
					BufferedImage copyImage = new BufferedImage( 	screenSize.width,
											screenSize.height,
											BufferedImage.TYPE_INT_ARGB );
					Graphics g = copyImage.getGraphics();
					//--- 
					g.drawImage( renderedImage, 0, 0, null );
					renderedCopys.add( copyImage );
				}
				else renderedCopys.add( null );
			}
		}
	}
	//--- Print debug info.
	public void printDebugInfo()
	{
		System.out.println( "RenderNode, id:" + id + ", iop:" + iop.getClass().getName() + " obj id " + toString() );
		System.out.println( "    sources:" );
		for( int i = 0; i < this.sources.size(); i++ )
		{
			RenderNode source = (RenderNode) this.sources.elementAt( i );
			String id = "null";
			String obID = "null";
			if( source != null )
			{
				id = new Integer(source.getID()).toString();
				obID = source.toString();
			}
			System.out.println( "    " + id + " objid:" + obID);
		}
		System.out.println( "    targets:" );
		for( int i = 0; i < this.targets.size(); i++ )
		{
			RenderNode target = (RenderNode) this.targets.elementAt( i );
			String id = "null";
			String obID = "null";
			if( target != null )
			{
				id = new Integer(target.getID()).toString();
				obID = target.toString();
			}
			System.out.println( "    " + id  + " objid:" + obID);
		}
	}
	
	public void buildConnections( Vector<RenderNode> allNodes )
	{
		//--- Connect sources( to this, the target).
		for( int i = 0; i < sourcesTempIDs.size(); i++ )
		{
			Integer nodeID = sourcesTempIDs.elementAt( i );
			RenderNode sourceNode = getRenderNode( allNodes, nodeID.intValue() );
			if( sourceNode != null )
			{
				int tIndexInSource = sourceNode.getLoadingTargetIndex( id );
				sourceNode.addTarget( this, tIndexInSource );
				addSource( sourceNode, i );
			}
		}
		
		//--- Since all connections are connected to sources, we only need to 
		//--- handle sources.
	}
	public void buildConnectionsFaster( Hashtable<Integer,RenderNode> nodesTable )
	{
		//--- Connect sources( to this, the target).
		for( int i = 0; i < sourcesTempIDs.size(); i++ )
		{
			Integer nodeID = sourcesTempIDs.elementAt( i );
			RenderNode sourceNode = nodesTable.get( nodeID );
			if( sourceNode != null )
			{
				int tIndexInSource = sourceNode.getLoadingTargetIndex( id );
				sourceNode.addTarget( this, tIndexInSource );
				addSource( sourceNode, i );
			}
		}
		
		//--- Since all connections are connected to sources, we only need to 
		//--- handle sources.
	}
	//--- Used when reading node and later building graph.
	public void loadSources( Vector<Integer> sourcesTempIDs, Vector<RenderNode> sources )
	{ 
		this.sourcesTempIDs = sourcesTempIDs;
		this.sources = sources;
	}
	//--- Used when reading node 
	public void loadTargets( Vector<Integer> targetsTempIDs, Vector <RenderNode> targets  )
	{ 
		this.targetsTempIDs = targetsTempIDs;
		this.targets = targets;
	}
	//---
	private RenderNode getRenderNode( Vector<RenderNode> allNodes, int id )
	{
		for( RenderNode node : allNodes )
			if( node.getID() == id ) return node;
	
		return null;
	}
	public int getLoadingTargetIndex( int targetID )
	{
		for( int i = 0; i < targetsTempIDs.size(); i++ )
		{
			Integer nodeID = targetsTempIDs.elementAt( i );
			if( nodeID.intValue() == targetID ) 
				return i;
		}
		
		return -1;
	}

	public RenderNode cloneNode()
	{
		RenderNode clone = new RenderNode();
		clone.iop = this.iop;// they share same iop but different all else;
		clone.id = this.id;
		clone.sourcesTempIDs = new Vector<Integer>();
		clone.targetsTempIDs = new Vector<Integer>();
		for( @SuppressWarnings("unused") RenderNode node : this.sources )
			clone.sources.add( null );
		for( @SuppressWarnings("unused") RenderNode node : this.targets )
			clone.targets.add( null );

		for( int i = 0; i < this.sources.size(); i++ )
		{
			RenderNode source = (RenderNode) this.sources.elementAt( i );
			int id = -1;
			if( source != null ) id = source.getID();
			clone.sourcesTempIDs.add( new Integer( id ) );
		}

		for( int i = 0; i < this.targets.size(); i++ )
		{
			RenderNode target = (RenderNode) this.targets.elementAt( i );
			int id = -1;
			if( target != null ) id = target.getID();
			clone.targetsTempIDs.add( new Integer( id ) );
		}
		return clone;
	}

	//--- Multiple outputs needs
	public static BufferedImage getImageClone( BufferedImage src )
	{
		ColorModel cm = src.getColorModel();
		return new BufferedImage( cm, src.copyData( null ), cm.isAlphaPremultiplied(), null);
	}
	
}//end class
