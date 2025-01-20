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
package org.fpgabros.fmt.model;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class Element {

    private String id;

    private String value;

    private final List<String> index;

    private final List<Element> children;

    public Element() {
        this(null, null);
    }

    public Element(int id) {
        this(Integer.toString(id), null);
    }

    public Element(String id) {
        this(id, null);
    }

    public Element(int id, String value) {
        this(Integer.toString(id), value);
    }

    public Element(String id, String value) {
        this.id = id;
        this.value = value;
        this.index = new ArrayList<>();
        this.children = new ArrayList<>();
    }

    public Element(String id, String value, List<String> index, List<Element> children) {
        this(id, value);
        this.index.addAll(index);
        this.children.addAll(children);
    }

    public void setIndex(List<String> index) {
        this.index.clear();
        this.index.addAll(index);
    }

    public void setChildren(List<Element> children) {
        this.children.clear();
        this.children.addAll(children);
    }

    public boolean isEmpty() {
        return value == null && children.isEmpty();
    }

    public boolean isLeaf() {
        return children.isEmpty();
    }

    public String dump(int indent) {
        if (indent <= 0) {
            return this.toString();
        } else {
            return dump(indent, 0);
        }
    }

    private String dump(int indent, int index) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < indent * index; i++) {
            sb.append(" ");
        }
        sb.append("E(");
        sb.append(id);
        sb.append(")");
        if (value != null) {
            sb.append("=");
            sb.append(value);
        }
        sb.append("\n");
        for (Element child : children) {
            sb.append(child.dump(indent, index + 1));
        }
        return sb.toString();
    }

}
