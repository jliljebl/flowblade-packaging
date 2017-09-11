package animator.phantom.gui.view.editlayer;

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
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.util.Vector;

import animator.phantom.controller.EditorsController;
import animator.phantom.controller.TimeLineController;
import animator.phantom.controller.UpdateController;
import animator.phantom.gui.view.EditPoint;
import animator.phantom.gui.view.SVec;
import animator.phantom.gui.view.ViewRenderUtils;
import animator.phantom.renderer.ImageOperation;
import animator.phantom.renderer.parent.TransformClone;

/**
* Edit layer for editing position, rotation and scaling of a rectangular shape that has an anchor point.
* <p> 
*  Use to edit all affine transformable rectangular image sources.
*/
public abstract class AnchorRectEditLayer extends ViewEditorLayer
{
	//--- EDIT MODES
	/**
	* No editing happening.
	*/
	protected final int NO_EDIT = 0;
	/**
	* User action rotates rectangle.
	*/
	protected final int ROTATE_EDIT = 1;
	/**
	* User action moves rectangle.
	*/
	protected final int MOVE_EDIT = 2;
	/**
	* User actions scales width AND height of rectangle.
	*/
	protected final int CORNER_EDIT = 3;
	/**
	* User actions scales width OR height of rectangle.
	*/
	protected final int HANDLE_EDIT = 4;
	/**
	* Current edit mode.
	*/
	protected int state;
	/**
	* Used to avoid divisions by zero.
	*/
	protected final float MINIM_SCALE = 0.000001f;

	//--- Vectors used to guide edit actions.
	private SVec handleGuide1;
	private SVec handleGuide2;
	private SVec anchorGuide1;
	private SVec anchorGuide2;
	
	//--- Handles used in edit action.
	private EditPoint handle1;
	private EditPoint handle2;

	//--- Point used to calculate rotate action.
	private EditPoint rotoMid;

	/**
	* Shape being edited.
	*/
	private AnchorRectEditShape editShape;

	//--- Flag for committing undo, only done on release
	private boolean commitUndo = false;

	/**
	* Constructor with editor reference, ImageOperation and size of untransformed rectangular source.
	*/
	public AnchorRectEditLayer( ImageOperation iop, Rectangle rect )
	{
		super( iop );
		editShape = new AnchorRectEditShape( rect, iop, iop.getCoords() );
		registerShape( editShape );
	}
	/**
	* Called after current frames changes.
	*/
	public void frameChanged()
	{
		editShape.transformShape( getCurrentFrame() );
	}

	//---------------------------------------------------- MOUSE EVENT HANDLING
	/**
	* Handles mouse press and selects edit mode.
	*/
	public void mousePressed()
	{	
		int frame = getCurrentFrame();

		//--- Undo flag is CLEARED here so that value changes when dragging do not cause undos.
		commitUndo = false;

		//--- We will not play with if x/yscale == 0
		if( editShape.animCoords().xScale.getValue( frame ) == 0 )
		{
			editShape.animCoords().xScale.setValue( frame, MINIM_SCALE );//parenting noop
			editShape.transformShape( frame );
		}
		if( editShape.animCoords().yScale.getValue( frame ) == 0 )
		{
			editShape.animCoords().yScale.setValue( frame, MINIM_SCALE );//parenting noop
			editShape.transformShape( frame );
		}
		//--- Get possibly pressed edit point.
		lastPressedPoint = getEditPoint( mouseStartPoint );

		//--- Editor in move edit mode, edit modes in next block are	
		//--- sub modes of that mode.
		if( getMode() == ViewEditorLayer.MOVE_MODE )
		{
			//--- Point pressed
			if( lastPressedPoint != null )
			{
				//--- corner pressed
				if( editShape.isCornerPoint( lastPressedPoint ) )
				{
					//--- Get opposite corner hadle
					int index = editShape.getIndexOfPoint( lastPressedPoint );
					int opHandleIndex = ( index + 4) % 8;
					EditPoint oppHandle = editShape.getEditPoint( opHandleIndex );

					//--- Get next corner handles to that
					int opp1 = (opHandleIndex + 1) % 8;
					int opp2 = (opHandleIndex + 7) % 8;
					int end1 = (opHandleIndex + 2) % 8;
					int end2 = (opHandleIndex + 6) % 8;
	
					//--- Get handles and create two handle guides for them
					handle1 = editShape.getEditPoint( opp1 );// these are used to check if with or height to be changed.
					handle2 = editShape.getEditPoint( opp2 );
					EditPoint endP1 = editShape.getEditPoint( end1 );
					EditPoint endP2 = editShape.getEditPoint( end2 );
					//--- Create guides. 
					handleGuide1 = new SVec( oppHandle.getPos(), endP1.getPos() );
					handleGuide2 = new SVec( oppHandle.getPos(), endP2.getPos() );

					//--- Anchor projections
					anchorGuide1 = handleGuide1.getProjectionVec( editShape.getPosition( frame ) );
					anchorGuide2 = handleGuide2.getProjectionVec( editShape.getPosition( frame ) );
	
					state = CORNER_EDIT;
				}
				//--- mid handle pressed
				else
				{
					int opHandleIndex =
						( editShape.getIndexOfPoint( lastPressedPoint ) + 4) % 8;
			
					handleGuide1 = 	new SVec( editShape.getEditPoint( opHandleIndex ).getPos(),
								 lastPressedPoint.getPos() );
					anchorGuide1 = handleGuide1.getProjectionVec( editShape.getPosition( frame ) );
					anchorGuide2 = handleGuide1.getDistanceVec( editShape.getPosition( frame ) );
					state = HANDLE_EDIT;
				}
				
			}
			//--- Area pressed
			else if( editShape.pointInArea( mouseStartPoint ) )
			{
				editShape.saveMoveStartValues( frame );
				state = MOVE_EDIT;
			}
		}

		//--- Editor in rotate mode.
		if( getMode() == ViewEditorLayer.ROTATE_MODE )
		{
			//--- if point pressed, start edit.
			if( lastPressedPoint != null )
			{
				state = ROTATE_EDIT;
				//-- This being EditPoint and not Point2D.Float is conceptually wrong
				rotoMid = new EditPoint( editShape.getChildPoint( editShape.getPosition( frame ) ) );
				editShape.saveMoveStartValues( frame );
			}
		}

		EditorsController.displayCurrentInViewEditor( true );
	}
	/**
	* Handles mouse drag and updates edit values.
	*/
	public void mouseDragged()
	{
		if( state == HANDLE_EDIT )
			updateHandleEdit();
		
		if( state == MOVE_EDIT )
			updateMoveEdit();

		if( state == CORNER_EDIT )
			updateCornerEdit();

		if( state == ROTATE_EDIT )
			updateRotateEdit();

		EditorsController.displayCurrentInViewEditor( true );
	}
	/**
	* Handles mouse release and sets flag to commit undo.
	*/
	public void mouseReleased()
	{
		//--- Undo flag is SET here so that undo is recorded.
		commitUndo = true;

		if( state == MOVE_EDIT )
			updateMoveEdit();

		if( state == HANDLE_EDIT )
			updateHandleEdit();

		if( state == CORNER_EDIT )
			updateCornerEdit();

		if( state == ROTATE_EDIT )
			updateRotateEdit();

		//--- Clear state
		handle1 = null;
		handle2 = null;
		handleGuide1 = null;
		handleGuide2 = null;
		clearMouseMoveData();
		state = NO_EDIT;

		//--- kf diamonds update
		iop.createKeyFramesDrawVector();
		TimeLineController.initClipsGUI();
		//--- value displayers update
		UpdateController.valueChangeUpdate( UpdateController.VIEW_EDIT );
	}

	private void updateHandleEdit()
	{
		int frame = getCurrentFrame();

		//--- Get handle length for normal projection point of mouse pos.
		float handleLength = handleGuide1.getLength();
		//-- No flipping here allowed here
		if( handleLength < 0 )
			handleGuide1.setZeroLength();

		float ptLength = getScaledLength( editShape.getChildLength( handleGuide1.getStartPos(), handleGuide1.getEndPos() ) );

		//--- See which type handle, calculate scale and set.
		if( editShape.isWidthHandle( lastPressedPoint  ) )
		{
			float unScaled = (float) editShape.getRect().width;
			float eScaled = getScaledLength( unScaled );
			float scale = legalizeScale( ptLength / eScaled );
			iop.getCoords().xScale.setValue( frame, editShape.getChildXScale( scale * 100.0f,frame ) );

		}
		else // height handle
		{
			float unScaled = (float) editShape.getRect().height;
			float eScaled = getScaledLength( unScaled );
			float scale = legalizeScale( ptLength / eScaled );
			iop.getCoords().yScale.setValue( frame, editShape.getChildYScale( scale * 100.0f, frame ) );
		}

		//--- Calculate position after scaling,
		//--- Anchor point stays in same position relative to image.
		SVec newProjVec = anchorGuide1.getMultipliedSVec( handleGuide1.getScaleFactor() );
		SVec posVec = newProjVec.getCombinedVec( anchorGuide2 );
		Point2D.Float newPos = posVec.getEndPos();

		iop.getCoords().x.setValue( frame, editShape.getChildX( newPos.x, newPos.y, frame) );
		iop.getCoords().y.setValue( frame, editShape.getChildY( newPos.y, newPos.x, frame) );

		if( commitUndo )	
		{
			iop.getCoords().x.registerUndo();
			iop.getCoords().y.registerUndo( false );//to pack all as single undo;
			iop.getCoords().xScale.registerUndo( false );
			iop.getCoords().yScale.registerUndo( false );
		}
	}

	private void updateMoveEdit()
	{
		int frame = getCurrentFrame();

		//--- c in real space, streight if no parent, transformed if has parent.
		TransformClone c = editShape.moveStartValues;

		//--- mouse point in real space is ptrans space for delta calculation.
		Point2D.Float ptStart = editShape.getChildPoint( mouseStartPoint );
		Point2D.Float ptEnd = editShape.getChildPoint( mouseCurrentPoint );

		//--- start c to ptrans space, add delta, get child value
		float x = editShape.getChildX( c.x.getValue( 0 ), c.y.getValue( 0 ), frame ) +  ptEnd.x - ptStart.x;
		float y = editShape.getChildY( c.y.getValue( 0 ), c.x.getValue( 0 ), frame ) +  ptEnd.y - ptStart.y;

		iop.getCoords().x.setValue( frame, x );
		iop.getCoords().y.setValue( frame, y );

		if( commitUndo )	
		{
			iop.getCoords().x.registerUndo();
			iop.getCoords().y.registerUndo( false );//to pack as single undo
		}
	}

	private void updateCornerEdit()
	{
		int frame = getCurrentFrame();

		handleGuide1.setSnappedEndPoint( mouseCurrentPoint );
		handleGuide2.setSnappedEndPoint( mouseCurrentPoint );

		float hLength1 = handleGuide1.getLength();
		float hLength2 = handleGuide2.getLength();

		if( hLength1 < 0 )
			handleGuide1.setZeroLength();
		if( hLength2 < 0 )
			handleGuide2.setZeroLength();

		float ptLength1 = getScaledLength( editShape.getChildLength( handleGuide1.getStartPos(), handleGuide1.getEndPos() ) );
		float ptLength2 = getScaledLength( editShape.getChildLength( handleGuide2.getStartPos(), handleGuide2.getEndPos() ) );

		//--- 
		float unScaledWidth = getScaledLength( (float) editShape.getRect().width );
		float unScaledHeight =  getScaledLength( (float) editShape.getRect().height );
		
		//--- handle1
		if( !editShape.isWidthHandle( handle1 ) )
		{
			float scale = legalizeScale( ptLength1 / unScaledWidth );
			iop.getCoords().xScale.setValue( frame, editShape.getChildXScale( scale * 100.0f, frame ) );
		}
		else
		{
			float scale = legalizeScale( ptLength1 /  unScaledHeight );
			iop.getCoords().yScale.setValue( frame,  editShape.getChildYScale( scale * 100.0f, frame ) );
		}

		//--- handle2
		if( !editShape.isWidthHandle( handle2 ) )
		{
			float scale = legalizeScale( ptLength2 / unScaledWidth );
			iop.getCoords().xScale.setValue( frame, editShape.getChildXScale( scale * 100.0f, frame ) );
		}
		else
		{
			float scale = legalizeScale( ptLength2 /  unScaledHeight );
			iop.getCoords().yScale.setValue( frame, editShape.getChildYScale( scale * 100.0f, frame ) );
		}

		//--- Set position
		SVec proj1 = anchorGuide1.getMultipliedSVec( handleGuide1.getScaleFactor() );
		SVec proj2 = anchorGuide2.getMultipliedSVec( handleGuide2.getScaleFactor() );
		SVec comb = proj1.getCombinedVec( proj2 );
		Point2D.Float newPos = comb.getEndPos();

		iop.getCoords().x.setValue( frame, editShape.getChildX( newPos.x, newPos.y, frame ));
		iop.getCoords().y.setValue( frame, editShape.getChildY( newPos.y, newPos.x, frame ));

		if( commitUndo )	
		{
			iop.getCoords().x.registerUndo();
			iop.getCoords().y.registerUndo( false );//to pack as single undo;
			iop.getCoords().xScale.registerUndo( false );
			iop.getCoords().yScale.registerUndo( false );
		}
	}
	
	private void updateRotateEdit()
	{
		int frame = getCurrentFrame();

		//--- Get angle change. all values to ptans space.

		float angleChange = getMouseRotationAngle( 	rotoMid,
								editShape.getChildPoint( mouseStartPoint ),
								editShape.getChildPoint( mouseCurrentPoint ) );
		//--- Get start value. in real space 
		TransformClone c = editShape.moveStartValues;
		float startRotation = c.rotation.getValue(0);

		//--- add delta and convert to ptrans space
		iop.getCoords().rotation.setValue( frame, editShape.getChildRotation( startRotation + angleChange, frame ) );
		if( commitUndo ) iop.getCoords().rotation.registerUndo();
	}

	private float legalizeScale( float scale )
	{
		if( scale <= 0 ) return MINIM_SCALE;
		else return scale;
	}
	
	/**
	* Updates handles display.
	*/
	public void modeChanged()
	{
		switch( getMode() )
		{
			case ViewEditorLayer.MOVE_MODE:
				editShape.setDisplayType( EditPoint.MOVE_HANDLE );
				break;

			case ViewEditorLayer.ROTATE_MODE:
				editShape.setDisplayType( EditPoint.ROTATE_HANDLE );
				break;
			
			default:
				editShape.setDisplayType( EditPoint.MOVE_HANDLE );
		}
	}
	//--------------------------------------------------------- PAINT
	/**
	* Paints layer.
	* @param g2 Paint target graphics object.
	*/
	public void paintLayer( Graphics2D g2 )
	{
		Vector<EditPoint> panelPoints = 
			getPanelCoordinatesEditPoints( editShape.getEditPoints() );

		if( isActive ) 
			ViewRenderUtils.drawPoints( g2, panelPoints );
		Color lines = getDrawColor();
		ViewRenderUtils.drawPolygon( g2, panelPoints, lines );

		if( isActive )
		{
			EditPoint panelMid = getPanelCoordinatesEditPoint( editShape.getAnchorPoint() );
			panelMid.setColor( Color.red );
			panelMid.paintPoint( g2 );
		}
	}

}//end class
