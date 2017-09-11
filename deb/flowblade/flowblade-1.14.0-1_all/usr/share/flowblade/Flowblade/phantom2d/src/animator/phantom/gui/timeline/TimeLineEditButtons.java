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


public class TimeLineEditButtons extends JPanel implements ActionListener
{
	private JButton moveClipHeadToCurrent  = new JButton( GUIResources.getIcon( GUIResources.clipHeadToCurrent ) );
	private JButton moveClipTailToCurrent = new JButton(  GUIResources.getIcon( GUIResources.clipTailToCurrent ) );
	private JButton clipInToCurrent = new JButton(  GUIResources.getIcon( GUIResources.clipInToCurrent ) );
	private JButton clipOutToCurrent = new JButton(  GUIResources.getIcon( GUIResources.clipOutToCurrent ) );

	public TimeLineEditButtons()
	{
		GUIResources.prepareMediumButton( clipOutToCurrent, this, "Strecth clip out to current frame" );
		GUIResources.prepareMediumButton( clipInToCurrent, this, "Strecth clip in to current frame" );
		GUIResources.prepareMediumButton( moveClipTailToCurrent, this, "Move clip to end in current frame" );
		GUIResources.prepareMediumButton( moveClipHeadToCurrent, this, "Move clip to start in current frame" );

		//--- Create layout.
		JPanel p = new JPanel();
		p.setLayout(new BoxLayout( p, BoxLayout.X_AXIS));
		//p.add( clipInToCurrent );
		//p.add( clipOutToCurrent );
		p.add( Box.createRigidArea( new Dimension( 6, 0 ) ) );
		//p.add( moveClipHeadToCurrent );
		//p.add( moveClipTailToCurrent );

		setLayout(new BoxLayout( this, BoxLayout.Y_AXIS));
		add( Box.createVerticalGlue() );
		add( p );
		add( Box.createVerticalGlue() );
	}

	public void actionPerformed( ActionEvent e )
	{
		if( e.getSource() == clipInToCurrent ) TimeLineController.trimSelectedStartToCurrent();
		if( e.getSource() == clipOutToCurrent ) TimeLineController.trimSelectedEndToCurrent();
		if( e.getSource() == moveClipHeadToCurrent ) TimeLineController.moveClipStartToCurrent();
		if( e.getSource() == moveClipTailToCurrent ) TimeLineController.moveClipEndToCurrent();
	}

}//end class