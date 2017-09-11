package animator.phantom.paramedit.imagefilter;

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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Vector;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import animator.phantom.controller.TimeLineController;
import animator.phantom.gui.GUIResources;
import animator.phantom.paramedit.AnimValueSliderEditor;
import animator.phantom.paramedit.ColorWheelEditor;
import animator.phantom.paramedit.ColorWheelListener;
import animator.phantom.paramedit.HalfRowTextEditor;
import animator.phantom.paramedit.ParamEditResources;
import animator.phantom.paramedit.UndoListener;
import animator.phantom.paramedit.panel.ParamEditPanel;
import animator.phantom.renderer.imagefilter.ColorCorrectorIOP;

public class ColorCorrectorEditPanel extends ParamEditPanel implements ColorWheelListener, ActionListener, PropertyChangeListener, UndoListener
{
	private static final int SHADOW = 0;
	private static final int MID = 1;
	private static final int HI = 2;

	private static final String[] rangeoptions = {"Shadows", "Midtones","Highlights" };

	private JLabel textLabel;
	private JPanel rightPanel;
	private JPanel leftPanel;
	private JPanel comboPanel;

	private JPanel rightPanel2;
	private JPanel leftPanel2;
	private JPanel editPanel;

	private String labelText = new String( "Tone range" );

	private JComboBox<Object> comboBox;
	private ColorWheelEditor wheel;

	private HalfRowTextEditor hue;
	private HalfRowTextEditor strength;

	private ColorCorrectorIOP cciop;

	public ColorCorrectorEditPanel( ColorCorrectorIOP cciop )
	{
		initParamEditPanel();

		this.cciop = cciop;

		leftPanel = new JPanel();
		rightPanel = new JPanel();
		comboPanel = new JPanel();

		leftPanel.setLayout( new BoxLayout( leftPanel, BoxLayout.X_AXIS) );
		leftPanel.setPreferredSize( ParamEditResources.EDIT_ROW_HALF_SIZE );

		rightPanel.setLayout( new BoxLayout( rightPanel, BoxLayout.X_AXIS) );
		rightPanel.setPreferredSize( ParamEditResources.EDIT_ROW_HALF_SIZE );

		textLabel = new JLabel( labelText );
		textLabel.setFont( GUIResources.PARAM_EDIT_LABEL_FONT );
		leftPanel.add( textLabel );
		leftPanel.add( Box.createHorizontalGlue() );

		comboBox = new JComboBox<Object>( rangeoptions );
		comboBox.addActionListener( this );
		comboBox.setFont( GUIResources.BASIC_FONT_12 );
		rightPanel.add( comboBox );
		rightPanel.add( Box.createHorizontalGlue() );

		comboPanel.setLayout( new BoxLayout( comboPanel, BoxLayout.X_AXIS) );
		comboPanel.add( leftPanel );
		comboPanel.add( rightPanel );
		comboPanel.setPreferredSize( ParamEditResources.EDIT_ROW_SIZE );
		comboPanel.setMaximumSize( ParamEditResources.EDIT_ROW_SIZE );

		leftPanel2 = new JPanel();
		rightPanel2 = new JPanel();
		editPanel = new JPanel();

		leftPanel2.setLayout( new BoxLayout( leftPanel2, BoxLayout.X_AXIS) );
		leftPanel2.setPreferredSize( ParamEditResources.EDIT_ROW_HALF_SIZE );

		rightPanel2.setLayout( new BoxLayout( rightPanel2, BoxLayout.X_AXIS) );
		rightPanel2.setPreferredSize( ParamEditResources.EDIT_ROW_HALF_SIZE );

		hue = new HalfRowTextEditor( "Hue", 50, this );
		strength = new HalfRowTextEditor("Strength", 50, this );
		
		float hueVal = cciop.shadowHue.getValue( TimeLineController.getCurrentFrame() );
		float distVal = cciop.shadowDistance.getValue( TimeLineController.getCurrentFrame() );
		valueChanged( hueVal, distVal, ColorWheelEditor.DRAG_EVENT  );

		leftPanel2.add( hue );
		rightPanel2.add( strength );

		editPanel.setLayout( new BoxLayout( editPanel, BoxLayout.X_AXIS) );
		editPanel.add( leftPanel2 );
		editPanel.add( rightPanel2 );
		editPanel.setPreferredSize( ParamEditResources.EDIT_ROW_SIZE );
		editPanel.setMaximumSize( ParamEditResources.EDIT_ROW_SIZE );

		wheel = new ColorWheelEditor( this, this );
		AnimValueSliderEditor lift = new AnimValueSliderEditor( "Lift", cciop.lift );
		AnimValueSliderEditor gain = new AnimValueSliderEditor( "Gain", cciop.gain );
		AnimValueSliderEditor gamma = new AnimValueSliderEditor( "Gamma", cciop.gamma );
		wheel.setCursor(  hueVal, distVal );

		//--- Tabs
		Vector<String> tabs = new Vector<String>();
		tabs.add( "Color Correct" );
		tabs.add( "LiftGainGamma" );
		setTabbedPanel( 350, tabs );

		addToTab( "Color Correct", comboPanel );
		addToTab( "Color Correct", wheel );
		addToTab( "Color Correct", editPanel );
		addToTab( "LiftGainGamma", lift );
		addToTab( "LiftGainGamma", gain );
		addToTab( "LiftGainGamma", gamma );
	}

	public void valueChanged( float angle, float distance, int eventType )
	{
		hue.setValue( angle );
		strength.setValue( distance );
		if( eventType == ColorWheelEditor.RELEASE_EVENT )
		{
			int range = comboBox.getSelectedIndex();
			cciop.colorCorrectionDone( range, angle, distance );
		}
	}

	//--- Get values from fields and set gui and 
	public void propertyChange( PropertyChangeEvent e )
	{
		wheel.setCursor( hue.getValue(), strength.getValue() );
		valueChanged( hue.getValue(), strength.getValue(), ColorWheelEditor.RELEASE_EVENT );
	}

	//--- Tone range selector listener
	public void actionPerformed( ActionEvent e )
	{
		setEditorsToCurrentValues();
	}

	public void setEditorsToCurrentValues()
	{
		float hueVal = 0;
		float distVal = 0;
	
		if( comboBox.getSelectedIndex() == SHADOW )
		{
			hueVal = cciop.shadowHue.getValue( TimeLineController.getCurrentFrame() );
			distVal = cciop.shadowDistance.getValue( TimeLineController.getCurrentFrame() );
		}

		if( comboBox.getSelectedIndex() == MID )
		{
			hueVal = cciop.midHue.getValue( TimeLineController.getCurrentFrame() );
			distVal = cciop.midDistance.getValue( TimeLineController.getCurrentFrame() );
		}

		if( comboBox.getSelectedIndex() == HI )
		{
			hueVal = cciop.hiHue.getValue( TimeLineController.getCurrentFrame() );
			distVal = cciop.hiDistance.getValue( TimeLineController.getCurrentFrame() );
		}

		wheel.setCursor(  hueVal, distVal );
		hue.setValue( hueVal );
		strength.setValue( distVal );

		repaint();
	}

	public void undoDone()
	{
		 setEditorsToCurrentValues();
	}

}//end class