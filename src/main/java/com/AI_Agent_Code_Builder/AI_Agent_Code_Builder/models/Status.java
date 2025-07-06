package com.AI_Agent_Code_Builder.AI_Agent_Code_Builder.models;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "status_table")
public class Status {

    @Id
    @Column(name = "job_id", nullable = false, unique = true)
    private String jobId;

    @Column(name = "status", nullable = false)
    private String status;

    @Column(name = "download_url")
    private String downloadUrl;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    // Constructors
    public Status() {}

    public Status(String jobId, String status, String downloadUrl, LocalDateTime createdAt) {
        this.jobId = jobId;
        this.status = status;
        this.downloadUrl = downloadUrl;
        this.createdAt = createdAt;
    }

    // Getters and Setters
    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Status status1 = (Status) o;
        return Objects.equals(jobId, status1.jobId) && Objects.equals(status, status1.status) && Objects.equals(downloadUrl, status1.downloadUrl) && Objects.equals(createdAt, status1.createdAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(jobId, status, downloadUrl, createdAt);
    }

    @Override
    public String toString() {
        return "Status{" +
                "jobId='" + jobId + '\'' +
                ", status='" + status + '\'' +
                ", downloadUrl='" + downloadUrl + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}
