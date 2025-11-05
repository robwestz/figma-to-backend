package com.robwestz.figmatobackend.ui;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;
import org.jetbrains.annotations.NotNull;

public class GenerateCodeAction extends AnAction {

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        if (e.getProject() == null) {
            return;
        }

        ToolWindowManager toolWindowManager = ToolWindowManager.getInstance(e.getProject());
        ToolWindow toolWindow = toolWindowManager.getToolWindow("Figma");
        
        if (toolWindow != null) {
            toolWindow.activate(null);
            Messages.showInfoMessage(
                e.getProject(),
                "Use the Figma tool window to generate code from your Figma designs.",
                "Figma to Backend"
            );
        } else {
            Messages.showErrorDialog(
                e.getProject(),
                "Figma tool window not found. Please ensure the plugin is properly installed.",
                "Error"
            );
        }
    }
}
