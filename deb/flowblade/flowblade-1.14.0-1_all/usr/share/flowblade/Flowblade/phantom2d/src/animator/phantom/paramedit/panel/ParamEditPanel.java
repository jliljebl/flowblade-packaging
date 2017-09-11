package animator.phantom.paramedit.panel;

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

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.util.Vector;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import animator.phantom.gui.GUIResources;
import animator.phantom.paramedit.FrameChangeListener;
import animator.phantom.paramedit.ParamEditResources;
import animator.phantom.paramedit.RowSeparator;
import animator.phantom.paramedit.UndoListener;

//--- Base class for all iop edit panels. All iops must provide one class extending this using method ImageOperation.getEditPanelInstance()
public class ParamEditPanel extends JPanel
{
	private Vector<FrameChangeListener> frameChangeListeners = new Vector<FrameChangeListener> ();
	private Vector<UndoListener> undoListeners = new Vector<UndoListener>();

	private Vector<JPanel> panes = null;
	private Vector<String> paneNames = null;	

	//--- Sets correct layout and takes reference to iop.
	public void initParamEditPanel()
	{
		setLayout( new BoxLayout( this, BoxLayout.Y_AXIS) );
		//add( Box.createRigidArea(new Dimension( 0, 4 ) ) );
		setBorder( null );
	}
	//--- Sets panel to use tabbed pane as holder for components
	public void setTabbedPanel( int height, Vector<String> paneNames )
	{
		JTabbedPane tabbedPane = new JTabbedPane();
		super.add( tabbedPane );
		super.add( Box.createVerticalGlue() );

		this.paneNames = paneNames;
		panes = new Vector<JPanel>();
		for( int i = 0; i < paneNames.size(); i++ )
		{
			JPanel addPane = new JPanel();
			addPane.setLayout( new BoxLayout( addPane, BoxLayout.Y_AXIS) );
			addPane.add(  Box.createRigidArea(new Dimension( 0, ParamEditResources.TABS_TOP_GAP ) ) );
			addPane.setBorder( null );
			panes.add( addPane );
			tabbedPane.addTab( paneNames.elementAt( i ), addPane );
		}
		
		tabbedPane.setPreferredSize( new Dimension( ParamEditResources.EDIT_ROW_SIZE.width, height ));
		tabbedPane.setFont( GUIResources.BASIC_FONT_12 );
	}
	//--- Intercepts component before adding it to panel 
	//--- Adds it to frameChangeListeners vec if component implements interface.
	public Component add(Component comp)
	{
		if( comp instanceof FrameChangeListener ) frameChangeListeners.add( (FrameChangeListener) comp );
		if( comp instanceof UndoListener ) undoListeners.add( (UndoListener) comp );
		return super.add( comp );
	}
	//--- Intercepts component before adding it to tab panel 
	//--- Adds it to frameChangeListeners vec if component implements interface.
	public Component addToTab(String tab, Component comp)
	{
		if( paneNames == null )
			return null;
		int indx = -1;
		for( int i = 0; i < paneNames.size(); i++ )
 			if( paneNames.elementAt( i ).equals( tab ) ) indx = i;
		if( indx == -1 ) return null;

		if( comp instanceof FrameChangeListener ) frameChangeListeners.add( (FrameChangeListener) comp );
		if( comp instanceof UndoListener ) undoListeners.add( (UndoListener) comp );

		JPanel p = panes.elementAt( indx );
		return p.add( comp );
	}
	//--- Help method that adds components in vector to extending panel.
	public void addComponentsVector( Vector <Component> components )
	{
		addComponentsVector( components, true );
	}

	//--- Help method that adds components in vector to extending panel.
	public void addComponentsVector( Vector <Component> components, boolean addLastSeparator )
	{
		for( int i = 0; i < components.size(); i++ )
		{
			add( components.elementAt ( i ) );
			if( addLastSeparator )
				add( new RowSeparator() );
			else if( i < components.size() - 1 )
				add( new RowSeparator() );
		}
	}
	//--- Frame changed, notify listeners.
	private void notifyFrameChangeListeners()
	{
		for( int i = 0; i < frameChangeListeners.size(); i++ )
		{
			FrameChangeListener listener = frameChangeListeners.elementAt( i );
			listener.frameChanged();
		}
	}
	//--- Used to set enbled state as context change to user actions.
	public void setAllEnabled( boolean val )
	{
		Component[] components = getComponents();
		for( int i = 0; i < components.length; i++ )
		{
			try
			{
				JComponent jc = (JComponent) components[ i ];
				setEnabledRecursively( jc, val );
			}
			catch( Exception e){}
		}
	}
	//--- Sets all components that can be cast JComponent to defined enabled state.
	public void setEnabledRecursively( Component c, boolean val )
	{
		try
		{
			JComponent jc = ( JComponent ) c;
			jc.setEnabled( val );
		}
		catch( Exception e){}

		Component[] components;
		try
		{
			Container cont = (Container) c;
			components = cont.getComponents();
		}
		catch( Exception e)
		{
			return;//not container, no childs.
		}

		for( int i = 0; i < components.length; i++ )
		{
			try
			{
				JComponent jc = (JComponent) components[ i ];
				setEnabledRecursively( jc, val );
			}
			catch( Exception e){}
		}
	}
 	//--- Method is called when current frame in editor has changed.
	public void currentFrameChanged()
	{
		notifyFrameChangeListeners();
		repaint();
	}
	//--- Method called when undo done.
	public void undoDone()
	{
		for( int i = 0; i < undoListeners.size(); i++ )
		{
			UndoListener listener = undoListeners.elementAt( i );
			listener.undoDone();
		}
	}

}//end class