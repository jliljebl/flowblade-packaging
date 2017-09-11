package animator.phantom.controller.keyaction;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;

import animator.phantom.controller.PreviewController;

public class PlayStopAction extends AbstractAction
{
	public void actionPerformed( ActionEvent e )
	{
		PreviewController.playPressed();
	}

}//end class
