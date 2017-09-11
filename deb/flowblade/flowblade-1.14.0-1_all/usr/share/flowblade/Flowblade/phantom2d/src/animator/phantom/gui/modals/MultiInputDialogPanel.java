package animator.phantom.gui.modals;

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
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

import animator.phantom.gui.GUIColors;

public class MultiInputDialogPanel extends JPanel
{
	public MultiInputDialogPanel( MInputPanel iPanel )
	{
		JPanel inputPane = new JPanel();
		inputPane.setLayout( new BoxLayout( inputPane, BoxLayout.Y_AXIS) );
		for( int i = 0; i < iPanel.areas.size(); i++ )
		{
			JPanel newArea = new JPanel();
			newArea.setLayout( new BoxLayout( newArea, BoxLayout.Y_AXIS) );
			EmptyBorder b1 = new EmptyBorder( new Insets( 0,0,0,0 )); 
			TitledBorder b2 = (TitledBorder) BorderFactory.createTitledBorder( 	b1,
									iPanel.areas.elementAt( i ).getTitle(),
									TitledBorder.CENTER,
									TitledBorder.TOP );
			b2.setTitleColor( GUIColors.grayTitle );
		
				//void 	setTitleFont(Font titleFont)
			Border b3 = BorderFactory.createCompoundBorder( b2, BorderFactory.createEmptyBorder( 8, 0, 10, 0));
			newArea.setBorder( b3 );
			fillPanel( newArea, iPanel.areas.elementAt( i ) );
			inputPane.add( newArea );
		}
		
		setLayout( new BoxLayout( this, BoxLayout.Y_AXIS) );
		add( inputPane );
	}
	
	//--- Put input fields into panel.
	private void fillPanel( JPanel p, MInputArea area )
	{
		for( int i = 0; i < area.fields.size(); i++ )
		{
			if( area.fields.elementAt( i ).topBuf != 0 )
				p.add( Box.createRigidArea( new Dimension( 0, area.fields.elementAt( i ).topBuf ) ) );
			p.add( area.fields.elementAt( i ) );
			if( area.fields.elementAt( i ).bottomBuf != 0 )
				p.add( Box.createRigidArea( new Dimension( 0, area.fields.elementAt( i ).bottomBuf ) ) );
		}
		p.add( Box.createRigidArea( new Dimension( 0, 4 ) ) ); 
	}

}//end class
