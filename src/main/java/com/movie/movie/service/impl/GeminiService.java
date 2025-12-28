package com.movie.movie.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.commons.lang3.StringEscapeUtils;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class GeminiService {

    private static final String ENDPOINT = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent?key=AIzaSyClbeVqdrSKfAgt73uiZ3hcpQyM7YEVUGM";

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public GeminiService() {
        this.restTemplate = new RestTemplate();
        this.objectMapper = new ObjectMapper();
    }

    public String generateSQLFromQuestion(String userQuestion) throws IOException {
        String schema = """
            Dưới đây là thông tin chi tiết về các bảng trong cơ sở dữ liệu website phim:
        
            Bảng `countries` (Quốc gia):
            - country_id (int): mã quốc gia (PK)
            - country_name (varchar): tên quốc gia (ví dụ: 'Việt Nam', 'Mỹ', 'Hàn Quốc')
            - country_code (varchar): mã viết tắt
            → Dùng để lọc phim theo quốc gia.
        
            Bảng `genres` (Thể loại):
            - genre_id (int): mã thể loại (PK)
            - genre_name (varchar): tên thể loại (ví dụ: 'Hành động', 'Kinh dị')
            - genre_slug (varchar): slug thể loại
            → Dùng để lọc phim theo thể loại.
        
            Bảng `movies` (Phim):
            - movie_id (bigint): mã phim (PK)
            - title (varchar): tên phim
            - sub_title (varchar): tên phụ/tên gốc
            - imdb_rating (decimal): điểm đánh giá IMDB
            - age_rating (varchar): giới hạn độ tuổi (PG-13, R...)
            - runtime (int): thời lượng (phút)
            - country_id (int): khóa ngoại tới countries.country_id
            - release_date (date): ngày phát hành
            - view_count (bigint): lượt xem
            - is_trending (boolean): 1 nếu là phim đang hot/thịnh hành
            - status (enum): 'coming_soon' (sắp chiếu), 'now_showing' (đang chiếu), 'completed' (hoàn thành)
            → Bảng chính chứa thông tin phim.
        
            Bảng `movie_genres` (Phân loại phim):
            - movie_id (bigint): khóa ngoại tới movies.movie_id
            - genre_id (int): khóa ngoại tới genres.genre_id
            → Bảng trung gian liên kết phim và thể loại.
        
            Bảng `persons` (Người nổi tiếng - Diễn viên/Đạo diễn):
            - person_id (bigint): mã người (PK)
            - full_name (varchar): tên đầy đủ
            - gender (varchar): giới tính
            - date_of_birth (date): ngày sinh
            - country_id (int): quốc tịch
        
            Bảng `movie_cast` (Diễn viên đóng phim):
            - movie_id (bigint): khóa ngoại tới movies.movie_id
            - person_id (bigint): khóa ngoại tới persons.person_id
            - character_name (varchar): tên nhân vật trong phim
            → Dùng để tìm phim theo diễn viên.
        
            Bảng `movie_crew` (Đạo diễn/Biên kịch):
            - movie_id (bigint): khóa ngoại tới movies.movie_id
            - person_id (bigint): khóa ngoại tới persons.person_id
            - role (enum): vai trò ('director', 'writer', 'producer'...)
            → Dùng để tìm phim theo đạo diễn hoặc nhà sản xuất.
        
            Bảng `seasons` (Mùa phim - Dành cho phim bộ):
            - season_id (bigint): mã mùa (PK)
            - movie_id (bigint): khóa ngoại tới movies.movie_id
            - season_number (int): số thứ tự mùa
            - title (varchar): tên mùa phim
        
            Bảng `episodes` (Tập phim):
            - episode_id (bigint): mã tập (PK)
            - season_id (bigint): khóa ngoại tới seasons.season_id
            - episode_number (int): số tập
            - title (varchar): tên tập phim
        
            ❗Hướng dẫn tạo SQL:
            - Nếu người dùng hỏi phim theo thể loại (ví dụ: "phim hành động") → JOIN `movies` với `movie_genres` và `genres`. WHERE genres.genre_name LIKE '%...%'
            - Nếu người dùng hỏi phim theo quốc gia (ví dụ: "phim Mỹ", "phim Hàn") → JOIN `movies` với `countries`. WHERE countries.country_name LIKE '%...%'
            - Nếu người dùng hỏi phim của diễn viên (ví dụ: "phim của Thành Long") → JOIN `movies` với `movie_cast` và `persons`. WHERE persons.full_name LIKE '%Thành Long%'
            - Nếu người dùng hỏi phim của đạo diễn (ví dụ: "phim đạo diễn Christopher Nolan") → JOIN `movies` với `movie_crew` và `persons`. WHERE persons.full_name LIKE '%...%' AND movie_crew.role = 'director'
            - Nếu hỏi phim hot, phim thịnh hành → WHERE movies.is_trending = 1 HOẶC ORDER BY movies.view_count DESC
            - Nếu hỏi phim mới nhất → ORDER BY movies.release_date DESC hoặc movies.created_at DESC
            - Nếu hỏi chi tiết các tập của một phim → JOIN `movies` -> `seasons` -> `episodes`
            - Lưu ý: Luôn SELECT các cột quan trọng như movie_id, title, poster_url, imdb_rating, view_count để hiển thị.
        
            ❗Yêu cầu đầu ra:
            - KHÔNG giải thích
            - KHÔNG sử dụng định dạng markdown
            - Chỉ trả về một câu SQL thuần chính xác và ngắn gọn
            """;

        String prompt = schema + "\n\nCâu hỏi: " + userQuestion;
//        return ask(prompt);
        return callGeminiApi(prompt);
    }

//    public String askGeminiWithResult(String question, String sqlResult) throws IOException {
//        String prompt = """
//            Người dùng hỏi: %s
//            Kết quả trả về từ database: %s
//            Hãy trả lời người dùng bằng giọng tự nhiên, dễ hiểu.
//        """.formatted(question, sqlResult);
//
//        return ask(prompt);
//    }
    public String askGeminiWithResult(String question, String jsonResult) {
        String prompt = String.format("""
                Người dùng hỏi: %s
                Kết quả từ Database (JSON): %s
                
                Hãy trả lời câu hỏi dựa trên kết quả trên một cách tự nhiên.
                """, question, jsonResult);
        return callGeminiApi(prompt);
    }

//    public String askWithHistory(List<Map<String, String>> messages) throws IOException {
//        StringBuilder parts = new StringBuilder();
//        for (Map<String, String> msg : messages) {
//            parts.append("{\"role\": \"").append(msg.get("role")).append("\", ")
//                    .append("\"parts\": [{\"text\": \"").append(msg.get("content")).append("\"}]},");
//
//        }
//        String jsonBody = String.format("{\"contents\": [%s]}", parts.substring(0, parts.length() - 1));
//        return sendRequest(jsonBody);
//    }

    public String askWithHistory(List<Map<String, String>> history) {
        // Xây dựng JSON body phức tạp cho history
        ObjectNode root = objectMapper.createObjectNode();
        ArrayNode contents = root.putArray("contents");

        for (Map<String, String> msg : history) {
            ObjectNode partNode = contents.addObject();
            partNode.put("role", msg.get("role"));
            partNode.putArray("parts").addObject().put("text", msg.get("content"));
        }

        return sendRequest(root);
    }

    private String callGeminiApi(String text) {
        ObjectNode root = objectMapper.createObjectNode();
        root.putArray("contents").addObject()
                .putArray("parts").addObject()
                .put("text", text);
        return sendRequest(root);
    }

    private String sendRequest(ObjectNode jsonBody) {
        try {
            String url = ENDPOINT;
//            String url = apiUrl + "?key=" + apiKey;

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<String> entity = new HttpEntity<>(jsonBody.toString(), headers);

            ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);

            // Parse kết quả trả về an toàn bằng Jackson
            JsonNode rootNode = objectMapper.readTree(response.getBody());
            return rootNode.path("candidates").get(0)
                    .path("content").path("parts").get(0)
                    .path("text").asText();

        } catch (Exception e) {
            e.printStackTrace();
            return "Xin lỗi, hệ thống AI đang bận. Lỗi: " + e.getMessage();
        }
    }

//    public String ask(String prompt) throws IOException {
//        String jsonBody = """
//            {
//              "contents": [
//                {
//                  "parts": [{ "text": "%s" }]
//                }
//              ]
//            }
//        """.formatted(prompt.replace("\"", "\\\""));
//
//        return sendRequest(jsonBody);
//    }

//    private String sendRequest(String jsonBody) throws IOException {
//        HttpURLConnection conn = (HttpURLConnection) new URL(ENDPOINT).openConnection();
//        conn.setRequestMethod("POST");
//        conn.setRequestProperty("Content-Type", "application/json");
//        conn.setDoOutput(true);
//        conn.getOutputStream().write(jsonBody.getBytes());
//
//        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
//        StringBuilder response = new StringBuilder();
//        String line;
//        while ((line = in.readLine()) != null) response.append(line);
//        in.close();
//
//        Matcher m = Pattern.compile("\"text\"\\s*:\\s*\"(.*?)\"", Pattern.DOTALL).matcher(response.toString());
//        return m.find() ? StringEscapeUtils.unescapeJava(m.group(1)) : "Không có phản hồi từ AI.";
//    }
}

