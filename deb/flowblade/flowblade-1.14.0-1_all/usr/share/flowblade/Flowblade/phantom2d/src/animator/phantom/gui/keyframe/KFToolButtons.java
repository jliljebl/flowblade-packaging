package animator.phantom.gui.keyframe;

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
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import animator.phantom.controller.EditorsController;
import animator.phantom.gui.GUIResources;
import animator.phantom.renderer.param.AnimationKeyFrame;
import animator.phantom.renderer.param.KeyFrameParam;

public class KFToolButtons extends JPanel implements ActionListener
{
	private JButton zoomIn = new JButton( GUIResources.getIcon(  GUIResources.scaleZoomIn ) );
	private JButton zoomOut = new JButton( GUIResources.getIcon(  GUIResources.scaleZoomOut ) );
	private JButton addKF = new JButton( GUIResources.getIcon(  GUIResources.addKF ) );
	private JButton deleteKF = new JButton( GUIResources.getIcon(  GUIResources.deleteKF ) );
	//private ImageIcon propsEnabled = GUIResources.getIcon( GUIResources.keyframeProperties );
	//private ImageIcon propsDisabled =  GUIResources.getIcon( GUIResources.keyframePropertiesDisabled );
	private ImageIcon stepped =  GUIResources.getIcon( GUIResources.stepped ); 
	//private JButton kfProperties = new JButton( propsDisabled );
	private JCheckBox steppedBox;
	
	public KFToolButtons()
	{
		GUIResources.prepareMediumButton( zoomIn, this, "Zoom in vertical" );
		GUIResources.prepareMediumButton( zoomOut, this, "Zoom out vertical" );
		GUIResources.prepareMediumButton( addKF, this, "Add keyframe" );
		GUIResources.prepareMediumButton( deleteKF, this, "Delete keyframe" );
		//GUIResources.prepareMediumButton( kfProperties, this, "Selected keyframe properties" );

		JLabel steppedLabel = new JLabel();
		steppedLabel.setIcon(stepped);

		steppedBox = new JCheckBox();
		steppedBox.setSelected( false );
		steppedBox.addActionListener( this );
		steppedBox.setPreferredSize( new Dimension( 20, 20 ));
		steppedBox.setToolTipText("Hold last keyframe value");

		JPanel p = new JPanel();
		p.setLayout(new BoxLayout( p, BoxLayout.X_AXIS));
		p.add( addKF );
		p.add( deleteKF );
		p.add( Box.createRigidArea( new Dimension( 6, 0 ) ) );
		p.add( steppedLabel );
		p.add( steppedBox );
		p.add( Box.createRigidArea( new Dimension( 6, 0 ) ) );
		p.add( zoomIn );
		p.add( zoomOut );
		p.add( Box.createRigidArea( new Dimension( 6, 0 ) ) );
		//p.add( kfProperties );

		setLayout(new BoxLayout( this, BoxLayout.Y_AXIS));
		add( Box.createVerticalGlue() );
		add( p );
		add( Box.createVerticalGlue() );

		setKeyFrame( null );
	}

	public void setKeyFrame( AnimationKeyFrame kf )
	{
		/*
		if( kf == null )
			kfProperties.setIcon( propsDisabled );
		else
			kfProperties.setIcon( propsEnabled );
			*/
	}

	public void setStepped( boolean stepped )
	{
		steppedBox.setSelected( stepped );
	}
	
	public void actionPerformed( ActionEvent e )
	{
		requestFocusInWindow();
	
		if( e.getSource() == zoomIn )
			EditorsController.zoomInKeyFrameEditor();
			
		if( e.getSource() == zoomOut )
			EditorsController.zoomOutKeyFrameEditor();
			
		if( e.getSource() == deleteKF )
			EditorsController.deleteKeyFrame();

		if( e.getSource() == addKF )
			EditorsController.addKeyFrame();

		if( e.getSource() == steppedBox )
		{
			KeyFrameParam kfParam = EditorsController.getCurrentKFParam();
			kfParam.setStepped( steppedBox.isSelected() );
			EditorsController.updateKFForValueChange();
		}
	}

}//end class