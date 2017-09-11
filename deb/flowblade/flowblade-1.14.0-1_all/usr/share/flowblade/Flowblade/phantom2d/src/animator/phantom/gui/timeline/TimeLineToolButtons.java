package animator.phantom.gui.timeline;

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

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;

import animator.phantom.controller.TimeLineController;
import animator.phantom.gui.GUIResources;

public class TimeLineToolButtons extends JPanel implements ActionListener
{
	private JButton clipDown = new JButton( GUIResources.getIcon(  GUIResources.clipDown ) );
	private JButton clipUp = new JButton( GUIResources.getIcon( GUIResources.clipUp ) );

	public TimeLineToolButtons()
	{
		GUIResources.prepareMediumButton( clipDown, this, "Move clip down" );
		GUIResources.prepareMediumButton( clipUp, this, "Move clip up" );

		JPanel p = new JPanel();
		p.setLayout(new BoxLayout( p, BoxLayout.X_AXIS));
		p.add( clipDown );
		p.add( clipUp );
		p.add( Box.createRigidArea( new Dimension( 6, 0 ) ) );

		setLayout(new BoxLayout( this, BoxLayout.Y_AXIS));
		add( Box.createVerticalGlue() );
		add( p );
		add( Box.createVerticalGlue() );
	}

	public void actionPerformed( ActionEvent e )
	{
		if( e.getSource() == clipUp )
			TimeLineController.moveSelectedClipsUp();
		if( e.getSource() == clipDown )
			TimeLineController.moveSelectedClipsDown();
	}

}//end class
