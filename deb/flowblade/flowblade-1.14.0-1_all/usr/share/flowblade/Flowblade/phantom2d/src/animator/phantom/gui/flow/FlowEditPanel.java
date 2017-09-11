package animator.phantom.gui.flow;

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

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.util.Vector;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;

import animator.phantom.controller.AppUtils;
import animator.phantom.controller.DarkTheme;
import animator.phantom.controller.FlowController;
import animator.phantom.controller.GUIComponents;
import animator.phantom.controller.KeyActionController;
import animator.phantom.controller.KeyStatus;
//import animator.phantom.controller.MenuActions;
import animator.phantom.controller.ParamEditController;
import animator.phantom.controller.PreviewController;
import animator.phantom.controller.ProjectController;
import animator.phantom.controller.UpdateController;
import animator.phantom.controller.UserActions;
import animator.phantom.controller.keyaction.CopyAction;
import animator.phantom.controller.keyaction.DeSelectAllAction;
import animator.phantom.controller.keyaction.DeleteAction;
import animator.phantom.controller.keyaction.KeyUtils;
import animator.phantom.controller.keyaction.PasteAction;
import animator.phantom.controller.keyaction.SelectAllAction;
//import animator.phantom.gui.AnimatorMenu;
import animator.phantom.gui.GUIColors;
import animator.phantom.gui.GUIResources;
import animator.phantom.gui.IOPMenuItem;
import animator.phantom.gui.MediaMenuItem;
import animator.phantom.renderer.FileSource;
import animator.phantom.renderer.ImageOperation;
import animator.phantom.renderer.RenderNode;
import animator.phantom.renderer.imagesource.MovingBlendedIOP;
//import animator.phantom.undo.MultiDeleteUndoEdit;
import animator.phantom.undo.NodeAddUndoEdit;
import animator.phantom.undo.PhantomUndoManager;

//--- GUI component used to edit the render flow.
public class FlowEditPanel extends JPanel //implements MouseListener, MouseMotionListener, ActionListener
{
	//--- All boxes
	public Vector<FlowBox> boxes;
	//--- Selected Boxes
	public Vector<FlowBox> selectedBoxes = new Vector<FlowBox>();
	//--- Arrows
	public Vector<FlowConnectionArrow> arrows = new Vector<FlowConnectionArrow>();
	//--- Currently selected arrows.
	public Vector<FlowConnectionArrow> selectedArrows = new Vector<FlowConnectionArrow>();
	//--- Movin and / or graphics that need to be updated.
	public Vector<FlowGraphic> movingGraphics = new Vector<FlowGraphic> ();
	//--- Edit mode that is in effect after mouse press.
	//private EditMode editMode;
	//--- ImageOperation being added.
	private ImageOperation addIOP = null;
	//--- Work area is divided into grid. Vectors in lookUpGrid[x][y] have references 
	//--- to all graphics that have cornerpoints in given grid cell.
	//--- This is a quite overkill optimization.
	public LookUpGrid lookUpGrid;
	//--- State flags.
	private boolean MOVING_STOPPED = true;
	//--- View target handling
	private BufferedImage viewTargetInFlow = GUIResources.getResourceBufferedImage( GUIResources.viewTargetInFlow );
	private FlowBox viewTargetBox;
	//--- Edit target handling
	private BufferedImage editTargetInFlow = GUIResources.getResourceBufferedImage( GUIResources.editTargetInFlow );
	private FlowBox editTargetBox;
	//--- Box right mouse pop-up
	private FlowBox popupSource = null;
	private int mediaPopUpX = 0;
	private int mediaPopUpY = 0;
	
	private  JPopupMenu popup;
	private  JMenuItem deleteNode;
	private  JMenuItem renameNode;
	private  JMenuItem setAsrenderTarget;
	private  JMenuItem cloneNode;
	private  JMenuItem freezeAllValues;

	private JPopupMenu editorPopup;
	private JMenuItem mediaList;
	private JMenuItem addImage;
	private JMenuItem addImageSequence;
	private JMenuItem addVideo;
	private JMenuItem noRefs;
	private JMenuItem replaceMedia;
	
	//--------------------------------------------------------------------- CONSTRUCTOR
	public FlowEditPanel( int width, int height )
	{
		System.out.println( "INITIALIZING FLOW EDIT PANEL..." );
		lookUpGrid = new LookUpGrid( width, height );

		//--- Size
		setPreferredSize( new Dimension(width, height ) );

		//--- Listeners
		//addMouseListener( this );
		//addMouseMotionListener( this );

		//--- Node context popup menu
		popup = new JPopupMenu();

		setAsrenderTarget = new JMenuItem("Set as View Editor Target");
		//setAsrenderTarget.addActionListener(this);
		popup.add( setAsrenderTarget );

		popup.addSeparator();

		replaceMedia = new JMenuItem("Replace Media...");
		//replaceMedia.addActionListener(this);
		popup.add( replaceMedia );

		popup.addSeparator();
		
		freezeAllValues = new JMenuItem("Freeze All Values To Current");
		//freezeAllValues.addActionListener(this);
		popup.add( freezeAllValues );

		popup.addSeparator();

		renameNode = new JMenuItem("Rename");
		//renameNode.addActionListener(this);
		popup.add( renameNode );
		cloneNode = new JMenuItem("Clone");
		//cloneNode.addActionListener(this);
		popup.add( cloneNode );
		deleteNode = new JMenuItem("Delete");
		//deleteNode.addActionListener(this);
		popup.add( deleteNode );

		//--- Node context popup menu
		editorPopup = new JPopupMenu();
		mediaList = new JMenu("Media");
		//updateMediaMenu();
		editorPopup.add( mediaList );
		
		editorPopup.addSeparator();
		
		/*
		Vector<JMenu> nodeGroupMenus = AnimatorMenu.getNodesMenus( this );
		for( JMenu subMenu : nodeGroupMenus )
		{
			editorPopup.add( subMenu );
		}
		*/
		//--- Key actions
		setFocusable( true );
		KeyUtils.setFocusAction( this, new DeleteAction(), "DELETE" );
		KeyUtils.setFocusAction( this, new CopyAction(), "control C" );
		KeyUtils.setFocusAction( this, new PasteAction(), "control V" );
		KeyUtils.setFocusAction( this, new SelectAllAction(), "control A" );
		KeyUtils.setFocusAction( this, new DeSelectAllAction(), "control shift A" );

		setBackground( GUIColors.flowBGColor );
	}
	
	/*
	//-------------------------------------------------------------- INTERFACE
	//--- Used when opening loaded project.
	//--- This is NOT an I/O operation, I/O already done.
	public void loadBoxes( Vector<FlowBox> newBoxes )
	{
		boxes = newBoxes;
		//--- Create arrows
		for( FlowBox box : boxes )
		{
			//--- Put addbox into grid.
			lookUpGrid.addFlowGraphicToGrid( box );
		}

		AppUtils.printOneTab( "boxes size:" + boxes.size() );
		createArrows();
		AppUtils.printOneTab( "arrows size:" + arrows.size() );
	}

	//--- Returns components coordinates into which new box fits near middle
	public Point getAddPos()
	{
		Point scrollPos = GUIComponents.animatorFrame.getScrollPos();
		Point viewMiddle = GUIComponents.animatorFrame.getViewPortMiddlePoint();
		int x = scrollPos.x + viewMiddle.x;
		int y = scrollPos.y + viewMiddle.y;
		return getAddPos(new Point(x, y));
	}

	public Point getAddPos( Point p )
	{
		int x = p.x;
		int y = p.y;
		FlowBox b = lookUpGrid.getBox( x, y );
		while( b != null )
		{
			x = x + 24;// 24 is const for finding a new place for box below and to the right.
			y = y + 24;
			b = lookUpGrid.getBox( x, y );
		}

		return new Point( x, y );
	}
	
	public void addIOPRightAway( ImageOperation iop, int x, int y )
	{ 
		addIOP = iop;
		addBox( x, y );
	}

	private void addBox( int x, int y )
	{
		//--- Create box where shadowBox was
		//--- Create node to added.
		RenderNode addNode = new RenderNode( addIOP );
		//--- Create box to be added.
		FlowBox addBox = new FlowBox(   x - ( FlowBox.width / 2 ),
						y - ( FlowBox.height / 2 ),
						addNode );

		NodeAddUndoEdit undoEdit = new NodeAddUndoEdit( addNode, addBox );

		//--- BANG! New iops to flow are added HERE!
		undoEdit.doEdit();

		//--- Register undo
		PhantomUndoManager.addUndoEdit( undoEdit );

		//--- Set all currently selected boxes unselected.
		for(  FlowBox b : selectedBoxes )
			b.setSelected( false );
		selectedBoxes.clear();

		//--- Draw panel
		movingGraphics.clear();
		paintImmediately( 0, 0, getWidth(), getHeight() );//--- to give an impression of a responsive GUI

		//ADD_IN_PROGRESS = false;
	}

	//--- Deletes all selected boxes, arrows connecting to
	//--- deleted boxes, selected arrows, disconnect nodes that had
	//--- arrows connecting them and clear references to deleted nodes 
	//--- from other nodes. Can be triggered by button or key
	public Vector<RenderNode> deleteSelectedBoxes()
	{
		Vector<RenderNode> removeNodes = new Vector<RenderNode> ();
		Vector<FlowConnectionArrow> removeArrows = new Vector<FlowConnectionArrow>();
		Vector<FlowBox> deletedBoxes = new Vector<FlowBox>();

		//--- Arrows and nodes are collected to be removed.
		for( int i = 0; i < selectedBoxes.size(); i++ )
		{
			//Collect Boxes
			FlowBox b = selectedBoxes.elementAt( i );
			deletedBoxes.add( b );
			//--- remove from main collection
			boxes.remove( b );
			//--- remove from grid
			lookUpGrid.removeFlowGraphicFromGridInArea( b ,b.getArea() );
			//--- 
			removeNodes.addElement( b.getRenderNode() );
			removeArrows.addAll( b.getAllArrows() );
				
		}
		//--- All selected arrows are set to be removed too.
		removeArrows.addAll( selectedArrows );
		
		//--- Disconnect nodes that had removed arrows connected to them.
		for( int i = 0; i < removeArrows.size(); i++ )
		{
			FlowConnectionArrow fa = removeArrows.elementAt( i );
			lookUpGrid.removeFlowGraphicFromGridInArea( fa ,fa.getArea() );

			FlowBox sourceBox = fa.getSourceBox();
			FlowBox targetBox = fa.getTargetBox();
			
			sourceBox.removeArrow( fa );
			targetBox.removeArrow( fa );

			RenderNode target = targetBox.getRenderNode();
			RenderNode source = sourceBox.getRenderNode();
			
			FlowBoxConnectionPoint sourceCP = fa.getSourceCP();
			FlowBoxConnectionPoint targetCP = fa.getTargetCP();

 			sourceCP.setActive( false );
			targetCP.setActive( false );

			sourceBox.redrawConnectionPoints();
			targetBox.redrawConnectionPoints();

			int sourceCIndex = sourceCP.getIndex();
			int targetCIndex = targetCP.getIndex();

			FlowController.disconnectNodes( source, target, sourceCIndex, targetCIndex);
		}
	
		//---- Delete the nodes from renderflow
		//FlowController.deleteRenderNodes( removeNodes );
			
		arrows.removeAll( removeArrows );

		//--- Clear selectedBoxes
		selectedBoxes.removeAllElements();

		//--- Clear selected arrows
		selectedArrows.removeAllElements();

		MultiDeleteUndoEdit undoEdit = new MultiDeleteUndoEdit( removeNodes, deletedBoxes, removeArrows );
		PhantomUndoManager.addUndoEdit( undoEdit );

		FlowController.updateViewTargetNode();
		UpdateController.updateCurrentFrameDisplayers( false );
		repaint();

		return removeNodes;
	}

	public void deleteBoxes( Vector<RenderNode> deleteNodes )
	{
		Vector<RenderNode> removeNodes = new Vector<RenderNode>();
		Vector<FlowConnectionArrow> removeArrows = new Vector<FlowConnectionArrow>();

		//--- Arrows and nodes are collected to be removed.
		for( RenderNode node : deleteNodes )
		{
			FlowBox b = getBox( node );
			//--- remove from main collection
			boxes.remove( b );
			selectedBoxes.remove( b );
			//--- remove from grid
			lookUpGrid.removeFlowGraphicFromGridInArea( b, b.getArea() );
			//--- 
			removeNodes.addElement( b.getRenderNode() );
			removeArrows.addAll( b.getAllArrows() );
				
		}
		
		//--- Disconnect nodes that had removed arrows connected to them.
		for( int i = 0; i < removeArrows.size(); i++ )
		{
			FlowConnectionArrow fa = (FlowConnectionArrow) removeArrows.elementAt( i );
			lookUpGrid.removeFlowGraphicFromGridInArea( fa, fa.getArea() );
			selectedArrows.remove( fa );

			FlowBox sourceBox = fa.getSourceBox();
			FlowBox targetBox = fa.getTargetBox();
			
			sourceBox.removeArrow( fa );
			targetBox.removeArrow( fa );

			RenderNode target = targetBox.getRenderNode();
			RenderNode source = sourceBox.getRenderNode();
			
			FlowBoxConnectionPoint sourceCP = fa.getSourceCP();
			FlowBoxConnectionPoint targetCP = fa.getTargetCP();

			sourceCP.setActive( false );
			targetCP.setActive( false );

			sourceBox.redrawConnectionPoints();
			targetBox.redrawConnectionPoints();

			int sourceCIndex = sourceCP.getIndex();
			int targetCIndex = targetCP.getIndex();

			FlowController.disconnectNodes( source, target, sourceCIndex, targetCIndex);
		}
	
		//---- Delete the nodes from renderflow
		//FlowController.deleteRenderNodes( removeNodes );
			
		arrows.removeAll( removeArrows );

		FlowController.updateViewTargetNode();

		repaint();
	}

	public Vector<RenderNode> getSelectedNodes()
	{
		Vector<RenderNode> selected = new Vector<RenderNode>();
		for( FlowBox b : selectedBoxes  )
			selected.addElement( b.getRenderNode() );

		return selected;
	}

	public Vector<FlowBox> getSelectedBoxes(){ return selectedBoxes; }

	public LookUpGrid getLookUpGrid(){ return lookUpGrid; }

	//--- NOTE: Does not remove arrow from connectionpoints, that must be done also.
	public void removeArrow( FlowConnectionArrow arrow )
	{
		lookUpGrid.removeFlowGraphicFromGridInArea( arrow, arrow.getArea() );
		arrows.remove( arrow );
		selectedArrows.remove( arrow );
	}

	public void updateForViewTarget()
	{
		RenderNode viewTarget = FlowController.getViewTarget();
		viewTargetBox = getBox( viewTarget );
	}

	public void updateForEditTarget()
	{
		RenderNode editTarget = FlowController.getEditTarget();
		editTargetBox = getBox( editTarget );
	}

	//--- Deletes arrows and last asks FlowBox to change Connection point size.
	public void nodeOutputsNumberChanged( RenderNode node, int outputsNumber )
	{
		//--- Get flow box.
		FlowBox sourceBox = null;
		for( FlowBox box : boxes )
			if( box.getRenderNode() == node ) sourceBox = box;

		//--- Get arrows connected to outputs and NOT in new range.
		Vector<FlowBoxConnectionPoint> outputPoints = sourceBox.getOutputConnectionPoints();
		Vector<FlowConnectionArrow> arrowsToDelete = new Vector<FlowConnectionArrow>();
		for( FlowBoxConnectionPoint cp : outputPoints )
		{
			int index = cp.getIndex();
			if( index >= outputsNumber && cp.getArrow() != null  ) 
								arrowsToDelete.add( cp.getArrow() );
		}
		//--- Output points are accesssed in FlowBox.changeOutputsNumber(...) too.
		outputPoints = null;

		//--- Delete arrows.
		for( FlowConnectionArrow fa : arrowsToDelete )
		{
			lookUpGrid.removeFlowGraphicFromGridInArea( fa ,fa.getArea() );
			FlowBox targetBox = fa.getTargetBox();
			sourceBox.removeArrow( fa );
			targetBox.removeArrow( fa );
			arrows.remove( fa );
		}

		//--- Update flowbox connection points.
		sourceBox.changeOutputsNumber( outputsNumber );
		sourceBox.setOutputArrowsToPoints();
	}

	//--- returns Vector of all FlowBoxes
	public Vector<FlowBox> getBoxes(){ return boxes; }
	//--- Return FlowBox containing node.
	public FlowBox getBox( RenderNode node )
	{
		for( FlowBox box : boxes )
			if( box.getRenderNode() == node )
				return box;
		return null;
	} 

	public void reCreateBox( RenderNode node )
	{
		FlowBox box = getBox( node );
		box.setRenderNode( node );
		box.preRender();
		repaint();
	}

	//--- Creates all arrows using boxes an nodes info.
	public void createArrows()
	{
		arrows = new Vector<FlowConnectionArrow>();

		for( FlowBox targetBox : boxes )
		{
			RenderNode targetNode = targetBox.getRenderNode();
			Vector<RenderNode> sourceNodes = targetNode.getSources();
			//--- Only sources need to be handled because all arrows have sources.
			for( int i = 0; i < sourceNodes.size(); i++ )
			{
				RenderNode sourceNode = sourceNodes.elementAt( i );
				if( sourceNode != null )
				{
					FlowBox sourceBox = getBox( sourceNode );
					Vector<RenderNode> sourceTargets = sourceNode.getActiveTargets();
					int tInxInSource = sourceTargets.indexOf( targetNode );

					FlowBoxConnectionPoint sCP = 
						sourceBox.getOutputCP( tInxInSource );
					FlowBoxConnectionPoint tCP = 
						targetBox.getInputCP( i );

					FlowConnectionArrow addArrow = 
						new FlowConnectionArrow( sourceBox, targetBox );

					addArrow.setConnectionPoints( sCP, tCP );

					sCP.setArrow( addArrow );
					tCP.setArrow( addArrow );

					addArrow.updatePosition();

					arrows.add( addArrow );
					lookUpGrid.addFlowGraphicToGrid( addArrow );
				}
			}
		}
	}
	//--- Get all selected iops.
	//--- Called from RenderFlowViewButtons when sending stuff to timeline.
	public Vector<ImageOperation> getAllSelectedIOPs()
	{
		Vector<ImageOperation> retVect = new Vector<ImageOperation>();
		for( FlowBox b : selectedBoxes )
			retVect.addElement( b.getImageOperation() );

		return retVect;
	}
	//--- Called when targetIOP selection made elsewhere.
	public void setAsOnlySelected( ImageOperation iop )
	{
		deselectAll();
		if( iop == null )
		{
			repaint();
			return;
		}
		for( FlowBox box : boxes )
			if( box.getImageOperation() == iop )
			{
				selectedBoxes.addElement( box );
				box.setSelected( true );
			}
		repaint();
	}

	public void deselectEverything()
	{
		//--- Set all boxes unselected and draw into BG
		for( FlowBox box : boxes )
			box.setSelected( false );
		selectedBoxes.clear();

		for( FlowConnectionArrow arrow : arrows )
		{
			arrow.setConnectionPointsSelected( false );
			arrow.getSourceCP().redrawIntoParentBox();
			arrow.getTargetCP().redrawIntoParentBox();
		}
		selectedArrows.clear();
	}

	public void deselectAll()
	{
		deselectBoxes();
		deselectArrows();
	}

	public void selectAll()
	{
		selectedBoxes.clear();
		//--- Set all boxes unselected and draw into BG
		for( FlowBox box : boxes )
		{
			box.setSelected( true );
			selectedBoxes.add( box );
		}
	}

	private void deselectBoxes()
	{
		//--- Set all boxes unselected and draw into BG
		for( FlowBox box : selectedBoxes )
			box.setSelected( false );

		selectedBoxes.clear();
	}
	//--- Clear selected arrows.
	private void deselectArrows()
	{
		for( FlowConnectionArrow sArrow : selectedArrows )
		{
			sArrow.setConnectionPointsSelected( false );
			sArrow.getSourceCP().redrawIntoParentBox();
			sArrow.getTargetCP().redrawIntoParentBox();
		}
		selectedArrows.clear();
	}
	//--- Ask movingGraphics to be cleared after next repaint.
	//--- Called by MoveMode
	public void setMovingStopped(){ MOVING_STOPPED = true; }
	//--- Arranges boxes always on top and returns orderer Vector
	private Vector<FlowGraphic>  getArrangedMovingGraphics()
	{
		Vector<FlowGraphic> retVec = new Vector<FlowGraphic>();
		for( FlowGraphic g : movingGraphics )
			if( g instanceof FlowBox ) retVec.add( g );
			else retVec.add( 0, g );

		return retVec;
	}
	
	//------------------------------------------------ MOUSE EVENTS HANDLING
	public void mousePressed(MouseEvent e)
	{
		requestFocusInWindow();
		PreviewController.stopPlaybackRequest();

		if( editMode != null )
			return;

		//--- Get mouse coordinates
		int mouseX = e.getX();
		int mouseY = e.getY();
		mediaPopUpX = mouseX;
		mediaPopUpY = mouseY;
		//--- Check if any box was selected.
		FlowBox selectedBox = lookUpGrid.getBox( mouseX, mouseY );
		if( !KeyStatus.ctrlIsPressed() && e.getButton() == MouseEvent.BUTTON1 && selectedBox == null ) 
			deselectAll();

		if( selectedBox != null )
		{
			if( ParamEditController.getParamEditIOP() != selectedBox.getImageOperation() )
			{
				ParamEditController.displayEditFrame( selectedBox.getImageOperation() );
			}
		}
		//--- Context menu for right mouse
		if( e.getButton() == MouseEvent.BUTTON3 && selectedBox == null )
		{
			deselectAll();
			showEditorPopUp( e );

			editMode = new NoEditMode();
		}
		
		//--- Check if any connection points was selected
		FlowBoxConnectionPoint cp = null;
		if( selectedBox != null )
			cp = selectedBox.getConnectionPoint(mouseX, mouseY );

		//--- Context menu for right mouse
		if( e.getButton() == MouseEvent.BUTTON3 && selectedBox != null )
		{
			deselectAll();
			selectedBox.setSelected( true );
			selectedBoxes.addElement( selectedBox );

			popupSource = selectedBox;
			showPopUp( e );

			editMode = new NoEditMode();
		}
		//--- Handle arrow draw events
		else if( selectedBox != null && cp != null && cp.getArrow() == null )
		{
			deselectAll();
			editMode = new ArrowDrawMode( this, lookUpGrid );
		}
		//--- Handle arrow select events
		else if( selectedBox != null && cp != null && cp.getArrow() != null )
		{
			deselectAll();
			editMode = new ArrowSelectMode( this, lookUpGrid );
		}
		//--- Handle move events.
		else if( selectedBox != null && cp == null )
		{
			//--- Box must be selected to be movable.
			if( !selectedBox.isSelected() )
			{
				if( !KeyStatus.ctrlIsPressed() ) 
					deselectAll();

				//--- Set selectedBox selected.
				selectedBox.setSelected( true );
				selectedBoxes.addElement( selectedBox );
			}
			//--- Displays selected iop in paramedit frame 
			//--- if param edit frame is visible.
			ImageOperation iop = selectedBox.getRenderNode().getImageOperation();
			UpdateController.editTargetIOPChanged( iop );
			
			editMode = new MoveMode( this, lookUpGrid );
		}
		//--- Handle box selection events.
		else editMode = new BoxSelectionMode( this );

		//--- Send mouse event to selected editMode to handle.
		editMode.mousePressed( e, selectedBox, cp  );

		repaint();
	}

	public void mouseDragged(MouseEvent e)
	{
		forceRange( e );
		if( editMode != null ) 
			editMode.mouseDragged( e );
		repaint();
	}
	
	public void mouseReleased(MouseEvent e)
	{
		forceRange( e );
		if( editMode != null ) 
			editMode.mouseReleased( e );
		repaint();
		if( selectedBoxes.size() > 0 ) UpdateController.editTargetIOPChanged( 
							selectedBoxes.elementAt(0).getImageOperation() );

		editMode = null;
	}
	
	//--- Double click opens in ParamEditor
	public void mouseClicked(MouseEvent e)
	{
		requestFocusInWindow();

		if( e.getClickCount() == 2 )
		{
			FlowBox selectedBox = lookUpGrid.getBox( e.getX(), e.getY() );
			if( selectedBox != null )
			{
				System.out.println("wwwwffrr");
				selectedBox.setSelected( true );
				if( !selectedBoxes.contains( selectedBox ) )
					selectedBoxes.addElement( selectedBox );
				repaint();
				ParamEditController.displayEditFrame( selectedBox.getImageOperation() );
			}
			System.out.println("eeegggee");
		}
	}
	public void mouseEntered(MouseEvent e){}
	public void mouseExited(MouseEvent e){}
	public void mouseMoved(MouseEvent e){}
	private void forceRange( MouseEvent e )
	{
		int dx = 0;
		int dy = 0;
		if( e.getX() < 0 ) dx = - e.getX();
		if( e.getY() < 0 ) dy = - e.getY();
		if( e.getX() > getWidth() ) dx = getWidth() - e.getX();
		if( e.getY() > getHeight() ) dy = getHeight() - e.getY();
		e.translatePoint( dx, dy );
	}

	//--------------------------------------------------------- POP UP
	private void showPopUp( MouseEvent e )
	{
		if ( popupSource.getImageOperation() instanceof MovingBlendedIOP )
		{
			replaceMedia.setEnabled( true );
			replaceMedia.setFont(DarkTheme.MENU_FONT);
		}
		else
		{
			replaceMedia.setEnabled( false );
			replaceMedia.setFont((GUIResources.BASIC_FONT_ITALIC_11));
		}
			
		popup.show( e.getComponent(), e.getX(), e.getY() );
	}

	private void showEditorPopUp( MouseEvent e )
	{
		editorPopup.show( e.getComponent(), e.getX(), e.getY() );
	}

	public void updateMediaMenu()
	{
		JMenu mediaMenu = ((JMenu) mediaList);
		mediaMenu.removeAll();

		Vector<FileSource> fileSources = ProjectController.getFileSources();
		
		
		if (fileSources.size() > 0)
		{
			AnimatorMenu.fillFileSourcesMenu(mediaMenu, this);
		}
		else
		{
			noRefs = new JMenuItem("No media sources");
			noRefs.setEnabled(false);
			noRefs.setFont(GUIResources.BASIC_FONT_ITALIC_11);
			mediaMenu.add(noRefs);
		}
		
		mediaMenu.addSeparator();
		
		addVideo = new JMenuItem("Add Video Clips...");
		addVideo.addActionListener(this);
		mediaMenu.add( addVideo );
		
		addImage  = new JMenuItem("Add Images...");
		addImage.addActionListener(this);
		mediaMenu.add( addImage );

		addImageSequence  = new JMenuItem("Add Image Sequence...");
		addImageSequence.addActionListener(this);
		mediaMenu.add( addImageSequence );
	}

	public void actionPerformed(ActionEvent e)
	{
		if( e.getSource() == deleteNode )
		{
			//FlowController.deleteSelected();
		}
		if( e.getSource() == renameNode )
		{
			//MenuActions.renameSelected();
		}
		if( e.getSource() == setAsrenderTarget )
		{
			FlowController.viewTargetPressed();
		}
		if( e.getSource() == cloneNode )
		{
			// use copy paste code to clone node
			Vector<RenderNode> nodes = getSelectedNodes();
			Object item = nodes.elementAt( 0 );
			KeyActionController.setSinglePasteItem( this, item );
			KeyActionController.pasteItems( this );
		}

		if( e.getSource() == replaceMedia )
		{
			FlowController.replaceMedia(selectedBoxes.elementAt(0).getRenderNode());
		}
		
		if( e.getSource() == freezeAllValues )
		{
			//MenuActions.freezeAllToCurrent();
		}

		if( e.getSource() instanceof IOPMenuItem )
		{
			IOPMenuItem source = ( IOPMenuItem ) e.getSource();
			//MenuActions.addIOP( source.getIopClassName(),  new Point(mediaPopUpX, mediaPopUpY) );
		}

		if( e.getSource() instanceof MediaMenuItem )
		{
			MediaMenuItem source =  ( MediaMenuItem ) e.getSource();
			//FlowController.addIOPFromFileSourceRightAway( source.getFileSource(), new Point(mediaPopUpX, mediaPopUpY));
			//FlowController.addToCenterFromFileSource( source.getFileSource() );
		}
		if( e.getSource() == addImage )
		{
			new Thread()
			{
				public void run()
				{
					UserActions.addSingleFileSources(FileSource.IMAGE_FILE, mediaPopUpX, mediaPopUpY);
				}
			}.start();
		}
		if( e.getSource() == addImageSequence )
		{
			new Thread()
			{
				public void run()
				{
					UserActions.addFileSequenceSource();
				}
			}.start();
		}
		if( e.getSource() == addVideo )
		{
			new Thread()
			{
				public void run()
				{
					UserActions.addSingleFileSources(FileSource.VIDEO_FILE, mediaPopUpX, mediaPopUpY);
				}
			}.start();
		}
	}

	//--------------------------------------------------------- PAINT METHODS
	//-- Component Paint method
	public void paintComponent( Graphics g )
	{
		Point viewPos = GUIComponents.animatorFrame.getScrollPos();
		Dimension viewSize = GUIComponents.animatorFrame.getViewPortSize();

		//--- Paint BG
		g.setColor( GUIColors.flowBGColor );
		g.fillRect( viewPos.x, viewPos.y, viewSize.width, viewSize.height);

		//--- When moving stops movingGraphics needs to be cleared.
		if( MOVING_STOPPED )
		{
			movingGraphics.clear();
			MOVING_STOPPED = false;
		}

		//--- Paint grid
		int grid_size = 60;
		int start_grid_x = viewPos.x / grid_size;
		int start_grid_y = viewPos.y / grid_size;
		int end_grid_x = (viewPos.x + viewSize.width) / grid_size + 1;
		int end_grid_y = (viewPos.y +  viewSize.height) / grid_size + 1;
				
		for (int row = start_grid_y; row < end_grid_y; row++ )
		{
			g.setColor( GUIColors.flowGridColor );
			g.drawLine( start_grid_x * grid_size, row * grid_size, end_grid_x * grid_size, row * grid_size );
		}
		
		for (int column = start_grid_x; column < end_grid_x; column++ )
		{
			g.setColor( GUIColors.flowGridColor );
			g.drawLine( column * grid_size, start_grid_y * grid_size, column * grid_size, end_grid_y * grid_size );
		}

		//--- Paint boxes
		Rectangle drawArea = new Rectangle( viewPos.x, viewPos.y,
							viewSize.width, viewSize.height);
		Vector<FlowGraphic> drawGraphics = lookUpGrid.getFlowGraphicsFromArea( drawArea );
		drawGraphics.addAll( getArrangedMovingGraphics() );
		for( FlowGraphic dg : drawGraphics ) 
			dg.draw( g );

		//--- Paint edit target img, some placement constants
		if( editTargetBox != null )
			g.drawImage( editTargetInFlow, editTargetBox.getX() - 5, editTargetBox.getY() - 5, null );

		//--- Paint view target img, some placement constants
		if( viewTargetBox != null )
			g.drawImage( viewTargetInFlow, viewTargetBox.getX() - 5, viewTargetBox.getY() - 5, null );

		g.dispose();
	}
	*/
}//end class
