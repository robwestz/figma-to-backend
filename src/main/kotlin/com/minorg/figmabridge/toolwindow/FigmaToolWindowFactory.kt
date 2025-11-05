package com.minorg.figmabridge.toolwindow

import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.content.ContentFactory

/**
 * Denna klass anropas av PyCharm när användaren klickar på "Figma Bridge"-fliken.
 * Den ansvarar för att instansiera och bygga själva UI-panelen.
 */
class FigmaToolWindowFactory : ToolWindowFactory {

    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        // 1. Skapa vår anpassade UI-panel
        val figmaPanel = FigmaToolWindowPanel()

        // 2. Hämta fabriken för att skapa "innehåll"
        val contentFactory = ContentFactory.getInstance()

        // 3. Skapa ett nytt innehållsobjekt med vår panel
        val content = contentFactory.createContent(figmaPanel, "", false)

        // 4. Lägg till det skapade innehållet i verktygsfönstret
        toolWindow.contentManager.addContent(content)
    }
}
