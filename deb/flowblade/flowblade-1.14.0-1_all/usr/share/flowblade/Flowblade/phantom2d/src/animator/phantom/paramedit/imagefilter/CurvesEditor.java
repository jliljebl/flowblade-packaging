package animator.phantom.paramedit.imagefilter;

/*
    Copyright Janne Liljeblad

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
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import animator.phantom.bezier.CRCurve;
import animator.phantom.controller.UpdateController;
import animator.phantom.gui.GUIResources;
import animator.phantom.paramedit.BoxEditorListener;
import animator.phantom.paramedit.ParamEditResources;
import animator.phantom.paramedit.UndoListener;
import animator.phantom.renderer.imagefilter.CurvesIOP;
import animator.phantom.renderer.param.CRCurveParam;

//--- Special editor component for editing CurvesIOP
public class CurvesEditor extends JPanel implements ActionListener, BoxEditorListener, UndoListener
{
	private CurvesIOP curvesIOP;
	private CRCurve curve;
	private CRCurveParam param;
	private Point lastPoint;
	private boolean editOn = false;

	private JComboBox<Object> comboBox;
	private JLabel textLabel;
	private JPanel rightPanel;
	private JPanel leftPanel;
	private JPanel comboPanel;
	private CurvesBoxEditor boxEditor;

	private static final int RGB = 0;
	private static final int R = 1;
	private static final int G = 2;
	private static final int B = 3;

	private static final String[] channeloptions = {"RGB","Red","Green","Blue" };

	public CurvesEditor( String text, CurvesIOP curvesIOP )
	{
		this.curvesIOP = curvesIOP;
		curve = curvesIOP.gammac.curve;
		param = curvesIOP.gammac;
		boxEditor = new CurvesBoxEditor( curve, 256, 256, this  );
		Dimension bsize = new Dimension( ParamEditResources.EDIT_ROW_SIZE.width, 270 );
		
		initCombo( "Channel", channeloptions );

		setLayout( new BoxLayout( this, BoxLayout.Y_AXIS) );
		add( Box.createRigidArea( new Dimension( 0, 5 ) ) );
		add( comboPanel );
		add( Box.createRigidArea( new Dimension( 0, 5 ) ) );
		add( boxEditor );
		add( Box.createRigidArea( new Dimension( 0, 5 ) ) );

		Dimension psize =  new Dimension( bsize.width, bsize.height + 32 );
		setPreferredSize( psize );
		setMaximumSize( psize );
	}

	public void initCombo( String text, String[] options )
	{
		leftPanel = new JPanel();
		rightPanel = new JPanel();
		comboPanel = new JPanel();

		leftPanel.setLayout( new BoxLayout( leftPanel, BoxLayout.X_AXIS) );
		leftPanel.setPreferredSize( ParamEditResources.EDIT_ROW_HALF_SIZE );

		rightPanel.setLayout( new BoxLayout( rightPanel, BoxLayout.X_AXIS) );
		rightPanel.setPreferredSize( ParamEditResources.EDIT_ROW_HALF_SIZE );

		textLabel = new JLabel( text );
		textLabel.setFont( GUIResources.PARAM_EDIT_LABEL_FONT );
		leftPanel.removeAll();
		leftPanel.add( textLabel );
		leftPanel.add( Box.createHorizontalGlue() );

		comboBox = new JComboBox<Object>( options );
		comboBox.addActionListener( this );
		comboBox.setFont( GUIResources.BASIC_FONT_12 );
		rightPanel.removeAll();
		rightPanel.add( comboBox );
		rightPanel.add( Box.createHorizontalGlue() );

		comboPanel.setLayout( new BoxLayout( comboPanel, BoxLayout.X_AXIS) );
		comboPanel.add( leftPanel );
		comboPanel.add( rightPanel );
		comboPanel.setPreferredSize( ParamEditResources.EDIT_ROW_SIZE );
		comboPanel.setMaximumSize( ParamEditResources.EDIT_ROW_SIZE );
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
		UpdateController.valueChangeUpdate( UpdateController.PARAM_EDIT );
		repaint();
	}

	public void undoDone()
	{
		curve = param.curve;
		repaint();
	}
	//--- change the curve being edited
	public void actionPerformed( ActionEvent e )
	{
		if( comboBox.getSelectedIndex() == RGB )
		{
			boxEditor.setCurve( curvesIOP.gammac.curve, Color.black );
			curve = curvesIOP.gammac.curve;
			param = curvesIOP.gammac;
		}
		if( comboBox.getSelectedIndex() == R )
		{
			boxEditor.setCurve( curvesIOP.redc.curve, Color.red );
			curve = curvesIOP.redc.curve;
			param = curvesIOP.redc;
		}
		if( comboBox.getSelectedIndex() == G )
		{
 			boxEditor.setCurve( curvesIOP.greenc.curve, Color.green );
			curve = curvesIOP.greenc.curve;
			param = curvesIOP.greenc;
		}
		if( comboBox.getSelectedIndex() == B )
		{
 			boxEditor.setCurve( curvesIOP.bluec.curve, Color.blue );
			curve = curvesIOP.bluec.curve;
			param = curvesIOP.bluec;
		}

		repaint();
	}

}//end class