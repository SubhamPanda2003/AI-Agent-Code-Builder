package com.AI_Agent_Code_Builder.AI_Agent_Code_Builder.services;

import com.AI_Agent_Code_Builder.AI_Agent_Code_Builder.services.extractors.GeminiExtractorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.*;
import java.util.stream.Stream;

@Service
public class LLMCoder {

    @Autowired
    private GeminiExtractorService geminiExtractorService;

    /**
     * For each file in the input folder, extract comment, generate full code using Gemini, and overwrite file.
     *
     * @param jobId job identifier
     */
    public void fillFilesWithCode(String jobId) {
        Path inputDir = Paths.get("/agent_data", jobId, "input");

        try (Stream<Path> pathStream = Files.walk(inputDir)) {
            pathStream.filter(Files::isRegularFile).forEach(file -> {
                try {
                    String fileDescription = extractFirstLineComment(file);
                    if (fileDescription != null && !fileDescription.isBlank()) {
                        String prompt = buildCodePrompt(fileDescription, file.getFileName().toString());
                        String code = geminiExtractorService.extractFromPrompt(prompt);
                        Files.writeString(file, code);
                    }
                } catch (IOException e) {
                    System.err.println("Failed to process file: " + file);
                    e.printStackTrace();
                }
            });
        } catch (IOException e) {
            System.err.println("Failed to walk input directory for jobId " + jobId);
            e.printStackTrace();
        }
    }

    /**
     * Extracts the first line comment from a source file (used as functional description).
     */
    private String extractFirstLineComment(Path file) throws IOException {
        try (BufferedReader reader = Files.newBufferedReader(file)) {
            String firstLine = reader.readLine();
            if (firstLine == null) return null;

            String line = firstLine.strip();

            if (line.startsWith("#")) {
                return line.substring(1).trim(); // Python, Shell, YAML
            } else if (line.startsWith("//")) {
                return line.substring(2).trim(); // JS, Java, C++
            } else if (line.startsWith("/*")) {
                return line.replace("/*", "").replace("*/", "").trim(); // C-style
            } else if (line.startsWith("<!--")) {
                return line.replace("<!--", "").replace("-->", "").trim(); // HTML/XML
            } else {
                return null;
            }
        }
    }


    /**
     * Builds a code generation prompt from the comment.
     */
    private String buildCodePrompt(String filePurpose, String fileName) {
        return """
            You are an expert developer.

            File: """ + fileName + """
            
            Description: """ + filePurpose + """

            Please write the complete implementation for this file.
            Use good coding practices, comments, proper structure, and necessary imports.

            Return only code, do not include explanations.
            """;
    }
}
