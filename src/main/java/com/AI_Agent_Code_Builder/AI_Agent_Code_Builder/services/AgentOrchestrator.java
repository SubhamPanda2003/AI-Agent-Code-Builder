package com.AI_Agent_Code_Builder.AI_Agent_Code_Builder.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
public class AgentOrchestrator {

    @Autowired
    private ModuleCreator moduleCreator;

    @Autowired
    private LLMCoder llmCoder;

    /**
     * Orchestrates the agent's actions:
     * - Creates module structure
     * - Fills code using Gemini
     * - Zips the folder
     *
     * @param prompt user task description
     * @param jobId  job identifier
     * @return path to the generated zip file
     */
    public String handleTask(String prompt, String jobId) {
        // Step 1: Create modular structure
        moduleCreator.createModuleStructure(prompt, jobId);

        // Step 2: Fill code using Gemini
        llmCoder.fillFilesWithCode(jobId);

        // Step 3: Zip the result
        Path inputDir = Paths.get("/agent_data", jobId, "input");
        Path zipPath = Paths.get("/agent_data", jobId, "output", jobId + "_code.zip");

        try {
            Files.createDirectories(zipPath.getParent());
            zipDirectory(inputDir, zipPath);
            return zipPath.toString();
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to zip generated code for job " + jobId);
        }
    }

    /**
     * Utility to zip a directory and save it as a single zip file.
     *
     * @param sourceDir source directory
     * @param zipFile   output zip file path
     */
    private void zipDirectory(Path sourceDir, Path zipFile) throws IOException {
        try (FileOutputStream fos = new FileOutputStream(zipFile.toFile());
             ZipOutputStream zos = new ZipOutputStream(fos)) {

            Files.walk(sourceDir).filter(Files::isRegularFile).forEach(path -> {
                ZipEntry zipEntry = new ZipEntry(sourceDir.relativize(path).toString());
                try {
                    zos.putNextEntry(zipEntry);
                    Files.copy(path, zos);
                    zos.closeEntry();
                } catch (IOException e) {
                    throw new UncheckedIOException("Failed to zip file: " + path, e);
                }
            });
        }
    }
}
