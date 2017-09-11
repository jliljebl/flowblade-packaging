package animator.phantom.controller.keyaction;

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

import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.KeyStroke;

public class KeyUtils
{
	//--- keyactions for focused component
	public static boolean setFocusAction( JComponent c, AbstractAction action, String keyStroke )
	{
		String KEY = keyStroke + "key";//--- name for action
		KeyStroke ks = KeyStroke.getKeyStroke(keyStroke);
		if( ks == null )
		{
			System.out.println(keyStroke + " bad keystring." );
			return false;
		}
		c.getInputMap().put( ks, KEY );
		c.getActionMap().put( KEY, action );
		return true;
	}
	//--- keyactions for ancestor of focused component
	public static boolean setAncestorFocusAction( JComponent c, AbstractAction action, String keyStroke )
	{
		String KEY = keyStroke + "key";//--- name for action
		KeyStroke ks = KeyStroke.getKeyStroke(keyStroke);
		if( ks == null )
		{
			System.out.println(keyStroke + " bad keystring." );
			return false;
		}
		c.getInputMap( JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT ).put( ks, KEY );
		c.getActionMap().put( KEY, action );
		return true;
	}
	//--- Global hotkey action.
	public static boolean setGlobalAction( AbstractAction action, String keyStroke )
	{
		String KEY = keyStroke + "key";//--- name for action
		KeyStroke ks = KeyStroke.getKeyStroke(keyStroke);
		if( ks == null )
		{
			System.out.println(keyStroke + " bad keystring." );
			return false;
		}
		setGlobalAction( KEY, action, ks );
		return true;
	}

	//--- Global hotkey action.
	private static void setGlobalAction( String KEY, AbstractAction action, KeyStroke ks )
	{
		GlobalKeyboardManager globalKeyboardManager = GlobalKeyboardManager.getInstance();
		globalKeyboardManager.getInputMap().put( ks, KEY) ;
		globalKeyboardManager.getActionMap().put( KEY, action );
	}

	//--- Clears global actions
	public static void clearGlobalActions()
	{
		GlobalKeyboardManager.clearActions();
	}

}//end class
