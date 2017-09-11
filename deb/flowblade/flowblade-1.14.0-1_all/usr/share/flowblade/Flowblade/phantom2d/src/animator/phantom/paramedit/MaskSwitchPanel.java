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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import animator.phantom.controller.UpdateController;
import animator.phantom.gui.GUIResources;
import animator.phantom.renderer.ImageOperation;
import animator.phantom.renderer.SwitchData;
// anchor centering needs stuff from here 

/**
* A GUI component for turning nodes on / off, setting motion blur, looping and other functionality.
* All plugins have this or some other 
* special panel the as the topmost component in the edit panel.
*/
public class MaskSwitchPanel extends JPanel implements ItemListener, ActionListener
{
	private ImageOperation iop;
	private SwitchData switches;
	
	//--- Labels
	private JLabel mbLabel = new JLabel( GUIResources.getIcon( GUIResources.motionBlurLabel ) );
	private JLabel visLabel = new JLabel( GUIResources.getIcon( GUIResources.iris ) );

	//--- CheckBoxes
	private JCheckBox mbBox = new JCheckBox();
	private JCheckBox visBox = new JCheckBox();

	//--- Button
	//private JButton loopingButton = new JButton(  GUIResources.getIcon( GUIResources.transformGroup ));

	/**
	* Constructor with <code>ImageOperation</code>.
	*/ 
	public MaskSwitchPanel( ImageOperation iop )
	{
		this.iop = iop;
		this.switches = iop.switches;
		
		//--- Prepare labels
		mbLabel.setToolTipText( "Motion Blur" );
		visLabel.setToolTipText( "Toggle On / Off" );

		//--- Prepare button
		//GUIResources.prepareMediumButton( loopingButton, this, "Launch looping dialog" );

		//-- Prepare boxes.
		mbBox.addItemListener(this);
		visBox.addItemListener(this);

		JPanel p1 = new JPanel();
		p1.setLayout( new BoxLayout( p1, BoxLayout.Y_AXIS) );
		p1.add( visLabel );
		p1.add( visBox );

		JPanel p2 = new JPanel();
		p2.setLayout( new BoxLayout( p2, BoxLayout.Y_AXIS) );
		p2.add(  mbLabel );
		p2.add(  mbBox );

		setLayout( new BoxLayout( this, BoxLayout.X_AXIS) );
		add( p1 );
		add( p2 );
		add( Box.createRigidArea( new Dimension( 50,0 ) ));
		//add( loopingButton );
		add( Box.createHorizontalGlue() );

		Dimension size = new Dimension( ParamEditResources.EDIT_ROW_SIZE.width, ParamEditResources.EDIT_ROW_SIZE.height * 2 );
 		setPreferredSize( size );
		setMaximumSize( size );

		//--- Set values
		if( iop.isOn() ) visBox.setSelected( true );
		if( switches.motionBlur ) mbBox.setSelected( true );
	}
	/**
	* Handles user edit actions.
	*/
	public void itemStateChanged(ItemEvent e)
 	{
		Object source = e.getItemSelectable();
		int stateChange = e.getStateChange();
	
		if( source == mbBox )
		{
			if( stateChange == ItemEvent.SELECTED) switches.motionBlur = true;
			else switches.motionBlur = false;
			UpdateController.valueChangeUpdate();
		}
		if( source == visBox )
		{
			if( stateChange == ItemEvent.SELECTED) iop.setOnOffState( true );
			else iop.setOnOffState( false );
			UpdateController.valueChangeUpdate();
		}
	}
	
	/**
	* Used to set state programmatically after menu selection.
	*/
	public void setOnOff( boolean on )
	{
		visBox.setSelected( on );
	}

	/**
	* Handles user edit actions.
	*/
	public void actionPerformed(ActionEvent e)
	{
		//if( e.getSource() == loopingButton )
			//UserActions.manageLoopSettings( iop );
	}

}//end class
