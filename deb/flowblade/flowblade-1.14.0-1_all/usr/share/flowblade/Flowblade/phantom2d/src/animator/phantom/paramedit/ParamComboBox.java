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
import java.awt.Font;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import animator.phantom.gui.GUIResources;
import animator.phantom.renderer.param.Param;

/** 
* Abstract base class for GUI components used to select between options.
* Notifies provided listener of selection change. 
* Listeners must set param values and register undos.
*/
public abstract class ParamComboBox extends JPanel
{
	/**
	* Combo box component.
	*/
	protected JComboBox<String> comboBox;
	/**
	* Text label component.
	*/
	protected JLabel textLabel;
	JPanel rightPanel;
	JPanel leftPanel;

	/**
	* Initialises component and sets parameter name.
	* @param paramName Displayed name for editor and parameter. Set to be parameter name
	* @param options Options texts.
	* @param listener Selection change listener.
	* @param p Parameter that is named here with paramName.
	*/
	public void initComponent( String paramName, String[] options, ActionListener listener, Param p )
	{
		p.setParamName( paramName );
		initComponent( paramName, options, listener );
	}
	/**
	* Initialises component.
	* @param text Displayed name for editor and parameter. Parameter name is not set.
	* @param options Options texts.
	* @param listener Selection change listener.
	*/
	public void initComponent( String text, String[] options, ActionListener listener )
	{
		leftPanel = new JPanel();
		rightPanel = new JPanel();

		leftPanel.setLayout( new BoxLayout( leftPanel, BoxLayout.X_AXIS) );
		leftPanel.setPreferredSize( ParamEditResources.EDIT_ROW_HALF_SIZE );

		rightPanel.setLayout( new BoxLayout( rightPanel, BoxLayout.X_AXIS) );
		rightPanel.setPreferredSize( ParamEditResources.EDIT_ROW_HALF_SIZE );

		makeAndPlaceComponents( text, options, listener );

		setLayout( new BoxLayout( this, BoxLayout.X_AXIS) );
		add( leftPanel );
		add( rightPanel );

		setPreferredSize( ParamEditResources.EDIT_ROW_SIZE );
		setMaximumSize( ParamEditResources.EDIT_ROW_SIZE );
	}
	
	//--- Creates gui components and puts them into existing layout.
	private void makeAndPlaceComponents( String text, String[] options, ActionListener listener )
	{
		//--- Left side
		textLabel = new JLabel( text );
		textLabel.setFont( GUIResources.PARAM_EDIT_LABEL_FONT );
		leftPanel.removeAll();
		leftPanel.add( Box.createHorizontalGlue() );
		leftPanel.add( textLabel );
		leftPanel.add( Box.createRigidArea( new Dimension( ParamEditResources.PARAM_MID_GAP, 6 ) ) );
		//--- Right side
		comboBox = new JComboBox<String>( options );
		comboBox.addActionListener( listener );
		comboBox.setFont( GUIResources.BASIC_FONT_12 );
		rightPanel.removeAll();
		rightPanel.add( comboBox );
		rightPanel.add( Box.createHorizontalGlue() );
	}
	/**
	* Sets font used to display options
	* @param font Options font.
	*/
	public void setBoxFont( Font font ){ comboBox.setFont( font ); }
	/**
	* Sets maximum number of combo box options.
	* @param rows Maximum number of options
	*/
	public void setMaxComboRows( int rows ){ comboBox.setMaximumRowCount( rows ); }
	/**
	* Gets combo box component
	* @return Combo box.
	*/
	public JComboBox<String> getComboBox(){ return comboBox; }
	/**
	* Gets selected index.
	* @return Selected index of combo box options.
	*/
	public int getSelectedIndex(){ return comboBox.getSelectedIndex(); }
	/**
	* Sets selected index of combobox.
	* @param newIndex New selected index of combo box options.
	*/
	public void setSelectedIndex( int newIndex ){ comboBox.setSelectedIndex( newIndex ); }

	
	public void setTransparent()
	{
		setOpaque( false );
		comboBox.setOpaque( false );
		textLabel.setOpaque( false );
		rightPanel.setOpaque( false );
		leftPanel.setOpaque( false );
	}

}//end class
