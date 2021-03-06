package org.telosys.tools.eclipse.plugin.commons;

import java.io.File;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.FileEditorInput;
import org.telosys.tools.commons.FileUtil;
import org.telosys.tools.commons.StrUtil;
import org.telosys.tools.eclipse.plugin.config.ProjectConfig;
import org.telosys.tools.eclipse.plugin.config.ProjectConfigManager;
import org.telosys.tools.generator.target.TargetDefinition;

public class TargetUtil {

	// Editor id defined in "plugin.xml" 
	private final static String VELOCITY_EDITOR_ID = "org.telosys.tools.eclipse.plugin.editors.velocity.VelocityEditor";
	//private final static String TEXT_EDITOR_ID     = "org.eclipse.ui.DefaultTextEditor" ;
	
	private static void log(String s) 
	{
		PluginLogger.log( TargetUtil.class.getName() + " : " + s );
	}

	/**
	 * @param project
	 * @param bundleName
	 * @param fileName
	 * @return
	 */
	private static IFile getFileFromTemplatesFolder( IProject project, String bundleName, String fileName ) {
		log( "getFileFromTemplatesFolder(..)..." + fileName );
		ProjectConfig projectConfig = ProjectConfigManager.getProjectConfig( project );
		if ( projectConfig != null ) {
			//String templatesFolder = projectConfig.getTemplatesFolder();
			String templatesFolder = projectConfig.getTelosysToolsCfg().getTemplatesFolder();
			
			// Add bundle folder if any
			if ( ! StrUtil.nullOrVoid(bundleName) ) {
				templatesFolder = FileUtil.buildFilePath(templatesFolder, bundleName );
			}

//			String templateFile = null ;
//			log( "  templates folder = " + templatesFolder );
//			if ( templatesFolder.endsWith("/") || templatesFolder.endsWith("\\") ) {
//				templateFile = templatesFolder + fileName;
//			}
//			else {
//				templateFile = templatesFolder + "/" + fileName;
//			}
			String templateFile = FileUtil.buildFilePath(templatesFolder, fileName );
			
			log("Templates file (path in project) = " + templateFile );		
			
			File f = EclipseProjUtil.getResourceAsFile( project, templateFile);
			if ( f != null) {
				return EclipseWksUtil.toIFile(f);
			}
			else {
				MsgBox.error("Template file '" + templateFile + "' not found !");
			}
		}
		return null ;
	}
	
	/**
	 * Returns the template file instance for the given target
	 * @param project
	 * @param target
	 * @return
	 */
	private static IFile getTemplateFile( IProject project, String bundleName, TargetDefinition target ) {
		
		log( "getTemplateFile(target)..." + target );
		return getFileFromTemplatesFolder( project, bundleName, target.getTemplate() );
	}
	
	/**
	 * Opens the template file of the given target in the specialized editor
	 * @param project
	 * @param bundleName
	 * @param target
	 */
	public static void openTemplateFileInEditor(IProject project, String bundleName, TargetDefinition target) {
		
		IFile templateFile = getTemplateFile( project, bundleName, target );
		
		IEditorInput editorInput = new FileEditorInput(templateFile);
		
		IWorkbenchWindow workbenchWindow = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		IWorkbenchPage workbenchPage = workbenchWindow.getActivePage();
		try {
			// Use class TextEditor : The standard/default text editor.
			// This editor has id "org.eclipse.ui.DefaultTextEditor". 
			//workbenchPage.openEditor(editorInput, "org.eclipse.ui.DefaultTextEditor" );
			workbenchPage.openEditor(editorInput, VELOCITY_EDITOR_ID );
			
		} catch (PartInitException e) {
			MsgBox.error("Cannot open file in editor (PartInitException)");
		}
	}

	public static void openTargetsConfigFileInEditor(IProject project, String bundleName) {
		
		IFile targetsConfigFile = getFileFromTemplatesFolder( project, bundleName, ProjectConfig.TEMPLATES_CFG );
		
		IEditorInput editorInput = new FileEditorInput(targetsConfigFile);
		
		IWorkbenchWindow workbenchWindow = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		IWorkbenchPage workbenchPage = workbenchWindow.getActivePage();
		try {
			// Use class TextEditor : The standard/default text editor.
			// This editor has id "org.eclipse.ui.DefaultTextEditor". 
			workbenchPage.openEditor(editorInput, "org.eclipse.ui.DefaultTextEditor" );
		} catch (PartInitException e) {
			MsgBox.error("Cannot open file in editor (PartInitException)");
		}
	}

}
