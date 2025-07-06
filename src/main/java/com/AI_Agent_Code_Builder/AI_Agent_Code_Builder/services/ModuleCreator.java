package com.AI_Agent_Code_Builder.AI_Agent_Code_Builder.services;

import com.AI_Agent_Code_Builder.AI_Agent_Code_Builder.services.extractors.GeminiExtractorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class ModuleCreator {

    @Autowired
    private GeminiExtractorService geminiExtractorService;

    /**
     * Create file structure from prompt using Gemini.
     *
     * @param prompt user request
     * @param jobId  folder name under /agent_data/{jobId}/input
     */
    public void createModuleStructure(String prompt, String jobId) {
        String structuredPrompt = buildLanguageAgnosticPrompt(prompt);
        String response = geminiExtractorService.extractFromPrompt(structuredPrompt);

        Map<String, String> fileCommentMap = parseGeminiResponse(response);
        String basePath = Paths.get("/agent_data", jobId, "input").toString();

        for (Map.Entry<String, String> entry : fileCommentMap.entrySet()) {
            String filePath = Paths.get(basePath, entry.getKey()).toString();
            String commentPrefix = getCommentPrefix(entry.getKey());
            createFileWithComment(filePath, commentPrefix + " " + entry.getValue());
        }

    }

    private String buildLanguageAgnosticPrompt(String task) {
        return """
            You are a software architect.

            Given the following software task:
            \"\"\" + task + \"\"\"

            Please break it into a modular file structure. Return only the list of filenames (e.g. main.py, utils/api.js, services/logger.ts, src/App.java),
            and for each file add a one-line summary describing the purpose of the file.

            Return in this format:
            <file_path>: <purpose>

            Do not include code, only file paths and what they should do.
            """;
    }

    private Map<String, String> parseGeminiResponse(String response) {
        Map<String, String> result = new LinkedHashMap<>();
        Pattern pattern = Pattern.compile("(.+?):\\s+(.+)");
        String[] lines = response.split("\\r?\\n");

        for (String line : lines) {
            Matcher matcher = pattern.matcher(line.trim());
            if (matcher.matches()) {
                result.put(matcher.group(1).trim(), matcher.group(2).trim());
            }
        }
        return result;
    }

    private String getCommentPrefix(String fileName) {
        String ext = fileName.substring(fileName.lastIndexOf('.') + 1).toLowerCase();

        return switch (ext) {
            case "py", "sh", "rb"       -> "#";
            case "js", "ts", "java",
                 "cpp", "c", "cs", "go" -> "//";
            case "html", "xml"          -> "<!--";
            case "css", "scss"          -> "/*";
            case "json", "yml", "yaml"  -> "#";
            default                     -> "//"; // fallback
        };
    }

    private void createFileWithComment(String filePath, String commentLine) {
        try {
            File file = new File(filePath);
            file.getParentFile().mkdirs();
            try (FileWriter writer = new FileWriter(file)) {
                writer.write(commentLine + "\n");
            }
        } catch (IOException e) {
            System.err.println("Failed to write to: " + filePath);
            e.printStackTrace();
        }
    }
}
