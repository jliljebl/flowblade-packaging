package animator.phantom.controller;

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

import java.awt.Point;
import java.awt.Rectangle;
import java.util.Collections;
import java.util.Vector;

import animator.phantom.gui.flow.FlowBox;
import animator.phantom.gui.flow.FlowConnectionArrow;
import animator.phantom.gui.flow.LookUpGrid;
import animator.phantom.gui.modals.DialogUtils;
import animator.phantom.gui.modals.MComboBox;
import animator.phantom.gui.modals.MInputArea;
import animator.phantom.gui.modals.MInputPanel;
import animator.phantom.gui.modals.PHDialog;
import animator.phantom.gui.view.editlayer.ViewEditorLayer;
import animator.phantom.renderer.FileSequenceSource;
import animator.phantom.renderer.FileSingleImageSource;
import animator.phantom.renderer.FileSource;
import animator.phantom.renderer.ImageOperation;
import animator.phantom.renderer.RenderNode;
import animator.phantom.renderer.VideoClipSource;
import animator.phantom.renderer.imagesource.FileImageSource;
import animator.phantom.renderer.imagesource.ImageSequenceIOP;
import animator.phantom.renderer.imagesource.MovingBlendedIOP;
import animator.phantom.renderer.imagesource.VideoClipIOP;
import animator.phantom.renderer.plugin.FileImagePatternMergePlugin;
//import animator.phantom.undo.FlowMoveUndoEdit;
//import animator.phantom.undo.MultiArrowAddUndoEdit;
import animator.phantom.undo.PhantomUndoManager;

//--- This class holds state and logic for render flow
public class FlowController
{
	//--- Used to create box columns in PlowPanel
	private static final int COLUMN_Y_GAP = 27;

	//--- Render result in this node is displayd if ViewEditor display mode is View Target
	//--- It is marked with crosshairs icon.
	private static RenderNode viewTargetNode = null;
	//--- The one with iop displayed in ParamEditor
	private static RenderNode editTargetNode = null;

		/*
	public static void addIOPFromFileSourceRightAway( FileSource fs, Point screenPoint )
	{
		// NOTE CODE DUPLICATION nearby methods
		ImageOperation addIOP = getNewIOPFromSource( fs );

		screenPoint.x = screenPoint.x;// 24 is const for finding a new place for box below and to the right.
		screenPoint.y = screenPoint.y;// 24 is const for finding a new place for box below and to the right.
		addIOPNow( addIOP, screenPoint );

	}
	*/
	/*
	//--- Adds iop right away near center i flow editor.
	public static void addToCenterFromFileSource( FileSource fs )
	{
		if( fs == null )
			return;
		ImageOperation addIOP = getNewIOPFromSource( fs );
		Point p = GUIComponents.renderFlowPanel.getAddPos();
		addIOPNow( addIOP, p );
	}
	*/
	/*
	public static void addFileMergeFromFileSource( FileSource fs )
	{
		if( fs == null )
			return;
		FileImagePatternMergePlugin plugin = new FileImagePatternMergePlugin();
		plugin.getIOP().registerFileSource( fs );
		Point p = GUIComponents.renderFlowPanel.getAddPos();
		addIOPNow( plugin.getIOP(), p );
	}
	*/
	/*
	//--- Adds iop right away near center i flow editor.
	public static void addIOPRightAway( ImageOperation addIOP )
	{
		Point p = GUIComponents.renderFlowPanel.getAddPos();
		addIOPNow( addIOP, p );
	}
	*/
	/*
	public static void addIOPNow( ImageOperation addIOP, Point p )
	{
		addIOP.initIOPTimelineValues();
		Point addP = GUIComponents.renderFlowPanel.getAddPos(p);
		GUIComponents.renderFlowPanel.addIOPRightAway( addIOP, addP.x, addP.y );
		ParamEditController.displayEditFrame( addIOP );// ALSO TO INIT PARAM NAMES IN RAW IOPS, plugins do this by them selves
	}
	-*/
	/*
	private static ImageOperation getNewIOPFromSource( FileSource fs )
	{
		if( fs.getType() == FileSource.IMAGE_FILE )
			return new FileImageSource( (FileSingleImageSource) fs );
		if( fs.getType() == FileSource.IMAGE_SEQUENCE )
			return new ImageSequenceIOP( (FileSequenceSource) fs );
		if( fs.getType() == FileSource.VIDEO_FILE )
			return new VideoClipIOP( (VideoClipSource) fs );
		return null; //this will crash very soon, and it should
		}
	*/
	/*
	//--- Delete selected boxes and nodes from flow.
	public static void deleteSelected()
	{

	}
	*/
	/*
	//--- Deletes Vector of nodes. USed user forces a delete of file source that has some nodes using it.
	public static void deleteVector( Vector<RenderNode> vec )
	{
		for( RenderNode node : vec )
			if(  ParamEditController.getParamEditIOP() == node.getImageOperation() )
				ParamEditController.clearEditframe();

		//TimeLineController.removeClipsCorrespondingtoNodes( vec );
		EditorsController.removeLayers( vec );
		EditorsController.clearKFEditIfNecessery( vec );
		//--- FlowPanel deletes boxes and arrows from its display.
		//--- It also disconnects nodes and removes them from flow data
		//--- using callbacks ( FlowController.disconnectNodes(..), FlowController.deleteRenderNodes(...) )
		GUIComponents.renderFlowPanel.deleteBoxes( vec );
	}
	*/
	/*
	//--- Called from RenderFlowPanel witch creates addNode.
	public static void addRenderNode( RenderNode addNode )
	{
		//--- Add node to flow. New node gets its ID.
		ProjectController.getFlow().addNode( addNode );

		//--- Update GUI
		ImageOperation iop = addNode.getImageOperation();
		TimeLineController.targetIopChanged( iop );

		EditorsController.addLayerForIop( iop );
		//--- If new iop does not have edit layer we need render view editor bg
		//--- because layer add did not trigger render.
		if( iop.getEditorlayer() == null )
			 EditorsController.displayCurrentInViewEditor( false );
		//--- Create initial state for undos
		PhantomUndoManager.newIOPCreated( iop );
		//--- Request editpanel so params will be named
		//--- Params are named in editors and names are used in save/load
		//--- and kfeditor
		//--- Do in thread because might take 500ms+
		final ImageOperation fholder = iop;
		new Thread()
		{
			public void run()
			{
				fholder.getEditFrame( false );
				EditorsController.initKeyFrameEditor( fholder );
			}
		}.start();
	}
	*/
	/*
	//--- Called from FlowController.deleteSelected() when it's ready to delete nodes.
	public static void deleteRenderNodes( Vector<RenderNode> nodes )
	{
		ProjectController.getFlow().removeNodes( nodes );
		for( Object node : nodes )
		{
			RenderNode rn = (RenderNode) node;
			if( ParamEditController.getParamEditIOP() == rn.getImageOperation() )
				 ParamEditController.clearEditframe();
		}
	}
	*/
	/*
	//--- Connects provided nodes.
	public static void connectNodes( RenderNode source, RenderNode target,
								int sourceCIndex,int targetCIndex )
	{
		ProjectController.getFlow().
			connectNodes( source, target, sourceCIndex, targetCIndex );
	}

	//--- Disconnects provided nodes.
	public static void disconnectNodes( RenderNode source, RenderNode target,
								int sourceCIndex, int targetCIndex )
	{
		ProjectController.getFlow().
			disconnectNodes( source, target, sourceCIndex, targetCIndex);
	}
	*/
	//--- Called when user changes outputs number for node.
	public static void outputsNumberChanged( ImageOperation iop, int outputsNumber )
	{
		/*
		//-- Get node
		RenderNode source = ProjectController.getFlow().getNode( iop );

		//--- Change FlowBox data and redraw it.
		//--- Destroy arrows in FlowViewPanel outside new outputs number range.
		//GUIComponents.renderFlowPanel.nodeOutputsNumberChanged( source, outputsNumber );

		//--- Get node targets vector
		Vector<RenderNode> targets = source.getTargetsVector();

		//--- Disconnect nodes that are outside new outputs number range.
		for( int i = 0; i < targets.size(); i++ )
		{
			RenderNode target = (RenderNode) targets.elementAt( i );
			if( target != null && i >= outputsNumber )
			{
				int targetCIndex = target.getSourceIndexForNode( source );
				disconnectNodes( source, target, i, targetCIndex );
			}
		}

		//--- Set source nodes targets vector to new size.
		if( outputsNumber < targets.size() )
			targets.subList( outputsNumber, targets.size() ).clear();
		else if( outputsNumber > targets.size() )
		{
			int times =  outputsNumber - targets.size();
			for( int i = 0; i < times; i++ )
				targets.add( null );
		}

		//--- Repaint
		GUIComponents.renderFlowPanel.repaint();
		*/
	}

		/*
	//--- Creates of column of selected boxes.
	public static void arrangeBoxRow()
	{
		Vector<FlowBox> selectedBoxes = GUIComponents.renderFlowPanel.getSelectedBoxes();
		Vector<FlowBox> startPlaceBoxes = new Vector<FlowBox>();
		for( FlowBox b : selectedBoxes )
			startPlaceBoxes.addElement( new FlowBox( b.getX(), b.getY() ) );

		Collections.sort( selectedBoxes );
		if( selectedBoxes.size() < 2 ) return;
		int startBoxY = selectedBoxes.elementAt( 0 ).getY();
		int startBoxX = selectedBoxes.elementAt( 0 ).getX();

		LookUpGrid lookUpGrid = GUIComponents.renderFlowPanel.getLookUpGrid();

		//--- Move boxes
		for( int i = 0; i < selectedBoxes.size(); i++ )
		{
			int boxY = startBoxY + i * COLUMN_Y_GAP;
			FlowBox b = selectedBoxes.elementAt( i );
			//--- remove, move, put back
			lookUpGrid.removeFlowGraphicFromGridInArea( b, b.getArea() );
			b.setPlace( startBoxX, boxY );
			lookUpGrid.addFlowGraphicToGrid( b );
		}

		//--- Move arrows
		Vector<FlowConnectionArrow> arrowsToSelected = getArrowsToSelected( selectedBoxes );
		Vector <Rectangle> startAreasForArrows = new Vector<Rectangle>();

		for( FlowConnectionArrow arrow : arrowsToSelected )
			startAreasForArrows.add( arrow.getArea() );


		for( FlowConnectionArrow arrow : arrowsToSelected )
		{
			Rectangle area = arrow.getArea();
			lookUpGrid.removeFlowGraphicFromGridInArea( arrow, area );
			arrow.updatePosition();
			lookUpGrid.addFlowGraphicToGrid( arrow );
		}

		//FlowMoveUndoEdit undoEdit = new FlowMoveUndoEdit( selectedBoxes, startPlaceBoxes,
		//					arrowsToSelected, startAreasForArrows );
		//PhantomUndoManager.addUndoEdit( undoEdit );

		GUIComponents.renderFlowPanel.repaint();
	}
	*/
	//---- Connects 2-n selected boxes in order
	/*
	public static void connectSelected()
	{
		Vector<FlowBox> selectedBoxes = GUIComponents.renderFlowPanel.getSelectedBoxes();
		Vector<FlowConnectionArrow> newArrows = new Vector<FlowConnectionArrow>();
		Collections.sort( selectedBoxes );
		if( selectedBoxes.size() < 2 ) return;

		LookUpGrid lookUpGrid = GUIComponents.renderFlowPanel.getLookUpGrid();

		for( int i = 0; i < selectedBoxes.size() - 1; i++ )
		{
			FlowBox b1 = selectedBoxes.elementAt( i );
			FlowBox b2 = selectedBoxes.elementAt( i + 1);

			int targetid = b1.getRenderNode().getFreeTargetID();
			int sourceid = b2.getRenderNode().getFreeSourceID();

			if( targetid != -1 && sourceid != -1 )
			{
				//--- Create, place and connect arrow.
				FlowConnectionArrow addArrow = new FlowConnectionArrow( b1, b2 );
				newArrows.add( addArrow );
				addArrow.setConnectionPoints( b1.getOutputCP( targetid ),
								b2.getInputCP( sourceid ) );
				b1.getOutputCP( targetid ).setArrow( addArrow );
				b2.getInputCP( sourceid ).setArrow( addArrow );
				addArrow.updatePosition();
				//--- Save arrow, put into grid
				GUIComponents.renderFlowPanel.arrows.add( addArrow );
				lookUpGrid.addFlowGraphicToGrid( addArrow );

				//--- Connect nodes.
				//connectNodes( b1.getRenderNode(), b2.getRenderNode(), targetid, sourceid );
			}
		}

		MultiArrowAddUndoEdit undoEdit = new MultiArrowAddUndoEdit( newArrows );
		PhantomUndoManager.addUndoEdit( undoEdit );

		rollBackCyclic();

		GUIComponents.renderFlowPanel.repaint();
		EditorsController.displayCurrentInViewEditor( false );
	}
	*/
	//--- Disconnects selected nodes
	/*
	public static void disConnectSelected()
	{
		Vector<FlowBox> selectedBoxes = GUIComponents.renderFlowPanel.getSelectedBoxes();
		Vector<FlowConnectionArrow> deleteArrows = new Vector<FlowConnectionArrow>();
		Collections.sort( selectedBoxes );
		if( selectedBoxes.size() < 2 ) return;

		for( int k = 0; k < selectedBoxes.size() - 1; k++ )
		{
			FlowBox sbox = selectedBoxes.elementAt( k );
			FlowBox tbox = selectedBoxes.elementAt( k + 1);

			RenderNode source = sbox.getRenderNode();
			RenderNode target = tbox.getRenderNode();

			Vector<RenderNode> targets = source.getTargetsVector();
			Vector<RenderNode> sources = target.getSources();

			for( int i = 0; i < targets.size(); i++ )
			{
				RenderNode t = (RenderNode) targets.elementAt( i );
				for( int j = 0; j < sources.size(); j++ )
				{
					RenderNode s = (RenderNode) sources.elementAt( j );
					if( 	t != null &&
						s != null &&
						t.getID() == target.getID() &&
						s.getID() == source.getID() )
					{
						disconnectNodes( source, target, i, j );
						FlowConnectionArrow arrow = sbox.getOutputCP( i ).getArrow();
						deleteArrows.add( arrow );
						sbox.getOutputCP( i ).setArrow( null );
						tbox.getOutputCP( j ).setArrow( null );
						GUIComponents.renderFlowPanel.removeArrow( arrow );
					}
				}
			}
		}

		MultiArrowAddUndoEdit undoEdit = new MultiArrowAddUndoEdit( deleteArrows );
		PhantomUndoManager.addUndoEdit( undoEdit );

		GUIComponents.renderFlowPanel.repaint();
		EditorsController.displayCurrentInViewEditor( false );
	}
	*/
	//--- Returns all arrows connected selected nodes
	private static Vector<FlowConnectionArrow> getArrowsToSelected( Vector<FlowBox> selectedBoxes )
	{
		Vector<FlowConnectionArrow> arrowsToSelected = new Vector<FlowConnectionArrow>();
		for( FlowBox b : selectedBoxes )
		{
			Vector<FlowConnectionArrow> arrows = b.getAllArrows();

			for( FlowConnectionArrow arrow : arrows )
				if( !arrowsToSelected.contains( arrow ) )
					arrowsToSelected.add( arrow );

		}
		return arrowsToSelected;
	}
	//--- Flip selected node as view target, on if not, off if it already is view target
	public static void viewTargetPressed()
	{
		/*
		Vector<FlowBox> selectedBoxes = GUIComponents.renderFlowPanel.getSelectedBoxes();
		if( selectedBoxes.size() == 0 )
			return;
		RenderNode selectedNode = selectedBoxes.elementAt( 0 ).getRenderNode();

		//--- If current selected is VIEW target node, clear it
		if( viewTargetNode == selectedNode )
		{
			viewTargetNode = null;
		}
		else if( selectedNode != null )
		{
			viewTargetNode = selectedNode;
		}

		//--- Call repaints on needed gui.
		GUIComponents.renderFlowPanel.updateForViewTarget();
		GUIComponents.renderFlowPanel.repaint();
		ParamEditController.updateEditFrame();
		EditorsController.displayCurrentInViewEditor( false );
		*/
	}
	//--- Target node handling. Called after removing nodes from flow.
	//--- Set target null if current not in flow anymore.
	/*
	public static void updateViewTargetNode()
	{
		if( viewTargetNode == null ) return;
		if(  ProjectController.getFlow().getNode( viewTargetNode.getID() ) == null ) viewTargetNode = null;
		//GUIComponents.renderFlowPanel.updateForViewTarget();
	}
	//---
	public static RenderNode getViewTarget()
	{
		return viewTargetNode;
	}

	public static void setEditTargetNode( ImageOperation iop )
	{
		if( iop == null )
			editTargetNode = null;
		else
			editTargetNode = ProjectController.getFlow().getNode( iop );

		//GUIComponents.renderFlowPanel.updateForEditTarget();
	}

	public static RenderNode getEditTarget()
	{
		return editTargetNode;
	}

	public static void iopNameChanged( RenderNode node )
	{
		//GUIComponents.renderFlowPanel.reCreateBox( node );
	}

	public static void clearSelection()
	{
		//GUIComponents.renderFlowPanel.deselectEverything();
		//GUIComponents.renderFlowPanel.repaint();
	}

	public static void selectAll()
	{
		//GUIComponents.renderFlowPanel.selectAll();
		//GUIComponents.renderFlowPanel.repaint();
	}
*/
	//--- Does undo if flow is cyclic, clears redos and displays message.
	//--- This is called after edit is done and added to undo manager.
	/*
	public static void rollBackCyclic()
	{
		if( ProjectController.getFlow().isCyclic() )
		{
			PhantomUndoManager.doUndo();
			PhantomUndoManager.clearRedos();
			String boldText = "Cyclic graph created";
			String[] tLines = {"You can't create render flows with cyclic parts.",
					   "Edit cancelled." };
			DialogUtils.showTwoStyleInfo( boldText, tLines, DialogUtils.WARNING_MESSAGE );
		}
	}
	*/
	/*
	public static void replaceMedia( RenderNode targetNode )
	{
		MovingBlendedIOP originalIOP = (MovingBlendedIOP) targetNode.getImageOperation();
		FileSource originalSource = originalIOP.getFileSource();
		Vector<FileSource> projectFileSources = ProjectController.getProject().getFileSources();
		Vector<FileSource> replacementSources = new Vector<FileSource>(projectFileSources);
		replacementSources.remove(originalSource);

		if (replacementSources.size() == 0)
		{
			String[] tLines = { "Only one media source found in project.","A media source cannot be replaced with itself.","Add more media sources to project."  };
			DialogUtils.showTwoStyleInfo( "Media replace not possible", tLines, PHDialog.WARNING_MESSAGE );
			return;
		}

 		String[] media = new String[replacementSources.size()];
 		for (int i = 0; i < replacementSources.size(); i++ )
 			media[i] = replacementSources.elementAt(i).getName();

		MComboBox replacementMedia = new MComboBox( "Select Replacement Media", media );

		MInputArea qArea = new MInputArea( "" );
		qArea.add( replacementMedia );

		MInputPanel panel = new MInputPanel( "Replaceme Node Media " );
		panel.add( qArea );

		int retVal = DialogUtils.showMultiInput( panel, 450, 120 );
		if( retVal != DialogUtils.OK_OPTION ) return;

		FileSource replacementFileSource = replacementSources.elementAt(replacementMedia.getSelectedIndex());
		//MovingBlendedIOP replacementIOP = (MovingBlendedIOP) getNewIOPFromSource( replacementFileSource );

		originalIOP.cloneValuesToReplacement(replacementIOP);
		//replacementIOP.loadParentIOP( ProjectController.getFlow() );

		targetNode.setImageOperation(replacementIOP);

		GUIComponents.viewEditor.removeLayer( originalIOP );
		ViewEditorLayer layer = replacementIOP.getEditorlayer();
		if( layer != null )
		{
			GUIComponents.viewEditor.addEditlayer( layer );
		}

		ParamEditController.displayEditFrame( null );
		iopNameChanged( targetNode );
		PreviewController.renderAndDisplayCurrent();
	}
	*/
}//end class
