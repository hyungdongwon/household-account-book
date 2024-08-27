package com.teamproject.account.utils;

import com.teamproject.account.annotation.ExcelColumn;
import com.teamproject.account.outcome.OutcomeDTO;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
public class ExcelUtils implements ExcelUtilMethodFactory {

    /*
     *   학생 엑셀 다운로드 수행 로직
     *   @param List<StudentDto>
     *   @param HttpServletResponse
     *   @throws IOException
     *   @throws RuntimeException
     * */
    @Override
    public void outcomeExcelDownload(List<OutcomeDTO> data, HttpServletResponse response) {
        // 엑셀파일(Workbook) 객체 생성
        Workbook workbook = getXSSFWorkBook();

        // 엑셀파일 sheet를 만들고, sheet의 이름을 지정해 줄 수 있다.
        Sheet sheet = workbook.createSheet("첫 번째 시트");

        // 엑셀의 열에 해당하는 Cell 객체 생성
        Cell cell = null;

        // 엑셀의 행에 해당하는 Row 객체 생성
        Row row = null;

        // List가 아닌 DTO를 넘겨줘야 하므로 메서드를 통해 DTO의 class 정보가 담긴 class 객체를 넣어준다.
        // Header의 내용을 List로 반환 받는다(엑셀의 Cell의 첫줄이 된다.)
        List<String> excelHeaderList = getHeaderName(getClass(data));

        // Header - 열의 첫줄(컬럼 이름들)을 그리는 작업

        // 첫 행을 생성해준다.
        row = sheet.createRow(0);

        // 헤더의 수(컬럼 이름의 수)만큼 반복해서 행을 생성한다.
        for (int i = 0; i < excelHeaderList.size(); i++) {

            // 열을 만들어준다.
            cell = row.createCell(i);

            // 열에 헤더이름(컬럼 이름)을 넣어준다.
            cell.setCellValue(excelHeaderList.get(i));
        }

        // Body
        // 헤더 밑의 엑셀 파일 내용부분에 들어갈 내용을 그리는 작업
        renderOutcomeExcelBody(data, sheet, row, cell);

        // DownLoad
        // 엑셀 파일이 완성 되면 파일 다운로드를 위해 content-type과 Header를 설정해준다.
        // filename=파일이름.xlsx 가 파일의 이름이 된다.
        response.setContentType("ms-vnd/excel");
        response.setHeader("Content-Disposition", "attachment;filename=outcome.xlsx");

        try {
            // 엑셀 파일을 다운로드 하기 위해 write() 메서드를 사용한다.
            workbook.write(response.getOutputStream());
        } catch (IOException e) {
            // checked 예외를 사용하면 추후 의존이나 예외 누수 문제가 생길 수 있으므로
            // RuntimeException으로 한번 감싸서, cause가 나올 수 있게 발생한 예외를 넣어준다.
            log.error("Workbook write 수행 중 IOException 발생!");
            throw new RuntimeException(e);
        } finally {
            // 파일 입출력 스트림을 사용한 후에는 예외 발생 여부와 관계없이 반드시 닫아 주어야 한다.
            closeWorkBook(workbook);
        }
    }

    /*
     *   엑셀의 본문에 내용을 그려주는 로직
     *   @param List<StudentDto>
     *   @param Sheet
     *   @param Row
     *   @param Cell
     * */
    @Override
    public void renderOutcomeExcelBody(List<OutcomeDTO> data, Sheet sheet, Row row, Cell cell) {
        // 현재 행의 개수를 가지고 있는 변수 rowCount 선언(Header를 그리고 시작했으므로 1부터 시작)
        int rowCount = 1;

        // 조회해온 데이터 리스트(List<StudentDto>)의 크기만큼 반복문을 실행한다.
        for (OutcomeDTO outcomeDTO : data) {

            // 헤더를 설정할때 0번 인덱스가 사용 되었으므로, i값에 1을 더해서 1번 로우(행)부터 생성한다.
            row = sheet.createRow(rowCount++);

            // TODO : 하드코딩 대신 추후 동적으로 처리 할 수 있도록 개선 예정
            // 첫 번째 cell(열)을 생성한다.
            cell = row.createCell(0);
            // 첫 번째 cell(열)의 값을 셋팅한다.
            cell.setCellValue(outcomeDTO.getRegDt());
            // 두 번째 cell(열)을 생성한다.
            cell = row.createCell(1);
            // 두 번째 cell(열)의 값을 셋팅한다.
            cell.setCellValue(outcomeDTO.getOutcomeCategoryName());

            cell = row.createCell(2);
            cell.setCellValue(outcomeDTO.getOutcomeContent());

            cell = row.createCell(3);
            cell.setCellValue(outcomeDTO.getCommaprice());

            cell = row.createCell(4);
            cell.setCellValue(outcomeDTO.getMemo());
        }
    }

    private XSSFWorkbook getXSSFWorkBook() {
        return new XSSFWorkbook();
    }

    private List<String> getHeaderName(Class<?> type) {

        // 스트림으로 엑셀 헤더 이름들을 리스트로 반환
        // 1. 매개변수로 전달된 클래스의 필드들을 배열로 받아, 스트림을 생성
        // 2. @ExcelColumn 애너테이션이 붙은 필드만 수집
        // 3. @ExcelColumn 애너테이션이 붙은 필드에서 애너테이션의 값을 매핑
        // 4. LinkedList로 반환
        List<String> excelHeaderNameList = Arrays.stream(type.getDeclaredFields())
                .filter(s -> s.isAnnotationPresent(ExcelColumn.class))
                .map(s -> s.getAnnotation(ExcelColumn.class).headerName())
                .collect(Collectors.toCollection(LinkedList::new));

        // 헤더의 이름을 담은 List가 비어있을 경우, 헤더 이름이 지정되지 않은 것이므로, 예외를 발생시킨다.
        if (CollectionUtils.isEmpty(excelHeaderNameList)) {
            log.error("헤더 이름이 조회되지 않아 예외 발생!");
            throw new IllegalStateException("헤더 이름이 없습니다.");
        }

        return excelHeaderNameList;
    }


    /*
     *   List(데이터 리스트)에 담긴 DTO의 클래스 정보를 반환하는 메서드
     *   @param List<?>
     *   @return Class<?>
     * */
    private Class<?> getClass(List<?> data) {
        // List가 비어있지 않다면 List가 가지고 있는 모든 DTO는 같은 필드를 가지고 있으므로,
        // 맨 마지막 DTO만 빼서 클래스 정보를 반환한다.
        if (!CollectionUtils.isEmpty(data)) {
            return data.get(data.size() - 1).getClass();
        } else {
            log.error("리스트가 비어 있어서 예외 발생!");
            throw new IllegalStateException("조회된 리스트가 비어 있습니다. 확인 후 다시 진행해주시기 바랍니다.");
        }
    }
    private void closeWorkBook(Workbook workbook) {
        try {
            if(workbook != null) {
                workbook.close();
            }
        } catch (IOException e) {
            // checked 예외를 사용하면 추후 의존이나 예외 누수 문제가 생길 수 있으므로
            // RuntimeException으로 한번 감싸서, cause가 나올 수 있게 발생한 예외를 넣어준다.
            throw new RuntimeException(e);
        }
    }

}
