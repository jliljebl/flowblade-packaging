package animator.phantom.controller;

import animator.phantom.gui.ParamEditFrame;
import animator.phantom.project.LayerCompositorProject;
import animator.phantom.project.Project;
import animator.phantom.renderer.RenderFlow;


public class AppData
{
		private static LayerCompositorProject layerProject;
		private static Project project;
		private static ParamEditFrame paramEditFrame;
		
		public static void setProject( Project newProject ) { project = newProject; }
		public static Project getProject() { return project; }
		
		public static RenderFlow getFlow(){ return project.getRenderFlow(); }
		
		public static void setLayerProject( LayerCompositorProject newProject )	{ layerProject = newProject; }
		public static LayerCompositorProject getLayerProject()	{ return layerProject; }

		public static void setParamEditFrame( ParamEditFrame newpParamEditFrame )	{ paramEditFrame = newpParamEditFrame; }
		public static ParamEditFrame getParamEditFrame() { return paramEditFrame; }

}// end class
