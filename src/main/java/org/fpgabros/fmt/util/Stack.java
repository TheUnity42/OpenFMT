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
package org.fpgabros.fmt.util;

import java.util.ArrayList;

public class Stack<T> extends ArrayList<T> {

    public void push(T t) {
        add(t);
    }

    public T pop() {
        if (isEmpty()) {
            return null;
        }
        return remove(size() - 1);
    }

    public T peek() {
        if (isEmpty()) {
            return null;
        }
        return get(size() - 1);
    }

}
