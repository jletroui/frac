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

import org.specs2.mutable._

class RuleBasedParserSpec extends SpecificationWithJUnit
{
    val text = """seed=F
angle=60
ratio=0.8
F=F-F++F-F
"""
    val renderer = new StringRenderer

    "a parser" should {
        "parse the angle of definition" in {
            new RuleBasedParser().parse(text).turnAngle must beEqualTo(60*2*math.Pi / 360)
        }
        "parse the ration of definition" in {
            new RuleBasedParser().parse(text).scaleRatio must beEqualTo(0.8)
        }
        "parse the rules of a definition" in {
            renderer.render (new RuleBasedParser().parse(text), 1) must beEqualTo("F-F++F-F")
        }
    }
}