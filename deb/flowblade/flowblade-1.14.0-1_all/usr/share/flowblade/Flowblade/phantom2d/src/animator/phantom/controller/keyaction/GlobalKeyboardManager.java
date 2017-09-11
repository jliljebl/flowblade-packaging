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

import java.awt.AWTEvent;
import java.awt.EventQueue;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.KeyStroke;

public class GlobalKeyboardManager extends EventQueue 
{
	private static final GlobalKeyboardManager instance = new GlobalKeyboardManager();
	private final InputMap keyStrokes = new InputMap();
	private final ActionMap actions = new ActionMap();

	static
	{
		Toolkit.getDefaultToolkit().getSystemEventQueue().push(instance);
	}

	private GlobalKeyboardManager() {}

	public static GlobalKeyboardManager getInstance() 
	{
		return instance;
	}
	public static void clearActions()
	{
		instance.keyStrokes.clear();
		instance.actions.clear();
	}
	public InputMap getInputMap() 
	{
		return keyStrokes;
	}
	public ActionMap getActionMap() 
	{
		return actions;
	}
	protected void dispatchEvent(AWTEvent event) 
	{
		if (event instanceof KeyEvent) 
		{
			KeyStroke ks = KeyStroke.getKeyStrokeForEvent((KeyEvent)event);
			String actionKey = (String)keyStrokes.get(ks);
			if (actionKey != null) 
			{
				Action action = actions.get(actionKey);
				if (action != null && action.isEnabled()) 
				{
					action.actionPerformed(
						new ActionEvent(event.getSource(), event.getID(),
						actionKey, ((KeyEvent)event).getModifiers()));
					return;
				}
			}
		}

		super.dispatchEvent(event);
	}

}//end class
