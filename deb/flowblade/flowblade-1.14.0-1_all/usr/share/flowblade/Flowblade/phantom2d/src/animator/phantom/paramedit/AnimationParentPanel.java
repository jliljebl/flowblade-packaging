package animator.phantom.paramedit;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import javax.swing.JPanel;

import animator.phantom.controller.FlowController;
import animator.phantom.controller.UpdateController;
import animator.phantom.controller.ProjectController;
import animator.phantom.gui.modals.DialogUtils;
import animator.phantom.gui.modals.MComboBox;
import animator.phantom.gui.modals.MInputArea;
import animator.phantom.gui.modals.MInputPanel;
import animator.phantom.gui.modals.MultiInputDialogPanel;
import animator.phantom.renderer.ImageOperation;
import animator.phantom.renderer.RenderNode;
import animator.phantom.renderer.parent.AbstractParentMover;

public class AnimationParentPanel extends JPanel implements ActionListener
{
	private ImageOperation iop;
	private Vector <ImageOperation> parentIops;
	private MComboBox parents;
	private MComboBox actions;
	private MComboBox looping;

	private boolean initializing = true;
	private static final int MAX_NAME_LENGTH = 20;

	public AnimationParentPanel( ImageOperation iop )
	{
		this.iop = iop;
		parentIops = ProjectController.getFlow().getAnimatebleIops();
		parentIops.remove( iop );

		int pselindex = 0;
		int typeselindex = 0;

		if( iop.parentNodeID != -1 )
		{
			ImageOperation piop = (  ProjectController.getFlow().getNode( iop.parentNodeID )).getImageOperation();
			pselindex = parentIops.indexOf( piop ) + 1;
			typeselindex = iop.parentMoverType;
		}

		String[] options = new String[parentIops.size() + 1];
		options[ 0 ] = "none";
		for( int i = 1; i < parentIops.size() + 1; i++ )
		{
			String name = parentIops.elementAt( i - 1 ).getName();
			if (name.length() > MAX_NAME_LENGTH)
			{
				name = name.substring(0, MAX_NAME_LENGTH - 3) + "...";
			}
			options[ i ] = name;
		}
		parents = new MComboBox( "Animation parent", 114, 200, options );
		parents.addActionListener( this );
		actions = new MComboBox( "Follow", AbstractParentMover.types );
		actions.addActionListener( this );
		parents.setSelectedIndex( pselindex );
		actions.setSelectedIndex( typeselindex );
		if( iop.parentNodeID == -1 )
		{
			actions.setEnabled( false );
		}

		String[] loopOptions = { "no looping","loop","ping-pong" };
		looping = new MComboBox( "Looping mode", loopOptions );
		looping.setSelectedIndex( iop.getLooping() );
		looping.addActionListener( this );

		MInputArea area = new MInputArea( "Animation Settings" );
		area.add( parents );
		area.add( actions );
		area.add( looping );

		MInputPanel panel = new MInputPanel( "Animation Properties" );
		panel.add( area );

		MultiInputDialogPanel multi = new MultiInputDialogPanel( panel );

		add( multi );

		initializing = false;
	}

	public void actionPerformed( ActionEvent e )
	{
		if ( initializing ) return;

		int p = parents.getSelectedIndex();
		int ac = actions.getSelectedIndex();
		if( p == 0 )
		{
			actions.setEnabled( false );
			iop.setParentMover( -1, -1, null );
		}
		else
		{
			ImageOperation parentIOP = parentIops.elementAt( p - 1 );
			if( isCyclicParenting( iop, parentIOP ) )
			{
				String[] tLines = {"Node/parent relationships can't form cycles.", "Parent set edit cancelled." };
				DialogUtils.showTwoStyleInfo( "Cyclic parenting detected", tLines, DialogUtils.WARNING_MESSAGE );
			}
			else
			{
				RenderNode node =  ProjectController.getFlow().getNode( parentIOP );
				iop.setParentMover( ac, node.getID(), parentIOP  );
				actions.setEnabled( true );
			}
		}
		iop.setLooping( looping.getSelectedIndex() );
		//ParamEditController.reBuildEditFrame();
		UpdateController.valueChangeUpdate();

	}

	private static boolean isCyclicParenting( ImageOperation child, ImageOperation parent )
	{
		Vector<ImageOperation> list = new Vector<ImageOperation>();
		list.add( child );
		if( list.contains( parent ) )
			return true;
		list.add( parent );
		ImageOperation nextParent = parent.getParent();
		while( nextParent != null )
		{
			if( list.contains( nextParent ) )
				return true;
			list.add( nextParent );
			nextParent = nextParent.getParent();
		}
		return false;
	}

}//end class
