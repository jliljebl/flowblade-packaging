package animator.phantom.paramedit;

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
import java.awt.Point;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;

import animator.phantom.bezier.CRCurve;
import animator.phantom.controller.UpdateController;
import animator.phantom.gui.GUIResources;
import animator.phantom.paramedit.imagefilter.CurvesBoxEditor;
import animator.phantom.renderer.param.CRCurveParam;

/**
* A GUI editor component for setting the value of a <code>CRCurveParam</code> parameter. 
*/
public class SingleCurveEditor extends JPanel implements BoxEditorListener, UndoListener
{
	private CRCurve curve;
	private CRCurveParam param;
	private Point lastPoint;
	private boolean editOn = false;

	private CurvesBoxEditor boxEditor;

	private static final int DEFAULT_SIZE = 256;
	private static final int VALUE_SIZE = 256;

	private static final int DEFAULT_HEIGHT = 270;

	public SingleCurveEditor( String text, CRCurveParam param )
	{
		this( text, param, DEFAULT_SIZE );
	}

	public SingleCurveEditor( String text, CRCurveParam param, int size )
	{
		this( text, param, size, DEFAULT_HEIGHT );
	}

	public SingleCurveEditor( String text, CRCurveParam param, int size, int height )
	{
		this.param = param;
		this.curve = param.curve;

		boxEditor = new CurvesBoxEditor( curve, size, VALUE_SIZE, this  );
		Dimension bsize = new Dimension( ParamEditResources.EDIT_ROW_SIZE.width, height );

		JLabel textLabel = new JLabel( text );
		textLabel.setFont(  GUIResources.BASIC_FONT_12 );

		JPanel labelPanel = new JPanel();
		labelPanel.setLayout( new BoxLayout( labelPanel, BoxLayout.X_AXIS) );
		labelPanel.add( textLabel );
		labelPanel.add( Box.createHorizontalGlue()  );
		labelPanel.setPreferredSize( ParamEditResources.EDIT_ROW_SIZE );
		labelPanel.setMaximumSize( ParamEditResources.EDIT_ROW_SIZE );

		setLayout( new BoxLayout( this, BoxLayout.Y_AXIS) );
		add( Box.createRigidArea( new Dimension( 0, 2 ) ) );
		add( labelPanel );
		add( Box.createRigidArea( new Dimension( 0, 1 ) ) );
		add( boxEditor );
		add( Box.createRigidArea( new Dimension( 0, 5 ) ) );

		Dimension psize =  new Dimension( bsize.width, bsize.height + 32 );
		setPreferredSize( psize );
		setMaximumSize( psize );
	}

	//--------------------------------------- BOX EVENTS
	public void boxMousePress( Point p )
	{
		lastPoint = p;
		editOn = true;
		curve.removeRange( lastPoint.x - 3, lastPoint.x + 3 );
		curve.setCurvePoint( lastPoint );
		repaint();
	}

	public void boxMouseDrag( Point p )
	{
		if( !editOn ) return;
		curve.removeRange( lastPoint, p );
		curve.setCurvePoint( p );
		lastPoint = p;
		repaint();
	}

	public void boxMouseRelease( Point p )
	{
		if( !editOn ) return;
		curve.removeRange( lastPoint, p );
		curve.setCurvePoint( p );
		param.registerUndo();
		UpdateController.valueChangeUpdate( UpdateController.VIEW_EDIT );
		repaint();
	}

	public void undoDone()
	{
		curve = param.curve;
		repaint();
	}

}//end class