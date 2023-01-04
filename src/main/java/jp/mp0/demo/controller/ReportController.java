package jp.mp0.demo.controller;

import java.nio.charset.StandardCharsets;
import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponseException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jp.mp0.demo.presentation.ExcelService;

@RestController
public class ReportController {
  private static final String TEMPLATE_PATH = "templates/テンプレート.xlsx";

  private final ExcelService service;

  public ReportController(ExcelService service) {
    this.service = service;
  }

  @GetMapping("/report")
  @Operation(description = "Excelファイルを生成してダウンロードします。")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "成功",
          content = @Content(
              mediaType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")),
      @ApiResponse(responseCode = "500", description = "Excel生成エラー",
          content = @Content(mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
              schema = @Schema(implementation = ProblemDetail.class)))})
  public ResponseEntity<Resource> downloadReport() {
    try {
      Resource file = service.create(TEMPLATE_PATH);
      return createResponse("エクスポート.xlsx", file);
    } catch (Exception e) {
      var problemDetail = ProblemDetail.forStatus(HttpStatus.INTERNAL_SERVER_ERROR);
      problemDetail.setDetail(e.getMessage());
      throw new ErrorResponseException(HttpStatus.INTERNAL_SERVER_ERROR, problemDetail, e);
    }
  }

  /**
   * レスポンスを生成します。
   * 
   * @param filename ファイル名
   * @param file ファイルリソース
   * @return レスポンス
   */
  private ResponseEntity<Resource> createResponse(String filename, Resource file) {
    return ResponseEntity.ok()
        .contentType(MediaType
            .parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
        .header(HttpHeaders.CONTENT_DISPOSITION, ContentDisposition.attachment()
            .filename(filename, StandardCharsets.UTF_8).build().toString())
        .body(file);
  }
}
