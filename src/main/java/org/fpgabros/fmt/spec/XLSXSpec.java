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
import java.io.IOException;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.fpgabros.fmt.exception.ParseException;
import org.fpgabros.fmt.model.Element;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class XLSXSpec extends Spec {

    private final String commentToken;

    public XLSXSpec() {
        this(";");
    }

    public XLSXSpec(final String commentToken) {
        super();
        this.commentToken = commentToken;
    }

    @Override
    public Element read(final File file) throws ParseException {
        // open the workbook
        try (Workbook workbook = new XSSFWorkbook(file)) {
            // file opened
            openFile(file.getName());

            for (final Sheet sheet : workbook) {
                parseSheet(sheet);
            }

            // close file
            return closeFile();
        } catch (IOException | InvalidFormatException ex) {
            reset(); // reset parse on failure
            throw new ParseException(ex);
        }
    }

    private void parseSheet(final Sheet sheet) throws ParseException {
        // open record for this sheet
        openRecord(sheet.getSheetName());
        boolean readingSubRecord = false;
        final int lastRowNum = sheet.getLastRowNum();
        // for each row
        for (int r = 0; r <= lastRowNum; r++) {
            Row row = sheet.getRow(r);
            if (isRowEmpty(row)) {
                if (readingSubRecord) {
                    closeRecord();
                }
                readingSubRecord = false;
            } else {
                // open a record
                if (!readingSubRecord) {
                    openRecord(null);
                    readingSubRecord = true;
                }
                // parse the row
                if (!isCommentRow(row)) {
                    parseRow(row);
                }
            }
        }

        // if reading a sub record, close it
        if (readingSubRecord) {
            closeRecord();
        }

        // close sheet record
        closeRecord();
    }

    private boolean isRowEmpty(final Row row) {
        if (row == null || row.getLastCellNum() <= 0) {
            return true;
        }
        return findFirstCell(row) == null;
    }

    private void parseRow(Row row) throws ParseException {
        // open row
        openRow(row.getRowNum());
        // get the index of the last cell
        final int lastCellNum = row.getLastCellNum();
        for (int i = 0; i < lastCellNum; i++) {
            final Cell cell = row.getCell(i);
            receive(i, isBlankCell(cell) ? "" : cell.toString());
        }
        // close row
        closeRow();
    }

    private boolean isCommentRow(final Row row) throws ParseException {
        // find the value of the first cell
        final String first = findFirstCell(row);
        if (first != null && first.stripLeading().startsWith(commentToken)) {
            final String stripped = first.stripLeading().substring(commentToken.length());
            // merge all the cells in this row into one string
            final StringBuilder sb = new StringBuilder(stripped);

            for (int i = 1; i < row.getLastCellNum(); i++) {
                final Cell cell = row.getCell(i);
                if (cell != null) {
                    sb.append(cell.toString());
                }
            }

            // issue comment
            comment(sb.toString());
            return true;
        }
        return false;
    }

    private String findFirstCell(final Row row) {
        // find the first non blank cell in the row
        for (final Cell cell : row) {
            if (!isBlankCell(cell)) {
                return cell.toString();
            }
        }
        return null;
    }

    private boolean isBlankCell(final Cell cell) {
        return cell == null || cell.getCellType() == CellType.BLANK || StringUtils.isBlank(cell.toString());
    }

}
