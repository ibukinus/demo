package jp.mp0.demo.presentation;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

/**
 * Excel帳票サービス
 * 
 * @implSpec buildReportメソッドをオーバライドして使用してください
 *
 */
public abstract class ExcelReportService {

  /**
   * テンプレートからExcelファイルを作成します。
   * 
   * @param templatePath テンプレートファイルへのパス
   * @return Excelファイル
   * @throws IOException Excelファイルの読み書きに失敗した場合
   */
  public Resource create(String templatePath) throws IOException {

    Workbook workbook;
    try (var is = new ClassPathResource(templatePath).getInputStream()) {
      workbook = WorkbookFactory.create(is);
    }

    buildReport(workbook);

    try (var os = new ByteArrayOutputStream()) {
      workbook.write(os);
      return new ByteArrayResource(os.toByteArray());
    }
  }

  /**
   * Excel帳票を作成します。
   * 
   * @param workbook Workbookオブジェクト
   */
  protected abstract void buildReport(Workbook workbook);

}
