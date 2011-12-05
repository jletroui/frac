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
import java.awt.Color
import BorderPanel.Position._

object Main extends SimpleSwingApplication {

    var definition = new RuleBasedDefinition("F--F--F", Map('F' -> "F+F--F+F"), 60)
    val parser = new RuleBasedParser

    lazy val fractalPanel = new Panel {
        override def paintComponent(g: Graphics2D) {
            super.paintComponent(g)
            g.setColor(new Color(100,100,100))
            new GraphicsRenderer(g).render(definition, depth.text.toInt)
        }
    }
    lazy val editor = new TextArea("angle=60\nseed=F--F--F\nF=F+F--F+F", 5, 20)
    lazy val depth = new TextField("1", 3) {
        verifier = (txt: String) => try { txt.toInt ; true} catch { case t: Throwable => false }
    }
    lazy val generateBtn = new Button {
        text = "Generate"
        reactions += {
            case ButtonClicked(_) =>
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
    lazy val definitionPanel = new BorderPanel {
        val bottomBar = new FlowPanel {
            contents += depth
            contents += generateBtn
        }
        layout(editor) = Center
        layout(bottomBar) = South
    }

    lazy val center = new SplitPane(Orientation.Vertical, fractalPanel, definitionPanel) {
        continuousLayout = true
        oneTouchExpandable = true
        dividerLocation = 1200
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
            case KeyReleased(_, Key.Plus, Key.Modifier.Control, _) =>
                depth.text = (depth.text.toInt + 1).toString
            case KeyReleased(_, Key.Minus, Key.Modifier.Control, _) =>
                depth.text = (depth.text.toInt - 1).toString
            case WindowClosing(e) =>
                println("Exiting...")
                System.exit(0)
        }
    }

    def top = topFrame
}