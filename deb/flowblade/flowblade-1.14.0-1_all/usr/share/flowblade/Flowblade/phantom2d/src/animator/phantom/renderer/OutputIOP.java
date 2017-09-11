package animator.phantom.renderer;

import animator.phantom.paramedit.panel.DummyEditPanel;
import animator.phantom.paramedit.panel.ParamEditPanel;

public class OutputIOP extends ImageOperation 
{
	public OutputIOP()
	{
		name = "Output";
	}

	public boolean isOutput()
	{ 
		return true; 
	}
	
	@Override
	public ParamEditPanel getEditPanelInstance() {
		return new DummyEditPanel();
	}

}//end class
