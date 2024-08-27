package com.teamproject.account.utils;

import com.teamproject.account.income.IncomeDTO;
import com.teamproject.account.outcome.OutcomeDTO;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

import java.util.List;

public interface ExcelUtilMethodFactory {

    void outcomeExcelDownload(List<OutcomeDTO> data, HttpServletResponse response);
    void renderOutcomeExcelBody(List<OutcomeDTO> data, Sheet sheet, Row row, Cell cell);
}
