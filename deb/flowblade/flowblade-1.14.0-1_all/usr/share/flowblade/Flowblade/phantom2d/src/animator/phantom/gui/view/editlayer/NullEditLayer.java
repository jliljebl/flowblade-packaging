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

import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.util.Vector;

import animator.phantom.controller.EditorsController;
import animator.phantom.gui.view.EditPoint;
import animator.phantom.gui.view.component.ViewControlButtons;
import animator.phantom.renderer.coordtransformer.NullIOP;
import animator.phantom.renderer.parent.TransformClone;

public class NullEditLayer extends ViewEditorLayer
{
	private	NullShape editShape;
	
	//--- EDIT MODES
	private final int NO_EDIT = 0;
	private final int MOVE_EDIT = 1;

	protected int state = NO_EDIT;

	public NullEditLayer( NullIOP iop, String name  )
	{
		super( iop  );

		editShape = new NullShape( iop );
		registerShape( editShape );
		setName( name );
	}

	//--- Extending may overload to set diferent buttons.
	public void setLayerButtons( ViewControlButtons buttons )
	{
		Vector<Integer> btns = new Vector<Integer>();
		btns.add( new Integer( ViewControlButtons.MOVE_B ));
		buttons.setModeButtons( btns );
	}

	//--- Udpate for frame change
	public void frameChanged()
	{
		editShape.transformShape( getCurrentFrame() );
	}

	//--- Udadate for mode change.
	//--- This only has one mode
	public void modeChanged(){}

	public void mousePressed()
	{
		int frame = getCurrentFrame();

		//--- Get possibly pressed edit point.
		lastPressedPoint = getEditPoint( mouseStartPoint );

		if( lastPressedPoint != null )
		{
			editShape.saveMoveStartValues( frame );
			state = MOVE_EDIT;
		}

		EditorsController.displayCurrentInViewEditor( true );
	}
	public void mouseDragged()
	{
		int frame = getCurrentFrame();

		if( state == MOVE_EDIT)
		{
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
		}

		EditorsController.displayCurrentInViewEditor( true );
	}
	public void mouseReleased()
	{
		int frame = getCurrentFrame();

		if( state == MOVE_EDIT)
		{
			//--- c in real space, streight if no parent, transformed if has parent.
			TransformClone c = editShape.moveStartValues;

			//--- mouse point in real space is ptrans space for delta calculation.
			//--- ptrans space  WTF!
			Point2D.Float ptStart = editShape.getChildPoint( mouseStartPoint );
			Point2D.Float ptEnd = editShape.getChildPoint( mouseCurrentPoint );

			//--- start c to ptrans space, add delta, get child value
			float x = editShape.getChildX( c.x.getValue( 0 ), c.y.getValue( 0 ), frame ) +  ptEnd.x - ptStart.x;
			float y = editShape.getChildY( c.y.getValue( 0 ), c.x.getValue( 0 ), frame ) +  ptEnd.y - ptStart.y;

			iop.getCoords().x.setValue( frame, x );
			iop.getCoords().y.setValue( frame, y );

			//--- Commit undo
			iop.getCoords().x.registerUndo();
			iop.getCoords().y.registerUndo( false );
		}

		mouseReleaseUpdate();
		lastPressedPoint = null;
		state = NO_EDIT;
	}

	public void paintLayer( Graphics2D g )
	{
		//--- Create lines lines
		Vector<EditPoint> line1 = new Vector<EditPoint>();
		line1.add( editShape.getEditPoints().elementAt( NullShape.UP ));
		line1.add( editShape.getEditPoints().elementAt( NullShape.DOWN ));
		Vector<EditPoint> line2 = new Vector<EditPoint>();
		line2.add( editShape.getEditPoints().elementAt( NullShape.LEFT ));
		line2.add( editShape.getEditPoints().elementAt( NullShape.RIGHT ));

		//--- Draw lines
		drawPolygon( g, line1, getDrawColor() );
		drawPolygon( g, line2, getDrawColor() );
	}

	//--- TO BE REMOVED
	public float getHitAreaSize(){ return 0; }//only points can be hit

}//emd class