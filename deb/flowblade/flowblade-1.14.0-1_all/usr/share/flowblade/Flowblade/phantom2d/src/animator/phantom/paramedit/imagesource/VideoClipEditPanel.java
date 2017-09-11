package animator.phantom.paramedit.imagesource;

import java.awt.Checkbox;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import animator.phantom.controller.GUIComponents;
import animator.phantom.paramedit.AnimValueNumberEditor;
import animator.phantom.paramedit.CheckBoxEditor;
import animator.phantom.paramedit.CoordsEditComponents;
import animator.phantom.paramedit.FlipSelect;
import animator.phantom.paramedit.IntegerComboBox;
import animator.phantom.paramedit.RowSeparator;
import animator.phantom.paramedit.panel.ParamEditPanel;
import animator.phantom.renderer.ImageOperation;
import animator.phantom.renderer.imagesource.VideoClipIOP;

public class VideoClipEditPanel extends ParamEditPanel  implements ItemListener
{
	VideoClipIOP vc;
	CheckBoxEditor asCanvas;

	public VideoClipEditPanel( VideoClipIOP vc )
	{
		this.vc = vc;

		initParamEditPanel();

		CoordsEditComponents coords = new CoordsEditComponents( vc );
		AnimValueNumberEditor opacityEdit = new AnimValueNumberEditor( "Opacity", vc.opacity );
		IntegerComboBox blendSelect = new IntegerComboBox( vc.blendMode,
									"Blend mode",
									ImageOperation.blendModes );//so that blenders can be changed without recompiling
		blendSelect.setMaxComboRows( ImageOperation.blendModes.length );
	
		FlipSelect flip = new FlipSelect( vc.flipTrans );
		CheckBoxEditor aOverB = new CheckBoxEditor( vc.useOverRule, "Alpha combine", true );
		asCanvas = new CheckBoxEditor( vc.asCanvas, "Send as canvas", true );
		asCanvas.addItemListener( this );

		//--- Put GUI together 
		addComponentsVector( coords.getEditComponents() );
		add( opacityEdit );
		add( new RowSeparator() );
		add( blendSelect );
		add( new RowSeparator() );
		add( flip );
		add( new RowSeparator() );
		add( aOverB );
		add( new RowSeparator() );
		add( asCanvas );

		//--- HACK TO GET EDITORS UNABLED AFTRE LOAD IF SEND_AS_CANVAS IS TRUE  
		itemStateChanged(new ItemEvent( new Checkbox(), 0, new Object(), -1000 ) );
	}

	public void itemStateChanged(ItemEvent e)
	{
		boolean newVal = vc.asCanvas.get();
		setAllEnabled( newVal == false );
		setEnabledRecursively( asCanvas, true );
		GUIComponents.viewEditor.setLayerControlsEnabled( vc, !newVal );
		GUIComponents.viewEditor.setLayerDrawingEnabled( vc, !newVal );
	}

}//end class
