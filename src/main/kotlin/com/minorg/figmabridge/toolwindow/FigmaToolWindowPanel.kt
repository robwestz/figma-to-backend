package com.minorg.figmabridge.toolwindow

import com.intellij.ui.components.JBPanel
import com.intellij.ui.components.JBLabel
import com.intellij.ui.components.JBButton
import java.awt.BorderLayout

/**
 * Denna klass definierar det faktiska UI-innehållet för vårt verktygsfönster.
 * Vi använder Swing-komponenter (från JetBrains UI-bibliotek, 'JB')
 * för att bygga gränssnittet.
 */
class FigmaToolWindowPanel : JBPanel<FigmaToolWindowPanel>() {

    init {
        // Sätt layout-hanteraren för denna panel
        layout = BorderLayout()

        // Skapa en etikett
        val titleLabel = JBLabel("Figma-anslutning")
        titleLabel.horizontalAlignment = JBLabel.CENTER
        
        // Skapa en knapp
        val authButton = JBButton("Autentisera")

        // Lägg till en enkel åtgärd (skriver bara till konsolen för nu)
        authButton.addActionListener {
            println("Knappen 'Autentisera' klickades!")
            // TODO: Här kommer vi att lägga till logik för Figma OAuth
        }

        // Lägg till komponenterna i panelen
        add(titleLabel, BorderLayout.NORTH)
        add(authButton, BorderLayout.CENTER)
    }
}
