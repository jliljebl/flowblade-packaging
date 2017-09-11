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

/**
* A GUI component used to turn plugins on and off. All plugins have this or some other 
* special panel the as the topmost component in the edit panel.
*/
public class OnOffPanel extends JPanel implements ItemListener, ActionListener
{
	private ImageOperation iop;
	private JLabel visLabel = new JLabel( GUIResources.getIcon( GUIResources.iris ) );
	private JCheckBox visBox = new JCheckBox();

	//--- Button
	//private JButton loopingButton = new JButton(  GUIResources.getIcon( GUIResources.transformGroup ));

	/**
	* Constructor with <code>ImageOperation</code>. Forplugins this ImageOperation is created when 
	* <code>initPlugin( int type )</code> is called.
	* @param iop <code>ImageOperation</code> to be turned on and off.
	*/
	public OnOffPanel( ImageOperation iop )
	{
		this.iop = iop;

		visLabel.setToolTipText( "Toggle On / Off" );
		visBox.addItemListener(this);

		//--- Prepare button
		//GUIResources.prepareMediumButton( loopingButton, this, "Launch looping dialog" );

		JPanel labelPanel = new JPanel();
		labelPanel.setLayout( new BoxLayout( labelPanel, BoxLayout.X_AXIS) );
		labelPanel.add( visLabel );
		labelPanel.add( Box.createHorizontalGlue() );

		JPanel swicthPanel = new JPanel();
		swicthPanel.setLayout( new BoxLayout( swicthPanel, BoxLayout.X_AXIS) );
		swicthPanel.add( visBox );
		swicthPanel.add( Box.createRigidArea( new Dimension( 50,0 ) ));
		//swicthPanel.add( loopingButton );
		swicthPanel.add( Box.createHorizontalGlue() );

		JPanel onPanel = new JPanel();
		onPanel.setLayout( new BoxLayout( onPanel, BoxLayout.Y_AXIS) );
		onPanel.add( labelPanel );
		onPanel.add( swicthPanel );

		setLayout( new BoxLayout( this, BoxLayout.X_AXIS) );
		add( onPanel );
		add( Box.createHorizontalGlue() );

		Dimension size = new Dimension( ParamEditResources.EDIT_ROW_SIZE.width, ParamEditResources.EDIT_ROW_SIZE.height * 2  + 5 );
 		setPreferredSize( size );
		setMaximumSize( size );

		//--- Set values
		if( iop.isOn() ) visBox.setSelected( true );
	}
	/**
	* Called when user edits check box.
	*/
	public void itemStateChanged(ItemEvent e)
 	{
		Object source = e.getItemSelectable();
		int stateChange = e.getStateChange();
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
		//	UserActions.manageLoopSettings( iop );
	}

}//end class