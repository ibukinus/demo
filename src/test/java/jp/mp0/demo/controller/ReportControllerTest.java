/**
 * 
 */
package jp.mp0.demo.controller;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import java.io.IOException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import jp.mp0.demo.presentation.ExcelService;

/**
 * @author myoni
 *
 */
@WebMvcTest(ReportController.class)
class ReportControllerTest {

  @Autowired
  private MockMvc mvc;

  @MockBean
  private ExcelService service;

  @Test
  @DisplayName("正常に処理が終了した場合、Excelファイルが返却されること")
  void testDownloadReport01() throws Exception {
    // 準備
    Resource mockResource = new ClassPathResource("mock.xlsx");
    when(service.create(anyString())).thenReturn(mockResource);

    // 実行＆検証
    int contentLength = mvc.perform(get("/report")).andExpect(status().isOk())
        .andExpect(content()
            .contentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
        .andReturn().getResponse().getContentLength();
    // コンテンツのサイズが0以上であることを確認する
    assertThat(contentLength).isNotZero();

  }

  @Test
  @DisplayName("Service内で例外が発生した場合、エラーレスポンスが返却されること")
  void testDownloadReport02() throws Exception {
    doThrow(new IOException("モックのIOException")).when(service).create(anyString());
    mvc.perform(get("/report")).andExpect(status().isInternalServerError())
        .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE))
        .andExpect(content().json(
            "{\"type\":\"about:blank\",\"title\":\"Internal Server Error\",\"status\":500,\"detail\":\"モックのIOException\",\"instance\":\"/report\"}"));

  }

}
