/*
 * Copyright (C) 2012 Julien Letrouit
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
import java.awt.{Desktop, Font, Color}
import java.net.URI
import javax.swing.KeyStroke
import java.awt.event.KeyEvent

object Main extends SimpleSwingApplication {
  val TURTLE_MOVES_STAT_TEMPLATE = "Turtle moves: %,d"
  val TURTLE_TURNS_STAT_TEMPLATE = "Turtle turns: %,d"
  val SEQUENCE_LENGTH_STAT_TEMPLATE = "Sequence length: %,d"
  val DURATION_STAT_TEMPLATE = "Drawing duration: %d ms"
  val CODE_COLOR = 80
  val parser = new FractalDefinitionParser
  val definitions = ExampleRepository.examples
  var definition = definitions(0)

  val refreshAction = new Action("Refresh") {
    override def apply() { refresh() }
    accelerator = Some(KeyStroke.getKeyStroke("F5"))
  }
  val increaseDepthAction = new Action("+") {
    override def apply() { increaseDepth() }
    accelerator = Some(KeyStroke.getKeyStroke("F6"))
    longDescription = "Increase depth"
  }
  val decreaseDepthAction = new Action("-") {
    override def apply() { decreaseDepth() }
    accelerator = Some(KeyStroke.getKeyStroke("F4"))
    longDescription = "Decrease depth"
  }

  val turtleMovesStat = new Label
  val turtleTurnsStat = new Label
  val squenceLengthStat = new Label
  val durationStat = new Label
  val menu = new MenuBar {
    contents += new Menu("Load example") {
      definitions.foreach(ds => contents += new MenuItem(Action(ds.title) {
        selectExample(ds)
      }))
    }
    contents += new Menu("View") {
      contents += new MenuItem(refreshAction)
      contents += new MenuItem("Decrease depth") {
        reactions += {
          case ButtonClicked(_) => decreaseDepth()
        }
        peer.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F4, 0))
      }
      contents += new MenuItem("Increase depth") {
        reactions += {
          case ButtonClicked(_) => increaseDepth()
        }
        peer.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F6, 0))
      }
    }
    contents += new Menu("Help") {
      contents += new MenuItem(Action("User manual") { browse("https://github.com/jletroui/frac/blob/master/README.markdown") })
      contents += new MenuItem(Action("License") { browse("https://raw.github.com/jletroui/frac/master/LICENSE") })
    }
  }
  val editor = new TextArea(definitions.head.sourceText, 5, 20) {
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
      contents += new Button(decreaseDepthAction)
      contents += depth
      contents += new Button(increaseDepthAction)
      contents += HStrut(10)
      contents += new Button(refreshAction)
    }
  }

  val center = new SplitPane(Orientation.Vertical, fractalPanel, rightSection) {
    continuousLayout = true
    oneTouchExpandable = true
    dividerLocation = 1200
  }

  lazy val topFrame = new MainFrame {
    title = "Frac 1.0.5"
    contents = new BorderPanel {
      preferredSize = (1600,1000)
      opaque = true
      layout(center) = Center
    }
    menuBar = menu
    centerOnScreen()
    listenTo(this)
    reactions += {
      case WindowClosing(e) =>
        println("Exiting...")
        System.exit(0)
    }
  }

  def top = topFrame

  def increaseDepth() {
    depth.text = (depth.text.toInt + 1).toString
    refresh()
  }

  def decreaseDepth() {
    depth.text = (depth.text.toInt - 1).toString
    refresh()
  }

  def selectExample(example: FractalDefinition) {
    editor.text = example.sourceText
    depth.text = "1"
    refresh()
  }

  def refresh() {
    try {
      val parsingResult = parser.parseFractalDefinition(editor.text)

      if (parsingResult.matched) {
        definition = parsingResult.result.get
        fractalPanel.repaint()
      }
      else {
        val error = parsingResult.parseErrors.head
        val message = new StringBuilder("There is a syntax error at the character %d.".format(error.getStartIndex))
        if (error.getErrorMessage != null) message.append(" Additional info: %s".format(error.getErrorMessage))
        Dialog.showMessage(
          message = message,
          title = "Syntax error",
          messageType = Dialog.Message.Error)

        editor.caret.position = error.getStartIndex
        editor.requestFocusInWindow()
      }
    }
    catch {
      case t: Throwable =>
        Dialog.showMessage(message = t.getMessage, title = "Syntax error", messageType = Dialog.Message.Error)
    }
  }

  lazy val desktop = if (Desktop.isDesktopSupported) Some(Desktop.getDesktop) else None
  lazy val browser = if (desktop.isDefined && desktop.get.isSupported(Desktop.Action.BROWSE)) Some(desktop.get.browse _) else None
  def browse(url: String) {
    browser.foreach(_(new URI(url)))
  }
}