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

import org.fpgabros.fmt.exception.ParseException;
import org.fpgabros.fmt.model.Element;

public interface ParseListener {

    void openFile(String id) throws ParseException;

    void openRecord(String id) throws ParseException;

    void openRow(Integer index) throws ParseException;

    void receive(Integer index, String value) throws ParseException;

    void comment(String value) throws ParseException;

    void closeRow() throws ParseException;

    void closeRecord() throws ParseException;

    Element closeFile() throws ParseException;

    void reset() throws ParseException;

}
