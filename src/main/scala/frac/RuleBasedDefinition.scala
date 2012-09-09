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

import annotation.tailrec

/** The heart of the L-System. This is parsing and executing definitions in the form of simple rules */
class RuleBasedDefinition(seed: String,
                          rules: Map[String, String],
                          turnAngleDeg: Int = 90,
                          val scaleRatio: Double = 0.5,
                          val startingPoint : StartingPoint.Value = StartingPoint.Left) extends Definition {
  val turnAngle = turnAngleDeg.toRad

  /** AST structure. Represents a primitive or rule. */
  private class CompiledToken(val token: Token, var rule: List[CompiledToken] = Nil) {
    def execute(level: Int, callback: Token => Unit) {
      executeRecurse(callback, List(level -> this))
    }

    @tailrec
    private def executeRecurse(callback: Token => Unit, nextTokens: List[(Int, CompiledToken)]) { nextTokens match {
      case Nil => ()
      case (level, compiledToken) :: xs =>
        if (level == 0 || compiledToken.rule.size== 0) {
          callback(compiledToken.token)
          executeRecurse(callback, xs)
        }
        else {
          executeRecurse(callback, compiledToken.rule.map((level - 1, _)) ::: xs)
        }
    }}
  }

  // Create a token for each rule
  private var compiledMap = rules.map { case (character, _) =>
    val token = new Primitive(character)
    token.asInstanceOf[Token] -> new CompiledToken(token)
  }

  // Map the rules to token lists
  compiledMap.foreach { case (token, compiledToken) =>
    compiledToken.rule = compileRule(rules(token.toString))
  }

  // Map the seed to a token list
  private val compiledSeed = compileRule(seed)

  private def compileRule(ruleValue: String) = {
    val tokens = new RuleParser().parseRule(ruleValue).result.get
    tokens.map { t =>
      // If we encounter this rule for the first time, add it to our map
      if (!compiledMap.contains(t)) compiledMap = compiledMap.updated(t, new CompiledToken(t))
      // Returns the rule
      compiledMap(t)
    }
  }

  def run(depth: Int, callback: Token => Unit) {
    compiledSeed.foreach(_.execute(depth, callback))
  }
}