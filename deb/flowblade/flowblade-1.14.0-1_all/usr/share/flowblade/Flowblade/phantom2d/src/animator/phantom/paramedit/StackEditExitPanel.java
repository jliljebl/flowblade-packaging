package animator.phantom.paramedit;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;

import animator.phantom.controller.GUIComponents;
import animator.phantom.controller.ParamEditController;
import animator.phantom.controller.UpdateController;
import animator.phantom.gui.PHButtonFactory;

public class StackEditExitPanel  extends JPanel implements ActionListener
{
	public StackEditExitPanel()
	{
		JButton exitButton = PHButtonFactory.getButton( "Exit To Node Editor" );
		exitButton.addActionListener( this );
		
		setLayout( new BoxLayout( this, BoxLayout.X_AXIS) );
		add( Box.createHorizontalGlue() );
		add( exitButton );
		add( Box.createHorizontalGlue() );
	}
	
	public void actionPerformed(ActionEvent e)
	{
		UpdateController.editTargetIOPChangedFromStackEditor( null );
		ParamEditController.displayEditFrame( GUIComponents.filterStackPanel.getIop() );
	}
	
}//end class
