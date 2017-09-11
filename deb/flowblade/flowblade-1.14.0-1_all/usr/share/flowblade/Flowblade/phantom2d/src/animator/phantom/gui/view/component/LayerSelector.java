package animator.phantom.gui.view.component;

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
import java.util.Vector;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JPanel;

import animator.phantom.controller.EditorsController;
import animator.phantom.gui.GUIResources;
import animator.phantom.gui.view.editlayer.ViewEditorLayer;

public class LayerSelector extends JPanel implements ActionListener
{
	private JComboBox<String> comboBox;
	private static String NONE = "no layer selected";

	public LayerSelector()
	{
		String[] options = { NONE };
		comboBox = new JComboBox<String>( options );
		comboBox.addActionListener( this );
		comboBox.setFont( GUIResources.TOP_LEVEL_COMBO_FONT );
		comboBox.setPreferredSize( new Dimension(168, 24 ));

		setLayout(new BoxLayout( this, BoxLayout.Y_AXIS));
		add( Box.createVerticalGlue() );
		add( comboBox );
		add( Box.createVerticalGlue() );
	}

	public void update( Vector<ViewEditorLayer> layers, ViewEditorLayer editLayer )
	{
		comboBox.removeActionListener( this );
		comboBox.removeAllItems();
		comboBox.addItem( NONE );
		int current = -1;
		for( int i = 0; i < layers.size(); i++ )
		{
			if( layers.elementAt( i ) == editLayer ) current = i;
			comboBox.addItem( layers.elementAt( i ).getName() );
		}
		if( editLayer == null ) comboBox.setSelectedIndex( 0 );
		else comboBox.setSelectedIndex( current + 1 );
		comboBox.addActionListener( this );
		repaint();
	}

 	public void actionPerformed(ActionEvent e)
	{
		EditorsController.setActiveLayer( comboBox.getSelectedIndex() );
	}

}//end class
