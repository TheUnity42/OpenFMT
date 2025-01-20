/*
 * Copyright © 2025 Talon Holton (jared.holton@fpgabros.org)
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
package org.fpgabros.fmt.spec;

import java.io.File;

import org.fpgabros.fmt.model.Element;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class XLSXSpecTest {

    @Test
    void testParse() {
        try {
            Element element = new XLSXSpec().read(new File("src/test/resources/xlsx/comment.xlsx"));
            Assertions.assertNotNull(element);
            System.out.println(element.dump(2));
        } catch (Exception ex) {
            Assertions.fail(ex);
        }
    }
    
    @Test
    void testMultisheet() {
        try {
            Element element = new XLSXSpec().read(new File("src/test/resources/xlsx/multisheet.xlsx"));
            Assertions.assertNotNull(element);
            System.out.println(element.dump(2));
        } catch (Exception ex) {
            Assertions.fail(ex);
        }
    }

    @Test
    void testMultiRecord() {
        try {
            Element element = new XLSXSpec().read(new File("src/test/resources/xlsx/multirecord.xlsx"));
            Assertions.assertNotNull(element);
            System.out.println(element.dump(2));
        } catch (Exception ex) {
            Assertions.fail(ex);
        }
    }

    @Test
    void testMultiMulti() {
        try {
            Element element = new XLSXSpec().read(new File("src/test/resources/xlsx/multimulti.xlsx"));
            Assertions.assertNotNull(element);
            System.out.println(element.dump(2));
        } catch (Exception ex) {
            Assertions.fail(ex);
        }
    }

}
