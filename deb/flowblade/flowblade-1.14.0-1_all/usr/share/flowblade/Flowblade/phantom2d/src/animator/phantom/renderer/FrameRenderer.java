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

import java.awt.image.BufferedImage;
import java.util.Vector;

import animator.phantom.plugin.PluginUtils;

//--- Renders a single frame from flow for given frame.
public class FrameRenderer
{
	//--- Frame to be rendered
	private int frame;
	
	//--- RenderFlow being rendered
	private RenderFlow flow = null;
	
	//--- Leafs of RenderFlow data structure.
	private Vector<RenderNode> leafs;
	
	//--- The nodes that have no targets.
	private Vector<RenderNode> ends;

	//---
	private boolean DEBUG = false;
	private boolean hasCloneFlow = false;

	//--- The two different passes that made to render a frame
	private static final int MOVEMENT_PASS = 1;
	private static final int IMAGE_PASS = 2;

	//--- Rendering stopped after this node is set.
	private RenderNode stopNode = null;
	private BufferedImage stopImg = null;

	//--- Render time
	private long lastRenderTime = 0;

	//--- Render thread running this
	private long threadID;

	//--- Abort flag
	private static boolean doAbort = false;

	//---------------------------------------------- CONSTRUCTOR
	public FrameRenderer( RenderFlow flow, long threadID )
	{
		this.flow = flow;
		this.threadID = threadID;
		doAbort = false;
	}
	
	//--- Abort handling
	public static void requestAbort(){ doAbort = true;}
	public static boolean abort(){ return doAbort; }

	public void setHasCloneFlow( boolean val ){ hasCloneFlow = val; }
	//--- 
	public void setDebug( boolean value ){ DEBUG = value; }
	//--- Set stop node.
	public void setStopNode( RenderNode node ){ stopNode = node; }

	//--- Returns time it took last time when frame was renderered.
	public int lastRendertime(){ return (int) lastRenderTime; }

	//--- Render frame.
	public BufferedImage renderFrame( int frame )
	{
		String cloneStr = " NO CLONE";
		if( hasCloneFlow )
			cloneStr = " CLONE!!!!!!";
		if( DEBUG )
			System.out.println("//--- FRAME " + frame + cloneStr + " threadID:" + threadID );
		//--- Start timing
		long start = System.currentTimeMillis();

		//--- Clear rendered images table from flow
		flow.emptyRenderedImages();

		//--- Capture frame.
		this.frame = frame;

		//--- Get leafs.
		leafs = flow.getLeafs();

		//--- if output node defined and no other stop node, use that
		if( stopNode == null )
			stopNode = flow.getOutputNode();
		
		//--- If we have stop node, only use leafs that are children of stop node if graph.
		if( stopNode != null )
		{
			leafs = getStopNodeLeafs();
		}

		printLeafs();

		if( DEBUG )
		{
			System.out.println( "RenderFlow has " + leafs.size() + " leaf(s)." );
			printLeafs();
		}
		if( DEBUG ) System.out.println( "MOVEMENT_PASS..." );


		//--- The whole render tree is rendered twice.
		//--- First, the movements.
		setAllNodesUnrendered();
		renderPass( MOVEMENT_PASS );

		//--- Abort handling if renderPass(...) returned because of abort request.
		if( abort() )
		{
			executeAbort();
			return null;
		}
		
		if( DEBUG )
		{
			System.out.println("");
			 System.out.println( "IMAGE_PASS -------------------------------------------------------------------#" );
		}

		//--- Then the images are modified and composited
		setAllNodesUnrendered();
		renderPass( IMAGE_PASS );

		//--- Abort handling if renderPass(...) returned because of abort request.
		if( abort() )
		{
			executeAbort();
			return null;
		}
		
		if( DEBUG )
			System.out.println( "RenderFlow has " + ends.size() + " end(s)." );
		
		//--- If we have stop node return its result.
		if( stopNode != null )
		{
			if( DEBUG && stopImg == null ) 
				System.out.println("stopImg null");

			return stopImg;
		}

		//--- ends.size() == 0 means there are no nodes is in flow, return black.
		if( ends.size() == 0 ) 
			return PluginUtils.createScreenCanvas();
		
		//--- Get finished image and return it.
		//--- We'll get > 1 end nodes if end node is a merge.
		RenderNode end = (RenderNode) ends.elementAt( 0 );
		if( ends.size() > 1 && allEndsSameNode() ) 
		{
			Vector<RenderNode> nonNullEnds = new Vector<RenderNode>();
			for (RenderNode endCand : ends)
			{
				if (endCand != null)
				{
					System.out.println(endCand);
					nonNullEnds.add(endCand);
				}
			}
			
			System.out.println("nonNullEnds:" + nonNullEnds.size());
			end = nonNullEnds.lastElement();
		}

		//--- Display render time time.
		lastRenderTime = Math.round( System.currentTimeMillis() - start );
		if( DEBUG ) 
			System.out.println( "//---" + cloneStr +" FRAME "+ frame + " RENDER TIME: " + lastRenderTime	+ "ms." );

		//--- Return finished image.
		return end.getNodeRenderedImage();
	}

	//--- Flow is passed twice, once for parent-child transformations second time for image rendering.
	private void renderPass( int PASS )
	{ 
		//--- Set up render lists and stop flag.
		
		//--- Members of renderList are quarenteed to be ready for rendering.
		//--- This means that all their sources are rendered or null;
		Vector<RenderNode> renderList = leafs;

		//--- This is the candidates for renderlist for next pass that is beig built during 
		//--- renderloop iteration.
		Vector<RenderNode> renderCandidatesList = new Vector<RenderNode>();

		//--- The nodes that have no outputs and might be the one that contains the result.
		//--- Correctly formed flow has only 1 end.
		ends = new Vector<RenderNode>();

		//--- Render loop.
		boolean keepRendering = true;
		while( keepRendering )
		{
			if( DEBUG ) outloopprint( "START NEW RENDER LIST. List size: " + renderList.size() );

			//--- Abort handling
			if( abort() ) return;

			//--- Go through current renderList 
			for( int i = 0; i < renderList.size(); i++ )
			{
				//--- Get node from list.
				RenderNode node = (RenderNode) renderList.elementAt( i );
				if( DEBUG )
				{
					outloopprint(  "NEXT NODE: " + node.getIOPName() );
					System.out.println(node);
				}
				//--- Render that node and all its decendant first
				//--- child nodes that have all their
				//--- sources rendered.
				//--- Put not-first child nodes into candidates list.
				//--- Put first child decandant with not rendered sources into candidates list.
				//--- First child == targets.elementAt( 0 );
				boolean renderNextNode = true;
				while( renderNextNode )
				{
					if( DEBUG )
					{
						outloopprint(  "NEXT NODE: " + node.getIOPName() );
						System.out.println(node);
					}

					//--- Abort handling
					if( abort() ) return;

					//--- Render node. Depents on pass if movements or image is rendered.
					if( PASS == MOVEMENT_PASS ) 
						node.renderNodeMovements( frame );
					else 
						node.renderNodeImage( frame, DEBUG );

					if( DEBUG && PASS != MOVEMENT_PASS && node.getNodeRenderedImage() == null )
						System.out.println(node.getImageOperation().getName() + "getNodeRenderedImage() == null" );

					//--- Set node rendered.
					node.setIsRendered( true );
			
					//--- If stop node set and this is it were done.
					if( stopNode != null && node.getImageOperation() == stopNode.getImageOperation()  )
					{
						System.out.println("stopImg created");
						stopImg = stopNode.getNodeRenderedImage();
						return;
					}
		
					//--- Remove it from candidates list, if it is there.
					boolean wasRemoved = renderCandidatesList.remove( node );

					if( DEBUG && wasRemoved )
						loopprint(  node.getIOPName() + " was removed from candidates list." );

					//--- Get nodes target nodes.
					Vector<RenderNode> targets = node.getActiveTargets();

// 					//--- The node has no targets. Its an end.
					if( targets.size() == 0)
					{
						renderNextNode = false;
						ends.addElement( node );
						if( DEBUG ) 
							loopprint(  node.getIOPName() + " has no targets. It's an end." );
					}
					//--- Node has 1 target 
					//--- Since node is it's target's source, target is
					//--- quaranteed to be possible to render
					else if( targets.size() == 1 )
					{
						if( DEBUG ) loopprint( node.getIOPName() + " has 1 target." );

						node = (RenderNode) targets.elementAt( 0 );
						//--- If it is possible to render new node render it next.
						//--- If it is NOT possible to render the first target on the vector
						//--- put all targets into candidates list rendrer next mermer in 
						//--- render list
						if( node.allSourcesRendered( DEBUG, threadID ) )
						{
							if( DEBUG ) loopprint(  "Single target can be rendered. Render it next." );
						}
						else
						{
							renderCandidatesList.add( node );
							renderNextNode = false;

							if( DEBUG ) loopprint( "Single target can NOT be rendered." +
										"Put it in candidates list and do next in renderList if any." );
						}

					}
					//--- Node has more than 1 target.
					//--- If it is possible to render the first target on the vector
					//--- set it to be rendered next and put rest of the targets into candidates
					//--- list.
					//--- If it is NOT possible to render the first target on the vector
					//--- put all targets into candidates list rendrer next member in 
					//--- render list.
					else
					{
						if( DEBUG ) loopprint( node.getIOPName() + " has multiple targets." );

						RenderNode target = (RenderNode) targets.elementAt( 0 );
						if( target.allSourcesRendered( DEBUG, threadID  ) )
						{
							node = target;
							Vector<RenderNode> rest = new Vector<RenderNode>( targets.subList( 1, targets.size() ) );
							renderCandidatesList.addAll( rest );

							if( DEBUG ) loopprint( "target( 0 ) can be rendred. Render it next." + 
							" Rest (" + rest.size() + " node(s)) go into candidates list." );
						}
						else
						{
							renderCandidatesList.addAll( targets );
							renderNextNode = false;

							if( DEBUG ) loopprint(  "target( 0 ) can NOT be rendred." + 
								" Targets go into candidates list. Do next in render list if any." );
						}
					}//--- end else

				}//--- end while for rendering node and all 
				 //--- its decendant first child nodes
			}//--- end for going thourgh renderlist
			
			if( DEBUG ) loopprint(  "renderList done. Create new renderlist." );
			if( DEBUG ) loopprint(  "There are " + renderCandidatesList.size() + 
							" nodes in renderCandidates list." );
			//--- Create new renderlist from candidates list.
			//--- All sources of node must be rendered to go to renderlist
			Vector<RenderNode> newRenderList = new Vector<RenderNode>();	
			for( int i = 0; i < renderCandidatesList.size(); i++ )
			{
				RenderNode candidate = (RenderNode) renderCandidatesList.elementAt( i );
				if( candidate.allSourcesRendered( DEBUG, threadID  ) )
				{
					newRenderList.addElement( candidate );

					if( DEBUG ) loopprint(  candidate.getIOPName() + " goes into new renderList" );
				}
				else if( DEBUG ) loopprint(  candidate.getIOPName() + " rejected from renderList." );
			}
			renderList = newRenderList;

			//--- Remove from candidates list all nodes that are going to be rendered next.
			renderCandidatesList.removeAll( renderList );

			if( DEBUG ) loopprint(  "renderCandidatesList has " + renderCandidatesList.size() + " elements." );
			
			//--- If renderlist is empty, stop rendering	
			if( renderList.size() == 0 ) keepRendering = false;

		}//end while for going though all connected nodes of renderFlow
		if( DEBUG ) loopprint( "Render pass done." );

	}//end renderPass()

	
	private void setAllNodesUnrendered()
	{
		flow.setAllNodesUnrendered();
	}

	private Vector<RenderNode> getStopNodeLeafs()
	{
		Vector<RenderNode> nodes = flow.getNodesAboveNode( stopNode );
		
		//--- If no nodes above, stop node is only lef.
		if( nodes.size() == 0 )
		{
			Vector<RenderNode> rVec = new Vector<RenderNode>();
			rVec.add( stopNode );
			return rVec;
		}

		//--- Return sub group leafs from nodes above.
		return RenderFlow.getLeafsFromVec( nodes );
	}

	private boolean allEndsSameNode()
	{
		boolean r = true;
		int nodeId = ((RenderNode) ends.elementAt( 0 )).getID();
		for( int i = 1; i < ends.size(); i++ )
			if( ((RenderNode) ends.elementAt( i )).getID() != nodeId ) r = false;

		return r;
	}

	//--- DEBUG help method
	private void printLeafs()
	{
		for( int i = 0; i < leafs.size(); i++ )
		{
			RenderNode rn = (RenderNode) leafs.elementAt( i );
			System.out.println( "leaf:" + rn.toString() + " threadID:" + threadID );
			System.out.println(rn);
		}
	}

	//--- Rendering has been aborted.
	private static void executeAbort()
	{
		EditorRendererInterface.framerendererAbort();
	}

	//--- Debug printing help stuff
	private void outloopprint( String s )
	{
		System.out.println("        " + s );
	}
	private void loopprint( String s )
	{
		System.out.println("                " + s );
	}

}//end class
