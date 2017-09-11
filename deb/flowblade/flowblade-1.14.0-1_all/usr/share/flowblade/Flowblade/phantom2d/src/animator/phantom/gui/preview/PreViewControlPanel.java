package animator.phantom.gui.preview;

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

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JToggleButton;

import animator.phantom.controller.AppUtils;
import animator.phantom.controller.PreviewController;
import animator.phantom.controller.ProjectController;
import animator.phantom.controller.TimeLineController;
import animator.phantom.controller.UpdateController;
import animator.phantom.gui.GUIResources;
import animator.phantom.gui.timeline.TCDisplay;

public class PreViewControlPanel extends JPanel implements ActionListener
{
	private JButton render = new JButton( GUIResources.getIcon( GUIResources.renderPreview));
	private JToggleButton loop = new JToggleButton( GUIResources.getIcon( GUIResources.loop ));
	private JButton stopPreviewRender = new JButton( GUIResources.getIcon( GUIResources.stopPreviewRender ));
	private JButton trashPreviewRender = new JButton( GUIResources.getIcon( GUIResources.trashPreviewRender ));
	private ImageIcon playIcon =  GUIResources.getIcon( GUIResources.play );
	private ImageIcon pauseIcon =  GUIResources.getIcon( GUIResources.pause );
	private JButton playStop = new JButton( playIcon );
	private JButton toPreviousFrame = new JButton( GUIResources.getIcon(GUIResources.toPreviousFrameNavi ));
	private JButton toNextFrame = new JButton( GUIResources.getIcon( GUIResources.toNextFrameNavi ));
	private JLabel previewTimeInfo = new JLabel();

	public PreViewControlPanel()
	{
		GUIResources.prepareMediumButton( loop, this, "Loop preview");
		GUIResources.prepareMediumButton( stopPreviewRender, this, "Stop preview render");
		GUIResources.prepareMediumButton( trashPreviewRender, this, "Clear preview");
		GUIResources.prepareMediumButton( toPreviousFrame, this, "Previous frame" );
		GUIResources.prepareMediumButton( playStop, this, "Play / Stop preview" );
		GUIResources.prepareMediumButton( toNextFrame, this, "Next frame" );
		GUIResources.prepareMediumMediumButton( render,this, "Render preview" );

		loop.setSelectedIcon( GUIResources.getIcon( GUIResources.loopPressed ) );
		loop.setSelected( true );

		previewTimeInfo.setFont( GUIResources.BASIC_FONT_11 );

		JPanel p2 = new JPanel();
		p2.setLayout(new BoxLayout( p2, BoxLayout.X_AXIS));
		p2.add( toPreviousFrame );
		p2.add( playStop );
		p2.add( toNextFrame );
		p2.add( loop );
		p2.add( Box.createRigidArea( new Dimension( 10, 0 ) ) );
		p2.add( render);
		p2.add( stopPreviewRender );
		p2.add( Box.createRigidArea( new Dimension( 10, 0 ) ) );
		p2.add( trashPreviewRender );
		p2.add( Box.createRigidArea( new Dimension( 10, 0 ) ) );
		p2.add( previewTimeInfo );
		p2.add( Box.createHorizontalGlue() );
		setLayout(new BoxLayout( this, BoxLayout.Y_AXIS));
		add( p2 );
	}

	public void updatePreviewRenderInfo( int millis, int frame )
	{
		String frameTime = 	AppUtils.createTimeString( millis, true );
		previewTimeInfo.setText(Integer.toString(frame) + "/" + Integer.toString(ProjectController.getLength()) + ", " + frameTime);
	}

	public void updatePlayButton()
	{
		if( PreviewController.playbackOn() ) playStop.setIcon( pauseIcon );
		else playStop.setIcon( playIcon );
	}

	public void actionPerformed( ActionEvent e )
	{
		if( e.getSource() == playStop ) PreviewController.playPressed();

		if( e.getSource() == render )
		{
			PreviewController.renderAndPlay();
		}

		if( e.getSource() == loop )
			PreviewController.setLoop( loop.isSelected() );

		if( e.getSource() == stopPreviewRender )
		{
			PreviewController.abortPreviewRender();
		}
		if( e.getSource() == trashPreviewRender )
		{
			PreviewController.clear();
			UpdateController.updateCurrentFrameDisplayers( false );
		}
		if( PreviewController.getIsLocked() ) return;

		if( e.getSource() == toNextFrame )
		{
			TimeLineController.changeCurrentFrame( 1 );
			UpdateController.updateCurrentFrameDisplayers( false );
		}
		if( e.getSource() == toPreviousFrame )
		{
			TimeLineController.changeCurrentFrame( -1 );
			UpdateController.updateCurrentFrameDisplayers( false );
		}
	}

}//end class
