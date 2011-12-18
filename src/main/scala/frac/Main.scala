/*
 * Copyright (C) 2011 Julien Letrouit
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package frac

import swing._
import event._
import Swing._
import BorderPanel.Position._
import java.awt.{Desktop, GraphicsEnvironment, Font, Color}
import java.net.URI

object Main extends SimpleSwingApplication {

    //GraphicsEnvironment.getLocalGraphicsEnvironment.getAvailableFontFamilyNames.foreach(println(_))

    val TURTLE_MOVES_STAT_TEMPLATE = "Turtle moves: %,d"
    val TURTLE_TURNS_STAT_TEMPLATE = "Turtle turns: %,d"
    val SEQUENCE_LENGTH_STAT_TEMPLATE = "Sequence length: %,d"
    val DURATION_STAT_TEMPLATE = "Drawing duration: %d ms"
    val CODE_COLOR = 80
    val parser = new RuleBasedParser
    val definitions = new DefaultDefinitionRepository().getDefinitions
    var definition = parser.parse(definitions(0).source)

    val turtleMovesStat = new Label//("", null, Alignment.Left)
    val turtleTurnsStat = new Label//("", null, Alignment.Left)
    val squenceLengthStat = new Label//("", null, Alignment.Left)
    val durationStat = new Label//("", null, Alignment.Left)
    val generateBtn = new Button("Refresh")
    val minusBtn = new Button("-")
    val plusBtn = new Button("+")
    val menu = new MenuBar {
        contents += new Menu("Load example") {
            definitions.foreach(ds => contents += new MenuItem(Action(ds.name) {
                selectExample(ds)
            }))
        }
        contents += new Menu("Help") {
            contents += new MenuItem(Action("User manual") { browse("https://github.com/jletroui/frac/blob/master/README.markdown") })
            contents += new MenuItem(Action("License") { browse("https://raw.github.com/jletroui/frac/master/LICENSE") })
        }
    }
    val editor = new TextArea(definitions.head.source, 5, 20) {
        font = new Font("Monospaced", Font.BOLD, 16)
        foreground = new Color(CODE_COLOR, CODE_COLOR, CODE_COLOR)
        border = TitledBorder(null, "Editor")
    }
    val depth = new TextField("1", 3) {
        maximumSize = preferredSize
        verifier = (txt: String) => try { txt.toInt ; true} catch { case t: Throwable => false }
    }
    val fractalPanel = new Panel {
        override def paintComponent(g: Graphics2D) {
            super.paintComponent(g)
            g.setColor(new Color(100,100,100))
            val stats = new GraphicsRenderer(g).render(definition, depth.text.toInt)
            turtleMovesStat.text = TURTLE_MOVES_STAT_TEMPLATE.format(stats.turtleMoves)
            turtleTurnsStat.text = TURTLE_TURNS_STAT_TEMPLATE.format(stats.turtleTurns)
            squenceLengthStat.text = SEQUENCE_LENGTH_STAT_TEMPLATE.format(stats.sequenceLength)
            durationStat.text = DURATION_STAT_TEMPLATE.format(stats.duration)
        }
    }

    val rightSection = new BoxPanel(Orientation.Vertical) {
        contents += editor
        contents += new BoxPanel(Orientation.Horizontal) {
            contents += new BoxPanel(Orientation.Vertical) {
                contents += turtleMovesStat
                contents += turtleTurnsStat
                contents += squenceLengthStat
                contents += durationStat
            }
            contents += HGlue

            border = TitledBorder(null, "Drawing stats")
        }
        contents += new BoxPanel(Orientation.Horizontal) {
            contents += HGlue
            contents += new Label("Depth: ")
            contents += minusBtn
            contents += depth
            contents += plusBtn
            contents += HStrut(10)
            contents += generateBtn
        }
    }

    val center = new SplitPane(Orientation.Vertical, fractalPanel, rightSection) {
        continuousLayout = true
        oneTouchExpandable = true
        dividerLocation = 1200
    }

    lazy val topFrame = new MainFrame {
        title = "Frac 1.0"
        contents = new BorderPanel {
            preferredSize = (1600,1000)
            opaque = true
            layout(center) = Center
        }
        menuBar = menu
        centerOnScreen()
        listenTo(this, minusBtn, plusBtn, generateBtn)
        defaultButton = generateBtn
        reactions += {
            case ButtonClicked(`minusBtn`) =>
                depth.text = (depth.text.toInt - 1).toString
                refresh()
            case ButtonClicked(`plusBtn`) =>
                depth.text = (depth.text.toInt + 1).toString
                refresh()
            case ButtonClicked(`generateBtn`) =>
                refresh()
            case WindowClosing(e) =>
                println("Exiting...")
                System.exit(0)
        }
    }

    def top = topFrame

    def selectExample(example: DefinitionSource)
    {
        editor.text = example.source
        depth.text = "1"
        refresh()
    }

    def refresh()
    {
        try {
            definition = parser.parse(editor.text)
            fractalPanel.repaint()
        }
        catch {
            case t: Throwable =>
                Dialog.showMessage(message = t.getMessage, title = "Syntax error", messageType = Dialog.Message.Error)
        }
    }

    lazy val desktop = if (Desktop.isDesktopSupported) Some(Desktop.getDesktop) else None
    lazy val browser = if (desktop.isDefined && desktop.get.isSupported(Desktop.Action.BROWSE)) Some(desktop.get.browse _) else None
    def browse(url: String)
    {
        browser.foreach(_(new URI(url)))
    }
}