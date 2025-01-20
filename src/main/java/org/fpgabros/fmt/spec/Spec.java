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

import org.fpgabros.fmt.exception.ParseException;
import org.fpgabros.fmt.model.Element;
import org.fpgabros.fmt.util.Stack;

import lombok.Data;

@Data
public abstract class Spec implements ParseListener {

    private final Stack<Element> stack;

    protected Spec() {
        this.stack = new Stack<>();
    }

    public abstract Element read(File file) throws ParseException;

    @Override
    public void openFile(String id) throws ParseException {
        if (!stack.isEmpty()) {
            throw new ParseException("Parser has not been reset.");
        }
        stack.push(new Element(id));
    }

    @Override
    public void openRecord(String id) throws ParseException {
        if (stack.isEmpty()) {
            throw new ParseException("A file must be opened to start a record.");
        }
        if (id == null) {
            int idx = stack.peek().getChildren().size();
            stack.push(new Element(idx));
        } else {
            stack.push(new Element(id));
        }
    }

    @Override
    public void openRow(Integer index) throws ParseException {
        if (stack.isEmpty()) {
            throw new ParseException("A record must be opened to start a row.");
        }
        int id = index != null ? index : stack.peek().getChildren().size();
        stack.push(new Element(id));
    }

    @Override
    public void receive(Integer index, String value) throws ParseException {
        if (stack.isEmpty()) {
            throw new ParseException("A row must be opened to receive a value.");
        }
        // create a new element
        int id = index != null ? index : stack.peek().getChildren().size();
        Element e = new Element(id, value);

        stack.peek().getChildren().add(e);
    }

    @Override
    public void comment(String value) throws ParseException {
        if (stack.isEmpty()) {
            throw new ParseException("A file must be opened to process a comment.");
        }
        // append comment to value of record
        String existing = stack.peek().getValue();
        stack.peek().setValue(existing == null ? value : (existing + "\n" + value));
    }

    @Override
    public void closeRow() throws ParseException {
        if (stack.size() < 2) {
            throw new ParseException("A row cannot be closed. No row is open.");
        }
        Element row = stack.pop();
        stack.peek().getChildren().add(row);
    }

    @Override
    public void closeRecord() throws ParseException {
        if (stack.size() < 2) {
            throw new ParseException("A record cannot be closed. No record is open.");
        }

        Element rec = stack.pop();
        fold(rec);
        stack.peek().getChildren().add(rec);
    }

    @Override
    public Element closeFile() throws ParseException {
        if (stack.isEmpty()) {
            throw new ParseException("A file cannot be closed. No file is open.");
        }

        // fold to convert single record files into a single record
        // instead of a 1-element list
        // while the root element has 1 child, fold it into the root element
        Element root = stack.pop();
        fold(root);
        return root;
    }

    @Override
    public void reset() throws ParseException {
        stack.clear();
    }

    private void fold(Element element) {
        // prevent single child records
        while (element.getChildren().size() == 1) {
            Element child = element.getChildren().get(0);
            element.setValue(child.getValue());
            element.setChildren(child.getChildren());
        }
    }

}
