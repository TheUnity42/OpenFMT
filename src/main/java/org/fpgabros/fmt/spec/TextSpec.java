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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import org.fpgabros.fmt.exception.ParseException;
import org.fpgabros.fmt.model.Element;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class TextSpec extends Spec {

    private final Charset charset;
    private final String delimiter;
    private final String commentToken;
    private final boolean multipart;

    public TextSpec() {
        this(StandardCharsets.UTF_8, ",", ";", true);
    }

    public TextSpec(Charset charset, String delimiter, String commentToken, boolean multipart) {
        super();
        this.charset = charset;
        this.delimiter = delimiter;
        this.commentToken = commentToken;
        this.multipart = multipart;
    }

    @Override
    public Element read(File file) throws ParseException {
        try (Reader reader = new FileReader(file, charset);
                BufferedReader bufferedReader = new BufferedReader(reader)) {
            // issue start of file
            openFile(file.getName());
            // open a root record (will get folded if a multipart)
            openRecord(null);

            parseLines(bufferedReader);

            // close the root record
            closeRecord();

            // issue end of file
            return closeFile();
        } catch (IOException ex) {
            reset();
            throw new ParseException(ex);
        }
    }

    private void parseLines(BufferedReader bufferedReader) throws IOException {
        // track the multi part record state
        boolean processingSubRecord = false;

        // read all the lines
        String line = null;
        while ((line = bufferedReader.readLine()) != null) {
            // process line
            if (line.isBlank()) {
                if (processingSubRecord) {
                    // issue end of record
                    closeRecord();
                    processingSubRecord = false;
                }
            } else {
                // if not in record, start new record (when enabled)
                if (multipart && !processingSubRecord) {
                    processingSubRecord = true;
                    // issue start of record. We never know the id
                    openRecord(null);
                }
                // process row
                if (!parseComment(line)) {
                    parseRow(line);
                }
            }
        }

        // if we're in a subrecord, we need to make sure it gets closed
        if (processingSubRecord) {
            // issue end of record
            closeRecord();
        }
    }

    private void parseRow(String line) throws ParseException {
        openRow(null);
        for (String field : line.split(delimiter)) {
            receive(null, field);
        }
        closeRow();
    }

    private boolean parseComment(String line) throws ParseException {
        // if the line starts with the comment token (or ws then token)
        if (line.stripLeading().startsWith(commentToken)) {
            // strip the comment token and issue a comment
            line = line.substring(line.indexOf(commentToken) + commentToken.length());
            comment(line);
            return true;
        }
        return false;
    }

}
