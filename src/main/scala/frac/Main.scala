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

object Main extends SimpleSwingApplication {
    private def toRad(deg: Int) = math.Pi * 2 * deg / 360

    def top = new MainFrame {
        title = "Frac"
        contents = new Panel {
            preferredSize = (600,600)
            opaque = true
            override def paint(g: Graphics2D) {
                val d = new RulesAndSeedDefinition("F", Map('F' -> "F+F--F+F"))
                val r = new GraphicsRenderer(g, 20.0, toRad(60))

                g.setColor(new Color(100,100,100))
                r.render(d, 1)
            }
        }
        centerOnScreen()
        listenTo(this)
        reactions += {
            case WindowClosing(e) =>
                println("Exiting...")
                System.exit(0)
        }
    }
}