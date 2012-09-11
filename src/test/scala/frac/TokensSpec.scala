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

import org.specs2.mutable._
import java.awt.Color

class TokensSpec extends Specification{
  "a ConstantColorStatement" should {
    "be created by RGB components" in {
      ConstantColorStatement(10, 20, 40).color must beEqualTo(new Color(10, 20, 40))
    }
    "be created by shifting RGB components if not in range" in {
      ConstantColorStatement(257, 20, -2).color must beEqualTo(new Color(1, 20, 254))
    }
    "be created by a color" in {
      ConstantColorStatement(new Color(10, 20, 40)).color must beEqualTo(new Color(10, 20, 40))
    }
    "be created by a predefined color name" in {
      ConstantColorStatement("red").color must beEqualTo(Color.red)
    }
    "throw an error when null color is passed" in {
      ConstantColorStatement(null.asInstanceOf[Color]) must throwA[Exception]
    }
    "throw an error when null color name is passed" in {
      ConstantColorStatement(null.asInstanceOf[String]) must throwA[Exception]
    }
    "throw an error when non predefined color name is passed" in {
      ConstantColorStatement("fancy") must throwA[Exception]
    }
    "returned the constant color when passed null" in {
      ConstantColorStatement("red").changeColor(null) must beEqualTo(Color.red)
    }
    "returned the constant color when passed an other color" in {
      ConstantColorStatement("red").changeColor(Color.green) must beEqualTo(Color.red)
    }
  }

  "a IncremenColorStatement" should {
    "dont't change color with 0 increment" in {
      IncrementColorStatement(0,0,0).changeColor(Color.red) must beEqualTo(Color.red)
    }
    "increment and decrement components" in {
      IncrementColorStatement(10,0,-10).changeColor(new Color(20, 20, 20)) must beEqualTo(new Color(30, 20, 10))
    }
    "shift components if result not in range" in {
      IncrementColorStatement(300,0,-100).changeColor(new Color(20, 20, 20)) must beEqualTo(new Color(64, 20, 176))
    }
  }
}
