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

import java.lang.StringBuffer

/** Renders the given definition in a simple string */
class StringRenderer extends Renderer[String]
{
    def render(definition: Definition, depth: Int): String = {
        val res = new StringBuffer()
        definition.run(depth, res.append(_))
        res.toString
    }
}