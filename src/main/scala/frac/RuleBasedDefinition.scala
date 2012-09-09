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

import annotation.tailrec

/** The heart of the L-System. This is parsing and executing definitions in the form of simple rules */
class RuleBasedDefinition(seed: String,
                          rules: Map[Char, String],
                          turnAngleDeg: Int = 90,
                          val scaleRatio: Double = 0.5,
                          val startingPoint : StartingPoint.Value = StartingPoint.Left) extends Definition {
    val turnAngle = turnAngleDeg.toRad

    /** AST structure. Represents a primitive or rule. */
    private class Token(val character: String, var rule: List[Token] = Nil) {
        def this(c: Char) = this(c.toString)

        def execute(level: Int, callback: String => Unit) {
            executeRecurse(callback, List(level -> this))
        }

        @tailrec
        private def executeRecurse(callback: String => Unit, nextTokens: List[(Int, Token)]) { nextTokens match {
            case Nil => ()
            case (level, token) :: xs =>
                if (level == 0 || token.rule.size== 0) {
                    callback(token.character)
                    executeRecurse(callback, xs)
                }
                else {
                    executeRecurse(callback, token.rule.map((level - 1, _)) ::: xs)
                }
        }}
    }

    // Create a token for each rule
    private var compiledMap = rules.map { case (character, _) => character -> new Token(character) }

    // Map the rules to token lists
    compiledMap.foreach { case (character, token) =>
        token.rule = toTokenList(rules(character))
    }

    // Map the seed to a token list
    private val compiledSeed = toTokenList(seed)

    private def toTokenList(ruleValue: String) = ruleValue.map { c =>
      if (!compiledMap.contains(c)) compiledMap = compiledMap.updated(c, new Token(c))
      compiledMap(c)
    }.toList

    def run(depth: Int, callback: String => Unit) {
        compiledSeed.foreach(_.execute(depth, callback))
    }
}