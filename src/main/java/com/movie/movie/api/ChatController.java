package com.movie.movie.api;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.movie.movie.model.dto.ChatRequest;
import com.movie.movie.model.dto.ConversationMemory;
import com.movie.movie.service.impl.GeminiService;
import com.movie.movie.service.impl.QueryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/movie/")
@CrossOrigin(origins = "http://localhost:3000")
public class ChatController {

    private final GeminiService geminiService;
    private final QueryService queryService;
    private final ConversationMemory conversationMemory;
    private final ObjectMapper objectMapper; // Dùng để convert list sang json string

    public ChatController(GeminiService geminiService,
                          QueryService queryService,
                          ConversationMemory conversationMemory) {
        this.geminiService = geminiService;
        this.queryService = queryService;
        this.conversationMemory = conversationMemory;
        this.objectMapper = new ObjectMapper();
    }

    @PostMapping("/send")
    public ResponseEntity<String> chat(@RequestBody ChatRequest request) {
        try {
            String question = request.getMessage();
            conversationMemory.addMessage("user", question);

            // 1. Nhờ AI sinh SQL
            String generatedText = geminiService.generateSQLFromQuestion(question);

            // 2. Làm sạch SQL (Xử lý kỹ hơn các trường hợp Markdown)
            String sql = cleanSql(generatedText);

            // 3. Kiểm tra xem có phải SQL hợp lệ không
            // Nếu AI trả về text thường (chào hỏi) hoặc SQL nguy hiểm -> Chat thường
            if (!isValidSelectSql(sql)) {
                // Nếu AI không tạo SQL, có thể nó đang chat thường, lưu câu trả lời vào memory
                conversationMemory.addMessage("model", generatedText);
                return ResponseEntity.ok(generatedText);
            }

            // 4. Chạy SQL
            System.out.println("Executing SQL: " + sql); // Log để debug
            List<Map<String, Object>> result = queryService.runSQLQuery(sql);

            // 5. Tổng hợp câu trả lời
            String finalReply;
            if (result.isEmpty()) {
                finalReply = "Tôi đã tìm trong dữ liệu nhưng không thấy kết quả nào phù hợp.";
            } else {
                // Chuyển kết quả database thành JSON string để gửi lại cho AI đọc
                String jsonResult = objectMapper.writeValueAsString(result);
                finalReply = geminiService.askGeminiWithResult(question, jsonResult);
            }

            conversationMemory.addMessage("model", finalReply);
            return ResponseEntity.ok(cleanMarkdown(finalReply));

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Đã xảy ra lỗi: " + e.getMessage());
        }
    }

    // Hàm làm sạch SQL
    private String cleanSql(String text) {
        return text.replaceAll("```sql", "")
                .replaceAll("```", "")
                .trim();
    }

    // Hàm kiểm tra SQL an toàn cơ bản
    private boolean isValidSelectSql(String sql) {
        String lower = sql.toLowerCase();
        return lower.startsWith("select") &&
                !lower.contains("delete") &&
                !lower.contains("update") &&
                !lower.contains("insert") &&
                !lower.contains("drop") &&
                !lower.contains("alter");
    }

    private String cleanMarkdown(String text) {
        // Chỉ xóa bold nếu cần thiết, hoặc giữ nguyên để FE hiển thị đẹp hơn
        return text.replace("**", "");
    }


//    @PostMapping("/chatbot")
//    public ResponseEntity<String> chatWithDatabase(@RequestBody ChatRequest request) {
//        try {
//            String question = request.getMessage();
//            conversationMemory.addMessage("user", question);
//
//            String sql = geminiService.generateSQLFromQuestion(question);
//
//            sql = sql.replaceAll("(?i)```sql", "")
//                    .replaceAll("```", "")
//                    .trim();
//
//            if (!sql.toLowerCase().startsWith("select") || sql.contains("--") || sql.length() < 15) {
//                String reply = geminiService.askWithHistory(conversationMemory.getHistory());
//                conversationMemory.addMessage("model", reply);
//                return ResponseEntity.ok(clean(reply));
//            }
//
//            List<Map<String, Object>> result = queryService.runSQLQuery(sql);
//
//            String reply;
//            if (result.isEmpty()) {
//                reply = "Không có kết quả phù hợp với yêu cầu của bạn.";
//            } else {
//                String jsonResult = new ObjectMapper().writeValueAsString(result);
//                reply = geminiService.askGeminiWithResult(question, jsonResult);
//            }
//
//            conversationMemory.addMessage("model", reply);
//            return ResponseEntity.ok(clean(reply));
//        } catch (Exception e) {
//            return ResponseEntity.internalServerError().body("Lỗi: " + e.getMessage());
//        }
//    }
//
//    private String clean(String text) {
//        return text.replaceAll("\\*\\*", "");
//    }
}
