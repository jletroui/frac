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

class RulesAndSeedDefinition(seed: String, rules: Map[Char, String]) extends Definition
{
    private class Token(character: Char, var rule: List[Token] = Nil)
    {
        def apply(level: Int, callback: Char => Unit)
        {
            if (level > 0 && rule.size > 0) rule.foreach(_.apply(level - 1, callback))
            else callback(character)
        }
    }

    // Create a token for each rule
    private var compiledMap = rules.map(pair => pair._1 -> new Token(pair._1))

    // Map the rules to token lists
    compiledMap.foreach { pair =>
        val (character, token) = pair
        token.rule =  rules(character).map { c =>
            if (!compiledMap.contains(c)) compiledMap = compiledMap.updated(c, new Token(c))
            compiledMap(c)
        }.toList
    }

    // Map the seed to a token list
    private val compiledSeed = seed.map { c =>
        if (!compiledMap.contains(c)) compiledMap = compiledMap.updated(c, new Token(c))
        compiledMap(c)
    }.toList

    def run(depth: Int, callback: Char => Unit) {
        compiledSeed.foreach(_(depth, callback))
    }
}