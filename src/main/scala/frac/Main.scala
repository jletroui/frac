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
import java.awt.{Font, Color}

object Main extends SimpleSwingApplication {

    val SEGMENT_STAT_TEMPLATE = "Segments: %d"
    val TOKEN_STAT_TEMPLATE = "Tokens: %d"
    val TIME_STAT_TEMPLATE = "Time: %d ms"
    val parser = new RuleBasedParser
    val definitions = new DefaultDefinitionRepository().getDefinitions
    var definition = parser.parse(definitions(0).source)

    lazy val fractalPanel = new Panel {
        override def paintComponent(g: Graphics2D) {
            super.paintComponent(g)
            g.setColor(new Color(100,100,100))
            val stats = new GraphicsRenderer(g).render(definition, depth.text.toInt)
            segmentStat.text = SEGMENT_STAT_TEMPLATE.format(stats.segments)
            tokensStat.text = TOKEN_STAT_TEMPLATE.format(stats.tokens)
            timeStat.text = TIME_STAT_TEMPLATE.format(stats.time)
        }
    }

    lazy val definitionList = new ComboBox[DefinitionSource](definitions) {
        selection.index = 0

        def select()
        {
            editor.text = definitions(selection.index).source
            depth.text = "1"
            refresh()
        }

        selection.reactions += {
            case SelectionChanged(_) => select()
        }
    }

    lazy val editor = new TextArea(definitions.head.source, 5, 20) {
        font = new Font("Verdana", Font.BOLD, 20)
        foreground = new Color(100, 100, 100)
    }

    lazy val segmentStat = new Label()
    lazy val tokensStat = new Label()
    lazy val timeStat = new Label()

    lazy val depth = new TextField("1", 3) {
        verifier = (txt: String) => try { txt.toInt ; true} catch { case t: Throwable => false }
    }
    lazy val generateBtn = new Button {
        text = "Generate"
        reactions += {
            case ButtonClicked(_) => refresh()
        }
    }

    lazy val minusBtn = new Button {
        text = "-"

        reactions += {
            case ButtonClicked(_) =>
                depth.text = (depth.text.toInt - 1).toString
                refresh()
        }
    }

    lazy val plusBtn = new Button {
        text = "+"
        reactions += {
            case ButtonClicked(_) =>
                depth.text = (depth.text.toInt + 1).toString
                refresh()
        }
    }

    lazy val definitionPanel = new BorderPanel {
        val rightSection = new BoxPanel(Orientation.Vertical) {
            contents += editor
            contents += segmentStat
            contents += tokensStat
            contents += timeStat
        }
        val bottomBar = new FlowPanel {
            contents += minusBtn
            contents += depth
            contents += plusBtn
            contents += generateBtn
        }
        layout(definitionList) = North
        layout(rightSection) = Center
        layout(bottomBar) = South
    }

    lazy val center = new SplitPane(Orientation.Vertical, fractalPanel, definitionPanel) {
        continuousLayout = true
        oneTouchExpandable = true
        dividerLocation = 1200
        reactions += {
            case KeyReleased(_, Key.Plus, Key.Modifier.Control, _) =>
                depth.text = (depth.text.toInt + 1).toString
            case KeyReleased(_, Key.Minus, Key.Modifier.Control, _) =>
                depth.text = (depth.text.toInt - 1).toString
            case KeyReleased(_, k, _, _) => println(k.toString)
        }
    }

    lazy val topFrame = new MainFrame {
        title = "Frac"
        contents = new BorderPanel {

            preferredSize = (1600,1000)
            opaque = true
            layout(center) = Center
        }
        centerOnScreen()
        listenTo(this)
        defaultButton = generateBtn
        reactions += {
            case WindowClosing(e) =>
                println("Exiting...")
                System.exit(0)
        }
    }

    def top = topFrame

    private def refresh()
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

}