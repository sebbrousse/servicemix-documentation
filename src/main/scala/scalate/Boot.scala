/**
 * Copyright (C) 2009-2010 the original author or authors.
 * See the notice.md file distributed with this work for additional
 * information regarding copyright ownership.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package scalate

import org.fusesource.scalate.util.Logging
import java.util.concurrent.atomic.AtomicBoolean
import org.fusesource.scalate.TemplateEngine
import org.fusesource.scalamd.{MacroDefinition, Markdown}
import java.util.regex.Matcher
import org.fusesource.scalate.wikitext.Pygmentize

class Boot(engine: TemplateEngine) extends Logging {

  private var _initialised = new AtomicBoolean(false)

  def run: Unit = {
    if (_initialised.compareAndSet(false, true)) {

      def pygmentize(m:Matcher):String = Pygmentize.pygmentize(m.group(2), m.group(1))

      // add some macros to markdown.
      Markdown.macros :::= List(
        MacroDefinition("""\{pygmentize::(.*?)\}(.*?)\{pygmentize\}""", "s", pygmentize, true),
        MacroDefinition("""\{pygmentize\_and\_compare::(.*?)\}(.*?)\{pygmentize\_and\_compare\}""", "s", pygmentize, true)
        )

      for( ssp <- engine.filter("ssp"); md <- engine.filter("markdown") ) {
        engine.pipelines += "ssp.md"-> List(ssp, md)
        engine.pipelines += "ssp.markdown"-> List(ssp, md)
      }
    }
    
    debug("Boot class started")
  }
}